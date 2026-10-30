package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class InstrumentDdaSessionTest {
    @get:Rule val temporaryFolder = TemporaryFolder()
    private val player = object : InstrumentPlayer {
        override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
            onStarted()
            return 134_505
        }
    }

    @Test
    fun `automatico reduce ayuda para la siguiente pregunta y deja dos rondas entre cambios`() = runTest {
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.C, InstrumentMode.AUTOMATIC)
        assertEquals(InstrumentLevel.A, session.state.level)
        for (round in 1..6) {
            val expected = if (round <= 3) 3 else 2
            assertEquals(expected, session.state.listensAllowed)
            session.listen()
            session.answer(Instrument.PIANO)
            assertEquals("La pregunta respondida conserva su ayuda", expected, session.state.listensAllowed)
            session.nextQuestion()
            assertEquals(if (round < 3) 3 else if (round < 6) 2 else 1, session.state.listensAllowed)
        }
    }

    @Test
    fun `errores recientes recuperan ayuda y el historial explica el limite superior`() = runTest {
        val file = File(temporaryFolder.root, "dda.json")
        val session = InstrumentSession({ 0 }, player, JsonInstrumentResults(file), clock = { Long.MAX_VALUE })
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(9) { index ->
            session.listen()
            session.answer(if (index < 3) Instrument.PIANO else Instrument.VIOLIN)
            session.nextQuestion()
        }
        assertEquals(3, session.state.listensAllowed)
        session.interrupt()
        val saved = InstrumentSession({ 0 }, player, JsonInstrumentResults(file)).history().single()
        val reduction = requireNotNull(saved.answers[2].ddaDecision)
        assertEquals(-1, reduction.appliedDelta)
        assertEquals(-11.0/18, requireNotNull(reduction.inference?.centroid), .003)
        val recovery = requireNotNull(saved.answers[5].ddaDecision)
        assertEquals(listOf(4, 5, 6), recovery.windowRounds)
        assertEquals(0.0, recovery.accuracy, 0.0)
        assertEquals(0.0, recovery.autonomy, 0.0)
        assertEquals(1, recovery.appliedDelta)
        assertEquals(11.0/18, requireNotNull(recovery.inference?.centroid), .003)
        assertEquals(1.0, requireNotNull(recovery.inference).memberships.getValue("exactitud").getValue("bajo"), 0.0)
        val capped = requireNotNull(saved.answers[8].ddaDecision)
        assertEquals(1, capped.proposedDelta)
        assertEquals(0, capped.appliedDelta)
        assertEquals(DdaReason.LIMIT, capped.reason)
        assertEquals("automatic", saved.origin)
        assertEquals("mamdani-help-v1", requireNotNull(saved.ddaConfiguration).inference.version)
    }

    @Test
    fun `aciertos con reescuchas conservan ayuda sin convertirlos en errores`() = runTest {
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(10) {
            repeat(3) { session.listen() }
            session.answer(Instrument.PIANO)
            session.nextQuestion()
        }
        assertEquals(10, session.state.correctCount)
        val decision = requireNotNull(session.state.answers[2].ddaDecision)
        assertEquals(1.0, decision.accuracy, 0.0)
        assertEquals(0.0, decision.autonomy, 0.0)
        assertEquals(0.0, requireNotNull(decision.inference?.centroid), .003)
        assertEquals(DdaReason.KEEP, decision.reason)
        assertTrue(session.state.answers.all { it.listensAllowed == 3 && it.correct })
        assertEquals(DdaReason.FINAL_ROUND, session.state.answers.last().ddaDecision?.reason)
    }

    @Test
    fun `alternancia de aciertos y errores mantiene ayuda con datos insuficientes al principio`() = runTest {
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(10) { index ->
            session.listen()
            session.answer(if (index % 2 == 0) Instrument.PIANO else Instrument.VIOLIN)
            session.nextQuestion()
        }
        assertEquals(5, session.state.correctCount)
        assertEquals(DdaReason.INSUFFICIENT_DATA, session.state.answers[0].ddaDecision?.reason)
        assertNull(session.state.answers[1].ddaDecision?.inference)
        assertTrue(session.state.answers.all { it.listensAllowed == 3 })
    }

    @Test
    fun `sin reglas validas no se retira ayuda y se registra el diagnostico`() = runTest {
        val configuration = InstrumentDdaConfiguration(defaultHelpInference().copy(version = "sin-reglas-test", rules = emptyList()))
        val session = InstrumentSession({ 0 }, player, ddaConfiguration = configuration)
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(3) {
            session.listen()
            session.answer(Instrument.PIANO)
            session.nextQuestion()
        }
        val decision = requireNotNull(session.state.answers.last().ddaDecision)
        assertNull(decision.inference?.centroid)
        assertNotNull(decision.inference?.diagnostic)
        assertEquals(DdaReason.INVALID_OUTPUT, decision.reason)
        assertEquals(3, session.state.listensAllowed)
    }

    @Test
    fun `minimo una escucha y una partida nueva reinicia ventana y espera`() = runTest {
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(10) {
            session.listen()
            session.listen() // Cuando solo queda una, la segunda petición no reproduce.
            session.answer(Instrument.PIANO)
            session.nextQuestion()
        }
        // Las primeras respuestas con reescuchas deben mantener ayuda.
        assertEquals(3, session.state.listensAllowed)
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        repeat(9) {
            session.listen()
            session.answer(Instrument.PIANO)
            session.nextQuestion()
        }
        assertEquals(1, session.state.listensAllowed)
        assertEquals(DdaReason.LIMIT, session.state.answers.last().ddaDecision?.reason)
        session.interrupt()
        session.start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
        assertEquals(3, session.state.listensAllowed)
        assertTrue(session.state.answers.isEmpty())
        repeat(3) { session.listen(); session.answer(Instrument.PIANO); session.nextQuestion() }
        assertEquals(2, session.state.listensAllowed)
    }

    @Test
    fun `practica manual conserva nivel y escuchas y no aplica la propuesta difusa`() = runTest {
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.C)
        repeat(3) { session.listen(); session.answer(Instrument.FLUTE); session.nextQuestion() }
        assertEquals(InstrumentLevel.C, session.state.level)
        val decision = requireNotNull(session.state.answers.last().ddaDecision)
        assertEquals(-1, decision.proposedDelta)
        assertEquals(0, decision.appliedDelta)
        assertEquals(DdaReason.MANUAL, decision.reason)
        assertEquals(3, session.state.listensAllowed)
    }
}

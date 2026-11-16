package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class InstrumentSessionTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test fun `responder durante la melodia registra una sola respuesta y conserva feedback al terminar audio`() = runTest {
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                onStarted()
                delay(5000)
                return 134_505
            }
        }
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.A)
        session.answer(Instrument.PIANO)
        assertTrue(session.state.answers.isEmpty())
        launch { session.listen() }
        runCurrent()
        session.answer(Instrument.PIANO)
        session.answer(Instrument.VIOLIN)
        assertEquals(InstrumentPhase.FEEDBACK, session.state.phase)
        assertTrue(session.state.answers.single().correct)
        assertEquals(1, session.state.answers.single().listensUsed)
        advanceUntilIdle()
        assertEquals(InstrumentPhase.FEEDBACK, session.state.phase)
    }

    @Test
    fun `azar que repite una frase con primera nota unica no bloquea la siguiente pregunta`() = runTest {
        val session = InstrumentSession({ bound -> if (bound == 5) 2 else 0 }, SuccessfulPlayer())
        session.start(InstrumentLevel.A)
        repeat(2) {
            session.listen()
            session.answer(Instrument.PIANO)
            if (it == 0) session.nextQuestion()
        }
        val melodies = session.state.answers.map { requireNotNull(it.melody) }
        assertNotEquals(melodies.first(), melodies.last())
        assertTrue(melodies.all { it.midiNotes.distinct().size >= 3 })
    }

    @Test
    fun `las reescuchas repiten la frase y la siguiente pregunta cambia aunque repita instrumento`() = runTest {
        val heard = mutableListOf<Pair<Instrument, InstrumentMelody>>()
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                heard.add(instrument to melody)
                onStarted()
                return 134_505
            }
        }
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.A)
        repeat(3) { session.listen() }
        session.listen() // Agotar escuchas no cambia ni reproduce la frase.
        assertEquals(3, heard.size)
        assertEquals(1, heard.distinct().size)
        session.answer(Instrument.PIANO)
        assertEquals(heard.first().second, session.state.answers.single().melody)
        session.nextQuestion()
        session.listen()
        assertEquals(Instrument.PIANO, heard.last().first)
        assertNotEquals(heard.first().second, heard.last().second)
    }

    @Test
    fun `historial de v03 conserva nota aislada sin inventar una melodia`() = runTest {
        val file = File(temporaryFolder.root, "legacy.json")
        file.writeText("""{"schemaVersion":1,"results":[{"id":"legacy-v03","level":"A","answers":[{"round":1,"target":"PIANO","options":["PIANO","VIOLIN","FLUTE","MARIMBA"],"selected":"PIANO","correct":true,"listensUsed":1,"answeredAtMillis":1500}],"completed":false,"startedAtMillis":1000,"finishedAtMillis":2000,"origin":"manual","configurationVersion":"instruments-0.3-three-listens","bankVersion":"iowa-c4-mf-v1","midi":60,"plannedQuestions":10}]}""")
        val session = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file))
        val legacy = session.history().single()
        assertEquals("iowa-c4-mf-v1", legacy.bankVersion)
        assertEquals("single-note", legacy.stimulusKind)
        assertEquals(60, legacy.midi)
        assertNull(legacy.answers.single().melody)
        session.start(InstrumentLevel.B)
        session.listen()
        session.answer(Instrument.FLUTE)
        session.interrupt()
        val reopened = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file)).history()
        assertEquals(2, reopened.size)
        assertEquals(legacy, reopened.first())
        assertNotNull(reopened.last().answers.single().melody)
    }

    @Test
    fun `cada respuesta conserva una melodia pentatonica breve que termina en tonica`() = runTest {
        val file = File(temporaryFolder.root, "melodies.json")
        val session = InstrumentSession(kotlin.random.Random(42)::nextInt, SuccessfulPlayer(), JsonInstrumentResults(file))
        session.start(InstrumentLevel.A)
        repeat(10) {
            session.listen()
            session.answer(session.state.options.first())
            session.nextQuestion()
        }
        val saved = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file)).history().single()
        saved.answers.forEach { answer ->
            assertNotNull("La frase debe poder reproducirse desde el resultado", answer.melody)
            val melody = requireNotNull(answer.melody)
            assertEquals(6, melody.midiNotes.size)
            assertTrue(melody.midiNotes.all { it in listOf(60, 62, 64, 67, 69) })
            assertTrue(melody.midiNotes.distinct().size >= 3)
            assertEquals(60, melody.midiNotes.last())
            assertTrue(melody.midiNotes.zipWithNext().all { (a, b) -> kotlin.math.abs(a-b) <= 5 })
            assertEquals(listOf(400, 400, 400, 400, 400, 800), melody.durationsMillis)
        }
        assertTrue(saved.answers.map { it.melody }.distinct().size > 1)
    }

    @Test
    fun `audio fallido antes de comenzar no consume escucha y puede reintentarse`() = runTest {
        var fail = true
        val session = InstrumentSession({ 0 }, object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                if (fail) throw IllegalStateException("No se pudo cargar el audio")
                onStarted()
                return 66_150
            }
        })
        session.start(InstrumentLevel.A)
        try { session.listen(); fail("Debía fallar") } catch (_: IllegalStateException) {}
        assertEquals(0, session.state.listensUsed)
        assertEquals(InstrumentPhase.READY, session.state.phase)
        fail = false
        session.listen()
        assertEquals(1, session.state.listensUsed)
    }

    @Test
    fun `abandonar durante una reescucha iniciada guarda solo las respuestas realizadas`() = runTest {
        val file = File(temporaryFolder.root, "instruments.json")
        var cancel = false
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                onStarted()
                if (cancel) throw kotlinx.coroutines.CancellationException("Salir")
                return 66_150
            }
        }
        val session = InstrumentSession({ 0 }, player, JsonInstrumentResults(file))
        session.start(InstrumentLevel.B)
        session.listen()
        session.answer(Instrument.FLUTE)
        session.nextQuestion()
        cancel = true
        try { session.listen() } catch (_: kotlinx.coroutines.CancellationException) {}
        assertEquals(1, session.state.listensUsed)
        session.interrupt()
        session.interrupt()
        val reopened = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file))
        val saved = reopened.history().single()
        assertFalse(saved.completed)
        assertEquals(1, saved.answers.size)
        assertTrue(saved.answers.single().correct)
    }

    @Test
    fun `resultado completo sobrevive a nueva sesion y cada pregunta reinicia escuchas`() = runTest {
        val file = File(temporaryFolder.root, "complete.json")
        val session = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file))
        session.start(InstrumentLevel.A)
        repeat(10) { index ->
            assertEquals(3, session.state.listensRemaining)
            repeat(if (index == 0) 3 else 1) { session.listen() }
            session.answer(Instrument.PIANO)
            session.nextQuestion()
        }
        val saved = InstrumentSession({ 0 }, SuccessfulPlayer(), JsonInstrumentResults(file)).history().single()
        assertTrue(saved.completed)
        assertEquals(10, saved.answers.count { it.correct })
        assertEquals(3, saved.answers.first().listensUsed)
        assertEquals(1, saved.answers.last().listensUsed)
        assertEquals("iowa-pentatonic-mf-v2", saved.bankVersion)
        assertEquals("generated-melody", saved.stimulusKind)
        assertNull(saved.midi)
    }
    @Test
    fun `tres escuchas reales agotan la ayuda sin habilitar una cuarta`() = runTest {
        var plays = 0
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                plays++
                onStarted()
                onStarted() // Una notificación duplicada no consume ayuda adicional.
                return 66_150
            }
        }
        val session = InstrumentSession({ 0 }, player)
        session.start(InstrumentLevel.B)
        repeat(5) { session.listen() }
        assertEquals(3, plays)
        assertEquals(3, session.state.listensUsed)
        assertEquals(0, session.state.listensRemaining)
        session.answer(Instrument.CLARINET)
        assertFalse(session.state.answers.single().correct)
        assertEquals(3, session.state.answers.single().listensUsed)
    }

    @Test
    fun `diez respuestas cierran y persisten una partida una sola vez`() = runTest {
        val results = MemoryInstrumentResults()
        val session = InstrumentSession({ 0 }, SuccessfulPlayer(), results, clock = { 1234L })
        session.start(InstrumentLevel.C)
        repeat(10) { index ->
            session.listen()
            session.answer(if (index % 2 == 0) Instrument.FLUTE else Instrument.OBOE)
            session.answer(Instrument.FLUTE)
            if (index < 9) session.nextQuestion()
        }
        assertEquals(InstrumentPhase.FINISHED, session.state.phase)
        assertEquals(5, session.state.correctCount)
        assertEquals(10, session.state.answers.size)
        session.nextQuestion()
        session.interrupt()
        val reopened = InstrumentSession({ 0 }, SuccessfulPlayer(), results)
        val saved = reopened.history().single()
        assertEquals(10, saved.answers.size)
        assertEquals(InstrumentLevel.C, saved.level)
        assertEquals("manual", saved.origin)
        assertEquals(1234L, saved.finishedAtMillis)
        assertTrue(saved.completed)
        assertEquals(listOf(1,2,3,4,5,6,7,8,9,10), saved.answers.map { it.round })
    }
    @Test
    fun `primera escucha cuenta y las cuatro opciones no revelan el objetivo`() = runTest {
        val session = InstrumentSession({ 0 }, player = SuccessfulPlayer())
        session.start(InstrumentLevel.A)
        assertEquals(4, session.state.options.distinct().size)
        assertEquals(0, session.state.listensUsed)
        session.answer(Instrument.PIANO)
        assertTrue(session.state.answers.isEmpty())
        session.listen()
        assertEquals(1, session.state.listensUsed)
        assertEquals(2, session.state.listensRemaining)
        session.answer(Instrument.PIANO)
        assertTrue(session.state.answers.single().correct)
    }

    private class SuccessfulPlayer : InstrumentPlayer {
        override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
            onStarted()
            return 66_150
        }
    }
}

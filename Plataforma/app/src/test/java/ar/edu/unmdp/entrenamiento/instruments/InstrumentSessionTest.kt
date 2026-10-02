package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class InstrumentSessionTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun `audio fallido antes de comenzar no consume escucha y puede reintentarse`() = runTest {
        var fail = true
        val session = InstrumentSession({ 0 }, object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
            override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
        assertEquals("iowa-c4-mf-v1", saved.bankVersion)
    }
    @Test
    fun `tres escuchas reales agotan la ayuda sin habilitar una cuarta`() = runTest {
        var plays = 0
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
        override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
            onStarted()
            return 66_150
        }
    }
}

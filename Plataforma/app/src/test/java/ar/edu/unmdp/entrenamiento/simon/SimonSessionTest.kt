package ar.edu.unmdp.entrenamiento.simon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlinx.coroutines.test.runTest
import java.io.File
import org.junit.Rule
import org.junit.rules.TemporaryFolder

class SimonSessionTest {
    @get:Rule val temporaryFolder = TemporaryFolder()
    @Test
    fun `la partida presenta una sola nota antes de recibir respuestas`() {
        val session = SimonSession(nextNote = { 2 })
        session.start(Scale.PENTATONIC, tonicMidi = 60)
        assertEquals(listOf(2), session.state.sequence)
        assertEquals(Phase.PLAYBACK, session.state.phase)
    }

    @Test
    fun `una ronda correcta agrega una nota y vuelve a reproducir la secuencia`() = runTest {
        val session = SimonSession(nextNote = { 2 })
        session.start(Scale.PENTATONIC, tonicMidi = 60)
        session.playbackFinished()
        session.answer(2)
        assertEquals(listOf(2, 2), session.state.sequence)
        assertEquals(Phase.PLAYBACK, session.state.phase)
    }

    @Test
    fun `completar cuatro notas y fallar en cinco da puntaje cuatro y conserva el error`() = runTest {
        val session = SimonSession(nextNote = { 2 })
        session.start(Scale.PENTATONIC, tonicMidi = 60)
        repeat(4) { round ->
            session.playbackFinished()
            repeat(round + 1) { session.answer(2) }
        }
        session.playbackFinished()
        session.answer(2)
        session.answer(3)
        assertEquals(Phase.FINISHED, session.state.phase)
        assertEquals(4, session.state.result?.completedLength)
        assertEquals(listOf(2, 2, 2, 2, 2), session.state.result?.attemptedSequence)
        assertEquals(1, session.state.result?.errorPosition)
        session.answer(2)
        assertEquals(4, session.state.result?.completedLength)
    }

    @Test
    fun `un resultado completo sobrevive a una nueva sesion y se guarda una sola vez`() = runTest {
        val file = File(temporaryFolder.root, "results.json")
        val session = SimonSession(nextNote = { 2 }, results = JsonSimonResults(file))
        session.start(Scale.PENTATONIC, tonicMidi = 60)
        session.playbackFinished()
        session.answer(3)
        session.answer(3)

        val reopened = SimonSession(nextNote = { 0 }, results = JsonSimonResults(file))
        assertEquals(1, reopened.history().size)
        assertEquals(0, reopened.history().single().completedLength)
        assertEquals(listOf(2), reopened.history().single().attemptedSequence)
        assertEquals(0, reopened.history().single().errorPosition)
    }

    @Test
    fun `abandonar conserva el resultado como interrumpido sin marcar partida completa`() = runTest {
        val session = SimonSession(nextNote = { 2 })
        session.start(Scale.PENTATONIC, tonicMidi = 60)
        session.playbackFinished()
        session.answer(2)
        session.interrupt()
        session.interrupt()
        assertEquals(Phase.INTERRUPTED, session.state.phase)
        assertEquals(1, session.history().size)
        assertEquals(1, session.history().single().completedLength)
        assertFalse(session.history().single().completed)
    }

    @Test
    fun `cromatica ofrece doce notas y conserva escala y tonica mientras crece`() = runTest {
        val session = SimonSession(nextNote = { it - 1 })
        session.start(Scale.CHROMATIC, tonicMidi = 72)
        assertEquals(listOf(11), session.state.sequence)
        assertEquals((72..83).toList(), session.state.midiNotes)
        session.answer(0)
        assertEquals(Phase.PLAYBACK, session.state.phase)
        session.playbackFinished()
        session.answer(11)
        assertEquals(listOf(11, 11), session.state.sequence)
        assertEquals(Scale.CHROMATIC, session.state.scale)
        assertEquals(72, session.state.tonicMidi)
    }

    @Test
    fun `historial conserva escala tonica origen y fecha del resultado jugado`() = runTest {
        val file = File(temporaryFolder.root, "metadata.json")
        val session = SimonSession({ 6 }, JsonSimonResults(file), clock = { 1234L })
        session.start(Scale.DIATONIC, tonicMidi = 62)
        session.playbackFinished()
        session.answer(0)
        val reopened = SimonSession({ 0 }, JsonSimonResults(file))
        val result = reopened.history().single()
        assertEquals(Scale.DIATONIC, result.scale)
        assertEquals(62, result.tonicMidi)
        assertEquals(1234L, result.startedAtMillis)
        assertEquals(1234L, result.finishedAtMillis)
        assertEquals("manual", result.origin)
        assertFalse(result.id.isBlank())
    }
}

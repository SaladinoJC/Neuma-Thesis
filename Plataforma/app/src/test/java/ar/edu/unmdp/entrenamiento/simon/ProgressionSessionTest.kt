package ar.edu.unmdp.entrenamiento.simon

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class ProgressionSessionTest {
    @Test fun `dos altas automaticas desbloquean diatonica al reabrir sin duplicar`() = runTest {
        val results = MemorySimonResults()
        val session = SimonSession({ 0 }, results)
        suspend fun high() {
            session.startAutomatic(60)
            repeat(5) {
                session.playbackFinished()
                repeat(session.state.sequence.size) { session.answer(0) }
            }
            session.playbackFinished()
            session.answer(1)
        }
        high()
        assertEquals(0, session.progress().level)
        assertEquals(1, session.progress().streak)
        high()
        assertEquals(1, session.progress().level)
        assertEquals(0, session.progress().streak)
        session.answer(1)
        val reopened = SimonSession({ 0 }, results)
        reopened.startAutomatic(60)
        assertEquals(Scale.DIATONIC, reopened.state.scale)
        assertEquals(2, reopened.history().size)
        assertTrue(reopened.history().all { it.mastery?.high == true })
    }
    @Test fun `manuales e interrupciones no cambian racha y una baja la reinicia`() = runTest {
        val results = MemorySimonResults()
        val session = SimonSession({ 0 }, results)
        suspend fun finish(length: Int, automatic: Boolean = true) {
            if (automatic) session.startAutomatic(60) else session.start(Scale.CHROMATIC, 60)
            repeat(length) {
                session.playbackFinished()
                repeat(session.state.sequence.size) { session.answer(0) }
            }
            session.playbackFinished(); session.answer(1)
        }
        finish(5)
        finish(9, false)
        session.startAutomatic(60); session.interrupt()
        assertEquals(1, session.progress().streak)
        finish(0)
        assertEquals(0, session.progress().streak)
        repeat(6) { finish(9) }
        assertEquals(2, session.progress().level)
        assertEquals(2, session.progress().streak)
    }
}

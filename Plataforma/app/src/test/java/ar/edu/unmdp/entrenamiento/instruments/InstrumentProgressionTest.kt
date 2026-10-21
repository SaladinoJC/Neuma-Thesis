package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.File

class InstrumentProgressionTest {
    @get:Rule val temporary = TemporaryFolder()
    @Test fun `dos altas desbloquean B y las partidas ayudadas reinician racha sin perder nivel`() = runTest {
        var target = Instrument.PIANO
        val player = object : InstrumentPlayer {
            override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                target = instrument; onStarted(); return 1
            }
        }
        val file = File(temporary.root, "instruments.json")
        var session = InstrumentSession({ 0 }, player, JsonInstrumentResults(file))
        suspend fun complete(helped: Boolean = false) {
            session.startAutomatic()
            repeat(10) {
                session.listen()
                if (helped) repeat(session.state.listensRemaining) { session.listen() }
                session.answer(target); session.nextQuestion()
            }
        }
        complete()
        assertEquals(1, session.progress().streak)
        complete()
        assertEquals(1, session.progress().level)
        session = InstrumentSession({ 0 }, player, JsonInstrumentResults(file))
        complete()
        assertEquals(InstrumentLevel.B, session.state.level)
        assertEquals(1, session.progress().streak)
        complete(true)
        assertEquals(1, session.progress().level)
        assertEquals(0, session.progress().streak)
        assertEquals(10, session.state.correctCount)
        assertFalse(requireNotNull(session.state.result?.mastery).high)
        assertEquals(4, session.history().size)
    }
}

package ar.edu.unmdp.entrenamiento.history

import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.instruments.*
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class HistorySessionTest {
    @Test fun `historial pagina sin borrar y conserva detalle con filtros`() = runTest {
        val simon = MemorySimonResults()
        repeat(26) { simon.save(SimonResult(it, listOf(0), null, id = "$it", finishedAtMillis = it.toLong(),
            scale = if (it%2 == 0) Scale.PENTATONIC else Scale.DIATONIC, origin = if (it%3 == 0) "automatic" else "manual")) }
        val voice = object : VoiceResults {
            override suspend fun all() = emptyList<VoiceResult>()
            override suspend fun save(result: VoiceResult) {}
        }
        val session = HistorySession(simon, MemoryInstrumentResults(), voice)
        session.load()
        assertEquals(26, session.state.total)
        assertEquals(20, session.state.items.size)
        assertEquals("25", session.state.items.first().simon?.id)
        session.more()
        assertEquals(26, session.state.items.size)
        session.filter(HistoryGame.SIMON, level = 0, origin = "automatic")
        assertEquals(5, session.state.total)
        session.select(session.state.items.first().key)
        assertEquals("24", session.state.selected?.simon?.id)
        assertEquals(26, simon.all().size)
        session.filter(HistoryGame.VOICE)
        assertEquals(0, session.state.total)
    }
    @Test fun `fallo de un historial no oculta voz y el resumen no inventa afinacion en silencio`() = runTest {
        val damaged = object : SimonResults {
            override suspend fun all(): List<SimonResult> = throw java.io.IOException("archivo ilegible")
            override suspend fun save(result: SimonResult) { error("No debe escribir") }
        }
        val result = VoiceResult("v", 0, 1000, "emulador", "34", YinConfiguration(), 44100,
            listOf(VoiceMeasurement(100, 69, PitchEstimate(440.0), 10, 0.0),
                VoiceMeasurement(200, 69, PitchEstimate(), 20, null),
                VoiceMeasurement(300, 69, PitchEstimate(442.0), 30, 8.0)))
        val voice = object : VoiceResults {
            override suspend fun all() = listOf(result)
            override suspend fun save(result: VoiceResult) { error("No debe escribir") }
        }
        val session = HistorySession(damaged, MemoryInstrumentResults(), voice)
        session.load()
        assertTrue(session.state.errors.getValue(HistoryGame.SIMON).contains("ilegible"))
        session.filter(HistoryGame.VOICE)
        session.select(session.state.items.single().key)
        val summary = requireNotNull(session.state.selected?.voiceSummary)
        assertEquals(2, summary.valid)
        assertEquals(3, summary.total)
        assertEquals(4.0, requireNotNull(summary.meanCents), .001)
        assertEquals(4.0, requireNotNull(summary.medianAbsoluteCents), .001)
        assertEquals(20L, summary.processingP50Micros)
        assertEquals(30L, summary.processingP95Micros)
    }
}

package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.tuner.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.*
import java.util.zip.ZipInputStream

class RhythmExportTest {
    @get:Rule val temporary = TemporaryFolder()

    @Test fun exportarYReiniciarIncluyeRitmoSinPerderConfiguracionNiSeguimientoAnterior() = runTest {
        val rhythm = RhythmHistorySession(JsonRhythmResults(File(temporary.root, "rhythm-results.json")))
        rhythm.begin(); rhythm.capture(rhythmResult()); rhythm.savePending()
        val tuner = TunerConfiguration(mode = TunerMode.GUIDED)
        JsonTunerConfiguration(File(temporary.root, "tuner-configuration.json")).save(tuner)
        val tunerHistory = TunerHistorySession(JsonTunerResults(File(temporary.root, "tuner-results.json")))
        val guided = GuidedTunerSession(); guided.begin(tuner.snapshot(), false); guided.interrupt()
        tunerHistory.begin(tuner); tunerHistory.finish(guided.state, EarTunerState()); tunerHistory.savePending()
        val data = Demonstration(temporary.root)
        val exported = Json.parseToJsonElement(data.export("test")).jsonObject
        assertEquals(1, exported["rhythm"]?.jsonArray?.size)
        assertEquals(1, exported["tuner"]?.jsonArray?.size)
        assertEquals(tuner.currentGuitarId, exported["tunerConfiguration"]?.jsonObject?.get("currentGuitarId")?.jsonPrimitive?.content)
        data.reset()
        assertTrue(JsonRhythmResults(File(temporary.root, "rhythm-results.json")).all().isEmpty())
        assertTrue(JsonTunerResults(File(temporary.root, "tuner-results.json")).all().isEmpty())
    }

    @Test fun recuperacionConservaBytesDeRitmoIlegibleYReinicioInterrumpidoLoElimina() = runTest {
        val file = File(temporary.root, "rhythm-results.json")
        val original = "{\"schemaVersion\":99,\"results\":[]}"
        file.writeText(original)
        val history = RhythmHistorySession(JsonRhythmResults(file))
        history.load(); assertNotNull(history.state.error)
        history.begin(); history.capture(rhythmResult()); history.savePending()
        assertNotNull(history.state.error)
        assertEquals(original, file.readText())
        val data = Demonstration(temporary.root)
        val output = ByteArrayOutputStream(); data.writeRecovery(output)
        val recovered = mutableMapOf<String, String>()
        ZipInputStream(ByteArrayInputStream(output.toByteArray())).use { zip ->
            while (true) { val entry = zip.nextEntry ?: break; recovered[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
        }
        assertEquals(original, recovered["rhythm-results.json"])
        File(temporary.root, "reset-pending").writeText("pending")
        Demonstration(temporary.root)
        assertFalse(file.exists())
    }

    @Test fun pendientesSeExportanSeparadosYLosDocumentosAnterioresSiguenLeyendose() = runTest {
        val history = RhythmHistorySession(MemoryRhythmResults())
        history.begin(); history.capture(rhythmResult(complete = false))
        val data = Demonstration(temporary.root)
        val exported = data.export("test", pendingRhythmSaves = history.state.pending)
        val decoded = Json.decodeFromString(DemonstrationExport.serializer(), exported)
        assertTrue(decoded.rhythm.isEmpty())
        assertEquals(history.state.pending, decoded.pendingRhythmSaves)
        assertFalse(exported.contains("\"samples\""))
        val old = Json.parseToJsonElement(exported).jsonObject.toMutableMap()
        old.remove("rhythm"); old.remove("pendingRhythmSaves")
        val previous = Json.decodeFromJsonElement(DemonstrationExport.serializer(), JsonObject(old))
        assertTrue(previous.rhythm.isEmpty()); assertTrue(previous.pendingRhythmSaves.isEmpty())
        val output = ByteArrayOutputStream()
        data.writeRecovery(output, pendingRhythmSaves = history.state.pending)
        ZipInputStream(ByteArrayInputStream(output.toByteArray())).use { zip ->
            assertEquals("pending-rhythm-saves.json", zip.nextEntry.name)
            assertEquals(history.state.pending.single().id, Json.parseToJsonElement(zip.readBytes().toString(Charsets.UTF_8))
                .jsonArray.single().jsonObject.getValue("id").jsonPrimitive.content)
        }
    }
}

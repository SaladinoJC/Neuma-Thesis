package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.tuner.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.io.*
import java.util.zip.ZipInputStream

class TunerExportTest {
    @get:Rule val temporary = TemporaryFolder()
    @Test fun exportacionIncluyeAfinadorYPendientesSinRomperDatosAnterioresYReinicioLimpiaAmbosArchivos() = runTest {
        val configuration = TunerConfiguration(mode = TunerMode.GUIDED, saveHistory = false)
        JsonTunerConfiguration(File(temporary.root, "tuner-configuration.json")).save(configuration)
        val history = TunerHistorySession(JsonTunerResults(File(temporary.root, "tuner-results.json")))
        val guided = GuidedTunerSession()
        history.begin(configuration.copy(saveHistory = true)); guided.begin(configuration.snapshot(), true); guided.interrupt()
        history.finish(guided.state, EarTunerState())
        val pending = history.state.pending
        history.savePending()
        val data = Demonstration(temporary.root)
        val exported = Json.decodeFromString(DemonstrationExport.serializer(), data.export("test", pendingTunerSaves = pending))
        assertEquals(1, exported.tuner.size)
        assertEquals(pending.single().id, exported.pendingTunerSaves.single().id)
        assertFalse(exported.tunerConfiguration!!.saveHistory)
        val old = Json.parseToJsonElement(data.export("old")).jsonObject.toMutableMap()
        old.remove("tuner"); old.remove("pendingTunerSaves"); old.remove("tunerConfiguration")
        assertTrue(Json.decodeFromJsonElement(DemonstrationExport.serializer(), JsonObject(old)).tuner.isEmpty())
        val output = ByteArrayOutputStream(); data.writeRecovery(output, pendingTunerSaves = pending)
        val recovered = mutableListOf<String>()
        ZipInputStream(ByteArrayInputStream(output.toByteArray())).use { zip ->
            while (true) { val entry = zip.nextEntry ?: break; recovered += entry.name }
        }
        assertTrue(recovered.containsAll(listOf("tuner-results.json", "tuner-configuration.json", "pending-tuner-saves.json")))
        data.reset()
        assertTrue(JsonTunerResults(File(temporary.root, "tuner-results.json")).all().isEmpty())
        assertTrue(JsonTunerConfiguration(File(temporary.root, "tuner-configuration.json")).load().saveHistory)
    }
    @Test fun resultadosDanadosNoSeSobrescribenYRecuperacionConservaOriginalAntesDelReinicio() = runTest {
        val file = File(temporary.root, "tuner-results.json")
        val original = "{\"schemaVersion\":99,\"results\":[]}"
        file.writeText(original)
        val history = TunerHistorySession(JsonTunerResults(file))
        history.load(); assertNotNull(history.state.error)
        val config = TunerConfiguration(mode = TunerMode.GUIDED)
        val guided = GuidedTunerSession()
        history.begin(config); guided.begin(config.snapshot(), true); guided.interrupt()
        history.finish(guided.state, EarTunerState()); history.savePending()
        assertNotNull(history.state.error)
        assertEquals(original, file.readText())
        val data = Demonstration(temporary.root)
        val output = ByteArrayOutputStream(); data.writeRecovery(output, pendingTunerSaves = history.state.pending)
        val recovered = mutableMapOf<String, String>()
        ZipInputStream(ByteArrayInputStream(output.toByteArray())).use { zip ->
            while (true) { val entry = zip.nextEntry ?: break; recovered[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
        }
        assertEquals(original, recovered["tuner-results.json"])
        assertTrue(recovered.getValue("pending-tuner-saves.json").contains("default-guitar"))
        File(temporary.root, "reset-pending").writeText("pending")
        val reopened = Demonstration(temporary.root)
        assertTrue(Json.decodeFromString(DemonstrationExport.serializer(), reopened.export("test")).tuner.isEmpty())
    }
}

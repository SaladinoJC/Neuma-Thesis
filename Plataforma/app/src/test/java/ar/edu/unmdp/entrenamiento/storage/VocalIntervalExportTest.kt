package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.voice.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipInputStream
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import ar.edu.unmdp.entrenamiento.ui.DemonstrationViewModel
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder

class VocalIntervalExportTest {
    @get:Rule val temporary = TemporaryFolder()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test fun `data actions forward pending vocal snapshots to both JSON and recovery ZIP`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val history = VocalIntervalHistorySession(MemoryVocalIntervalResults(), newId = { "pending-vocal" })
            history.begin(57, 7, 1, "JVM" to "synthetic", "test")
            val round = VocalIntervalSession(7)
            round.beginAnchor(); round.interrupt()
            history.finish(round.state.result!!, null, null)
            val model = DemonstrationViewModel(Demonstration(temporary.root), {}, {},
                pendingVocalIntervals = { history.state.pending })
            model.prepareExport(); model.ui.first { it.pickerReady }; model.pickerLaunched()
            val json = ByteArrayOutputStream()
            model.export { json }; model.ui.first { !it.busy }
            val exported = Json.parseToJsonElement(json.toString("UTF-8")).jsonObject
            assertEquals("pending-vocal", exported.getValue("pendingVocalIntervalSaves").jsonArray.single().jsonObject.getValue("id").jsonPrimitive.content)
            model.prepareRecovery(); model.ui.first { it.recoveryReady }; model.recoveryLaunched()
            val bytes = ByteArrayOutputStream()
            model.exportRecovery { bytes }; model.ui.first { !it.busy }
            val recovered = mutableMapOf<String, String>()
            ZipInputStream(bytes.toByteArray().inputStream()).use { zip ->
                while (true) { val entry = zip.nextEntry ?: break; recovered[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
            }
            assertTrue(recovered.getValue("pending-vocal-interval-saves.json").contains("pending-vocal"))
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `saved vocal rounds export separately from the technical lab and participate in recovery and reset`() = runTest {
        val file = File(temporary.root, "vocal-interval-results.json")
        val history = VocalIntervalHistorySession(JsonVocalIntervalResults(file), now = { 1234 }, newId = { "vocal-1" })
        history.begin(57, 7, 1, "JVM" to "synthetic", "test")
        val round = VocalIntervalSession(7)
        round.beginAnchor(); round.accept(0, PitchEstimate(220.0), 20); round.interrupt("user")
        history.finish(requireNotNull(round.state.result), 44100, null)
        history.savePending()
        val data = Demonstration(temporary.root)
        val export = Json.parseToJsonElement(data.export("test")).jsonObject
        assertTrue(export.getValue("voice").jsonArray.isEmpty())
        val saved = export.getValue("vocalIntervals").jsonArray.single().jsonObject
        assertEquals("vocal-1", saved.getValue("id").jsonPrimitive.content)
        assertEquals("user", saved.getValue("result").jsonObject.getValue("interruption").jsonPrimitive.content)
        assertTrue(export.getValue("pendingVocalIntervalSaves").jsonArray.isEmpty())
        val oldShape = JsonObject(export.filterKeys { it !in setOf("vocalIntervals", "pendingVocalIntervalSaves", "vocalIntervalProgress") })
        val oldExport = Json.decodeFromJsonElement(DemonstrationExport.serializer(), oldShape)
        assertTrue(oldExport.vocalIntervals.isEmpty())
        assertTrue(oldExport.pendingVocalIntervalSaves.isEmpty())
        val bytes = ByteArrayOutputStream()
        data.writeRecovery(bytes)
        val recovered = mutableMapOf<String, String>()
        ZipInputStream(bytes.toByteArray().inputStream()).use { zip ->
            while (true) { val entry = zip.nextEntry ?: break; recovered[entry.name] = zip.readBytes().toString(Charsets.UTF_8) }
        }
        assertTrue(recovered.getValue("vocal-interval-results.json").contains("vocal-1"))
        data.reset()
        assertFalse(file.exists())
        assertTrue(Json.parseToJsonElement(Demonstration(temporary.root).export("test")).jsonObject.getValue("vocalIntervals").jsonArray.isEmpty())
    }
}

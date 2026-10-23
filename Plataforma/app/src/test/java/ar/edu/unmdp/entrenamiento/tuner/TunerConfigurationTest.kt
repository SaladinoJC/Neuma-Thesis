package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.coroutines.*
import kotlin.math.abs
import ar.edu.unmdp.entrenamiento.voice.YinEstimator
import ar.edu.unmdp.entrenamiento.voice.referenceTone

class TunerConfigurationTest {
    @Test fun `seis presets conservan las alturas aprobadas y numeracion`() = runTest {
        val session = TunerConfigurationSession(MemoryTunerConfiguration())
        session.load()
        val expected = mapOf(
            "standard" to listOf(40, 45, 50, 55, 59, 64), "drop-d" to listOf(38, 45, 50, 55, 59, 64),
            "half-down" to listOf(39, 44, 49, 54, 58, 63), "drop-c" to listOf(36, 43, 48, 53, 57, 62),
            "open-g" to listOf(38, 43, 50, 55, 59, 62), "dadgad" to listOf(38, 45, 50, 55, 57, 62),
        )
        for ((id, notes) in expected) {
            session.selectTuning(id)
            val configuration = requireNotNull(session.state.configuration)
            assertEquals(notes, configuration.currentTuning.notesSixToFirst)
            val tuning = TunerSession(); tuning.configure(configuration.snapshot())
            for (index in notes.indices) {
                tuning.fixString(6 - index)
                assertEquals(notes[index], tuning.state.targetMidi)
            }
        }
    }
    @Test fun `personalizada respeta orden no ascendente duplicados y notas con octava`() = runTest {
        val setup = TunerConfigurationSession(MemoryTunerConfiguration(), newId = { "custom" })
        setup.load()
        setup.saveCustomNames("No ascendente", listOf("Mi4", "Do2", "F#3", "Sib3", "Do2", "E2"))
        val configuration = requireNotNull(setup.state.configuration)
        assertEquals(listOf(64, 36, 54, 58, 36, 40), configuration.currentTuning.notesSixToFirst)
        val session = TunerSession(); session.configure(configuration.snapshot()); session.start()
        session.measure(PitchEstimate(65.406391), 0)
        assertNull(session.state.stringNumber) // Dos cuerdas comparten Do2: no inventar una.
        session.fixString(5); session.measure(PitchEstimate(65.406391), 100)
        assertEquals(5, session.state.stringNumber); assertEquals(36, session.state.targetMidi)
    }
    @Test fun `entradas invalidas no reemplazan configuracion guardada`() = runTest {
        val store = MemoryTunerConfiguration()
        val setup = TunerConfigurationSession(store)
        setup.load(); setup.selectTuning("drop-c")
        val before = store.load()
        val invalid = listOf(
            listOf("Mi", "La2", "Re3", "Sol3", "Si3", "Mi4"),
            listOf("Si1", "La2", "Re3", "Sol3", "Si3", "Mi4"),
            listOf("Mi2", "La2", "Re3", "Sol3", "Si3", "Fa4"),
            listOf("Mi2", "La2", "Re3", "Sol3", "Si3"),
        )
        for (notes in invalid) {
            setup.saveCustomNames("Inválida", notes)
            assertNotNull(setup.state.error)
            assertEquals(before, store.load())
        }
        setup.createGuitar(" ")
        assertNotNull(setup.state.error); assertEquals(before, store.load())
        setup.saveCustom(" ", listOf(40, 45, 50, 55, 59, 64))
        assertNotNull(setup.state.error); assertEquals(before, store.load())
        setup.saveCustom("Editar preset", listOf(40, 45, 50, 55, 59, 64), "standard")
        assertNotNull(setup.state.error); assertEquals(before, store.load())
    }
    @Test fun `archivo corrupto incompleto o futuro no se sobreescribe y se recupera sin interpretar`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("tuner-invalid").toFile()
        try {
            val file = File(directory, "tuner-configuration.json")
            val data = Demonstration(directory)
            for (original in listOf("{truncado", "{}", "{\"schemaVersion\":99}")) {
                file.writeText(original)
                val store = JsonTunerConfiguration(file)
                val setup = TunerConfigurationSession(store)
                setup.load(); assertNull(setup.state.configuration); assertNotNull(setup.state.error)
                setup.createGuitar("No guardar")
                try { store.save(TunerConfiguration()); fail("Debe conservar el original") } catch (_: Exception) { }
                val recovery = ByteArrayOutputStream(); data.writeRecovery(recovery)
                ZipInputStream(recovery.toByteArray().inputStream()).use { zip ->
                    assertEquals("tuner-configuration.json", zip.nextEntry.name)
                    assertEquals(original, zip.readBytes().toString(Charsets.UTF_8))
                }
                assertEquals(original, file.readText())
                data.reset(); setup.load()
                assertEquals("Guitarra", requireNotNull(setup.state.configuration).currentGuitar.name)
            }
        } finally { directory.deleteRecursively() }
    }
    @Test fun `guardado fallido conserva datos y cancelacion no publica exito tardio`() = runTest {
        val underlying = MemoryTunerConfiguration()
        var failSave = true
        val saving = CompletableDeferred<Unit>()
        val store = object : TunerConfigurationStore {
            override suspend fun load() = underlying.load()
            override suspend fun save(configuration: TunerConfiguration) {
                if (failSave) error("Disco no disponible")
                saving.complete(Unit); awaitCancellation()
            }
        }
        val setup = TunerConfigurationSession(store)
        setup.load(); setup.createGuitar("Fallida")
        assertNotNull(setup.state.error)
        assertEquals("Guitarra", requireNotNull(setup.state.configuration).currentGuitar.name)
        setup.load(); failSave = false
        val job = launch { setup.createGuitar("Cancelada") }
        saving.await(); job.cancelAndJoin()
        assertTrue(job.isCancelled); assertNull(setup.state.error)
        assertEquals("Guitarra", requireNotNull(setup.state.configuration).currentGuitar.name)
        assertEquals("Guitarra", underlying.load().currentGuitar.name)
    }
    @Test fun `rango provisional completo tiene referencia sintetica y estimacion sin cambiar octava`() {
        val estimator = YinEstimator()
        var maximum = 0.0
        for (midi in 36..64) {
            val pcm = referenceTone(midi)
            val estimate = estimator.estimate(FloatArray(4096) { pcm[8000 + it] / 32768f })
            assertEquals(midi, estimate.midi)
            val error = abs(requireNotNull(estimate.cents(midi)))
            maximum = maxOf(maximum, error)
            assertTrue("MIDI $midi: $error cents", error < 1.0)
        }
        println("A2 synthetic range MIDI36..64 frames4096 sampleRate44100 maxCents=$maximum")
    }
    @Test fun `marcador de reinicio elimina tambien configuracion y export anterior sigue legible`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("tuner-reset").toFile()
        try {
            File(directory, "tuner-configuration.json").writeText("{\"schemaVersion\":99}")
            File(directory, "reset-pending").writeText("pending")
            val data = Demonstration(directory)
            val exported = Json.decodeFromString(DemonstrationExport.serializer(), data.export("test"))
            assertNull(exported.tunerConfiguration)
            assertTrue(exported.simon.isEmpty()); assertTrue(exported.instruments.isEmpty()); assertTrue(exported.voice.isEmpty())
            val legacy = JsonObject(Json.parseToJsonElement(data.export("test")).jsonObject.toMutableMap().apply { remove("tunerConfiguration") }).toString()
            assertNull(Json.decodeFromString(DemonstrationExport.serializer(), legacy).tunerConfiguration)
        } finally { directory.deleteRecursively() }
    }
    @Test fun `editar personalizada no reinterpreta copia y exportacion recuperacion reinicio incluyen configuracion`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("tuner-custom").toFile()
        try {
            val file = File(directory, "tuner-configuration.json")
            val setup = TunerConfigurationSession(JsonTunerConfiguration(file), newId = { "custom-1" })
            setup.load()
            setup.saveCustom("Mi afinación", listOf(40, 45, 50, 55, 59, 62))
            val old = requireNotNull(setup.state.configuration).snapshot()
            setup.saveCustom("Mi afinación corregida", listOf(38, 45, 50, 55, 59, 64), "custom-1")
            assertEquals(40, old.tuning.notesSixToFirst.first())
            assertEquals(1, old.tuning.revision)
            val exported = Json.decodeFromString(DemonstrationExport.serializer(), Demonstration(directory).export("test"))
            val configuration = requireNotNull(exported.tunerConfiguration)
            assertEquals(38, configuration.currentTuning.notesSixToFirst.first())
            assertEquals(2, configuration.currentTuning.revision)
            assertTrue(exported.voice.isEmpty())
            val output = ByteArrayOutputStream()
            Demonstration(directory).writeRecovery(output)
            ZipInputStream(output.toByteArray().inputStream()).use { zip ->
                assertEquals("tuner-configuration.json", zip.nextEntry.name)
                assertEquals(file.readText(), zip.readBytes().toString(Charsets.UTF_8))
            }
            Demonstration(directory).reset()
            setup.load()
            assertTrue(requireNotNull(setup.state.configuration).customTunings.isEmpty())
            assertEquals("standard", requireNotNull(setup.state.configuration).currentTuning.id)
        } finally { directory.deleteRecursively() }
    }
    @Test fun `guitarra afinacion y preferencias se releen sin iniciar captura`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("tuner-config").toFile()
        try {
            val store = JsonTunerConfiguration(File(directory, "tuner-configuration.json"))
            val setup = TunerConfigurationSession(store, newId = { "guitar-2" })
            setup.load()
            assertEquals("Guitarra", setup.state.configuration!!.currentGuitar.name)
            setup.createGuitar("Acústica")
            setup.selectTuning("drop-d")
            setup.preferences(TunerCriterion.PRO, TunerTracking.STRINGS)
            val reopened = TunerConfigurationSession(store)
            reopened.load()
            val configuration = requireNotNull(reopened.state.configuration)
            assertEquals("Acústica", configuration.currentGuitar.name)
            assertEquals(38, configuration.currentTuning.notesSixToFirst.first())
            assertEquals(TunerCriterion.PRO, configuration.criterion)
            val session = TunerSession()
            session.configure(configuration.snapshot())
            assertFalse(session.state.running)
            session.start()
            session.measure(PitchEstimate(73.416192), 0)
            assertEquals(6, session.state.stringNumber)
            assertEquals(38, session.state.targetMidi)
        } finally { directory.deleteRecursively() }
    }
}

package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.simon.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

class DataResilienceTest {
    @get:Rule val temporary = TemporaryFolder()
    @Test fun `pendiente de voz se exporta separado y reintentar no duplica`() = runTest {
        val pending = ar.edu.unmdp.entrenamiento.voice.VoiceResult("pending", 1, 2, "JVM", "test",
            ar.edu.unmdp.entrenamiento.voice.YinConfiguration(), 44100, emptyList())
        val data = Demonstration(temporary.root)
        val exported = kotlinx.serialization.json.Json.parseToJsonElement(data.export("test", pendingVoiceSave = pending))
        assertTrue(exported.toString().contains("\"pendingVoiceSave\":{\"id\":\"pending\""))
        val output = ByteArrayOutputStream()
        data.writeRecovery(output, pending)
        ZipInputStream(output.toByteArray().inputStream()).use { zip ->
            assertEquals("pending-voice-save.json", zip.nextEntry.name)
            assertTrue(zip.readBytes().toString(Charsets.UTF_8).contains("\"pending\""))
        }
        val store = ar.edu.unmdp.entrenamiento.voice.JsonVoiceResults(File(temporary.root, "voice-results.json"))
        store.save(pending); store.save(pending)
        assertEquals(listOf("pending"), store.all().map { it.id })
    }
    @Test fun `descartar sesiones activas para reinicio evita guardar al salir después`() = runTest {
        val simon = SimonSession({ 0 }, JsonSimonResults(File(temporary.root, "simon-results.json")))
        val instruments = ar.edu.unmdp.entrenamiento.instruments.InstrumentSession({ 0 },
            object : ar.edu.unmdp.entrenamiento.instruments.InstrumentPlayer {
                override suspend fun play(instrument: ar.edu.unmdp.entrenamiento.instruments.Instrument,
                    melody: ar.edu.unmdp.entrenamiento.instruments.InstrumentMelody, onStarted: () -> Unit) = 1
            }, ar.edu.unmdp.entrenamiento.instruments.JsonInstrumentResults(File(temporary.root, "instrument-results.json")))
        simon.startAutomatic(60); instruments.startAutomatic()
        simon.discardForReset(); instruments.discardForReset()
        Demonstration(temporary.root).reset()
        simon.interrupt(); instruments.interrupt()
        assertTrue(simon.history().isEmpty()); assertTrue(instruments.history().isEmpty())
    }
    @Test fun `dos instancias conservan todos los guardados concurrentes`() = runTest {
        val file = File(temporary.root, "simon-results.json")
        coroutineScope {
            repeat(30) { index -> launch(Dispatchers.IO) {
                JsonSimonResults(file).save(SimonResult(index, listOf(0), 0, id = "concurrent-$index"))
            } }
        }
        assertEquals(30, JsonSimonResults(file).all().size)
    }
    @Test fun `archivos dañados o futuros se conservan y pueden recuperarse sin parsear`() = runTest {
        val data = Demonstration(temporary.root)
        val file = File(temporary.root, "simon-results.json")
        listOf("{truncado", "{\"schemaVersion\":99,\"results\":[]}").forEach { original ->
            file.writeText(original)
            try {
                JsonSimonResults(file).save(SimonResult(0, listOf(0), 0))
                fail("Debe rechazar el documento")
            } catch (_: IllegalArgumentException) { } catch (_: IllegalStateException) { }
            assertEquals(original, file.readText())
            val output = ByteArrayOutputStream()
            data.writeRecovery(output)
            ZipInputStream(output.toByteArray().inputStream()).use { zip ->
                assertEquals("simon-results.json", zip.nextEntry.name)
                assertEquals(original, zip.readBytes().toString(Charsets.UTF_8))
            }
            assertEquals(original, file.readText())
        }
        data.reset()
        assertTrue(JsonSimonResults(file).all().isEmpty())
    }
}

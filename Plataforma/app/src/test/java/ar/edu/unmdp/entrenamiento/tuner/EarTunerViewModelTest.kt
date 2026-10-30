package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class EarTunerViewModelTest {
    @Test fun comprobarYReferenciaDrenanCapturaYLosCallbacksViejosNoRevelanLecturas() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0; var released = 0
            lateinit var send: suspend (PitchEstimate, Long, Int) -> Unit
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++; send = onMeasurement
                    try { awaitCancellation() } finally { withContext(NonCancellable) { delay(50) }; released++ }
                }
            }
            val player = object : TunerReferencePlayer {
                override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int {
                    assertEquals(opened, released); assertEquals(40, midi)
                    delay(500); return 22050
                }
            }
            val model = TunerViewModel(recorder, referencePlayer = player, clock = { testScheduler.currentTime })
            model.mode(TunerMode.TRAINING); model.awaitConfigurationIdle(); model.start(); runCurrent()
            val old = send
            repeat(6) { send(PitchEstimate(82.406889), 0, 44100); advanceTimeBy(100) }
            assertNull(model.ui.value.tuning.cents)
            model.checkEar(); runCurrent(); old(PitchEstimate(82.406889), 0, 44100)
            advanceTimeBy(60); runCurrent()
            assertEquals(2, opened)
            assertTrue(model.ui.value.ear.results.isEmpty())
            val beforeReference = send
            model.playReference(); runCurrent()
            advanceTimeBy(1200); runCurrent()
            assertEquals(3, opened)
            beforeReference(PitchEstimate(82.406889), 0, 44100)
            assertTrue(model.ui.value.ear.results.isEmpty())
            repeat(5) { send(PitchEstimate(82.406889), 0, 44100); advanceTimeBy(100) }
            assertTrue(model.ui.value.ear.results.single().initialPassed)
            model.playReference(); runCurrent(); model.stop()
            advanceTimeBy(2000); runCurrent()
            assertEquals(3, opened)
            assertEquals(EarPhase.INTERRUPTED, model.ui.value.ear.phase)
            assertFalse(model.ui.value.tuning.running)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun modoYClasificacionPersistenYArchivosAnterioresMantienenSusPreferencias() = runTest {
        val directory = kotlin.io.path.createTempDirectory("ear-preferences").toFile()
        try {
            val file = File(directory, "tuner-configuration.json")
            val store = JsonTunerConfiguration(file)
            val previous = TunerConfiguration(mode = TunerMode.GUIDED, finalReview = false, referenceTimbre = ReferenceTimbre.PIANO)
            store.save(previous)
            val fields = Json.parseToJsonElement(file.readText()).jsonObject.toMutableMap()
            fields.remove("earClassification"); fields.remove("saveHistory"); file.writeText(JsonObject(fields).toString())
            assertEquals(previous, store.load())
            val session = TunerConfigurationSession(store); session.load()
            session.guidedPreferences(TunerMode.TRAINING, true); session.earClassification(true); session.saveHistory(false)
            val reopened = JsonTunerConfiguration(file).load()
            assertEquals(TunerMode.TRAINING, reopened.mode)
            assertTrue(reopened.earClassification)
            assertFalse(reopened.saveHistory)
            assertEquals(ReferenceTimbre.PIANO, reopened.referenceTimbre)
        } finally { directory.deleteRecursively() }
    }
}

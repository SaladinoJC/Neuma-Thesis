package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlin.math.pow

@OptIn(ExperimentalCoroutinesApi::class)
class GuidedTunerViewModelTest {
    @Test fun `preparacion guiada muestra criterio actual sin iniciar captura`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++; awaitCancellation()
                }
            }
            val model = TunerViewModel(recorder, MemoryTunerConfiguration()) { testScheduler.currentTime }
            model.awaitConfigurationIdle()
            model.mode(TunerMode.GUIDED); model.awaitConfigurationIdle()
            model.criterion(TunerCriterion.PRO); model.awaitConfigurationIdle()
            assertEquals(TunerCriterion.PRO, model.ui.value.tuning.criterion)
            assertFalse(model.ui.value.tuning.running)
            assertEquals(0, opened)
            model.start(); runCurrent()
            model.criterion(TunerCriterion.NORMAL); model.awaitConfigurationIdle()
            assertEquals(GuidedPhase.INTERRUPTED, model.ui.value.guided.phase)
            assertEquals(TunerCriterion.PRO, model.ui.value.guided.setup!!.criterion)
            assertEquals(TunerCriterion.NORMAL, model.ui.value.tuning.criterion)
            assertFalse(model.ui.value.tuning.running)
            assertEquals(1, opened)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `captura guiada completa dos pasadas sin guardar desempeno y al reabrir no inicia sola`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var released = 0; var opened = 0
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++
                    try {
                        repeat(2) {
                            for (midi in listOf(40, 45, 50, 55, 59, 64)) {
                                repeat(11) {
                                    onMeasurement(PitchEstimate(440 * 2.0.pow((midi - 69) / 12.0)), 0, 44100)
                                    delay(100)
                                }
                            }
                        }
                        awaitCancellation()
                    } finally { released++ }
                }
            }
            val store = MemoryTunerConfiguration()
            val model = TunerViewModel(recorder, store) { testScheduler.currentTime }
            model.awaitConfigurationIdle()
            model.mode(TunerMode.GUIDED); model.awaitConfigurationIdle()
            model.start(); runCurrent(); advanceTimeBy(13_300); runCurrent()
            assertTrue(model.ui.value.guided.checked)
            assertEquals(6, model.ui.value.guided.firstPass.size)
            assertEquals(6, model.ui.value.guided.review.size)
            assertFalse(model.ui.value.tuning.running)
            assertEquals(1, released)
            model.stop(); model.stop()
            assertEquals(GuidedPhase.COMPLETE, model.ui.value.guided.phase)
            val reopened = TunerViewModel(recorder, store) { testScheduler.currentTime }
            reopened.awaitConfigurationIdle()
            assertEquals(TunerMode.GUIDED, reopened.ui.value.configuration.configuration!!.mode)
            assertEquals(GuidedPhase.READY, reopened.ui.value.guided.phase)
            assertFalse(reopened.ui.value.tuning.running)
            assertEquals(1, opened)
            model.resetDisplay(); model.awaitConfigurationIdle()
            assertTrue(model.ui.value.guided.firstPass.isEmpty())
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `configuracion A2 se lee con defaults y preferencias guiadas sobreviven cierre`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("guided-config").toFile()
        try {
            val file = File(directory, "tuner-configuration.json")
            val store = JsonTunerConfiguration(file)
            store.save(TunerConfiguration())
            val old = Json.parseToJsonElement(file.readText()).jsonObject.toMutableMap()
            old.remove("mode"); old.remove("finalReview")
            file.writeText(JsonObject(old).toString())
            assertEquals(TunerMode.FREE, store.load().mode)
            assertTrue(store.load().finalReview)
            val setup = TunerConfigurationSession(store)
            setup.load(); setup.guidedPreferences(TunerMode.GUIDED, false)
            val reopened = JsonTunerConfiguration(file).load()
            assertEquals(TunerMode.GUIDED, reopened.mode)
            assertFalse(reopened.finalReview)
            assertEquals(1, reopened.schemaVersion)
        } finally { directory.deleteRecursively() }
    }
}

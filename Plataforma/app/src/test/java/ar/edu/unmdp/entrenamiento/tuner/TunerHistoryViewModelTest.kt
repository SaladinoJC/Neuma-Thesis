package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.pow

@OptIn(ExperimentalCoroutinesApi::class)
class TunerHistoryViewModelTest {
    @Test fun recorridoCompletoSeGuardaAlTerminarYElSiguienteSinSeguimientoSoloDejaResumen() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val store = MemoryTunerResults()
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    for (midi in listOf(40, 45, 50, 55, 59, 64)) repeat(11) {
                        onMeasurement(PitchEstimate(440 * 2.0.pow((midi - 69) / 12.0)), 0, 44100); delay(100)
                    }
                    awaitCancellation()
                }
            }
            val model = TunerViewModel(recorder, resultsStore = store, clock = { testScheduler.currentTime })
            model.mode(TunerMode.GUIDED); model.finalReview(false); model.awaitConfigurationIdle()
            model.start(); advanceTimeBy(7000); runCurrent(); model.awaitConfigurationIdle()
            assertEquals(GuidedPhase.COMPLETE, model.ui.value.guided.phase)
            assertTrue(store.all().single().completed)
            assertEquals(400L, store.all().single().stableRequiredMillis)
            assertEquals(.003, store.all().single().analysisConfiguration.minimumRms, 0.0)
            assertEquals("tuner-plucked-v2", store.all().single().configurationVersion)
            assertEquals(6, store.all().single().observations.size)
            model.stop(); model.stop(); model.awaitConfigurationIdle()
            assertEquals(1, store.all().size)
            model.createGuitar("Otra guitarra"); model.awaitConfigurationIdle()
            model.saveHistory(false); model.awaitConfigurationIdle()
            model.start(); advanceTimeBy(7000); runCurrent(); model.awaitConfigurationIdle()
            assertEquals(GuidedPhase.COMPLETE, model.ui.value.guided.phase)
            assertEquals(6, model.ui.value.guided.firstPass.size)
            assertEquals(1, store.all().size)
            assertEquals("Guitarra", store.all().single().setup.guitar.name)
            assertTrue(model.pendingForExport().isEmpty())
            model.stop()
        } finally { Dispatchers.resetMain() }
    }
    @Test fun falloVisibleConservaPendienteExportableConReferenciasSolicitadasYReintentoNoDuplica() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val saved = MemoryTunerResults()
            var fail = true
            val store = object : TunerResults {
                override suspend fun all() = saved.all()
                override suspend fun save(result: TunerResult) { saved.save(result); if (fail) { fail = false; error("Escritura sin confirmar") } }
            }
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) { awaitCancellation() }
            }
            val player = object : TunerReferencePlayer {
                override suspend fun play(timbre: ReferenceTimbre, midi: Int) = 44100
            }
            val model = TunerViewModel(recorder, referencePlayer = player, resultsStore = store, clock = { testScheduler.currentTime })
            model.mode(TunerMode.TRAINING); model.awaitConfigurationIdle(); model.start(); runCurrent()
            model.referenceTimbre(ReferenceTimbre.PIANO); runCurrent()
            model.playReference(); runCurrent(); model.stop(); runCurrent(); model.awaitConfigurationIdle()
            assertNotNull(model.ui.value.history.error)
            val pending = model.pendingForExport().single()
            assertEquals(ReferenceTimbre.GUITAR, pending.referenceTimbre)
            assertEquals(ReferenceTimbre.PIANO, pending.requestedReferences.single().timbre)
            assertEquals(40, pending.requestedReferences.single().targetMidi)
            model.retryHistory(); runCurrent(); model.awaitConfigurationIdle()
            assertNull(model.ui.value.history.error)
            assertTrue(model.pendingForExport().isEmpty())
            assertEquals(1, saved.all().size)
            model.stop()
        } finally { Dispatchers.resetMain() }
    }
}

package ar.edu.unmdp.entrenamiento

import android.Manifest
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.tuner.*
import ar.edu.unmdp.entrenamiento.ui.TunerApp
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow

/** Recorrido de UI/captura sustituida: no es una prueba de afinación acústica. */
class GuidedTunerAndroidTest {
    @get:Rule val compose = createComposeRule()
    @Test fun dosPasadasMuestranResumenYConjuntoComprobado() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.executeShellCommand(
            "pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").close()
        val clock = AtomicLong()
        val samples = Channel<Pair<Int, Long>>(Channel.UNLIMITED)
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                for ((midi, time) in samples) {
                    clock.set(time)
                    onMeasurement(PitchEstimate(440 * 2.0.pow((midi - 69) / 12.0)), time, 44100)
                }
            }
        }
        lateinit var model: TunerViewModel
        instrumentation.runOnMainSync { model = TunerViewModel(recorder, clock = clock::get) }
        compose.setContent {
            val ui by model.ui.collectAsState()
            TunerApp(ui, model, model::stop)
        }
        try {
            compose.onNodeWithTag("tuner-mode-GUIDED").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
            compose.onNodeWithTag("tuner-final-review").assertIsOn()
            compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
            var time = 0L
            repeat(2) { pass ->
                for ((index, midi) in listOf(40, 45, 50, 55, 59, 64).withIndex()) {
                    repeat(11) { samples.trySend(midi to time).getOrThrow(); time += 100 }
                    compose.waitUntil(10_000) {
                        val state = model.ui.value.guided
                        (if (pass == 0) state.firstPass else state.review).size == index + 1
                    }
                }
            }
            compose.onNodeWithTag("tuner-guided-phase").performScrollTo().assertTextEquals("Conjunto comprobado")
            compose.onNodeWithTag("tuner-guided-summary").performScrollTo().assertExists()
            assertTrue(model.ui.value.guided.checked)
            assertEquals(6, model.ui.value.guided.firstPass.size)
            assertEquals(6, model.ui.value.guided.review.size)
            compose.onNodeWithTag("tuner-status").performScrollTo().assertTextEquals("Micrófono detenido")
        } finally {
            instrumentation.runOnMainSync { model.stop() }
            samples.close()
        }
    }
}

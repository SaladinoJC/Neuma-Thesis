package ar.edu.unmdp.entrenamiento

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.asAndroidBitmap
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
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow

/** UI real con estimaciones sintéticas en el límite de captura, sin medir guitarra física. */
class EarTunerAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun clasificacionErrorCorreccionSeisCuerdasYRevisionNoFiltranLaMedicionOculta() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.executeShellCommand("pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").close()
        val clock = AtomicLong()
        val samples = Channel<Pair<PitchEstimate, Long>>(Channel.UNLIMITED)
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                for ((pitch, time) in samples) { clock.set(time); onMeasurement(pitch, time, 44100) }
            }
        }
        lateinit var model: TunerViewModel
        instrumentation.runOnMainSync {
            model = TunerViewModel(recorder, clock = clock::get)
            model.mode(TunerMode.TRAINING)
            model.earClassification(true)
        }
        compose.setContent { val ui by model.ui.collectAsState(); TunerApp(ui, model, model::stop) }
        var time = 0L
        fun feed(midi: Int, cents: Double, count: Int) {
            repeat(count) {
                samples.trySend(PitchEstimate(440.0 * 2.0.pow((midi - 69 + cents / 100) / 12)) to time).getOrThrow()
                time += 100
            }
        }
        try {
            compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
            compose.onNodeWithTag("ear-classification").assertIsOn()
            screenshot("preparacion")
            compose.onNodeWithTag("ear-start").performScrollTo().performClick()
            for ((index, midi) in listOf(40, 45, 50, 55, 59, 64).withIndex()) {
                compose.onNodeWithTag("ear-hidden").assertExists()
                // Comprueba ambos árboles; no basta ocultar píxeles si TalkBack aún tiene el dato.
                compose.onAllNodesWithText("cents", substring = true, useUnmergedTree = true).assertCountEquals(0)
                compose.onNodeWithTag("ear-live-note").assertDoesNotExist()
                compose.onNodeWithTag("ear-classify-LOW").performScrollTo().performClick()
                compose.onNodeWithTag("ear-message").assertTextContains("Tocá nuevamente", substring = true)
                feed(midi, -30.0, 5)
                compose.waitUntil(10_000) { clock.get() == time - 100 }
                compose.onNodeWithTag("ear-classify-LOW").performScrollTo().performClick()
                compose.onNodeWithTag("ear-check").performScrollTo().performClick()
                compose.waitUntil(10_000) { model.ui.value.ear.measurement.running }
                assertEquals(EarPhase.CHECKING, model.ui.value.ear.phase)
                compose.onNodeWithTag("ear-check").assertIsNotEnabled()
                feed(midi, if (index == 0) 30.0 else 0.0, 5)
                compose.waitUntil(10_000) { model.ui.value.ear.results.size == index + 1 }
                compose.onNodeWithTag("ear-initial-result").performScrollTo()
                    .assertTextContains(if (index == 0) "error" else "acierto", substring = true)
                if (index == 0) screenshot("correccion")
                feed(midi, 0.0, 11)
                compose.waitUntil(10_000) { model.ui.value.ear.results.last().corrected }
                if (index == 0) screenshot("siguiente-oculta")
            }
            assertEquals(EarPhase.REVIEW, model.ui.value.ear.phase)
            for ((index, midi) in listOf(40, 45, 50, 55, 59, 64).withIndex()) {
                feed(midi, 0.0, 11)
                compose.waitUntil(10_000) { model.ui.value.ear.review.review.size == index + 1 }
            }
            compose.onNodeWithTag("ear-phase").performScrollTo().assertTextEquals("Conjunto comprobado")
            compose.onNodeWithTag("ear-summary").performScrollTo().assertExists()
            compose.onNodeWithText("6 intentos válidos · 1 error inicial").assertExists()
            assertEquals(6, model.ui.value.ear.results.size)
            assertTrue(model.ui.value.ear.results.all { it.classification?.correct == true })
            assertFalse(model.ui.value.tuning.running)
            screenshot("resumen")
        } finally { instrumentation.runOnMainSync { model.stop() }; samples.close() }
    }

    private fun screenshot(name: String) {
        if (name == "preparacion") compose.onNodeWithText("Afinar de oído").performScrollTo()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val width = context.resources.configuration.screenWidthDp
        val scale = context.resources.configuration.fontScale
        val directory = File(context.getExternalFilesDir(null), "ear-tuner").apply { mkdirs() }
        File(directory, "$width-$scale-$name.png").outputStream().use {
            compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}

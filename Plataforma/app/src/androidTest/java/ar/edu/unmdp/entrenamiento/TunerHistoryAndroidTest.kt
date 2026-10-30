package ar.edu.unmdp.entrenamiento

import android.Manifest
import android.graphics.Bitmap
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.tuner.*
import ar.edu.unmdp.entrenamiento.ui.TunerApp
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow

class TunerHistoryAndroidTest {
    @get:Rule val compose = createComposeRule()
    @Test fun falloDeGuardadoSeVeYReintentarConservaUnaUnicaSesion() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val saved = MemoryTunerResults()
        var fail = true
        val store = object : TunerResults {
            override suspend fun all() = saved.all()
            override suspend fun save(result: TunerResult) {
                if (fail) { fail = false; error("Almacenamiento no disponible") }
                saved.save(result)
            }
        }
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) { awaitCancellation() }
        }
        lateinit var model: TunerViewModel
        instrumentation.runOnMainSync { model = TunerViewModel(recorder, resultsStore = store); model.mode(TunerMode.GUIDED) }
        compose.setContent { val ui by model.ui.collectAsState(); TunerApp(ui, model, model::stop) }
        compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
        instrumentation.runOnMainSync { model.start(); model.stop() }
        compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.ui.value.history.error != null }
        compose.onNodeWithTag("tuner-history-error").performScrollTo().assertTextContains("Almacenamiento no disponible", substring = true)
        screenshot("fallo")
        compose.onNodeWithTag("tuner-history-retry").performScrollTo().performClick()
        compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.pendingForExport().isEmpty() }
        compose.onNodeWithTag("tuner-history-error").assertDoesNotExist()
        assertEquals(1, runBlocking { saved.all().size })
        assertFalse(runBlocking { saved.all().single().completed })
        instrumentation.runOnMainSync { model.stop() }
    }
    @Test fun guardarReabrirFiltrarYApagarSeguimientoConservaElResumenSinAgregarHistorial() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.RECORD_AUDIO}").close()
        val directory = File(context.cacheDir, "history-a6-${System.nanoTime()}").apply { mkdirs() }
        val store = JsonTunerResults(File(directory, "tuner-results.json"))
        val config = JsonTunerConfiguration(File(directory, "tuner-configuration.json"))
        val samples = Channel<Pair<PitchEstimate, Long>>(Channel.UNLIMITED)
        val clock = AtomicLong()
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                for ((pitch, time) in samples) { clock.set(time); onMeasurement(pitch, time, 44100) }
            }
        }
        lateinit var model: TunerViewModel
        instrumentation.runOnMainSync { model = TunerViewModel(recorder, config, resultsStore = store, clock = clock::get) }
        compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
        instrumentation.runOnMainSync { model.mode(TunerMode.TRAINING); model.finalReview(false) }
        compose.setContent { val ui by model.ui.collectAsState(); TunerApp(ui, model, model::stop) }
        var time = 0L
        fun feed(midi: Int, cents: Double, count: Int) {
            repeat(count) { samples.trySend(PitchEstimate(440 * 2.0.pow((midi - 69 + cents / 100) / 12)) to time).getOrThrow(); time += 100 }
        }
        try {
            compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
            compose.onNodeWithTag("tuner-save-history").performScrollTo().assertIsOn()
            compose.onNodeWithTag("ear-start").performScrollTo().performClick()
            for ((index, midi) in listOf(40, 45, 50, 55, 59, 64).withIndex()) {
                compose.onNodeWithTag("ear-check").performScrollTo().performClick()
                compose.waitUntil(10_000) { model.ui.value.ear.measurement.running }
                feed(midi, if (index == 0) 30.0 else 0.0, 5)
                compose.waitUntil(10_000) { model.ui.value.ear.results.size == index + 1 }
                feed(midi, 0.0, 11)
                compose.waitUntil(10_000) { model.ui.value.ear.results.last().corrected }
            }
            compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.ui.value.history.results.size == 1 }
            compose.onNodeWithTag("tuner-open-history").performScrollTo().performClick()
            historyNode("tuner-history-progress").assertTextContains("5/6", substring = true)
            screenshot("progreso")
            historyNode("tuner-history-string").performClick()
            compose.onNodeWithText("Cuerda 6", substring = false).performClick()
            historyNode("tuner-history-progress").assertTextContains("0/1", substring = true)
            screenshot("filtrado")
            historyNode("tuner-history-string").performClick()
            compose.onNodeWithText("Sin filtro", substring = false).performClick()
            historyNode("tuner-history-detail").performClick()
            compose.onNodeWithTag("tuner-history-screen").performScrollToNode(hasText("Intento de oído", substring = false))
            compose.onNodeWithText("Intento de oído", substring = false).assertExists()
            compose.onNodeWithTag("tuner-history-screen").performScrollToNode(hasText("Corrección con ayuda", substring = false))
            compose.onNodeWithText("Corrección con ayuda", substring = false).assertExists()
            screenshot("detalle")
            historyNode("tuner-history-back").performClick()
            historyNode("tuner-history-instrument").performClick()
            historyNode("tuner-history-empty").assertExists()
            historyNode("tuner-history-stage").performClick()
            compose.onNodeWithText("Corrección con ayuda", substring = false).performClick()
            historyNode("tuner-history-detail").assertExists()
            screenshot("instrumento")
            historyNode("tuner-history-close").performClick()
            compose.onNodeWithTag("tuner-save-history").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.configurationBusy }
            compose.onNodeWithTag("tuner-save-history").assertIsOff()
            compose.onNodeWithTag("ear-start").performScrollTo().performClick()
            compose.onNodeWithTag("ear-check").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.ear.measurement.running }
            feed(40, 30.0, 5)
            compose.waitUntil(10_000) { model.ui.value.ear.results.size == 1 }
            compose.onNodeWithTag("ear-stop").performScrollTo().performClick()
            compose.onNodeWithTag("ear-summary").performScrollTo().assertExists()
            assertEquals(1, runBlocking { store.all().size })
            assertFalse(runBlocking { config.load().saveHistory })
            assertTrue(model.pendingForExport().isEmpty())
            lateinit var reopened: TunerViewModel
            instrumentation.runOnMainSync { reopened = TunerViewModel(recorder, config, resultsStore = store, clock = clock::get) }
            compose.waitUntil(10_000) { !reopened.ui.value.configurationBusy }
            assertEquals(1, reopened.ui.value.history.results.size)
            assertFalse(reopened.ui.value.configuration.configuration!!.saveHistory)
            assertFalse(reopened.ui.value.tuning.running)
        } finally { instrumentation.runOnMainSync { model.stop() }; samples.close() }
    }
    private fun historyNode(tag: String): SemanticsNodeInteraction {
        compose.onNodeWithTag("tuner-history-screen").performScrollToNode(hasTestTag(tag))
        return compose.onNodeWithTag(tag).performScrollTo()
    }
    private fun screenshot(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val directory = File(context.getExternalFilesDir(null), "tuner-history").apply { mkdirs() }
        val width = context.resources.configuration.screenWidthDp
        val scale = context.resources.configuration.fontScale
        File(directory, "$width-$scale-$name.png").outputStream().use {
            instrumentation.uiAutomation.takeScreenshot().compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}

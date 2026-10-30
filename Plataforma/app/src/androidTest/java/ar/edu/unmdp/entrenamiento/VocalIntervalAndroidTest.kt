package ar.edu.unmdp.entrenamiento

import android.Manifest
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.ui.VocalIntervalApp
import ar.edu.unmdp.entrenamiento.voice.*
import org.junit.Rule
import org.junit.Test
import java.io.File

/** Prepared and compiled on host; execution requires explicit permission to use an emulator. */
class VocalIntervalAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun freeProductionHasNoReferenceControlsAndReopensItsSungAnchorInHistory() {
        grant()
        val file = File(compose.activity.cacheDir, "free-v6-${System.nanoTime()}.json")
        var now = 0L
        var captures = 0
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                val hz = if (++captures == 1) 196.0 else 293.66818722158955
                now += 100
                repeat(11) { onMeasurement(PitchEstimate(hz), 10, 44100); now += 50 }
            }
        }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(capture, object : ReferencePlayer {
            override suspend fun play(midi: Int): Int = error("No audio reference in free production")
        }, { now }, resultsStore = JsonVocalIntervalResults(file)) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-scaffolding").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-scaffolding-3").performClick()
            compose.waitUntil(5000) { model.ui.value.scaffoldingLevel == 3 && !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-listen").assertDoesNotExist()
            compose.onNodeWithTag("vocal-register").assertDoesNotExist()
            compose.onNodeWithTag("vocal-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.READY_TARGET && !model.ui.value.working }
            compose.onNodeWithTag("vocal-microphone").performScrollTo().assertTextEquals("Micrófono detenido")
            compose.onNodeWithTag("vocal-target").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.working && !model.ui.value.historyBusy }
            val id = model.ui.value.currentRecordId!!
            org.junit.Assert.assertEquals(0, model.ui.value.history.records.single().referenceListens)
            compose.onNodeWithTag("vocal-history").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-record-$id").performScrollTo().performClick()
            compose.onNodeWithText("Sin referencia sonora · Nota inicial elegida al cantar").assertExists()
            compose.onNodeWithText("Ancla cantada: 196.0 Hz").assertExists()
            compose.onNodeWithText("22 mediciones derivadas · Sin grabación de audio").assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
            file.delete()
        }
    }

    @Test fun freeProgressionWithoutGivenMasteryKeepsItsPracticeAvailableWithoutCredit() {
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(automaticInitially = true) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-scaffolding").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-scaffolding-3").performClick()
            compose.waitUntil(5000) { model.ui.value.scaffoldingLevel == 3 && !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-free-locked").assertExists()
            compose.onNodeWithTag("vocal-series-start").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("vocal-listen").assertDoesNotExist()
            compose.onNodeWithTag("vocal-mode-manual").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-start").performScrollTo().assertIsEnabled()
            org.junit.Assert.assertTrue(model.ui.value.history.progress.freeProductionIntervals.isEmpty())
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
        }
    }

    @Test fun givenAnchorPlaysOnlyTheAnchorAndSavesOneTargetCapture() {
        grant()
        val file = File(compose.activity.cacheDir, "given-v5-${System.nanoTime()}.json")
        var now = 0L
        var captures = 0
        val played = mutableListOf<Int>()
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                captures++; now += 100
                repeat(11) { onMeasurement(PitchEstimate(329.6275569128699), 10, 44100); now += 50 }
            }
        }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread {
            model = VocalIntervalViewModel(capture, object : ReferencePlayer {
                override suspend fun play(midi: Int): Int { played += midi; return 26460 }
            }, { now }, resultsStore = JsonVocalIntervalResults(file))
        }
        render(model)
        try {
            compose.onNodeWithTag("vocal-scaffolding").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-scaffolding-2").performClick()
            compose.waitUntil(5000) { model.ui.value.scaffoldingLevel == 2 && !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-listen").performScrollTo().assertTextEquals("Escuchar ancla").performClick()
            compose.waitUntil(5000) { model.ui.value.heardReference }
            compose.onNodeWithTag("vocal-start").assertDoesNotExist()
            compose.onNodeWithTag("vocal-given-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.historyBusy && !model.ui.value.working }
            org.junit.Assert.assertEquals(listOf(57), played)
            org.junit.Assert.assertEquals(1, captures)
            val id = model.ui.value.currentRecordId!!
            compose.onNodeWithTag("vocal-history").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-record-$id").performScrollTo().performClick()
            compose.onNodeWithText("Ancla reproducida: 220.0 Hz").assertExists()
            compose.onNodeWithText("11 mediciones derivadas · Sin grabación de audio").assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
            file.delete()
        }
    }

    @Test fun givenAutomaticLocksWithoutImitationButManualPracticeDoesNotGrantAccess() {
        grant()
        var now = 0L
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                now += 100
                repeat(11) { onMeasurement(PitchEstimate(220.0), 10, 44100); now += 50 }
            }
        }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(capture,
            object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }, { now }, automaticInitially = true) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-scaffolding").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-scaffolding-2").performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy && model.ui.value.scaffoldingLevel == 2 }
            compose.onNodeWithTag("vocal-given-locked").assertExists()
            compose.onNodeWithTag("vocal-series-start").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("vocal-mode-manual").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-listen").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.heardReference }
            compose.onNodeWithTag("vocal-given-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.historyBusy && !model.ui.value.working }
            compose.onNodeWithTag("vocal-new").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-mode-auto").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-given-locked").assertExists()
            compose.onNodeWithTag("vocal-series-start").performScrollTo().assertIsNotEnabled()
            org.junit.Assert.assertTrue(model.ui.value.history.progress.givenAnchorIntervals.isEmpty())
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
        }
    }

    @Test fun automaticPreparationCancelAndSimulationLeaveTheOriginalProfileUntouched() {
        compose.awaitGardenMenu()
        val file = File(compose.activity.filesDir, "vocal-interval-results.json")
        val before = if (file.exists()) file.readBytes() else null
        compose.openMenuDestination("game-vocal-intervals")
        compose.onNodeWithTag("vocal-mode-auto").assertIsSelected()
        compose.onNodeWithTag("vocal-series-start").performScrollTo().performClick()
        compose.waitUntil(5000) { compose.onAllNodesWithTag("vocal-series-status").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("vocal-microphone").performScrollTo().assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("vocal-cancel").performScrollTo().performClick()
        compose.onNodeWithTag("vocal-series-new").performScrollTo().performClick()
        compose.onNodeWithTag("vocal-progress").performScrollTo().performClick()
        compose.onNodeWithTag("vocal-progress-screen").assertExists()
        compose.onNodeWithTag("vocal-simulation-tab").performScrollTo().performClick()
        compose.onNodeWithTag("vocal-simulation-run").performScrollTo().performClick()
        compose.waitUntil(15_000) { compose.onAllNodesWithText("10 rondas sintéticas", substring = true).fetchSemanticsNodes().isNotEmpty() }
        if (before == null) org.junit.Assert.assertFalse(file.exists()) else org.junit.Assert.assertArrayEquals(before, file.readBytes())
    }

    @Test fun fiveAutomaticSyntheticRoundsKeepCurrentToleranceAndOfferTheNextRound() {
        grant()
        var now = 0L
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                now += 100
                repeat(11) { onMeasurement(PitchEstimate(220.0), 10, 44100); now += 50 }
            }
        }
        val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(capture, player, { now }, automaticInitially = true) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-series-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.series != null && !model.ui.value.historyBusy }
            repeat(5) { index ->
                val tolerance = model.ui.value.toleranceCents
                compose.onNodeWithTag("vocal-listen").performScrollTo().performClick()
                compose.waitUntil(5000) { model.ui.value.heardReference }
                compose.onNodeWithTag("vocal-start").performScrollTo().performClick()
                compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.READY_TARGET && !model.ui.value.working }
                compose.onNodeWithTag("vocal-target").performScrollTo().performClick()
                compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.historyBusy && !model.ui.value.working }
                org.junit.Assert.assertEquals(tolerance, model.ui.value.round!!.result!!.configuration.toleranceCents, 0.0)
                if (index < 4) compose.onNodeWithTag("vocal-new").performScrollTo().performClick()
            }
            org.junit.Assert.assertTrue(model.ui.value.series!!.ended)
            org.junit.Assert.assertEquals(1, model.ui.value.history.progress[0].highSeries)
            compose.onNodeWithTag("vocal-progress").performScrollTo().performClick()
            compose.onNodeWithText("1/2 series altas", substring = true).assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
        }
    }

    private fun grant() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").use { descriptor ->
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).readBytes()
        }
    }

    private fun render(model: VocalIntervalViewModel) {
        compose.runOnUiThread {
            compose.activity.setContent {
                val ui by model.ui.collectAsState()
                VocalIntervalApp(ui, model) {}
            }
        }
    }

    @Test fun savedRoundReopensInHistoryWithItsDerivedMeasurements() {
        grant()
        val file = File(compose.activity.cacheDir, "vocal-v3-${System.nanoTime()}.json")
        var now = 0L
        var captures = 0
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                val hz = if (++captures == 1) 220.0 else 329.6275569128699
                now += 100
                repeat(11) { onMeasurement(PitchEstimate(hz, .98, .2), 37, 44100); now += 50 }
            }
        }
        val player = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(capture, player, { now }, resultsStore = JsonVocalIntervalResults(file)) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-listen").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.heardReference }
            compose.onNodeWithTag("vocal-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.READY_TARGET && !model.ui.value.working }
            compose.onNodeWithTag("vocal-target").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.working && !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-save-state").performScrollTo().assertTextEquals("Ronda guardada")
            val id = model.ui.value.currentRecordId!!
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
            compose.runOnUiThread { model = VocalIntervalViewModel(resultsStore = JsonVocalIntervalResults(file)) }
            render(model)
            compose.onNodeWithTag("vocal-history").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy && model.ui.value.history.records.isNotEmpty() }
            compose.onNodeWithTag("vocal-record-$id").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-history-result").assertExists()
            compose.onNodeWithTag("vocal-history-measurements").performScrollTo().performClick()
            compose.onNodeWithText("22 mediciones derivadas · Sin grabación de audio").assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
            file.delete()
        }
    }

    @Test fun failedSaveBlocksNewRoundAndRetryRecoversTheSameResult() {
        grant()
        var failing = true
        val memory = MemoryVocalIntervalResults()
        val store = object : VocalIntervalResults {
            override suspend fun all() = memory.all()
            override suspend fun save(record: VocalIntervalRecord) { if (failing) error("Disco de prueba no disponible"); memory.save(record) }
        }
        val capture = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                throw SecurityException("Captura sintética interrumpida")
            }
        }
        val reference = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread { model = VocalIntervalViewModel(capture, reference, resultsStore = store) }
        render(model)
        try {
            compose.onNodeWithTag("vocal-listen").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.heardReference }
            compose.onNodeWithTag("vocal-start").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy && model.pendingForExport().isNotEmpty() }
            val id = model.pendingForExport().single().id
            compose.onNodeWithTag("vocal-save-state").performScrollTo().assertTextEquals("Guardado pendiente")
            compose.onNodeWithTag("vocal-new").performScrollTo().assertIsNotEnabled()
            compose.runOnUiThread { failing = false }
            compose.onNodeWithTag("vocal-retry-save").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy && model.pendingForExport().isEmpty() }
            compose.onNodeWithTag("vocal-save-state").performScrollTo().assertTextEquals("Ronda guardada")
            compose.onNodeWithTag("vocal-new").performScrollTo().assertIsEnabled()
            compose.onNodeWithTag("vocal-history").performScrollTo().performClick()
            compose.waitUntil(5000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("vocal-record-$id").assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working && !model.ui.value.historyBusy }
        }
    }

    @Test fun fifthBookEntryOpensManualPracticeAndReturnsToSamePage() {
        compose.awaitGardenMenu()
        compose.openMenuDestination("game-vocal-intervals")
        compose.onNodeWithText("Intervalos vocales").assertExists()
        compose.onNodeWithTag("vocal-microphone").assertTextEquals("Micrófono detenido")
        compose.onNodeWithTag("vocal-start").assertDoesNotExist()
        compose.onNodeWithTag("vocal-home").performScrollTo().performClick()
        compose.awaitGardenMenu()
        compose.onNodeWithTag("game-vocal-intervals").assertExists()
        compose.onNodeWithTag("garden-index").performScrollTo().assertTextEquals("5 de 5")
    }

    @Test fun referenceAndTwoSustainedNotesProduceVisibleResultWithoutHardwareCapture() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").use { descriptor ->
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).readBytes()
        }
        var now = 0L
        var captures = 0
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                val hz = if (++captures == 1) 220.0 else 329.6275569128699
                now += 100
                repeat(11) { onMeasurement(PitchEstimate(hz), 10, 44100); now += 50 }
            }
        }
        val reference = object : ReferencePlayer { override suspend fun play(midi: Int) = 26460 }
        lateinit var model: VocalIntervalViewModel
        compose.runOnUiThread {
            model = VocalIntervalViewModel(recorder, reference, { now })
            compose.activity.setContent {
                val ui by model.ui.collectAsState()
                VocalIntervalApp(ui, model) {}
            }
        }
        try {
            compose.onNodeWithTag("vocal-listen").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.heardReference }
            compose.onNodeWithTag("vocal-start").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.READY_TARGET && !model.ui.value.working }
            compose.onNodeWithTag("vocal-microphone").performScrollTo().assertTextEquals("Micrófono detenido")
            compose.onNodeWithTag("vocal-target").performScrollTo().performClick()
            compose.waitUntil(5000) { model.ui.value.round?.phase == VocalIntervalPhase.COMPLETED && !model.ui.value.working }
            compose.onNodeWithTag("vocal-outcome").performScrollTo().assertTextEquals("Dentro de la tolerancia de práctica")
            compose.onNodeWithTag("vocal-new").performScrollTo().performClick()
            compose.onNodeWithTag("vocal-listen").assertExists()
        } finally {
            compose.runOnUiThread { model.stop() }
            compose.waitUntil(5000) { !model.ui.value.working }
        }
    }
}

package ar.edu.unmdp.entrenamiento

import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.JsonRhythmResults
import ar.edu.unmdp.entrenamiento.rhythm.RhythmViewModel
import ar.edu.unmdp.entrenamiento.rhythm.RhythmPhase
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File

class RhythmHistoryNavigationAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun pausaGuardaEnPerfilRealYDatosExportaYReiniciaElHistorial() {
        compose.awaitGardenMenu()
        fun reset() {
            compose.onNodeWithTag("data-reset").performScrollTo().performClick()
            compose.onNodeWithTag("data-reset-confirm").performClick()
            compose.waitUntil(10_000) { compose.onAllNodesWithText("Datos reiniciados. Ambos juegos comienzan en el primer nivel.").fetchSemanticsNodes().isNotEmpty() }
        }
        compose.openMenuDestination("data-open"); reset()
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.openMenuDestination("game-rhythm")
        compose.onNodeWithTag("rhythm-category-R20").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("rhythm-play").fetchSemanticsNodes().isNotEmpty() }
        lateinit var model: RhythmViewModel
        compose.runOnUiThread { model = ViewModelProvider(compose.activity)[RhythmViewModel::class.java] }
        // Igual que R1: la espera automática no debe dejar transcurrir los 20 s antes de pausar.
        compose.mainClock.autoAdvance = false
        try {
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.activityRule.scenario.moveToState(Lifecycle.State.STARTED)
            compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.RESULTS && !model.ui.value.historyBusy }
            assertFalse(model.ui.value.session.result!!.completed)
            compose.mainClock.advanceTimeBy(64)
        } finally {
            compose.mainClock.autoAdvance = true
        }
        compose.onNodeWithTag("rhythm-save-status").performScrollTo().assertTextEquals("Ejercicio guardado")
        val store = JsonRhythmResults(File(compose.activity.filesDir, "rhythm-results.json"))
        val record = runBlocking { store.all().single() }
        assertFalse(record.result.completed)
        compose.onNodeWithTag("rhythm-open-history").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-history-screen").performScrollToNode(hasTestTag("rhythm-history-detail"))
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("rhythm-history-screen").performScrollToNode(hasTestTag("rhythm-history-detail"))
        captureRhythmEvidence(compose, "r5-history", "perfil-real")
        compose.onNodeWithTag("rhythm-history-screen").performScrollToNode(hasTestTag("rhythm-data"))
        compose.onNodeWithTag("rhythm-data").performClick()
        val exported = runBlocking { Json.decodeFromString(DemonstrationExport.serializer(), Demonstration(compose.activity.filesDir).export("test")) }
        assertEquals(record, exported.rhythm.single())
        compose.onNodeWithTag("data-export").performScrollTo().performClick()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        compose.waitUntil(30_000) { instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true }
        val provider = instrumentation.uiAutomation.rootInActiveWindow
        assertTrue("Destino local de pruebas: Downloads", provider.findAccessibilityNodeInfosByViewId("com.google.android.documentsui:id/breadcrumb_text").any { it.text?.toString() == "Downloads" })
        val editor = provider.findAccessibilityNodeInfosByViewId("android:id/title").single { it.className?.toString() == "android.widget.EditText" }
        val filename = "r5-export-${System.nanoTime()}.json"
        assertTrue(editor.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, filename)
        }))
        val save = instrumentation.uiAutomation.rootInActiveWindow.findAccessibilityNodeInfosByViewId("android:id/button1").single { it.isEnabled }
        assertTrue(save.performAction(AccessibilityNodeInfo.ACTION_CLICK))
        compose.waitUntil(15_000) {
            // DocumentsUI may still own the window for a frame after accepting Save.
            try { compose.onAllNodesWithText("Exportación guardada.").fetchSemanticsNodes().isNotEmpty() }
            catch (missing: IllegalStateException) {
                if (missing.message?.startsWith("No compose hierarchies found") == true) false else throw missing
            }
        }
        compose.onNodeWithTag("data-message").performScrollTo().assertTextEquals("Exportación guardada.")
        val descriptor = instrumentation.uiAutomation.executeShellCommand("cat /sdcard/Download/$filename")
        val content = ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().use { it.readText() }
        assertEquals(record, Json.decodeFromString(DemonstrationExport.serializer(), content).rhythm.single())
        val evidence = File(instrumentation.targetContext.getExternalFilesDir(null), "r5-history").apply { mkdirs() }
        File(evidence, "native-export.json").writeText(content)
        captureRhythmEvidence(compose, "r5-history", "datos-exportados")
        reset()
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        captureRhythmEvidence(compose, "r5-history", "menu-retorno")
        compose.openMenuDestination("game-rhythm")
        compose.onNodeWithTag("rhythm-open-history").performScrollTo().performClick()
        compose.onNodeWithTag("rhythm-history-screen").performScrollToNode(hasTestTag("rhythm-history-empty"))
        compose.onNodeWithTag("rhythm-history-empty").assertTextEquals("Todavía no hay ejercicios guardados.")
        captureRhythmEvidence(compose, "r5-history", "vacio-perfil")
        assertTrue(runBlocking { store.all().isEmpty() })
    }
}

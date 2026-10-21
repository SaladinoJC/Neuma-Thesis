package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import java.io.File
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DataRecoveryAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    @Test fun historialDañadoPermiteRecuperarYReiniciarSinSobrescribirOriginal() {
        compose.waitUntil(15_000) { compose.activity.hasWindowFocus() }
        val data = Demonstration(compose.activity.filesDir)
        runBlocking { data.reset() }
        val file = File(compose.activity.filesDir, "simon-results.json")
        file.writeText("{cortado")
        try {
        compose.onNodeWithTag("game-simon").performScrollTo().performClick()
        compose.onNodeWithText("Jugar Simón").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Reintentar lectura").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithText("Jugar Simón").assertDoesNotExist()
        compose.onNodeWithText("Volver al menú").performClick()
        val output = ByteArrayOutputStream()
        runBlocking { data.writeRecovery(output) }
        ZipInputStream(output.toByteArray().inputStream()).use { zip ->
            assertEquals("simon-results.json", zip.nextEntry.name)
            assertEquals("{cortado", zip.readBytes().toString(Charsets.UTF_8))
        }
        compose.onNodeWithTag("data-open").performScrollTo().performClick()
        compose.onNodeWithTag("data-recovery").performScrollTo().performClick()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // El proveedor externo puede arrancar por primera vez tras un inicio en frío del AVD.
        compose.waitUntil(30_000) {
            instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true
        }
        repeat(3) {
            if (instrumentation.uiAutomation.rootInActiveWindow?.packageName?.toString()?.contains("documentsui") == true) {
                instrumentation.sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
                Thread.sleep(500)
            }
        }
        compose.waitUntil(10_000) { compose.onAllNodesWithTag("data-message").fetchSemanticsNodes().isNotEmpty() }
        compose.onNodeWithTag("data-message").performScrollTo().assertTextEquals("Recuperación cancelada.")
        assertEquals("{cortado", file.readText())
        compose.onNodeWithTag("data-reset").performScrollTo().performClick()
        compose.onNodeWithTag("data-reset-confirm").performClick()
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Datos reiniciados. Ambos juegos comienzan en el primer nivel.").fetchSemanticsNodes().isNotEmpty() }
        assertFalse(file.exists())
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.onNodeWithText("Reintentar lectura").assertDoesNotExist()
        runBlocking { assertTrue(data.export("test").contains("schemaVersion")) }
        } finally { runBlocking { data.reset() } }
    }
}

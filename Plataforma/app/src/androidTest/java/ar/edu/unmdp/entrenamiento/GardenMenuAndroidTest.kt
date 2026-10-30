package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.lifecycle.Lifecycle
import android.graphics.Bitmap
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GardenMenuAndroidTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun afinadorAbreEntrenamientoSinEncenderMicrofono() {
        compose.openMenuDestination("garden-tuner-training")
        compose.onNodeWithText("Afinar de oído").assertExists()
        compose.onNodeWithTag("ear-microphone").assertTextEquals("Micrófono detenido")
        compose.onNodeWithText("Volver al menú").performScrollTo().performClick()
        compose.onNodeWithTag("garden-section-AFINADOR").assertIsSelected()
    }

    @Test fun libroAbrePersonajeYCadaPortadaQuedaDentroDelPapel() {
        compose.onNodeWithTag("garden-section-PERSONAJE").assertIsSelected()
        screenshot("personaje")
        compose.onNodeWithTag("garden-section-JUEGOS").performClick()
        for (index in 0..3) {
            compose.onNodeWithTag("garden-index").performScrollTo().performClick()
            compose.onNodeWithTag("garden-index-$index").performClick()
            compose.onNodeWithTag("garden-illustration").performScrollTo()
            val page = compose.onNodeWithTag("garden-paper-content").getUnclippedBoundsInRoot()
            val image = compose.onNodeWithTag("garden-illustration").getUnclippedBoundsInRoot()
            assertTrue("La ilustración queda dentro de la página: $index",
                image.left >= page.left && image.right <= page.right && image.top >= page.top && image.bottom <= page.bottom)
            compose.onNodeWithText("Jugar").assertExists()
            screenshot("juego-$index")
        }
        compose.onNodeWithTag("game-defender").performScrollTo().performClick()
        compose.onNodeWithText("‹ Menú").performClick()
        compose.onNodeWithText("Defender").assertExists()
        compose.onNodeWithTag("game-defender").assertExists()
        compose.onNodeWithTag("garden-section-AFINADOR").performScrollTo().performClick()
        compose.onNodeWithTag("garden-section-JUEGOS").performScrollTo().performClick()
        compose.onNodeWithTag("game-defender").assertExists()
    }

    @Test fun opcionesConservanAccesosYReducenElMovimiento() {
        compose.onNodeWithTag("garden-more").performClick()
        compose.onNodeWithTag("garden-reduce-motion").performClick()
        compose.onNodeWithTag("garden-close-more").performClick()
        compose.onNodeWithTag("garden-section-JUEGOS").performClick()
        compose.onNodeWithTag("garden-index").performScrollTo().performClick()
        compose.onNodeWithTag("garden-index-2").performClick()
        compose.onNodeWithTag("game-rhythm").performScrollTo().performClick()
        compose.onNodeWithText("Rhythm Tap").assertExists()
    }

    @Test fun paginaYSeccionSobrevivenRecreacionYSegundoPlanoSinCircular() {
        compose.showMenuDestination("game-defender")
        compose.onNodeWithTag("garden-next").performScrollTo().assertIsNotEnabled()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("garden-section-JUEGOS").assertIsSelected()
        compose.onNodeWithTag("game-defender").assertExists()
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithTag("game-defender").assertExists()
        compose.showMenuDestination("game-simon")
        compose.onNodeWithTag("garden-previous").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithTag("garden-next").performClick()
        compose.onNodeWithTag("game-instruments").assertExists()
    }

    private fun screenshot(name: String) {
        val resources = compose.activity.resources
        val width = resources.configuration.screenWidthDp
        val scale = resources.configuration.fontScale
        val directory = File(compose.activity.getExternalFilesDir(null), "garden-menu").apply { mkdirs() }
        File(directory, "$width-$scale-$name.png").outputStream().use {
            compose.waitForIdle()
            // Captura del compositor: PixelCopy sobre la ventana tras cerrar Índice agota su espera.
            checkNotNull(InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot())
                .compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}

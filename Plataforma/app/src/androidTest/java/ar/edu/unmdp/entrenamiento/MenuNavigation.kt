package ar.edu.unmdp.entrenamiento

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule

/** Espera contenido de la app: el foco de ventana no es estable en emuladores sin interfaz visible. */
internal fun ComposeTestRule.awaitGardenMenu() {
    waitUntil(15_000) { onAllNodesWithTag("garden-menu").fetchSemanticsNodes().isNotEmpty() }
}

/** Recorre los controles reales del libro antes de entrar a un módulo. */
internal fun ComposeTestRule.showMenuDestination(tag: String) {
    val startingControl = if (tag in listOf("history-open", "game-voice", "data-open")) "garden-more" else "garden-menu"
    waitUntil(10_000) { onAllNodesWithTag(startingControl).fetchSemanticsNodes().isNotEmpty() }
    when (tag) {
        "game-tuner", "garden-tuner-guided", "garden-tuner-training" -> onNodeWithTag("garden-section-AFINADOR").performScrollTo().performClick()
        "history-open", "game-voice", "data-open" ->
            onNodeWithTag("garden-more").performScrollTo().performClick()
        else -> {
            val page = listOf("game-simon", "game-instruments", "game-rhythm", "game-defender").indexOf(tag)
            require(page >= 0) { "Destino desconocido: $tag" }
            onNodeWithTag("garden-section-JUEGOS").performScrollTo().performClick()
            onNodeWithTag("garden-index").performScrollTo().performClick()
            onNodeWithTag("garden-index-$page").performClick()
        }
    }
}

internal fun ComposeTestRule.openMenuDestination(tag: String) {
    showMenuDestination(tag)
    onNodeWithTag(tag).performScrollTo().performClick()
}

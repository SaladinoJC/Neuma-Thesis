package ar.edu.unmdp.entrenamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicLong

class RhythmR3AndroidTest {
    @get:Rule val compose = createComposeRule()
    private val categories = RhythmCategory.entries.filter { it.code in setOf("R03", "R04", "R05", "R06", "R07", "R08", "R13", "R14") }

    @Test fun ejemplosCanonicosDeOchoCelulasBinariasEnTresMetricas() {
        val fixtures = categories.associateWith { category -> (2..4).map { meter ->
            val generated = RhythmSession().apply { generate(setOf(category), 12L, meter) }.state.score!!
            // Lámina de máxima densidad: repite la célula real del generador en un compás.
            // No se presenta como una partida generada que cumpla cuota del 60 %.
            val notes = generated.occurrences.first { it.target }.notes
            val bar = RhythmBar(0, List(meter) { RhythmOccurrence(it, 0, Beat(it), category, true, notes) })
            generated.copy(bars = listOf(bar))
        } }
        var selected by mutableStateOf(categories.first())
        compose.setContent {
            TunerGardenTheme {
                Column(Modifier.fillMaxSize().background(GardenTokens.Background).padding(16.dp).testTag("r3-notation"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${selected.code} · ${selected.label}", style = MaterialTheme.typography.titleMedium)
                    Text("Ejemplo de notación · máxima densidad por compás", style = MaterialTheme.typography.bodySmall)
                    fixtures.getValue(selected).forEach { score ->
                        Text("${score.beatsPerBar}/4", style = MaterialTheme.typography.bodySmall)
                        RhythmBarDrawing(score.bars.single(), score, RhythmState(RhythmPhase.PREVIEW, score), Modifier.fillMaxWidth().height(100.dp))
                    }
                }
            }
        }
        for (category in categories) {
            compose.runOnIdle { selected = category }
            compose.onNodeWithTag("r3-notation").assertIsDisplayed()
            compose.onNodeWithText("${category.code} · ${category.label}").assertExists()
            screenshot(category.code)
        }
    }

    @Test fun ejercicioCombinadoSinMetronomoConAccionAccesibleYRelojControlado() {
        val clock = AtomicLong()
        val sounded = mutableListOf<Long>()
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                assertFalse(metronome); return RhythmAudioStart(clock.get(), "controlled-r3", 0)
            }
            override fun tap() { sounded += clock.get() }
            override fun stop() = Unit
        }
        val model = RhythmViewModel(audio, clock::get, { clock.get() / 1_000_000L })
        compose.setContent { val ui by model.ui.collectAsState(); RhythmApp(ui, model) { model.home() } }
        try {
            fun tap() { compose.onNodeWithTag("rhythm-tap").performSemanticsAction(SemanticsActions.OnClick) { it() } }
            for (code in listOf("R03", "R07", "R14", "R09")) compose.onNodeWithTag("rhythm-category-$code").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-metronome").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
            val score = model.ui.value.session.score!!
            compose.onNodeWithTag("rhythm-score").performScrollTo().assertIsDisplayed()
            screenshot("previa")
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            tap()
            val attacks = model.ui.value.session.attacks.toList()
            val start = 4_000_000_000L
            for ((index, attack) in attacks.withIndex()) {
                if (index == 2) continue // Omisión deliberada.
                val delta = when (index) { 0 -> 80_000_000L; 1 -> -80_000_000L; else -> 0L }
                compose.runOnIdle { clock.set(start + attack.offsetNanos + delta) }
                tap()
                if (index == 0) tap() // Extra, sin moverlo a otra nota.
            }
            compose.runOnIdle { clock.set(20_000_000_001L) }
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.RESULTS }
            val result = model.ui.value.session.result!!
            assertTrue(result.completed); assertFalse(result.metronome)
            assertEquals("Extra deliberado", 1, result.extras); assertEquals("Omision deliberada", 1, result.omissions)
            assertEquals(1, result.attacks.count { it.kind == TapKind.LATE })
            assertEquals(1, result.attacks.count { it.kind == TapKind.EARLY })
            assertEquals(80_000_000L, result.attacks.first().deltaNanos)
            assertEquals(result.taps.map { it.timestampNanos }, sounded)
            compose.onNodeWithTag("rhythm-summary").performScrollTo().assertExists()
            compose.onNodeWithTag("rhythm-result-details").performScrollTo().performClick()
            for (code in listOf("R03", "R07", "R14", "R09")) compose.onNodeWithTag("rhythm-summary-$code").performScrollTo().assertExists()
            screenshot("resumen")
            compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
            assertEquals(score, model.ui.value.session.score)
            assertTrue(model.ui.value.session.repeated)
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-edit-categories").performScrollTo().performClick()
            compose.onNodeWithTag("rhythm-category-R03").performScrollTo().assertIsSelected()
        } finally { compose.runOnIdle { model.background() } }
    }
    private fun screenshot(name: String) = captureRhythmEvidence(compose, "r3-notation", name)
}

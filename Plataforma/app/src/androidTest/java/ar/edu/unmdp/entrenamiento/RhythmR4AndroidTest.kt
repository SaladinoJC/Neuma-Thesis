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

class RhythmR4AndroidTest {
    @get:Rule val compose = createComposeRule()
    private val categories = RhythmCategory.entries.filter { it.code in setOf("R15", "R16", "R17") }

    @Test fun ejemplosCanonicosDeTresilloSincopaYLigaduraEnTresMetricas() {
        val fixtures = categories.associateWith { category -> (2..4).map { meter ->
            val generated = RhythmSession().apply { generate(setOf(category), 12L, meter) }.state.score!!
            val notes = generated.occurrences.first { it.target }.notes
            val duration = notes.fold(Beat(0)) { sum, note -> sum + note.duration }.numerator
            // Máxima densidad sin cruzar barras; el último pulso de 3/4 queda como conexión.
            var position = 0
            val occurrences = mutableListOf<RhythmOccurrence>()
            while (position < meter) {
                val target = position + duration <= meter
                val cellNotes = if (target) notes else listOf(RhythmNote(Beat(1)))
                occurrences += RhythmOccurrence(occurrences.size, 0, Beat(position),
                    if (target) category else RhythmCategory.QUARTER, target, cellNotes)
                position += if (target) duration else 1
            }
            generated.copy(bars = listOf(RhythmBar(0, occurrences)))
        } }
        var selected by mutableStateOf(categories.first())
        compose.setContent {
            TunerGardenTheme {
                Column(Modifier.fillMaxSize().background(GardenTokens.Background).padding(16.dp).testTag("r4-notation"),
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
            compose.onNodeWithText("${category.code} · ${category.label}").assertIsDisplayed()
            captureRhythmEvidence(compose, "r4-notation", category.code)
        }
    }

    @Test fun ejercicioCombinadoCuentaAtaquesRealesYLigaduraSinReataqueConSonidoOpcional() {
        val clock = AtomicLong()
        val sounded = mutableListOf<Long>()
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(clock.get(), "controlled-r4", 0)
            override fun tap() { sounded += clock.get() }
            override fun stop() = Unit
        }
        val model = RhythmViewModel(audio, clock::get, { clock.get() / 1_000_000L })
        compose.setContent { val ui by model.ui.collectAsState(); RhythmApp(ui, model, reduceMotion = true) { model.home() } }
        try {
            fun tap() { compose.onNodeWithTag("rhythm-tap").performSemanticsAction(SemanticsActions.OnClick) { it() } }
            for (code in listOf("R15", "R16", "R17", "R03")) compose.onNodeWithTag("rhythm-category-$code").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-tap-sound").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
            val score = model.ui.value.session.score!!
            compose.onNodeWithTag("rhythm-score").performScrollTo().assertIsDisplayed()
            captureRhythmEvidence(compose, "r4-notation", "previa")
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            val attacks = model.ui.value.session.attacks.toList()
            val tied = score.occurrences.first { it.category.code == "R17" }
            val extra = (Beat(tied.bar * 4) + tied.beat + Beat(1)).nanos(60)
            val taps = (attacks.map { it.offsetNanos } + extra).sorted()
            for (offset in taps) {
                compose.runOnIdle { clock.set(4_000_000_000L + offset) }
                tap()
            }
            compose.runOnIdle { clock.set(20_000_000_001L) }
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.RESULTS }
            val result = model.ui.value.session.result!!
            assertTrue(result.completed); assertFalse(result.tapSound)
            assertEquals(1, result.extras); assertEquals(0, result.omissions)
            assertEquals(attacks.size, result.attacks.count { it.kind == TapKind.ON_TIME })
            assertTrue(sounded.isEmpty())
            compose.onNodeWithTag("rhythm-result-details").performScrollTo().performClick()
            for (code in listOf("R15", "R16", "R17", "R03")) compose.onNodeWithTag("rhythm-summary-$code").performScrollTo().assertExists()
            captureRhythmEvidence(compose, "r4-notation", "resumen")
            compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
            assertEquals(score, model.ui.value.session.score)
            assertEquals(score.fingerprint, model.ui.value.session.score!!.fingerprint)
            assertTrue(model.ui.value.session.repeated)
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-tap-sound").performScrollTo().performClick()
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.runOnIdle { clock.set(24_000_000_001L) }
            tap()
            assertEquals(listOf(clock.get()), sounded)
            compose.runOnIdle { model.background() }
            assertFalse(model.ui.value.session.result!!.completed)
        } finally { compose.runOnIdle { model.background() } }
    }
}

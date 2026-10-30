package ar.edu.unmdp.entrenamiento

import androidx.compose.runtime.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.defender.*
import ar.edu.unmdp.entrenamiento.ui.DefenderApp
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicLong

/** Compilar sin ejecutar hasta que el usuario habilite emulador o dispositivo. */
class DefenderD5AndroidTest {
    @get:Rule val compose = createComposeRule()
    private val played = mutableListOf<List<Int>>()
    private val audio = object : DefenderAudio {
        override suspend fun note(midi: Int) = error("No sustituir acorde por raíz")
        override suspend fun chord(midis: List<Int>): Int { played += midis; return 0 }
        override suspend fun effect(effect: DefenderEffect) = 0
    }

    @Test fun septimaInvertidaEnFaPartidaRevisionYReapertura() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val now = AtomicLong()
        val file = File(instrumentation.targetContext.cacheDir, "defender-d5-${System.nanoTime()}/defender-results.json")
        fun create() = DefenderViewModel(audio, clock = now::get, resultsStore = JsonDefenderResults(file))
        lateinit var initial: DefenderViewModel
        instrumentation.runOnMainSync { initial = create() }
        var model by mutableStateOf(initial)
        compose.setContent { val ui by model.ui.collectAsState(); DefenderApp(ui, model) { model.home() } }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("defender-variant-chords").performScrollTo().performClick()
            compose.onNodeWithTag("defender-quality-DOMINANT7").performScrollTo().performClick()
            compose.onNodeWithTag("defender-chord-inversion-3").performScrollTo().performClick()
            compose.onNodeWithTag("defender-chord-clefs-BASS").performScrollTo().performClick()
            compose.onNodeWithTag("defender-count-1").performScrollTo().performClick()
            compose.onNodeWithTag("defender-start").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.game.phase == DefenderPhase.RUNNING }
            repeat(24) {
                compose.runOnIdle {
                    while (model.ui.value.game.enemies.isEmpty()) { now.addAndGet(100); model.tick() }
                    now.incrementAndGet()
                }
                val chord = model.ui.value.game.enemy!!.chord!!
                assertEquals(3, chord.inversion); assertEquals(DefenderClef.BASS, chord.root.clef)
                assertNotEquals(chord.root.name, chord.notes.first().name)
                compose.onNodeWithTag("defender-name-${chord.root.name.name}").performClick()
                compose.onNodeWithTag("defender-accidental-${chord.root.accidental.name}").performClick()
                compose.onNodeWithTag("defender-answer-quality-DOMINANT7").performScrollTo().performClick()
                compose.onNodeWithTag("defender-fire").performSemanticsAction(SemanticsActions.OnClick) { it() }
            }
            compose.runOnIdle {
                while (model.ui.value.game.phase == DefenderPhase.RUNNING) { now.addAndGet(100); model.tick() }
            }
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            assertTrue(played.isNotEmpty()); assertTrue(played.all { it.size == 4 })
            captureRhythmEvidence(compose, "defender-d5", "septima-fa-invertida-resumen")
            compose.onNodeWithTag("defender-history-open").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("defender-history-screen").performScrollToNode(hasTestTag("defender-history-detail"))
            compose.onAllNodesWithTag("defender-history-detail").onFirst().performClick()
            captureRhythmEvidence(compose, "defender-d5", "septima-fa-detalle")
            compose.runOnIdle { model.home(); model = create() }
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            assertEquals(DefenderChordTarget(DefenderChordQuality.DOMINANT7, 3, DefenderChordClefs.BASS),
                model.ui.value.history.results.single().game.configuration.chordTarget)
            assertEquals(setOf(DefenderChordTarget(DefenderChordQuality.MAJOR)), model.ui.value.history.progressFor("chords").unlockedTargets)
        } finally { instrumentation.runOnMainSync { model.home() } }
    }

    @Test fun mezclaDeSeptimasConReduccionPausaYBloqueosDePerfilNuevo() {
        val instrumentation = InstrumentationRegistry.getInstrumentation(); val now = AtomicLong()
        lateinit var model: DefenderViewModel
        instrumentation.runOnMainSync { model = DefenderViewModel(audio, clock = now::get) }
        compose.setContent { val ui by model.ui.collectAsState(); DefenderApp(ui, model, reduceMotion = true) { model.home() } }
        try {
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            compose.onNodeWithTag("defender-variant-chords").performScrollTo().performClick()
            compose.onNodeWithTag("defender-mode-progression").performScrollTo().performClick()
            compose.onNodeWithTag("defender-chord-inversion-1").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("defender-chord-clefs-BASS").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("defender-mode-practice").performScrollTo().performClick()
            compose.onNodeWithTag("defender-quality-MAJOR7").performScrollTo().performClick()
            compose.onNodeWithTag("defender-chord-inversion-1").performScrollTo().performClick()
            compose.onNodeWithTag("defender-chord-clefs-MIXED").performScrollTo().performClick()
            compose.onNodeWithTag("defender-start").performScrollTo().performClick()
            compose.runOnIdle {
                var attempts = 0
                while (model.ui.value.game.events.filter { it.kind == "SPAWN" }.map { it.enemy!!.note.clef }.distinct().size < 2 && attempts++ < 300) {
                    now.addAndGet(100); model.tick()
                }
            }
            val clefs = model.ui.value.game.events.filter { it.kind == "SPAWN" }.map { it.enemy!!.note.clef }.toSet()
            assertEquals(DefenderClef.entries.toSet(), clefs)
            captureRhythmEvidence(compose, "defender-d5", "mezcla-septima-segundas")
            compose.onNodeWithTag("defender-pause").performClick()
            val frozen = model.ui.value.game.activeMillis
            compose.runOnIdle { now.addAndGet(5000); model.tick() }
            assertEquals(frozen, model.ui.value.game.activeMillis)
            compose.onNodeWithTag("defender-resume").performClick()
            assertEquals(frozen, model.ui.value.game.activeMillis)
        } finally { instrumentation.runOnMainSync { model.home() } }
    }
}

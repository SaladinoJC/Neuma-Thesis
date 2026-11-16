package ar.edu.unmdp.entrenamiento

import androidx.compose.runtime.*
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class RhythmHistoryAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test fun guardarReabrirFiltrarConsultarPartituraYRepetirComoPractica() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = File(instrumentation.targetContext.cacheDir, "rhythm-r5-${System.nanoTime()}").apply { mkdirs() }
        val clock = AtomicLong()
        val file = File(directory, "rhythm-results.json")
        var starts = 0
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart {
                starts++; return RhythmAudioStart(clock.get(), "controlled-r5", 0)
            }
            override fun tap() = Unit
            override fun stop() = Unit
        }
        fun create() = RhythmViewModel(audio, clock::get, { clock.get() / 1_000_000L }, resultsStore = JsonRhythmResults(file))
        lateinit var model: RhythmViewModel
        instrumentation.runOnMainSync { model = create() }
        var current by mutableStateOf(model)
        compose.setContent { val ui by current.ui.collectAsState(); RhythmApp(ui, current) { current.home() } }
        try {
            for (code in listOf("R10", "R15", "R17")) compose.onNodeWithTag("rhythm-category-$code").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-metronome").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
            compose.waitUntil(10_000) { current.ui.value.session.phase == RhythmPhase.PREVIEW }
            val score = current.ui.value.session.score!!
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { current.ui.value.session.phase == RhythmPhase.COUNT_IN }
            val attacks = current.ui.value.session.attacks.toList()
            for (attack in attacks) {
                compose.runOnIdle { clock.set(4_000_000_000L + attack.offsetNanos) }
                compose.onNodeWithTag("rhythm-tap").performSemanticsAction(SemanticsActions.OnClick) { it() }
            }
            compose.runOnIdle { clock.set(20_000_000_001L) }
            compose.waitUntil(10_000) { current.ui.value.session.phase == RhythmPhase.RESULTS && !current.ui.value.historyBusy }
            val record = runBlocking { JsonRhythmResults(file).all().single() }
            assertEquals(score, record.result.score)
            assertEquals(0, record.result.omissions)
            assertTrue(record.result.silences.all { it.respected == true })
            compose.runOnIdle { current.background(); current = create() }
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            assertEquals(RhythmPhase.SETUP, current.ui.value.session.phase)
            assertEquals(1, starts)
            compose.onNodeWithTag("rhythm-open-history").performScrollTo().performClick()
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            historyNode("rhythm-history-detail").assertExists()
            captureRhythmEvidence(compose, "r5-history", "lista")
            historyNode("rhythm-history-filters").performClick()
            historyNode("rhythm-history-category").performClick()
            compose.onNodeWithText("R02 · Redondas").performScrollTo().performClick()
            historyNode("rhythm-history-empty").assertTextEquals("No hay ejercicios con estos filtros.")
            captureRhythmEvidence(compose, "r5-history", "sin-coincidencias")
            historyNode("rhythm-history-clear-filters").performClick()
            historyNode("rhythm-history-category").performClick()
            compose.onNodeWithText("R15 · Tresillo de corcheas").performScrollTo().performClick()
            assertEquals(1, current.ui.value.history.entries.size)
            historyNode("rhythm-history-filters").performClick()
            historyNode("rhythm-history-detail").performClick()
            historyNode("rhythm-score").assertIsDisplayed()
            captureRhythmEvidence(compose, "r5-history", "partitura")
            historyNode("rhythm-silence-detail-0").assertExists()
            captureRhythmEvidence(compose, "r5-history", "silencios")
            historyNode("rhythm-history-technical").performClick()
            historyNode("rhythm-record-fingerprint").assertTextEquals("Huella: ${record.fingerprint}")
            captureRhythmEvidence(compose, "r5-history", "datos-ejercicio")
            historyNode("rhythm-history-repeat").performClick()
            assertEquals(score, current.ui.value.session.score)
            assertTrue(current.ui.value.session.repeated)
            assertEquals(1, starts)
            compose.onNodeWithTag("rhythm-score").performScrollTo().assertIsDisplayed()
            captureRhythmEvidence(compose, "r5-history", "repeticion")
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { current.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.onNodeWithTag("rhythm-interrupt").performClick()
            compose.waitUntil(10_000) { !current.ui.value.historyBusy }
            val records = runBlocking { JsonRhythmResults(file).all() }
            val repeat = records.single { it.id != record.id }
            assertEquals(record.id, repeat.repeatedFromId)
            assertEquals(record.fingerprint, repeat.fingerprint)
            assertFalse(repeat.result.completed)
            val exported = runBlocking { Json.decodeFromString(DemonstrationExport.serializer(), Demonstration(directory).export("test")) }
            assertEquals(records, exported.rhythm)
        } finally { compose.runOnIdle { model.background(); current.background() } }
    }

    @Test fun falloVisibleYReintentoGuardanUnSoloEjercicioInterrumpido() {
        val saved = MemoryRhythmResults()
        var failing = true
        var readingFails = false
        val store = object : RhythmResults {
            override suspend fun all(): List<RhythmRecord> {
                if (readingFails) error("Lectura interrumpida")
                return saved.all()
            }
            override suspend fun save(record: RhythmRecord) { if (failing) error("Almacenamiento no disponible"); saved.save(record) }
        }
        val audio = object : RhythmAudio {
            override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(0, "controlled-r5", 0)
            override fun tap() = Unit
            override fun stop() = Unit
        }
        val model = RhythmViewModel(audio, { 0 }, { 0 }, resultsStore = store)
        compose.setContent { val ui by model.ui.collectAsState(); RhythmApp(ui, model) { model.home() } }
        try {
            compose.onNodeWithTag("rhythm-category-R20").performScrollTo().performClick()
            compose.openRhythmPreparationIfCollapsed()
            compose.onNodeWithTag("rhythm-generate").performScrollTo().performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.onNodeWithTag("rhythm-interrupt").performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.ui.value.history.error != null }
            compose.onNodeWithTag("rhythm-history-error").performScrollTo().assertTextContains("Almacenamiento no disponible", substring = true)
            captureRhythmEvidence(compose, "r5-history", "fallo")
            val pending = model.pendingForExport().single()
            compose.onNodeWithTag("rhythm-history-retry").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy }
            assertNotNull(model.ui.value.history.error)
            compose.runOnIdle { failing = false }
            compose.onNodeWithTag("rhythm-history-retry").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.pendingForExport().isEmpty() }
            compose.onNodeWithTag("rhythm-save-status").performScrollTo().assertTextEquals("Ejercicio guardado")
            captureRhythmEvidence(compose, "r5-history", "recuperado")
            val stored = runBlocking { saved.all().single() }
            assertEquals(pending.id, stored.id)
            assertFalse(stored.result.completed)
            assertTrue(stored.result.attacks.all { it.kind == null })
            compose.runOnIdle { readingFails = true }
            compose.onNodeWithTag("rhythm-open-history").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.ui.value.history.error != null }
            historyNode("rhythm-history-error").assertTextContains("Lectura interrumpida", substring = true)
            captureRhythmEvidence(compose, "r5-history", "lectura")
            compose.runOnIdle { readingFails = false }
            historyNode("rhythm-history-retry").performClick()
            compose.waitUntil(10_000) { !model.ui.value.historyBusy && model.ui.value.history.error == null }
        } finally { compose.runOnIdle { model.background() } }
    }

    private fun historyNode(tag: String): SemanticsNodeInteraction {
        compose.onNodeWithTag("rhythm-history-screen").performScrollToNode(hasTestTag(tag))
        return compose.onNodeWithTag(tag).performScrollTo()
    }
}

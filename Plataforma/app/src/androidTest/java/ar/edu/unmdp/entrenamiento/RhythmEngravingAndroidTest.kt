package ar.edu.unmdp.entrenamiento

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.rhythm.*
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import ar.edu.unmdp.entrenamiento.ui.RhythmNotationWebView
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class RhythmEngravingAndroidTest {
    @get:Rule val compose = createComposeRule()
    private val clock = AtomicLong()
    private val model = RhythmViewModel(object : RhythmAudio {
        override suspend fun start(bpm: Int, metronome: Boolean) = RhythmAudioStart(clock.get(), "controlled-engraving", 0)
        override fun tap() = Unit
        override fun stop() = Unit
    }, clock::get, { clock.get() / 1_000_000 })

    private fun prepare(reduced: Boolean = false, category: RhythmCategory = RhythmCategory.TIED_QUARTERS): RhythmNotationWebView {
        lateinit var root: View
        compose.setContent { root = LocalView.current; val ui by model.ui.collectAsState(); RhythmApp(ui, model, reduced) { model.home() } }
        compose.runOnIdle { model.select(category); model.generate(12L) }
        compose.waitUntil(10_000) { model.ui.value.session.phase == RhythmPhase.PREVIEW }
        compose.onNodeWithTag("rhythm-score").performScrollTo()
        compose.awaitRhythmScore()
        compose.onNodeWithTag("rhythm-continuous-score").performScrollTo().assertIsDisplayed()
        lateinit var web: RhythmNotationWebView
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            fun find(view: View): RhythmNotationWebView? {
                if (view is RhythmNotationWebView) return view
                if (view is ViewGroup) for (index in 0 until view.childCount) find(view.getChildAt(index))?.let { return it }
                return null
            }
            web = requireNotNull(find(root.rootView))
        }
        return web
    }

    private fun inspect(web: RhythmNotationWebView, expression: String = "RhythmNotation.inspect()"): JSONObject {
        val latch = CountDownLatch(1)
        var value = "null"
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            web.evaluateJavascript(expression) { value = it; latch.countDown() }
        }
        assertTrue("Respuesta del renderer", latch.await(5, TimeUnit.SECONDS))
        return JSONObject(value)
    }
    private fun awaitInspection(web: RhythmNotationWebView, condition: (JSONObject) -> Boolean): JSONObject {
        var result = JSONObject()
        compose.waitUntil(10_000) { result = inspect(web); condition(result) }
        return result
    }
    private fun tap() { compose.onNodeWithTag("rhythm-tap").performSemanticsAction(SemanticsActions.OnClick) { it() } }

    @Test fun svgLocalSeDeslizaSinRedibujarYLigadurasCompartenAcierto() {
        val web = prepare()
        try {
            val initial = inspect(web)
            assertTrue(initial.getBoolean("ready"))
            assertEquals("rgba(0, 0, 0, 0)", initial.getString("background"))
            compose.onNodeWithTag("rhythm-continuous-score").performTouchInput { swipeLeft() }
            awaitInspection(web) { it.getDouble("scrollLeft") > 0 }
            assertTrue(model.ui.value.session.taps.isEmpty())
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            val attack = model.ui.value.session.attacks.first { item ->
                model.ui.value.session.score!!.occurrences.single { it.index == item.occurrenceIndex }.category == RhythmCategory.TIED_QUARTERS
            }
            compose.runOnIdle { clock.set(4_000_000_000L + attack.offsetNanos) }; tap()
            val painted = awaitInspection(web) { data ->
                val heads = data.getJSONArray("heads")
                (0 until heads.length()).count { heads.getJSONObject(it).optInt("attackIndex", -1) == attack.index && heads.getJSONObject(it).getString("color") == "rgb(49, 88, 68)" } == 2
            }
            captureRhythmEvidence(compose, "rhythm-engraving", "acierto-ligadura")
            val before = painted.getDouble("scrollLeft")
            compose.runOnIdle { clock.addAndGet(250_000_000L) }
            val after = awaitInspection(web) { it.getDouble("scrollLeft") > before }
            assertEquals(painted.getDouble("markerX"), after.getDouble("markerX"), 0.01)
            assertEquals(initial.getInt("renderCount"), after.getInt("renderCount"))
            compose.runOnIdle { clock.set(20_000_000_001L) }
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.RESULTS }
            assertEquals(TapKind.ON_TIME, model.ui.value.session.result!!.attacks[attack.index].kind)
            compose.onNodeWithTag("rhythm-repeat").performScrollTo().performClick()
            compose.awaitRhythmScore()
            compose.onNodeWithTag("rhythm-score").performScrollTo()
            awaitInspection(web) { data ->
                val heads = data.getJSONArray("heads")
                (0 until heads.length()).all { heads.getJSONObject(it).getString("color") == "rgb(40, 57, 47)" }
            }
            captureRhythmEvidence(compose, "rhythm-engraving", "repeticion-sin-marcas")
        } finally { compose.runOnIdle { model.background() } }
    }

    @Test fun movimientoReducidoMantieneVentanasEstaticasTambienConSemicorcheas() {
        val web = prepare(reduced = true, category = RhythmCategory.SIXTEENTHS)
        try {
            compose.onNodeWithTag("rhythm-play").performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            compose.runOnIdle { clock.set(4_250_000_000L) }; tap()
            val first = awaitInspection(web) { !it.getBoolean("markerVisible") && it.getDouble("scrollLeft") > 0 }
            compose.runOnIdle { clock.set(4_750_000_000L) }; tap()
            val second = inspect(web)
            assertEquals(first.getDouble("scrollLeft"), second.getDouble("scrollLeft"), 0.01)
            compose.runOnIdle { clock.set(5_250_000_000L) }; tap()
            awaitInspection(web) { it.getDouble("scrollLeft") > second.getDouble("scrollLeft") }
            captureRhythmEvidence(compose, "rhythm-engraving", "movimiento-reducido")
        } finally { compose.runOnIdle { model.background() } }
    }

    @Test fun falloDelRendererOfreceLecturaNativaSinBloquearPlay() {
        val web = prepare()
        try {
            val key = model.ui.value.session.score!!.fingerprint
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                web.evaluateJavascript("RhythmHost.status('$key','renderer de prueba no disponible')", null)
            }
            compose.waitUntil(5_000) {
                compose.onAllNodesWithText("No se pudo abrir la partitura continua. Usá la lectura por compases.").fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithText("No se pudo abrir la partitura continua. Usá la lectura por compases.").performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("rhythm-play").assertIsEnabled().performClick()
            compose.waitUntil(5_000) { model.ui.value.session.phase == RhythmPhase.COUNT_IN }
            tap()
            assertEquals(TapKind.PREPARATION, model.ui.value.session.taps.single().kind)
            captureRhythmEvidence(compose, "rhythm-engraving", "alternativa-nativa")
        } finally { compose.runOnIdle { model.background() } }
    }
}

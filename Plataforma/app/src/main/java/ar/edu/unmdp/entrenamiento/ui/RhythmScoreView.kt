package ar.edu.unmdp.entrenamiento.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import ar.edu.unmdp.entrenamiento.rhythm.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream

@Serializable
private data class ScorePalette(val ink: String, val correct: String, val missed: String,
    val timing: String, val focus: String)
@Serializable
private data class ScorePayload(val score: RhythmNotationScore, val reduced: Boolean, val palette: ScorePalette)
@Serializable
private data class ScoreTransport(val phase: String, val elapsedBeats: Double, val kinds: List<String?>)

private fun Color.css() = "#%06x".format(toArgb() and 0xffffff)

/** A local engraver, never an input/audio clock. The pad and judging remain native. */
@Composable
internal fun RhythmScoreView(score: RhythmScore, state: RhythmState, reduced: Boolean,
    modifier: Modifier = Modifier, onReady: (Boolean) -> Unit = {}) {
    val notation = remember(score) { score.toNotation() }
    var failed by remember(notation) { mutableStateOf(false) }
    var ready by remember(notation) { mutableStateOf(false) }
    val readiness by rememberUpdatedState(onReady)
    val report by rememberUpdatedState<(String, String) -> Unit>({ key, message ->
        if (key == notation.key) {
            failed = message.isNotEmpty(); ready = message.isEmpty()
            readiness(true) // Native fallback is also playable.
        }
    })
    val payload = remember(notation, reduced) {
        Json.encodeToString(ScorePayload(notation, reduced, ScorePalette(RhythmTokens.Text.css(),
            RhythmTokens.Primary.css(), RhythmTokens.ErrorText.css(), RhythmTokens.ErrorText.css(), RhythmTokens.Focus.css())))
    }
    // Result revisions (including late input) have the same attack ownership as the session.
    val transport = Json.encodeToString(ScoreTransport(state.phase.name,
        state.elapsedNanos.toDouble() / Beat(1).nanos(score.bpm), (state.result?.attacks ?: state.attacks).map { it.kind?.name }))
    Column(modifier.testTag("rhythm-score")) {
        if (failed) {
            Text("No se pudo abrir la partitura continua. Usá la lectura por compases.", color = RhythmTokens.ErrorText)
            score.bars.forEach { bar ->
                Text("Compás ${bar.index + 1}", style = MaterialTheme.typography.labelMedium)
                RhythmBarDrawing(bar, score, state, Modifier.fillMaxWidth().height(100.dp))
            }
        } else {
            // Compose owns the accessible description, avoiding duplicate WebView/DOM focus.
            val description = score.bars.joinToString(". ") { bar ->
                "Compás ${bar.index + 1}, ${score.beatsPerBar}/4: " + bar.occurrences.joinToString("; ") { it.category.label }
            }
            AndroidView(factory = { context ->
                RhythmNotationWebView(context) { key, message -> report(key, message) }
            }, modifier = Modifier.fillMaxWidth().height(176.dp).testTag("rhythm-continuous-score")
                .semantics { contentDescription = description },
                onRelease = { it.release() }, update = { it.show(payload, transport) })
            if (!ready) Text("Preparando partitura…", modifier = Modifier.testTag("rhythm-score-loading"),
                style = MaterialTheme.typography.bodySmall)
            if (state.phase == RhythmPhase.PREVIEW || state.phase == RhythmPhase.RESULTS) {
                Text("Deslizá la partitura para revisar los cuatro compases.", color = RhythmTokens.TextSecondary,
                    style = MaterialTheme.typography.bodySmall)
            } else if (reduced) Text("Lectura por pulsos · movimiento reducido", color = RhythmTokens.TextSecondary,
                style = MaterialTheme.typography.bodySmall)
        }
    }
    LaunchedEffect(notation) { readiness(false) }
}

/** Only three packaged files are served. No external pages or native game commands. */
@SuppressLint("SetJavaScriptEnabled", "ViewConstructor") // Constructed programmatically, never inflated from XML.
internal class RhythmNotationWebView(context: Context, private val onStatus: (String, String) -> Unit) : WebView(context) {
    private var pageReady = false
    private var released = false
    private var inFlight = false
    private var payload = ""
    private var sentPayload = ""
    private var transport = ""
    private var sentTransport = ""
    private val timeout = Runnable { statusFromPayload("La partitura no terminó de cargar") }

    init {
        setBackgroundColor(AndroidColor.TRANSPARENT)
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        isVerticalScrollBarEnabled = false
        settings.apply {
            javaScriptEnabled = true
            allowFileAccess = false
            allowContentAccess = false
            blockNetworkLoads = true
            domStorageEnabled = false
            textZoom = 100 // Musical engraving has its own dp scale; native UI still follows font scale.
            builtInZoomControls = false
            displayZoomControls = false
        }
        addJavascriptInterface(object {
            @JavascriptInterface fun status(key: String, message: String) {
                post { if (!released) { removeCallbacks(timeout); onStatus(key, message) } }
            }
        }, "RhythmHost")
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = true
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse {
                val uri = request.url
                val file = if (uri.scheme == "https" && uri.host == "appassets.androidplatform.net")
                    uri.path?.removePrefix("/assets/rhythm/") else null
                val mime = when (file) { "score.html" -> "text/html"; "score.js", "vexflow-bravura-5.0.0.js" -> "text/javascript"; else -> null }
                return if (mime != null) {
                    WebResourceResponse(mime, "UTF-8", context.assets.open("rhythm/$file"))
                } else {
                    WebResourceResponse("text/plain", "UTF-8", 403, "Blocked", emptyMap(), ByteArrayInputStream(byteArrayOf()))
                }
            }
            override fun onPageFinished(view: WebView, url: String) { pageReady = true; flush() }
            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                statusFromPayload("Renderizador no disponible")
                return true
            }
        }
        loadUrl("https://appassets.androidplatform.net/assets/rhythm/score.html")
    }

    fun show(score: String, state: String) {
        if (payload != score) { removeCallbacks(timeout); postDelayed(timeout, 10_000) }
        payload = score; transport = state; flush()
    }
    private fun flush() {
        if (!pageReady || released || inFlight || payload.isEmpty()) return
        val script = buildString {
            append("try { if (!window.RhythmNotation) throw new Error('Notación no disponible');")
            if (payload != sentPayload) append("RhythmNotation.setScore($payload);")
            if (transport != sentTransport || payload != sentPayload) append("RhythmNotation.update($transport);")
            append("null; } catch(e) { String(e); }")
        }
        if (payload == sentPayload && transport == sentTransport) return
        sentPayload = payload; sentTransport = transport; inFlight = true
        evaluateJavascript(script) { result ->
            inFlight = false
            if (released) return@evaluateJavascript
            if (result != "null") statusFromPayload("No se pudo dibujar la partitura")
            else flush() // Coalesce visual updates rather than queueing ticks behind the WebView.
        }
    }
    private fun statusFromPayload(message: String) {
        if (!released && payload.isNotEmpty()) {
            val key = Json.decodeFromString<ScorePayload>(payload).score.key
            onStatus(key, message)
        }
    }
    fun release() {
        released = true
        removeCallbacks(timeout)
        stopLoading(); removeJavascriptInterface("RhythmHost"); destroy()
    }
}

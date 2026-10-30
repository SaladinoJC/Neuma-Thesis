package ar.edu.unmdp.entrenamiento

import android.graphics.Bitmap
import android.os.SystemClock
import android.view.Choreographer
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** WebView font loading is asynchronous and is not a Compose idling resource. */
internal fun ComposeContentTestRule.awaitRhythmScore() {
    waitUntil(15_000) {
        if (!mainClock.autoAdvance) mainClock.advanceTimeByFrame()
        onAllNodesWithTag("rhythm-play").fetchSemanticsNodes().any { !it.config.contains(SemanticsProperties.Disabled) }
    }
}

/** Solo evidencia de pruebas: no introduce esperas en el reloj ni la entrada del producto. */
internal fun captureRhythmEvidence(compose: ComposeContentTestRule, folder: String, name: String) {
    compose.waitForIdle()
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val context = instrumentation.targetContext
    val directory = File(context.getExternalFilesDir(null), folder).apply { mkdirs() }
    val width = context.resources.configuration.screenWidthDp
    val font = context.resources.configuration.fontScale
    val presented = CountDownLatch(1)
    instrumentation.runOnMainSync {
        var remaining = 3
        val callback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (--remaining == 0) presented.countDown()
                else Choreographer.getInstance().postFrameCallback(this)
            }
        }
        Choreographer.getInstance().postFrameCallback(callback)
    }
    assertTrue("La captura necesita frames presentados", presented.await(5, TimeUnit.SECONDS))
    SystemClock.sleep(1_000)
    val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
    File(directory, "$width-$font-$name.png").outputStream().use {
        assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
    }
}

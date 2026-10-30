package ar.edu.unmdp.entrenamiento

import android.Manifest
import android.graphics.Bitmap
import android.media.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import ar.edu.unmdp.entrenamiento.audio.AndroidTunerReferencePlayer
import ar.edu.unmdp.entrenamiento.tuner.*
import ar.edu.unmdp.entrenamiento.ui.TunerApp
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

class TunerReferenceAndroidTest {
    @get:Rule val compose = createComposeRule()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    @Test fun bancoCompletoDecodificaConAlturaAbsolutaYLosTresTimbresReproducen() = runBlocking {
        val context = instrumentation.targetContext
        val estimator = YinEstimator()
        var maximum = 0.0
        for (timbre in listOf(ReferenceTimbre.GUITAR, ReferenceTimbre.PIANO)) {
            for (midi in tunerTargetRange) {
                val pcm = decode(tunerReferenceAsset(timbre, midi))
                assertEquals(52920, pcm.size)
                val cents = listOf(0.2, 0.4, 0.6).map { offset ->
                    val start = (offset * 44100).toInt()
                    val estimate = estimator.estimate(pcm.copyOfRange(start, start + 4096))
                    assertEquals("$timbre MIDI $midi: ${estimate.reason}", midi, estimate.midi)
                    abs(requireNotNull(estimate.cents(midi)))
                }
                maximum = maxOf(maximum, cents.max())
                assertTrue("$timbre MIDI $midi cents=$cents", cents.all { it < 5.0 })
            }
        }
        val player = AndroidTunerReferencePlayer(context)
        for (midi in tunerTargetRange) {
            val pcm = referenceTone(midi)
            val estimate = estimator.estimate(FloatArray(4096) { pcm[4410 + it] / 32768f })
            assertEquals(midi, estimate.midi)
            assertTrue(abs(requireNotNull(estimate.cents(midi))) < 2.0)
        }
        for (timbre in ReferenceTimbre.entries) {
            for (midi in listOf(36, 40, 64)) {
                val frames = player.play(timbre, midi)
                assertTrue(frames >= if (timbre == ReferenceTimbre.PURE) 26460 else 52920)
                println("A4 audio $timbre midi=$midi frames=$frames")
            }
        }
        println("A4 banco 58 OGG x 3 ventanas YIN, máximo absoluto=$maximum cents; emulador, sin medición acústica")
    }

    @Test fun capturaRealSeCierraAntesDeReferenciaYReabreConMargenSinAutoarranqueAlSalir() {
        instrumentation.uiAutomation.executeShellCommand("pm grant ar.edu.unmdp.entrenamiento ${Manifest.permission.RECORD_AUDIO}").close()
        val active = AtomicInteger(); val opened = AtomicInteger(); val released = AtomicInteger()
        val completedAt = AtomicLong(); val resumedAt = AtomicLong()
        val real = AndroidVoiceCapture()
        val recorder = object : VoiceCapture {
            override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                active.incrementAndGet(); opened.incrementAndGet()
                if (opened.get() > 1) resumedAt.set(System.nanoTime())
                try { real.capture(configuration, onMeasurement) }
                finally { active.decrementAndGet(); released.incrementAndGet() }
            }
        }
        val realPlayer = AndroidTunerReferencePlayer(instrumentation.targetContext)
        val player = object : TunerReferencePlayer {
            override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int {
                assertEquals(0, active.get()); assertEquals(opened.get(), released.get())
                return realPlayer.play(timbre, midi).also { completedAt.set(System.nanoTime()) }
            }
        }
        lateinit var model: TunerViewModel
        instrumentation.runOnMainSync { model = TunerViewModel(recorder, referencePlayer = player) }
        compose.setContent { val ui by model.ui.collectAsState(); TunerApp(ui, model, model::stop) }
        try {
            compose.onNodeWithText("6 · Mi2").performScrollTo().performClick()
            compose.onNodeWithTag("tuner-start").performScrollTo().performClick()
            compose.waitUntil(10_000) { active.get() == 1 }
            compose.onNodeWithTag("tuner-reference").performScrollTo().performClick()
            compose.onNodeWithTag("tuner-reference").assertIsNotEnabled()
            compose.onNodeWithTag("tuner-reference-status").assertExists()
            assertFalse(model.ui.value.tuning.running)
            screenshot("referencia-guitarra")
            compose.waitUntil(10_000) { opened.get() == 2 && !model.ui.value.referenceBusy }
            assertTrue((resumedAt.get() - completedAt.get()) / 1_000_000 >= TUNER_REFERENCE_SETTLE_MS)
            assertFalse(model.ui.value.tuning.inTune)
            compose.onNodeWithTag("tuner-timbre-PIANO").performScrollTo().performClick()
            compose.waitUntil { !model.ui.value.configurationBusy }
            compose.onNodeWithTag("tuner-timbre-PIANO").assertIsSelected()
            compose.onNodeWithTag("tuner-reference").performClick()
            compose.onNodeWithTag("tuner-reference-cancel").performScrollTo().performClick()
            compose.waitUntil(10_000) { !model.ui.value.referenceBusy && active.get() == 0 }
            assertEquals(2, opened.get())
            screenshot("referencia-detenida")
        } finally { instrumentation.runOnMainSync { model.stop() } }
    }

    private fun screenshot(name: String) {
        val directory = File(instrumentation.targetContext.getExternalFilesDir(null), "afinador-a4").apply { mkdirs() }
        instrumentation.uiAutomation.takeScreenshot().let { bitmap ->
            File(directory, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
        }
    }

    /** Decodificación de prueba: PCM → estimador, independiente del reproductor. */
    private fun decode(asset: String): FloatArray {
        val extractor = MediaExtractor()
        try {
            instrumentation.targetContext.assets.openFd(asset).use { extractor.setDataSource(it.fileDescriptor, it.startOffset, it.declaredLength) }
            extractor.selectTrack(0)
            val format = extractor.getTrackFormat(0)
            val codec = MediaCodec.createDecoderByType(requireNotNull(format.getString(MediaFormat.KEY_MIME)))
            try {
                format.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
                codec.configure(format, null, null, 0); codec.start()
                val output = ByteArrayOutputStream()
                val info = MediaCodec.BufferInfo()
                var inputDone = false
                val deadline = System.nanoTime() + 5_000_000_000
                while (System.nanoTime() < deadline) {
                    if (!inputDone) {
                        val index = codec.dequeueInputBuffer(10000)
                        if (index >= 0) {
                            val buffer = requireNotNull(codec.getInputBuffer(index)); buffer.clear()
                            val size = extractor.readSampleData(buffer, 0)
                            if (size < 0) { codec.queueInputBuffer(index, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM); inputDone = true }
                            else { codec.queueInputBuffer(index, 0, size, extractor.sampleTime, 0); extractor.advance() }
                        }
                    }
                    val index = codec.dequeueOutputBuffer(info, 10000)
                    if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        assertEquals(44100, codec.outputFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE))
                        assertEquals(1, codec.outputFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT))
                    }
                    if (index >= 0) {
                        requireNotNull(codec.getOutputBuffer(index)).let { buffer ->
                            buffer.position(info.offset); buffer.limit(info.offset + info.size)
                            val bytes = ByteArray(info.size); buffer.get(bytes); output.write(bytes)
                        }
                        codec.releaseOutputBuffer(index, false)
                        if (info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            val bytes = output.toByteArray()
                            val pcm = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                            return FloatArray(bytes.size / 2) { pcm.get() / 32768f }
                        }
                    }
                }
                error("Tiempo de decodificación agotado: $asset")
            } finally { codec.release() }
        } finally { extractor.release() }
    }
}

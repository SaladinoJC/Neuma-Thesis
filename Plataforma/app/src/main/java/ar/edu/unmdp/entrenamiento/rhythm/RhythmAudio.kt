package ar.edu.unmdp.entrenamiento.rhythm

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTimestamp
import android.media.AudioTrack
import android.media.AudioManager
import android.media.SoundPool
import android.content.Context
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlinx.coroutines.*
import kotlin.math.PI
import kotlin.math.sin

data class RhythmAudioStart(val originNanos: Long, val source: String, val uncertaintyNanos: Long?)
interface RhythmAudio {
    suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart
    suspend fun start(bpm: Int, metronome: Boolean, beatsPerBar: Int): RhythmAudioStart = start(bpm, metronome)
    fun tap()
    fun stop()
    fun close() = stop()
}

/** One static PCM timeline for count-in/pulses; it contains no target-pattern attacks. */
class AndroidRhythmAudio(context: Context) : RhythmAudio {
    private val cacheDir = context.applicationContext.cacheDir
    private val lock = Any()
    private var timeline: AudioTrack? = null
    private var feedback: SoundPool? = null
    private var feedbackId = 0
    private var lastFrames = 0
    fun playbackFrames(): Int = synchronized(lock) { timeline?.playbackHeadPosition ?: lastFrames }
    fun activeTracks(): Int = synchronized(lock) { (if (timeline != null) 1 else 0) + (if (feedback != null) 1 else 0) }

    override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart = start(bpm, metronome, 4)
    override suspend fun start(bpm: Int, metronome: Boolean, beatsPerBar: Int): RhythmAudioStart = withContext(Dispatchers.IO) {
        require(bpm in 40..120)
        require(beatsPerBar in 2..4)
        val sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC).takeIf { it in 8000..192000 } ?: 48000
        // El reloj debe estar disponible antes del primer pulso audible, incluso en el fallback.
        val leadFrames = sampleRate * 3 / 4
        val leadNanos = leadFrames * 1_000_000_000L / sampleRate
        val totalPulses = beatsPerBar * 5
        val pcm = ShortArray(leadFrames + (totalPulses.toLong() * 60 * sampleRate / bpm).toInt())
        for (pulse in 0 until if (metronome) totalPulses else beatsPerBar) {
            currentCoroutineContext().ensureActive()
            val click = click(sampleRate, if (pulse % beatsPerBar == 0) 1300.0 else 900.0)
            val frame = leadFrames + (pulse.toLong() * 60 * sampleRate / bpm).toInt()
            click.copyInto(pcm, frame, 0, minOf(click.size, pcm.size - frame))
        }
        val track = track(pcm, sampleRate)
        var tapPool: SoundPool? = null
        var adopted = false
        try {
            val pool = SoundPool.Builder().setMaxStreams(8).setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build()
            tapPool = pool
            val loaded = CompletableDeferred<Unit>()
            pool.setOnLoadCompleteListener { _, _, status ->
                if (status == 0) loaded.complete(Unit) else loaded.completeExceptionally(IllegalStateException("No se pudo cargar el sonido del tap."))
            }
            val tapFile = File(cacheDir, "rhythm-tap-v1-$sampleRate.wav")
            tapFile.writeBytes(wav(click(sampleRate, 550.0), sampleRate))
            val sampleId = pool.load(tapFile.path, 1)
            check(sampleId != 0) { "No se pudo preparar el sonido del tap." }
            withTimeout(3000) { loaded.await() }
            currentCoroutineContext().ensureActive()
            val before = System.nanoTime()
            synchronized(lock) {
                stopLocked()
                timeline = track; feedback = pool; feedbackId = sampleId; adopted = true
                track.play()
            }
            val after = System.nanoTime()
            val timestamp = AudioTimestamp()
            val available = withTimeoutOrNull(500) {
                while (true) {
                    ensureActive()
                    val obtained = synchronized(lock) { timeline === track && track.getTimestamp(timestamp) }
                    if (obtained && timestamp.framePosition > 0 && timestamp.nanoTime > 0) return@withTimeoutOrNull true
                    delay(5)
                }
                @Suppress("UNREACHABLE_CODE") false
            } == true
            if (available) RhythmAudioStart(timestamp.nanoTime - timestamp.framePosition * 1_000_000_000L / sampleRate + leadNanos,
                "AudioTimestamp/monotonic; rate=$sampleRate; 750ms silent lead; SoundPool preloaded; touch quantization=1ms; total uncertainty unknown", null)
            else RhythmAudioStart(before + (after - before) / 2 + leadNanos,
                "play-call fallback; acoustic offset unknown", null)
        } catch (failure: Throwable) {
            synchronized(lock) {
                if (adopted) { if (timeline === track) stopLocked() }
                else { track.release(); tapPool?.release() }
            }
            throw failure
        }
    }
    override fun tap() = synchronized(lock) {
        val pool = feedback ?: return@synchronized
        check(pool.play(feedbackId, 1f, 1f, 1, 0, 1f) != 0) { "No se pudo reproducir el sonido del tap." }
    }
    override fun stop() = synchronized(lock) { stopLocked() }
    private fun stopLocked() {
        timeline?.let { lastFrames = it.playbackHeadPosition; it.release() }
        feedback?.release()
        timeline = null; feedback = null; feedbackId = 0
    }
    private fun track(pcm: ShortArray, sampleRate: Int): AudioTrack {
        val track = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(sampleRate).setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build())
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(pcm.size * 2).build()
        try {
            check(track.write(pcm, 0, pcm.size) == pcm.size && track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo preparar el audio rítmico." }
            return track
        } catch (failure: Throwable) { track.release(); throw failure }
    }
    private fun click(rate: Int, hz: Double): ShortArray = ShortArray(rate * 35 / 1000) { frame ->
        val envelope = 1.0 - frame.toDouble() / (rate * 35 / 1000)
        (sin(2 * PI * hz * frame / rate) * .18 * 32767 * envelope).toInt().toShort()
    }
    private fun wav(pcm: ShortArray, rate: Int): ByteArray = ByteBuffer.allocate(44 + pcm.size * 2).order(ByteOrder.LITTLE_ENDIAN).apply {
        put("RIFF".toByteArray()); putInt(36 + pcm.size * 2); put("WAVEfmt ".toByteArray()); putInt(16)
        putShort(1); putShort(1); putInt(rate); putInt(rate * 2); putShort(2); putShort(16)
        put("data".toByteArray()); putInt(pcm.size * 2); pcm.forEach { putShort(it) }
    }.array()
}

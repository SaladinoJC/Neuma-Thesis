package ar.edu.unmdp.entrenamiento.rhythm

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTimestamp
import android.media.AudioTrack
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
class AndroidRhythmAudio : RhythmAudio {
    private val lock = Any()
    private var timeline: AudioTrack? = null
    private var feedback: AudioTrack? = null
    private var lastFrames = 0
    fun playbackFrames(): Int = synchronized(lock) { timeline?.playbackHeadPosition ?: lastFrames }
    fun activeTracks(): Int = synchronized(lock) { listOfNotNull(timeline, feedback).size }

    override suspend fun start(bpm: Int, metronome: Boolean): RhythmAudioStart = start(bpm, metronome, 4)
    override suspend fun start(bpm: Int, metronome: Boolean, beatsPerBar: Int): RhythmAudioStart = withContext(Dispatchers.IO) {
        require(bpm in 40..120)
        require(beatsPerBar in 2..4)
        val sampleRate = 44100
        val totalPulses = beatsPerBar * 5
        val pcm = ShortArray((totalPulses.toLong() * 60 * sampleRate / bpm).toInt())
        for (pulse in 0 until if (metronome) totalPulses else beatsPerBar) {
            currentCoroutineContext().ensureActive()
            val click = click(sampleRate, if (pulse % beatsPerBar == 0) 1300.0 else 900.0)
            val frame = (pulse.toLong() * 60 * sampleRate / bpm).toInt()
            click.copyInto(pcm, frame, 0, minOf(click.size, pcm.size - frame))
        }
        val track = track(pcm)
        var tapTrack: AudioTrack? = null
        var adopted = false
        try {
            tapTrack = track(click(sampleRate, 550.0))
            currentCoroutineContext().ensureActive()
            val before = System.nanoTime()
            synchronized(lock) {
                stopLocked()
                timeline = track; feedback = tapTrack; adopted = true
                track.play()
            }
            val after = System.nanoTime()
            val timestamp = AudioTimestamp()
            val available = withTimeoutOrNull(500) {
                while (true) {
                    ensureActive()
                    val obtained = synchronized(lock) { timeline === track && track.getTimestamp(timestamp) }
                    if (obtained) return@withTimeoutOrNull true
                    delay(5)
                }
                @Suppress("UNREACHABLE_CODE") false
            } == true
            if (available) RhythmAudioStart(timestamp.nanoTime - timestamp.framePosition * 1_000_000_000L / sampleRate,
                "AudioTimestamp/monotonic; touch quantization=1ms; total uncertainty unknown", null)
            else RhythmAudioStart(before + (after - before) / 2,
                "play-call fallback; acoustic offset unknown", null)
        } catch (failure: Throwable) {
            synchronized(lock) {
                if (adopted) { if (timeline === track) stopLocked() }
                else { track.release(); tapTrack?.release() }
            }
            throw failure
        }
    }
    override fun tap() = synchronized(lock) {
        val track = feedback ?: return@synchronized
        track.pause()
        check(track.reloadStaticData() == AudioTrack.SUCCESS) { "No se pudo repetir el sonido del tap." }
        check(track.setPlaybackHeadPosition(0) == AudioTrack.SUCCESS) { "No se pudo posicionar el tap." }
        track.play()
    }
    override fun stop() = synchronized(lock) { stopLocked() }
    private fun stopLocked() {
        timeline?.let { lastFrames = it.playbackHeadPosition; it.release() }
        feedback?.release()
        timeline = null; feedback = null
    }
    private fun track(pcm: ShortArray): AudioTrack {
        val track = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(44100).setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build())
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
}

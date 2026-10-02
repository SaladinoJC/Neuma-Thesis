package ar.edu.unmdp.entrenamiento.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

interface NotePlayer {
    suspend fun play(midiNote: Int): Int
}

class AndroidNotePlayer : NotePlayer {
    private val mutex = Mutex()

    override suspend fun play(midiNote: Int): Int = withContext(Dispatchers.IO) {
        mutex.withLock {
            val sampleRate = 48_000
            val frames = sampleRate * 300 / 1000
            val frequency = 440.0 * 2.0.pow((midiNote - 69) / 12.0)
            val samples = ShortArray(frames) { index ->
                val attack = index.toDouble() / (sampleRate * 0.015)
                val release = (frames - 1 - index).toDouble() / (sampleRate * 0.025)
                val envelope = min(1.0, min(attack, release))
                (sin(2 * PI * frequency * index / sampleRate) * envelope * 0.25 * Short.MAX_VALUE).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAudioFormat(AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(samples.size * 2)
                .build()
            try {
                check(track.write(samples, 0, samples.size) == samples.size) { "No se pudo preparar la nota." }
                check(track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo iniciar el audio." }
                track.play()
                withTimeout(3000) {
                    while (track.playbackHeadPosition < frames) delay(10)
                }
                track.playbackHeadPosition
            } finally {
                track.release()
            }
        }
    }
}

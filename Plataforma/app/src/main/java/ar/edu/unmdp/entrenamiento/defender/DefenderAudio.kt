package ar.edu.unmdp.entrenamiento.defender

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import ar.edu.unmdp.entrenamiento.voice.referenceTone
import kotlinx.coroutines.*
import kotlin.math.*

enum class DefenderEffect { HIT, MISS, ARRIVAL }

interface DefenderAudio {
    suspend fun note(midi: Int): Int
    suspend fun effect(effect: DefenderEffect): Int
}

/** Generated PCM, no microphone or third-party assets. Tracks release on cancellation. */
class AndroidDefenderAudio : DefenderAudio {
    override suspend fun note(midi: Int): Int = play(referenceTone(midi))
    override suspend fun effect(effect: DefenderEffect): Int {
        val frequency = when (effect) { DefenderEffect.HIT -> 1800.0; DefenderEffect.MISS -> 180.0; DefenderEffect.ARRIVAL -> 90.0 }
        val frames = 4410
        val pcm = ShortArray(frames) { i ->
            val envelope = minOf(1.0, i / 220.0, (frames - 1 - i) / 440.0)
            (.10 * 32767 * envelope * sin(2 * PI * frequency * i / 44100)).roundToInt().toShort()
        }
        return play(pcm)
    }
    private suspend fun play(pcm: ShortArray): Int = withContext(Dispatchers.IO) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(44100).setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build())
            .setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(pcm.size * 2).build()
        try {
            check(track.write(pcm, 0, pcm.size) == pcm.size) { "No se pudo preparar el sonido de Defender." }
            check(track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo iniciar el audio de Defender." }
            track.play()
            withTimeout(3500) {
                while (track.playbackHeadPosition < pcm.size) { ensureActive(); delay(10) }
            }
            track.playbackHeadPosition
        } finally { track.release() }
    }
}

package ar.edu.unmdp.entrenamiento.voice

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*

class AndroidReferencePlayer : ReferencePlayer {
    override suspend fun play(midi: Int): Int = withContext(Dispatchers.IO) {
        val pcm = referenceTone(midi)
        val track = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            .setAudioFormat(AudioFormat.Builder().setSampleRate(44100).setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT).build())
            .setTransferMode(AudioTrack.MODE_STATIC).setBufferSizeInBytes(pcm.size*2).build()
        try {
            check(track.write(pcm, 0, pcm.size) == pcm.size) { "No se pudo preparar el objetivo sonoro." }
            check(track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo iniciar el objetivo sonoro." }
            track.play()
            withTimeout(3500) { while (track.playbackHeadPosition < pcm.size) { ensureActive(); delay(10) } }
            track.playbackHeadPosition
        } finally { track.release() }
    }
}

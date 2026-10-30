package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import ar.edu.unmdp.entrenamiento.tuner.*
import ar.edu.unmdp.entrenamiento.voice.AndroidReferencePlayer

class AndroidTunerReferencePlayer(context: Context) : TunerReferencePlayer {
    private val samples = AndroidSamplePlayer(context)
    private val pure = AndroidReferencePlayer()
    override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int {
        require(midi in tunerTargetRange) { "Referencia fuera de Do2–Mi4. No se cambió la octava." }
        return if (timbre == ReferenceTimbre.PURE) pure.play(midi)
            else samples.play(tunerReferenceAsset(timbre, midi))
    }
}

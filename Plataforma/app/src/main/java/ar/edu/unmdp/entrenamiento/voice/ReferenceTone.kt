package ar.edu.unmdp.entrenamiento.voice

import kotlin.math.*

interface ReferencePlayer {
    suspend fun play(midi: Int): Int
    fun close() {}
}
/** Reference PCM only, never treated as a microphone recording. */
fun referenceTone(midi: Int, sampleRate: Int = 44100): ShortArray {
    require(midi in 24..96 && sampleRate >= 8000)
    val frequency = 440*2.0.pow((midi-69)/12.0)
    val frames = sampleRate*600/1000
    val fade = sampleRate*20/1000
    return ShortArray(frames) { i ->
        val envelope = minOf(1.0, i.toDouble()/fade, (frames-1-i).toDouble()/fade)
        (.2*32767*envelope*sin(2*PI*frequency*i/sampleRate)).roundToInt().toShort()
    }
}

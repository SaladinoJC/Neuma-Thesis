package ar.edu.unmdp.entrenamiento.tuner

import kotlinx.serialization.Serializable

@Serializable enum class ReferenceTimbre(val label: String) { GUITAR("Guitarra"), PIANO("Piano"), PURE("Tono puro") }

interface TunerReferencePlayer {
    /** Devuelve frames reproducidos; debe liberar la salida también al fallar o cancelar. */
    suspend fun play(timbre: ReferenceTimbre, midi: Int): Int
}

fun tunerReferenceAsset(timbre: ReferenceTimbre, midi: Int): String {
    require(midi in tunerTargetRange) { "Referencia fuera de Do2–Mi4. No se cambió la octava." }
    require(timbre != ReferenceTimbre.PURE)
    return "tuner/${if (timbre == ReferenceTimbre.GUITAR) "guitar" else "piano"}_$midi.ogg"
}

/** Salida residual provisional: 600 ms con micrófono cerrado; pendiente de medición física. */
const val TUNER_REFERENCE_SETTLE_MS = 600L

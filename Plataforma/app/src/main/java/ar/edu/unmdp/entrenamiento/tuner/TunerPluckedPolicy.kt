package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.YinConfiguration
/** Valores experimentales para cuerdas pulsadas; no modifican el análisis de voz. */
object TunerPluckedPolicy {
    const val stableMillis = 400L
    const val version = "tuner-plucked-v2"
    val analysis = YinConfiguration(version = "yin-tuner-plucked-v1", minimumRms = .003)
}

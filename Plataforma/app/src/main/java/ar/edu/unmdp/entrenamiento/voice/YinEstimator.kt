package ar.edu.unmdp.entrenamiento.voice

import kotlinx.serialization.Serializable
import kotlin.math.*

@Serializable
data class YinConfiguration(val version: String = "yin-v1", val sampleRate: Int = 44100,
    val windowSize: Int = 4096, val hopSize: Int = 1024, val minHz: Double = 55.0,
    val maxHz: Double = 1000.0, val threshold: Double = .15, val minimumRms: Double = .01) {
    init {
        require(sampleRate > 0 && windowSize >= 4 && hopSize in 1..windowSize)
        require(minHz.isFinite() && maxHz.isFinite() && minHz > 0 && maxHz > minHz && maxHz < sampleRate/2.0)
        require(ceil(sampleRate/minHz).toInt()+1 <= windowSize/2 && sampleRate/maxHz >= 2)
        require(threshold > 0 && threshold < 1 && minimumRms.isFinite() && minimumRms > 0)
    }
}
@Serializable
data class PitchEstimate(val frequency: Double? = null, val periodicity: Double = 0.0,
    val rms: Double = 0.0, val reason: String? = null) {
    val valid: Boolean get() = frequency != null
    val midi: Int? get() = frequency?.let { (69 + 12 * log2(it / 440.0)).roundToInt() }
    fun cents(targetMidi: Int): Double? = frequency?.let { 1200 * log2(it / (440 * 2.0.pow((targetMidi-69)/12.0))) }
}

/** YIN difference + cumulative mean normalization + threshold and parabolic interpolation. */
class YinEstimator(val configuration: YinConfiguration = YinConfiguration()) {
    fun estimate(samples: FloatArray): PitchEstimate {
        val c = configuration
        if (samples.size != c.windowSize || samples.any { !it.isFinite() || abs(it) > 1f })
            return PitchEstimate(reason = "PCM inválido")
        val mean = samples.sumOf { it.toDouble() }/samples.size
        val rms = sqrt(samples.sumOf { (it-mean).pow(2) }/samples.size)
        if (rms < c.minimumRms) return PitchEstimate(rms = rms, reason = "Silencio o nivel insuficiente")
        val maxLag = ceil(c.sampleRate/c.minHz).toInt()
        val minLag = floor(c.sampleRate/c.maxHz).toInt().coerceAtLeast(2)
        val difference = DoubleArray(maxLag+2)
        var sum = 0.0
        difference[0] = 1.0
        for (lag in 1..maxLag+1) {
            var value = 0.0
            for (i in 0 until samples.size/2) {
                val delta = (samples[i]-samples[i+lag]).toDouble()
                value += delta*delta
            }
            sum += value
            difference[lag] = if (sum == 0.0) 1.0 else value*lag/sum
        }
        var lag = minLag
        while (lag <= maxLag && difference[lag] >= c.threshold) lag++
        if (lag > maxLag) return PitchEstimate(rms = rms, reason = "Sin periodicidad fiable")
        while (lag < maxLag && difference[lag+1] < difference[lag]) lag++
        val a = difference[lag-1]; val b = difference[lag]; val d = difference[lag+1]
        val denominator = a-2*b+d
        val refined = lag + if (abs(denominator) < 1e-12) 0.0 else (.5*(a-d)/denominator).coerceIn(-1.0, 1.0)
        val frequency = c.sampleRate/refined
        if (frequency !in c.minHz..c.maxHz) return PitchEstimate(rms = rms, reason = "Fuera del rango de prueba")
        return PitchEstimate(frequency, (1-b).coerceIn(0.0, 1.0), rms)
    }
}

package ar.edu.unmdp.entrenamiento.voice

import kotlin.math.*
import org.junit.Assert.*
import org.junit.Test

class YinTest {
    @Test fun `senales conocidas tienen menos de diez cents de error y silencio no inventa tono`() {
        val yin = YinEstimator()
        for (hz in listOf(110.0, 220.0, 440.0)) {
            val samples = FloatArray(4096) { (.4 * sin(2 * PI * hz * it / 44100)).toFloat() }
            val result = yin.estimate(samples)
            println("YIN synthetic reference=$hz estimated=${result.frequency} cents=${result.cents(if (hz == 110.0) 45 else if (hz == 220.0) 57 else 69)}")
            assertTrue("$hz: $result", result.valid)
            assertTrue(abs(1200 * ln(requireNotNull(result.frequency)/hz)/ln(2.0)) <= 10.0)
        }
        assertFalse(yin.estimate(FloatArray(4096)).valid)
    }
    @Test fun `PCM invalido continua y ruido no se interpretan como una nota sostenida`() {
        val yin = YinEstimator()
        assertFalse(yin.estimate(FloatArray(4096) { .5f }).valid)
        assertFalse(yin.estimate(FloatArray(4096) { Float.NaN }).valid)
        assertFalse(yin.estimate(FloatArray(12)).valid)
        val random = kotlin.random.Random(42)
        assertFalse(yin.estimate(FloatArray(4096) { random.nextFloat()-.5f }).valid)
        val a = yin.estimate(FloatArray(4096) { (.3*sin(2*PI*440*it/44100)).toFloat() })
        assertEquals(69, a.midi)
        assertEquals(0.0, requireNotNull(a.cents(69)), 1.0)
        assertEquals(1200.0, requireNotNull(a.cents(57)), 1.0)
    }
}

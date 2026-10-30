package ar.edu.unmdp.entrenamiento.voice

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*

interface VoiceCapture {
    suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit)
}
class AndroidVoiceCapture : VoiceCapture {
    @SuppressLint("MissingPermission")
    override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) = withContext(Dispatchers.IO) {
        val c = configuration
        val minimum = AudioRecord.getMinBufferSize(c.sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        check(minimum > 0) { "El dispositivo no admite la configuración de captura." }
        val recorder = AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, c.sampleRate,
            AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, maxOf(minimum, c.windowSize*4))
        try {
            check(recorder.state == AudioRecord.STATE_INITIALIZED) { "No se pudo abrir el micrófono." }
            check(recorder.sampleRate == c.sampleRate) { "Frecuencia de muestreo inesperada." }
            recorder.startRecording()
            check(recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) { "El micrófono no comenzó a capturar." }
            val estimator = YinEstimator(c)
            val window = FloatArray(c.windowSize)
            val block = ShortArray(c.hopSize)
            var filled = 0
            var blockFilled = 0
            while (currentCoroutineContext().isActive) {
                val count = recorder.read(block, blockFilled, block.size-blockFilled, AudioRecord.READ_NON_BLOCKING)
                check(count >= 0) { "Falló la lectura del micrófono ($count)." }
                if (count == 0) { delay(5); continue }
                blockFilled += count
                if (blockFilled < block.size) continue
                blockFilled = 0
                window.copyInto(window, 0, block.size, window.size)
                for (i in block.indices) window[window.size-block.size+i] = block[i]/32768f
                filled += block.size
                if (filled < window.size) continue
                val start = System.nanoTime()
                val estimate = estimator.estimate(window)
                onMeasurement(estimate, (System.nanoTime()-start)/1000, recorder.sampleRate)
            }
        } finally {
            try { if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) recorder.stop() }
            finally { recorder.release() }
        }
    }
}

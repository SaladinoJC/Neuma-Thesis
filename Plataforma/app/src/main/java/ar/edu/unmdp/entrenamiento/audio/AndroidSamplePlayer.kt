package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/** Assets mono/44,1 kHz: decode cacheado y AudioTrack liberado incluso al cancelar. */
internal class AndroidSamplePlayer(context: Context) {
    private val assets = context.applicationContext.assets
    private val mutex = Mutex()
    private val cache = mutableMapOf<String, PcmSample>()
    suspend fun play(asset: String, onStarted: () -> Unit = {}): Int {
        val callerContext = currentCoroutineContext().minusKey(Job)
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val sample = cache[asset] ?: withTimeout(5000) { decode(asset) }.also { cache[asset] = it }
                val track = AudioTrack.Builder()
                    .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                    .setAudioFormat(AudioFormat.Builder().setSampleRate(sample.sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(sample.bytes.size).build()
                try {
                    check(track.write(sample.bytes, 0, sample.bytes.size) == sample.bytes.size) { "No se pudo preparar la muestra." }
                    check(track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo iniciar el audio." }
                    track.play()
                    var started = false
                    withTimeout(4000) {
                        while (true) {
                            val frames = track.playbackHeadPosition
                            if (frames > 0 && !started) {
                                started = true
                                withContext(callerContext) { onStarted() }
                            }
                            if (frames >= sample.frames) break
                            delay(10)
                        }
                    }
                    track.playbackHeadPosition
                } finally {
                    // También se ejecuta al volver al menú o poner la app en segundo plano.
                    track.release()
                }
            }
        }
    }

    private suspend fun decode(asset: String): PcmSample {
        val extractor = MediaExtractor()
        try {
            assets.openFd(asset).use {
                extractor.setDataSource(it.fileDescriptor, it.startOffset, it.declaredLength)
            }
            check(extractor.trackCount == 1) { "Sample inválido." }
            extractor.selectTrack(0)
            val format = extractor.getTrackFormat(0)
            val codec = MediaCodec.createDecoderByType(checkNotNull(format.getString(MediaFormat.KEY_MIME)))
            try {
                format.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
                codec.configure(format, null, null, 0)
                codec.start()
                var rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                var inputEnded = false
                var outputEnded = false
                val output = ByteArrayOutputStream()
                val info = MediaCodec.BufferInfo()
                while (!outputEnded) {
                    currentCoroutineContext().ensureActive()
                    if (!inputEnded) {
                        val inputIndex = codec.dequeueInputBuffer(10_000)
                        if (inputIndex >= 0) {
                            val input = checkNotNull(codec.getInputBuffer(inputIndex))
                            input.clear()
                            val size = extractor.readSampleData(input, 0)
                            if (size < 0) {
                                codec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                inputEnded = true
                            } else {
                                codec.queueInputBuffer(inputIndex, 0, size, extractor.sampleTime, 0)
                                extractor.advance()
                            }
                        }
                    }
                    val outputIndex = codec.dequeueOutputBuffer(info, 10_000)
                    if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        val decoded = codec.outputFormat
                        rate = decoded.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                        check(decoded.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1) { "El banco debe ser mono." }
                        if (decoded.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
                            check(decoded.getInteger(MediaFormat.KEY_PCM_ENCODING) == AudioFormat.ENCODING_PCM_16BIT)
                        }
                    } else if (outputIndex >= 0) {
                        val buffer = checkNotNull(codec.getOutputBuffer(outputIndex))
                        if (info.size > 0) {
                            buffer.position(info.offset)
                            buffer.limit(info.offset + info.size)
                            val bytes = ByteArray(info.size)
                            buffer.get(bytes)
                            output.write(bytes)
                            check(output.size() <= 200_000) { "Sample demasiado largo." }
                        }
                        outputEnded = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        codec.releaseOutputBuffer(outputIndex, false)
                    }
                }
                check(rate == 44100 && output.size() > 0 && output.size() % 2 == 0) { "PCM inválido." }
                return PcmSample(output.toByteArray(), rate)
            } finally {
                codec.release()
            }
        } finally {
            extractor.release()
        }
    }

    private data class PcmSample(val bytes: ByteArray, val sampleRate: Int) {
        val frames: Int get() = bytes.size / 2
    }
}

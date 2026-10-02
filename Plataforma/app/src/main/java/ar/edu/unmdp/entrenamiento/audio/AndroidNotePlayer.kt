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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

interface NotePlayer {
    suspend fun play(midiNote: Int): Int
}

/** Samples mf locales, decodificados una vez por nota y reproducidos sin red. */
class AndroidNotePlayer(context: Context) : NotePlayer {
    private val assets = context.applicationContext.assets
    private val mutex = Mutex()
    private val cache = mutableMapOf<Int, PcmNote>()

    override suspend fun play(midiNote: Int): Int = withContext(Dispatchers.IO) {
        require(midiNote in 60..82) { "Nota fuera del banco de piano: $midiNote" }
        mutex.withLock {
            val note = cache[midiNote] ?: withTimeout(5000) { decode(midiNote) }.also { cache[midiNote] = it }
            val track = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAudioFormat(AudioFormat.Builder().setSampleRate(note.sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(note.bytes.size).build()
            try {
                check(track.write(note.bytes, 0, note.bytes.size) == note.bytes.size) { "No se pudo preparar la nota de piano." }
                check(track.state == AudioTrack.STATE_INITIALIZED) { "No se pudo iniciar el audio." }
                track.play()
                withTimeout(4000) {
                    while (track.playbackHeadPosition < note.frames) delay(10)
                }
                track.playbackHeadPosition
            } finally {
                // También se ejecuta al volver al menú o poner la app en segundo plano.
                track.release()
            }
        }
    }

    private suspend fun decode(midi: Int): PcmNote {
        val extractor = MediaExtractor()
        try {
            assets.openFd("piano/mf_$midi.ogg").use {
                extractor.setDataSource(it.fileDescriptor, it.startOffset, it.declaredLength)
            }
            check(extractor.trackCount == 1) { "Sample de piano inválido." }
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
                            check(output.size() <= 200_000) { "Sample de piano demasiado largo." }
                        }
                        outputEnded = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        codec.releaseOutputBuffer(outputIndex, false)
                    }
                }
                check(rate == 44100 && output.size() > 0 && output.size() % 2 == 0) { "PCM de piano inválido." }
                return PcmNote(output.toByteArray(), rate)
            } finally {
                codec.release()
            }
        } finally {
            extractor.release()
        }
    }

    private data class PcmNote(val bytes: ByteArray, val sampleRate: Int) {
        val frames: Int get() = bytes.size / 2
    }
}

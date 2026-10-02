package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.SoundPool
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CompletableDeferred
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
    suspend fun prepare(midiNotes: List<Int>) {}
    suspend fun play(midiNote: Int): Int
    /** Inicia el ataque sin esperar la cola de audio; devuelve el identificador de reproducción. */
    fun feedback(midiNote: Int): Int
    fun stopFeedback() {}
    fun close() {}
}

/** Samples mf locales, decodificados una vez por nota y reproducidos sin red. */
class AndroidNotePlayer(context: Context) : NotePlayer {
    private val assets = context.applicationContext.assets
    private val mutex = Mutex()
    private val cache = mutableMapOf<Int, PcmNote>()
    private val feedbackPool = SoundPool.Builder().setMaxStreams(16)
        .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build()
    private val soundIds = ConcurrentHashMap<Int, Int>()
    private val loading = mutableMapOf<Int, CompletableDeferred<Unit>>()
    private val loadLock = Any()
    // Acotado a las últimas 16 voces; SoundPool también limita y reemplaza las colas más viejas.
    private val feedbackStreams = ArrayDeque<Int>()

    init {
        feedbackPool.setOnLoadCompleteListener { _, id, status ->
            synchronized(loadLock) {
                loading.remove(id)?.let {
                    if (status == 0) it.complete(Unit)
                    else it.completeExceptionally(IllegalStateException("No se pudo cargar el piano ($status)."))
                }
            }
        }
    }

    override suspend fun prepare(midiNotes: List<Int>) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val pending = mutableListOf<Triple<Int, Int, CompletableDeferred<Unit>>>()
            try {
                for (midi in midiNotes.distinct().filterNot(soundIds::containsKey)) {
                    require(midi in 60..82)
                    val ready = CompletableDeferred<Unit>()
                    val id = synchronized(loadLock) {
                        assets.openFd("piano/mf_$midi.ogg").use {
                            feedbackPool.load(it, 1).also { sound ->
                                check(sound != 0) { "No se pudo preparar el piano." }
                                loading[sound] = ready
                            }
                        }
                    }
                    pending.add(Triple(midi, id, ready))
                }
                // Encolar todas las cargas primero evita doce esperas/decode duplicados en cromática.
                withTimeout(15_000) { pending.forEach { (_, _, ready) -> ready.await() } }
                pending.forEach { (midi, id, _) -> soundIds[midi] = id }
            } catch (error: Exception) {
                pending.forEach { (_, id, _) ->
                    synchronized(loadLock) { loading.remove(id) }
                    feedbackPool.unload(id)
                }
                throw error
            }
        }
    }

    override fun feedback(midiNote: Int): Int {
        val id = checkNotNull(soundIds[midiNote]) { "El piano todavía no está preparado." }
        val stream = feedbackPool.play(id, 0.8f, 0.8f, 1, 0, 1f)
        check(stream != 0) { "No se pudo tocar la nota de piano." }
        if (feedbackStreams.size == 16) feedbackStreams.removeFirst()
        feedbackStreams.addLast(stream)
        return stream
    }

    override fun stopFeedback() {
        feedbackStreams.forEach(feedbackPool::stop)
        feedbackStreams.clear()
    }

    override fun close() {
        stopFeedback()
        feedbackPool.release()
    }

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

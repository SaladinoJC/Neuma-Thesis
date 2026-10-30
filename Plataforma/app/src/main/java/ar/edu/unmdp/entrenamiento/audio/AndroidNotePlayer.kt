package ar.edu.unmdp.entrenamiento.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
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
    private val samples = AndroidSamplePlayer(context)
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

    override suspend fun play(midiNote: Int): Int {
        require(midiNote in 60..82) { "Nota fuera del banco de piano: $midiNote" }
        return samples.play("piano/mf_$midiNote.ogg")
    }
}

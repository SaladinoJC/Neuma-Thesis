package ar.edu.unmdp.entrenamiento.storage

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** Una transacción por perfil. Las llamadas anidadas son secuenciales, sin lanzar hijos. */
internal class ProfileFiles private constructor() {
    private val mutex = Mutex()
    val generation = AtomicLong()
    private class Transaction(val owner: ProfileFiles) : AbstractCoroutineContextElement(Key) {
        companion object Key : CoroutineContext.Key<Transaction>
    }
    suspend fun <T> transaction(action: suspend () -> T): T {
        if (currentCoroutineContext()[Transaction]?.owner === this) return action()
        return mutex.withLock { withContext(Transaction(this)) { action() } }
    }
    companion object {
        private val profiles = ConcurrentHashMap<String, ProfileFiles>()
        fun inDirectory(directory: File): ProfileFiles =
            profiles.computeIfAbsent(directory.canonicalPath) { ProfileFiles() }
    }
}

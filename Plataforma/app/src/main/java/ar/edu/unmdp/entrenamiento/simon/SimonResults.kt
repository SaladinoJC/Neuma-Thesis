package ar.edu.unmdp.entrenamiento.simon

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.AtomicMoveNotSupportedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface SimonResults {
    suspend fun all(): List<SimonResult>
    suspend fun save(result: SimonResult)
}

class MemorySimonResults : SimonResults {
    private val entries = mutableListOf<SimonResult>()
    override suspend fun all() = entries.toList()
    override suspend fun save(result: SimonResult) { entries.add(result) }
}

class JsonSimonResults(private val file: File) : SimonResults {
    private val mutex = Mutex()
    private val json = Json { encodeDefaults = true }

    override suspend fun all(): List<SimonResult> = withContext(Dispatchers.IO) {
        mutex.withLock { read().results }
    }

    override suspend fun save(result: SimonResult) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val previous = read()
            val document = previous.copy(results = previous.results + result)
            val parent = file.absoluteFile.parentFile!!
            check(parent.isDirectory || parent.mkdirs()) { "No se pudo crear el directorio de resultados." }
            val temporary = File.createTempFile("simon-", ".tmp", parent)
            try {
                temporary.writeText(json.encodeToString(document), Charsets.UTF_8)
                try {
                    Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                } catch (_: AtomicMoveNotSupportedException) {
                    Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            } finally {
                temporary.delete()
            }
            Unit
        }
    }

    private fun read(): ResultDocument {
        if (!file.exists()) return ResultDocument()
        return json.decodeFromString<ResultDocument>(file.readText(Charsets.UTF_8)).also {
            check(it.schemaVersion == 1) { "Versión de resultados no compatible." }
        }
    }
}

@Serializable
private data class ResultDocument(val schemaVersion: Int = 1, val results: List<SimonResult> = emptyList())

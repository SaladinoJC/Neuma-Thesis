package ar.edu.unmdp.entrenamiento.storage

import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** JSON versionado y sustitución atómica; conserva el esquema previo de Simón. */
internal class JsonResultsFile<T>(private val file: File, itemSerializer: KSerializer<T>) {
    private val profile = ProfileFiles.inDirectory(file.absoluteFile.parentFile!!)
    private val json = Json { encodeDefaults = true }
    private val serializer = ResultsDocument.serializer(itemSerializer)

    suspend fun all(): List<T> = withContext(Dispatchers.IO) { profile.transaction { read().results } }

    suspend fun append(result: T, isDuplicate: (T) -> Boolean = { false }) {
        val generation = profile.generation.get()
        withContext(Dispatchers.IO) { profile.transaction {
            check(profile.generation.get() == generation) { "El perfil se reinició durante el guardado." }
            val previous = read()
            if (previous.results.any(isDuplicate)) return@transaction
            val document = previous.copy(results = previous.results + result)
            val parent = file.absoluteFile.parentFile!!
            check(parent.isDirectory || parent.mkdirs()) { "No se pudo crear el directorio de resultados." }
            val temporary = File.createTempFile("results-", ".tmp", parent)
            try {
                temporary.writeText(json.encodeToString(serializer, document), Charsets.UTF_8)
                try {
                    Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                } catch (_: AtomicMoveNotSupportedException) {
                    Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
            } finally { temporary.delete() }
        } }
    }

    private fun read(): ResultsDocument<T> {
        if (!file.exists()) return ResultsDocument()
        return json.decodeFromString(serializer, file.readText(Charsets.UTF_8)).also {
            check(it.schemaVersion == 1) { "Versión de resultados no compatible." }
        }
    }
}

@Serializable
private data class ResultsDocument<T>(val schemaVersion: Int = 1, val results: List<T> = emptyList())

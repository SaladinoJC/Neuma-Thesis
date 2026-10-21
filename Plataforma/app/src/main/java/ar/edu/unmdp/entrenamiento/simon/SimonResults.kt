package ar.edu.unmdp.entrenamiento.simon

import java.io.File
import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile

interface SimonResults {
    suspend fun all(): List<SimonResult>
    suspend fun save(result: SimonResult)
}

class MemorySimonResults : SimonResults {
    private val entries = mutableListOf<SimonResult>()
    override suspend fun all() = entries.toList()
    override suspend fun save(result: SimonResult) { if (entries.none { it.id.isNotEmpty() && it.id == result.id }) entries.add(result) }
}

class JsonSimonResults(file: File) : SimonResults {
    private val store = JsonResultsFile(file, SimonResult.serializer())
    override suspend fun all(): List<SimonResult> = store.all()
    override suspend fun save(result: SimonResult) = store.append(result) { it.id.isNotEmpty() && it.id == result.id }
}

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
    override suspend fun save(result: SimonResult) { entries.add(result) }
}

class JsonSimonResults(file: File) : SimonResults {
    private val store = JsonResultsFile(file, SimonResult.serializer())
    override suspend fun all(): List<SimonResult> = store.all()
    override suspend fun save(result: SimonResult) = store.append(result)
}

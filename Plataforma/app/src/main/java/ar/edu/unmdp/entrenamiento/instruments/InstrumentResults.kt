package ar.edu.unmdp.entrenamiento.instruments

import java.io.File
import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile

interface InstrumentResults {
    suspend fun all(): List<InstrumentResult>
    suspend fun save(result: InstrumentResult)
}

class MemoryInstrumentResults : InstrumentResults {
    private val entries = mutableListOf<InstrumentResult>()
    override suspend fun all() = entries.toList()
    override suspend fun save(result: InstrumentResult) {
        if (entries.none { it.id == result.id }) entries.add(result)
    }
}

class JsonInstrumentResults(file: File) : InstrumentResults {
    private val store = JsonResultsFile(file, InstrumentResult.serializer())
    override suspend fun all(): List<InstrumentResult> = store.all()
    override suspend fun save(result: InstrumentResult) = store.append(result) { it.id == result.id }
}

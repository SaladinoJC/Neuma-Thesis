package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import java.io.File

class JsonTunerResults(file: File) : TunerResults {
    private val store = JsonResultsFile(file, TunerResult.serializer())
    override suspend fun all() = store.all()
    override suspend fun save(result: TunerResult) = store.append(result) { it.id == result.id }
}

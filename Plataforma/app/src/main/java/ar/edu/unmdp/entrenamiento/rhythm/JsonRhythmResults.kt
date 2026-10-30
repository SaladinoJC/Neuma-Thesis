package ar.edu.unmdp.entrenamiento.rhythm

import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import java.io.File

class JsonRhythmResults(file: File) : RhythmResults {
    private val store = JsonResultsFile(file, RhythmRecord.serializer())
    override suspend fun all() = store.all()
    override suspend fun save(record: RhythmRecord) { commit(record) }
    override suspend fun commit(record: RhythmRecord): RhythmRecord = store.upsertPrepared(record,
        sameIdentity = { it.id == record.id }, replace = { previous ->
            check(previous.revision != record.revision || previous.copy(dda = null) == record.copy(dda = null,
                result = record.result.copy(origin = previous.result.origin))) { "Identidad y revisión rítmica en conflicto." }
            record.revision > previous.revision
        }, prepare = ::prepareRhythmCommit)
}

package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RhythmRecord(val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val result: RhythmResult, val revision: Int = 1, val repeatedFromId: String? = null,
    val fingerprint: String = result.score.fingerprint, val fingerprintVersion: String = "rhythm-fingerprint-v1",
    val schemaVersion: Int = 1, val dda: RhythmProgressCommit? = null) {
    init {
        require(schemaVersion == 1 && fingerprintVersion == "rhythm-fingerprint-v1") { "Versión de historial rítmico no compatible." }
        require(id.isNotBlank() && revision > 0 && fingerprint == result.score.fingerprint) { "Identidad o huella rítmica inválida." }
    }
}

interface RhythmResults {
    suspend fun all(): List<RhythmRecord>
    suspend fun save(record: RhythmRecord)
    suspend fun commit(record: RhythmRecord): RhythmRecord {
        check(record.result.origin != "progression") { "El almacenamiento no admite progreso coordinado." }
        save(record)
        return record
    }
}

class MemoryRhythmResults : RhythmResults {
    private var records = emptyList<RhythmRecord>()
    override suspend fun all() = records.toList()
    override suspend fun save(record: RhythmRecord) { commit(record) }
    override suspend fun commit(record: RhythmRecord): RhythmRecord {
        val previous = records.firstOrNull { it.id == record.id }
        if (previous != null && previous.revision >= record.revision) {
            check(previous.revision != record.revision || previous.copy(dda = null) == record.copy(dda = null,
                result = record.result.copy(origin = previous.result.origin))) { "Identidad y revisión rítmica en conflicto." }
            return previous
        }
        val saved = prepareRhythmCommit(record, records)
        records = mergeRhythmRecords(records, listOf(saved))
        return saved
    }
}

/** Una revisión tardía reemplaza el mismo intento; nunca vuelve un ID a una revisión anterior. */
internal fun mergeRhythmRecords(first: List<RhythmRecord>, second: List<RhythmRecord>): List<RhythmRecord> =
    (first + second).groupBy { it.id }.values.map { revisions ->
        val latest = revisions.maxOf { it.revision }
        val candidates = revisions.filter { it.revision == latest }
        check(candidates.distinct().size == 1) { "Dos resultados distintos comparten identidad y revisión." }
        candidates.first()
    }

data class RhythmHistoryFilter(val category: RhythmCategory? = null, val beatsPerBar: Int? = null,
    val bpm: Int? = null, val origin: String? = null, val metronome: Boolean? = null)

data class RhythmHistoryState(val results: List<RhythmRecord> = emptyList(), val pending: List<RhythmRecord> = emptyList(),
    val error: String? = null, val filter: RhythmHistoryFilter = RhythmHistoryFilter()) {
    val progress: RhythmProgress get() = rhythmProgress(results)
    val entries: List<RhythmRecord> get() = results.filter { record ->
        val result = record.result
        (filter.category == null || filter.category in result.score.selected) &&
            (filter.beatsPerBar == null || filter.beatsPerBar == result.score.beatsPerBar) &&
            (filter.bpm == null || filter.bpm == result.score.bpm) &&
            (filter.origin == null || filter.origin == result.origin) &&
            (filter.metronome == null || filter.metronome == result.metronome)
    }.sortedWith(compareByDescending<RhythmRecord> { it.startedAtMillis }.thenByDescending { it.id })
}

class RhythmHistorySession(private val store: RhythmResults,
    private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() }) {
    var state = RhythmHistoryState()
        private set
    private var current: RhythmRecord? = null
    private var runId: String? = null
    private var started = 0L
    private var sourceId: String? = null
    private var epoch = 0L
    val currentId: String? get() = runId

    fun begin(repeatedFromId: String? = null) {
        runId = newId(); started = now(); sourceId = repeatedFromId; current = null
    }
    fun capture(result: RhythmResult): Boolean {
        val id = runId ?: return false
        if (current?.result == result) return false
        val record = RhythmRecord(id, started, current?.finishedAtMillis ?: now(), result,
            revision = (current?.revision ?: 0) + 1, repeatedFromId = sourceId)
        current = record
        state = state.copy(pending = mergeRhythmRecords(state.pending, listOf(record)))
        return true
    }
    fun filter(filter: RhythmHistoryFilter) { state = state.copy(filter = filter) }
    fun find(id: String): RhythmRecord? = mergeRhythmRecords(state.results, state.pending).firstOrNull { it.id == id }
    suspend fun load() {
        val token = epoch
        try {
            val records = store.all()
            if (token == epoch) state = state.copy(results = mergeRhythmRecords(records, state.results), error = null)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { if (token == epoch) state = state.copy(error = "No se pudo leer el historial de ritmo: ${error.message}") }
    }
    suspend fun savePending() {
        val token = epoch
        try {
            while (state.pending.isNotEmpty() && token == epoch) {
                val record = state.pending.first()
                val saved = store.commit(record)
                if (token != epoch) return
                // La captura puede haberse corregido mientras la escritura estaba suspendida.
                state = state.copy(results = mergeRhythmRecords(state.results, listOf(saved)),
                    pending = state.pending.filterNot { it.id == record.id && it.revision <= record.revision }, error = null)
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { if (token == epoch) state = state.copy(error = "No se pudo guardar el ejercicio: ${error.message}") }
    }
    fun clear() { epoch++; current = null; runId = null; sourceId = null; state = RhythmHistoryState() }
}

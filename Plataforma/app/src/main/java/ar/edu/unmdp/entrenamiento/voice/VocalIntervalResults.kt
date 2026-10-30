package ar.edu.unmdp.entrenamiento.voice

import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
data class VocalIntervalRecord(
    val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val referenceAnchorMidi: Int?, val referenceTargetMidi: Int?, val referenceListens: Int,
    val result: VocalIntervalResult, val actualSampleRate: Int?, val device: String,
    val androidVersion: String, val appVersion: String,
    val origin: String = "manual", val scaffoldingLevel: Int = 1,
    val analysisConfiguration: YinConfiguration = YinConfiguration(),
    val schemaVersion: Int = 1, val configurationVersion: String = "vocal-imitation-v3",
    val captureError: String? = null,
    val series: VocalSeriesStep? = null, val dda: VocalDdaCommit? = null,
) {
    init {
        require(schemaVersion == 1) { "Versión de resultado vocal no compatible." }
        require(id.isNotBlank())
        if (scaffoldingLevel == 3) require(referenceListens == 0 && referenceAnchorMidi == null && referenceTargetMidi == null)
        else {
            require(referenceListens >= 1 && referenceAnchorMidi != null && referenceTargetMidi != null)
            require(referenceTargetMidi - referenceAnchorMidi == result.targetSemitones)
        }
    }
}

interface VocalIntervalResults {
    suspend fun all(): List<VocalIntervalRecord>
    suspend fun save(record: VocalIntervalRecord)
}

class MemoryVocalIntervalResults : VocalIntervalResults {
    private val records = mutableListOf<VocalIntervalRecord>()
    override suspend fun all() = records.toList()
    override suspend fun save(record: VocalIntervalRecord) { if (records.none { it.id == record.id }) records += commitVocalRecord(record, records) }
}

class JsonVocalIntervalResults(file: File) : VocalIntervalResults {
    private val store = JsonResultsFile(file, VocalIntervalRecord.serializer())
    override suspend fun all() = store.all()
    override suspend fun save(record: VocalIntervalRecord) {
        store.upsertPrepared(record, { it.id == record.id }, { false }, ::commitVocalRecord)
    }
}

data class VocalIntervalHistoryState(val records: List<VocalIntervalRecord> = emptyList(),
    val pending: List<VocalIntervalRecord> = emptyList(), val error: String? = null) {
    val progress: VocalProgress get() = vocalProgress(records)
    val entries: List<VocalIntervalRecord> get() = (pending + records.filterNot { stored -> pending.any { it.id == stored.id } })
        .sortedByDescending { it.startedAtMillis }
}

/** Receives only terminal snapshots. All calls are serialized by the owning model. */
class VocalIntervalHistorySession(private val store: VocalIntervalResults,
    private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() }) {
    var state = VocalIntervalHistoryState()
        private set
    private data class Draft(val id: String, val started: Long, val anchor: Int?, val semitones: Int,
        val listens: Int, val device: String, val android: String, val version: String,
        val origin: String, val series: VocalSeriesStep?, val scaffoldingLevel: Int)
    private var current: Draft? = null

    fun begin(anchor: Int?, semitones: Int, listens: Int, device: Pair<String, String>, version: String,
        origin: String = "manual", series: VocalSeriesStep? = null, scaffoldingLevel: Int = 1): String {
        check(current == null && state.pending.isEmpty()) { "Hay un resultado vocal pendiente." }
        val draft = Draft(newId(), now(), anchor, semitones, listens, device.first, device.second, version, origin, series, scaffoldingLevel)
        current = draft
        return draft.id
    }

    fun finish(result: VocalIntervalResult, sampleRate: Int?, error: String?): Boolean {
        val draft = current ?: return false
        val record = VocalIntervalRecord(draft.id, draft.started, now(), draft.anchor,
            draft.anchor?.plus(draft.semitones), draft.listens, result, sampleRate, draft.device,
            draft.android, draft.version, origin = draft.origin, series = draft.series, captureError = error,
            scaffoldingLevel = draft.scaffoldingLevel,
            configurationVersion = if (draft.scaffoldingLevel == 3) {
                if (draft.origin == "dda") "vocal-free-production-dda-v1" else "vocal-free-production-v1"
            } else if (draft.scaffoldingLevel == 2) {
                if (draft.origin == "dda") "vocal-given-anchor-dda-v1" else "vocal-given-anchor-v1"
            } else if (draft.origin == "dda") "vocal-imitation-dda-v1" else "vocal-imitation-v3")
        current = null
        state = state.copy(pending = state.pending + record)
        return true
    }

    suspend fun savePending() {
        try {
            while (state.pending.isNotEmpty()) {
                val record = state.pending.first()
                store.save(record)
                state = state.copy(pending = state.pending.filterNot { it.id == record.id },
                    records = state.records.filterNot { it.id == record.id } + record, error = null)
            }
            load()
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { state = state.copy(error = "No se pudo guardar la ronda vocal: ${error.message}") }
    }

    suspend fun load() {
        try {
            val records = store.all()
            state = state.copy(records = records, error = null)
        }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { state = state.copy(error = "No se pudo leer el historial vocal: ${error.message}") }
    }

    fun clear() { current = null; state = VocalIntervalHistoryState() }
}

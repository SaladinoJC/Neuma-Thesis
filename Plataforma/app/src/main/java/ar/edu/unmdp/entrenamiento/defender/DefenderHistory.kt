package ar.edu.unmdp.entrenamiento.defender

import ar.edu.unmdp.entrenamiento.dda.FuzzyInference
import ar.edu.unmdp.entrenamiento.dda.MamdaniConfiguration
import ar.edu.unmdp.entrenamiento.dda.inferMamdani
import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID

@Serializable
data class DefenderStageProgress(val highStreak: Int = 0, val mastered: Boolean = false,
    val streakEvidence: List<String> = emptyList(), val masteryEvidence: List<String> = emptyList())

@Serializable
data class DefenderProgress(val variant: String = "notes", val version: String = "defender-progress-v1",
    val stages: Map<DefenderStage, DefenderStageProgress> = emptyMap(),
    val qualities: Map<DefenderChordQuality, DefenderStageProgress> = emptyMap(),
    val chordContents: Map<String, DefenderStageProgress> = emptyMap()) {
    init { require(version == "defender-progress-v1") }
    operator fun get(stage: DefenderStage) = stages[stage] ?: DefenderStageProgress()
    operator fun get(quality: DefenderChordQuality) = get(DefenderChordTarget(quality))
    operator fun get(target: DefenderChordTarget) = chordContents[target.key] ?:
        if (target.inversion == 0 && target.clefs == DefenderChordClefs.TREBLE) qualities[target.quality] ?: DefenderStageProgress()
        else DefenderStageProgress()
    val unlockedTargets: Set<DefenderChordTarget> get() = if (variant != "chords") emptySet() else
        defenderChordTargets().filter { target -> target.prerequisites.all { get(it).mastered } }.toSet()
    val unlocked: Set<DefenderStage> get() {
        if (variant != "notes") return emptySet()
        val available = linkedSetOf(DefenderStage.N1)
        for (stage in DefenderStage.entries.dropLast(1)) {
            if (!get(stage).mastered) break
            available += DefenderStage.entries[stage.ordinal + 1]
        }
        return available
    }
    val unlockedQualities: Set<DefenderChordQuality> get() {
        return unlockedTargets.filter { it.inversion == 0 && it.clefs == DefenderChordClefs.TREBLE }.map { it.quality }.toSet()
    }
}

@Serializable
data class DefenderContentEvidence(val clef: DefenderClef?, val resolved: Int, val eliminated: Int,
    val survival: Double, val precision: Double, val inference: FuzzyInference?, val mastery: String?,
    val quality: DefenderChordQuality? = null, val inversion: Int? = null)

@Serializable
enum class DefenderProgressReason(val label: String) {
    PRACTICE("Práctica: no modifica desbloqueos"), INTERRUPTED("Interrumpida: conserva la racha"),
    LOCKED("Contenido no habilitado para progresión"), INSUFFICIENT("Se requieren diez resueltos y tres por contenido objetivo"),
    INVALID_OUTPUT("Sin salida válida: conservar avance"), NOT_HIGH("Resultado no alto: reinicia la racha"),
    UNSEALED("Resultado provisional: el avance se confirma al cerrar el resumen"),
    FIRST_HIGH("Primera partida alta: falta otra completa elegible"), MASTERED("Dos partidas altas: dominio registrado")
}

@Serializable
data class DefenderProgressCommit(val sequence: Long, val evaluatedRevision: Int, val before: DefenderProgress,
    val after: DefenderProgress, val evidence: List<DefenderContentEvidence>, val reason: DefenderProgressReason,
    val configuration: MamdaniConfiguration, val newlyUnlocked: Set<DefenderStage> = emptySet(),
    val newlyUnlockedQualities: Set<DefenderChordQuality> = emptySet(),
    val newlyUnlockedTargets: Set<DefenderChordTarget> = emptySet())

@Serializable
data class DefenderRecord(val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val game: DefenderState, val revision: Int = 1, val variant: String = game.configuration.variant,
    val progress: DefenderProgressCommit? = null, val schemaVersion: Int = 1, val sealed: Boolean = true) {
    init {
        require(id.isNotBlank() && revision > 0 && schemaVersion == 1 && variant in setOf("notes", "chords"))
        require(variant == game.configuration.variant)
        require(game.phase in setOf(DefenderPhase.FINISHED, DefenderPhase.INTERRUPTED))
    }
}

fun defenderProgress(records: List<DefenderRecord>, variant: String = "notes"): DefenderProgress =
    records.filter { it.variant == variant }.mapNotNull { it.progress }.maxByOrNull { it.sequence }?.after ?: DefenderProgress(variant)

private fun defenderEvidence(game: DefenderState, clef: DefenderClef?, inference: MamdaniConfiguration,
    quality: DefenderChordQuality? = null, inversion: Int? = null): DefenderContentEvidence {
    val results = game.results.filter { (clef == null || it.enemy.note.clef == clef) &&
        (quality == null || it.enemy.chord?.quality == quality) && (inversion == null || it.enemy.chord?.inversion == inversion) }
    val eliminated = results.count { it.resolution == DefenderResolution.ELIMINATED }
    val hits = game.events.count { it.kind == "SHOT_HIT" }
    val misses = game.events.count { it.kind == "SHOT_MISS" }
    // Global failed shots have no selected target. Global precision gates every clef;
    // this never invents an attribution of a failed shot to a specific note or clef.
    val precision = if (hits + misses == 0) 0.0 else hits.toDouble() / (hits + misses)
    val survival = if (results.isEmpty()) 0.0 else eliminated.toDouble() / results.size
    val output = if (results.isNotEmpty()) inferMamdani(inference, mapOf("supervivencia" to survival, "precision" to precision)) else null
    val mastery = output?.centroid?.let { if (it >= .75) "alto" else if (it >= .40) "medio" else "bajo" }
    return DefenderContentEvidence(clef, results.size, eliminated, survival, precision, output, mastery, quality, inversion)
}

internal fun prepareDefenderRecord(record: DefenderRecord, stored: List<DefenderRecord>): DefenderRecord {
    val old = stored.firstOrNull { it.id == record.id }
    check(old == null || old.game.configuration == record.game.configuration) { "La revisión no puede cambiar la variante o configuración del intento." }
    val last = stored.mapNotNull { it.progress }.maxOfOrNull { it.sequence } ?: 0
    if (old?.progress != null && stored.any { it.variant == record.variant && (it.progress?.sequence ?: 0) > old.progress.sequence })
        return record.copy(progress = old.progress)
    val before = old?.progress?.before ?: defenderProgress(stored, record.variant)
    val game = record.game
    val configuration = old?.progress?.configuration ?: game.configuration.dda.masteryInference
    val clefs = when (game.configuration.stage) {
        DefenderStage.N1, DefenderStage.N2 -> listOf(DefenderClef.TREBLE)
        DefenderStage.N3 -> listOf(DefenderClef.BASS)
        DefenderStage.N4 -> DefenderClef.entries
    }
    val chords = record.variant == "chords"
    val quality = game.configuration.chordQuality
    val target = game.configuration.chordTarget
    val evidence = listOf(defenderEvidence(game, null, configuration)) + if (chords)
        target.clefs.values.map { defenderEvidence(game, it, configuration, quality, target.inversion) }
        else clefs.map { defenderEvidence(game, it, configuration) }
    val stage = game.configuration.stage
    val previous = if (chords) before[target] else before[stage]
    val reason = when {
        game.origin != "progression" -> DefenderProgressReason.PRACTICE
        !record.sealed -> DefenderProgressReason.UNSEALED
        game.phase != DefenderPhase.FINISHED -> DefenderProgressReason.INTERRUPTED
        (if (chords) target !in before.unlockedTargets else stage !in before.unlocked) -> DefenderProgressReason.LOCKED
        game.results.size < 10 || evidence.drop(1).any { it.resolved < 3 } -> DefenderProgressReason.INSUFFICIENT
        evidence.any { it.inference?.centroid == null } -> DefenderProgressReason.INVALID_OUTPUT
        evidence.any { it.mastery != "alto" } -> DefenderProgressReason.NOT_HIGH
        previous.highStreak == 0 -> DefenderProgressReason.FIRST_HIGH
        else -> DefenderProgressReason.MASTERED
    }
    val next = when (reason) {
        DefenderProgressReason.FIRST_HIGH -> previous.copy(highStreak = 1, streakEvidence = listOf(record.id))
        DefenderProgressReason.MASTERED -> previous.copy(highStreak = 0, streakEvidence = emptyList(), mastered = true,
            masteryEvidence = if (previous.mastered) previous.masteryEvidence else (previous.streakEvidence + record.id).takeLast(2))
        DefenderProgressReason.NOT_HIGH -> previous.copy(highStreak = 0, streakEvidence = emptyList())
        else -> previous
    }
    var after = if (chords) before.copy(chordContents = before.chordContents + (target.key to next),
        qualities = if (target.inversion == 0 && target.clefs == DefenderChordClefs.TREBLE) before.qualities + (quality to next) else before.qualities)
        else before.copy(stages = before.stages + (stage to next))
    fun preserve(current: DefenderStageProgress, earned: DefenderStageProgress) = current.copy(
        mastered = current.mastered || earned.mastered, masteryEvidence = if (earned.mastered) earned.masteryEvidence else current.masteryEvidence)
    old?.progress?.after?.let { acquired ->
        after = if (chords) after.copy(chordContents = defenderChordTargets().filter {
            it.key in after.chordContents || it.key in acquired.chordContents || acquired[it].mastered
        }.associate { it.key to preserve(after[it], acquired[it]) },
            qualities = DefenderChordQuality.entries.filter { it in after.qualities || it in acquired.qualities }
                .associateWith { preserve(after[it], acquired[it]) })
            else after.copy(stages = DefenderStage.entries.associateWith { preserve(after[it], acquired[it]) })
    }
    return record.copy(progress = DefenderProgressCommit(old?.progress?.sequence ?: last + 1, record.revision,
        before, after, evidence, reason, configuration, after.unlocked - before.unlocked,
        after.unlockedQualities - before.unlockedQualities, after.unlockedTargets - before.unlockedTargets))
}

interface DefenderResults {
    suspend fun all(): List<DefenderRecord>
    suspend fun commit(record: DefenderRecord): DefenderRecord
}

class MemoryDefenderResults : DefenderResults {
    private var records = emptyList<DefenderRecord>()
    override suspend fun all() = records.toList()
    override suspend fun commit(record: DefenderRecord): DefenderRecord {
        records.firstOrNull { it.id == record.id }?.let {
            check(it.revision != record.revision || it.copy(progress = null) == record.copy(progress = null)) { "Revisión de Defender en conflicto." }
            if (it.revision >= record.revision) return it
        }
        val saved = prepareDefenderRecord(record, records)
        records = mergeDefenderRecords(records, listOf(saved))
        return saved
    }
}

class JsonDefenderResults(file: File) : DefenderResults {
    private val store = JsonResultsFile(file, DefenderRecord.serializer())
    override suspend fun all() = store.all()
    override suspend fun commit(record: DefenderRecord): DefenderRecord = store.upsertPrepared(record,
        sameIdentity = { it.id == record.id }, replace = {
            check(it.revision != record.revision || it.copy(progress = null) == record.copy(progress = null)) { "Revisión de Defender en conflicto." }
            record.revision > it.revision
        }, prepare = ::prepareDefenderRecord)
}

internal fun mergeDefenderRecords(first: List<DefenderRecord>, second: List<DefenderRecord>): List<DefenderRecord> =
    (first + second).groupBy { it.id }.values.map { versions ->
        val latest = versions.maxOf { it.revision }
        val candidates = versions.filter { it.revision == latest }
        check(candidates.distinct().size == 1) { "Revisión de Defender en conflicto." }
        candidates.first()
    }

data class DefenderHistoryFilter(val stage: DefenderStage? = null, val origin: String? = null,
    val accidentals: Boolean? = null, val completed: Boolean? = null, val variant: String? = null,
    val quality: DefenderChordQuality? = null, val inversion: Int? = null, val clefs: DefenderChordClefs? = null)

data class DefenderHistoryState(val results: List<DefenderRecord> = emptyList(), val pending: List<DefenderRecord> = emptyList(),
    val error: String? = null, val filter: DefenderHistoryFilter = DefenderHistoryFilter()) {
    val progress: DefenderProgress get() = defenderProgress(results)
    fun progressFor(variant: String) = defenderProgress(results, variant)
    val entries: List<DefenderRecord> get() = results.filter { r ->
        (filter.variant == null || r.variant == filter.variant) &&
            (filter.stage == null || (r.variant == "notes" && r.game.configuration.stage == filter.stage)) &&
            (filter.quality == null || (r.variant == "chords" && r.game.configuration.chordQuality == filter.quality)) &&
            (filter.inversion == null || (r.variant == "chords" && r.game.configuration.chordInversion == filter.inversion)) &&
            (filter.clefs == null || (r.variant == "chords" && r.game.configuration.chordClefs == filter.clefs)) &&
            (filter.origin == null || r.game.origin == filter.origin) &&
            (filter.accidentals == null || (r.variant == "notes" && r.game.configuration.accidentals == filter.accidentals)) &&
            (filter.completed == null || (r.sealed && r.game.phase == DefenderPhase.FINISHED) == filter.completed)
    }.sortedWith(compareByDescending<DefenderRecord> { it.startedAtMillis }.thenByDescending { it.id })
}

class DefenderHistorySession(private val store: DefenderResults, private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() }) {
    var state = DefenderHistoryState()
        private set
    private var current: DefenderRecord? = null
    private var runId: String? = null
    private var started = 0L
    private var epoch = 0L
    val currentId: String? get() = runId
    fun begin() { runId = newId(); started = now(); current = null }
    fun capture(game: DefenderState, finalized: Boolean = true): Boolean {
        // A source-time correction can undo the third arrival and resume the live game.
        // Invalidate its provisional stored ending without awarding a completed session.
        val snapshot = if (game.phase == DefenderPhase.RUNNING && current?.game?.phase == DefenderPhase.FINISHED)
            game.copy(phase = DefenderPhase.INTERRUPTED, enemies = emptyList(), selected = null) else game
        if (snapshot.phase !in setOf(DefenderPhase.FINISHED, DefenderPhase.INTERRUPTED) ||
            (current?.game == snapshot && current?.sealed == finalized)) return false
        val id = runId ?: return false
        val record = DefenderRecord(id, started, now(), snapshot, (current?.revision ?: 0) + 1, sealed = finalized)
        current = record; state = state.copy(pending = mergeDefenderRecords(state.pending, listOf(record)))
        return true
    }
    fun filter(filter: DefenderHistoryFilter) { state = state.copy(filter = filter) }
    fun find(id: String) = mergeDefenderRecords(state.results, state.pending).firstOrNull { it.id == id }
    suspend fun load() {
        val token = epoch
        try { val records = store.all(); if (token == epoch) state = state.copy(results = mergeDefenderRecords(records, state.results), error = null) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { if (token == epoch) state = state.copy(error = "No se pudo leer Defender: ${error.message}") }
    }
    suspend fun savePending() {
        val token = epoch
        try {
            while (state.pending.isNotEmpty() && token == epoch) {
                val record = state.pending.first(); val saved = store.commit(record)
                if (token != epoch) return
                state = state.copy(results = mergeDefenderRecords(state.results, listOf(saved)),
                    pending = state.pending.filterNot { it.id == record.id && it.revision <= record.revision }, error = null)
            }
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { if (token == epoch) state = state.copy(error = "No se pudo guardar Defender: ${error.message}") }
    }
    fun clear() { epoch++; current = null; runId = null; state = DefenderHistoryState() }
}

package ar.edu.unmdp.entrenamiento.tuner

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable
import java.util.UUID
import kotlin.math.abs

@Serializable
enum class TunerStage(val label: String) {
    EAR_ATTEMPT("Intento de oído"), EAR_CORRECTION("Corrección con ayuda"),
    GUIDED_ADJUSTMENT("Ajuste guiado"), FINAL_REVIEW("Revisión final"), FINAL_CORRECTION("Corrección final")
}

@Serializable
data class TunerObservation(val stage: TunerStage, val number: Int, val targetMidi: Int,
    val cents: Double?, val passed: Boolean, val measuredMidi: Int? = null, val classification: EarClassification? = null)

@Serializable
data class TunerReferenceRequest(val timbre: ReferenceTimbre, val targetMidi: Int)

@Serializable
data class TunerResult(val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val setup: TunerSetupSnapshot, val mode: TunerMode, val referenceTimbre: ReferenceTimbre,
    val finalReview: Boolean, val classificationEnabled: Boolean, val completed: Boolean,
    val observations: List<TunerObservation> = emptyList(), val schemaVersion: Int = 1,
    val configurationVersion: String = "tuner-history-v1", val requestedReferences: List<TunerReferenceRequest> = emptyList(),
    val criterionMarginCents: Double = setup.criterion.margin, val stableRequiredMillis: Long = 1000,
    val maximumGapMillis: Long = 250, val earCheckVersion: String = "ear-check-v1",
    val analysisConfiguration: ar.edu.unmdp.entrenamiento.voice.YinConfiguration = ar.edu.unmdp.entrenamiento.voice.YinConfiguration()) {
    init { require(schemaVersion == 1 && setup.schemaVersion == 1) { "Versión de sesión del afinador no compatible." } }
}

interface TunerResults {
    suspend fun all(): List<TunerResult>
    suspend fun save(result: TunerResult)
}

class MemoryTunerResults : TunerResults {
    private val results = mutableListOf<TunerResult>()
    override suspend fun all() = results.toList()
    override suspend fun save(result: TunerResult) { if (results.none { it.id == result.id }) results += result }
}

data class TunerHistoryFilter(val guitarId: String? = null, val tuningKey: String? = null,
    val criterion: TunerCriterion? = null, val stringNumber: Int? = null,
    val mode: TunerMode? = null, val stage: TunerStage? = null)

/** Incluye altura y revisión: dos afinaciones editadas no son objetivos equivalentes. */
val GuitarTuning.historyKey get() = "$id:$revision:${notesSixToFirst.joinToString(",")}"

data class TunerHistoryEntry(val result: TunerResult, val observations: List<TunerObservation>) {
    val earProgress get() = earProgress(observations)
}
data class EarProgress(val validAttempts: Int, val correctAttempts: Int, val meanInitialAbsoluteCents: Double?,
    val classified: Int, val correctClassifications: Int)

private fun earProgress(observations: List<TunerObservation>): EarProgress {
    val initial = observations.filter { it.stage == TunerStage.EAR_ATTEMPT && it.cents != null }
    val classified = initial.mapNotNull { it.classification }
    return EarProgress(initial.size, initial.count { it.passed }, initial.takeIf { it.isNotEmpty() }?.map { abs(it.cents!!) }?.average(),
        classified.size, classified.count { it.correct })
}

data class TunerHistoryState(val results: List<TunerResult> = emptyList(),
    val pending: List<TunerResult> = emptyList(), val error: String? = null, val filter: TunerHistoryFilter = TunerHistoryFilter()) {
    val entries: List<TunerHistoryEntry> get() = results.asSequence().filter {
        (filter.guitarId == null || it.setup.guitar.id == filter.guitarId) &&
            (filter.tuningKey == null || it.setup.tuning.historyKey == filter.tuningKey) &&
            (filter.criterion == null || it.setup.criterion == filter.criterion) &&
            (filter.mode == null || it.mode == filter.mode)
    }.sortedByDescending { it.startedAtMillis }.map { result -> TunerHistoryEntry(result, result.observations.filter {
        (filter.stringNumber == null || it.number == filter.stringNumber) && (filter.stage == null || it.stage == filter.stage)
    }) }.filter { it.observations.isNotEmpty() || (filter.stringNumber == null && filter.stage == null) }.toList()
    val earProgress get() = earProgress(entries.flatMap { it.observations })
}

/** La sesión sella copias al terminar; nunca recibe PCM ni mediciones ocultas continuas. */
class TunerHistorySession(private val store: TunerResults,
    private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() }) {
    var state = TunerHistoryState()
        private set
    private var current: TunerResult? = null
    fun referenceRequested(timbre: ReferenceTimbre, targetMidi: Int) {
        current = current?.let { it.copy(requestedReferences = it.requestedReferences + TunerReferenceRequest(timbre, targetMidi)) }
    }
    fun filter(filter: TunerHistoryFilter) { state = state.copy(filter = filter) }
    fun begin(configuration: TunerConfiguration, stableRequiredMillis: Long = 1000,
        analysisConfiguration: ar.edu.unmdp.entrenamiento.voice.YinConfiguration = ar.edu.unmdp.entrenamiento.voice.YinConfiguration()) {
        current = if (!configuration.saveHistory || configuration.mode == TunerMode.FREE) null else
            TunerResult(newId(), now(), now(), configuration.snapshot(), configuration.mode,
                configuration.referenceTimbre, configuration.finalReview, configuration.earClassification, false,
                stableRequiredMillis = stableRequiredMillis, analysisConfiguration = analysisConfiguration,
                earCheckVersion = if (stableRequiredMillis <= 400) "ear-check-v2-plucked" else "ear-check-v1",
                configurationVersion = if (stableRequiredMillis == TunerPluckedPolicy.stableMillis) TunerPluckedPolicy.version else "tuner-history-v1")
    }
    fun finish(guided: GuidedTunerState, ear: EarTunerState): Boolean {
        val result = current ?: return false
        val complete = if (result.mode == TunerMode.GUIDED) guided.phase == GuidedPhase.COMPLETE else ear.phase == EarPhase.COMPLETE
        val interrupted = if (result.mode == TunerMode.GUIDED) guided.phase == GuidedPhase.INTERRUPTED else ear.phase == EarPhase.INTERRUPTED
        if (!complete && !interrupted) return false
        current = null
        val observations = buildList {
            fun addGuided(results: List<GuidedStringResult>, stage: TunerStage) {
                addAll(results.map { TunerObservation(stage, it.number, it.midi, it.cents, it.passed) })
            }
            if (result.mode == TunerMode.GUIDED) {
                addGuided(guided.firstPass, TunerStage.GUIDED_ADJUSTMENT)
                addGuided(guided.review, TunerStage.FINAL_REVIEW)
                addGuided(guided.corrections, TunerStage.FINAL_CORRECTION)
            } else {
                ear.results.forEach {
                    add(TunerObservation(TunerStage.EAR_ATTEMPT, it.number, it.targetMidi, it.initialCents,
                        it.initialPassed, it.measuredMidi, it.classification))
                    if (it.corrected) add(TunerObservation(TunerStage.EAR_CORRECTION, it.number, it.targetMidi, it.correctedCents, true))
                }
                addGuided(ear.review.review, TunerStage.FINAL_REVIEW)
                addGuided(ear.review.corrections, TunerStage.FINAL_CORRECTION)
            }
        }
        state = state.copy(pending = state.pending + result.copy(finishedAtMillis = now(), completed = complete, observations = observations))
        return true
    }
    suspend fun savePending() {
        try {
            while (state.pending.isNotEmpty()) {
                val result = state.pending.first()
                store.save(result)
                state = state.copy(pending = state.pending.filterNot { it.id == result.id }, error = null)
            }
            load()
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { state = state.copy(error = "No se pudo guardar el seguimiento: ${error.message}") }
    }
    suspend fun load() {
        try {
            val results = store.all()
            // Guardados/filtros pueden cambiar mientras el almacenamiento está suspendido.
            state = state.copy(results = results, error = null)
        }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { state = state.copy(error = "No se pudo leer el seguimiento: ${error.message}") }
    }
}

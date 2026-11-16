package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.PitchEstimate
import kotlin.math.abs
import kotlinx.serialization.Serializable

enum class EarPhase { READY, HIDDEN, CHECKING, CORRECTING, REVIEW, COMPLETE, INTERRUPTED }
@Serializable enum class EarDirection(val label: String) { LOW("Grave"), HIGH("Agudo"), IN_TUNE("Afinado") }
@Serializable
data class EarClassification(val answer: EarDirection, val actual: EarDirection, val cents: Double, val atMillis: Long) {
    val correct get() = answer == actual
}
data class EarStringResult(val number: Int, val targetMidi: Int, val measuredMidi: Int,
    val initialCents: Double, val initialPassed: Boolean, val atMillis: Long,
    val classification: EarClassification?, val corrected: Boolean = false, val correctedCents: Double? = null)
data class EarTunerState(
    val phase: EarPhase = EarPhase.READY, val setup: TunerSetupSnapshot? = null,
    val finalReview: Boolean = true, val classificationEnabled: Boolean = false,
    val classification: EarClassification? = null, val results: List<EarStringResult> = emptyList(),
    val classificationSubmitted: Boolean = false,
    val review: GuidedTunerState = GuidedTunerState(), val measurement: TunerState = TunerState(),
    val farWarning: Boolean = false, val message: String? = null,
    val configurationVersion: String = "ear-check-v1",
) {
    val active get() = phase in setOf(EarPhase.HIDDEN, EarPhase.CHECKING, EarPhase.CORRECTING, EarPhase.REVIEW)
    val hidden get() = phase == EarPhase.HIDDEN || phase == EarPhase.CHECKING
}

/** ear-check-v1: ventana >=400 ms, amplitud <=5 cents y huecos <=250 ms.
 * Parámetros provisionales de estabilidad de evaluación, independientes del margen de aprobación.
 * El estado público elimina toda medición oculta, incluidas pistas de estabilidad.
 */
class EarTunerSession(private val stableRequiredMillis: Long = 1000) {
    private val meter = TunerSession(stableRequiredMillis)
    private var review = GuidedTunerSession(stableRequiredMillis)
    var state = EarTunerState()
        private set
    private var since: Long? = null
    private var lastTime: Long? = null
    private var minimum = 0.0
    private var maximum = 0.0
    private var stable = false
    private var farSince: Long? = null
    private var warning = false
    fun begin(snapshot: TunerSetupSnapshot, finalReview: Boolean, classification: Boolean) {
        this.classification = null
        meter.configure(snapshot.copy(tracking = TunerTracking.STRINGS))
        review = GuidedTunerSession(stableRequiredMillis)
        state = EarTunerState(phase = EarPhase.HIDDEN, setup = meter.setup,
            finalReview = finalReview, classificationEnabled = classification,
            configurationVersion = if (stableRequiredMillis <= 400) "ear-check-v2-plucked" else "ear-check-v1")
        target(6)
    }
    private fun resetEvidence() { since = null; lastTime = null; stable = false; farSince = null; warning = false }
    private fun target(number: Int) {
        meter.fixString(number); meter.start(); resetEvidence()
        state = state.copy(phase = EarPhase.HIDDEN, classification = null, classificationSubmitted = false, message = null)
        publish()
    }
    private fun publish() {
        val raw = if (state.phase == EarPhase.REVIEW || (state.phase == EarPhase.COMPLETE && state.finalReview)) review.state.measurement else meter.state
        state = state.copy(measurement = if (state.hidden) raw.copy(measuredMidi = null, frequency = null,
            cents = null, inTune = false, stableMillis = 0, lastConfirmation = null) else raw,
            farWarning = if (state.phase == EarPhase.REVIEW) review.state.farWarning else warning,
            review = review.state)
    }
    private fun direction(cents: Double) = when {
        abs(cents) <= meter.state.criterion.margin + 1e-9 -> EarDirection.IN_TUNE
        cents < 0 -> EarDirection.LOW
        else -> EarDirection.HIGH
    }
    fun classify(answer: EarDirection, timeMillis: Long) {
        if (state.phase != EarPhase.HIDDEN || !state.classificationEnabled || state.classification != null) return
        val fresh = lastTime?.let { timeMillis >= it && timeMillis - it <= 250 } == true
        val cents = meter.state.cents
        if (!stable || !fresh || cents == null) {
            state = state.copy(message = "Tocá nuevamente una sola cuerda antes de clasificar."); return
        }
        // La clasificación queda sellada; no se publica hasta revelar el intento de ajuste.
        classification = EarClassification(answer, direction(cents), cents, timeMillis)
        meter.start(); resetEvidence()
        state = state.copy(classification = null, classificationSubmitted = true, message = "Clasificación recibida. Ajustá de oído y comprobá.")
        publish()
    }
    private var classification: EarClassification? = null
    fun check() {
        if (state.phase != EarPhase.HIDDEN) return
        if (state.classificationEnabled && classification == null) {
            state = state.copy(message = "Clasificá la cuerda antes de ajustar y comprobar."); return
        }
        meter.start(); resetEvidence()
        state = state.copy(phase = EarPhase.CHECKING, message = "Tocá nuevamente la cuerda para comprobar.")
        publish()
    }
    fun measure(estimate: PitchEstimate, timeMillis: Long) {
        if (!state.active || !state.measurement.running) return
        if (state.phase == EarPhase.REVIEW) {
            review.measure(estimate, timeMillis)
            if (!review.state.active) state = state.copy(phase = EarPhase.COMPLETE)
            publish(); return
        }
        // Un callback histórico no debe retroceder el reloj ni modificar el intento.
        if (lastTime != null && timeMillis <= requireNotNull(lastTime)) return
        meter.measure(estimate, timeMillis)
        val cents = meter.state.cents
        val continuous = lastTime?.let { timeMillis - it <= 250 } == true
        if (!continuous) { since = null; farSince = null; warning = false }
        if (cents == null) { since = null; stable = false; farSince = null; warning = false }
        else {
            if (since == null || maxOf(maximum, cents) - minOf(minimum, cents) > 5.0) {
                since = timeMillis; minimum = cents; maximum = cents
            } else { minimum = minOf(minimum, cents); maximum = maxOf(maximum, cents) }
            stable = timeMillis - requireNotNull(since) >= 400
            if (abs(cents) <= 500) { farSince = null; warning = false }
            else if (abs(cents) >= 700) {
                if (farSince == null) farSince = timeMillis
                if (timeMillis - requireNotNull(farSince) >= 400) warning = true
            } else farSince = null
        }
        lastTime = timeMillis
        if (state.phase == EarPhase.CHECKING && stable && cents != null) {
            val result = EarStringResult(requireNotNull(meter.state.fixedString), requireNotNull(meter.state.targetMidi),
                requireNotNull(meter.state.measuredMidi), cents, direction(cents) == EarDirection.IN_TUNE,
                timeMillis, classification)
            if (result.initialPassed && meter.state.inTune) {
                state = state.copy(results = state.results + result, classification = classification, message = null)
                nextString(); publish(); return
            }
            // La corrección requiere evidencia nueva; no reutiliza el tiempo de evaluación.
            meter.start(); resetEvidence()
            state = state.copy(phase = EarPhase.CORRECTING, results = state.results + result,
                classification = classification, message = null)
        } else if (state.phase == EarPhase.CORRECTING && meter.state.inTune) {
            state = state.copy(results = state.results.dropLast(1) + state.results.last().copy(corrected = true, correctedCents = meter.state.cents))
            nextString()
        }
        if (state.phase == EarPhase.CHECKING && cents == null) state = state.copy(message = "Sin lectura válida. Tocá nuevamente la cuerda.")
        publish()
    }
    private fun nextString() {
        val number = requireNotNull(meter.state.fixedString)
        if (number > 1) { classification = null; target(number - 1); return }
        meter.stop(); resetEvidence()
        if (state.finalReview) {
            review.beginReview(requireNotNull(state.setup))
            state = state.copy(phase = EarPhase.REVIEW, message = null)
        } else state = state.copy(phase = EarPhase.COMPLETE)
    }
    fun leavePending() { if (state.phase == EarPhase.REVIEW) { review.leavePending(); publish() } }
    fun suspendMeasurement() {
        if (!state.active) return
        meter.stop(); review.suspendMeasurement(); resetEvidence(); publish()
    }
    fun resumeMeasurement() {
        if (!state.active) return
        if (state.phase == EarPhase.REVIEW) review.resumeMeasurement() else meter.start()
        resetEvidence(); publish()
    }
    fun interrupt() {
        if (!state.active) return
        meter.stop(); review.interrupt(); resetEvidence()
        state = state.copy(phase = EarPhase.INTERRUPTED, message = "Recorrido interrumpido. Iniciar comienza uno nuevo.")
        publish()
    }
}

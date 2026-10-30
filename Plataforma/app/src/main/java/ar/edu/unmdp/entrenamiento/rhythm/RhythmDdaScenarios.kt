package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.serialization.Serializable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

enum class RhythmDdaScenario(val label: String) {
    SUCCESS("Dos ejercicios altos"), ERRORS("Alto seguido de omisiones"), MIX("Mezcla con categoría más lenta"),
    INSUFFICIENT("Una sola aparición objetivo"), CEILING("Avance hasta 120 BPM"),
    REPEATED("Misma partitura con otra semilla"), FAILURE("Fallo y reintento de guardado")
}

@Serializable
data class RhythmDdaSimulation(val scenario: String, val records: List<RhythmRecord>,
    val progress: RhythmProgress, val notes: List<String>, val origin: String = "simulated")

/** Exercises go through the public game session. This store is private to the simulation. */
suspend fun simulateRhythmDda(scenario: RhythmDdaScenario): RhythmDdaSimulation {
    val store = MemoryRhythmResults()
    var serial = 0
    var seed = 100L
    val notes = mutableListOf("Datos sintéticos: no son desempeño de un estudiante ni escriben en el perfil.")
    suspend fun next(categories: Set<RhythmCategory> = setOf(RhythmCategory.QUARTER),
        perfect: Boolean = true, insufficient: Boolean = false): RhythmRecord {
        val progress = rhythmProgress(store.all())
        var score: RhythmScore
        var candidates = 0
        do {
            currentCoroutineContext().ensureActive()
            check(candidates++ < 500) { "No se encontró una partitura nueva para el escenario." }
            val session = RhythmSession().apply { generate(categories, seed++) }
            score = requireNotNull(session.state.score).copy(bpm = progress.tempo(categories))
        } while (store.all().any { it.fingerprint == score.fingerprint })
        if (insufficient) {
            var seen = false
            score = score.copy(bars = score.bars.map { bar -> bar.copy(occurrences = bar.occurrences.map { cell ->
                val keep = cell.target && !seen
                if (cell.target) seen = true
                cell.copy(target = keep)
            }) })
        }
        val session = RhythmSession().apply { preview(score, origin = "progression"); play(0, metronome = serial % 2 == 0) }
        if (perfect) session.state.attacks.forEach { session.tap(session.executionStartNanos!! + it.offsetNanos) }
        session.advance(session.executionStartNanos!! + Beat(16).nanos(score.bpm) + 1)
        serial++
        return RhythmRecord("simulated-$serial", serial.toLong(), serial.toLong(), session.state.result!!)
    }
    when (scenario) {
        RhythmDdaScenario.SUCCESS -> { store.commit(next()); store.commit(next()) }
        RhythmDdaScenario.ERRORS -> { store.commit(next()); store.commit(next(perfect = false)) }
        RhythmDdaScenario.MIX -> {
            store.commit(next()); store.commit(next())
            store.commit(next(setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS)))
        }
        RhythmDdaScenario.INSUFFICIENT -> store.commit(next(insufficient = true))
        RhythmDdaScenario.CEILING -> repeat(26) { store.commit(next()) }
        RhythmDdaScenario.REPEATED -> {
            val first = next(); store.commit(first)
            store.commit(first.copy(id = "simulated-repeat", result = first.result.copy(score = first.result.score.copy(seed = -1))))
        }
        RhythmDdaScenario.FAILURE -> {
            var fail = true
            val failing = object : RhythmResults {
                override suspend fun all() = store.all()
                override suspend fun save(record: RhythmRecord) { commit(record) }
                override suspend fun commit(record: RhythmRecord): RhythmRecord {
                    if (fail) throw java.io.IOException("Fallo simulado antes de escribir")
                    return store.commit(record)
                }
            }
            val history = RhythmHistorySession(failing, now = { 1 }, newId = { "simulated-retry" })
            history.begin(); history.capture(next().result); history.savePending()
            check(store.all().isEmpty() && history.state.progress == RhythmProgress())
            notes += "Primer guardado fallido: un pendiente en memoria, cero cambios de progreso."
            fail = false; history.savePending(); history.savePending()
            notes += "Reintento aplicado una sola vez con la misma identidad."
        }
    }
    return RhythmDdaSimulation(scenario.label, store.all(), rhythmProgress(store.all()), notes)
}

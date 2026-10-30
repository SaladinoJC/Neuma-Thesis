package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.Serializable

enum class DefenderScenario(val label: String) {
    FAST("Aciertos rápidos: acelerar hasta 6 s"), SLOW("Aciertos lentos: alargar hasta 14 s"),
    STEADY("Aciertos a mitad de viaje: mantener"), NO_SHOTS("Tres llegadas sin intentos"),
    UNLOCK("Dos partidas altas de N1"), MIXED("Ruta N1–N4 y evidencia por clave"),
    PRACTICE("Práctica manual sin aplicar DDA"), RETRY("Fallo y reintento idempotente"),
    CHORD_ROUTE("Catálogo completo: séptimas, inversiones y claves")
}

@Serializable
data class DefenderSimulation(val scenario: String, val records: List<DefenderRecord>,
    val progress: DefenderProgress, val notes: List<String>, val origin: String = "simulated", val variant: String = "notes")

suspend fun simulateDefender(scenario: DefenderScenario, variant: String = "notes"): DefenderSimulation {
    require(variant in setOf("notes", "chords"))
    val store = MemoryDefenderResults()
    var serial = 0
    val notes = mutableListOf("Datos sintéticos, sin audio ni cambios en el perfil real.")
    suspend fun game(stage: DefenderStage = DefenderStage.N1, hits: Int = 24,
        reaction: Double = .01, origin: String = "progression", travel: Long = 10_000,
        quality: DefenderChordQuality = DefenderChordQuality.MAJOR, inversion: Int = 0,
        clefs: DefenderChordClefs = DefenderChordClefs.TREBLE): DefenderRecord {
        var now = 0L
        val session = DefenderSession { now }
        session.start(42 + serial, DefenderConfiguration(stage = stage, maxEnemies = 1, travelMillis = travel,
            variant = variant, chordQuality = quality, chordInversion = inversion, chordClefs = clefs), origin)
        var count = 0
        repeat(20_000) {
            currentCoroutineContext().ensureActive()
            if (session.state.phase == DefenderPhase.FINISHED) {
                serial++
                return DefenderRecord("simulated-$serial", serial.toLong(), serial.toLong(), session.state)
            }
            val enemy = session.state.enemies.firstOrNull()
            if (count < hits && enemy != null) {
                now = maxOf(now + 1, enemy.appearedAtMillis + (enemy.travelMillis * reaction).toLong())
                session.select(enemy.note.name); session.accidental(enemy.note.accidental)
                enemy.chord?.let { session.quality(it.quality) }
                session.fire(); count++
            } else { now += 100; session.tick() }
        }
        error("Presupuesto de simulación agotado.")
    }
    when (scenario) {
        DefenderScenario.FAST -> store.commit(game())
        DefenderScenario.SLOW -> store.commit(game(reaction = .90))
        DefenderScenario.STEADY -> {
            store.commit(game(reaction = .50))
            notes += "Los aciertos a mitad de viaje mantienen; las pérdidas usadas para cerrar pueden proponer alargar."
        }
        DefenderScenario.NO_SHOTS -> store.commit(game(hits = 0))
        DefenderScenario.UNLOCK -> { store.commit(game()); store.commit(game()) }
        DefenderScenario.MIXED -> {
            if (variant == "chords") {
                for (quality in DefenderChordQuality.entries.take(4)) { store.commit(game(quality = quality)); store.commit(game(quality = quality)) }
            } else {
                for (stage in listOf(DefenderStage.N1, DefenderStage.N2, DefenderStage.N3)) {
                    store.commit(game(stage)); store.commit(game(stage))
                }
                store.commit(game(DefenderStage.N4, hits = 60))
            }
        }
        DefenderScenario.CHORD_ROUTE -> {
            if (variant == "notes") {
                store.commit(game()); notes += "El catálogo de acordes corresponde a la variante por nombre; aquí solo se simula N1."
            } else {
                repeat(defenderChordTargets().size) {
                    currentCoroutineContext().ensureActive()
                    val progress = defenderProgress(store.all(), variant)
                    val target = progress.unlockedTargets.firstOrNull { !progress[it].mastered }
                        ?: error("No hay siguiente objetivo alcanzable")
                    repeat(2) { store.commit(game(quality = target.quality, inversion = target.inversion, clefs = target.clefs)) }
                }
                notes += "Ruta sintética de 84 combinaciones y 168 partidas; no es un recorrido de estudiantes."
            }
        }
        DefenderScenario.PRACTICE -> store.commit(game(origin = "practice", travel = 12_000))
        DefenderScenario.RETRY -> {
            var failed = true
            val backing = object : DefenderResults {
                override suspend fun all() = store.all()
                override suspend fun commit(record: DefenderRecord): DefenderRecord {
                    if (failed) error("Fallo sintético antes de escribir")
                    return store.commit(record)
                }
            }
            val history = DefenderHistorySession(backing, now = { 1 }, newId = { "simulated-retry" })
            history.begin(); history.capture(game().game); history.savePending()
            check(store.all().isEmpty() && history.state.pending.size == 1)
            notes += "Guardado fallido: un pendiente, ningún cambio de progreso."
            failed = false; history.savePending(); history.savePending()
            notes += "Reintento confirmado una sola vez."
        }
    }
    val label = if (variant == "chords" && scenario == DefenderScenario.MIXED) "Ruta de cuatro tríadas" else scenario.label
    return DefenderSimulation(label, store.all(), defenderProgress(store.all(), variant), notes, variant = variant)
}

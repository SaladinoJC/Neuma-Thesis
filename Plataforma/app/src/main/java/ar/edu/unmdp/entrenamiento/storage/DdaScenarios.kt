package ar.edu.unmdp.entrenamiento.storage

import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.instruments.*
import ar.edu.unmdp.entrenamiento.progress.*
import kotlinx.coroutines.yield
import kotlinx.serialization.Serializable

@Serializable
enum class ProgressScenario(val label: String) {
    TWO_HIGH("Dos altas consecutivas"), RECOVERY("Alta, baja, alta, alta"), HELPED("Aciertos con ayuda"),
    ERRORS("Errores sostenidos"), ALTERNATING("Alternancia de resultados"), INTERRUPTED("Interrupción entre altas"),
    MANUAL_BETWEEN("Práctica manual entre altas"), LAST_LEVEL("Recorrido hasta último nivel"), DUPLICATE("Resultado repetido sin doble avance")
}
@Serializable
data class ProgressStep(val game: String, val resultId: String, val level: Int, val effectiveMode: String,
    val completed: Boolean, val high: Boolean, val before: Progress, val after: Progress,
    val reason: String, val duplicateAttempted: Boolean = false, val origin: String = "simulated")
@Serializable
data class ScenarioCheck(val label: String, val passed: Boolean, val expected: String, val observed: String)
@Serializable
data class BoundaryMastery(val game: String, val level: Int, val description: String, val mastery: Mastery)
@Serializable
data class DdaBenchReport(val simulations: List<ProgressSimulation>, val checks: List<ScenarioCheck>,
    val boundaries: List<BoundaryMastery>, val origin: String = "simulated", val version: String = "dda-bench-v1") {
    fun markdown(): String = buildString {
        appendLine("# Banco DDA · Datos simulados")
        appendLine("Configuración: $version. Sin audio, historial ni progreso reales.")
        appendLine(); appendLine("| Comprobación | Esperado | Observado | Resultado |")
        appendLine("| --- | --- | --- | --- |")
        checks.forEach { appendLine("| ${it.label} | ${it.expected} | ${it.observed} | ${if (it.passed) "Cumple" else "FALLA"} |") }
        appendLine(); appendLine("## Fronteras de dominio · Parámetros sin calibrar")
        appendLine("| Juego/nivel | Caso | Centroide | Alto |")
        appendLine("| --- | --- | --- | --- |")
        boundaries.forEach { appendLine("| ${it.game}/${it.level+1} | ${it.description} | ${it.mastery.inference.centroid} | ${it.mastery.high} |") }
        appendLine("Las fronteras registran el comportamiento actual; no equivalen a dominio musical validado.")
    }
}
private data class Script(val high: Boolean = true, val mode: String = "automatic", val interrupted: Boolean = false,
    val helped: Boolean = false, val alternating: Boolean = false)
private class SilentPlayer : InstrumentPlayer {
    var target = Instrument.PIANO
    override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
        target = instrument; onStarted(); return 0
    }
}
private suspend fun completeSimon(session: SimonSession, length: Int, interrupted: Boolean = false) {
    repeat(length) {
        yield(); session.playbackFinished(); repeat(session.state.sequence.size) { session.answer(0) }
    }
    if (interrupted) session.interrupt() else { session.playbackFinished(); session.answer(1) }
}
private fun step(game: String, id: String, level: Int, mode: String, completed: Boolean, high: Boolean,
    before: Progress, after: Progress, duplicate: Boolean): ProgressStep {
    val reason = when {
        mode != "automatic" -> "Práctica manual: excluida de progresión"
        !completed -> "Interrumpida: conserva racha y nivel"
        after.level > before.level -> "Siguiente nivel disponible en próxima partida"
        after.level == 2 -> "Último nivel; no hay avance adicional"
        high -> "Dominio alto: acumula racha"
        else -> "Dominio no alto: reinicia racha"
    }
    return ProgressStep(game, id, level, mode, completed, high, before, after, reason, duplicate)
}

suspend fun simulateProgress(scenario: ProgressScenario): ProgressSimulation {
    val simonResults = MemorySimonResults()
    val instrumentResults = MemoryInstrumentResults()
    val simon = SimonSession({ 0 }, simonResults, clock = { 0L })
    val player = SilentPlayer()
    val instruments = InstrumentSession({ 0 }, player, instrumentResults, clock = { 0L })
    val scripts = when (scenario) {
        ProgressScenario.TWO_HIGH, ProgressScenario.DUPLICATE -> listOf(Script(), Script())
        ProgressScenario.RECOVERY -> listOf(Script(), Script(false), Script(), Script())
        ProgressScenario.HELPED -> List(2) { Script(helped = true) }
        ProgressScenario.ERRORS -> List(2) { Script(false) }
        ProgressScenario.ALTERNATING -> List(4) { Script(high = it%2 == 0, alternating = true) }
        ProgressScenario.INTERRUPTED -> listOf(Script(), Script(interrupted = true), Script())
        ProgressScenario.MANUAL_BETWEEN -> listOf(Script(), Script(false, mode = "manual"), Script())
        ProgressScenario.LAST_LEVEL -> List(6) { Script() }
    }
    val steps = mutableListOf<ProgressStep>()
    val duplicate = scenario == ProgressScenario.DUPLICATE
    for (script in scripts) {
        yield()
        val beforeSimon = simon.progress()
        if (script.mode == "manual" || script.helped) simon.start(Scale.CHROMATIC, 60) else simon.startAutomatic(60)
        completeSimon(simon, if (script.interrupted) 3 else if (script.high) 9 else 0, script.interrupted)
        val sr = checkNotNull(simon.state.result)
        if (duplicate) simonResults.save(sr)
        steps.add(step("simon", sr.id, sr.scale.ordinal, sr.origin, sr.completed, sr.mastery?.high == true,
            beforeSimon, simon.progress(), duplicate))
        val beforeInstruments = instruments.progress()
        if (script.mode == "manual") instruments.start(InstrumentLevel.C) else instruments.startAutomatic()
        repeat(if (script.interrupted) 3 else 10) { round ->
            instruments.listen()
            if (script.helped) repeat(instruments.state.listensRemaining) { instruments.listen() }
            val correct = if (script.alternating) round%2 == 0 else script.high
            instruments.answer(if (correct) player.target else instruments.state.options.first { it != player.target })
            instruments.nextQuestion()
        }
        if (script.interrupted) instruments.interrupt()
        val ir = checkNotNull(instruments.state.result)
        if (duplicate) instrumentResults.save(ir)
        steps.add(step("instruments", ir.id, ir.level.ordinal, ir.origin, ir.completed, ir.mastery?.high == true,
            beforeInstruments, instruments.progress(), duplicate))
    }
    return ProgressSimulation(scenario = scenario.label,
        simon = simon.history().map { it.copy(origin = "simulated") }, instruments = instruments.history().map { it.copy(origin = "simulated") },
        simonProgress = simon.progress(), instrumentProgress = instruments.progress(), scenarioId = scenario.name, steps = steps)
}

suspend fun runDdaBench(): DdaBenchReport {
    val simulations = ProgressScenario.entries.map { simulateProgress(it) }
    val checks = mutableListOf<ScenarioCheck>()
    val expected = mapOf(ProgressScenario.TWO_HIGH to Progress(1), ProgressScenario.RECOVERY to Progress(1),
        ProgressScenario.HELPED to Progress(), ProgressScenario.ERRORS to Progress(), ProgressScenario.ALTERNATING to Progress(),
        ProgressScenario.INTERRUPTED to Progress(1), ProgressScenario.MANUAL_BETWEEN to Progress(1),
        ProgressScenario.LAST_LEVEL to Progress(2, 2), ProgressScenario.DUPLICATE to Progress(1))
    simulations.forEach { simulation ->
        val goal = expected.getValue(ProgressScenario.valueOf(simulation.scenarioId))
        for ((game, progress) in listOf("Simón" to simulation.simonProgress, "Instrumentos" to simulation.instrumentProgress))
            checks.add(ScenarioCheck("${simulation.scenario} · $game", progress == goal, goal.toString(), progress.toString()))
    }
    val excluded = simulations.flatMap { it.steps }.filter { !it.completed || it.effectiveMode != "automatic" }
    checks.add(ScenarioCheck("Manuales/interrupciones aislados", excluded.all { it.before == it.after }, "Sin cambios", "${excluded.size} pasos excluidos"))
    val steps = simulations.flatMap { it.steps }
    checks.add(ScenarioCheck("Límites de nivel/racha", steps.all { it.after.level in 0..2 && it.after.streak in 0..2 }, "0..2", "${steps.size} pasos"))
    val player = SilentPlayer()
    val invalid = InstrumentSession({ 0 }, player, clock = { 0L }, ddaConfiguration = InstrumentDdaConfiguration(defaultHelpInference().copy(rules = emptyList())))
    invalid.startAutomatic()
    repeat(10) { invalid.listen(); invalid.answer(player.target); invalid.nextQuestion() }
    val decisions = invalid.state.answers.mapNotNull { it.ddaDecision }.drop(2)
    val safe = decisions.all { it.inference?.centroid == null && it.appliedDelta == 0 && it.listensAfter == 3 }
    checks.add(ScenarioCheck("Ayuda sin salida válida", safe, "Mantener 3 escuchas", "${decisions.size} decisiones, sin reducción=$safe"))
    val boundaries = mutableListOf<BoundaryMastery>()
    for (scale in Scale.entries) for (length in 0..listOf(5, 7, 9)[scale.ordinal]+1) {
        val session = SimonSession({ 0 }, clock = { 0L }); session.start(scale, 60); completeSimon(session, length)
        boundaries.add(BoundaryMastery("simon", scale.ordinal, "$length notas completadas", checkNotNull(session.state.result?.mastery)))
    }
    for (correct in 0..10) for (autonomous in 0..correct) {
        yield()
        val p = SilentPlayer(); val session = InstrumentSession({ 0 }, p, clock = { 0L }); session.start(InstrumentLevel.A)
        repeat(10) { round ->
            session.listen()
            if (round in autonomous until correct) repeat(2) { session.listen() }
            session.answer(if (round < correct) p.target else session.state.options.first { it != p.target }); session.nextQuestion()
        }
        boundaries.add(BoundaryMastery("instruments", 0, "$correct aciertos; $autonomous autónomos", checkNotNull(session.state.result?.mastery)))
    }
    return DdaBenchReport(simulations, checks, boundaries)
}

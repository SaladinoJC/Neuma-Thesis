package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.coroutines.yield

enum class InstrumentDdaScenario(val label: String) {
    AUTONOMOUS("Aciertos sin reescuchas"), RECOVERY("Aciertos y luego errores"),
    HELPED("Aciertos con reescuchas"), ALTERNATING("Aciertos y errores alternados"),
}

/** Usa la misma sesión con audio simulado y almacenamiento exclusivamente en memoria. */
internal suspend fun simulateInstrumentDda(scenario: InstrumentDdaScenario, level: InstrumentLevel,
    configuration: InstrumentDdaConfiguration): InstrumentResult {
    val player = object : InstrumentPlayer {
        override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
            onStarted()
            return 0 // No son frames observados en hardware.
        }
    }
    val session = InstrumentSession({ 0 }, player, clock = { 0L }, ddaConfiguration = configuration)
    session.start(level, InstrumentMode.SIMULATED)
    repeat(10) { index ->
        yield()
        val correct = when (scenario) {
            InstrumentDdaScenario.AUTONOMOUS, InstrumentDdaScenario.HELPED -> true
            InstrumentDdaScenario.RECOVERY -> index < 3
            InstrumentDdaScenario.ALTERNATING -> index % 2 == 0
        }
        repeat(if (scenario == InstrumentDdaScenario.HELPED) session.state.listensAllowed else 1) { session.listen() }
        session.answer(level.instruments[if (correct) 0 else 1])
        session.nextQuestion()
    }
    return checkNotNull(session.state.result)
}

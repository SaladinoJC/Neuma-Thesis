package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.serialization.Serializable
import java.util.UUID
import ar.edu.unmdp.entrenamiento.progress.*

@Serializable
enum class Instrument(val label: String, val assetKey: String) {
    PIANO("Piano", "piano"), VIOLIN("Violín", "violin"), FLUTE("Flauta", "flute"),
    MARIMBA("Marimba", "marimba"), CLARINET("Clarinete", "clarinet"), TRUMPET("Trompeta", "trumpet"),
    TROMBONE("Trombón", "trombone"), OBOE("Oboe", "oboe"), BASSOON("Fagot", "bassoon");
    val asset: String get() = "instruments/${assetKey}_c4_mf.ogg"
    fun melodyAsset(midi: Int): String = "instruments/melody/${assetKey}_${midi}_mf.ogg"
}

@Serializable
enum class InstrumentLevel(val label: String, val description: String, val instruments: List<Instrument>) {
    A("Nivel A", "Familias distintas", listOf(Instrument.PIANO, Instrument.VIOLIN, Instrument.FLUTE, Instrument.MARIMBA)),
    B("Nivel B", "Vientos: madera y metal", listOf(Instrument.FLUTE, Instrument.CLARINET, Instrument.TRUMPET, Instrument.TROMBONE)),
    C("Nivel C", "Vientos de madera", listOf(Instrument.FLUTE, Instrument.CLARINET, Instrument.OBOE, Instrument.BASSOON)),
}

enum class InstrumentPhase { IDLE, READY, PLAYBACK, RESPONSE, FEEDBACK, FINISHED, INTERRUPTED }

enum class InstrumentMode { AUTOMATIC, MANUAL, SIMULATED }

interface InstrumentPlayer {
    /** onStarted solo se invoca una vez, al observar avance real del audio. */
    suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int
    fun close() {}
}

@Serializable
data class InstrumentAnswer(
    val round: Int, val target: Instrument, val options: List<Instrument>, val selected: Instrument,
    val correct: Boolean, val listensUsed: Int, val answeredAtMillis: Long,
    val listensAllowed: Int = 3,
    val melody: InstrumentMelody? = null,
    val ddaDecision: InstrumentDdaDecision? = null,
)

@Serializable
data class InstrumentResult(
    val id: String, val level: InstrumentLevel, val answers: List<InstrumentAnswer>,
    val completed: Boolean, val startedAtMillis: Long, val finishedAtMillis: Long,
    val origin: String = "manual", val configurationVersion: String = "instruments-0.3-three-listens",
    val bankVersion: String = "iowa-c4-mf-v1", val midi: Int? = 60,
    val plannedQuestions: Int = 10,
    val stimulusKind: String = "single-note",
    val ddaConfiguration: InstrumentDdaConfiguration? = null,
    val mastery: Mastery? = null,
)

data class InstrumentState(
    val phase: InstrumentPhase = InstrumentPhase.IDLE,
    val level: InstrumentLevel = InstrumentLevel.A,
    val round: Int = 1,
    val options: List<Instrument> = emptyList(),
    val listensUsed: Int = 0,
    val answers: List<InstrumentAnswer> = emptyList(),
    val result: InstrumentResult? = null,
    val listensAllowed: Int = 3,
    val mode: InstrumentMode = InstrumentMode.MANUAL,
) {
    val listensRemaining: Int get() = listensAllowed - listensUsed
    val correctCount: Int get() = answers.count { it.correct }
}

/** Reglas independientes de Android. El objetivo solo se expone después de responder. */
class InstrumentSession(
    private val randomIndex: (Int) -> Int,
    private val player: InstrumentPlayer,
    private val results: InstrumentResults = MemoryInstrumentResults(),
    private val clock: () -> Long = System::currentTimeMillis,
    val ddaConfiguration: InstrumentDdaConfiguration = InstrumentDdaConfiguration(),
) {
    var state = InstrumentState()
        private set
    private var target = Instrument.PIANO
    private var melody: InstrumentMelody? = null
    private var id = ""
    private var startedAtMillis = 0L
    private var lastChangeRound: Int? = null
    private var nextListensAllowed = 3

    suspend fun start(level: InstrumentLevel, mode: InstrumentMode = InstrumentMode.MANUAL) {
        check(state.phase in listOf(InstrumentPhase.IDLE, InstrumentPhase.FINISHED, InstrumentPhase.INTERRUPTED)) {
            "La partida anterior sigue activa."
        }
        id = UUID.randomUUID().toString()
        startedAtMillis = clock()
        state = InstrumentState(level = if (mode == InstrumentMode.AUTOMATIC) InstrumentLevel.entries[progress().level] else level, mode = mode)
        melody = null
        lastChangeRound = null
        nextListensAllowed = 3
        prepareQuestion()
    }

    suspend fun progress(): Progress = progression(results.all().map {
        ProgressEntry(it.id, it.level.ordinal, it.origin, it.completed, it.mastery)
    })

    suspend fun startAutomatic() {
        start(InstrumentLevel.A, InstrumentMode.AUTOMATIC)
    }

    private fun prepareQuestion() {
        target = state.level.instruments[randomIndex(4)]
        melody = generateInstrumentMelody(randomIndex, melody)
        val options = state.level.instruments.toMutableList()
        for (index in options.lastIndex downTo 1) {
            val other = randomIndex(index + 1)
            val previous = options[index]
            options[index] = options[other]
            options[other] = previous
        }
        state = state.copy(phase = InstrumentPhase.READY, options = options, listensUsed = 0)
    }

    suspend fun listen(onStarted: () -> Unit = {}) {
        if (state.phase !in listOf(InstrumentPhase.READY, InstrumentPhase.RESPONSE) || state.listensRemaining == 0) return
        state = state.copy(phase = InstrumentPhase.PLAYBACK)
        var started = false
        try {
            player.play(target, checkNotNull(melody)) {
                if (!started) {
                    started = true
                    state = state.copy(listensUsed = state.listensUsed + 1)
                    onStarted()
                }
            }
            check(started) { "El audio no llegó a reproducirse." }
        } finally {
            state = state.copy(phase = if (state.listensUsed > 0) InstrumentPhase.RESPONSE else InstrumentPhase.READY)
        }
    }

    suspend fun answer(selected: Instrument) {
        if (state.phase != InstrumentPhase.RESPONSE || selected !in state.options) return
        val answer = InstrumentAnswer(state.round, target, state.options, selected, selected == target,
            state.listensUsed, clock(), listensAllowed = state.listensAllowed, melody = checkNotNull(melody))
        val decision = decideInstrumentHelp(state.answers + answer, state.listensAllowed, lastChangeRound, state.mode, ddaConfiguration)
        val answers = state.answers + answer.copy(ddaDecision = decision)
        nextListensAllowed = decision.listensAfter
        if (decision.appliedDelta != 0) lastChangeRound = state.round
        if (answers.size == 10) {
            val result = makeResult(answers, completed = true)
            results.save(result)
            state = state.copy(phase = InstrumentPhase.FINISHED, answers = answers, result = result)
        } else state = state.copy(phase = InstrumentPhase.FEEDBACK, answers = answers)
    }

    fun nextQuestion() {
        if (state.phase != InstrumentPhase.FEEDBACK) return
        state = state.copy(round = state.round + 1, listensAllowed = nextListensAllowed)
        prepareQuestion()
    }

    /** Solo después de una confirmación de reinicio de perfil; no guarda una interrupción. */
    fun discardForReset() { state = InstrumentState() }
    suspend fun history(): List<InstrumentResult> = results.all()

    suspend fun interrupt() {
        if (state.phase in listOf(InstrumentPhase.IDLE, InstrumentPhase.FINISHED, InstrumentPhase.INTERRUPTED)) return
        val result = makeResult(state.answers, completed = false)
        results.save(result)
        state = state.copy(phase = InstrumentPhase.INTERRUPTED, result = result)
    }

    private fun makeResult(answers: List<InstrumentAnswer>, completed: Boolean) = InstrumentResult(
        id, state.level, answers, completed, startedAtMillis, clock(),
        origin = state.mode.name.lowercase(java.util.Locale.ROOT),
        configurationVersion = "instruments-0.5-progression", bankVersion = "iowa-pentatonic-mf-v2",
        midi = null, stimulusKind = "generated-melody",
        ddaConfiguration = ddaConfiguration,
        mastery = if (completed) mastery(mapOf("exactitud" to answers.count { it.correct }.toDouble()/answers.size,
            "autonomía" to if (answers.none { it.correct }) 0.0 else
                answers.count { it.correct && it.listensUsed == 1 }.toDouble()/answers.count { it.correct })) else null,
    )
}

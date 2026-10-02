package ar.edu.unmdp.entrenamiento.simon

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class Scale(val label: String, val intervals: List<Int>) {
    PENTATONIC("Pentatónica", listOf(0, 2, 4, 7, 9)),
    DIATONIC("Diatónica mayor", listOf(0, 2, 4, 5, 7, 9, 11)),
    CHROMATIC("Cromática", (0..11).toList()),
}
enum class Phase { IDLE, PLAYBACK, RESPONSE, FINISHED, INTERRUPTED }
@Serializable
data class SimonResult(
    val completedLength: Int,
    val attemptedSequence: List<Int>,
    val errorPosition: Int?,
    val completed: Boolean = true,
    val id: String = "",
    val scale: Scale = Scale.PENTATONIC,
    val tonicMidi: Int = 60,
    val startedAtMillis: Long = 0,
    val finishedAtMillis: Long = 0,
    val origin: String = "manual",
    val configurationVersion: String = "simon-0.1",
)
data class SimonState(
    val phase: Phase = Phase.IDLE,
    val sequence: List<Int> = emptyList(),
    val responseIndex: Int = 0,
    val completedLength: Int = 0,
    val result: SimonResult? = null,
    val scale: Scale = Scale.PENTATONIC,
    val tonicMidi: Int = 60,
) {
    val midiNotes: List<Int> get() = scale.intervals.map { tonicMidi + it }
}

class SimonSession(
    private val nextNote: (Int) -> Int,
    private val results: SimonResults = MemorySimonResults(),
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private var id = ""
    private var startedAtMillis = 0L
    var state = SimonState()
        private set

    fun start(scale: Scale, tonicMidi: Int): SimonState {
        require(tonicMidi in 48..84)
        check(state.phase !in listOf(Phase.PLAYBACK, Phase.RESPONSE)) { "La partida anterior sigue activa." }
        id = UUID.randomUUID().toString()
        startedAtMillis = clock()
        state = SimonState(
            phase = Phase.PLAYBACK,
            sequence = listOf(nextNote(scale.intervals.size)),
            scale = scale,
            tonicMidi = tonicMidi,
        )
        return state
    }

    fun playbackFinished(): SimonState {
        if (state.phase == Phase.PLAYBACK) state = state.copy(phase = Phase.RESPONSE)
        return state
    }

    suspend fun answer(button: Int): SimonState {
        if (state.phase != Phase.RESPONSE) return state
        if (button == state.sequence[state.responseIndex]) {
            val responseIndex = state.responseIndex + 1
            state = if (responseIndex == state.sequence.size) {
                state.copy(
                    phase = Phase.PLAYBACK,
                    sequence = state.sequence + nextNote(state.scale.intervals.size),
                    responseIndex = 0,
                    completedLength = state.sequence.size,
                )
            } else {
                state.copy(responseIndex = responseIndex)
            }
        } else {
            val result = makeResult(completed = true, errorPosition = state.responseIndex)
            results.save(result)
            state = state.copy(
                phase = Phase.FINISHED,
                result = result,
            )
        }
        return state
    }

    suspend fun history(): List<SimonResult> = results.all()
    suspend fun interrupt(): SimonState {
        if (state.phase !in listOf(Phase.PLAYBACK, Phase.RESPONSE)) return state
        val result = makeResult(completed = false, errorPosition = null)
        results.save(result)
        state = state.copy(phase = Phase.INTERRUPTED, result = result)
        return state
    }

    private fun makeResult(completed: Boolean, errorPosition: Int?) = SimonResult(
        completedLength = state.completedLength,
        attemptedSequence = state.sequence,
        errorPosition = errorPosition,
        completed = completed,
        id = id,
        scale = state.scale,
        tonicMidi = state.tonicMidi,
        startedAtMillis = startedAtMillis,
        finishedAtMillis = clock(),
    )
}

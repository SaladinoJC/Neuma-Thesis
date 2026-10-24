package ar.edu.unmdp.entrenamiento.defender

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
enum class DefenderName(val solfege: String, val letter: String, val semitone: Int) {
    DO("Do", "C", 0), RE("Re", "D", 2), MI("Mi", "E", 4), FA("Fa", "F", 5),
    SOL("Sol", "G", 7), LA("La", "A", 9), SI("Si", "B", 11)
}
@Serializable
data class DefenderNote(val name: DefenderName, val octave: Int,
    val accidental: DefenderAccidental = DefenderAccidental.NATURAL,
    val clef: DefenderClef = DefenderClef.TREBLE) {
    val midi: Int get() = (octave + 1) * 12 + name.semitone + accidental.semitones
    val staffStep: Int get() = octave * 7 + name.ordinal - clef.bottomDiatonic
}
@Serializable data class DefenderPoint(val x: Float, val y: Float)
@Serializable
data class DefenderEnemy(val id: Int, val note: DefenderNote, val appearedAtMillis: Long,
    val travelMillis: Long, val entry: DefenderPoint, val destination: DefenderPoint = DefenderPoint(.5f, .82f)) {
    val deadlineMillis: Long get() = appearedAtMillis + travelMillis
    fun position(activeMillis: Long): DefenderPoint {
        val fraction = ((activeMillis - appearedAtMillis).toDouble() / travelMillis).coerceIn(0.0, 1.0).toFloat()
        return DefenderPoint(entry.x + (destination.x - entry.x) * fraction, entry.y + (destination.y - entry.y) * fraction)
    }
}
@Serializable enum class DefenderPhase { SETUP, RUNNING, PAUSED, FINISHED, INTERRUPTED }
@Serializable enum class DefenderResolution { ELIMINATED, ARRIVED }
@Serializable
data class DefenderConfiguration(val version: String = "defender-d2-v1", val travelMillis: Long = 10_000,
    val noteSound: Boolean = true, val effectsSound: Boolean = true, val letterNames: Boolean = false,
    val stage: DefenderStage = DefenderStage.N1, val accidentals: Boolean = false, val maxEnemies: Int = 1) {
    init { require(travelMillis == 10_000L); require(maxEnemies in 1..4) }
}
@Serializable
data class DefenderResult(val enemy: DefenderEnemy, val resolution: DefenderResolution,
    val atMillis: Long, val position: DefenderPoint)
@Serializable
data class DefenderEvent(val kind: String, val atMillis: Long, val enemy: DefenderEnemy? = null,
    val selection: DefenderName? = null, val accidental: DefenderAccidental = DefenderAccidental.NATURAL)
@Serializable
data class DefenderState(val phase: DefenderPhase = DefenderPhase.SETUP, val lives: Int = 3,
    val points: Int = 0, val selected: DefenderName? = null, val enemies: List<DefenderEnemy> = emptyList(),
    val selectedAccidental: DefenderAccidental = DefenderAccidental.NATURAL,
    val activeMillis: Long = 0, val failedShots: Int = 0, val feedback: String? = null,
    val seed: Int = 0, val configuration: DefenderConfiguration = DefenderConfiguration(),
    val origin: String = "practice", val results: List<DefenderResult> = emptyList(),
    val events: List<DefenderEvent> = emptyList()) {
    val enemy: DefenderEnemy? get() = enemies.firstOrNull()
}

/** Timestamp-based arcade. Rendering only observes this session; it cannot cause a collision. */
class DefenderSession(private val clock: () -> Long = { System.nanoTime() / 1_000_000 }) {
    var state = DefenderState()
        private set
    private var startedAt = 0L
    private var pausedAt = 0L
    private var pausedTotal = 0L
    private var nextSpawnAt = 0L
    private var nextId = 1
    private var feedbackUntil = 0L
    private data class Checkpoint(val state: DefenderState, val nextSpawnAt: Long, val nextId: Int, val feedbackUntil: Long)
    private var checkpoint: Checkpoint? = null
    private var lastFire: Triple<Long, DefenderName, DefenderAccidental>? = null
    private val entries = listOf(DefenderPoint(.13f, .12f), DefenderPoint(.87f, .12f),
        DefenderPoint(.37f, .10f), DefenderPoint(.63f, .10f),
        DefenderPoint(.13f, .36f), DefenderPoint(.87f, .36f))

    fun start(seed: Int = 0, configuration: DefenderConfiguration = DefenderConfiguration()) {
        startedAt = clock(); pausedAt = 0; pausedTotal = 0
        nextId = 1; nextSpawnAt = 0; feedbackUntil = 0; lastFire = null
        state = DefenderState(phase = DefenderPhase.RUNNING, seed = seed, configuration = configuration,
            events = listOf(DefenderEvent("START", 0)))
        advance(0)
        rememberInput()
    }
    fun select(name: DefenderName) {
        if (state.phase != DefenderPhase.RUNNING) return
        tick()
        if (state.phase == DefenderPhase.RUNNING) {
            state = state.copy(selected = name)
            rememberInput()
        }
    }
    fun accidental(value: DefenderAccidental) {
        if (state.phase != DefenderPhase.RUNNING || !state.configuration.accidentals) return
        tick()
        if (state.phase == DefenderPhase.RUNNING) {
            state = state.copy(selectedAccidental = value)
            rememberInput()
        }
    }
    fun fire() {
        val answer = state.selected
        if (answer == null) { tick(); return }
        fireAt(clock(), answer, state.selectedAccidental)
    }
    /** Source timestamp and selection travel together, including delayed dispatch past a frame tick. */
    fun fireAt(eventMonotonicMillis: Long, capturedSelection: DefenderName,
        capturedAccidental: DefenderAccidental = DefenderAccidental.NATURAL) {
        if (state.phase !in listOf(DefenderPhase.RUNNING, DefenderPhase.FINISHED)) return
        if (lastFire == Triple(eventMonotonicMillis, capturedSelection, capturedAccidental)) return
        val saved = checkpoint ?: return
        if (saved.state.phase != DefenderPhase.RUNNING) return
        val eventTime = eventMonotonicMillis - startedAt - pausedTotal
        val present = maxOf(state.activeMillis, clock() - startedAt - pausedTotal)
        if (eventTime < saved.state.activeMillis || eventTime > present) return
        if (eventTime < state.activeMillis) {
            state = saved.state; nextSpawnAt = saved.nextSpawnAt; nextId = saved.nextId; feedbackUntil = saved.feedbackUntil
        }
        advance(eventTime)
        if (state.phase != DefenderPhase.RUNNING) return
        val answer = capturedSelection
        val enemy = defenderMatch(state.enemies, answer, capturedAccidental, state.activeMillis)
        if (enemy != null) {
            val result = DefenderResult(enemy, DefenderResolution.ELIMINATED, state.activeMillis, enemy.position(state.activeMillis))
            state = state.copy(enemies = state.enemies.filterNot { it.id == enemy.id }, selected = null,
                selectedAccidental = DefenderAccidental.NATURAL, points = state.points + 100,
                results = state.results + result, feedback = null,
                events = state.events + DefenderEvent("SHOT_HIT", state.activeMillis, enemy, answer, capturedAccidental))
        } else {
            state = state.copy(failedShots = state.failedShots + 1, feedback = "No coincide · probá otra nota",
                events = state.events + DefenderEvent("SHOT_MISS", state.activeMillis, selection = answer, accidental = capturedAccidental))
            feedbackUntil = state.activeMillis + 1200
        }
        lastFire = Triple(eventMonotonicMillis, capturedSelection, capturedAccidental)
        rememberInput()
        advance(present)
    }
    fun tick() {
        if (state.phase == DefenderPhase.RUNNING)
            advance((clock() - startedAt - pausedTotal).coerceAtLeast(state.activeMillis))
    }
    fun pause() {
        if (state.phase != DefenderPhase.RUNNING) return
        tick()
        if (state.phase != DefenderPhase.RUNNING) return
        pausedAt = clock()
        state = state.copy(phase = DefenderPhase.PAUSED,
            events = state.events + DefenderEvent("PAUSE", state.activeMillis))
        rememberInput()
    }
    fun resume() {
        if (state.phase != DefenderPhase.PAUSED) return
        pausedTotal += (clock() - pausedAt).coerceAtLeast(0)
        state = state.copy(phase = DefenderPhase.RUNNING,
            events = state.events + DefenderEvent("RESUME", state.activeMillis))
        rememberInput()
    }
    fun interrupt() {
        if (state.phase !in listOf(DefenderPhase.RUNNING, DefenderPhase.PAUSED)) { rememberInput(); return }
        tick()
        if (state.phase == DefenderPhase.FINISHED) { rememberInput(); return }
        state = state.copy(phase = DefenderPhase.INTERRUPTED, enemies = emptyList(), selected = null,
            events = state.events + DefenderEvent("INTERRUPT", state.activeMillis))
        rememberInput()
    }

    fun prepare() {
        interrupt()
        state = DefenderState()
        checkpoint = null; lastFire = null
    }
    private fun rememberInput() { checkpoint = Checkpoint(state, nextSpawnAt, nextId, feedbackUntil) }

    private fun interval(at: Long): Long = when {
        at < 30_000 -> 4000; at < 60_000 -> 3500; at < 90_000 -> 3000; else -> 2500
    }
    private fun advance(target: Long) {
        while (state.phase == DefenderPhase.RUNNING) {
            val arriving = state.enemies.minWithOrNull(compareBy<DefenderEnemy> { it.deadlineMillis }.thenBy { it.id })
            val deadline = arriving?.deadlineMillis ?: Long.MAX_VALUE
            val next = minOf(deadline, nextSpawnAt)
            if (next > target) break
            state = state.copy(activeMillis = next)
            if (deadline <= nextSpawnAt) {
                val enemy = requireNotNull(arriving)
                val result = DefenderResult(enemy, DefenderResolution.ARRIVED, deadline, enemy.destination)
                val remaining = state.lives - 1
                state = state.copy(enemies = state.enemies.filterNot { it.id == enemy.id }, lives = remaining, results = state.results + result,
                    events = state.events + DefenderEvent("ARRIVAL", deadline, enemy),
                    phase = if (remaining == 0) DefenderPhase.FINISHED else DefenderPhase.RUNNING)
                if (remaining == 0) state = state.copy(selected = null, enemies = emptyList(),
                    events = state.events + DefenderEvent("GAME_OVER", deadline))
            } else {
                if (state.enemies.size < state.configuration.maxEnemies) {
                    val id = nextId
                    val random = Random(state.seed xor (id * 0x45d9f3b))
                    val pool = defenderNotePool(state.configuration.stage, state.configuration.accidentals)
                    val note = pool[random.nextInt(pool.size)]
                    val routes = entries.shuffled(random).map { entry ->
                        val destination = if (state.configuration.maxEnemies == 1) DefenderPoint(.5f, .82f)
                            else DefenderPoint(.5f + (entry.x - .5f) * .55f, .84f)
                        DefenderEnemy(id, note, nextSpawnAt, state.configuration.travelMillis, entry, destination)
                    }
                    val enemy = routes.firstOrNull { defenderHasRoom(it, state.enemies) }
                    if (enemy != null) {
                        nextId++
                        state = state.copy(enemies = state.enemies + enemy,
                            events = state.events + DefenderEvent("SPAWN", nextSpawnAt, enemy))
                    }
                }
                // An occupied slot consumes its scheduled opportunity. Nothing queues for later.
                nextSpawnAt += interval(nextSpawnAt)
            }
        }
        if (state.phase == DefenderPhase.RUNNING) state = state.copy(activeMillis = target,
            feedback = state.feedback.takeIf { target < feedbackUntil })
    }
}

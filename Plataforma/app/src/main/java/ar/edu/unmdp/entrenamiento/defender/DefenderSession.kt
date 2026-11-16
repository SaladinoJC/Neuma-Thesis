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
    val travelMillis: Long, val entry: DefenderPoint, val destination: DefenderPoint = DefenderPoint(.5f, .82f),
    val chord: DefenderChord? = null) {
    init { require(chord == null || chord.root == note) }
    val deadlineMillis: Long get() = appearedAtMillis + travelMillis
    fun position(activeMillis: Long): DefenderPoint {
        val fraction = ((activeMillis - appearedAtMillis).toDouble() / travelMillis).coerceIn(0.0, 1.0).toFloat()
        return DefenderPoint(entry.x + (destination.x - entry.x) * fraction, entry.y + (destination.y - entry.y) * fraction)
    }
}
@Serializable enum class DefenderPhase { SETUP, RUNNING, PAUSED, FINISHED, INTERRUPTED }
@Serializable enum class DefenderResolution { ELIMINATED, ARRIVED }
@Serializable
data class DefenderConfiguration(val version: String = "defender-pressure-v2", val travelMillis: Long = 10_000,
    val noteSound: Boolean = true, val effectsSound: Boolean = true, val letterNames: Boolean = false,
    val stage: DefenderStage = DefenderStage.N1, val accidentals: Boolean = false, val maxEnemies: Int = 1,
    val dda: DefenderDdaConfiguration = DefenderDdaConfiguration(), val variant: String = "notes",
    val chordQuality: DefenderChordQuality = DefenderChordQuality.MAJOR,
    val chordInversion: Int = 0, val chordClefs: DefenderChordClefs = DefenderChordClefs.TREBLE,
    val spawnIntervalMillis: Long? = null) {
    init {
        require(travelMillis in 3000..14_000 && travelMillis in dda.minimumTravelMillis..dda.maximumTravelMillis)
        require(maxEnemies in 1..4)
        require(spawnIntervalMillis == null || spawnIntervalMillis in 1000..6000)
        require(variant in setOf("notes", "chords"))
        require(chordInversion in chordQuality.intervals.indices)
    }
}
@Serializable
data class DefenderResult(val enemy: DefenderEnemy, val resolution: DefenderResolution,
    val atMillis: Long, val position: DefenderPoint)
@Serializable
data class DefenderEvent(val kind: String, val atMillis: Long, val enemy: DefenderEnemy? = null,
    val selection: DefenderName? = null, val accidental: DefenderAccidental = DefenderAccidental.NATURAL,
    val quality: DefenderChordQuality? = null)
@Serializable
data class DefenderState(val phase: DefenderPhase = DefenderPhase.SETUP, val lives: Int = 3,
    val points: Int = 0, val selected: DefenderName? = null, val enemies: List<DefenderEnemy> = emptyList(),
    val selectedAccidental: DefenderAccidental = DefenderAccidental.NATURAL,
    val activeMillis: Long = 0, val failedShots: Int = 0, val feedback: String? = null,
    val seed: Int = 0, val configuration: DefenderConfiguration = DefenderConfiguration(),
    val origin: String = "practice", val results: List<DefenderResult> = emptyList(),
    val events: List<DefenderEvent> = emptyList(), val currentTravelMillis: Long = configuration.travelMillis,
    val ddaDecisions: List<DefenderDdaDecision> = emptyList(), val selectedQuality: DefenderChordQuality? = null) {
    val enemy: DefenderEnemy? get() = enemies.firstOrNull()
    val spawnIntervalMillis: Long get() = configuration.spawnIntervalMillis ?: when {
        activeMillis < 30_000 -> 4000; activeMillis < 60_000 -> 3500; activeMillis < 90_000 -> 3000; else -> 2500
    }
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
    private data class ShotStamp(val time: Long, val name: DefenderName, val accidental: DefenderAccidental, val quality: DefenderChordQuality?)
    private var lastFire: ShotStamp? = null
    private val entries = listOf(DefenderPoint(.13f, .12f), DefenderPoint(.87f, .12f),
        DefenderPoint(.37f, .10f), DefenderPoint(.63f, .10f),
        DefenderPoint(.13f, .36f), DefenderPoint(.87f, .36f))

    fun start(seed: Int = 0, configuration: DefenderConfiguration = DefenderConfiguration(), origin: String = "practice") {
        require(origin in setOf("practice", "progression"))
        require(origin != "progression" || configuration.travelMillis == 10_000L) { "La progresión inicia cada partida con viaje de 10 s." }
        require(origin != "progression" || configuration.spawnIntervalMillis == null) { "La progresión conserva la curva automática de aparición." }
        startedAt = clock(); pausedAt = 0; pausedTotal = 0
        nextId = 1; nextSpawnAt = 0; feedbackUntil = 0; lastFire = null
        state = DefenderState(phase = DefenderPhase.RUNNING, seed = seed, configuration = configuration, origin = origin,
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
        if (state.phase != DefenderPhase.RUNNING || (!state.configuration.accidentals && state.configuration.variant != "chords")) return
        tick()
        if (state.phase == DefenderPhase.RUNNING) {
            state = state.copy(selectedAccidental = value)
            rememberInput()
        }
    }
    fun quality(value: DefenderChordQuality) {
        if (state.phase != DefenderPhase.RUNNING || state.configuration.variant != "chords") return
        tick()
        if (state.phase == DefenderPhase.RUNNING) { state = state.copy(selectedQuality = value); rememberInput() }
    }
    fun fire() {
        val answer = state.selected
        if (answer == null) { tick(); return }
        fireAt(clock(), answer, state.selectedAccidental, state.selectedQuality)
    }
    /** Source timestamp and selection travel together, including delayed dispatch past a frame tick. */
    fun fireAt(eventMonotonicMillis: Long, capturedSelection: DefenderName,
        capturedAccidental: DefenderAccidental = DefenderAccidental.NATURAL, capturedQuality: DefenderChordQuality? = null) {
        if (state.phase !in listOf(DefenderPhase.RUNNING, DefenderPhase.FINISHED)) return
        if (state.configuration.variant == "chords" && capturedQuality == null) return
        if (lastFire == ShotStamp(eventMonotonicMillis, capturedSelection, capturedAccidental, capturedQuality)) return
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
        val enemy = if (state.configuration.variant == "chords") state.enemies.firstOrNull {
            it.chord?.answer == DefenderChordAnswer(answer, capturedAccidental, requireNotNull(capturedQuality))
        } else defenderMatch(state.enemies, answer, capturedAccidental, state.activeMillis)
        if (enemy != null) {
            val result = DefenderResult(enemy, DefenderResolution.ELIMINATED, state.activeMillis, enemy.position(state.activeMillis))
            state = state.copy(enemies = state.enemies.filterNot { it.id == enemy.id }, selected = null,
                selectedAccidental = DefenderAccidental.NATURAL, selectedQuality = null, points = state.points + 100,
                results = state.results + result, feedback = null,
                events = state.events + DefenderEvent("SHOT_HIT", state.activeMillis, enemy, answer, capturedAccidental, capturedQuality))
            evaluateTravel()
        } else {
            state = state.copy(failedShots = state.failedShots + 1, feedback = if (state.configuration.variant == "chords") "No coincide · revisá raíz y calidad" else "No coincide · probá otra nota",
                events = state.events + DefenderEvent("SHOT_MISS", state.activeMillis, selection = answer, accidental = capturedAccidental, quality = capturedQuality))
            feedbackUntil = state.activeMillis + 1200
        }
        lastFire = ShotStamp(eventMonotonicMillis, capturedSelection, capturedAccidental, capturedQuality)
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
            events = state.events + state.enemies.map { DefenderEvent("CANCEL", state.activeMillis, it) } + DefenderEvent("INTERRUPT", state.activeMillis))
        rememberInput()
    }

    fun prepare() {
        interrupt()
        state = DefenderState()
        checkpoint = null; lastFire = null
    }
    /** Closing the result/input surface is an explicit lifecycle boundary, not a timing offset. */
    fun sealInput() { checkpoint = null; lastFire = null }
    private fun rememberInput() { checkpoint = Checkpoint(state, nextSpawnAt, nextId, feedbackUntil) }

    private fun evaluateTravel() {
        val decision = defenderTravelDecision(state)
        state = state.copy(currentTravelMillis = decision.afterTravelMillis, ddaDecisions = state.ddaDecisions + decision)
    }

    private fun interval(at: Long): Long = state.configuration.spawnIntervalMillis ?: when {
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
                evaluateTravel()
                if (remaining == 0) state = state.copy(selected = null, enemies = emptyList(),
                    events = state.events + state.enemies.map { DefenderEvent("CANCEL", deadline, it) } + DefenderEvent("GAME_OVER", deadline))
            } else {
                if (state.enemies.size < state.configuration.maxEnemies) {
                    val chords = if (state.configuration.variant == "chords") defenderChordPool(state.configuration.chordQuality,
                        state.configuration.chordInversion, if (state.configuration.chordClefs == DefenderChordClefs.MIXED)
                            if (nextId % 2 == 1) DefenderChordClefs.TREBLE else DefenderChordClefs.BASS else state.configuration.chordClefs)
                        .filter { chord -> state.enemies.none { it.chord?.answer == chord.answer } } else null
                    if (chords != null && chords.isEmpty()) { nextSpawnAt += interval(nextSpawnAt); continue }
                    val id = nextId
                    val random = Random(state.seed xor (id * 0x45d9f3b))
                    val pool = defenderNotePool(state.configuration.stage, state.configuration.accidentals)
                    val chord = chords?.let { it[random.nextInt(it.size)] }
                    val note = chord?.root ?: pool[random.nextInt(pool.size)]
                    val routes = entries.shuffled(random).map { entry ->
                        val safeEntry = if (chord != null) entry.copy(x = entry.x.coerceIn(.18f, .82f)) else entry
                        val destination = if (state.configuration.maxEnemies == 1) DefenderPoint(.5f, .82f)
                            else DefenderPoint(.5f + (safeEntry.x - .5f) * .55f, .84f)
                        DefenderEnemy(id, note, nextSpawnAt, state.currentTravelMillis, safeEntry, destination, chord)
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

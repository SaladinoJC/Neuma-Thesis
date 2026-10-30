package ar.edu.unmdp.entrenamiento.defender

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable enum class DefenderClef(val label: String, val bottomDiatonic: Int) {
    TREBLE("Sol", 4 * 7 + 2), BASS("Fa", 2 * 7 + 4)
}
@Serializable enum class DefenderAccidental(val semitones: Int, val symbol: String) {
    FLAT(-1, "♭"), NATURAL(0, "♮"), SHARP(1, "♯")
}
@Serializable enum class DefenderStage(val label: String) {
    N1("N1 · Sol dentro"), N2("N2 · Sol ampliada"),
    N3("N3 · Fa ampliada"), N4("N4 · Sol y fa")
}

/** Finite written-note corpus: spelling and sounding pitch remain distinct. */
fun defenderNotePool(stage: DefenderStage, accidentals: Boolean): List<DefenderNote> {
    val ranges = when (stage) {
        DefenderStage.N1 -> listOf(DefenderClef.TREBLE to (30..38))
        DefenderStage.N2 -> listOf(DefenderClef.TREBLE to (26..42))
        DefenderStage.N3 -> listOf(DefenderClef.BASS to (14..30))
        DefenderStage.N4 -> listOf(DefenderClef.TREBLE to (26..42), DefenderClef.BASS to (14..30))
    }
    val alterations = if (accidentals) DefenderAccidental.entries else listOf(DefenderAccidental.NATURAL)
    return ranges.flatMap { (clef, range) -> range.flatMap { index -> alterations.map { accidental ->
        DefenderNote(DefenderName.entries[index % 7], index / 7, accidental, clef)
    } } }
}

/** Shared shield segment, also drawn by the arena; independent of speed/deadline. */
fun DefenderEnemy.distanceToBaseSquared(at: Long): Float {
    val p = position(at)
    val dx = p.x - p.x.coerceIn(.25f, .75f)
    val dy = p.y - .84f
    return dx * dx + dy * dy
}

fun defenderMatch(enemies: List<DefenderEnemy>, name: DefenderName,
    accidental: DefenderAccidental, at: Long): DefenderEnemy? = enemies
    .filter { it.note.name == name && it.note.accidental == accidental }
    .minWithOrNull(compareBy<DefenderEnemy> { it.distanceToBaseSquared(at) }
        .thenBy { it.appearedAtMillis }.thenBy { it.id })

/** Swept card reservations; checks the entire shared lifetime, not the rendering framerate. */
fun defenderHasRoom(candidate: DefenderEnemy, live: List<DefenderEnemy>): Boolean = live.none { other ->
    val from = maxOf(candidate.appearedAtMillis, other.appearedAtMillis)
    val until = minOf(candidate.deadlineMillis, other.deadlineMillis)
    if (from >= until) false else {
        val a = candidate.position(from); val b = other.position(from)
        val endA = candidate.position(until); val endB = other.position(until)
        fun axisWindow(start: Float, end: Float, distance: Float): ClosedFloatingPointRange<Float>? {
            val delta = end - start
            if (abs(delta) < .000001f) return if (abs(start) < distance) 0f..1f else null
            val first = (-distance - start) / delta; val second = (distance - start) / delta
            val lo = maxOf(0f, minOf(first, second)); val hi = minOf(1f, maxOf(first, second))
            return if (lo < hi) lo..hi else null
        }
        val chords = candidate.chord != null || other.chord != null
        val x = axisWindow(a.x - b.x, endA.x - endB.x, if (chords) .35f else .245f)
        val y = axisWindow(a.y - b.y, endA.y - endB.y, if (chords) .24f else .205f)
        x != null && y != null && maxOf(x.start, y.start) < minOf(x.endInclusive, y.endInclusive)
    }
}

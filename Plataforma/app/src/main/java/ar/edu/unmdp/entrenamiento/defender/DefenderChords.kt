package ar.edu.unmdp.entrenamiento.defender

import kotlinx.serialization.Serializable

@Serializable
enum class DefenderChordQuality(val label: String, val intervals: List<Int>) {
    MAJOR("Mayor", listOf(0, 4, 7)), MINOR("Menor", listOf(0, 3, 7)),
    DIMINISHED("Disminuida", listOf(0, 3, 6)), AUGMENTED("Aumentada", listOf(0, 4, 8)),
    DOMINANT7("Dominante 7", listOf(0, 4, 7, 10)), MAJOR7("Mayor 7", listOf(0, 4, 7, 11)),
    MINOR7("Menor 7", listOf(0, 3, 7, 10)), HALF_DIMINISHED7("Semidisminuida 7", listOf(0, 3, 6, 10))
}

@Serializable
enum class DefenderChordClefs(val label: String, val values: List<DefenderClef>) {
    TREBLE("Sol", listOf(DefenderClef.TREBLE)), BASS("Fa", listOf(DefenderClef.BASS)),
    MIXED("Sol y fa", DefenderClef.entries)
}

fun defenderInversionLabel(inversion: Int): String = when (inversion) {
    0 -> "Fundamental"; 1 -> "Primera inversión"; 2 -> "Segunda inversión"; 3 -> "Tercera inversión"
    else -> error("Inversión no disponible")
}

@Serializable
data class DefenderChordTarget(val quality: DefenderChordQuality, val inversion: Int = 0,
    val clefs: DefenderChordClefs = DefenderChordClefs.TREBLE) {
    init { require(inversion in quality.intervals.indices) }
    val key: String get() = "${quality.name}:$inversion:${clefs.name}"
    val label: String get() = "${quality.label} · ${defenderInversionLabel(inversion)} · ${clefs.label}"
    val prerequisites: Set<DefenderChordTarget> get() {
        val sol = DefenderChordQuality.entries.map { DefenderChordTarget(it) }.toSet()
        return if (inversion > 0) sol + copy(inversion = inversion - 1)
        else when (clefs) {
            DefenderChordClefs.TREBLE -> if (quality.ordinal == 0) emptySet()
                else setOf(copy(quality = DefenderChordQuality.entries[quality.ordinal - 1]))
            DefenderChordClefs.BASS -> sol
            DefenderChordClefs.MIXED -> DefenderChordQuality.entries.map { DefenderChordTarget(it, clefs = DefenderChordClefs.BASS) }.toSet()
        }
    }
}

fun defenderChordTargets(): List<DefenderChordTarget> = DefenderChordClefs.entries.flatMap { clefs ->
    DefenderChordQuality.entries.flatMap { quality -> quality.intervals.indices.map { DefenderChordTarget(quality, it, clefs) } }
}

@Serializable
data class DefenderChordAnswer(val name: DefenderName, val accidental: DefenderAccidental, val quality: DefenderChordQuality)

@Serializable
data class DefenderChord(val root: DefenderNote, val quality: DefenderChordQuality,
    val notes: List<DefenderNote>, val inversion: Int = 0, val version: String = "defender-chords-v2") {
    init {
        require(version in setOf("defender-triads-v1", "defender-chords-v2"))
        require(notes.size == quality.intervals.size && inversion in notes.indices)
        if (version == "defender-triads-v1") require(inversion == 0 && notes.size == 3 && root.clef == DefenderClef.TREBLE)
        require(notes.all { it.clef == root.clef && it.staffStep in -4..12 && it.midi in 24..96 })
        val pitches = quality.intervals.drop(inversion) + quality.intervals.take(inversion).map { it + 12 }
        require(notes.map { it.midi - root.midi } == pitches)
        val letters = notes.indices.map { it * 2 }
        require(notes.map { it.octave * 7 + it.name.ordinal - (root.octave * 7 + root.name.ordinal) } ==
            letters.drop(inversion) + letters.take(inversion).map { it + 7 })
    }
    val answer: DefenderChordAnswer get() = DefenderChordAnswer(root.name, root.accidental, quality)
}

private val chordPools: Map<DefenderChordTarget, List<DefenderChord>> by lazy {
    defenderChordTargets().filter { it.clefs != DefenderChordClefs.MIXED }.associateWith { target ->
        val quality = target.quality; val clef = target.clefs.values.single()
        (clef.bottomDiatonic - 10..clef.bottomDiatonic + 12).flatMap { diatonic -> DefenderAccidental.entries.mapNotNull { alteration ->
            val root = DefenderNote(DefenderName.entries[diatonic % 7], diatonic / 7, alteration, clef)
            val notes = quality.intervals.mapIndexed { index, interval ->
                val step = diatonic + index * 2
                val natural = DefenderNote(DefenderName.entries[step % 7], step / 7, clef = clef)
                val accidental = DefenderAccidental.entries.firstOrNull { natural.midi + it.semitones == root.midi + interval }
                accidental?.let { natural.copy(accidental = it) }
            }
            if (notes.any { it == null }) null else {
                val base = notes.filterNotNull()
                val voiced = base.drop(target.inversion) + base.take(target.inversion).map { it.copy(octave = it.octave + 1) }
                if (voiced.any { it.staffStep !in -4..12 || it.midi !in 24..96 }) null
                else DefenderChord(root, quality, voiced, target.inversion)
            }
        } }
    }
}

/** Written thirds/fifths first; reject any voicing that would require a double alteration. */
fun defenderChordPool(quality: DefenderChordQuality, inversion: Int = 0,
    clefs: DefenderChordClefs = DefenderChordClefs.TREBLE): List<DefenderChord> {
    val target = DefenderChordTarget(quality, inversion, clefs)
    return if (clefs == DefenderChordClefs.MIXED) listOf(DefenderChordClefs.TREBLE, DefenderChordClefs.BASS)
        .flatMap { chordPools.getValue(target.copy(clefs = it)) } else chordPools.getValue(target)
}

fun DefenderNote.writtenName(letterNames: Boolean = false, withOctave: Boolean = true): String =
    (if (letterNames) name.letter else name.solfege) + (if (accidental == DefenderAccidental.NATURAL) "" else accidental.symbol) +
        if (withOctave) octave.toString() else ""

fun DefenderEnemy.solution(letterNames: Boolean = false): String {
    return chord?.let { "${it.root.writtenName(letterNames, false)} ${it.quality.label.lowercase()}" } ?: note.writtenName(letterNames)
}

val DefenderConfiguration.contentLabel: String get() =
    if (variant == "chords") chordTarget.label else stage.label

val DefenderConfiguration.chordTarget: DefenderChordTarget get() = DefenderChordTarget(chordQuality, chordInversion, chordClefs)

/** Opposite sides of the common stem for written seconds; coordinates are engraving units. */
fun defenderChordHeadOffsets(notes: List<DefenderNote>): List<Int> {
    val down = notes.map { it.staffStep }.average() >= 4
    val offsets = mutableListOf<Int>()
    notes.forEachIndexed { index, note ->
        val adjacent = index > 0 && note.staffStep - notes[index - 1].staffStep == 1
        offsets += if (adjacent && offsets.last() == 0) { if (down) -12 else 12 } else 0
    }
    return offsets
}

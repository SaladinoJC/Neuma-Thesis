package ar.edu.unmdp.entrenamiento.instruments

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class InstrumentMelody(
    val midiNotes: List<Int>,
    val durationsMillis: List<Int>,
    val gapMillis: Int = 50,
    val generatorVersion: String = "pentatonic-c4-v1",
) {
    init {
        require(midiNotes.size in 1..8 && durationsMillis.size == midiNotes.size)
        require(midiNotes.all { it in supportedMidi })
        require(durationsMillis.all { it in 100..800 } && gapMillis in 0..100)
    }

    companion object {
        val supportedMidi = listOf(60, 62, 64, 67, 69)
    }
}

/** Misma gramática para todos los instrumentos; no repite la frase anterior. */
internal fun generateInstrumentMelody(randomIndex: (Int) -> Int, previous: InstrumentMelody?): InstrumentMelody {
    val scale = InstrumentMelody.supportedMidi
    val notes = mutableListOf(scale[randomIndex(scale.size)])
    repeat(3) {
        val candidates = scale.filter { it != notes.last() && abs(it-notes.last()) <= 5 }
        notes.add(candidates[randomIndex(candidates.size)])
    }
    val cadence = listOf(62, 64).filter {
        it != notes.last() && abs(it-notes.last()) <= 5 && (notes + it + 60).distinct().size >= 3
    }
    notes.add(cadence[randomIndex(cadence.size)])
    notes.add(60)
    if (notes == previous?.midiNotes) {
        val alternatives = (0..4).flatMap { index ->
            val candidates = if (index == 4) listOf(62, 64) else scale
            candidates.mapNotNull { replacement ->
                val changed = notes.toMutableList().apply { this[index] = replacement }
                changed.takeIf {
                    replacement != notes[index] && it.distinct().size >= 3 &&
                        it.zipWithNext().all { (a, b) -> a != b && abs(a-b) <= 5 }
                }
            }
        }
        val changed = alternatives[randomIndex(alternatives.size)]
        notes.clear()
        notes.addAll(changed)
    }
    return InstrumentMelody(notes.toList(), listOf(400, 400, 400, 400, 400, 800))
}

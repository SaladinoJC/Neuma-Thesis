package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import ar.edu.unmdp.entrenamiento.rhythm.*
import java.util.Locale

internal fun rhythmOccurrenceAt(score: RhythmScore, relativeNanos: Long): RhythmOccurrence? = score.occurrences.firstOrNull { occurrence ->
    val start = Beat(occurrence.bar * score.beatsPerBar) + occurrence.beat
    val end = occurrence.notes.fold(start) { sum, note -> sum + note.duration }
    relativeNanos >= start.nanos(score.bpm) && relativeNanos < end.nanos(score.bpm)
}

@Composable
internal fun RhythmResultSummary(result: RhythmResult, detailed: Boolean = false) {
    val score = result.score
    val owners = score.occurrences.associateBy { it.index }
    fun ownerLabel(index: Int?): String {
        val owner = owners[index] ?: return "Sin categoría asignada"
        return "Compás ${owner.bar + 1} · ${owner.category.label} (${if (owner.target) "objetivo" else "conexión"})"
    }
    Column {
        Text("Resumen · ${if (result.origin == "progression") "Progresión" else "Práctica"}", fontWeight = FontWeight.Bold, modifier = Modifier.testTag("rhythm-summary"))
        Text("A tiempo: ${result.attacks.count { it.kind == TapKind.ON_TIME }} / ${result.attacks.size} · Omitidos: ${result.omissions} · Extras: ${result.extras}")
        Text("Adelantados: ${result.attacks.count { it.kind == TapKind.EARLY }} · Atrasados: ${result.attacks.count { it.kind == TapKind.LATE }}")
        val unevaluated = result.attacks.count { it.kind == null }
        if (unevaluated > 0) Text("Sin evaluar por interrupción: $unevaluated")
        score.selected.forEach { category ->
            val cells = score.occurrences.filter { it.target && it.category == category }
            val indices = cells.map { it.index }.toSet()
            val attacks = result.attacks.filter { it.occurrenceIndex in indices }
            Text("${category.label}: ${attacks.count { it.kind == TapKind.ON_TIME }} / ${attacks.size} a tiempo (células objetivo)", modifier = Modifier.testTag("rhythm-summary-${category.code}"))
            val extras = result.taps.count { it.kind == TapKind.EXTRA && rhythmOccurrenceAt(score, it.relativeNanos)?.index in indices }
            Text("Adelantados ${attacks.count { it.kind == TapKind.EARLY }} · Atrasados ${attacks.count { it.kind == TapKind.LATE }} · Omitidos ${attacks.count { it.kind == TapKind.OMITTED }} · Extras $extras", style = MaterialTheme.typography.bodySmall)
            val silences = result.silences.filter { it.occurrenceIndex in indices }
            if (silences.isNotEmpty()) Text("Silencios respetados ${silences.count { it.respected == true }} / ${silences.size} · Pendientes ${silences.count { it.respected == null }}", style = MaterialTheme.typography.bodySmall)
            if (detailed) {
                val beats = cells.flatMap { it.notes }.fold(Beat(0)) { sum, note -> sum + note.duration }
                Text("${cells.size} apariciones objetivo · ${beats.numerator}/${beats.denominator} pulsos", style = MaterialTheme.typography.bodySmall)
            }
        }
        Text("Cada golpe conserva su desvío con signo; los errores no detienen el ejercicio.", color = GardenTokens.TextSecondary)
        result.attacks.forEach { attack ->
            if (detailed) Text(ownerLabel(attack.occurrenceIndex), style = MaterialTheme.typography.bodySmall)
            Text("Golpe ${attack.index + 1}: ${attack.kind?.let(::rhythmKindLabel) ?: "Sin evaluar (interrupción)"}" +
                (attack.deltaNanos?.let { String.format(Locale.ROOT, " · %+.1f ms", it / 1_000_000.0) } ?: ""), style = MaterialTheme.typography.bodySmall)
            if (detailed) Text("Posición: ${rhythmMillis(attack.offsetNanos)} ms · ventanas ${rhythmMillis(attack.narrowNanos)}/${rhythmMillis(attack.wideNanos)} ms", style = MaterialTheme.typography.bodySmall)
        }
        result.taps.forEachIndexed { index, tap ->
            if (tap.kind == TapKind.EXTRA || tap.kind == TapKind.PREPARATION) {
                Text("${rhythmKindLabel(tap.kind)} · ${rhythmMillis(tap.relativeNanos)} ms", style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("rhythm-tap-detail-$index"))
                if (detailed) Text(if (tap.kind == TapKind.PREPARATION) "Compás de entrada"
                    else rhythmOccurrenceAt(score, tap.relativeNanos)?.let { ownerLabel(it.index) } ?: "Fuera de las apariciones del ejercicio", style = MaterialTheme.typography.bodySmall)
            }
        }
        if (detailed) result.silences.forEachIndexed { index, silence ->
            Text("Silencio · ${ownerLabel(silence.occurrenceIndex)}", style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.testTag("rhythm-silence-detail-$index"))
            Text("${rhythmMillis(silence.startNanos)}–${rhythmMillis(silence.endNanos)} ms · " + when (silence.respected) {
                true -> "Respetado"
                false -> "${silence.extraCount} golpes extra"
                null -> "Sin evaluar (interrupción)"
            }, style = MaterialTheme.typography.bodySmall)
        }
    }
}

internal fun rhythmMillis(nanos: Long) = String.format(Locale.ROOT, "%.2f", nanos / 1_000_000.0)

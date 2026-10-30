package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ar.edu.unmdp.entrenamiento.dda.FuzzyInference
import java.util.Locale

@Composable
internal fun FuzzyRuleDetails(inference: FuzzyInference) {
    inference.memberships.forEach { (input, terms) ->
        Text("$input: " + terms.entries.joinToString { "${it.key} ${String.format(Locale.getDefault(), "%.3f", it.value)}" })
    }
    inference.rules.forEach { rule ->
        Text("${rule.id} · ${rule.label} · ${String.format(Locale.getDefault(), "%.3f", rule.strength)}", style = MaterialTheme.typography.bodySmall)
    }
    inference.diagnostic?.let { Text(it, color = GardenTokens.ErrorText) }
}

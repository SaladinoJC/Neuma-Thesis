package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.progress.Mastery
import java.util.Locale

private fun decimal(n: Double) = String.format(Locale.ROOT, "%.3f", n)
@Composable
internal fun MasteryButton(mastery: Mastery) {
    var open by remember { mutableStateOf(false) }
    TextButton(onClick = { open = true }) { Text("Dominio ${if (mastery.high) "alto" else "no alto"} · Inspeccionar") }
    if (open) AlertDialog(onDismissRequest = { open = false }, confirmButton = {
        TextButton(onClick = { open = false }) { Text("Cerrar") }
    }, title = { Text("Dominio de partida · Experimental") }, text = {
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(mastery.configuration.version)
            mastery.reference?.let { Text("Referencia de prueba: $it notas completadas") }
            mastery.inputs.forEach { (key, value) -> Text("$key: ${decimal(value)}") }
            mastery.inference.memberships.forEach { (key, terms) -> Text("$key: " + terms.entries.joinToString { "${it.key}=${decimal(it.value)}" }) }
            mastery.inference.rules.forEach { Text("${it.id}: ${it.label} · ${decimal(it.strength)}") }
            Text("Centroide: ${mastery.inference.centroid?.let(::decimal) ?: "Sin salida válida"} · Alto desde ${mastery.threshold}")
            mastery.inference.diagnostic?.let { Text(it) }
            Text("AND/implicación mínimo; agregación máximo; centroide, ${mastery.configuration.samplingPoints} puntos.")
            mastery.configuration.inputs.forEach { (key, terms) -> Text("Entrada $key: $terms") }
            Text("Salida: ${mastery.configuration.outputs}")
            Text("Los valores son parámetros de prueba, sin calibración pedagógica.")
        }
    })
}

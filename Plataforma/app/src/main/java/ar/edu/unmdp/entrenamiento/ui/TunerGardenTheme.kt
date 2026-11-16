package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import ar.edu.unmdp.entrenamiento.tuner.*

@Composable
internal fun TunerGardenTheme(content: @Composable () -> Unit) {
    val body = MaterialTheme.typography.bodyLarge.copy(fontFamily = gardenBody)
    MaterialTheme(colorScheme = lightColorScheme(primary = GardenTokens.Primary, onPrimary = GardenTokens.OnPrimary,
        background = GardenTokens.Background, surface = GardenTokens.Surface, surfaceContainerHigh = GardenTokens.Surface,
        onSurface = GardenTokens.Text, onBackground = GardenTokens.Text, onSurfaceVariant = GardenTokens.TextSecondary,
        secondary = GardenTokens.Primary, secondaryContainer = GardenTokens.SurfaceSelected,
        onSecondaryContainer = GardenTokens.Text, outline = GardenTokens.Border,
        error = GardenTokens.ErrorText, errorContainer = GardenTokens.ErrorSurface),
        typography = Typography(bodyLarge = body, bodyMedium = body, labelLarge = body, titleMedium = body,
            titleLarge = MaterialTheme.typography.titleLarge.copy(fontFamily = gardenTitle),
            titleSmall = MaterialTheme.typography.titleSmall.copy(fontFamily = gardenBody),
            labelMedium = MaterialTheme.typography.labelMedium.copy(fontFamily = gardenBody),
            labelSmall = MaterialTheme.typography.labelSmall.copy(fontFamily = gardenBody),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = gardenBody),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = gardenTitle),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = gardenTitle),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(fontFamily = gardenTitle)), content = content)
}

@Composable
internal fun TunerOption(label: String, checked: Boolean, enabled: Boolean, tag: String, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = GardenTokens.TouchMinimum).testTag(tag)
        .toggleable(checked, enabled = enabled, role = Role.Switch, onValueChange = change), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked, null, enabled = enabled)
    }
}

@Composable
internal fun TunerHistoryAccess(ui: TunerUiState, model: TunerViewModel) {
    val config = ui.configuration.configuration
    if (config != null && config.mode != TunerMode.FREE) {
        TunerOption("Guardar seguimiento", config.saveHistory,
            !ui.configurationBusy && !ui.referenceBusy && !ui.guided.active && !ui.ear.active, "tuner-save-history", model::saveHistory)
        Text(if (config.saveHistory) "Se guardará el recorrido al terminar o interrumpirlo."
            else "Seguimiento apagado: resumen en pantalla, sin guardar desempeño.", color = GardenTokens.TextSecondary)
    }
    if (ui.historyBusy) Text("Actualizando seguimiento…", modifier = Modifier.testTag("tuner-history-busy"))
    if (ui.history.pending.isNotEmpty()) Text("${ui.history.pending.size} recorrido(s) pendientes de guardar. Conservados en esta apertura de la app.",
        color = GardenTokens.ErrorText, modifier = Modifier.testTag("tuner-history-pending"))
    ui.history.error?.let { Text(it, color = GardenTokens.ErrorText, modifier = Modifier.testTag("tuner-history-error")) }
    if (ui.history.error != null || ui.history.pending.isNotEmpty()) OutlinedButton(onClick = model::retryHistory,
        enabled = !ui.historyBusy, modifier = Modifier.testTag("tuner-history-retry")) { Text("Reintentar seguimiento") }
    TextButton(onClick = model::openHistory, enabled = !ui.guided.active && !ui.ear.active && !ui.configurationBusy && !ui.referenceBusy,
        modifier = Modifier.heightIn(min = GardenTokens.TouchMinimum).testTag("tuner-open-history")) { Text("Historial del afinador") }
}

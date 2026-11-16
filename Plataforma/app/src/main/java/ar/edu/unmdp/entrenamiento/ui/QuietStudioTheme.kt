package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import ar.edu.unmdp.entrenamiento.BuildConfig

@Composable
internal fun QuietStudioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = QuietTokens.Primary, onPrimary = QuietTokens.OnPrimary,
            background = QuietTokens.Background, onBackground = QuietTokens.Text,
            surface = QuietTokens.Surface, onSurface = QuietTokens.Text,
            surfaceContainer = QuietTokens.Background, surfaceContainerHigh = QuietTokens.Surface,
            surfaceContainerHighest = QuietTokens.SurfaceSelected,
            surfaceVariant = QuietTokens.SurfaceSelected, onSurfaceVariant = QuietTokens.TextSecondary,
            secondary = QuietTokens.Primary, secondaryContainer = QuietTokens.SurfaceSelected,
            onSecondaryContainer = QuietTokens.Text, outline = QuietTokens.Border,
            error = QuietTokens.ErrorText, errorContainer = QuietTokens.ErrorSurface,
            onErrorContainer = QuietTokens.Text),
        typography = Typography(
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = gardenBody),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = gardenBody),
            bodySmall = MaterialTheme.typography.bodySmall.copy(fontFamily = gardenBody),
            labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = gardenBody),
            titleMedium = MaterialTheme.typography.titleMedium.copy(fontFamily = gardenBody),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(fontFamily = gardenTitle),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(fontFamily = gardenTitle)),
        content = content)
}

@Composable
internal fun RhythmTheme(content: @Composable () -> Unit) {
    if (BuildConfig.QUIET_STUDIO) QuietStudioTheme(content) else TunerGardenTheme(content)
}

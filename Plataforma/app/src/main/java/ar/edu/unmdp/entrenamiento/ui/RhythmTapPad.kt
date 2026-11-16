package ar.edu.unmdp.entrenamiento.ui

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.BuildConfig

/** Un ACTION_DOWN por contacto, sin esperar a soltar ni al siguiente frame de Compose. */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun RhythmTapPad(label: String, active: Boolean, onTouch: (Long) -> Unit, onAccessibleTap: () -> Unit,
    tag: String = "rhythm-tap") {
    val quiet = BuildConfig.QUIET_STUDIO
    val shape = RoundedCornerShape(if (quiet) QuietTokens.PadRadius else RhythmTokens.RadiusLarge)
    Box(Modifier.fillMaxWidth().heightIn(min = if (quiet) QuietTokens.PadHeight else 100.dp)
        .background(if (active) RhythmTokens.SurfaceSelected else RhythmTokens.DisabledSurface, shape)
        .border(1.dp, RhythmTokens.Border, shape)
        .testTag(tag).semantics {
            contentDescription = "Registrar un tap rítmico"
            if (active) onClick { onAccessibleTap(); true } else disabled()
        }.pointerInteropFilter { event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN || event.actionMasked == MotionEvent.ACTION_POINTER_DOWN)
                onTouch(event.eventTime)
            true
        }, contentAlignment = Alignment.Center) {
        Text(label, style = MaterialTheme.typography.headlineMedium,
            color = if (active) RhythmTokens.Primary else RhythmTokens.DisabledText,
            modifier = Modifier.padding(RhythmTokens.SpaceMd))
    }
}

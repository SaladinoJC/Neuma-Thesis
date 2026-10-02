package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private fun isBlack(midi: Int) = midi % 12 in listOf(1, 3, 6, 8, 10)

/** Teclado cromático continuo; solo las notas de la escala responden al toque. */
@Composable
fun PianoKeyboard(notes: List<Int>, highlighted: Int?, enabled: Boolean,
    onNote: (Int) -> Unit, modifier: Modifier = Modifier) {
    if (notes.isEmpty()) return
    val first = notes.first().let { if (isBlack(it)) it - 1 else it }
    val last = notes.last().let { if (isBlack(it)) it + 1 else it }
    val whiteNotes = (first..last).filterNot(::isBlack)
    BoxWithConstraints(modifier.testTag("piano-keyboard")) {
        val keyWidth = maxWidth / whiteNotes.size
        Row(Modifier.fillMaxSize()) {
            whiteNotes.forEach { midi ->
                val index = notes.indexOf(midi)
                PianoKey(midi, index, false, index >= 0 && highlighted == index,
                    enabled && index >= 0, onNote, Modifier.width(keyWidth).fillMaxHeight())
            }
        }
        (first..last).filter(::isBlack).forEach { midi ->
            val index = notes.indexOf(midi)
            val whitesBefore = whiteNotes.count { it < midi }
            PianoKey(midi, index, true, index >= 0 && highlighted == index,
                enabled && index >= 0, onNote,
                Modifier.offset(x = keyWidth * whitesBefore - keyWidth * 0.31f)
                    .width(keyWidth * 0.62f).fillMaxHeight(0.62f))
        }
    }
}

@Composable
private fun PianoKey(midi: Int, index: Int, black: Boolean, highlighted: Boolean,
    enabled: Boolean, onNote: (Int) -> Unit, modifier: Modifier) {
    val active = index >= 0
    val color = when {
        highlighted -> Pink
        black -> if (active) Color(0xFF222636) else Color(0xFF101723)
        else -> if (active) Color(0xFFF5F1EC) else Color(0xFFACB0BA)
    }
    val animated = animateColorAsState(color, label = "piano-key").value
    val label = noteName(midi)
    val noteAction by rememberUpdatedState(onNote)
    val semantics = modifier.padding(horizontal = 1.dp)
        .testTag(if (active) "note-$index" else "inactive-key-$midi")
        .pointerInput(index, enabled) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                down.consume()
                if (enabled) noteAction(index)
                waitForUpOrCancellation()?.consume()
            }
        }
        .semantics {
            role = Role.Button
            contentDescription = "Tecla $label"
            if (!enabled) disabled()
            onClick(label = "Tocar $label") {
                if (enabled) { noteAction(index); true } else false
            }
            stateDescription = when {
                highlighted -> "Sonando"
                !active -> "Fuera de la escala"
                else -> "En espera"
            }
        }
    Surface(modifier = semantics,
        shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp), color = animated,
        border = BorderStroke(if (highlighted) 2.dp else 1.dp,
            if (highlighted) Color(0xFFFFAED0) else if (black) Color(0xFF434759) else Color(0xFF8F9299)),
        shadowElevation = if (black) 6.dp else 0.dp) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent,
            if (black) Color.Black.copy(alpha = 0.25f) else Color.Black.copy(alpha = 0.08f))))) {
            Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label.dropLast(1), color = if (black) Color(0xFFD8D8E8) else Color(0xFF30323D),
                    fontSize = if (black) 9.sp else 11.sp, fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Medium)
                if (!black) Text("${midi / 12 - 1}", color = Color(0xFF656977), fontSize = 9.sp)
            }
        }
    }
}

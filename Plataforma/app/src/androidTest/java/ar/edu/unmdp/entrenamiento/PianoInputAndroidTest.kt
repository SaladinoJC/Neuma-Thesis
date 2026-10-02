package ar.edu.unmdp.entrenamiento

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import ar.edu.unmdp.entrenamiento.ui.PianoKeyboard
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PianoInputAndroidTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun notasRapidasYDosDedosSeRegistranEnOrdenAlApoyarlos() {
        val pressed = mutableListOf<Int>()
        compose.setContent {
            MaterialTheme {
                PianoKeyboard((60..71).toList(), null, true, pressed::add,
                    Modifier.fillMaxWidth().height(220.dp))
            }
        }
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        fun position(index: Int, white: Boolean): Offset {
            val key = compose.onNodeWithTag("note-$index").fetchSemanticsNode().boundsInRoot
            return Offset(key.center.x - root.left,
                (if (white) key.bottom - 12f else key.center.y) - root.top)
        }
        val c = position(0, true)
        val cs = position(1, false)
        val d = position(2, true)
        compose.onRoot().performTouchInput {
            down(0, c)
            advanceEventTime(10)
            down(1, cs) // Segundo dedo mientras el primero sigue apoyado.
            advanceEventTime(10)
            up(0)
            up(1)
            repeat(6) {
                down(d)
                advanceEventTime(10)
                up()
            }
        }
        compose.runOnIdle { assertEquals(listOf(0, 1) + List(6) { 2 }, pressed) }
    }
}

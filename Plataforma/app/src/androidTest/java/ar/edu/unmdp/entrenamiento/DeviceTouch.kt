package ar.edu.unmdp.entrenamiento

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.geometry.Offset
import androidx.test.platform.app.InstrumentationRegistry

/** Los adaptadores temporales esperan uptime del dispositivo, no el reloj virtual de Compose. */
fun tapWithDeviceUptime(view: View, positionInRoot: Offset) {
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val downTime = SystemClock.uptimeMillis()
        for (action in listOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP)) {
            val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), action,
                positionInRoot.x, positionInRoot.y, 0)
            try {
                event.source = InputDevice.SOURCE_TOUCHSCREEN
                view.dispatchTouchEvent(event)
            } finally { event.recycle() }
        }
    }
}

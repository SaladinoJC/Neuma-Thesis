package ar.edu.unmdp.entrenamiento

import android.os.Bundle
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import ar.edu.unmdp.entrenamiento.audio.AndroidNotePlayer
import ar.edu.unmdp.entrenamiento.audio.AndroidInstrumentPlayer
import ar.edu.unmdp.entrenamiento.instruments.InstrumentSession
import ar.edu.unmdp.entrenamiento.instruments.JsonInstrumentResults
import ar.edu.unmdp.entrenamiento.ui.InstrumentViewModel
import ar.edu.unmdp.entrenamiento.ui.InstrumentApp
import ar.edu.unmdp.entrenamiento.simon.JsonSimonResults
import ar.edu.unmdp.entrenamiento.simon.SimonSession
import ar.edu.unmdp.entrenamiento.ui.SimonApp
import ar.edu.unmdp.entrenamiento.ui.SimonViewModel
import ar.edu.unmdp.entrenamiento.ui.AppScreen
import java.io.File
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var model: SimonViewModel
    private lateinit var instruments: InstrumentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        model = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(SimonViewModel::class.java))
                return SimonViewModel(
                    SimonSession({ Random.nextInt(it) }, JsonSimonResults(File(filesDir, "simon-results.json"))),
                    AndroidNotePlayer(applicationContext),
                ) as T
            }
        })[SimonViewModel::class.java]
        instruments = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(InstrumentViewModel::class.java))
                val player = AndroidInstrumentPlayer(applicationContext)
                return InstrumentViewModel(InstrumentSession({ Random.nextInt(it) }, player,
                    JsonInstrumentResults(File(filesDir, "instrument-results.json"))), player) as T
            }
        })[InstrumentViewModel::class.java]
        setContent {
            val ui by model.ui.collectAsStateWithLifecycle()
            val instrumentUi by instruments.ui.collectAsStateWithLifecycle()
            LaunchedEffect(ui.screen, instrumentUi.open) {
                requestedOrientation = if (ui.screen == AppScreen.MENU || instrumentUi.open)
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            if (instrumentUi.open) InstrumentApp(instrumentUi, instruments::selectLevel, instruments::start,
                instruments::listen, instruments::answer, instruments::next, instruments::home)
            else SimonApp(ui, model::openSimon, model::selectScale, model::start, model::answer, model::home,
                instruments::open, instrumentUi.history)
        }
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) model.resume()
        if (::instruments.isInitialized) instruments.resume()
    }

    override fun onStop() {
        // Una recreación por orientación no significa que el jugador abandonó la app.
        if (::model.isInitialized && !isChangingConfigurations) model.background()
        if (::instruments.isInitialized && !isChangingConfigurations) instruments.background()
        super.onStop()
    }
}

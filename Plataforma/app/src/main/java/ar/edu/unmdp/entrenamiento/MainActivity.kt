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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import ar.edu.unmdp.entrenamiento.voice.*
import ar.edu.unmdp.entrenamiento.history.HistorySession
import ar.edu.unmdp.entrenamiento.ui.HistoryApp
import ar.edu.unmdp.entrenamiento.ui.HistoryViewModel
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.ui.VoiceApp
import ar.edu.unmdp.entrenamiento.ui.DemonstrationApp
import ar.edu.unmdp.entrenamiento.ui.DemonstrationViewModel
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
    private lateinit var voice: VoiceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        val data = Demonstration(filesDir)
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
        voice = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                VoiceViewModel(JsonVoiceResults(File(filesDir, "voice-results.json"))) as T
        })[VoiceViewModel::class.java]
        val simonModel = model
        val instrumentModel = instruments
        val voiceModel = voice
        val demonstration = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = DemonstrationViewModel(data,
                drain = { voiceModel.stopAndJoin(); simonModel.awaitIdle(); instrumentModel.awaitIdle() },
                refresh = { simonModel.reload(); instrumentModel.reload(); voiceModel.resetDisplay() },
                helpSimulation = { instrumentModel.ui.value.simulation }) as T
        })[DemonstrationViewModel::class.java]
        val history = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = HistoryViewModel(HistorySession(
                JsonSimonResults(File(filesDir, "simon-results.json")), JsonInstrumentResults(File(filesDir, "instrument-results.json")),
                JsonVoiceResults(File(filesDir, "voice-results.json")))) as T
        })[HistoryViewModel::class.java]
        setContent {
            val ui by model.ui.collectAsStateWithLifecycle()
            val instrumentUi by instruments.ui.collectAsStateWithLifecycle()
            val voiceUi by voice.ui.collectAsStateWithLifecycle()
            val dataUi by demonstration.ui.collectAsStateWithLifecycle()
            val historyUi by history.ui.collectAsStateWithLifecycle()
            var auxiliary by rememberSaveable { mutableStateOf("") }
            LaunchedEffect(ui.screen, instrumentUi.open, auxiliary) {
                requestedOrientation = if (ui.screen == AppScreen.MENU || instrumentUi.open || auxiliary.isNotEmpty())
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            if (auxiliary == "voice") VoiceApp(voiceUi, voice) {
                voice.stop()
                auxiliary = ""
            }
            else if (auxiliary == "history") HistoryApp(historyUi, history) { auxiliary = "" }
            else if (auxiliary == "data") DemonstrationApp(dataUi, demonstration) { auxiliary = "" }
            else if (instrumentUi.open) InstrumentApp(instrumentUi, instruments::selectLevel, instruments::start,
                instruments::listen, instruments::answer, instruments::next, instruments::home,
                instruments::selectMode, instruments::openInspector, instruments::closeInspector, instruments::simulateScenario)
            else SimonApp(ui, model::openSimon, model::selectScale, model::start, model::answer, model::home,
                instruments::open, instrumentUi.history, model::selectAutomatic,
                onVoice = { auxiliary = "voice" }, onData = { auxiliary = "data" }, onHistory = { history.load(); auxiliary = "history" })
        }
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) model.resume()
        if (::instruments.isInitialized) instruments.resume()
    }

    override fun onPause() {
        if (::voice.isInitialized) voice.stop()
        super.onPause()
    }

    override fun onStop() {
        // Una recreación por orientación no significa que el jugador abandonó la app.
        if (::model.isInitialized && !isChangingConfigurations) model.background()
        if (::instruments.isInitialized && !isChangingConfigurations) instruments.background()
        super.onStop()
    }
}

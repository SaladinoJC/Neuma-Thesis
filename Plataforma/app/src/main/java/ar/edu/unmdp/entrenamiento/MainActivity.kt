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
import ar.edu.unmdp.entrenamiento.ui.PitchLabViewModel
import ar.edu.unmdp.entrenamiento.ui.PitchLabApp
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
import ar.edu.unmdp.entrenamiento.tuner.TunerViewModel
import ar.edu.unmdp.entrenamiento.tuner.JsonTunerConfiguration
import ar.edu.unmdp.entrenamiento.defender.DefenderViewModel
import ar.edu.unmdp.entrenamiento.ui.DefenderApp
import ar.edu.unmdp.entrenamiento.rhythm.RhythmViewModel
import ar.edu.unmdp.entrenamiento.ui.RhythmApp
import java.io.File
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var model: SimonViewModel
    private lateinit var instruments: InstrumentViewModel
    private lateinit var voice: VoiceViewModel
    private lateinit var pitchLab: PitchLabViewModel
    private lateinit var tuner: TunerViewModel
    private lateinit var defender: DefenderViewModel
    private lateinit var rhythm: RhythmViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        val data = Demonstration(filesDir)
        tuner = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = TunerViewModel(
                configurationStore = JsonTunerConfiguration(File(filesDir, "tuner-configuration.json"))) as T
        })[TunerViewModel::class.java]
        defender = ViewModelProvider(this)[DefenderViewModel::class.java]
        rhythm = ViewModelProvider(this)[RhythmViewModel::class.java]
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
        pitchLab = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = PitchLabViewModel(PitchLabSession(
                JsonPitchLabResults(File(filesDir, "pitch-lab-results.json")),
                environment = "Android ${android.os.Build.VERSION.SDK_INT} · ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}")) as T
        })[PitchLabViewModel::class.java]
        val labModel = pitchLab
        val simonModel = model
        val instrumentModel = instruments
        val voiceModel = voice
        val tunerModel = tuner
        val demonstration = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = DemonstrationViewModel(data,
                drain = { labModel.cancelAndJoin(); voiceModel.stopAndJoin(); tunerModel.awaitConfigurationIdle(); simonModel.awaitIdle(); instrumentModel.awaitIdle() },
                refresh = { simonModel.reload(); instrumentModel.reload(); voiceModel.resetDisplay(); labModel.resetDisplay(); tunerModel.resetDisplay() },
                helpSimulation = { instrumentModel.ui.value.simulation }, pendingVoice = voiceModel::pendingForExport,
                drainForReset = { labModel.cancelAndJoin(); voiceModel.stopAndJoin(); tunerModel.stop(); tunerModel.awaitConfigurationIdle(); simonModel.discardForReset(); instrumentModel.discardForReset() }) as T
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
            val pitchUi by pitchLab.ui.collectAsStateWithLifecycle()
            val tunerUi by tuner.ui.collectAsStateWithLifecycle()
            val defenderUi by defender.ui.collectAsStateWithLifecycle()
            val rhythmUi by rhythm.ui.collectAsStateWithLifecycle()
            var auxiliary by rememberSaveable { mutableStateOf("") }
            LaunchedEffect(ui.screen, instrumentUi.open, auxiliary) {
                requestedOrientation = if (ui.screen == AppScreen.MENU || instrumentUi.open || auxiliary.isNotEmpty())
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            if (auxiliary == "rhythm") RhythmApp(rhythmUi, rhythm) {
                rhythm.home()
                auxiliary = ""
            }
            else if (auxiliary == "defender") DefenderApp(defenderUi, defender) {
                defender.home()
                auxiliary = ""
            }
            else if (auxiliary == "tuner") ar.edu.unmdp.entrenamiento.ui.TunerApp(tunerUi, tuner) {
                tuner.stop()
                auxiliary = ""
            }
            else if (auxiliary == "voice") VoiceApp(voiceUi, voice) {
                voice.stop()
                auxiliary = ""
            }
            else if (auxiliary == "history") HistoryApp(historyUi, history) { auxiliary = "" }
            else if (auxiliary == "pitch-lab") PitchLabApp(pitchUi, pitchLab) { auxiliary = "data" }
            else if (auxiliary == "data") DemonstrationApp(dataUi, demonstration, onHome = { auxiliary = "" }, onPitchLab = { pitchLab.load(); auxiliary = "pitch-lab" })
            else if (instrumentUi.open) InstrumentApp(instrumentUi, instruments::selectLevel, instruments::start,
                instruments::listen, instruments::answer, instruments::next, instruments::home,
                instruments::selectMode, instruments::openInspector, instruments::closeInspector, instruments::simulateScenario, instruments::retryStorage)
            else SimonApp(ui, model::openSimon, model::selectScale, model::start, model::answer, model::home,
                instruments::open, instrumentUi.history, model::selectAutomatic,
                onVoice = { auxiliary = "voice" }, onData = { auxiliary = "data" }, onHistory = { history.load(); auxiliary = "history" }, onRetryStorage = model::retryStorage,
                onTuner = { auxiliary = "tuner" }, onDefender = { defender.prepare(); auxiliary = "defender" },
                onRhythm = { auxiliary = "rhythm" })
        }
    }

    override fun onStart() {
        super.onStart()
        if (::model.isInitialized) model.resume()
        if (::instruments.isInitialized) instruments.resume()
    }

    override fun onPause() {
        if (::rhythm.isInitialized) rhythm.background()
        if (::defender.isInitialized) defender.background()
        if (::tuner.isInitialized) tuner.stop()
        if (::voice.isInitialized) voice.stop()
        if (::pitchLab.isInitialized) pitchLab.cancel()
        super.onPause()
    }

    override fun onStop() {
        // Una recreación por orientación no significa que el jugador abandonó la app.
        if (::model.isInitialized && !isChangingConfigurations) model.background()
        if (::instruments.isInitialized && !isChangingConfigurations) instruments.background()
        super.onStop()
    }
}

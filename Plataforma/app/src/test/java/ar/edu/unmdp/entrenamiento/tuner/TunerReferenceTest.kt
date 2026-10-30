package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlinx.serialization.json.*

@OptIn(ExperimentalCoroutinesApi::class)
class TunerReferenceTest {
    @Test fun `cambiar timbre con guardado inmediato libera controles y conserva cuerda guiada`() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
        try {
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    awaitCancellation()
                }
            }
            val model = TunerViewModel(recorder, clock = { testScheduler.currentTime })
            model.mode(TunerMode.GUIDED); model.awaitConfigurationIdle(); model.start()
            model.referenceTimbre(ReferenceTimbre.PIANO)
            assertFalse(model.ui.value.configurationBusy)
            assertEquals(ReferenceTimbre.PIANO, model.ui.value.configuration.configuration!!.referenceTimbre)
            assertEquals(GuidedPhase.FIRST_PASS, model.ui.value.guided.phase)
            assertEquals(40, model.ui.value.tuning.targetMidi)
            assertTrue(model.ui.value.tuning.running)
            model.stop(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `referencia drena captura y descarta callbacks viejos sin reiniciar la pasada`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0; var released = 0; var played = 0
            var send: (suspend (PitchEstimate, Long, Int) -> Unit)? = null
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++; send = onMeasurement
                    try { awaitCancellation() } finally { delayRelease(); released++ }
                }
                private suspend fun delayRelease() = withContext(NonCancellable) { delay(50) }
            }
            val player = object : TunerReferencePlayer {
                override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int {
                    assertEquals(opened, released); assertEquals(45, midi)
                    played++; delay(1200); return 52920
                }
            }
            val model = TunerViewModel(recorder, referencePlayer = player, clock = { testScheduler.currentTime })
            model.mode(TunerMode.GUIDED); model.awaitConfigurationIdle()
            model.start(); runCurrent()
            repeat(11) { send!!(PitchEstimate(82.406889), 0, 44100); advanceTimeBy(100) }
            assertEquals(1, model.ui.value.guided.firstPass.size)
            val stale = send!!
            model.playReference(); runCurrent()
            assertFalse(model.ui.value.tuning.running)
            assertTrue(model.ui.value.referenceBusy)
            stale(PitchEstimate(110.0), 0, 44100)
            advanceTimeBy(1400); runCurrent()
            assertEquals(1, played); assertEquals(1, opened) // todavía en margen de salida
            assertEquals(1, model.ui.value.guided.firstPass.size)
            advanceTimeBy(500); runCurrent()
            assertEquals(2, opened)
            assertEquals(GuidedPhase.FIRST_PASS, model.ui.value.guided.phase)
            assertEquals(45, model.ui.value.tuning.targetMidi)
            stale(PitchEstimate(110.0), 0, 44100)
            assertNull(model.ui.value.tuning.frequency)
            assertEquals(0L, model.ui.value.tuning.stableMillis)
            repeat(10) { send!!(PitchEstimate(110.0), 0, 44100); advanceTimeBy(100) }
            assertEquals(1, model.ui.value.guided.firstPass.size)
            send!!(PitchEstimate(110.0), 0, 44100)
            assertEquals(2, model.ui.value.guided.firstPass.size)
            model.stop(); advanceTimeBy(60); runCurrent()
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `fallo y salida durante referencia no reabren microfono ni completan cuerda`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var opened = 0; var released = 0; var fail = false
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    opened++; awaitCancellation()
                }
            }
            val player = object : TunerReferencePlayer {
                override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int {
                    try { if (fail) error("salida no disponible"); awaitCancellation() } finally { released++ }
                }
            }
            val model = TunerViewModel(recorder, referencePlayer = player, clock = { testScheduler.currentTime })
            model.fixString(6); model.start(); runCurrent()
            model.playReference(); runCurrent(); model.playReference(); model.stop()
            advanceTimeBy(3000); runCurrent()
            assertEquals(1, released); assertEquals(1, opened)
            assertFalse(model.ui.value.referenceBusy); assertFalse(model.ui.value.tuning.running)
            fail = true; model.playReference(); runCurrent()
            assertTrue(model.ui.value.error!!.contains("salida no disponible"))
            advanceTimeBy(3000); runCurrent(); assertEquals(1, opened)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun `timbre persiste y archivos anteriores usan guitarra sin alterar afinacion`() = runTest {
        val store = MemoryTunerConfiguration()
        val session = TunerConfigurationSession(store)
        session.load(); assertEquals(ReferenceTimbre.GUITAR, session.state.configuration!!.referenceTimbre)
        session.referenceTimbre(ReferenceTimbre.PIANO)
        val reopened = TunerConfigurationSession(store); reopened.load()
        assertEquals(ReferenceTimbre.PIANO, reopened.state.configuration!!.referenceTimbre)
        assertEquals("standard", reopened.state.configuration!!.currentTuning.id)
        assertThrows(IllegalArgumentException::class.java) { tunerReferenceAsset(ReferenceTimbre.GUITAR, 35) }
        assertThrows(IllegalArgumentException::class.java) { tunerReferenceAsset(ReferenceTimbre.PIANO, 65) }
    }

    @Test fun `archivo A3 sin timbre se migra con default y guarda preferencia sin perder perfiles`() = runTest {
        val directory = kotlin.io.path.createTempDirectory("a4-config").toFile()
        try {
            val file = File(directory, "tuner-configuration.json")
            val store = JsonTunerConfiguration(file)
            val original = TunerConfiguration(mode = TunerMode.GUIDED, finalReview = false,
                guitars = listOf(GuitarProfile("default-guitar", "Mi guitarra", "drop-c")))
            store.save(original)
            val fields = Json.parseToJsonElement(file.readText()).jsonObject.toMutableMap()
            fields.remove("referenceTimbre"); file.writeText(JsonObject(fields).toString())
            assertEquals(original, store.load())
            val session = TunerConfigurationSession(store); session.load()
            session.referenceTimbre(ReferenceTimbre.PURE)
            assertEquals(original.copy(referenceTimbre = ReferenceTimbre.PURE), JsonTunerConfiguration(file).load())
        } finally { directory.deleteRecursively() }
    }

    @Test fun `sin objetivo o fuera del rango no reproduce ni cambia octava y conserva captura`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var played = 0
            var send: (suspend (PitchEstimate, Long, Int) -> Unit)? = null
            val recorder = object : VoiceCapture {
                override suspend fun capture(configuration: YinConfiguration, onMeasurement: suspend (PitchEstimate, Long, Int) -> Unit) {
                    send = onMeasurement; awaitCancellation()
                }
            }
            val player = object : TunerReferencePlayer {
                override suspend fun play(timbre: ReferenceTimbre, midi: Int): Int { played++; return 52920 }
            }
            val model = TunerViewModel(recorder, referencePlayer = player, clock = { testScheduler.currentTime })
            model.playReference(); runCurrent()
            assertTrue(model.ui.value.error!!.contains("Fijá una cuerda"))
            model.tracking(TunerTracking.CHROMATIC); model.awaitConfigurationIdle()
            model.start(); runCurrent(); send!!(PitchEstimate(440.0), 0, 44100)
            model.playReference(); runCurrent()
            assertEquals(0, played); assertEquals(69, model.ui.value.tuning.targetMidi)
            assertTrue(model.ui.value.error!!.contains("fuera de Do2–Mi4"))
            assertTrue(model.ui.value.tuning.running)
            model.stop(); runCurrent()
        } finally { Dispatchers.resetMain() }
    }
}

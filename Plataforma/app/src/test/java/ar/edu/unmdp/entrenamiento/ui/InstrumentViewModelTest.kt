package ar.edu.unmdp.entrenamiento.ui

import ar.edu.unmdp.entrenamiento.instruments.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InstrumentViewModelTest {
    @Test fun `historial ilegible bloquea partida manual hasta relectura correcta`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var broken = true
            val results = object : InstrumentResults {
                override suspend fun all(): List<InstrumentResult> { check(!broken); return emptyList() }
                override suspend fun save(result: InstrumentResult) {}
            }
            var plays = 0
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    plays++; onStarted(); return 134505
                }
            }
            val model = InstrumentViewModel(InstrumentSession({ 0 }, player, results), player)
            model.selectLevel(InstrumentLevel.A); model.start(); advanceUntilIdle()
            assertEquals(0, plays); assertNotNull(model.ui.value.storageError)
            model.home(); advanceUntilIdle(); assertFalse(model.ui.value.open)
            broken = false; model.retryStorage(); advanceUntilIdle()
            model.start(); advanceUntilIdle(); assertEquals(1, plays)
        } finally { Dispatchers.resetMain() }
    }
    @Test
    fun `inspector y simulaciones usan la configuracion efectiva de la partida activa`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val configuration = InstrumentDdaConfiguration(defaultHelpInference().copy(version = "configuracion-inyectada-test", rules = emptyList()), .4)
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    onStarted()
                    return 134_505
                }
            }
            val model = InstrumentViewModel(InstrumentSession({ 0 }, player, ddaConfiguration = configuration),
                player, StandardTestDispatcher(testScheduler))
            model.open()
            model.start()
            advanceUntilIdle()
            repeat(3) { model.answer(Instrument.PIANO); advanceUntilIdle(); model.next(); advanceUntilIdle() }
            assertNull(model.ui.value.game.result)
            assertEquals(configuration, model.ui.value.ddaConfiguration)
            model.simulateScenario(InstrumentDdaScenario.AUTONOMOUS, InstrumentLevel.B)
            advanceUntilIdle()
            val simulated = requireNotNull(model.ui.value.simulation)
            assertEquals(configuration, simulated.ddaConfiguration)
            assertEquals(DdaReason.INVALID_OUTPUT, simulated.answers[2].ddaDecision?.reason)
            assertEquals(3, simulated.answers[2].ddaDecision?.listensAfter)
            model.home()
            advanceUntilIdle()
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun `simulacion del inspector no reproduce audio ni cambia partida o historial reales`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var actualPlays = 0
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    actualPlays++
                    onStarted()
                    return 134_505
                }
            }
            val session = InstrumentSession({ 0 }, player)
            val model = InstrumentViewModel(session, player, StandardTestDispatcher(testScheduler))
            model.open()
            model.start()
            advanceUntilIdle()
            val before = model.ui.value.game
            model.openInspector()
            model.simulateScenario(InstrumentDdaScenario.RECOVERY, InstrumentLevel.C)
            advanceUntilIdle()
            assertNotNull(model.ui.value.simulation)
            val simulation = requireNotNull(model.ui.value.simulation)
            assertEquals("simulated", simulation.origin)
            assertEquals(InstrumentLevel.C, simulation.level)
            assertTrue(simulation.completed)
            assertEquals(-1, simulation.answers[2].ddaDecision?.appliedDelta)
            assertEquals(1, simulation.answers[5].ddaDecision?.appliedDelta)
            assertEquals(before, model.ui.value.game)
            assertTrue(session.history().isEmpty())
            assertEquals(1, actualPlays)
            model.closeInspector()
            model.home()
            advanceUntilIdle()
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun `volver al menu durante audio cancela y guarda una sola interrupcion`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    onStarted()
                    delay(1500)
                    return 66_150
                }
            }
            val model = InstrumentViewModel(InstrumentSession({ 0 }, player), player)
            model.open()
            model.start()
            runCurrent()
            assertEquals(1, model.ui.value.game.listensUsed)
            model.listen() // No iniciar otra escucha durante la primera.
            model.home()
            model.home()
            advanceUntilIdle()
            assertFalse(model.ui.value.open)
            assertFalse(model.ui.value.busy)
            assertEquals(InstrumentPhase.INTERRUPTED, model.ui.value.game.phase)
            assertFalse(model.ui.value.history.single().completed)
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun `timeout de audio permite reintentar y no deja bloqueada la interfaz`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    withTimeout(5) { delay(100) }
                    return 0
                }
            }
            val model = InstrumentViewModel(InstrumentSession({ 0 }, player), player)
            model.open()
            model.start()
            advanceUntilIdle()
            assertFalse(model.ui.value.busy)
            assertEquals(0, model.ui.value.game.listensUsed)
            assertNotNull(model.ui.value.error)
            model.home()
            advanceUntilIdle()
        } finally { Dispatchers.resetMain() }
    }

    @Test
    fun `salir durante guardado final conserva diez respuestas y no duplica resultado`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val allowedSave = CompletableDeferred<Unit>()
            val saved = mutableListOf<InstrumentResult>()
            val results = object : InstrumentResults {
                override suspend fun all() = saved.toList()
                override suspend fun save(result: InstrumentResult) {
                    allowedSave.await()
                    saved.add(result)
                }
            }
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, melody: InstrumentMelody, onStarted: () -> Unit): Int {
                    onStarted()
                    return 66_150
                }
            }
            val model = InstrumentViewModel(InstrumentSession({ 0 }, player, results), player)
            model.open()
            model.start()
            advanceUntilIdle()
            repeat(9) {
                model.answer(Instrument.PIANO)
                advanceUntilIdle()
                model.next()
                advanceUntilIdle()
            }
            model.answer(Instrument.PIANO)
            runCurrent()
            model.home()
            runCurrent()
            allowedSave.complete(Unit)
            advanceUntilIdle()
            assertFalse(model.ui.value.open)
            assertTrue(model.ui.value.history.single().completed)
            assertEquals(10, model.ui.value.history.single().answers.size)
        } finally { Dispatchers.resetMain() }
    }
    @Test
    fun `el historial tardio no deshace la apertura de instrumentos`() = runTest {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher(testScheduler))
        try {
            val loaded = kotlinx.coroutines.CompletableDeferred<Unit>()
            val results = object : ar.edu.unmdp.entrenamiento.instruments.InstrumentResults {
                override suspend fun all(): List<ar.edu.unmdp.entrenamiento.instruments.InstrumentResult> {
                    loaded.await(); return emptyList()
                }
                override suspend fun save(result: ar.edu.unmdp.entrenamiento.instruments.InstrumentResult) {}
            }
            val player = object : ar.edu.unmdp.entrenamiento.instruments.InstrumentPlayer {
                override suspend fun play(instrument: ar.edu.unmdp.entrenamiento.instruments.Instrument,
                    melody: ar.edu.unmdp.entrenamiento.instruments.InstrumentMelody, onStarted: () -> Unit) = 0
            }
            val model = InstrumentViewModel(ar.edu.unmdp.entrenamiento.instruments.InstrumentSession({ 0 }, player, results), player)
            runCurrent()
            model.open()
            org.junit.Assert.assertTrue(model.ui.value.open)
            loaded.complete(Unit); advanceUntilIdle()
            org.junit.Assert.assertTrue("La carga no debe cerrar la pantalla recién abierta", model.ui.value.open)
        } finally { Dispatchers.resetMain() }
    }
}

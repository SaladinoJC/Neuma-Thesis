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
    @Test
    fun `volver al menu durante audio cancela y guarda una sola interrupcion`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val player = object : InstrumentPlayer {
                override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
                override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
                override suspend fun play(instrument: Instrument, onStarted: () -> Unit): Int {
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
}

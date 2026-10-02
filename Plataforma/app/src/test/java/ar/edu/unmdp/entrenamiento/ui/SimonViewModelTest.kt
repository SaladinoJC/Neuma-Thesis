package ar.edu.unmdp.entrenamiento.ui

import ar.edu.unmdp.entrenamiento.audio.NotePlayer
import ar.edu.unmdp.entrenamiento.simon.Phase
import ar.edu.unmdp.entrenamiento.simon.SimonResult
import ar.edu.unmdp.entrenamiento.simon.SimonResults
import ar.edu.unmdp.entrenamiento.simon.SimonSession
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimonViewModelTest {
    @Test
    fun `si la preparacion de audio excede su limite la partida sale de la espera y avisa`() = runTest {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher(testScheduler))
        try {
            val player = object : NotePlayer {
                override suspend fun prepare(midiNotes: List<Int>) { withTimeout(5) { delay(100) } }
                override suspend fun play(midiNote: Int) = 26460
                override fun feedback(midiNote: Int) = 1
            }
            val model = SimonViewModel(SimonSession({ 0 }), player)
            model.start()
            advanceUntilIdle()
            assertEquals(Phase.INTERRUPTED, model.ui.value.game.phase)
            assertFalse(model.ui.value.busy)
            assertNotNull(model.ui.value.error)
        } finally {
            advanceUntilIdle()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `una rafaga termina en el primer error y guarda una sola partida`() = runTest {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher(testScheduler))
        try {
            var next = 0
            val feedback = mutableListOf<Int>()
            val player = object : NotePlayer {
                override fun feedback(midiNote: Int): Int {
                    feedback.add(midiNote)
                    return feedback.size
                }
                override suspend fun play(midiNote: Int): Int { delay(600); return 26460 }
            }
            val model = SimonViewModel(SimonSession({ next++ }), player)
            model.start()
            advanceUntilIdle()
            model.answer(0)
            advanceUntilIdle()

            model.answer(0)
            model.answer(2) // Falla en la segunda nota de la ronda de dos.
            model.answer(1) // Ya no corresponde a una partida activa.
            advanceUntilIdle()
            assertEquals(Phase.FINISHED, model.ui.value.game.phase)
            assertEquals(1, model.ui.value.game.result?.completedLength)
            assertEquals(1, model.ui.value.game.result?.errorPosition)
            assertEquals(1, model.ui.value.history.size)
            assertEquals(3, feedback.size)
        } finally {
            advanceUntilIdle()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `las respuestas rapidas cuentan sin esperar a que termine el sonido anterior`() = runTest {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher(testScheduler))
        try {
            var next = 0
            val played = mutableListOf<Int>()
            val player = object : NotePlayer {
                override fun feedback(midiNote: Int): Int {
                    played.add(midiNote)
                    return played.size
                }
                override suspend fun play(midiNote: Int): Int {
                    played.add(midiNote)
                    delay(600)
                    return 26460
                }
            }
            val model = SimonViewModel(SimonSession({ next++ }), player)
            model.start()
            advanceUntilIdle()
            model.answer(0)
            advanceUntilIdle()
            assertEquals(Phase.RESPONSE, model.ui.value.game.phase)
            assertEquals(1, model.ui.value.game.completedLength)

            model.answer(0)
            runCurrent()
            model.answer(1) // No avanzó el reloj: el sonido de 0 todavía estaría sonando.
            runCurrent()
            assertEquals(2, model.ui.value.game.completedLength)
            advanceUntilIdle()
            assertEquals(Phase.RESPONSE, model.ui.value.game.phase)
        } finally {
            advanceUntilIdle()
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `volver al menu mientras se guarda no reproduce feedback pendiente`() = runTest {
        Dispatchers.setMain(kotlinx.coroutines.test.StandardTestDispatcher(testScheduler))
        try {
            val allowSave = CompletableDeferred<Unit>()
            val saved = mutableListOf<SimonResult>()
            val results = object : SimonResults {
                override suspend fun all() = saved.toList()
                override suspend fun save(result: SimonResult) {
                    allowSave.await()
                    saved.add(result)
                }
            }
            val played = mutableListOf<Int>()
            val player = object : NotePlayer {
                override fun feedback(midiNote: Int): Int {
                    played.add(midiNote)
                    return played.size
                }
                override suspend fun play(midiNote: Int): Int {
                    played.add(midiNote)
                    delay(300)
                    return 14400
                }
            }
            val model = SimonViewModel(SimonSession({ 0 }, results), player)
            model.start()
            advanceUntilIdle()
            assertEquals(Phase.RESPONSE, model.ui.value.game.phase)
            assertEquals(1, played.size)

            model.answer(1)
            runCurrent()
            model.home()
            runCurrent()
            allowSave.complete(Unit)
            advanceUntilIdle()

            assertFalse(model.ui.value.inGame)
            assertEquals(1, model.ui.value.history.size)
            assertEquals(1, played.size)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

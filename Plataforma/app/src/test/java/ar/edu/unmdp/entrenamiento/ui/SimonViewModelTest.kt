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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SimonViewModelTest {
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

package ar.edu.unmdp.entrenamiento.defender

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefenderViewModelTest {
    @Test fun `sonidos independientes solo tras respuesta y pausa cancela sin repeticion`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val notes = mutableListOf<Int>(); val effects = mutableListOf<DefenderEffect>(); var released = 0
        val audio = object : DefenderAudio {
            override suspend fun note(midi: Int): Int { notes += midi; try { awaitCancellation() } finally { released++ } }
            override suspend fun effect(effect: DefenderEffect): Int { effects += effect; try { awaitCancellation() } finally { released++ } }
        }
        val model = DefenderViewModel(audio) { testScheduler.currentTime }
        try {
            model.noteSound(false); model.effectsSound(true); model.start(7); runCurrent()
            assertTrue(notes.isEmpty()); assertTrue(effects.isEmpty())
            model.select(requireNotNull(model.ui.value.game.enemy).note.name); model.fire(); runCurrent()
            assertTrue(notes.isEmpty()); assertEquals(listOf(DefenderEffect.HIT), effects)
            model.background(); runCurrent(); assertEquals(1, released)
            model.resume(); runCurrent(); assertEquals(1, effects.size)
            model.home(); model.prepare(); model.noteSound(true); model.effectsSound(false); model.start(7); runCurrent()
            val target = requireNotNull(model.ui.value.game.enemy).note
            model.select(target.name); model.fire(); runCurrent()
            assertEquals(listOf(target.midi), notes); assertEquals(1, effects.size)
        } finally { model.home(); runCurrent(); Dispatchers.resetMain() }
    }
    @Test fun `corregir llegada por timestamp no duplica sonido del mismo enemigo`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val sounds = mutableListOf<String>()
        val audio = object : DefenderAudio {
            override suspend fun note(midi: Int): Int { sounds += "note"; return 26460 }
            override suspend fun effect(effect: DefenderEffect): Int { sounds += effect.name; return 4410 }
        }
        var now = 0L
        val model = DefenderViewModel(audio) { now }
        try {
            model.start(7); runCurrent()
            val target = requireNotNull(model.ui.value.game.enemy).note.name
            model.select(target)
            now = 10_001; model.tick(); runCurrent()
            model.firePointer(9999, 10_001, target); runCurrent()
            assertEquals(100, model.ui.value.game.points)
            assertEquals(3, model.ui.value.game.lives)
            assertEquals(listOf("ARRIVAL"), sounds)
        } finally { model.home(); runCurrent(); Dispatchers.resetMain() }
    }
    @Test fun `preparar y pasar a segundo plano no resucita la partida anterior`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val model = DefenderViewModel(clock = { testScheduler.currentTime })
        try {
            model.start(7); runCurrent()
            model.home(); model.prepare()
            assertEquals(DefenderPhase.SETUP, model.ui.value.game.phase)
            model.background()
            assertEquals(DefenderPhase.SETUP, model.ui.value.game.phase)
            assertTrue(model.ui.value.game.results.isEmpty())
        } finally { model.home(); runCurrent(); Dispatchers.resetMain() }
    }
}

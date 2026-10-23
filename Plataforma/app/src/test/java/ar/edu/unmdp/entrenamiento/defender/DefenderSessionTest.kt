package ar.edu.unmdp.entrenamiento.defender

import org.junit.Assert.*
import org.junit.Test
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class DefenderSessionTest {
    @Test fun `fallar conserva seleccion y solo una llegada puede quitar vida`() {
        var now = 0L
        val session = DefenderSession { now }; session.start(11)
        val enemy = requireNotNull(session.state.enemy)
        val wrong = DefenderName.entries.first { it != enemy.note.name }
        session.select(wrong); session.fire()
        assertEquals(wrong, session.state.selected)
        assertEquals(3, session.state.lives)
        assertEquals(enemy.id, session.state.enemy?.id)
        assertEquals(1, session.state.failedShots)
        assertNotNull(session.state.feedback)
        now = 6000; session.tick(); assertNull(session.state.feedback)
        session.select(enemy.note.name); session.fire()
        assertEquals(100, session.state.points)
        now = 10_000; session.tick()
        assertEquals(3, session.state.lives)
        assertEquals(1, session.state.results.size)
    }
    @Test fun `pausa excluye tiempo y no acumula apariciones`() {
        var now = 0L
        val session = DefenderSession { now }; session.start(9)
        now = 7000; session.pause()
        val enemy = session.state.enemy
        now = 107_000; session.tick()
        assertEquals(7000L, session.state.activeMillis)
        assertEquals(enemy, session.state.enemy)
        session.resume(); now = 109_999; session.tick()
        assertEquals(3, session.state.lives)
        now = 110_000; session.tick(); assertEquals(2, session.state.lives)
        assertNull(session.state.enemy)
        now = 112_000; session.tick()
        assertEquals(2, session.state.events.count { it.kind == "SPAWN" })
        assertEquals(12_000L, session.state.enemy?.appearedAtMillis)
    }
    @Test fun `tick grande termina al tercer impacto y salida cancela sin sumar llegadas`() {
        var now = 0L
        val session = DefenderSession { now }; session.start(4)
        now = 100_000; session.tick()
        assertEquals(0, session.state.lives)
        assertEquals(DefenderPhase.FINISHED, session.state.phase)
        assertEquals(34_000L, session.state.activeMillis)
        assertEquals(3, session.state.results.size)
        now = 200_000; session.tick(); assertEquals(3, session.state.results.size)
        session.start(4); now += 5000; session.interrupt()
        assertEquals(DefenderPhase.INTERRUPTED, session.state.phase)
        assertEquals(3, session.state.lives)
        assertNull(session.state.enemy)
        now += 100_000; session.tick()
        assertTrue(session.state.results.isEmpty())
    }
    @Test fun `semilla eventos y resultados no dependen de cantidad de frames y serializan`() {
        var now = 0L
        val fine = DefenderSession { now }; val coarse = DefenderSession { now }
        fine.start(99); coarse.start(99)
        for (t in 0L..34_000L step 37) { now = t; fine.tick() }
        now = 34_000; fine.tick(); coarse.tick()
        assertEquals(fine.state, coarse.state)
        assertEquals(fine.state, Json.decodeFromString<DefenderState>(Json.encodeToString(fine.state)))
        assertEquals("practice", fine.state.origin)
    }
    @Test fun `corpus ofrece naturales Mi4 Fa5 y rutas superiores laterales reales`() {
        val notes = mutableSetOf<Int>(); val routes = mutableSetOf<DefenderPoint>()
        for (seed in 0..200) {
            val session = DefenderSession { 0 }; session.start(seed)
            val enemy = requireNotNull(session.state.enemy)
            notes += enemy.note.midi; routes += enemy.entry
            assertTrue(enemy.note.staffStep in 0..8)
            assertEquals(DefenderPoint(.5f, .82f), enemy.position(10_000))
        }
        assertEquals(setOf(64, 65, 67, 69, 71, 72, 74, 76, 77), notes)
        assertTrue(routes.any { it.x < .5f }); assertTrue(routes.any { it.x > .5f })
        assertTrue(routes.any { it.y > .3f }); assertTrue(routes.any { it.y < .3f })
    }
    @Test fun `curva de aparicion se acelera sin modificar viaje de visibles`() {
        var now = 0L
        val session = DefenderSession { now }; session.start(1)
        for (t in 0L..110_000L step 500) {
            now = t; session.tick()
            session.state.enemy?.let { session.select(it.note.name); session.fire() }
        }
        val spawns = session.state.events.filter { it.kind == "SPAWN" }
        assertEquals(4000L, spawns[1].atMillis - spawns[0].atMillis)
        val later = spawns.filter { it.atMillis >= 95_000 }
        assertTrue(later.size >= 3)
        assertEquals(2500L, later[1].atMillis - later[0].atMillis)
        assertTrue(spawns.all { it.enemy?.travelMillis == 10_000L })
    }
    @Test fun `fronteras fuente antes empate despues prevalecen sobre despacho tardio`() {
        for (offset in listOf(-1L, 0L, 1L)) {
            var now = 0L
            val session = DefenderSession { now }; session.start(7)
            val enemy = requireNotNull(session.state.enemy)
            session.select(enemy.note.name)
            now = 10_050; session.tick()
            session.fireAt(10_000 + offset, enemy.note.name)
            assertEquals(if (offset < 0) 3 else 2, session.state.lives)
            assertEquals(if (offset < 0) 100 else 0, session.state.points)
        }
    }
    @Test fun `evento antes del deadline recibido tras el tick conserva acierto sin duplicarlo`() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(7)
        val enemy = requireNotNull(session.state.enemy)
        session.select(enemy.note.name)
        now = 10_001; session.tick()
        session.fireAt(9_999, enemy.note.name)
        assertEquals(3, session.state.lives)
        assertEquals(100, session.state.points)
        assertEquals(listOf(DefenderResolution.ELIMINATED), session.state.results.map { it.resolution })
        session.fireAt(9_999, enemy.note.name)
        assertEquals(100, session.state.points)
        assertEquals(1, session.state.results.size)
    }
    @Test fun `disparo correcto anterior al deadline elimina y empate cuesta vida`() {
        var now = 0L
        val session = DefenderSession { now }
        session.start(seed = 7)
        val first = requireNotNull(session.state.enemy)
        session.select(first.note.name)
        now = 9_999
        session.fire()
        assertEquals(100, session.state.points)
        assertEquals(3, session.state.lives)
        assertNull(session.state.selected)
        assertNull(session.state.enemy)
        now = 12_000; session.tick()
        val second = requireNotNull(session.state.enemy)
        session.select(second.note.name)
        now = second.deadlineMillis; session.fire()
        assertEquals(100, session.state.points)
        assertEquals(2, session.state.lives)
        assertEquals(DefenderResolution.ARRIVED, session.state.results.last().resolution)
    }
}

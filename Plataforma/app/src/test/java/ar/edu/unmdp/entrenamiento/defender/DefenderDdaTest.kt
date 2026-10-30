package ar.edu.unmdp.entrenamiento.defender

import org.junit.Assert.*
import org.junit.Test

internal class DefenderHarness(val stage: DefenderStage = DefenderStage.N1,
    val origin: String = "progression", val policy: DefenderDdaConfiguration = DefenderDdaConfiguration(),
    val travel: Long = 10_000, val seed: Int = 42, val maxEnemies: Int = 4,
    val variant: String = "notes", val quality: DefenderChordQuality = DefenderChordQuality.MAJOR,
    val inversion: Int = 0, val clefs: DefenderChordClefs = DefenderChordClefs.TREBLE) {
    var now = 0L
    val session = DefenderSession { now }
    init { session.start(seed, DefenderConfiguration(stage = stage, maxEnemies = maxEnemies,
        travelMillis = travel, dda = policy, variant = variant, chordQuality = quality,
        chordInversion = inversion, chordClefs = clefs), origin) }
    fun hit(reaction: Long = 100) {
        var attempts = 0
        while (session.state.enemies.isEmpty() && attempts++ < 1000) {
            now += 100; session.tick()
        }
        val enemy = session.state.enemies.minBy { it.appearedAtMillis }
        now = maxOf(now + 1, enemy.appearedAtMillis + reaction)
        session.select(enemy.note.name)
        session.accidental(enemy.note.accidental)
        enemy.chord?.let { session.quality(it.quality) }
        session.fire()
    }
    fun finish(hits: Int = 24): DefenderState {
        repeat(hits) { hit() }
        repeat(2000) {
            if (session.state.phase != DefenderPhase.RUNNING) return session.state
            now += 100; session.tick()
        }
        error("La partida no terminó")
    }
}

class DefenderDdaTest {
    @Test fun aceleraDesdeQuintaResolucionConEnfriamientoDeTresYLimiteSeis() {
        val h = DefenderHarness()
        repeat(4) { h.hit() }
        assertEquals(10_000L, h.session.state.currentTravelMillis)
        h.hit()
        assertEquals(9_000L, h.session.state.currentTravelMillis)
        assertEquals(5, h.session.state.ddaDecisions.last().afterResolved)
        repeat(2) { h.hit() }
        assertEquals(9_000L, h.session.state.currentTravelMillis)
        assertEquals(DefenderDdaReason.COOLDOWN, h.session.state.ddaDecisions.last().reason)
        h.hit()
        assertEquals(8_000L, h.session.state.currentTravelMillis)
        repeat(15) { h.hit() }
        assertEquals(6_000L, h.session.state.currentTravelMillis)
        assertTrue(h.session.state.ddaDecisions.any { it.reason == DefenderDdaReason.LIMIT })
        assertTrue(h.session.state.ddaDecisions.all { kotlin.math.abs(it.appliedDeltaMillis) <= 1000 })
    }

    @Test fun vivosConservanViajeYDeadlineCuandoAjustaElMotor() {
        val h = DefenderHarness()
        repeat(4) { h.hit() }
        while (h.session.state.enemies.isEmpty()) { h.now += 100; h.session.tick() }
        h.now += 4000; h.session.tick()
        val before = h.session.state.enemies.associateBy { it.id }
        h.hit()
        assertEquals(9_000L, h.session.state.currentTravelMillis)
        assertTrue(h.session.state.enemies.isNotEmpty())
        h.session.state.enemies.filter { it.id in before }.forEach {
            assertEquals(before.getValue(it.id).travelMillis, it.travelMillis)
            assertEquals(before.getValue(it.id).deadlineMillis, it.deadlineMillis)
        }
    }

    @Test fun practicaConViajeManualInspeccionaSinAplicarYReiniciaPresionEnNuevaPartida() {
        val h = DefenderHarness(origin = "practice", travel = 12_000)
        repeat(8) { h.hit() }
        assertEquals(12_000L, h.session.state.currentTravelMillis)
        assertTrue(h.session.state.ddaDecisions.last().inference!!.centroid!! < -.25)
        assertEquals(DefenderDdaReason.MANUAL, h.session.state.ddaDecisions.last().reason)
        h.session.interrupt(); h.session.start(2, origin = "progression")
        assertEquals(10_000L, h.session.state.currentTravelMillis)
    }

    @Test fun sinDisparosNoDesaparecenPerdidasNiSeInventaPrecision() {
        val h = DefenderHarness()
        val game = h.finish(hits = 0)
        assertEquals(3, game.results.size)
        assertEquals(DefenderPhase.FINISHED, game.phase)
        val decision = game.ddaDecisions.last()
        assertEquals(0, decision.attempts)
        assertEquals(0.0, decision.precision, 0.0)
        assertEquals(0.0, decision.survival, 0.0)
        assertEquals(0.0, decision.rapidity, 0.0)
        assertEquals(DefenderDdaReason.INSUFFICIENT, decision.reason)
    }

    @Test fun ningunaReglaValidaMantieneViajeYExplicaFallo() {
        val policy = DefenderDdaConfiguration(inference = defenderTravelInference().copy(rules = emptyList()))
        val h = DefenderHarness(policy = policy)
        repeat(5) { h.hit() }
        assertEquals(10_000L, h.session.state.currentTravelMillis)
        assertNull(h.session.state.ddaDecisions.last().inference!!.centroid)
        assertEquals(DefenderDdaReason.INVALID_OUTPUT, h.session.state.ddaDecisions.last().reason)
    }

    @Test fun fallosGlobalesAfectanPrecisionAunqueTodosLosEnemigosSeEliminen() {
        val h = DefenderHarness()
        repeat(5) {
            while (h.session.state.enemies.isEmpty()) { h.now += 100; h.session.tick() }
            val wrong = DefenderName.entries.first { name -> h.session.state.enemies.none { it.note.name == name } }
            repeat(5) { h.now++; h.session.select(wrong); h.session.fire() }
            h.hit()
        }
        val decision = h.session.state.ddaDecisions.last()
        assertEquals(5, decision.correctShots)
        assertEquals(25, decision.failedShots)
        assertEquals(5.0 / 30, decision.precision, .000001)
        assertEquals(1.0, decision.survival, 0.0)
        assertEquals(11_000L, h.session.state.currentTravelMillis)
        assertEquals(3, h.session.state.lives)
    }

    @Test fun tresPerdidasEnVentanaReducenRapidezYSupervivenciaAunqueNoHayaFallosDeRespuesta() {
        val game = DefenderHarness().finish(hits = 2)
        val decision = game.ddaDecisions.last()
        assertEquals(5, decision.afterResolved)
        assertEquals(.4, decision.survival, .00001)
        assertTrue(decision.rapidity < .4)
        assertEquals(1.0, decision.precision, 0.0)
        assertEquals(11_000L, decision.afterTravelMillis)
    }
}

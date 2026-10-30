package ar.edu.unmdp.entrenamiento.rhythm

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport

class RhythmProgressionTest {
    private fun exercise(seed: Long, categories: Set<RhythmCategory> = setOf(RhythmCategory.QUARTER),
        bpm: Int = 60, perfect: Boolean = true, origin: String = "progression", metronome: Boolean = false): RhythmResult {
        val session = RhythmSession().apply { generate(categories, seed) }
        session.preview(session.state.score!!.copy(bpm = bpm), origin = origin)
        session.play(0, metronome = metronome)
        if (perfect) session.state.attacks.forEach { session.tap(session.executionStartNanos!! + it.offsetNanos) }
        session.advance(session.executionStartNanos!! + Beat(16).nanos(bpm) + 1)
        return session.state.result!!
    }
    private fun record(id: String, result: RhythmResult) = RhythmRecord(id, 1, 2, result)

    @Test fun dosEjerciciosNuevosSinMetronomoSubenCincoYDesbloqueanAntesDeReiniciarRacha() = runBlocking {
        val history = RhythmHistorySession(MemoryRhythmResults(), newId = { "unused" })
        val store = MemoryRhythmResults()
        assertEquals(setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS), history.state.progress.unlocked)
        val first = store.commit(record("a", exercise(1)))
        assertEquals(1, first.dda!!.after[RhythmCategory.QUARTER].highStreak)
        assertTrue(first.dda.decisions.single().inference!!.centroid!! >= .75)
        val second = store.commit(record("b", exercise(2)))
        val progress = second.dda!!.after
        assertEquals(65, progress[RhythmCategory.QUARTER].bpm)
        assertEquals(0, progress[RhythmCategory.QUARTER].highStreak)
        assertTrue(progress[RhythmCategory.QUARTER].mastered)
        assertEquals(listOf("a", "b"), progress[RhythmCategory.QUARTER].masteryEvidence)
        assertTrue(progress.unlocked.containsAll(listOf(RhythmCategory.HALF, RhythmCategory.QUARTER_REST)))
        assertEquals(60, progress.tempo(setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS)))
    }

    @Test fun practicaRepetidaContenidoIdenticoYOmisionesNoRegalanProgreso() = runBlocking {
        val store = MemoryRhythmResults()
        val result = exercise(4)
        store.commit(record("a", result))
        val duplicate = store.commit(record("b", result.copy(score = result.score.copy(seed = 999))))
        assertEquals("practice", duplicate.result.origin)
        assertEquals(1, duplicate.dda!!.after[RhythmCategory.QUARTER].highStreak)
        store.commit(record("p", exercise(5, origin = "practice")))
        assertEquals(duplicate.dda.after, rhythmProgress(store.all()))
        val failed = store.commit(record("f", exercise(6, perfect = false)))
        assertEquals(0, failed.dda!!.after[RhythmCategory.QUARTER].highStreak)
        assertEquals(60, failed.dda.after[RhythmCategory.QUARTER].bpm)
    }

    @Test fun mezclaNoElevaCategoriaMasRapidaYConexionNoEsEvidencia() = runBlocking {
        val store = MemoryRhythmResults()
        store.commit(record("a", exercise(1)))
        store.commit(record("b", exercise(2)))
        val mixed = store.commit(record("c", exercise(3, setOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS))))
        val decisions = mixed.dda!!.decisions.associateBy { it.category }
        assertEquals(RhythmDdaReason.LOWER_TEMPO, decisions.getValue(RhythmCategory.QUARTER).reason)
        assertEquals(0, mixed.dda.after[RhythmCategory.QUARTER].highStreak)
        assertEquals(1, mixed.dda.after[RhythmCategory.EIGHTHS].highStreak)
        val own = mixed.result.score.occurrences.filter { it.target && it.category == RhythmCategory.QUARTER }.map { it.index }
        assertEquals(mixed.result.attacks.count { it.occurrenceIndex in own }, decisions.getValue(RhythmCategory.QUARTER).units)
    }

    @Test fun jsonReabreYReintentoODobleGuardadoNoDuplicanAvance() = runBlocking {
        val file = Files.createTempDirectory("rhythm-r6").resolve("rhythm-results.json").toFile()
        val store = JsonRhythmResults(file)
        val a = record("a", exercise(1)); val b = record("b", exercise(2))
        store.commit(a); val saved = store.commit(b)
        assertEquals(saved, store.commit(b))
        assertEquals(65, JsonRhythmResults(file).all().let(::rhythmProgress)[RhythmCategory.QUARTER].bpm)
        assertEquals(2, store.all().size)
        val corrected = store.commit(b.copy(revision = 2))
        assertEquals(65, corrected.dda!!.after[RhythmCategory.QUARTER].bpm)
        assertEquals(0, corrected.dda.after[RhythmCategory.QUARTER].highStreak)
    }

    @Test fun grafoTieneTodasLasCategoriasAlcanzablesYSinCiclos() {
        val reached = mutableSetOf(RhythmCategory.QUARTER, RhythmCategory.EIGHTHS)
        repeat(RhythmCategory.entries.size) {
            rhythmPrerequisites.filterValues { reached.containsAll(it) }.keys.forEach { reached += it }
        }
        assertEquals(RhythmCategory.entries.toSet(), reached)
        fun visit(c: RhythmCategory, path: Set<RhythmCategory>) {
            assertFalse("Ciclo en ${c.code}", c in path)
            rhythmPrerequisites[c].orEmpty().forEach { visit(it, path + c) }
        }
        RhythmCategory.entries.forEach { visit(it, emptySet()) }
    }

    @Test fun silencioSoloSumaCuandoSeRespetaYUnExtraNoSeAtribuyeADosApariciones() = runBlocking {
        val store = MemoryRhythmResults()
        store.commit(record("a", exercise(1))); store.commit(record("b", exercise(2)))
        val session = RhythmSession().apply { generate(setOf(RhythmCategory.QUARTER_REST), 11); preview(state.score!!, origin = "progression"); play(0) }
        session.state.attacks.forEach { session.tap(session.executionStartNanos!! + it.offsetNanos) }
        val rest = session.state.score!!.occurrences.first { it.target }
        val extra = (Beat(rest.bar * 4) + rest.beat).nanos(60) + 350_000_000L
        session.tap(session.executionStartNanos!! + extra)
        session.advance(session.executionStartNanos!! + Beat(16).nanos(60) + 1)
        val saved = store.commit(record("rests", session.state.result!!))
        val decision = saved.dda!!.decisions.single()
        assertEquals(1, decision.extras)
        assertEquals(decision.units - 1, decision.successes)
        assertEquals(session.state.result!!.silences.count { it.occurrenceIndex in session.state.score!!.occurrences.filter { cell -> cell.target }.map { cell -> cell.index } }, decision.units)
        assertEquals(1, saved.result.silences.count { it.respected == false })
    }

    @Test fun revisionesTardiasNoDuplicanAumentosNiRetiranUnDesbloqueo() = runBlocking {
        val store = MemoryRhythmResults()
        store.commit(record("a", exercise(1)))
        val b = store.commit(record("b", exercise(2)))
        val revised = store.commit(b.copy(revision = 2, result = exercise(2, perfect = false)))
        assertEquals(65, revised.dda!!.after[RhythmCategory.QUARTER].bpm)
        store.commit(record("c", exercise(3, bpm = 65, perfect = false)))
        val late = store.commit(b.copy(revision = 3))
        assertEquals(2, late.dda!!.evaluatedRevision)
        assertEquals(65, rhythmProgress(store.all())[RhythmCategory.QUARTER].bpm)
        assertTrue(RhythmCategory.HALF in rhythmProgress(store.all()).unlocked)
    }

    @Test fun bancoSimuladoEsAisladoYCompruebaInsuficienciaTechoRepeticionYFallo() = runBlocking {
        val live = MemoryRhythmResults()
        val insufficient = simulateRhythmDda(RhythmDdaScenario.INSUFFICIENT)
        assertEquals(RhythmDdaReason.INSUFFICIENT, insufficient.records.single().dda!!.decisions.single().reason)
        val ceiling = simulateRhythmDda(RhythmDdaScenario.CEILING)
        assertEquals(120, ceiling.progress[RhythmCategory.QUARTER].bpm)
        assertEquals(RhythmDdaReason.CEILING, ceiling.records.last().dda!!.decisions.single().reason)
        val repeated = simulateRhythmDda(RhythmDdaScenario.REPEATED)
        assertEquals(1, repeated.progress[RhythmCategory.QUARTER].highStreak)
        val failed = simulateRhythmDda(RhythmDdaScenario.FAILURE)
        assertEquals(1, failed.records.size)
        assertEquals(1, failed.progress[RhythmCategory.QUARTER].highStreak)
        assertTrue(live.all().isEmpty())
        val all = RhythmDdaScenario.entries.map { simulateRhythmDda(it) }
        val output = java.io.File("build/verification/rhythm-r6/scenarios.json")
        output.parentFile!!.mkdirs()
        output.writeText(Json.encodeToString(kotlinx.serialization.builtins.ListSerializer(RhythmDdaSimulation.serializer()), all))
    }

    @Test fun guardadosConcurrentesExportacionYReinicioConservanUnaSolaSecuenciaCoherente() = runBlocking {
        val directory = Files.createTempDirectory("rhythm-r6-export").toFile()
        val file = directory.resolve("rhythm-results.json")
        val a = async { JsonRhythmResults(file).commit(record("a", exercise(1))) }
        val b = async { JsonRhythmResults(file).commit(record("b", exercise(2))) }
        a.await(); b.await()
        val store = JsonRhythmResults(file)
        assertEquals(setOf(1L, 2L), store.all().map { it.dda!!.sequence }.toSet())
        val data = Demonstration(directory)
        val export = Json.decodeFromString(DemonstrationExport.serializer(), data.export("test"))
        assertEquals(rhythmProgress(store.all()), export.rhythmProgress)
        assertEquals(65, export.rhythmProgress!![RhythmCategory.QUARTER].bpm)
        data.reset()
        assertEquals(RhythmProgress(), rhythmProgress(store.all()))
    }
}

package ar.edu.unmdp.entrenamiento.defender

import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class DefenderD5ProgressTest {
    private val json = Json { encodeDefaults = true }
    private fun game(id: String, target: DefenderChordTarget) = DefenderRecord(id, 1, 2,
        DefenderHarness(variant = "chords", quality = target.quality, inversion = target.inversion,
            clefs = target.clefs, maxEnemies = 1).finish())
    private suspend fun sol(store: DefenderResults) {
        for (q in DefenderChordQuality.entries) repeat(2) { store.commit(game("sol-$q-$it", DefenderChordTarget(q))) }
    }

    @Test fun grafoSinCiclosRutaPublicaAlcanzaOchentaYCuatroObjetivosConEvidenciaPropia() = runBlocking {
        val pending = defenderChordTargets().toMutableSet(); val reached = mutableSetOf<DefenderChordTarget>()
        while (pending.isNotEmpty()) {
            val next = pending.firstOrNull { reached.containsAll(it.prerequisites) }
            assertNotNull("Grafo con ciclo o dependencia ausente", next)
            reached += next!!; pending -= next
        }
        val route = simulateDefender(DefenderScenario.CHORD_ROUTE, "chords")
        assertEquals(168, route.records.size)
        assertEquals(defenderChordTargets().toSet(), route.progress.unlockedTargets)
        assertTrue(defenderChordTargets().all { route.progress[it].mastered })
        assertEquals(setOf(DefenderStage.N1), defenderProgress(route.records).unlocked)
        assertTrue(route.records.all { r -> r.progress!!.evidence.drop(1).all { it.resolved >= 3 && it.mastery == "alto" } })
        val summary = buildJsonObject {
            put("origin", "simulated"); put("targets", 84); put("games", route.records.size)
            put("records", buildJsonArray { route.records.forEach { r -> add(buildJsonObject {
                put("id", r.id); put("target", r.game.configuration.chordTarget.key)
                put("reason", r.progress!!.reason.name)
                put("evidence", buildJsonArray { r.progress.evidence.forEach { e -> add(buildJsonObject {
                    put("clef", e.clef?.name ?: "global"); put("resolved", e.resolved); put("eliminated", e.eliminated)
                    put("survival", e.survival); put("precision", e.precision); put("centroid", e.inference!!.centroid!!)
                }) } })
            }) } })
        }
        val out = java.io.File("build/verification/defender-d5/route-summary.json")
        out.parentFile!!.mkdirs(); out.writeText(summary.toString())
    }

    @Test fun perfilD4ConservaSoloDominioFundamentalEnSolYContinuaLaCadena() = runBlocking {
        val route = simulateDefender(DefenderScenario.MIXED, "chords")
        fun old(element: JsonElement): JsonElement = when (element) {
            is JsonArray -> JsonArray(element.map(::old))
            is JsonObject -> JsonObject(element.filterKeys { it !in setOf("chordContents", "chordInversion", "chordClefs", "newlyUnlockedTargets") }
                .mapValues { (k, v) -> if (k == "version" && v is JsonPrimitive) when (v.content) {
                    "defender-d5-v1" -> JsonPrimitive("defender-d4-v1")
                    "defender-chords-v2" -> JsonPrimitive("defender-triads-v1")
                    else -> v
                } else old(v) })
            else -> element
        }
        val dir = Files.createTempDirectory("defender-d5-legacy").toFile()
        dir.resolve("defender-results.json").writeText(buildJsonObject {
            put("schemaVersion", 1); put("results", old(json.encodeToJsonElement(ListSerializer(DefenderRecord.serializer()), route.records)))
        }.toString())
        val store = JsonDefenderResults(dir.resolve("defender-results.json"))
        val before = defenderProgress(store.all(), "chords")
        assertEquals(5, before.unlockedTargets.size)
        assertTrue(DefenderChordQuality.entries.take(4).all { before[it].mastered })
        assertTrue(before.unlockedTargets.all { it.inversion == 0 && it.clefs == DefenderChordClefs.TREBLE })
        val dominant = DefenderChordTarget(DefenderChordQuality.DOMINANT7)
        store.commit(game("new-1", dominant)); store.commit(game("new-2", dominant))
        val after = defenderProgress(store.all(), "chords")
        assertTrue(after[dominant].mastered); assertTrue(DefenderChordTarget(DefenderChordQuality.MAJOR7) in after.unlockedTargets)
        assertEquals(before[DefenderChordQuality.MAJOR], after[DefenderChordQuality.MAJOR])
        assertEquals("defender-d4-v1", store.all().first().game.configuration.version)
    }

    @Test fun inversionAjenaYMezclaSinFaSonInsuficientesSinCompartirRachas() = runBlocking {
        val store = MemoryDefenderResults(); sol(store)
        val first = DefenderChordTarget(DefenderChordQuality.MAJOR, 1)
        val root = game("wrong-inversion", first.copy(inversion = 0))
        val wrong = store.commit(root.copy(game = root.game.copy(configuration = root.game.configuration.copy(chordInversion = 1))))
        assertEquals(DefenderProgressReason.INSUFFICIENT, wrong.progress!!.reason)
        assertEquals(0, wrong.progress.after[first].highStreak)
        repeat(2) { store.commit(game("inv1-$it", first)) }
        val progress = defenderProgress(store.all(), "chords")
        assertTrue(first.copy(inversion = 2) in progress.unlockedTargets)
        assertFalse(progress[first.copy(clefs = DefenderChordClefs.BASS)].mastered)
        assertFalse(progress[first.copy(quality = DefenderChordQuality.MINOR)].mastered)
        assertFalse(DefenderChordTarget(DefenderChordQuality.MAJOR, clefs = DefenderChordClefs.MIXED) in progress.unlockedTargets)
        for (q in DefenderChordQuality.entries) repeat(2) {
            store.commit(game("fa-$q-$it", DefenderChordTarget(q, clefs = DefenderChordClefs.BASS)))
        }
        val mixed = game("missing-fa", DefenderChordTarget(DefenderChordQuality.MAJOR))
        val saved = store.commit(mixed.copy(game = mixed.game.copy(configuration = mixed.game.configuration.copy(chordClefs = DefenderChordClefs.MIXED))))
        assertEquals(DefenderProgressReason.INSUFFICIENT, saved.progress!!.reason)
        assertEquals(0, saved.progress.evidence.single { it.clef == DefenderClef.BASS }.resolved)
        assertEquals(0, saved.progress.after[DefenderChordTarget(DefenderChordQuality.MAJOR, clefs = DefenderChordClefs.MIXED)].highStreak)
    }

    @Test fun septimaInvertidaEnFaReabreExportaYReintentaSinCambiarNotas() = runBlocking {
        val memory = MemoryDefenderResults(); sol(memory)
        val quality = DefenderChordQuality.DOMINANT7
        for (inversion in 0..2) repeat(2) {
            memory.commit(game("fa-$inversion-$it", DefenderChordTarget(quality, inversion, DefenderChordClefs.BASS)))
        }
        val dir = Files.createTempDirectory("defender-d5-json").toFile()
        dir.resolve("defender-results.json").writeText(buildJsonObject {
            put("schemaVersion", 1); put("results", json.encodeToJsonElement(ListSerializer(DefenderRecord.serializer()), memory.all()))
        }.toString())
        val store = JsonDefenderResults(dir.resolve("defender-results.json"))
        val target = DefenderChordTarget(quality, 3, DefenderChordClefs.BASS)
        val input = game("third-inversion", target)
        val saved = store.commit(input); assertEquals(saved, store.commit(input))
        val reopened = JsonDefenderResults(dir.resolve("defender-results.json"))
        val last = reopened.all().last()
        assertEquals(target, last.game.configuration.chordTarget)
        assertTrue(last.game.results.all { it.enemy.chord!!.notes.size == 4 && it.enemy.chord.inversion == 3 })
        val exported = json.decodeFromString(DemonstrationExport.serializer(), Demonstration(dir).export("0.12.15"))
        assertEquals(1, exported.defenderProgressByVariant.getValue("chords")[target].highStreak)
        assertEquals(setOf(DefenderStage.N1), exported.defenderProgress!!.unlocked)
        assertEquals(reopened.all(), exported.defender)
    }
}

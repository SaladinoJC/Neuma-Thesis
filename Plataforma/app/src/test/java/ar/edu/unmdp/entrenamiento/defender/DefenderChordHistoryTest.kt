package ar.edu.unmdp.entrenamiento.defender

import ar.edu.unmdp.entrenamiento.storage.Demonstration
import ar.edu.unmdp.entrenamiento.storage.DemonstrationExport
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class DefenderChordHistoryTest {
    private val withDefaults = Json { encodeDefaults = true }
    private fun chord(id: String, quality: DefenderChordQuality = DefenderChordQuality.MAJOR, origin: String = "progression") =
        DefenderRecord(id, 1, 2, DefenderHarness(variant = "chords", quality = quality, origin = origin).finish())

    @Test fun notasNoHabilitanCalidadesYDosAltasDeUnaCalidadNoAcreditanOtra() = runBlocking {
        val store = MemoryDefenderResults()
        for (id in listOf("n1", "n2")) store.commit(DefenderRecord(id, 1, 2, DefenderHarness().finish()))
        assertTrue(DefenderStage.N2 in defenderProgress(store.all()).unlocked)
        assertEquals(setOf(DefenderChordQuality.MAJOR), defenderProgress(store.all(), "chords").unlockedQualities)
        val locked = store.commit(chord("locked", DefenderChordQuality.MINOR))
        assertEquals(DefenderProgressReason.LOCKED, locked.progress!!.reason)
        store.commit(chord("c1")); val second = store.commit(chord("c2"))
        assertEquals(setOf(DefenderChordQuality.MAJOR, DefenderChordQuality.MINOR), second.progress!!.after.unlockedQualities)
        assertFalse(second.progress.after[DefenderChordQuality.MINOR].mastered)
        val noteProgress = defenderProgress(store.all())
        store.commit(chord("practice", DefenderChordQuality.AUGMENTED, "practice"))
        assertEquals(noteProgress, defenderProgress(store.all()))
        val major = chord("wrong-content")
        val synthetic = major.copy(game = major.game.copy(configuration = major.game.configuration.copy(chordQuality = DefenderChordQuality.MINOR)))
        val missing = store.commit(synthetic)
        assertEquals(DefenderProgressReason.INSUFFICIENT, missing.progress!!.reason)
        assertEquals(0, missing.progress.after[DefenderChordQuality.MINOR].highStreak)
    }

    @Test fun rutaCompletaDeTriadasEsSimuladaYNoConcedeEtapasDeNotas() = runBlocking {
        val report = simulateDefender(DefenderScenario.MIXED, "chords")
        assertEquals(8, report.records.size)
        assertTrue(report.progress.unlockedQualities.containsAll(DefenderChordQuality.entries.take(4)))
        assertTrue(DefenderChordQuality.entries.take(4).all { report.progress[it].mastered })
        assertFalse(report.progress[DefenderChordQuality.DOMINANT7].mastered)
        assertTrue(report.progress.stages.isEmpty())
        assertEquals(setOf(DefenderStage.N1), defenderProgress(report.records).unlocked)
        val dir = java.io.File("build/verification/defender-d4").apply { mkdirs() }
        java.io.File(dir, "route.json").writeText(Json.encodeToString(DefenderSimulation.serializer(), report))
    }

    @Test fun jsonReabreAmbasVariantesYExportaAvanceIndependienteConReintentoIdempotente() = runBlocking {
        val dir = Files.createTempDirectory("defender-d4").toFile(); val store = JsonDefenderResults(dir.resolve("defender-results.json"))
        val note = DefenderRecord("note", 1, 2, DefenderHarness().finish())
        store.commit(note); store.commit(chord("c1")); val raw = chord("c2")
        val saved = store.commit(raw); assertEquals(saved, store.commit(raw))
        val reopened = JsonDefenderResults(dir.resolve("defender-results.json"))
        assertEquals(3, reopened.all().size)
        val history = DefenderHistoryState(results = reopened.all())
        assertEquals(2, history.copy(filter = DefenderHistoryFilter(variant = "chords", quality = DefenderChordQuality.MAJOR)).entries.size)
        assertEquals(1, history.copy(filter = DefenderHistoryFilter(variant = "notes", stage = DefenderStage.N1)).entries.size)
        assertTrue(history.copy(filter = DefenderHistoryFilter(variant = "chords", quality = DefenderChordQuality.MINOR)).entries.isEmpty())
        val export = Json.decodeFromString(DemonstrationExport.serializer(), Demonstration(dir).export("test"))
        assertEquals(reopened.all(), export.defender)
        assertEquals(1, export.defenderProgressByVariant.getValue("notes")[DefenderStage.N1].highStreak)
        assertTrue(DefenderChordQuality.MINOR in export.defenderProgressByVariant.getValue("chords").unlockedQualities)
        val original = dir.resolve("defender-results.json").readBytes()
        val output = java.io.ByteArrayOutputStream(); Demonstration(dir).writeRecovery(output)
        java.util.zip.ZipInputStream(output.toByteArray().inputStream()).use { zip ->
            assertEquals("defender-results.json", zip.nextEntry.name); assertArrayEquals(original, zip.readBytes())
        }
        var rejected = false
        try { store.commit(DefenderRecord("note", 1, 2, chord("other").game, revision = 2)) }
        catch (expected: IllegalStateException) { rejected = true }
        assertTrue(rejected); assertArrayEquals(original, dir.resolve("defender-results.json").readBytes())
        Demonstration(dir).reset(); assertTrue(reopened.all().isEmpty())
    }

    @Test fun documentoD3SinCamposNuevosSeLeeComoNotasSinAcreditarAcordes() = runBlocking {
        val dir = Files.createTempDirectory("defender-d3-legacy").toFile()
        val record = DefenderRecord("legacy", 1, 2, DefenderHarness().finish())
        fun strip(element: JsonElement): JsonElement = when (element) {
            is JsonArray -> JsonArray(element.map(::strip))
            is JsonObject -> JsonObject(element.filterKeys { it !in setOf("chord", "chordQuality", "qualities", "newlyUnlockedQualities", "selectedQuality", "quality",
                "chordContents", "chordInversion", "chordClefs", "newlyUnlockedTargets", "inversion", "spawnIntervalMillis") }
                .filterKeys { it != "variant" }.mapValues { (key, value) ->
                    if (key == "minimumTravelMillis") JsonPrimitive(6000)
                    else if (key == "version" && value is JsonPrimitive && value.content in setOf("defender-d4-v1", "defender-d5-v1", "defender-pressure-v2")) JsonPrimitive("defender-d3-v1") else strip(value)
                })
            else -> element
        }
        val encoded = withDefaults.encodeToJsonElement(DefenderRecord.serializer(), record)
        val document = JsonObject(mapOf("schemaVersion" to JsonPrimitive(1), "results" to JsonArray(listOf(strip(encoded)))))
        dir.resolve("defender-results.json").writeText(document.toString())
        val store = JsonDefenderResults(dir.resolve("defender-results.json"))
        val old = store.all().single()
        assertEquals("notes", old.variant)
        assertEquals("defender-d3-v1", old.game.configuration.version)
        assertEquals(6000L, old.game.configuration.dda.minimumTravelMillis)
        assertNull(old.game.configuration.spawnIntervalMillis)
        assertTrue(old.game.results.all { it.enemy.chord == null })
        assertEquals(setOf(DefenderChordQuality.MAJOR), defenderProgress(store.all(), "chords").unlockedQualities)
    }
}

package ar.edu.unmdp.entrenamiento.storage

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import ar.edu.unmdp.entrenamiento.simon.*

class DemonstrationTest {
    @get:Rule val temporary = TemporaryFolder()
    @Test fun `exportar conserva resultados y reiniciar deja perfil vacio al reabrir`() = runTest {
        val demonstration = Demonstration(temporary.root)
        val session = SimonSession({ 0 }, JsonSimonResults(java.io.File(temporary.root, "simon-results.json")))
        session.startAutomatic(60); session.playbackFinished(); session.answer(1)
        val exported = Json.parseToJsonElement(demonstration.export("test")).jsonObject
        assertEquals(1, exported.getValue("schemaVersion").jsonPrimitive.int)
        assertEquals(1, exported.getValue("simon").jsonArray.size)
        assertEquals(1, session.history().size)
        demonstration.reset()
        val reopened = Demonstration(temporary.root)
        assertEquals(0, Json.parseToJsonElement(reopened.export("test")).jsonObject.getValue("simon").jsonArray.size)
        assertEquals(0, session.progress().level)
    }
    @Test fun `un reinicio interrumpido se completa al reabrir y los escenarios se exportan separados`() = runTest {
        java.io.File(temporary.root, "reset-pending").writeText("pending")
        java.io.File(temporary.root, "simon-results.json").writeText("corrupted old data")
        val demonstration = Demonstration(temporary.root)
        val simulation = simulateProgress(true)
        assertEquals(1, simulation.simonProgress.level)
        assertEquals(1, simulation.instrumentProgress.level)
        assertTrue(simulation.simon.all { it.origin == "simulated" })
        val exported = Json.parseToJsonElement(demonstration.export("test", simulation)).jsonObject
        assertTrue(exported.getValue("simon").jsonArray.isEmpty())
        assertEquals(4, exported.getValue("simulations").jsonObject.getValue("simon").jsonArray.size)
        assertEquals(0, exported.getValue("simonProgress").jsonObject.getValue("level").jsonPrimitive.int)
    }
}

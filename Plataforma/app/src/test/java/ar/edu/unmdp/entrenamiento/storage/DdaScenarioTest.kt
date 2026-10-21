package ar.edu.unmdp.entrenamiento.storage

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class DdaScenarioTest {
    @Test fun `banco DDA distingue modos e interrupciones y no duplica avance`() = runTest {
        val report = runDdaBench()
        assertEquals(9, report.simulations.size)
        assertTrue(report.checks.joinToString { "${it.label}: ${it.observed}" }, report.checks.all { it.passed })
        val interrupted = report.simulations.single { it.scenarioId == ProgressScenario.INTERRUPTED.name }
        assertEquals(1, interrupted.simonProgress.level)
        assertTrue(interrupted.steps.any { !it.completed && it.before == it.after })
        assertEquals("simulated", report.origin)
        assertTrue(report.boundaries.isNotEmpty())
        java.io.File("build/reports/dda-bench.md").apply { parentFile?.mkdirs(); writeText(report.markdown(), Charsets.UTF_8) }
    }
}

package ar.edu.unmdp.entrenamiento.voice

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class PitchLabSessionTest {
    @Test fun `laboratorio registra referencias conocidas y silencio sin mezclar microfono`() = runTest {
        val lab = PitchLabSession(environment = "JVM test")
        val report = lab.run(listOf(PitchFixture("a", FixtureKind.SINE, 440.0), PitchFixture("silence", FixtureKind.SILENCE)))
        assertEquals("synthetic", report.origin)
        assertEquals(8, report.cases.sumOf { it.windows.size })
        assertTrue(report.cases.all { it.requiredPassed == true })
        assertTrue(report.cases.first().windows.all { it.absoluteCents != null && it.absoluteCents <= 10 })
        assertTrue(report.cases.last().windows.none { it.valid })
        assertEquals(1, lab.history().size)
        assertTrue(report.markdown().contains("JVM test"))
    }
    @Test fun `banco completo publica limites y conserva criterio inicial sin recalibrar`() = runTest {
        val lab = PitchLabSession(environment = "JVM ${System.getProperty("java.version")} · ${System.getProperty("os.arch")}")
        val report = lab.run()
        assertEquals(83, report.cases.size)
        assertTrue(report.cases.filter { it.fixture.required }.all { it.requiredPassed == true })
        val transition = report.cases.single { it.fixture.kind == FixtureKind.TRANSITION }
        assertTrue(transition.windows[0].valid)
        assertFalse(transition.windows[2].valid)
        assertTrue(transition.windows[3].valid)
        val repeat = lab.run(listOf(report.cases.first().fixture))
        assertEquals(report.cases.first().windows.map { it.estimate.frequency }, repeat.cases.single().windows.map { it.estimate.frequency })
        java.io.File("build/reports/pitch-lab-jvm.md").apply { parentFile.mkdirs(); writeText(report.markdown(), Charsets.UTF_8) }
        println("PitchLab: ${report.cases.size} casos, obligatorios fallidos=${report.cases.count { it.requiredPassed == false }}")
    }

    @Test fun `cancelar laboratorio no guarda informe parcial`() = runTest {
        val lab = PitchLabSession(environment = "JVM test")
        try { lab.run(onProgress = { _, _ -> throw kotlinx.coroutines.CancellationException("cancelado") }); fail("Debe cancelar") }
        catch (_: kotlinx.coroutines.CancellationException) { }
        assertTrue(lab.history().isEmpty())
    }
}

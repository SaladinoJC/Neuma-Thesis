package ar.edu.unmdp.entrenamiento.ui

import ar.edu.unmdp.entrenamiento.storage.Demonstration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class DemonstrationViewModelTest {
    @get:Rule val temporary = TemporaryFolder()
    @Before fun main() { Dispatchers.setMain(StandardTestDispatcher()) }
    @After fun reset() { Dispatchers.resetMain() }

    @Test fun `recuperacion funciona sin parsear historial roto y distingue cancelacion e interrupcion`() = runTest {
        java.io.File(temporary.root, "simon-results.json").writeText("{incompleto")
        val model = DemonstrationViewModel(Demonstration(temporary.root), { error("No debe necesitar leer historiales") }, {})
        model.prepareRecovery(); model.ui.first { it.recoveryReady }; model.recoveryLaunched()
        val output = ByteArrayOutputStream()
        model.exportRecovery { output }; model.ui.first { !it.busy }
        assertEquals("Originales guardados para recuperación.", model.ui.value.message)
        java.util.zip.ZipInputStream(output.toByteArray().inputStream()).use { zip ->
            assertEquals("simon-results.json", zip.nextEntry.name)
            assertEquals("{incompleto", zip.readBytes().toString(Charsets.UTF_8))
        }
        var opened = false
        model.exportRecovery { opened = true; output }
        assertFalse(opened)
        model.prepareRecovery(); model.ui.first { it.recoveryReady }; model.exportRecovery(null)
        assertEquals("Recuperación cancelada.", model.ui.value.message)
    }

    @Test fun `resultado sin instantanea informa interrupcion y no abre el destino`() = runTest {
        val model = DemonstrationViewModel(Demonstration(temporary.root), {}, {})
        var opened = false
        model.export { opened = true; ByteArrayOutputStream() }
        advanceUntilIdle()
        assertFalse(opened)
        assertFalse(model.ui.value.busy)
        assertTrue(requireNotNull(model.ui.value.message).contains("interrumpió"))
    }
    @Test fun `exportar notifica una vez conserva ultimo resultado y libera espera al cancelar`() = runTest {
        val model = DemonstrationViewModel(Demonstration(temporary.root), {}, {})
        model.prepareExport()
        model.ui.first { it.pickerReady }
        model.pickerLaunched()
        assertTrue(model.ui.value.busy)
        val output = ByteArrayOutputStream()
        model.export { output }
        model.ui.first { !it.busy }
        assertTrue(output.toString("UTF-8").contains("schemaVersion"))
        assertEquals("Exportación guardada.", model.ui.value.message)
        val id = model.ui.value.noticeId
        assertTrue(model.ui.value.noticePending)
        model.consumeNotice(id)
        assertFalse(model.ui.value.noticePending)
        assertEquals("Exportación guardada.", model.ui.value.message)
        model.prepareExport(); model.ui.first { it.pickerReady }; model.pickerLaunched()
        model.export(null)
        assertFalse(model.ui.value.busy)
        assertFalse(model.ui.value.awaitingDestination)
        assertTrue(model.ui.value.noticeId > id)
        assertEquals("Exportación cancelada.", model.ui.value.message)
    }

    @Test fun `fallo de destino queda informado y permite otra exportacion`() = runTest {
        val model = DemonstrationViewModel(Demonstration(temporary.root), {}, {})
        model.prepareExport(); model.ui.first { it.pickerReady }; model.pickerLaunched()
        model.export { throw java.io.IOException("destino desconectado") }
        model.ui.first { !it.busy }
        assertTrue(requireNotNull(model.ui.value.message).contains("destino desconectado"))
        model.prepareExport(); model.ui.first { it.pickerReady }
        model.export(null)
        assertFalse(model.ui.value.busy)
    }
}

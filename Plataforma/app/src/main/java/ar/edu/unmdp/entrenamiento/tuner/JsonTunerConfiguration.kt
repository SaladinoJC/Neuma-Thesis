package ar.edu.unmdp.entrenamiento.tuner

import ar.edu.unmdp.entrenamiento.storage.ProfileFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.intOrNull
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/** Sustitución atómica bajo la misma barrera de perfil que exportación y reinicio. */
class JsonTunerConfiguration(private val file: File) : TunerConfigurationStore {
    private val profile = ProfileFiles.inDirectory(file.absoluteFile.parentFile!!)
    private val json = Json { encodeDefaults = true }
    override suspend fun load(): TunerConfiguration = withContext(Dispatchers.IO) { profile.transaction { read() } }
    override suspend fun save(configuration: TunerConfiguration) {
        val generation = profile.generation.get()
        withContext(Dispatchers.IO) { profile.transaction {
            check(profile.generation.get() == generation) { "El perfil se reinició durante el guardado." }
            read() // No sustituir archivos ilegibles o futuros por un valor por defecto.
            val parent = file.absoluteFile.parentFile!!
            check(parent.isDirectory || parent.mkdirs()) { "No se pudo crear el directorio de configuración." }
            val temporary = File.createTempFile("tuner-", ".tmp", parent)
            try {
                temporary.writeText(json.encodeToString(TunerConfiguration.serializer(), configuration), Charsets.UTF_8)
                try { Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING) }
                catch (_: AtomicMoveNotSupportedException) { Files.move(temporary.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING) }
            } finally { temporary.delete() }
        } }
    }
    private fun read(): TunerConfiguration {
        if (!file.exists()) return TunerConfiguration()
        val document = json.parseToJsonElement(file.readText(Charsets.UTF_8)).jsonObject
        check(document["schemaVersion"]?.jsonPrimitive?.intOrNull == 1) { "Versión de configuración del afinador no compatible." }
        check(document.keys.containsAll(listOf("guitars", "currentGuitarId", "customTunings", "criterion", "tracking"))) {
            "Configuración del afinador incompleta."
        }
        return json.decodeFromJsonElement(TunerConfiguration.serializer(), document)
    }
}

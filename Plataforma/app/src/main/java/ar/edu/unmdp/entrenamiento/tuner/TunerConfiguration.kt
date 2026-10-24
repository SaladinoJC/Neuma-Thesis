package ar.edu.unmdp.entrenamiento.tuner

import kotlinx.serialization.Serializable
import java.util.UUID
import java.util.Locale
import kotlinx.coroutines.CancellationException

/** Rango provisional de objetivos A2, pendiente de validación con guitarra física. */
val tunerTargetRange = 36..64

/** Entrada explícita de nota y octava; nunca se corrige una octava fuera del rango. */
fun parseTunerNote(text: String): Int {
    val match = Regex("^(do|re|mi|fa|sol|la|si|[a-g])([#♯b♭]?)(-?\\d+)$")
        .matchEntire(text.trim().lowercase(Locale.ROOT))
    require(match != null) { "Usá nota y octava, por ejemplo Mi2, F#3 o Sib3." }
    val pitch = mapOf("do" to 0, "c" to 0, "re" to 2, "d" to 2, "mi" to 4, "e" to 4,
        "fa" to 5, "f" to 5, "sol" to 7, "g" to 7, "la" to 9, "a" to 9, "si" to 11, "b" to 11)
    val octave = match.groupValues[3].toIntOrNull()
    require(octave != null && octave in 0..5) { "Octava fuera del rango Do2–Mi4." }
    val accidental = when (match.groupValues[2]) { "#", "♯" -> 1; "b", "♭" -> -1; else -> 0 }
    return (12 * (octave + 1) + pitch.getValue(match.groupValues[1]) + accidental).also {
        require(it in tunerTargetRange) { "Nota fuera del rango Do2–Mi4. No se cambió la octava." }
    }
}

@Serializable
data class GuitarProfile(val id: String, val name: String, val tuningId: String = "standard")

@Serializable enum class TunerMode { FREE, GUIDED }

@Serializable
data class GuitarTuning(val id: String, val name: String, val notesSixToFirst: List<Int>, val revision: Int = 1) {
    init {
        require(id.isNotBlank() && name.isNotBlank() && name.length <= 60 && revision > 0) { "Nombre o identificador de afinación inválido." }
        require(notesSixToFirst.size == 6 && notesSixToFirst.all { it in tunerTargetRange }) {
            "Ingresá seis notas con octava entre Do2 y Mi4, en orden sexta a primera."
        }
    }
}

val guitarTuningPresets = listOf(
    GuitarTuning("standard", "Estándar", listOf(40, 45, 50, 55, 59, 64)),
    GuitarTuning("drop-d", "Drop D", listOf(38, 45, 50, 55, 59, 64)),
    GuitarTuning("half-down", "Medio tono abajo", listOf(39, 44, 49, 54, 58, 63)),
    GuitarTuning("drop-c", "Drop C", listOf(36, 43, 48, 53, 57, 62)),
    GuitarTuning("open-g", "Open G", listOf(38, 43, 50, 55, 59, 62)),
    GuitarTuning("dadgad", "DADGAD", listOf(38, 45, 50, 55, 57, 62)),
)

@Serializable
data class TunerConfiguration(
    val schemaVersion: Int = 1,
    val guitars: List<GuitarProfile> = listOf(GuitarProfile("default-guitar", "Guitarra")),
    val currentGuitarId: String = "default-guitar",
    val customTunings: List<GuitarTuning> = emptyList(),
    val criterion: TunerCriterion = TunerCriterion.NORMAL,
    val tracking: TunerTracking = TunerTracking.STRINGS,
    val mode: TunerMode = TunerMode.FREE,
    val finalReview: Boolean = true,
) {
    init {
        require(schemaVersion == 1) { "Versión de configuración del afinador no compatible." }
        require(guitars.isNotEmpty() && guitars.map { it.id }.distinct().size == guitars.size &&
            guitars.all { it.id.isNotBlank() && it.name.isNotBlank() && it.name.length <= 60 }) { "Perfiles de guitarra inválidos." }
        require(guitars.any { it.id == currentGuitarId }) { "No se encontró la guitarra seleccionada." }
        val ids = (guitarTuningPresets + customTunings).map { it.id }
        require(ids.distinct().size == ids.size && guitars.all { it.tuningId in ids }) { "Referencias de afinación inválidas." }
    }
    val currentGuitar: GuitarProfile get() = guitars.first { it.id == currentGuitarId }
    val tunings: List<GuitarTuning> get() = guitarTuningPresets + customTunings
    val currentTuning: GuitarTuning get() = tunings.first { it.id == currentGuitar.tuningId }
    fun snapshot() = TunerSetupSnapshot(guitar = currentGuitar.copy(),
        tuning = currentTuning.copy(notesSixToFirst = currentTuning.notesSixToFirst.toList()), criterion = criterion, tracking = tracking)
}

@Serializable
data class TunerSetupSnapshot(val schemaVersion: Int = 1, val guitar: GuitarProfile, val tuning: GuitarTuning,
    val criterion: TunerCriterion, val tracking: TunerTracking)

interface TunerConfigurationStore {
    suspend fun load(): TunerConfiguration
    suspend fun save(configuration: TunerConfiguration)
}

class MemoryTunerConfiguration : TunerConfigurationStore {
    private var configuration = TunerConfiguration()
    override suspend fun load() = configuration
    override suspend fun save(configuration: TunerConfiguration) { this.configuration = configuration }
}

data class TunerConfigurationState(val configuration: TunerConfiguration? = null, val error: String? = null)

/** Configuración visible a través de acciones públicas; los errores no sustituyen datos guardados. */
class TunerConfigurationSession(private val store: TunerConfigurationStore,
    private val newId: () -> String = { UUID.randomUUID().toString() }, initialConfiguration: TunerConfiguration? = null) {
    var state = TunerConfigurationState(initialConfiguration)
        private set
    suspend fun load() {
        state = try { TunerConfigurationState(store.load()) }
        catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { TunerConfigurationState(error = "No se pudo leer la configuración: ${error.message}") }
    }
    private suspend fun change(transform: (TunerConfiguration) -> TunerConfiguration) {
        try {
            val previous = checkNotNull(state.configuration) { "Primero reintentá la lectura de configuración." }
            val next = transform(previous)
            store.save(next)
            state = TunerConfigurationState(next)
        } catch (cancelled: CancellationException) { throw cancelled }
        catch (error: Exception) { state = state.copy(error = "No se pudo guardar la configuración: ${error.message}") }
    }
    suspend fun createGuitar(name: String) = change { previous ->
        val cleaned = name.trim()
        require(cleaned.isNotEmpty() && cleaned.length <= 60) { "Ingresá un nombre de guitarra de 1 a 60 caracteres." }
        val guitar = GuitarProfile(newId(), cleaned)
        previous.copy(guitars = previous.guitars + guitar, currentGuitarId = guitar.id)
    }
    suspend fun selectGuitar(id: String) = change { it.copy(currentGuitarId = id) }
    suspend fun selectTuning(id: String) = change { previous ->
        require(previous.tunings.any { it.id == id }) { "Afinación desconocida." }
        previous.copy(guitars = previous.guitars.map { if (it.id == previous.currentGuitarId) it.copy(tuningId = id) else it })
    }
    suspend fun preferences(criterion: TunerCriterion, tracking: TunerTracking) = change { it.copy(criterion = criterion, tracking = tracking) }
    suspend fun guidedPreferences(mode: TunerMode, finalReview: Boolean) = change { it.copy(mode = mode, finalReview = finalReview) }
    suspend fun saveCustom(name: String, notes: List<Int>, id: String? = null) = change { withCustom(it, name, notes, id) }
    suspend fun saveCustomNames(name: String, notes: List<String>, id: String? = null) = change {
        withCustom(it, name, notes.map(::parseTunerNote), id)
    }
    private fun withCustom(previous: TunerConfiguration, name: String, notes: List<Int>, id: String?): TunerConfiguration {
        val old = id?.let { requested -> previous.customTunings.find { it.id == requested } }
        require(id == null || old != null) { "Solo se editan afinaciones personalizadas existentes." }
        val tuning = GuitarTuning(old?.id ?: newId(), name.trim(), notes.toList(), (old?.revision ?: 0) + 1)
        return previous.copy(customTunings = previous.customTunings.filterNot { it.id == tuning.id } + tuning,
            guitars = previous.guitars.map { if (it.id == previous.currentGuitarId) it.copy(tuningId = tuning.id) else it })
    }
}

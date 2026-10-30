package ar.edu.unmdp.entrenamiento.history

import ar.edu.unmdp.entrenamiento.simon.*
import ar.edu.unmdp.entrenamiento.instruments.*
import ar.edu.unmdp.entrenamiento.voice.*
import kotlinx.coroutines.CancellationException
import kotlin.math.abs

enum class HistoryGame(val label: String) { SIMON("Simón"), INSTRUMENTS("Instrumentos"), VOICE("Voz") }
data class HistoryItem(val key: String, val game: HistoryGame, val level: Int?, val origin: String, val atMillis: Long,
    val simon: SimonResult? = null, val instrument: InstrumentResult? = null, val voice: VoiceResult? = null) {
    val voiceSummary: VoiceSummary? get() = voice?.let(::voiceSummary)
}
data class HistoryState(val game: HistoryGame = HistoryGame.SIMON, val level: Int? = null,
    val origin: String? = null, val items: List<HistoryItem> = emptyList(), val total: Int = 0,
    val selected: HistoryItem? = null, val errors: Map<HistoryGame, String> = emptyMap())

/** Read-only application session. Pagination limits rendered rows, never stored results. */
class HistorySession(private val simon: SimonResults, private val instruments: InstrumentResults, private val voice: VoiceResults) {
    var state = HistoryState(); private set
    private var all = emptyList<HistoryItem>()
    private var visible = 20
    suspend fun load() {
        val errors = mutableMapOf<HistoryGame, String>()
        suspend fun <T> read(game: HistoryGame, action: suspend () -> List<T>): List<T> = try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { errors[game] = error.message ?: "No se pudo leer el historial"; emptyList() }
        all = (read(HistoryGame.SIMON) { simon.all() }.mapIndexed { i, r ->
            HistoryItem("SIMON:${r.id.ifEmpty { "legacy-$i" }}", HistoryGame.SIMON, r.scale.ordinal, r.origin, r.finishedAtMillis, simon = r)
        } + read(HistoryGame.INSTRUMENTS) { instruments.all() }.mapIndexed { i, r ->
            HistoryItem("INSTRUMENTS:${r.id.ifEmpty { "legacy-$i" }}", HistoryGame.INSTRUMENTS, r.level.ordinal, r.origin, r.finishedAtMillis, instrument = r)
        } + read(HistoryGame.VOICE) { voice.all() }.mapIndexed { i, r ->
            HistoryItem("VOICE:${r.id.ifEmpty { "legacy-$i" }}", HistoryGame.VOICE, null, r.origin, r.finishedAtMillis, voice = r)
        }).filter { it.origin != "simulated" }.sortedWith(compareByDescending<HistoryItem> { it.atMillis }.thenBy { it.key })
        state = state.copy(errors = errors, selected = state.selected?.let { old -> all.firstOrNull { it.key == old.key } })
        publish()
    }
    fun filter(game: HistoryGame, level: Int? = null, origin: String? = null) {
        visible = 20
        state = state.copy(game = game, level = if (game == HistoryGame.VOICE) null else level,
            origin = if (game == HistoryGame.VOICE) null else origin, selected = null)
        publish()
    }
    fun more() { visible += 20; publish() }
    fun select(key: String) { state = state.copy(selected = state.items.firstOrNull { it.key == key }) }
    fun back() { state = state.copy(selected = null) }
    private fun publish() {
        val filtered = all.filter { it.game == state.game && (state.level == null || it.level == state.level) &&
            (state.origin == null || it.origin == state.origin) }
        state = state.copy(items = filtered.take(visible), total = filtered.size)
    }
}

data class VoiceSummary(val total: Int, val valid: Int, val meanCents: Double?, val medianAbsoluteCents: Double?,
    val processingP50Micros: Long?, val processingP95Micros: Long?)
fun voiceSummary(result: VoiceResult): VoiceSummary {
    val valid = result.measurements.filter { it.valid && it.estimate.valid && it.cents?.isFinite() == true }
    val cents = valid.map { checkNotNull(it.cents) }
    val times = result.measurements.map { it.processingMicros }.sorted()
    fun percentile(p: Double): Long? = if (times.isEmpty()) null else times[(kotlin.math.ceil(times.size*p).toInt()-1).coerceAtLeast(0)]
    val absolute = cents.map(::abs).sorted()
    val median = if (absolute.isEmpty()) null else if (absolute.size%2 == 1) absolute[absolute.size/2]
        else (absolute[absolute.size/2-1]+absolute[absolute.size/2])/2
    return VoiceSummary(result.measurements.size, valid.size, cents.takeIf { it.isNotEmpty() }?.average(), median, percentile(.5), percentile(.95))
}

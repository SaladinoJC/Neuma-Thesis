package ar.edu.unmdp.entrenamiento.voice

import ar.edu.unmdp.entrenamiento.storage.JsonResultsFile
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.yield
import kotlinx.serialization.Serializable
import java.io.File
import java.util.UUID
import kotlin.math.*
import kotlin.random.Random

enum class FixtureKind { SINE, HARMONIC, NOISY, NOISE, SILENCE, DC, INVALID, TRANSITION }
@Serializable
data class PitchFixture(val id: String, val kind: FixtureKind, val frequency: Double? = null,
    val amplitude: Double = .4, val phase: Double = 0.0, val snrDb: Double? = null, val seed: Int = 42,
    val required: Boolean = kind in listOf(FixtureKind.SILENCE, FixtureKind.DC, FixtureKind.INVALID) ||
        (kind == FixtureKind.SINE && frequency in listOf(110.0, 220.0, 440.0) && amplitude == .4 && phase == 0.0)) {
    init {
        require(id.isNotEmpty() && amplitude.isFinite() && amplitude in 0.0..1.0 && phase.isFinite())
        require(kind !in listOf(FixtureKind.SINE, FixtureKind.HARMONIC, FixtureKind.NOISY, FixtureKind.TRANSITION) || frequency?.let { it.isFinite() && it > 0 } == true)
        require(kind != FixtureKind.NOISY || snrDb?.isFinite() == true)
    }
}
@Serializable
data class LabWindow(val index: Int, val referenceHz: Double?, val estimate: PitchEstimate,
    val absoluteCents: Double?, val octaveError: Boolean, val processingMicros: Long, val normalizationGain: Double, val valid: Boolean = estimate.valid)
@Serializable
data class PitchLabCase(val fixture: PitchFixture, val windows: List<LabWindow>, val requiredPassed: Boolean?)
@Serializable
data class PitchLabReport(val id: String, val startedAtMillis: Long, val finishedAtMillis: Long,
    val environment: String, val configuration: YinConfiguration, val cases: List<PitchLabCase>,
    val origin: String = "synthetic", val generatorVersion: String = "pitch-fixtures-v1") {
    fun markdown(): String = buildString {
        appendLine("# Laboratorio YIN · Señales sintéticas")
        appendLine(); appendLine("Entorno: $environment · Generador: $generatorVersion · Origen: $origin")
        appendLine("Configuración: $configuration")
        appendLine("Duración de ejecución: ${finishedAtMillis-startedAtMillis} ms. No es latencia acústica.")
        appendLine("Cuatro ventanas completas por caso; amplitud/fase/semilla/SNR se conservan en JSON.")
        appendLine("Criterio obligatorio: tres senoidales limpias ≤10 cents; silencio/DC/PCM inválido sin tono. Los demás casos son exploratorios.")
        appendLine(); appendLine("| Caso | Válidas | Error máximo (cents) | Octavas | Criterio |")
        appendLine("| --- | --- | --- | --- | --- |")
        cases.forEach { c -> appendLine("| ${c.fixture.id} | ${c.windows.count { it.valid }}/${c.windows.size} | ${c.windows.mapNotNull { it.absoluteCents }.maxOrNull() ?: "—"} | ${c.windows.count { it.octaveError }} | ${c.requiredPassed?.let { if (it) "Cumple" else "FALLA" } ?: "Exploratorio"} |") }
        val times = cases.flatMap { it.windows }.map { it.processingMicros }.sorted()
        fun percentile(p: Double) = times[(ceil(times.size*p).toInt()-1).coerceAtLeast(0)]
        if (times.isNotEmpty()) appendLine("Procesamiento p50/p95: ${percentile(.5)}/${percentile(.95)} µs (medido en el entorno indicado).")
        appendLine("No es una prueba de voz humana, eficacia pedagógica o precisión en teléfono físico.")
    }
}
interface PitchLabResults { suspend fun all(): List<PitchLabReport>; suspend fun save(report: PitchLabReport) }
class MemoryPitchLabResults : PitchLabResults {
    private val entries = mutableListOf<PitchLabReport>()
    override suspend fun all() = entries.toList()
    override suspend fun save(report: PitchLabReport) { if (entries.none { it.id == report.id }) entries.add(report) }
}
class JsonPitchLabResults(file: File) : PitchLabResults {
    private val store = JsonResultsFile(file, PitchLabReport.serializer())
    override suspend fun all() = store.all()
    override suspend fun save(report: PitchLabReport) = store.append(report) { it.id == report.id }
}

fun defaultPitchFixtures(): List<PitchFixture> = buildList {
    for (hz in listOf(55.0, 65.406, 82.407, 110.0, 146.832, 220.0, 261.626, 440.0, 523.251, 880.0, 1000.0))
        for (amplitude in listOf(.02, .1, .4)) for (phase in listOf(0.0, PI/3))
            add(PitchFixture("sine-$hz-$amplitude-$phase", FixtureKind.SINE, hz, amplitude, phase))
    for (kind in listOf(FixtureKind.SILENCE, FixtureKind.DC, FixtureKind.INVALID, FixtureKind.NOISE)) add(PitchFixture(kind.name.lowercase(), kind))
    add(PitchFixture("note-silence-note", FixtureKind.TRANSITION, 220.0))
    for (hz in listOf(110.0, 220.0, 440.0)) {
        add(PitchFixture("harmonic-$hz", FixtureKind.HARMONIC, hz))
        for (snr in listOf(20.0, 10.0, 0.0)) add(PitchFixture("noise-$hz-snr$snr", FixtureKind.NOISY, hz, snrDb = snr))
    }
}

/** Generates PCM in memory only; raw windows are never included in a report. */
class PitchLabSession(private val results: PitchLabResults = MemoryPitchLabResults(),
    private val configuration: YinConfiguration = YinConfiguration(), private val environment: String) {
    suspend fun history(): List<PitchLabReport> = results.all()
    suspend fun run(fixtures: List<PitchFixture> = defaultPitchFixtures(), onProgress: suspend (Int, Int) -> Unit = { _, _ -> }): PitchLabReport {
        require(fixtures.isNotEmpty() && fixtures.size <= 1000)
        val started = System.currentTimeMillis()
        val estimator = YinEstimator(configuration)
        val cases = fixtures.mapIndexed { fixtureIndex, fixture ->
            currentCoroutineContext().ensureActive()
            val windows = (0..3).map { index ->
                yield()
                val (pcm, gain) = render(fixture, index)
                val begin = System.nanoTime()
                val estimate = estimator.estimate(pcm)
                val micros = (System.nanoTime()-begin)/1000
                val reference = fixture.frequency.takeUnless { fixture.kind == FixtureKind.TRANSITION && index in 1..2 }
                val cents = if (reference != null && estimate.frequency != null) 1200*log2(estimate.frequency/reference) else null
                val octaves = cents?.let { round(it/1200) }
                LabWindow(index, reference, estimate, cents?.let(::abs),
                    cents != null && octaves != null && octaves != 0.0 && abs(cents-octaves*1200) <= 50, micros, gain)
            }
            onProgress(fixtureIndex+1, fixtures.size)
            PitchLabCase(fixture, windows, if (!fixture.required) null else if (fixture.frequency == null) windows.none { it.valid }
                else windows.all { it.valid && it.absoluteCents != null && it.absoluteCents <= 10 })
        }
        currentCoroutineContext().ensureActive()
        val report = PitchLabReport(UUID.randomUUID().toString(), started, System.currentTimeMillis(), environment, configuration, cases)
        results.save(report)
        return report
    }
    private fun render(f: PitchFixture, window: Int): Pair<FloatArray, Double> {
        val c = configuration
        if (f.kind == FixtureKind.INVALID) return FloatArray(c.windowSize) { Float.NaN } to 1.0
        val values = DoubleArray(c.windowSize) { i ->
            val angle = 2*PI*(f.frequency ?: 0.0)*(window*c.windowSize+i)/c.sampleRate+f.phase
            when (f.kind) {
                FixtureKind.SILENCE -> 0.0
                FixtureKind.DC -> f.amplitude
                FixtureKind.HARMONIC -> f.amplitude*(sin(angle)+.5*sin(2*angle)+.25*sin(3*angle))/1.75
                FixtureKind.TRANSITION -> if (window == 2 || (window == 1 && i >= c.windowSize/2)) 0.0 else f.amplitude*sin(angle)
                FixtureKind.NOISE -> 0.0
                else -> f.amplitude*sin(angle)
            }
        }
        if (f.kind in listOf(FixtureKind.NOISY, FixtureKind.NOISE)) {
            val random = Random(f.seed+window)
            val noise = DoubleArray(c.windowSize) { random.nextDouble(-1.0, 1.0) }
            val mean = noise.average()
            for (i in noise.indices) noise[i] -= mean
            val noiseRms = sqrt(noise.sumOf { it*it }/noise.size)
            val desired = if (f.kind == FixtureKind.NOISE) f.amplitude else sqrt(values.sumOf { it*it }/values.size)/10.0.pow(checkNotNull(f.snrDb)/20)
            for (i in values.indices) values[i] += noise[i]*desired/noiseRms
        }
        val peak = values.maxOf(::abs)
        val gain = if (peak > .95) .95/peak else 1.0
        return FloatArray(values.size) { (values[it]*gain).toFloat() } to gain
    }
}

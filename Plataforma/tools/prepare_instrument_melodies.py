"""Notas reales mf de Iowa para frases pentatónicas; no transpone el audio."""
from hashlib import sha256
import json
from pathlib import Path
import numpy as np
import soundfile as sf
from scipy.signal import find_peaks, resample_poly
import prepare_instruments as bank

OUTPUT = bank.OUTPUT / "melody"
MIDIS = {60: "C4", 62: "D4", 64: "E4", 67: "G4", 69: "A4"}


def estimate(audio, rate):
    estimates = []
    for seconds in (.15, .35, .55):
        x = audio[round(seconds*rate):round((seconds+.2)*rate):4].astype(np.float64)
        x -= x.mean()
        reduced = rate / 4
        # El banco se acota a Do4–La4; excluir subarmónicos fuera de ese registro.
        low, high = round(reduced/800), round(reduced/240)
        scores = np.array([
            np.dot(x[:-lag], x[lag:]) / max(1e-20, np.sqrt(np.dot(x[:-lag], x[:-lag])*np.dot(x[lag:], x[lag:])))
            for lag in range(low, high+1)
        ])
        peaks, _ = find_peaks(scores)
        if not len(peaks):
            raise ValueError("Sin periodicidad")
        index = int(peaks[np.argmax(scores[peaks])])
        a, b, c = scores[index-1:index+2]
        lag = low + index + .5*(a-c)/(a-2*b+c)
        estimates.append((reduced/lag, float(scores[index])))
    return float(np.median([f for f, _ in estimates])), min(c for _, c in estimates)


def source(key, midi):
    page, filename = bank.SOURCES[key]
    cache = bank.CACHE
    if key == "piano":
        filename = f"Piano.mf.{MIDIS[midi]}.aiff"
        cache = bank.ROOT / "build/piano-sources"
    elif key == "violin" and midi in (67, 69):
        filename = "Violin.arco.mf.sulD.D4A4.aiff"
    parser = bank.Links(bank.BASE+page)
    bank.download(bank.BASE+page, cache/page)
    parser.feed((cache/page).read_text(encoding="utf-8", errors="replace"))
    url = parser.links[filename]
    original = cache/filename
    bank.download(url, original)
    return bank.BASE+page, url, original


def convert(key, midi, prepared):
    page, url, original = source(key, midi)
    if original not in prepared:
        data, original_rate = sf.read(original, dtype="float32", always_2d=True)
        audio = data.mean(axis=1)
        if original_rate == 96000:
            audio = resample_poly(audio, 147, 320)
        elif original_rate != 44100:
            raise ValueError(f"Sample rate inesperado: {original_rate}")
        candidates = []
        # 30 ms permite separar notas del fagot con pausas de 60–90 ms.
        gap_blocks = 3 if key == "bassoon" else 10
        for start, end in bank.segments(audio, 44100, maximum_gap_blocks=gap_blocks):
            # Medir también la cola que queda por debajo del umbral de segmentación.
            frequency, confidence = estimate(audio[start:start+round(.75*44100)], 44100)
            candidates.append((start, end, frequency, confidence))
        prepared[original] = audio, original_rate, data.shape[1], candidates
    audio, original_rate, channels, candidates = prepared[original]
    expected_hz = 440 * 2**((midi-69)/12)
    start, end, frequency, confidence = min(candidates, key=lambda part: abs(1200*np.log2(part[2]/expected_hz)))
    cents = float(1200*np.log2(frequency/expected_hz))
    if abs(cents) > 30 or confidence < .7:
        raise ValueError(f"Nota no comprobada: {key} {midi}: {frequency:.2f} Hz ({cents:.2f} cents), {confidence:.3f}")
    rate, frames = 44100, 39690
    clip = np.zeros(frames, dtype=np.float32)
    length = min(frames, end-start)
    clip[:length] = audio[start:start+length]
    gain = .08/float(np.sqrt(np.mean(clip**2)))
    clip *= gain
    fade = min(length, round(rate*.08))
    clip[length-fade:length] *= np.linspace(1, 0, fade)
    if np.max(np.abs(clip)) >= .95:
        raise ValueError(f"Pico excesivo en {key} {midi}")
    target = OUTPUT/f"{key}_{midi}_mf.ogg"
    sf.write(target, clip, rate, format="OGG", subtype="VORBIS", compression_level=.65)
    decoded, decoded_rate = sf.read(target)
    if decoded_rate != rate or len(decoded) != frames or not np.isfinite(decoded).all():
        raise ValueError(f"OGG inválido: {target}")
    print(f"{key} {MIDIS[midi]}: {frequency:.2f} Hz, {cents:+.2f} cents", flush=True)
    return {
        "instrument": key, "midi": midi, "source_page": page, "source_url": url,
        "original_sha256": sha256(original.read_bytes()).hexdigest(),
        "original_bytes": original.stat().st_size, "original_sample_rate": original_rate,
        "original_channels": channels, "onset_seconds": start/rate,
        "source_segment_end_seconds": end/rate, "estimated_hz": frequency,
        "estimated_cents": cents, "autocorrelation": confidence, "gain": gain,
        "frames": frames, "decoded_rms": float(np.sqrt(np.mean(decoded**2))),
        "decoded_peak": float(np.max(np.abs(decoded))),
        "asset": f"instruments/melody/{target.name}", "ogg_bytes": target.stat().st_size,
        "ogg_sha256": sha256(target.read_bytes()).hexdigest(),
    }


def main():
    OUTPUT.mkdir(parents=True, exist_ok=True)
    prepared = {}
    samples = [convert(key, midi, prepared) for key in bank.SOURCES for midi in MIDIS]
    manifest = {
        "bank_version": "iowa-pentatonic-mf-v2", "prepared_on": "2026-10-09",
        "usage_statement": bank.BASE+"MIS.html",
        "conversion": {"sample_rate": 44100, "channels": 1, "duration_seconds": .9,
                       "target_rms": .08, "fade_seconds": .08, "transposition": False,
                       "maximum_pitch_deviation_cents": 30,
                       "codec": "Vorbis", "compression_level": .65,
                       "tool": f"soundfile {sf.__version__} / libsndfile {sf.__libsndfile_version__}"},
        "samples": samples,
    }
    (OUTPUT/"manifest.json").write_text(json.dumps(manifest, indent=2)+"\n", encoding="utf-8")
    print("TOTAL OGG:", sum(s["ogg_bytes"] for s in samples), flush=True)


if __name__ == "__main__":
    main()

"""Comprueba el banco incluido: inventario, hashes y PCM decodificado offline."""
from hashlib import sha256
import json
from pathlib import Path
import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT/"app/src/main/assets"
manifest = json.loads((ASSETS/"instruments/melody/manifest.json").read_text(encoding="utf-8"))
expected_instruments = {"piano", "violin", "flute", "marimba", "clarinet", "trumpet", "trombone", "oboe", "bassoon"}
expected_notes = {60, 62, 64, 67, 69}
samples = manifest["samples"]
assert len(samples) == 45
assert {(s["instrument"], s["midi"]) for s in samples} == {
    (instrument, midi) for instrument in expected_instruments for midi in expected_notes
}
rms_values, peak_values = [], []
for sample in samples:
    path = ASSETS/sample["asset"]
    assert sha256(path.read_bytes()).hexdigest() == sample["ogg_sha256"], path
    audio, rate = sf.read(path, always_2d=True)
    assert rate == 44100 and audio.shape == (39690, 1), path
    assert np.isfinite(audio).all(), path
    rms, peak = float(np.sqrt(np.mean(audio**2))), float(np.max(np.abs(audio)))
    assert .06 <= rms <= .085 and peak < .95, path
    assert abs(rms-sample["decoded_rms"]) < 1e-8 and abs(peak-sample["decoded_peak"]) < 1e-8, path
    assert abs(sample["estimated_cents"]) <= 30 and sample["autocorrelation"] >= .7, path
    rms_values.append(rms)
    peak_values.append(peak)
print(json.dumps({
    "bank_version": manifest["bank_version"], "samples": len(samples),
    "ogg_bytes": sum((ASSETS/s["asset"]).stat().st_size for s in samples),
    "decoded_rms_range": [min(rms_values), max(rms_values)],
    "decoded_maximum_peak": max(peak_values),
    "source_estimated_cents_range": [min(s["estimated_cents"] for s in samples), max(s["estimated_cents"] for s in samples)],
}, indent=2))

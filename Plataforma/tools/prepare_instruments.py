"""Banco fijo de Iowa: mf, Do4 real, sin transposición. Originales fuera del APK."""
from concurrent.futures import ThreadPoolExecutor
from hashlib import sha256
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import quote, urljoin
import json
import subprocess
import numpy as np
import soundfile as sf
from scipy.signal import resample_poly

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / "build/instrument-sources"
OUTPUT = ROOT / "app/src/main/assets/instruments"
BASE = "https://theremin.music.uiowa.edu/"
SOURCES = {
    "piano": ("MISpiano.html", "Piano.mf.C4.aiff"),
    "violin": ("MISviolin.html", "Violin.arco.mf.sulG.B3B4.aiff"),
    "flute": ("MISflute.html", "Flute.nonvib.mf.B3B4.aiff"),
    "marimba": ("Mismarimba.html", "Marimba.cord.mf.C4B4.aif"),
    "clarinet": ("MISBbclarinet.html", "BbClar.mf.C4B4.aiff"),
    "trumpet": ("MISBbtrumpet.html", "Trumpet.novib.mf.C4B4.aiff"),
    "trombone": ("MIStenortrombone.html", "TenorTrombone.mf.C4B4.aiff"),
    "oboe": ("MISoboe.html", "Oboe.mf.C4B4.aiff"),
    "bassoon": ("MISbassoon.html", "Bassoon.mf.C4B4.aiff"),
}

class Links(HTMLParser):
    def __init__(self, page):
        super().__init__()
        self.page, self.links = page, {}
    def handle_starttag(self, tag, attrs):
        for key, value in attrs:
            if tag == "a" and key == "href" and value and value.lower().endswith((".aiff", ".aif")):
                self.links[Path(value).name] = quote(urljoin(self.page, value), safe=":/")

def download(url, target):
    if not target.exists():
        subprocess.run(["curl.exe", "--fail", "--location", "--silent", "--show-error",
                        "--retry", "2", "--output", str(target), url], check=True)

def fetch(item):
    key, (page_name, name) = item
    page_url = BASE + page_name
    page = CACHE / page_name
    download(page_url, page)
    parser = Links(page_url)
    parser.feed(page.read_text(encoding="utf-8", errors="replace"))
    if name not in parser.links:
        print(key, "AVAILABLE:", [n for n in parser.links if ".mf." in n], flush=True)
        return None
    original = CACHE / name
    download(parser.links[name], original)
    print(key, name, sf.info(original), flush=True)
    return key, page_url, parser.links[name], original

def segments(audio, rate):
    hop = round(rate * .01)
    rms = np.array([np.sqrt(np.mean(audio[i:i+hop] ** 2)) for i in range(0, len(audio), hop)])
    active = rms > max(float(rms.max()) * .025, .0001)
    # Unir huecos menores de 100 ms dentro de una nota.
    indices = np.flatnonzero(active)
    if not len(indices):
        raise ValueError("Audio silencioso")
    groups = np.split(indices, np.flatnonzero(np.diff(indices) > 10) + 1)
    return [(max(0, int(g[0] * hop) - hop), min(len(audio), int((g[-1]+1)*hop)))
            for g in groups if len(g) >= 20]

def pitch(audio, rate):
    # Autocorrelación normalizada, ventana de 200 ms tras el ataque.
    x = audio[round(.15*rate):round(.35*rate):4].astype(np.float64)
    x -= x.mean()
    reduced_rate = rate / 4
    low, high = round(reduced_rate/350), round(reduced_rate/200)
    scores = [np.dot(x[:-lag], x[lag:]) / np.sqrt(np.dot(x[:-lag], x[:-lag]) * np.dot(x[lag:], x[lag:]))
              for lag in range(low, high+1)]
    index = int(np.argmax(scores))
    lag = float(low + index)
    if 0 < index < len(scores)-1:
        a, b, c = scores[index-1:index+2]
        lag += .5 * (a-c) / (a-2*b+c)
    return reduced_rate/lag, float(scores[index])

def convert(item):
    key, page, url, original = item
    data, rate = sf.read(original, dtype="float32", always_2d=True)
    original_rate = rate
    if rate not in (44100, 96000):
        raise ValueError(f"Sample rate inesperado: {rate}")
    audio = data.mean(axis=1)
    if rate == 96000:
        audio = resample_poly(audio, 147, 320)
        rate = 44100
    candidates = []
    for start, end in segments(audio, rate):
        frequency, confidence = pitch(audio[start:end], rate)
        candidates.append((start, end, frequency, confidence))
    print(key, "segments", [(round(s/rate,3), round(e/rate,3), round(f,2), round(c,3)) for s,e,f,c in candidates], flush=True)
    chosen = min(candidates, key=lambda part: abs(1200*np.log2(part[2]/261.625565)))
    start, end, frequency, confidence = chosen
    cents = float(1200*np.log2(frequency/261.625565))
    if abs(cents) > 25 or confidence < .7:
        raise ValueError(f"No se encontró Do4 en {key}: {frequency} Hz ({cents} cents)")
    frames = round(1.5*rate)
    clip = np.zeros(frames, dtype=np.float32)
    length = min(frames, end-start)
    clip[:length] = audio[start:start+length]
    # Nivel RMS comparable, sin limitar/comprimir ni cambiar frecuencia.
    rms = float(np.sqrt(np.mean(clip**2)))
    gain = .08/rms
    clip *= gain
    fade = min(length, round(rate*.08))
    clip[length-fade:length] *= np.linspace(1, 0, fade)
    if np.max(np.abs(clip)) >= .95:
        raise ValueError(f"Pico excesivo en {key}; revisar RMS común")
    target = OUTPUT / f"{key}_c4_mf.ogg"
    sf.write(target, clip, rate, format="OGG", subtype="VORBIS", compression_level=.65)
    decoded, decoded_rate = sf.read(target)
    if decoded_rate != rate or len(decoded) != frames or not np.isfinite(decoded).all():
        raise ValueError(f"OGG inválido: {key}")
    return {"instrument": key, "source_page": page, "source_url": url,
            "original_sha256": sha256(original.read_bytes()).hexdigest(),
            "original_bytes": original.stat().st_size, "original_sample_rate": original_rate,
            "original_channels": data.shape[1], "onset_seconds": start/rate,
            "source_segment_end_seconds": end/rate, "gain": gain,
            "estimated_hz": frequency, "estimated_cents_from_c4": cents,
            "autocorrelation": confidence, "frames": frames,
            "decoded_rms": float(np.sqrt(np.mean(decoded**2))),
            "decoded_peak": float(np.max(np.abs(decoded))),
            "asset": f"instruments/{target.name}", "ogg_bytes": target.stat().st_size,
            "ogg_sha256": sha256(target.read_bytes()).hexdigest()}

def main():
    CACHE.mkdir(parents=True, exist_ok=True)
    OUTPUT.mkdir(parents=True, exist_ok=True)
    with ThreadPoolExecutor(max_workers=3) as pool:
        sources = list(pool.map(fetch, SOURCES.items()))
    if any(item is None for item in sources):
        raise ValueError("Resolver nombres de fuentes antes de convertir")
    samples = [convert(item) for item in sources]
    manifest = {"bank_version": "iowa-c4-mf-v1", "usage_statement": BASE+"MIS.html",
                "prepared_on": "2026-10-02", "conversion": {"sample_rate":44100, "channels":1,
                "duration_seconds":1.5, "target_rms":.08, "fade_seconds":.08,
                "transposition":False, "codec":"Vorbis", "compression_level":.65,
                "tool": f"soundfile {sf.__version__} / libsndfile {sf.__libsndfile_version__}"},
                "samples":samples}
    (OUTPUT/"manifest.json").write_text(json.dumps(manifest, indent=2)+"\n", encoding="utf-8")
    print("TOTAL OGG:", sum(s["ogg_bytes"] for s in samples), flush=True)

if __name__ == "__main__":
    main()

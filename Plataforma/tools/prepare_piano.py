"""Descarga solo mf C4..Bb5 y genera el banco OGG usado por Simón.

Requiere Python 3, numpy, soundfile (libsndfile con Vorbis) y curl con TLS.
Los originales quedan en build/piano-sources, fuera de Git y del APK.
"""

from concurrent.futures import ThreadPoolExecutor
from hashlib import sha256
from datetime import date
from html.parser import HTMLParser
import json
from pathlib import Path
import subprocess
from urllib.parse import quote, urljoin

import numpy as np
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
SOURCE_PAGE = "https://theremin.music.uiowa.edu/MISpiano.html"
CACHE = ROOT / "build/piano-sources"
OUTPUT = ROOT / "app/src/main/assets/piano"
NOTE_NAMES = ["C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B"]
DURATION = 0.6
GAIN = 5.0


class PianoLinks(HTMLParser):
    def __init__(self):
        super().__init__()
        self.links = {}

    def handle_starttag(self, tag, attrs):
        for key, value in attrs:
            if tag == "a" and key == "href" and value and "Piano.mf." in value:
                self.links[Path(value).name] = quote(urljoin(SOURCE_PAGE, value), safe=":/")


def download(url, target):
    subprocess.run(
        ["curl", "--fail", "--location", "--silent", "--show-error", "--retry", "2",
         "--output", str(target), url], check=True,
    )


def prepare_note(item):
    midi, links = item
    name = f"{NOTE_NAMES[midi % 12]}{midi // 12 - 1}"
    original_name = f"Piano.mf.{name}.aiff"
    url = links[original_name]
    original = CACHE / original_name
    if not original.exists():
        download(url, original)
    stereo, rate = sf.read(original, dtype="float32", always_2d=True)
    if rate != 44100:
        raise ValueError(f"Frecuencia inesperada en {original_name}: {rate}")
    mono = stereo.mean(axis=1)
    # Conservar 10 ms antes del ataque, quitando solo el silencio inicial.
    active = np.flatnonzero(np.abs(mono) > np.max(np.abs(mono)) * 0.02)
    if not len(active):
        raise ValueError(f"Sample silencioso: {original_name}")
    onset = max(0, int(active[0]) - round(rate * 0.01))
    frames = round(rate * DURATION)
    clip = mono[onset:onset + frames].copy()
    if len(clip) != frames:
        raise ValueError(f"Sample demasiado corto: {original_name}")
    clip *= GAIN
    if np.max(np.abs(clip)) >= 0.95:
        raise ValueError(f"Ganancia excesiva en {original_name}")
    # No normalizar cada nota: conservar las diferencias de la dinámica mf.
    fade = round(rate * 0.07)
    clip[-fade:] *= np.linspace(1, 0, fade)
    target = OUTPUT / f"mf_{midi}.ogg"
    sf.write(target, clip, rate, format="OGG", subtype="VORBIS", compression_level=0.65)
    decoded, decoded_rate = sf.read(target)
    if decoded_rate != rate or len(decoded) != frames or not np.isfinite(decoded).all():
        raise ValueError(f"OGG inválido: {target}")
    metadata = {
        "midi": midi, "note": name, "dynamic": "mf", "source_url": url,
        "original_bytes": original.stat().st_size,
        "original_sha256": sha256(original.read_bytes()).hexdigest(),
        "onset_seconds": round(onset / rate, 6), "frames": frames,
        "asset": f"piano/{target.name}", "ogg_bytes": target.stat().st_size,
        "ogg_sha256": sha256(target.read_bytes()).hexdigest(),
    }
    print(f"{name} mf -> {target.name}: {target.stat().st_size:,} bytes", flush=True)
    return metadata


def main():
    CACHE.mkdir(parents=True, exist_ok=True)
    OUTPUT.mkdir(parents=True, exist_ok=True)
    page = CACHE / "MISpiano.html"
    download(SOURCE_PAGE, page)
    parser = PianoLinks()
    parser.feed(page.read_text())
    # Tónicas actuales MIDI 60..71 + escala cromática 0..11: unión 60..82.
    with ThreadPoolExecutor(max_workers=3) as pool:
        notes = list(pool.map(prepare_note, ((midi, parser.links) for midi in range(60, 83))))
    manifest = {
        "source_page": SOURCE_PAGE,
        "usage_statement": "https://theremin.music.uiowa.edu/MIS.html",
        "prepared_on": date.today().isoformat(),
        "conversion": {
            "tool": f"python-soundfile {sf.__version__} / libsndfile {sf.__libsndfile_version__}",
            "codec": "Vorbis", "compression_level": 0.65,
            "sample_rate": 44100, "channels": 1, "duration_seconds": DURATION,
            "trailing_fade_seconds": 0.07, "per_note_normalization": False,
            "global_gain": GAIN,
        },
        "notes": notes,
    }
    (OUTPUT / "manifest.json").write_text(json.dumps(manifest, indent=2) + "\n", encoding="utf-8")
    original_bytes = sum(n["original_bytes"] for n in notes)
    ogg_bytes = sum(n["ogg_bytes"] for n in notes)
    print(f"TOTAL: {len(notes)} notas mf; AIFF {original_bytes:,} bytes -> OGG {ogg_bytes:,} bytes")


if __name__ == "__main__":
    main()

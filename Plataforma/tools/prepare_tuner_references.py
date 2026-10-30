"""Banco A4: originales cacheados, afinación explícita, OGG y manifiesto verificable.

Python/numpy/scipy/soundfile; --verify comprueba los assets sin descargar.
"""
import argparse
from concurrent.futures import ThreadPoolExecutor
from hashlib import sha256
import json
from pathlib import Path
from urllib.request import urlopen
from math import gcd
from threading import Lock

import numpy as np
from scipy.signal import resample_poly
import soundfile as sf

ROOT = Path(__file__).resolve().parents[1]
CACHE = ROOT / 'build/tuner-sources'
OUT = ROOT / 'app/src/main/assets/tuner'
REV = 'b4920dc662fd9cad6dcaccdeecffdd91c8725d8c'
GUITAR = f'https://raw.githubusercontent.com/sfzinstruments/karoryfer.emilyguitar/{REV}/'
NAMES = ['C', 'Db', 'D', 'Eb', 'E', 'F', 'Gb', 'G', 'Ab', 'A', 'Bb', 'B']
GUITAR_KEYS = [37, 40, 42, 45, 48, 51, 54, 57, 60, 63, 66]
RATE, FRAMES = 44100, 52920
DOWNLOAD_LOCK = Lock()

def name(midi):
    return NAMES[midi % 12] + str(midi // 12 - 1)

def hz(midi):
    return 440 * 2 ** ((midi - 69) / 12)

def fetch(url, filename):
    CACHE.mkdir(parents=True, exist_ok=True)
    path = CACHE / filename
    with DOWNLOAD_LOCK:
        if not path.exists():
            with urlopen(url, timeout=60) as response:
                path.write_bytes(response.read())
    return path

def frequency(audio, expected):
    # Autocorrelación normalizada cerca de la fundamental nominal, interpolación parabólica.
    # No acredita ausencia de errores de octava: Android verifica además con YIN sin esta guía.
    x = audio[round(.15 * RATE):round(.65 * RATE):4].astype(np.float64)
    x -= x.mean()
    low, high = int(RATE / 4 / (expected * 1.04)), int(RATE / 4 / (expected * .96)) + 1
    scores = np.array([np.dot(x[:-k], x[k:]) / np.sqrt(np.dot(x[:-k], x[:-k]) * np.dot(x[k:], x[k:]))
                       for k in range(low, high + 1)])
    index = int(np.argmax(scores))
    assert 0 < index < len(scores) - 1, 'Fundamental fuera de ventana'
    a, b, c = scores[index - 1:index + 2]
    lag = low + index + .5 * (a - c) / (a - 2 * b + c)
    return RATE / 4 / lag

def prepare(item):
    timbre, midi = item
    source_midi = min(GUITAR_KEYS, key=lambda k: abs(k - midi)) if timbre == 'guitar' else midi
    filename = f'{name(source_midi).lower()}_mf_rr1.wav' if timbre == 'guitar' else f'Piano.mf.{name(midi)}.aiff'
    url = GUITAR + 'notes/' + filename if timbre == 'guitar' else 'https://theremin.music.uiowa.edu/sound%20files/MIS/Piano_Other/piano/' + filename
    original = fetch(url, filename)
    data, rate = sf.read(original, always_2d=True)
    audio = data.mean(axis=1)
    divisor = gcd(rate, RATE)
    audio = resample_poly(audio, RATE // divisor, rate // divisor)
    onset = max(0, int(np.flatnonzero(np.abs(audio) > np.max(np.abs(audio)) * .02)[0]) - 220)
    audio = audio[onset:]
    measured = frequency(audio, hz(source_midi))
    ratio = hz(midi) / measured
    output = np.interp(np.arange(FRAMES) * ratio, np.arange(len(audio)), audio, right=0)
    gain = min(.08 / np.sqrt(np.mean(output ** 2)), .85 / np.max(np.abs(output)))
    output *= gain
    output[:441] *= np.linspace(0, 1, 441)
    output[-3528:] *= np.linspace(1, 0, 3528)
    target = OUT / f'{timbre}_{midi}.ogg'
    sf.write(target, output, RATE, format='OGG', subtype='VORBIS', compression_level=.65)
    decoded, _ = sf.read(target)
    cents = 1200 * np.log2(frequency(decoded, hz(midi)) / hz(midi))
    assert abs(cents) < 2, (timbre, midi, cents)
    return dict(file=target.name, timbre=timbre, midi=midi, sourceMidi=source_midi, url=url,
                sourceSha256=sha256(original.read_bytes()).hexdigest(), onsetFrames=onset, ratio=ratio, gain=gain,
                frames=FRAMES, sampleRate=RATE, measuredCents=float(cents),
                sha256=sha256(target.read_bytes()).hexdigest())

def verify():
    records = json.loads((OUT / 'manifest.json').read_text())['samples']
    assert {(r['timbre'], r['midi']) for r in records} == {(t, m) for t in ['guitar', 'piano'] for m in range(36, 65)}
    for r in records:
        path = OUT / r['file']
        assert sha256(path.read_bytes()).hexdigest() == r['sha256']
        audio, rate = sf.read(path)
        assert rate == RATE and audio.ndim == 1 and len(audio) == FRAMES
        assert np.isfinite(audio).all() and np.max(np.abs(audio)) < .95
        assert abs(1200 * np.log2(frequency(audio, hz(r['midi'])) / hz(r['midi']))) < 2
    print(f'{len(records)} OGG verificados: Do2–Mi4, mono/44100/1200 ms, hashes y afinación <2 cents (autocorrelación guiada).')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--verify', action='store_true')
    args = parser.parse_args()
    if not args.verify:
        OUT.mkdir(parents=True, exist_ok=True)
        with ThreadPoolExecutor(max_workers=4) as pool:
            records = list(pool.map(prepare, [(t, m) for t in ['guitar', 'piano'] for m in range(36, 65)]))
        (OUT / 'manifest.json').write_text(json.dumps(dict(version=1, concertA=440, range=[36, 64],
            guitarLicense='CC0: https://shop.karoryfer.com/pages/free-samples',
            pianoLicense='https://theremin.music.uiowa.edu/MIS.html', samples=records), indent=2) + '\n')
    verify()

# Banco de piano para Simón

El usuario pidió el 2026-10-02 sustituir los tonos sintetizados por muestras `mf` del [piano de University of Iowa](https://theremin.music.uiowa.edu/MISpiano.html), descargando solo las notas usadas y convirtiéndolas a OGG.

## Procedencia y uso

University of Iowa Electronic Music Studios, Musical Instrument Samples (MIS). Piano Steinway & Sons model B, intérprete Evan Mazunik, técnico Michael Cash; grabaciones de noviembre de 2001 según la página del instrumento. Originales AIFF estéreo, 16 bits/44,1 kHz.

La [página MIS](https://theremin.music.uiowa.edu/MIS.html), consultada el 2026-10-02, autoriza descargar y usar las grabaciones en cualquier proyecto sin restricciones. Se conserva atribución y procedencia; no se asigna una licencia Creative Commons inventada. No son grabaciones producidas por la tesis.

## Selección y conversión

Las tónicas actuales de la app abarcan MIDI 60–71. La escala cromática alcanza once semitonos por encima de la tónica: el conjunto mínimo de notas usadas es MIDI 60–82, Do4–Si♭5, **23 muestras, todas `mf`**. La grafía de Iowa para alteraciones es bemol (`Db`, `Eb`, `Gb`, `Ab`, `Bb`); la interfaz puede mostrar el sostenido equivalente.

Script reproducible: `Plataforma/tools/prepare_piano.py`. Requiere Python, numpy, python-soundfile/libsndfile con Vorbis y curl. No necesita FFmpeg ni dependencias Python para ejecutar la app.

```powershell
python -m pip install numpy soundfile
cd Plataforma
python tools/prepare_piano.py
```

El script obtiene los enlaces del índice oficial, descarga únicamente los 23 AIFF necesarios a `Plataforma/build/piano-sources/` (ignorado en Git) y genera `app/src/main/assets/piano/mf_<midi>.ogg`. Comprueba frecuencia, longitud, ausencia de muestras inválidas y decodificación del OGG. `manifest.json` conserva URL y SHA-256 del original y del derivado, tamaño y recorte por nota.

Conversión: mezcla estéreo a mono, 44,1 kHz, detección del ataque conservando 10 ms anteriores, recorte de 600 ms, fade final de 70 ms, ganancia común ×5 sin normalización individual y Vorbis con `compression_level=0.65`. La ganancia preserva las diferencias entre notas; se rechaza un recorte con pico previo a compresión ≥0,95. Son elecciones técnicas provisionales para este prototipo.

Tamaños observados: 101.529.674 bytes de AIFF originales → **190.589 bytes de OGG** en total (aproximadamente 186 KiB). La reducción incluye tanto el recorte de las grabaciones largas como la compresión, no es una comparación de códecs a igual duración.

Los archivos fuente no se incluyen en el APK ni se versionan. Los OGG y su manifiesto sí se incluyen para funcionar sin red.

## Validación

Cada OGG se reabrió con libsndfile y conserva 26.460 frames mono a 44,1 kHz. En Do4 se observó energía espectral principal alrededor de 262 Hz (FFT con resolución de 4 Hz), coherente con la nota; esto no es una medición de precisión acústica del dispositivo ni una evaluación completa de afinación del banco.

La reproducción Android debe comprobar decodificación del OGG real y avance de frames de salida. La escucha en el S22 Ultra y el ajuste perceptual de duración/volumen quedan a cargo del próximo recorrido manual.

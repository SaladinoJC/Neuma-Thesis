# Banco de audio de «¿Qué instrumento es?»

Este documento conserva el banco de nota aislada usado en v0.3. Desde v0.3.1 se usan melodías generadas con cinco notas reales por instrumento; procedencia, conversión y política actual en [Melodias-instrumentos-v0.3.1.md](Melodias-instrumentos-v0.3.1.md). Los resultados históricos conservan el banco v1.

Preparado el 2026-10-02 para el incremento [#3](https://github.com/SaladinoJC/Tesis/issues/3). Elecciones técnicas provisionales del agente, pendientes de escucha en el S22 Ultra y calibración con el director.

## Procedencia y permiso

Las grabaciones son de [University of Iowa Musical Instrument Samples](https://theremin.music.uiowa.edu/MIS.html), creadas por Lawrence Fritts y su equipo. La página de la colección permite descargar y usar los archivos en proyectos sin restricciones. Se consultó el 2026-10-02; no se atribuye una licencia Creative Commons que la fuente no declara. Se conserva atribución aquí y en el manifiesto. No se enviaron mensajes al autor.

Se descargaron únicamente los archivos `mf` necesarios: una nota de piano y ocho archivos que contienen escalas cromáticas. Los originales y las páginas fuente están en `Plataforma/build/instrument-sources/`, ignorados por Git y excluidos del APK. El manifiesto conserva enlaces exactos, SHA-256 de fuentes y OGG, recortes, ganancias, formatos y valores medidos.

## Catálogo

| Nivel manual | Opciones |
| --- | --- |
| A: familias distintas | Piano, violín, flauta, marimba |
| B: vientos de madera y metal | Flauta, clarinete, trompeta, trombón |
| C: vientos de madera | Flauta, clarinete, oboe, fagot |

Este banco concreta el catálogo candidato de la especificación. La marimba sustituye el ejemplo de batería de la ficha porque tiene altura definida y puede compartir la nota de control. El orden de dificultad sigue siendo una hipótesis, no una equivalencia perceptual comprobada.

| Instrumento | Archivo original | Inicio seleccionado (s) | Frecuencia estimada (Hz) | Desviación Do4 (cents) |
| --- | --- | ---: | ---: | ---: |
| [Piano](https://theremin.music.uiowa.edu/MISpiano.html) | `Piano.mf.C4.aiff` | 0,33 | 262,64 | +6,69 |
| [Violín](https://theremin.music.uiowa.edu/MISviolin.html) | `Violin.arco.mf.sulG.B3B4.aiff` | 12,66 | 263,28 | +10,94 |
| [Flauta](https://theremin.music.uiowa.edu/MISflute.html) | `Flute.nonvib.mf.B3B4.aiff` | 2,86 | 259,23 | −15,94 |
| [Marimba](https://theremin.music.uiowa.edu/Mismarimba.html) | `Marimba.cord.mf.C4B4.aif` | 0,65 | 263,71 | +13,73 |
| [Clarinete](https://theremin.music.uiowa.edu/MISBbclarinet.html) | `BbClar.mf.C4B4.aiff` | 2,21 | 260,91 | −4,72 |
| [Trompeta](https://theremin.music.uiowa.edu/MISBbtrumpet.html) | `Trumpet.novib.mf.C4B4.aiff` | 2,92 | 261,80 | +1,14 |
| [Trombón](https://theremin.music.uiowa.edu/MIStenortrombone.html) | `TenorTrombone.mf.C4B4.aiff` | 2,02 | 262,75 | +7,45 |
| [Oboe](https://theremin.music.uiowa.edu/MISoboe.html) | `Oboe.mf.C4B4.aiff` | 0,12 | 260,52 | −7,36 |
| [Fagot](https://theremin.music.uiowa.edu/MISbassoon.html) | `Bassoon.mf.C4B4.aiff` | 0,04 | 261,47 | −1,05 |

Se seleccionó Do4 por su disponibilidad real en los nueve instrumentos. La altura nominal compartida no significa afinación idéntica: las grabaciones conservan sus desviaciones naturales. En especial, las muestras de clarinete y trompeta se comprobaron acústicamente en lugar de asumir la nota por ser instrumentos transpositores.

## Conversión y comprobación

Script reproducible: `Plataforma/tools/prepare_instruments.py`. Requiere Python, NumPy, SciPy, SoundFile y curl con TLS; no son dependencias de la app.

```powershell
cd Plataforma
python tools/prepare_instruments.py
```

Se promedia a mono. La marimba original es estéreo de 24 bits/96 kHz; se remuestrea con filtro polifásico `resample_poly(147, 320)` a 44,1 kHz, sin modificar la altura. Los demás originales están a 44,1 kHz. Se detectan regiones sonoras por RMS en bloques de 10 ms; el recorte conserva 10 ms anteriores al ataque detectado. Se elige la región más próxima a Do4 mediante autocorrelación normalizada de una ventana de 200 ms desde 150 ms después del inicio, reducida por factor cuatro e interpolada parabólicamente. El control requiere correlación ≥ 0,7 y desviación ≤ 25 cents. Es un control de selección de material, no una evaluación del estimador vocal ni una medición de hardware.

Cada estímulo ocupa 66.150 frames: 1,5 s, mono, 44,1 kHz. Conserva el ataque y el decaimiento hasta el límite del recorte o de la región detectada; los sonidos que terminan antes se completan con silencio. Se aplica ganancia constante por muestra para RMS objetivo 0,08 antes del fade, fade de 80 ms al final de la región sonora y conversión Vorbis con compresión 0,65. No hay transposición, compresión dinámica ni recorte por saturación. RMS eléctrico comparable no acredita sonoridad perceptual idéntica; esta última debe revisarse escuchando en el dispositivo. Los nueve OGG ocupan 142.201 bytes (~139 KiB).

Identificador del banco: `iowa-c4-mf-v1`. Catálogo completo: `Plataforma/app/src/main/assets/instruments/manifest.json`. El banco se incluye en el APK y funciona offline. No se modifica el banco de piano de Simón.

# Melodías generadas para reconocimiento tímbrico

## Cambio y origen

El usuario informó el 2026-10-09 que probó v0.3 en el celular y pidió reemplazar el Do4 repetido por una melodía breve generada aleatoriamente al elegir el instrumento. No detalló condiciones ni incidencias de esa prueba. Esta solicitud actualiza el estímulo de la ficha 15 y de la especificación v1.

En v0.3.1 cada pregunta sortea un instrumento y genera una frase independiente de su identidad. Al reescuchar se conserva exactamente la misma frase; la siguiente pregunta cambia la melodía incluso si vuelve a salir el mismo instrumento. Se mantienen las diez preguntas, cuatro opciones, niveles manuales y tres escuchas totales. Una frase completa consume una escucha.

## Gramática inicial del agente, pendiente de calibración

- Seis notas en la pentatónica de Do mayor: Do4, Re4, Mi4, Sol4 y La4; al menos tres alturas distintas.
- Inicio aleatorio y movimiento sin repetir notas consecutivas, con saltos de hasta cinco semitonos.
- Penúltima nota Re4 o Mi4, cierre en Do4. Si el azar repite la frase anterior, se sortea una variación que conserva esas restricciones.
- Cinco notas de 400 ms y final de 800 ms; separaciones de 50 ms, sin silencio adicional al final. Duración total: 3,05 s.
- Misma gramática y duración para todos los instrumentos y niveles. No hay una melodía característica asignada a cada instrumento.

Estos valores concretan «melodía lógica» como coherencia tonal, movimiento acotado y cierre. Son elecciones técnicas provisionales del agente, no una validación musicológica o pedagógica ni decisiones del director. La variación melódica agrega una variable a la comparación con resultados de v0.3; las versiones quedan identificadas en el historial.

## Audio real y procedencia

Se usan grabaciones `mf` de [University of Iowa Musical Instrument Samples](https://theremin.music.uiowa.edu/MIS.html). Se volvió a comprobar el 2026-10-09 que la página permite descargarlas y usarlas en proyectos sin restricciones. No se atribuye una licencia Creative Commons ni se enviaron mensajes al autor.

El banco `iowa-pentatonic-mf-v2` tiene 45 OGG: cinco notas reales para cada uno de los nueve instrumentos. Se reutilizan los originales de v0.3 y los del piano de Simón; solo se agregó el archivo de violín [`Violin.arco.mf.sulD.D4A4.aiff`](https://theremin.music.uiowa.edu/sound%20files/MIS/Strings/violin/Violin.arco.mf.sulD.D4A4.aiff) para Sol4 y La4. Do4, Re4 y Mi4 de violín provienen de `sulG`; el cambio de cuerda entre registros debe revisarse perceptualmente. Las notas Sol4 y La4 del original `sulG` tenían una desviación demasiado grande para el control propuesto. No se transpone ni se cambia la velocidad de las muestras.

Preparación reproducible: `Plataforma/tools/prepare_instrument_melodies.py`, sobre el código de descarga/segmentación de `prepare_instruments.py`. En mono, la marimba se remuestrea de 96 a 44,1 kHz con filtro polifásico. Se detectan regiones por RMS en bloques de 10 ms; se unen pausas inferiores a 100 ms, salvo el fagot, que requiere 30 ms para separar notas con pausas cortas. El recorte empieza 10 ms antes del ataque detectado.

El control de selección estima frecuencia mediante autocorrelación normalizada en tres ventanas de 200 ms, desde 150, 350 y 550 ms del inicio. Usa el mayor máximo local en el rango 240–800 Hz y la mediana de las frecuencias; acota así subarmónicos por debajo del registro previsto. Se mide la cola de la fuente aunque caiga por debajo del umbral de segmentación. Se exige correlación mínima 0,7 y desviación máxima de 30 cents respecto de la nota nominal. Las grabaciones conservan afinación natural. Es un control offline del material, no una medición de hardware ni una evaluación del futuro YIN vocal.

Cada OGG ocupa 39.690 frames (900 ms, mono/44,1 kHz). Se completa con silencio si la región detectada termina antes; ganancia constante por nota para RMS objetivo 0,08 antes del fade de 80 ms. Vorbis con compresión 0,65, sin compresión dinámica. El manifiesto guarda enlaces, hashes SHA-256, recortes, ganancias y medidas: `Plataforma/app/src/main/assets/instruments/melody/manifest.json`. Los originales permanecen en `Plataforma/build/` y no se empaquetan.

La app decodifica y conserva el PCM por nota fuera del hilo de interfaz. Compone la frase en memoria, recorta cada nota a su duración y aplica fade de 40 ms al final antes de insertar la separación. Reproduce todo con una sola `AudioTrack`, liberada al finalizar, fallar o cancelar; la notificación de inicio se emite una sola vez cuando avanza el audio. El tiempo máximo de reproducción depende de la duración de la frase. Simón sigue usando sus muestras y duraciones previas.

## Registro y compatibilidad

Cada respuesta conserva notas MIDI, duraciones, separación y versión del generador `pentatonic-c4-v1`. Los resultados nuevos declaran `stimulusKind: generated-melody`, banco v2 y configuración `instruments-0.3.1-pentatonic-v1`; `midi` pasa a `null` porque no hay una única altura.

Al leer un resultado de v0.3 se conserva su banco v1 y MIDI 60; se identifica como `single-note` y su melodía es `null`. No se inventa una frase para datos históricos. El esquema del archivo sigue en versión 1 con campos nuevos opcionales; el lector de v0.3.1 acepta el historial de v0.3. No se asegura que versiones antiguas acepten datos escritos por v0.3.1.

## Verificación reproducible

Desde `Plataforma/`:

```powershell
python tools/prepare_instrument_melodies.py
python tools/verify_instrument_melodies.py
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest
```

La preparación usa archivos cacheados si están disponibles; la descarga inicial requiere red. Python/NumPy/SciPy/SoundFile son herramientas de preparación, no dependencias de Android.

El recorrido físico y los resultados de las comprobaciones están en `docs/Verificacion-Instrumentos-v0.3.1.md`. El balance RMS no acredita igualdad de sonoridad percibida y el banco recortado no sustituye una interpretación instrumental continua. Escuchar en el S22 Ultra permitirá valorar ataques, articulación, duración y continuidad de estas frases.

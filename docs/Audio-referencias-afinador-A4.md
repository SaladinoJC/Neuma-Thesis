# Referencias del afinador A4

Elección técnica provisional del agente para #12, autorizada la implementación por el usuario el 2026-10-26. Tres timbres offline, guitarra predeterminada, para el rango **MIDI 36–64 / Do2–Mi4**, temperamento igual y La4=440 Hz. Cubre las seis afinaciones predefinidas y todas las personalizadas admitidas por A2. Un objetivo cromático fuera de ese rango se rechaza sin transportar octava ni detener la captura existente.

## Fuentes y permisos

- **Guitarra eléctrica limpia:** Emilyguitar, D. Smolken / Karoryfer Lecolds. El [readme fijado del original](https://github.com/sfzinstruments/karoryfer.emilyguitar/blob/b4920dc662fd9cad6dcaccdeecffdd91c8725d8c/readme.txt) describe una Epiphone Emily the Strange grabada directamente, cuerdas flatwound y uso libre de regalías. La [declaración actual del autor](https://shop.karoryfer.com/pages/free-samples), consultada el 2026-10-26, incluye Emilyguitar y asigna CC0 a sus bibliotecas gratuitas, aclarando que las descargas antiguas pueden tener permisos previos. Se conserva esta distinción. No se presenta como guitarra acústica ni como clonación del instrumento del usuario.
- **Piano:** muestras `mf` de [University of Iowa](https://theremin.music.uiowa.edu/MISpiano.html), una fuente por nota. La [página MIS](https://theremin.music.uiowa.edu/MIS.html), consultada el 2026-10-26, permite su descarga y uso en cualquier proyecto sin restricciones. No se inventa una licencia CC. El banco de A4 es independiente del de Simón.
- **Tono puro:** seno generado por `referenceTone`, sin fuente externa ni assets; 600 ms, amplitud 0,2, fades de 20 ms, mono/44,1 kHz.

## Preparación y verificación

```powershell
python Plataforma/tools/prepare_tuner_references.py
python Plataforma/tools/prepare_tuner_references.py --verify
```

Dependencias: Python, NumPy, SciPy y SoundFile/libsndfile con Vorbis. Los originales quedan en `Plataforma/build/tuner-sources/`, ignorados. El script serializa las descargas compartidas y prepara los derivados con cuatro trabajadores. Guitarra fijada al commit `b4920dc662fd9cad6dcaccdeecffdd91c8725d8c`; originales y derivados quedan identificados por SHA-256.

Guitarra: diez fuentes `mf/rr1` entre Reb2 y Mib4, la más cercana a cada objetivo, con transposición aplicada máxima de un semitono. Do2 usa Reb2. El LEEME del banco declara una cota conservadora de dos semitonos; las diferencias exactas constan en el manifiesto. Piano: 29 originales, sin transporte de nota, con corrección fina. Se convierten a mono/44,1 kHz, se localiza el comienzo, se estima fundamental por autocorrelación normalizada entre 150 y 650 ms cerca de la altura nominal, con interpolación parabólica, y se remuestrea a la frecuencia objetivo. La interpolación modifica también la duración y el timbre; es una referencia preparada, sin preservar la afinación original.

Duración final de muestras: 1200 ms / 52.920 frames; RMS objetivo 0,08 limitado por pico 0,85, fades de 10 y 80 ms. Vorbis, nivel 0,65. Son **58 OGG / 682.763 bytes**. El [manifiesto](../Plataforma/app/src/main/assets/tuner/manifest.json) registra URL, nota fuente/objetivo, hashes, recorte, razón de remuestreo, ganancia y medición por archivo. La verificación sobre OGG decodificado exige mono/44,1 kHz/52.920 frames, pico menor a 0,95 y desviación menor a 2 cents con ese método; máximo observado **0,831 cents**. Esta autocorrelación está guiada por el objetivo: por sí sola no descarta errores de octava. La prueba Android añade YIN sin esa guía en tres ventanas por muestra.

## Exclusión de captura y referencia

Al pulsar Escuchar se invalida la generación de captura, se limpian lectura y estabilidad, y se espera la finalización y liberación del capturador antes de abrir la salida. AudioTrack espera el avance real de frames y se libera en `finally`. Luego se mantiene el micrófono cerrado **600 ms** antes de crear una captura nueva, con ventana YIN nueva. Solo se reanuda si estaba activo; escuchar con micrófono detenido no lo inicia. Los callbacks de generaciones previas no se aceptan.

Guiado conserva objetivo, fase y resultados de la pasada; la suspensión no aprueba, falla ni acumula estabilidad. Repetir el botón mientras suena no superpone reproducciones. Detener, salir, pausar o cambiar configuración cancela la referencia y evita la reanudación; un fallo queda visible y requiere inicio explícito. Cambiar solo el timbre guarda la preferencia sin interrumpir el recorrido. La configuración sigue en esquema 1 con campo opcional `referenceTimbre`; archivos A2/A3 usan guitarra por defecto y participan en exportación/recuperación/reinicio existentes.

Los 600 ms son un **valor provisional**, no una medición de reverberación o latencia acústica. Android verifica liberación, frames reproducidos y separación temporal antes de reabrir captura. Audibilidad, equilibrio de volumen y suficiencia del margen con altavoz/Bluetooth/S22 Ultra siguen pendientes de medición física. No se afirma precisión ±2 cents sobre guitarra real ni eficacia pedagógica.

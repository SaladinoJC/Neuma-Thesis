# Verificación de melodías de instrumentos v0.3.1

Fecha: 2026-10-09. Cambio solicitado por el usuario al informar que probó v0.3 en el celular: generar una melodía breve aleatoria por pregunta en lugar de repetir una nota aislada. Diseño y procedencia en [Melodias-instrumentos-v0.3.1.md](Melodias-instrumentos-v0.3.1.md).

## Comprobación automatizada

Windows, JBR de Android Studio y SDK Android local. Emulador Pixel_6/API 34 (Android 14), `emulator-5554`, ejecutado sin ventana ni salida acústica. No se conectó el S22 Ultra.

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
$env:ANDROID_SERIAL = 'emulator-5554'
.\gradlew.bat --offline :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest
```

Resultado final: **BUILD SUCCESSFUL en 4 min 14 s**. 25 pruebas JVM: 10 de sesión de instrumentos, 3 de su ViewModel, 8 de sesión de Simón y 4 de su ViewModel. 11 pruebas Android: 3 de instrumentos, 7 de Simón y 1 de teclado. Cero fallos, errores o pruebas omitidas. Lint: cero errores y 14 advertencias. Se conservan los avisos de herramientas SDK y de funciones de Gradle obsoletas ya observados; no impidieron compilar.

Pruebas sobre la sesión pública:

- Cada respuesta registra una frase de seis notas del conjunto disponible, al menos tres alturas distintas, saltos acotados, duraciones y cierre en Do4; persistencia y reapertura conservan esos datos.
- Las tres escuchas repiten la misma frase; una cuarta no reproduce audio. La siguiente pregunta cambia la melodía aunque el objetivo vuelva a ser piano.
- Un archivo JSON literal de v0.3 se lee con su nota aislada y sin inventar melodía. Agregar una partida nueva y reabrir conserva ambas.
- Caso de azar reiterado con una primera nota única: evitar la repetición no deja al generador sin alternativas ni bloquea la siguiente pregunta.
- Regresiones previas de respuestas, ayuda, interrupciones, cancelación, timeout y guardado final.

La prueba Android del banco reproduce una frase conocida `Do4–Mi4–La4–Sol4–Re4–Do4` con los nueve instrumentos, recorriendo las 45 muestras. Cada frase avanza al menos **134.505 frames** (3,05 s) y emite exactamente una notificación de escucha. También pasa el recorrido de diez preguntas con reescuchas, recreación, resumen e historial; se verifican niveles manuales y salida durante presentación. Se mantienen las pruebas reales de audio/teclado de Simón.

Se observaron ciclos rojo → verde para ausencia de melodía persistida, reproducción anterior de una sola nota y el caso de azar que dejaba vacía la primera lista de alternativas. La corrección final considera variaciones válidas en varias posiciones de la frase.

## Control del banco offline

`python tools/prepare_instrument_melodies.py` completó las 45 notas. `python tools/verify_instrument_melodies.py` comprobó inventario, SHA-256, decodificación mono/44,1 kHz/39.690 frames, ausencia de NaN/infinito y límites de nivel. Resultados:

| Medida | Resultado |
| --- | --- |
| OGG del banco nuevo | 497.231 bytes (~486 KiB) |
| RMS decodificado | 0,07420–0,08060 |
| Pico máximo decodificado | 0,45404 |
| Desviación estimada en las fuentes | −23,29 a +23,80 cents |

El banco v1 permanece disponible como antecedente. Los enlaces exactos, recortes y ganancias del v2 están en su manifiesto. Estos valores son mediciones de archivos; no describen volumen acústico, latencia ni eficacia pedagógica.

## Inspección manual de interfaz

Tras las pruebas se instaló el APK mediante `adb install -r`, se abrió el menú, la preparación y una pregunta del nivel A. Capturas en [prototipo-v0.3.1](imagenes/prototipo-v0.3.1/): menú con versión, instrucciones actualizadas, niveles y botón de inicio, y presentación de la melodía con escuchas restantes. Preparación y pregunta caben en la pantalla observada. La captura de la pregunta se tomó durante reproducción, con opciones deshabilitadas y dos escuchas restantes.

Durante la inspección posterior apareció un aviso «System UI isn't responding» del emulador; se seleccionó Wait y se continuó el recorrido. El aviso era de System UI; no se investigó su causa y no se atribuye a la app. Las once pruebas Android ya habían finalizado sin fallos. No se hizo una escucha acústica en ese emulador.

## APK

`Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.3.1.apk`

- Versión 0.3.1, versionCode 5, paquete `ar.edu.unmdp.entrenamiento`, firma debug habitual.
- Tamaño: 11.940.300 bytes.
- SHA-256: `90f08fd0a8b4e668cb37d189050b7e17356f2cb7df5abeda93376769d45aabb5`.
- Instalar encima de v0.3, sin desinstalar, para conservar datos. La compatibilidad del historial de instrumentos se comprobó con una fixture de v0.3; se conservan las regresiones del formato de Simón.

## Recorrido pendiente en S22 Ultra

1. Actualizar sin desinstalar y revisar ambos historiales.
2. Jugar A, B y C: comprobar frases distintas entre preguntas, incluso al repetirse un instrumento.
3. Reescuchar dos veces una pregunta: la frase debe ser exactamente la misma y consumir una escucha por reproducción completa.
4. Valorar ataques, continuidad, duración y balance entre instrumentos/notas. En violín, comprobar especialmente el paso entre las muestras de cuerda Sol y cuerda Re.
5. Salir durante una melodía y girar/recrear: conservar la conducta previa de interrupción al salir y sesión al girar. Completar diez preguntas y revisar resumen/historial.

Registrar versión Android, altavoz/auriculares, volumen, ruido ambiente y observaciones concretas. El usuario informó una prueba de v0.3; eso no acredita el funcionamiento físico de v0.3.1. No se midieron latencia acústica ni eficacia pedagógica. Gramática y tiempos siguen siendo propuestas del agente pendientes de calibración.

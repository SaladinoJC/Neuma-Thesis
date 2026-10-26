# Aplicación Android

Versión 0.12.1: agrega únicamente **Afinador A3 / #11**, con recorrido guiado sexta → primera, revisión final opcional recordada y corrección de cuerdas pendientes. El resumen distingue ambas pasadas y permanece en memoria; recrear, pausar o salir interrumpe la captura sin completar el recorrido. Los cambios de Ritmo R2 y Defender D2 siguen guardados aparte. Ver [guía y verificación A3](../docs/Demostracion-afinador-A3.md). Instalar encima de v0.12 conserva los datos; las tolerancias siguen siendo experimentales.

Versión 0.12: incorpora **Afinador con perfiles, seis presets y afinaciones personalizadas**, **Rhythm Tap** de cuatro compases con negras/corcheas y **Defender de notas** en clave de sol. Los tres módulos comparten menú y ciclo de vida; la configuración del afinador participa en exportación JSON, recuperación ZIP y reinicio. Ritmo y Defender muestran resúmenes de práctica en memoria; su persistencia y DDA siguen en los tickets posteriores. [Recorrido](../docs/Demostracion-prototipo-v0.12.md) y [verificación integrada](../docs/Verificacion-prototipo-v0.12.md).

APK: `build/apk-archives/entrenamiento-auditivo-v0.12.apk`. Mismo paquete y certificado debug que v0.11: instalar encima, sin desinstalar. Se conservan las funciones de la base v0.10 y el afinador Libre de v0.11 ([antecedente](../docs/Demostracion-afinador-v0.11.md)). Guiado, referencias y entrenamiento siguen en #11–#14; los criterios Normal/Pro y el DDA actual son experimentales y requieren comprobación física.

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*tuner*'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.TunerAndroidTest'
```

Usar únicamente un emulador para pruebas instrumentadas; para teléfono seguir el recorrido manual. Configurar `JAVA_HOME`, `ANDROID_HOME` y `ANDROID_SERIAL` como en la verificación reproducible de abajo.

Versión 0.10: Simón y «¿Qué instrumento es?» con progresión automática persistida, inspección Mamdani y práctica manual. Incluye historial completo con filtros y detalle, laboratorio YIN de 83 casos, referencia sonora/gráfica de voz y banco DDA de nueve escenarios. «Datos y demostración» exporta JSON y originales ZIP para recuperación, y permite reiniciar el perfil con confirmación. La voz conserva en memoria un guardado fallido y permite reintentarlo. Las reglas son experimentales; la prueba física del S22 Ultra sigue pendiente. [Guía actual](../docs/Demostracion-prototipo-v0.10.md) y [verificación por incremento](../docs/Verificacion-iteraciones-post-v0.5.md).

El piano usa 23 muestras `mf` de University of Iowa, Do4–Si♭5, incluidas como OGG mono a 44,1 kHz, recortadas a 600 ms con fade final. Los OGG ocupan aproximadamente 186 KiB en total. `MediaCodec` las decodifica fuera del hilo de interfaz; el PCM de cada nota presentada se decodifica una vez, se conserva en memoria y se reproduce con `AudioTrack`. Las teclas usan `SoundPool` precargado, con hasta 16 colas simultáneas y volumen de reproducción 0,8, sin bloquear la entrada por los 600 ms de duración. La entrada de notas se mantiene ordenada e independiente del audio; al exceder 16 colas, se reemplazan las más viejas, no se descartan respuestas por ese límite. Procedencia, condiciones y script de conversión en [Audio-piano-Simon.md](../docs/Audio-piano-Simon.md). No se necesita conexión para jugar.

Los resultados se guardan en almacenamiento privado como JSON versionado. El menú muestra las cinco partidas más recientes; «Ver todos los resultados» permite consultar todas en bloques de 20, filtrar y abrir detalles. Los archivos originales se conservan al consultar. El de Simón mantiene su esquema anterior; el de instrumentos incluye cada respuesta, opciones, objetivo, acierto, escuchas, nivel, fechas y versión del banco. Las salidas al menú o a segundo plano se guardan como interrupciones. La nueva versión mantiene el identificador de la app y su firma debug para actualizar el APK anterior sin desinstalarlo y conservar su historial.

Instrumentos utiliza 45 muestras reales `mf` de Iowa: Do4, Re4, Mi4, Sol4 y La4 para cada uno de los nueve instrumentos. Los OGG mono/44,1 kHz de 900 ms ocupan 497.231 bytes (~486 KiB), además del banco histórico v1 conservado. El generador usa seis notas, saltos acotados, al menos tres alturas distintas y cierre en Do4: cinco notas de 400 ms, final de 800 ms y separaciones de 50 ms (3,05 s en total). La gramática es común a los instrumentos; sus parámetros son provisionales. La app compone una única pista PCM con fade al terminar cada nota, sin transposición, y registra la frase por respuesta. El contador confirma una escucha cuando avanza AudioTrack; si falla antes de comenzar, no consume ayuda. Un fallo posterior conserva la escucha consumida. [Procedencia, conversión y mediciones](../docs/Melodias-instrumentos-v0.3.1.md). El modo automático adapta las escuchas; la selección manual A/B/C conserva ayuda fija. Los desbloqueos están disponibles desde v0.5.

## Preparación

El DDA Mamdani `mamdani-help-v1` evalúa las últimas tres respuestas desde la tercera. Considera exactitud y autonomía (aciertos con primera escucha entre aciertos), sin utilizar tiempo de respuesta. Cambia como máximo una escucha, solo en la próxima pregunta, con dos rondas entre cambios. El panel DDA permite recorrer decisiones reales e inspeccionar cuatro escenarios preparados, con nivel A/B/C elegido manualmente dentro de la simulación. Los casos simulados no reproducen sonido ni escriben en el historial real. Parámetros, reglas y resolución son valores de prueba pendientes de calibración: [DDA v0.4](../docs/DDA-instrumentos-v0.4.md).

Abrir esta carpeta en Android Studio. Se requieren SDK 36, build-tools 35, JDK 17 o 21 y acceso inicial a los repositorios de dependencias. El proyecto incluye Gradle Wrapper 8.14.3 con checksum de distribución, AGP 8.11.0 y Kotlin/Compose compiler 2.2.10. Es compatible desde Android 8 (API 26).

Configurar el SDK desde Android Studio o mediante `ANDROID_HOME`; las rutas locales no se versionan. Ejemplo PowerShell para esta máquina:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Ejecutar

Seleccionar la configuración `app` y un dispositivo en Android Studio y pulsar Run. Alternativamente:

```powershell
.\gradlew.bat :app:assembleDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n ar.edu.unmdp.entrenamiento/.MainActivity
```

Si hay varios dispositivos, agregar `-s <serial>` a los comandos ADB. El S22 Ultra requiere depuración USB habilitada y autorizar el equipo. Simón e instrumentos no requieren micrófono. La prueba de voz solicita permiso al iniciarse.

## Verificación

```powershell
# Sesión: secuencia, respuestas, puntaje y persistencia
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonSessionTest'

# Regresión de salida al menú durante guardado
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonViewModelTest'

# Android: solo sobre un emulador de pruebas; las pruebas reinician datos
$env:ANDROID_SERIAL = "emulator-5554"
.\gradlew.bat :app:connectedDebugAndroidTest
```

Recorrido manual: abrir el menú, elegir Simón, seleccionar escala e iniciar partida. Escuchar el piano, repetir la secuencia en sus teclas y comprobar que crece. Equivocarse, revisar longitud completada y error, volver al menú, cerrar/reabrir y comprobar el historial. Abandonar otra partida y comprobar que figura como interrumpida. Comprobar que la preparación y el teclado entran completos en horizontal, repetir rápidamente una secuencia, girar el teléfono hacia ambos lados sin perder la partida y comprobar vuelta al menú vertical. Probar las tres escalas, teclas blancas/negras y notas atenuadas fuera de escala, volumen multimedia, vuelta al menú y salida de la app durante reproducción.

Los informes de pruebas y lint se generan en `app/build/reports/`. Las verificaciones de emulador deben identificarse como tales; no demuestran audibilidad, latencia o funcionamiento en el teléfono físico.

Consultar el [registro de verificación v0.2.1](../docs/Verificacion-Simon-v0.2.1.md) para este ajuste y el recorrido pendiente en el S22 Ultra. El [registro v0.2](../docs/Verificacion-Simon-v0.2.md) conserva la verificación inicial del menú y piano. El [registro v0.1](../docs/Verificacion-Simon-v0.1.md) conserva la verificación del primer APK.

## Verificar instrumentos

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*InstrumentSessionTest' --tests '*InstrumentViewModelTest'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.InstrumentAndroidTest'
```

Recorrido: elegir «¿Qué instrumento es?» en el menú; seleccionar A, B o C e iniciar. Comprobar primera reproducción automática y dos escuchas restantes. Agotar las dos reescuchas y comprobar que el botón se deshabilita. Responder y revisar el instrumento correcto; seguir hasta diez preguntas y comprobar resumen e historial al salir/reabrir. Abandonar una segunda partida durante reproducción: debe guardarse como interrumpida. La recreación por configuración conserva la sesión; salir realmente de la app la interrumpe. El tiempo de respuesta no modifica el resultado. En el S22 Ultra revisar audibilidad, timbre, equilibrio de volumen y comodidad de los botones.

La versión 0.3.1 usa el mismo paquete y firma debug que v0.3: instalar encima, sin desinstalar, conserva los historiales. Las partidas nuevas guardan la melodía; las anteriores conservan MIDI 60 y se identifican como nota aislada. Registro actual: [Instrumentos v0.3.1](../docs/Verificacion-Instrumentos-v0.3.1.md); antecedente: [Instrumentos v0.3](../docs/Verificacion-Instrumentos-v0.3.md).

Para comprobar el banco nuevo: `python tools/verify_instrument_melodies.py`. Para prepararlo desde los originales cacheados o descargar los que falten: `python tools/prepare_instrument_melodies.py` (Python/NumPy/SciPy/SoundFile). Comprobar en el teléfono que las reescuchas repiten la misma frase, las siguientes preguntas cambian la melodía y el volumen/ataque/articulación resultan adecuados en los tres niveles.

## Verificar adaptación e inspección DDA

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*InstrumentDdaSessionTest' --tests '*InstrumentViewModelTest'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.InstrumentDdaAndroidTest'
```

En preparación, elegir Automático e iniciar: nivel A y tres escuchas iniciales. Contestar y revisar la ayuda anunciada para la próxima pregunta; en DDA → Partida real, recorrer las preguntas y contrastar entradas, pertenencias, reglas, centroide y motivo. En DDA → Simulación, elegir nivel y «Aciertos sin reescuchas»: cambios tras 3 y 6; «Aciertos y luego errores»: reducción tras 3 y recuperación tras 6; los otros dos escenarios mantienen ayuda. Cerrar el panel y comprobar que la partida real continúa igual. Seleccionar una tarjeta de nivel activa práctica manual; iniciar allí conserva tres escuchas. La nueva versión se instala encima de v0.3.1 y conserva los historiales. Registro: [Verificación DDA v0.4](../docs/Verificacion-DDA-v0.4.md).


## Verificar v0.5

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.apk`. Mismo paquete y firma debug: instalar encima para conservar datos. Abrir «Datos y demostración» para exportar resultados antes de un reinicio voluntario. Ver [demostración v0.5](../docs/Demostracion-prototipo-v0.5.md) y [verificación](../docs/Verificacion-prototipo-v0.5.md). La verificación física en S22 Ultra sigue pendiente.

## Verificación y paquete reproducible

Desde `Plataforma/`, con un emulador iniciado:

```powershell
.\tools\verify-prototype.ps1 -Serial emulator-5554
# Solo JVM/lint/APK, sin instalar en ningún dispositivo:
.\tools\verify-prototype.ps1 -SkipAndroid
```

El comando comprueba JDK/SDK y que el serial sea un emulador. Conserva log, informes comprimidos, APK, tamaño y SHA-256 en `build/verification/<fecha>/summary.json`. No admite teléfonos físicos: las pruebas Android reinician el perfil de la app. `ANDROID_SERIAL` queda configurado en la sesión de PowerShell; indicarlo nuevamente al cambiar de emulador.

El guardado JSON usa una barrera compartida por directorio, sustitución atómica e identificadores para deduplicar. Exportar el JSON genera una instantánea coherente. Un archivo ilegible o de esquema desconocido se conserva y bloquea el uso afectado; se puede reintentar la lectura o conservar los originales en ZIP antes de reiniciar. La recuperación copia bytes, no repara ni importa. El ZIP incluye los cinco JSON conocidos que existan (incluido `tuner-configuration.json`), el LEEME y, si corresponde, `pending-voice-save.json`; nunca grabaciones.

`pendingVoiceSave` en el JSON y `pending-voice-save.json` en el ZIP identifican mediciones derivadas pendientes de persistencia. No se mezclan con las sesiones guardadas. El pendiente vive en memoria y **no sobrevive a la muerte del proceso**; reintentar usa el mismo identificador. Reiniciar con confirmación también descarta ese pendiente. No se migra a Room ni se cambia la configuración YIN/DDA en esta entrega.

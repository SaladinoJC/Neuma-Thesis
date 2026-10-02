# Aplicación Android

Versión 0.3: menú principal con Simón y «¿Qué instrumento es?». Simón conserva el teclado de piano táctil y la selección manual de escala. Se abre en horizontal; preparación, teclado y resumen caben sin scroll. Las pulsaciones rápidas no esperan el sonido anterior y las rotaciones/recreaciones de pantalla conservan la sesión. El menú vuelve a vertical y muestra la versión instalada. Instrumentos permite seleccionar los niveles A/B/C y jugar diez preguntas, con cuatro opciones y tres escuchas totales por pregunta. La primera escucha comienza automáticamente; cada reproducción real consume una escucha y no se pueden superponer. Voz sigue pendiente. Las reglas se prueban mediante la sesión pública, independiente de Compose; DDA y desbloqueos siguen pendientes.

El piano usa 23 muestras `mf` de University of Iowa, Do4–Si♭5, incluidas como OGG mono a 44,1 kHz, recortadas a 600 ms con fade final. Los OGG ocupan aproximadamente 186 KiB en total. `MediaCodec` las decodifica fuera del hilo de interfaz; el PCM de cada nota presentada se decodifica una vez, se conserva en memoria y se reproduce con `AudioTrack`. Las teclas usan `SoundPool` precargado, con hasta 16 colas simultáneas y volumen de reproducción 0,8, sin bloquear la entrada por los 600 ms de duración. La entrada de notas se mantiene ordenada e independiente del audio; al exceder 16 colas, se reemplazan las más viejas, no se descartan respuestas por ese límite. Procedencia, condiciones y script de conversión en [Audio-piano-Simon.md](../docs/Audio-piano-Simon.md). No se necesita conexión para jugar.

Los resultados se guardan en almacenamiento privado como JSON versionado. El historial muestra las cinco partidas más recientes de cada juego, conservando todas en archivos separados. El de Simón mantiene su esquema anterior; el de instrumentos incluye cada respuesta, opciones, objetivo, acierto, escuchas, nivel, fechas y versión del banco. Las salidas al menú o a segundo plano se guardan como interrupciones. La nueva versión mantiene el identificador de la app y su firma debug para actualizar el APK anterior sin desinstalarlo y conservar su historial.

Instrumentos utiliza nueve grabaciones reales `mf` de Iowa con Do4 nominal compartido, incluidas como OGG mono/44,1 kHz de 1,5 s. El banco ocupa 142.201 bytes (~139 KiB). Las ganancias por muestra igualan aproximadamente el RMS eléctrico. El contador confirma una escucha cuando avanza AudioTrack; si la reproducción falla antes de comenzar, no consume ayuda. Un fallo después de comenzar conserva la escucha consumida. [Procedencia, conversión y mediciones](../docs/Audio-instrumentos.md). Los niveles son manuales y todavía no desbloquean ni ajustan ayuda.

## Preparación

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

Si hay varios dispositivos, agregar `-s <serial>` a los comandos ADB. El S22 Ultra requiere depuración USB habilitada y autorizar el equipo. No se necesitan permisos de micrófono para Simón.

## Verificación

```powershell
# Sesión: secuencia, respuestas, puntaje y persistencia
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonSessionTest'

# Regresión de salida al menú durante guardado
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonViewModelTest'

# Android, con un dispositivo o emulador conectado
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

La versión 0.3 usa el mismo paquete y firma debug que v0.2.1: instalar encima, sin desinstalar, conserva el historial de Simón. Registro de verificación: [Instrumentos v0.3](../docs/Verificacion-Instrumentos-v0.3.md).

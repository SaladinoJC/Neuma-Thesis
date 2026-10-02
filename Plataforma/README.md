# Aplicación Android

Versión 0.2: menú principal, preparación de Simón y teclado de piano táctil con selección manual de escala. La interfaz sigue `docs/ui simon.png`, con fondo oscuro y acentos rosas. Instrumentos y voz aparecen como próximos juegos, sin acceso todavía. Las reglas de juego se prueban mediante la sesión pública, independiente de Compose; DDA y desbloqueos siguen pendientes.

El piano usa 23 muestras `mf` de University of Iowa, Do4–Si♭5, incluidas como OGG mono a 44,1 kHz, recortadas a 600 ms con fade final. Los OGG ocupan aproximadamente 186 KiB en total. `MediaCodec` las decodifica fuera del hilo de interfaz; el PCM se conserva en memoria al usar cada nota y se reproduce con `AudioTrack`. Procedencia, condiciones y script de conversión en [Audio-piano-Simon.md](../docs/Audio-piano-Simon.md). No se necesita conexión para jugar.

Los resultados se guardan en almacenamiento privado como JSON versionado. El historial muestra las cinco partidas más recientes, conservando todas en el archivo. La nueva versión mantiene el identificador de la app y su firma debug para poder actualizar el APK anterior sin desinstalarlo y conservar su historial.

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

Recorrido manual: abrir el menú, elegir Simón, seleccionar escala e iniciar partida. Escuchar el piano, repetir la secuencia en sus teclas y comprobar que crece. Equivocarse, revisar longitud completada y error, volver al menú, cerrar/reabrir y comprobar el historial. Abandonar otra partida y comprobar que figura como interrumpida. Probar las tres escalas, teclas blancas/negras y notas atenuadas fuera de escala, volumen multimedia, vuelta al menú y salida de la app durante reproducción.

Los informes de pruebas y lint se generan en `app/build/reports/`. Las verificaciones de emulador deben identificarse como tales; no demuestran audibilidad, latencia o funcionamiento en el teléfono físico.

Consultar el [registro de verificación v0.2](../docs/Verificacion-Simon-v0.2.md) para los resultados de este incremento y el recorrido pendiente en el S22 Ultra. El [registro v0.1](../docs/Verificacion-Simon-v0.1.md) conserva la verificación del primer APK.

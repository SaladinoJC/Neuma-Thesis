# Aplicación Android

Primer incremento: Simón musical con selección manual de escala. Las reglas de juego se prueban mediante la sesión pública, independiente de Compose. Instrumentos, DDA y voz pertenecen a las siguientes issues.

Las notas se sintetizan localmente como senoidales mono PCM de 16 bits a 48 kHz, con duración de 300 ms y envolvente para suavizar los extremos. Son valores técnicos provisionales; no requieren muestras descargadas. Los resultados se guardan en almacenamiento privado como JSON versionado. El historial muestra las cinco partidas más recientes, conservando todas en el archivo.

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

Recorrido manual: iniciar partida, escuchar la secuencia, repetirla y comprobar que crece. Equivocarse, revisar longitud completada y error, volver al menú, cerrar/reabrir y comprobar el historial. Abandonar otra partida y comprobar que figura como interrumpida. Probar las tres escalas, volumen multimedia, vuelta al menú y salida de la app durante reproducción.

Los informes de pruebas y lint se generan en `app/build/reports/`. Las verificaciones de emulador deben identificarse como tales; no demuestran audibilidad, latencia o funcionamiento en el teléfono físico.

Consultar el [registro de verificación](../docs/Verificacion-Simon-v0.1.md) para los resultados de este incremento y el recorrido pendiente en el S22 Ultra.

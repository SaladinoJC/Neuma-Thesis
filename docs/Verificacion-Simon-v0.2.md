# Verificación de menú y piano v0.2

Fecha: 2026-10-02. Cambios solicitados por el usuario tras probar el APK v0.1: menú principal, preparación separada de Simón, piano táctil y muestras reales `mf` de Iowa comprimidas en OGG. Referencia visual: `ui simon.png`.

## Comportamiento

- Abrir la app muestra el menú «Entrená tu oído». Simón conduce a instrucciones y selección de escala; la partida comienza con «Jugar Simón».
- Piano con teclas blancas y negras en su posición musical. Las notas de la escala responden al apoyar el dedo, con etiquetas y resalte rosa; las notas ajenas a la escala quedan atenuadas e inactivas. Durante la escucha se bloquea la entrada del jugador.
- Las reglas siguen siendo secuencia creciente, tónica/escala fijas y primer error termina. No se añadieron vidas ni escalas a partir de la referencia gráfica.
- Instrumentos y voz aparecen como «Próximamente». No se implementaron esos juegos ni DDA en este cambio.
- VersionCode 2/versionName 0.2.0, mismo identificador y firma debug. Actualizar sobre el APK anterior conserva el historial. Los resultados nuevos registran configuración `simon-0.2-piano-mf`; el JSON anterior sigue siendo compatible.

## Verificación ejecutada

Windows/JBR 21, Gradle 8.14.3, AGP 8.11.0, Kotlin 2.2.10, SDK 36. Android: emulador Pixel_6/API 34, sin teléfono conectado al agente.

```powershell
cd Plataforma
# Con JAVA_HOME y ANDROID_HOME configurados
$env:ANDROID_SERIAL = 'emulator-5556'
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug
```

- Ocho pruebas JVM aprobadas: sesión, puntaje, metadatos, archivo persistido, interrupción y carrera de salida/guardado.
- Cinco pruebas Android aprobadas: menú→preparación→menú; doce teclas cromáticas visibles y respuesta física al presionar una tecla negra sin soltar; abandono/historial tras recreación; error/resumen/historial tras recreación; reproducción de **las 23 notas** usando los OGG y el reproductor real.
- Cada nota reprodujo al menos 26.460 frames en `AudioTrack`, correspondientes a 600 ms a 44,1 kHz. Se comprobó el pipeline de decodificación/salida; no la escucha acústica en un teléfono ni su latencia.
- Las pruebas de menú, OGG y respuesta al apoyar el dedo fallaron con la conducta anterior y pasaron después de implementar cada cambio.
- Validación de los 23 OGG con libsndfile: mono/44,1 kHz/26.460 frames, muestras finitas, SHA-256 concordante con el manifiesto y pico máximo observado 0,8048. Total de OGG 190.589 bytes; detalles en [Audio-piano-Simon.md](Audio-piano-Simon.md).
- Compilación, lint sin errores y APK debug generados. Los avisos de lint sobre actualizaciones de dependencias y extracción de datos no impidieron la compilación.

Durante la inspección visual reapareció el diálogo de ANR de System UI del emulador, ya observado en v0.1. Se descartó y se reinició la actividad para inspeccionar la aplicación. La instrumentación completó sin fallos.

Capturas reales del emulador: [menú](imagenes/prototipo-v0.2/menu.png), [preparación](imagenes/prototipo-v0.2/preparacion.png) y [piano](imagenes/prototipo-v0.2/piano.png). Tras revisar el espaciado se compactaron preparación/partida y se repitieron las cinco pruebas Android, lint y compilación, sin fallos.

APK de entrega: `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.2.apk` (~10,97 MiB), copia del APK debug generado. Firma comprobada con `apksigner verify`; metadatos comprobados con `aapt dump badging`: versionCode 2/versionName 0.2.0, minSdk 26/targetSdk 36.

## Recorrido en el S22 Ultra

Actualizar instalando el nuevo APK sobre el anterior, sin desinstalar. Comprobar que abre el menú y conserva partidas anteriores; entrar a Simón y probar las tres escalas, teclas blancas/negras, resalte, volumen/timbre del piano y salida al menú durante reproducción. Registrar si duración de las notas, volumen o ritmo de respuesta requieren ajuste; son parámetros técnicos provisionales.

El usuario informó funcionamiento general de v0.1. Esta nueva versión requiere otra prueba manual; no se atribuye al usuario una comprobación de v0.2 que todavía no realizó.

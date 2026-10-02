# Verificación de Simón musical v0.1

Fecha: 2026-10-02. Incremento [#2](https://github.com/SaladinoJC/Tesis/issues/2), con selección manual de escala; sin DDA ni desbloqueos todavía.

## Entorno y reproducción

Windows, JBR 21 de Android Studio, SDK 36, Gradle Wrapper 8.14.3, AGP 8.11.0 y Kotlin 2.2.10. Emulador Pixel_6 con Android 14/API 34, iniciado sin ventana y sin cargar/guardar snapshots. No hubo teléfono físico conectado. Durante la inspección visual apareció un diálogo de ANR de System UI del emulador; se descartó y se pudo inspeccionar inicio y partida. Las pruebas instrumentadas terminaron sin fallos.

Ejecutar desde `Plataforma/`, con `JAVA_HOME` y `ANDROID_HOME` configurados. Si hay varios dispositivos, fijar `ANDROID_SERIAL` para la instrumentación:

```powershell
$env:ANDROID_SERIAL = 'emulator-5556'
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug
```

APK: `Plataforma/app/build/outputs/apk/debug/app-debug.apk`. Informes locales en `Plataforma/app/build/reports/` y XML en `app/build/test-results/` / `app/build/outputs/androidTest-results/`.

## Resultados

- Ocho pruebas JVM aprobadas: siete de sesión y una de salida al menú durante un guardado suspendido. Cubren secuencia creciente, entrada durante reproducción, escalas/tónica fija, completar cuatro y fallar cinco, metadatos, persistencia en archivo y abandono sin duplicar registros.
- Tres pruebas Android aprobadas: historial de partida interrumpida tras recrear la actividad; partida terminada por error, resumen e historial tras recreación; reproducción usando el `AndroidNotePlayer` real.
- La reproducción produjo al menos 14.400 frames para una nota de 300 ms a 48 kHz, mono PCM de 16 bits. Se observó el contador de reproducción de `AudioTrack`; no se midió audibilidad en un altavoz, precisión acústica ni latencia de extremo a extremo.
- Compilación, lint sin errores y APK debug generado. Lint conserva avisos sobre versiones más recientes de dependencias y reglas de extracción de datos; la aplicación tiene `allowBackup=false`.
- Revisión independiente de estándares y especificación: observaciones resueltas. La revisión detectó una carrera al salir durante el guardado; su prueba falló antes de corregirla y pasó después. También se evitó leer dos veces el archivo al guardar.

La instrumentación detectó además una inicialización incorrecta del buffer estático de `AudioTrack`: el estado se comprueba después de escribir PCM. La prueba con el reproductor real pasó con la corrección.

## Pendientes en el teléfono

Tras solicitar la compilación del APK para su celular, el usuario informó (2026-10-02): «va queriendo, falta corregir muchos detalles pero anda». Primera comprobación manual reportada con funcionamiento general; detalles a corregir todavía sin especificar. No se registraron condiciones del dispositivo ni el recorrido realizado.

Completar el recorrido en el Samsung S22 Ultra y registrar versión de Android, volumen y salida de audio usada: jugar las tres escalas, completar rondas y provocar un error; comprobar sonido/resaltes, puntaje y resumen; volver al menú durante reproducción; salir a otra app; cerrar/reabrir y comprobar historial. Registrar por separado incidencias de audio, interfaz y persistencia.

La demostración física prevista en la especificación sigue pendiente. Este incremento verifica funcionalidad e integración en emulador; no acredita eficacia pedagógica ni calibración de dificultad. La prueba de micrófono/YIN corresponde a #6.

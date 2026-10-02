# «¿Qué instrumento es?» v0.3

Fecha: 2026-10-02. Incremento [#3](https://github.com/SaladinoJC/Tesis/issues/3), autorizado por el usuario al retomar el desarrollo. La especificación y los puntos de prueba ya estaban aprobados. Flujo aplicado: `ask-matt` → `implement` con TDD y revisión en dos ejes.

## Comportamiento entregado

- Acceso desde el menú; pantalla vertical con niveles manuales A/B/C y diez preguntas por partida.
- Cuatro opciones aleatorias por pregunta, con una respuesta correcta. El nivel y la nota de control se mantienen durante la partida. No hay reloj ni penalización por demorar.
- Primera escucha automática y dos reescuchas disponibles. Una reproducción consume ayuda solo cuando se observa avance de AudioTrack; una falla previa al inicio no la consume. No se permiten reproducciones simultáneas ni una cuarta escucha.
- La respuesta se habilita después de escuchar. Feedback con instrumento correcto y selección realizada; avance explícito a la siguiente pregunta. La décima respuesta cierra la partida y muestra resumen por pregunta.
- Historial local separado por juego, con selección, opciones, objetivo, corrección, escuchas, nivel, origen manual, fechas y versiones del banco/configuración. Salir al menú o a segundo plano registra interrupción. Una recreación por configuración conserva la partida.
- Simón conserva el piano con SoundPool, la orientación horizontal, las respuestas rápidas y el historial previo. Los dos juegos comparten el reproductor PCM y la escritura de JSON; los resultados mantienen sus tipos y archivos independientes.

DDA, desbloqueos y voz siguen en sus tickets. Las elecciones numéricas de audio y del juego son parámetros de prueba; esta entrega no acredita eficacia pedagógica.

## Banco de audio

Nueve grabaciones reales `mf` de Iowa con Do4 nominal compartido y sin transposición, convertidas a OGG mono/44,1 kHz/1,5 s. Ocupan **142.201 bytes** (~139 KiB). [Permiso, catálogo, transformación y mediciones](Audio-instrumentos.md). Manifiesto en `Plataforma/app/src/main/assets/instruments/manifest.json`.

Control de los archivos finales: SHA-256 coincide con el manifiesto; nueve OGG mono de 66.150 frames, muestras finitas, RMS decodificado 0,0769–0,0806 y picos menores que 0,95. La afinación estimada de las fuentes va de −15,94 a +13,73 cents respecto de Do4. Esta comprobación de selección no evalúa tono vocal ni sonoridad perceptual en teléfono.

## Verificación reproducible

Entorno: Windows, JBR 21, Gradle 8.14.3, emulador Pixel_6/API 34, serial `emulator-5556`. No se conectó el Samsung S22 Ultra.

```powershell
cd Plataforma
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
$env:ANDROID_SERIAL = 'emulator-5556'
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug --offline --max-workers=2
```

Cobertura JVM: seis pruebas de sesión de instrumentos y tres de su ViewModel; ocho de sesión Simón y cuatro de su ViewModel. Incluyen límite de escuchas, audio fallido antes/después de iniciar, aciertos/errores, diez preguntas, persistencia completa/parcial, cancelación durante audio, timeout y salida durante guardado final. El historial de Simón se comprueba con un documento literal del esquema v0.2.1 y un agregado posterior.

Cobertura Android: tres pruebas de instrumentos, siete de flujo/audio de Simón y una del teclado. La partida completa de nivel C agota las escuchas de la primera pregunta, recrea la actividad y comprueba resumen e historial al reabrir. Otra prueba comprueba niveles y salida durante presentación. El banco de nueve instrumentos avanza al menos 66.150 frames por muestra y confirma una sola escucha; el banco de 23 notas de Simón sigue avanzando al menos 26.460 frames. Estos avances acreditan reproducción por la API en emulador, no audibilidad o latencia acústica en S22 Ultra.

Los informes están en `Plataforma/app/build/reports/`. Se inspeccionaron capturas Compose del [menú](imagenes/prototipo-v0.3/menu.png), [preparación](imagenes/prototipo-v0.3/preparacion.png), [pregunta](imagenes/prototipo-v0.3/pregunta.png), [feedback](imagenes/prototipo-v0.3/respuesta.png) y [resumen](imagenes/prototipo-v0.3/resumen.png). Los datos de estas capturas corresponden a pruebas automatizadas, no a un estudiante real.

Resultado final después de las correcciones de revisión: **21 pruebas JVM y 11 Android aprobadas**, sin fallos ni pruebas omitidas; lint **0 errores / 14 advertencias**, APK generado. `BUILD SUCCESSFUL in 3m 50s`. No se contabilizan como aprobados los intentos rojos descritos debajo.

APK: `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.3.apk`, **11.329.014 bytes** (~10,80 MiB), copia del `app-debug.apk` final. SHA-256: `3e818b5bdf84d1ad96eccf4d4f5b7e3cee18cf2f3a06a2bf591f44f55445bc5a`. Aapt confirma paquete `ar.edu.unmdp.entrenamiento`, versionCode 4/versionName 0.3, minSdk 26/targetSdk 36. Firma debug verificada con apksigner; certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`. Se conserva la configuración de firma del proyecto; el APK v0.2.1 no estaba disponible para comparar ambos binarios.

## Ciclos de implementación y revisión

La primera prueba de sesión fue roja por la ausencia del juego; pasó al implementar opciones y conteo de audio. La prueba de diez respuestas fue roja hasta agregar cierre/persistencia. La integración Android fue roja por la falta de acceso desde el menú, y luego identificó un control de salida fuera del viewport del resumen. Mantener «Otra partida» y «Volver al menú» fijos resolvió ese fallo. Los casos adicionales cubren interrupciones y guardado final sin duplicación.

Revisión contra el estado inicial `59f19605cb47f22b5a951a6d25d2b1f78319daeb`:

- **Standards:** cero incumplimientos documentados. Dos observaciones de duplicación, corregidas extrayendo PCM/AudioTrack a `AndroidSamplePlayer` y documento JSON/escritura atómica a `JsonResultsFile`. Una observación adicional sobre cancelación, corregida retornando al dispatcher llamador sin sustituir el Job del timeout. Sin hallazgos pendientes.
- **Spec:** cero faltantes, ampliaciones de alcance o errores detectados; la segunda revisión conserva esa conclusión tras las extracciones. Revisión estática independiente de las pruebas.

## Prueba pendiente en S22 Ultra

Instalar v0.3 encima de v0.2.1, sin desinstalar. Anotar versión Android, salida de audio utilizada y volumen multimedia. Comprobar que el historial anterior de Simón sigue presente.

Jugar diez preguntas en cada nivel. Comprobar primera escucha automática, contador 2→1→0, bloqueo de una cuarta reproducción, cuatro botones completos, devolución correcta y resumen. Probar aciertos con una y tres escuchas. Reabrir la app y comprobar el historial. Abandonar otra partida durante el sonido y confirmar que figura como interrumpida. Revisar timbre, ataque y equilibrio de volumen entre instrumentos; RMS parecido no garantiza igualdad de sonoridad. Revisar también las pulsaciones rápidas y el giro horizontal de Simón según [su recorrido](Verificacion-Simon-v0.2.1.md).

No se han medido latencia acústica, comodidad en el teléfono, precisión del reconocimiento de estudiantes ni eficacia pedagógica.

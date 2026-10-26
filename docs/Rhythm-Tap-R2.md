# Rhythm Tap — incremento R2 / #17

Implementación local de [R2/#17](https://github.com/SaladinoJC/Tesis/issues/17), retomada individualmente por el usuario el 2026-10-26 sobre A3 verificado. Rama `trabajo/ritmo-r2-continuacion`, versión 0.12.2/código 17. No se incorporó D2 ni se avanzó R3–R6; sin push ni cambios en GitHub. La sección final registra la verificación actual; los apartados anteriores conservan el checkpoint del 2026-10-24.

Alcance del ticket: R01/R02/R09/R10/R11/R12/R18/R19, además de R20/R21; cuatro compases 2/4, 3/4 o 4/4, cuota conjunta 50–75 % más cercana al 60 %, mínimo dos apariciones por seleccionada, conexión exclusivamente negras/corcheas, silencios sin ataques ni penalización duplicada de ataques vecinos válidos. No hay persistencia nueva ni DDA.

## Plan técnico provisional

Factibilidad exacta de cantidades objetivo y empaquetado en cuatro barras antes del sorteo. Selecciones incompatibles/imposibles se distinguen de un presupuesto de búsqueda agotado. Rechazo aleatorio máximo 500 candidatos/un segundo, cancelable fuera del hilo UI. La redonda requiere 4/4; el silencio de compás usa su métrica. Se conserva el generador básico v1 para su selección anterior en 4/4 y se versiona la ampliación.

El conteo de audio y la ejecución deben tomar la métrica de la misma partitura. `RhythmAudio.start(bpm,metronome)` conserva compatibilidad con los doubles anteriores; el overload con métrica permite ampliar Android. Los intervalos de silencio se evaluarán contando únicamente taps `EXTRA`, no ataques válidos dentro de su ventana.

## Verificación y pendientes

- Primer rojo observado: `:app:testDebugUnitTest --tests '*RhythmR2Test'`, **BUILD FAILED en 1 min 27 s**, por API de métrica/presupuesto, categorías y silencios todavía ausentes.
- **Verde posterior comprobado: 12 JVM `*Rhythm*Test` aprobadas (6 R2 + 5 sesión R1 + 1 ViewModel), 0 fallos/errores/omitidas, y `:app:compileDebugAndroidTestKotlin` aprobado.** Una invocación, un worker, sin emulador: `BUILD SUCCESSFUL in 1m 41s`. La producción, UI/audio y los tests Android compilan a este corte. No es una ejecución Android ni una inspección visual.
- Seis tests JVM nuevos contra la sesión pública: todas las categorías/métricas, cuota/duración, rechazo explicado, presupuesto agotado, silencios con/sin extras, ataque vecino válido, corpus mixto de 180 casos y cancelación. El corpus básico R1 conserva explícitamente sus dos categorías originales para no convertirlo accidentalmente en una selección imposible de diez categorías.
- Dos tests Android preparatorios escritos: recorrido controlado por las tres métricas y audio real por métrica. Sin ejecutar; conservan `DeviceTouch` de v0.12 para usar uptime real.
- Sin emulador ni Gradle concurrentes. La integración y documentos globales corresponden al agente principal.

Una simulación del reloj o avance de frames en emulador no acredita sincronía acústica ni eficacia pedagógica. Prueba física pendiente.

## Código guardado y próximo paso

Checkpoints locales: `aa5deb5` tests JVM, `3a22ead` Android preparatorio, `95e6ae4` contexto, `423046b` generación/evaluación, `ee19a13` UI/audio/VM, `73aa1a0` corpus/cancelación. No hay cambios en archivos compartidos del host, versión o persistencia.

El generador `rhythm-simple-v2` enumera cantidades mínimas compatibles con la cuota y prueba empaquetado exacto por capacidad de barra antes del rechazo aleatorio. Esta es una elección técnica provisional, sin mediciones de rendimiento. `rhythm-basic-v1` sigue resolviendo la selección R20/R21 en 4/4 con las mismas semillas. Los intervalos de silencio se conservan en `RhythmResult.silences`; en resultados interrumpidos no se afirma respeto de un silencio todavía abierto.

Próximo: integrar y ejecutar suite JVM completa, lint, APK/APK de pruebas y Android; revisar capturas canónicas de las figuras nuevas en las tres métricas. El dibujo de silencios/puntillo/figuras largas está escrito y compila, pero **sin inspección visual**. Los tests Android R2 todavía no se ejecutaron. No avanzar R3 ni considerar #17 completado antes de cerrar esas comprobaciones.

Comando de reproducción del verde, desde `Plataforma/` con JAVA_HOME/ANDROID_HOME del registro global:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*Rhythm*Test' :app:compileDebugAndroidTestKotlin --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Informes JVM locales: `Plataforma/app/build/test-results/testDebugUnitTest/TEST-ar.edu.unmdp.entrenamiento.rhythm.*.xml`. Se conservaron los avisos previos del SDK XML y del receptor nullable en `PitchLabSessionTest`; no impidieron esta compilación. No se hicieron push, cambios de issues ni mediciones físicas.

## Entrega individual 0.12.2 — 2026-10-26

Se recuperaron los ocho checkpoints de R2 sobre la rama verificada del afinador A3. Configuración de ritmo explícita, cuatro compases, 60 BPM fijos; la búsqueda de 500 candidatos/un segundo, cuota 50–75 % cercana a 60 % y ventanas de evaluación siguen siendo valores experimentales. Generar rechaza selecciones imposibles sin quitar categorías; redonda solo 4/4 y silencio de compás según la métrica. La práctica se repite con la misma partitura, no agrega progresión ni persistencia nueva.

**Verificación conjunta: 110 JVM/25 clases, cero fallos/errores/omisiones; lint sin errores/14 advertencias; app y pruebas Android compiladas.** Gradle terminó en 6 min 04 s con un worker, heap 768 MiB y Kotlin in-process. **Cinco Android seleccionadas aprobadas en 102,205 s**, Pixel_6/API 34, 2 GiB de RAM, sin Gradle simultáneo: flujo R1, reproducción/liberación real, recorrido R2 por tres métricas con conteo y repetición, audio por métrica y ejemplos canónicos.

Ejemplos mediante `RhythmSession.generate(setOf(category), 12L, meter)`: ocho categorías nuevas, cada una en 2/4, 3/4 y 4/4 salvo redonda solo en 4/4; **22 casos en ocho láminas**. Se usa el mismo componente `RhythmBarDrawing` de producción, ahora interno para reutilizarlo en la verificación. `RhythmNotationAndroidTest` exporta las láminas; el recorrido R2 exporta también `flow-2.png`, `flow-3.png` y `flow-4.png`. Capturas en `docs/imagenes/ritmo-r2/`.

Revisión visual con la skill local `design-taste-frontend`, adaptada a Kotlin/Compose nativo: se conserva la paleta, tipografía y representación rítmica existente; la pregunta de la pantalla es qué ataques y silencios leer en cada compás. No aplica el inspector HTML/Chrome ni se atribuyen mediciones automáticas de contraste al nativo. Se revisan separación de símbolos, puntillo, plicas, cabezas huecas, orientación de los silencios de blanca/compás y barras sin solapamientos. La primera captura reveló rótulos negros en el contenedor de prueba; se fijó allí el color de texto y el margen, sin cambios en la paleta de la app.

Verificación tras corregir el contenedor y añadir capturas del flujo: APK de pruebas recompilado en **52 s**, y las **tres Android afectadas aprobadas en 50,367 s**. Se inspeccionaron las ocho láminas iniciales y, tras el ajuste, una representativa y las tres vistas reales de la app; las figuras de producción no cambiaron entre ambos recorridos. Rótulos finales legibles, símbolos separados, cuatro compases y Play accesibles mediante scroll. Son cinco pruebas Android distintas comprobadas; la segunda ejecución repite tres, no suma otras tres pruebas nuevas. El APK de producción conserva su hash.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.2.apk`, 12.456.243 bytes, SHA-256 `7BDC58381393EF9C12FF1746FCB46B041323C011C29319D3924F479CB7945EBB`. Mismo paquete y firma debug: instalar encima de 0.12.1 sin desinstalar. Evidencia local: `Plataforma/build/verification/2026-10-26-R2/`, con log Gradle, resultados JVM, lint, instrumentación y capturas anteriores a corregir los rótulos.

Desde `Plataforma/`, repetir JVM/lint/empaquetado con los parámetros documentados arriba y los tasks `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest`. Con el emulador de pruebas iniciado, desbloqueado y sin diálogos, instalar ambos APK y ejecutar:

```powershell
adb -s emulator-5554 shell am instrument -w -r -e class ar.edu.unmdp.entrenamiento.RhythmAndroidTest,ar.edu.unmdp.entrenamiento.RhythmR2AndroidTest,ar.edu.unmdp.entrenamiento.RhythmNotationAndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
adb -s emulator-5554 pull /sdcard/Android/data/ar.edu.unmdp.entrenamiento/files/r2-notation/. ../docs/imagenes/ritmo-r2
```

Prueba física pendiente en S22 Ultra: seleccionar una categoría por vez en cada métrica, generar, revisar partitura y pulsar Play. Esperar los 2/3/4 pulsos de entrada, tapear ataques sin sostener el dedo ni tapear silencios, revisar errores/silencios y repetir. Probar redonda en 2/4 para comprobar el rechazo; después, en 4/4 para jugarla. Probar una selección excesiva y comprobar que sigue seleccionada tras el mensaje. Registrar Android/app, ruta de audio, tempo y método externo si se mide latencia. Los taps/relojes controlados y frames del emulador no acreditan sincronía acústica ni eficacia pedagógica. No se ejecutó toda la suite Android ni API 36.

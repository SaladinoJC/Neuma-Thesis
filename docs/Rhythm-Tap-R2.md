# Rhythm Tap — incremento R2 / #17

Checkpoint en curso, 2026-10-24. Trabajo autorizado junto con A3 y D2; rama `trabajo/ritmo-r2`, worktree `C:/Users/Juan/Tesis-worktrees/ritmo-r2`, base común `d0f1624` (v0.12). Sin push ni cambios en GitHub. No se considera completado #17 ni se avanza R3–R6.

Alcance del ticket: R01/R02/R09/R10/R11/R12/R18/R19, además de R20/R21; cuatro compases 2/4, 3/4 o 4/4, cuota conjunta 50–75 % más cercana al 60 %, mínimo dos apariciones por seleccionada, conexión exclusivamente negras/corcheas, silencios sin ataques ni penalización duplicada de ataques vecinos válidos. No hay persistencia nueva ni DDA.

## Plan técnico provisional

Factibilidad exacta de cantidades objetivo y empaquetado en cuatro barras antes del sorteo. Selecciones incompatibles/imposibles se distinguen de un presupuesto de búsqueda agotado. Rechazo aleatorio máximo 500 candidatos/un segundo, cancelable fuera del hilo UI. La redonda requiere 4/4; el silencio de compás usa su métrica. Se conserva el generador básico v1 para su selección anterior en 4/4 y se versiona la ampliación.

El conteo de audio y la ejecución deben tomar la métrica de la misma partitura. `RhythmAudio.start(bpm,metronome)` conserva compatibilidad con los doubles anteriores; el overload con métrica permite ampliar Android. Los intervalos de silencio se evaluarán contando únicamente taps `EXTRA`, no ataques válidos dentro de su ventana.

## Verificación y pendientes

- Primer rojo observado: `:app:testDebugUnitTest --tests '*RhythmR2Test'`, **BUILD FAILED en 1 min 27 s**, por API de métrica/presupuesto, categorías y silencios todavía ausentes. Producción posterior escrita, **todavía sin compilar/verificar** a este corte.
- Seis tests JVM nuevos contra la sesión pública: todas las categorías/métricas, cuota/duración, rechazo explicado, presupuesto agotado, silencios con/sin extras, ataque vecino válido, corpus mixto de 180 casos y cancelación. El corpus básico R1 conserva explícitamente sus dos categorías originales para no convertirlo accidentalmente en una selección imposible de diez categorías.
- Dos tests Android preparatorios escritos: recorrido controlado por las tres métricas y audio real por métrica. Sin ejecutar; conservan `DeviceTouch` de v0.12 para usar uptime real.
- Sin emulador ni Gradle concurrentes. La integración y documentos globales corresponden al agente principal.

Una simulación del reloj o avance de frames en emulador no acredita sincronía acústica ni eficacia pedagógica. Prueba física pendiente.

## Código guardado y próximo paso

Checkpoints locales: `aa5deb5` tests JVM, `3a22ead` Android preparatorio, `95e6ae4` contexto, `423046b` generación/evaluación, `ee19a13` UI/audio/VM, `73aa1a0` corpus/cancelación. No hay cambios en archivos compartidos del host, versión o persistencia.

El generador `rhythm-simple-v2` enumera cantidades mínimas compatibles con la cuota y prueba empaquetado exacto por capacidad de barra antes del rechazo aleatorio. Esta es una elección técnica provisional, sin mediciones de rendimiento. `rhythm-basic-v1` sigue resolviendo la selección R20/R21 en 4/4 con las mismas semillas. Los intervalos de silencio se conservan en `RhythmResult.silences`; en resultados interrumpidos no se afirma respeto de un silencio todavía abierto.

Próximo: turno exclusivo de Gradle para `*Rhythm*Test` y `:app:compileDebugAndroidTestKotlin`, corregir solo errores concretos y registrar resultados reales. Después corresponde integrar/verificar Android y revisar capturas canónicas de las figuras nuevas en las tres métricas. El dibujo de silencios/puntillo/figuras largas está escrito pero **sin inspección visual**. No avanzar R3 ni considerar #17 completado antes de cerrar esas comprobaciones.

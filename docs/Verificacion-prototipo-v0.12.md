# Verificación integrada v0.12

Fecha: 2026-10-24. Trabajo local para A2/[#10](https://github.com/SaladinoJC/Tesis/issues/10), R1/[#16](https://github.com/SaladinoJC/Tesis/issues/16) y D1/[#23](https://github.com/SaladinoJC/Tesis/issues/23). Las ramas de cada frente se conservaron en worktrees independientes; compilaciones y emulador se coordinaron por turnos. No se publicaron cambios ni se cerraron tickets.

**Verificación completa en API 34 y comprobación de compatibilidad en API 36 aprobadas.**

## Resultado integrado

En Pixel_6/API 34 (`emulator-5554`), ejecución final conjunta de JVM/lint/APK/Android: **BUILD SUCCESSFUL en 10 min 23 s**. Los 32 casos Android aprobaron sin fallos, errores ni omisiones. La suite JVM conserva 97 aprobados y lint 0 errores/14 advertencias.

La suite Android comprende perfiles/personalizada/validación/recreación de Afinador, captura real liberada al salir, partitura/conteo/tap con uptime real/repetición/interrupción de Ritmo, audio real de pulsos, Defender con disparo único/pausa/tres llegadas/pentagrama/audio, navegación conjunta y conservación de historiales. También pasaron las regresiones previas de Simón, instrumentos, DDA, voz, historial, laboratorio y recuperación.

En Medium_Phone_API_36.0/API 36 (`emulator-5556`), **10 Android aprobadas sin fallos, errores ni omisiones**, 3 min 39 s: `DefenderAndroidTest`, `RhythmAndroidTest`, `TunerConfigurationAndroidTest`, `ParallelFeaturesAndroidTest` y `TunerAndroidTest`. Es una selección de compatibilidad; no una segunda ejecución de las 32 regresiones. El primer arranque API 36 en el puerto reutilizado 5554 no expuso conexión ADB; se conservaron logs y se reinició únicamente ese emulador en 5556 antes de ejecutar pruebas. Las pruebas se ejecutaron en un dispositivo por vez.

APK debug archivado en `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.apk`, versión 0.12/code 15. Firma v2 verificada; el certificado SHA-256 coincide con v0.11 (`4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`), permitiendo actualizar encima sin desinstalar. El historial/progreso anterior también se comprobó desde el host integrado.

Tamaño: **12.464.760 bytes**. SHA-256 del APK: `35C3BA3BF24952BCE0A6E4F3CFBB91592CC9144E09135C168F0BF725C1FD3832`. Las pruebas se ejecutaron sobre este corte de producción, con los probes retirados. Informes preservados en `Plataforma/build/verification/v0.12/android-api34-final/` y `android-api36-final/`; logs `verificacion-final-api34.log` y `android-api36.log`.

## Software y compilación

- Suite JVM integrada: **97 pruebas, 22 clases, sin fallos, errores ni omisiones**. Incluye reglas existentes, perfiles/configuración del afinador, generación/evaluación rítmica y supervivencia de Defender.
- Lint: **0 errores y 14 advertencias**. APK de aplicación y APK de pruebas compilados. Comando conjunto: 3 min 10 s.
- Después se ajustó únicamente el reloj de una prueba táctil Android de Defender: avanzar 1 s desde el comienzo simulado evita que la demora real de entrega ubique el evento antes de la partida. Reconstrucción del APK de pruebas: 31 s. No se modificó producción con ese ajuste.
- Comandos desde `Plataforma/`, JBR de Android Studio y SDK local:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Los logs locales están en `Plataforma/build/verification/v0.12/`. Los XML JVM y el informe de lint están bajo `Plataforma/app/build/`.

Para las pruebas Android completas, iniciar únicamente el emulador de pruebas y ejecutar `:app:connectedDebugAndroidTest` con las mismas opciones de memoria y `$env:ANDROID_SERIAL = 'emulator-5554'`. Para API 36 se usó `emulator-5556` y el argumento `-Pandroid.testInstrumentationRunnerArguments.class=` con las cinco clases listadas arriba. Las pruebas reinician datos; no ejecutarlas en el teléfono.

## Inspección visual

Capturas guardadas e inspeccionadas en `docs/imagenes/prototipo-v0.12/`: menú con versión, afinador detenido y sus seis cuerdas, preparación/escenario de Defender, extremos de pentagrama, solución/resumen y [partitura rítmica generada en API 36](imagenes/prototipo-v0.12/ritmo-previa-api36.png). Se comprobaron controles completos, contraste y ausencia de superposiciones en ambos emuladores; los cuatro compases se recorren mediante scroll antes de Play. La captura `ritmo-generando-api34.png` muestra el estado transitorio de generación, no una partitura terminada. Tras las suites se reinstaló el APK para las capturas porque el runner retira la app al terminar; ese arranque desde cero no es evidencia de conservación de datos. La conservación se comprobó en los tests de integración anteriores.

## Correcciones y límites

Primera ejecución Android completa en Pixel_6/API 34: **28 de 32 aprobadas**, cuatro fallos, 8 min 33 s. No se consideró una aprobación de la entrega. Tras corregir las pruebas, una ejecución concentrada en los tres frentes aprobó **7 de 8**; el restante era el recorrido temporal de Ritmo. Los informes originales se conservaron bajo `Plataforma/build/verification/v0.12/`.

- **Defender, disparo táctil:** `performTouchInput` entregó `uptimeMillis=448` frente al uptime real `804855`; el adaptador rechazó correctamente el evento por anterior al comienzo. Una traza temporal del límite público reprodujo el síntoma; inyectar `MotionEvent` con uptime del dispositivo produjo un solo acierto de 100 puntos, limpió selección y no agregó fallos. Se corrigió la inyección del test, manteniendo reglas y aserciones. La traza productiva se retiró.
- **Defender, ciclo de vida de Activity:** una aserción sincronizada aguardaba inactividad mientras el escenario se refrescaba. Se comprueba el estado después de la pausa real de Activity, en lugar de esperar inactividad durante movimiento. El recorrido aislado aprobó pausa explícita, objetivos ocultos, tres vidas y preparación al reingresar.
- **Afinador, configuración:** el texto visible contenía el nombre y Drop D; la aserción esperaba coincidencia completa. Se declararon como parciales las seis comprobaciones correspondientes. El recorrido posterior aprobó creación/selección de guitarra, personalizada, persistencia al recrear, Pro, rechazo de Si1 y micrófono detenido.
- **Ritmo, conteo táctil:** la traza registró `RESULTS` y `elapsed=16003761061 ns` antes de intentar el gesto; no se había inyectado un tap durante el conteo. La espera de inactividad había consumido la ejecución. El test corregido observa el estado público durante la timeline real, dibuja frames de Compose explícitos e inyecta con uptime real. Las comprobaciones de preparación, resultado, repetición exacta e interrupción de Activity aprobaron en la suite completa final. Se retiró el probe.

Los seis casos JVM de Ritmo verificaron los parches pendientes de la sesión anterior: un evento histórico no retrocede la fase y un tap válido entregado tras el cierre puede corregir una omisión conservando el timestamp original. La espera inicial de hasta 500 ms por AudioTimestamp puede retrasar la aparición del área de tap durante el conteo; no equivale a una medición de latencia acústica.

Defender aprobó 13 pruebas JVM. Se verifican preparación tras interrupción, pausa, empate de disparo/llegada, corrección por timestamp y tercer impacto sin duplicar resultados. Una comparación geométrica exacta de Float se cambió a tolerancia de 1e-6 (`0,82` frente a `0,82000005`). Si ya sonó una llegada antes de recibirse un disparo histórico, se corrige el estado pero no se puede retirar ese sonido: se evita emitir una segunda resolución del mismo enemigo.

Afinador aprobó la suite de su rama con 78 JVM antes de integrar. Configurar detiene y drena la captura; las preferencias se serializan y recargar no inicia el micrófono. La prueba sintética de 29 alturas MIDI 36–64 usa senoidales a 44,1 kHz y ventanas de 4096 muestras desde la muestra 8000: máximo error absoluto observado de 0,042644243 cents, sin cambiar octava. Es simulación PCM, no precisión medida con guitarra.

## Pendiente físico

No se probó el Samsung S22 Ultra. Quedan guitarra real y exactitud Pro, sincronía acústica y respuesta táctil, legibilidad y comodidad de Defender, permisos y ciclo de vida en el teléfono. No se midió eficacia pedagógica. Guiado/entrenamiento del afinador y los incrementos posteriores de ritmo/Defender conservan su alcance en los tickets dependientes.

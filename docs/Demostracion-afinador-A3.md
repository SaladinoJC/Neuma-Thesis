# Afinador A3 — guiado y revisión final

Implementación local de [#11](https://github.com/SaladinoJC/Tesis/issues/11), autorizada por el usuario el 2026-10-24 sobre v0.12 (`d0f1624`) y retomada individualmente el 2026-10-26. Versión 0.12.1, rama `trabajo/afinador-a3-continuacion`: solo A3; Ritmo R2 y Defender D2 permanecen aparte. La sección final registra la verificación actual; los párrafos de continuidad conservan el cierre anterior.

Contrato previsto: elegir Libre/Guiado, guitarra/afinación y Normal/Pro antes de comenzar. Guiado recorre sexta → primera, un segundo continuo dentro del margen; cada objetivo nuevo descarta toda lectura previa. Revisión final activada inicialmente y recordada. La segunda pasada permite dejar cuerdas pendientes y volver a ellas; solo el final sin pendientes declara conjunto comprobado. Desactivarla completa la primera pasada sin declarar comprobación final.

Resumen únicamente en memoria, separado por primera pasada, revisión y correcciones, con copia de guitarra/afinación/criterio. Detener, salir o pasar a segundo plano interrumpe el recorrido sin completarlo ni guardar resultados; iniciar otra vez requiere acción explícita y empieza un recorrido nuevo. Recrear la vista no debe duplicar avances. A4 (referencias) y A5–A6 (entrenamiento e historial) permanecen fuera de este incremento.

Parámetros provisionales del agente para el aviso «Muy lejos, revisar cuerda correcta»: distancia absoluta ≥700 cents durante al menos 400 ms de lecturas válidas continuas, separación máxima250 ms; despeje a ≤500 cents, lectura inválida u objetivo nuevo. No identifica inequívocamente una cuerda ni pliega octavas. Permanencia de afinación conserva `tuner-continuity-v1`: un segundo, hueco máximo250 ms, Normal±5/Pro±2 cents. No están calibrados físicamente.

## Verificación y continuidad

Primer rojo real: `:app:testDebugUnitTest --tests '*GuidedTunerTest'`, 1 min22s, sesión `GuidedTunerSession` ausente. Después de implementar reglas: mismo comando, **cuatro pruebas aprobadas**, 1 min27s. Se comprobaron orden DropD, permanencia/invalidas/desvíos/huecos, aviso de octava sin plegado, revisión pendiente→corrección sin reescribir evaluación original, e interrupción/Pro. Parámetros Gradle: `--max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain`.

Segunda regresión detectada en revisión del padre: preparar Guiado → elegir Pro mostraba `ui.tuning.criterion=NORMAL`, aunque la configuración contenía Pro. Prueba pública roja real (1 min29s, esperabaPRO/obtuvoNORMAL), luego ajuste para que preparación/interrupción muestren la configuración actual y el resumen conserve su snapshot anterior. El verde posterior ejecutó **toda la suite JVM: 104 pruebas, 24 clases, cero fallos/errores/omisiones, 1 min04s**. Se comprobó además captura controlada de dos pasadas completas, liberación automática al completar, ausencia de reinicio al reabrir, limpieza del resumen al reiniciar y lectura del JSON A2 con preferencias predeterminadas compatibles. Compilación de Kotlin/Compose aprobada en esa ejecución.

`GuidedTunerAndroidTest` está escrito pero **sin compilar ni ejecutar**: usa lecturas controladas para recorrer la interfaz y no mide afinación acústica. El siguiente paso es integrar y compilar Android; instrumentación a cargo del agente principal únicamente en emulador. No hay otro Gradle ni emulador ejecutándose en este frente. No se ejecutaron lint ni generación de APK en este worktree.

El host conserva APIs anteriores. Para reinicio de perfil, el callback compartido debe usar `tuner.resetDisplay()` (vacía resumen) en lugar de `reload()` (solo relee configuración). El padre realiza esa integración. No se modifica `MainActivity` en este worktree. Configuración JSON1 añade `mode` y `finalReview` con valores predeterminados Libre/activada; los archivos A2 siguen legibles. No se agregan archivos de resultados ni se modifica `Demonstration.kt`.

Verificación pendiente al cierre del 2026-10-24: integración entre frentes, Android y S22 Ultra/guitarra real. Distinguir lecturas sintéticas y emulador de mediciones físicas. No se guarda PCM ni se modifica el historial de voz.

## Continuación individual del 2026-10-26

Se recuperaron los seis checkpoints A3 sobre `main`, conservando fuera los cambios R2/D2 de la integración triple. El callback de reinicio de perfil ahora invoca `tunerModel.resetDisplay()` para descartar el resumen en memoria. Se agregó `GuidedTunerLifecycleAndroidTest`: captura real en emulador, recreación, segundo plano y salida/reentrada sin completar ni reiniciar automáticamente el recorrido. El recorrido con lecturas controladas sigue en `GuidedTunerAndroidTest`.

Verificación JVM: **104 pruebas, 24 clases, cero fallos/errores/omisiones**. Lint: **cero errores, 14 advertencias**. Compilación de app y pruebas Android aprobada; primer conjunto Gradle 8 min 01 s y empaquetado final 0.12.1 en 1 min 53 s. Un worker, heap 768 MiB, Kotlin in-process, mismos parámetros documentados arriba. Gradle y emulador se ejecutaron por turnos debido a la memoria disponible.

Evidencia local en `Plataforma/build/verification/2026-10-26-A3/`: `gradle.log`, `paquete.log` y `android.log`. APK candidato en `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.1.apk`, código 16, mismo paquete y firma debug para actualizar v0.12 sin desinstalar. SHA-256: `CF4C6A4660D6246D3C2047E941E2F3C02BC7DA0B5E682AA0F433EE5CF140F1C5`.

**Resultado Android final: cinco pruebas aprobadas en 77,249 s**, Pixel_6/API 34, `emulator-5554`, 2 GiB de RAM, sin Gradle simultáneo. Incluye dos pruebas de captura/ciclo de vida anteriores, recorrido completo de dos pasadas controladas, ciclo de vida guiado y perfiles/afinaciones persistidos. No es la suite Android completa ni una ejecución en API 36. La captura real acredita apertura, recepción y liberación en el emulador; las dos pasadas usan estimaciones inyectadas, sin guitarra acústica.

Los primeros intentos quedan en `android-intento-1.log` a `android-intento-3.log`. Apareció un ANR de `com.android.systemui`, documentado en `screen.png`, que retenía el foco; se reinició con 2 GiB, se cerró ese diálogo y se comprobó el foco del escritorio. Después de borrar datos, la primera apertura de micrófono fallaba aunque las posteriores funcionaban: el helper anterior ejecuta `pm grant` y cierra su descriptor sin esperar. Conceder el permiso explícitamente mediante ADB antes de la instrumentación permitió la ejecución completa desde datos limpios; es compatible con una carrera de preparación de permisos, sin modificar la captura de producción. Mantener ese paso al reproducir y asegurar emulador desbloqueado, sin diálogos y con arranque concluido.

Comando de instrumentación seleccionada, después de instalar los dos APK en el emulador de pruebas:

```powershell
adb -s emulator-5554 shell pm grant ar.edu.unmdp.entrenamiento android.permission.RECORD_AUDIO
adb -s emulator-5554 shell am instrument -w -r -e class ar.edu.unmdp.entrenamiento.TunerAndroidTest,ar.edu.unmdp.entrenamiento.GuidedTunerAndroidTest,ar.edu.unmdp.entrenamiento.GuidedTunerLifecycleAndroidTest,ar.edu.unmdp.entrenamiento.TunerConfigurationAndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Recorrido manual pendiente en S22 Ultra: elegir Guiado, guitarra, afinación y Normal/Pro; completar sexta → primera manteniendo cada nota un segundo, repetir con revisión desactivada y volver a activarla comprobando su persistencia. En segunda pasada, dejar una cuerda pendiente y corregirla al final; solo entonces debe indicar «Conjunto comprobado». Comprobar aviso con distancia excesiva, silencio y pausas; al salir/recrear no debe completar ni arrancar solo. No guardar audio ni atribuir precisión física o eficacia pedagógica a las lecturas controladas del emulador.

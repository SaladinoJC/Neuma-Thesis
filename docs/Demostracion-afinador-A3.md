# Afinador A3 — guiado y revisión final

Implementación local de [#11](https://github.com/SaladinoJC/Tesis/issues/11), autorizada por el usuario el 2026-10-24 sobre v0.12 (`d0f1624`). Reglas puras implementadas y cuatro casos JVM aprobados. Configuración, ViewModel, interfaz, dos pruebas JVM adicionales y recorrido Android escritos posteriormente y todavía pendientes de compilación/verificación. No es una entrega comprobada.

Contrato previsto: elegir Libre/Guiado, guitarra/afinación y Normal/Pro antes de comenzar. Guiado recorre sexta → primera, un segundo continuo dentro del margen; cada objetivo nuevo descarta toda lectura previa. Revisión final activada inicialmente y recordada. La segunda pasada permite dejar cuerdas pendientes y volver a ellas; solo el final sin pendientes declara conjunto comprobado. Desactivarla completa la primera pasada sin declarar comprobación final.

Resumen únicamente en memoria, separado por primera pasada, revisión y correcciones, con copia de guitarra/afinación/criterio. Detener, salir o pasar a segundo plano interrumpe el recorrido sin completarlo ni guardar resultados; iniciar otra vez requiere acción explícita y empieza un recorrido nuevo. Recrear la vista no debe duplicar avances. A4 (referencias) y A5–A6 (entrenamiento e historial) permanecen fuera de este incremento.

Parámetros provisionales del agente para el aviso «Muy lejos, revisar cuerda correcta»: distancia absoluta ≥700 cents durante al menos 400 ms de lecturas válidas continuas, separación máxima250 ms; despeje a ≤500 cents, lectura inválida u objetivo nuevo. No identifica inequívocamente una cuerda ni pliega octavas. Permanencia de afinación conserva `tuner-continuity-v1`: un segundo, hueco máximo250 ms, Normal±5/Pro±2 cents. No están calibrados físicamente.

## Verificación y continuidad

Primer rojo real: `:app:testDebugUnitTest --tests '*GuidedTunerTest'`, 1 min22s, sesión `GuidedTunerSession` ausente. Después de implementar reglas: mismo comando, **cuatro pruebas aprobadas**, 1 min27s. Se comprobaron orden DropD, permanencia/invalidas/desvíos/huecos, aviso de octava sin plegado, revisión pendiente→corrección sin reescribir evaluación original, e interrupción/Pro. Parámetros Gradle: `--max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain`.

El código añadido después de ese verde **todavía no está verificado**: persistencia de modo/revisión, adaptador de captura y pantalla, tests `GuidedTunerViewModelTest` y `GuidedTunerAndroidTest`. El Android usa lecturas controladas para recorrer la interfaz; no mide afinación acústica. El siguiente turno debe ejecutar suite JVM completa y compilar Android; instrumentación a cargo del agente principal únicamente en emulador. No hay otro Gradle ni emulador ejecutándose en este frente.

El host conserva APIs anteriores. Para reinicio de perfil, el callback compartido debe usar `tuner.resetDisplay()` (vacía resumen) en lugar de `reload()` (solo relee configuración). El padre realiza esa integración. No se modifica `MainActivity` en este worktree. Configuración JSON1 añade `mode` y `finalReview` con valores predeterminados Libre/activada; los archivos A2 siguen legibles. No se agregan archivos de resultados ni se modifica `Demonstration.kt`.

Verificación pendiente: suite JVM completa, integración Compose/captura, Android y S22 Ultra/guitarra real. Distinguir lecturas sintéticas y emulador de mediciones físicas. No se guarda PCM ni se modifica el historial de voz.

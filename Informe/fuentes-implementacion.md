# Implementación consolidada: procedencia y decisiones editoriales

## Adición 0.12.27 — 16/11/2026

Continuación autorizada por «Dale, ponete con eso», sobre #34 y la síntesis
confirmada. Código contrastado: VocalIntervalApp, VocalIntervalHistoryApp,
VocalProgressApp, GardenPageScaffold y delegación de ritmo. Recurso original
MG-VOCAL-CLEARING: SVG, exportador y VectorDrawable; previa en navegador
separada de revisión Android pendiente. No se modifican modelos de sesión,
captura, referencias, YIN, DDA ni datos. La ayuda visual se conserva estable
durante captura; pruebas vocales existentes no se presentan como ejecutadas
en Android. Guía: `docs/Acabado-Intervalos-vocales-v0.12.27.md`.

## Adición 0.12.26 — 16/11/2026

Continuación autorizada por «Seguí con el siguiente», sin aprobación de la
presentación nativa anterior atribuida. Alcance de #33 contrastado con RhythmApp,
RhythmGardenInterior, calibración/historial/progreso/resumen/pad y GardenPaperPanel.
No se modifican fuentes de sesiones, audio, generador, evaluación ni persistencia.
Guía `docs/Acabado-Rhythm-Tap-v0.12.26.md` y evidencia duradera del incremento.
Se distinguen ejecución JVM, compilación AndroidTest y revisión nativa pendiente.

## Adición 0.12.25 — 16/11/2026

Acuerdo visual atribuido al relato del autor sobre la reunión y síntesis Q1–Q6
confirmada con «Ok, hagamos eso». Alcance del primer incremento contrastado con
InstrumentApp, GardenInterior, MainActivity, HistoryApp, InstrumentDdaInspector y
tema compartido. Sesiones/audio/DDA/persistencia sin cambios en este incremento.
Recursos originales inventariados; composición y verificación física pendientes.
Guía y evidencia: `docs/Acabado-Instrumentos-v0.12.25.md`. No se atribuye al director
aprobación del código, de la composición concreta ni de criterios de evaluación.

## Adición 0.12.23 — 12/11/2026

Apartado del afinador complementario y nota de validación contrastados con
`TunerPluckedPolicy`, sesiones de afinador/Guiado/Oído, captura del ViewModel,
historial y pruebas públicas de pulsación/decaimiento. Evidencia y parámetros
en `docs/Verificacion-afinador-v0.12.23.md`. Observación real del autor separada
del banco sintético; no se afirma resolución física ni exactitud medida.

## Adición del 12/11/2026: correcciones locales 0.12.21/0.12.22

Capítulos 10/11 incorporan únicamente comportamiento implementado y evidencia
local de las correcciones: `InstrumentSession`/`InstrumentViewModel`,
`RhythmSession`/`RhythmAudio`/`RhythmCalibration`/`RhythmViewModel`, pad nativo y
configuración/sesión/controles de Defender. Método, hashes y XML respaldados en
`docs/Verificacion-ritmo-defender-v0.12.22.md` y su ruta de verificación local.
Observaciones del autor conservadas por separado en
`docs/Pruebas-manuales-S22-Ultra-2026-11-12.md`, atribuidas a 0.12.20. Calibración
personal y muestras controladas no se presentan como medición de latencia física
ni eficacia pedagógica. No se cambia la etiqueta GitHub de la base anterior.

Fecha: **2026-11-04**. El autor autoriza consolidar el capítulo 10. Se aplica
`thesis-writing`; se reorganiza lo construido por funcionamiento, sin modificar
la aplicación ni realizar pruebas nuevas de producto.

## Referencia y criterio

- Candidata **0.12.20/código 35**, contrastada con `Plataforma/app/build.gradle.kts`
  y `docs/Entrega-GitHub-v0.12.20.md`. Etiqueta `v0.12.20-preview.1` en
  `413061a22a3648bb3bf1ab51838235d55319cf30`; no se publica una versión nueva.
- Instructivo, páginas físicas **51–54**: concreción del diseño, organización
  del código, decisiones, dificultades, construcción incremental y evidencia.
  El cuerpo no necesita reproducir grandes bloques de código ni toda la bitácora.
- El capítulo 5 conserva selección y versiones de herramientas; el 9,
  responsabilidades, dominio, cálculo Mamdani y persistencia general; el 11,
  pruebas/resultados/condiciones. El 10 explica cómo interactúan los componentes.
- Se conservan las **12 etiquetas previas**, incluidas fórmulas, audio/YIN,
  R6 y D3/D4/D5. Se agregan etiquetas de integración por actividad. Las referencias
  de Metodología, Marco y Pruebas continúan resolviendo.

## Organización aplicada

1. Código y versión de referencia.
2. Captura/análisis: bloque técnico ya contrastado sobre ventanas, YIN y admisión.
3. Intervalos: tres ayudas, coordinación, adaptación, series y registro como un
   recorrido actual, en lugar de repetir estados transitorios V1–V6.
4. Instrumentos: melodías, reescuchas, ayuda intrapartida, resultados y compatibilidad;
   este bloque faltaba en la redacción incremental anterior.
5. Rhythm Tap: partitura, entrada, evaluación, progresión y revisiones.
6. Defender: tiempo/cierre, grafía/sonido e inversiones/progreso.
7. Integración de datos, estados de error y límites de la candidata.

## Evidencia del código consultada

Rutas bajo `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/`:

| Bloque | Código / documentos | Uso y límites |
| --- | --- | --- |
| Organización | settings.gradle.kts, paquetes, src/test, src/androidTest, assets/res | Un módulo; ViewModels no están todos en el mismo paquete |
| Audio | `voice/AndroidVoiceCapture.kt`, `YinEstimator.kt`, `PitchLab.kt` | PCM mono 16 bits, no bloqueante, IO, acumulación de salto y ventana; diferencias/normalización/umbral/interpolación y motivos de rechazo |
| Configuración YIN | `YinConfiguration` | 44.100 Hz, 4096 muestras y salto 1024: 92,8798 ms y 23,2200 ms por cálculo, redondeados 92,9/23,2. No equivalen a latencia medida |
| Vocal | `VocalIntervalSession.kt`, `VocalIntervalViewModel.kt`, `VocalIntervalResults.kt`, `VocalProgression.kt` y guías V1–V6 | Ancla SUNG/GIVEN, tres ayudas, captura por etapas, callbacks invalidados, guardado/series y progreso independiente |
| Instrumentos | `InstrumentSession.kt`, `InstrumentMelody.kt`, `InstrumentDda.kt`, `audio/AndroidInstrumentPlayer.kt`; guías melodías v0.3.1 y DDA v0.4 | Melodía conservada, cuatro opciones, política de escuchas, manual/automático, registros históricos sin melodía ficticia |
| Ritmo | `RhythmSession.kt`, `RhythmGeneration.kt`, `RhythmViewModel.kt`, `RhythmProgression.kt`, `JsonRhythmResults.kt`, `ui/RhythmScoreView.kt`; especificación y R6 | Duraciones racionales, generación acotada, notación local, timestamps de taps, huella de contenido y commit/revisión por categoría |
| Defender | `DefenderSession.kt`, `DefenderViewModel.kt`, `DefenderHistory.kt`, `DefenderChords.kt` y guías D3–D5 | Entrada por timestamp, corrección tardía y sellado; grafía frente a altura; voces/inversiones y progreso específico |
| Datos e interfaz | `ui/DemonstrationViewModel.kt`, `storage/Demonstration.kt` y MainActivity | Drenaje previo a exportación JSON/reinicio, destino/cancelación/fallo, refresco y pendientes. No se generaliza ese drenaje a prepareRecovery, que sigue otro recorrido |

La fuente técnica y las citas previas del artículo YIN se conservan: se sigue
describiendo una implementación **basada en YIN**, con diferencias frente al
algoritmo publicado. No se trasladan sus métricas al prototipo.

## Correspondencia con incrementos preservados

| Incremento | Versión histórica | Contenido integrado |
| --- | --- | --- |
| R6 | 0.12.12 | Progresión, estado por categoría, commit y revisión evaluada |
| D3 | 0.12.13 | Entrada/cierre provisional, sellado y progreso de notas |
| D4 | 0.12.14 | Tríadas por nombre, grafía y síntesis; alcance histórico en sol/fundamental |
| D5 | 0.12.15 | Séptimas/inversiones/claves, 84 objetivos y compatibilidad de perfiles |
| V1/V2 | núcleo / 0.12.16 | Ronda relativa y coordinación Android |
| V3/V4 | 0.12.17 / 0.12.18 | Persistencia/historial, series y Mamdani |
| V5/V6 | 0.12.19 / 0.12.20 | Ancla dada/libre con progreso y procedencia independientes |

Los registros detallados permanecen en las guías y evidencia originales. El
capítulo 11 mantiene sus atribuciones por incremento: se aclara que los casos
D4 referidos a la subsección consolidada no acreditan las ampliaciones D5.

## Trazabilidad y pendientes

Organización/audio atienden RF-02/03; integración vocal RF-04/09/10/11;
instrumentos RF-05/09/10; ritmo RF-06/09/10; Defender RF-07/09/10;
datos RF-11–RF-15, con los límites de RNF-02/04/06/07. Son relaciones de
implementación, sin declaración automática de cumplimiento.

Piano en Defender y selección automática de ayuda vocal siguen pendientes de
concreción/implementación/verificación. Se mantienen separadas las verificaciones
de host, Android y dispositivo físico. No se incorporan cifras nuevas de prueba,
mediciones acústicas, calibración, eficacia pedagógica, cambios del Gantt o
estimaciones de horas a partir de tiempo del agente.

## Variantes 0.12.24 (12/11/2026)

Fuente local: `docs/Comparacion-Garden-Quiet-Studio-v0.12.24.md`, Gradle de app, `QuietStudioMenu.kt`, `QuietStudioTheme.kt`, tokens y respaldo previo. Dos paquetes con datos independientes; presentación comparativa de catálogo/ritmo, sesiones y audio conservados. Evidencia automatizada separada de revisión nativa/director pendientes. Trazabilidad: capítulos 10 y 11.

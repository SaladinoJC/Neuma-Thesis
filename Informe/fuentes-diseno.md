# Diseño: arquitectura, dominio y datos

Fecha: **2026-11-04**. El autor autoriza continuar con arquitectura y modelo
de dominio. Se aplica `thesis-writing` y, para precisar el glosario,
`domain-modeling`. Se describe código existente y extensiones ya propuestas;
no se implementan funciones ni se registra una arquitectura nueva como decisión.

## Criterios de lectura

Instructivo, páginas físicas **50–51**: arquitectura, modelo de dominio,
diseño de datos y patrones. Se conserva la distinción con Implementación,
páginas 51–54. El capítulo 9 explica responsabilidades, relaciones y decisiones;
el 10 conserva ventanas/YIN y registros por incremento. El cálculo Mamdani ya
redactado permanece en diseño con sus citas y etiquetas; no se duplica en las
secciones nuevas ni se agregan referencias bibliográficas.

`CONTEXT.md`, ADR 0001/0002, `Plataforma/AGENTS.md` y requisitos RF/RNF del
capítulo 8 aportan vocabulario, restricciones y condiciones de verificación.

## Arquitectura reconstruida desde el código

Rutas Kotlin bajo `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/`:

| Fuente | Evidencia utilizada | Interpretación y límite |
| --- | --- | --- |
| `Plataforma/settings.gradle.kts` | `include(":app")` | Un módulo Android; los bloques del diagrama son responsabilidades lógicas |
| `MainActivity.kt` | Factories de ViewModel, repositorios en filesDir, adaptadores Android, collectAsStateWithLifecycle y onPause/onStop | Composición manual, navegación/ciclo de vida; no contenedor de inyección o backend remoto |
| `ui/*App.kt` y `ui/RhythmScoreView.kt` | Estado/acciones de presentación; WebView local y restricciones de red | La notación de ritmo es una vista local; no convierte la app en arquitectura web |
| `voice/VocalIntervalViewModel.kt` | Estado observable, sesiones, permisos, referencias, capture, cancelación y generaciones | Orquesta captura/reproducción; no confundir esta coordinación con el núcleo musical |
| `voice/VocalIntervalSession.kt` | Fases, entrada de estimaciones/tiempos y resultado, sin imports Android/Compose | Núcleo de una ronda, con ancla cantada o dada; no administra micrófono |
| `instruments/InstrumentSession.kt` | InstrumentPlayer, InstrumentResult/Answer y estados propios | Sesión específica; adaptador de audio sustituible por contrato |
| `rhythm/RhythmSession.kt` | Beat, RhythmScore, ataques/figuras y fases | Partida de un ejercicio; modelo no idéntico a ronda vocal |
| `defender/DefenderSession.kt` | Objetivos simultáneos, eventos y reloj activo | Sesión continua; no inventar una ronda secuencial por cada objetivo |
| `dda/Mamdani.kt` | Configuración, inferMamdani, pertenencias, reglas y centroide | Motor común sin política global de juego; no suponer todos los cambios directamente de su salida |
| `voice/AndroidVoiceCapture.kt` y `AndroidReferencePlayer.kt` | Adaptadores de audio local y análisis | YIN no depende conceptualmente de Android aunque se use dentro de esa captura |

La figura agrupa colaboraciones de estado/acciones, sesiones, DDA, datos y audio;
no declara capas rígidas, microservicios, módulos Gradle separados o un contrato
único de todas las actividades. Tampoco atribuye una implementación completa de
Clean Architecture o un patrón MVVM exhaustivo.

## Dominio y glosario

Se conserva la terminología existente de partida, ronda, respuesta por variante,
ayudas y desbloqueos. Se añaden siete definiciones breves al glosario único:
perfil local, configuración de práctica, respuesta musical, resultado de práctica,
decisión de adaptación, progreso por habilidad y ayuda auditiva vocal. Son una
precisión terminológica del agente desde comportamientos y datos existentes,
sin nuevas reglas funcionales o afirmaciones pedagógicas.

El modelo conceptual relaciona actividad/partida, configuración, respuesta,
ronda cuando corresponde, resultado, decisión y progreso. La respuesta se
relaciona con su evaluación sin exigir una ronda en juegos continuos. Las
actividades conservan granularidad propia: no hay un Resultado/Ronda universal
impuesto como clase de implementación ni un modelo relacional inferido del dibujo.

Las estructuras consultadas son `InstrumentAnswer/Result`, `RhythmScore/Record`,
`DefenderState/Result/Record`, `VocalIntervalConfiguration/Measurement/Result/Record`
y progresión por actividad. El progreso vocal separa intervalo firmado y ayuda;
el rítmico categorías/tempo; Defender variante/contenido. Un prerrequisito puede
habilitar acceso sin atribuir evidencia de desempeño a otra ayuda.

## Persistencia y patrones constatados

- `storage/JsonResultsFile.kt`: documentos versionados, escritura temporal,
  movimiento ATOMIC_MOVE con fallback a REPLACE_EXISTING. Se describe la
  estrategia, sin prometer atomicidad o durabilidad absoluta en todos los FS.
- `storage/ProfileFiles.kt`: mutex compartido por ruta canónica y generación;
  transacciones anidadas secuenciales. Es coordinación dentro del proceso, no
  una transacción ACID de base de datos ni exclusión entre aplicaciones.
- `voice/VocalIntervalResults.kt` y `VocalProgression.kt`: repositorios memoria/JSON,
  preparación de decisión en el guardado, identidad y pendientes en memoria.
- `rhythm/RhythmHistorySession.kt`, `JsonRhythmResults.kt` y `RhythmProgression.kt`:
  identidad/revisión, commit preparado y política de revisiones.
- `defender/DefenderHistory.kt`: JsonDefenderResults, prepareDefenderRecord,
  revisiones y progreso coordinado.
- `storage/Demonstration.kt`: nueve archivos conocidos, exportación interpretada,
  ZIP de originales y pendientes, marcador reset-pending, invalidación por generación.
  El ZIP no implementa importación o reparación; el borrado no elimina exportados.

Se describen ViewModel/estado observable, repositorios, adaptadores, suministro
de dependencias por constructor y fases explícitas. No se inventan frameworks,
jerarquías State/Strategy o bus de eventos por semejanza de nombres.

## Vínculo con requisitos y pendientes

| Responsabilidad | Requisitos relacionados |
| --- | --- |
| Presentación y coordinación por actividad | RF-01/02/03/08/12, RNF-05 |
| Reglas y modelos musicales específicos | RF-04/05/06/07, RNF-03 |
| Políticas, Mamdani y progreso | RF-09/10/15, RNF-07 |
| Historial, persistencia, exportación/reinicio | RF-11/12/13/14, RNF-02/06/07 |
| Diseño por completar | RF-07 piano y RF-16 gobernanza automática de ayuda |

Piano en Defender y selección automática de ayuda vocal siguen como compromisos
propuestos sujetos al director, con detalle pendiente. Las tres ayudas vocales
actuales se seleccionan explícitamente. No se reviven promedio móvil/CA de fichas
antiguas como política vigente del Mamdani ni se presentan las extensiones como
componentes ya existentes.

## Figuras

`tools/generar_diseno.py` genera PDF vectorial y SVG de arquitectura y dominio.
El original editable y procedencia se registran en `figuras/LEEME.md`. Las flechas
arquitectónicas expresan colaboraciones; las del modelo, relaciones conceptuales.
No se incorporan assets de aplicación ni bibliotecas nuevas. El PDF del informe
se verifica mediante compilación y revisión visual; no se ejecutan pruebas de app.

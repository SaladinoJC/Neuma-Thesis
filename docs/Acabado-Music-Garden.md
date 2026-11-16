# Acabado transversal Music Garden

Estado: síntesis y división confirmadas por el autor con «Ok, hagamos eso»,
2026-11-16. Especificación en `Especificacion-acabado-Music-Garden.md`.
Este registro de entrevista no acredita cambios implementados.

## Decisiones confirmadas

Origen: respuestas explícitas del autor a Q1–Q3 de `grill-with-docs`, 16/11/2026.

- **Q1, alternativa C:** preparación, resultados e historial se presentan como
  páginas del libro; durante el juego aparece un escenario propio. Identidad
  compartida Music Garden, manteniendo limpias las superficies musicales.
- **Q2:** se autoriza reorganizar distribución, agrupación de controles y
  navegación interna para mejorar la experiencia. Se conservan reglas musicales,
  audio, DDA y datos; estructura acordada del menú del libro conservada.
- **Q3:** este incremento aborda acabado visual y movimiento dentro de las reglas
  vigentes. **Queda pendiente definir e incorporar sonidos de interfaz y respuesta
  táctil.** No se considera implementado ni descartado ese trabajo futuro.

Origen adicional: respuestas explícitas del autor a Q4–Q6 de la misma entrevista.

- **Q4, «dale»:** invernadero musical para Instrumentos, madera, plantas y luz
  cálida. La decoración no revela el instrumento correcto de la ronda.
- **Q5, «ok probemos»:** terraza del jardín para Rhythm Tap, con atril/partitura
  protagonistas, vegetación de borde y pad despejado. Dirección elegida para
  probar; composición y acabado concreto pendientes de evaluación visual.
- **Q6, «ok probemos»:** claro de canto con pequeño anfiteatro natural para
  intervalos vocales. Referencia/captura y mensajes directos en primer plano.
  Dirección elegida para probar; composición y acabado concreto pendientes de
  evaluación visual.

Personaje continúa pausado. Quiet Studio se conserva como antecedente sin borrar
variantes ni perfiles. Se mantienen los escenarios ya acordados de Defender
(árbol sin rostro y polillas botánicas) y Simón (claro con piano, horizontal).

## Inspección documental y de código

Auditoría de solo lectura: InstrumentApp y SimonApp mantienen estilos oscuros
heredados; HistoryApp utiliza darkColorScheme. Defender ya emplea tema Garden,
pero árbol/polillas y vidas conservan presentación provisional. La portada vocal
reutiliza provisionalmente Instrumentos. Estos hallazgos no equivalen a revisión
visual ejecutada sobre Android.

Reutilizar `GardenTokens.kt`, `TunerGardenTheme.kt`, `TunerOption`, fuentes
existentes, notación estructurada y componentes de resultados/progreso.
`design-tokens.json` continúa como fuente de tokens. Ver ART_DIRECTION e inventario
de assets antes de producir o modificar recursos.

## Síntesis y división propuestas para confirmar

El acabado abarca todas las superficies existentes de Garden: menú, preparación,
partidas, resultados, historial, progreso, afinador, datos y diagnósticos. Las
herramientas auxiliares usan el lenguaje de papel y controles compartidos, sin
inventar escenas de juego para operaciones técnicas. No agrega mecánicas,
medallas, personaje funcional ni ayudas musicales nuevas.

Orden propuesto por el agente y confirmado por el autor:

1. Instrumentos: recorrido completo libro → preparación → invernadero → resultado
   → historial/progreso → libro; introducir y comprobar componentes compartidos.
2. Rhythm Tap: mismo lenguaje y terraza a evaluar, preservando notación,
   sincronización, pad y calibración.
3. Intervalos vocales: claro de canto a evaluar, portada propia y todos los
   estados de captura/permiso/error/resultados.
4. Defender: completar árbol/polillas/hojas y sus recorridos auxiliares.
5. Simón: unificación del claro/piano y recorridos, conservando horizontal.
6. Afinador, datos, historiales compartidos e inspectores: cubrir superficies
   restantes y estados de guardado/exportación/error.
7. Revisión transversal del libro y recorridos completos, consistencia y
   accesibilidad; corregir las discrepancias encontradas.

El primer recorrido establece la referencia; los siguientes dependen de esa base.
La revisión transversal depende de todos los recorridos. No introducir una
reescritura general antes de poder mostrar una actividad terminada.

Comprobación propuesta: comportamiento visible mediante recorridos públicos,
estados de interacción y resultados; controles de distintas dimensiones, texto
ampliado, reducción de movimiento y notación legible. Mantener las pruebas
existentes de sesiones/audio/DDA/datos como regresión cuando corresponda. La
compilación no sustituye revisión Android; sin emulador, registrar como pendiente
la revisión nativa que requiera teléfono. No añadir pruebas que solo dupliquen
valores decorativos ni atribuir mediciones acústicas a este acabado.

Síntesis, orden, granularidad y enfoque de comprobación confirmados; procede
especificación/tareas e implementación incremental según el flujo ask-matt.
En la publicación, la revisión transversal se agrupa con el sexto incremento,
tal como se presentó en la síntesis final confirmada.

## Verificación de esta sesión

Solo registro de acuerdos e inspección de fuentes; sin cambios de aplicación,
assets nuevos, compilación ni ejecución de pruebas. No abrir emulador.
Tiempo personal pendiente en H0014, sin inferirlo de la duración de herramientas.

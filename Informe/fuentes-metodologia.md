# Metodología: procedencia, artefactos y límites de la reconstrucción

Fecha de reconstrucción y redacción: **2026-11-03**. El autor confirma con «ok»
la síntesis de la entrevista `grill-me` → `grilling` Q1–Q8 y autoriza redactar
el capítulo 4. Se aplica `thesis-writing`, modalidad tipo 1 y primera persona.
Las declaraciones describen su experiencia; los artefactos permiten contrastar
prácticas concretas, sin atribuir aprobación del director o una auditoría completa
de todos los incrementos.

## Origen de las afirmaciones del capítulo

Revisión del 04/11: los nombres de herramientas y la secuencia de uso se concentran
en el capítulo 5, con referencia desde el 4. El contraste de aportes y las fases
de investigación permanecen como procedimiento metodológico. Nuevas declaraciones
y fuentes en `fuentes-herramientas.md`; no se altera la procedencia histórica
descrita a continuación.

Ampliación del autor, 2026-11-03: utilizó Gemini, GPT, Claude y Grok para contrastar
aportes y realizar críticas entre modelos en algunos casos. La fuente de esta
descripción es su declaración; se incorpora como estrategia orientada a reducir
sesgos y detectar errores, sin afirmar un efecto medido o ausencia de sesgo.
No se aportaron versiones concretas, asignaciones exclusivas o comparaciones
controladas, y no se inventan esos datos.

| Afirmación o bloque | Procedencia | Tratamiento |
| --- | --- | --- |
| Investigación con Gemini Deep Research en tres fases y lectura manual parcial | Respuestas Q1/Q4 y prompt aportado por el autor; `guia-metodologica.md`; [prompt conservado](insumos/prompt-deep-research.md) | Se describen las consultas y las instrucciones, distinguiéndolas de procedimientos cuya ejecución pueda demostrarse |
| Definición de objetivos/restricciones/aceptación; delegación; supervisión y lectura selectiva de código | Q2/Q6 | Proceso declarado por el autor, sin afirmar que cada incremento recibió la misma prueba física |
| Experiencia previa con harness y conocimiento de SDD; eficiencia y dificultad estética | Q7/Q8 | Motivación y valoración profesional en primera persona; sin medición comparativa de productividad ni garantía universal de coherencia |
| Conversaciones con colegas y decisiones de revisión | Q3/Q5 | Retroalimentación espontánea durante el diseño; sesiones con tareas registradas como actividad futura |
| Revisión y verificación técnica | Instrucciones de proyecto, guías de incrementos y registros seleccionados | Método de comprobación; cifras y resultados detallados permanecen en pruebas/evaluación |

## Referencias de los enfoques

`lopopolo2026harness` y `githubSpecDriven` ya estaban contrastadas y citadas;
su procedencia está en `fuentes-marco-teorico.md`. El capítulo conserva sus
definiciones y las vincula con instrucciones, contexto, especificaciones y pruebas
propias. Citar Spec Kit como referencia de SDD no atribuye uso de esa herramienta.
La experiencia previa de harness y la aplicación progresiva de SDD proceden de
las declaraciones del autor, no de las publicaciones.

## Lecturas manuales identificadas por el autor

Cuatro entradas nuevas, citadas como ejemplos de lecturas declaradas. Se verifican
identidad y datos editoriales; esta sesión no utiliza sus resultados para añadir
nuevas conclusiones al marco. El autor informó lectura de resúmenes y partes
concretas, sin precisar todos los pasajes o las fechas originales.

| Clave | Datos y fuente contrastados | Acceso usado en esta reconstrucción |
| --- | --- | --- |
| `yuan2022spacing` | Xuechen Yuan (2022), *Evidence of the Spacing Effect and Influences on Perceptions of Learning and Science Curricula*, Cureus 14(1), e21201. [Original en PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC8759977/), DOI 10.7759/cureus.21201 | Cabecera, autoría, resumen y estructura; volumen/número/identificador de PMC, metadatos contrastados con Crossref |
| `lieder2024behavior` | Falk Lieder, Pin-Zhen Chen, Mike Prentice, Victoria Amo y Mateo Tošić (2024), *Gamification of Behavior Change: Mathematical Principle and Proof-of-Concept Study*, JMIR Serious Games 12, e43078. [Editorial](https://games.jmir.org/2024/1/e43078), DOI 10.2196/43078 | Identificación editorial y resumen; se usa fecha del artículo de 2024, diferenciada del preprint de 2022 |
| `sailer2017motivates` | Michael Sailer, Jan Ulrich Hense, Sarah Katharina Mayr y Heinz Mandl (2017), *How gamification motivates: An experimental study of the effects of specific game design elements on psychological need satisfaction*, Computers in Human Behavior 69, 371–380. [Editorial/DOI](https://doi.org/10.1016/j.chb.2016.12.033) | Registro/resumen editorial indexado y Crossref. La apertura directa devolvió 403; no se declara descarga o lectura íntegra en esta sesión |
| `bellotti2010designing` | Francesco Bellotti, Riccardo Berta y Alessandro De Gloria (2010), *Designing Effective Serious Games: Opportunities and Challenges for Research*, iJET 5(SI3), 22–35. [Editorial](https://online-journals.org/index.php/i-jet/article/view/1500) | Autoría, resumen y cita editorial; DOI contrastado como se detalla abajo |

Cuatro respuestas Crossref conservadas en `build/fuentes-metodologia/`.
Crossref omite volumen/número/eid de Yuan; se completan desde la cabecera primaria.
Para Bellotti, el DOI que muestra la página editorial, `10.3991/ijet.v5iSI3.1500`,
devolvió 404 en Crossref. Se utiliza `10.3991/ijet.v5s3.1500`, presente en copias
del original y confirmado por Crossref con el mismo título, autores, fecha, volumen
y número. Crossref informa solo página inicial 22; el intervalo 22–35 se toma de
la cita editorial. Se conserva esta discrepancia en vez de trasladar el DOI
inaccesible sin comprobación. No se afirma descarga de cuatro PDF completos.

## Ejemplo de trazabilidad de un incremento

Se consultan en modo lectura:

- `docs/Definicion-ritmo.md`: entrevista y decisiones del 22/10/2026.
- `docs/Especificacion-ritmo-v1.md` y `docs/Plan-tareas-ritmo-v1.md`: alcance,
  comportamientos y seis incrementos. Su estado resumido puede ser histórico;
  no se toma «R6 pendiente» del plan como ausencia actual de implementación.
- `git show c0f9307:docs/Especificacion-ritmo-v1.md` y el historial de los archivos:
  conservación de la especificación y planes antes del incremento R6.
- `docs/Rhythm-Tap-R6.md`: implementación local de 0.12.12 del 30/10/2026,
  adaptación entre ejercicios, progreso, persistencia y verificación.
- `docs/evidencias/ritmo-r6/host-summary.json`: ejecución JVM, lint/ensamblados
  y ejecución Android pendiente, diferenciadas en el registro.
- `git show --stat d5de1e6` sobre la guía R6 y su evidencia: incorporación de esos
  artefactos al historial en el commit de integración del 30/10.
- `AGENTS.md`, `Plataforma/AGENTS.md`, `CONTEXT.md`, ADR y `docs/ART_DIRECTION.md`:
  contexto de dominio, restricciones de prueba y pautas visuales, con referencias
  a tokens e inventario de recursos.

El capítulo utiliza el ejemplo para explicar el vínculo definición → especificación
→ incrementos → implementación → evidencia. No se ejecutan pruebas de aplicación
nuevas ni se atribuyen los resultados de host a Android o al teléfono físico.

## Evaluación prevista y trabajo pendiente

Ampliación confirmada el **2026-11-04**, entrevista `grilling` Q1–Q10 y síntesis
aceptada con «perfecto»: el autor mantiene el eje de ingeniería y acepta el
relevamiento como insumo que puede cambiar el diseño del prototipo existente.
Informa que preparó ambos formularios para revisión del director el 06/11,
sin declarar difusión, aprobación ni respuestas.

Para evaluar uso acuerda dos modalidades independientes según disponibilidad:
entrega de APK para uso autónomo y sesiones presenciales acompañadas. Se prevén
pocas actividades comunes y exploración libre; cuatro criterios (consignas,
completar ejercicio, interpretar devolución, saber continuar); ayuda tras el
intento inicial cuando se solicite o haya bloqueo; ficha por código de participante
y formulario final autónomo; cierre sobre utilidad y dificultades. Se distinguirán
observaciones directas de experiencias informadas y se registrarán problemas,
impacto, correcciones, nuevas comprobaciones y pendientes. Estos acuerdos son
decisiones de planificación del autor, no resultados de pruebas ejecutadas.
Tareas, duración y cantidad de participantes quedan por acordar con el director.

La estrategia del capítulo articula criterios ya presentes en el proyecto:
sesiones/control de dependencias, pruebas Android, análisis de señales y decisiones
DDA. Las sesiones exploratorias con adultos con conocimientos musicales básicos
proceden del alcance previamente revisado con el autor. Tareas, versión, instrumentos
de registro, participantes concretos y método de medición física deben precisarse
antes de ejecutar el protocolo. El texto plantea esas actividades en futuro.

Permanece abierta la recuperación de registros bibliográficos históricos que
permitan ampliar la reconstrucción. Se describe el recorrido actualmente respaldado
como investigación asistida y consulta focalizada, distinguiendo la propuesta
inicial de revisión sistemática de sus registros de ejecución. La reformulación
o sustitución de Simón sigue pendiente; no se retoma su fundamentación de memoria.

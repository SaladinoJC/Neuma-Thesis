# Ejes metodológicos del informe

## Distribución con Herramientas y tecnologías — 2026-11-04

Entrevista específica Q1–Q9 y síntesis confirmada con «ok»: procedimientos,
responsabilidades, SDD/harness, fases bibliográficas y criterios de evaluación
permanecen en el capítulo 4. Identificación, función, selección y configuración
de herramientas se concentran en el 5. Se sustituye la enumeración de modelos/
asistentes del 4 por referencia cruzada a `sec:herramientas-ia`; se conserva el
contraste de aportes como procedimiento. El nombre Gemini Deep Research y la
secuencia de clientes se presentan en el 5. No se mueve el ejemplo de trazabilidad
de Rhythm Tap. Acuerdos y evidencia en `guia-herramientas.md` y
`fuentes-herramientas.md`.

## Revisión del capítulo 4: acuerdos confirmados — 2026-11-04

Entrevista `grill-me` → `grilling`, Q1–Q10; el autor confirma la síntesis con
«perfecto» y autoriza incorporarla. El eje sigue siendo la conducción del proceso
de ingeniería, con agentes/harness/SDD dentro de ese recorrido.

- Q2: la encuesta se realiza con un prototipo existente y puede modificar el
  diseño, revisar prioridades o motivar el descarte de actividades.
- Q3: alcance de evaluación centrado en funcionamiento técnico y uso.
- Q4/Q7: modalidades independientes según disponibilidad: APK para uso autónomo,
  incluso sin encuentro presencial, o prueba presencial acompañada. No se exige
  una sesión inicial ni un período posterior común.
- Q5: comprensión de consignas, realización del ejercicio, interpretación de la
  devolución y comprensión de cómo continuar; cierre sobre utilidad y dificultades.
- Q6: registrar problemas, impacto, correcciones, nueva comprobación y pendientes.
- Q8: ficha por participante con código y formulario final para uso autónomo.
- Q9: pocas actividades comunes, seguidas de exploración libre.
- Q10: intento inicial sin ayuda; intervención cuando se solicite o ante bloqueo,
  dejando constancia para distinguir desempeño autónomo y acompañado.

El autor informa que armó los formularios de encuesta y contacto para mostrarlos
al director el viernes 06/11. No informa difusión, aprobación ni respuestas.
Las pruebas siguen previstas. Tareas, duración y cantidad de participantes por
acordar con el director; versión e instrumentos concretos pendientes. Distinguir
observación presencial de experiencia informada en modalidad autónoma.

El capítulo 4 incorpora estos acuerdos sin modificar la reconstrucción
bibliográfica ni atribuir resultados de aprendizaje.

## Uso de varios modelos y revisión cruzada — 2026-11-03

El autor informa y pide incorporar el uso de Gemini, GPT, Claude y Grok para
contrastar aportes y, en algunos casos, pedir a un modelo que critique a otro.
Se agrega un párrafo a Organización del trabajo, en primera persona. La reducción
de sesgos y detección de errores se presentan como propósitos de la estrategia;
no se afirma eliminación de sesgos, independencia entre modelos o eficacia medida.
No se asignan versiones, tareas exclusivas ni un procedimiento ciego no informado.
Se conserva la revisión y decisión humana ya descrita.

## Estado actual: síntesis confirmada y capítulo redactado — 2026-11-03

El autor confirma con «ok» la síntesis de Q1–Q8. Se cierra la entrevista y se
redactan las cuatro secciones del capítulo 4: organización iterativa, agentes/
harness/SDD, investigación bibliográfica y estrategia de verificación/evaluación.
Primera persona, valoración profesional de eficiencia y roles de decisión,
delegación, revisión y aceptación explícitos. Coherencia atribuida por el autor
al harness; dificultad estética abordada mediante pautas documentadas.

Se coteja Rhythm Tap como ejemplo de trazabilidad y se incorporan cuatro citas
de lecturas manuales identificadas, sin atribuir lectura integral. El prompt
aportado se conserva en `insumos/prompt-deep-research.md`. Fuentes y artefactos
contrastados en `fuentes-metodologia.md`. Las sesiones estructuradas y las
mediciones físicas siguen previstas, con procedimientos concretos por completar.
Simón permanece en revisión. Los apartados siguientes conservan la historia
de la entrevista y los acuerdos previos.

## Continuación e inicio de entrevista — 2026-11-03

El autor indica que Simón será reformulado, reemplazado o eliminado porque
cuestiona su aporte y encaje en la tesis. La alternativa concreta queda pendiente;
no se elimina software o evidencia existente. Se pospone su fundamentación de
memoria secuencial y se revisará su tratamiento en el informe después de definir
la actividad. La prioridad de escritura pasa a metodología.

El autor invoca `grill-me`, que remite a `grilling`. Se inicia entrevista para
reconstruir investigación asistida, responsabilidades en el desarrollo y criterios
de iteración. Los acuerdos metodológicos de abajo son antecedentes vigentes;
las respuestas nuevas todavía no están obtenidas y no se redactan como hechos.

## Entrevista de reconstrucción: respuestas Q1–Q6 — 2026-11-03

Declaraciones del autor durante `grilling`. Registro previo al cierre de la
entrevista; no constituye todavía una redacción aceptada del capítulo.

### Investigación (Q1 y Q4)

El autor utilizó Gemini Deep Research en varias consultas, organizadas en tres
fases. Aportó el prompt con los siguientes componentes:

1. Metodología, conceptos y preguntas: delimitar términos; diseñar un protocolo
   basado en PRISMA o Kitchenham sin ejecutar todavía la búsqueda; proponer RQ,
   fuentes, consultas booleanas, criterios y extracción.
2. Estado del arte y extracción: comparar productos/prototipos, analizar DSP/ML,
   viabilidad cliente/servidor, adaptación y feedback, y construir una tabla de
   estudios con campos de evaluación. Los datos inaccesibles debían marcarse como
   «No disponible en el abstract/texto accesible».
3. Síntesis, brechas y viabilidad: tendencias, distinción de brechas de investigación,
   tecnología y producto, refutación de novedad y selección de propuestas viables.

El prompt priorizaba los diez años previos y especialmente 2020–2026, con excepciones
seminales; permitía identificación web y conocimiento preentrenado ante restricciones
de acceso. Pedía declarar muros de pago, no inventar referencias/métricas, separar
evidencia/interpretación/conclusión e indicar tipo de evidencia. Solicitaba formato
IEEE en aquellos informes; el informe de tesis conserva APA según su configuración.
Estas son instrucciones de la consulta, no acreditación automática de que cada
filtro o procedimiento haya sido ejecutado por la herramienta.

El autor consultó manualmente resúmenes y partes concretas de artículos. Recuerda:

| Título informado | Original localizado en la consulta de esta entrevista |
| --- | --- |
| Evidence of the Spacing Effect and Influences on Perceptions of Learning and Science Curricula | [Texto original alojado en PMC](https://pmc.ncbi.nlm.nih.gov/articles/PMC8759977/) |
| Gamification of Behavior Change: Mathematical Principle and Proof-of-Concept Study | [Artículo original](https://pmc.ncbi.nlm.nih.gov/articles/PMC10998180/) |
| How gamification motivates: An experimental study of the effects of specific game design elements on psychological need satisfaction | [Registro editorial](https://doi.org/10.1016/j.chb.2016.12.033) |
| Designing Effective Serious Games: Opportunities and Challenges for Research | [Registro editorial](https://online-journals.org/index.php/i-jet/article/view/1500) |

Se localizaron los cuatro títulos para identificar las lecturas declaradas, sin
atribuir lectura integral al autor ni añadir automáticamente estas fuentes a la
bibliografía citada. La identificación actual no reconstruye retrospectivamente
los pasajes exactos consultados o las decisiones históricas de inclusión.

### Responsabilidad en el desarrollo y pruebas (Q2 y Q6)

Antes de implementar, el autor establecía objetivos, restricciones y criterios de
aceptación. Revisaba el resumen de cambios y los reportes de pruebas, y probaba las
funcionalidades. Ante discrepancias, proporcionaba retroalimentación concreta para
corregirlas. La lectura de código era selectiva: arquitectura, comprensión de una
implementación o investigación de comportamientos inesperados.

Delegaba implementación, ejecución de pruebas y correcciones técnicas al agente;
conservaba decisiones de diseño, requisitos y aceptación final. Esta declaración
de proceso debe vincularse con artefactos/versiones al redactar; no demuestra que
cada incremento haya recibido idénticas verificaciones ni convierte ensayos JVM
en mediciones físicas.

### Iteración y devoluciones musicales (Q3 y Q5)

Los cambios surgen principalmente de probar el prototipo, su experiencia musical
y mostrarlo a colegas músicos. Hasta ahora fueron conversaciones espontáneas.
La evaluación mediante tareas y observaciones registradas de forma más metódica
está prevista para una versión más armada; no se presenta como ya realizada.

Pendientes de entrevista para desarrollo: cómo incorporó los enfoques SDD/harness
engineering a su práctica y qué razones/limitaciones personales guiaron el uso de
agentes. Mantener las respuestas separadas de la evidencia verificable del repositorio.

## Respuestas Q7–Q8 y síntesis para confirmar — 2026-11-03

El autor conocía ambos enfoques antes del proyecto. Aplicaba harness engineering
en su trabajo y en proyectos personales o académicos. Conocía SDD, pero no lo
había implementado previamente «técnica y correctamente». Esto no se convierte
en una afirmación de adopción completa o certificada de SDD en Neuma: su aplicación
se describirá mediante especificaciones, criterios y artefactos concretos.

Eligió trabajar con agentes por considerarlo actualmente la forma más eficiente
de trabajar «y POR MUCHO». Es una valoración profesional y personal informada por
su experiencia; no declara una medición comparativa de productividad. Identifica
como dificultades la evolución constante de las herramientas y el aprendizaje
y mejora de sus prácticas mientras trabaja. Considera que todavía tiene margen
para mejorar su eficiencia.

Mantener coherencia no fue un problema relevante en su experiencia: lo atribuye
al uso adecuado del harness. La dificultad más persistente fue conservar una
estética definida a través de iteraciones y sesiones; considera que la abordó
mediante archivos Markdown. Mantener esta atribución y no sustituirla por una
narrativa de problemas de coherencia que el autor no informó. Los documentos
de arte/contexto respaldan la existencia de las pautas; la valoración de su
resultado corresponde al autor y las comprobaciones concretas.

La síntesis de Q1–Q8 se presenta al autor para confirmar comprensión compartida
antes de redactar el capítulo, conforme a `grilling`. La memoria de Simón sigue
pospuesta; no se inicia implementación o eliminación de ese juego.

Acuerdo del autor, 2026-11-02: incluir el proceso de desarrollo de la tesis y del
software tomando como referencias **harness engineering** y **SDD**. El autor
confirmó que SDD significa **Specification-Driven Development**, desarrollo guiado
por especificaciones. También pidió dar un lugar destacado a la investigación y
revisión sistemática utilizadas para buscar proyectos similares, antecedentes y
vocabulario. Esto fija una prioridad de escritura; no acredita aprobación del director
ni la ejecución de todos los pasos de un método formal.

## Ubicación y propósito

| Contenido | Ubicación | Qué debe aportar |
| --- | --- | --- |
| Proceso de investigación, desarrollo y escritura | Capítulo 4, Organización del trabajo | Reconstruir etapas, iteraciones y cambios respecto del plan |
| Harness engineering y SDD | Capítulo 4, Desarrollo asistido por agentes y guiado por especificaciones | Explicar prácticas concretas, su justificación, límites y revisión humana |
| Método de revisión bibliográfica | Capítulo 4, Procedimiento de revisión bibliográfica | Permitir reconstruir búsquedas, selección, extracción y síntesis |
| Conceptos y vocabulario fundamentados | Capítulo 3, Marco teórico | Definir lo necesario para entender el problema y el sistema |
| Hallazgos de la revisión y comparación de proyectos | Capítulo 3, Estado del arte | Sintetizar acuerdos, diferencias, límites y relación con el aporte propuesto |
| Cadenas completas, planillas y decisiones detalladas | Anexos, referenciados desde los capítulos 3 y 4 | Conservar evidencia consultable sin duplicar el cuerpo del informe |

El esquema responde a la modalidad tipo 1 existente. Las fuentes verificadas para
introducir los enfoques están en `fuentes-marco-teorico.md`; las fuentes externas
explican los enfoques y los registros del proyecto respaldan su aplicación real.

## Reconstrucción del desarrollo

Comenzar por un incremento completo y seguir su historia en Git antes de generalizar
el proceso. Candidatos: ritmo o Defender, con entrevistas, especificaciones y planes
fechados. Insumos iniciales consultados:

- `docs/Especificacion-ritmo-v1.md`: origen en entrevista, historias, decisiones,
  casos y condiciones verificables. Su resumen de estado puede ser histórico;
  contrastar con las guías de cada incremento.
- `docs/Definicion-ritmo.md` y `docs/Plan-tareas-ritmo-v1.md`: seguir el vínculo con la
  especificación y comprobar las revisiones antes de redactar la secuencia.
- `AGENTS.md`, `Plataforma/AGENTS.md`, `CONTEXT.md`, `docs/adr/` y
  `.agents/estado-prototipo.md`: instrucciones, contexto, decisiones y continuidad.
- Guías, código, commits y evidencia seleccionada: ejecución, correcciones y resultados.

Explicar quién definió y aceptó los requisitos, qué hizo el agente, qué revisó el
autor y cómo se comprobaron los cambios. Diferenciar prácticas utilizadas desde
el inicio de interpretaciones metodológicas posteriores. No atribuir uso de Spec Kit
por citar su documentación ni presentar el proyecto como evaluación comparativa de
productividad. No extender una comprobación JVM a hardware o aprendizaje musical.

## Reconstrucción de la revisión

El autor informa que se realizó investigación y revisión sistemática. El material
local confirma un borrador extenso que debe auditarse para describir su método real:

- `docs/InvestigacionSistematica.md`, fase 1, secciones 3.3 y 3.5: búsqueda y matriz
  de extracción formuladas en futuro. El cierre de fase solicita habilitar la fase 2.
  Esas partes acreditan una propuesta metodológica, sin demostrar por sí solas su ejecución.
- El mismo archivo contiene fases posteriores y bibliografías; esta sesión no auditó
  todo el corpus ni verificó todas sus citas o afirmaciones. No se concluye que la revisión
  no haya sido realizada: queda pendiente reconstruirla a partir de sus registros.
- El `.html` es una representación del material. Sus fechas de modificación locales
  son 01/09/2026 22:42:58 y 22:40:16 para el `.md`; no se comprobó equivalencia ni
  se considera que esa diferencia pruebe una actualización del contenido. Ambos se preservan.
- Las reglas locales de consulta están en `requisitos-tipo-1.md`, apartado de revisión;
  la guía institucional detalla el protocolo y los registros en páginas PDF 4–16.

Recuperar preguntas, bases/fuentes, fechas, consultas y filtros, resultados exportados,
criterios, duplicados, exclusiones y sus motivos, textos consultados, extracción,
evaluación de calidad, síntesis y uso de IA. Separar exploración de vocabulario,
revisión de estudios y consultas complementarias de documentación técnica.

Registrar como faltante lo que no se conserve. Toda búsqueda nueva debe llevar su
fecha real: no completar retrospectivamente conteos, decisiones o un flujo de selección.
La búsqueda bibliográfica puntual del 02/11 para iniciar el marco teórico se registra
por separado en `fuentes-marco-teorico.md`; no se presenta como repetición de la revisión
histórica ni como búsqueda exhaustiva.

Si se utiliza PRISMA al desarrollar esta sección, contrastar la declaración original:
es una guía de reporte y su mención no acredita por sí misma cómo se condujo la revisión.
Fuente de consulta inicial: [PRISMA 2020](https://www.prisma-statement.org/prisma-2020)
y [declaración original, DOI 10.1136/bmj.n71](https://doi.org/10.1136/bmj.n71).
No se incorpora todavía como referencia citada del informe.

## Próximos incrementos de escritura

Revisión con el autor completada el 04/11. Ampliar la reconstrucción histórica
si aparecen registros adicionales. Antes de ejecutar sesiones y ensayos físicos,
cerrar tareas, instrumentos, versión y condiciones de medición en un protocolo
concreto. Herramientas/tecnologías ya redactadas el 04/11 desde versiones y
decisiones reales; siguiente bloque posible: factibilidad. Mantener
Simón pospuesto y conservar el marco compacto.

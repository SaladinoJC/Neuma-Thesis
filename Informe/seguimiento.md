# Seguimiento de escritura

## Agradecimientos: primer borrador personal — 2026-10-30

Entrevista `grilling` Q1–Q16 y autorización explícita para guardar guía y preparar borrador.
Se creó `guia-agradecimientos.md` y se redactaron en `preliminares/agradecimientos.tex`
los bloques de padres, amigos, Leonel y profesores con `thesis-writing`. Se conservaron
las expresiones personales y los dos fragmentos musicales aportados por el autor, con
atribuciones pendientes de cotejo final. Las demás canciones las elegirá el autor.
Abril, Lydia, hermanos y menciones individuales quedan pendientes; dedicatoria para
Abril prevista en página propia, aún sin redactar ni incorporar. Orden y extensión abiertos.

Verificación: tres pasadas XeLaTeX completadas; PDF de 34 páginas, agradecimientos en
página física 2, revisada visualmente. Sin desbordes ni referencias indefinidas; aviso
esperado de bibliografía vacía. Primer borrador para revisión de voz por el autor.

Actualizado: 2026-10-30. Modalidad tipo 1 confirmada por el usuario el 2026-10-29.

## Estado actual

Base LaTeX por capítulos aceptada por el usuario, que eligió `TesisPrueba.pdf` como
referencia estética el 2026-10-29. Tipografía, márgenes y composición documentados en
`estilo.md`; categorías y contenido según tipo 1. Agradecimientos tiene una hoja propia
después de la portada, con texto pendiente del autor. Los cuatro PDF de pautas se conservan
y tienen copias Markdown por página.
La base sigue mayormente pendiente. Incluye **registros técnicos parciales de R6, D3, D4 y D5** en
implementación y sus planes de pruebas; no constituye una redacción completa de la tesis.
Los ajustes del director se registrarán cuando existan. La bibliografía comienza vacía para evitar
trasladar referencias no verificadas de los borradores.

| Bloque | Estado | Insumo que se debe consultar al redactar |
| --- | --- | --- |
| Portada | Completada con datos del usuario; fecha provisional | Juan Cruz Saladino; director Leonel Guccione; sin codirector; marzo de 2027; título en `datos.tex` |
| Objetivos | Primer borrador: general del protocolo y OE1?OE7 propuestos para revisi?n | Protocolo disponible y acuerdos sobre el alcance |
| Agradecimientos | Primer borrador de padres, amigos, Leonel y profesores; resto pendiente | Entrevista Q1–Q16; `guia-agradecimientos.md`; revisión de voz y selección musical del autor |
| Introducción y alcance | Pendiente | Protocolo y decisiones confirmadas, diferenciados del prototipo actual |
| Marco teórico y estado del arte | Pendiente | Borradores de investigación y fuentes primarias verificadas |
| Metodología, herramientas y factibilidad | Pendiente | Registros del proceso, decisiones y recursos reales |
| Planificación | Pendiente | `docs/Plan_de_trabajo_y_Gantt_Saladino.xlsx`, contrastado con ejecución |
| Requerimientos y diseño | Pendiente | Especificaciones vigentes, `CONTEXT.md`, `docs/adr/` y código |
| Implementación | Registros parciales de R6, D3, D4 y D5; resto pendiente | Incrementos, documentación técnica y versiones identificadas; capítulo 10 |
| Pruebas y evaluación | Planes parciales de R6/D3/D4/D5; resultados consolidados pendientes | Reportes, datos exportados, dispositivos/entornos y límites comprobados; Android R6/D3/D4/D5 pendiente |
| Conclusiones, resumen y abstract | Pendiente | Resultados consolidados y objetivos revisados |
| Referencias y anexos | Pendiente | Citas comprobadas, manuales y evidencia seleccionada |

Los registros del prototipo indican avances de software y también verificaciones pendientes.
Consultar `.agents/estado-prototipo.md` antes de atribuir una entrega o medición. Para trasladar
un resultado al informe, revisar su evidencia original; citar este seguimiento no sustituye esa revisión.

## Trazabilidad

Completar los vínculos con objetivos revisados al acordar el alcance académico; la fila técnica
de D4/D5 no atribuye aprobación a los objetivos propuestos del capítulo 2.
Para cada fila registrar objetivo, requerimiento, decisión/diseño, módulo/versión, caso de prueba,
evidencia y resultado/limitación. La evidencia debe tener ruta o URL y entorno/fecha identificables.

| Objetivo | Requerimiento | Diseño | Implementación / versión | Prueba / entorno | Evidencia | Resultado o limitación |
| --- | --- | --- | --- | --- | --- | --- |
| Vínculo con objetivo académico pendiente de revisión | D4/#26: lectura de tríadas por nombre y avance independiente | Grafía diatónica, respuesta sin octava y progreso por variante/calidad | Defender 0.12.14, capítulo 10 | Corpus, sesión pública, PCM y persistencia JVM; Android preparado | `docs/evidencias/defender-d4/`; guía D4 | Verificación de host; interfaz/audio físico y evaluación con estudiantes pendientes |
| Vínculo con objetivo académico pendiente de revisión | D5/#27: séptimas, inversiones, fa y mezcla | Raíz distinta del bajo, disposición cerrada y grafo por calidad/inversión/claves | Defender 0.12.15, capítulo 10 | Corpus, ruta pública sintética, PCM, compatibilidad y ViewModel | `docs/evidencias/defender-d5/`; guía D5 | Verificación de host; Android, sonido físico y legibilidad pendientes |

Los archivos dentro de `Plataforma/build/` están excluidos de Git. Antes de usarlos como respaldo
único de una afirmación del informe, conservar la evidencia seleccionada en una ubicación duradera
y registrar su procedencia. No duplicar todos los archivos generados.

## Bitácora de escritura y correcciones

| Fecha | Actividad o corrección | Evidencia / sección | Estado |
| --- | --- | --- | --- |
| 2026-10-29 | Usuario confirma modalidad tipo 1 y expresa preferencia por LaTeX; autoriza preparar material de consulta | Conversación; cuatro PDF de `Informe/` | Registrado |
| 2026-10-29 | Extracción automática de 123 páginas, con numeración física y SHA-256 por original | `referencias/manifest.json`; script reproducible | Generado; revisar visuales en los PDF cuando corresponda |
| 2026-10-29 | Preparación de estructura LaTeX de tipo 1 y síntesis de requisitos | `main.tex`, `requisitos-tipo-1.md` | Base provisional; contenido pendiente |
| 2026-10-29 | Compilación XeLaTeX → Biber → XeLaTeX (dos pasadas), comprobación de capítulos y revisión visual de portada, índice, resumen/abstract e introducción | `build/main.pdf`, 26 páginas; logs en `build/` | Aprobado con bibliografía aún vacía; aviso esperado por ausencia de citas |
| 2026-10-29 | Comparación de extracciones con originales y verificación independiente del número de páginas mediante pypdf | `extraer_referencias.py --check`; 17 + 74 + 10 + 22 páginas | Cuatro copias vigentes, marcadores consecutivos y UTF-8 comprobados |
| 2026-10-29 | Usuario acepta la base, elige `TesisPrueba.pdf` solo para estética y solicita hoja de Agradecimientos | Conversación; `estilo.md`, `preliminares/agradecimientos.tex` | Composición aplicada; texto personal pendiente; estructura tipo 1 conservada |
| 2026-10-29 | Usuario indica título y datos para completar únicamente la portada; solicita quitar “Aplicación: Neuma” | `datos.tex`, `preliminares/portada.tex` | Autor Juan Cruz Saladino; director Leonel Guccione; sin codirector; marzo de 2027 provisional. Portada sin marcadores ni leyenda de borrador |
| 2026-10-29 | Compilación y revisión visual de la portada completada; comprobación del título y nombres exactos | `build/main.pdf` y copia de una página `build/portada.pdf` | Sin errores ni desbordes; título distribuido en tres líneas; única advertencia de bibliografía aún vacía |
| 2026-10-29 | Recompilación del estilo y revisión visual de portada, Agradecimientos, índice, encabezados e introducción | `build/main.pdf`, 27 páginas; `build/main.log` | Sin errores; Agradecimientos en página PDF 2 y en el índice; 13 capítulos, sin páginas vacías. Avisos de bibliografía vacía y caja vertical poco llena por el contenido provisional |
| 2026-10-30 | Verificación de host del prototipo Neuma 0.12.11, sin emulador por pedido del usuario | `docs/Rhythm-Tap-lectura-lateral.md`; evidencia local `Plataforma/build/verification/2026-10-30-host/` | Lint nuevo: 0 errores/17 advertencias; 167 JVM y ensamblados previos vigentes (`UP-TO-DATE`), APK idéntico al de prueba. Android final sigue pendiente; no se trasladan resultados al capítulo de evaluación hasta revisar esa evidencia y conservarla en ubicación duradera |
| 2026-10-30 | Implementación local R6/#21 y registro técnico incremental | `docs/Rhythm-Tap-R6.md`; capítulos 10 y 11 | Se describen adaptación entre ejercicios, guardado coordinado y política de revisiones, más plan de pruebas. Valores experimentales, decisiones técnicas provisionales y Android pendiente explícitos; no se da por aprobado el alcance académico |
| 2026-10-30 | Cierre de verificación de host de R6 y recompilación del informe | `docs/evidencias/ritmo-r6/host-summary.json`, XML JVM seleccionados y síntesis de siete escenarios; `build/main.pdf` | 178 JVM ejecutadas sin fallos, lint 0 errores/17 advertencias, app/AndroidTest compilados, firma compatible; Android sin ejecutar. XeLaTeX genera 29 páginas sin errores ni cajas horizontales desbordadas; bibliografía vacía y dos avisos verticales por contenido provisional |
| 2026-10-30 | Implementación D3 y verificación de host sin emulador; registro técnico y plan incremental | `docs/Demostracion-defender-D3.md`; `docs/evidencias/defender-d3/host-summary.json`, tres XML seleccionados y síntesis de ocho escenarios; capítulos 10/11 | 196 JVM ejecutadas sin fallos (18 nuevas), lint 0 errores/17 advertencias, app/AndroidTest compilados. Caso de tercera llegada corregida y cierre provisional verificado. Interfaz, audio físico y Android pendientes; no se acredita eficacia ni aprobación académica |
| 2026-10-30 | Recompilación del informe tras el registro D3 | `build/main.pdf`, 30 páginas; `build/main.log` | XeLaTeX sin errores ni cajas horizontales desbordadas. Bibliografía aún vacía y tres avisos de cajas verticales poco llenas por contenido provisional; no se modificó el estilo |
| 2026-10-30 | Implementación D4 y registro técnico con la skill `thesis-writing`, conservando tipo 1 y formato vigente | Capítulos 10/11; `docs/Demostracion-defender-D4.md`, `docs/evidencias/defender-d4/host-summary.json`, cuatro XML, catálogo y síntesis de ruta | 209 JVM sin fallos (13 nuevas), lint 0 errores/17 advertencias, app/AndroidTest compilados; 115 realizaciones válidas y ocho partidas sintéticas de tríadas. Firma compatible. Android, audio físico, legibilidad y evaluación con estudiantes pendientes; sin emulador |
| 2026-10-30 | Recompilación del informe tras D4 y revisión visual de las secciones agregadas | `build/main.pdf`, 34 páginas; páginas físicas 25/27, capturas y `build/main.log` | Tres pasadas XeLaTeX sin errores, referencias indefinidas ni desbordes horizontales. Bibliografía vacía y cuatro avisos verticales por contenido provisional. Capítulos 1/2 y estilo conservados |
| 2026-10-30 | Implementación D5 y registro incremental con `thesis-writing`, manteniendo el tipo 1 y la estructura vigente | Capítulos 10/11; guía D5; `docs/evidencias/defender-d5/host-summary.json`, tres XML, catálogo y ruta | 218 JVM sin fallos (9 nuevas), lint 0 errores/17 advertencias, app/AndroidTest compilados. 1.502 realizaciones y ruta sintética de 168 partidas/84 objetivos. Compatibilidad D3/D4 comprobada, sin atribuir evidencia de fa/inversiones a las tríadas anteriores. Android, audio físico y eficacia pendientes |
| 2026-10-30 | Compilación y revisión visual del registro D5 | `build/main.pdf`, 36 páginas; páginas físicas 25/26/28/29 y `build/main.log` | Tres pasadas XeLaTeX sin errores, referencias indefinidas ni desbordes horizontales. Bibliografía vacía y cinco avisos verticales por contenido provisional. No se modificaron portada, estilo, capítulos 1/2 ni Agradecimientos |

Para las correcciones del director, agregar fecha, observación, sección afectada, acción y estado
de resolución. Una observación se considera resuelta al verificar el texto o la evidencia modificados.

## Próximo paso

Ampliar el contexto y comenzar el marco teórico con fuentes primarias verificadas. Los capítulos
1 y 2 ya fueron revisados con el autor mediante entrevista Q1–Q11 y síntesis confirmada el
2026-10-30. Revisar con el director las diferencias del protocolo en `contraste-protocolo.md`.
Preparar después criterios medibles y método de evaluación, sin suponer resultados pedagógicos.


## Inicio de redacción — 2026-10-30

Se redactaron los capítulos 1 y 2 con la skill `thesis-writing`, conservando la estructura de tipo 1 y el formato LaTeX existente. El usuario identificó los archivos añadidos a `Informe/` como protocolo y plan entregados; no se infiere aprobación. Se verificó que coinciden con las copias de `docs/` y se leyeron el texto Word y las tres hojas del XLSX.

El registro `contraste-protocolo.md` conserva procedencia, diferencias de arquitectura de audio/ML, mínimo de juegos, integración vocal pendiente, fechas y orden de ejecución, publicación y trazabilidad OE1–OE7. El objetivo general recupera el protocolo; los específicos son una descomposición de trabajo, sin aprobación atribuida al director. El capítulo 1 hace visibles las diferencias de alcance pertinentes. No se incorporaron métricas no verificadas ni resultados pedagógicos.

La revisión con el autor de los capítulos 1/2 se completó mediante la entrevista registrada a continuación. El acuerdo con el director sigue pendiente. El resumen y las conclusiones dependen de resultados.

Verificación de esta redacción: compilar.ps1 completó tres pasadas XeLaTeX; PDF de 33 páginas, sin referencias indefinidas ni desbordes horizontales en el log. Único aviso LaTeX: bibliografía vacía. Se revisó visualmente la primera página de introducción (página física 10 del PDF).

## Incorporación de acuerdos de entrevista — 2026-10-30

Entrevista `grilling`, Q1–Q11, síntesis aceptada explícitamente y aplicación solicitada con `thesis-writing`. Introducción y objetivos ajustados: destinatarios principalmente de conservatorio con base musical; uso complementario; motivación personal e informal; variaciones por habilidad como criterio de diseño; integración vocal de intervalos; sesiones inicialmente con adultos; entrega gratuita, offline y abierta. OE7 exige publicación efectiva. «Aprender» queda como futuro; la transferencia no se presenta como resultado evaluado.

El autor acepta tratar Oboe/ML como alternativas sujetas a medición; justificación y acuerdo con el director pendientes. `contraste-protocolo.md` registra origen y decisiones. Los ejemplos de aplicaciones mencionados en la conversación no se presentan como comparaciones verificadas. Se corrigieron caracteres dañados del registro anterior.

Verificación: compilación de tres pasadas XeLaTeX completada, PDF de 34 páginas. Revisión visual de motivación (página física 11) y cierre de objetivos (15). Se corrigió un desborde de una ruta larga en el capítulo 11 usando `\path`, sin cambiar contenido técnico. Log final sin referencias indefinidas ni desbordes horizontales; permanece el aviso esperado de bibliografía vacía. No se incorporaron nuevas fuentes externas en esta revisión.

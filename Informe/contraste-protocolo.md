# Contraste del protocolo entregado y el desarrollo

Fecha: 2026-10-30. Registro de trabajo para revisión del autor y del director; no modifica los originales ni acredita aprobación académica.

El usuario identifica los archivos de `Informe/` como el protocolo y el plan entregados para solicitar autorización de inicio. El protocolo consigna presentación el 28/09/2026. Las copias de `docs/` son idénticas por SHA-256:

- `PROTOCOLO_Saladino.doc`: `ea6ef419e45d6e79213f0dfe99a2bc8d12f7c28ef4868a56e8409cd6252bf066`.
- `Plan_de_trabajo_y_Gantt_Saladino.xlsx`: `8e445e56b7426c06182418edaa91ac7ba60b8387f98163e8548aee08350a558e`.

Se leyó el texto del Word mediante su tabla de piezas OLE, sin modificarlo; esta lectura no interpreta el Gantt incrustado como imagen. Se consultaron las tres hojas del XLSX con openpyxl, valores almacenados, sin recalcular fórmulas. La tabla siguiente localiza las afirmaciones por sección o identificador de tarea.

| Tema | Protocolo / plan entregados | Decisiones o situación posterior | Tratamiento en el informe |
| --- | --- | --- | --- |
| Objetivo | «OBJETIVO DEL PROYECTO»: integrar análisis vocal, retroalimentación y dificultad; evaluar precisión, latencia y DDA | El prototipo incremental no acredita todavía el cumplimiento conjunto | Objetivo general recuperado; OE1–OE7 revisados con el autor en la entrevista y síntesis confirmada el 30/10; revisión del director pendiente |
| Audio | «ANÁLISIS DE FACTIBILIDAD»: C++/Oboe, buffers sin bloqueos, YIN vectorizado y multiplexor DSP/ML dentro del MVP | Plan, F03/F06/F07: opciones por validar, YIN candidato y ML condicionado. Decisión del usuario 02/10: Kotlin/Compose, YIN inicial, ML posterior; estado técnico en `.agents/estado-prototipo.md` | Discrepancia entre documentos y respecto del desarrollo explícita en 1.4. Falta justificar con mediciones y acordar si se modifica la arquitectura propuesta |
| Garantías técnicas | Factibilidad y síntesis atribuyen latencias deterministas y ventajas/inviabilidad a algoritmos/modelos, incluyendo cifras de tamaño y precisión | Esas afirmaciones no fueron verificadas contra fuentes primarias en esta sesión | No trasladarlas como hechos al texto. Verificar bibliografía y medir el prototipo antes de usarlas |
| Juegos | «PRODUCTO ESPERADO»: cinco como selección inicial | Plan, F02/F09/F10 y P04: mínimo dos, uno vocal, tres adicionales opcionales. Usuario 02/10: demostración inicial Simón/instrumentos y voz separada | Distinguir selección tentativa, mínimo y orden de demostración. La prueba vocal separada y el afinador no se equiparan sin justificación a reproducción vocal de intervalos integrada con DDA |
| DDA | Motor genérico configurable; no impone lógica difusa en el objetivo | Usuario 02/10: Mamdani, intrapartida y desbloqueo; aplicación según juego | Concreción técnica posterior, no contradicción por sí sola. Evaluar reutilización real y comportamiento, sin atribuir calibración a parámetros experimentales |
| Evaluación de uso | Plan F14–F16: sesiones exploratorias con estudiantes | No se aportaron registros de sesiones en esta redacción | OE6 previsto, sin resultados ni mejora de aprendizaje inferida. Confirmar participantes, instrumentos y condiciones antes de ejecutar |
| Publicación | «CARACTERÍSTICAS DE INNOVACIÓN LOCAL» y F21: gratuita, código abierto | Repositorio configurado como privado; publicación/licencia final pendientes | Compromiso de entrega OE7, no resultado cumplido. No se publica nada en esta sesión |
| Cronograma | Protocolo: 01/08/2026–27/02/2027, informes 01/12/2026 y 01/02/2027, 400 h | XLSX: 30 h previas estimadas + 335 previstas + 35 de reserva; última semana/tareas hasta 20/02/2027. Defensa indicada por usuario: marzo 2027 provisional | Diferenciar fin de tareas del XLSX y finalización del protocolo; margen de siete días por aclarar, no asumir su finalidad. No son horas reales medidas |
| Orden de ejecución | Plan F04: primer código 12–30/10; F09/F10: juegos desde diciembre | El estado registra primeros incrementos desde 02/10 y varios juegos anteriores a diciembre | Variación del orden previsto. Reconstruir ejecución antes de redactar planificación; los estados originales del XLSX no son seguimiento actualizado |

## Trazabilidad inicial de objetivos propuestos

| Objetivo | Origen | Evidencia que deberá respaldar su evaluación |
| --- | --- | --- |
| OE1 | Producto esperado, módulo de audio; F05/F06/F09 | Captura real, señal de referencia, estimaciones y retroalimentación |
| OE2 | Producto esperado, motor DDA; F08 | Configuraciones, decisiones reconstruibles y casos de comportamiento |
| OE3 | Objetivo del proyecto; F02/F09/F10/F13 | Recorridos de integración de juego vocal y segundo juego |
| OE4 | Producto esperado, progreso y registro; F11/F12 | Persistencia, exportaciones e interpretación de eventos |
| OE5 | Segundo párrafo del objetivo; F07/F17 | Método, referencias, dispositivos, datos y resultados de precisión/latencia/DDA |
| OE6 | F14/F15/F16 | Protocolo de sesiones y observaciones reales |
| OE7 | Innovación local, acceso y reutilización; F21 | Licencia, auditoría de recursos, código/documentación y versión publicada |

La redacción de los capítulos 1 y 2 describe el proyecto y sus propósitos. No presenta una revisión de literatura ni utiliza las afirmaciones técnicas del protocolo como evidencia externa. El contexto pedagógico y los antecedentes deberán ampliarse con fuentes primarias verificadas. No se agregaron citas bibliográficas ficticias.

## Acuerdos de la entrevista de revisión — 2026-10-30

Origen: respuestas Q1–Q11 del usuario y aceptación explícita de la síntesis («Dale, perfecto, al aplicar los cambios usá thesis-writing»). Son decisiones confirmadas por el autor; no acreditan acuerdo del director ni resultados de evaluación. Actualizan las cuestiones pendientes de la tabla anterior:

- Aporte central: integración de análisis vocal, retroalimentación y dificultad adaptativa, evaluada técnicamente.
- Público: principalmente estudiantes de conservatorio con conocimientos básicos, incluida lectura musical pertinente; también otros estudiantes e interesados con esa base. Comprende niños, adolescentes y adultos. Uso complementario de clases u otras formas de aprendizaje; enseñanza desde cero y sección «Aprender» fuera del alcance actual.
- Motivación: experiencia personal de estudio en conservatorio, herramientas poco adecuadas a sus necesidades, barreras de pago y preocupación por aprender a resolver el minijuego sin transferencia clara. Conversaciones informales locales, sin atribuirles representatividad o metodología de estudio.
- Ejemplos mencionados por el autor: Duolingo Música y Pitch Trainer - Ear training. Son percepciones personales; no se incorporan como comparaciones objetivas al texto. Antes de analizar sus prestaciones, adaptación, precios o restricciones, identificar el producto y verificar fuentes actuales. No se realizó esa verificación en esta sesión.
- Criterio de diseño: explicitar la habilidad de cada actividad y contemplar variaciones pertinentes (por ejemplo, timbre, registro o referencia) para reducir pistas ajenas a ella. No implica implementar todas las variaciones en todos los ejercicios ni acredita transferencia.
- Integración vocal: se mantiene reproducción vocal de intervalos con referencia escuchada, respuesta vocal, retroalimentación y adaptación. El usuario lo identifica como próximo desarrollo; esta sesión solo actualiza documentos y no inicia implementación.
- Arquitectura: autor acepta tratar C++/Oboe y DSP/ML como alternativas condicionadas a mediciones. Permanece pendiente justificar y acordar con el director la diferencia respecto del protocolo.
- Sesiones: inicialmente estudiantes de música adultos con conocimientos básicos. Público de la aplicación más amplio; conclusiones delimitadas a participantes y condiciones observadas. No se evalúa eficacia pedagógica.
- Entrega: toda la versión gratuita, sin suscripción, sin conexión, código y documentación abiertos con licencia compatible. Publicación efectiva y elección de licencia siguen pendientes; OE7 exige publicar, no solo preparar la publicación.

Los capítulos 1 y 2 incorporan estos acuerdos. Los umbrales técnicos, instrumentos de evaluación y detalles de las sesiones se definirán al preparar el método, sin inventar mediciones ni asumir resultados.

# Guía de consulta para el informe de tipo 1

Síntesis manual preparada el 2026-10-29. **Las páginas indicadas son páginas físicas del PDF,
contadas desde 1**, no posiciones tomadas del sumario. Ante una diferencia, revisar el original.
Las guías ofrecen sugerencias de contenido y estructura a acordar con el director
(Instructivo, página PDF 32); esta síntesis no acredita una aprobación del protocolo.

## Qué consultar

| Documento | Páginas PDF | Uso principal |
| --- | --- | --- |
| [Estructura del informe](<referencias/2026_05_27_Estructura Informe TF.md>) | 5–8 | Plantilla de tipo 1; las páginas 1–4 contienen la comparación de modalidades |
| [Instructivo del Trabajo Final](<referencias/2026_05_27_INSTRUCTIVO_TRABAJO FINAL.md>) | 13–14, 18–27, 32–63, 72–74 | Modalidad, citas, criterios de redacción, partes del informe y revisión bibliográfica |
| [Guía operativa V3](<referencias/2026_05_29_GUIA OPERATIVA PARA EL DESARROLLO DEL TF V3.md>) | 3–10 | Escritura incremental, evidencias, trazabilidad, seguimiento con el director y defensa |
| [Apuntes de revisión sistemática](<referencias/2026_06_05_Apuntes revisión sistemática.md>) | 4–16, 17–22 | Método de revisión, planillas de selección/extracción y estructura de su redacción |

La guía operativa complementa al instructivo: expresa que no lo reemplaza (página PDF 3).
Los apuntes bibliográficos ayudan al estado del arte; no cambian la modalidad confirmada de tipo 1.

## Estructura de trabajo

Basada en la plantilla específica, páginas PDF 5–8 de *Estructura del informe*:

| Parte | Contenido a cubrir |
| --- | --- |
| Preliminares | Portada; dedicatoria y agradecimientos opcionales; índices general, de figuras y de tablas; resumen en español, abstract, palabras clave; frase opcional |
| Introducción | Contexto, motivación, problema, alcance y delimitaciones |
| Objetivos | General y específicos |
| Marco teórico / estado del arte | Conceptos necesarios, antecedentes y análisis crítico |
| Metodología | Proceso efectivamente seguido |
| Herramientas y tecnologías | Función y justificación de las herramientas empleadas |
| Factibilidad | Técnica, operativa, temporal, económica y riesgos |
| Planificación temporal | Gantt y camino crítico |
| Requerimientos | Funcionales, no funcionales, restricciones y casos de uso |
| Diseño del sistema | Arquitectura, datos, patrones y modelo de dominio |
| Implementación | Tecnologías implementadas, decisiones relevantes y estructura del código |
| Pruebas y validación | Plan, resultados obtenidos y métricas de calidad |
| Evaluación | Cumplimiento de objetivos, limitaciones y riesgos detectados |
| Cierre | Conclusiones y trabajos futuros; referencias bibliográficas |
| Anexos | Manual de usuario, manual técnico, código fuente referenciado y diagramas completos |

## Reglas que afectan la escritura

- **Resumen y abstract:** hoja en español e inglés con título y palabras clave; resumen
  autosuficiente, sin citas, cuadros ni figuras; se recomienda hasta 300 palabras, un solo
  párrafo e interlínea simple. Redactarlo al terminar el trabajo. Palabras clave: 3–6 términos
  en minúscula, separados por comas (Instructivo, página PDF 33).
- **Portada:** mes y año de la defensa oral y pública (Instructivo, página PDF 32).
- **Índice:** cubrir hasta el tercer nivel; preliminares con numeración romana y cuerpo con
  arábiga conforme a la descripción del instructivo (página PDF 33).
- **Citas y bibliografía:** el instructivo desarrolla APA 7, autor-fecha; correspondencia entre
  citas y referencias, orden alfabético, sangría francesa, doble espacio, sin numeración y solo
  fuentes citadas (páginas PDF 22–24). Se configuró APA como base; los ejemplos de referencias
  de las guías son ejemplos didácticos, no fuentes automáticamente verificadas.
- **Antecedentes:** fundamentar los conceptos y comparar trabajos; enumerar referencias no
  alcanza. Conservar la procedencia de cada afirmación antes de incorporarla
  (Instructivo, páginas PDF 18–24 y 36–39).
- **Trazabilidad:** problema → objetivos → metodología/requerimientos → diseño → implementación
  → validación → resultados → conclusiones. Una matriz puede relacionar requerimientos,
  componentes, módulos y pruebas (Instructivo, páginas PDF 24–25; Guía operativa, 4–5).
- **Desarrollo:** conservar alternativas, motivos, dificultades, versiones, capturas y resultados
  parciales. La bitácora puede registrar fecha, actividad, decisión, justificación, evidencia e
  impacto (Instructivo, páginas PDF 53–54; Guía operativa, 5–7).
- **Validación:** separar plan, ejecución y análisis; declarar entorno, criterios y vínculo con
  requerimientos. Las conclusiones deben derivarse de resultados comprobables
  (Instructivo, páginas PDF 55–60; Guía operativa, 5–7).
- **Apoyo de IA:** verificar la información y las fuentes; conservar la revisión del estudiante.
  Las guías mantienen su responsabilidad académica y contemplan describir las herramientas
  usadas como apoyo (Instructivo, páginas PDF 25–26, 39–41 y 54–55; Guía operativa, 8).
- **Seguimiento:** el instructivo exige dos informes de avance durante el desarrollo
  (página PDF 14); registrar acuerdos y correcciones del director (Guía operativa, página PDF 8).

## Al trabajar en la revisión bibliográfica

Consultar las páginas PDF 4–9 de *Apuntes*: preguntas, protocolo, bases de datos, cadenas de
búsqueda, criterios de inclusión/exclusión, eliminación de duplicados, selección, extracción
y síntesis. Registrar el procedimiento realmente ejecutado y sus fechas.

Las páginas PDF 10–16 proponen dos planillas: **A**, caracterización de estudios, y **B**,
datos que responden las preguntas de investigación. Las columnas de B se derivan de esas
preguntas; sus datos deben conservar la procedencia y distinguirse de interpretaciones propias.
Las páginas PDF 17–22 orientan la redacción de la revisión. Sus microejemplos no constituyen
resultados de este Trabajo Final.

## Elecciones pendientes de acuerdo

- Ratificación de la fecha de defensa, provisionalmente marzo de 2027. El usuario indicó
  título, autor Juan Cruz Saladino y director Leonel Guccione, sin codirector, el 2026-10-29;
  se incorporan a la portada desde `datos.tex`.
- Objetivos y alcance contrastados con `docs/PROTOCOLO_Saladino.doc` y su estado de aprobación.
- Las cuatro guías no establecen una fuente, tamaño o márgenes generales del cuerpo.
  El usuario eligió `TesisPrueba.pdf` como referencia exclusivamente estética el 2026-10-29:
  aplicación y medidas en `estilo.md`, sujetas a ajustes que indique el director.
- Modalidad de revisión con el director: PDF, LaTeX o copia Word, según su preferencia.
- Alcance del procedimiento de revisión bibliográfica efectivamente ejecutado; el archivo de
  investigación existente no acredita por su nombre una revisión sistemática reproducible.

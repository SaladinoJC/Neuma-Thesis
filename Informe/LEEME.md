# Escritura del Trabajo Final

Modalidad **tipo 1: Proyecto y/o Diseño de Ingeniería**, confirmada por el usuario el 2026-10-29.
La base LaTeX fue propuesta por el agente y aceptada por el usuario, que eligió
`TesisPrueba.pdf` como referencia estética el 2026-10-29. En la misma fecha indicó el título
y los datos de portada de `datos.tex`: Juan Cruz Saladino, director Leonel Guccione,
sin codirector y defensa provisional en marzo de 2027. Estos datos no acreditan la aprobación
del protocolo. Neuma es el nombre confirmado de la aplicación.

## Formato recomendado

Escribir el informe en **LaTeX por capítulos**, mantener la bibliografía en **BibLaTeX** y
usar **Markdown para guías y seguimiento**. El PDF es la salida para leer y revisar.

| Opción | Ventaja para este trabajo | Costo o límite |
| --- | --- | --- |
| LaTeX por capítulos | Control de formato, citas, índices y referencias cruzadas; cambios de texto revisables en Git | Requiere compilación; la colaboración directa depende de que el director acepte revisar PDF o LaTeX |
| Markdown con conversión | Sintaxis de redacción breve; permite producir varios formatos | Las tablas complejas, figuras y referencias cruzadas pueden necesitar configuración o extensiones |
| DOCX como fuente | Conveniente si la revisión exige comentarios y control de cambios de Word | Su estructura empaquetada requiere herramientas de extracción/edición; los cambios son menos legibles en Git |

LaTeX y Markdown son texto editable. LaTeX agrega comandos, pero en los párrafos corrientes
esa diferencia no justifica descartar el formato preferido. Para administrar contexto conviene
leer el índice y el capítulo afectado, buscar la regla específica y cargar solo las evidencias
necesarias. No se midió un ahorro de tokens ni se promete un porcentaje.

Se mantiene una sola fuente del informe: los `.tex`. Las notas Markdown organizan el trabajo;
no contienen otra versión de los capítulos. Si el director necesita Word, evaluar una copia
DOCX para revisión: Pandoc admite esa conversión, aunque hay que revisar pérdidas de formato,
macros y referencias. Pandoc no está instalado al preparar esta base y esa exportación no se probó.

## Archivos para empezar

- `main.tex`: orden del informe; capítulos en `capitulos/` mediante `\input`.
- `datos.tex`: título y datos de portada indicados por el usuario; fecha de defensa provisional.
- `configuracion.tex`: formato centralizado y bibliografía APA mediante Biber.
- `estilo.md`: fuentes, medidas y adaptaciones de la referencia estética elegida.
- `preliminares/agradecimientos.tex`: borrador propio suministrado por el autor el 02/11; conserva menciones abiertas para completar después.
- `preliminares/al-lector.tex`: mensaje del autor en una página propia antes de Agradecimientos, sin título ni numeración visible.
- `guia-agradecimientos.md`: acuerdos de voz, destinatarios y pendientes de la entrevista con el autor.
- `bibliografia/referencias.bib`: fuentes verificadas y citadas en el informe.
- `fuentes-marco-teorico.md`: procedencia y límites de las fuentes teóricas y metodológicas.
- `fuentes-estado-del-arte.md`: selección focalizada, fuentes primarias y comparación de antecedentes; pendiente de reconstrucción de la revisión histórica.
- `fuentes-metodologia.md`: procedencia de la entrevista, lecturas y trazabilidad del proceso; prompt conservado en `insumos/`.
- `guia-herramientas.md` y `fuentes-herramientas.md`: acuerdos, inventario técnico y fuentes primarias del capítulo 5; distribución respecto de Metodología.
- `fuentes-factibilidad.md`: recursos y gastos declarados, lectura del protocolo/plan, proyección temporal y límites del capítulo 6.
- `fuentes-planificacion.md`: Gantt original, dependencias, inicio documental de implementación y método de estimación de horas; comparación diferida a febrero.
- `fuentes-requerimientos.md`: alcance propuesto de cuatro actividades, origen de RF/RNF/RES y casos de uso; funciones y criterios pendientes.
- `fuentes-diseno.md`: reconstrucción de arquitectura, modelo conceptual, persistencia y patrones desde código/ADR; figuras y glosario.
- `fuentes-implementacion.md`: consolidación del capítulo 10 por funcionamiento, fuentes de código y correspondencia con incrementos R6/D3–D5/V1–V6.
- `../docs/seguimiento/LEEME.md` y `../docs/seguimiento/horas.csv`: procedimiento y tabla única de dedicación estimada, informada, medida o pendiente.
- `investigacion-spacing-musical.md`: comparación focalizada de experimentos sobre espaciamiento y aprendizaje musical/auditivo, con fuentes y límites.
- `guia-metodologica.md`: acuerdos sobre harness engineering, SDD y revisión bibliográfica; ubicación y evidencia por reconstruir.
- `guia-marco-teorico.md`: acuerdos de entrevista sobre lector, habilidades, aprendizaje musical, práctica y ayudas; enlace a mejoras para próxima implementación.
- `guia-ritmo-timbre-lectura.md`: segunda entrevista y acuerdos sobre primera vista, dictado por selección y escucha de instrumentos/funciones; enlace a propuestas futuras.
- `guia-juegos-serios-y-gamificacion.md`: tercera entrevista y criterios de desafío/DDA, progreso personal, autonomía, recompensas y reintento.
- `requisitos-tipo-1.md`: síntesis de las cuatro guías con páginas de consulta.
- `seguimiento.md`: estado de redacción, trazabilidad y registro de correcciones.
- `referencias/`: extracciones completas de los PDF con páginas físicas y SHA-256.
- `build/main.pdf`: salida regenerable, excluida de Git.

La base contiene borradores de introducción y objetivos revisados con el autor.
Desde la revisión editorial del 03/11, el marco teórico reúne seis bloques:
conceptos y habilidades musicales; aprendizaje y práctica; juegos/gamificación y
motivación; DDA y lógica difusa; audio y estimación; síntesis de relación con Neuma.
Las decisiones de ayudas/progresión y el cálculo difuso se reúnen en el capítulo 9;
ventanas, RMS y particularidades del YIN implementado, en el 10; criterios de
medición de audio, en el 11. Las referencias cruzadas enlazan esos desarrollos.
El estado del arte incluye una primera comparación focalizada de sistemas,
análisis vocal y adaptación, con tabla de aportes y límites. Quedan pendientes
el fundamento específico de memoria secuencial y la reconstrucción de la revisión histórica.
La metodología describe el proceso iterativo, la supervisión del desarrollo asistido
por agentes, harness engineering/SDD y la investigación con Gemini Deep Research
complementada con consultas manuales. Distingue las verificaciones realizadas de
las sesiones y mediciones físicas previstas; sus protocolos concretos siguen pendientes.
La revisión con el autor del 04/11 precisa pruebas de uso autónomo mediante APK y
sesiones presenciales acompañadas, independientes según disponibilidad, con tareas
comunes, exploración libre y registro diferenciado de observaciones y experiencias
informadas. Los formularios de encuesta y contacto están preparados para revisión
del director; no se registran difusión ni resultados.
El capítulo 5 presenta seis secciones sobre herramientas y tecnologías, con una
tabla de configuración de Neuma 0.12.20. La entrevista del 04/11 fundamenta la
elección nativa y el uso de asistentes; Gradle, código, ADR y fuentes oficiales
respaldan funciones y versiones. Metodología conserva los procedimientos y remite
a ese capítulo para la presentación de herramientas.
El capítulo 6 analiza factibilidad con equipos preexistentes, USD 50 mensuales
en suscripciones declaradas y unas 12 horas semanales. Mantiene finalización
prevista a fines de febrero y defensa provisional en marzo de 2027, distingue
estimaciones de horas reales e incluye riesgos y condiciones para el cierre.
El capítulo 7 presenta el Gantt original en dos figuras vectoriales y sus
dependencias. Por decisión del autor del 04/11, la comparación con la ejecución
y su análisis final se realizarán en febrero de 2027. Se inicia un registro de
horas separado con estimaciones previas del Gantt, reconstrucción desde el primer
código del 02/10 y correcciones informadas por el autor. El CSV conserva las
cantidades vigentes y las próximas sesiones informadas, medidas o pendientes.
El capítulo 8 reúne requisitos funcionales, no funcionales, restricciones y seis
casos de uso con diagrama UML. El autor confirma el 04/11 cuatro actividades como
alcance propuesto, incluyendo piano en Defender y selección automática de ayuda
vocal todavía pendientes. Umbrales de precisión/latencia por acordar con el
director; aprobación académica y cumplimiento de requisitos se mantienen separados.
El capítulo 9 incorpora arquitectura lógica del único módulo Android, modelo
conceptual, datos y patrones, con dos figuras vectoriales. Conserva el desarrollo
difuso y los criterios de práctica existentes; las dos extensiones pendientes
se distinguen de la implementación. El glosario de raíz reúne los conceptos.
El capítulo 10 integra organización del código, audio, las cuatro actividades y
datos desde la candidata 0.12.20; las guías conservan la historia incremental.
Pruebas mantiene registros parciales de R6, D3, D4, D5 y V1–V6, pendientes de
consolidación. Conserva títulos y marcadores `[COMPLETAR]` para el resto; no contiene una tesis completa
ni resultados pedagógicos. La estructura reproduce la plantilla de tipo 1, con herramientas y tecnologías
como capítulo propio. Después de la portada aparece el mensaje al lector y luego Agradecimientos,
cada uno en página propia por pedido del usuario. La dedicatoria sigue pendiente del autor.

## Compilar en Windows

Desde la raíz del repositorio:

```powershell
& ./Informe/compilar.ps1
```

Al incorporar o modificar referencias citadas:

```powershell
& ./Informe/compilar.ps1 -Bibliografia
```

Requiere XeLaTeX, Biber y los paquetes declarados en `configuracion.tex`. Esta máquina ya
dispone de MiKTeX y las fuentes Latin Modern. La compilación deshabilita la instalación automática
de paquetes y la ejecución de comandos externos desde TeX. Ante un error se conserva el log.
El comando con bibliografía ejecuta XeLaTeX, Biber y dos pasadas finales de XeLaTeX.
En esta máquina Biber no completó el arranque con el entorno inicial; respondió usando
una caché aislada y locale `C`. El script aplica ambos ajustes solo durante Biber, restaura
las variables al terminar y guarda la caché regenerable en `build/biber-cache/`; no se
atribuyó la causa a una de las dos condiciones por separado.

Se agregó el paquete faltante `csquotes` a MiKTeX. La configuración usa caracteres Unicode
directamente y desactiva los atajos de caracteres de Babel español para evitar la
[incompatibilidad documentada con los hooks de APA](https://github.com/plk/biblatex-apa/issues/260).

La presentación se basa en `TesisPrueba.pdf`: A4, Latin Modern Roman de 12 puntos,
márgenes de 2,54 cm, párrafos con sangría, títulos en negrita y encabezados con línea fina
y número exterior. La portada conserva una composición centrada con espacio generoso.
Las medidas observadas y las adaptaciones están en `estilo.md`. El resumen usa interlínea
simple y la bibliografía interlineado doble, según el instructivo. La referencia aporta
solo estética; las categorías y los contenidos corresponden a `requisitos-tipo-1.md`.
Estos valores responden a la preferencia del usuario y no se atribuyen a una norma institucional.

## Consultar o renovar las guías

Los cuatro PDF originales se conservan en esta carpeta. Las copias Markdown preservan el
texto en bloques por página y su disposición aproximada; no reemplazan imágenes, fórmulas
o tablas visuales. La numeración física de las copias permite volver al PDF aunque el
sumario o la numeración impresa difieran.

```powershell
python Informe/tools/extraer_referencias.py
python Informe/tools/extraer_referencias.py --check
rg -n -i 'resumen|APA|trazabilidad' Informe/referencias
```

El script usa `pdftotext`, disponible con MiKTeX. `--check` verifica las extracciones y el
manifest contra los PDF actuales sin escribir. Cuando cambie un original, regenerar las
copias y revisar también la síntesis de requisitos: esa síntesis es manual.
La lista `SOURCES` del script selecciona las cuatro guías; agregar otros PDF a `Informe/`
no los incorpora automáticamente a este conjunto de referencias institucionales.

## Ciclo de escritura durante el desarrollo

Voz confirmada por el autor el 2026-11-03: escribir en primera persona singular
cuando se describan su experiencia, decisiones y acciones. Evitar referirse a él
como «el autor» o «el estudiante». Para quien utiliza Neuma, emplear «usuario»;
para personas de una evaluación, «participante», conservando «estudiantes de música»
cuando caracteriza una población. Las fuentes y sus autores mantienen su atribución.

Al fundamentar el diseño, relacionar evidencia y decisión concreta. Concentrar
los límites de interpretación y conservar las condiciones de cada estudio;
evitar cerrar cada párrafo con advertencias genéricas sobre lo que Neuma no demuestra.
Presentar el aporte de ingeniería mediante decisiones, integración y verificación,
sin atribuir novedad absoluta, superioridad o eficacia pedagógica no evaluadas.

1. Leer `seguimiento.md` y el capítulo afectado; consultar en `requisitos-tipo-1.md` las reglas
   aplicables y abrir solo las páginas o fuentes pertinentes.
2. Al cerrar un incremento sustancial, registrar qué problema abordó, qué decisión se tomó,
   por qué, qué se implementó y cómo se verificó. Referenciar rutas, versión/commit, fecha,
   entorno y evidencia; redactar la sección correspondiente cuando esos datos lo permitan.
3. Mantener separadas las decisiones confirmadas, las elecciones provisionales, lo implementado
   y lo validado. En audio/DDA distinguir simulaciones, mediciones físicas y evaluación pedagógica.
4. Actualizar la trazabilidad entre objetivos, requerimientos, diseño, implementación y pruebas.
   Registrar las correcciones del director y su estado en el mismo seguimiento.
5. Verificar cada fuente antes de citarla, compilar los cambios y revisar el PDF. Actualizar
   el estado del capítulo y los pendientes. El autor revisa y valida el texto antes de presentarlo.

La revisión existente en `docs/InvestigacionSistematica.md` es material de trabajo: sus citas,
cifras y procedimiento requieren comprobación antes de trasladarlos al informe. Los informes
de avance exigidos por el instructivo deben coordinarse con el director; la escritura incremental
no permite inferir que esos entregables ya fueron presentados.

## Referencias sobre herramientas

- [LaTeX: funciones del sistema de preparación de documentos](https://www.latex-project.org/about/).
- [Pandoc: formatos y límites de conversión](https://pandoc.org/MANUAL.html).
- [MiKTeX: administración de paquetes](https://docs.miktex.org/manual/miktex-packages.html).

Los requisitos académicos se obtienen de los cuatro PDF locales; estos enlaces solo documentan herramientas.

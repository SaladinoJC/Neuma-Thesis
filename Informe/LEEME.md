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
- `preliminares/agradecimientos.tex`: primer borrador personal de Agradecimientos; textos y canciones aún pendientes.
- `guia-agradecimientos.md`: acuerdos de voz, destinatarios y pendientes de la entrevista con el autor.
- `bibliografia/referencias.bib`: fuentes verificadas; comienza vacío a propósito.
- `requisitos-tipo-1.md`: síntesis de las cuatro guías con páginas de consulta.
- `seguimiento.md`: estado de redacción, trazabilidad y registro de correcciones.
- `referencias/`: extracciones completas de los PDF con páginas físicas y SHA-256.
- `build/main.pdf`: salida regenerable, excluida de Git.

La base contiene títulos y marcadores `[COMPLETAR]` y registros técnicos parciales de R6, D3, D4 y D5
en implementación y planes de pruebas. El resto sigue pendiente; no contiene una tesis completa ni
resultados pedagógicos. La estructura reproduce la plantilla de tipo 1, con herramientas y tecnologías
como capítulo propio. Agradecimientos tiene una hoja propia después de la portada por pedido
del usuario. Dedicatoria y frase siguen siendo opcionales.

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

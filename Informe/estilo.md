# Estilo del informe

El usuario elige `TesisPrueba.pdf` como **referencia exclusivamente estética** el 2026-10-29.
La estructura y los contenidos siguen `requisitos-tipo-1.md`. Agradecimientos se incluye
por pedido expreso del usuario en esta misma fecha.

## Inspección de la referencia

PDF local de 104 páginas, A4: 595,276 × 841,89 puntos PDF. SHA-256:
`25c78eb96c1e33f3c0312b7ae6aee98f59cd9952efada2b19e8dfda78a7f1e86`.

Se inspeccionaron fuentes embebidas, posiciones de caracteres y muestras visuales de
portada, índice, preliminares, apertura de capítulo y páginas interiores. Las páginas
citadas a continuación son físicas, desde 1.

| Elemento | Observación de la referencia | Aplicación en la plantilla |
| --- | --- | --- |
| Papel | A4 | A4 |
| Texto principal | Computer Modern Roman, recurso CMR12, aproximadamente 11,96 puntos PDF: 12 puntos TeX | Latin Modern Roman a 12 puntos, equivalente Unicode de la misma tradición tipográfica |
| Encabezados | Familia Computer Modern Sans; título de capítulo o sección y número exterior | Latin Modern Sans; capítulo/sección y número exterior alternados |
| Márgenes laterales | En las páginas interiores el texto ocupa aproximadamente x = 72 a 523,28 puntos PDF: una pulgada por lado | 2,54 cm a cada lado |
| Separación entre líneas | Paso frecuente de aproximadamente 17,93 puntos PDF en el cuerpo | `\onehalfspacing`, alrededor de 18 puntos TeX para texto de 12 puntos |
| Párrafos | Justificados, primera línea con sangría; sin espacio adicional entre párrafos | Sangría de 1,5 em y `\parskip` de 0 |
| Apertura de capítulo | Etiqueta y título en dos líneas, negrita, aproximadamente 24,79 puntos PDF | `\Huge`, 24,88 puntos TeX, con separación vertical amplia |
| Secciones y subsecciones | Negrita; aproximadamente 17,22 y 14,35 puntos PDF | `\Large` y `\large`: 17,28 y 14,4 puntos TeX |
| Paginación | Romanos en preliminares, arábigos en el cuerpo; número centrado abajo al abrir capítulo | Mismo criterio |
| Portada | Bloques centrados, título destacado, autoría y dirección separados, espacio vertical generoso | Misma composición general con los datos propios de UNMdP y Neuma |

Método: `pypdf` para tamaño/recursos y `pdfplumber` para fuentes y cajas de caracteres;
`pdftoppm` para revisión visual. Muestras principales: páginas PDF 1, 5–7, 17, 19, 21–25 y 41.
Las medidas del PDF permiten aproximar la composición; no reconstruyen su archivo LaTeX original.

## Adaptaciones de esta tesis

- Se usa Latin Modern mediante XeLaTeX para escribir directamente caracteres Unicode.
- Los cuatro márgenes se fijan en una pulgada. Los laterales se observan directamente;
  los márgenes verticales y las separaciones de encabezado son una adaptación editorial.
- Los encabezados tienen una línea negra de 0,4 puntos y numeración exterior. Las aperturas
  de capítulo conservan un pie centrado y no muestran encabezado.
- El documento permite encabezados alternados para impresión a doble cara, pero los capítulos
  empiezan en la siguiente página disponible (`openany`), sin insertar hojas vacías de cortesía.
- Los vínculos conservan una presentación discreta, sin el azul/verde intenso de la referencia.
- Agradecimientos ocupa una hoja propia después de la portada, antes del índice general,
  en la sección preliminar y con una entrada en el índice. Su texto queda pendiente del autor.
- El resumen mantiene interlínea simple y la bibliografía APA mantiene doble espacio,
  conforme a las guías de tipo 1. La referencia estética no modifica esas pautas.

No se transfieren textos, temas, categorías, referencias bibliográficas, instituciones, nombres,
logotipos ni agradecimientos de la tesis de muestra. Tampoco se incorpora esa tesis al corpus
de guías institucionales ni se extraen sus 104 páginas a Markdown: esta ficha concentra la
información visual necesaria para trabajar con poco contexto.

La implementación se centraliza en `configuracion.tex`, `main.tex` y los archivos de
`preliminares/`. El formato responde a la preferencia del usuario y no se presenta como
una exigencia institucional. Los datos de portada fueron indicados por el usuario el
2026-10-29 y se mantienen en `datos.tex`, con fecha de defensa provisional en marzo de 2027.
La portada muestra solo autor y director, y el nombre Neuma forma parte del título, sin
una línea adicional para el nombre de la aplicación. El contenido del informe sigue pendiente.

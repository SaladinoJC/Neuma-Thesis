# Libro simple 2D v3 — maestros de estudio

**Selección vigente (usuario, 2026-10-28): C/Escenario como base definitiva.** Se conservan jardín, libro, piano C y placa verde; A/B son antecedentes y no se adopta la placa dorada. El refinamiento modifica solo presentación y controles nativos en `escenario.*`, sin alterar PNG/WebP. Doce hashes comprobados en `escenario-asset-check.json`; no hay nuevos maestros ni exportaciones. La selección de base está confirmada y el ajuste fino queda disponible para revisión visual. No cambia el estado de definición de Personaje documentado aparte.

Solicitud del usuario, 2026-10-28: simplificar v2 y comparar tres composiciones de **una sola portada**, antes de extender el sistema. C/Escenario elegida; A/B quedan como antecedentes.

No llegaron nuevas imágenes adjuntas. Se usó el pedido escrito y `design/references/music-garden/menu.png`, solo como referencia de estilo: sin extraer arte ni copiar personaje. No se afirma haber inspeccionado una segunda imagen nueva.

Seis recursos generados con la herramienta integrada `imagegen`. Prompts exactos, referencias y rutas originales en `provenance.json`; dimensiones, alfa, bytes y hashes en `metadata.json`. Origen IA sin licencia externa atribuida como CC0. Los PNG son maestros raster sin capas internas; la composición HTML/CSS/JS es editable.

| Maestro | Propósito | Dimensiones | WebP consumido |
| --- | --- | --- | --- |
| `garden.png` | Fondo con masas amplias, pocos objetos y tres planos suaves | 1024×1536 RGB | 768×1152 |
| `book.png` | Página limpia, grosor visible y tapa madera; sin ornamentos | 1024×1536 RGBA | 1024×1536 |
| `controls.png` | Dos placas Jugar y una flecha; sin texto/adornos | 1254×1254 RGBA | 1254×1254 |
| `piano-a.png` | Piano vertical para Cuento | 1536×1024 RGBA | 768×512 |
| `piano-b.png` | Piano de cola para Cuaderno | 1536×1024 RGBA | 768×512 |
| `piano-c.png` | Piano bajo una rama amplia para Escenario | 1536×1024 RGBA | 768×512 |

Exportaciones en `assets/exports/prototype-menu-simple-v3/`: 396.270 bytes frente a 9.587.284 bytes de maestros. Conversión Canvas de Chrome, WebP calidad 0,9 con alfa preservado, sin edición creativa. Para reexportar con servidor activo:

```powershell
node design/specimens/prototype-menu-book/simple-capture.mjs --export
```

Ventanas del atlas `(x,y,ancho,alto)`: verde `(80,150,1090,290)`, madera `(80,540,1090,290)`, flecha `(510,890,235,280)`. Ventanas SVG sobre raster; no dibujos SVG que sustituyan el arte. Flecha derecha reflejada.

Señaladores: piezas sencillas de cartón con material del papel reutilizado, base continuada detrás de la página y activa más alta. Son controles semánticos, visualmente integrados al libro; no banderines. Al ampliar texto conservan una fila física desplazable horizontalmente. Texto, foco y áreas táctiles independientes de las imágenes. Sin notación generada; los pianos son decoración, no diagramas didácticos.

Sin bucles ambientales: solo transición y respuesta visual a la presión, con reducción manual/sistema; sin vibración física acreditada. No hay exportaciones Android ni nueva personalización/progresión.

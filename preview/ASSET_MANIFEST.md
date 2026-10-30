# Assets del prototipo Neuma

Recursos del estudio visual del 2026-10-29. El logo horizontal y el favicon ahora proceden del paquete corregido `docs/logo/`, aportado por el usuario para uso temporal. Su inventario y adaptaciones Android están en `assets/ASSET_MANIFEST.md`; el rediseño general permanece fuera de Android. Ningún recurso se obtiene de la red al abrir la demo.

| Ruta | Propósito / formato | Dimensiones / estados | Origen y licencia |
| --- | --- | --- | --- |
| `neuma-concept.html` | Fuente editable HTML/CSS/JS, iconos SVG lineales y partitura desde datos | Objetivo 390 × 844, adaptación móvil/escritorio. Inicio A/B/C, ritmo preparado/cuenta/escucha/ejecución/resultado, lectura preparada/activa/pausa/resultado, paneles, feedback y reducción de movimiento | Código e iconos originales para esta muestra. Los cuatro glifos musicales derivados se detallan debajo. |
| `assets/Bravura.otf` | Maestro de la fuente musical conservado; no se carga en runtime | OpenType, 1.000 unidades por em. Glifos estáticos | Steinberg Media Technologies, [Bravura](https://github.com/steinbergmedia/bravura/blob/master/redist/otf/Bravura.otf), SIL Open Font License 1.1. Descargado 2026-10-29. |
| `assets/Bravura-LICENSE.txt` | Licencia OFL y atribuciones de la fuente | Texto | [Licencia del repositorio original](https://github.com/steinbergmedia/bravura/blob/master/LICENSE.txt). |
| `assets/music-glyphs.json` | Derivación editable de cuatro contornos SVG: clave de sol E050, clave de percusión E069, cabeza negra E0A4 y blanca E0A3 | Coordenadas del maestro, escala a espacios de pentagrama; posiciones y duraciones determinadas por datos JS | Contornos de Bravura extraídos con `fontTools.pens.svgPathPen.SVGPathPen`, bajo la misma OFL. Incluidos literalmente en el HTML para que funcione con doble clic. |
| Síntesis dentro de `neuma-concept.html` | Audio procedural, sin WAV/MP3 externos | Fieltro ~35 ms, madera ~40–45 ms, papel ~95 ms, transición ~65 ms, confirmación con tres ataques; música/taps separados | Diseño procedural propio para el prototipo, sin grabaciones ni muestras externas. |
| `review/*.png` y `review/*/*.jpg` | Evidencia del render y sus estados, no assets del producto | Dimensiones indicadas por nombre o reporte | Capturas propias de este prototipo, con el mismo uso de Bravura que la interfaz. |

Tipografía de interfaz: familias del sistema (`Segoe UI`, sistema Apple, Arial) y Georgia/Times New Roman. No se redistribuyen archivos de esas fuentes.

SHA-256 de los originales:

```text
Bravura.otf          cdf0f893ee1fdb64b7f6713d71ee0dcfc349c0ac01429a8e451b01a9e79f5f3b
Bravura-LICENSE.txt  bdca252186865f3b2c0e76da4a4e335c14ba1d37f80194fca254ddfa88e84fbf
music-glyphs.json   efca19d2f3eabd01aaae5aea2a4eedeb66e9aea21b92b3063b0ac48ecf58c81f
```

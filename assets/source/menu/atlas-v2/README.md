# Atlas ilustrado v2 — maestros de estudio

Estado actualizado: acabado descartado por el usuario el 2026-10-28 por exceso de realismo y ornamentos. Se conserva como antecedente; la referencia nueva es simple-v3.

Generados el 2026-10-28 mediante la herramienta integrada `imagegen`, por solicitud expresa del usuario de mejorar artísticamente A/Atlas. Son recursos originales generados por IA; no se utilizó arte de juegos ajenos ni se les atribuye una licencia externa como CC0. Pendientes de aprobación visual del usuario y preparación final Android.

`provenance.json` conserva los ocho prompts exactos, herramienta, ruta original y SHA-256. `metadata.json` registra tamaño, formato, transparencia real y peso. Se preservan los PNG originales sin modificarlos. No hay un PSD ni un maestro de ilustración con capas internas: los recursos son rasterizados. La **composición por capas es editable** en HTML/CSS/JS y las ventanas del atlas están en `atlas-layout.json`.

| Archivo | Rol y zona segura | Tamaño maestro | Estados |
| --- | --- | --- | --- |
| `garden.png` | Jardín musical con tres planos de profundidad pintados, flores-campana, pabellón y sendero. Márgenes superior/laterales/inferior decorativos; libro por delante. | 1024×1536 RGB | fondo estático, siete motas independientes superpuestas |
| `book.png` | Libro de una página con tapa verde, hojas, fibras y bordes orgánicos. Centro limpio para texto; el relieve se concentra en bordes. | 1024×1536 RGBA | base y franja de borde animada como capa independiente |
| `controls.png` | Atlas: señalador verde, seleccionado ocre, Jugar, placa clara, flecha izquierda y ornamento. Sin texto incrustado. | 1254×1254 RGBA | normal/selección mediante dos materiales; foco, presión y deshabilitado por UI; derecha por reflejo de flecha |
| `simon.png` | Piano entre helechos y flores-campana; portada completa sin texto. | 1536×1024 RGBA | ilustración estática; microinteracción al tocar |
| `instruments.png` | Violín, guitarra y flauta en invernadero; portada y cabecera de Afinador en este estudio. | 1536×1024 RGBA | ilustración estática |
| `rhythm.png` | Tambor, mazas, percusión y semillas; sin notación. | 1536×1024 RGBA | ilustración estática |
| `defender.png` | Árbol de copa ancha y tronco curvo, sin rostro; polillas de alas vegetales. | 1536×1024 RGBA | ilustración estática |
| `character.png` | Viajero botánico genérico y temporal; no define recompensas, creación ni personalización. | 1024×1536 RGBA | personaje estático |

La transparencia solicitada fue comprobada leyendo el canal alfa. El fondo es opaco; los siete recursos restantes conservan alfa también en WebP. Los instrumentos de las portadas son ilustración decorativa, no diagramas didácticos ni ejercicios. No hay partituras o símbolos musicales generados.

## Exportación e integración

Exportaciones consumidas: `assets/exports/prototype-menu-v2/*.webp`. Se obtienen mediante Canvas de Chrome, escalado conservando proporción, WebP calidad 0,9, sin regenerar ni alterar creativamente el contenido. `metadata.json` de exportación registra dimensiones, bytes, alfa y hashes. El servidor del prototipo resuelve `/art-assets/*.webp` a esa carpeta; `/art-assets/*.png` a estos maestros.

Para reexportar, iniciar el servidor indicado en el README del prototipo y ejecutar:

```powershell
node design/specimens/prototype-menu-book/capture.mjs --export
```

No son recursos de `Plataforma/`. Para reemplazar arte, conservar nombre/rol y revisar proporción y zona segura; para sustituir el atlas, actualizar sus ventanas explícitas. El texto permanece independiente y real. La dirección nueva admite más riqueza pintada, textura y volumen que el boceto SVG inicial, por solicitud del usuario; se mantienen botánica, marfil/verdes/ocre, lectura limpia y ausencia de estética neón/vidrio.

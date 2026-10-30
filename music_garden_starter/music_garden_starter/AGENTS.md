# AGENTS.md — reglas de trabajo para Codex

> Ejemplo para fusionar con el `AGENTS.md` existente, si lo hay. No reemplazar otras instrucciones sin revisarlas.

## Identidad visual obligatoria

La aplicación de entrenamiento musical adopta la dirección artística **Music Garden / Botanical Storybook** definida en `docs/ART_DIRECTION.md`. Antes de crear o modificar pantallas, componentes visuales, assets o animaciones, leer ese documento y el inventario `assets/ASSET_MANIFEST.md`.

### Reglas no negociables

1. Conservar coherencia entre pantallas y minijuegos: papel cálido, verdes naturales, ilustración botánica 2D y formas orgánicas. Evitar gradientes neón, estética arcade, glassmorphism y assets de estilo mixto.
2. Usar tokens de `design-tokens.json` (adaptados a las convenciones ya existentes del proyecto), evitando valores visuales hardcodeados cuando exista un token.
3. La notación musical debe ser precisa y renderizada desde datos estructurados; no usar emojis, caracteres improvisados ni imágenes generadas como sustitutos de partituras.
4. La decoración no puede reducir legibilidad, contraste ni tamaño táctil de los controles. Priorizar el aprendizaje musical sobre la ornamentación.
5. Animaciones suaves y orgánicas. No bloquear acciones ni reproducción/validación de notas con animaciones. Respetar reducción de movimiento.
6. Reutilizar componentes antes de introducir duplicados. No añadir bibliotecas de animación, iconos o UI sin revisar soporte y necesidad.
7. Para nuevos assets, actualizar inventario con ruta, propósito, formato, origen/licencia, estados y dimensiones; preservar un editable cuando corresponda.
8. No usar arte extraído de juegos de referencia ni licencias incompatibles.
9. Al finalizar un cambio visual, describir qué reglas se aplicaron, qué pantallas/estados se probaron y cualquier desviación consciente del sistema.

## Proceso para cada tarea visual

- Localizar componentes y tokens existentes.
- Consultar la guía de arte antes de tomar decisiones.
- Proponer la solución más simple que mantenga la identidad.
- Implementar en el stack ya existente, sin imponer otro framework.
- Validar diferentes dimensiones, estados de interacción y legibilidad de notación.
- Registrar los assets agregados y reportar los criterios comprobados.

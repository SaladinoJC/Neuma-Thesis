# Prompts para continuar en Codex

## Primera tarea: auditar proyecto sin modificarlo

Revisá la estructura actual del repositorio, el framework, las pantallas y componentes existentes. Leé `AGENTS.md`, `docs/ART_DIRECTION.md`, `design-tokens.json` y `assets/ASSET_MANIFEST.md`. Sin modificar código todavía, decime:
1. Qué componentes ya cumplen el sistema Music Garden y cuáles se desvían.
2. Dónde conviene integrar los tokens para que sean reutilizables.
3. Qué partes de Music Defender deben ser componentes UI, cuáles assets y cuáles animaciones programáticas.
4. Qué riesgos hay para la notación musical, la accesibilidad y el rendimiento.
5. Un plan incremental para aplicar la identidad, priorizando una sola pantalla de referencia.
No agregues dependencias hasta justificar una necesidad concreta.

## Segunda tarea: implementar después de aprobar plan

Implementá únicamente la primera pantalla acordada según `docs/ART_DIRECTION.md`. Reutilizá componentes y el stack existente; usá tokens semánticos. No agregues ilustraciones generadas ni placeholders finales sin marcarlos como tales. Mantené separada la lógica musical del diseño. Probá estados principales, tamaños de pantalla y contraste. Reportá cambios, pruebas y decisiones de diseño.

## Prompt reutilizable para cualquier pantalla nueva

Creá/modificá [PANTALLA] siguiendo el sistema Music Garden. Antes de tocar código leé `docs/ART_DIRECTION.md`, `design-tokens.json` y `assets/ASSET_MANIFEST.md`. Conservá la coherencia con [PANTALLA DE REFERENCIA YA APROBADA]. Priorizá claridad musical, adaptación móvil y control táctil. Si falta un asset, indicá exactamente cuál, con nombre, tamaño, formato, estados y prompt visual; evitá reemplazarlo arbitrariamente por emojis o estilos incompatibles. Al finalizar, incluí checklist de conformidad visual y pruebas.

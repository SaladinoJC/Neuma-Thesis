# Inventario inicial de assets — Music Garden

## Defender D5 — séptimas e inversiones desde datos (2026-10-30)

| ID | Editable / formato y dimensiones | Propósito y estados | Origen / condiciones |
| --- | --- | --- | --- |
| DEF-D5-STAFF | `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/DefenderApp.kt`, `NoteStaff`; `defender/DefenderChords.kt`, `defenderChordHeadOffsets` / Canvas vectorial, viewport de acordes 144×120 unidades | Tres/cuatro cabezas, plica común, cabezas alternadas para segundas, tres columnas de alteraciones. Sol/fa, fundamental/inversiones, preparación, objetivos, resumen y pérdidas. Notas individuales conservan 100×120. | Código original, sin imagen generada, arte extraído ni fuente nueva. Sustituye el viewport de acordes de DEF-D4-STAFF; el máster sigue siendo Kotlin editable. Revisión visual nativa pendiente. |
| DEF-D5-AUDIO | `defender/DefenderAudio.kt`, `defenderChordTone`, con `voice/ReferenceTone.kt` / PCM16 mono generado, runtime 44.100 Hz y 26.460 muestras | Tres o cuatro voces de la disposición mostrada al acertar; pausa/salida cancelan el audio. | Síntesis propia, sin muestras externas ni archivos persistidos. Banco de extremos a 8.000 Hz de análisis, controles anteriores a 44.100 Hz conservados; audio físico pendiente. |

Se reutilizan DEF-D3-TREE/MOTH/FEEDBACK y tipografías existentes. La reserva horizontal de acordes se amplía y las calidades de respuesta usan una fila desplazable, sin nueva animación. Sin bitmaps, dependencias o archivos sonoros de runtime. [Guía D5](../docs/Demostracion-defender-D5.md).

## Defender D4 — notación y síntesis desde código (2026-10-30)

| ID | Editable / formato y dimensiones | Propósito y estados | Origen / condiciones |
| --- | --- | --- | --- |
| DEF-D4-STAFF | `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/DefenderApp.kt`, `NoteStaff` / Canvas vectorial adaptable, coordenadas 120×120 para tríadas (100×120 en notas) | Tres cabezas, plica común, alteraciones escalonadas y líneas adicionales desde `DefenderChord.notes`; preparación, objetivo, pérdidas y revisión. Pausa oculta objetivos; reducción conserva notación quieta. | Código propio; sin arte extraído ni imagen generada. No es bitmap ni partitura rasterizada. Tarjetas/reservas ampliadas; revisión visual nativa pendiente. |
| DEF-D4-AUDIO | `defender/DefenderAudio.kt`, `defenderChordTone` / PCM16 mono generado, 44.100 Hz, 26.460 muestras | Tres voces simultáneas al acertar, no antes; mezcla promedio de `voice/ReferenceTone.kt` (`referenceTone`) | Código propio sin muestras externas; no persistido, sin licencia externa incorporada. Pruebas digitales de cuatro tríadas Do natural; sonido físico pendiente. |

No hay nuevos archivos de audio/imagen de runtime. Reutiliza DEF-D3-TREE/MOTH/FEEDBACK y fuentes existentes. Las cuatro calidades no requieren ilustraciones diferentes. [Guía y límites D4](../docs/Demostracion-defender-D4.md).

## Defender D3 — gráficos nativos simplificados (2026-10-30)

| ID | Fuente editable / formato | Propósito y dimensiones | Origen y estados |
| --- | --- | --- | --- |
| DEF-D3-TREE | `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/DefenderApp.kt`, `DefenderArena` / Canvas | Árbol guardián sin rostro al pie del campo; ancla normalizada al área útil, copa aproximada de 71×49 dp, conjunto de 73 dp de alto y tronco de 44 dp. No es un raster ni un sprite. | Código original del proyecto, sin material externo ni licencia de terceros incorporada; adaptación conceptual de la guía, sin extraer la referencia. Estático; vidas en texto accesible, tres hojas finales pendientes. |
| DEF-D3-MOTH | Mismo editable, `DefenderArena` / Path y Canvas | Silueta de polilla de alas vegetales en área 24×12 dp, subordinada al pentagrama. | Código original; sin arte extraído. Desplazamiento lógico radial en juego, ubicación quieta al reducir movimiento, oculta en pausa y eliminada al resolver. |
| DEF-D3-FEEDBACK | Mismo editable / Canvas | Anillo de acierto ocre de radio 9 dp; no altera matching/audio. | Código original y token `Success`; visible durante el estado de feedback, omitido con reducción de movimiento. |

Se reutilizan tokens, fuentes, notación vectorial y controles, sin nuevos bitmaps, fuentes, dependencias ni animación de entrada. Son aproximaciones nativas para este incremento, **sin aprobación de arte final ni revisión de capturas**; esa validación se posterga por pedido de no abrir el emulador. [Guía de D3](../docs/Demostracion-defender-D3.md).

**Rhythm Tap R6 (2026-10-30):** controles de práctica/progresión, tempo, consulta de progreso y diagnóstico reutilizan tema, tokens, fuentes y componentes nativos. No se agregan assets ni dependencias. Revisión visual de pantallas/estados/dimensiones pendiente por pedido de no abrir el emulador; [guía R6](../docs/Rhythm-Tap-R6.md).

## Neuma — marca temporal (2026-10-29)

Nombre confirmado por el usuario; uso temporal del paquete **corregido** `docs/logo/` solicitado en esta sesión. Origen: archivos aportados por el usuario; el paquete no declara licencia de distribución de terceros. Se registra su procedencia y autorización de uso en este proyecto, sin atribuir una licencia abierta ni registro comercial. Originales conservados íntegros; `vista-previa.png` y `referencia-conceptual.png` son referencias, fuera de runtime.

| ID | Ruta / exportación | Propósito y formato | Dimensiones / estados |
| --- | --- | --- | --- |
| NEUMA-001 | `docs/logo/assets/neuma-logo-horizontal.svg` y `.png` → `Plataforma/app/src/main/res/drawable-nodpi/neuma_logo_horizontal.png` | Logo de cabecera; PNG copiado sin alteración, SVG editable conservado. La demo HTML embebe el SVG con viewport ajustado al contenido. | 1600×480; presentación estática, nombre accesible Neuma. La palabra del SVG conserva la fuente declarada por el paquete; puede variar por sistema. |
| NEUMA-002 | `docs/logo/assets/neuma-symbol-clivis.svg`, `neuma-app-icon-dark-1024.svg`, `neuma-android-adaptive-foreground.svg` → `res/drawable/ic_launcher.xml`, `neuma_launcher_foreground.xml`, `neuma_launcher_monochrome.xml`, `res/mipmap-anydpi-v26/ic_launcher.xml` y `mipmap-anydpi-v33/ic_launcher.xml` | Icono oscuro, adaptable a la máscara del launcher y variante monocromática del sistema. VectorDrawable desde el trazado original; colores en `design-tokens.json`, sección `brand`. | Original maestro 256×256, icono/foreground 1024×1024; versión monocromática Android 13+, sin animación. |
| NEUMA-003 | `docs/logo/assets/neuma-symbol-clivis.svg` → `res/drawable/neuma_symbol.xml`, `neuma_splash_symbol.xml`, `neuma_launch_background.xml` | Apertura nativa: símbolo verde centrado sobre marfil; adaptación al splash del sistema, sin pantalla adicional ni espera artificial. | Vector 256×256; viewport seguro 384×384 en Android 12+. El splash original 1080×1920 permanece como referencia editable, sin estirarlo a cada pantalla. |
| NEUMA-004 | `docs/logo/assets/neuma-favicon.svg` → favicon embebido en `preview/neuma-concept.html` | Favicon Clivis, HTML autónomo sin red. | 64×64; estático. |

Regeneración: `python Plataforma/tools/prepare_neuma_brand.py`. Los otros logos, wordmark y variantes salvia/marfil del paquete se conservan como alternativas; no sustituyen recursos de minijuegos, iconos de notación ni portadas. La marca temporal no aprueba el rediseño del HTML.

## Notación lateral de Rhythm Tap (2026-10-29)

Adopción autorizada por el usuario tras investigar Glisso. Recursos de VexFlow distribuidos por su proyecto, sin extracción de Glisso. Los SVG se generan desde datos; no hay partituras raster ni arte nuevo.

| ID | Ruta bajo `Plataforma/app/src/main/assets/rhythm/` | Propósito/formato/dimensiones | Origen/licencia | Estados/editable |
| --- | --- | --- | --- | --- |
| NOT-001 | `vexflow-bravura-5.0.0.js` | VexFlow 5.0.0 UMD, 728.171 bytes; Bravura y Academico embebidas; sin dimensiones raster | [Distribución versionada](https://cdn.jsdelivr.net/npm/vexflow@5.0.0/build/cjs/vexflow-bravura.js); MIT con `VEXFLOW-LICENSE.txt` y `VEXFLOW-AUTHORS.md`. Fuentes SIL OFL 1.1 con `BRAVURA-LICENSE.txt` y `ACADEMICO-LICENSE.txt` | recurso local sin modificar; [fuente editable upstream](https://github.com/vexflow/vexflow/tree/5.0.0); SHA-256 `2d4451bf31c877cf289cf173452b77a6b08e362e3cdf75a1136e485eed3c0f89` |
| NOT-002 | `score.html`, `score.js` | Adaptador propio HTML/JS, SVG transparente de 160 CSS px de alto y ancho dinámico; contenedor 176 dp | código original del proyecto, sin código de Glisso | previa/revisión desplazable, conteo, seguimiento, resultados por cabeza, repetición y movimiento reducido; ambos archivos son editables |

Colores recibidos de `GardenTokens`, sin copiar otra paleta. Verde `primary` para acierto por pedido expreso, excepción al ocre común. UI Fraunces/Nunito Sans conservada; Academico se limita a la notación. Alternativa Canvas reutilizada cuando el renderer no puede cargar. Ver [guía y condiciones de prueba](../docs/Rhythm-Tap-lectura-lateral.md).

**Rhythm Tap R5 (2026-10-29):** historial, detalle y errores en Compose con tokens/fuentes/render musical existentes; selector compartido con afinador y resumen compartido con el juego. No se agregan assets, fuentes, audio ni dependencias. Las 40 capturas de `docs/imagenes/ritmo-r5/` son evidencia nativa de UI, no recursos de producto; editables en `ui/RhythmHistoryApp.kt`/`RhythmResultSummary.kt`. [Guía R5](../docs/Rhythm-Tap-R5.md).

**Rhythm Tap R4 (2026-10-29):** número/corchete de tresillo y curvas de ligadura propios desde datos, en el renderer Compose existente. Se reutilizan tokens y fuentes; no hay nuevos assets, fuentes, audio o dependencias. Las 17 capturas en `docs/imagenes/ritmo-r4/` son evidencia de UI, no recursos de producto. [Guía R4](../docs/Rhythm-Tap-R4.md).

**Rhythm Tap R3 (2026-10-29):** reutiliza tokens, tema y FONT-001/002; notación vectorial propia desde duraciones racionales en `ui/RhythmApp.kt`. Sin nuevos assets, fuentes, audio o dependencias. Las 32 capturas de `docs/imagenes/ritmo-r3/` son evidencia de interfaz, no recursos de producto. Ver [guía R3](../docs/Rhythm-Tap-R3.md).

**Afinador A6 (2026-10-29):** historial, filtros, detalle y estados de guardado reutilizan los tokens y FONT-001/002 existentes. Tema compartido con Libre/Guiado/Oído y Datos; sin imágenes, fuentes, audio ni dependencias nuevos. Las capturas de verificación están en `docs/imagenes/afinador-a6/` y no se incluyen como assets de producto. Ver [guía A6](../docs/Demostracion-afinador-A6.md).

> Estado `pendiente`: idea especificada, no es un archivo exportado ni aprobado. Sustituir rutas por las reales del repositorio al integrar.

**Personaje, catálogo confirmado en definición (usuario, 2026-10-28, Q1–Q24):** el [plan](../docs/Plan-personaje-Music-Garden.md) requiere dos tipos de cuerpo compatibles con todos los rasgos/prendas, quince modelos instrumentales (tres por Guitarra/Piano/Trompeta/Flauta/Violín), seis premios de ropa/accesorios y opciones iniciales gratuitas. Respiración/balanceo, gestos y escena de piano requieren piezas/capas/anclajes reutilizables. Esto es una necesidad de producción, no quince archivos ya creados ni aprobación final de sus dibujos. CHAR-001/EXP-007/MG2-008 siguen siendo estudios genéricos, no sustituyen el catálogo modular. En P0 se definirán las piezas reales y se registrarán rutas, dimensiones, estados, editables, origen/licencia y exportaciones. Audio criolla/nylon pendiente de fuente/preparación; timbres existentes se revisarán sin atribuir nuevos modelos a sus grabaciones. No se añadieron assets durante la entrevista.

Confirmaciones del usuario (2026-10-26): Q1–Q4 fijan las bases; Q5–Q7 aprueban el acabado y las siluetas de árbol/polilla de EXP-002 como referencia; Q8–Q10 aprueban menú/tipografías/estados comunes. Q11–Q13 aceptan Defender, Simón y movimiento **solo como primer acercamiento**, sujeto a revisión nativa. Las exportaciones y estados de producción siguen pendientes. Una referencia aprobada o provisional no equivale a un asset listo para runtime.

| ID | Asset propuesto | Rol | Formato preferido | Variantes / estados | Estado | Origen / licencia |
| --- | --- | --- | --- | --- | --- | --- |
| BG-001 | `background_paper_soft.webp` | Fondo común | WebP | light | pendiente | A definir |
| CHAR-001 | Boceto SVG en EXP-007 y nuevo raster MG2-008 | Inicio del libro, sección Personaje | SVG inicial y PNG/WebP ilustrado | presentación estática | solicitado por usuario 2026-10-28; apariencia final y producción Android pendientes | Boceto propio inicial; raster generado con `imagegen`, trazabilidad debajo |
| EXP-007 | `design/specimens/prototype-menu-book/app.js` | Bosque, personaje genérico y cuatro portadas de estudio | SVG editables embebidos; personaje/portadas viewBox 300×320, bosque 480×900 | personaje estático, bosque, piano/guitarra/tambor/árbol | dibujos propios del agente; sin recursos de terceros, fuera de runtime; apariencia pendiente de elección |
| EXP-008 | `design/specimens/prototype-menu-book/index.html` y `style.css` | Tres composiciones interactivas del libro | HTML/CSS/JS editables, capturas PNG y comparación HTML | A/B/C, Personaje/Juegos/Afinador/Logros, índice y destinos simulados | implementación propia; fuentes OFL existentes; A elegida como composición el 2026-10-28, acabado ilustrado posterior pendiente de revisión |
| DEC-001 | `decoration_leaf_corner.svg` | Decoración de esquinas | SVG | izquierda / derecha | pendiente | A definir |
| DEC-002 | `decoration_vine_divider.svg` | Divisor | SVG | neutro | pendiente | A definir |
| HUD-001 | `hud_leaf_life.svg` | Vida de Music Defender | SVG | full / empty | pendiente | A definir |
| GAME-001 | `game_tree_guardian.svg` | Árbol objetivo | SVG con capas o imagen | idle / hit / low_life | pendiente | A definir |
| EN-001 | `enemy_moth_green.svg` | Enemigo básico | SVG / atlas | idle / selected / success / escaped | pendiente | A definir |
| EN-002 | `enemy_firefly_gold.svg` | Enemigo variante | SVG / atlas | idle / selected / success / escaped | pendiente | A definir |
| FX-001 | `effect_sound_wave` | Acierto | Animación programática | success | pendiente | Generación procedural |
| FX-002 | `effect_pollen` | Transformación de enemigo | Partículas programáticas | success | pendiente | Generación procedural |
| MUSIC-001 | `component_note_staff` | Pentagrama, clave y nota | Render estructurado/SMuFL | notas y alteraciones | pendiente | Implementación propia / fuente según licencia |
| UI-001 | `component_primary_button` | CTA | UI nativa | default / pressed / disabled / focus | pendiente | Implementación propia |
| UI-002 | `component_paper_card` | Panel común | UI nativa | default / selected / disabled | pendiente | Implementación propia |

## Libro simple 2D v3 — una portada, tres composiciones

**Adaptación Android en revisión (2026-10-28):** `Plataforma/app/src/main/res/drawable-nodpi/garden_*.webp` copia MG3-001/002/003/006 y las cuatro ilustraciones siguientes. `assets/exports/android-menu-v1/android-manifest.json` registra ruta, peso y SHA-256 de cada copia; `tools/prepare_garden_assets.py` reproduce la preparación. Los maestros PNG y prompts están en `assets/source/menu/simple-v3/native/`; exportaciones 768×512 (juegos) y 512×768 (Personaje), WebP RGBA, estado estático, origen `imagegen` original con referencia de estilo MG3-006 y sin arte externo. Los PNG son fuentes conservadas sin capas internas editables; composición, texto y controles permanecen editables en Compose. `res/font/garden_*.ttf` copia FONT-001/002 con licencias OFL en `app/src/main/assets/music-garden/licenses/`. Estas imágenes son muestras de menú para revisión nativa, no modelos finales del plan P0–P7.

| ID | Maestro → exportación Android | Propósito | Formato / estados |
| --- | --- | --- | --- |
| MG3-N-001 | `assets/source/menu/simple-v3/native/instruments.png` → `assets/exports/android-menu-v1/instruments.webp` → `res/drawable-nodpi/garden_instruments.webp` | Portada Instrumentos y Afinador | PNG fuente 1536×1024 → WebP RGBA 768×512; estático |
| MG3-N-002 | `assets/source/menu/simple-v3/native/rhythm.png` → `assets/exports/android-menu-v1/rhythm.webp` → `res/drawable-nodpi/garden_rhythm.webp` | Portada Rhythm Tap | PNG fuente 1536×1024 → WebP RGBA 768×512; estático |
| MG3-N-003 | `assets/source/menu/simple-v3/native/defender.png` → `assets/exports/android-menu-v1/defender.webp` → `res/drawable-nodpi/garden_defender.webp` | Portada Defender | PNG fuente 1536×1024 → WebP RGBA 768×512; estático |
| MG3-N-004 | `assets/source/menu/simple-v3/native/character.png` → `assets/exports/android-menu-v1/character.webp` → `res/drawable-nodpi/garden_character.webp` | Personaje genérico del menú | PNG fuente 1024×1536 → WebP RGBA 512×768; estático, provisional |

**Actualización del 2026-10-28:** el usuario elige C/Escenario como base definitiva. Se conservan el bosque, libro, `piano-c.webp` y placa verde de Jugar; no se adopta la dorada de B. El refinamiento de presentación está en `escenario.css/js`: filtros y textura reutilizada, sin alterar ningún raster (doce hashes comprobados en `escenario-asset-check.json`). A/B son antecedentes no seleccionados. Ajuste fino disponible para revisión visual; no se extiende el arte a otras pantallas.

| ID | Editable / recurso | Propósito | Formato / dimensiones | Estados y origen |
| --- | --- | --- | --- | --- |
| UI-C-NAV | `design/specimens/prototype-menu-book/escenario.js` y `escenario.css` | Flechas sobre papel recortado, alto contraste | Dos trazos SVG propios en viewBox 24×24; control nativo 48×48 | normal, presionado, foco y deshabilitado; implementación propia, sin biblioteca; no modifica el atlas existente |
| UI-C-TABS | `design/specimens/prototype-menu-book/escenario.css` | Cartón irregular insertado detrás del papel | UI nativa con material MG3-002 reutilizado; activa sobresale 10 px | normal, seleccionado, foco y presión; etiquetas reales, 13 px a 360; implementación propia |

La textura adicional reutiliza el centro del libro al 7 %; no se generan/añaden imágenes. Efectos visuales y transiciones no cambian reglas musicales ni progresión. Evidencia específica en `escenario-verification.json` y README del prototipo.

Pedido del usuario, 2026-10-28: simplificar; ninguna variante elegida todavía. Referencia: `menu.png` guardada y pedido escrito; no se recibieron nuevas imágenes. Estado común: estudio integrado solo en `simple.html`, sin consumo Android. Origen: herramienta integrada `imagegen`, sin arte extraído ni licencia externa atribuida. Prompts exactos en `assets/source/menu/simple-v3/provenance.json`; maestros/exportaciones con dimensiones, alfa, peso y hashes en sus `metadata.json`. Composición editable en `simple.html`, `simple.css`, `simple.js`; PNG sin capas internas.

| ID | Maestro → exportación consumida | Propósito | Dimensiones maestro → exportación | Estados |
| --- | --- | --- | --- | --- |
| MG3-001 | `assets/source/menu/simple-v3/garden.png` → `assets/exports/prototype-menu-simple-v3/garden.webp` | Fondo de grandes masas 2D | PNG RGB 1024×1536 → WebP 768×1152 | estático |
| MG3-002 | `assets/source/menu/simple-v3/book.png` → `assets/exports/prototype-menu-simple-v3/book.webp` | Libro limpio, grosor y material de señaladores | PNG RGBA 1024×1536 → WebP RGBA 1024×1536 | base estática; señalador activo más alto |
| MG3-003 | `assets/source/menu/simple-v3/controls.png` → `assets/exports/prototype-menu-simple-v3/controls.webp` | Placas Jugar verde/madera y flecha simple | PNG RGBA 1254×1254 → WebP RGBA 1254×1254 | normal/foco/presión/deshabilitado; flecha reflejada |
| MG3-004 | `assets/source/menu/simple-v3/piano-a.png` → `assets/exports/prototype-menu-simple-v3/piano-a.webp` | Simón A/Cuento | PNG RGBA 1536×1024 → WebP RGBA 768×512 | estática |
| MG3-005 | `assets/source/menu/simple-v3/piano-b.png` → `assets/exports/prototype-menu-simple-v3/piano-b.webp` | Simón B/Cuaderno | PNG RGBA 1536×1024 → WebP RGBA 768×512 | estática |
| MG3-006 | `assets/source/menu/simple-v3/piano-c.png` → `assets/exports/prototype-menu-simple-v3/piano-c.webp` | Simón C/Escenario | PNG RGBA 1536×1024 → WebP RGBA 768×512 | estática |

Seis WebP: 396.270 bytes; alfa preservado. Texto, foco y áreas táctiles separados. Señaladores de cartón con bases detrás de páginas; sin notación generada ni bucles ambientales. Ventanas del atlas documentadas en README de maestros. Otras secciones conservan acceso al prototipo anterior, sin nuevo arte.

## Atlas ilustrado v2 — antecedente, acabado descartado

Solicitud y elección A/Atlas: usuario, 2026-10-28. Estado común: **acabado v2 descartado por el usuario; antecedente funcional y de trazabilidad, sin consumo Android**. Origen común: herramienta integrada `imagegen`, sin arte externo extraído ni licencia externa atribuida. Todos los prompts exactos, hashes y rutas originales están en `assets/source/menu/atlas-v2/provenance.json`; dimensiones/alfa/pesos en los metadatos de maestros y exportaciones. Los PNG preservan los originales; HTML/CSS/JS y `atlas-layout.json` son la composición editable. No existen editables internos por capas de los dibujos.

| ID | Maestro real → exportación consumida | Propósito | Formato / dimensiones maestro → exportación | Estados |
| --- | --- | --- | --- | --- |
| MG2-001 | `assets/source/menu/atlas-v2/garden.png` → `assets/exports/prototype-menu-v2/garden.webp` | Jardín musical con profundidad, vegetación y flores-campana | PNG RGB 1024×1536 → WebP 768×1152 | fondo; motas superpuestas independientes |
| MG2-002 | `assets/source/menu/atlas-v2/book.png` → `assets/exports/prototype-menu-v2/book.webp` | Papel sutil, bordes orgánicos, tapa y hojas con volumen | PNG RGBA 1024×1536 → WebP RGBA 1024×1536 | base; borde recortado animado sin alterar texto |
| MG2-003 | `assets/source/menu/atlas-v2/controls.png` → `assets/exports/prototype-menu-v2/controls.webp` | Atlas de señaladores, Jugar, placa clara, flecha y ornamento | PNG RGBA 1254×1254 → WebP RGBA 1254×1254 | normal/selección; foco/presión/deshabilitado por UI; flecha derecha reflejada |
| MG2-004 | `assets/source/menu/atlas-v2/simon.png` → `assets/exports/prototype-menu-v2/simon.webp` | Portada de piano en jardín para Simón | PNG RGBA 1536×1024 → WebP RGBA 960×640 | estática, microinteracción ornamental |
| MG2-005 | `assets/source/menu/atlas-v2/instruments.png` → `assets/exports/prototype-menu-v2/instruments.webp` | Portada de Instrumentos; cabecera de Afinador reutilizada en estudio | PNG RGBA 1536×1024 → WebP RGBA 960×640 | estática |
| MG2-006 | `assets/source/menu/atlas-v2/rhythm.png` → `assets/exports/prototype-menu-v2/rhythm.webp` | Portada de percusión para Rhythm Tap | PNG RGBA 1536×1024 → WebP RGBA 960×640 | estática |
| MG2-007 | `assets/source/menu/atlas-v2/defender.png` → `assets/exports/prototype-menu-v2/defender.webp` | Portada con árbol sin rostro y polillas vegetales | PNG RGBA 1536×1024 → WebP RGBA 960×640 | estática |
| MG2-008 | `assets/source/menu/atlas-v2/character.png` → `assets/exports/prototype-menu-v2/character.webp` | Personaje genérico, sin recompensas ni personalización | PNG RGBA 1024×1536 → WebP RGBA 768×1152 | estática |

Integración y reproducción en `design/specimens/prototype-menu-book/README.md`, `art.js` y `art.css`. Capturas y galería `art-*` registran los estados. Las exportaciones preservan alfa; no hay texto ni notación musical incrustados en el arte. La textura/volumen más ricos se limitan a los materiales ilustrados pedidos por el usuario; textos, controles semánticos y movimiento reducido siguen las reglas comunes.

## Láminas exploratorias (fuera de los assets de aplicación)

| ID | Ruta real | Propósito | Formato / dimensiones | Origen y condiciones | Estado |
| --- | --- | --- | --- | --- | --- |
| EXP-001 | `design/specimens/music-garden-material-v1.png` | Primera exploración de papel, rama, árbol y polilla | PNG opaco, 1536 × 1024 px | IA con herramienta integrada `imagegen`; prompt homónimo; sin licencia externa atribuida; sin máster por capas | exploración anterior, demasiado detalle/relieve según revisión del agente |
| EXP-002 | `design/specimens/music-garden-material-v2.png` | Revisión más plana, tinta y siluetas simplificadas | PNG opaco, 1536 × 1024 px | IA con herramienta integrada `imagegen`, edición de EXP-001; prompt homónimo; sin licencia externa atribuida; sin máster por capas | referencia de acabado/árbol/polilla aprobada en Q5–Q7; no asset final |
| EXP-003 | `design/specimens/menu-v1.html` | Composición estática del menú y estudio de estados | HTML con SVG editable y capturas PNG a 360/411/600 px; texto 200% a 360 px | Generación propia desde `build_menu_preview.py`; motivos propios provisionales; fuentes OFL registradas debajo | referencia de composición/tipografías/estados aprobada en Q8–Q10; fuera de la aplicación |
| EXP-004 | `design/specimens/defender-v1.html` | Estudio de campo y estados de Defender | HTML/SVG propios; capturas PNG 360×800 y 411×891 | Generador editable `build_games_preview.py`; renderer de música trasladado desde NoteStaff y datos estructurados; árbol/polilla propios de estudio desde referencia IA | aceptado solo como primer acercamiento Q11; revisión nativa pendiente; sin consumo Android |
| EXP-005 | `design/specimens/simon-v1.html` | Estudio de escucha/respuesta de Simón horizontal | HTML/SVG propios; PNG 640×360, 800×360 y 891×411 | Generador editable `build_games_preview.py`; geometría cromática basada en PianoKeyboard | aceptado solo como primer acercamiento Q12; revisión nativa pendiente; sin consumo Android |
| EXP-006 | `design/specimens/motion-v1.html` | Onda/polen y alternativa reducida | HTML/CSS/SVG propios; PNG 411×700 | Generador editable `build_motion_preview.py`, arte de estudio compartido; ocho partículas programáticas | aceptado solo como primer acercamiento Q13; tiempos ajustables; sin audio ni gameplay |
| REF-007 | `design/references/music-garden/menu.png` | Inspiración de menú de libro/señaladores/páginas | PNG, 532 × 725 px | Captura aportada por el usuario; autor/fuente original no informados; no se extrae arte para producción | referencia para diseño en definición; nueva composición sin aprobar |

## Fuentes de los estudios (sin integrar en Android)

| ID | Ruta | Uso | Formato/estado | Origen/licencia |
| --- | --- | --- | --- | --- |
| FONT-001 | `design/specimens/fonts/fraunces-variable.ttf` | Títulos de muestras | TTF variable; pareja/uso aprobados en Q9, sin integrar en Android | Google Fonts/Fraunces; SIL OFL 1.1, copia `fraunces-OFL.txt`, hashes/procedencia en README de fonts |
| FONT-002 | `design/specimens/fonts/nunito-sans-variable.ttf` | Instrucciones y controles de muestras | TTF variable; pareja/uso aprobados en Q9, sin integrar en Android | Google Fonts/Nunito Sans; SIL OFL 1.1, copia `nunito-sans-OFL.txt`, hashes/procedencia en README de fonts |

Método y límites en `design/specimens/README.md`. Ninguna lámina se consume desde `Plataforma/`; ninguna aprueba por sí sola notación, tipografía, contraste o rendimiento.

## Convenciones

**A5, entrenamiento de oído (2026-10-28):** reutiliza FONT-001/002 desde `res/font/garden_*.ttf`, sin nuevos raster, audio o dependencias. `ui/EarTunerApp.kt` es composición Compose editable con tokens de color/espaciado/tamaño táctil compartidos. Estados: preparación, medición oculta, comprobación, corrección, revisión, resumen e interrupción. Capturas sintéticas de verificación en `docs/imagenes/afinador-a5/`; son evidencia de UI, no assets de producto ni mediciones físicas. Referencias sonoras A4 conservadas sin cambios.

- No crear imágenes para controles básicos si se pueden dibujar nativamente.
- Mantener originales editables dentro del repositorio o enlazados de manera estable.
- Cada asset final requiere fuente, licencia y, cuando aplique, atribución.
- Anotar fecha/versión o commit cuando se cambien assets utilizados en pantallas aprobadas.

# Music Garden — registro de referencias aprobadas

Selección vigente (usuario, 2026-10-28): **C/Escenario es la base definitiva**, refinada solo en la portada de Simón. Categoría/título arriba de la ilustración; aumento medido 16,2–18 % a 360/411/600; cartón irregular/activa +10 px; papel y fondo suavizados; flechas de papel 48×48 y Jugar verde. Raster originales conservados por hash; sin nueva vegetación/dependencias. Excepción a 320: imagen +8 % por bordes. Evidencia y ruta `simple.html?variant=C` en README del prototipo. La definición de Personaje sigue su propio documento/entrevista.

Actualización vigente (usuario, 2026-10-28): acabado v2 descartado por exceso de realismo/ornamentos. Se solicitan tres composiciones 2D simples de una sola portada con señaladores físicos detrás de páginas. A/Cuento, B/Cuaderno y C/Escenario en `simple.html`, ninguna elegida todavía. No llegaron imágenes nuevas; se usó la referencia guardada `menu.png` y el pedido escrito. Ver `docs/Diseno-menu-libro-Music-Garden.md` y `assets/source/menu/simple-v3/README.md`.

## REF-001 — material, árbol y polilla

- Referencia: [lámina v2](../../specimens/music-garden-material-v2.png), 1536 × 1024 px.
- Confirmación: usuario, 2026-10-26, Q5 «sí, prefiero ese acabado», Q6 «me gusta», Q7 «dale».
- Normativo: papel recortado con tinta botánica; acabado mate y cálido; árbol sin rostro de copa ancha y tronco curvo; silueta de polilla con alas vegetales; familia cromática de verdes sobre papel con acentos ocres.
- Adaptable: cantidad de venas/detalles, densidad del follaje y complejidad de antenas según tamaño de uso. La información musical mantiene prioridad.
- No aprobado por esta referencia: una pantalla completa, texto sobre textura, valores exactos de color tomados por cuentagotas, animaciones, formas de controles o uso del PNG completo como sprite.
- Origen: herramienta integrada `imagegen`, edición de la exploración v1. Prompts exactos y método en [specimens](../../specimens/README.md); sin máster editable por capas.

El archivo canónico se conserva en `design/specimens/` para evitar duplicar imágenes divergentes. Este registro le otorga estado de referencia aprobada para los rasgos enumerados, no de asset final.

## REF-002 — menú y tipografías

- Revisión vigente: el usuario propone reemplazar la lista por un libro con señaladores. La composición de REF-002 pasa a antecedente en revisión; se mantienen sus aportes de tipografía/controles. Ver [nuevo diseño en definición](../../../docs/Diseno-menu-libro-Music-Garden.md). No reutilizar la lista por encima de esta instrucción más reciente.
- Referencias: [menú 360](../../specimens/menu-v1-360.png), [menú 411](../../specimens/menu-v1-411.png), [menú 600](../../specimens/menu-v1-600.png), [texto 200%](../../specimens/menu-v1-360-text200.png).
- Confirmación: usuario, 2026-10-26, Q8 «ok» y Q9 «ok».
- Normativo: composición de atlas con tarjetas en una columna, cabecera botánica contenida, misma familia de superficies y jerarquía; Fraunces para títulos y Nunito Sans para instrucciones/controles.
- Adaptable: reflujo, saltos de línea y densidad según espacio útil y texto ampliado; no comprimir texto o controles para imitar una captura.
- Pendiente: acabado final de miniaturas, render nativo, tacto, TalkBack y barras del sistema.
- Fuente editable: `design/specimens/build_menu_preview.py`; archivo pasivo `menu-v1.html`. Conservar capturas aprobadas; una revisión visual posterior usa un nombre versionado nuevo.

## REF-003 — estados comunes

- Referencia: [estados v1](../../specimens/menu-v1-states.png).
- Confirmación: usuario, 2026-10-26, Q10 «ok».
- Normativo: normal/presionado en verde, foco visible independiente, deshabilitado diferenciado, selección clara con contorno y marca, acierto ocre y error terracota oscuro con texto. Valores semánticos de la muestra promovidos a `design-tokens.json`.
- Pendiente: respuesta táctil/lectores de pantalla y combinaciones sobre nuevas superficies en Android; movimiento no aprobado por una captura estática.

## REF-004 — Defender, primer acercamiento

- Referencias: [360](../../specimens/defender-v1-360.png), [411](../../specimens/defender-v1-411.png) y sus capturas de acierto, error, llegada y pausa en `specimens/`.
- Aceptación: usuario, 2026-10-26, Q11 «como un primer acercamiento está bien».
- Estado: **provisional para la primera iteración**, no aprobación final de pantalla o assets.
- Base: notación dominante sobre superficie clara, criaturas secundarias, árbol/vidas botánicos, HUD y respuesta inferior.
- Revisar en Android: composición en movimiento, tamaño musical, espacio disponible, controles, acabado de criaturas/árbol y accesibilidad. Conservar reglas, trayectorias y límites funcionales.

## REF-005 — Simón, primer acercamiento

- Referencias: [escucha](../../specimens/simon-v1-listening.png), [respuesta](../../specimens/simon-v1-response.png) y [ancho menor](../../specimens/simon-v1-small.png).
- Aceptación: usuario, 2026-10-26, Q12 «como un primer acercamiento está bien».
- Estado: **provisional para la primera iteración**, no aprobación final de pantalla o assets.
- Base: piano horizontal dominante, decoración exterior, fase explícita y resaltado ocre con marca adicional. Mantener la geometría y reglas existentes.
- Revisar en Android: lectura de fases, teclas negras, tacto, fuente ampliada y sincronización de resaltado/audio.

## REF-006 — movimiento, primer acercamiento

- Referencia: [muestra](../../specimens/motion-v1.html), [fotograma](../../specimens/motion-v1-frame220.png) y [alternativa reducida](../../specimens/motion-v1-reduced.png).
- Aceptación: usuario, 2026-10-26, Q13 «como un primer acercamiento está bien».
- Estado: **provisional para la primera iteración**. Presión 140 ms y acierto 450 ms son puntos de partida a ajustar.
- Base: onda/polen sutiles, resultado inmediato, acciones sin espera y alternativa reducida que conserva información.
- Revisar en Android: ritmo percibido, partículas, respuesta real y ajustes del sistema; no acreditar FPS o audio mediante esta muestra.

## Alcance del cierre documental

Q1–Q10 fijan la identidad y las referencias comunes; Q11–Q13 habilitan su uso como primer acercamiento, con revisión posterior. Se cierra la propuesta inicial con esa distinción, sin elevar los estudios provisionales a diseños finales y sin autorización para implementar la app en esta etapa.

## REF-007 — idea de menú aportada por el usuario

- Archivo: [menu.png](menu.png), captura inspeccionada el 2026-10-26.
- Rol: inspiración de composición y navegación para bosque/libro/señaladores/páginas. Fuente/autor original no informado; no se incorpora su arte como recurso de producción.
- Observado: página protagonista vertical, pestañas superiores, flechas laterales, ilustración central y bordes de hojas/tapa que sugieren profundidad.
- Estado: referencia de estructura confirmada en L1/Q1–Q3: una página protagonista vertical, portada/descripcion/Jugar y Logros con medallas/progreso diferenciados. No es un mockup final ni implica adoptar monedas, personajes, contadores o recompensas de la captura.
- Actualización 2026-10-28: síntesis L1–L3 confirmada y A/Atlas elegida como composición. Personaje es cuarto señalador e inicio, solo con ilustración. El usuario pide enriquecer artísticamente A con recursos reales: estudio ilustrado `?variant=A&art=2`, PNG maestros/prompts en `assets/source/menu/atlas-v2/` y WebP en `assets/exports/prototype-menu-v2/`. Ese acabado generado está pendiente de revisión; la captura original sigue siendo referencia y no fuente de arte. Detalles en `docs/Diseno-menu-libro-Music-Garden.md`.

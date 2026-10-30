# Music Garden — muestras para revisión

**Revisión posterior del menú:** la composición de lista de `menu-v1` se reabre por solicitud del usuario para explorar bosque/libro/señaladores. Sigue siendo antecedente aprobado y referencia de tipografía/controles; no es la composición obligatoria para el nuevo diseño. Ver `docs/Diseno-menu-libro-Music-Garden.md`. No se generó todavía una maqueta del libro.

Fecha: 2026-10-26. Estado: **v2, menú, tipografías y estados comunes aprobados; juegos/movimiento aceptados como primer acercamiento; exportaciones finales y Android pendientes**.

El usuario confirmó las bases en Q1–Q4: papel recortado con tinta, atlas con tarjetas, árbol sin rostro/polillas/«Tocar» en Defender y paleta/pareja tipográfica como base de muestras. Ver `docs/ART_DIRECTION.md` y `docs/Propuesta-identidad-visual-Music-Garden.md`.

## Lámina de material y personajes

| Archivo | Dimensiones | Origen/método | Estado |
| --- | --- | --- | --- |
| `music-garden-material-v1.png` | 1536 × 1024 px, PNG opaco | Herramienta integrada `imagegen`, generación inicial | Exploración anterior; demasiado detalle naturalista y relieve según revisión del agente. Se conserva para comparar. |
| `music-garden-material-v2.png` | 1536 × 1024 px, PNG opaco | Herramienta integrada `imagegen`, edición de v1 | Referencia aprobada por el usuario en Q5–Q7 para acabado, árbol y polilla; no es un asset de runtime. |

Los prompts exactos están en los archivos `.prompt.txt` homónimos. Para v2 se incluyó únicamente la imagen anterior como referencia de edición. Se conservaron también los originales en el directorio de generación de Codex; los archivos de este proyecto no dependen de esa ruta externa.

Autoría/origen registrado: imágenes generadas mediante IA, sin archivo de terceros aportado como referencia. No se les atribuye una licencia externa como CC0 ni se las considera arte final exportable. Los PNG son muestras rasterizadas; todavía no hay máster editable por capas de los personajes. Si se adoptan para producción, deberán normalizarse/redibujarse y registrarse los editables y las condiciones de uso correspondientes.

## Qué se revisa

Las cuatro zonas muestran rama, árbol guardián, polilla y detalle de hojas. Se revisan tinta, papel, silueta y grado de detalle. No son pantallas de la app; no contienen notación, textos, controles, muestras tipográficas reales ni comprobación exacta de tokens.

Inspección visual del agente: v2 reduce detalle naturalista y volumen, refuerza contornos y conserva paleta y sujetos sin rostros humanos. El árbol todavía necesitará una versión más simple si se usa pequeño; las venas/antenas de la polilla deben comprobarse a 32–48 dp. No se midió legibilidad en dispositivo. La textura de esta lámina nunca se trasladará sin más detrás de texto o música.

## Ronda Q5–Q7 confirmada

- Q5: el usuario prefiere el acabado de v2. Superficies funcionales lisas.
- Q6: el usuario acepta la silueta del árbol de copa ancha y tronco curvo, sin rostro. Simplificar por escala.
- Q7: el usuario acepta la polilla botánica como primera especie. Simplificar detalles por escala.

La elección de estos rasgos no autoriza integrar los PNG como assets de la app. Después corresponde revisar tipografía/composición del menú estático y sus estados antes de implementar interfaz.

## Menú estático v1: aprobado como referencia

`menu-v1.html` es un documento de diseño pasivo, generado mediante `build_menu_preview.py` desde los tokens de raíz. Q8–Q10 aprobaron composición, pareja tipográfica y estados (usuario, 2026-10-26, tres respuestas «ok»); los roles semánticos usados en esos estados se promovieron al JSON. No es un sitio publicado, no cambia la app, no tiene navegación ni conexión con perfiles/resultados. Incluye los seis módulos existentes, accesos a resultados y demostración y una hoja separada de estados (`?states=1`).

Las fuentes reales están en `fonts/`, con OFL, procedencia y hashes. No se instalaron fuentes del sistema ni bibliotecas. Los motivos SVG de cabecera y categorías son bocetos editables contenidos en el generador, pendientes de acabado final de acuerdo con la lámina aprobada. No representan partituras. Se mantiene el orden del menú actual; no se inventan resultados o desbloqueos.

### Reproducción

Desde la raíz, con Python y Node ya disponibles y Chrome instalado:

```powershell
python design/specimens/build_menu_preview.py
node design/specimens/render_menu_preview.mjs
```

El segundo comando inicia un servidor local temporal y Chrome headless con perfil aislado. Es una herramienta para exportar documentos de diseño, no una dependencia de la app. Puede configurarse otra ruta de Chrome con `MUSIC_GARDEN_CHROME`. El navegador no usa el perfil habitual del usuario; el helper no borra directorios recursivamente.

### Verificación del estudio

Capturas de página completa a 360, 411 y 600 px; capturas del primer viewport a los mismos anchos; texto al 200% a 360 px; y hoja de estados a 411 px. Datos en `menu-v1-verification.json`: Fraunces y Nunito Sans cargadas en los cinco casos; sin desbordamiento horizontal ni texto recortado por su caja. Tarjetas de al menos 104 px de alto y acciones secundarias de al menos 48 px. El texto ampliado crece en altura, oculta motivos decorativos y conserva contenido/acciones.

Se observaron las cinco capturas de página completa y el viewport de 411 px. El resto de viewports se revisó en el cierre del estudio. Inspección: jerarquía y texto legibles, superficies limpias, controles separados y colores coherentes. La textura se reserva para ilustración futura y los motivos actuales siguen esquemáticos. No hay prueba de TalkBack, tacto, barras del sistema, animación ni render Android. Los píxeles CSS de este estudio no certifican medidas dp/sp nativas. No hay notación musical en el menú o en los ejemplos de estado; no se acredita su lectura con estas capturas.

### Ronda Q8–Q10 confirmada

- Q8: composición/densidad del menú con tarjetas de una columna y desplazamiento vertical, aceptada.
- Q9: Fraunces en títulos y Nunito Sans en instrucciones/controles, aceptadas con render real.
- Q10: familia de estados normal, presionado, foco, deshabilitado, seleccionado, acierto y error, aceptada como base, con futuras verificaciones en Android.

La aprobación de esta ronda cierra la base visual del menú; su [registro de referencias](../references/music-garden/README.md) delimita qué se considera normativo.

## Juegos y movimiento v1: aceptados como primer acercamiento

El usuario aceptó Q11–Q13 el 2026-10-26 con el alcance «como un primer acercamiento está bien». Son referencias **provisionales de la primera iteración**: se conserva el enfoque y se revisarán composición, acabado, tamaños y tiempos al verlos funcionar en Android. No se consideran pantallas, assets o animaciones finales.

Estudios pasivos `defender-v1.html` y `simon-v1.html`, generados por `build_games_preview.py`. No ejecutan sesiones ni reproducen audio. Los valores de ronda, puntos, vidas y notas son ejemplos sintéticos para diseño, registrados en `games-v1-data.json`; no son desempeño ni resultados de un jugador.

- Defender: campo vertical, HUD con etapa/puntos/vidas, cuatro objetivos y controles abajo; variantes `?state=hit`, `error`, `life` y `pause`. El acierto de ejemplo retira un objetivo, suma 100 puntos y limpia la selección; el error conserva selección y vidas; la llegada muestra la solución y resta una vida, conforme al contrato existente.
- Las tarjetas usan notas estructuradas con clave y grafía. La maqueta traslada a SVG las reglas y paths de `NoteStaff`: altura diatónica, dos líneas adicionales, clave de sol/fa y sostenido/bemol. Se conservan escala uniforme y tinta del sistema. Los cuatro casos son La bemol 3 en sol, Mi bemol 4 en fa, Do sostenido 6 en sol y Do sostenido 2 en fa. No hay símbolos musicales generados por IA ni notas en imágenes.
- Simón: horizontal; capturas de escucha y respuesta; Do4–Si4 cromático visible, notas activas de la escala diatónica mayor y Mi4 resaltado solo en escucha. La proporción de teclas negras 0,62 y la posición por teclas blancas anteriores proceden del teclado existente. No se añadió una octava ni una tecla objetivo nueva. Sin decoración sobre las teclas.
- Árbol y polilla: SVG propios de estudio, simplificados a partir de la referencia de acabado aprobada. Los dibujos concretos de estos estudios y sus exportaciones no son todavía assets finales.

`motion-v1.html`, generado por `build_motion_preview.py`, es una muestra aislada: botón para repetir el acierto, mensaje inmediato, disolución/onda de 450 ms, ocho partículas y presión de 140 ms. Se puede repetir sin esperar. La casilla de reducción y el ajuste de sistema eliminan animaciones manteniendo resultado visible. No implementa gameplay ni acredita latencia audiovisual. El modo `?frame=220` congela la animación para una captura; `&reduce=1` muestra su alternativa.

### Reproducir juegos y movimiento

```powershell
python design/specimens/build_motion_preview.py
node design/specimens/render_menu_preview.mjs --games
```

El primer comando genera también los juegos; requiere solo Python estándar y los archivos/fuentes ya presentes. El segundo reutiliza Chrome/Node existentes. Sin paquetes ni dependencias nuevas.

### Verificación realizada

Doce capturas: seis de Defender a 360×800/411×891, tres de Simón a 640×360/800×360/891×411 y tres de movimiento (normal, reducción manual y reducción del sistema). En todas: fuentes cargadas, sin desborde horizontal, sin recorte de texto por sus cajas y contenido dentro del alto del viewport. Inspección visual de cada composición/estado, incluida la reducción del sistema.

En las posiciones estáticas de Defender no se intersecan los rectángulos completos de los objetivos ni salen del campo; esto no verifica las reservas de trayectoria ni futuras animaciones. Tarjetas de 94×112,8 px con escala uniforme (estudio compacto distinto de la hipótesis inicial de 96×112); lectura y tamaños físicos por validar en Android. Controles de nombre/alteración de al menos 48 px de alto. Teclas negras de Simón desde 52,42 px de ancho en el caso menor, con geometría visual sin solapamientos no intencionales. No se afirma equivalencia de píxeles CSS con dp/sp.

En la muestra de movimiento se pulsó dos veces consecutivas: el mensaje estuvo actualizado al retornar cada evento y el botón siguió habilitado. Se observaron 10 animaciones (criatura, onda y ocho partículas), frente a cero con cada ruta de reducción; no hay medición de FPS o sincronización con audio. Datos de comprobación en `games-v1-verification.json`. Sin pruebas Android, TalkBack ni medidas físicas.

### Ronda Q11–Q13: aceptación provisional registrada

- Q11: enfoque de Defender (tarjetas dominantes, criaturas pequeñas, árbol/vidas y respuesta inferior), aceptado como primer acercamiento. La legibilidad prevalece sobre la simultaneidad.
- Q12: enfoque de Simón (piano horizontal dominante, fase explícita y resaltado ocre con marca), aceptado como primer acercamiento.
- Q13: onda/polen 450 ms y presión 140 ms, sin bloquear resultado ni repetición, con alternativa reducida estática, aceptados como primer acercamiento. Valores ajustables tras la comprobación nativa.

La definición inicial queda documentada en `docs/ART_DIRECTION.md` v0.5 y en el registro de referencias, distinguiendo aprobaciones comunes de aceptación provisional. No quedan preguntas de estética necesarias para redactar esta base; las revisiones de Q11–Q13 requieren observar una futura demostración nativa, no otra aprobación abstracta. Las capturas no autorizan implementación.

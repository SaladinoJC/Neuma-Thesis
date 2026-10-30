# Prototipos descartables del menú de libro

## Versión vigente: C/Escenario refinada

El usuario confirmó C como **base definitiva** el 2026-10-28 y pidió pulir una sola portada. Abrir http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=C (también predeterminada sin parámetro). A/B permanecen como antecedentes comparables.

Orden categoría→título→ilustración→descripción→Jugar. Ilustración sin marco, aumento real 18 % a 360 px, 16,2 % a 411 y 17,9 % a 600 sobre el área pintada de C anterior; cartón irregular detrás de páginas/activa +10 px/etiquetas 13 px a 360; bosque saturación 0,86 y contraste 0,90; libro contraste 0,94/brillo 1,015 y textura reutilizada al 7 %; flechas de papel 48×48 y presión; Jugar usa la placa verde original y relieve discreto. Filtros/geometría fina son elecciones técnicas provisionales dentro del pedido, no nuevos tokens globales aprobados.

Los doce raster maestros/exportaciones simple-v3 siguen idénticos por SHA-256 (`escenario-asset-check.json`). La textura proviene del mismo libro. Flechas de dos trazos propios, sin iconoteca ni imagen nueva. Estados/foco/semántica conservados; no se afirma vibración física. Sin nuevas fuentes, bibliotecas o vegetación.

Transición entre documentos mediante [View Transitions nativas](https://developer.chrome.com/docs/web-platform/view-transitions/cross-document), con participación de ambos destinos. `book-motion.*` añade solo esa infraestructura a la maqueta anterior. Desplazamiento de contenido de 8 px, reacción de libro 0,14–0,18 grados, duración del token `cardEnterMs` (220 ms) y presión de `pressMs` (140 ms). No espera timers para navegar; otra interacción puede saltar la transición. Sin soporte del navegador, navegación directa. Reducción manual/sistema elimina movimiento y se propaga por URL, sin persistencia. Sin bucles ambientales en Simón; no afecta la excepción de Personaje documentada aparte.

Con servidor activo:

```powershell
node design/specimens/prototype-menu-book/simple-capture.mjs
node design/specimens/prototype-menu-book/escenario-capture.mjs
```

Quince vistas generales sin problemas y cinco específicas: 360×800, 411×891, 600×1000, 320×640 y texto 200 % a 360×800. Orden, tamaño pintado, altura de señaladores, etiquetas/pulsación y navegación medidos. Una transición de página y una de libro incluso al repetir rápidamente, con controles habilitados. Cero animaciones en reposo y con reducción manual/sistema. Navegación real hacia Instrumentos con `pagereveal`/transición activa comprobada en Chrome; con reducción llega al mismo juego sin transición activa y conserva la opción.

Excepción consciente: a 320 px se limita el aumento al 8 % para cuidar bordes. Scroll vertical en pantallas bajas/texto ampliado y solo interno de señaladores al ampliar texto. Comparador pasa al flujo si podría tapar controles. Capturas `escenario-*` y `escenario-verification.json`. Inspector en `escenario-inspection/` a 390/768/1440, sin hallazgos con excepciones de crema, título de portada, color de escenario, vista móvil aislada y relieve pedido (`palette-cream,app-title-size,colour-sprawl,hero-void,button-fx`). Pendientes tacto/TalkBack y medición nativa; no acredita equivalencia CSS a dp/sp.

Conservación local: `prototipo/menu-escenario-refinado-2026-10-28`. Sin Android, extensión del arte a otros juegos, push o publicaciones en issues. C confirmada como base; refinamiento disponible para revisión visual.

## Antecedente: comparación de tres libros simples 2D

El usuario descarta el acabado realista/recargado de v2 y pide tres propuestas de **una sola pantalla** antes de extender el sistema (2026-10-28). Comparación: http://127.0.0.1:8765/prototype-menu-book/simple-gallery.html · Ruta única: http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=A (también `B` y `C`). Ninguna elegida todavía.

- A/Cuento: título centrado, ilustración en ventana de papel, descripción y Jugar.
- B/Cuaderno: lectura alineada a la izquierda, descripción primero y acción lateral.
- C/Escenario: ilustración primero; título/descripción/acción cierran la página.

Solo Simón recibe nuevo arte. Los señaladores superiores son cartón físico por detrás de la página, con base oculta y activa más alta. Fondo de masas amplias, libro sin herrajes y seis assets 2D nuevos. Maestros/prompts/procedencia en `assets/source/menu/simple-v3/`, WebP en `assets/exports/prototype-menu-simple-v3/`. No llegaron nuevas imágenes: se usó la referencia guardada `menu.png` y el pedido escrito.

Ejecutar desde la raíz:

```powershell
python design/specimens/prototype-menu-book/serve.py
```

Barra flotante y flechas de teclado cambian `?variant=`; panel con estado, reducción de movimiento y texto 200 %. Sin persistencia ni animaciones ambientales. Jugar conserva la partida simulada y retorno. Índice/siguiente y otros señaladores abren sus destinos en el prototipo anterior, con su aspecto anterior; `section`/`game` permiten abrir el destino correcto. No se rediseñaron las otras secciones ni se alteró Android. Esta ruta abre Juegos solo para comparar; el inicio de producto sigue siendo Personaje.

Verificación reproducible con servidor activo:

```powershell
node design/specimens/prototype-menu-book/simple-capture.mjs
```

Quince vistas: tres variantes × 360×800, 411×891, 600×1000, 320×640 y texto 200 % a 360×800. Sin desborde horizontal, recorte de texto, controles menores de 48 px de alto, contenido fuera del papel o controles tapados por comparador. Etiquetas visibles pulsables y bases superpuestas con libro. Texto ampliado desplaza solo la fila de señaladores horizontalmente. La medición de su texto usa Range, excluyendo la base decorativa oculta tras la página.

Siete comprobaciones: retorno simulado, URL de variante, reducción manual/sistema, índice de cuatro juegos, siguiente hacia Instrumentos y apertura correcta de Personaje. Cero animaciones en reposo. Evidencia: `simple-verification.json`, capturas `simple-*`, galería e inspector 390/768/1440 en `simple-inspection/`. Inspector sin hallazgos con excepciones declaradas `palette-cream,app-title-size,colour-sprawl,hero-void` por identidad y composición de videojuego móvil. Sin nuevas fuentes/bibliotecas; Fraunces/Nunito Sans y tokens existentes.

Pendientes: elección visual y posterior prueba nativa de tacto, TalkBack y tamaños dp/sp. Conservación local: `prototipo/menu-libro-simple-v3-2026-10-28`; sin push ni publicaciones en issues. Enlace al ticket de implementación pendiente de variante validada y autorización de publicación.

## Antecedente: Atlas ilustrado v2 (acabado descartado)

El usuario eligió **A/Atlas** y pidió un salto artístico mediante ilustraciones y assets reales (2026-10-28). Versión enriquecida: http://127.0.0.1:8765/prototype-menu-book/?variant=A&art=2 · Galería: http://127.0.0.1:8765/prototype-menu-book/art-gallery.html. La dirección solicitada está confirmada; el acabado generado sigue pendiente de revisión del usuario. Las tres opciones iniciales de abajo se conservan como antecedentes.

Ocho imágenes generadas con `imagegen`: jardín, libro, atlas de controles, cuatro portadas y personaje genérico. Maestros, prompts exactos, ventanas del atlas y procedencia en `assets/source/menu/atlas-v2/`; WebP consumidos en `assets/exports/prototype-menu-v2/`. Aproximadamente 2,6 MB de exportaciones frente a 23 MB de maestros. No se dibujaron nuevas ilustraciones complejas con SVG/CSS: las ventanas SVG contienen imágenes rasterizadas. Tipografía Fraunces con tratamiento editorial/ornamentos y Nunito Sans legible, sin nuevas fuentes.

El arte se integra en `art.js`/`art.css`, activados por `art=2`, reutilizando navegación y estado del estudio inicial. Texto, etiquetas, foco y áreas táctiles siguen siendo HTML independiente del arte. La barra técnica permite regresar al boceto anterior; no pertenece al producto. No hay persistencia ni cambios Android.

Composición editable: jardín → motas → libro → contenido y portadas → señaladores/controles → borde de papel y rama frontal. No implica que los PNG tengan capas internas editables. Nueve animaciones ambientales discretas (siete motas, rama y borde de papel); microinteracción de presión y cambio breve de página, sin bloquear entradas. Reducción manual/sistema elimina animaciones; segundo plano las detiene.

### Comprobar versión ilustrada

```powershell
node design/specimens/prototype-menu-book/capture.mjs --art
```

`art-verification.json`: 21 vistas (siete destinos/portadas × 360/411/600 px) y doce adicionales (320×640, 360×800 y texto 200 % en cuatro secciones). Sin desborde horizontal, texto recortado ni controles tapados por el comparador; controles visibles de al menos 48 px en las vistas base. En pantallas bajas y texto ampliado se permite desplazamiento vertical y el comparador pasa al flujo, conservando controles accesibles. Las capturas son la evidencia del estudio, sin equivalencia acreditada a dp/sp Android.

Interacciones comprobadas: retorno desde Defender a su portada, índice, límites, reinicio en Personaje, URL; nueve animaciones ambientales normales y cero con reducción manual/sistema; tres avances rápidos llegan a Defender y dejan a lo sumo una transición de página. No se midieron FPS, latencia, batería o rendimiento físico. No hay audio ni notación que validar en estas pantallas.

Contraste del papel texturado: 14.250 muestras cada 8 px en la región de lectura del maestro. Mínimos contra los tokens: texto 9,02:1; título/verde primario 5,93:1; texto secundario 4,65:1. Método y límites en `assets/source/menu/atlas-v2/paper-contrast.json`; no incluye controles, ilustraciones ni certifica accesibilidad nativa.

Inspector adicional a 390/768/1440 en `art-inspection/`. Se eliminaron sombra del texto y sombra externa del marco. Excepciones expresas al inspector de dashboards: `palette-cream` (identidad aprobada), `app-title-size` (título decorativo solicitado), `hero-void` (Personaje solo ilustración) y `colour-sprawl` (jardín ilustrado solicitado). Informe sin hallazgos restantes con esas excepciones. Lectura, superficies y jerarquía se revisaron en capturas; falta tacto, TalkBack y validación nativa.

Conservación: rama local `prototipo/menu-libro-atlas-ilustrado-2026-10-28`. Sin push, publicaciones ni cambios en el perfil del jugador.

Pregunta: ¿qué disposición de señaladores funciona mejor en el libro móvil de Music Garden?

Tres composiciones en una ruta, sin integración Android, perfiles reales, audio ni persistencia. La síntesis funcional fue confirmada por el usuario el 2026-10-28; **ninguna variante visual fue elegida todavía**.

## Ejecutar

Desde la raíz del repositorio:

```powershell
python design/specimens/prototype-menu-book/serve.py
```

- A · Atlas: http://127.0.0.1:8765/prototype-menu-book/?variant=A
- B · Herbario: http://127.0.0.1:8765/prototype-menu-book/?variant=B
- C · Libro de campo: http://127.0.0.1:8765/prototype-menu-book/?variant=C

La barra flotante y las flechas del teclado cambian de variante conservando el estado de estudio. La URL conserva solo la variante. El panel desplegable muestra el estado completo y permite simular cierre/apertura o reducir movimiento. Es una herramienta de diseño, fuera de la distribución Android, no un control de producto.

A coloca señaladores arriba y acción después de la portada. B los distribuye en el lateral derecho y usa texto alineado a la izquierda. C los coloca abajo y adelanta la acción antes de la ilustración. Las tres conservan una sola página vertical, el mismo catálogo, principio/fin e índice.

Se puede recorrer Defender, pulsar Jugar y simular fin de partida para comprobar el retorno a su página. Afinador y Más muestran destinos simulados; entrenamiento de oído aparece como futuro. Logros usa estados vacíos explícitos: no inventa medallas, reglas ni resultados del perfil.

## Materiales y límites

- Colores, tamaño táctil, radio y duración se leen de `design-tokens.json` mediante el servidor local; CSS conserva los mismos colores como respaldo de primer pintado.
- Fraunces y Nunito Sans se cargan desde `../fonts/`, con sus licencias y procedencia existentes.
- Bosque, personaje y cuatro portadas: SVG propios editables en `app.js`. Arte de estudio, sin reutilizar la captura aportada ni generar notación musical. No son assets finales; la apariencia del personaje no está aprobada.
- Estados únicamente en memoria. El retorno desde segundo plano se modela conservando el documento; recargar reinicia en Personaje. No acredita el ciclo de vida Android.
- Sin nuevas bibliotecas, motores o frameworks. Los tamaños CSS no certifican dp/sp, tacto o TalkBack.

## Verificación

Con el servidor activo:

```powershell
node design/specimens/prototype-menu-book/capture.mjs
```

`verification.json`: 36 vistas (tres variantes × cuatro secciones × 360/411/600 px), fuentes y tokens cargados, sin desborde horizontal ni texto recortado; controles visibles de al menos 48 px y sin solapamiento con el comparador. Capturas por variante/sección a 411 × 891. Interacciones comprobadas: retorno de partida, índice/límite inicial, reducción de movimiento, reinicio y URL de variante.

Inspección adicional con `design-taste-frontend` a 390/768/1440 px en `inspection/`. Se corrigieron el contraste del nombre sobre el bosque, el marcador `data-ui` innecesario y la tercera fuente del panel técnico. Excepciones deliberadas al inspector de dashboards: crema por identidad aprobada; capas sólidas del libro para representar tapa/hojas; título de 30 px y personaje aislado por el diseño solicitado. Se autorizaron únicamente esas categorías (`heavy-shadow,palette-cream,app-title-size,hero-void`); informe final sin hallazgos restantes.

La dirección sorteada fue Editorial greeting, semilla 85973517, conservando paleta y tipo. Su grilla de dashboard y presupuesto de color no se adoptan porque contradicen la composición de libro/bosque confirmada. La identidad del proyecto prevalece: libro como estructura de navegación y arte botánico como ambiente. No hay indicadores numéricos de desempeño ni datos sintéticos de partidas.

Pendiente: elección o combinación por el usuario, texto ampliado, prueba de tacto/ayudas técnicas y futura verificación nativa. No se trasladó ninguna variante al código real.

## Conservación

Rama local de estudio: `prototipo/menu-libro-visual-2026-10-28`. Se conserva una instantánea del prototipo y sus recursos necesarios sin incorporar los cambios ajenos del directorio de trabajo. Al elegir una variante, registrar el veredicto y el enlace de contexto en el ticket de implementación correspondiente; no se publicaron issues/comentarios ni se hizo push en esta sesión.

# Renderizador de partituras de Glisso Rhythm

**Actualización posterior, 2026-10-29:** el usuario autorizó implementar estos comportamientos e intentar VexFlow; después pidió retomarlos tras un apagado. La investigación siguiente conserva el alcance y las conclusiones de su momento. Implementación y verificación propias: [lectura lateral](Rhythm-Tap-lectura-lateral.md). No se reutiliza código de Glisso.

Inspección: 2026-10-29. Pedido del usuario: investigar el desplazamiento lateral, el fondo integrado y el feedback verde por nota de [Glisso Rhythm](https://glisso.net/rhythm). **Sin implementación ni cambios de prioridades del prototipo.** Complementa la [comparación visual anterior](Comparacion-Glisso-Rhythm-Tap.md), que no había identificado el motor.

## Fuentes y método

Se inspeccionaron los recursos que sirve el sitio y el DOM en Chrome headless, como invitado, a 1280 × 900. Se omitió la calibración inicial y se inició una ejecución en modo Tap. Un arnés local registró posiciones y colores e inyectó eventos de teclado al acercarse las figuras al marcador. Estos taps artificiales permiten observar el feedback; no miden precisión humana, latencia física ni eficacia pedagógica.

Fuentes primarias de esta versión desplegada:

- [Página Rhythm](https://glisso.net/rhythm).
- [JavaScript de la aplicación: index-Vk630zsi.js](https://glisso.net/assets/index-Vk630zsi.js).
- [CSS de la aplicación: index-DJmU7ypu.css](https://glisso.net/assets/index-DJmU7ypu.css).

Los nombres con hash y símbolos minificados pueden cambiar al publicar otra versión. No se accedió al repositorio privado ni se obtuvo una licencia para reutilizar código de Glisso. La inspección HTTP directa devolvió 403; el navegador pudo cargar los recursos públicos.

Capturas: [previa](imagenes/glisso-renderizador/01-previa.png) y [ejecución](imagenes/glisso-renderizador/02-ejecucion.png). Son evidencia de un sitio de terceros; la autoría de su interfaz corresponde a Glisso. El arnés, las respuestas descargadas, el DOM y las muestras quedan como evidencia local reproducible en `Plataforma/build/verification/2026-10-29-glisso-renderer/`.

## Qué motor usa

**VexFlow con backend SVG y las fuentes musicales Bravura/Academico.** No se deduce solamente de los nombres: el bundle configura esas fuentes y crea el renderer con `Backends.SVG`; el DOM de Rhythm contiene el SVG generado y grupos `.vf-notehead`. El Canvas presente en la página no es el soporte de esta partitura. No se determinó la versión exacta de VexFlow. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js).

VexFlow resuelve la notación; una capa propia organiza la línea musical, el seguimiento temporal, el montaje por sectores y los resultados. Se reconocen objetos de notas, plicas, barras de agrupación, tresillos y ligaduras. No es una imagen de partitura desplazada. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js).

## Cómo logra el scroll continuo

La partitura ocupa una línea horizontal más ancha que el área visible. El contenedor `.music-scroller` recorta el contenido con `overflow: hidden`; la barra de posición actual es otro elemento, separado de esa línea. [Fuente: CSS desplegado](https://glisso.net/assets/index-DJmU7ypu.css).

En cada actualización de Rhythm, el código avanza el tiempo musical, evalúa las entradas y mueve la partitura. El reloj consultado termina en `AudioContext.currentTime`; convierte segundos transcurridos a pulsos según tempo y contempla mapas de tempo. El movimiento visual sigue ese tiempo, en lugar de determinar la evaluación. Esto describe el código, sin acreditar sincronía acústica física. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js), métodos `update`, `_advanceClock` y funciones `bE`/`ZT` de este bundle.

El método `_beatToX` interpola entre anclas de pulso y posición gráfica de las notas. `_scrollTo` aplica la coordenada resultante a **`scrollLeft`**, con redondeo a píxeles y un margen previo. Por eso el desplazamiento sigue el recorrido musical entre notas; no consiste en saltar de compás ni en una transición CSS independiente del tempo. El marcador conserva su posición durante la ejecución a tamaño de ventana constante. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js).

Además, `_ensureChunksNear` dibuja sectores cercanos a la ventana y retira sectores suficientemente antiguos. Al volver a dibujar recupera los colores desde los resultados guardados. Esto limita el contenido SVG montado en pasajes extensos; no significa que toda la partitura se redibuje en cada frame. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js), métodos `_drawChunk`, `_undrawChunk` y `_reapplyJudgeColors`.

Comprobación de una ejecución: 105 muestras con estado activo, marcador constante en x = 558,421875 píxeles CSS y `scrollLeft` desde 478 hasta 2079. Son muestras aproximadamente cada 100 ms, no un benchmark de FPS ni una medición de suavidad en teléfono.

## Por qué no aparece un rectángulo detrás de la partitura

El SVG y los contenedores de notación tienen **fondo transparente**. Se ve el fondo de la aplicación, definido mediante variables de tema como `--color-bg`. No hace falta pintar otro fondo idéntico encima. El DOM comprobado informa `rgba(0, 0, 0, 0)` para el contenedor de notación, la pantalla Rhythm y el scroller. [Fuente: CSS desplegado](https://glisso.net/assets/index-DJmU7ypu.css) y estilos computados de [Rhythm](https://glisso.net/rhythm).

## Cómo marca los aciertos

El evaluador de Rhythm emite un resultado asociado a un índice de nota. `_showVerdict` actualiza ese registro y llama al coloreado; el acierto se traduce al estado interno `taught`, heredado del componente común de música. `_colorNote` cambia el `fill` de los glifos de la **cabeza de la nota**, conservando plicas y agrupaciones. Para aciertos usa `#4caf50`; para resultados fallidos usa `#ec7d7d`. Las continuaciones ligadas también se contemplan. No hay que sustituir la figura por otra imagen. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js).

En la ejecución automatizada hubo 32 eventos de tap y 28 cabezas verdes al terminar el intervalo observado. La primera cabeza verde apareció alrededor de los 3,06 s desde el inicio del registro, incluido el conteo. No se completó ni calificó una partida humana. La captura también muestra marcas de entradas extra sobre silencios, provocadas por el arnés: no deben interpretarse como una evaluación de usuario.

La capa visual también contempla destello del marcador y avisos Early/Late configurables. El modo de foco puede posponer el coloreado hasta terminar; no todas las configuraciones muestran feedback inmediato. [Fuente: JavaScript desplegado](https://glisso.net/assets/index-Vk630zsi.js).

## Qué sería trasladable a nuestra app, si se aprueba

La referencia expresada por el usuario queda registrada: línea continua con marcador estable, fondo integrado y resultado sobre la nota que acaba de pasar. **No constituye autorización para implementarlo todavía.**

Para Android/Compose serían comportamientos a diseñar sobre el motor actual: coordenadas musicales separadas del reloj de evaluación, desplazamiento de la ventana de lectura, fondo transparente sobre el papel de Music Garden y estado visual por ataque. Usar VexFlow mediante una capa web o cambiar de renderer es una decisión distinta que esta inspección no resuelve. Habría que evaluar calidad de grabado, densidad, rendimiento, reducción de movimiento y accesibilidad antes de elegir una solución.

No se modificaron Kotlin, dependencias, APK, reglas de puntuación ni assets de la aplicación. Las únicas imágenes añadidas son capturas de investigación.

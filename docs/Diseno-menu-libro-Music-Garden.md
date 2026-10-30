# Music Garden — menú del libro

Estado vigente: **C/Escenario base definitiva confirmada; refinamiento de Simón preparado; plan de Personaje confirmado e implementación pendiente por pedido del usuario**. Actualizado: 2026-10-28.

**Adaptación Android disponible:** menú nativo v0.12.5 con cuatro portadas, Personaje estático, índice, Afinador/Logros y Más. Verificación final 46/46 Android en API 34 y revisión parcial de foco con TalkBack. [APK, evidencia y límites](Verificacion-menu-android-v0.12.5.md). Las menciones posteriores «sin Android» corresponden a las etapas históricas de maqueta; Personaje P0–P7 sigue pendiente.

**Personaje, plan confirmado e implementación pendiente (usuario, 2026-10-28):** confirma síntesis y orden P0–P7 y pide dejarlo pendiente sin implementar. Entrevista cerrada; no iniciar prototipos, assets ni código del personaje hasta que solicite retomar. Esta decisión supera las menciones históricas de entrevista en curso o confirmación pendiente. Ver [plan](Plan-personaje-Music-Garden.md).

**Personaje, definición Q1–Q24 completada:** elección de familia antes de aspecto, cambio gratuito posterior conservando hitos y modelos equivalentes, catálogo de quince modelos y seis premios de ropa/accesorios. Escena de piano adaptada y melodía con timbre por tipo, compartido entre acabados equivalentes. Quedan confirmados rasgos, compatibilidad, armario, movimiento, hitos de prueba y entrega; las preguntas pendientes de las rondas anteriores que siguen debajo son antecedentes resueltos. [Plan consolidado P0–P7](Plan-personaje-Music-Garden.md) listo para confirmar entendimiento compartido, sin código ni assets nuevos. El contenido funcional amplía la ilustración genérica anterior sin cambiar los cuatro señaladores ni el inicio en Personaje.

**Personaje, ronda 4 confirmada:** se elige Guitarra/Piano/Trompeta/Flauta/Violín al crear el personaje y se parte de un modelo genérico/desgastado. Los dos premios instrumentales pasan a ser modelos de la familia elegida; se conservan los otros seis objetos. Hitos aceptados para probar; obtención automática con aviso al terminar partida, marca Nuevo y sin equipar automáticamente. Melodía breve común aceptada. Ronda 5 pendiente sobre cambio de familia, timbres/modelos, representación del piano y omitir apariencia conservando elección musical. Ver [plan](Plan-personaje-Music-Garden.md); sin cambios en las maquetas.

**Personaje, ronda 3 confirmada:** rasgos/prendas compatibles con ambos cuerpos y conservación de objetos al cambiar; accesorios libres con incompatibilidades por ubicación. Nombre, personaje animado y Personalizar abren edición/armario, con hitos y progreso de objetos bloqueados. Se agrega Hacerlo sonar para una melodía breve del instrumento equipado. Primera colección: ocho recompensas (práctica/desempeño por juego), además de opciones gratuitas. Catálogo, hitos concretos y detalle de reproducción pendientes; no se implementa ni cambia la maqueta con esta entrevista. Ver [plan](Plan-personaje-Music-Garden.md).

**Personaje, ronda 2 confirmada:** creación con tono de piel, peinado/color, nombre, vestimenta inicial, tipos de cuerpo sin etiquetas de género y vello facial. Equipamiento: cabeza, torso, piernas, instrumento y dos accesorios (joyas, relojes, aros, etc.). Práctica manual elegible, mínimos de actividad por juego y concesión retroactiva verificable aceptados. Ronda 3 pendiente: compatibilidad entre cuerpos/accesorios, función del instrumento, acceso al armario y primera colección. No modifica la maqueta C/Escenario ni implica implementación de estas funciones. Ver [plan](Plan-personaje-Music-Garden.md).

## C/Escenario — base elegida y refinamiento

El usuario elige C como base definitiva y pide ajustar proporciones/jerarquía conservando estética 2D natural y ligeramente low-poly. Orden: categoría pequeña, título, ilustración, descripción y Jugar. Solo una pantalla de referencia, mismos comportamientos, sin dependencias ni sustitución de assets.

Implementado en `simple.html?variant=C`: ilustración original sin marco, aumento medido 16,2–18 % a 360/411/600; cartón con borde irregular/activa +10 px y etiquetas legibles a 360; bosque suavizado, papel reutilizado y sombras laterales menos contrastadas; Jugar verde con relieve/presión y nuevas flechas de papel 48×48. Los doce raster originales conservan hash. Excepción a 320: aumento limitado a 8 % para cuidar bordes.

Transición breve de contenido/reacción del libro hacia destinos existentes, sin temporizador que retenga la navegación; reducción manual/sistema conservada al llegar. Sin bucles ambientales en esta portada, preservando la excepción de Personaje definida aparte. Otros juegos/secciones siguen en la maqueta anterior sin nuevo arte. La ruta abre Simón para revisar; el inicio de producto sigue siendo Personaje.

Reproducción/evidencia en README del prototipo y `escenario-verification.json`. C está confirmada; los valores precisos de presentación son elecciones técnicas del agente disponibles para revisión. Esto no implementa Android ni cierra la entrevista o el alcance del Personaje.

## Ampliación de Personaje — entrevista en curso

El usuario abre la etapa antes reservada de creación y progresión estética. Ronda 1 confirmada el 2026-10-28: humano explorador 2D personalizable, primera creación omitible y edición posterior gratuita; cada hito entrega un accesorio fijo, con accesorios propios de cada juego y tienda diferida. Respiración/balanceo continuos mientras esté visible y gestos al entrar, tocarlo o cambiar ropa, respetando reducción de movimiento. Esto amplía el contenido antes limitado a una ilustración genérica y exceptúa al personaje de la regla sin bucles del menú; no cambia la navegación acordada ni las maquetas existentes. Campos, hitos, controles y alcance de implementación siguen pendientes en [la entrevista del personaje](Plan-personaje-Music-Garden.md).

## Simplificación 2D v3 — alcance vigente

El usuario descarta el acabado realista/recargado y solicita tres prototipos de una sola pantalla antes de extender: libro central simple, grosor/papel discreto, señaladores físicos detrás de páginas, activa más alta, fondo de masas amplias y controles sin ornamentos. Sin bucles ambientales. Se preservan las decisiones funcionales L1–L3 y señaladores superiores; se revisa el acabado artístico.

Preparadas tres portadas de Simón: A/Cuento (centrada y enmarcada), B/Cuaderno (lectura editorial/acción lateral), C/Escenario (ilustración primero). Ruta `simple.html?variant=A|B|C`, galería `simple-gallery.html` en el prototipo existente. Ninguna elegida. Seis assets 2D nuevos con maestros/prompts y exportaciones separadas; guía y límites en su README. No llegaron nuevas imágenes adjuntas; se usó la referencia guardada `menu.png` y la descripción, sin afirmar correspondencia con una segunda imagen nueva.

Solo esta portada está refinada. Los otros destinos permanecen en la maqueta funcional anterior, accesibles por enlaces correctos; no se reinterpretan sus datos o reglas. Sin Android ni nuevas dependencias. Próximo paso: elección/combinación del usuario y recién después extender el sistema.

## Atlas ilustrado — selección y evolución (2026-10-28)

El usuario elige A y pide alejar el menú de la apariencia de app web básica mediante arte real: jardín musical mágico con profundidad, papel texturado y volumen, títulos con personalidad/ornamentos, señaladores/flechas/Jugar ilustrados, cuatro portadas protagonistas y movimiento ambiental. Mantener las decisiones funcionales previas.

Preparado en la misma ruta con `?variant=A&art=2`, sin reemplazar destructivamente el boceto anterior. Ocho PNG maestros generados con la herramienta integrada `imagegen` en `assets/source/menu/atlas-v2/`, prompts exactos/procedencia/inventario y ocho WebP consumidos desde `assets/exports/prototype-menu-v2/`. La composición es editable y los textos independientes; los PNG no tienen capas internas editables. Libro y atlas son reutilizables y sustituibles. Arte generado pendiente de aprobación del usuario, personaje aún genérico, sin reglas nuevas de progresión.

Ver [ejecución y comprobaciones](../design/specimens/prototype-menu-book/README.md): siete vistas por tres anchos y doce adicionales de pantalla baja/texto 200 %, cinco comprobaciones de navegación y reducción de movimiento manual/sistema. Sin cambios Android, audio, dependencias ni datos del perfil. Próximo paso: revisión visual del usuario, ajustes concretos y posterior encargo de integración nativa cuando lo solicite.

## Prototipos visuales (2026-10-28)

El usuario confirmó expresamente la síntesis L1–L3 y pidió prototipos con `prototype`. Estudios interactivos descartables en [prototype-menu-book](../design/specimens/prototype-menu-book/README.md): A/Atlas con señaladores arriba; B/Herbario con señaladores laterales; C/Libro de campo con señaladores abajo y acción antes de la portada. Mantienen el alcance confirmado. El personaje y las ilustraciones son SVG propios provisionales; no fijan su apariencia final. Pendiente elección/combinación del usuario. Sin implementación Android autorizada.

## Confirmaciones L3 (usuario, 2026-10-28)

- **Q8:** Personaje muestra únicamente la ilustración genérica. Se entra a los juegos mediante el señalador Juegos ya existente, sin botón adicional ni quinto señalador. Creación, personalización y progresión estética siguen reservadas para después.
- **Q9:** el indicador abre un índice para elegir directamente otro juego.
- **Q10:** cambiar de aplicación o bloquear el teléfono conserva la ubicación al volver. Cerrar y abrir de nuevo inicia en Personaje; una apertura tras terminar el proceso también. Esta política afecta navegación, no los datos de progreso.
- **Q11:** cambio de hoja breve y silencioso, sin bloquear controles; cambio inmediato con reducción de movimiento. Pulsaciones rápidas actualizan el destino sin acumular animaciones; cambio de sección discreto.

La frontera de decisiones generales del menú queda resuelta. Próximo paso: confirmar la síntesis compartida y preparar maqueta móvil, con orden/disposición y proporciones a revisar visualmente. Los contenidos futuros o vacíos se presentarán como estados de estudio, sin inventar logros del jugador. No hay implementación Android autorizada.

## Decisiones vigentes del 2026-10-28

Esta actualización sustituye las propuestas y preguntas L2 que se conservan debajo como antecedentes:

- **Q4:** Medallas / Progreso como selector interno, varias medallas por página y una ficha por juego en Progreso. Aceptado «por ahora»; reglas de medallas pendientes.
- **Q5:** recordar página al cambiar de sección y volver de una partida durante el uso. No recordar ubicación tras cerrar y abrir la app. Falta precisar el retorno desde segundo plano.
- **Q6:** principio y fin, con indicador. El índice al pulsarlo no fue confirmado expresamente.
- **Q7:** Simón → Instrumentos → Rhythm Tap → Defender; Más fuera del libro para Resultados, Prueba de voz y Datos y demostración.
- **Personaje:** cuarto señalador y sección inicial al abrir la app, sustituyendo Juegos como inicio. Por ahora tendrá un personaje genérico, todavía no producido. Orden y disposición de señaladores pendientes de maqueta.
- **Para después:** progresión estética del personaje según desbloqueos, creación inicial, personalización y reglas de recompensas. Es una idea reservada; no inventar condiciones ni estadísticas.

### Antecedente: preguntas L3 (respondidas arriba)

**Q8 — Página de Personaje:** ¿ilustración genérica protagonista y botón «Ir a Juegos», dejando creación y personalización para después?

Recomendación: sí; el botón lleva a la última página de Juegos durante el uso y a Simón después de una apertura nueva. Sin estadísticas ni recompensas ficticias.

**Q9 — Índice:** ¿pulsar «2 de 4» abre un índice para elegir directamente otro juego?

Recomendación: sí, con nombres de juegos y selección actual visible.

**Q10 — Segundo plano y cierre:** ¿cambiar de app o bloquear el teléfono conserva la página al volver, mientras cerrar y abrir de nuevo inicia en Personaje?

Recomendación: sí. Una apertura nueva después de terminar el proceso también inicia en Personaje. Esta política afecta solo la navegación, no el progreso guardado del jugador.

**Q11 — Movimiento:** ¿cambio de hoja breve y silencioso entre páginas, sin bloquear controles y con cambio inmediato al reducir movimiento?

Recomendación: sí; las pulsaciones rápidas actualizan el destino sin acumular una cola de animaciones. Los señaladores cambian de sección con una transición discreta.

Las respuestas L3 vigentes están registradas arriba; las recomendaciones siguientes no prevalecen sobre ellas. Sin implementación Android autorizada.

## Antecedente: pausa del 2026-10-26 (L2 respondida el 2026-10-28)

El usuario pidió guardar las preguntas porque debía retirarse (2026-10-26). **Q4–Q7 siguen sin responder**: no interpretar las recomendaciones como aceptadas ni avanzar con la maqueta antes de recibir las respuestas. L1/Q1–Q3 permanece confirmado.

Preguntas exactas pendientes de la ronda L2:

**Q4 — Organización de Logros:** ¿separamos dentro de esa sección Medallas y Progreso?

Recomendación: un selector pequeño «Medallas / Progreso». Medallas mostraría varias piezas por página; Progreso, una ficha por juego. Seguimos teniendo únicamente tres señaladores principales.

**Q5 — Recordar la página:** ¿querés que cada sección recuerde dónde estabas, incluso al cerrar y volver a abrir la app?

Recomendación: que sí. Al salir de Defender volvés a su portada; cambiar a Afinador conserva tu ubicación en Juegos. Al abrir la app, entrás en Juegos, en su última página visitada.

**Q6 — Principio, fin y acceso rápido:** ¿preferís que el libro tenga principio y fin, o que desde la última página se vuelva circularmente a la primera?

Recomendación: principio y fin, deshabilitando la flecha correspondiente. Un indicador como «2 de 4» permitiría abrir un índice para saltar directamente a otro juego.

**Q7 — Contenido y accesos secundarios:** ¿dejamos inicialmente en Juegos este orden: Simón → Instrumentos → Rhythm Tap → Defender, y ubicamos Resultados, Prueba de voz y Datos y demostración en un botón discreto «Más» fuera del libro?

Recomendación: esa distribución, para conservar los accesos actuales sin sumar señaladores.

Próximo paso: retomar estas cuatro preguntas, sin repetir L1 ni reabrir paleta/materiales/tipografías. Después de las respuestas, preparar la primera maqueta del libro y sus tres secciones. No hay implementación Android autorizada.

## Origen y alcance

El usuario propone revisar el menú principal: fondo de bosque, libro central y tres señaladores, **Juegos**, **Afinador** y **Logros**. Juegos es la sección inicial; muestra un minijuego por página con flechas izquierda/derecha y movimiento de hojas. Logros también se pagina. Afinador ofrece acceso a libre por instrumento, afinación guiada y ejercicio de afinar de oído.

Referencia aportada: [menu.png](../design/references/music-garden/menu.png). Es inspiración para composición e interacción, no un asset de producción ni una aprobación de sus monedas, contadores, personaje o estilo completo. No se extrae su arte para usarlo en la app. El archivo fue inspeccionado: página vertical protagonista, señaladores superiores, ilustración central, flechas laterales y profundidad por bordes de hojas/tapa. No muestra por sí solo una animación.

Esta solicitud reemplaza como objetivo **la composición de lista** aprobada en Q8. Se conservan dirección Music Garden, paleta, tipografías, materiales y estados comunes. La lista anterior queda como referencia histórica de controles/tipo. L1 confirma la estructura del libro, sin aprobar todavía una maqueta concreta, implementación Android, dependencias o nuevas condiciones de medallas.

### Confirmaciones de L1 (usuario)

- Q1 «como en la captura, como decís vos»: una página protagonista en vertical, con tapa y hojas detrás para reconocer el libro.
- Q2 «portada + descripción breve + jugar»: presentación del juego con ilustración, nombre, una explicación breve y Jugar; preparación dentro del juego.
- Q3 «ambos, claramente diferenciados»: Logros reúne medallas por objetivos y progreso/desbloqueos como contenidos distintos. No hay aún reglas de obtención ni sistema de medallas implementado.

## Requisitos expresados por el usuario

- Bosque como entorno y libro como centro de navegación.
- Señaladores Juegos, Afinador y Logros; Juegos por defecto.
- Un minijuego por página en Juegos.
- Flechas visibles para avanzar y retroceder, con efecto de cambio de hoja.
- Páginas navegables en Logros.
- Página de Afinador con tres elecciones de experiencia.

Esas tres decisiones ya están resueltas. Quedan por definir navegación detallada, organización interna de Logros, accesos auxiliares y comportamiento de hojas. Los detalles no atribuidos a una confirmación siguen siendo recomendaciones del agente.

## Composición acordada y detalle propuesto

Menú vertical y una página protagonista visible, con tapa y hojas detrás: acordado en L1/Q1. Se conserva la orientación actual y no se divide el contenido en dos páginas simultáneas pequeñas.

El libro debería dominar el área útil; el bosque se concentra en márgenes y fondo, con contraste menor que el contenido. Fondo ilustrado diurno/cálido y acabado de papel recortado con tinta según la referencia Music Garden aprobada. Mantener texto real sobre la superficie limpia de la página, no dibujarlo dentro del asset.

La página de juego acordada en L1/Q2 contiene ilustración propia, nombre, una frase que explique la actividad y botón **Jugar**. Conserva las pantallas existentes de preparación para seleccionar escala, modalidad o dificultad. Se propone que las flechas pertenezcan al marco estable y que el contenido cambie; un indicador «2 de 4» puede mostrar ubicación sin crear puntuación o progreso ficticio. Límites, índice y memoria de página se revisan en L2.

La navegación debe funcionar por pulsación, sin exigir arrastrar o hacer gestos precisos. Un gesto de deslizar podría complementarla después. Se recomienda movimiento 2D de hojas, breve y legible, con alternativa reducida; volumen, duración, interrupción y comportamiento ante pulsaciones rápidas se definirán cuando se elija la geometría. La captura no obliga a introducir 3D, motor de juegos o biblioteca de animación.

## Correspondencia con el estado real

Hechos contrastados con código y documentación en una exploración de solo lectura:

- Libre y Guiado están implementados como modos del mismo afinador (`TunerMode.FREE/GUIDED`).
- Los perfiles actuales son guitarras de seis cuerdas, presets y afinaciones personalizadas. No existe todavía un catálogo genérico de instrumentos afinables. Piano/guitarra/tono puro son timbres de referencia, no un catálogo de instrumentos objetivo.
- Entrenamiento de oído está especificado en A5/#13, pendiente de implementación. El diseño puede incluir el destino previsto, sin presentarlo como una función operativa actual.
- No hay un sistema implementado de medallas/insignias. Sí existen progreso y desbloqueos de Simón e Instrumentos; no son sinónimos automáticos de una colección de logros.
- Resultados es un módulo aparte, con historial de Simón, Instrumentos y voz. Defender/Ritmo conservan resúmenes en memoria; historial de afinador está pendiente.
- Además de cuatro minijuegos, el menú tiene prueba técnica de voz, afinador, resultados y Datos y demostración. El laboratorio YIN se abre desde Datos y demostración. La nueva navegación debe ubicar estos accesos sin hacerlos desaparecer por omisión.

Fuentes: `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/SimonApp.kt`, `MainActivity.kt`, `ui/HistoryApp.kt`, `ui/TunerApp.kt`, `ui/GuidedTunerPanel.kt`, `tuner/TunerConfiguration.kt`, `progress/Progression.kt`, `docs/Especificacion-afinador-v1.md`, `docs/Plan-tareas-afinador-v1.md` y estado del prototipo. Sin builds ni nuevas pruebas de hardware.

## Assets y responsabilidades

No es necesario que el usuario consiga assets antes de cerrar composición y tamaños. Primero se prepara una maqueta a escala móvil; luego un encargo modular. Puede producirse arte propio con las herramientas disponibles o utilizar ese mismo encargo con un ilustrador. No se presupone contratación ni compra.

| Pieza | Propuesta de producción | Restricciones |
| --- | --- | --- |
| Bosque | Fondo ilustrado WebP, eventualmente en dos planos | Centro de lectura despejado, recorte adaptable y sin textura sobre texto. |
| Libro | Editable por capas: tapa, bloque de hojas, página, lomo y sombra corta | Una página protagonista con volumen sugerido, confirmada en L1; proporciones exactas y tratamiento de vuelta de hoja pendientes. Sin texto, botones ni señaladores funcionales incrustados. |
| Señaladores y flechas | Controles nativos con tratamiento gráfico común | Etiquetas reales, foco/selección y área táctil; no requerir imágenes distintas por cada palabra. |
| Portadas de juegos | Ilustraciones independientes coherentes con árbol/polilla/papel/tinta aprobados | Composición y proporción a fijar después de la maqueta; sin notación musical generada. |
| Logros | Iconos de medalla y recursos de progreso diferenciados | Ambos contenidos confirmados; faltan organización, condiciones de obtención y estilo de las medallas. No presentar desbloqueos como medallas ya obtenidas. |
| Cambio de hoja | Transformaciones/recorte nativos sobre capas del libro | No producir sprites o adoptar runtimes nuevos sin necesidad demostrada. |

Cada encargo final deberá registrar dimensiones, zona segura, transparencia, capas, estados, editable, exportación y procedencia/licencia en el manifiesto. Los nombres anteriores son roles de producción, no archivos que ya existan.

## Árbol de decisiones

### L1 resuelta

1. **Geometría confirmada:** una página protagonista en vertical con tapa y hojas detrás.
2. **Página de juego confirmada:** portada + descripción breve + Jugar, con preparación dentro del juego.
3. **Logros confirmado:** medallas y progreso/desbloqueos diferenciados. El diseño contempla ambos; no se aprueban aún reglas ni implementación de medallas.

### Frontera L2, recomendaciones pendientes

- **Q4, Logros:** dos apartados internos, Medallas y Progreso, sin sumar otro señalador principal. Medallas como colección de varias piezas por página; Progreso con una ficha por juego. La cantidad exacta de medallas visibles se valida en maqueta, no se fija antes de medir.
- **Q5, memoria:** recordar la página de cada sección al cambiar señalador y al volver de un juego. Conservar esa ubicación entre aperturas; el arranque sigue siendo Juegos por indicación inicial del usuario. Ejemplo: salir desde Defender vuelve a Defender; abrir Afinador no cambia la página guardada de Juegos.
- **Q6, límites e índice:** libro con principio y fin, flecha anterior/siguiente deshabilitada en su límite y contador de página. Pulsar el contador abre un índice sencillo para elegir destino directamente. No navegación circular por defecto.
- **Q7, accesos existentes:** cuatro páginas iniciales de Juegos en el orden Simón, Instrumentos, Rhythm Tap y Defender. Botón discreto Más fuera del libro para Resultados, Prueba de voz y Datos y demostración (que conserva acceso al laboratorio). Los tres señaladores principales permanecen como se solicitaron.

Estas cuatro preguntas pueden resolverse ahora sin depender entre sí. No se registran como confirmadas hasta recibir respuesta.

### Después de L2

Con la navegación acordada, preparar la maqueta de las secciones y fijar proporciones/posición de señaladores, bosque y libro sobre esa evidencia. Definir el alcance del cambio de hoja y los estados de contenido futuro/vacío. Las reglas de medallas son una rama funcional separada que no debe bloquear indefinidamente el diseño del menú: pueden mostrarse como ejemplos rotulados de estudio, sin atribuirlas al perfil real. No hay implementación Android autorizada.

Se preserva la intención de tres entradas de Afinador expresada por el usuario; la distinción entre módulos reales y futuros se resolverá al fijar la demostración. No volver a preguntar si quiere esas tres opciones.

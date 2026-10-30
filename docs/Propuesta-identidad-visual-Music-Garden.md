# Music Garden — propuesta de identidad visual

Fecha: 2026-10-26. Estado: **propuesta inicial revisada; bases comunes confirmadas y juegos/movimiento aceptados como primer acercamiento**.

## Estado y alcance

**Revisión posterior:** el usuario propone reemplazar la composición de lista por bosque y libro con señaladores Juegos/Afinador/Logros. Ver [menú de libro en definición](Diseno-menu-libro-Music-Garden.md). Las aprobaciones del menú registradas abajo son antecedentes; paleta, tipografía y estados siguen vigentes, pero no debe imponerse la lista frente a esta solicitud más reciente.

El usuario adoptó Music Garden como nombre provisional de la dirección artística: naturaleza, cuento ilustrado, papel artesanal, botánica y fantasía suave, con identidad reconocible y escenarios propios por minijuego. No es una confirmación del nombre comercial de la plataforma.

El usuario confirmó las cuatro bases de la primera ronda: Q1 «dale, papel recortado con tinta»; Q2 «atlas con tarjetas»; Q3 «dale» al árbol sin rostro, polillas de alas vegetales y acción «Tocar»; Q4 «dale» a marfil/verdes/ocre y Fraunces + Nunito Sans como base de las muestras. Se registran también en `ART_DIRECTION.md`. La aprobación del concepto no aprueba una ilustración concreta ni acredita los tamaños, estados, duraciones o legibilidad propuestos.

En Q5–Q7 el usuario aprobó el acabado y las siluetas de árbol/polilla de la lámina v2. Ver [registro de referencia](../design/references/music-garden/README.md). Las exportaciones pequeñas y sus estados siguen pendientes.

En Q8–Q10 (tres respuestas «ok») el usuario aprobó composición del menú, tipografías renderizadas y familia de estados. Sus colores semánticos se promovieron a `design-tokens.json`. Las métricas de juego, movimiento y nuevas ilustraciones continúan sujetas a revisión; no se autoriza implementar la interfaz. Los estudios de menú, Defender, Simón y movimiento usan fuentes reales únicamente bajo `design/`, sin dependencias nuevas ni cambios Android. Ver [muestras y prompts](../design/specimens/README.md).

En Q11–Q13 el usuario aceptó Defender, Simón y movimiento **como un primer acercamiento**. Esa aceptación es provisional, no aprobación final: revisar otra vez al verlos funcionar en Android. La base documental inicial se consolida en `ART_DIRECTION.md` v0.5; este documento conserva alternativas, hipótesis y trazabilidad y no es una segunda fuente normativa. No se necesita otra ronda abstracta para describir la identidad; la siguiente revisión de esos tres estudios debe basarse en una demostración concreta cuando se solicite implementación.

Fuentes internas: `AGENTS.md`, `Plataforma/AGENTS.md`, `.agents/estado-prototipo.md`, `CONTEXT.md`, `docs/adr/0001-plataforma-android.md`, `docs/adr/0002-kotlin-compose-y-dda-mamdani.md`, `docs/ART_DIRECTION.md`, `design-tokens.json`, `assets/ASSET_MANIFEST.md` y la auditoría de código/capturas de esta conversación.

## 1. Dirección recomendada

**Un atlas musical de jardín, ilustrado sobre papel.** La plataforma se siente como un libro de exploración; cada juego es un lugar del mismo jardín. El sonido se expresa mediante resonancias, luz cálida y polen discreto. Esta metáfora visual no introduce un jardín persistente, monedas, cultivos ni reglas de progresión nuevas.

### Lenguaje plástico

| Aspecto | Especificación propuesta |
| --- | --- |
| Material principal | Papel marfil mate. Grano de fibra fino, apenas visible, limitado al fondo y a ilustraciones. |
| Ilustración | Capas de papel recortado con tinta botánica; masas de color planas y detalles interiores escasos. Evitar mezclar acuarela difusa con contornos vectoriales rígidos en un mismo conjunto. |
| Formas | Hojas asimétricas, tallos curvos, copas redondeadas. Las irregularidades pertenecen al dibujo; las áreas táctiles mantienen geometría estable. |
| Contorno | Tinta verde oscura. Referencia inicial: trazo de 1,5–2 dp para iconos de 24 dp, con remates redondos; revisar por tamaño óptico. La notación conserva sus propias métricas. |
| Profundidad | Hasta tres planos decorativos: fondo, vegetación lejana y motivo principal. UI y notación se superponen como una capa funcional limpia. |
| Iluminación | Luz diurna cálida y difusa desde arriba a la izquierda. Ocre localizado en respuestas correctas; sin luces pulsantes generales. |
| Textura | Sin manchas, fibras o ruido bajo pentagramas, teclas, texto y controles. Sin bordes envejecidos que parezcan suciedad. |
| Personajes | Siluetas reconocibles, anatomía simplificada y expresión contenida. Sin cabezas desproporcionadas, ojos enormes, mejillas rosadas ni sonrisas en todos los objetos. |

La sensación adulta proviene de la composición editorial, el espacio, el dibujo cuidado y el vocabulario directo. La interfaz dice «Escuchá la secuencia», no «¡Hacé magia con tus amiguitos!».

### Lo que se repite

- Fondo papel, tinta, verde principal y tratamiento de superficies.
- Tipografías y jerarquía; controles con la misma geometría y estados.
- Iconos de tinta de una misma familia; textura y orientación de la luz.
- Tarjetas musicales, selectores, encabezados y mensajes de resultado compartidos.
- Resonancia azul, recompensa ocre y error terracota acompañado de texto.
- Ritmo de movimiento y criterios de accesibilidad.

### Lo que puede variar

- Lugar: claro, invernadero, estanque o jardín de polillas.
- Planta dominante, criatura y objeto central.
- Composición del campo y controles exigidos por las mecánicas existentes.
- Un acento natural secundario por juego; nunca una paleta y una familia de botones independientes.

Una variante cambia la escena, no la tinta ni la legibilidad de la información musical. Escenarios nocturnos quedan fuera de la primera referencia para evitar introducir otro tema antes de validar el claro.

### Exclusiones

Neón, estética espacial/pixel art como presentación final, glassmorphism, metal o plástico brillante, fondos fotográficos, 3D volumétrico, tipografía infantil, emojis como assets, explosiones violentas, confeti multicolor y animaciones de rebote constante. Los degradados no serán un recurso de controles o fondos UI; el sombreado pintado dentro de una ilustración sí puede existir.

## 2. Paleta semántica

La paleta marfil/verdes/ocre está confirmada como base de las muestras. Se conservan los nueve colores del JSON como punto de partida. Los nuevos roles siguientes evitan usar colores decorativos como texto funcional; su ajuste final continúa pendiente de revisión visual.

| Rol | Valor | Uso |
| --- | --- | --- |
| `background` | `#F5EEDC` | Papel principal. |
| `surface` | `#FFF9ED` | Tarjetas, notación y controles claros. |
| `text` / `onSurface` | `#28392F` | Instrucciones, títulos funcionales, pentagramas y notas. |
| `textSecondary` | `#536457` | Información secundaria legible; no aplicar opacidad adicional. |
| `primary` | `#315844` | Acción principal y estado seleccionado. |
| `onPrimary` | `#FFF9ED` | Texto sobre verde principal. |
| `primaryPressed` | `#254633` | Presión de acción primaria. |
| `surfaceSelected` | `#E1E9DB` | Fondo de opción elegida; texto verde principal. |
| `border` | `#667A67` | Límites funcionales de controles. No sirve como texto pequeño. |
| `sage` | `#A7B99E` | Vegetación y masas decorativas secundarias. |
| `moss` | `#718563` | Detalles botánicos; no texto pequeño sobre papel. |
| `success` | `#D9AD69` | Acento de acierto/recompensa con tinta oscura. |
| `warning` | `#C9806B` | Acento terracota decorativo; sin texto pequeño encima. |
| `errorText` | `#8B4436` | Mensajes de error y contorno asociado. |
| `errorSurface` | `#F3DED5` | Fondo de mensaje de error. |
| `audio` | `#86AAB3` | Ondas e ilustración sonora; no único indicador esencial. |
| `audioText` | `#365F69` | Etiquetas de escucha/sonido. |
| `audioSurface` | `#E1ECEB` | Fondo de estado de escucha. |
| `focus` | `#3C6872` | Anillo visible de foco, separado del borde. |
| `disabledSurface` | `#E2DECF` | Control deshabilitado. |
| `disabledText` | `#627063` | Etiqueta de control deshabilitado. |

Proporción orientativa: 60% papel, 25% verdes, 10% otros naturales y 5% acentos. No convertirla en una cuota ni colorear cada tarjeta de un tono distinto.

**Comprobación numérica inicial:** contraste de luminancia relativa sRGB en colores sólidos, sin textura/transparencia: tinta/papel 10,57:1; claro/verde principal 7,67:1; tinta/ocre 5,90:1; secundario/papel 5,45:1; error/superficie de error 5,45:1; verde/selección 6,47:1; audioText/audioSurface 5,80:1. El borde/surface da 4,41:1: se reserva para geometría, no texto normal. Estas cuentas no verifican pantallas renderizadas.

Referencia para texto normal: 4,5:1; texto grande: 3:1, siguiendo [W3C, contraste mínimo](https://www.w3.org/WAI/WCAG22/Understanding/contrast-minimum.html). Los indicadores esenciales también necesitan contraste y formas/texto redundantes; la paleta clara no garantiza por sí sola accesibilidad.

## 3. Tipografía y notación

| Uso | Familia propuesta | Tamaño/altura de línea inicial | Peso |
| --- | --- | --- | --- |
| Título del menú | Fraunces | 30/36 sp | 600 |
| Título de pantalla | Fraunces | 24/30 sp | 600 |
| Sección y HUD | Nunito Sans | 18/24 sp | 600–700 |
| Instrucción y cuerpo | Nunito Sans | 16/24 sp | 400 |
| Botón y opción | Nunito Sans | 16/20 sp | 700 |
| Ayuda secundaria | Nunito Sans | 14/20 sp | 400–600 |
| Notación | Renderer vectorial existente, sujeto a validación | Métricas musicales; no escala tipográfica de texto | No aplica |

Reservar Fraunces para títulos; números, temporizadores e instrucciones usan Nunito Sans. Sin mayúsculas extendidas, tracking amplio o cursiva ornamental en HUD. La tabla es una referencia: no se fuerza una altura de componente que recorte texto ampliado.

La selección exacta de archivos y pesos queda pendiente. Las distribuciones consultadas declaran OFL: [Fraunces](https://github.com/google/fonts/blob/main/ofl/fraunces/upstream_info.md) y [Nunito Sans](https://github.com/google/fonts/blob/main/ofl/nunitosans/METADATA.pb). Al integrar se conservarán versión, procedencia y licencia del archivo concreto, con carga local/offline.

La notación actual se conserva como base: es programática y procede de datos estructurados. No se introduce Bravura hasta detectar una limitación real del trazado existente. Si se adopta, una fuente musical no sustituye la lógica de disposición de notas, alteraciones y líneas adicionales.

Propuesta inicial para tarjetas de nota: 112 × 120 dp preferidos, variante compacta de 96 × 112 dp por validar; separación entre líneas de 7 dp preferida y 6 dp como hipótesis mínima de prueba. Mantener escala uniforme, márgenes para extremos y no encoger indefinidamente para alcanzar cuatro enemigos. Son dimensiones experimentales, no una legibilidad demostrada. Si no caben, el sistema debe respetar su reserva de espacio y reducir simultaneidad efectiva.

## 4. Componentes y estados

### Geometría común

- Escala de espaciado: 4, 8, 12, 16, 24 y 32 dp.
- Margen móvil: 16 dp; 24 dp cuando el ancho útil permita conservar controles y lectura.
- Radios: 8 dp para etiquetas pequeñas; 12 dp para selectores; 16 dp para botones, tarjetas y paneles. Radio 24 reservado a piezas deliberadas, sin convertir todos los controles en píldoras.
- Botón principal: mínimo 52 dp de alto, padding horizontal 24 dp. Secundario: mínimo 48 dp. Ambos admiten crecer con texto.
- Área táctil: mínimo objetivo de 48 × 48 dp. No resolver teclas contiguas mediante áreas invisibles superpuestas.
- Bordes: 1 dp normal; 2 dp seleccionado; foco externo de 2 dp separado 2 dp de la pieza. Verificar con densidades reales.
- Sin sombra en tarjetas de listas. Diálogos pueden usar elevación nativa de 4 dp como punto de partida; la sombra no será el único límite de una superficie.

### Familia mínima

| Componente | Presentación y conducta |
| --- | --- |
| Botón primario | Verde/tinta clara, radio 16; una acción dominante por bloque de decisión. |
| Botón secundario | Papel claro, borde verde, texto verde. |
| Acción terciaria | Texto verde y área táctil completa; volver, ayuda o inspección. |
| Tarjeta de minijuego | Papel claro, título, descripción, pequeño motivo botánico/musical y acción inequívoca. La fila completa puede ser pulsable. |
| Panel | Papel claro; agrupación por espacio y fondo. Decoración solo fuera del contenido. |
| Menú | Lista vertical de juegos dentro de una composición editorial; navegación e historial existentes conservados. |
| Selector | Fondo claro; seleccionado con fondo salvia claro, contorno verde y marca/texto, más estado semántico. |
| HUD | Información compacta en Nunito Sans. Vidas representadas con hojas y cantidad escrita. |
| Mensaje de estado | Texto específico, signo/forma y color. No depender solo de vibración, partículas o cambio de tono. |
| Tarjeta musical | Superficie lisa con tinta oscura. El personaje queda fuera del área de notación. |

| Estado | Regla visual |
| --- | --- |
| Normal | Colores base, sin oscilación ni animación permanente. |
| Presionado | Primario se oscurece; escala opcional 0,98 en su contenido, sin reducir el área táctil. Respuesta inmediata. |
| Seleccionado | Fondo `surfaceSelected`, borde de 2 dp y marca de elección. No agrandar/reacomodar el control. |
| Deshabilitado | Fondo/etiqueta dedicados, acción realmente inactiva y semántica deshabilitada. Explicar el motivo cuando no sea evidente. |
| Foco | Anillo visible independiente de la selección. |
| Cargando | Etiqueta útil y progreso cuando corresponda; reservar espacio para evitar saltos. No convertir cada audio breve en un bloqueo ornamental. |
| Acierto | Acento ocre, texto «Correcto» o información equivalente y onda discreta. |
| Error | Texto `errorText`, superficie clara de error y mensaje concreto. Mantener selección y reintento conforme a las reglas del juego. |

Estados de error, vacío, permiso de micrófono rechazado, guardado fallido y partida interrumpida usan la misma familia de superficies. Las metáforas botánicas no deben ocultar qué pasó.

### Adaptación móvil

Diseñar por espacio útil en dp, con texto en sp; no escalar una captura completa. Validar 360 y 411 dp, un ancho mayor, proporciones diferentes, barras del sistema y fuente a 100%, 130% y 200%. Son casos de comprobación, no certificación.

Menú y preparación pueden desplazarse. Controles de una partida quedan alcanzables mientras la escena cede espacio. No fijar obligatoriamente la relación 2/3 de escena y 1/3 de controles.

**Simón conserva horizontal**, por decisión previa del usuario. Menú y Defender continúan verticales. Una identidad compartida no requiere la misma orientación.

## 5. Aplicaciones concretas

### Menú principal: primera pantalla de referencia

Composición de página de atlas: fondo papel, título «Entrená tu oído» en Fraunces y una rama ilustrada lateral que no tape texto. Debajo, lista de tarjetas de papel de una misma familia. Cada juego tiene una miniatura propia; no un fondo saturado y un símbolo Unicode distinto.

Motivos iniciales: piano entre hojas para Simón; instrumentos en un invernadero para reconocimiento tímbrico; árbol y polilla para Defender. Herramientas de voz y afinación conservan nombres explícitos. Historial y demostración mantienen acceso, como acciones secundarias. La versión pasa a una posición discreta.

El usuario eligió atlas con tarjetas en Q2. Se descarta el mapa navegable para esta definición inicial: exigiría producir más arte y descubrir puntos interactivos para realizar una tarea simple.

### Music Defender: jardín del árbol guardián

Campo vertical con vegetación poco contrastada en bordes y árbol en el centro inferior. Las criaturas entran desde el arco superior y laterales, siguiendo las trayectorias actuales. Cada una transporta una tarjeta clara de notación; la tarjeta domina y el cuerpo de la criatura es secundario.

Concepto confirmado en Q3: polilla con alas vegetales y árbol sin rostro; acción «Tocar». El dibujo y escala concretos quedan sujetos a muestra visual. Color salvia propuesto para la polilla. HUD con tres hojas más «3 vidas», puntaje y pausa. Panel inferior con los nombres y alteraciones actuales.

- Acierto: evaluación inmediata; onda azul desde el árbol y criatura que se disuelve en un pequeño grupo de partículas ocres.
- Respuesta incorrecta: conservar selección, texto «No coincide; probá otra nota» y borde terracota oscuro; sin daño adicional.
- Llegada: descontar vida según sesión, apagar una hoja, breve inclinación del árbol y mostrar respuesta correcta en una superficie limpia.
- Pausa: campo detenido/oculto según la conducta actual, panel de reanudación; sin movimiento decorativo de fondo.

No se cambia matching, tiempo de viaje, aparición, vidas o DDA con este rediseño. La especie, el árbol sin rostro y el término «Tocar» están confirmados; Q5–Q7 aprueban el acabado/siluetas de v2, quedando pendientes sus exportaciones pequeñas y estados. El código dice nave/base/disparar; la traducción a jardín no está implementada.

### Simón Dice Musical: claro del piano

Pantalla horizontal. Encabezado compacto, fase de actividad visible y teclado como elemento dominante. Papel claro en teclas blancas, tinta oscura en negras y marco ilustrado de madera/papel con follaje solamente por fuera del teclado.

Durante la escucha: etiqueta «Escuchá la secuencia» y señal azul de sonido. La tecla que suena recibe resaltado ocre y marca adicional visible, sin modificar su geometría. Durante la respuesta: «Ahora repetí la secuencia» y los mismos controles musicales, sin partículas sobre las teclas.

Selección de escala en tarjetas de papel compartidas. Al terminar, resumen claro con longitud máxima completada; pequeño brote ilustrado como acompañamiento, sin afirmar un logro pedagógico ni crear progreso adicional. No introducir una flor o animación larga por cada nota: la secuencia y su sincronización tienen prioridad.

## 6. Assets e ilustraciones

### Formatos y carpetas propuestas

```text
assets/
  ASSET_MANIFEST.md
  source/
    common/
    menu/
    games/{defender,simon,instruments,rhythm,tuner,voice}/
  exports/android/
    common/
    menu/
    games/{defender,simon,instruments,rhythm,tuner,voice}/
  licenses/
design/
  references/music-garden/
  specimens/
```

Es una estructura futura; no implica crear carpetas vacías. `source` conserva los editables; `exports` identifica exportaciones revisadas. Al integrar, `res/drawable*` y `res/font` reciben los recursos concretos usados por Android, con procedencia/versiones anotadas para evitar copias divergentes. No mantener un segundo conjunto de tokens dentro de `music_garden_starter/`.

Nombre: `familia_objeto_variante_estado.ext`, por ejemplo `enemy_moth_sage_idle.svg`, `scene_simon_clearing_base.webp` o `hud_leaf_life_empty.xml`. El nombre del recurso Android no incluye mayúsculas ni guiones.

| Tipo | Producción |
| --- | --- |
| Iconos, hojas y motivos simples | Máster SVG; exportar a VectorDrawable XML/ImageVector compatible. |
| Texturas y escenas con grano | Máster editable con capas; WebP/PNG a tamaños de uso medidos, evitando fondos gigantes. |
| Árbol y criatura sencillos | SVG por capas o raster transparente; decidir tras ver la muestra a tamaño real. |
| Personaje con animación dibujada | Sprite/atlas únicamente si transformaciones por capas no expresan el movimiento aprobado. Sin sprite sheets por defecto. |
| Botones, tarjetas, notación, gráficos y ondas | UI/renderizado programático; sin texto funcional pintado en imagen. |

### Inventario propuesto por familia

El inventario siguiente describe las familias de producción. Las fuentes para muestras y la referencia de acabado/árbol/polilla ya existen en `design/`; los assets finales y exportaciones Android siguen pendientes. Ver el manifiesto para estado y rutas reales.

| Familia | Assets previstos | Estados/criterio |
| --- | --- | --- |
| Común | Papel fino, rama de esquina, motivos de hoja; iconos volver, pausa, reproducir, volumen y resultado | Iconos funcionales legibles a 24 dp; decoraciones sin semántica. |
| Menú | Una cabecera botánica y miniaturas de los seis módulos visibles | Mismo encuadre/estilo; sin badges ni textos incrustados. |
| Defender compartido por variantes | Árbol, polilla, hoja de vida; luciérnaga posterior opcional | Árbol normal/impacto; hoja llena/vacía. Entrada/acierto de criatura preferentemente programáticos. No producir especie distinta por dificultad sin motivo didáctico. |
| Simón | Claro, marco exterior de piano y brote de cierre | Teclas, notas y selección son UI; sin imágenes por cada tecla. |
| Instrumentos | Invernadero de borde y siluetas coherentes de los instrumentos realmente disponibles | No mostrar la silueta de la respuesta correcta antes de contestar. Miniaturas genéricas para preparación; revelación cuando corresponda. |
| Ritmo | Juncos o tallos en marco exterior | Figuras, compases, cursor y zona de tap programáticos; plantas no sustituyen notación. |
| Afinador | Motivo de madera/hoja y eventual silueta genérica de instrumento | Aguja, cents, cuerdas y estados medidos programáticos. No sugerir afinación con una planta ambigua. |
| Voz | Motivo de resonancia/hojas | Tono, desviación y trazas son datos/render; ninguna onda decorativa se presenta como medición. |
| Historial/laboratorio | Reutilizar papel, iconos y paneles comunes | No necesitan una escena nueva. |

El manifiesto debe separar ilustraciones, fuentes, iconos y componentes programáticos. Para cada asset real: ID, ruta de máster/exportación/runtime, propósito, tamaño lógico previsto, dimensiones en píxeles/viewBox, variantes, origen, autor, licencia, atribución, versión, estado y pantallas consumidoras.

### Encargo reproducible para una ilustración

Cada encargo debe indicar sujeto, uso, encuadre, tamaño lógico, transparencia, paleta, dirección de luz, zonas libres, capas y estados. Ejemplo pendiente de aprobación:

> Polilla botánica de alas salvia, silueta simple de papel recortado, tinta verde oscura y grano sutil; vista frontal ligeramente elevada, sin ojos grandes ni expresión humana. Luz suave superior izquierda, fondo transparente, cuerpo secundario respecto de una tarjeta musical externa. Máster separado en cuerpo/alas; referencia de uso 40–48 dp, comprobar reducción a 32 dp. Sin texto, pentagrama, resplandor neón o detalles que desaparezcan al reducir.

Las muestras deberán verse sobre la superficie real y junto a la notación. Para producción con IA se revisarán anatomía, transparencia, estilo y exportación; se registrará su origen. No se genera ningún asset en esta ronda.

## 7. Movimiento

Lenguaje: asentarse, inclinarse, flotar y resonar. Sin elasticidad de juguete, sacudidas de pantalla ni rebotes repetidos. El juego debe verse completo con la ambientación desactivada.

| Evento | Parámetro inicial | Curva/comportamiento |
| --- | --- | --- |
| Presión | 140 ms | `cubic-bezier(0.4,0,0.2,1)`; escala hasta 0,98 como máximo recorrido. La acción no espera la animación. |
| Entrada de panel/tarjeta | 220 ms | `cubic-bezier(0,0,0.2,1)`; opacidad y desplazamiento de hasta 8 dp, sin escalonar listas largas. |
| Salida/transición de pantalla | 160 ms | Fundido; evitar desplazamientos grandes durante cambios de orientación. |
| Aparición de criatura | 340 ms | Opacidad y escala 0,96→1. La tarjeta musical debe ser legible desde su activación lógica; no ocultar el estímulo mientras corre su tiempo. |
| Acierto | 450 ms | Onda con expansión y desvanecimiento; criatura resuelta lógicamente desde la respuesta. |
| Impacto al árbol | 320 ms | Inclinación local de hasta 2 grados y retorno; sin mover todo el campo. |
| Error en control | 160 ms | Cambio de borde/superficie y mensaje; sin sacudida obligatoria. |
| Ambientación | 5 s/ciclo | Senoidal, desplazamiento de hasta 2 dp o giro de 1 grado; máximo dos motivos simultáneos. |
| Idle de criatura | 3 s/ciclo | Movimiento del cuerpo/alas de hasta 2 dp; mantener estable la tarjeta de lectura. |
| Trayectoria funcional | Tiempo de sesión | Derivada del reloj de juego; sin easing ornamental que altere llegada, evaluación o dificultad. |

Partículas iniciales: hasta ocho por acierto, vida máxima de 450 ms y tope global propuesto de 24 visibles; únicamente ocre/azul, sin blur costoso. Son presupuestos iniciales a medir, no garantías de rendimiento.

Reducción de movimiento: quitar ciclos ambientales, balanceo, inclinaciones y partículas; conservar mensajes y estados estáticos. Puede usarse fundido breve de hasta 100 ms si el ajuste permite animación, o cambio inmediato si está desactivada. Mantener trayectorias funcionales necesarias y ofrecer información adicional cuando corresponda; no cambiar tiempos o puntuación al reducir decoración.

Usar Canvas, recursos y animación nativa de Compose. Cachear geometría estable y desacoplar HUD/decoración de actualizaciones del campo. El ticker actual de Defender de unos 33 ms no es una medición de FPS. Se medirá antes de añadir complejidad; no se fijan promesas de rendimiento o latencia.

## 8. Integración documental y reglas para agentes

Después de la revisión, consolidar las decisiones aceptadas en `docs/ART_DIRECTION.md` y los valores en `design-tokens.json`. El borrador conserva su estado histórico o se archiva claramente; no dejar dos fuentes normativas divergentes.

Distribución de responsabilidades propuesta:

- `ART_DIRECTION.md`: reglas, componentes, variantes, movimiento y excepciones.
- `design-tokens.json`: valores canónicos y semánticos; adaptación tipada en Kotlin cuando se autorice implementar.
- `ASSET_MANIFEST.md`: recursos reales y encargos pendientes diferenciados.
- `design/references/music-garden/`: muestras/capturas con estado aprobado o pendiente, fecha y descripción de lo normativo.
- `CONTEXT.md`: solo vocabulario del dominio; sin paletas ni instrucciones de implementación.
- `AGENTS.md`: índice breve hacia las fuentes, como ya existe. Ajustar solo si la revisión cambia reglas estables.
- `.agents/estado-prototipo.md`: avance, verificación y siguiente paso.

No hace falta un ADR por color o radio. Solo considerar uno para una elección transversal costosa y con alternativas reales, si llega a existir.

Antes de implementar un nuevo juego, el agente deberá identificar sus componentes reutilizados, su variante de escenario y sus estados; consultar tokens y referencia aprobada; comprobar lectura musical y audio antes de decorar; registrar assets nuevos y excepciones. No inferir una aprobación visual a partir de un boceto, ni aprobación pedagógica a partir de una demostración.

Reutilizar Kotlin/Compose/Material 3 y separar reglas de presentación. No introducir biblioteca de UI, iconos, SVG, Rive o Lottie sin una necesidad identificada que las herramientas actuales no resuelvan. Mantener las pruebas funcionales existentes al migrar; comprobar pantallas y estados en Android, no con un inspector HTML.

## 9. Secuencia de revisión y decisiones pendientes

### Árbol de decisiones

1. **Base ya expresada por el usuario:** dirección natural/editorial, fantasía suave, identidad común y variantes por juego; sin implementar UI ahora.
2. **Ronda 1 confirmada por el usuario:** papel recortado con tinta; atlas con tarjetas; árbol sin rostro, polillas y «Tocar»; paleta y pareja tipográfica como base de prueba.
3. **Ronda 2 confirmada:** Q5–Q7 aceptan el acabado, árbol y polilla de v2 como referencia, con simplificación por escala. No equivalen a exportaciones finales aprobadas.
4. **Ronda 3 confirmada:** Q8–Q10 aprueban menú v1, tipografías y estados; capturas de referencia y colores semánticos registrados. Sin verificación Android.
5. **Ronda 4, aceptación provisional:** Q11–Q13 se aceptan como primer acercamiento, con notación estructurada, cuatro objetivos en posiciones estáticas, piano horizontal y reducción de movimiento. No son diseños finales.
6. **Cierre documental inicial:** reglas consolidadas en ART_DIRECTION v0.5 y referencias clasificadas por alcance. Próxima evidencia pendiente: demostración nativa y assets finales, sin autorización de implementación en esta etapa.

### Qué requiere referencia visual

- Grado de textura, irregularidad del trazo y profundidad de papel.
- Fraunces/Nunito Sans con texto real en español y al tamaño de dispositivo.
- Aspecto y escala del árbol y la polilla; nivel de fantasía y expresividad.
- Densidad del menú y proporción de ilustración frente a contenido.
- Contraste renderizado y lectura de claves/alteraciones en tarjetas compactas.
- Movimiento a velocidad real; la tabla no acredita una animación agradable.

### Decisiones de ronda 1 confirmadas

- Q1: papel recortado con tinta como acabado dominante.
- Q2: menú como atlas con tarjetas.
- Q3: árbol sin rostro, polilla de alas vegetales y acción «Tocar» como traducción de Defender a Music Garden.
- Q4: paleta y pareja tipográfica propuestas como base de las muestras.

### Pendiente de aprobación posterior

Revisión nativa de los primeros acercamientos de Defender/Simón/movimiento, exportaciones finales de ilustraciones y ajustes físicos/nativos de tamaños, accesibilidad y rendimiento. La referencia de material y el menú/estados están aprobados dentro de los límites de su registro; Q11–Q13 siguen provisionales. No se modifica ninguna regla de juego ni se autoriza implementar la interfaz al cerrar la definición documental inicial.

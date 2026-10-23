# Definición del afinador de instrumentos

Estado: síntesis funcional y concreciones confirmadas por el usuario mediante «ok» el 2026-10-22. Puntos de prueba y división aprobados mediante «Perfecto»; especificación #8 y tickets #9–#14 publicados; implementación pendiente.

## Intención confirmada

El usuario pidió el 2026-10-22 un afinador para afinar instrumentos, con una función que lo haga especialmente útil respecto de un afinador común. Quiere definir primero esta función y retomar después Rhythm Tap, generación/renderizado de partituras y Defender.

En la primera ronda confirmó:

- Diseñar primero para guitarra. Luego definir soporte para ukelele, bajo, violín, viola, cello y otros instrumentos; todavía no se especificó ni verificó ese soporte.
- Mantener un afinador cromático normal.
- Función protagonista: afinar y entrenar el oído.
- Segunda prioridad: afinación guiada con comprobación final.
- Tercera prioridad: seguimiento del instrumento.
- Las tres funciones adicionales deben poder activarse/desactivarse a voluntad del usuario. No están definidas todavía sus combinaciones ni persistencia de preferencias.
- Problemas que quiere resolver: reconocimiento de una nota distinta, afinación demasiado mecánica y dependencia de la pantalla sin aprender a afinar de oído.

Estas son decisiones de intención y prioridad del usuario; no implican eficacia demostrada ni aprobación del alcance académico final por el director.

## Segunda ronda confirmada por el usuario (2026-10-22)

- Ofrecer dos prácticas: ajustar el instrumento de oído y comprobar, o clasificar primero el sonido como grave/agudo/afinado y luego ajustar. La primera es la experiencia principal; la clasificación es opcional (aceptación de la recomendación C).
- Durante el ajuste de oído no mostrar la medición ni ofrecer pistas. Se permite repetir la referencia; el usuario solicita la comprobación cuando considera que suena bien. Sin avisos hablados.
- Detectar la cuerda automáticamente y permitir fijarla. La tercera ronda concreta que la detección libre corresponde al modo libre; entrenamiento y guiado siguen objetivos ordenados por la app.
- Incluir afinación estándar, un conjunto de alternativas y afinaciones personalizadas que puedan ingresarse y guardarse. Todavía no se eligieron los presets ni se definió el editor.

## Tercera ronda confirmada por el usuario (2026-10-22)

- Tres modos: entrenamiento guiado cuerda por cuerda en el orden de la app; afinación guiada cuerda por cuerda con comprobación final; libre con reconocimiento automático y fijación manual opcional de una cuerda cuando el reconocimiento no resulte adecuado.
- En entrenamiento y guiado, ante un sonido mucho más grave/agudo que el objetivo, avisar «Muy lejos, revisar cuerda correcta». Intención: advertir una posible confusión de cuerda durante el ajuste. No se estableció un umbral ni se garantiza detectar toda confusión o evitar daños. El aviso expresa una posibilidad, no identifica inequívocamente otra cuerda.
- En entrenamiento, «Comprobar» abre el micrófono: al tocar, el usuario ve la nota que afinó realmente y puede corregir con la medición visible. Un intento desafinado se registra como error aunque después se corrija. Luego se pasa a la siguiente cuerda; al terminar se muestran resultados para observar mejora y motivar el uso.
- Aceptada la comprobación final: segunda pasada por las cuerdas; las que no pasen quedan pendientes hasta corregirse.
- Aceptado el editor propuesto para guitarra de seis cuerdas: nota y octava por cuerda y guardado con nombre. El «Dale» no enumera presets; estándar y alternativas están confirmados como alcance, pero el conjunto exacto permanece por concretar.
- Seguimiento de instrumento y progreso en entrenamiento: ambos, diferenciados.

Incompatibilidad detectada en esta ronda: abrir el micrófono únicamente al comprobar no permite emitir el aviso de posible cuerda equivocada durante el ajuste de oído. La cuarta ronda resuelve este punto.

## Cuarta ronda confirmada por el usuario (2026-10-22)

- Durante el ajuste de oído el micrófono puede escuchar, sin mostrar mediciones ni pistas; solo se permite el aviso excepcional de posible cuerda equivocada. Esta decisión resuelve la incompatibilidad de la ronda anterior. «Comprobar» revela la medición y evalúa el intento.
- Tras la corrección, avanzar automáticamente cuando la afinación se mantenga estable durante un intervalo. Tolerancia y duración siguen pendientes. Silencio, ruido o lectura incierta no cuentan como error: pedir tocar nuevamente.
- Permitir instrumentos con nombre e historiales separados, con un instrumento predeterminado para comenzar sin configuración. Asociar el seguimiento a instrumento, afinación y cuerda; el progreso de oído podrá verse globalmente o filtrado.
- Criterios fijos y explícitos, sin DDA inicialmente. El usuario propone dos exigencias: Normal, con una desviación pequeña pero razonable, y Pro o Concierto, con menos desviación admitida. Se adopta provisionalmente «Pro» como nombre de trabajo; ninguno de los modos acredita por su nombre precisión profesional ni aptitud para concierto.
- No se fijaron valores numéricos ni se verificó la precisión sobre guitarra real.

## Quinta ronda confirmada por el usuario (2026-10-22)

- Probar Normal con ±5 cents y Pro con ±2 cents. Exigir un segundo de medición estable dentro del margen antes del avance automático. Son parámetros experimentales aceptados para probar; no constituyen precisión medida ni calibración.
- Presets iniciales: estándar, Drop D, medio tono abajo, Drop C, Open G y DADGAD; además de personalizadas guardables de seis cuerdas con nota, octava y nombre.
- Orden guiado y entrenamiento: sexta a primera cuerda, identificadas por número y nota objetivo.
- Configuración: modo Libre/Guiado/Entrenamiento, criterio Normal/Pro, comprobación final opcional y seguimiento opcional. En entrenamiento, clasificación previa grave/agudo/afinado opcional.
- Recordar preferencias. Comprobación final y seguimiento activados inicialmente en guiado/entrenamiento; clasificación previa desactivada. Con seguimiento desactivado, mostrar el resumen actual sin guardarlo.
- Tres referencias sonoras: instrumento siendo afinado (predeterminada), piano y tono puro. Para el primer alcance el instrumento es guitarra. El soporte de otros instrumentos y sus referencias se definirá posteriormente.
- Suspender el análisis del micrófono mientras se reproduce la referencia, para no evaluar el sonido emitido por la app (aceptación de la recomendación Q4, con ampliación de las opciones de referencia).

## Síntesis funcional confirmada

### Alcance inicial

Afinador Android/offline para guitarra de seis cuerdas, conservando el modo cromático normal. Extensiones a otros instrumentos posteriores. Sin DDA en este primer alcance.

### Modos

1. **Libre:** reconocimiento automático, con posibilidad de fijar una cuerda si la detección no resulta adecuada. Incluir uso cromático normal.
2. **Guiado:** objetivos de sexta a primera, medición visible y avance automático al cumplir el criterio estable. Comprobación final opcional.
3. **Entrenamiento:** objetivos de sexta a primera; referencia repetible, ajuste de oído sin medición visible ni pistas ni voz. Escucha durante el ajuste para el aviso excepcional de posible cuerda equivocada. Al pulsar «Comprobar», revelar la medición, evaluar el intento y permitir corregir con ayuda visual antes de avanzar. El error inicial no desaparece al corregir. Resumen al terminar; clasificación previa opcional.

Concreción confirmada al aceptar la síntesis: evaluar el intento de oído con la primera medición válida y estable posterior a «Comprobar», antes de la corrección asistida. Una lectura inválida no es un error del usuario.

### Comprobación y seguimiento

- Comprobación final: segunda pasada, con corrección de las cuerdas que no superen la revisión. No tratarla como otro intento de oído independiente.
- Seguimiento del instrumento separado del progreso personal de oído. Instrumentos con nombre, uno predeterminado y filtros por instrumento/afinación/cuerda/criterio.
- Conservar resultados iniciales y resultados tras corregir de manera diferenciada; el resumen debe permitir observar evolución sin atribuir automáticamente causas mecánicas ni eficacia pedagógica.

### Límites y trabajo técnico pendiente

- «Muy lejos, revisar cuerda correcta» expresa una posibilidad; el umbral y su comportamiento ante armónicos/lecturas inciertas requieren pruebas. No garantiza evitar daños.
- Verificar estimador y captura con guitarra real, especialmente sexta cuerda, errores de octava y criterio Pro. No equiparar una pantalla estable con una medición exacta.
- Definir procedencia y permisos de las referencias de guitarra y piano, y comprobar que permiten comparar la nota/octava objetivo en el teléfono.
- La estabilidad, las ventanas de análisis y los márgenes se especificarán de forma reproducible. Los valores aceptados son iniciales y experimentales.
- No se implementó código ni se publicaron nuevas issues durante esta entrevista.

## Base existente y límites

La entrega v0.10 documenta «Afiná tu voz»: captura local con YIN, objetivo seleccionable, referencia sonora, desviación en cents y gráfica temporal. Es una base técnica candidata; no demuestra precisión sobre instrumentos. Ver [Demostración v0.10](Demostracion-prototipo-v0.10.md).

## Funciones elegidas; comportamiento pendiente de definir

Las tres direcciones fueron elegidas por el usuario. Los detalles de esta sección se completan mediante las rondas confirmadas; los puntos no resueltos siguen siendo propuestas:

- Afinación guiada y verificación final: ayudar durante el ajuste y volver a comprobar los objetivos al terminar. La interfaz y el criterio de verificación dependen del instrumento.
- Afinación con entrenamiento del oído: ajuste sin pistas, referencia repetible y comprobación solicitada por el usuario; clasificación grave/agudo/afinado opcional. Conservar un acceso directo para afinar sin ejercicio.
- Seguimiento entre sesiones: comparar mediciones de objetivos equivalentes, sin atribuir una causa mecánica a las diferencias observadas.

No se afirma novedad comercial ni eficacia pedagógica de estas propuestas. Los afinadores existentes ya incluyen funciones avanzadas: [TonalEnergy](https://www.tonalenergy.com/home) describe seguimiento de tendencias e historial; [Peterson](https://www.petersontuners.com/sweeteners/) ofrece afinaciones específicas. Estas referencias delimitan ideas, no constituyen una revisión exhaustiva.

## Próximo paso

El afinador queda definido en [la especificación](Especificacion-afinador-v1.md) y [el plan publicado](Plan-tareas-afinador-v1.md). Primer ticket disponible: [#9, afinador libre](https://github.com/SaladinoJC/Tesis/issues/9). La implementación no comenzó. Ritmo, generación/renderizado y Defender siguen pendientes de definición en esta ampliación. La aprobación del usuario no implica aprobación del alcance académico final por el director.

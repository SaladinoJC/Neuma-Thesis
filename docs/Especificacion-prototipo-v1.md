# Primera demostración Android: dos minijuegos, DDA difuso y prueba de voz

Estado: síntesis y división en tareas aprobadas por el usuario el 2026-10-02 para publicación. Valores técnicos nuevos señalados como propuestas iniciales, no calibrados.

Publicada como [especificación #1](https://github.com/SaladinoJC/Tesis/issues/1). Las tareas y sus bloqueos están en GitHub; este documento conserva la síntesis de referencia.

Actualización confirmada por el usuario (2026-10-02), posterior al primer APK: inicio en un menú de la app, preparación de Simón en una pantalla separada y entrada mediante un piano en pantalla, sustituyendo la grilla de botones. Referencia visual: `docs/ui simon.png`; paleta oscura con acentos rosas. El sonido usa muestras de piano `mf` de University of Iowa convertidas a OGG. Las menciones a botones en este documento se interpretan ahora como teclas del piano; se mantienen las tres escalas, tónica fija, secuencia creciente y fin al primer error. Los catálogos de escalas/juegos y vidas de la imagen son orientativos, no cambios adicionales aprobados. Procedencia y conversión en `docs/Audio-piano-Simon.md`.

Ajuste confirmado por el usuario al probar v0.2 (2026-10-02): Simón se abre en horizontal, con preparación y partida sin scroll y teclado ancho. Las pulsaciones del turno del jugador se procesan en orden sin esperar a que termine el sonido anterior; girar el dispositivo no interrumpe la sesión. El menú muestra la versión instalada y elimina las tres frases indicadas por el usuario. Las reglas de escala, puntuación y fin al primer error se conservan.

Actualización solicitada por el usuario (2026-10-09), después de probar v0.3 en el celular: «¿Qué instrumento es?» debe generar una melodía breve aleatoria al elegir el instrumento, sustituyendo la nota aislada repetida. Implementación v0.3.1: frase nueva por pregunta, conservada en las reescuchas y registrada en cada respuesta; misma gramática para todos los instrumentos. Se mantienen samples reales, alturas disponibles y duraciones comparables, cuatro opciones y límite de escuchas. La gramática pentatónica de seis notas y sus tiempos son elecciones provisionales del agente, pendientes de calibración, descritas en [Melodias-instrumentos-v0.3.1.md](Melodias-instrumentos-v0.3.1.md). Esta actualización prevalece sobre las referencias a estímulo fijo/nota compartida de los borradores; no cambia el alcance pendiente de DDA y voz.

## Problem Statement

Estado de implementación al 2026-10-09: el incremento #4 incorpora adaptación de escuchas en modo automático (nivel A), panel de inspección y escenarios aislados. La práctica manual A/B/C conserva tres escuchas como elección provisional del agente para mantener el recorrido anterior. Los parámetros iniciales de esta especificación se concretan en [DDA-instrumentos-v0.4.md](DDA-instrumentos-v0.4.md); siguen pendientes de calibración. Desbloqueos, voz y exportación/reinicio corresponden a incrementos posteriores.

El tesista necesita una demostración ejecutable que él y su director puedan usar para discutir el sistema de entrenamiento auditivo. Actualmente existen documentos y fichas de diseño, pero no una aplicación. Antes de una evaluación con estudiantes se necesita comprobar el funcionamiento de dos juegos, observar un DDA explicable y explorar captura y estimación de tono en el teléfono objetivo.

## Solution

Una aplicación Android offline con Simón musical armónico, ¿Qué instrumento es? y una pantalla técnica de voz. Cada juego registra resultados en un perfil local. Un motor Mamdani compartido interpreta indicadores y reglas específicos por juego, modifica la ayuda en reconocimiento tímbrico y habilita nuevos niveles sin retirar progreso. Un panel permite inspeccionar decisiones y ejecutar casos simulados separados del desempeño real. El dispositivo inicial de demostración es el Samsung S22 Ultra del usuario.

## User Stories

1. Como tesista, quiero instalar la aplicación en Android para demostrar el prototipo en mi teléfono.
2. Como jugador, quiero acceder a ambos minijuegos y a la prueba de voz desde una pantalla inicial para recorrer la demostración.
3. Como jugador, quiero usar la aplicación offline para jugar sin cuentas ni conectividad.
4. Como jugador, quiero leer instrucciones en español para entender cada mecánica.
5. Como jugador de Simón, quiero escuchar y ver una secuencia para repetirla con los botones correspondientes.
6. Como jugador de Simón, quiero que cada botón tenga una posición, etiqueta y sonido reconocibles para identificarlo.
7. Como jugador de Simón, quiero que la secuencia crezca una nota después de cada ronda correcta para aumentar el desafío.
8. Como jugador de Simón, quiero que escala y tónica permanezcan fijas durante la partida para conservar las asociaciones entre botones y sonidos.
9. Como jugador de Simón, quiero terminar al primer error y ver la longitud máxima completada para conocer mi resultado.
10. Como evaluador, quiero distinguir la secuencia completada de la intentada para interpretar correctamente el puntaje.
11. Como jugador de instrumentos, quiero escuchar una muestra real y elegir entre cuatro opciones para identificar el instrumento.
12. Como jugador de instrumentos, quiero que las muestras se presenten con altura y duración comparables para reducir pistas ajenas al timbre.
13. Como jugador de instrumentos, quiero jugar diez preguntas y ver un resumen para tener una partida acotada.
14. Como jugador de instrumentos, quiero reescuchar dentro del límite visible para disponer de ayuda.
15. Como jugador de instrumentos, quiero que el DDA ajuste las escuchas de la próxima pregunta para responder a mi desempeño reciente.
16. Como jugador, quiero que el tiempo de respuesta no se penalice para poder pensar antes de elegir.
17. Como jugador, quiero que un acierto con reescuchas siga siendo correcto para distinguir resultado de autonomía.
18. Como jugador, quiero iniciar las partidas automáticas en el nivel más alto desbloqueado para avanzar en el curriculum.
19. Como jugador, quiero desbloquear el siguiente nivel tras dos partidas automáticas completas consecutivas de dominio alto para progresar con evidencia sostenida.
20. Como jugador, quiero conservar los niveles desbloqueados aunque luego baje la dificultad para mantener mi progreso.
21. Como jugador, quiero conservar resultados y progreso al cerrar la aplicación para retomarlos después.
22. Como tesista, quiero elegir niveles manualmente para explorar el catálogo sin alterar los desbloqueos.
23. Como evaluador, quiero distinguir partidas automáticas y manuales para interpretar los resultados.
24. Como evaluador, quiero ver indicadores, pertenencias, reglas activadas y salida difusa para entender la inferencia.
25. Como evaluador, quiero distinguir la salida propuesta del ajuste aplicado para detectar límites o esperas de la política.
26. Como tesista, quiero ejecutar casos simulados de errores, aciertos y alternancia para reproducir cambios del DDA.
27. Como evaluador, quiero mantener simulaciones separadas de partidas reales para evitar confundirlas con desempeño observado.
28. Como tesista, quiero elegir una nota objetivo en la pantalla de voz para observar mi desviación respecto de ella.
29. Como usuario de la prueba de voz, quiero ver frecuencia, nota aproximada y desviación en cents durante una nota sostenida para inspeccionar el estimador.
30. Como usuario, quiero que silencio o una estimación no fiable se indiquen como ausencia de tono para evitar resultados engañosos.
31. Como usuario, quiero poder rechazar el permiso de micrófono sin que falle la aplicación para controlar la captura.
32. Como tesista, quiero comprobar YIN con señales de frecuencia conocida y silencio para separar exactitud del algoritmo de afinación de mi voz.
33. Como tesista, quiero exportar resultados y decisiones con su configuración para analizarlos fuera de la aplicación.
34. Como usuario, quiero reiniciar datos locales de forma explícita para repetir la demostración desde el inicio.
35. Como evaluador, quiero distinguir parámetros de prueba, resultados simulados y mediciones reales para conocer los límites de la demostración.

## Implementation Decisions

### Decisiones confirmadas

- Android exclusivo, Kotlin y Jetpack Compose. Reglas de juego/DDA independientes de interfaz; no hay requisitos multiplataforma.
- Perfil local único, funcionamiento offline, persistencia de resultados y desbloqueos, opción de reinicio. Inicialmente no se guardan grabaciones de voz.
- Simón: comienza con una nota; cada ronda superada agrega una nota a la misma secuencia; termina al primer error. Escala y tónica fijas por partida. Curriculum pentatónica → diatónica → cromática. Puntaje por longitud máxima completada, además de secuencia intentada y posición del error. El DDA no agrega variación intrapartida a esta primera versión.
- Instrumentos: diez preguntas como valor inicial; cuatro opciones; muestras reales comparables; curriculum A/B/C según cercanía de instrumentos. El nivel de instrumentos permanece fijo durante la partida. La ayuda intrapartida es el máximo de escuchas, comenzando con tres totales y variando entre una y tres, incluida la primera reproducción.
- Evaluar adaptación de instrumentos después de cada respuesta desde la tercera, con las últimas tres rondas. Cambiar como máximo una escucha; dejar dos rondas entre cambios. El cambio se aplica a la pregunta siguiente, nunca a una ya presentada. No se penaliza tiempo de respuesta.
- Indicadores de instrumentos: aciertos recientes y uso de reescuchas. Un acierto con ayuda sigue siendo correcto; expresa menor autonomía que acertar con la primera escucha. Acertar tiene más peso que el uso de ayuda.
- Modo automático: nivel más alto desbloqueado. Dos partidas automáticas completas consecutivas calificadas como dominio alto en el nivel actual habilitan el siguiente para la próxima partida. Los desbloqueos son permanentes y específicos de cada juego.
- Modo manual: selección de nivel para práctica/inspección; resultados identificados y excluidos de desbloqueos. Casos simulados claramente separados de datos jugados.
- Motor compartido Mamdani con configuración e interpretación específicas por juego. No se mezclan puntajes ni historiales de juegos o niveles para inferir una supuesta habilidad común.
- Panel técnico: entradas, pertenencias, reglas activadas, salida difusa, acción propuesta, acción aplicada y motivo de espera/límite. Configuración identificable en resultados/exportaciones.
- Prueba de voz separada: YIN, nota sostenida, frecuencia/nota aproximada y desviación en cents respecto del objetivo. Mostrar diferencia respecto del objetivo no demuestra exactitud del detector ni eficacia educativa.

### Estructura propuesta para implementación

- Un punto de entrada de sesión acepta acciones del jugador y expone estado observable, resultados y decisiones del DDA. Coordina las reglas de ambos juegos y la persistencia sin depender de Compose. Se prueba este comportamiento completo; no la estructura interna del motor.
- Los servicios de audio y almacenamiento quedan detrás de límites pequeños reemplazables en pruebas. La reproducción real y la captura se verifican también en Android; sustituirlas en una prueba no acredita funcionamiento del hardware.
- La captura entrega bloques de muestras PCM y sus tiempos al estimador. Procesamiento fuera del hilo de interfaz; detener captura/reproducción al salir de la pantalla y no continuar una partida silenciosamente en segundo plano.
- Persistir nivel desbloqueado, racha de dominio alto por juego/nivel, partidas y decisiones con origen y versión de configuración. Usar identificadores de partida/ronda para evitar duplicar progresión al reabrir una pantalla o repetir una acción.
- Una partida interrumpida se identifica como tal y no desbloquea. No se exige recuperar una ronda incompleta después de cerrar la app; sí conservar progreso y resultados ya finalizados.
- Cada partida de instrumentos comienza con tres escuchas y una ventana reciente vacía. Tras un cambio posterior a la ronda 3, las rondas 4 y 5 no pueden provocar otro cambio; el próximo cambio posible se decide al finalizar la ronda 6 para la 7.
- El modo manual puede explorar los niveles para la demostración sin alterar rachas ni desbloqueos. Las simulaciones usan un estado aislado. Dos partidas automáticas válidas se consideran consecutivas entre las automáticas completas del mismo nivel; una automática completa no calificada reinicia la racha. Al desbloquear un nivel se inicia su racha desde cero.
- Exportación estructurada portable, con esquema/versiones y origen automático/manual/simulado; incluir medidas de voz sin audio crudo. Reinicio con confirmación explícita, abarcando perfil, resultados, rachas y desbloqueos; configuración de fábrica disponible para repetir escenarios.

### Propuestas numéricas iniciales — no calibradas

Estas propuestas concretan valores que no se fijaron en la entrevista. Se documentarán, versionarán y podrán cambiarse tras pruebas; no representan umbrales pedagógicos validados.

- Exactitud de instrumentos: proporción de respuestas correctas. Autonomía: proporción de aciertos logrados con la primera escucha entre las respuestas correctas; si no hay aciertos, autonomía cero. Un error domina la necesidad de ayuda frente a una supuesta independencia de reescuchas.
- Para la adaptación, calcular esos indicadores en la ventana reciente de tres rondas. Para calificar dominio de una partida, usar la partida completa, no solo sus últimas rondas.
- Términos de entradas normalizadas: bajo con trapecio (0, 0, 0,35, 0,55), medio con triángulo (0,35, 0,60, 0,85), alto con trapecio (0,65, 0,85, 1, 1). Representar extremos con hombros y cubrir todo el intervalo, incluyendo 0 y 1.
- Operadores iniciales: AND mínimo, OR máximo, implicación mínimo, agregación máximo y defuzzificación por centroide. La resolución numérica será documentada y comprobada por tolerancias.
- Reglas iniciales de ayuda: exactitud baja → aumentar ayuda; exactitud media → mantener; exactitud alta y autonomía baja → mantener; exactitud alta y autonomía media/alta → reducir ayuda. La prioridad conductual es no reducir ayuda ante malos resultados aunque no se hayan usado reescuchas.
- Salida de ayuda en [-1, 1]: reducir con hombro (-1, -1, -0,5, 0), mantener con triángulo (-0,5, 0, 0,5), aumentar con hombro (0, 0,5, 1, 1). Salida ≤ -0,25 propone quitar una escucha; ≥ 0,25 propone agregar una; las restantes mantienen. Aplicar después los límites y la espera entre cambios, conservando ambas decisiones en la traza.
- Salida de dominio en [0, 1]: bajo con hombro (0, 0, 0,20, 0,35), medio con triángulo (0,25, 0,50, 0,75), alto con hombro (0,65, 0,80, 1, 1). Calificación operativa alta a partir de 0,75. Para instrumentos, exactitud baja → dominio bajo; media → medio; alta con autonomía baja → medio; alta con autonomía media/alta → alto. Revisar casos fronterizos antes de integrar desbloqueos.
- Para Simón, normalizar longitud máxima completada frente a una referencia de prueba por escala: 5, 7 y 9 notas respectivamente, limitada a [0, 1]. Usar reglas bajo/medio/alto sobre esa entrada para la salida de dominio. Las referencias se podrán ajustar y no expresan equivalencia de dificultad entre escalas ni entre juegos.
- Una inferencia sin salida válida mantiene el estado y presenta un diagnóstico; no concede dominio alto ni desbloquea. Registrar el identificador de reglas y configuración en los escenarios de prueba.

### Audio y muestras

- Banco candidato: University of Iowa Musical Instrument Samples, sujeto a comprobar cada archivo y su permiso/procedencia. Catálogo propuesto A: piano/violín/flauta/marimba; B: flauta/clarinete/trompeta/trombón; C: flauta/clarinete/oboe/fagot.
- Seleccionar una altura sonora realmente compartida y una presentación comparable, conservar ataques y evitar transposición para producir supuesta equivalencia. Registrar recortes/ganancia/procedencia. La equivalencia perceptual y el orden de dificultad son hipótesis, no resultados validados.
- Primera captura mediante APIs Android compatibles con el teléfono. Incorporar C++/Oboe solo si la prueba lo justifica; no constituye un requisito de este incremento ni una garantía de baja latencia.
- En voz, registrar tamaño y frecuencia de muestreo, configuración YIN, frecuencia estimada, tiempo de procesamiento y validez. No etiquetar tiempo de procesamiento como latencia acústica extremo a extremo.
- Propuesta de comprobación matemática inicial: señales limpias de 110, 220 y 440 Hz, con error ≤ 10 cents después del arranque; silencio sin tono válido. Ampliar frecuencias y ruido al evaluar límites. Estos criterios son técnicos de prueba, no compromisos de precisión de voz real.

## Testing Decisions

- No existe código ni pruebas previas que reutilizar. El usuario aprobó un límite principal de sesión para probar acciones → estado/resultados/decisiones/progreso/exportación con audio y almacenamiento controlados.
- Un segundo límite justificado es muestras PCM → estimación de tono: permite comparar contra frecuencias conocidas sin depender de canto o de un micrófono.
- Probar conductas observables, no métodos privados ni forma de los módulos. Las pruebas deben detectar errores de puntaje, ajustes fuera de rango, cambios de una ronda iniciada, avances duplicados y mezcla de datos simulados/manuales con progreso real.
- Casos de juegos: repetición correcta e incorrecta de Simón; fallo en cinco después de completar cuatro; respuesta correcta/incorrecta de instrumentos; límite real de reproducciones; diez respuestas y resumen; interrupción de partida.
- Casos DDA: errores sostenidos, aciertos sin reescuchas, aciertos con ayuda, alternancia, datos insuficientes, extremos de pertenencia, salida vacía, espera entre ajustes, máximos/mínimos y traza de regla aplicada. Comparar contra el comportamiento esperado documentado, no contra resultados generados por la propia implementación.
- Casos de progreso: primera y segunda partida de dominio alto; partida no alta que reinicia racha; separación por juego/nivel/origen; nivel final sin avance adicional; persistencia al cerrar/reabrir; reinicio y acciones duplicadas.
- Pruebas de integración Android para rutas principales y persistencia, y comprobación real de reproducción/captura en S22 Ultra. La prueba física debe dejar datos de dispositivo/configuración y resultados; si aún no se puede ejecutar, registrar pendiente y no declarar cumplida la demostración.
- Comprobar PCM de frecuencias conocidas y silencio; medir el estimador por separado de la desviación del cantante respecto del objetivo. Las mediciones de procesamiento no acreditan latencia acústica.
- Exportación/reimportación para inspección: estructura parseable, versiones y origen preservados, sin grabaciones de voz y sin alteración del progreso al exportar.

## Out of Scope

- Los otros tres minijuegos propuestos y el catálogo completo.
- Cuentas, backend, sincronización, multijugador, iOS y web.
- ML de apoyo al tono, extracción de voz polifónica, XR/3D y optimización avanzada sin evidencia de necesidad.
- Grabaciones de voz persistidas, estudio con estudiantes, demostración de transferencia o eficacia pedagógica.
- Una afirmación de latencia extremo a extremo o de precisión de voz real basada únicamente en tests sintéticos.
- Recuperar exactamente una partida interrumpida, varios perfiles y un editor general de reglas en la interfaz. La configuración inicial será versionada; el panel es de inspección y reproducción de escenarios.
- Actualizar automáticamente el protocolo Word; la discrepancia del DDA se documenta y su actualización académica se hará por separado.

## Further Notes

- Primera demostración sin fecha de reunión fijada, para el tesista y director. Prototipo de discusión y medición técnica, no producto final.
- El diseño de plataforma y motor fue confirmado en la entrevista; las propuestas numéricas son decisiones de ingeniería reversibles y deben conservar su condición experimental.
- Fuentes primarias: [Compose](https://developer.android.com/compose), [inferencia Mamdani/Sugeno](https://www.mathworks.com/help/fuzzy/types-of-fuzzy-inference-systems.html), [proceso de inferencia](https://www.mathworks.com/help/fuzzy/fuzzy-inference-process.html), [muestras de Iowa](https://theremin.music.uiowa.edu/MIS.html) y [Oboe para Android](https://developer.android.com/games/sdk/oboe).

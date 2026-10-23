# Rhythm Tap, generador y renderizador — definición

Estado: síntesis funcional y valores experimentales iniciales confirmados mediante «ok»; puntos de prueba y seis incrementos aprobados mediante «perfecto» el 2026-10-22. Especificación #15 y tickets #16–#21 publicados. Implementación pendiente.

## Dirección confirmada por el usuario (2026-10-22)

- Incorporar Rhythm Tap a partir de la ficha 08, con generación procedural de partituras por categorías y un renderizador para el minijuego.
- Buscar una cobertura amplia de células rítmicas y combinaciones, convirtiendo esa intención en un catálogo explícito.
- Diseñar un catálogo ampliable hacia avanzado, entregando primero un conjunto inicial/intermedio completo y comprobable (aceptación mediante «Ok dale»).
- Definir este conjunto después del afinador. Defender queda para después de ritmo. No se inició implementación del afinador ni de ritmo en esta sesión de definición.

## Base documental

[Ficha 08](Ideas%20Desarrolladas/08-Eco-ritmico.md) y [selección de prototipo](IdeasPrototipo.md).

La ficha atribuye a una entrevista anterior decisiones particulares: entrada por tap táctil, lectura contra metrónomo, un compás de entrada, notación de una sola línea rítmica sin alturas, reproducción opcional del patrón y registro de desviación temporal por ataque. Se conserva esa atribución como punto de partida, sin presentar sus afirmaciones de validación o novedad como resultados comprobados.

La ficha también registra árbol rítmico y generación por rechazo como decisiones previas. Su representación de ligaduras, cobertura y coste de generación deberán verificarse técnicamente; el texto no acredita que estén implementados ni que todas las combinaciones sean viables.

El DDA del proyecto está confirmado como Mamdani, con adaptación intrapartida y desbloqueos. Su aplicación concreta a ritmo necesita especificación propia; no trasladar las reglas de instrumentos ni los márgenes Normal/Pro del afinador.

## Primera ronda confirmada por el usuario (2026-10-22)

- Compases simples 2/4, 3/4 y 4/4 primero; compuestos 6/8, 9/8 y 12/8 después; irregulares como 5/8 y 7/8 previstos para avanzado.
- Cobertura inicial/intermedia progresiva: redondas, blancas, negras, corcheas, semicorcheas, silencios correspondientes, puntillos, ligaduras, contratiempos, síncopas y tresillos. Fusas y otros grupos de valoración especial quedan para avanzado.
- Todas las opciones de práctica se llaman **categorías**, y corresponden a células concretas. Ejemplos del usuario: semicorcheas, silencios de corchea, corchea con puntillo + semicorchea y otras combinaciones. No crear dos niveles de selección obligatorios separados como familias y células.
- Comenzar con cuatro compases. Mostrar la partitura antes de iniciar; al pulsar «OK» comienza el compás de entrada y luego la ejecución.
- Prever ampliación a ocho y dieciséis compases con desplazamiento lento de la partitura sincronizado con la música. La dirección, distribución y forma de seguimiento todavía no están definidas. No implica prometer todos los compases visibles simultáneamente en un teléfono.

## Segunda ronda confirmada por el usuario (2026-10-22)

- Seleccionar categorías debe garantizar presencia considerable pero moderada de sus células; no basta una aparición aislada. Negras y corcheas permitidas por defecto como conexión. No introducir otros recursos no seleccionados. Falta concretar una cuota verificable y cómo distribuirla entre varias categorías.
- Varios renglones, desplazamiento vertical suave, compás actual y siguientes visibles y marcador de posición temporal. Cantidad de compases por renglón según legibilidad. Se aplica también al diseño previsto para ocho y dieciséis compases.
- Sin reproducción del patrón antes de comenzar.
- Permitir activar/desactivar que suene «el ritmo que toca el usuario». Interpretación propuesta, pendiente de precisión: sonido disparado por sus taps reales, no reproducción de los ataques ideales del patrón. No asumir equivalencia con el toggle de audio objetivo de la ficha anterior.
- Compás de entrada audible; permitir apagar el metrónomo durante la ejecución para que el usuario mantenga por sí mismo tempo y ritmo.
- Feedback inmediato sencillo sin detener el ejercicio, y revisión posterior sobre partitura con adelantados, atrasados, omitidos y golpes extra. Sonido del tap activable/desactivable, sujeto a la precisión del punto anterior.

Estas decisiones reemplazan la demo previa opcional y revisan la asistencia sonora de la ficha. No se trasladan automáticamente sus afirmaciones sobre ausencia de ventaja al escuchar el patrón.

## Tercera ronda confirmada por el usuario (2026-10-22)

- Sonido opcional de la ejecución real: cada tap dispara su sonido en el instante en que se realiza, incluyendo adelantos/atrasos. No reproducir automáticamente el patrón correcto. Independiente del metrónomo.
- Probar una ocupación aproximada del 60 % de la duración con las categorías seleccionadas y el resto con conexión. Repartir presencia entre categorías, buscando varias apariciones por cada una. Si no caben, avisar para reducir selección o ampliar extensión; no omitirlas silenciosamente. Es una cuota experimental, no un valor calibrado ni un 60 % para cada categoría individual.
- **Una partida es un único ejercicio**, no cinco. Flujo: marcar categorías → «Generar» → generación en el momento → mostrar partitura → «Play» → compás de entrada → ejecución → resultados. El botón «Play» concreta el «OK» de la primera ronda.
- Desde resultados se permite repetir el mismo ejercicio como práctica. Falta cerrar cómo cuenta para progresión, conservando la distinción existente entre práctica y evidencia usada para desbloqueos.
- DDA propuesto por el usuario para probar, todavía no plenamente decidido: desbloquear categorías y aumentar de a poco el tempo en categorías que ya domina. No confundir esta intención con reglas difusas, umbrales o dominio musical validado.

Consecuencia por resolver: si el tempo se mantiene fijo en un ejercicio y cambia al generar el siguiente, la adaptación de ritmo ocurre entre partidas. No llamarla intrapartida. La definición general del proyecto contempla ambas funciones, pero no obliga a aplicarlas de igual forma en todos los juegos.

## Cuarta ronda confirmada por el usuario (2026-10-22)

- Tempo fijo durante la ejecución. Los cambios se aplican al generar el siguiente ejercicio: adaptación entre partidas.
- Progresión: elegir categorías desbloqueadas y resolver ejercicios nuevos. Práctica libre: permitir también categorías bloqueadas, sin evidencia para desbloquear. Repetir la misma partitura es práctica, con resultados diferenciados y sin aportar a desbloqueos.
- Al mezclar categorías, usar el menor tempo recomendado entre las seleccionadas. Evaluar cada categoría por separado, sin permitir que los aciertos de células fáciles oculten dificultades con las nuevas.
- Mantener incrementos de tempo y criterios de dominio como parámetros experimentales inspeccionables y ajustables. La política sigue siendo una primera prueba, no un DDA calibrado.

## Quinta ronda confirmada por el usuario (2026-10-22)

- Aceptado el primer conjunto de categorías para compases simples, presentado en la ronda anterior y registrado en [el catálogo](Catalogo-ritmico-borrador.md). Ampliaciones posteriores separadas; no equivale a aprobar toda posible combinación rítmica.
- Ejercicios de progresión nuevos con metrónomo apagado también aportan a desbloqueos y aumento de tempo. Registrar y mostrar con/sin metrónomo por separado, sin exigir ambas modalidades para desbloquear. Registrar también la preferencia de sonido de taps.

## Síntesis funcional confirmada

- Android/offline, entrada táctil y notación rítmica sin alturas. Una partida es un ejercicio generado de cuatro compases, preparado para ampliar posteriormente a ocho y dieciséis.
- Seleccionar categorías y compás, generar en el momento, revisar partitura, pulsar Play, escuchar un compás de entrada y ejecutar; resultados al terminar. No reproducir el patrón objetivo antes ni durante el ejercicio.
- Catálogo inicial aceptado: negras, corcheas, figuras largas, células de semicorcheas, puntillos, silencios, tresillos, síncopa y ligaduras concretadas en el catálogo. Simples primero; compuestos e irregulares se amplían después.
- Presencia conjunta de seleccionadas aproximadamente 60 % de duración, buscando varias apariciones por categoría, sin doble conteo; conexión con negras/corcheas. No omitir una seleccionada si no cabe: explicar incompatibilidad o pedir reducir selección/ampliar longitud cuando esté disponible.
- Varios renglones, marcador temporal y desplazamiento vertical suave. No comprimir hasta volver ilegible la notación ni interpretar la vista previa como permiso para iniciar automáticamente.
- Conteo de entrada audible. Metrónomo posterior y sonido de taps reales opcionales e independientes. Sin voz ni demo previa. Feedback inmediato sencillo, revisión posterior por ataque y resultados.
- Progresión: ejercicios nuevos con categorías desbloqueadas, evaluación por categoría, tempo fijo durante ejecución y cambios en la siguiente partida. Mezclas usan el menor tempo recomendado entre seleccionadas; una categoría fácil no compensa errores en otra.
- Práctica libre permite bloqueadas, elección manual de tempo y repetir la partitura, sin aportar a desbloqueos. Resultados diferenciados de progresión.
- Desbloqueos y ajuste de tempo con Mamdani explicable, primera configuración experimental; no presentar las categorías «dominadas» como diagnóstico de competencia musical.
- Con/sin metrónomo pueden progresar, con resultados identificados y comparaciones diferenciadas. No exigir dominar ambas modalidades.

## Concreciones técnicas propuestas para la primera prueba

Elecciones del agente para revisar con la síntesis; no mediciones ni parámetros pedagógicos calibrados:

### Evaluación y registro

- Evaluar ataques, no cuánto tiempo se mantiene presionada la pantalla. Una ligadura de prolongación no exige otro tap. Un silencio exige ausencia de taps, no una pulsación de confirmación.
- Emparejamiento uno a uno, ordenado temporalmente: un tap no satisface dos ataques. Conservar timestamps originales, emparejamientos, desviación con signo, ataques perdidos y taps extra. No corregir el reloj a partir de los taps del usuario cuando apaga el metrónomo: mantener la grilla original para medir también deriva de tempo.
- Ventanas experimentales por ataque: a tiempo hasta `min(60 ms, 20 % del intervalo local entre ataques)`; ventana amplia hasta `min(150 ms, 45 % de ese intervalo)`. Definir intervalo local como la menor distancia positiva al ataque esperado anterior/siguiente; si no hay vecinos, usar duración de un pulso. Esto limita solapamientos al aumentar tempo o subdivisión. Registrar valores efectivos y versión; no cambiar tolerancias por DDA inicialmente.
- Dentro de amplia pero fuera de estrecha: adelantado/atrasado. Sin tap asociado: perdido. Tap no asociado: extra. No puntuar favorablemente una categoría de silencios solo porque no tenga ataques: evaluar ausencia de taps en sus intervalos y errores en su contexto.
- Feedback de un tap puede aparecer al recibirlo; omisiones se confirman al cerrar su ventana. Resumen por categoría y general: aciertos, desvíos, omisiones, extras y deriva respecto del reloj de referencia, sin reducir la evidencia a un único promedio.
- Guardar semilla, partitura exacta, categorías objetivo y presencia real, tempo, compás, configuración de evaluación, audio activo, origen, datos de entrada/resultados y propuestas/aplicaciones del DDA. Repetir usa el mismo patrón y se etiqueta práctica. No registrar simulaciones como datos jugados.
- Mantener historial local/exportación y compatibilidad del prototipo. Salir o pausar durante la ejecución deja interrupción, sin progreso ni reanudación silenciosa a mitad del ejercicio. Se puede comenzar de nuevo como práctica. No alterar reglas de Simón/instrumentos.

### Configuración inicial del DDA

- Propuesta: 60 BPM iniciales por categoría y aumentos máximos de 5 BPM para la siguiente partida; techo experimental inicial 120 BPM. Práctica permite elegir tempo dentro del rango verificado, sin que ello implique dominio.
- Evidencia por categoría: precisión de ataques y respeto de silencios, omisiones y extras, con soporte mínimo de varias apariciones. Dos resultados nuevos completos consecutivos de dominio operativo alto permiten proponer aumento de tempo; prácticas e interrupciones no cuentan.
- Desbloqueos permanentes mediante una ruta gradual con prerrequisitos explícitos; inicialmente negras y corcheas. El grafo de prerrequisitos, las funciones de pertenencia, las reglas Mamdani y el mínimo de evidencia se concretarán en la especificación como configuración de prueba, inspeccionable con escenarios simulados. No tratar el orden de identificadores del catálogo como secuencia pedagógica.
- No aumentar tempo por un promedio global que esconda una categoría débil. El motor expone entradas, reglas activadas, salida y motivo de aplicación/no aplicación. Si los datos son insuficientes, conservar tempo y accesos.
- El primer prototipo busca verificar generación, renderizado, evaluación y conducta del DDA. La eficacia pedagógica, latencia física y exactitud temporal requieren mediciones separadas.

## Próximas decisiones

Definición y plan cerrados y publicados: [especificación](Especificacion-ritmo-v1.md) y [seis incrementos](Plan-tareas-ritmo-v1.md). Primer ticket disponible: [#16, ejercicio básico de cuatro compases](https://github.com/SaladinoJC/Tesis/issues/16). No volver a preguntar decisiones ya aceptadas. La implementación no comenzó; el siguiente tema de definición del usuario es Defender.

## Distinciones por resolver

- «Compás simple» describe la organización métrica; el tag `simple` de la ficha describe baja complejidad. No usarlos como equivalentes.
- El usuario resolvió unificar las opciones bajo «categorías». Internamente cada una debe definir qué célula o variantes contiene y cómo comprobar su presencia; no asumir que una mención genérica a semicorcheas ya especifica todas sus combinaciones.
- El alcance de notación rítmica no implica que el mismo renderizador ya cubra alturas/acordes de Defender.

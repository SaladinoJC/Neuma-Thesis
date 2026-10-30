# DDA de escuchas en reconocimiento tímbrico

Incremento [#4](https://github.com/SaladinoJC/Tesis/issues/4), autorizado por el usuario el 2026-10-09 al pedir continuar con el siguiente desarrollo mientras prueba v0.3.1. Conserva las melodías generadas y el banco real de la versión anterior. No implementa desbloqueos ni captura vocal.

## Conducta

El modo automático inicia en A con tres escuchas y ventana vacía. Después de cada respuesta, desde la tercera, evalúa las últimas tres preguntas. Puede proponer un cambio de una escucha para la próxima pregunta, dentro de [1, 3]. La pregunta ya iniciada conserva su ayuda. Después de un cambio en la pregunta 3, las preguntas 4 y 5 no pueden cambiarla; el próximo ajuste posible se decide tras la 6. Una partida nueva reinicia ventana y espera. La décima respuesta conserva una inferencia pero no aplica ajuste porque no existe próxima pregunta.

Elección provisional del agente para preservar la práctica anterior: seleccionar manualmente A/B/C mantiene tres escuchas. Se registra la propuesta difusa con motivo «Práctica manual: ayuda fija», sin aplicarla. El modo automático es la opción inicial de la interfaz. La política manual y los parámetros del motor pueden revisarse con el director.

La exactitud es la proporción de respuestas correctas en la ventana. La autonomía es la proporción de aciertos con primera escucha entre los aciertos; si no hay aciertos, vale cero. Un acierto con reescuchas sigue siendo correcto. No se utiliza el tiempo de respuesta. Las propuestas numéricas provienen de `Especificacion-prototipo-v1.md`; no son umbrales pedagógicos calibrados.

## Inferencia reproducible

Implementación Kotlin independiente de Android, sin biblioteca externa. Configuración `mamdani-help-v1`, AND/implicación mínimo, agregación máximo y centroide. Se consultó el 2026-10-09 la descripción primaria del [proceso de inferencia de MathWorks](https://www.mathworks.com/help/fuzzy/fuzzy-inference-process.html) para contrastar esas etapas; no se ejecutó MATLAB ni se atribuye una comparación experimental con su toolbox.

Entradas en [0, 1], ambas con los mismos términos:

| Término | Trapecio (a, b, c, d) |
| --- | --- |
| bajo | (0; 0; 0,35; 0,55) |
| medio | (0,35; 0,60; 0,60; 0,85) |
| alto | (0,65; 0,85; 1; 1) |

El triángulo se representa con b=c. Los hombros incluyen los extremos 0 y 1.

| Regla | Condición | Salida |
| --- | --- | --- |
| R1 | Exactitud baja | aumentar |
| R2 | Exactitud media | mantener |
| R3 | Exactitud alta y autonomía baja | mantener |
| R4 | Exactitud alta y autonomía media | reducir |
| R5 | Exactitud alta y autonomía alta | reducir |

R4/R5 expresan la alternativa media/alta mediante dos reglas agregadas por máximo. No hay un operador OR configurable en los antecedentes de este incremento.

Salida en [-1, 1]: reducir (-1; -1; -0,5; 0), mantener (-0,5; 0; 0; 0,5), aumentar (0; 0,5; 1; 1). Se evalúan 1.001 puntos equidistantes (paso 0,002), integrando área y momento con pesos de cuadratura trapezoidal en los extremos. Salida ≤ −0,25 propone quitar una escucha; ≥ 0,25 propone agregarla; entre ambas, mantener. Se aplican después el modo, datos suficientes, salida válida, fin de partida, espera y límites.

La inferencia sin área de salida mantiene ayuda y registra un diagnóstico; nunca se interpreta como una reducción. La configuración completa y el umbral se conservan en el resultado, junto con su versión. La precisión numérica inicial se contrasta con los casos extremos: el centroide continuo de los hombros de aumentar/reducir es ±11/18, y el del triángulo simétrico de mantener es cero; tolerancia de prueba 0,003.

## Panel y datos

El botón DDA abre un panel con decisiones de la partida actual o la más reciente del historial. Permite recorrer preguntas y observar ventana, exactitud/autonomía con numeradores y denominadores, pertenencias, fuerza de cada regla, centroide, propuesta, ajuste aplicado para la próxima pregunta y motivo. También expone funciones de pertenencia, resolución y umbral. Las partidas anteriores sin trazas se identifican sin inventar decisiones.

La pestaña Simulación permite elegir A/B/C y cuatro escenarios. Cada ejecución usa la misma sesión de juego con un reproductor simulado y almacenamiento en memoria, desde cero y fuera del hilo de interfaz. No emite sonido ni escribe en resultados/progreso reales. Sus registros llevan origen `simulated`, tiempo sintético cero y una marca visible de datos simulados; no son mediciones de desempeño de personas ni de reproducción física.

| Escenario | Respuestas preparadas | Ajustes esperados |
| --- | --- | --- |
| Aciertos sin reescuchas | 10 aciertos con primera escucha | Tras 3: 3→2; tras 6: 2→1; después conserva mínimo |
| Aciertos y luego errores | 3 aciertos sin reescuchas y 7 errores | Tras 3: 3→2; tras 6: 2→3; después conserva máximo |
| Aciertos con reescuchas | 10 aciertos usando todas las escuchas | Mantiene 3; autonomía cero |
| Aciertos y errores alternados | Acierto/error alternados, una escucha | Mantiene 3 con las reglas iniciales |

Cada respuesta registra su límite de escuchas y una `ddaDecision` opcional con ventana, indicadores, inferencia, propuesta, aplicación y motivo. El resultado identifica `automatic` o `manual`, versión `instruments-0.4-melodies-dda` y configuración. Los campos nuevos son opcionales al leer archivos de v0.3/v0.3.1; sus registros anteriores mantienen los datos originales. No se garantiza lectura de archivos nuevos con versiones antiguas de la app.

Los desbloqueos y la calificación difusa de dominio corresponden a #5; la progresión todavía no existe. La simulación no acredita eficacia pedagógica. La prueba física de v0.4 queda a cargo del recorrido documentado en `Verificacion-DDA-v0.4.md`.

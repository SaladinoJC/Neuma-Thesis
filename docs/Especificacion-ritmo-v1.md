# Rhythm Tap — especificación del primer alcance

Estado: síntesis funcional y parámetros iniciales confirmados mediante «ok»; puntos de prueba y seis incrementos aprobados mediante «perfecto» el 2026-10-22. R1–R5 implementados y verificados en software hasta v0.12.10, incluido historial/exportación; progresión R6 pendiente. Consultar el plan y sus guías para alcance y límites comprobados.

Publicada como [issue #15](https://github.com/SaladinoJC/Tesis/issues/15). [Plan aprobado y tickets #16–#21](Plan-tareas-ritmo-v1.md).

Origen: [entrevista](Definicion-ritmo.md) y [catálogo inicial aceptado](Catalogo-ritmico-borrador.md). Android/offline, Kotlin/Compose y Mamdani según las decisiones vigentes. Todas las concreciones técnicas de esta especificación son configuraciones iniciales verificables, no parámetros calibrados.

## Problema

El usuario quiere practicar lectura y precisión rítmica con partituras nuevas que contengan células elegidas, sin depender de ejemplos prefijados ni de escuchar previamente la respuesta. Necesita que las categorías seleccionadas aparezcan varias veces, medir su ejecución real y avanzar por categoría sin que un buen resultado global oculte dificultades puntuales.

## Solución

Una partida es un ejercicio procedural de cuatro compases. Seleccionar categorías y compás → Generar → revisar partitura → Play → compás de entrada audible → ejecución táctil → resultados. Repetir conserva el ejercicio como práctica. Ejercicios nuevos de progresión desbloquean categorías y ajustan el tempo recomendado para futuras partidas.

Primer alcance: compases simples 2/4, 3/4 y 4/4 y las 21 categorías aceptadas. Ampliaciones posteriores: ocho/dieciséis compases, compuestos, irregulares, fusas y otros grupos de valoración especial. El renderizador se prepara para desplazamiento vertical, sin prometer notación de alturas para Defender.

## Historias de usuario

1. Como estudiante, quiero elegir categorías asociadas a células concretas para practicar lo que necesito.
2. Como estudiante, quiero que cada elegida aparezca varias veces, con negras/corcheas de conexión.
3. Como estudiante, quiero elegir compás y recibir una explicación si mi selección no cabe o es incompatible.
4. Como estudiante, quiero generar una partitura nueva localmente y verla antes de empezar.
5. Como estudiante, quiero decidir cuándo pulsar Play y escuchar un compás de entrada.
6. Como estudiante, quiero leer una notación legible con barras, silencios, puntillos, tresillos y ligaduras cuando correspondan.
7. Como estudiante, quiero un marcador fijo y desplazamiento lateral continuo para seguir mi posición, con ventanas estáticas cuando reduzco movimiento.
8. Como estudiante, quiero responder con taps, sin micrófono ni necesidad de sostener el dedo.
9. Como estudiante, quiero escuchar opcionalmente mis taps reales, incluyendo sus errores de tiempo.
10. Como estudiante, quiero activar/desactivar el metrónomo después del conteo para practicar mantener el tempo.
11. Como estudiante, quiero feedback inmediato sin que el ejercicio se detenga por un error.
12. Como estudiante, quiero conocer adelantos, atrasos, omisiones y golpes extra al terminar.
13. Como estudiante, quiero respetar silencios y ligaduras sin que se esperen ataques inexistentes.
14. Como estudiante, quiero repetir exactamente la partitura anterior como práctica.
15. Como estudiante, quiero practicar categorías bloqueadas con tempo manual sin modificar desbloqueos.
16. Como estudiante, quiero progresar con partituras nuevas y categorías desbloqueadas seleccionadas por mí.
17. Como estudiante, quiero que el tempo permanezca fijo durante la ejecución.
18. Como estudiante, quiero que el siguiente ejercicio considere el tempo recomendado de la categoría más lenta de mi selección.
19. Como estudiante, quiero progreso por categoría para que acertar las fáciles no oculte errores en otras.
20. Como estudiante, quiero progresar con o sin metrónomo, sin obligación de dominar ambas modalidades.
21. Como estudiante, quiero comparar resultados distinguiendo ayudas, tempo y práctica/progresión.
22. Como usuario, quiero conservar resultados y progreso al reabrir y poder exportarlos.
23. Como usuario, quiero que una interrupción o error de audio no produzca una partida completada ni desbloqueos.
24. Como tesista, quiero inspeccionar entradas, pertenencias, reglas y acciones del DDA, con simulaciones separadas de datos jugados.
25. Como tesista, quiero reproducir un ejercicio y su evaluación con semilla, configuración y datos originales.

## Decisiones de implementación

### Modelo y catálogo

- Reglas de generación, evaluación y progreso independientes de Android/Compose. Reutilizar motor Mamdani, transacciones e historial en sus límites; no trasladar reglas de instrumentos ni criterios del afinador.
- Modelo musical con duraciones racionales exactas, compás, figuras/silencios, agrupación ternaria, ligaduras y procedencia de cada aparición de categoría. Transformar a tiempos de ejecución solo después de fijar tempo. Conservar árbol de subdivisión de la ficha donde corresponda, ampliándolo explícitamente con ligaduras y ocurrencias; una hoja aislada no representa toda la notación necesaria.
- Ataques esperados, dibujo, resaltado y audio de conteo derivan de la misma partitura. Redondeos no deben acumularse compás a compás. Una ligadura une figuras sin crear un ataque en su continuación.
- Catálogo de compases simples, duración en negras: R01 blanca=2; R02 redonda=4; R03 cuatro semicorcheas=1; R04 corchea+dos semicorcheas=1; R05 dos semicorcheas+corchea=1; R06 semicorchea+corchea+semicorchea=1; R07 corchea con puntillo+semicorchea=1; R08 semicorchea+corchea con puntillo=1; R09 negra con puntillo+corchea=2; R10 silencio de corchea+corchea=1; R11 corchea+silencio de corchea=1; R12 silencio de negra=1; R13 silencio de semicorchea+tres semicorcheas=1; R14 tres semicorcheas+silencio de semicorchea=1; R15 tresillo de corcheas=1; R16 corchea+negra+corchea=2, comenzando en tiempo entero para verificar el cruce sincopado; R17 dos negras ligadas entre tiempos=2, un ataque; R18 silencio de blanca=2; R19 silencio de compás completo=duración de ese compás; R20 negra=1; R21 dos corcheas=1.
- R17 se concreta inicialmente como dos negras ligadas, diferenciadas visualmente de una blanca; otras variantes requieren extensión explícita. R02 solo en 4/4 en este alcance, sin cruzar barras. R19 representa silencio de compás según métrica, no una duración fija de cuatro tiempos. Cada plantilla tiene contexto métrico admitido; no colocar una figura larga ilegiblemente sobre una barra para hacerla caber.
- No introducir variantes nuevas de silencios, tresillos o ligaduras bajo etiquetas ya aprobadas sin ampliar el catálogo. «Compás simple» y dificultad baja son conceptos distintos.

### Generación y presencia

- Categorías elegidas ocupan conjuntamente ≈60 % de duración; resto negras/corcheas. Propuesta técnica inicial: aceptar ocupación entre 50 % y 75 %, eligiendo la solución más cercana a 60 %, con al menos dos apariciones explícitas por categoría. Las duraciones discretas impiden garantizar 60 % exacto.
- No contar dos veces una misma duración si varias etiquetas describen subpatrones solapados. Usar procedencia de plantilla para cuota y evaluación por categoría; conservar coincidencias observadas sin inventar nuevas apariciones objetivo. Para negras/corcheas seleccionadas distinguir procedencia objetivo/conexión aunque visualmente coincidan.
- Verificar ausencia de recursos nuevos en las uniones: no crear una síncopa o silencio no permitido como efecto lateral. No considerar cada subfigura de una célula compleja como otra categoría practicada.
- Comprobar factibilidad antes de sortear: métrica, duración, cantidad mínima de apariciones, recursos y espacio disponible. Si no cabe, explicar y proponer reducir selección; no quitar categorías ni ampliar longitud automáticamente. En v1 todavía no ofrecer ocho/dieciséis como si estuvieran implementados.
- Generación por rechazo acotada y cancelable, con semilla y versión. Propuesta: máximo 500 candidatos y presupuesto monótono de un segundo, lo que ocurra primero, fuera del hilo de UI. Agotar el presupuesto es un fallo de búsqueda recuperable, no prueba de imposibilidad; distinguirlo del rechazo de factibilidad. No usar la afirmación de coste aceptable de la ficha como una medición.
- Historial/repetición guardan partitura exacta, además de semilla, para sobrevivir cambios del generador. Repetir el mismo patrón o regenerarlo solo cambiando su identificador no aporta evidencia nueva: comparar huella musical del contenido, no el UUID. Un patrón ya evaluado conserva origen práctica si se repite para progresión.

### Presentación, audio y reloj

- Notación rítmica de una voz sin alturas: cabezas, plicas, barras, silencios, puntillos, corchete/número de tresillo y ligaduras. Agrupar según pulso y métrica, sin cambiar la duración semántica para facilitar el dibujo.
- Actualización autorizada por el usuario el 2026-10-29: línea continua SVG con VexFlow local, fondo transparente sobre el papel, marcador fijo y desplazamiento lateral desde tiempo musical. Cabezas verdes para ataques a tiempo, sin recolorear plicas/barras ni introducir ataques en continuaciones ligadas. Revisión manual antes de Play y en resultados/historial; repetición sin marcas. Reducción de movimiento con ventanas estáticas por pulso y sin marcador móvil; alternativa nativa si no puede cargar el renderer. Esto sustituye la presentación por renglones/seguimiento vertical, sin depender del scroll para medir tiempo ni cambiar audio o evaluación. [Implementación y verificación](Rhythm-Tap-lectura-lateral.md).
- Conteo audible de un compás; metrónomo durante ejecución opcional. Sonido de taps reales opcional e independiente. No demo ni reproducción del patrón ideal. Recordar preferencias, sin iniciar audio por reabrir pantalla.
- Un único reloj monótono para timeline, taps y marcador. Marcar tiempos de eventos táctiles al ocurrir, no al terminar composición o dibujo. El adaptador debe mapear el reloj táctil y el audio a esa base y registrar incertidumbre/configuración; no asumir que llamar a reproducir significa que ya sonó.
- No programar precisión musical mediante una sucesión de esperas de UI. Reutilizar reproducción de bajo retardo cuando sirva, pero el reproductor de frases actual no acredita por sí solo un metrónomo sincronizado con taps. Verificar avance real y cancelación del audio en Android.
- Al apagar metrónomo mantener grilla y tempo originales; no realinear a los taps, porque se busca medir también deriva. No compensar automáticamente latencia con el desempeño observado. Cualquier offset técnico medido debe registrarse y no presentarse como exactitud acústica.
- Un contacto inicial es un tap; sostener el dedo no crea otros. Una caída de frames visual no crea errores adicionales de entrada. Segundo plano/pausa/salida interrumpen y liberan recursos; no reanudar una partida de progresión a mitad del ejercicio ni declarar completa una interrumpida.

### Evaluación

- Para cada ataque, intervalo local d=menor distancia positiva al ataque esperado anterior/siguiente; sin vecinos, duración de un pulso. Ventana estrecha `min(60 ms, 0,20d)` y amplia `min(150 ms, 0,45d)`. Guardar valores efectivos; el DDA no las modifica inicialmente.
- Asociación ordenada uno a uno, sin reasignar taps ya confirmados. Estas ventanas no deben permitir contar un tap para dos ataques. A tiempo si está dentro de estrecha; adelantado/atrasado dentro de amplia; fuera de asociaciones es extra; sin tap al cerrar amplia es omitido. Fronteras inclusivas documentadas.
- Mantener timestamps originales, desviación con signo y asignaciones. Durante el conteo, taps son anticipados de preparación y no respuestas. Conservar una cola de evaluación hasta cerrar la ventana del último ataque, sin cortar su tolerancia por dibujar resultados antes.
- Los silencios se evalúan por ausencia de extras en sus intervalos; un tap legítimamente asociado al ataque vecino dentro de su ventana no se penaliza dos veces. Ligaduras no esperan reataques. Omitir toda respuesta no puede dar dominio alto en una categoría con notas.
- Feedback inmediato sencillo por tap; omisiones cuando cierre la ventana. Resumen general y por categoría con serie de errores, omisiones, extras, silencios y deriva temporal; no sustituir resultados granulares por un promedio.

### Progreso y DDA experimental

- Progresión con categorías desbloqueadas y ejercicios nuevos; práctica libre permite bloqueadas y tempo manual. Repeticiones, simulaciones e interrupciones no desbloquean ni elevan tempo. Las partidas completas nuevas con metrónomo apagado también cuentan; guardar la ayuda para filtrar y comparar, sin exigir practicar ambas variantes.
- Inicialmente R20/R21 desbloqueadas y 60 BPM por categoría. Aumento máximo de 5 BPM y techo de prueba 120 BPM; no reducir tempo ni retirar categorías adquiridas en esta configuración inicial. Falta de datos conserva estado y expone motivo. No se infiere eficacia pedagógica.
- Dos resultados completos elegibles consecutivos de dominio alto por categoría permiten aumento para el próximo ejercicio. Al evaluar mezclas, cada categoría conserva sus propias entradas/racha; el tempo efectivo es el mínimo recomendado. Un éxito por debajo del tempo recomendado de una categoría no basta para aumentarlo; registrar «evidencia a tempo inferior» sin penalizarla por incluir otra nueva.
- Evidencia mínima por categoría: dos apariciones y al menos dos unidades evaluables. Unidad evaluable es ataque esperado o intervalo de silencio de la plantilla. A tiempo y silencio respetado son éxitos; desvíos, omisiones y silencios con extras no son éxitos. Solo contar unidades de las apariciones objetivo, no material de conexión.
- Entradas propuestas en [0,1]: `precision`=unidades exitosas/total; `control`=1−min(1, extras atribuibles/total). Asignar cada extra a una sola aparición mediante posición temporal; extras de conexión se conservan en resumen y diagnóstico general sin regalar aciertos a categorías. No usar latencia del dispositivo como indicador de dominio.
- Pertenencias trapezoidales propuestas (a,b,c,d), con punto decimal: precisión baja `[0,0,0.50,0.70]`, media `[0.50,0.70,0.80,0.90]`, alta `[0.80,0.90,1,1]`; control bajo `[0,0,0.60,0.85]`, alto `[0.70,0.90,1,1]`.
- Salidas sobre [0,1]: bajo `[0,0,0.25,0.45]`, medio `[0.30,0.45,0.55,0.70]`, alto `[0.60,0.80,1,1]`. Reglas: precisión baja→bajo; media+control bajo→bajo; media+control alto→medio; alta+control bajo→medio; alta+control alto→alto. AND/implicación mínimo, agregación máximo y centroide con 1001 puntos, reutilizando el motor vigente. Dominio alto si centroide≥0,75; medio si ≥0,40; bajo en otro caso. Sin área o entradas válidas: no aplicar cambios.
- Una completa no alta con evidencia suficiente y al tempo recomendado reinicia la racha de esa categoría. Práctica, falta de evidencia o fallo técnico no fingen una evaluación. Calcular evidencia de dominio/desbloqueo antes de reiniciar racha al aumentar tempo, y conservar el hito ya ganado. Aplicar decisiones idempotentemente solo junto al guardado del resultado; si falla, exponer pendiente y reintentar sin doble desbloqueo.

Prerrequisitos propuestos: «dominio registrado» significa dos resultados altos elegibles de la categoría a su tempo recomendado. Desbloquear cuando todos los prerrequisitos se cumplan; conservar evidencia y configuración. No exigir llegar a 120 BPM para abrir el catálogo.

| Categorías a desbloquear | Prerrequisitos |
| --- | --- |
| R01 | R20 |
| R02 | R01 |
| R12 | R20 |
| R18 | R12 y R01 |
| R19 | R12 |
| R10, R11 | R21 y R12 |
| R03 | R21 |
| R04, R05, R06 | R03 |
| R07, R08 | R04 y R05 |
| R09 | R01 y R21 |
| R13, R14 | R03, R10 y R11 |
| R15 | R21 |
| R16 | R10, R11 y R09 |
| R17 | R01 y R16 |

Este grafo es una elección técnica/pedagógica provisional para experimentar, no una secuencia validada ni una decisión atribuida al director. Mostrarlo e inspeccionarlo, sin confundir orden de identificadores con dificultad.

### Persistencia y compatibilidad

- Guardar configuración/versiones, partitura/semilla/huella, compás, tempo y procedencia objetivo/conexión, categorías y ocupación, ayudas, origen, datos crudos de taps, evaluación y decisiones de progreso. Partidas completas/interrumpidas diferenciadas y guardado idempotente.
- Perfil de progreso y resultados coordinados; conservar historial previo al cambiar reglas. Exportación, recuperación de originales y reinicio incluyen datos nuevos sin romper Simón, instrumentos, voz ni laboratorio. Si el afinador se implementa antes, conservar sus datos también.
- Historial con filtros por categoría/compás/tempo/origen/ayudas y detalle sobre la partitura exacta. No asumir comparabilidad de resultados si cambia configuración, tempo o ayuda. El texto de «dominio» es operativo y experimental.

## Decisiones de prueba propuestas

### Sesión pública: conducta completa

Acciones de selección/generación/Play/taps/repetición y reloj controlado → partitura, estado, evaluación, resumen, historial y progreso. Controlar audio, almacenamiento, azar y reloj solo en sus límites. Preferir este límite alto y comprobar resultados públicos, sin tests espejo de métodos privados. Antecedentes: sesiones de instrumentos, pruebas de DDA y resiliencia de guardado.

Casos necesarios: selección imposible, varios éxitos y errores por categoría, variación de semilla, no iniciar antes de Play, conteo, tap adelantado/atrasado/extra/omitido, silencios, ligaduras, fronteras de ventanas, deriva sin metrónomo, repetir sin progresar, mezcla a tempo menor, idempotencia y fallo/interrupción.

### Partitura y flujo Android

- Contrato partitura→representación/ataques: duración exacta por compás, barras/ternarias/ligaduras coherentes, cuota/ocurrencias sin duplicados, al menos dos por seleccionada, casos negativos y corpus reproducible. Configuración por versión; no afirmar «todas las células» por aprobar unos ejemplos.
- Verificación visual Android de ejemplos canónicos de cada categoría en 2/4, 3/4 y 4/4 cuando corresponda, renglones, marcador y scroll, más un recorrido completo real con audio y taps. No basar toda la revisión en imágenes pixel a pixel ni solamente en pruebas JVM.
- Banco simulado DDA aislado: alto→aumento, errores→mantenimiento, evidencia insuficiente, mezcla, bloqueo/práctica/repetición, ambos metrónomos, techo120 y duplicado de guardado. Mostrar pertenencias y reglas calculadas, no texto estático.
- Compilación, pruebas pertinentes y lint conforme al proyecto. Prueba física en S22 Ultra documenta Android/app, ruta de audio, entorno, conteo, tempo, marcas táctiles y método de comparación externo para latencia. Las medidas internas y el emulador no acreditan sincronía acústica ni eficacia pedagógica; informar lo pendiente.

## Fuera de alcance

- Micrófono, palmas, instrumentos físicos o evaluación de duración mantenida; entrada por tap.
- Audio del patrón objetivo, demo previa, polifonía, alturas/claves y renderizado completo para Defender.
- Ocho/dieciséis compases, compuestos/irregulares y categorías avanzadas en la primera entrega; quedan previstos para ampliaciones identificables.
- Cambios de tempo en mitad de la ejecución, DDA de tolerancias y revocar desbloqueos.
- Backend, multijugador, grabaciones, diagnóstico pedagógico validado o modificación del protocolo oficial.

## Notas

El protocolo disponible propone Eco rítmico dentro de su selección inicial; los nuevos detalles se registran como decisiones del usuario para el prototipo y propuestas técnicas del agente. No implican aprobación del director, cambio del Gantt o validación de las atribuciones de investigación de la ficha original.

El usuario confirmó la síntesis y parámetros iniciales, y aprobó los puntos de prueba y el plan concreto de seis incrementos mediante «perfecto», autorizando su publicación en GitHub. Las elecciones técnicas se conservan como configuración experimental, sin atribuirles calibración ni validación pedagógica.

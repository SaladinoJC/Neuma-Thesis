# 3. Interval Runner

> Endless runner donde, al acercarse un obstáculo, la animación entra en cámara lenta
> y suenan las dos notas del intervalo en sucesión (melódico, no simultáneo). El
> usuario tiene una ventana fija (ej. 3 segundos) para marcar la opción correcta de
> intervalo entre varias mostradas en pantalla, y así sortear el obstáculo. El tipo de
> obstáculo (uno de salto, uno de agache) ya indica si el intervalo es ascendente o
> descendente — configurable por dificultad: ventana de reacción, cantidad de opciones
> mostradas, "tensión" (grado de confundibilidad) entre las opciones. Dentro de una
> misma partida la dificultad sube gradualmente con el tiempo transcurrido, al estilo
> del juego del dinosaurio de Chrome sin conexión.

**Estado:** DESARROLLADA

## 3.1 Fundamento

**Qué habilidad entrena.** Sigue siendo reconocimiento de intervalos bajo presión de
tiempo, pero el diseño real (cámara lenta + ventana fija de varios segundos + opciones
en pantalla) es, en esencia, un **quiz de selección múltiple con reloj**, envuelto en
la estética y la progresión de un endless runner — no un juego de reflejos puros. Esto
es una diferencia importante respecto de un planteo anterior (que asumía una decisión
de reacción sub-segundo tipo gesto direccional): acá el jugador tiene tiempo real para
*analizar* (¿es una 3ª mayor o menor? ¿una 6ª o una 7ª?), no solo para reaccionar
instintivamente a una dirección. Sigue siendo más exigente que un quiz estático porque
hay un reloj corriendo y el contexto visual de "estoy por chocar", pero la habilidad
cognitiva central es identificación de calidad/tamaño del intervalo, con presión de
tiempo como variable de dificultad, no como el objetivo de entrenamiento en sí mismo.

**Cómo resuelve el riesgo de carga cognitiva extrínseca (RQ3, item 26 del documento de
ideas).** La cámara lenta es, de hecho, la pieza de diseño clave acá: desacopla la
tensión narrativa/visual del runner (que motiva y gamifica) de la exigencia perceptiva
real (que necesita una ventana de análisis genuina, no un reflejo de 300 ms). El juego
"se siente" un runner de acción, pero el instante de evaluación auditiva ocurre en una
burbuja de tiempo casi detenido — mitiga bien la tensión entre estética de acción y
precisión de la tarea auditiva.

**Rol dentro de la arquitectura de la tesis — RESUELTO: sin DDA, minijuego tipo
arcade/high-score.** No usa micrófono ni PDA, es reproducción de audio + UI de
selección + motor de juego. A diferencia de lo que planteaba la primera versión de
este documento, este minijuego **no tiene ninguna interacción con el DDA entre
sesiones** — es deliberadamente más lúdico, con dificultad de arranque y pendiente de
rampa fijas e idénticas para todos los usuarios y todas las partidas. La progresión
que percibe el jugador es su propio **récord personal** (distancia/tiempo
sobrevivido), no un ajuste de dificultad entre sesiones — si el sistema le facilitara
la dificultad según su historial, el jugador no vería reflejada su mejora real al
superar su marca. Su aporte a la arquitectura de la tesis pasa a ser más acotado:
sigue siendo un **tercer tipo de señal de desempeño** (par discreto
acierto+latencia, ver 3.2) que se registra como telemetría de validación pura, sin
alimentar ningún componente adaptativo.

## 3.2 Base técnica

**Reproducción melódica, no armónica.** Las dos notas suenan **en sucesión** — nota 1,
silencio breve, nota 2 — igual que en la Idea 1 (intervalo melódico) y coherente con
cómo realmente se entrena oído de intervalos en conservatorio (rara vez se pide
identificar un intervalo armónico simultáneo salvo en ejercicios avanzados). Esto
simplifica el motor de síntesis respecto de lo que hubiera exigido un intervalo
armónico real (dos osciladores/samples sonando a la vez sin batir de forma
perceptible).

**Ya no hay una exigencia dura de sincronía audio-visual a nivel de milisegundos.**
Al introducir la cámara lenta con una ventana de decisión de varios segundos, el
problema de calibración de offset audio-visual (relevante en juegos de ritmo con
ventanas de <200 ms) deja de ser crítico: alcanza con que el audio termine de sonar
*dentro* de la ventana de cámara lenta, no en un instante exacto. Se mantiene sí la
necesidad de dos disparadores bien definidos en el motor de juego:
1. **Trigger de entrada a cámara lenta**: a qué distancia/tiempo del obstáculo se
   activa (define cuánto dura, en tiempo de juego "real", la desaceleración).
2. **Trigger de fin de ventana de respuesta**: temporizador fijo (ej. 3s de tiempo de
   usuario, independiente del framerate) que corre en paralelo a la cámara lenta —
   conviene que sea un timer de reloj real, no atado a frames, para que sea consistente
   entre dispositivos con distinto rendimiento gráfico.

**Tipo de métrica de desempeño:** par discreto
`(correcto: bool, tiempo_de_respuesta_ms)` — sostiene el punto de arquitectura de que
el DDA de la tesis necesita ser un **componente genérico** que acepte distintos tipos
de señal de desempeño (serie continua de cents en Idea 1/2, par discreto
acierto+latencia acá), no uno acoplado únicamente a varianza en cents.

**Un único motor de dificultad: rampa intra-partida fija, sin DDA entre sesiones —
RESUELTO.** Dentro de una misma corrida, la dificultad sube de forma determinista con
el tiempo/distancia recorrida (tipo Dino de Chrome), independientemente del desempeño
puntual del usuario en esa partida. **No hay un segundo motor de dificultad
entre-sesiones**: la velocidad inicial y la pendiente de la rampa son las mismas para
todos los usuarios y todas las partidas — no se calibran según historial. Se descarta
así el planteo original de "dos motores complementarios" (rampa intra-partida + DDA
entre-partidas calibrando el punto de arranque): con DDA entre-partidas, el usuario no
vería reflejada su propia mejora al superar su récord, porque el sistema le estaría
facilitando la dificultad de arranque en la misma medida que mejoró. Ver 3.1 para el
rol que esto le deja a la idea dentro de la arquitectura general.

**Reutilización con la Idea 2 (distractor tímbrico) — RESUELTO: timbre fijo por
partida, no randomizado por ronda.** Se sortea un timbre del banco de la Idea 2 al
empezar la corrida y se mantiene igual toda la partida — da variedad entre partidas
sin sumar una variable de dificultad más dentro de una misma corrida, que ya
acumula ventana de tiempo + cantidad de opciones + tensión.

## 3.3 Diseño

**No hace falta una tercera acción ("girar").** Con el obstáculo mismo indicando la
dirección (obstáculo de salto = intervalo ascendente, obstáculo de agache =
descendente), la pregunta real que resuelve el jugador no es "¿para qué lado va el
intervalo?" sino "¿cuál es exactamente, entre las opciones mostradas?" — dos tipos de
obstáculo alcanzan, ya no sobra una acción sin categoría asociada.

**Loop de una ronda:**
1. Se sortea un intervalo (dirección + calidad); el timbre es el sorteado al inicio
   de la partida (fijo durante toda la corrida, ver 3.2).
2. Aparece a distancia el obstáculo correspondiente al tipo de dirección (salto o
   agache), visualmente distinguible desde lejos.
3. Al llegar a cierta distancia de decisión, la animación entra en cámara lenta.
4. Suenan las dos notas del intervalo en sucesión.
5. En pantalla aparecen varias opciones de intervalo (multiple choice) — la cantidad y
   la "tensión" (confundibilidad) de las opciones respecto del intervalo correcto son
   parámetros de la rampa intra-partida (ver abajo).
6. El usuario tiene una ventana fija de tiempo real (ej. 3s) para tocar la opción
   correcta.
7. Si acierta: el personaje ejecuta la acción (salta/agacha) y sortea el obstáculo, la
   cámara vuelve a velocidad normal. **Si no acierta o se agota el timer: pierde una
   vida / termina la partida** (RESUELTO, ver 3.4).
8. Se registra `(correcto, tiempo_de_respuesta_ms, intervalo, timbre, cantidad_opciones,
   tensión_opciones)` como telemetría de tesis — no alimenta ningún ajuste de
   dificultad (ver 3.1).
9. Se actualiza únicamente la rampa intra-partida (por tiempo/distancia transcurrida
   en la corrida actual). Al finalizar la partida (por fallo), se compara la marca
   alcanzada contra el récord personal del usuario.

**Parámetros de dificultad explícitos (todos gobernados solo por la rampa
intra-partida, fija e idéntica para todos los usuarios):**
- Ventana de reacción/respuesta (segundos disponibles para elegir).
- Cantidad de opciones de intervalo mostradas.
- "Tensión" (confundibilidad) de las opciones respecto del intervalo correcto —
  **RESUELTO:** tabla inicial teórica basada en distancia en semitonos entre el
  intervalo correcto y cada distractor (ej. 3ª mayor vs. 3ª menor = 1 semitono =
  alta tensión), ajustable más adelante con datos reales de confusión acumulados en
  la telemetría de este y otros minijuegos.
- (Implícito de la rampa Dino-style) velocidad general de acercamiento a obstáculos,
  que indirectamente comprime cuánto dura la cámara lenta o cada cuánto aparecen
  obstáculos nuevos.

**Capa visual:** el riesgo de carga cognitiva extrínseca sigue siendo válido para la
fase de "correr" (estética, fondos, personaje), pero la fase crítica de evaluación
ocurre en cámara lenta con foco claro en las opciones — conviene mantener la fase de
decisión (paso 3-6) visualmente limpia (opciones grandes, legibles, sin elementos
compitiendo).

## 3.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver 3.1–3.3,
marcadas **RESUELTO**):

- Fallar (elegir mal o agotar el timer) = pierde una vida / termina la partida — 3.3.
- "Tensión": tabla teórica inicial por distancia en semitonos, ajustable después con
  datos reales de confusión — 3.3.
- Sin DDA entre sesiones: rampa intra-partida fija e idéntica para todos, sin
  calibración por historial; la progresión percibida es el récord personal — 3.1/3.2.
- Timbre fijo por partida, no randomizado por ronda — 3.2.

**No quedan decisiones de diseño abiertas para esta idea.** Los únicos valores sin
fijar son de tuning (velocidad exacta de la rampa, duración de la ventana de
respuesta, valores concretos de la tabla de tensión por semitono), a calibrar
empíricamente durante el desarrollo.

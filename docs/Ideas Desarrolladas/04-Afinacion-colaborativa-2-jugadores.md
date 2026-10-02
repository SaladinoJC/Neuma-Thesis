# 4. Afinación colaborativa (2 jugadores)

> Cada uno canta una nota de un intervalo objetivo sin verse, el sistema muestra qué
> tan afinado quedó el intervalo combinado — entrena escucha "en conjunto", no solo
> individual.

**Estado:** DESARROLLADA

## 4.1 Fundamento

**Qué habilidad entrena, y por qué es un hueco real.** Todas las ideas desarrolladas
previamente (1-3) son de escucha/producción **individual**. Esta es la primera que
apunta a una habilidad genuinamente distinta: afinar la propia voz *en relación a la de
otra persona en tiempo real*, que es exactamente lo que hace un cantante de coro o un
instrumentista de cámara — escuchar el resultado combinado, no solo el propio aporte.
Ninguna de las apps comerciales relevadas en la investigación (todas de uso individual
con micrófono) cubre esto. Es un hueco de mercado real, no solo de contenido.

**Distinción importante respecto de la sección H del brainstorming (multiplayer/
social, ideas #24-25):** esta idea no es competitiva (no hay "quién responde más
rápido" ni leaderboard) — es *cooperativa*: ambos jugadores tienen éxito o fracasan
juntos, según qué tan afinado quede el intervalo combinado. Vale la pena mantenerla
categorizada aparte de la lógica de duelo de la sección H.

**Alcance de tesis — RESUELTO: fuera del MVP, trabajo futuro / experimento
secundario.** Mismo estatus que la Idea 5 (instrumento): no forma parte del alcance
central de la tesis. Al sacarla del MVP, se quita también la presión de forzar un
diseño simplificado de un solo dispositivo — el diseño que se documenta abajo es el
diseño real/objetivo (multi-dispositivo), no un workaround para encajar en las
restricciones del MVP.

## 4.2 Base técnica

**Por qué nunca se transmite ni mezcla audio — solo el valor numérico de pitch ya
calculado.** Aunque esta idea ya no necesita encajar dentro de las restricciones del
MVP (queda fuera de él, ver 4.1), la arquitectura de "transmitir solo un escalar"
sigue siendo la decisión técnica correcta, por dos razones que se mantienen válidas
incluso como trabajo futuro:

1. **Streaming de audio en tiempo real exige latencia sub-15ms** para no sonar
   desincronizado — una complejidad de sincronización real que no aporta nada al
   diseño de esta idea (lo que importa es el resultado combinado, no escuchar la voz
   cruda del otro jugador).
2. **Si ambos cantaran a la vez frente a un solo micrófono**, el resultado sería una
   señal polifónica mezclada — la extracción de tono en entornos polifónicos es un
   problema no resuelto por el pipeline monofónico que sustenta toda la Idea 1. Con
   dispositivos separados, cada uno captura su propia voz de forma limpia y
   monofónica — el problema ni siquiera se plantea.

**La solución: cada dispositivo captura y procesa la voz de su propio jugador de
forma completamente local**, con el mismo pipeline monofónico de la Idea 1 (Oboe →
YIN/multiplexor → F0 en Hz/cents). Lo único que cruza la red es **un escalar de pitch
actualizado varias veces por segundo** (ej. 20-50 Hz), no audio crudo. Esto cambia la
naturaleza del problema:

- Un stream de audio en tiempo real exige latencia sub-15ms para no sonar
  "robotizado" o desincronizado — es exactamente lo que el MVP descarta por
  justificados motivos de rigor científico.
- Un stream de **un float** (la lectura de pitch del otro jugador) para alimentar un
  **medidor visual compartido** (no audio) tolera muchísima más latencia (100-200ms es
  perfectamente aceptable para una aguja que se mueve en pantalla) sin invalidar la
  experiencia ni la medición.
- Cada jugador **nunca escucha la voz del otro en vivo a través de la app** — solo ve
  (o, como variante, escucha un tono sintetizado puro reconstruido a partir del valor
  de F0 recibido, no la voz real) la posición relativa del otro. Esto de hecho encaja
  mejor todavía con el "sin verse" del enunciado original — ni se ven ni se escuchan
  el uno al otro directamente, solo el resultado combinado que muestra el sistema.

**Qué mide exactamente el "intervalo combinado" — y por qué no hace falta afinación
absoluta de ninguno de los dos:** el error relevante no es "qué tan afinado está cada
uno respecto a un LA 440 absoluto", sino la **distancia en cents entre las dos alturas
que están sonando en simultáneo**, comparada contra el tamaño del intervalo objetivo.
Es decir: `error_combinado = |diferencia_cents(pitch_A, pitch_B) - tamaño_intervalo_objetivo|`.
Esto es coherente con la recomendación de pitch relativo ya adoptada en la Idea 1 (no
se necesita calibrar el rango vocal absoluto de nadie), y es matemáticamente más
interesante que una simple suma de errores individuales: si ambos jugadores se
desvían la misma cantidad y en la misma dirección de su nota "ideal", el intervalo
combinado puede seguir estando perfectamente afinado — lo que se evalúa es la
consonancia final, no la perfección individual. Esto es, en sí mismo, un buen ángulo
de validación experimental para la tesis (una métrica de "afinación de conjunto" que
no es la suma trivial de las afinaciones individuales).

## 4.3 Diseño

**Multi-dispositivo — RESUELTO: diseño objetivo, no la variante de un solo
dispositivo.** Cada jugador tiene su propio dispositivo, conectados por red — ya no
se documenta la variante de "un solo dispositivo, turnos con auriculares" como
recomendación principal (esa era un workaround para evitar la complejidad de red
dentro de las restricciones del MVP, que ya no aplican).

**Transporte — RESUELTO: peer-to-peer en red local (misma WiFi), sin relay
server.** Dos personas jugando juntas, cada una con su propio dispositivo en la misma
red local, es el escenario natural para una idea cooperativa como esta — de hecho es
el mismo escenario físico que la variante de un solo dispositivo, solo que ahora cada
jugador tiene el suyo. Esto evita necesitar relay server (costo de mantenimiento fuera
del alcance típico de una tesis) o resolver NAT traversal para conexiones a
distancia — ese caso (jugadores en ubicaciones distintas) queda fuera incluso de este
documento de trabajo futuro.

**Variante de sincronización — RESUELTO: (a) turnos con anclaje ("lock-then-match")
como diseño principal.** El Jugador A canta primero y sostiene su nota; el sistema
fija ese F0 como referencia (aunque A no haya estado perfectamente afinado respecto
de nada externo, ya es la referencia real para esta ronda). El Jugador B, escuchando
un tono sintetizado puro reconstruido a partir del F0 de A (no la voz real de A),
debe cantar el intervalo objetivo por encima/debajo de esa referencia mientras A
sigue sosteniendo. Solo hay un flujo de datos "en vivo" necesario (el F0 de A hacia
el dispositivo de B). Se descarta, para este documento, adoptar directamente la
variante **(b) simultáneo real** ("ambos a la vez", sin referencia fija, ajuste
mutuo en vivo — el verdadero afinar en conjunto de un coro) como diseño principal:
sigue siendo más simple razonar y construir primero la versión con turnos, incluso
sin la presión de tiempo de tesis que originalmente motivó esa elección. (b) queda
como posible modo avanzado, no desarrollado acá.

**Roles — RESUELTO: alternables entre rondas, no fijos.** Cada ronda (o cada
partida) se invierte quién ancla y quién ajusta, para que ambos jugadores entrenen
las dos mitades de la habilidad cooperativa (sostener una referencia vs. ajustarse a
una ajena). El campo `quién_cantó_primero`, que el loop ya registra, es el que
captura esta alternancia — no hace falta un campo nuevo, solo asegurar que
efectivamente varíe entre rondas.

**Loop de una ronda:**
1. Se sortea un intervalo objetivo y se determina quién ancla esta ronda (rol
   alternado respecto de la ronda anterior).
2. El jugador ancla canta y sostiene una nota (cualquiera, dentro de su rango
   cómodo — no hay pitch objetivo absoluto).
3. El sistema captura ese F0 localmente y lo transmite como escalar (P2P, red local)
   al dispositivo del otro jugador.
4. El otro jugador escucha un tono de referencia sintetizado a partir de ese F0 (no
   la voz real) y canta intentando el intervalo objetivo por encima/debajo.
5. El sistema calcula `error_combinado` en tiempo real mientras sostiene, y lo
   muestra en un medidor compartido.
6. Al finalizar la ronda se registra `(intervalo_objetivo, pitch_ancla, pitch_ajuste,
   error_combinado_final, quién_cantó_primero)`.

**Rol del DDA:** mismo componente genérico ya señalado en las Ideas 1 y 3 — acá el eje
de dificultad natural es el tamaño/consonancia del intervalo objetivo y la tolerancia
del `error_combinado`, alimentado por el mismo tipo de señal continua (serie de cents)
que la Idea 1, solo que aplicada a un valor derivado de dos fuentes en vez de una.

**Más de 2 jugadores (acordes) — RESUELTO: mencionado como extensión natural, sin
desarrollar en este documento.** Generalizar de intervalo (2 notas) a acorde (3+ notas,
cada jugador cantando una voz) no es trivial: la métrica de "qué tan afinado está un
acorde" es cualitativamente más compleja que una distancia en cents entre dos alturas.
Si alguna vez se retoma, merece su propia sesión de diseño — este documento mantiene
el foco en 2 jugadores/intervalos.

## 4.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver 4.1–4.3,
marcadas **RESUELTO**):

- Alcance de tesis: fuera del MVP, trabajo futuro/experimento secundario (mismo
  estatus que la Idea 5) — 4.1.
- Diseño objetivo: multi-dispositivo, no la variante de un solo dispositivo con
  turnos (esa era un workaround del MVP que ya no aplica) — 4.1/4.3.
- Transporte: peer-to-peer en red local, sin relay server; conexión a distancia
  queda fuera incluso de este documento — 4.3.
- Variante de sincronización: turnos con anclaje (a) como diseño principal;
  simultáneo real (b) queda como modo avanzado no desarrollado — 4.3.
- Roles alternables entre rondas, no fijos — 4.3.
- Más de 2 jugadores (acordes): mencionado como extensión futura, sin desarrollar
  acá — 4.3.

**No quedan decisiones de diseño abiertas para esta idea.**

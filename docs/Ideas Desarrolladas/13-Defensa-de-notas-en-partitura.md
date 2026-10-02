# 13. Defensa de notas en partitura (multi-clave)

> Vuelta de tuerca al ítem 15 original ("reconocimiento de notas en distintas
> claves — básico, ya existe en el mercado"). Idea hermana de la Idea 12,
> mismo motor arcade de defensa, adaptado a una nota individual en vez de un
> acorde: el usuario lee la nota en el pentagrama (en la clave vigente) y
> "escribe" su nombre + alteración con un teclado dedicado, antes de que el
> enemigo llegue a la base.

**Estado:** DESARROLLADA

## 13.1 Fundamento

**Por qué es una idea separada de la Idea 12, no un modo del mismo juego.**
Mismo criterio que ya separó la Idea 1 (voz) de la Idea 5 (instrumento): el
motor arcade (spawn, movimiento, vidas, DDA de dos capas) se comparte casi
por completo, pero el **eje de curriculum es de naturaleza distinta** — acá
no es calidad de acorde, es clave + líneas adicionales + alteraciones (ver
13.2). Mezclar ambos ejes de dificultad en una sola gobernanza de DDA
complicaría la ventana de desbloqueo sin necesidad; mantenerlos separados
preserva la simplicidad y comparabilidad de cada curriculum, igual
justificación que ya usaron las Ideas 1/5.

**Por qué sigue siendo una vuelta de rosca real frente al mercado.** Mismo
argumento que la Idea 12: la inmensa mayoría de apps de lectura de notas
evalúan por selección múltiple entre opciones. Acá el jugador debe
**recordar y escribir** el nombre de la nota con un teclado dedicado de
primitivas (no elegir de una lista), bajo presión de tiempo real en formato
arcade — mismo salto de reconocimiento pasivo a recall activo ya validado en
la Idea 12.

**Aporte arquitectónico — reutiliza el motor de la Idea 12 sin componentes
nuevos, pero extiende su validación empírica en dos sentidos:**
1. Un **curriculum de ejes distintos** (clave/líneas adicionales/alteraciones
   en vez de calidad de acorde), que pone a prueba si el motor DDA de dos
   capas generaliza a otro tipo de contenido sin rediseño.
2. Una **política de desambiguación distinta ante duplicados** (ver 13.2):
   a diferencia de la Idea 12 (que prohíbe duplicados), acá se permiten
   enemigos con la misma respuesta esperada en pantalla a la vez, resueltos
   por proximidad a la base — un caso de matching que la Idea 12 no necesitó
   resolver.

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento
secundario, fuera del MVP, mismo criterio que las Ideas 5, 7, 11 y 12.** No
toca el pipeline de captura de voz ni el multiplexor DSP↔ML. Es catálogo de
producto adicional y validación empírica extra del motor DDA de dos capas ya
introducido en la Idea 12, no un componente arquitectónico nuevo.

## 13.2 Base técnica

**Sin pipeline de audio de entrada** (igual que la Idea 12) — no hay
micrófono, ring buffer, PDA ni multiplexor DSP↔ML. Reutiliza el motor arcade
completo de la Idea 12 (spawn, movimiento, vidas, DDA de dos capas), con el
contenido y la lógica de matching adaptados a nota individual.

```
Selección de clave vigente + nombre de nota + alteración (curriculum
   persistente, ventana móvil por combinación clave+rango+alteración)
   → Renderizado en pentagrama miniatura (una sola nota, con símbolo de
        clave visible cuando el nivel de mezcla de claves está desbloqueado)
   → Spawn del enemigo (misma curva de aparición de la Idea 12) → avanza
        hacia la base a la velocidad vigente (rubber-banding intra-partida)
   → Usuario arma nota con teclado dedicado (nombre A-G/Do-Si + alteración
        ♯/♭/♮, sin octava) → Enter
   → Matching contra enemigos vivos: si hay más de uno con la misma
        respuesta esperada, se mata al más próximo a la base
   → Señal de éxito binaria (capa persistente) + tiempo_hasta_matar_ms
        (capa intra-partida)
   → Motor DDA de dos capas (mismo mecanismo de la Idea 12)
```

**Teclado dedicado — RESUELTO: nombre de nota (A-G) + modificador de
alteración (♯/♭/♮), sin octava.** Lo que se entrena es identificar la nota
por su posición en el pentagrama según la clave vigente, no memorizar el
registro absoluto — mismo criterio de pitch relativo ya aplicado en todas las
demás ideas de la tesis (1, 5, 6, 10), aplicado acá al nombre de nota en vez
de a la frecuencia. La alteración mostrada en la partitura es la que hay que
escribir tal cual (sin aceptar el enarmónico equivalente, para no introducir
ambigüedad de "qué contaba como correcto").

**Curriculum de dificultad — RESUELTO: dos ejes, orden fijo, mezcla de claves
como nivel final:**
1. **Progresión dentro de una clave (orden fijo):** clave de sol, notas
   dentro del pentagrama (sin líneas adicionales) → clave de sol con 1-2
   líneas adicionales arriba/abajo → alteraciones (♯/♭) dentro de clave de
   sol → se repite la misma progresión completa para clave de fa.
2. **Mezcla de claves (nivel final desbloqueable):** una vez superada la
   ventana de éxito en clave de sol y en clave de fa por separado, se
   desbloquea un modo donde los enemigos alternan de clave al azar, cada uno
   mostrando su propio símbolo de clave junto al pentagrama — el nivel más
   exigente, fiel al objetivo original del ítem 15 ("distintas claves", no
   una clave fija memorizada de antemano).
3. **Clave de do (alto/tenor) — fuera del alcance inicial.** Mucho menos
   frecuente en el repertorio general; queda mencionada como extensión
   futura, no como parte del curriculum base.

**Manejo de duplicados — RESUELTO: se permiten, ambigüedad resuelta por
proximidad a la base (a diferencia de la Idea 12).** Como la respuesta no
incluye octava (teclado dedicado, arriba), dos enemigos con distinta posición
en el pentagrama pero el mismo nombre+alteración son indistinguibles para el
matching. En vez de prohibir duplicados (como en la Idea 12), acá se
permiten: si el Enter coincide con más de un enemigo vivo, **se mata al que
esté más próximo a la base** — resuelve la ambigüedad sin restringir la
generación de contenido, y le agrega al juego una dimensión extra de
priorización bajo presión (con varios candidatos iguales en pantalla, importa
cuál está por caer).

**Resto del motor arcade — reutilizado sin modificaciones de la Idea 12:**
3 vidas, sin límite de tiempo fijo, curva de spawn creciente con el tiempo
transcurrido, intento fallido cuenta para precisión sin costar vida, DDA de
dos capas (persistente por combinación de clave+rango+alteración con ventana
móvil de éxito binario, intra-partida con rubber-banding de velocidad según
`tiempo_hasta_matar_ms` reciente).

## 13.3 Diseño

**Símbolo de clave junto al pentagrama — visible siempre, no solo en el nivel
de mezcla.** Aunque durante la progresión de una sola clave el símbolo es
redundante (todos los enemigos comparten la misma clave), mostrarlo desde el
principio evita que el jugador aprenda a ignorar esa parte de la partitura —
cuando se desbloquea el nivel de mezcla, el símbolo ya es un elemento familiar
de la lectura, no una sorpresa nueva de UI.

**Gobernanza — mismo mecanismo de dos capas que la Idea 12, aplicado a la
combinación clave+rango+alteración en vez de calidad de acorde:**
- **Capa persistente (curriculum):** ventana móvil de éxito binario
  (`matado antes de tocar la base` sí/no) por combinación específica de
  clave+rango(dentro/con líneas adicionales)+alteración, con el orden fijo
  de 13.2. Override manual disponible (`origen: manual|dda`).
- **Capa intra-partida (velocidad):** rubber-banding en tiempo real dentro de
  la partida, arrancando de una velocidad base calibrada según el nivel ya
  alcanzado en el curriculum persistente, sin persistir entre partidas.

**Loop de partida:**

1. Arranca la partida con velocidad base calibrada según el nivel de
   habilidad ya alcanzado en el curriculum persistente.
2. Los enemigos aparecen según la curva de spawn, con clave+rango+alteración
   sorteados entre lo desbloqueado (o clave mezclada, si ese nivel ya está
   desbloqueado) y nota aleatoria dentro de esos parámetros — se permiten
   nombres+alteraciones repetidos entre enemigos vivos.
3. Cada enemigo se renderiza como pentagrama miniatura con la nota y el
   símbolo de clave correspondiente, avanzando hacia la base a la velocidad
   vigente.
4. El usuario arma una nota con el teclado dedicado y presiona Enter:
   - Coincide con uno o más enemigos vivos → se mata al más próximo a la
     base; se registra `(clave, nombre_nota, alteracion, exito=true,
     tiempo_hasta_matar_ms)`.
   - No coincide con ninguno → `intento_fallido`, sin costo de vida, teclado
     se limpia.
5. Si un enemigo toca la base sin ser destruido → se pierde una vida; se
   registra `(clave, nombre_nota, alteracion, exito=false)` para la ventana
   persistente; el enemigo desaparece.
6. Tras cada resultado, el DDA ajusta la capa persistente (ventana móvil por
   combinación, desbloqueo del siguiente paso del curriculum si corresponde)
   y la capa intra-partida (velocidad de los próximos enemigos).
7. La partida continúa hasta perder la tercera vida (`game over`).

**Datos a registrar por aparición de enemigo:**
`{timestamp, partida_id, clave, nombre_nota, alteracion, rango (dentro_pentagrama|lineas_adicionales),
modo_clave (fija|mezclada), resultado (matado|llego_a_base),
tiempo_hasta_matar_ms (null si llegó a la base), velocidad_movimiento_vigente,
nivel_dificultad_persistente_antes, nivel_dificultad_persistente_despues}`

**Datos a registrar por partida (agregado):**
`{partida_id, timestamp_inicio, duracion_total_ms, vidas_perdidas,
enemigos_matados, intentos_fallidos_totales, combinaciones_en_juego,
velocidad_inicial, velocidad_final}`

## 13.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
13.1–13.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Encuadre: idea separada de la Idea 12 (no un modo del mismo juego) — mismo
  criterio que separó las Ideas 1/5, por tener un eje de curriculum de
  naturaleza distinta — 13.1.
- Teclado dedicado: nombre de nota (A-G) + alteración (♯/♭/♮), sin octava —
  mismo criterio de pitch relativo que el resto de la tesis — 13.2.
- Curriculum: dos ejes con orden fijo — progresión dentro de clave de sol
  (sin líneas adicionales → con líneas adicionales → alteraciones), repetida
  para clave de fa, con mezcla de claves como nivel final desbloqueable;
  clave de do fuera del alcance inicial — 13.2.
- Manejo de duplicados: a diferencia de la Idea 12, se permiten; ambigüedad
  resuelta matando al enemigo más próximo a la base — 13.2.
- Resto del motor arcade (vidas, spawn, intento fallido, DDA de dos capas):
  reutilizado sin modificaciones de la Idea 12 — 13.2.
- Símbolo de clave: visible siempre junto al pentagrama, no solo en el nivel
  de mezcla — 13.3.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que las Ideas 5, 7, 11 y 12) — 13.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito por combinación
clave+rango+alteración; curva de spawn; magnitud del rubber-banding de
velocidad; cantidad concreta de líneas adicionales del paso "con líneas
adicionales" (1 vs. 2).

# 6. Constructor de acordes por voz (no piano)

> En vez de tocar teclas, el usuario canta las notas de un acorde una por una
> (arpegiado) y el sistema arma el acorde en tiempo real con lo cantado —
> reutiliza el mismo motor de pitch-tracking que la Idea 1.

**Estado:** DESARROLLADA

## 6.1 Fundamento

**Por qué la captura sigue siendo monofónica, y por qué eso importa.** La
sección 4.3 de `InvestigacionSistematica.md` descarta explícitamente del MVP la
"Extracción Directa de Tono Vocal en Entornos Polifónicos" (voces simultáneas
superpuestas, tipo separación de fuentes sobre una base orquestal): requeriría
modelos pesados (RMVPE) inviables en el dispositivo o inferencia en la nube con
latencia inaceptable. Esta idea **no cae en ese descarte** porque, aunque el
resultado final es un acorde (objeto armónico de 3+ notas), la captura sigue
siendo estrictamente secuencial: una nota sostenida por vez, el mismo tipo de
señal monofónica cuasi-periódica que ya procesa el pipeline de la Idea 1. El
acorde se arma **en el sistema**, acumulando notas ya detectadas una a una — no
**en el micrófono**, mezclando voces. Esta distinción es la que mantiene la
idea dentro del alcance de viabilidad ya validado, en vez de abrir el frente de
investigación de separación polifónica que la propia investigación descarta.

**Pedagógico.** Los ítems 14-16 del brainstorming (reconocimiento visual de
acordes, selección múltiple en piano virtual) son, según el propio documento,
"básicos del mercado" — apps ya existentes evalúan reconocimiento pasivo de
acordes por selección. Esta idea, igual que la Idea 1 con intervalos, mueve el
ejercicio de **reconocimiento pasivo a producción activa**: el usuario no
selecciona el acorde correcto entre opciones, lo construye con su propia voz,
nota por nota, y el sistema valida si lo que produjo corresponde efectivamente
a la calidad de acorde pedida. Esto es más exigente que cantar un intervalo
aislado (Idea 1): exige sostener una relación armónica de 3 notas simultáneas
*en la cabeza* mientras se cantan de a una, coherente con la habilidad de
"dictado armónico activo" que en conservatorio es de nivel más avanzado que el
simple dictado de intervalos.

**Por qué esta idea aporta algo nuevo a la arquitectura de tesis (no es solo
una variante de la Idea 1).** A diferencia de la Idea 5 (instrumento en vez de
voz, mismo pipeline sin cambios, catalogada como trabajo futuro/experimento
secundario), esta idea introduce un componente de arquitectura genuinamente
nuevo: un **segmentador híbrido de eventos de nota** sobre la salida ya
existente del pipeline (ver 6.2), necesario porque acá, a diferencia de la
Idea 1, una sola ronda contiene 3 notas consecutivas que hay que separar en el
tiempo, no una sola nota sostenida. También extiende la validación empírica
del DDA a una tercera unidad de evaluación (acorde de 3 notas con clasificación
categórica por intervalo, ver 6.2), sumándose a la serie continua de cents
(Idea 1/2) y al triple discreto acierto+latencia (Idea 5).

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP.** No requiere una fuente
tímbrica nueva, no rompe el control experimental (sigue siendo voz,
monofónica), y el segmentador híbrido es un aporte de arquitectura legítimo y
acotado, no un frente de investigación nuevo que ponga en riesgo el
cronograma.

## 6.2 Base técnica

**Pipeline: mismo motor de captura y PDA de la Idea 1, con una máquina de
estados de segmentación de notas encima.**

```
Micrófono (Oboe, AAudio/OpenSL ES)
   → Ring buffer lock-free (igual que Idea 1)
   → Worker de PDA: YIN vectorizado + multiplexor DSP↔ML en cascada
        (idéntico a 1.2, sin modificar)
   → Vector F0 (Hz) + envolvente RMS, frame a frame
   → Segmentador híbrido de eventos de nota (NUEVO, ver abajo)
        → Máquina de estados: esperando_raiz → esperando_3ra → esperando_5ta
          → acorde_completo
   → Motor DDA (mismo componente genérico de Ideas 1/3/4/5)
```

**Segmentador híbrido de eventos de nota — RESUELTO: robusto al estilo de
canto del usuario, sin exigirle un modo particular.** No se le pide al usuario
que cante "con pausa" o "en legato" — el sistema tiene que detectar el cambio
de nota en cualquiera de los dos casos, y de cualquier estilo intermedio,
transparentemente. El corte de una nota y apertura de la siguiente se dispara
ante **el primero de dos eventos que ocurra**, evaluados sobre la misma salida
F0+RMS que ya produce el pipeline, sin agregar un motor de PDA nuevo:

- **Disparador A — silencio:** la envolvente RMS cae por debajo de un umbral
  durante una ventana mínima de X ms (usuario cantó con pausa entre notas).
- **Disparador B — salto de F0:** la desviación en cents entre frames
  consecutivos supera un umbral Δ dentro de una ventana corta de pocos frames
  (usuario cantó en legato, sin cortar la voz).

**Resolución del caso intermedio (glissando/portamento):** un deslizamiento
continuo de F0 (rampa, ni silencio ni salto abrupto) no dispara ninguno de los
dos eventos por sí solo. Mientras la pendiente de F0 no se estabiliza, el
segmentador no considera que hay una nota nueva todavía — es una zona de
transición, no un evento. Recién cuando el F0 se estabiliza en un nuevo valor
durante la misma ventana de "nota sostenida válida" ya definida en 1.2, se
cierra la transición y arranca la medición de la nota siguiente. No se agrega
un tercer disparador para el glissando: se resuelve reutilizando el criterio
de estabilización que ya existe.

Los umbrales concretos (ventana de silencio en ms, Δ de cents para salto
abrupto, ventana de frames) son valores de tuning, no decisiones de diseño —
mismo tratamiento que los umbrales de 1.2/1.3.

**Métrica de evaluación — RESUELTO: sin promedio único, clasificación
categórica por intervalo.** Cada ronda produce 3 mediciones de CA individuales
(raíz, 3ra, 5ta respecto de la raíz, pitch relativo igual que en 1.2 — sin
exigir frecuencia absoluta) que se registran como serie, no se promedian en un
score compuesto. A partir de los intervalos raíz→3ra y raíz→5ta ya medidos, se
deriva una **clasificación categórica de la calidad de acorde percibida**:

- Raíz→3ra dentro de tolerancia de 3ra mayor (400 cents ± tolerancia) →
  contribución "mayor"; dentro de tolerancia de 3ra menor (300 cents ±
  tolerancia) → contribución "menor"; fuera de ambas ventanas →
  "irreconocible".
- Raíz→5ta dentro de tolerancia de 5ta justa (700 cents ± tolerancia) →
  "justa"; fuera de eso → "alterada/error" (relevante recién cuando se
  introduzcan disminuido/aumentado, que alteran la 5ta).
- El resultado de la ronda es la tupla `(calidad_percibida, CA_raiz, CA_3ra,
  CA_5ta)`, comparada contra la calidad objetivo pedida.

Se prefiere esto a un promedio simple porque diluiría información que sí le
importa al DDA y a la telemetría de tesis: un error grande específicamente en
la 3ra (que define mayor/menor) es un dato cualitativamente distinto a un
error del mismo tamaño en la 5ta, y un promedio los trata igual.

**Los umbrales de tolerancia por intervalo son valores de tuning**, no
decisiones de diseño, igual criterio que en 1.2/1.3.

## 6.3 Diseño

**Escalera de 3 niveles de andamiaje — RESUELTO: misma escalera de la Idea 1,
adaptada a 3 notas en vez de 2.**

- **Nivel 1 — Imitación:** el sistema reproduce la tríada completa (bloque o
  arpegiada) y el usuario la reproduce cantando raíz→3ra→5ta.
- **Nivel 2 — Ancla dada:** el sistema reproduce solo la raíz y nombra la
  calidad del acorde ("cantá un acorde mayor desde acá"); el usuario canta 3ra
  y 5ta a partir de esa raíz.
- **Nivel 3 — Producción libre:** el sistema solo nombra la calidad del
  acorde, sin audio; el usuario elige su propia raíz y canta las 3 notas.

**Gobernanza del nivel — reutiliza tal cual el mecanismo de 1.3**: DDA por
promedio móvil de éxito sobre ventana de N rondas, **por calidad de acorde
específica** (no global), con override manual disponible y etiquetado
`origen: manual|dda` (las rondas manuales no cuentan para el desbloqueo). No
se reinventa el mecanismo de gobernanza — el aporte arquitectónico de la tesis
en este eje ya está cerrado en la Idea 1.

**Manejo de error a mitad de ronda — RESUELTO: no se corta, feedback agregado
al final.** Si una nota intermedia (típicamente la 3ra) sale desafinada al
punto de cambiar la calidad percibida del acorde, el sistema no interrumpe la
ronda para pedir que se repita esa nota — deja completar las 3 notas y da el
feedback recién al final, de forma agregada (tupla `calidad_percibida` +
3 CA). Razones:
- Coherente con el patrón ya establecido en la Idea 1: el CA se congela al
  cerrar la nota, y el feedback es post-ronda, no in-the-moment — evita
  exponer el mecanismo interno de ajuste (RQ3, seductive details).
- Cortar a mitad de un acorde rompe el objetivo pedagógico real: la habilidad
  de sostener y completar una relación armónica de memoria, no notas
  aisladas corregidas una por una.
- El usuario no queda a ciegas mientras tanto: sigue habiendo feedback
  continuo de cents *dentro* de cada nota mientras se sostiene (aguja/barra
  de la Idea 1), solo no hay interrupción de flujo entre notas.

**Curriculum de calidades de acorde — RESUELTO: orden de introducción fijo,
selección dentro de rondas aleatoria/intercalada entre lo ya desbloqueado.**
El orden de introducción es fijo e igual para todos los usuarios — **mayor →
menor → disminuido → aumentado → séptimas (dominante → mayor7 → menor7 →
semidisminuido) → inversiones** — mismo criterio que el curriculum fijo de
intervalos en 1.3 (comparabilidad entre usuarios para la validación de tesis).
Pero una vez que una calidad queda desbloqueada, **la selección de qué acorde
tocar en cada ronda es aleatoria entre todas las calidades ya habilitadas, no
por bloques secuenciales de un solo tipo**: cuando se desbloquea "aumentado",
las rondas alternan entre mayor/menor/disminuido/aumentado sin previsibilidad
posicional. Esto evita que el usuario "adivine" la calidad del acorde por
contexto de sesión (venían todos mayores, el próximo probablemente sea el tipo
recién desbloqueado) en vez de reconocerla de oído — el riesgo de
memorización posicional es más tentador acá que en intervalos sueltos, porque
las rondas de una misma calidad tienden a sonar más parecidas entre sí que
intervalos aislados.

**Progresión posterior (séptimas e inversiones) — RESUELTO: dentro de la
misma idea, no una idea hermana separada.** A diferencia de cómo se resolvió
el modo por instrumento (Idea 5, separada de la Idea 1 por razones de control
experimental y calibración de rango), acá séptimas e inversiones no abren una
variable experimental nueva ni cambian la fuente de captura — son
simplemente más pasos del mismo curriculum de complejidad creciente, y por
eso quedan documentados como progresión dentro de esta idea, no como una idea
aparte.

**Loop de una ronda:**

1. Se define una calidad de acorde objetivo por selección aleatoria entre las
   ya desbloqueadas por el curriculum fijo, y su `nivel_andamiaje` actual para
   esa calidad (gobernado por el DDA, o forzado manualmente).
2. Según el nivel: se reproduce la tríada completa (Nivel 1), se reproduce
   solo la raíz + nombre de la calidad (Nivel 2), o se informa solo el nombre
   de la calidad sin audio (Nivel 3).
3. El usuario canta raíz, 3ra y 5ta en secuencia, en el estilo que le resulte
   natural (con pausas o en legato) — el segmentador híbrido (6.2) detecta el
   cierre de cada nota y avanza la máquina de estados sin requerir que el
   usuario adopte un estilo particular.
4. Mientras se sostiene cada nota, la UI muestra el indicador continuo de
   cents de desviación (igual que en la Idea 1), específico de la nota que se
   está cantando en ese momento (raíz, 3ra o 5ta).
5. Al completarse las 3 notas (`acorde_completo`), se calculan los 3 CA y la
   `calidad_percibida` (6.2), se compara contra el objetivo, y se envía el
   resultado agregado al motor DDA (salvo ronda `origen: manual`).
6. El DDA ajusta el `nivel_andamiaje` de esa calidad de acorde si corresponde,
   y/o habilita la siguiente calidad del curriculum.

**UI mínima necesaria:**
- Reproductor de la tríada/raíz según nivel, con botón de repetir sujeto a
  límite de repeticiones gobernado por el DDA (no aplica en Nivel 3).
- Indicador de pitch en tiempo real, reetiquetado según qué nota de la tríada
  se está cantando (raíz / 3ra / 5ta), para que el usuario sepa en qué parte
  de la secuencia está.
- Feedback post-ronda: `calidad_percibida` vs. objetivo, CA de cada una de
  las 3 notas, `nivel_andamiaje` actual y progreso hacia el próximo
  desbloqueo — mismo criterio de la Idea 1 de no exponer los parámetros
  finos del DDA (tolerancias, umbrales del segmentador).

**Datos a registrar por ronda (para DDA y telemetría de tesis):**
`{timestamp, calidad_objetivo, nivel_andamiaje (1|2|3), origen (manual|dda),
estilo_segmentacion_detectado (pausa|legato|mixto), serie_temporal_cents_por_nota
(raiz|3ra|5ta), CA_raiz, CA_3ra, CA_5ta, calidad_percibida, motor_PDA_usado
(YIN|ML), tiempo_hasta_acorde_completo, repeticiones_usadas,
nivel_dificultad_antes, nivel_dificultad_despues}`

## 6.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver 6.1–6.3,
marcadas **RESUELTO**/**CONFIRMADO**):

- Captura estrictamente secuencial/monofónica (nunca simultánea) — 6.1.
- Alcance inicial: tríadas en estado fundamental (mayor, menor, disminuido,
  aumentado); séptimas e inversiones como progresión posterior dentro de la
  misma idea, no como idea separada — 6.1, 6.3.
- Escalera de 3 niveles de andamiaje adaptada de la Idea 1 (imitación / ancla
  dada / producción libre) para tríada completa — 6.3.
- Segmentador híbrido de eventos de nota: disparador de silencio O disparador
  de salto de F0, lo que ocurra primero — robusto a cualquier estilo de canto
  del usuario, sin exigir un modo particular; glissando resuelto por la misma
  ventana de estabilización de nota sostenida (sin tercer disparador) — 6.2.
- Sin corte a mitad de ronda ante error en nota intermedia — feedback
  agregado al final de las 3 notas — 6.3.
- Métrica: sin promedio único — serie de 3 CA + clasificación categórica de
  `calidad_percibida` por intervalo (raíz→3ra, raíz→5ta) — 6.2.
- Curriculum de calidades: orden de introducción fijo (mayor → menor →
  disminuido → aumentado → séptimas → inversiones), pero selección de acorde
  dentro de rondas aleatoria/intercalada entre lo ya desbloqueado (no por
  bloques) — 6.3.
- Alcance de tesis: dentro del MVP (no trabajo futuro como la Idea 5) — 6.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el desbloqueo
de nivel/calidad; umbrales del segmentador híbrido (ventana de silencio en ms,
Δ de cents para salto abrupto, ventana de frames); tolerancias en cents por
intervalo para la clasificación de `calidad_percibida` (3ra mayor/menor, 5ta
justa/alterada).

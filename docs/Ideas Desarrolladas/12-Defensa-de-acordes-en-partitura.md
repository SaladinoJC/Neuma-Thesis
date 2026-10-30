# 12. Defensa de acordes en partitura

> Actualización 2026-10-22: el usuario confirmó planificar las tres variantes de Defender en orden notas (13) → acordes por nombre (12) → piano virtual (14), conservando supervivencia con tres vidas y sin coste directo de vida por intento incorrecto. Ver [definición en curso](../Definicion-defender.md). Esta ficha describe la segunda etapa; el DDA previo por ventanas/umbrales necesita concretarse con el Mamdani vigente. No implica aprobación del director ni código implementado.

> Quinta ronda de la entrevista: mayor→menor→disminuido→aumentado→séptimas (dominante, mayor7, menor7, semidisminuido), primero tríadas fundamentales y luego séptimas/inversiones. Respuesta raíz+calidad, sin exigir identificar la inversión; raíz no necesariamente es el bajo. Sol→fa→mezcla con progreso propio, escritura exacta de raíz y sin duplicados simultáneos por raíz escrita+calidad. La referencia vertical, movimiento radial, audio y feedback comunes están definidos en la entrevista. Estas decisiones resuelven la ambigüedad de inversión/respuesta y prevalecen sobre detalles anteriores incompatibles.

> Vuelta de tuerca al ítem 14 original ("reconocimiento visual de acordes en
> partitura, básico, ya existe en el mercado"). Acordes en notación de
> pentagrama avanzan hacia una base que el usuario debe proteger; para
> destruirlos, el usuario los lee y "escribe" el acorde (raíz + calidad, con
> un teclado dedicado) antes de que lleguen — recall activo bajo presión de
> tiempo, en formato arcade de supervivencia, no selección múltiple pasiva.

**Estado:** DESARROLLADA

## 12.1 Fundamento

**Por qué el ítem 14 tal cual, incluso con la vuelta de tuerca genérica del
ítem 17 (contrarreloj/DDA), seguía siendo indistinguible del mercado.** El
ítem 17 del brainstorming propone acelerar la aparición de opciones en una
pantalla de selección múltiple gobernada por DDA — una mejora real de
producto, pero la mecánica de fondo sigue siendo **reconocimiento pasivo por
selección entre opciones**, que es exactamente lo que ya hace la inmensa
mayoría de apps de lectura de acordes/notas del mercado (§3.3 de
`InvestigacionSistematica.md`). La vuelta de rosca resuelta en esta sesión no
pasa por acelerar la selección, sino por **cambiar el tipo de tarea
cognitiva**: de reconocer (elegir entre 4 opciones ya dadas) a recordar y
ensamblar la respuesta desde cero (raíz + calidad, con un teclado dedicado de
primitivas, no una lista de acordes completos) — un salto de "recognition" a
"recall" activo, envuelto en un formato arcade de supervivencia (tipo
tower-defense/space-invaders de acordes) que le da una razón de urgencia real
al recall bajo presión de tiempo.

**Por qué no se tomó el camino de conectar con el pipeline de voz (Idea 6).**
Se evaluó explícitamente la alternativa de pedir al usuario que cante el
acorde mostrado en partitura, reutilizando el segmentador híbrido de la
Idea 6 — la opción con más conexión directa al eje arquitectónico central de
la tesis (multiplexor DSP↔ML). Se descartó por decisión explícita: la vuelta
de rosca elegida es de mecánica de juego (arcade de supervivencia + recall
por teclado dedicado), no de modalidad de entrada.

**Aporte arquitectónico — dos componentes nuevos respecto de todas las ideas
anteriores:**
1. **Renderizado de pentagrama en miniatura por enemigo.** Ninguna idea
   anterior trabaja con notación musical visual — todas operan sobre audio
   (síntesis de salida o captura de entrada). Esto abre un componente de
   UI/rendering de partitura que no existía en el catálogo hasta ahora.
2. **Motor DDA de dos capas, con ciclos de vida distintos.** Todas las ideas
   anteriores (1, 5, 6, 7, 11) usan el motor DDA genérico como una sola capa
   persistente entre sesiones. Acá el mismo motor se separa por primera vez
   en dos capas independientes: una **persistente** (curriculum de calidades
   de acorde disponibles, se acumula entre partidas) y una **intra-partida**
   (velocidad de movimiento de los enemigos, rubber-banding que nace y muere
   con cada partida). Es una extensión genuina del componente DDA genérico ya
   validado, no una reimplementación.

**Rol dentro de la tesis — trabajo futuro / experimento secundario, fuera del
MVP, mismo criterio que las Ideas 5, 7 y 11.** No toca el pipeline de captura
de voz ni el multiplexor DSP↔ML, que siguen siendo el aporte arquitectónico
central de la tesis. A diferencia de las Ideas 7 y 11 (que solo reutilizan el
motor DDA genérico tal cual), esta idea sí aporta dos componentes nuevos
(rendering de notación, DDA de dos capas) — pero ninguno de los dos abre una
pregunta de investigación sobre el eje central (detección de tono en voz), por
lo que se clasifica igual: catálogo de producto adicional y extensión del
componente DDA genérico, no núcleo de tesis.

## 12.2 Base técnica

**Sin pipeline de audio de entrada** (como las Ideas 7 y 11) — no hay
micrófono, ring buffer, PDA ni multiplexor DSP↔ML. Los componentes nuevos son
el renderizado de partitura y el motor DDA de dos capas.

```
Selección de calidad de acorde (curriculum persistente, ventana móvil por
   calidad) + raíz aleatoria, sin repetir calidad+raíz entre enemigos vivos
   → Renderizado en pentagrama miniatura (notas apiladas, sin nombre visible)
   → Spawn del enemigo (curva de aparición en función del tiempo transcurrido
        en la partida) → avanza hacia la base a la velocidad vigente
        (ajustada por rubber-banding intra-partida)
   → Usuario arma acorde con teclado dedicado (raíz + calidad) → Enter
   → Matching contra acordes vivos en pantalla
   → Señal de éxito binaria (capa persistente) + tiempo_hasta_matar_ms
        (capa intra-partida)
   → Motor DDA de dos capas (persistente + intra-partida)
```

**Notación — RESUELTO: pentagrama miniatura con las notas del acorde
apiladas, nunca nombre/símbolo en texto.** Es lo único que mantiene al juego
fiel al ítem 14 original (lectura de partitura), en vez de degradar a un
juego de memoria de símbolos desconectado de la lectura musical.

**Entrada del usuario — RESUELTO: teclado dedicado de raíz (12 notas
cromáticas) + calidad (según curriculum desbloqueado), no texto libre.**
Evita el problema de parsing de notación musical (enarmónicos, alias de
calidad como "min"/"m"/"-") y la fricción de escribir texto musical con el
teclado del sistema en mobile, sin perder el carácter de recall activo: el
usuario ensambla la respuesta desde primitivas, no elige entre acordes
completos ya armados.

**Sin acordes duplicados simultáneos en pantalla — RESUELTO.** Cada
combinación de raíz+calidad activa es única mientras esté viva, evitando
ambigüedad de a cuál enemigo corresponde un Enter correcto.

**Cantidad de enemigos simultáneos — propiedad emergente, no gobernada
directamente por el DDA.** Surge de la intersección entre la curva de spawn
(función creciente del tiempo transcurrido en la partida, valor de tuning) y
la velocidad a la que el jugador los elimina — no es una variable explícita
que el DDA ajuste por separado.

**Manejo de intento fallido — RESUELTO: se registra para precisión, no cuesta
vida.** Si el Enter no coincide con ningún enemigo vivo, se cuenta como
`intento_fallido` (afecta la métrica de precisión de la partida) pero no resta
vidas ni bloquea al jugador — el teclado se limpia y puede reintentar de
inmediato. Las vidas solo se pierden cuando un enemigo llega a tocar la base
sin ser destruido, manteniendo separadas dos señales de desempeño: precisión
de lectura (aciertos vs. intentos fallidos) y velocidad de reacción bajo
presión (vidas perdidas).

**Vidas y fin de partida — RESUELTO: 3 vidas, sin límite de tiempo fijo.**
Partida termina en `game over` al perder la tercera vida. Sin cronómetro
artificial — la presión creciente ya está dada por la curva de spawn (más
enemigos con el tiempo) y por el rubber-banding de velocidad, no hace falta
un límite adicional.

## 12.3 Diseño

**Curriculum de calidades de acorde — RESUELTO: mismo orden fijo que la
Idea 6** (mayor → menor → disminuido → aumentado → séptimas → inversiones) —
es una propiedad de la calidad de acorde en sí, no del modo de interacción
(voz vs. lectura+tipeo), no hay razón para que cambie entre ideas.

**Gobernanza de la capa persistente (curriculum) — RESUELTO: mismo mecanismo
que la Idea 6.** Promedio móvil de éxito sobre ventana de N apariciones, por
calidad específica, con éxito binario definido como `matado antes de tocar la
base` (sí/no) — sin ponderar por tiempo de reacción, mismo criterio de señal
simple y estable ya usado para gobernar desbloqueos en la Idea 6. Se acumula
entre partidas, no se resetea al perder. Override manual disponible
(`origen: manual|dda`).

**Gobernanza de la capa intra-partida (velocidad) — RESUELTO: rubber-banding
en tiempo real dentro de la partida, sin persistencia entre partidas.** Cada
partida arranca con una velocidad base de movimiento calibrada según el nivel
de habilidad ya alcanzado en el curriculum persistente para las calidades
actualmente desbloqueadas (no arranca "de cero" cada vez). A partir de ahí, el
`tiempo_hasta_matar_ms` de las apariciones más recientes ajusta la velocidad
de los próximos enemigos al alza o a la baja — señal de telemetría/ajuste
fino, no gatekeeper del curriculum.

**Loop de partida:**

1. Arranca la partida con velocidad base de movimiento calibrada según el
   nivel de habilidad ya alcanzado en el curriculum persistente.
2. Los enemigos aparecen según la curva de spawn (función del tiempo
   transcurrido), con calidad sorteada entre las desbloqueadas y raíz
   aleatoria, sin repetir calidad+raíz entre los enemigos vivos.
3. Cada enemigo se renderiza como pentagrama miniatura con las notas del
   acorde apiladas, y avanza hacia la base a la velocidad vigente.
4. El usuario arma un acorde con el teclado dedicado y presiona Enter:
   - Coincide con un enemigo vivo → ese enemigo muere; se registra
     `(calidad, raiz, exito=true, tiempo_hasta_matar_ms)`.
   - No coincide con ninguno → `intento_fallido`, sin costo de vida, teclado
     se limpia.
5. Si un enemigo toca la base sin ser destruido → se pierde una vida; se
   registra `(calidad, raiz, exito=false)` para la ventana persistente; el
   enemigo desaparece.
6. Tras cada resultado, el DDA ajusta: la capa persistente (ventana móvil de
   éxito por calidad, desbloqueo de la siguiente calidad si corresponde) y la
   capa intra-partida (velocidad de movimiento de los próximos enemigos, según
   `tiempo_hasta_matar_ms` reciente).
7. La partida continúa hasta perder la tercera vida (`game over`).

**Datos a registrar por aparición de enemigo:**
`{timestamp, partida_id, calidad_acorde, raiz, resultado (matado|llego_a_base),
tiempo_hasta_matar_ms (null si llegó a la base), velocidad_movimiento_vigente,
nivel_dificultad_persistente_antes, nivel_dificultad_persistente_despues}`

**Datos a registrar por partida (agregado):**
`{partida_id, timestamp_inicio, duracion_total_ms, vidas_perdidas,
enemigos_matados, intentos_fallidos_totales, calidades_en_juego,
velocidad_inicial, velocidad_final}`

## 12.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
12.1–12.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Vuelta de tuerca elegida: mecánica de recall activo (raíz+calidad
  ensamblada) bajo presión, formato arcade de supervivencia — no el
  contrarreloj/DDA genérico del ítem 17, ni la conexión con voz de la
  Idea 6 (evaluada y descartada explícitamente) — 12.1.
- Entrada del usuario: teclado dedicado de raíz + calidad, no texto libre —
  evita ambigüedad de notación y fricción de tipeo en mobile — 12.2.
- Notación del enemigo: pentagrama miniatura con notas apiladas, nunca
  nombre/símbolo en texto — mantiene la fidelidad al ítem 14 original — 12.2.
- Sin acordes duplicados simultáneos en pantalla — 12.2.
- Cantidad de enemigos simultáneos: propiedad emergente de la curva de spawn
  vs. velocidad de eliminación, no gobernada directamente por el DDA — 12.2.
- Manejo de intento fallido: cuenta para precisión, no cuesta vida ni
  bloquea reintento — 12.2.
- Vidas: 3 por partida, sin límite de tiempo fijo, game over al perder la
  tercera — 12.2.
- Curriculum de calidades: mismo orden fijo que la Idea 6 — 12.3.
- DDA de dos capas: persistente (curriculum de calidades, ventana móvil,
  éxito binario `matado antes de tocar la base`, mismo mecanismo de la
  Idea 6) + intra-partida (rubber-banding de velocidad de movimiento, sin
  persistencia entre partidas, arrancando de una base calibrada por el nivel
  ya alcanzado) — 12.3.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que las Ideas 5, 7 y 11) — 12.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): curva de spawn (función del tiempo transcurrido);
tamaño de ventana N y umbral de éxito para el desbloqueo de calidad en la
capa persistente; magnitud y sensibilidad del rubber-banding de velocidad
intra-partida; velocidad base inicial por nivel de habilidad.

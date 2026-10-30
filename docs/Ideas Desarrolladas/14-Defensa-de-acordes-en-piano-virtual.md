# 14. Defensa de acordes en piano virtual (lectura + digitación)

> Actualización 2026-10-22: el usuario confirmó planificar Defender como notas (13) → acordes por nombre (12) → piano virtual (14), conservando supervivencia con tres vidas y sin pérdida directa de vida por respuesta incorrecta. Ver [definición en curso](../Definicion-defender.md). Esta ficha corresponde a la tercera etapa; las reglas previas por ventanas/umbrales necesitan concretarse con Mamdani. No implica aprobación del director ni implementación existente.

> Sexta ronda: Do3–Do5, selección toggle y Disparar, conjunto exacto con octavas y sin extras; tras error se conserva selección. Las teclas suenan al tocarlas, el acorde al acertar y hay sonido de error. Vertical/movimiento radial y reglas comunes del arcade según la entrevista; distribución concreta de teclado pendiente de verificación visual. La regla anterior de limpiar teclas después de fallar queda reemplazada.

> Vuelta de tuerca al ítem 16 original ("elegir notas en piano virtual para
> formar un acorde — básico, ya existe en el mercado"). Tercera idea de la
> familia "defensa" (Ideas 12 y 13): el acorde se muestra en pentagrama, como
> en la Idea 12, pero en vez de escribir su nombre con un teclado abstracto,
> el jugador debe **tocarlo en un piano virtual** (rango fijo Do3-Do5) antes
> de que el enemigo llegue a la base — combina lectura de partitura con
> traducción a digitación real.

**Estado:** DESARROLLADA

## 14.1 Fundamento

**Camino elegido frente a las alternativas evaluadas.** Se consideraron tres
caminos para la vuelta de rosca del ítem 16: (A) invertir el estímulo de la
Idea 12 (anunciar el nombre del acorde, tocarlo en piano), (B) combinar
lectura de partitura con ejecución en piano virtual, (C) un "detective de
acordes" reutilizando el ítem 6 del brainstorming (aún sin desarrollar). Se
eligió el **Camino B** por ser el que más se aleja del mercado: ninguna app
relevada combina lectura de partitura con ejecución en piano virtual bajo
presión arcade — la oferta comercial, o bien pide tocar un acorde ya
nombrado (sin lectura), o bien pide reconocer un acorde ya tocado (sin
producción). Acá se exigen ambas habilidades a la vez.

**Por qué no es redundante con la Idea 12 pese a compartir el estímulo
(pentagrama).** La Idea 12 exige responder con una **abstracción** (nombre de
raíz + calidad, sin octava — ver 6.2/12.2, mismo criterio de pitch relativo
del resto de la tesis). Esta idea exige la **traducción completa a
digitación real**, con octava exacta: leer la partitura y no poder tocar la
tecla correcta porque no se identificó bien la octava es un error real y
distinto del error de no identificar la calidad del acorde. Es el mismo tipo
de salto que ya diferenció a la Idea 6 (producción vocal activa) de los
ítems básicos 14-16 originales, aplicado acá a la ejecución instrumental
simulada en vez de a la voz.

**Aporte arquitectónico — reutiliza el motor de las Ideas 12/13 (spawn,
vidas, DDA de dos capas), pero introduce un componente de input nuevo:**
un **piano virtual de rango fijo con selección tipo toggle + confirmación
explícita**, y un mecanismo de matching por **conjunto exacto de teclas**
(pitch + octava), distinto del teclado abstracto de nombre usado en las
Ideas 12 y 13. No es una regresión a "selección múltiple" del ítem 16
original: el jugador sigue construyendo la respuesta desde cero (no elige
entre acordes completos ya armados), solo que el "alfabeto" con el que
construye es un teclado de piano en vez de un teclado de nombre+calidad.

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento
secundario, fuera del MVP, mismo criterio que las Ideas 5, 7, 11, 12 y 13.**
No toca el pipeline de captura de voz ni el multiplexor DSP↔ML. Extiende el
catálogo de producto y la validación empírica del motor DDA de dos capas a
un tercer tipo de input (piano virtual con octava exacta), sin abrir una
pregunta de investigación nueva sobre el eje central de la tesis.

## 14.2 Base técnica

**Sin pipeline de audio de entrada** (igual que las Ideas 12 y 13) — no hay
micrófono, ring buffer, PDA ni multiplexor DSP↔ML. Reutiliza el motor arcade
completo (spawn, vidas, DDA de dos capas) con el input y el matching
adaptados a selección de teclas de piano.

```
Selección de calidad+raíz+inversión (curriculum persistente de la Idea 6,
   restringido al rango visible Do3-Do5)
   → Renderizado en pentagrama miniatura (notas con octava exacta)
   → Spawn del enemigo (misma curva de la Idea 12) → avanza hacia la base a
        la velocidad vigente (rubber-banding intra-partida)
   → Usuario togglea teclas en el piano virtual (rango fijo Do3-Do5) →
        confirma con botón "Enviar"
   → Matching contra enemigos vivos: exige el conjunto exacto de teclas
        (pitch + octava) igual al objetivo, sin duplicados en pantalla
   → Señal de éxito binaria (capa persistente) + tiempo_hasta_matar_ms
        (capa intra-partida, medido hasta el "Enviar")
   → Motor DDA de dos capas (mismo mecanismo de la Idea 12)
```

**Piano virtual — RESUELTO: rango fijo de Do3 a Do5 (una octava abajo y una
arriba del Do central), selección tipo toggle, confirmación explícita con
botón "Enviar".** El rango fijo responde a una limitación real de espacio en
pantalla mobile (un teclado completo no entra ni es usable con el pulgar); el
toggle (tocar marca, tocar de nuevo desmarca) más un botón de confirmación
reemplaza la necesidad de detectar presión simultánea o una ventana de
tolerancia temporal (evaluada y descartada frente a exigir simultaneidad
estricta) — el jugador arma el conjunto de teclas a su ritmo y decide
explícitamente cuándo evaluarlo, sin ambigüedad de timing.

**Octava exacta requerida — RESUELTO.** A diferencia de las Ideas 12 y 13
(respuesta abstracta o solo nombre de nota), acá la naturaleza física del
piano virtual hace que la octava sea inseparable de la tarea: no se puede
tocar "un Do" sin elegir una tecla concreta. Leer mal la octava en la
partitura es un error real y evaluable, coherente con el objetivo del
Camino B de exigir la traducción completa lectura→digitación.

**Generación de contenido — RESUELTO: restringida al rango visible.** El
generador de raíz+calidad+inversión descarta (o transporta a la octava más
cercana dentro del rango) cualquier combinación cuya tríada completa no entre
en Do3-Do5 — mismo tipo de restricción conservadora ya usada en la
calibración de rango por instrumento de la Idea 5, aplicada acá al rango fijo
del piano virtual en vez de a la tesitura de un instrumento real.

**Curriculum de calidades — RESUELTO: mismo orden que la Idea 6/12** (mayor →
menor → disminuido → aumentado → séptimas → inversiones), incluyendo
inversiones como paso final. Acá las inversiones tienen un peso pedagógico
mayor que en la Idea 12: tocar una inversión en un piano real implica una
digitación genuinamente distinta (otra disposición de teclas), no solo una
etiqueta abstracta distinta.

**Sin acordes duplicados simultáneos en pantalla — RESUELTO, mismo criterio
que la Idea 12 (no el de la Idea 13).** Al exigir octava exacta, dos enemigos
con el mismo conjunto exacto de teclas de respuesta son un caso raro pero
posible; se prohíben para no introducir una regla de desempate adicional —
la Idea 13 permitió duplicados porque ahí la respuesta era más gruesa (solo
nombre de nota, sin octava), acá la respuesta ya es lo bastante específica
como para que la probabilidad de colisión sea baja y no valga la pena la
complejidad de una regla de desambiguación.

## 14.3 Diseño

**Gobernanza — mismo mecanismo de dos capas que las Ideas 12/13, aplicado a
la combinación calidad+raíz+inversión:**
- **Capa persistente (curriculum):** ventana móvil de éxito binario
  (`matado antes de tocar la base` sí/no) por combinación específica de
  calidad+inversión, con el orden fijo de 14.2. Override manual disponible
  (`origen: manual|dda`).
- **Capa intra-partida (velocidad):** rubber-banding en tiempo real dentro de
  la partida, arrancando de una velocidad base calibrada según el nivel ya
  alcanzado en el curriculum persistente, sin persistir entre partidas.

**Resto del motor arcade — reutilizado sin modificaciones de la Idea 12:**
3 vidas, sin límite de tiempo fijo, curva de spawn creciente con el tiempo
transcurrido, intento fallido (conjunto de teclas enviado que no coincide con
ningún enemigo vivo) cuenta para precisión sin costar vida.

**Loop de partida:**

1. Arranca la partida con velocidad base calibrada según el nivel de
   habilidad ya alcanzado en el curriculum persistente.
2. Los enemigos aparecen según la curva de spawn, con calidad+raíz+inversión
   sorteadas entre lo desbloqueado y restringidas al rango Do3-Do5, sin
   repetir conjunto exacto de teclas entre enemigos vivos.
3. Cada enemigo se renderiza como pentagrama miniatura con las notas del
   acorde (octava exacta), avanzando hacia la base a la velocidad vigente.
4. El usuario togglea teclas en el piano virtual y presiona "Enviar":
   - El conjunto coincide con un enemigo vivo → ese enemigo muere; se
     registra `(calidad, raiz, inversion, exito=true, tiempo_hasta_matar_ms)`.
   - No coincide con ninguno → `intento_fallido`, sin costo de vida, el
     estado del piano virtual se limpia (todas las teclas se desmarcan).
5. Si un enemigo toca la base sin ser destruido → se pierde una vida; se
   registra `(calidad, raiz, inversion, exito=false)` para la ventana
   persistente; el enemigo desaparece.
6. Tras cada resultado, el DDA ajusta la capa persistente (ventana móvil por
   combinación, desbloqueo del siguiente paso del curriculum si corresponde)
   y la capa intra-partida (velocidad de los próximos enemigos).
7. La partida continúa hasta perder la tercera vida (`game over`).

**Datos a registrar por aparición de enemigo:**
`{timestamp, partida_id, calidad_acorde, raiz, inversion, notas_objetivo
(pitch+octava x3-4), resultado (matado|llego_a_base), tiempo_hasta_matar_ms
(null si llegó a la base), velocidad_movimiento_vigente,
nivel_dificultad_persistente_antes, nivel_dificultad_persistente_despues}`

**Datos a registrar por partida (agregado):**
`{partida_id, timestamp_inicio, duracion_total_ms, vidas_perdidas,
enemigos_matados, intentos_fallidos_totales, combinaciones_en_juego,
velocidad_inicial, velocidad_final}`

## 14.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
14.1–14.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Vuelta de tuerca elegida: Camino B (lectura de partitura + ejecución en
  piano virtual), sobre los caminos A (estímulo invertido) y C (detective de
  acordes, ítem 6 pendiente) — 14.1.
- Piano virtual: rango fijo Do3-Do5, selección tipo toggle, confirmación
  explícita con botón "Enviar" (sin exigir simultaneidad ni ventana de
  tolerancia temporal) — 14.2.
- Octava exacta requerida (a diferencia de las Ideas 12 y 13) — 14.2.
- Generación de contenido restringida al rango visible del piano virtual —
  14.2.
- Curriculum de calidades: mismo orden que la Idea 6/12, incluyendo
  inversiones con peso pedagógico propio (digitación real distinta) — 14.2.
- Sin acordes duplicados simultáneos en pantalla, mismo criterio que la
  Idea 12 (no el de la Idea 13) — 14.2.
- Gobernanza y resto del motor arcade (vidas, spawn, intento fallido, DDA de
  dos capas): reutilizados sin modificaciones de la Idea 12 — 14.3.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que las Ideas 5, 7, 11, 12 y 13) — 14.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito por combinación
calidad+inversión en la capa persistente; curva de spawn; magnitud del
rubber-banding de velocidad; criterio exacto de transporte de octava cuando
una combinación generada no entra en el rango visible.

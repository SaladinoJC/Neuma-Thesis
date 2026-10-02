# 8. Eco rítmico (lectura + generador procedural de ritmos)

> El sistema muestra un patrón rítmico generado proceduralmente (con partitura
> en pentagrama rítmico simple), el usuario lo tapea en pantalla en tiempo
> real leyendo la notación, y se mide desviación en ms por nota (no solo
> aciertos binarios).

**Estado:** DESARROLLADA

## 8.1 Fundamento

**De "eco por imitación auditiva" a "lectura rítmica a primera vista" — cómo
cambió el alcance de la idea original.** El brainstorming original planteaba
un eco por oído: "el sistema toca un patrón, el usuario lo repite
tocando/tapeando". En la sesión de grilling se resolvió algo más ambicioso:
mientras el usuario tapea, **solo suena un click de referencia**, y la única
fuente de información de qué ritmo tocar es la **partitura mostrada en
pantalla** (ver 8.3). Esto convierte la idea en un ejercicio de **lectura
rítmica a primera vista con verificación automática de precisión temporal**,
una habilidad distinta y más avanzada que la imitación puramente auditiva —
más cercana a lo que hace un instrumentista leyendo una parte en tiempo real.

**Por qué esta idea no usa el pipeline de captura de voz de las Ideas 1-6.**
El input es tap táctil directo en pantalla (evento del SO), no captura por
micrófono — **RESUELTO explícitamente en contra de la detección de onset
acústico** (palmas, tapping en una superficie): un evento táctil tiene una
ruta de medición de latencia mucho más corta y predecible que detección de
onset acústico (que además abriría un frente de DSP nuevo — ruido ambiente,
doble triggering por reverberación — no cubierto por el pipeline YIN/F0 ya
construido, y sin aportar nada al objetivo pedagógico real de esta idea, que
es precisión temporal, no calidad tímbrica del golpe).

**Por qué esta idea sí aporta arquitectura nueva, y de peso: TERCER PILAR de
la tesis.** A diferencia de las Ideas 5, 6 y 7 (que reutilizan el motor DDA
genérico sin agregar componentes nuevos, o en el caso de la Idea 6 agregan
una pieza acotada — el segmentador híbrido), esta idea introduce un
componente de arquitectura sustancial y autocontenido, ortogonal al
multiplexor DSP↔ML y al DDA: un **generador procedural de ritmos por
predicados (tags) + un renderizador de notación rítmica**. Esto se suma como
tercer pilar arquitectónico de la tesis, junto con:

1. El multiplexor híbrido DSP↔ML (Oportunidad 7, validado en la Idea 1).
2. El motor DDA por varianza/desempeño continuo (Oportunidad 2, validado en
   las Ideas 1, 5, 6, y ahora también acá con una quinta señal de desempeño:
   desviación temporal en ms por nota).
3. **El generador procedural de ritmos con tags + renderizador de notación
   rítmica** (nuevo, ver 8.2) — no tiene antecedente directo en las
   Oportunidades de `InvestigacionSistematica.md`, pero es coherente con el
   espíritu general del documento de que el aporte real de la tesis debe
   estar en arquitectura computacional, no en "gamificar" per se (línea 449).

**Pedagógico.** Entrena lectura rítmica bajo presión de tiempo real (no
transcripción offline ni dictado sin reloj), con feedback cuantitativo
continuo (ms de desviación por nota) en vez de acierto/error binario — cubre
un hueco que ninguna de las apps comerciales relevadas en §3.3 atiende
(evalúan reconocimiento pasivo de ritmos escuchados, no lectura activa
cronometrada).

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP**, como tercer pilar de
arquitectura junto al multiplexor DSP↔ML y al DDA genérico.

## 8.2 Base técnica

**Sin pipeline de captura de audio de entrada.** El único input capturado es
el evento táctil (tap) con su timestamp — no hay micrófono, ring buffer, PDA
ni multiplexor DSP↔ML involucrados en la captura de esta idea. El componente
nuevo es el generador+renderizador, y el DDA genérico se reutiliza igual que
en las Ideas 1/5/6/7.

**Representación interna: árbol de ritmo (rhythm tree).** Un compás es un
nodo raíz que se subdivide recursivamente — cada subdivisión puede ser
binaria (2, → figuras normales) o ternaria (3, → tresillo), con profundidad
variable (mayor profundidad → figuras más cortas, hasta fusa = 1/32). Las
hojas del árbol son las figuras finales, cada una marcada nota o silencio.
Este único modelo cubre tresillos, fusas y silencios como casos particulares
de la misma recursión, sin lógica especial por cada tipo de figura.

**Tags como predicados sobre el árbol, generación por rejection sampling —
RESUELTO.** Cada tag es un predicado evaluable sobre el árbol de ritmo
generado:
- `simple`: solo subdivisiones binarias, profundidad baja, ataques alineados
  a la grilla del pulso.
- `silencios`: al menos una hoja marcada silencio.
- `tresillo`: al menos un nodo con subdivisión ternaria.
- `sincopa`: existe una nota que ataca fuera del pulso y se sostiene
  atravesando el límite del pulso siguiente (ligadura de prolongación
  cruzando el beat).
- `fusas`: la profundidad de subdivisión alcanza el nivel de fusa (1/32).
- `complejo`: profundidad de subdivisión alta / alto número de hojas.

Para generar un ritmo que cumpla una combinación de tags (ej. `complejo +
sincopa + fusas`), se sortea un árbol al azar dentro de reglas generales
(profundidad máxima, compás) y se evalúan los predicados de tags sobre el
resultado; si no cumple la combinación pedida, se descarta y se vuelve a
sortear (rejection sampling). Se prefiere esto a codificar una gramática de
generación distinta por cada combinación posible de tags — mucho más simple
de implementar correctamente, al costo de eventuales reintentos para
combinaciones raras de tags (costo aceptable, es CPU local, no bloquea UX de
forma perceptible con compases cortos).

**Renderizador de notación — RESUELTO: acotado a pentagrama rítmico simple,
no notación completa.** Se descarta explícitamente notación completa
multi-voz con claves y alturas (eso sí sería un frente de "engraving"
profesional de meses, fuera del cronograma de tesis según el criterio de
§4.3). Se implementa un renderizador acotado a una sola línea rítmica: cabezas
de nota simplificadas (sin pentagrama de alturas), plicas, corchetes de barra
(beaming) agrupados por pulso, corchetes de tresillo, ligaduras de
prolongación (para síncopas) y silencios. El algoritmo central y
genuinamente nuevo de esta pieza es la **conversión árbol de ritmo →
agrupación de barras/corchetes/ligaduras**, un problema de reglas de
engraving bien documentado y acotado (agrupar barras por pulso, romper el
beam en la síncopa, dibujar corchete de tresillo sobre el grupo ternario) —
independiente de la tecnología final de dibujo (motor gráfico propio de la
app, o una librería de notación embebida), que queda como decisión de
implementación, no de diseño.

## 8.3 Diseño

**Mecánica central — RESUELTO: lectura en tiempo real contra un click, sin
audio del ritmo objetivo sonando en simultáneo.** Durante el tapeo, solo
suena un click/metrónomo de referencia (con count-off de 1 compás antes de
empezar) — la partitura generada es la **única fuente de información
rítmica**. Esto es lo que justifica la inversión de ingeniería en el
generador+renderizador: si el audio completo del ritmo sonara al mismo
tiempo que el usuario tapea, la partitura sería un apoyo visual redundante y
no habría necesidad de tanto desarrollo para eso.

**Audio del ritmo objetivo — RESUELTO: toggle opcional, sin efecto en
dificultad.** El usuario puede activar/desactivar que el audio del patrón
suene junto con su propio tapeo, como preferencia de feedback auditivo. No
aporta ninguna ventaja anticipatoria real: el audio de cada nota suena
exactamente en el instante en que corresponde tapearla, así que para cuando
el usuario la escucha ya pasó el momento de reaccionar a tiempo — es
puramente un refuerzo sensorial de "esto es lo que sonó", no una pista que
facilite el ejercicio.

**Curriculum de tags — RESUELTO: orden de introducción fijo, mismo criterio
que intervalos (Idea 1) y calidades de acorde (Idea 6):**

`simple → silencios → tresillo → síncopa → fusas → complejo (combinaciones
de 2+ tags)`

Una vez desbloqueado un tag, entra al pool disponible para la generación por
rejection sampling. El DDA gobierna, por combinación específica de tags ya
habilitada, cuándo exigir más precisión (ver tolerancias abajo) antes de
habilitar la siguiente combinación del curriculum. Override manual disponible
con el mismo esquema `origen: manual|dda` de siempre (rondas manuales no
cuentan para el desbloqueo).

**Métrica de desviación temporal — RESUELTO: doble corte por nota, serie
completa registrada, sin promedio único.** Por cada nota del patrón
(excluyendo silencios, que no requieren tap), se mide la diferencia en ms
entre el tap real y el tiempo esperado según el click/subdivisión:

- **Corte 1 — nota vs. no-nota:** sin ningún tap dentro de una ventana amplia
  alrededor del tiempo esperado → `perdida`.
- **Corte 2 — a tiempo vs. desviado:** dentro de la ventana amplia, un corte
  más chico separa `a_tiempo` de `desviado`.

Se registra la serie completa `[(nota_esperada, desviacion_ms,
clasificacion)]` por ronda, no un promedio único — mismo criterio de
preservar información granular para el DDA y la telemetría de tesis ya usado
en la Idea 6.

**Parámetros que el DDA puede mover:** ancho de las ventanas de tolerancia
(se achica con buen desempeño), tempo del click, y el nivel de complejidad de
tags habilitados según el curriculum.

**Loop de una ronda:**

1. Se determina la combinación de tags objetivo según el curriculum
   desbloqueado (o el override manual), y se genera un ritmo que la cumpla
   por rejection sampling sobre el árbol de ritmo.
2. Se renderiza la partitura del ritmo generado en pantalla (pentagrama
   rítmico simple).
3. Opcionalmente, suena una demo del ritmo completo antes de empezar (a
   definir como parámetro de tuning/UX, no bloqueante para el diseño).
4. Count-off de 1 compás con click.
5. El click sigue sonando; el usuario tapea en pantalla leyendo la partitura,
   con el audio del ritmo objetivo sonando o no según el toggle del usuario.
6. Se registra el timestamp de cada tap, se calcula la desviación en ms
   contra cada nota esperada, y se clasifica (`a_tiempo` / `desviado` /
   `perdida`).
7. Se envía la serie de resultados al motor DDA (salvo ronda `origen:
   manual`), que ajusta tolerancias/tempo y/o habilita la siguiente
   combinación de tags del curriculum.

**Datos a registrar por ronda:**
`{timestamp, tags_objetivo, arbol_ritmo_generado, tempo_click_bpm,
origen (manual|dda), audio_ritmo_activado (bool),
serie_por_nota: [(nota_esperada, desviacion_ms, clasificacion)],
nivel_dificultad_antes, nivel_dificultad_despues}`

## 8.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
8.1–8.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Captura: tap táctil en pantalla, no detección de onset acústico por
  micrófono — 8.1.
- Encuadre de tesis: tercer pilar arquitectónico (generador procedural de
  ritmos con tags + renderizador de notación rítmica simple), además del
  multiplexor DSP↔ML y el DDA genérico — 8.1.
- Representación interna: árbol de ritmo (rhythm tree), subdivisiones
  binarias/ternarias con profundidad variable — 8.2.
- Tags como predicados sobre el árbol; generación por rejection sampling
  (sortear + verificar + descartar si no cumple), no gramática por
  combinación — 8.2.
- Renderizador acotado a pentagrama rítmico simple (sin claves/alturas), con
  el algoritmo árbol→beaming/ligaduras/corchetes de tresillo como pieza
  central nueva — 8.2.
- Mecánica: lectura en tiempo real contra un click, sin audio del ritmo
  objetivo como pista obligatoria — la partitura es la única fuente de
  información rítmica — 8.3.
- Audio del ritmo objetivo: toggle opcional sin efecto en dificultad (suena
  en el mismo instante del tap esperado, sin ventaja anticipatoria) — 8.3.
- Curriculum de tags: orden fijo (simple → silencios → tresillo → síncopa →
  fusas → complejo), selección de generación intercalada entre tags
  desbloqueados — 8.3.
- Métrica: doble corte (nota/no-nota, a-tiempo/desviado) + serie completa por
  nota, sin promedio único — 8.3.
- Alcance de tesis: dentro del MVP — 8.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): anchos de ventana de tolerancia temporal, tempo inicial
del click, profundidad máxima de subdivisión permitida por nivel de
curriculum, y si la demo previa del ritmo completo (paso 3 del loop) es
obligatoria u opcional.

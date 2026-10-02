# 5. Interval Playing con instrumento

> Minijuego hermano de la Idea 1 ("Ear-training de intervalos por reproducción
> vocal"): en vez de cantar, el jugador toca ambas notas del intervalo con su propio
> instrumento (violín, flauta, clarinete, guitarra, etc.), capturadas por el mismo
> micrófono/pipeline de detección de pitch.

**Estado:** DESARROLLADA

## 5.1 Fundamento

**Por qué es una idea separada y no un modo dentro de la Idea 1.** La sección 1.4 de
`01-Ear-training-intervalos-reproduccion-vocal.md` ya establece que el pipeline de
captura (Oboe → ring buffer → YIN/multiplexor → cents) es **agnóstico a la fuente**
por construcción: YIN y sus alternativas operan sobre cualquier señal monofónica
cuasi-periódica, sea voz o un instrumento afinado tocando una nota por vez. No hace
falta un motor de PDA distinto. Pero mezclarlo dentro del mismo minijuego que la voz
rompe tres cosas a la vez:

- **La razón de ser de la Idea 1** — su ventaja explícita es no requerir instrumento
  a mano, clave para una app mobile. Si el core del minijuego termina dependiendo de
  tener un violín o clarinete en la mano, esa ventaja desaparece.
- **El control experimental.** Cada instrumento estresa el PDA de forma distinta —
  eso es una variable experimental, no un detalle de UI. La propia investigación
  señala como línea de tesis legítima la "resiliencia algorítmica ante espectros
  fuertemente inarmónicos" (línea 449 de `InvestigacionSistematica.md`). Instrumentos
  como el clarinete (predominio de armónicos impares, débil fundamental → riesgo de
  error de octava en YIN) o el violín (vibrato intenso, ataque con ruido de arco) son
  casos de prueba reales para ese problema. Mezclarlos sin control experimental (sin
  saber qué instrumento generó qué error) contamina cualquier medición seria de
  RPA/CA por fuente.
- **La calibración de rango.** Una guitarra y un violín tienen tesituras muy
  distintas entre sí y respecto de la voz humana — el intervalo objetivo y la nota
  ancla tendrían que adaptarse al rango del instrumento seleccionado.

**Qué habilidad entrena.** La misma que la Idea 1 (producción activa de intervalos,
no solo reconocimiento), pero trasladada al instrumento del propio usuario — relevante
para instrumentistas que quieren entrenar oído en su propio timbre e instrumento, no
en abstracto vocal. Reutiliza también el motor de captura para la línea de trabajo
F19/F20 del brainstorming (reconocimiento e identidad tímbrica) — mismo pipeline,
otro objetivo pedagógico.

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento secundario, fuera
del MVP.** No abre una pregunta de investigación nueva respecto de la Idea 1: extiende
el mismo pipeline a otra fuente tímbrica y valida el componente DDA genérico con un
tercer tipo de señal de desempeño (ver 5.2), pero el aporte arquitectónico central de
la tesis (multiplexor DSP↔ML, DDA por varianza continua) ya queda cubierto por
completo con la voz en la Idea 1.

## 5.2 Base técnica

**Pipeline: idéntico al de la Idea 1, sin modificar el motor de PDA.** La única
diferencia real es la fuente de captura y la calibración de rango:

```
Instrumento (micrófono del dispositivo, sin contacto/pickup)
   → Ring buffer lock-free (mismo productor que en Idea 1)
   → Worker de PDA (mismo multiplexor YIN↔ML en cascada, ver 1.2 de la Idea 1)
   → Vector F0 (Hz) → conversión a cents respecto de la nota ancla (pitch relativo,
        igual que en Idea 1 — sin exigir afinación absoluta del instrumento)
   → Motor DDA (mismo componente genérico ya usado en Ideas 1, 3 y 4)
```

**Cambio de tipo de métrica — RESUELTO: de CA continua a triple discreto.** A
diferencia de la Idea 1, acá lo que se entrena no es afinación (la dificultad de
ejecutar una quinta justa vs. una séptima no varía sistemáticamente en un
instrumento, a diferencia de la voz) sino **velocidad y precisión de ubicar la nota
correcta en el instrumento**. La señal de desempeño pasa a ser:

`(resultado: ok | ok_desafinado | incorrecto, tiempo_de_respuesta_ms)`

Esto convierte a esta idea en el **tercer caso de uso** del componente DDA genérico
ya señalado en la Idea 3: serie continua de cents (Idea 1/2), par discreto
acierto+latencia (Idea 3), y ahora un triple discreto con 3 categorías de resultado.

**Definición de las 3 categorías (dos cortes en cents sobre la señal de F0 ya
calculada por el mismo pipeline):**
- **Corte 1 — nota vs. no-nota:** más allá de cierto desvío (candidato: ~50 cents,
  medio semitono) respecto de la nota correcta más cercana, ya no es "la nota
  correcta desafinada" sino otra nota → `incorrecto`.
- **Corte 2 — afinado vs. desafinado**, dentro de la nota correcta: una tolerancia
  más chica (candidato: ~15-20 cents) separa `ok` de `ok_desafinado`.
- Ambos valores concretos son parámetros de tuning, no decisiones de diseño (igual
  que los umbrales de la Idea 1).

**`ok` y `ok_desafinado` cuentan igual para la progresión de dificultad** — el eje de
afinación es informativo/feedback, no gatekeeper, coherente con que el objetivo
central de este minijuego es velocidad de ubicación, no afinación fina.
**`incorrecto` solo se marca al cierre de la ventana de respuesta**, si la nota final
sostenida (tras la misma ventana de estabilización mínima que en la Idea 1) sigue
siendo la equivocada — un toque de paso que el jugador corrige antes de estabilizarse
no cuenta como error.

**Selección de instrumento como config explícita, no mezclada en el dataset de
validación.** El usuario elige su fuente como paso de configuración separado del
minijuego de voz. Cada instrumento es su propio espacio de datos — no se combinan
sesiones de distintos instrumentos al analizar los resultados, para preservar la
comparabilidad experimental que motivó separar esta idea de la Idea 1.

**Calibración de rango por instrumento — RESUELTO: tabla fija, conservadora.** Se usa
una tabla fija de rango tesitural por tipo de instrumento (no calibración por
usuario), con margen conservador hacia el centro del rango de catálogo — evita
proponer intervalos objetivo fuera de la comodidad real de un jugador principiante,
sin agregar un paso de onboarding.

**Instrumentos de la primera iteración — RESUELTO: guitarra y piano.** Ambos con
fundamental fuerte y afinación de posiciones/teclas fija (sin ambigüedad de "nota
correcta" por variación de embocadura o posición, a diferencia de flauta/violín).
Clarinete y violín (casos adversariales para YIN — fundamental débil, vibrato/ruido
de arco) quedan para una iteración posterior.

## 5.3 Diseño

**Dos modos, no una escalera de 3 niveles como en la Idea 1** (no hay equivalente al
Nivel 1 de imitación completa — acá el jugador siempre genera el intervalo, nunca
imita un contorno reproducido):

- **Modo con ancla:** el sistema reproduce **y nombra** la nota ancla ("tocá una 5ª
  justa ascendente desde Do" — suena la nota y se dice cuál es, relevante porque un
  instrumentista piensa en nombres de nota/digitación, no solo en altura relativa
  "de oído"). El jugador toca solo la nota objetivo.
- **Modo libre:** el sistema solo nombra el intervalo ("tocá una 5ª justa"), sin
  ancla dada; el jugador elige su propia nota de partida y toca ambas.

**Gobernanza del modo — mismo mecanismo que la Idea 1, con la métrica adaptada.** El
DDA sube de "con ancla" a "libre" **por intervalo específico**, sobre una ventana
móvil de N rondas, exigiendo **ambas condiciones a la vez**: tasa de éxito
(`ok`+`ok_desafinado` vs. `incorrecto`) por encima de un umbral, **y** tiempo de
respuesta por debajo de un umbral — no alcanza con acertar siempre si se tarda
demasiado, dado que la velocidad es el objetivo central de este minijuego. Permite
override manual con el mismo esquema de etiquetado que la Idea 1
(`origen: manual|dda`; las rondas manuales no cuentan para el desbloqueo).

**Sin curriculum de dificultad por intervalo — RESUELTO.** A diferencia de la Idea 1
(orden fijo por consonancia auditiva), acá todos los intervalos están disponibles
desde el principio, en orden aleatorio: la dificultad técnica de ejecución no varía
sistemáticamente por tipo de intervalo del modo en que sí varía la dificultad
auditiva en la voz. La única progresión real es la de modo con-ancla → libre.

**Loop de una ronda:**
1. Se sortea un intervalo (sin curriculum de orden) y se determina su modo actual
   (con ancla / libre) según el DDA para ese intervalo, o el override manual.
2. Modo con ancla: se reproduce y nombra la nota ancla. Modo libre: solo se nombra
   el intervalo.
3. El jugador toca la nota ancla (si aplica) y la nota objetivo en su instrumento.
4. Al estabilizarse la nota objetivo (ventana mínima de sostenido, igual criterio
   que la Idea 1), se congela el tiempo de respuesta y se clasifica el resultado
   (`ok` / `ok_desafinado` / `incorrecto`) según los cortes en cents definidos en 5.2.
5. Se envía `(resultado, tiempo_de_respuesta_ms)` al DDA (salvo ronda `origen: manual`).
6. El DDA ajusta el modo (con ancla → libre) del intervalo si corresponde.

**Datos a registrar por ronda:**
`{timestamp, intervalo_objetivo, instrumento_usado, modo (con_ancla|libre),
origen (manual|dda), resultado (ok|ok_desafinado|incorrecto), tiempo_de_respuesta_ms,
desviacion_cents_final, motor_PDA_usado (YIN|ML)}`

## 5.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver 5.1–5.3,
marcadas **RESUELTO**/**CONFIRMADO**):

- Sin referencia auditiva de imitación completa — dos modos (con ancla / libre), no
  una escalera de 3 niveles — 5.3.
- Cambio de métrica de CA continua a triple discreto
  `(ok|ok_desafinado|incorrecto, tiempo_de_respuesta_ms)` — 5.2.
- `ok`/`ok_desafinado` equivalentes para la progresión; `incorrecto` solo al cierre
  de la ventana de respuesta — 5.2.
- Progresión con-ancla → libre por intervalo, exigiendo tasa de éxito **y** tiempo de
  respuesta — 5.3.
- Sin curriculum de dificultad por intervalo (todos disponibles desde el inicio) — 5.3.
- Calibración de rango: tabla fija conservadora por instrumento — 5.2.
- Primera iteración: guitarra y piano — 5.2.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP — 5.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no decisión de
diseño): los cortes concretos en cents (corte nota/no-nota, corte afinado/desafinado),
el tamaño de ventana N y los umbrales de tasa de éxito/tiempo de respuesta para el
desbloqueo de modo.

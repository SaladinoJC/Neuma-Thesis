# 9. Rítmica por capas (loop layering)

> El usuario debe entrar tocando una subdivisión distinta (corchea/tresillo/
> semicorchea) sobre un click que sigue un patrón base, evaluando estabilidad
> de tempo al superponer complejidad polirrítmica.

**Estado:** DESARROLLADA

## 9.1 Fundamento

**Prima hermana de la Idea 8, mismo dominio, objetivo pedagógico distinto.**
Comparte con "Eco rítmico" (Idea 8) el mecanismo de captura (tap táctil en
pantalla, sin micrófono) y el generador procedural de ritmos por árbol +
tags. Pero el objetivo es otro: la Idea 8 mide precisión de lectura de un
patrón prescripto; esta idea mide **estabilidad de una subdivisión propia y
constante mientras suena (o se tapea) un patrón en conflicto polirrítmico**
— la habilidad de no "contagiarse" del pulso ajeno y sostener el propio,
error típico y bien documentado en la práctica de polirritmia (2 contra 3,
3 contra 4, etc.).

**Por qué no reutiliza el rol de pilar arquitectónico de la Idea 8.** El
generador procedural + renderizador de notación ya se construyó como tercer
pilar de la tesis en la Idea 8 — esta idea **lo reutiliza tal cual**, sin
agregar ningún componente de arquitectura nuevo: el mismo árbol de ritmo
sirve para generar tanto el patrón base como la subdivisión objetivo,
aplicado ahora a dos capas simultáneas en vez de una sola.

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento
secundario, fuera del MVP.** Mismo criterio que las Ideas 5 y 7: no abre un
componente de arquitectura nuevo (el pilar rítmico ya está cubierto por
completo con la Idea 8). Sí aporta una señal de desempeño más al motor DDA
genérico (estabilidad de subdivisión sostenida, distinta de la desviación
contra un patrón prescripto de la Idea 8), lo cual la hace valiosa como
extensión de la validación empírica, pero no alcanza por sí solo para
justificar su inclusión en el núcleo — mismo argumento ya usado con la
Idea 7.

## 9.2 Base técnica

**Reutiliza el generador procedural + renderizador de notación de la Idea 8
sin modificaciones** — ver 8.2 para el árbol de ritmo, los tags y el
algoritmo de conversión a notación. No hay pipeline de captura de audio
(sigue siendo tap táctil), ni componente nuevo de generación de contenido.

**Métrica de estabilidad — RESUELTO: primaria por grilla absoluta, secundaria
de isocronía como diagnóstico.**

- **Primaria (gobierna el DDA):** cada tap del usuario se compara contra el
  tiempo esperado de su subdivisión según la posición matemática exacta
  dentro del compás en relación al patrón base (ej. en 3 contra 2, la
  subdivisión de 3 vuelve a coincidir con el pulso del patrón base en
  puntos calculables) — reutiliza el mismo esquema de doble corte
  (`a_tiempo` / `desviado` / `perdida`) ya definido en 8.3 para la Idea 8,
  sin inventar una métrica nueva.
- **Secundaria (registrada, no gatekeeper):** varianza/desviación estándar
  de los intervalos entre taps consecutivos del propio usuario (isocronía) —
  captura el error típico de esta mecánica (el usuario arranca bien pero se
  "contagia" del patrón base y deriva el tempo con el correr del loop), dato
  valioso para telemetría de tesis aunque no gobierne el desbloqueo de
  dificultad.

**Estructura del loop — RESUELTO: N repeticiones fijas, no indefinido.** El
patrón base suena en loop durante un número fijo de repeticiones (valor de
tuning, candidato 4 u 8 compases) — necesario para que la deriva de tempo
tenga margen real de manifestarse (con una sola pasada corta el usuario
podría sostener la subdivisión correcta por pura inercia sin que se note
inestabilidad), pero acotado para que la ronda tenga un final claro y
comparable entre usuarios.

## 9.3 Diseño

**Dos modos — RESUELTO: conviven, no se reemplazan entre sí.**

- **Modo base (punto de entrada, más simple):** el sistema reproduce/muestra
  el patrón base (audio y/o notación, igual criterio de toggle que en la
  Idea 8) y el usuario sostiene únicamente la subdivisión objetivo
  (corchea/tresillo/semicorchea) tapeando en una sola zona de pantalla, en
  polirritmia contra ese patrón base.
- **Modo dos zonas (progresión de dificultad posterior, dentro de la misma
  idea):** el sistema no toca la base — el usuario genera **ambas capas**
  con sus propias manos, cada una en una zona distinta de la pantalla
  (ej. mitad izquierda = patrón base, mitad derecha = subdivisión objetivo).
  Entrena coordinación de manos independientes, no solo resistencia a
  interferencia externa.

**Evaluación del modo dos zonas — RESUELTO:**
- Cada zona se evalúa con su propia instancia de la métrica primaria (doble
  corte a_tiempo/desviado/perdida, independiente por mano).
- Se agrega una **métrica extra de coherencia de fase entre ambas manos**:
  diferencia entre la relación de fase real observada (momento en que cada
  mano tapeó su nota más cercana en el tiempo) y la relación de fase
  matemática esperada entre las dos subdivisiones. Es lo que distingue a
  este modo del modo base: no alcanza con que cada mano sea precisa por
  separado, hay que sostener la relación *entre* ambas.

**Curriculum de complejidad polirrítmica — RESUELTO: orden fijo, mismo
criterio que toda la progresión de tesis:**

`2 contra 3 → 3 contra 2 → 4 contra 3 → combinaciones/mezclas`

Gobernanza por el mismo motor DDA genérico (ventanas de tolerancia que se
achican, complejidad polirrítmica que sube, override manual con esquema
`origen: manual|dda`). El modo dos zonas se habilita como escalón posterior
al dominio del modo base para la misma combinación polirrítmica, no en
paralelo desde el principio.

**Datos a registrar por ronda:**
`{timestamp, combinacion_polirritmica, modo (base|dos_zonas),
origen (manual|dda), repeticiones_del_loop,
serie_primaria_zona_A: [(nota_esperada, desviacion_ms, clasificacion)],
serie_primaria_zona_B (solo en modo dos_zonas),
varianza_intervalos_zona_A, varianza_intervalos_zona_B (solo dos_zonas),
coherencia_fase (solo dos_zonas), nivel_dificultad_antes,
nivel_dificultad_despues}`

## 9.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
9.1–9.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Reutiliza el generador procedural + renderizador de notación de la Idea 8
  tal cual, sin componente de arquitectura nuevo — 9.1, 9.2.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que Ideas 5 y 7) — 9.1.
- Métrica: primaria por grilla absoluta (reutiliza el doble corte de la Idea
  8, gobierna el DDA), secundaria de varianza de intervalos/isocronía como
  diagnóstico registrado, sin gobernar dificultad — 9.2.
- Loop de N repeticiones fijas (valor de tuning), no indefinido — 9.2.
- Dos modos conviven: modo base (patrón base tocado por el sistema, usuario
  sostiene una capa) y modo dos zonas (usuario genera ambas capas, una por
  mano/zona de pantalla) como progresión posterior dentro de la misma idea
  — 9.3.
- Evaluación del modo dos zonas: métrica primaria independiente por zona +
  métrica extra de coherencia de fase entre ambas manos — 9.3.
- Curriculum polirrítmico fijo: 2c3 → 3c2 → 4c3 → combinaciones — 9.3.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): número de repeticiones del loop, anchos de ventana de
tolerancia temporal, y el umbral de coherencia de fase para el modo dos
zonas.

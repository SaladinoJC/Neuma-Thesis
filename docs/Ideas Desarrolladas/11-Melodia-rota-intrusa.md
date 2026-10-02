# 11. Melodía rota / intrusa

> Se toca una melodía conocida (banco fijo, no generada) con una nota
> alterada; el usuario marca en qué punto "algo sonó mal" sin necesariamente
> poder nombrar la nota — entrena detección temprana antes que nomenclatura.

**Estado:** DESARROLLADA

## 11.1 Fundamento

**Por qué esta idea no usa el pipeline de captura de voz de las Ideas 1-6.**
Igual que la Idea 7 (Progresión rota), acá el jugador solo escucha y marca
una nota (tap en pantalla/línea de tiempo) — no hay señal de audio de entrada
del usuario que capturar ni procesar. El multiplexor DSP↔ML no aplica en
absoluto. El único componente compartido con las ideas anteriores es el
**motor DDA genérico**.

**Por qué igual vale la pena, encuadrada correctamente.** Extiende la
validación empírica del motor DDA a un **quinto tipo de señal de desempeño**
distinto, sumándose a la serie continua de cents (Ideas 1/2), el triple
discreto de instrumento (Idea 5), el categórico de calidad de acorde
(Idea 6) y el booleano+distancia+tiempo de reacción por compás (Idea 7):
acá la señal es `(nota_marcada_correcta: bool,
distancia_en_notas_al_real, tiempo_de_reacción_ms)` — mismo tipo de tupla que
la Idea 7, pero medida en notas de una melodía en vez de compases de una
progresión.

**Pedagógico — por qué esta idea es distinta de la Idea 10, no una
variante.** La Idea 10 (Dictado melódico por canto) entrena producción activa
de una melodía **generada, sin memoria previa** del usuario: el oyente no
tiene ninguna expectativa formada antes de escucharla. Esta idea depende de
exactamente lo opuesto — una melodía **conocida**, ya internalizada por el
usuario como esquema de memoria a largo plazo (un tema folklórico, una melodía
de conservatorio sin derechos de autor). El fenómeno perceptivo que entrena es
la violación de una expectativa ya formada ("esa nota de un tema conocido
sonó mal"), no la detección de un intervalo aislado sin contexto previo. Es
también un nivel de habilidad distinto y anterior al de nombrar la nota o el
intervalo exacto: el objetivo explícito de la idea original es la detección
temprana, no la nomenclatura — coherente con cómo efectivamente progresa el
entrenamiento auditivo real (primero se nota que "algo está mal", después se
aprende a nombrarlo).

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento
secundario, fuera del MVP.** Mismo argumento que dejó a las Ideas 5 y 7 fuera
del núcleo: no abre una pregunta de investigación nueva sobre el multiplexor
DSP↔ML ni aporta un componente de arquitectura genuino nuevo — es una
reutilización más del motor DDA genérico ya cerrado, con valor de producto
(catálogo de minijuegos) y de validación empírica adicional del DDA sobre un
quinto tipo de señal.

## 11.2 Base técnica

**Sin pipeline de audio de entrada.** No hay micrófono, ring buffer, PDA ni
multiplexor DSP↔ML involucrados — la única señal de audio es de **salida**
(reproducción de la melodía). El único componente compartido con las ideas
anteriores es el **motor DDA genérico** (promedio móvil de éxito sobre
ventana de N rondas, con override manual y etiquetado `origen: manual|dda`,
igual mecanismo que en Ideas 1, 5, 6 y 7).

```
Selección de melodía (banco fijo) + nota alterada
   (nivel de curriculum A/B/C, posición entre la 2ª y la anteúltima nota)
   → Reproducción de audio (sintetizador o samples pregrabados, sin captura)
   → Captura de input del usuario (tap sobre la nota, con timestamp),
        según el modo de interacción vigente (ver 11.3)
   → Evaluación: nota tapeada vs. nota real, distancia_en_notas_al_real,
        tiempo_de_reacción_ms si aplica
   → Motor DDA genérico (mismo componente de Ideas 1/5/6/7)
```

**Generación de contenido — RESUELTO: banco fijo de melodías conocidas de
dominio público, no generación procedural.** A diferencia de la Idea 10 (que
sí construye un generador procedural propio), acá la familiaridad previa del
usuario con la melodía es el mecanismo pedagógico central, no un detalle
reemplazable — una melodía generada al vuelo no tiene la expectativa de
memoria a largo plazo que la idea necesita para funcionar como está pensada.
Se usa un banco fijo de temas de dominio público (folklore, melodías de
conservatorio sin derechos de autor), mismo criterio de banco fijo ya resuelto
en la Idea 7 para el mismo tipo de mecánica ("algo insertado en una secuencia
ya conocida").

**Longitud de la melodía — RESUELTO: 6 a 10 notas.** Un fragmento reconocible
del tema (no la pieza completa), corto como la progresión de 4 compases de la
Idea 7, pero coherente con una frase melódica real que el usuario pueda
identificar como "conocida" en pocos segundos.

**Curriculum de niveles de alteración de la nota — RESUELTO: 3 niveles,
cruzando eje de escala (diatónico/cromático) y eje de magnitud del salto
respecto de la nota original, mismo criterio de progresión fija que en las
Ideas 1, 6 y 7:**

1. **Nivel A — cromática y salto grande:** nota fuera de la escala, con salto
   de 3ra o más respecto de la nota original — disonancia obvia, calibra la
   mecánica de tap antes de exigir oído fino.
2. **Nivel B — diatónica, grado incorrecto:** nota dentro de la escala, pero
   un grado distinto al esperado — rompe el contorno/identidad de la melodía
   sin sonar "fuera de tono", exige oído funcional/melódico real.
3. **Nivel C — cromática, salto chico (semitono):** la más sutil — fuera de
   la escala, pero con un desvío mínimo respecto de la nota original, casi
   indistinguible de una desafinación de ejecución. Entrena la detección más
   fina, la más cercana al "error típico de oído intermedio" de la idea
   original.

**Posición de la nota alterada — RESUELTO: aleatoria entre la 2ª y la
anteúltima nota, excluyendo la primera.** Alterar la primera nota es un caso
degenerado: el usuario todavía no tiene ninguna expectativa melódica
construida (no hay nota anterior contra la cual la alteración rompa una
continuidad), así que detectarla no mide la misma habilidad que el resto de
la mecánica. La última nota sí queda incluida, porque ahí ya hay contexto
previo pleno.

Se genera exactamente una nota alterada por ronda (no múltiples), coherente
con la formulación original de la idea.

## 11.3 Diseño

**Dos modos de interacción como escalón de andamiaje — RESUELTO: mismo
esquema de la Idea 7, adaptado de compás a nota.**

- **Modo "escuchar y elegir después" (más fácil):** el usuario escucha la
  melodía completa y luego marca en qué nota (de una línea de tiempo/lista)
  ocurrió la alteración, sin presión de tiempo. Señal de desempeño: solo
  precisión (`nota_marcada_correcta`, `distancia_en_notas_al_real`) — no
  aplica tiempo de reacción real.
- **Modo "tap en tiempo real" (más difícil):** el usuario tapea en el momento
  en que percibe que "algo sonó mal", mientras la melodía suena, sin poder
  escucharla completa antes ni corregir después. Señal de desempeño:
  precisión + `tiempo_de_reacción_ms`.

**Gobernanza — mismo mecanismo que en la Idea 7**: el DDA sube de "escuchar y
elegir después" a "tap en tiempo real" **por melodía específica**, sobre
ventana móvil de N rondas, cuando el promedio de éxito supera un umbral.
Override manual disponible, con etiquetado `origen: manual|dda` (rondas
manuales no cuentan para el desbloqueo).

**Manejo de falsos positivos/negativos en modo tap en tiempo real —
RESUELTO: mismo criterio que la Idea 7, sin variantes.** Si el usuario no
tapea en ninguna nota durante la reproducción, se registra como error total
(sin detección). Si tapea más de una vez, cuenta el **primer tap** como la
respuesta — no se permite corregir después, coherente con que la señal que se
mide es reacción real, no un intento editable.

**Curriculum de niveles de alteración (A→B→C) — RESUELTO: orden de
introducción fijo, ortogonal al andamiaje de modo de interacción.** Igual que
en la Idea 7, el orden de introducción de niveles de alteración es fijo e
igual para todos los usuarios, y se gobierna por separado del escalón de modo
de interacción (escuchar-y-elegir vs. tap-en-vivo), que es específico de cada
melodía.

**Loop de una ronda:**

1. Se selecciona una melodía del banco fijo y un nivel de alteración (A, B o
   C) según el curriculum, más una posición aleatoria (entre la 2ª y la
   anteúltima nota) para la nota alterada.
2. Se determina el modo de interacción actual (escuchar-y-elegir /
   tap-en-vivo) para esa melodía específica, según el DDA o el override
   manual.
3. Se reproduce la melodía completa.
4. Modo escuchar-y-elegir: al terminar, el usuario selecciona la nota de una
   lista/línea de tiempo. Modo tap-en-vivo: el usuario tapea durante la
   reproducción; se registra el primer tap con su timestamp.
5. Se evalúa `(nota_marcada_correcta, distancia_en_notas_al_real,
   tiempo_de_reacción_ms si aplica)` contra la nota real alterada.
6. Se envía el resultado al motor DDA (salvo ronda `origen: manual`), que
   ajusta el modo de interacción de esa melodía y/o habilita el siguiente
   nivel de alteración del curriculum.

**Datos a registrar por ronda:**
`{timestamp, melodia_id, nivel_alteracion (A|B|C), modo_interaccion
(escuchar_elegir|tap_en_vivo), origen (manual|dda), nota_real_alterada_idx,
nota_marcada_idx, nota_marcada_correcta (bool), distancia_en_notas,
tiempo_de_reaccion_ms (null si modo escuchar_elegir), nivel_dificultad_antes,
nivel_dificultad_despues}`

## 11.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
11.1–11.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Modalidad de interacción: puramente receptiva (tap), sin captura de voz —
  sin pipeline de captura ni multiplexor DSP↔ML, quinto caso de uso del motor
  DDA genérico — 11.1.
- Generación de contenido: banco fijo de melodías conocidas de dominio
  público, no generación procedural — la familiaridad previa es el mecanismo
  pedagógico central, distinto del enfoque de la Idea 10 — 11.2.
- Longitud de la melodía: 6-10 notas (fragmento reconocible, no la pieza
  completa) — 11.2.
- Curriculum de niveles de alteración: 3 niveles cruzando eje de escala
  (diatónico/cromático) y eje de magnitud del salto (cromática+salto grande →
  diatónica+grado incorrecto → cromática+salto chico) — 11.2.
- Posición de la nota alterada: aleatoria entre la 2ª y la anteúltima nota,
  excluyendo la primera (sin contexto previo) — 11.2.
- Mecanismo de interacción: dos modos (escuchar-y-elegir / tap-en-vivo)
  conviven como escalón de andamiaje gobernado por el mismo DDA de la
  Idea 7, no como modos libres — 11.3.
- Manejo de falsos positivos/negativos en tap en tiempo real: mismo criterio
  que la Idea 7, sin variantes — sin detección cuenta como error total;
  primer tap es la respuesta, sin corrección — 11.3.
- Curriculum de niveles de alteración: orden de introducción fijo, ortogonal
  al modo de interacción, que se gobierna por melodía específica — 11.3.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que las Ideas 5 y 7) — 11.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el desbloqueo
de modo de interacción y de nivel de alteración; el banco concreto de
melodías del nivel inicial.

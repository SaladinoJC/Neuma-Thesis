# 16. EQ Detective

> Se reproduce un sonido de referencia y una versión alterada (con un corte
> de frecuencia, ej. atenuación en agudos), y el usuario debe identificar qué
> banda fue alterada — oído técnico de ingeniería de audio, no oído de
> conservatorio. Conecta directamente con la población de "profesionales de
> ingeniería de audio requiriendo entrenamiento auditivo técnico" que el
> propio PICOC de la tesis define como parte de su alcance.

**Estado:** DESARROLLADA

## 16.1 Fundamento

**Anclaje directo en el PICOC de la tesis.** `InvestigacionSistematica.md`
(§3.2, línea 69) define la población de la investigación incluyendo
explícitamente "profesionales de ingeniería de audio requiriendo
entrenamiento auditivo técnico". A diferencia de la mayoría de las ideas del
catálogo (centradas en oído de conservatorio: intervalos, acordes, ritmo,
melodía), esta idea entrena una habilidad perceptiva distinta —
identificación de banda de frecuencia alterada— que es el ejercicio estándar
de formación de "oído técnico" en audio engineering (el mismo tipo de tarea
que entrenan herramientas ya existentes en esa industria, como Golden Ears o
Quiztones), y es la única idea del catálogo que responde a ese segmento
específico del PICOC.

**Por qué comparación A/B, no estímulo único (a diferencia de la Idea 15).**
Se evaluó explícitamente seguir el patrón de estímulo único de la Idea 15
(escuchar el sonido ya alterado y elegir entre opciones, sin referencia), y
se descartó: en entrenamiento de oído técnico real, casi nunca se juzga un
sonido "en abstracto" — se compara una referencia contra una versión
procesada, y es esa diferencia relativa la que permite aislar qué banda
cambió. Un formato de estímulo único mediría una habilidad distinta (memoria
tímbrica absoluta) y menos representativa del trabajo real de mezcla/
mastering que motiva la conexión con la población de audio engineers.

**Por qué tres ejes de curriculum completamente independientes, no cruzados
(a diferencia de la Idea 11).** Se evaluó cruzar granularidad de bandas y
magnitud del corte en niveles combinados (como en la Idea 11), y se descartó
a favor de tres ejes ortogonales con gobernanza propia cada uno — decisión
explícita del usuario. Esto permite que un usuario avance en un eje (ej.
tolerar cortes más sutiles) sin necesidad de haber completado otro (ej. la
granularidad de bandas más fina), reflejando mejor cómo se desarrolla el
oído técnico en la práctica: no son habilidades estrictamente acopladas.

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP**, mismo argumento que
la Idea 15 — a pesar de no tocar el pipeline de captura de voz ni el
multiplexor DSP↔ML, esta idea responde directamente a un segmento de
población explícitamente definido en el PICOC de la tesis, no es una
extensión de catálogo sin respaldo documental.

## 16.2 Base técnica

**Sin pipeline de audio de entrada.** Puramente receptivo — el usuario
escucha y selecciona, no hay captura de voz ni instrumento. Único componente
compartido con las ideas anteriores: el **motor DDA genérico**, instanciado
**tres veces en paralelo**, una por cada eje de curriculum independiente.

```
Estado independiente de los 3 ejes (granularidad de bandas, magnitud del
   corte, tipo de contenido) → se sortea una combinación con los valores
   actualmente desbloqueados de cada eje
   → Se genera el sonido de referencia (banco fijo si es música real, o
        ruido rosa/blanco generado) y la versión alterada (corte aplicado a
        la banda sorteada, con la magnitud del eje vigente)
   → Usuario reproduce referencia/alterado alternadamente (hasta 3 pasadas
        completas)
   → Usuario selecciona la banda alterada entre las opciones (tantas como
        bandas tenga la granularidad vigente)
   → Evaluación: selección correcta/incorrecta, registrada por separado
        para cada uno de los 3 ejes
   → Motor DDA genérico x3 (uno por eje)
```

**Comparación A/B — RESUELTO: referencia + alterado, hasta 3 pasadas
completas antes de responder.** Más generoso que el límite de 2-3 escuchas de
otras ideas (Idea 1, 15) porque la tarea es comparativa — necesita ida y
vuelta entre dos estímulos, no solo reescuchar uno.

**Tipo de alteración — RESUELTO: solo cortes/atenuación, sin boosts.**
Coherente con la formulación original de la idea ("un corte de frecuencia").
Boosts quedan fuera del alcance de esta idea, mencionados como posible
extensión futura, no como parte del curriculum base.

**Tres ejes de curriculum, cada uno con progresión y gobernanza propia,
completamente independiente de los otros dos:**

1. **Eje 1 — Granularidad de bandas:**
   1. 3 bandas anchas (graves/medios/agudos).
   2. 5-6 bandas standard de EQ paramétrico (sub-bass, bass, low-mid, mid,
      high-mid, presence/air).
2. **Eje 2 — Magnitud del corte:**
   1. Corte grande (-9 a -12dB).
   2. Corte moderado (-6dB).
   3. Corte sutil (-3dB).
3. **Eje 3 — Tipo de contenido:**
   1. Ruido rosa/blanco (aísla la percepción de banda sin contenido
      musical de por medio, calibra la mecánica de comparación A/B).
   2. Música real (loops/mezclas, banco fijo — más representativo del
      trabajo real de ingeniería de audio, más difícil porque el corte debe
      percibirse dentro de contenido con energía desigual por banda).

En cada ronda se sortea la combinación entre los valores **actualmente
desbloqueados** de cada eje de forma independiente — ej. si el Eje 1 ya
desbloqueó "5-6 bandas" pero el Eje 2 sigue en "corte grande" y el Eje 3 en
"ruido", la ronda combina 5-6 bandas + corte grande + ruido rosa/blanco.

**Cantidad de opciones en la selección múltiple — se ajusta según la
granularidad vigente del Eje 1**: 3 opciones cuando está en "3 bandas
anchas", 5-6 opciones cuando avanza a "EQ paramétrico".

## 16.3 Diseño

**Gobernanza — RESUELTO: tres ventanas móviles independientes, mismo
mecanismo genérico de toda la tesis, una por eje.** Éxito = selección
correcta de la banda alterada (la misma ronda produce una señal de éxito/
fracaso que alimenta a las tres ventanas simultáneamente, pero cada eje
avanza según su propio umbral y ventana, sin depender de los otros dos).
Override manual disponible por eje, con etiquetado `origen: manual|dda`
(rondas manuales no cuentan para el desbloqueo de ningún eje).

**Sin escalera de andamiaje de modos de interacción** (a diferencia de las
Ideas 1/6/7/11-14): el formato de interacción (comparación A/B, selección
múltiple, hasta 3 pasadas) es fijo a lo largo de todo el curriculum — la
única progresión es la de los tres ejes de contenido.

**Loop de una ronda:**

1. Se determina el valor vigente de cada uno de los 3 ejes (granularidad,
   magnitud, contenido) según el DDA de cada uno, o el override manual.
2. Se genera el sonido de referencia (ruido o sample de música real, según el
   Eje 3 vigente) y la versión alterada (corte en una banda sorteada entre las
   disponibles según el Eje 1 vigente, con la magnitud del Eje 2 vigente).
3. El usuario reproduce referencia y alterado alternadamente, hasta 3 pasadas
   completas.
4. El usuario selecciona la banda alterada entre las opciones (3 o 5-6 según
   el Eje 1 vigente).
5. Se evalúa la selección (correcta/incorrecta) y se envía el resultado a los
   tres motores DDA (salvo ronda `origen: manual` en alguno de ellos).
6. Cada eje ajusta su propio nivel si corresponde, de forma independiente de
   los otros dos.

**Datos a registrar por ronda:**
`{timestamp, nivel_granularidad, nivel_magnitud, nivel_contenido,
origen_granularidad (manual|dda), origen_magnitud (manual|dda),
origen_contenido (manual|dda), banda_alterada, magnitud_db, contenido_id,
repeticiones_usadas, seleccion_usuario, correcto (bool),
nivel_granularidad_antes, nivel_granularidad_despues, nivel_magnitud_antes,
nivel_magnitud_despues, nivel_contenido_antes, nivel_contenido_despues}`

## 16.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
16.1–16.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Mecánica de comparación: A/B (referencia + alterado), no estímulo único
  como en la Idea 15 — fiel al entrenamiento real de oído técnico — 16.1,
  16.2.
- Contenido: ruido rosa/blanco y música real, como eje de curriculum propio,
  no como decisión fija — 16.2.
- Tipo de alteración: solo cortes/atenuación, sin boosts (fuera de alcance,
  posible extensión futura) — 16.2.
- Tres ejes de curriculum completamente independientes (granularidad de
  bandas, magnitud del corte, tipo de contenido), cada uno con su propia
  ventana móvil de gobernanza — decisión explícita de no cruzarlos, a
  diferencia de la Idea 11 — 16.1, 16.2, 16.3.
- Pasos concretos de cada eje: granularidad (3 bandas anchas → 5-6 bandas
  paramétricas), magnitud (-9/-12dB → -6dB → -3dB), contenido (ruido →
  música real) — 16.2.
- Repeticiones de A/B: hasta 3 pasadas completas — 16.2.
- Cantidad de opciones de selección múltiple: ajustada según la granularidad
  vigente del Eje 1 (3 o 5-6) — 16.2.
- Alcance de tesis: dentro del MVP, mismo argumento que la Idea 15 — conecta
  directo con la población de audio engineers definida en el PICOC — 16.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito por cada uno de
los 3 ejes; frecuencias de corte exactas de las bandas paramétricas del
Eje 1; banco concreto de samples de música real del Eje 3.

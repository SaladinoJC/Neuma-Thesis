# 10. Dictado melódico por canto

> El sistema toca una melodía corta, el usuario la canta de vuelta y se
> compara contorno + intervalos (no nota por nota exacta) — más tolerante y
> más cercano a la práctica real de solfeo que un dictado de coincidencia
> estricta.

**Estado:** DESARROLLADA

## 10.1 Fundamento

**Idea hermana directa de la Idea 1, extendida de un intervalo a una
melodía.** Reutiliza el mismo pipeline de captura por voz (Oboe → ring
buffer → YIN/multiplexor → cents, pitch relativo) y el mismo segmentador
híbrido de notas de la Idea 6 (silencio O salto de F0), aplicados ahora a una
secuencia de muchas notas en vez de a un intervalo de dos o un acorde de
tres. El salto pedagógico es real: sostener y reproducir de memoria una
secuencia larga (10-20 notas) exige memoria auditiva a corto plazo muy por
encima de la de un intervalo o un acorde, y es la habilidad que en
conservatorio se conoce como dictado melódico activo/solfeo de producción.

**Por qué "tolerante a contorno" y no coincidencia exacta.** Un dictado
melódico estricto (nota por nota, cents exactos) sobre una secuencia de 10-20
notas sería exigir una precisión acumulativa poco realista incluso para un
oído entrenado. La tolerancia por contorno (¿la dirección del salto es
correcta?) además de por intervalo exacto (¿la magnitud es correcta?) es
coherente con cómo efectivamente se evalúa el solfeo en la práctica real:
primero se juzga si la forma general de la melodía se entendió, después la
precisión fina de cada salto.

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP.** No aporta un
componente de arquitectura nuevo (reutiliza el pipeline de la Idea 1 y el
segmentador de la Idea 6 sin modificaciones), pero a diferencia de las Ideas
5, 7 y 9 (trabajo futuro), sí extiende de forma no trivial la validación
empírica de esas piezas: aplicarlas a secuencias largas (10-20 notas, no 2-3)
pone a prueba la robustez del pipeline y del segmentador a una escala mucho
mayor, y suma una métrica de dos niveles (contorno + intervalo exacto) nueva
para el DDA.

## 10.2 Base técnica

**Pipeline: idéntico al de la Idea 1 + segmentador de la Idea 6, sin
modificar ninguno de los dos.**

```
Micrófono (Oboe) → Ring buffer → Worker de PDA (YIN/multiplexor, igual 1.2)
   → Vector F0 + envolvente RMS
   → Segmentador híbrido de notas (igual 6.2: silencio O salto de F0)
   → Máquina de estados: nota_1 → nota_2 → ... → nota_N (N = longitud de la
     melodía objetivo)
   → Motor DDA (mismo componente genérico de todas las ideas anteriores)
```

**Generación de melodías — RESUELTO: generador procedural, combinando el
generador rítmico ya existente con un generador de alturas nuevo, más
simple.**
- **Duraciones:** se reutiliza el árbol de ritmo de la Idea 8 tal cual, sin
  modificaciones, para las duraciones de las notas de la melodía.
- **Alturas:** generador nuevo, mucho más simple que el de ritmo — no
  requiere estructura de árbol ni rejection sampling. Alcanza con
  **restringir directamente el rango de intervalos permitidos al sortear
  cada nota siguiente** (a diferencia del generador rítmico, acá no hace
  falta generar-y-descartar: la restricción se aplica en el momento mismo de
  elegir cada intervalo). Trabaja siempre en semitonos relativos a una nota
  ancla (pitch relativo, mismo criterio de la Idea 1 — sin exigir ni
  calibrar rango vocal absoluto).

**Curriculum de restricciones de altura (tags del generador) — RESUELTO,
orden fijo:**

`grados_conjuntos (solo pasos de 2da) → saltos_pequeños (3ras/4tas) →
saltos_grandes (5tas o más) → cromatismos → mezclas`

**Comparación por contorno + intervalo — RESUELTO: sin DTW, alineación 1:1,
evaluación de dos niveles por nota.**
- **Alineación:** se asume que el usuario canta exactamente N notas (la
  cantidad de la melodía objetivo), segmentadas por el mismo mecanismo
  híbrido de la Idea 6. **No se implementa un algoritmo de alineación
  tolerante a inserciones/omisiones (tipo DTW)** — es un problema de
  investigación en sí mismo, desconectado del eje arquitectónico de la
  tesis. Si el usuario canta una cantidad de notas distinta a la esperada,
  la ronda se marca como intento fallido (mismo criterio pragmático usado
  para descartar detección de onset acústico en la Idea 8).
- **Evaluación por nota emparejada, dos niveles booleanos (no un CA único):**
  para cada intervalo consecutivo (nota N vs. N-1, pitch relativo en cents):
  - `contorno_ok`: el signo del intervalo (ascendente/descendente/igual)
    coincide con el objetivo.
  - `intervalo_ok`: además del signo correcto, la magnitud cae dentro de
    tolerancia en cents (mismo esquema de cortes que la Idea 1). Si
    `contorno_ok` es falso, `intervalo_ok` no se evalúa — es error directo
    en ese paso.

Se registra la serie completa de tuplas `(contorno_ok, intervalo_ok,
CA_cents)` por nota, no un promedio único — mismo criterio de preservar
información granular ya usado en las Ideas 6, 8 y 9.

## 10.3 Diseño

**Escalera de andamiaje — RESUELTO: 2 niveles adaptados de la Idea 1, no los
3 originales.** El Nivel 3 "Producción libre" de la Idea 1 no tiene
equivalente natural acá (el objetivo siempre es reproducir una melodía
específica generada por el sistema, no inventar una propia — eso es otra
idea, "Call & response improvisado", ítem #13 del brainstorming, pendiente
de relevar). Los niveles que sí aplican:

- **Nivel 1 — Imitación completa:** el sistema toca la melodía completa
  (según la partitura de duraciones+alturas generada), el usuario la canta
  de vuelta.
- **Nivel 2 — Repeticiones reducidas:** el andamiaje se expresa como
  **cantidad de veces que se permite escuchar la melodía antes de cantarla**
  (reutiliza el "límite de repeticiones" ya usado como parámetro del DDA en
  la Idea 1) — arranca en 2-3 escuchas permitidas, baja a 1 sola escucha a
  medida que el DDA sube el nivel de exigencia para esa combinación de
  tags/longitud.

**Longitud de la melodía — RESUELTO: 10 a 20 notas en el nivel inicial,
ajustable por el DDA.** Valor deliberadamente exigente (más largo que lo
recomendado inicialmente en la sesión de grilling) — el DDA puede tanto
acortar como alargar el rango según el desempeño, como un parámetro más de
dificultad, análogo a como ya ajusta tolerancias y complejidad de tags en
las ideas anteriores.

**Gobernanza — mismo mecanismo que toda la tesis:** DDA por promedio móvil de
éxito sobre ventana de N rondas, por combinación específica de tags de
altura + longitud, con override manual disponible (`origen: manual|dda`,
rondas manuales no cuentan para el desbloqueo).

**Loop de una ronda:**

1. Se determina la combinación de tags de altura y la longitud objetivo
   según el curriculum desbloqueado (o el override manual), y se genera una
   melodía combinando el generador rítmico (duraciones) con el generador de
   alturas (alturas restringidas por tags).
2. Según el `nivel_andamiaje`: se reproduce la melodía completa, con el
   límite de repeticiones vigente disponible para repasar antes de cantar.
3. El usuario canta la secuencia completa; el segmentador híbrido (igual
   6.2) separa cada nota.
4. Si la cantidad de notas cantadas no coincide con la esperada, la ronda se
   marca como fallida sin evaluación nota por nota.
5. Si coincide, se calcula `(contorno_ok, intervalo_ok, CA_cents)` para cada
   intervalo consecutivo y se registra la serie completa.
6. Se envía el resultado agregado al motor DDA (salvo ronda `origen:
   manual`), que ajusta límite de repeticiones, longitud de la melodía y/o
   habilita la siguiente combinación de tags de altura.

**Datos a registrar por ronda:**
`{timestamp, tags_altura, longitud_melodia, nivel_andamiaje (1|2),
repeticiones_permitidas, repeticiones_usadas, origen (manual|dda),
cantidad_notas_cantadas, ronda_fallida_por_conteo (bool),
serie_por_nota: [(contorno_ok, intervalo_ok, CA_cents)], motor_PDA_usado
(YIN|ML), nivel_dificultad_antes, nivel_dificultad_despues}`

## 10.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
10.1–10.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Generación de melodías: procedural, combinando el generador rítmico ya
  existente (Idea 8, duraciones) con un generador de alturas nuevo y más
  simple (restricción directa por tags, sin rejection sampling) — 10.2.
- Curriculum de tags de altura: grados conjuntos → saltos pequeños → saltos
  grandes → cromatismos → mezclas — 10.2.
- Comparación: sin DTW, alineación 1:1 asumiendo cantidad de notas igual a
  la esperada (ronda fallida si no coincide); evaluación de dos niveles por
  nota (`contorno_ok`, `intervalo_ok`) en vez de un CA promediado — 10.2.
- Escalera de andamiaje: 2 niveles adaptados de la Idea 1 (imitación
  completa / repeticiones reducidas), sin equivalente al Nivel 3 de
  producción libre — 10.3.
- Longitud de melodía: 10-20 notas en el nivel inicial, ajustable por el
  DDA — 10.3.
- Alcance de tesis: dentro del MVP — 10.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el
desbloqueo de nivel/tags; cantidad inicial de repeticiones permitidas;
tolerancias en cents para `intervalo_ok`; rango concreto de longitud de
melodía por nivel de dificultad.

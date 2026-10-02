# 2. Intervalos "a ciegas" con distractor tímbrico

> Se reproduce el intervalo con un timbre distinto cada ronda (piano, voz,
> sintetizador) para evitar que el usuario memorice por color de sonido en vez de por
> relación de altura.

**Estado:** DESARROLLADA

## 2.1 Fundamento

**El problema que ataca es real y documentado en ear-training.** Cuando un intervalo
siempre se presenta con el mismo timbre (ej. siempre piano MIDI), el usuario puede
desarrollar una heurística espuria: reconoce el "color" combinado de las dos notas con
ese timbre particular en vez de internalizar la relación de altura en abstracto. Es un
caso de *overfitting* perceptual al estímulo de entrenamiento, y es una crítica
conocida a los drills estáticos de ear-training tradicionales (el usuario "aprueba el
test" sin haber generalizado la habilidad). Randomizar el timbre por ronda fuerza a que
la única invariante entre rondas sea la relación de altura — que es, precisamente, lo
que se quiere entrenar.

**Conexión directa con una de las líneas de investigación de más alto nivel de la
tesis.** La Oportunidad 4 de `InvestigacionSistematica.md` (Self-Supervised Learning /
SPICE, embeddings sobre CQT) parte exactamente de esta premisa: la percepción musical
humana privilegia relaciones de tono *relativas* e invariantes a color tímbrico, no
identificaciones absolutas. Esta idea es, en el fondo, la puesta en juego pedagógica de
esa misma hipótesis: si el usuario mejora entrenando con distractor tímbrico, es
evidencia indirecta (conductual, no solo algorítmica) a favor de la hipótesis que
sustenta la Oportunidad 4. Esto le da a un minijuego aparentemente simple una lectura
de instrumento de validación experimental, no solo de contenido.

**Rol dentro del conjunto de mecánicas — RESUELTO: solo variante (b), integrada al
motor de la Idea 1.** El brainstorming original planteaba dos formas de implementar
esto, no mutuamente excluyentes:

- **(a) Minijuego de reconocimiento independiente** (selección múltiple del nombre
  del intervalo escuchado, timbre randomizado por ronda) — de **percepción**, no de
  producción.
- **(b) Modificador/distractor inyectado en el motor de la Idea 1**: en vez de
  reproducir siempre la referencia con un timbre neutro, randomizar el timbre de la
  referencia por ronda. Fusiona percepción + producción en una sola mecánica,
  reutilizando el motor de captura/DDA de la Idea 1 sin construir un segundo sistema
  de evaluación.

**Se prioriza únicamente (b)** para el alcance de esta idea, siguiendo el mismo
patrón de separación que con la Idea 5 (instrumento): (a) es un tipo de habilidad
distinto (reconocimiento pasivo vs. producción), y mezclar dos mecánicas de
evaluación en un mismo documento diluye el foco. Si en algún momento se retoma el
reconocimiento standalone, se documenta como idea hermana separada — no forma parte
del alcance de esta idea.

## 2.2 Base técnica

A diferencia de la Idea 1, el desafío técnico central acá **no es el detector de
pitch (PDA) sino el motor de síntesis/reproducción**: hay que producir el mismo
intervalo exacto (mismo salto en cents) en distintos timbres, de forma determinista y
con latencia baja, usando Oboe también del lado de **playback** (no solo captura).

**Opciones para el banco de timbres:**

| Enfoque | Ventaja | Riesgo |
|---|---|---|
| Muestras multi-sample por semitono (soundfont-like: piano, voz sintética, pad) | Timbre realista, sin artefactos de pitch-shifting grande | Peso de assets (múltiples instrumentos × rango completo), necesita interpolación fina solo para el ajuste de cents residual (no para saltos grandes) |
| Síntesis paramétrica (aditiva/FM) por receta armónica | Ligero, pitch exacto matemáticamente garantizado, sin artefactos | Timbres "sintéticos", menos variedad tímbrica realista que muestras reales |
| Pitch-shifting agresivo de una única muestra base | Un solo asset por timbre | **Riesgo alto**: el pitch-shifting puede introducir corrimientos o artefactos que desafinen sutilmente el intervalo objetivo — inaceptable en un juego cuya métrica central es precisión en cents. Se descarta como enfoque principal. |

**Banco de timbres inicial — RESUELTO: 4 timbres.** Piano y voz sintética
(multi-sample por semitono) + onda senoidal pura y onda cuadrada tipo synth clásico
(síntesis paramétrica). Da variedad real entre "realista acústico", "vocal", "tono
puro" y "sintético agresivo" sin disparar el costo de producir múltiples instrumentos
multi-sample completos. **Los timbres multi-sample (piano, voz) se pre-renderizan
como assets estáticos del build; los sintéticos (senoidal, cuadrada) se generan en
runtime vía síntesis paramétrica** — se desprende directamente de por qué se eligió
cada enfoque por timbre (los paramétricos ya garantizan pitch exacto sin necesidad de
asset, los multi-sample se benefician de sonar reales sin artefactos de síntesis en
tiempo real).

**Restricción no negociable:** el intervalo debe ser exacto en cents independientemente
del timbre elegido — el timbre es una variable de UI/percepción, nunca debe filtrarse
como ruido en la métrica de "verdad" del ejercicio (a diferencia de la Idea 1, acá el
sistema controla el estímulo, no la respuesta del usuario, así que la precisión
depende enteramente del motor de síntesis, no de un PDA).

**Latencia:** relevante para el modo (b) fusionado — el intervalo con timbre
randomizado se reproduce, y de inmediato el usuario debe empezar a cantar; cualquier
demora perceptible entre la reproducción y la ventana de captura rompe el flujo del
loop de Idea 1. Esto reutiliza la misma exigencia de latencia determinista de Oboe ya
establecida como requisito del MVP (§4.1 de la investigación).

## 2.3 Diseño

**Alcance dentro de la escalera de andamiaje de la Idea 1 — RESUELTO: solo Nivel 1.**
El distractor tímbrico randomiza el timbre del **intervalo completo reproducido**, lo
cual solo existe en el Nivel 1 (imitación) de la escalera de 3 niveles de la Idea 1
— el Nivel 2 reproduce solo la ancla (una nota aislada no tiene "color combinado" que
memorizar por timbre en vez de por altura) y el Nivel 3 no reproduce nada. En cuanto
un intervalo específico asciende a Nivel 2/3, el distractor tímbrico deja de aplicar
para ese intervalo — se considera que la invariancia tímbrica ya se entrenó lo
suficiente en Nivel 1.

**Modificador dentro del motor de Idea 1:**
- Se añade `timbre_referencia` como parámetro más de cada ronda de Nivel 1 (junto a
  `intervalo_objetivo` y `nivel_andamiaje`).
- Dato adicional a registrar por ronda: `timbre_referencia_usado`, para analizar si
  el CA del usuario varía sistemáticamente según el timbre (validación conductual de
  la hipótesis de invariancia tímbrica de la Oportunidad 4).

**Gobernanza de la variedad tímbrica — RESUELTO: global, no por intervalo
específico.** A diferencia del desbloqueo de `nivel_andamiaje` (por intervalo, porque
la dificultad auditiva sí varía por intervalo), la invariancia tímbrica es una
habilidad transversal: una vez que el usuario demuestra CA estable con timbre fijo
(ej. piano) en Nivel 1 sobre un subconjunto de intervalos ya practicados, el timbre
se randomiza para **todos** los intervalos de ahí en adelante, incluidos los que se
introduzcan después en el curriculum — no hay que re-demostrar invariancia tímbrica
por cada intervalo nuevo. Se registra un flag global
`variedad_timbrica_habilitada: bool` (no por intervalo).

**Loop de una ronda en Nivel 1 (con distractor tímbrico ya habilitado):**
1. Se sortea un intervalo objetivo según el curriculum fijo de la Idea 1, en Nivel 1.
2. Se sortea un timbre del banco disponible (si `variedad_timbrica_habilitada`;
   si no, se usa el timbre fijo por defecto).
3. El sistema reproduce el intervalo completo (ancla + objetivo) con ese timbre.
4. El usuario canta de vuelta (loop de Idea 1 sin cambios desde acá).
5. Se registra `timbre_referencia_usado` junto al resto de los datos de la ronda.

## 2.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver 2.1–2.3,
marcadas **RESUELTO**):

- Solo variante (b) entra al alcance de esta idea; la variante (a) de reconocimiento
  standalone queda fuera, documentable aparte si se retoma en el futuro — 2.1.
- Distractor tímbrico limitado al Nivel 1 de la escalera de la Idea 1 — 2.3.
- Banco de timbres inicial: piano + voz sintética (multi-sample, pre-renderizados) +
  senoidal + onda cuadrada (síntesis paramétrica, runtime) — 2.2.
- Gobernanza de la variedad tímbrica: global, no por intervalo específico — 2.3.

**Lo único que queda como valor de tuning:** el umbral de CA y la cantidad de
intervalos sobre los que se mide la estabilidad antes de habilitar
`variedad_timbrica_habilitada` — a calibrar empíricamente, no una decisión de diseño.

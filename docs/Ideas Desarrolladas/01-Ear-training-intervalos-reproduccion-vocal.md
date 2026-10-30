# 1. Ear-training de intervalos por reproducción vocal

> El sistema reproduce un intervalo, el usuario lo canta de vuelta con el micrófono,
> se mide la afinación en cents en tiempo real.

**Estado:** DESARROLLADA

> Implementación parcial, 2026-10-30: [V1 del núcleo de imitación](../Intervalos-vocales-V1.md)
> evalúa dos notas sostenidas respecto del ancla cantada y conserva la serie.
> [V2](../Intervalos-vocales-V2.md), autorizada con «dale, hacelo», conecta una ronda
> manual con referencia/micrófono y resumen en memoria. Android sin ejecutar por
> orden de no emular. [V3](../Intervalos-vocales-V3.md) agrega persistencia, historial,
> exportación JSON/ZIP, pendientes y reintento. [V4](../Intervalos-vocales-V4.md)
> implementa Mamdani entre rondas de series de imitación y progresión por contenido/
> dirección, con inspección y simulaciones. [V5](../Intervalos-vocales-V5.md),
> autorizada con «Dale, vamos con ancla dada», reproduce solo el ancla y captura
> el objetivo, con prerrequisito de imitación y progreso independiente por dirección.
> [V6](../Intervalos-vocales-V6.md), autorizada con «dale, segui con eso», agrega
> producción libre sin audio, ancla propia y dos capturas, con acceso por hito de
> ancla dada y progreso independiente. Selección automática global de ayuda, CA
> y ML siguen pendientes. El orden y los parámetros de V4–V6 son experimentales del agente.
> La prioridad vocal procede de la entrevista del informe del 2026-10-30.
> Los umbrales/promedio móvil de esta ficha requieren revisión frente a la
> decisión posterior de usar Mamdani; V4–V6 los sustituyen para los tres andamiajes
> implementados con elección explícita. Oboe/ML permanecen
> sujetos a medición y acuerdo con el director según el contraste del protocolo.

## 1.1 Fundamento

**Pedagógico.** La literatura de *aural skills* de conservatorio distingue percepción
(reconocer un intervalo escuchado) de producción (reproducirlo vocalmente), y trata la
segunda como la habilidad de orden superior — exige que el modelo auditivo interno se
traduzca en control motor fonatorio preciso. La inmensa mayoría de las apps comerciales
relevadas en la investigación (Smule Ocarina, Music Tutor, Music Crab, Notes Teacher,
Note Brainer — §3.3 de `InvestigacionSistematica.md`) evalúan por **selección múltiple**
o comparación pasiva, no por producción activa. Esta idea ocupa ese hueco: convierte el
ear-training en un ejercicio de producción, no de reconocimiento.

**Por qué esta idea es el vehículo correcto para la tesis (no solo para el producto).**
La investigación concluye explícitamente que "gamificar la música" ya está agotado como
aporte original (línea 449); el aporte real debe estar en la arquitectura computacional
subyacente. Esta idea es la que **obliga** a construir esa arquitectura, porque:

- La voz cantada es la señal de entrada más "sucia" del dominio (vibrato, formantes,
  ruido de sibilancia, glissandos de aproximación a la nota) — es la que más justifica
  el **multiplexor híbrido DSP↔ML** (Oportunidad 7 de la investigación: YIN por defecto,
  conmutación a un modelo ML cuando la métrica de ruido/entropía supera un umbral).
- Medir en **cents continuos** (no acierto/error binario) es precisamente el insumo
  matemático que necesita el **DDA por varianza microtonal** (Oportunidad 2): el sistema
  captura una serie temporal de desviación en cents, calcula su entropía/varianza, y
  ajusta la dificultad (tempo, ventana de tolerancia, complejidad del intervalo) en
  función de eso — no de un simple contador de aciertos.
- No requiere instrumento externo → viable en un dispositivo móvil sin hardware adicional,
  coherente con la restricción de "sin XR, sin hardware pesado" (§4.3, descartes del MVP).

**Gap que cubre en el DGBL relevado.** Ninguna de las apps comerciales citadas hace DDA
basado en varianza continua de afinación; casi todas usan progresión de niveles fija o
por aciertos acumulados. Este es el hueco explotable.

## 1.2 Base técnica

**Alcance de señal.** Monofónico continuo únicamente (una nota cantada por vez, sin
acordes ni polifonía) — coherente con el descarte explícito de "Extracción Directa de
Tono Vocal en Entornos Polifónicos" en §4.3.

**Pipeline (de captura a feedback), mapeado a los 4 componentes obligatorios del MVP:**

```
Micrófono (Oboe, AAudio/OpenSL ES)
   → Ring buffer lock-free (productor: hilo de audio nativo C++)
   → Worker de PDA (consumidor 1): YIN vectorizado, ventana 20–32 ms
        ↳ Multiplexor de entropía (Oportunidad 7):
            si SHR/varianza espectral > umbral → swap a modelo ML ligero
            residente en memoria (candidato: SwiftF0, MLP <0.005 ms/muestra,
            ~0.639 MB — la fila con mejor viabilidad edge de la tabla 4.2)
   → Vector F0 (Hz) → conversión a cents respecto de la nota objetivo
   → Ring buffer de salida (consumidor 2: motor gráfico / UI thread)
   → Motor DDA: acumula serie temporal de error en cents, calcula varianza/entropía
        en ventana deslizante → ajusta parámetro de dificultad
```

**Por qué YIN como baseline y no CREPE/pYIN directamente:** la tabla comparativa de
§4.2 descarta CNNs profundas (CREPE) para inferencia in-situ por costo computacional
(~90x más lento que DSP), y posiciona YIN vectorizado como la opción de "viabilidad
altísima" para MVP. El modelo ML solo entra como *fallback* del multiplexor cuando el
ruido lo justifica — no como motor por defecto. Esto es una decisión de arquitectura,
no solo de producto.

**Nota objetivo / referencia de afinación — RESUELTO: pitch relativo.** El sistema
nunca exige una frecuencia absoluta (ej. A4 = 440 Hz) ni calibración previa de rango
vocal. Se evalúa siempre el salto en cents entre una nota ancla y la nota objetivo,
sin importar en qué octava/tonalidad cante el usuario. Esto es coherente con la
Oportunidad 4 (SPICE/CQT, aprendizaje de relaciones relativas) y evita la fricción de
calibrar la voz de cada usuario.

Cómo se fija la nota ancla en cada caso (cantada por el usuario, reproducida por el
sistema, o ambas cosas) ya no es una variante libre — queda determinado por el
**nivel de andamiaje** de la escalera descripta en 1.3.

**Nota sostenida válida — RESUELTO.** Se cierra la medición con una ventana de tiempo
fija (ej. 400–600 ms) y tolerancia simple en cents (sin distinguir algorítmicamente
vibrato de desafinación). A cambio, la UI **instruye explícitamente al usuario a no
cantar con vibrato** antes de cada ronda — es una restricción de diseño pedagógico
razonable para un minijuego de reconocimiento/producción de intervalos (el vibrato no
es parte de la habilidad que se está evaluando acá), y evita la complejidad de
detectar periodicidad de baja frecuencia en la señal de cents.

**Métrica de evaluación:** Cents Accuracy (CA), penalización exponencial de la
desviación (no lineal) — tal como se describe en Oportunidad 2. Se registra la serie
temporal completa por intento, no solo el valor final, para alimentar el DDA y para
la telemetría de validación empírica de la tesis (RPA/CA como variable dependiente).

**Multiplexor DSP↔ML — arquitectura en cascada (RESUELTO).** YIN corre siempre sobre
cada frame; la métrica de ruido/entropía (SHR o varianza espectral) se calcula
directamente sobre la señal, **sin invocar el modelo ML**. Solo cuando esa métrica
supera el umbral se invoca el modelo ML (ej. SwiftF0) bajo demanda, para ese frame
puntual. Se descarta la variante "paralelo" (correr YIN y ML en cada frame y elegir
cuál resultado usar) por gastar cómputo del modelo en casi todos los frames sin
necesidad, contradiciendo la premisa de eficiencia que motivó elegir YIN como default.
El valor concreto del umbral que dispara el swap sigue siendo una **variable
experimental de la tesis** (Oportunidad 1: benchmarking A/B de motores), no una
constante a fijar a priori — lo que se resolvió acá es la arquitectura del
multiplexor, no el valor del umbral.

## 1.3 Diseño de la mecánica de juego

**Escalera de 3 niveles de andamiaje auditivo (reemplaza el binario guiado/ciego).**
En vez de dos modos separados con reglas propias, hay un único parámetro continuo
por intervalo — `nivel_andamiaje: 1 | 2 | 3` — que determina cuánta información
auditiva da el sistema antes de que el usuario cante:

- **Nivel 1 — Imitación:** el sistema reproduce el intervalo completo (ancla +
  objetivo) y el usuario lo canta de vuelta imitando lo que acaba de escuchar. Modo
  de entrada: evalúa memoria auditiva a corto plazo + reproducción vocal.
- **Nivel 2 — Ancla dada:** el sistema reproduce únicamente la nota ancla y dice el
  nombre teórico del intervalo ("cantá una 6ª menor ascendente desde acá"); el
  usuario canta solo la nota objetivo. Ya no hay intervalo completo que imitar, pero
  sigue habiendo una referencia externa fija y exacta.
- **Nivel 3 — Producción libre:** el sistema no reproduce nada, solo informa el
  nombre del intervalo por texto/UI. El usuario elige su propia nota de partida y
  canta ambas notas (ancla + objetivo) desde su propio criterio. Es la habilidad más
  avanzada: en conservatorio equivale a la diferencia entre "dictado" y "solfeo
  activo/producción" — no hay modelo auditivo externo que imitar, solo el modelo
  interno del propio usuario.

**Gobernanza del nivel — automática por defecto, con override manual.** El DDA baja
al usuario un escalón (1→2→3) **por intervalo específico** cuando el promedio móvil
de CA sobre una ventana de N rondas para ese intervalo supera un umbral (N y el
umbral son parámetros de tuning, no de diseño — ver nota al final de esta sección).
No tiene sentido medir precisión en Nivel 3 si el usuario no domina ese mismo
intervalo en Nivel 1 primero.

El usuario también puede **seleccionar manualmente** cualquier nivel para practicar,
sin esperar a que el DDA se lo habilite. Para no romper la integridad del desbloqueo
ni contaminar los datos que alimentan al DDA, las rondas jugadas en modo manual se
etiquetan `origen: manual` y **no cuentan** para el promedio móvil que gobierna el
desbloqueo automático (que solo considera rondas con `origen: dda`). Esto también
habilita un análisis de tesis adicional: comparar CA en `origen: manual` vs.
`origen: dda`.

**Curriculum de intervalos: fijo, no adaptativo.** El *orden de introducción* de
intervalos nuevos (unísono/octava → 5ª/4ª → 3ª/6ª → 2ª/7ª disonantes → cromatismos)
sigue una secuencia predefinida igual para todos los usuarios, alineada con la
progresión estándar de aural skills de conservatorio. Esto es independiente y
ortogonal al desbloqueo de nivel de andamiaje (que sí es adaptativo, pero *dentro*
de cada intervalo ya introducido, no en el orden de aparición de intervalos nuevos).
Se prioriza así la comparabilidad entre usuarios para la validación de tesis por
sobre una adaptatividad que no tiene evidencia de aportar en este eje.

**Loop de una ronda:**

1. Se define un intervalo objetivo según el curriculum fijo (qué intervalos están
   habilitados para este usuario) y su `nivel_andamiaje` actual para ese intervalo
   (gobernado por el DDA, o forzado manualmente).
2. Según el nivel: se reproduce el intervalo completo (Nivel 1), se reproduce solo
   la ancla + nombre teórico (Nivel 2), o se informa solo el nombre teórico sin
   audio (Nivel 3).
3. El usuario canta: en Niveles 1 y 3, primero la ancla (propia o imitada) y luego
   la objetivo; en Nivel 2, solo la objetivo (la ancla ya la fijó el sistema con
   precisión exacta, no hace falta que el usuario la re-cante).
4. En tiempo real, mientras el usuario sostiene la nota objetivo, la UI muestra un
   indicador continuo de cents de desviación (no solo un check/cross al final) — barra
   o aguja que se centra cuando está afinado.
5. Al soltar la nota (ventana de sostenido válida, ver 1.2), se congela el CA de esa
   ronda y se envía al motor DDA (salvo que la ronda sea `origen: manual`).
6. El DDA ajusta el próximo intervalo/nivel: tolerancia en cents, `nivel_andamiaje`
   (si corresponde subir de escalón para este intervalo), repeticiones permitidas del
   estímulo, y/o introducción del siguiente intervalo del curriculum.

**Parámetros que el DDA puede mover (candidatos):**
- Tolerancia en cents para considerar "afinado" (se estrecha con buen desempeño).
- `nivel_andamiaje` por intervalo (1→2→3, ver arriba).
- Límite de repeticiones del estímulo de audio (ver UI, abajo) — arranca generoso
  cuando se introduce un intervalo nuevo en Nivel 1, y se reduce a medida que el CA
  se estabiliza, siguiendo la misma lógica de "menos andamiaje con el tiempo".
- Tiempo de preparación antes de tener que empezar a cantar.
- Uso de distractor tímbrico (idea #2 del brainstorming, reutilizable como modificador
  de dificultad dentro de este mismo motor).

**UI mínima necesaria:**
- Reproductor del intervalo/ancla según nivel, con botón de repetir sujeto al
  límite de repeticiones gobernado por el DDA (no aplica en Nivel 3, no hay audio
  que repetir).
- Indicador de pitch en tiempo real (aguja o barra horizontal centrada en 0 cents).
- Feedback post-ronda — **RESUELTO:** CA numérico de la ronda + `nivel_andamiaje`
  actual + progreso hacia el próximo desbloqueo para ese intervalo (ej. "3 de 5
  rondas necesarias para pasar a producción libre"). Se decide **no** mostrar la
  curva de dificultad completa ni los parámetros finos del DDA (tolerancia en cents,
  repeticiones permitidas) — evita el riesgo de "seductive details" (RQ3) mostrando
  progreso hacia una meta clara en vez del mecanismo interno de ajuste.

**Datos a registrar por ronda (para DDA y para telemetría de tesis):**
`{timestamp, intervalo_objetivo, nivel_andamiaje (1|2|3), origen (manual|dda),
serie_temporal_cents, CA_final, motor_PDA_usado (YIN|ML), tiempo_hasta_nota_estable,
repeticiones_usadas, nivel_dificultad_antes, nivel_dificultad_despues}`

**Nota sobre parámetros de tuning vs. decisiones de diseño.** El valor concreto de N
(tamaño de ventana del promedio móvil) y el umbral de CA que dispara el desbloqueo de
nivel no se fijan en este documento — son variables a calibrar empíricamente durante
el desarrollo/validación, no decisiones de arquitectura. Lo mismo aplica al umbral de
entropía/SHR del multiplexor (ver 1.2).

## 1.4 ¿Es jugable con instrumentos (violín, flauta, clarinete, guitarra), no solo voz?

**RESUELTO: queda fuera del alcance de esta idea.** El modo por instrumento no se
implementa como una variante dentro de este minijuego — se documenta como una idea
hermana separada, **Idea 5 — "Interval Playing" con instrumento**
(`05-Interval-Playing-instrumento.md`), que reutiliza el mismo pipeline agnóstico a
la fuente descripto abajo, pero donde el usuario toca ambas notas del intervalo con
su instrumento en vez de cantarlas. Se mantiene el análisis de por qué el pipeline
es agnóstico a la fuente (relevante como fundamento de esa idea hermana), pero la
decisión de alcance (dentro o fuera del MVP de tesis) se trata en ese documento, no
en este.

**Técnicamente, sí, sin cambiar el motor.** El pipeline de la sección 1.2 (Oboe →
ring buffer → YIN/multiplexor → cents) no asume nada sobre la fuente del sonido:
YIN y sus alternativas operan sobre cualquier señal monofónica cuasi-periódica, sea
voz o un instrumento afinado tocando una nota por vez. No haría falta un motor de PDA
distinto — el mismo pipeline sirve.

**Pero conviene NO mezclarlo dentro del mismo modo/ronda por varias razones:**

- **Contradice la razón de ser original de la idea.** La ventaja explícita de elegir
  voz (línea de `IdeasPuntuales.md`: "no requiere que el usuario tenga un instrumento
  a mano, clave para una app mobile") desaparece si el core del minijuego termina
  dependiendo de tener un violín o clarinete en la mano. Como *modo alternativo
  opcional*, no como reemplazo, no hay conflicto.
- **Cada instrumento estresa el PDA de forma distinta — eso es una variable
  experimental, no un detalle de UI.** La propia investigación señala como línea de
  tesis legítima la "resiliencia algorítmica ante espectros fuertemente inarmónicos"
  (línea 449 de `InvestigacionSistematica.md`). Instrumentos como el clarinete
  (predominio de armónicos impares, débil fundamental → riesgo de error de octava en
  YIN) o el violín (vibrato intenso, ataque con ruido de arco) son casos de prueba
  reales para ese problema. Mezclarlos sin control experimental (sin saber qué
  instrumento generó qué error) contamina cualquier medición seria de RPA/CA por
  fuente.
- **Calibración de rango:** una guitarra y un violín tienen tesituras muy distintas
  entre sí y respecto de la voz humana — el intervalo objetivo y la nota ancla
  tendrían que adaptarse al rango del instrumento seleccionado, agregando una capa de
  configuración que no existe en el modo vocal (que ya resolvimos como pitch relativo,
  sección 1.2).

**Recomendación de diseño:** mantener voz como input por defecto del MVP (así se
preserva el control experimental simple: una sola fuente tímbrica, foco en DDA +
multiplexor). Diseñar el módulo de captura como **agnóstico a la fuente** desde el
principio (ya lo es, por construcción del pipeline), y dejar la selección de
instrumento como un **modo/config explícito y separado** ("elegí tu fuente: voz /
violín / flauta / clarinete / guitarra"), no como una opción mezclada dentro de la
misma ronda ni el mismo dataset de validación. Esto abre naturalmente una línea de
trabajo futuro (o un experimento secundario de la tesis, reusando a la Oportunidad 1
de benchmarking): comparar el RPA/CA y la tasa de activación del multiplexor ML por
tipo de fuente tímbrica. Se conecta también con la idea F19/F20 del brainstorming
(reconocimiento e identidad tímbrica) — mismo motor de captura, otro objetivo
pedagógico.

## 1.5 Decisiones de diseño — historial de resolución

Todas las decisiones de arquitectura y mecánica quedaron resueltas en sesión de
`/grill-me` (ver secciones 1.2–1.4 arriba, marcadas **RESUELTO**):

- Pitch relativo (no absoluto) — 1.2.
- Nota sostenida válida: ventana + tolerancia simple, instrucción anti-vibrato — 1.2.
- Escalera de 3 niveles de andamiaje reemplazando guiado/ciego, desbloqueo por
  intervalo específico vía promedio móvil, con override manual — 1.3.
- Curriculum de intervalos fijo, ortogonal al desbloqueo de andamiaje — 1.3.
- Multiplexor DSP↔ML en arquitectura cascada — 1.2.
- Límite de repeticiones como parámetro más del DDA — 1.3.
- Feedback post-ronda: nivel + progreso, sin curva de dificultad completa — 1.3.
- Modo por instrumento: fuera de esta idea, pasa a ser la Idea 5 — 1.4.

**Lo único que queda genuinamente abierto son valores de tuning, no decisiones de
diseño** (a calibrar empíricamente durante desarrollo/validación, no a priori):
- Tamaño de ventana N y umbral de CA para el promedio móvil de desbloqueo de nivel.
- Umbral concreto de entropía/SHR que dispara el swap DSP→ML — variable experimental
  de la tesis en sí misma (Oportunidad 1: benchmarking A/B de motores).
- Valores concretos de tolerancia en cents, límite de repeticiones inicial, y tiempo
  de preparación por nivel de dificultad.

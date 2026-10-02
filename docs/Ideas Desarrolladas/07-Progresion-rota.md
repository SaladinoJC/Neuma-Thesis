# 7. Progresión rota

> Se reproduce una progresión de acordes con uno "fuera de lugar" (ajeno a la
> tonalidad) insertado en una posición aleatoria; el usuario debe marcar en
> qué compás ocurrió — entrena oído armónico funcional, no solo
> identificación aislada de acordes.

**Estado:** DESARROLLADA

## 7.1 Fundamento

**Por qué esta idea no usa el pipeline de captura de voz de las Ideas 1-6.**
A diferencia de todas las ideas anteriores, acá el jugador solo escucha y
marca un compás (tap en pantalla) — no hay señal de audio de entrada del
usuario que capturar ni procesar. Esto la separa del eje central de la tesis
(`InvestigacionSistematica.md`, línea 449: "gamificar la música" está agotado
como aporte original, el aporte real debe estar en la arquitectura
computacional del multiplexor DSP↔ML y el DDA por captura de voz). El
multiplexor no aplica acá en absoluto.

**Por qué igual vale la pena, encuadrada correctamente.** El motor DDA
genérico ya validado en las Ideas 1, 5 y 6 no depende de que la señal de
desempeño venga de pitch-tracking — depende de tener una serie de resultados
de ronda sobre la que calcular varianza/promedio móvil y ajustar dificultad.
Esta idea aporta una **cuarta señal de desempeño distinta** para ese mismo
motor: `(compás_marcado_correcto: bool, distancia_en_compases_al_real,
tiempo_de_reacción_ms)`, sumándose a la serie continua de cents (Ideas 1/2),
el triple discreto de instrumento (Idea 5) y el categórico de acorde
(Idea 6). No aporta nada al multiplexor DSP↔ML, pero sí extiende la
validación empírica del componente DDA a un cuarto tipo de señal.

**Pedagógico.** Entrena oído armónico **funcional** (relación entre acordes
dentro de una progresión, no identificación aislada de un acorde suelto) —
un nivel de habilidad distinto y más avanzado que el de las Ideas 1 y 6, que
evalúan intervalos/acordes de forma aislada. Cubre un hueco que la propia
investigación no señala explícitamente para este caso puntual, pero que es
consistente con el diagnóstico general del documento: casi toda la oferta
comercial relevada (§3.3) evalúa reconocimiento aislado de elementos
musicales, no relaciones funcionales entre ellos dentro de una progresión.

**Rol dentro de la tesis — CONFIRMADO: trabajo futuro / experimento
secundario, fuera del MVP.** Mismo argumento que dejó a la Idea 5 fuera del
núcleo: no abre una pregunta de investigación nueva ni aporta un componente
de arquitectura genuino (a diferencia de la Idea 6, que sí sumó el
segmentador híbrido de notas). Es una reutilización más del motor DDA
genérico ya cerrado, con valor de producto (catálogo de minijuegos) y de
validación empírica adicional del DDA, pero no de núcleo arquitectónico.

## 7.2 Base técnica

**Sin pipeline de audio de entrada.** No hay micrófono, ring buffer, PDA ni
multiplexor DSP↔ML involucrados — la única señal de audio es de **salida**
(reproducción de la progresión pregrabada/sintetizada). El único componente
compartido con las ideas anteriores es el **motor DDA genérico** (promedio
móvil de éxito sobre ventana de N rondas, con override manual y etiquetado
`origen: manual|dda`, igual mecanismo que en Ideas 1, 5 y 6).

```
Selección de progresión (banco fijo, ver 7.3) + acorde ajeno (posición e
identidad según nivel de curriculum actual)
   → Reproducción de audio (sintetizador o samples pregrabados, sin captura)
   → Captura de input del usuario (tap en pantalla, con timestamp)
   → Evaluación: compás tapeado vs. compás real, tiempo_de_reacción_ms
   → Motor DDA genérico (mismo componente de Ideas 1/5/6)
```

**Generación de contenido — RESUELTO: banco fijo de progresiones
pre-compuestas, no generación procedural.** Componer progresiones
armónicamente coherentes por reglas de gramática funcional/voice-leading es
un problema de composición algorítmica no trivial, desconectado del eje de
investigación de la tesis (DDA/multiplexor). Se usa en cambio un **banco fijo
de progresiones estándar** (I-IV-V-I, I-vi-IV-V, ii-V-I, etc.), mismo
criterio que el curriculum fijo de intervalos/acordes de las Ideas 1 y 6
(comparabilidad entre usuarios por sobre generación dinámica sin evidencia de
aportar).

**Longitud de la progresión — RESUELTO: 4 compases/acordes.** Suficiente para
que "detectar en qué compás" sea una tarea real (no trivial con solo 2
acordes), pero corta para no exigir memoria auditiva más allá de lo que la
mecánica necesita medir.

**Definición del acorde "fuera de lugar" — RESUELTO: curriculum progresivo de
3 niveles de ajenidad, no una sola definición fija:**

1. **Nivel A — ajeno total a la escala:** acorde completamente fuera de la
   tonalidad, disonancia obvia. Sirve de calibración inicial de la mecánica
   de tap antes de exigir oído funcional fino.
2. **Nivel B — préstamo modal/cromático:** acorde con alguna nota fuera de
   la escala diatónica pero que "casi encaja" (ej. subdominante menor
   prestada en tonalidad mayor) — exige oído armónico funcional real.
3. **Nivel C — acorde diatónico en función incorrecta:** todas las notas
   pertenecen a la escala; lo que rompe es la lógica funcional/cadencial
   (ej. resolución a un grado inesperado donde se esperaba una cadencia
   típica). El caso más sutil, más cercano al "error típico de oído
   intermedio" original de la idea.

Este curriculum es análogo (mismo criterio de progresión fija) al ya resuelto
para intervalos (Idea 1) y calidades de acorde (Idea 6).

## 7.3 Diseño

**Mecanismo de interacción — RESUELTO: conviven dos modos como escalón de
andamiaje gobernado por el DDA, no como modos libres sin jerarquía:**

- **Modo "escuchar y elegir después" (más fácil):** el usuario escucha la
  progresión completa de 4 compases y luego elige de una lista/línea de
  tiempo en qué compás ocurrió el acorde ajeno, sin presión de tiempo. Señal
  de desempeño: solo precisión (`compás_marcado_correcto`,
  `distancia_en_compases_al_real`) — no aplica tiempo de reacción real.
- **Modo "tap en tiempo real" (más difícil):** el usuario tapea en el momento
  en que percibe el acorde ajeno, mientras la progresión suena, sin poder
  escuchar todo antes ni corregir después. Señal de desempeño: precisión +
  `tiempo_de_reacción_ms`.

**Gobernanza — mismo mecanismo que `nivel_andamiaje` en las Ideas 1 y 6**: el
DDA sube de "escuchar y elegir después" a "tap en tiempo real" **por
progresión específica**, sobre ventana móvil de N rondas, cuando el promedio
de éxito supera un umbral. Override manual disponible, con etiquetado
`origen: manual|dda` (rondas manuales no cuentan para el desbloqueo) — mismo
patrón ya usado en las tres ideas anteriores.

**Manejo de falsos positivos/negativos en modo tap en tiempo real —
RESUELTO:** si el usuario no tapea en ningún compás durante la reproducción,
se registra como error total (sin detección). Si tapea en más de un compás,
cuenta el **primer tap** como la respuesta — no se permite corregir después,
coherente con que la señal que se mide es reacción real, no un intento
editable.

**Curriculum de niveles de ajenidad (A→B→C) — RESUELTO: orden de introducción
fijo, ortogonal al andamiaje de modo de interacción.** Igual que en las Ideas
1 y 6, el orden de introducción de niveles de ajenidad es fijo e igual para
todos los usuarios, y es independiente del escalón de andamiaje
(escuchar-y-elegir vs. tap-en-vivo), que se gobierna por separado y por
progresión específica.

**Posición del acorde ajeno dentro de la progresión:** aleatoria en cada
ronda (no fija ni predecible), para evitar que el usuario memorice la
posición en vez de detectarla de oído.

**Loop de una ronda:**

1. Se selecciona una progresión del banco fijo y un nivel de ajenidad (A, B o
   C) según el curriculum, más una posición aleatoria para el acorde ajeno.
2. Se determina el modo de interacción actual (escuchar-y-elegir / tap-en-vivo)
   para esa progresión específica, según el DDA o el override manual.
3. Se reproduce la progresión completa.
4. Modo escuchar-y-elegir: al terminar, el usuario selecciona el compás de una
   lista. Modo tap-en-vivo: el usuario tapea durante la reproducción; se
   registra el primer tap con su timestamp.
5. Se evalúa `(compás_marcado_correcto, distancia_en_compases_al_real,
   tiempo_de_reacción_ms si aplica)` contra el compás real del acorde ajeno.
6. Se envía el resultado al motor DDA (salvo ronda `origen: manual`), que
   ajusta el modo de interacción de esa progresión y/o habilita el siguiente
   nivel de ajenidad del curriculum.

**Datos a registrar por ronda:**
`{timestamp, progresion_id, nivel_ajenidad (A|B|C), modo_interaccion
(escuchar_elegir|tap_en_vivo), origen (manual|dda), compas_real,
compas_marcado, compas_marcado_correcto (bool), distancia_en_compases,
tiempo_de_reaccion_ms (null si modo escuchar_elegir), nivel_dificultad_antes,
nivel_dificultad_despues}`

## 7.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
7.1–7.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Encuadre dentro de la tesis: sin pipeline de captura de voz ni multiplexor
  DSP↔ML — cuarto caso de uso del motor DDA genérico, con señal de desempeño
  booleano+distancia+tiempo de reacción — 7.1.
- Definición del acorde ajeno: curriculum progresivo de 3 niveles (ajeno
  total → préstamo modal/cromático → diatónico en función incorrecta) — 7.2.
- Generación de contenido: banco fijo de progresiones pre-compuestas, no
  generación procedural — 7.2.
- Longitud de progresión: 4 compases — 7.2.
- Mecanismo de interacción: dos modos (escuchar-y-elegir / tap-en-vivo)
  conviven como escalón de andamiaje gobernado por el mismo DDA de las Ideas
  1/6, no como modos libres — 7.3.
- Manejo de falsos positivos/negativos en tap en tiempo real: sin detección
  cuenta como error total; primer tap es la respuesta, sin corrección — 7.3.
- Posición del acorde ajeno: aleatoria por ronda — 7.3.
- Alcance de tesis: trabajo futuro/experimento secundario, fuera del MVP
  (mismo criterio que la Idea 5) — 7.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el desbloqueo
de modo de interacción y de nivel de ajenidad; el banco concreto de
progresiones del nivel inicial.

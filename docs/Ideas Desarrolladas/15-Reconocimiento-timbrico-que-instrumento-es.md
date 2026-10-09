# 15. ¿Qué instrumento es?

> Reconocimiento tímbrico por selección múltiple, con distractores elegidos
> por cercanía taxonómica (viento vs. viento, no viento vs. cuerda) para subir
> la dificultad de forma progresiva — cierra un hueco pedagógico señalado
> explícitamente por la investigación, no una mecánica trillada del mercado.

**Estado:** DESARROLLADA

## Actualización solicitada — 2026-10-09

En el siguiente incremento autorizado por el usuario, v0.4 implementa el DDA intrapartida de escuchas con las propuestas numéricas de la especificación y un panel de inspección/simulación. Automático inicia en A; práctica manual A/B/C conserva tres escuchas, como elección provisional del agente para preservar el recorrido anterior. Escenarios, configuración y límites en [DDA-instrumentos-v0.4.md](../DDA-instrumentos-v0.4.md). Los desbloqueos todavía corresponden a #5 y no están implementados.

Tras probar v0.3 en el celular, el usuario pidió variar el Do4 aislado mediante una melodía breve generada aleatoriamente al seleccionar el instrumento. Esta solicitud prevalece sobre la frase fija descrita en §15.2 y §15.3. En v0.3.1 se genera una frase nueva por pregunta, independiente de la identidad del instrumento; todas sus reescuchas conservan exactamente la misma. Se usa el mismo conjunto de notas reales, gramática y duración para todos los instrumentos, evitando asignar melodías características. Las respuestas registran la frase y las versiones de generador/banco. El diseño concreto de seis notas pentatónicas, cierre en tónica y tiempos es una elección provisional del agente, no una decisión calibrada o del director. Detalles y procedencia: [Melodias-instrumentos-v0.3.1.md](../Melodias-instrumentos-v0.3.1.md). Se mantienen las reglas DDA confirmadas abajo, todavía pendientes de implementación.

## Actualización confirmada — 2026-10-02

El usuario revisó la política DDA en la entrevista `grill-with-docs`. Esta actualización prevalece sobre las descripciones de gobernanza por promedio móvil y de escuchas fijas que se conservan abajo como antecedente:

- Una partida tendrá inicialmente 10 preguntas; cada pregunta es una ronda. El tamaño es un valor de prueba, pendiente de calibración.
- Un motor basado en lógica difusa ajustará la cantidad de escuchas disponibles entre rondas. Se mantienen cuatro opciones y el nivel de instrumentos de la partida; no se modifica un estímulo ya presentado.
- Sus indicadores iniciales serán aciertos/errores recientes y uso de reescuchas. El tiempo de respuesta no será penalizado.
- El desbloqueo de niveles por desempeño se conserva y será permanente. La dificultad activa puede bajar para ayudar al jugador sin retirar niveles desbloqueados.
- Valores iniciales de prueba aprobados: comenzar con 3 escuchas totales, incluida la primera, y variar entre 1 y 3. Evaluar desde la tercera respuesta sobre las últimas 3 rondas; cambiar como máximo una escucha por ajuste y dejar dos rondas entre cambios. Nunca retirar escuchas de una pregunta iniciada.
- Se eligió inferencia Mamdani. Las partidas automáticas comienzan en el nivel más alto desbloqueado; dos partidas automáticas completas consecutivas calificadas como dominio alto en ese nivel habilitan el siguiente para la próxima partida. Las partidas manuales no desbloquean niveles.
- Un acierto con reescuchas sigue siendo correcto, pero aporta menor evidencia de autonomía que acertar con la primera escucha. Acertar pesa más que el uso de reescuchas. Esta interpretación es una hipótesis de diseño, no una medida validada.
- Un panel de inspección mostrará entradas, reglas activadas, salida difusa y cambio aplicado. Los casos simulados quedarán separados del desempeño real.
- Las funciones de pertenencia, la combinación numérica de indicadores y la frontera operativa de dominio alto serán valores iniciales explícitos de prueba, configurables y pendientes de calibración.

## 15.1 Fundamento

**El hueco que cubre, con cita directa de la investigación base.**
`InvestigacionSistematica.md` (§1.1, línea 407) señala que "la inmensa
mayoría de las soluciones tecnológicas se centran en evaluar habilidades
receptivas (reconocimiento auditivo de intervalos, dictado de notas)...
marginando casi por completo la evaluación de habilidades expresivas y
articulatorias, tales como el staccato, el legato, o **el control dinámico
del timbre**". A diferencia de los ítems 14-16 (básicos del mercado que
necesitaron una vuelta de rosca explícita para no ser una copia de lo ya
existente — ver Ideas 12-14), este ítem no parte de una mecánica trillada:
parte de un hueco que la propia revisión sistemática identifica como
infravalorado, lo que le da un respaldo directo desde el documento de
investigación de la tesis, no solo una justificación de producto.

**Por qué no se llevó al formato arcade "defensa" de las Ideas 12-14 — decisión
explícita.** Se evaluó adaptar el mismo motor arcade (spawn, vidas, DDA de
dos capas) reemplazando el estímulo visual por uno auditivo, pero se
descartó: el mecanismo pedagógico central de esta idea es la **discriminación
fina entre timbres taxonómicamente próximos**, no la velocidad de reacción
bajo presión — forzarla al mismo molde arcade habría subordinado la
discriminación auditiva a la mecánica de supervivencia, diluyendo el eje que
la hace valiosa. Queda en cambio con un formato deliberativo, sin presión de
tiempo, con selección múltiple de 4 opciones (1 correcta + 3 distractores).

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP**, a pesar de no tocar
el pipeline de captura de voz ni el multiplexor DSP↔ML (que sigue siendo el
aporte arquitectónico central). La decisión de incluirlo en el MVP, a
diferencia de las Ideas 7, 11 y la familia "defensa" (12-14), se apoya en que
esta idea **responde directamente a un hueco documentado en la investigación
de la tesis** (línea 407 citada arriba), no es solo una extensión de
catálogo de producto — deja evidencia de que el sistema de tesis no se limita
a entrenar percepción de tono (el eje ya cubierto en profundidad por las
Ideas 1-11), sino que también cubre un eje perceptivo distinto y
explícitamente señalado como descuidado por el estado del arte. Su costo de
implementación es además bajo: reutiliza el motor DDA genérico ya validado y
un banco fijo de samples, sin requerir un componente de arquitectura nuevo.

## 15.2 Base técnica

**Sin pipeline de audio de entrada.** Es puramente receptivo — el usuario
escucha y selecciona, no hay captura de voz ni instrumento. El único
componente compartido con las ideas anteriores es el **motor DDA genérico**
(promedio móvil de éxito sobre ventana de N rondas, con override manual y
etiquetado `origen: manual|dda`).

```
Selección de instrumento objetivo + 3 distractores
   (curriculum de nivel A/B/C, ver abajo)
   → Reproducción del sample de audio del instrumento objetivo
        (banco fijo, misma nota/frase de control para todos los candidatos)
   → Usuario escucha (límite de repeticiones vigente) y selecciona entre
        las 4 opciones (instrumento objetivo + 3 distractores, en orden
        aleatorio)
   → Evaluación: selección correcta/incorrecta
   → Motor DDA genérico (mismo componente de Ideas 1/5/6/7/11)
```

**Control experimental del audio — RESUELTO: misma nota/frase de control para
todos los candidatos, banco fijo de samples reales, sin síntesis.** Si el
instrumento correcto sonara una frase distinta (otra altura, otro ritmo) que
los distractores, el usuario podría discriminar por pistas ajenas al timbre
(contorno melódico, registro, duración), contaminando la medición de si
realmente reconoce el timbre. Se usa una nota o frase corta fija, tocada por
cada instrumento candidato, con **samples pregrabados reales** (no síntesis —
el timbre sintético de baja fidelidad introduciría un sesgo de reconocimiento
distinto al de un instrumento real, especialmente relevante en el Nivel C,
donde la diferencia entre candidatos ya es sutil de por sí). Mismo criterio
de banco fijo ya usado en la Idea 7.

**Curriculum de distractores — RESUELTO: 3 niveles, orden fijo, por cercanía
taxonómica creciente:**

1. **Nivel A — familias completamente distintas:** ej. piano, violín, flauta,
   batería (máxima distancia tímbrica entre las 4 opciones, calibra la
   mecánica de selección antes de exigir discriminación fina).
2. **Nivel B — misma familia amplia, subfamilia distinta:** ej. todas de
   viento, pero mezclando madera y metal (flauta vs. trompeta vs. clarinete
   vs. trombón).
3. **Nivel C — misma subfamilia:** ej. todas viento-madera (flauta vs.
   clarinete vs. oboe vs. fagot), o todas cuerda-frotada (violín vs. viola
   vs. cello vs. contrabajo) — la más difícil, el "error típico" real de
   discriminación tímbrica fina que motiva la idea original.

**Taxonomía de familias — RESUELTO: banco fijo de organología estándar, sin
generación procedural.** Viento-madera, viento-metal, cuerda-frotada,
cuerda-pulsada, percusión, teclado — mismo criterio de banco fijo antes que
generación ya usado en la Idea 7 (no hay necesidad de generar taxonomías
dinámicamente, es un conjunto cerrado y estable).

**Límite de repeticiones — RESUELTO: 2-3 escuchas permitidas por ronda antes
de responder.** Mismo patrón de "límite de repeticiones" ya usado como
parámetro de andamiaje en la Idea 1 — reduce fricción sin eliminar el
desafío de discriminación (no es una prueba de memoria auditiva de una sola
pasada, es una prueba de discriminación tímbrica).

## 15.3 Diseño

**Gobernanza — RESUELTO: ventana móvil de éxito, mismo mecanismo genérico de
toda la tesis.** Éxito = selección correcta. Promedio móvil sobre ventana de
N rondas; sube de Nivel A→B→C cuando el promedio supera un umbral. Override
manual disponible, con etiquetado `origen: manual|dda` (rondas manuales no
cuentan para el desbloqueo).

**Sin escalera de andamiaje de niveles de interacción** (a diferencia de las
Ideas 1/6/7/11-14): acá no hay modos con-ancla/libre ni escuchar-y-elegir/tap
en vivo — la única progresión es el curriculum de distractores (A→B→C). El
formato de interacción (selección múltiple de 4 opciones, límite de
repeticiones) es fijo a lo largo de todo el curriculum.

**Loop de una ronda:**

1. Se determina el nivel de curriculum vigente (A, B o C) según el DDA o el
   override manual.
2. Se sortea el instrumento objetivo y 3 distractores dentro de las
   restricciones taxonómicas de ese nivel (misma familia/subfamilia según
   corresponda).
3. Se reproduce el sample del instrumento objetivo (nota/frase de control
   fija), con el límite de repeticiones vigente disponible para reescuchar.
4. El usuario selecciona una de las 4 opciones (instrumento objetivo +
   distractores, en orden aleatorio).
5. Se evalúa la selección (correcta/incorrecta) y se envía el resultado al
   motor DDA (salvo ronda `origen: manual`).
6. El DDA ajusta el nivel de curriculum si corresponde.

**Datos a registrar por ronda:**
`{timestamp, nivel_curriculum (A|B|C), instrumento_objetivo, distractores
(lista de 3), origen (manual|dda), repeticiones_permitidas,
repeticiones_usadas, seleccion_usuario, correcto (bool),
nivel_dificultad_antes, nivel_dificultad_despues}`

## 15.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
15.1–15.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Formato de interacción: sin presión de tiempo, sin el molde arcade de las
  Ideas 12-14 — evaluado y descartado explícitamente para no subordinar la
  discriminación tímbrica fina a la velocidad de reacción — 15.1.
- Respuesta: selección múltiple de 4 opciones (1 correcta + 3 distractores),
  no recall libre — la dificultad real vive en la cercanía taxonómica de los
  distractores, no en la ausencia de opciones — 15.2.
- Curriculum de distractores: 3 niveles por cercanía taxonómica creciente
  (familias distintas → subfamilia distinta dentro de familia amplia →
  misma subfamilia) — 15.2.
- Taxonomía de familias: banco fijo de organología estándar (viento-madera,
  viento-metal, cuerda-frotada, cuerda-pulsada, percusión, teclado), sin
  generación procedural — 15.2.
- Control experimental del audio: misma nota/frase de control para todos los
  candidatos de una ronda, banco fijo de samples reales, sin síntesis — 15.2.
- Límite de repeticiones de escucha: 2-3 por ronda — 15.2.
- Gobernanza: ventana móvil de éxito (selección correcta), mismo mecanismo
  genérico de toda la tesis, sin escalera de andamiaje de modos adicional —
  15.3.
- Alcance de tesis: **dentro del MVP**, a pesar de no tocar el pipeline de
  captura de voz — decisión explícita del usuario, respaldada por responder
  directamente al hueco de timbre documentado en la investigación (línea 407
  de `InvestigacionSistematica.md`), a diferencia de las Ideas 7, 11 y 12-14
  (clasificadas fuera del MVP por ser extensiones de catálogo sin respaldo
  directo en un hueco de investigación) — 15.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el
desbloqueo de nivel; cantidad inicial de repeticiones permitidas; el banco
concreto de samples e instrumentos por familia/subfamilia.

# 17. Simón musical armónico

> Variante del clásico "Simon says" con notas de una escala: el sistema
> reproduce una secuencia creciente de notas (botones en pantalla), el
> usuario debe repetirla en el mismo orden tocando los botones. La secuencia
> crece de a una nota por ronda superada; al primer error, termina la
> partida. Memoria auditiva pura, sin captura de voz.

**Estado:** DESARROLLADA

## Actualización confirmada — 2026-10-02

Después de probar el primer APK, el usuario pidió un menú principal, una preparación separada y un piano táctil en lugar de la grilla de botones, siguiendo aproximadamente `docs/ui simon.png`. También eligió muestras de piano `mf` de University of Iowa convertidas a OGG (`docs/Audio-piano-Simon.md`). Las menciones posteriores a botones quedan como antecedente: la entrada sigue siendo táctil, ahora mediante teclas de piano. No se añadieron escalas, vidas ni otros juegos a partir del mockup.

En la entrevista `grill-with-docs`, el usuario confirmó mantener escala y tónica fijas durante la partida, crecimiento de una nota por ronda superada y fin al primer error. El motor DDA compartido usará lógica difusa para el desbloqueo de escalas entre partidas; no habrá un ajuste intrapartida adicional en esta primera versión. Los desbloqueos serán permanentes.

Esta decisión sustituye la política anterior de desbloqueo por promedio móvil y umbral que se conserva abajo como antecedente. El puntaje es la longitud máxima completada correctamente: completar cuatro notas y fallar en la secuencia de cinco da un puntaje de cuatro; se conserva además la secuencia intentada y la posición del error.

Las partidas automáticas comienzan en la escala más alta desbloqueada. Dos partidas automáticas completas consecutivas calificadas como dominio alto en la escala actual habilitan la siguiente para la próxima partida. Las partidas manuales se registran sin contribuir a desbloqueos. La traducción del puntaje a dominio alto y las funciones de pertenencia serán valores de prueba configurables y pendientes de calibración, específicos de Simón.

Ajuste confirmado por el usuario al probar v0.2 (2026-10-02): Simón se abre en horizontal, con preparación y partida sin scroll y teclado ancho. Las pulsaciones del turno del jugador se procesan en orden sin esperar a que termine el sonido anterior; girar el dispositivo no interrumpe la sesión. El menú muestra la versión instalada y elimina las tres frases indicadas por el usuario. Las reglas de escala, puntuación y fin al primer error se conservan.

## 17.1 Fundamento

**Por qué botones en pantalla, no captura de voz — decisión explícita para
evitar redundancia con la Idea 10.** La Idea 10 (Dictado melódico por canto)
ya cubre "escuchar una melodía y reproducirla cantando", incluyendo memoria
de secuencias largas (10-20 notas) con el pipeline de voz completo. Si el
Simón armónico también usara voz, sería esencialmente la Idea 10 con otro
envoltorio, sin aportar una habilidad ni una arquitectura distinta. Con
botones en pantalla, en cambio, la idea mide **memoria de secuencia pura**,
sin la interferencia de precisión de ejecución vocal — un error de afinación
en la Idea 10 puede confundir memoria con producción; acá el acierto/error es
binario y limpio, exactamente la variable que se quiere medir. El atractivo
del ítem original, además, es la mecánica reconocible de "Simon", no el
canto — diluirla con captura de voz la habría alejado de esa fórmula.

**Qué habilidad entrena, distinta de las demás ideas del catálogo.** Ninguna
otra idea desarrollada hasta ahora aísla la memoria auditiva de secuencia
como variable única, sin mezclarla con producción vocal (Idea 10), lectura de
partitura (Ideas 12-14) o discriminación tímbrica (Ideas 15/16). Es una
medición de "near-transfer" pura (memoria auditiva secuencial), coherente con
la recomendación de `InvestigacionSistematica.md` (§1.1, línea 406) de medir
"el incremento comprobable en habilidades musicales receptivas y
productivas... y memoria rítmica" — acá aplicada a memoria melódica/armónica
en vez de rítmica.

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP** (decisión explícita
del usuario). No toca el pipeline de captura de voz ni el multiplexor
DSP↔ML, y no tiene el mismo respaldo directo en el PICOC que las Ideas 15/16,
pero se incluye en el MVP porque: (a) es la única medición limpia de memoria
auditiva de secuencia sin ruido de ejecución, un constructo distinto y útil
como línea de base para la validación empírica del sistema; (b) su costo de
implementación es mínimo, reutiliza el motor DDA genérico sin ningún
componente de arquitectura nuevo; (c) amplía el portafolio de habilidades
"near-transfer" que la tesis puede reportar como validadas, más allá de
percepción de tono y producción vocal.

## 17.2 Base técnica

**Sin pipeline de audio de entrada.** No hay micrófono, ring buffer, PDA ni
multiplexor DSP↔ML — la entrada es táctil (botones), la salida es audio
(reproducción de la secuencia). Único componente compartido con las ideas
anteriores: el **motor DDA genérico**.

```
Selección de escala vigente (curriculum persistente, ventana móvil)
   → Botones fijos en pantalla, uno por nota de la escala, cada uno con
        color + sonido propio
   → El sistema reproduce la secuencia actual (arranca en 1 nota, crece de a
        una por ronda superada)
   → El usuario repite la secuencia tocando los botones en el mismo orden
   → Si coincide: secuencia crece en 1, se reproduce la nueva secuencia
     completa (incluyendo la nota agregada)
   → Si no coincide: partida termina, se registra la longitud máxima
        alcanzada
   → Motor DDA genérico (gobierna el curriculum de escala entre partidas)
```

**Fuente de las notas — RESUELTO: curriculum de 3 niveles por escala
creciente en cantidad de notas/botones, orden fijo:**
1. **Nivel A — Pentatónica (5 notas/botones):** mínima disonancia entre notas
   adyacentes, calibra la mecánica de memoria de secuencia sin exigir además
   discriminación de relaciones tonales tensas.
2. **Nivel B — Diatónica (7 notas/botones):** escala mayor/menor estándar,
   más notas para retener y diferenciar.
3. **Nivel C — Cromática (12 notas/botones):** máxima dificultad de
   discriminación entre botones adyacentes (semitono a semitono), techo de
   complejidad del curriculum.

La escala y la tónica de cada partida quedan fijas durante toda la partida
(no cambian a mitad de secuencia). Cada botón se identifica visualmente
(color + posición fija) y suena al tocarlo, reforzando la asociación
visual-auditiva, mismo criterio que el Simon clásico.

**Manejo de errores — RESUELTO: se corta al primer error, sin tolerancia,
fiel al Simon clásico.** Sin vidas ni reintentos. La partida termina en el
primer error y se registra la **longitud máxima de secuencia alcanzada**
como puntaje de esa partida — métrica de un solo número, limpia y directa
para alimentar al DDA, sin necesitar ventanas de tolerancia adicionales
dentro de la partida.

## 17.3 Diseño

**Sin escalera de andamiaje adicional.** La propia progresión de longitud
creciente dentro de cada partida ya es el mecanismo de dificultad ascendente
— no hace falta un nivel de andamiaje separado (a diferencia de las Ideas
1/6/7/11), el "Simon" ya es intrínsecamente progresivo por diseño.

**Gobernanza del curriculum de escala — RESUELTO: ventana móvil sobre
longitud máxima promedio, mismo mecanismo genérico de toda la tesis.** Se
calcula el promedio móvil de `longitud_maxima_alcanzada` sobre las últimas N
partidas; cuando supera un umbral, se desbloquea la siguiente escala
(pentatónica → diatónica → cromática). Persistente entre partidas, no se
resetea al perder. Override manual disponible, con etiquetado
`origen: manual|dda` (partidas manuales no cuentan para el desbloqueo).

**Loop de partida:**

1. Se determina la escala vigente (pentatónica/diatónica/cromática) según el
   DDA o el override manual, y se sortea la tónica de la partida.
2. Se muestran los botones correspondientes a esa escala (5, 7 o 12), y
   arranca la secuencia en 1 nota (sorteada al azar entre los botones).
3. El sistema reproduce la secuencia completa (sonido + resalte visual de
   cada botón en orden).
4. El usuario repite la secuencia tocando los botones:
   - Coincide en su totalidad → la secuencia crece en 1 nota (sorteada al
     azar), se reproduce la secuencia completa actualizada, vuelve al
     paso 3.
   - No coincide (error en cualquier posición) → termina la partida.
5. Al terminar la partida, se registra `longitud_maxima_alcanzada` y se
   envía al motor DDA (salvo partida `origen: manual`).
6. El DDA ajusta la escala vigente para la siguiente partida si corresponde.

**Datos a registrar por partida:**
`{timestamp, partida_id, escala (pentatonica|diatonica|cromatica),
tonica, origen (manual|dda), longitud_maxima_alcanzada,
secuencia_completa_intentada, posicion_del_error (null si no hubo),
nivel_dificultad_antes, nivel_dificultad_despues}`

## 17.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
17.1–17.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Modalidad de entrada: botones/teclas en pantalla, no voz — evita
  redundancia directa con la Idea 10 y aísla memoria de secuencia sin ruido
  de ejecución vocal — 17.1.
- Fuente de notas: curriculum de 3 niveles por escala (pentatónica → 5
  botones → diatónica → 7 botones → cromática → 12 botones), orden fijo,
  escala y tónica fijas por partida — 17.2.
- Manejo de errores: se corta al primer error, sin tolerancia, fiel al
  Simon clásico; se registra la longitud máxima alcanzada — 17.2.
- Sin escalera de andamiaje adicional: la longitud creciente dentro de la
  partida ya es el mecanismo de dificultad — 17.3.
- Gobernanza: ventana móvil de longitud máxima promedio entre partidas,
  mismo mecanismo genérico de toda la tesis — 17.3.
- Alcance de tesis: **dentro del MVP** (decisión explícita del usuario) —
  única medición limpia de memoria auditiva de secuencia sin ruido de
  ejecución, costo de implementación mínimo, amplía el portafolio de
  habilidades near-transfer validadas por la tesis — 17.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): tamaño de ventana N y umbral de éxito para el
desbloqueo de escala; longitud inicial de secuencia (si distinta de 1);
velocidad de reproducción de la secuencia.

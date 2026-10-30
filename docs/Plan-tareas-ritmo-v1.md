# Plan de incrementos de Rhythm Tap v1

Estado: puntos de prueba, división y dependencias aprobados por el usuario mediante «perfecto» el 2026-10-22. Especificación y seis tickets publicados. **R1–R5 terminados en software hasta v0.12.10**: 163 JVM, 57/57 Android completas en API 34, revisión de R5 a 411/360/320 dp y texto 200 %, lint 0 errores/14 advertencias. **R6 pendiente**; prueba física y cierre de issues pendientes. Verificación actual [R5](Rhythm-Tap-R5.md) y antecedentes [R4](Rhythm-Tap-R4.md), [R3](Rhythm-Tap-R3.md), [R2](Rhythm-Tap-R2.md) y [v0.12](Verificacion-prototipo-v0.12.md).

Fuente: [especificación](Especificacion-ritmo-v1.md). Cada ticket entrega un recorrido ejecutable a través de interfaz, reglas, audio/datos necesarios y verificación. No separar generador, renderizador y evaluador en tickets horizontales que no permitan practicar nada al terminar.

Especificación publicada: [#15](https://github.com/SaladinoJC/Tesis/issues/15). Todas las issues llevan `ready-for-agent` y sus bloqueos nativos fueron verificados. La especificación es referencia, no un bloqueo.

## División aprobada y publicada

| Incremento | Issue | Entrega | Bloqueado por |
| --- | --- | --- | --- |
| R1 | [#16](https://github.com/SaladinoJC/Tesis/issues/16) | Generar y ejecutar cuatro compases básicos en 4/4 | Ninguno |
| R2 | [#17](https://github.com/SaladinoJC/Tesis/issues/17) | Practicar figuras largas y silencios en compases simples | R1 (#16) |
| R3 | [#18](https://github.com/SaladinoJC/Tesis/issues/18) | Practicar semicorcheas y puntillos con sus combinaciones | R2 (#17) |
| R4 | [#19](https://github.com/SaladinoJC/Tesis/issues/19) | Practicar tresillos, síncopas y ligaduras | R2 (#17) |
| R5 | [#20](https://github.com/SaladinoJC/Tesis/issues/20) | Consultar, repetir y exportar ejercicios e historial | R1 (#16) |
| R6 | [#21](https://github.com/SaladinoJC/Tesis/issues/21) | Desbloquear categorías y adaptar tempo con DDA inspeccionable | R3, R4 y R5 (#18, #19 y #20) |

R3 y R4 pueden desarrollarse en paralelo; R5 usa el modelo de partitura/resultados de R1 y no necesita esperar las categorías nuevas. R6 necesita el catálogo completo y el guardado idempotente. No depende de las issues del afinador: aprovecha la base local v0.10 y debe conservar cualquier ampliación integrada antes. La especificación es referencia, no bloqueo que deba cerrarse.

R1–R5 se entregan como práctica sin desbloqueos ni progreso automático fingidos. R5 añade persistencia de los resultados; antes se presentan como resumen en memoria. Cada categoría disponible puede ejecutarse y repetirse al terminar su incremento; no esperar a R6 para demostrar el juego.

## R1 — Generar y ejecutar cuatro compases básicos en 4/4

**Qué construir:** seleccionar negras/dos corcheas, generar en el momento, revisar, pulsar Play, escuchar conteo, tapear y revisar resultados. Es el primer recorrido musical completo.

**Bloqueado por:** ninguno.

- [ ] Entrada Rhythm Tap desde la app; cuatro compases 4/4 con R20/R21, tempo inicial de prueba 60 BPM y selección explícita.
- [ ] Modelo de duraciones racionales, compás, procedencia de células y resultado serializable previsto para silencios, tresillos y ligaduras; no implementar su renderizado futuro antes de necesitarlo.
- [ ] Generar localmente con semilla/versión; duración exacta y cuota objetivo/conexión identificables. Para estas categorías coincidentes visualmente, no duplicar conteo de duración.
- [ ] Vista previa sin audio objetivo; Play inicia un compás de conteo audible antes de admitir respuestas. Generar/revisar no inicia ejecución.
- [ ] Partitura legible y seguimiento temporal sin depender del refresco visual para registrar taps. La presentación por renglones/seguimiento vertical original queda sustituida por lectura lateral autorizada el 2026-10-29: [guía y verificación](Rhythm-Tap-lectura-lateral.md).
- [ ] Metrónomo de ejecución y sonido de taps reales opcionales/independientes; ningún patrón ideal suena antes ni durante.
- [ ] Reloj común, asociación uno a uno y ventanas experimentales de la especificación; feedback, omisiones y extras sin detener ejercicio. Resumen en memoria y repetir mismo patrón como práctica.
- [ ] Salir/segundo plano interrumpe y libera audio sin completar; sin micrófono ni permisos de voz innecesarios.
- [ ] Pruebas de sesión con tiempo/azar/audio controlados y recorrido Android con audio real, taps y scroll; separar sincronía interna de acústica.

## R2 — Figuras largas, silencios y compases simples

**Qué construir:** elegir 2/4, 3/4 o 4/4 y practicar blancas, redondas y categorías básicas de silencios con conteo y evaluación coherentes.

**Bloqueado por:** R1.

- [ ] Añadir R01/R02/R09/R10/R11/R12/R18/R19 y sus duraciones/contextos según especificación; cada categoría seleccionable y jugable.
- [ ] Redonda solo donde cabe sin cruzar barra en v1; silencio de compás representa la duración métrica correcta, no siempre cuatro tiempos.
- [ ] Cuota conjunta próxima a60 %, rango experimental50–75 %, mínimo dos apariciones por seleccionada, negras/corcheas de conexión y sin doble conteo.
- [ ] Factibilidad antes de búsqueda; combinaciones imposibles explicadas, sin quitar categorías. Rechazo aleatorio cancelable y acotado, distinguiendo agotamiento de búsqueda e imposibilidad demostrada.
- [ ] Dibujar silencios, puntillo de R09 y figuras largas legiblemente; no exigir sostener el dedo para duración ni tapear silencios.
- [ ] Evaluar ausencia de extras en silencios, sin penalizar dos veces un ataque vecino válido. Resumen por categoría y práctica repetible.
- [ ] Corpus reproducible con casos válidos/negativos, métrica/duración/presencia, silencios con y sin taps, y recorrido Android en las tres métricas.

## R3 — Semicorcheas y puntillos combinados

**Qué construir:** generar y ejecutar ejercicios de las células binarias cortas aceptadas, mezcladas con categorías anteriores cuando sean compatibles.

**Bloqueado por:** R2.

- [x] Añadir R03/R04/R05/R06/R07/R08/R13/R14 según catálogo; conservar orden de figuras/silencios y posiciones admitidas.
- [x] Cada seleccionada aparece repetidamente en la cuota, sin introducir recursos no elegidos al concatenar células.
- [x] Renderizar barras, puntillos y silencios de semicorchea con agrupación por pulso, sin comprimir ilegiblemente un renglón.
- [x] Recalcular ventanas por intervalo local y tempo: un tap no satisface varios ataques aunque aumente densidad.
- [x] Mantener ataque real para sonido/feedback; no programar el tap sobre la grilla ideal.
- [x] Resultados y revisión señalan anticipos, retrasos, extras y omisiones en estas células. Metrónomo apagado conserva la grilla y permite observar deriva.
- [x] Pruebas de catálogo y de fronteras temporales, ejemplos visuales Android canónicos y ejercicio completo combinado.

## R4 — Tresillos, síncopas y ligaduras

**Qué construir:** ejecutar R15/R16/R17 con notación y evaluación de sus ataques reales, completando el catálogo inicial junto a R3.

**Bloqueado por:** R2. No necesita semicorcheas de R3 para estas tres plantillas.

- [x] Tresillo de corcheas suma exactamente un tiempo en compases simples; notación ternaria reconocible y tres ataques, sin redondeos acumulativos.
- [x] R16 corchea+negra+corchea ocupa dos tiempos y se coloca de modo que exista el cruce sincopado especificado.
- [x] R17 inicial: dos negras ligadas entre tiempos, con un ataque; no sustituirlas visualmente por blanca ni esperar otro tap en la continuación.
- [x] Generación seleccionable con cuota, compatibilidad y contexto; exclusión de versiones de tresillo/silencios/ligaduras aún no incluidas.
- [x] Partitura, timeline, feedback y resumen derivados del mismo contenido racional, con ejemplos negativos de suma incorrecta y reataque extra.
- [x] Corpus de estas categorías, revisión visual Android y ejecución/repetición completas con audio de taps opcional.

## R5 — Historial, repetición y exportación de ejercicios

**Qué construir:** guardar sesiones de práctica completas/interrumpidas, abrir la partitura y resultados exactos después de reabrir la app, repetir y exportar.

**Bloqueado por:** R1. El contenido serializable de R1 admite las extensiones de R2–R4; ellas aportan sus pruebas de representación específicas.

- [x] Guardar partitura exacta, semilla/huella/versiones, selección/presencia, tempo/compás, ayudas, datos de taps, evaluación y origen; no guardar solo semilla ni recalcular resultados con otra configuración silenciosamente.
- [x] Historial y filtros por categoría, compás, tempo, práctica/progresión y metrónomo; mostrar solo opciones de progresión implementadas cuando llegue R6.
- [x] Abrir detalle con partitura, ataques/silencios, desviaciones, extras/omisiones y categoría propietaria, conservando datos ausentes como ausentes.
- [x] Repetir exactamente el patrón desde resultados/historial como práctica; no cambiarlo por nueva semilla ni declararlo evidencia nueva por cambiar UUID.
- [x] Error de lectura/guardado visible, recuperación del original y reintento idempotente; interrupciones no se presentan como completas.
- [x] Exportación/reinicio/recuperación integran almacenamiento nuevo preservando datos de juegos/voz/laboratorio y afinador si ya existe.
- [x] Pruebas públicas de persistencia, filtros, huella, repetición, fallos y compatibilidad; recorrido Android cerrar/reabrir→detalle→repetir→exportar.

## R6 — Progresión por categoría y tempo con Mamdani

**Qué construir:** elegir entre progresión y práctica, resolver ejercicios nuevos con categorías desbloqueadas, conservar avance y explicar decisiones reales/simuladas del motor.

**Bloqueado por:** R3, R4 y R5; incluye transitivamente R1/R2.

- [ ] Progresión selecciona desbloqueadas; práctica permite bloqueadas/tempo manual y repeticiones, con resultados separados y sin modificar rachas/desbloqueos.
- [ ] Inicialmente negras/corcheas a60 BPM; aplicar grafo/versionado de la especificación sin ciclos ni categorías inalcanzables. No exigir llegar al techo para desbloquear.
- [ ] Medir precisión/control por categoría con al menos dos apariciones y dos unidades; silencios requieren respeto real de sus intervalos y conexión no regala evidencia.
- [ ] Funciones, reglas y centroide Mamdani reproducen configuración experimental; dominio alto≥0,75, dos resultados elegibles consecutivos y aumentos máximos5 BPM hasta120, efectivos en la siguiente partida.
- [ ] Tempo fijo en ejecución; mezclas usan el mínimo recomendado; éxito a tempo inferior no eleva una categoría más rápida. Información insuficiente conserva estado y expone motivo.
- [ ] Metrónomo apagado también permite progresar; identificar ayudas para filtrar resultados, sin exigir dominar ambas. Ejercicios repetidos o simulados no generan progreso real.
- [ ] Desbloqueos permanentes, evidencia registrada antes de reiniciar rachas al aumentar tempo, y resultado/progreso aplicados una vez en guardado coordinado. Fallo/reintento no duplica avances.
- [ ] Panel con entradas, pertenencias, reglas, salida, propuesta/acción y explicación; banco simulado aislado para éxito, error, mezcla, insuficiencia, techo, repetición y fallo.
- [ ] Pruebas de sesión/persistencia/Android, compilación y lint pertinentes; guía de demostración y condiciones de prueba física distinguiendo sintéticos, emulador y teléfono pendiente.

## Puntos de prueba aprobados

1. **Sesión pública completa:** selección/Play/taps y reloj controlado → partitura/estado/resultados/historial/progreso; audio, azar y almacenamiento controlados solo en sus límites.
2. **Partitura y Android:** corpus verificable de generación/ataques/notación, revisión visual y recorrido Android real de audio/taps/scroll. Prueba física de sincronía documentada aparte.

## Registro de publicación

Publicadas #15–#21 con `ready-for-agent`, criterios y cuerpos UTF-8 con saltos de línea reales mediante `--body-file`. Bloqueos nativos verificados: #17←#16, #18←#17, #19←#17, #20←#16, #21←#18/#19/#20; #16 sin bloqueos. Identificadores y verificación en `.agents/ritmo-issues.json` para no duplicar. No se modificaron ni cerraron issues anteriores. No se inició implementación en esta fase documental.

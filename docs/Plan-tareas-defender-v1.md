# Plan de incrementos de Defender v1

Estado: puntos de prueba, siete incrementos y dependencias aprobados por el usuario mediante «perfecto» el 2026-10-22. Especificación y siete tickets publicados; implementación pendiente.

Fuente: [especificación](Especificacion-defender-v1.md), [entrevista](Definicion-defender.md) y referencia visual aportada por el usuario. Cada incremento entrega un recorrido jugable, con interfaz, reglas, datos necesarios y verificación. Conservar decisiones experimentales identificadas y compatibilidad con las ampliaciones ya integradas.

Especificación publicada: [#22](https://github.com/SaladinoJC/Tesis/issues/22). Todas las issues llevan `ready-for-agent` y sus bloqueos nativos fueron verificados. La especificación es referencia, no un bloqueo que deba cerrarse.

## División aprobada y publicada

| Incremento | Issue | Entrega | Bloqueado por |
| --- | --- | --- | --- |
| D1 | [#23](https://github.com/SaladinoJC/Tesis/issues/23) | Defender básico de notas en sol, vertical y radial | Ninguno |
| D2 | [#24](https://github.com/SaladinoJC/Tesis/issues/24) | Notas ampliadas, claves, alteraciones y hasta cuatro enemigos | D1 (#23) |
| D3 | [#25](https://github.com/SaladinoJC/Tesis/issues/25) | Notas con DDA, desbloqueos, historial e inspección | D2 (#24) |
| D4 | [#26](https://github.com/SaladinoJC/Tesis/issues/26) | Defender de tríadas por nombre | D3 (#25) |
| D5 | [#27](https://github.com/SaladinoJC/Tesis/issues/27) | Acordes con séptimas, inversiones, fa y mezcla | D4 (#26) |
| D6 | [#28](https://github.com/SaladinoJC/Tesis/issues/28) | Defender con piano virtual jugable en vertical | D5 (#27) |
| D7 | [#29](https://github.com/SaladinoJC/Tesis/issues/29) | Piano con catálogo completo y progresión independiente | D6 (#28) |

La secuencia respeta el orden confirmado notas→acordes por nombre→piano. D3 entrega motor/persistencia/inspección reutilizables; D5 completa generación y notación de acordes que usa el piano. La dependencia no es completar afinador ni ritmo. La especificación será referencia, no un bloqueo que deba cerrarse.

D1 limita inicialmente a un enemigo para comprobar el recorrido completo; D2 alcanza el máximo de cuatro y el contenido de notas. D1/D2 ofrecen resumen en memoria/práctica, sin fingir persistencia ni desbloqueos todavía. No hacer primero tickets aislados de «motor», «renderer» o «assets» sin ninguna partida demostrable. Integrar una extracción local junto al comportamiento que la necesita.

## D1 — Defender básico de notas en sol

**Qué construir:** abrir Defender de notas en vertical, reconocer una nota natural dentro del pentagrama, disparar y jugar hasta perder tres vidas. Primer recorrido arcade completo.

**Bloqueado por:** ninguno; parte de la base local del prototipo y conserva funciones existentes.

- [ ] Preparación y entrada al juego, clave de sol visible, notas Mi4–Fa5, nombres Do–Si/C–B y Disparar sin octava.
- [ ] Escena espacial vertical basada en la referencia, nave/base inferior, HUD y controles abajo. Un enemigo a la vez inicialmente, variando su entrada sobre arco superior/laterales con viaje radial, no solo descenso recto.
- [ ] Tiempo de viaje10 s y curva de aparición configurada; si el cupo está lleno no acumular ráfagas. Reloj monótono activo, sin decisiones por framerate.
- [ ] Tres vidas, solo llegada quita una; al tercer impacto termina. Disparo correcto anterior al deadline elimina lógicamente, empate/llegada previa no. Animación decorativa no cambia resultado.
- [ ] Fallo visual inferior sin pausar ni borrar selección; pérdida muestra solución en posición y revisión al terminar. Acierto limpia selección; 100 puntos por eliminado.
- [ ] Nota al acertar y efectos independientes; pausa oculta objetivos/congela audio/tiempo, segundo plano pausa y salida interrumpe. No micrófono.
- [ ] Resumen en memoria con eventos/resultados, origen práctica y configuración reproducible. Sin presentar DDA/desbloqueo no implementados.
- [ ] Pruebas públicas de sesión sobre tiempos/fronteras, vidas, errores, pausa y cancelación; recorrido/capturas Android vertical de una partida completa y audio real.

## D2 — Ampliar notas y presión visual

**Qué construir:** jugar notas con líneas adicionales, fa y mezcla, alteraciones opcionales y hasta cuatro naves radiales simultáneas, manteniendo controles y notación legibles.

**Bloqueado por:** D1.

- [ ] Ofrecer N1 sol dentro, N2 sol ampliada, N3 fa ampliada y N4 mezcla en práctica; máximo dos líneas adicionales según rangos de la especificación y clave visible siempre.
- [ ] Alteraciones simples opcionales sin etapa propia, natural por defecto al responder; Do–Si/C–B como preferencia de nombres. Registrar grafía/octava aunque matching no exija octava.
- [ ] Exigir nombre+alteración escritos, sin aceptar enarmónico distinto; no inferir ni arrastrar alteraciones entre enemigos independientes.
- [ ] Hasta cuatro vivos con reserva de espacio para pentagramas; trayectorias radiales y espera si no hay lugar legible, sin solapamientos ni daño adicional por efectos.
- [ ] Responder sin seleccionar enemigo; duplicados permitidos y resolver al más cercano geométricamente a frontera de base, con desempate estable. Un acierto no elimina varios.
- [ ] Resumen separa clave/rango/alteración y pérdidas; no presentar estas prácticas como desbloqueos.
- [ ] Audio cubre los extremos realmente ofrecidos, con procedencia/permiso y transformaciones documentados, sin reproducir otra octava para suplir muestras ausentes.
- [ ] Corpus de extremos, grafías y claves; sesión con duplicados/velocidades distintas y prueba Android de cuatro enemigos, referencia visual y controles.

## D3 — Notas con DDA, progreso e historial

**Qué construir:** elegir práctica/progresión, desbloquear N1→N2→N3→N4 y ver adaptaciones reales de velocidad, resultados persistidos, exportación e inspección calculada.

**Bloqueado por:** D2.

- [ ] Motor Mamdani configurable según especificación: ventana cinco resueltos, al menos tres nuevas resoluciones entre ajustes, precisión/supervivencia/rapidez incluyendo pérdidas. Exponer datos insuficientes y ausencia de salida válida.
- [ ] Viaje inicial10 s, límites6–14 y paso máximo1 s solo para futuros enemigos. Los vivos conservan velocidad/deadline aunque se evalúe DDA; aparición gradual/cupo independientes de inferencia.
- [ ] Progresión elige desbloqueadas; práctica permite cualquier etapa y viaje manual sin aplicar desbloqueos. Dos completas elegibles altas por etapa, mínimo10 resueltos y exposición suficiente, desbloqueo permanente efectivo próxima partida.
- [ ] Mezcla exige evidencia de ambas claves; opciones de alteraciones no crean niveles ni permiten acreditar una etapa distinta. Puntaje bruto no sustituye evidencia.
- [ ] Persistir resultados/eventos/configuración y avance coordinados e idempotentes; lectura/guardado fallidos visibles y reintento sin duplicados. Incompletas y simuladas diferenciadas.
- [ ] Historial/detalle/revisión de pérdidas, filtros pertinentes, exportación/reinicio/recuperación conservan datos previos y ampliaciones integradas. No grabar PCM.
- [ ] Inspector y banco simulado aislados, mostrando pertenencias/reglas/centroide/acción, no textos estáticos. Base preparada para adaptar las otras variantes sin compartir sus logros.
- [ ] Pruebas conductuales de cambios, enfriamiento, techo/suelo, pérdidas sin disparos, elegibilidad, doble guardado y pausa; Android con persistencia, demostración y simulaciones sin alterar perfil.

## D4 — Defender de tríadas por nombre

**Qué construir:** jugar la segunda variante con acordes escritos, controles de raíz/alteración/calidad y progresión inicial de tríadas en sol, conservando su propio historial y avance.

**Bloqueado por:** D3, del que reutiliza arcade, DDA y persistencia.

- [ ] Tríadas mayor/menor/disminuida/aumentada en fundamental y clave de sol; intervalos y grafías de la especificación, sin dobles alteraciones no soportadas.
- [ ] Seleccionar raíz escrita+calidad y Disparar, sin octava ni texto libre; no aceptar sustitución enarmónica de raíz. Respuesta mostrada se deriva del mismo modelo que pentagrama/sonido.
- [ ] Sin duplicados vivos por respuesta raíz escrita+calidad; esperar si pool temporalmente agotado. No bloquear búsqueda ni producir configuraciones musicales inválidas para llenar cupo.
- [ ] Acorde al acertar, controles y feedback comunes, selección conservada al fallar. Escena vertical y radial mantiene legibilidad de notas apiladas.
- [ ] DDA intrapartida y cadena de las cuatro tríadas con evidencia propia; catálogo futuro visible como no disponible si se muestra. Logros de notas no desbloquean acordes.
- [ ] Historial/exportación/detalle admiten contenido de acorde y errores sin reinterpretar datos de notas. Práctica de todas las tríadas implementadas sin desbloquear.
- [ ] Corpus de raíces/calidades y audio de acordes; sesión de unicidad/errores/progreso separado y partida Android completa con capturas verticales.

## D5 — Completar lectura de acordes

**Qué construir:** jugar las cuatro séptimas, reconocer acordes invertidos por raíz/calidad y avanzar por fa/mezcla con reglas y resultados propios.

**Bloqueado por:** D4.

- [ ] Dominante7, mayor7, menor7 y semidisminuido7 con cuatro voces correctas; no seguir usando restricciones de «tríada» para sus rangos/datos.
- [ ] Inversiones de disposición cerrada: primera/segunda de tríadas y hasta tercera de séptimas. Por nombre sigue siendo raíz+calidad; no confundir raíz con bajo ni añadir respuesta de inversión.
- [ ] Sol/fa/mezcla y prerequisitos de la especificación, sin dar acceso por notas. Primera inversión requiere el hito indicado; resto encadena por calidad, sin ciclos ni estados imposibles por omisión del catálogo.
- [ ] Notación legible, inversión/grafía correctas, pool finito y sin duplicados por respuesta aunque cambien registro/inversión.
- [ ] Evaluación por combinación calidad/inversión/clave y mínimos de exposición. No desbloquear por partidas de una calidad fácil ni por mezcla sin evidencia de ambas claves.
- [ ] Audio/renderizado/modelo cubren objetivos y registros realmente ofrecidos, con fuentes/transformaciones documentadas si se amplían muestras.
- [ ] Pruebas de todas las calidades/inversiones, unicidad, rangos y progresión; recorrido Android con séptimas, inversión, fa/mezcla, revisión y exportación.

## D6 — Piano virtual jugable en vertical

**Qué construir:** tercera variante con teclado Do3–Do5, selección toggle, sonido real al tocar y confirmación por conjunto exacto. Entrega inicial con mayores/menores fundamentales en práctica.

**Bloqueado por:** D5, que completa modelo/notación/generación de acordes y audio reutilizables.

- [ ] Misma escena vertical/radial y piano de Do3–Do5 inclusivos. Validar usabilidad en pantalla antes de fijar disposición: prototipo local de teclado si hace falta, documentando resultado/capturas. No reducir rango ni forzar horizontal sin nueva decisión.
- [ ] Tocar selecciona/desmarca, suena tecla; Disparar confirma sin simultaneidad obligatoria. Al fallar conservar conjunto y sonar error; al acertar limpiar y sonar acorde.
- [ ] Matching por conjunto exacto de teclas/octavas, sin extras. Enarmónicos escritos corresponden a misma tecla; no exigir nombres de teclas imposibles de distinguir físicamente.
- [ ] Generar solo objetivos completos dentro de Do3–Do5 y claves legibles. No transportar luego de mostrar ni aceptar otra octava como corrección. Unicidad por conjunto exacto vivo, no por raíz escrita.
- [ ] Práctica inicial mayor/menor fundamental, resumen e historial de la variante, sin heredar progresión de acordes. No anunciar catálogo completo o desbloqueos aún no disponibles.
- [ ] Banco sonoro cubre MIDI48–72, afinación/permiso/procedencia documentados; piano previo no se asume suficiente. Manejar precarga/fallo sin emitir respuestas o penalizar vidas.
- [ ] Pruebas de selección, notas extra, octava incorrecta, grafías y unicidad; recorrido Android con sonido y piano vertical. Registrar verificación física pendiente sin presentarla como realizada.

## D7 — Piano completo con progreso independiente

**Qué construir:** practicar y progresar con todas las calidades/inversiones aceptadas en el teclado, inspeccionar decisiones y revisar historial de las tres variantes con compatibilidad completa.

**Bloqueado por:** D6.

- [ ] Habilitar catálogo mayor/menor/disminuido/aumentado/séptimas e inversiones exactas dentro de rango; filtrar pool por clave/registro sin prometer toda raíz en cualquier contexto.
- [ ] Progresión propia de calidad/inversión/clave y desbloqueos de la especificación; la variante por nombre no acredita habilidades del piano. Práctica de cualquier contenido implementado no desbloquea.
- [ ] DDA intrapartida por errores, supervivencia y tiempo normalizado; el tiempo hasta construir y Disparar cuenta, sin penalizar una supuesta simultaneidad de teclas no exigida.
- [ ] Mostrar solución de teclas/octavas al perder enemigo sin alterar selección del jugador ni detener partida; revisión final distingue conjunto esperado/enviado.
- [ ] Historial/filtros/inspector muestran las tres variantes separadas, datos y reglas correctos; exportación/reinicio/recuperación preservan todos los formatos previos y pendientes de guardado.
- [ ] Banco reproducible de casos de piano y DDA calculados, sin tocar perfil. No atribuir error a una tecla particular si no existe objetivo elegido inequívoco.
- [ ] Compilación, pruebas y lint pertinentes; guía de demostración por variante y registro visual/audio/funcional, identificando resultados de JVM, emulador y teléfono. No afirmar eficacia ni calibración por aprobar pruebas.

## Puntos de prueba aprobados

1. **Sesión pública del arcade:** acciones/disparos/pausa + tiempo/azar controlados → enemigos, vidas, respuesta, resultados, historial y DDA; audio/almacenamiento en sus límites.
2. **Contenido musical y Android:** corpus de grafía→altura→respuesta/teclas, más revisión visual/audio en vertical y pruebas de flujo real. Separar prueba física de las verificaciones de emulador.

## Registro de publicación

Publicadas #22–#29 con `ready-for-agent`, criterios completos y cuerpos UTF-8 con saltos de línea reales mediante `--body-file`. Bloqueos nativos verificados: #24←#23, #25←#24, #26←#25, #27←#26, #28←#27, #29←#28; #23 sin bloqueos. Identificadores y verificación en `.agents/defender-issues.json` para retomar sin duplicados. No se modificaron ni cerraron issues anteriores. No se inició código Defender en esta fase documental.

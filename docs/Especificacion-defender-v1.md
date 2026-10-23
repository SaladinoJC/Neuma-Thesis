# Defender — especificación de notas, acordes y piano

Estado: síntesis y valores iniciales confirmados mediante «perfeto»; puntos de prueba, división y dependencias aprobados mediante «perfecto» el 2026-10-22. Implementación pendiente.

Publicada como [issue #22](https://github.com/SaladinoJC/Tesis/issues/22). [Plan aprobado y tickets #23–#29](Plan-tareas-defender-v1.md).

Origen: [entrevista confirmada](Definicion-defender.md). Referencia visual aportada: [capturadefender.png](imagenes/capturadefender.png). Android/offline, Kotlin/Compose y motor Mamdani vigentes. Las elecciones técnicas, funciones difusas y prerrequisitos siguientes son configuración experimental inicial; no implican calibración, eficacia pedagógica ni aprobación del director.

## Problema

El usuario quiere practicar lectura musical bajo presión arcade mediante enemigos que muestran notas o acordes en pentagrama. Necesita responder construyendo el nombre o el conjunto de teclas, recibir feedback sin interrumpir la partida, revisar errores y progresar sin confundir velocidad de respuesta con dominio de contenidos todavía no practicados.

## Solución

Tres variantes por etapas, con motor arcade común y progreso independiente: notas → acordes por nombre → acordes en piano virtual. Pantalla vertical espacial, acercamiento radial hacia base inferior, tres vidas y supervivencia sin duración fija. Responder con Disparar; solo llegar a base quita vida. DDA ajusta velocidad de enemigos nuevos y desbloquea contenido al terminar partidas elegibles.

## Historias de usuario

1. Como estudiante, quiero elegir Defender de notas, acordes por nombre o piano para practicar tareas distintas.
2. Como estudiante, quiero ver siempre la clave y notación legible de cada enemigo.
3. Como jugador, quiero enemigos que se acerquen radialmente hacia mi base, como en la referencia vertical.
4. Como jugador, quiero tres vidas y perderlas únicamente cuando llega un enemigo.
5. Como jugador, quiero responder a cualquier enemigo sin seleccionarlo ni apuntar.
6. Como jugador, quiero que Disparar evalúe en el instante de pulsación y que la animación no me haga perder una respuesta a tiempo.
7. Como estudiante, quiero construir la respuesta con controles dedicados y no con texto libre.
8. Como estudiante, quiero que un error se marque abajo sin frenar la partida ni borrar mi selección.
9. Como estudiante, quiero ver la solución donde estaba un enemigo perdido y revisarla al terminar.
10. Como jugador, quiero escuchar notas/acordes al acertar y controlar efectos por separado.
11. Como jugador, quiero pausar sin que los enemigos avancen ni se acumule una ráfaga de apariciones.
12. Como estudiante de notas, quiero progresar de sol dentro del pentagrama a líneas adicionales, fa y mezcla.
13. Como estudiante de notas, quiero activar alteraciones sin que sean una etapa obligatoria de desbloqueo.
14. Como estudiante de notas, quiero nombres Do–Si o C–B y responder sin octava.
15. Como estudiante, quiero que se respete la escritura musical, sin aceptar otra raíz/nombre enarmónico en respuestas por nombre.
16. Como jugador de notas, quiero que una coincidencia duplicada elimine solo el enemigo más próximo a base.
17. Como estudiante de acordes, quiero reconocer mayor, menor, disminuido, aumentado y las cuatro séptimas acordadas.
18. Como estudiante de acordes, quiero identificar raíz y calidad aunque el acorde esté invertido, sin responder la inversión.
19. Como estudiante de acordes, quiero progresar de sol a fa y mezcla con evidencia propia de esta variante.
20. Como estudiante de piano, quiero seleccionar y desmarcar teclas entre Do3 y Do5 y confirmar sin simultaneidad obligatoria.
21. Como estudiante de piano, quiero escuchar las teclas al tocarlas, el acorde al acertar y error al fallar.
22. Como estudiante de piano, quiero que se exijan exactamente las notas y octavas escritas, sin teclas extra.
23. Como estudiante, quiero practicar contenido bloqueado con dificultad manual sin modificar mis desbloqueos.
24. Como estudiante, quiero elegir una etapa desbloqueada para progresar, sin que repetir otra fácil acredite esa etapa.
25. Como jugador, quiero cambios de velocidad graduales solo para enemigos futuros.
26. Como usuario, quiero guardar y exportar resultados, opciones y progreso, conservando el historial previo de la app.
27. Como tesista, quiero inspeccionar entradas, pertenencias, reglas y decisiones reales del DDA y ejecutar simulaciones separadas.
28. Como tesista, quiero reproducir generación, trayectorias y evaluación con semilla, eventos y configuración originales.

## Decisiones de implementación

### Motor, reloj y matching

- Sesión pública independiente de Android/Compose. Inyectar reloj, azar, audio y repositorios en sus límites. Reutilizar motor Mamdani y transacciones existentes sin copiar políticas de otros juegos.
- Enemigo: identificador, contenido escrito/alturas, respuesta esperada, etapa, trayectoria, aparición, tiempo de viaje y deadline. Usar tiempo monótono activo, excluyendo pausas; dibujo y simulación separados. No basar colisiones en frame de animación ni tiempo de llegada del callback de UI.
- Llegada si tiempo de evento ≥deadline; disparo anterior correcto elimina inmediatamente. En empate exacto prevalece llegada. Procesar eventos pendientes cronológicamente, desempate estable por ID, sin quitar una cuarta vida ni contar enemigos cancelados por game over como llegados.
- Notas: matching por nombre diatónico+alteración, sin octava; duplicados permitidos, eliminar el más próximo geométricamente a la frontera de base, empate por aparición/ID. No cambiar esa regla por «menor tiempo restante» si las velocidades difieren.
- Acordes por nombre: raíz escrita+calidad, sin octava/inversión; no duplicar esa respuesta entre enemigos vivos. Piano: conjunto exacto de teclas/MIDI, sin duplicados simultáneos por conjunto, aun con otra grafía enarmónica.
- Solo permitir envío de respuesta completa. Al acertar limpiar selección; al fallar conservarla y registrar intento completo sin coste directo de vida. En piano sonido al tocar una tecla, incluso al desmarcar; al confirmar fallo sonar error. Controles de audio no cambian la respuesta esperada.
- Game over al perder tercera vida; 100 puntos por eliminación, sin bonos inicialmente. Sin vida perdida por efectos visuales ni objetivos todavía vivos al cerrar. Salida explícita registra interrupción, no partida completa de progresión.

### Composición y presentación vertical

- Referencia de composición/estética espacial en pixel art, fondo oscuro y acentos neón. HUD superior con etapa, vidas, puntos y pausa; combate encima de controles; nave/base inferior; botón Disparar grande. La captura no sustituye el renderizado musical.
- Aparición en arco superior/laterales y trayectorias radiales hacia base, no solo descenso vertical. Coordenadas normalizadas del campo y duración propia por enemigo para que distancia/tamaño de pantalla no cambien la dificultad accidentalmente.
- Máximo cuatro vivos. Reservar espacio para pentagramas y trayectorias legibles; si no existe espacio o contenido distinto admisible, esperar sin penalización. No encimar claves, alteraciones o notas para llenar el cupo. La legibilidad prevalece sobre mostrar siempre cuatro.
- Enemigo acertado desaparece lógicamente y su animación de impacto es decorativa. Error: destello/aviso inferior sin detener; perdido: solución sobre última posición durante un intervalo breve (propuesta 1,2 s), sin tapar controles. Revisión final conserva contenido y solución.
- Pausa congela tiempo activo/audio, oculta objetivos y retoma sin saltos ni ráfagas. Segundo plano pausa; recreación no reinicia vidas ni duplica resultados; muerte de proceso puede dejar interrupción, nunca completar. Registrar pausas sin penalizar tiempo de reacción.
- No mostrar nombre del estímulo antes de resolver. En notas/acordes solo sonarlo al acertar; efectos con volumen/activación separados. Piano suena al pulsar teclas y reproduce el acorde al acertar, sin demo del objetivo. Sonido de error activado inicialmente y gobernado por efectos.
- Verificar controles de nombres/alteraciones, calidad y piano en vertical. Propuesta inicial de piano: dos octavas completas en una fila dentro del panel inferior; si el tamaño táctil no resulta usable, prototipo local de distribución antes de fijarla. No recortar rango, sustituirlo por una octava ni exigir giro horizontal silenciosamente.

### Generación y notación musical

- Modelo separado de grafía y altura absoluta. Cada objetivo conserva nombre diatónico, alteración, octava, clave, calidad, inversión y alturas exactas cuando corresponda. Notación y respuesta esperada derivan del mismo contenido.
- Pentagramas independientes sin armadura compartida ni arrastre de alteraciones entre enemigos. Alteraciones simples explícitas; excluir dobles sostenidos/bemoles inicialmente. No aceptar Do♯ como Re♭ en respuestas por nombre. En piano ambas grafías se corresponden con la misma tecla física.
- Notas: cuatro etapas N1 sol dentro; N2 sol ampliada; N3 fa ampliada; N4 mezcla sol/fa. Concreción conservadora: N1 Mi4–Fa5; N2 La3–Do6; N3 Do2–Mi4; N4 combina los rangos ampliados. Extremos en la segunda línea adicional, sin extenderse al siguiente espacio exterior. Hasta dos líneas adicionales arriba/abajo, clave siempre visible. Alteraciones opcionales en notas, sin nuevo nivel, registradas en resultados; no equivalen a prohibir las alteraciones internas necesarias de un acorde.
- Calidades e intervalos cromáticos desde fundamental: mayor `[0,4,7]`, menor `[0,3,7]`, disminuido `[0,3,6]`, aumentado `[0,4,8]`, dominante7 `[0,4,7,10]`, mayor7 `[0,4,7,11]`, menor7 `[0,3,7,10]`, semidisminuido7 `[0,3,6,10]`. Escribir terceras/quintas/séptimas según letras musicales, no elegir un nombre enarmónico por conveniencia gráfica. Filtrar combinaciones que exigirían dobles alteraciones no soportadas.
- Inversiones iniciales de disposición cerrada: estado fundamental y primera/segunda de tríadas; hasta tercera en séptimas. Sin omisiones ni duplicaciones de voces. Por nombre se sigue respondiendo fundamental+calidad; en piano se exige el conjunto concreto mostrado.
- Acordes por nombre: sol→fa→mezcla; elegir registro que mantenga el acorde completo legible, con máximo dos líneas adicionales como configuración gráfica inicial, filtrando voicings incompatibles. No prometer todas las raíces/inversiones en cualquier registro/clave.
- Piano: Do3–Do5 inclusivos (MIDI48–72), todas las notas del objetivo dentro del rango, incluida la cuarta de una séptima. Elegir combinación válida antes de mostrarla; no transportar después ni aceptar otra octava al corregir. Usar claves habilitadas que permitan lectura; las restricciones pueden reducir el pool disponible para una combinación concreta de clave/calidad/inversión.
- Construir pool finito válido por configuración, filtrar unicidad contra vivos y sortear con semilla. Si el pool queda vacío, explicar configuración no disponible antes de iniciar; si solo se agota temporalmente por enemigos vivos, esperar. Evitar rechazo sin límite y registrar contenido realmente ofrecido.
- Fuentes/permiso y tratamiento de sprites, fuentes musicales y samples documentados antes de incorporarlos. Piano actual no cubre por sí solo Do2 ni todo Do3–Do5; ampliar audio con fuente verificable o síntesis explícita y comparar afinación/registro, sin reproducir una octava incorrecta.

### DDA intrapartida experimental

Valores confirmados como punto de partida: tiempo de viaje inicial10 s, rango6–14 s y paso máximo1 s. Aparición inicial al comenzar; intervalos4 s, 3,5 s desde30 s, 3 s desde60 s y 2,5 s desde90 s de tiempo activo. Mantener cupo4; omitir apariciones imposibles sin acumular pendientes. Curva fija de aparición, sin un segundo control DDA.

- Ventana: últimos cinco enemigos resueltos, contando eliminados y llegados. Ajustar por primera vez al quinto y luego tras al menos tres resoluciones nuevas desde la última evaluación aplicable. Enemigos cancelados por interrupción/game over no son resueltos. Si datos inválidos, mantener y explicar.
- `supervivencia S`=eliminados/resueltos. `precision P`=disparos correctos/(correctos+fallidos) en los intervalos de tiempo de las cinco resoluciones, incluyendo los fallos sin enemigo asignable; si no hubo intentos, P=0 y registrar denominador0. Conservar eventos para reconstruir la ventana. No atribuir una respuesta fallida a una raíz/calidad específica sin objetivo seleccionado.
- `rapidez A`=1−promedio de fracción de viaje consumida; cada eliminado aporta `(tiempo activo desde aparición)/(viaje asignado)`, acotado a[0,1], y cada llegada aporta1. Así un enemigo perdido no desaparece de la evaluación de rapidez.
- Entrada `solidez Q`=min(S,P). Pertenencias de Q: bajo `[0,0,0.40,0.60]`, medio `[0.40,0.60,0.70,0.85]`, alto `[0.70,0.85,1,1]`. A: lenta `[0,0,0.25,0.45]`, media `[0.25,0.45,0.60,0.80]`, rápida `[0.60,0.80,1,1]`.
- Salida delta de tiempo de viaje sobre[-1,1]: alargar `[0.20,0.60,1,1]`, mantener `[-0.30,-0.10,0.10,0.30]`, acortar `[-1,-1,-0.60,-0.20]`. Reglas: Q baja→alargar; Q media+A lenta→alargar; Q media+A media/rápida→mantener (dos reglas); Q alta+A lenta→alargar; Q alta+A media→mantener; Q alta+A rápida→acortar.
- AND/implicación mínimo, agregación máximo y centroide con1001 puntos. Centroide>0,25 propone+1 s; <−0,25 propone−1 s; resto0. Aplicar límites6–14 s y registrar propuesta, aplicación y motivo. Ninguna regla válida/centroide ausente no provoca ajuste.
- Solo los futuros enemigos usan el nuevo tiempo de viaje. Los vivos conservan trayectoria, velocidad y deadline. Partida nueva arranca en10 s de prueba; etapa recién desbloqueada no hereda presión creciente de otra partida. Práctica usa tiempo de viaje manual y puede inspeccionar propuestas sin aplicarlas.

### Desbloqueos y evidencia persistente

- Progreso independiente por variante y etapa/contenido. Práctica, simulación e interrupción no desbloquean. Progresión elige entre etapas desbloqueadas, sin seleccionar manualmente contenido bloqueado que luego cuente como logro automático.
- Partida completa=game over legítimo por tercera llegada, no salida manual. Elegible con≥10 resueltos; al evaluar un contenido/clave concreto exigir al menos tres apariciones resueltas. Fallidos globales cuentan en precisión del conjunto para evitar desbloquear por disparos al azar; no inventar atribución a enemigo.
- Motor persistente usa S/P de la partida y del contenido objetivo. Misma partición baja/media/alta definida para Q, aplicada a ambas entradas; salida bajo `[0,0,0.25,0.45]`, medio `[0.30,0.45,0.55,0.70]`, alto `[0.60,0.80,1,1]` sobre[0,1]. Reglas: S baja→bajo; S media+P baja→bajo; S media+P media/alta→medio; S alta+P baja→bajo; S alta+P media→medio; S alta+P alta→alto. Listas con barra implican una regla por combinación, no operador ambiguo.
- Alto si centroide≥0,75, medio si≥0,40, bajo en otro caso. Dos completas elegibles consecutivas altas del MISMO objetivo habilitan siguiente prerrequisito al guardar y para próxima partida. No alta elegible reinicia racha; insuficiencia/interrupción/práctica conservan sin sumar. No retirar logros existentes.
- Notas N1→N2→N3→N4. Para dominar N4 exigir evidencia de ambas claves, sin dejar que una compense la otra; el modo opcional de alteraciones se registra/filtra y no añade un escalón. Naturales y alteradas pueden desbloquear la misma ruta, sin exigir jugar ambas.
- Acordes y piano: calidad inicial mayor en sol/fundamental. Cadena de calidades mayor→menor→disminuido→aumentado→dominante7→mayor7→menor7→semidisminuido7. La etapa elegida fija calidad objetivo; no desbloquearla solo por aciertos de otras calidades.
- Propuesta de prerrequisitos independiente para cada variante: completar dominio de las ocho calidades fundamentales en sol habilita fa y primeras inversiones. Mezcla se habilita al dominar las ocho fundamentales en fa. Primera inversión por calidad requiere su fundamental y el hito previo de las ocho en sol; segunda requiere primera; tercera solo para séptimas y requiere segunda. La mezcla no se hereda de otra variante. Son decisiones de configuración pedagógica provisional que se pueden ajustar sin reescribir el arcade.
- Identificar objetivo como variante+calidad/inversión+clave o etapa de notas. No confundir reconocimiento de acorde invertido por nombre con respuesta explícita de inversión. En mezcla registrar cada clave y exigir mínimo de exposición por ambas. Si un contexto no tiene pool válido, no consumir vida ni fingir evidencia; mostrar restricción.
- Guardado de resultado y avance coordinados e idempotentes, con reintento visible y sin doble desbloqueo. Mantener evidencia/versiones si cambia la configuración; no reinterpretar logros previos silenciosamente.

### Registro, revisión y compatibilidad

- Por sesión: variante, origen, etapa elegida, configuración/versiones, semilla/pool, tiempos activos/pausas, opciones de nombres/alteraciones/audio, puntos/vidas y completa/interrumpida. Por enemigo: contenido escrito/MIDI y esperado, ID, trayectoria, viaje/deadline, resultado o cancelación y tiempo de resolución. Por disparo: timestamp, respuesta, coincidencia o fallo global. Registrar decisiones y rachas antes/después.
- Historial local y filtros por variante, etapa/origen/opciones; detalle con enemigos perdidos, solución y tiempos, sin mezclar estadísticas de variantes. Puntaje100 por eliminado es presentación, no indicador único de dominio.
- Mostrar en HUD la etapa y vidas; si hay barra de desbloqueo, derivarla de evidencia real y explicar cuándo falta una partida elegible. Presión intrapartida separada del contenido desbloqueado; no inventar avance de nivel por animar una barra.
- Exportación/reinicio/recuperación originales incluyen datos nuevos conservando todos los historiales existentes, incluidas ampliaciones integradas de afinador/ritmo. No almacenar grabaciones ni introducir micrófono. Fallos de lectura/guardado no dejan app bloqueada ni aparentan guardado exitoso.

## Decisiones de prueba propuestas

### Sesión pública común

Acciones de selección/disparo/pausa y reloj/azar controlados → enemigos, vidas, resultado, historial y decisiones DDA. Sustituir audio y almacenamiento en sus límites, sin comprobar internals privados. Antecedentes: sesiones de instrumentos/Simón, Mamdani e historial/resiliencia existentes.

Casos: respuesta correcta justo antes/deadline/justo después; duplicados y distintas velocidades; fallo sin objetivo; selección conservada; tres llegadas y cancelados; pausa/recreación; cupo/pool agotado sin ráfagas; enemigos visibles no acelerados; práctica sin desbloqueo; evidencia por contenido, reintento de guardado y simulación separada.

### Musical y Android visual/audio

- Corpus verificable objetivo escrito→respuesta→teclas/sonido: extremos de rango, claves, grafías enarmónicas no intercambiables por nombre, todas las calidades/inversiones, ausencia de dobles alteraciones no soportadas, filtro de rango y pools pequeños. Dibujar pentagrama correcto, no confiar en los símbolos musicales dibujados en la captura.
- Verificación Android en vertical contra referencia: legibilidad hasta cuatro enemigos, trayectorias radiales, controles, feedback inferior/solución en posición, pausa, sonidos y piano. Mostrar captura/recorrido de cada incremento; si el piano no resulta usable, prototipo de distribución y registrar decisión antes de ampliar catálogo.
- Banco DDA aislado con datos calculados: acelerar/ralentizar/mantener, pérdidas sin disparos, evidencia insuficiente, enfriamiento, límites6/14, pertenencias/fronteras, desbloqueos, claves mezcladas y guardado duplicado. La simulación no altera perfil.
- Compilación, pruebas pertinentes y lint según proyecto. Prueba física S22 Ultra: versión/app/Android, legibilidad y pulsación vertical, audio y latencias con condiciones/método. Emulador no acredita comodidad física ni latencia acústica; informar pendiente sin inventar resultados.

## Fuera de alcance

- Micrófono/instrumentos físicos, puntería, daño por balas decorativas, multijugador y backend.
- Clave de do, dobles alteraciones, voicings abiertos/omisiones/doblajes y rango de piano mayor que Do3–Do5.
- DDA que acelera enemigos ya visibles o modifica frecuencia de aparición; retirar desbloqueos, bonos de velocidad y diagnóstico pedagógico.
- Protocolo oficial/Gantt y atribuir al director aprobación de esta ampliación. No depender de implementar primero afinador o ritmo.

## Notas

El protocolo disponible propone Defensa de acordes en su selección inicial; las fichas12/13/14 la clasificaban como futura/secundaria. El usuario confirmó planificar las tres por etapas. Este documento registra ese alcance del prototipo y sustituye reglas anteriores incompatibles, sin transformar esa decisión en aprobación académica ni en implementación existente.

El usuario aprobó puntos de prueba y granularidad/dependencias mediante «perfecto», autorizando publicación de especificación y siete tickets. Las configuraciones experimentales podrán cambiar tras medición, conservando trazabilidad.

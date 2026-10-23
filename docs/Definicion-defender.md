# Defender — definición del primer alcance

Estado: síntesis y valores iniciales confirmados mediante «perfeto»; puntos de prueba, siete incrementos y dependencias aprobados mediante «perfecto» el 2026-10-22. Especificación #22 y tickets #23–#29 publicados; implementación pendiente.

## Intención confirmada

El usuario quiere incorporar alguno de los minijuegos Defender desarrollados: enemigos representados por contenido musical avanzan hacia un límite; el jugador responde antes de que lleguen o pierde una vida. Después de definir y publicar afinador y ritmo, pidió continuar con Defender mediante `grill-with-docs`.

## Variantes documentadas

| Ficha | Estímulo | Respuesta documentada | Currículum |
| --- | --- | --- | --- |
| [13 — Notas](Ideas%20Desarrolladas/13-Defensa-de-notas-en-partitura.md) | Una nota en pentagrama con clave visible | Nombre y alteración con botones, sin octava | Clave, rango/líneas adicionales y alteraciones |
| [12 — Acordes](Ideas%20Desarrolladas/12-Defensa-de-acordes-en-partitura.md) | Acorde escrito en pentagrama | Raíz y calidad con botones | Calidades y posterior tratamiento de inversiones |
| [14 — Piano virtual](Ideas%20Desarrolladas/14-Defensa-de-acordes-en-piano-virtual.md) | Acorde escrito en pentagrama | Conjunto de teclas, con octava exacta, selección toggle y Enviar | Calidad e inversión dentro del rango visible |

Las fichas atribuyen sus detalles a entrevistas anteriores y separan los currículos/progreso aunque proponen compartir el motor arcade. La elección actual del usuario y su precedencia se registran en la primera ronda siguiente.

## Primera ronda confirmada por el usuario (2026-10-22)

- Planificar las tres variantes por etapas: **notas (13) → acordes por nombre (12) → acordes en piano virtual (14)**, aceptando la recomendación mediante «dale, vamos en ese orden».
- Conservar supervivencia con tres vidas y sin duración fija. Solo perder vida cuando un enemigo llega a la base; respuesta incorrecta registrada y reintento inmediato, sin pérdida directa de vida.
- Compartir componentes arcade y conservar contenido y progreso propios de cada variante. Esto no acredita que los componentes ya existan ni que las tres variantes deban entregarse juntas.

## Segunda ronda confirmada por el usuario (2026-10-22)

- Progresión de notas: sol dentro del pentagrama → líneas adicionales → fa → mezcla de claves. Hasta dos líneas adicionales arriba y abajo; clave de do para futura ampliación. El usuario cuestionó el valor de un nivel separado de alteraciones: no se conserva ese escalón. Falta precisar si las alteraciones se excluyen del alcance o quedan como opción sin nivel propio, y el rango inicial de fa.
- Respuesta con botones de nombre y alteración, botón de envío y sin octava. Aceptó Do–Re–Mi–Fa–Sol–La–Si con opción C–D–E–F–G–A–B y exigir la escritura mostrada, sin sustituirla por un enarmónico. La presencia efectiva de alteraciones depende del punto anterior.
- Responder a cualquier enemigo visible sin seleccionarlo. Respuesta coincidente elimina uno; si hay duplicados, eliminar el más próximo a la base.
- **Pantalla vertical**, reemplazando la propuesta horizontal del agente. Referencia aproximada aportada por el usuario: [capturadefender.png](imagenes/capturadefender.png), inspeccionada visualmente.

## Tercera ronda confirmada por el usuario (2026-10-22)

- Alteraciones como opción activable/desactivable, sin escalón propio de desbloqueo. Registrar su uso para distinguir resultados.
- Fa incluye directamente el rango ya habilitado de hasta dos líneas adicionales arriba/abajo. Se conservan cuatro etapas: sol dentro → sol ampliada → fa → mezcla. Clave siempre visible.
- Aparición en la zona superior alrededor de la nave/base y **acercamiento radial**, no descenso vertical recto. Interpretación de composición: arco superior/laterales que converge en la base inferior de la referencia. No se incorpora juego de puntería.
- Hasta cuatro enemigos simultáneos para la primera prueba, ajustable tras comprobar legibilidad en teléfono. Mantener pentagramas separados; limitar capacidad no obliga a esperar que desaparezcan todos para volver a generar.
- DDA intrapartida ajusta velocidad de enemigos nuevos; cada enemigo visible conserva su velocidad. Curva de aparición gradual y límite de simultáneos, sin un segundo control DDA de frecuencia inicialmente. Desbloqueos permanentes separados de la adaptación de velocidad.

## Cuarta ronda confirmada por el usuario (2026-10-22)

- Evaluar al pulsar Disparar y eliminar lógicamente el enemigo acertado en ese instante; la animación del proyectil puede continuar, sin hacer perder una vida por su demora.
- Sonido de la nota musical al eliminar correctamente y efectos de disparo/explosión opcionales, con controles independientes. No sonar el enemigo antes de responder.
- Respuesta sin coincidencia: error visual en zona inferior sin detener partida, conservando la selección. Posible sonido de error, aún como opción por probar.
- Enemigo que llega a base: mostrar respuesta correcta donde estaba, sin detener partida. Animación/duración por concretar durante diseño/verificación visual. Al terminar, revisar enemigos perdidos.
- Progresión con contenido desbloqueado y DDA; práctica con cualquier etapa y dificultad manual, resultados diferenciados y sin desbloqueos.
- En progresión se elige entre etapas desbloqueadas y se evalúa por etapa. Repetir una fácil no demuestra dominio de otra.

Estas reglas comunes se conservan para las etapas posteriores salvo una diferencia explícita de su modalidad de respuesta.

## Quinta ronda confirmada por el usuario (2026-10-22): acordes por nombre

- Calidades: mayor → menor → disminuido → aumentado → séptimas. Las séptimas documentadas y aceptadas son dominante, mayor7, menor7 y semidisminuido, incorporadas por separado. Primero tríadas en estado fundamental; después séptimas e inversiones.
- En inversiones se responde raíz/fundamental y calidad, sin pedir la inversión ni la octava. La inversión agrega dificultad de lectura; no confundir raíz con nota más grave. Esto resuelve la ambigüedad de la ficha 12 sin añadir un campo de respuesta que no estaba especificado.
- Clave de sol primero, después fa y luego mezcla, con progreso propio. Desbloquear una clave en notas no la desbloquea automáticamente en acordes.
- Respetar escritura mostrada, no aceptar sustitución enarmónica de raíz. Controles: nombre de raíz, alteración, calidad y Disparar.
- Sin dos enemigos simultáneos con la misma respuesta esperada raíz escrita+calidad, aunque sus octavas/inversiones difieran. La generación debe respetarlo sin bloquear indefinidamente si el pool es pequeño.

## Sexta ronda confirmada por el usuario (2026-10-22): piano virtual

- Rango inicial Do3–Do5, dos octavas, con objetivos completos dentro del rango. Comprobar usabilidad de las teclas en la pantalla vertical; no considerar aprobada una distribución concreta que aún no se mostró.
- Selección toggle: tocar selecciona, volver a tocar desmarca. Disparar confirma el conjunto, sin simultaneidad obligatoria. Tras error conservar selección para corregir.
- Exigir conjunto exacto de teclas, incluida octava, sin notas extra. Las inversiones exigen la disposición mostrada. Una tecla física sirve para nombres enarmónicos equivalentes, a diferencia del matching por grafía de notas/acordes por nombre.
- Sonar al tocar las teclas, sonar el acorde al acertar y sonido de error al errar. No reproducir el objetivo previamente. Conserva progresión de calidades propia, sin heredar desbloqueos de la variante por nombre.

## Síntesis funcional confirmada

### Motor y experiencia comunes

- Tres variantes en orden: notas → acordes por nombre → acordes en piano virtual, con progreso independiente y motor arcade compartido.
- Vertical y estética/composición aproximadas de la referencia espacial. Enemigos con pentagrama y clave visibles, llegada radial desde arco superior/laterales hacia nave/base inferior. Controles abajo. Probar máximo cuatro simultáneos y evitar solapar notación.
- Supervivencia de tres vidas sin tiempo fijo. Solo llegar a base quita vida; respuesta fallida permite reintentar y conserva selección. No añadir daño por los proyectiles decorativos.
- Disparar evalúa en el instante de entrada y elimina lógicamente el objetivo antes de animar el proyectil. Sin selección manual de enemigo: notas resuelve duplicados por proximidad; acordes no repite respuesta raíz escrita+calidad; piano no repite conjunto exacto de teclas.
- Error visual inferior sin detener partida. Enemigo que llega muestra respuesta correcta donde estaba y aparece en revisión final. Sonidos musicales al acertar y efectos con controles independientes; en piano las teclas suenan al tocarlas y hay sonido de error.
- Progresión con etapas desbloqueadas y DDA; práctica con cualquier etapa/dificultad manual, sin desbloquear. Historial/resultados separados por variante, etapa, origen y ayudas/opciones.
- DDA Mamdani: desbloqueos permanentes y velocidad intrapartida de nuevos enemigos. Los visibles conservan su velocidad. Aparición gradual limitada, sin DDA de frecuencia inicialmente.

### Contenido por variante

- **Notas:** sol dentro → sol con hasta dos líneas adicionales arriba/abajo → fa con el rango ampliado → mezcla. Clave de do futura. Alteraciones opcionales sin nivel propio. Nombre/alteración con escritura exacta, sin octava; Do–Si o C–B.
- **Acordes por nombre:** mayor, menor, disminuido, aumentado; después séptimas dominante, mayor7, menor7 y semidisminuido e inversiones. Sol → fa → mezcla con progreso propio. Raíz escrita+calidad incluso en inversiones; no pedir octava ni número de inversión.
- **Piano:** mismas calidades con progreso propio, Do3–Do5, selección toggle y conjunto exacto con octavas. Generación/clave deben mantener legibilidad del registro, sin transportar después de mostrarlo ni cambiar la respuesta a otra octava.

## Concreciones propuestas para la primera prueba

Elecciones iniciales del agente aceptadas para probar al confirmar la síntesis. Sin mediciones ni validación pedagógica:

- **Velocidad:** tiempo de viaje base de 10 s por enemigo, rango experimental de 6–14 s; DDA puede modificar el tiempo de viaje de futuros enemigos en hasta 1 s por ajuste. Medir longitud de trayectoria y conservar deadline por enemigo para que pantalla y posición de entrada no otorguen tiempos distintos accidentalmente. Inicio de una partida nueva vuelve a base de prueba; no presentar esa base como calibrada según habilidad.
- **Aparición:** primera nave al comenzar, intervalo inicial de 4 s, reduciendo 0,5 s cada 30 s hasta un mínimo de 2,5 s. Máximo cuatro vivos; no acumular apariciones pendientes para soltarlas juntas cuando se libere un lugar. Si no hay contenido distinto o espacio legible, esperar sin perder vidas.
- **DDA intrapartida:** observar últimos cinco enemigos resueltos (eliminados o llegada a base), ajustar como máximo cada tres resoluciones nuevas. Combinar precisión de disparos, supervivencia y fracción de tiempo de viaje consumida; no evaluar solo eliminaciones rápidas ignorando enemigos perdidos. Datos insuficientes mantienen velocidad. Reglas y pertenencias se concretan como configuración explicable en la especificación.
- **Desbloqueos:** proponer dos partidas completas consecutivas de dominio operativo alto por etapa, con al menos diez enemigos resueltos y exposición suficiente al contenido objetivo. Evaluar precisión/supervivencia por etapa/contenido; no desbloquear una calidad nueva sin que se haya practicado la que sirve de prerrequisito. Velocidad de reacción gobierna presión intrapartida, no por sí sola el acceso a contenido. Concretar grafo y reglas en la especificación; el rótulo dominio no acredita competencia musical validada.
- **Puntaje:** 100 puntos por enemigo eliminado, sin bonos de velocidad/racha inicialmente. No usar puntaje bruto como único criterio de desbloqueo. HUD separa etapa de contenido y presión de velocidad; barra, si se usa, representa evidencia real de desbloqueo, no un aumento ficticio en cada disparo.
- **Entrada y tiempo:** aceptar solo respuesta completa; al acertar limpiar selección para próxima respuesta, al fallar conservar. Comparar timestamp monótono contra deadline: respuesta anterior a llegada elimina, llegada en el mismo instante o antes cuesta vida. Animación/framerate no decide el resultado.
- **Pausa:** congelar reloj de simulación y audio, ocultar objetivos mientras está pausado y restaurar sin avanzar enemigos ni generar ráfagas. Segundo plano pausa; salir termina como interrumpida sin acreditar partida completa. Registrar pausas y excluir ese tiempo de reacción. Verificar conducta al recrear la actividad.
- **Feedback visual:** destello inferior de error, overlay breve de respuesta sobre última posición del enemigo perdido, explosión decorativa sin daño propio. Duraciones/contraste y distribución del teclado se verifican con una demostración visual en vertical; no prometer la imagen de referencia como interfaz ya implementada.
- **Registro:** semilla, contenido escrito/alturas, respuesta esperada, trayectoria y tiempos de aparición/llegada/eliminación, intentos, vidas, decisiones DDA y versiones, configuración/modo/audio/alteraciones, resumen e interrupción. Persistencia/exportación/reinicio compatibles con prototipo y ampliaciones integradas. Datos simulados aparte, sin grabaciones de audio.
- **Límites:** sin micrófono, puntería, multijugador, daño extra por balas, clave de do ni diagnósticos pedagógicos. El renderizador de ritmo no sustituye notación de alturas/acordes. No hay código Defender implementado en esta fase.

## Referencia visual y concreciones pendientes

La captura muestra un arcade espacial en pixel art, fondo oscuro con acentos neón, HUD superior con nivel/barra, tres corazones, puntaje y pausa; naves enemigas con pentagrama encima; base/ciudad abajo; panel inferior con nombres de nota y botón grande de disparo. Es referencia de composición/estética, no una imagen para sustituir el renderizado musical ni un contrato de todas sus mecánicas.

Se propone interpretar el botón de envío como «Disparar» y animar un acierto sin añadir puntería manual ni cambiar la regla de matching ya aceptada. La semántica de barra/nivel debe definirse. La supervivencia sigue dependiendo de enemigos que llegan a base; no asumir daño adicional por los proyectiles dibujados en la referencia.

Concreción técnica propuesta para la síntesis: evaluar la respuesta en el instante de pulsar Disparar y retirar lógicamente al enemigo acertado antes de la animación. La duración del proyectil visual no debería hacer perder una vida después de responder correctamente a tiempo. Resolver con reloj monótono y una regla de empate explícita, no con el framerate.

## Base común documentada y límites

- Tres vidas, supervivencia sin tiempo fijo; perder vida al llegar un enemigo a la base. Intento incorrecto registrado, sin coste directo de vida, con reintento disponible.
- Varios enemigos posibles, generación y movimiento, lectura de pentagrama miniatura y respuesta construida con controles dedicados. Las fichas no usan voz ni teclado de texto del sistema.
- Diferencias de matching: notas permiten respuestas duplicadas y resuelven por proximidad a la base; acordes evitan duplicados; piano exige conjunto exacto con octavas. Resolver después de elegir variante, sin unificar estas reglas silenciosamente.
- DDA de dos capas en los borradores: desbloqueos persistentes y velocidad durante partida. El proyecto usa Mamdani; ventanas/umbrales y rubber-banding de las fichas requieren concretarse de forma compatible, sin llamarlos calibrados.
- El renderizador rítmico está especificado, no implementado; tampoco cubre automáticamente alturas, claves, alteraciones o acordes. Las fichas describen componentes propuestos, no un motor Defender ya construido.
- Discrepancia de alcance: las fichas clasifican estas ideas como futuras/secundarias; `IdeasPrototipo.md` sugiere incluir la 12. La petición actual confirma planificar las tres variantes, empezando por la 13; no constituye aprobación del alcance académico final por el director.

## Próximas decisiones

Definición y plan cerrados y publicados: [especificación](Especificacion-defender-v1.md) y [siete incrementos](Plan-tareas-defender-v1.md). Primer ticket disponible: [#23, notas básicas en sol](https://github.com/SaladinoJC/Tesis/issues/23), independiente de implementar primero afinador/ritmo. No reabrir decisiones aceptadas ni repetir aprobaciones. La implementación no comenzó en esta fase; afinador, ritmo y Defender quedan definidos para próximos incrementos.

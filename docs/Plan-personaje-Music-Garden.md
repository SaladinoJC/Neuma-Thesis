# Personaje personalizable — definición y plan de implementación

Estado: **plan y orden P0–P7 confirmados por el usuario; implementación pendiente por pedido expreso**, 2026-10-28. Sin implementación iniciada ni publicación en issues.

Confirmación de cierre: «La confirmo, todavía no implementemos nada, dejalo como pendiente». La entrevista queda cerrada; no iniciar P0 ni los demás incrementos, generar assets o prototipos por este plan hasta que el usuario solicite retomarlo.

## Síntesis vigente

Esta sección y el plan por incrementos expresan el resultado de las cinco rondas. Las recomendaciones históricas que aparecen más abajo quedan resueltas por las respuestas posteriores; no representan preguntas todavía pendientes.

- **Entrada:** elegir Guitarra, Piano, Trompeta, Flauta o Violín; después personalizar aspecto o continuar con un personaje predeterminado. Usuarios existentes eligen su familia una sola vez y conservan historial. Editar apariencia y cambiar de familia después es gratuito.
- **Personaje:** humano explorador 2D de Music Garden, formas simples y planos suaves de papel recortado. Piel, peinado/color, nombre, vestimenta inicial, dos tipos de cuerpo sin etiquetas de género y vello facial. Todas las prendas/rasgos compatibles con ambos cuerpos; no se pierde la colección al cambiar.
- **Personaje en el libro:** nombre, ilustración animada, Personalizar y Hacerlo sonar; conservar los cuatro señaladores y la política de navegación ya acordada. El armario reúne piezas disponibles/bloqueadas, hito y progreso. Espacios: cabeza, torso, piernas, instrumento y dos accesorios libres con control de incompatibilidad por ubicación.
- **Movimiento y audio:** respiración/balanceo visibles y gestos breves por interacción, con reducción de movimiento y acciones inmediatas. Piano con escena propia y personaje sentado al hacerlo sonar; portátiles sostenidos/apoyados. Frase original común de unos tres segundos; timbre coherente con el tipo de instrumento, compartible entre acabados del mismo tipo. Instrumento usado con sonido afinado; reproducción explícita, detenible y sin cola.
- **Progresión:** ocho hitos fijos, práctica y desempeño por cada juego, con los umbrales experimentales de Q18. Seis prendas/accesorios y dos grados de modelo instrumental. Cada grado tiene cinco variantes, una por familia; cambiar de familia habilita el equivalente de los hitos cumplidos sin repetirlos y conserva lo anterior. Mismo hito y familia producen la misma recompensa en todos los dispositivos.
- **Entrega:** obtener automáticamente al cerrar una partida válida, avisar al finalizar, marcar Nuevo en el armario y dejar la decisión de equipar al jugador. Reconocer historial previo solo cuando permita verificar el hito; deduplicar entregas.
- **Catálogo:** quince modelos instrumentales (cinco iniciales y diez desbloqueables), seis recompensas de ropa/accesorios y opciones iniciales gratuitas. Las cinco filas de Q22 están aceptadas como catálogo de partida; los dibujos concretos siguen sujetos a revisión visual. No hay monedas ni tienda en esta entrega.

La implementación y las decisiones técnicas detalladas de abajo son trabajo futuro. La función se encuadra como gamificación complementaria al núcleo de audio/DDA; no se presenta como evidencia de eficacia pedagógica.

## Pedido y antecedentes

El usuario solicita planificar la creación inicial de un personaje al abrir por primera vez la app, su presencia en el señalador Personaje y su personalización estética con ropa y accesorios obtenidos jugando. Considera desbloqueos directos o monedas ganadas en partidas para comprar en una tienda; no eligió un modelo. Propone 2D con aspecto algo low poly, coherente con Music Garden, y consulta por animación.

El menú ya tiene acordados cuatro señaladores y apertura en Personaje. La ilustración genérica era una solución temporal: la creación y progresión estética se habían reservado para después. Esta entrevista desarrolla esa ampliación; las maquetas anteriores no implementan sus reglas.

Referencias: `ART_DIRECTION.md`, `Diseno-menu-libro-Music-Garden.md`, `../CONTEXT.md`, `../assets/ASSET_MANIFEST.md` y `../.agents/estado-prototipo.md`.

La guía pide papel recortado, formas orgánicas y paleta natural. La ronda 1 confirma la interpretación low poly como humano estilizado de planos suaves compatible con ese acabado, y establece una excepción a la ausencia de bucles del menú: respiración y balanceo del personaje mientras esté visible. La guía se actualiza con esta excepción; no se extiende al fondo ni al libro.

## Ronda 1 — decisiones confirmadas

Origen: respuestas explícitas del usuario a Q1–Q5, 2026-10-28. Sustituyen las recomendaciones iniciales allí donde difieren.

1. **Identidad (Q1 «sí»):** humano estilizado tipo explorador del jardín, con silueta compartida y acabado de papel recortado con planos de color suaves. Apariencia concreta pendiente de muestra visual.
2. **Obtención (Q2, ampliada por Q17):** cada hito concede una recompensa predeterminada, sin elección aleatoria. Originalmente idéntica para todos los jugadores; Q17 introduce una excepción explícita para modelos de instrumento: se concede el modelo correspondiente a la familia elegida. Para el mismo hito y familia se conserva igualdad de recompensa. Tienda diferida, sin economía de monedas en esta primera definición. La igualdad de recompensa no implica sincronización entre dispositivos.
3. **Qué se recompensa (Q3 «dale», con precisión):** habrá recompensas por práctica y algunas por desempeño. Debe haber accesorios asociados a cada juego, de modo que no se pueda obtener toda la colección jugando solo Simón. No se fijaron todavía hitos, cantidades, condiciones ni cobertura del primer incremento.
4. **Primera apertura (Q4 «dale»):** presentación breve con posibilidad de personalizar o aceptar un personaje predeterminado; edición posterior gratuita. Rasgos editables pendientes.
5. **Movimiento (Q5):** respiración y balanceo permanentes mientras el personaje esté visible; también gestos breves al entrar, tocarlo o cambiar ropa. Sustituye la recomendación de reposo estático. Se conserva la reducción de movimiento y la prohibición de bloquear acciones/audio. Amplitud, ritmo, combinación de gestos y suspensión fuera de pantalla se resolverán en el diseño técnico.

## Ronda 2 — decisiones confirmadas

Origen: respuestas explícitas del usuario a Q6–Q10, 2026-10-28. Sustituyen las recomendaciones iniciales allí donde difieren.

- **Q6 — Rasgos:** tono de piel, peinado, color de pelo, nombre, vestimenta inicial, tipo de cuerpo y vello facial. El usuario pide dos tipos de cuerpo asociados a las siluetas masculina/femenina sin mostrarlos con etiquetas de género, por inclusividad. Esto amplía la silueta única de Q1: se conserva identidad compartida, pero no se presuponen proporciones idénticas. Disponibilidad cruzada de rasgos/prendas y cambio posterior de cuerpo pendientes de Q11. La edición posterior gratuita acordada en Q4 se mantiene.
- **Q7 — Categorías:** cabeza, torso, piernas, instrumento, accesorio 1 y accesorio 2. Los accesorios pueden ser joyas, relojes, aros, etc. Sustituye la propuesta de calzado y espalda como categorías independientes. Compatibilidad anatómica entre los dos espacios de accesorios y función del instrumento pendientes de Q12–Q13.
- **Q8 — Práctica válida («ok»):** repetir ejercicios fáciles puede avanzar los hitos de práctica si se cumple un mínimo de actividad por juego. No cuentan abandonos ni simplemente abrir/cerrar. En Simón se requerirá alguna secuencia completada antes del error final. Mínimos y cantidades exactos pendientes como parámetros de prueba; no implican eficacia pedagógica validada.
- **Q9 — Modo manual:** cuenta para hitos de práctica. Los hitos de desempeño verifican las condiciones explícitas del objetivo, sin cambiar el desbloqueo actual de niveles. Laboratorios y resultados simulados no otorgan cosméticos.
- **Q10 — Historial previo («ok»):** concesión retroactiva cuando los resultados guardados permiten comprobar el hito, sin duplicados ni inferencia de actividad perdida. No recupera partidas de ritmo/Defender que solo estuvieron en memoria.

## Ronda 3 — decisiones confirmadas

Origen: respuestas explícitas del usuario a Q11–Q15, 2026-10-28. Sustituyen las recomendaciones iniciales allí donde difieren.

- **Q11 — Compatibilidad con el cuerpo («sí»):** todos los peinados, opciones de vello facial y prendas disponibles para ambos tipos de cuerpo. Se puede cambiar de cuerpo conservando los objetos obtenidos. Selector mediante miniaturas sin etiquetas de género; las prendas deben adaptarse a ambas siluetas.
- **Q12 — Dos accesorios («sí»):** espacios libres con compatibilidad por ubicación; no equipar dos piezas incompatibles que ocupen el mismo lugar. Un par de aros es un objeto. Mostrar la incompatibilidad antes de sustituir una pieza.
- **Q13 — Instrumento:** además de llevarlo, existe una acción explícita **Hacerlo sonar** que reproduce una pequeña melodía con el timbre del instrumento equipado. Sustituye la recomendación anterior de función puramente estética. No se solicitó teclado interactivo ni ejecución de notas por el jugador. Instrumentos iniciales, melodía y reglas de reproducción pendientes.
- **Q14 — Armario y objetivos («sí»):** Personaje muestra nombre, ilustración animada y Personalizar. La personalización permite editar rasgos y acceder al armario con objetos disponibles y bloqueados; cada ficha bloqueada muestra su hito y progreso. Se conservan los cuatro señaladores del menú.
- **Q15 — Primera colección («ok»):** ocho recompensas, dos por cada uno de los cuatro juegos, una por práctica y otra por desempeño, además de opciones iniciales gratuitas. Catálogo y umbrales concretos pendientes. Persistir resultados de ritmo/Defender es una dependencia; se puede dividir la entrega en incrementos.

## Ronda 4 — decisiones confirmadas

Origen: respuestas explícitas del usuario a Q16–Q19, 2026-10-28. Los números aceptados son parámetros iniciales de prueba, sin calibración pedagógica.

**Q16 — Melodía («dale»):** misma frase breve original de unos tres segundos en cada instrumento, reproducida solo al pulsar Hacerlo sonar. Detener durante reproducción, sin cola; salir de Personaje o cambiar instrumento corta la reproducción. La propuesta técnica añade cancelación en segundo plano; la reproducción no depende del final de un gesto ni de que haya movimiento habilitado. Seis notas es una propuesta de composición, no una melodía ya producida. La flauta como inicio fijo queda sustituida por Q17.

**Q17 — Instrumento elegido y modelos:** al crear el personaje se elige Guitarra, Piano, Trompeta, Flauta o Violín, según lo que toca el jugador o su preferencia. Se comienza con un modelo genérico/desgastado de esa familia y se desbloquean otros modelos de la misma. Sustituye tanto flauta inicial para todos como violín/trompeta fijos en las recompensas. El usuario acepta las demás prendas/accesorios propuestos. Guitarra criolla vieja, Stratocaster, Les Paul y guitarra de concierto son ejemplos dados por el usuario para la progresión, no un catálogo de cuatro guitarras aprobado. Los modelos concretos y sus timbres quedan por definir. El concepto de mejora se refiere a la colección/apariencia; no se acordaron ventajas en juegos ni alterar la afinación del audio.

| Juego | Recompensa de práctica | Recompensa de desempeño |
| --- | --- | --- |
| Simón | Sombrero de explorador (cabeza) | Chaleco botánico (torso) |
| Instrumentos | Aros de hojas (accesorio) | Otro modelo de la familia de instrumento elegida |
| Rhythm Tap | Pantalón de lino (piernas) | Reloj de madera (accesorio) |
| Defender | Collar de semilla (accesorio) | Otro modelo de la familia de instrumento elegida |

**Q18 — Condiciones («ok para probar»):** práctica: cinco partidas válidas de cada juego para su respectiva recompensa. Pueden repetirse contenidos y jugarse manualmente. Cada ejecución debe tener identidad propia; reabrir un resultado no cuenta de nuevo. Condiciones aceptadas como primera configuración experimental:

| Juego | Partida válida para práctica | Hito de desempeño, en una partida válida |
| --- | --- | --- |
| Simón | Cierre normal por error tras completar al menos dos notas de secuencia (`completedLength >= 2`) | Completar secuencia de seis notas o más en pentatónica |
| Instrumentos | Contestar las diez preguntas y finalizar, sin mínimo de aciertos | Al menos ocho aciertos de diez en nivel A |
| Rhythm Tap | Finalizar el ejercicio con al menos un tap asociado a un ataque esperado | A 60 BPM, negras/corcheas, al menos ocho ataques esperados, 80 % o más `ON_TIME` sobre todos los ataques esperados y como máximo dos taps extra; metrónomo permitido |
| Defender | Final natural de partida con al menos un enemigo eliminado | Al menos diez eliminados en una partida terminada, etapa N1 sin alteraciones, viaje de 10 s y máximo un enemigo |

Abandonos y simulaciones no cuentan. Las omisiones de ritmo permanecen en el denominador; no medir precisión solo sobre los taps realizados. Práctica y desempeño pueden cumplirse a la vez. Los modos deben satisfacer las condiciones del hito: no se altera el DDA ni el desbloqueo de niveles. Las configuraciones nuevas no heredan elegibilidad por tener un puntaje parecido: requiere revisión/versionado de reglas.

**Q19 — Entrega:** los objetos se agregan automáticamente al armario; se avisa al terminar la partida si hubo desbloqueos y se marcan como Nuevo. No se equipan automáticamente. Propuestas técnicas complementarias: agrupar concesiones retroactivas en un resumen al volver a Personaje, conservar objetos al bajar desempeño o equipar otra pieza e integrar estos datos al reinicio completo del perfil. La conservación de objetos al cambiar cuerpo ya está confirmada por Q11; cambio de familia pendiente de Q20.

## Ronda 5 — decisiones confirmadas

Origen: respuestas explícitas del usuario a Q20–Q24, 2026-10-28. No quedan preguntas de estas rondas pendientes.

- **Q20 — Cambiar de familia («ok»):** cambio gratuito, conservando hitos y modelos anteriores; se habilitan los modelos equivalentes de los hitos cumplidos, sin repetirlos. Cada hito instrumental representa un grado de colección con cinco variantes; no se consume al conceder la de una familia. Ver [ADR 0003](adr/0003-hitos-cosmeticos-y-modelos-por-familia.md).
- **Q21 — Sonido de modelos («ok»):** timbre coherente con el tipo real (criolla frente a eléctrica, por ejemplo); compartir audio entre acabados del mismo tipo inicialmente. El instrumento usado suena afinado y funcional. No reproducir eléctrica y presentarla como criolla.
- **Q22 — Catálogo de modelos («ok»):** un modelo inicial y dos desbloqueables por familia, con la distribución siguiente. Los acabados no acreditan mayor calidad acústica ni ventajas de juego. Dibujos/nombres definitivos se revisan en muestra.

| Familia | Inicio | Hito de Instrumentos | Hito de Defender |
| --- | --- | --- | --- |
| Guitarra | Criolla usada | Eléctrica estilo Stratocaster | Guitarra de concierto |
| Piano | Vertical antiguo | Vertical restaurado | Piano de cola |
| Trompeta | Latón desgastado | Latón pulido | Acabado plateado |
| Flauta | Travesera usada | Travesera plateada | Travesera con detalles botánicos |
| Violín | Modelo de estudio usado | Barniz miel | Modelo de concierto |

Hay ocho hitos, seis recompensas no instrumentales y dos hitos de modelo con cinco variantes cada uno (16 objetos de recompensa en el catálogo). Los instrumentos requieren quince modelos incluyendo los cinco iniciales, además de las prendas iniciales. No describir la producción como solo ocho dibujos u ocho objetos máximos por perfil: cambiar de familia permite acceder a equivalentes.

- **Q23 — Representación del piano («sí»):** escena adaptada con personaje junto al piano, sentado cuando lo hace sonar; instrumentos portátiles sostenidos/apoyados. Mantener visibles personaje/rasgos y controles; comprobar oclusiones con ropa/accesorios en ambos cuerpos.
- **Q24 — Omitir personalización («ok»):** elegir primero una de las cinco familias y después personalizar aspecto o continuar con personaje predeterminado. Para usuarios existentes, preguntar la familia una sola vez sin borrar resultados; no inferirla desde el historial.

## Hechos del prototipo que condicionan el plan

Relevamiento de código y documentación, 2026-10-28:

- Android usa Kotlin/Compose; el menú de libro y Personaje siguen como estudio web. No existen onboarding, editor, catálogo cosmético, armario ni economía implementados.
- La navegación se integra desde `Plataforma/app/src/main/java/` mediante `MainActivity.kt`; la ubicación exacta de nuevas clases se resolverá al definir los incrementos.
- La persistencia JSON versionada dispone de reemplazo atómico y deduplicación. Los datos nuevos deben incluirse también en exportación, recuperación y reinicio: `Demonstration.kt` enumera explícitamente los archivos existentes.
- El desbloqueo de niveles existente se deriva de resultados y no constituye una economía cosmética. Debe conservarse la distinción de dominio.
- Simón e Instrumentos tienen resultados persistidos. En Simón, una partida completa puede terminar por un error: completada no significa ganada. Una regla que premie simplemente terminar permitiría iniciar y fallar de inmediato; requiere una condición de práctica elegible.
- Rhythm Tap y Defender conservan resultados en memoria en el incremento actual; recompensar sus partidas de forma persistente necesita ampliar esa base o limitar explícitamente la primera cobertura.
- Los borradores contienen posturas sobre recompensas extrínsecas, pero no se verificaron sus fuentes primarias en esta entrevista. No son evidencia suficiente para aceptar o descartar monedas.

Relevamiento adicional, 2026-10-28:

- `InstrumentMelody.kt` admite 1–8 notas MIDI entre Do4/Re4/Mi4/Sol4/La4 y duraciones de 100–800 ms. `AndroidInstrumentPlayer.kt` y `AndroidSamplePlayer.kt` ya reproducen frases por instrumento con muestras locales. Hay nueve timbres y 45 OGG; flauta/violín/trompeta están incluidos.
- El bloqueo del reproductor es por instancia: la integración debe garantizar exclusión y cancelación al navegar, no asumir un control global inexistente. No reutilizar la sesión del juego para esta demostración sonora ni generar resultados/recompensas desde ella.
- Guitarra tiene banco aparte del afinador, en otro registro: no incorporarla a esta frase sin adaptar explícitamente banco/composición. Piano/marimba tienen audio, pero su representación equipable requiere una escena distinta a un instrumento portátil.
- Q17 exige guitarra desde la primera colección: el banco existente es guitarra eléctrica limpia, no criolla/nylon. Hay que presupuestar audio acústico apropiado y adaptar el catálogo/reproductor; no presentar todos los timbres/modelos como recursos ya listos. La relación modelo→timbre y la frase común se fijarán después de Q21.
- No ampliar por defecto el enum educativo `Instrument` ni las opciones del juego de reconocimiento para añadir Guitarra: el catálogo del personaje debe poder referenciar audio independientemente de las reglas del minijuego. Con los bancos actuales, el registro común para guitarra del afinador y los otros cuatro timbres es Do4/Re4/Mi4; otra frase requerirá adaptar o ampliar las muestras. Decidir composición después de resolver los timbres, sin transportar audio silenciosamente.
- En Defender, final natural significa perder las tres vidas (`FINISHED`/`GAME_OVER`), no una victoria. Bajo los hitos Q18 propuestos y aceptados, abandonar tras diez eliminaciones con vidas restantes no concede el premio. Incluir esa condición en el texto del hito y la demostración; no interpretar diez eliminaciones como fin automático de partida.
- Rhythm Tap carece de identificador estable de ejecución/fecha; el fingerprint representa contenido, no partida. Defender registra resultados por enemigo con IDs reiniciados en cada sesión: falta el resumen de partida persistible con UUID y fecha. Son dependencias de deduplicación, no detalles opcionales.
- Se consultó directamente el cuerpo de `PROTOCOLO_Saladino.doc` mediante extracción en memoria del Word OLE, sin modificarlo. Prevé progreso y reconocimiento de logros y excluye XR/motores 3D pesados; no prescribe personajes, cosméticos ni tienda. La ampliación 2D puede encuadrarse como gamificación subordinada al núcleo de audio/DDA, sin atribuir eficacia motivacional o pedagógica. No se interpretaron figura/Gantt incrustados ni se fijó cronograma académico.

## Plan de implementación por incrementos

División técnica propuesta por el agente y confirmada por el usuario junto con la síntesis el 2026-10-28. Q1–Q24 fijan el producto; la entrevista está cerrada. Todos los incrementos P0–P7 quedan pendientes por pedido expreso, hasta que el usuario solicite retomarlos.

| Incremento | Resultado demostrable | Dependencias |
| --- | --- | --- |
| P0 — Prueba visual y de capas | Ambos cuerpos, prendas/accesorios, instrumento portátil y escena de piano; muestra de movimiento y contrato de anclajes | Guía de arte, tokens y catálogo confirmado |
| P1 — Primera apertura y perfil | Elegir una de cinco familias, personalizar o continuar, ver Personaje y conservarlo al reabrir | P0; integrar menú nativo existente/propuesto sin romper acceso a juegos |
| P2 — Armario y colección | Editar/equipar piezas compatibles en ambos cuerpos, consultar bloqueados, cambiar de familia y conservar equivalentes | P1; catálogo versionado, quince modelos instrumentales y seis premios no instrumentales |
| P3 — Movimiento y Hacerlo sonar | Respiración/balanceo/gestos y frase común con timbre apropiado para las cinco familias/modelos | P2; audio criolla/nylon preparado y muestras existentes revisadas |
| P4 — Recompensas de Simón e Instrumentos | Cuatro hitos con progreso persistente, reconocimiento histórico y aviso al final | P2; resultados persistidos existentes, P3 para demostrar el modelo ganado con sonido |
| P5 — Recompensas de Rhythm Tap | Dos hitos adicionales evaluados desde resultados identificables y durables | P4; incorporar UUID/fecha/persistencia de cada ejecución de ritmo |
| P6 — Recompensas de Defender | Últimos dos hitos y segundo grado de modelo de instrumento | P4; incorporar resumen final de partida con UUID/fecha/persistencia |
| P7 — Demostración integrada | Recorrido de cuatro juegos, ocho hitos, cambios de cuerpo/familia, sonido y recuperación de perfil | P0–P6 completos |

P5 y P6 no dependen entre sí. El orden puede ajustarse a la disponibilidad de cada frente sin inventar progreso ni mostrar como concedidos hitos aún sin evaluación. La planificación no fija fechas ni cambia el Gantt académico.

### P0 — Validar arte antes de producir el catálogo completo

- Preparar una muestra con ambos cuerpos, un conjunto básico, dos accesorios compatibles/incompatibles, guitarra y piano; ensayar respiración, balanceo y gesto con todas las capas. Reutilizar Music Garden C/Escenario como referencia común sin rediseñarlo.
- Definir coordenadas/anclajes, orden de capas, partes que siguen brazos/cabeza y variantes sentada/de pie. Evitar que cada combinación exija una ilustración completa independiente.
- Entrega: muestra revisable, maestros editables y matriz de piezas/estados. Aprobar el acabado de la muestra antes de producir los quince modelos. Las cantidades de peinados, tonos y variantes de ropa gratuita se proponen en la lámina; no se presentan como cifras ya confirmadas por el usuario.
- Criterio: rasgos/prendas legibles en ambos cuerpos, controles libres y movimiento sin desprender accesorios. Registrar todo asset producido con dimensiones, origen/licencia, estados y exportación. Ningún boceto genérico actual se considera automáticamente asset final.

### P1 — Perfil y primera apertura

- Incorporar Personaje a Kotlin/Compose manteniendo Juegos/Afinador/Logros y accesos existentes. La maqueta web no es una implementación Android del menú.
- Selección de familia antes de apariencia; cierre y reapertura durante la creación permiten retomar sin bloquear el acceso. Como elección técnica inicial, guardar la familia confirmada y un aspecto predeterminado; el editor usa borrador con Guardar/Cancelar, sin sobrescribir silenciosamente cambios no confirmados.
- Registrar nombre/rasgos, familia activa y versión del perfil. No borrar historial al migrar usuarios existentes; preguntar familia cuando todavía no fue elegida.
- Criterio: nuevas instalaciones y perfiles existentes llegan a Personaje; omitir apariencia funciona; cambiar rasgos persiste tras reapertura; una falla de guardado informa y permite reintentar.
- Integrar desde este incremento los datos nuevos a exportación, recuperación, reinicio confirmado y coordinación de escritura existentes; no postergar estas garantías hasta P7.

### P2 — Armario y reglas de compatibilidad

- Catálogo con IDs estables por objeto, familia, grado de modelo y hito; separar objeto adquirido, equipado y marca Nuevo. Una categoría Instrumento no introduce Guitarra en el enum ni en los niveles del juego de reconocimiento.
- Resolver disponibilidad de modelos a partir de hitos permanentes independientes de familia. Cambiar de familia conserva todas las adquisiciones; el modelo que queda equipado se selecciona mediante una elección explícita del usuario o su básico al entrar por primera vez, sin autoequipar premios.
- Accesorios por ubicación: permitir combinaciones compatibles, avisar antes de sustituir. Adaptar todas las prendas a ambos cuerpos; mantener rasgos al cambiar ropa. Mostrar requisitos/progreso de bloqueados sin simular cumplimiento.
- Criterio: dos perfiles con mismo hito/familia muestran el mismo modelo; un cambio guitarra→violín→guitarra conserva hitos y equivalentes. Cambiar cuerpo no elimina prendas ni permite superposiciones prohibidas. Cerrar/reabrir conserva equipamiento.

### P3 — Movimiento y reproducción

- Preferencia técnica inicial del agente: piezas 2D por capas, anclajes y transformaciones nativas Compose. Adoptar Rive/Lottie/sprites solo si P0 evidencia una limitación concreta que justifique dependencia y mantenimiento.
- Respiración/balanceo mientras Personaje sea visible; gestos al entrar/tocar/cambiar ropa. Evitar colas ante toques rápidos y suspender trabajo decorativo fuera de pantalla/segundo plano. Con reducción de movimiento, conservar toda la funcionalidad con presentación estática o transición inmediata.
- Frase original común de aproximadamente tres segundos, estructurada como notas/duraciones; asignar timbre por tipo y compartirlo entre acabados equivalentes. Criolla/concierto pueden compartir nylon; eléctrica usa timbre eléctrico. El banco actual de piano/trompeta/flauta/violín es candidato reutilizable, sujeto a escucha; nylon requiere preparación con fuente/licencia comprobables.
- Hacerlo sonar/Detener: una frase activa, cancelación al navegar, cambiar modelo/familia o pasar a segundo plano. No bloquear equipamiento por un gesto, no mezclar con entrenamiento y no activar micrófono ni producir resultados de juego.
- Criterio: ejecutar la misma frase con las cinco familias, distinguir acústica/eléctrica, mantener instrumento/accesorios alineados y verificar cancelación repetida en Android. La escucha en teléfono y el rendimiento se registran con dispositivo/condiciones; no se dan por medidos desde el estudio web.

### P4 — Reglas de recompensa y primeros cuatro hitos

- Evaluar las reglas Q18 desde resultados guardados, con ID de partida e identificador/versionado del hito. Los ocho hitos pertenecen a una colección común y no reemplazan medallas ni acceso a niveles del DDA.
- Registrar cumplimiento durable, reconocer historial verificable y derivar disponibilidad de familia sin volver a consumir un hito. Separar concesión de equipamiento y de presentación del aviso.
- Al guardar el cierre de partida, evaluar y persistir premios, actualizar armario y avisar al final con Nuevo. Si la app se interrumpe entre resultado y concesión, reconciliar al reabrir sin perder el premio ni duplicarlo. Mostrar fallos de persistencia; no anunciar guardado exitoso si no ocurrió.
- Criterio: historial importado/reprocesado/reabierto no suma la misma partida dos veces; mínimos/abandonos/manual/simulado se tratan según Q8/Q9/Q18; una partida puede conceder práctica y desempeño juntos. Cambiar de familia conserva equivalencias y no requiere nuevas partidas.

### P5 y P6 — Incorporar ritmo y Defender con datos durables

- Ritmo: UUID por ejecución, fecha, partitura/configuración, taps/ataques, final completo/interrumpido y procedencia. No usar fingerprint ni semilla como identidad de partida. Cada repetición realmente ejecutada puede tener identidad propia; reabrir resultados no crea una ejecución.
- Defender: resumen de partida con UUID/fecha, configuración congelada, causa de cierre, resultados por enemigo, eliminaciones y procedencia. Los IDs de enemigo no identifican partidas. Final natural para Q18 es pérdida de tres vidas; abandonar tras diez eliminaciones no basta.
- Incluir cada nuevo resultado en exportación/recuperación/reinicio y evaluar sus dos hitos mediante el mismo mecanismo de P4. No fabricar resultados de sesiones antiguas que nunca se guardaron.
- Criterio: concesión y progreso sobreviven al cierre; las omisiones de ritmo cuentan en el denominador, los extras no se convierten en aciertos y la configuración queda registrada. Mantener intactas reglas de juego y temporización de audio/entrada; el trabajo de guardado/evaluación no se ejecuta en el render ni condiciona cuándo llega un enemigo.

### P7 — Comprobación integrada

- Pruebas JVM: reglas en sus límites, identidad/deduplicación, concesión tras interrupción, equivalencias de familias, compatibilidad, migración/exportación/recuperación y exclusión de simulaciones. Verificar conducta pública, sin tests que solo repitan la estructura interna.
- Pruebas Android: primera apertura existente/nueva, elección y edición, cuatro juegos→premio→armario, cambio de cuerpo/familia, Hacerlo sonar/Detener, navegación/segundo plano, cierre/reapertura y recuperación. No ejecutar reinicios instrumentados contra el perfil personal del teléfono.
- Validación visual en 320/360/411/600 unidades útiles cuando corresponda, texto ampliado al 200 %, controles táctiles objetivo de 48 dp y TalkBack. Comprobar oclusiones del piano, selección y estados bloqueado/Nuevo/error, y reducción de movimiento.
- Registrar método/dispositivo/configuración de audio y resultado observado. La prueba funcional no acredita eficacia educativa ni calibración de los umbrales.
- Entrega: instrucciones de ejecución/demostración, resultados de checks y pendientes reales; actualizar estado del prototipo y manifiesto. No dar por terminada la colección si falta un timbre correcto, una familia o una recompensa sin persistencia.

## Decisiones de implementación todavía ajustables

Son elecciones técnicas del agente, no preguntas de producto pendientes: estructura exacta de clases/archivos, JSON y migración concretos, frase musical original, amplitud/ritmo de gestos y cantidades iniciales de muestras visuales. Resolverlas dentro de los incrementos y documentarlas, manteniendo las decisiones Q1–Q24. Si una limitación obliga a cambiar esas decisiones, señalarla antes de sustituirlas.

## Documentación y límites

Se aplican `grill-with-docs`, `grilling` y `domain-modeling`: registrar términos cuando queden resueltos y usar ADR solo para decisiones con un costo relevante de reversión y alternativas reales. No presentar las recomendaciones como reglas del glosario.

El protocolo ya se contrastó con el alcance de esta ampliación; no se modifica el documento académico ni se atribuye eficacia pedagógica a la personalización. Las cinco rondas y la síntesis/orden de incrementos están confirmados; el cierre de `grilling` está cumplido. El usuario pidió dejar la implementación pendiente: no iniciar código, prototipos ni producción de assets hasta que solicite retomar este plan.

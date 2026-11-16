# Neuma — guía de pruebas manuales en Samsung S22 Ultra

Preparada el **03/11/2026** para revisar la app antes de la reunión con el director.

**Esta guía es un plan de prueba: no registra pruebas físicas ya realizadas.** Los resultados se completan al ejecutarla. Actualización del 12/11/2026: la referencia es **Neuma Garden 0.12.24, código 39**, con las correcciones de afinador, Instrumentos, ritmo y Defender. Las observaciones del autor se conservan por versión en el [registro del 12/11](Pruebas-manuales-S22-Ultra-2026-11-12.md). Garden conserva el menú de libro y los casos de navegación de esta guía. Quiet Studio se instala al lado como variante comparativa de catálogo y ritmo: [instalación y recorrido](Comparacion-Garden-Quiet-Studio-v0.12.24.md).

## 1. Cómo usar la guía

Hacé los bloques en orden, en varias sesiones si hace falta. Primero probá sin grabar pantalla; después grabá los recorridos que quieras mostrar. La grabación puede afectar rendimiento y, si usa el micrófono, interferir con las pruebas de captura.

- Marcá un caso como **OK** únicamente si comprobaste todos sus resultados esperados.
- Usá **FALLA**, **BLOQUEADO** o **PENDIENTE** para los demás resultados. Una prueba que no pudiste completar no equivale a un fallo del algoritmo.
- Anotá el ID del caso, los pasos que hiciste y lo que ocurrió. Conservá captura o video cuando ayude a reproducirlo.
- Si un botón no entra, intentá desplazar la pantalla o la fila de opciones. Anotá si no es evidente que se puede desplazar.
- Si aparece un error de lectura o guardado, registralo y exportá lo disponible antes de cerrar el proceso. No hace falta provocar errores de almacenamiento para completar esta guía.

Las pruebas generan historial y pueden modificar tu progreso real cuando elegís Automático o Progresión. **Práctica permite recorrer contenidos sin desbloquearlos.** Los bancos de simulación comprueban reglas sin trasladar sus logros al perfil real.

### Orden de trabajo

| Bloque | Qué revisás | Requisito |
| --- | --- | --- |
| 2–3 | Preparación, instalación y navegación | Teléfono y APK |
| 4–7 | Simón, instrumentos, ritmo y lectura | Poder escuchar y tocar la pantalla |
| 8–9 | Prueba técnica de voz e intervalos | Micrófono, ambiente tranquilo y voz |
| 10 | Afinador completo | Guitarra de seis cuerdas para comprobar su uso real |
| 11–12 | Historiales, simulaciones y archivos | Datos generados en los bloques anteriores |
| 13 | Interrupciones y accesibilidad | Ajustes del teléfono |
| 14 | Borrado del perfil | Opcional; elimina datos |
| 15–16 | Preparación de demo y registro final | Resultados de las pruebas |

El recorrido cubre las funciones disponibles y propone matrices para sus catálogos. No verifica todas las combinaciones posibles de partidas aleatorias, acordes, dispositivos o condiciones acústicas.

## 2. Preparar el teléfono y la versión

### P1. Registrar las condiciones

Completá antes de empezar:

```text
Fecha y hora:
Modelo exacto del S22 Ultra:
Android / One UI:
Versión de Neuma instalada antes de actualizar:
Versión que se prueba:
Batería inicial / ahorro de energía:
Tamaño de fuente y zoom de pantalla:
Navegación por gestos o botones:
Salida de audio: altavoz / auriculares USB-C / Bluetooth:
Volumen multimedia aproximado:
Lugar y ruido ambiente:
Guitarra y afinación, si se usan:
Distancia aproximada entre fuente sonora y teléfono:
Grabación de pantalla activa: sí/no; micrófono de la grabación: sí/no:
```

1. Cargá el teléfono y elegí un lugar tranquilo.
2. Para la primera pasada usá el altavoz del teléfono, sin Bluetooth. Si después probás otra salida, registrala como otra condición.
3. Dejá libre el micrófono: cerrá grabadores y otras aplicaciones que estén capturando audio.
4. Conservá inicialmente tu configuración habitual de pantalla. Las variantes de accesibilidad se prueban al final.
5. Tené papel o este documento abierto en la computadora para anotar resultados.

**Esperado:** podés identificar las condiciones de cada prueba y repetirlas después.

### P2. Conservar datos e instalar la candidata

1. Si ya tenés Neuma con datos que querés conservar, abrí **Más → Datos y demostración → Exportar JSON** y guardalos antes de actualizar. Exportá también los originales ZIP si necesitás conservar los archivos locales.
2. Copiá al teléfono el [APK Neuma Garden 0.12.24](../Plataforma/build/apk-archives/neuma-garden-v0.12.24.apk).
3. Abrilo desde el administrador de archivos. Si Android pide habilitar la instalación desde ese origen, seguí el aviso del sistema.
4. Elegí actualizar la aplicación existente. **No la desinstales ni borres su almacenamiento para actualizar.**
5. Abrí Neuma Garden y consultá **Más**: debe indicar **Neuma Garden · 0.12.24**.
6. Revisá que sigan disponibles los historiales, niveles y guitarras que existían antes.

**Esperado:** se actualiza la misma aplicación, abre sin cerrarse y conserva los datos previos. Si la instalación falla, anotá el mensaje y detené este caso; no desinstales como solución automática. Si tenés una versión posterior instalada, no la reemplaces por una anterior para seguir esta guía.

Referencia del archivo comprobada al preparar la guía:

```text
Nombre: neuma-garden-v0.12.24.apk
Tamaño: 16.152.135 bytes
SHA-256: 6bf15b35b0c5cf3e5810f23bcf843f4b636ae32993d8f500a3cfd7044e8f8ce5
```

Esta candidata tiene comprobaciones de compilación y pruebas en computadora; sus ampliaciones recientes aún requieren la validación física que propone este documento. No ejecutes pruebas automatizadas de Android sobre tu teléfono: las del proyecto están destinadas a emuladores y pueden reiniciar el perfil.

## 3. Menú y navegación

### N1. Recorrer todas las entradas

1. Abrí la app desde el icono. Revisá nombre, icono, apertura y disposición respecto de la cámara y las barras del sistema.
2. Entrá en **Personaje**: comprobá que muestra su estado de preparación, sin esperar un editor de personaje funcional.
3. Entrá en **Juegos** y recorré las cinco páginas con las flechas: **Simón, Instrumentos, Rhythm Tap, Defender e Intervalos vocales**.
4. Tocá el contador de páginas para abrir **Índice**. Elegí cada actividad y comprobá que coincide portada, descripción y destino de **Jugar**.
5. Entrá a cada preparación y volvé al menú con su control visible. Anotá también qué hace Atrás de Android; si cierra la app en lugar de volver, registrá la pantalla exacta para revisar la navegación.
6. Entrá en **Afinador** y localizá **Libre, Guiado y Entrenamiento de oído**.
7. Entrá en **Logros** y alterná **Medallas / Progreso**. Recorré las páginas disponibles.
8. Abrí **Más** y comprobá **Resultados, Prueba de voz, Datos y demostración, Reducir movimiento y versión**.

**Esperado:** todas las entradas llevan al destino correspondiente; los controles son legibles y alcanzables. Personaje y Medallas tienen contenido provisional explícito: su personalización y colección no están implementadas. Logros no debe inventar progreso de los juegos que todavía no resume; consultá sus paneles propios.

**Anotá también:** cambios de orientación, textos cortados y diferencias visuales entre pantallas. Simón conserva una interfaz interior anterior oscura; no confundas esa diferencia existente con una instalación incorrecta.

## 4. Simón

Ruta: **Juegos → Simón → Jugar**.

### S1. Partida manual, teclado y escalas

1. Elegí práctica manual con **Pentatónica** e iniciá.
2. Comprobá la orientación horizontal y que el piano y los controles principales entren en pantalla.
3. Escuchá la primera nota y repetila. Continuá al menos tres secuencias correctas.
4. Durante una respuesta, tocá las notas con rapidez: no debería ser necesario esperar a que termine el sonido anterior para ingresar la siguiente.
5. Equivocate deliberadamente en una nota. Revisá longitud completada, secuencia intentada y posición del error.
6. Salí y revisá el resultado guardado. Abrí el mismo registro después de cerrar y reabrir la app.
7. Repetí una partida corta con **Diatónica mayor** y otra con **Cromática**. Revisá especialmente teclas negras, resaltado y correspondencia entre nota tocada y sonido.

**Esperado:** cada acierto agrega una nota a la secuencia; el primer error termina la partida. Los toques rápidos se aceptan sin superponer entradas del sistema. El resultado coincide con lo jugado y la práctica no concede desbloqueos.

### S2. Automático y progresión real

1. Anotá nivel y racha actuales. Elegí **Automático**.
2. Comprobá que comienza en el nivel más alto que ya tenés desbloqueado.
3. Jugá hasta obtener un resultado que el resumen clasifique como dominio alto; inspeccioná su explicación. No presupongas que cualquier partida terminada es alta.
4. Repetí en el mismo nivel. Si obtenés dos partidas completas consecutivas calificadas como altas, revisá el desbloqueo para la próxima partida.
5. Intercalá una práctica manual y comprobá que no suma evidencia automática.
6. Cerrá y reabrí la app: revisá nivel y racha persistidos.

**Esperado:** el desbloqueo se aplica entre partidas. La escala y la tónica de una partida iniciada no cambian por DDA. Si no conseguís dos resultados altos, dejá esa comprobación pendiente y usá el banco del bloque 12 para verificar la regla de forma reproducible.

### S3. Giro e interrupción

1. Iniciá otra partida y girá el teléfono durante reproducción y durante respuesta.
2. Comprobá que el cambio de orientación no duplica notas ni reinicia la secuencia.
3. En otra partida, salí al menú antes de terminar.
4. Repetí saliendo a Inicio de Android durante reproducción.

**Esperado:** el giro conserva la sesión; abandonar realmente interrumpe, detiene audio y no acredita una partida completa. Revisá que no se generen registros duplicados.

## 5. Reconocimiento de instrumentos

Ruta: **Juegos → Instrumentos → Jugar**.

### I1. Partidas manuales y reescuchas

1. Elegí el nivel A manual e iniciá una partida de diez preguntas.
2. En cada pregunta comprobá cuatro respuestas y una primera escucha automática.
3. Usá las dos reescuchas disponibles. Deben repetir la misma melodía de esa pregunta y no superponerse.
4. Intentá una cuarta escucha total: debe estar deshabilitada.
5. Respondé bien y mal en preguntas diferentes. Probá responder mientras suena la primera escucha y también durante una reescucha: las opciones se habilitan al comenzar el audio y elegir una detiene la melodía, muestra el resultado y conserva la cantidad de escuchas usadas. Tocá dos opciones rápidamente y comprobá que solo quede una respuesta. Revisá el instrumento correcto y continuá hasta el resumen.
6. Compará el resumen con tus respuestas y revisá su historial.
7. Repetí el recorrido en B y C. Anotá qué instrumentos efectivamente escuchaste; repetí preguntas o partidas si necesitás cubrir los nueve.

| Nivel manual | Instrumentos del banco |
| --- | --- |
| A | Piano, violín, flauta, marimba |
| B | Flauta, clarinete, trompeta, trombón |
| C | Flauta, clarinete, oboe, fagot |

**Esperado:** diez preguntas por partida, melodía conservada en las reescuchas y variedad entre preguntas. Los sonidos no se cortan inesperadamente, saturan ni presentan diferencias de volumen molestas; responder durante la reproducción sí detiene intencionalmente el audio. El tiempo que tardás en responder no se penaliza. Escuchar los nueve timbres no verifica por sí solo todas sus muestras de audio.

### I2. Adaptación de ayuda e inspector

1. Anotá nivel y racha. Elegí **Automático** e iniciá.
2. Intentá una secuencia de aciertos sin reescuchar. Después probá respuestas incorrectas y/o aciertos con reescuchas.
3. Tras cada respuesta revisá la ayuda anunciada para la siguiente pregunta.
4. Abrí el panel **DDA → Partida real** y recorré decisiones: preguntas de la ventana, aciertos, autonomía, pertenencias, reglas, centroide y ajuste.
5. Contrastá una decisión con lo que efectivamente hiciste. Cerrá el panel y continuá.
6. Terminá y comprobá el dominio y la progresión. Si conseguís dos completas altas consecutivas del mismo nivel, revisá el siguiente desbloqueo.

**Esperado:** ayuda entre una y tres escuchas totales, cambios de hasta una escucha y sin retirar oportunidades de una pregunta ya iniciada. El cálculo comienza con evidencia suficiente, desde la tercera respuesta, y respeta su separación entre cambios. El inspector explica el ajuste aplicado o por qué se mantuvo.

Los resultados reales pueden no producir la subida/bajada que buscabas. Usá las simulaciones del inspector para comprobar esos patrones sin responder al azar hasta obtenerlos.

### I3. Salida durante audio

1. Iniciá otra partida y salí al menú mientras suena la presentación.
2. Volvé a entrar, empezá de nuevo y mandá la app a segundo plano durante una reescucha.
3. Revisá audio, resumen e historial al volver.

**Esperado:** se detiene la salida, la partida abandonada queda interrumpida y no continúa reproduciendo preguntas detrás del menú.

## 6. Rhythm Tap

Ruta: **Juegos → Rhythm Tap → Jugar**.

### R0. Calibración y comparación antes de jugar

1. Conservá altavoz y volumen. En **Antes de tocar: calibración**, elegí **Calibrar antes de jugar → Iniciar calibración**.
2. Escuchá cuatro pulsos. Desde el quinto, tocá una vez por pulso durante doce pulsos, siguiendo el sonido. Los taps de calibración no suenan.
3. Revisá ajuste propuesto, cantidad de pulsos y dispersión. Elegí **Usar ajuste y volver** si seguiste el pulso con comodidad; si faltaron golpes o fueron irregulares, repetí.
4. Comprobá el ajuste indicado antes de jugar y en el resumen/historial de la siguiente partida. **Quitar ajuste** vuelve a 0 ms para partidas futuras; no modifica las anteriores.
5. Cancelá otra calibración y comprobá que cesa el audio, se conserva la partitura y no aparece un intento puntuado de calibración.
6. Compará negras y cuatro semicorcheas a 60, 90 y 120 BPM. Anotá ajuste, salida, cantidad de contactos y clasificación. Distinguí un contacto ausente de uno registrado fuera de ventana.
7. Compará sonido de taps activado/apagado. Registrá si persiste eco perceptible. Una compensación de evaluación no elimina el retraso audible.

**Esperado:** breve preparación silenciosa y conteo al tempo elegido. Ajuste fijo y explícito, sin aprender de partidas puntuadas ni dar progreso. Se conserva mientras siga abierta la app; repetir al reiniciarla o cambiar salida de audio. Combina sincronización personal y respuesta del equipo, sin medir por separado latencia física.

### R1. Generación, lectura y evaluación básica

1. Elegí **Práctica**, **4/4**, **60 BPM** y solo **Negras**. Pulsá **Generar ejercicio**.
2. Revisá cuatro compases, figuras completas y separación legible. La vista previa no reproduce el patrón que tenés que leer.
3. Pulsá **Play · Conteo y ejecución**. Esperá un compás de entrada y marcá los ataques en el área de taps.
4. Revisá lectura lateral, marcador, notas acertadas en verde y resultado final.
5. Repetí el mismo patrón intentando acertar. Después repetilo sin tocar nada y otra vez dando golpes de más y fuera de tiempo.
6. Contrastá aciertos, anticipos, atrasos, omisiones y extras con cada intento. No esperes un número exacto de milisegundos basándote solo en tu sensación de sincronía.
7. Probá las cuatro combinaciones de **Metrónomo después del conteo** y **Sonido de mis taps reales**: ambos activos, ambos apagados y cada uno por separado.
8. Generá ejercicios en **2/4** y **3/4**. Comprobá cuatro compases y conteo coherente con la métrica.

**Esperado:** el área responde a cada toque; la partitura se puede seguir hasta el final; el conteo no se confunde con la ejecución. No tocar produce omisiones donde había ataques. El sonido de taps corresponde a tus toques y se controla independientemente del metrónomo.

### R2. Recorrer las 21 células

En **Práctica, 4/4 y 60 BPM**, seleccioná una categoría por vez, generá y ejecutá. Las notas de conexión pueden agregar negras/corcheas aunque hayas elegido otra categoría; leé la partitura completa. Marcá cada fila después de revisar representación y respuesta.

| OK | Código | Célula | Punto a comprobar |
| --- | --- | --- | --- |
| [ ] | R20 | Negras | Un ataque por negra |
| [ ] | R21 | Dos corcheas | Dos ataques por pulso |
| [ ] | R01 | Blancas | Un ataque; no repetir durante su duración |
| [ ] | R02 | Redondas | Un ataque al inicio; cabe en 4/4 |
| [ ] | R09 | Negra con puntillo + corchea | Proporción larga/corta y puntillo visible |
| [ ] | R10 | Silencio de corchea + corchea | Primer medio pulso sin toque |
| [ ] | R11 | Corchea + silencio de corchea | Segundo medio pulso sin toque |
| [ ] | R12 | Silencio de negra | Silencio reconocido sin toque |
| [ ] | R18 | Silencio de blanca | Dos pulsos de silencio |
| [ ] | R19 | Silencio de compás | Compás completo sin toque |
| [ ] | R03 | Cuatro semicorcheas | Cuatro ataques por pulso |
| [ ] | R04 | Corchea + dos semicorcheas | Ataques largo/corto/corto |
| [ ] | R05 | Dos semicorcheas + corchea | Ataques corto/corto/largo |
| [ ] | R06 | Semicorchea + corchea + semicorchea | Ataques corto/largo/corto |
| [ ] | R07 | Corchea con puntillo + semicorchea | Puntillo y ataque final |
| [ ] | R08 | Semicorchea + corchea con puntillo | Ataque inicial corto |
| [ ] | R13 | Silencio de semicorchea + tres semicorcheas | Primera subdivisión sin toque |
| [ ] | R14 | Tres semicorcheas + silencio de semicorchea | Última subdivisión sin toque |
| [ ] | R15 | Tresillo de corcheas | Tres ataques iguales y cifra 3 legible |
| [ ] | R16 | Corchea + negra + corchea | Negra que comienza entre pulsos |
| [ ] | R17 | Dos negras ligadas | Un único ataque; la continuación no se vuelve a tocar |

1. En una célula con silencio, repetí y tocá deliberadamente dentro del silencio: revisá su extra y el silencio no respetado.
2. En dos negras ligadas, repetí tocando también la continuación: ese reataque no debe contarse como un ataque esperado adicional.
3. En 4/4 seleccioná **Tresillo de corcheas + Corchea/negra/corchea + Dos negras ligadas + Cuatro semicorcheas** y generá.
4. Conservá esa selección y cambiá a 2/4: la combinación no cabe con las cuotas actuales y debe explicar el rechazo sin quitar categorías silenciosamente.
5. Probá redondas en 2/4: debe rechazar la selección sin deformar la figura ni cruzarla silenciosamente de compás.

**Esperado:** notación musical correcta, figuras sin recortes y mensajes comprensibles cuando la selección no cabe.

### R3. Progresión, repetición e historial

1. Abrí **Progreso y DDA** y anotá categorías disponibles, tempo recomendado y rachas.
2. Elegí **Progresión** y una categoría disponible. En un perfil nuevo empiezan negras y dos corcheas a 60 BPM.
3. Generá un ejercicio nuevo y completalo. Inspeccioná su decisión y el motivo de elegibilidad o exclusión.
4. Si conseguís dos resultados altos elegibles para la categoría, comprobá el aumento de cinco BPM para un ejercicio futuro y los accesos que correspondan.
5. Usá **Repetir mismo patrón**: debe conservar la música y quedar como práctica, sin acreditar de nuevo el contenido repetido.
6. Abrí **Historial de ritmo**, filtrá por categoría, compás, tempo y modo. Abrí la partitura y resultado de un intento.
7. Repetí desde el historial. Comprobá que no cambia el registro anterior y que vuelve como práctica.
8. Salí durante un conteo y durante una ejecución. Revisá ambos intentos interrumpidos.

**Esperado:** tempo fijo durante el ejercicio; progreso confirmado junto con el guardado; repetición sin doble crédito. La música guardada coincide con la que se tocó. No es necesario alcanzar el techo de 120 BPM para probar este bloque.

## 7. Defender: notas y acordes

Ruta: **Juegos → Defender → Jugar**. La respuesta actual se construye con botones y se confirma con **Tocar**; el teclado de piano de Defender sigue pendiente.

### D1. Lectura de notas y reglas de partida

1. Elegí **Práctica → Notas → N1 · Sol dentro**, un enemigo y viaje de 14 segundos.
2. Iniciá y reconocé una nota. Seleccioná nombre y alteración y pulsá **Tocar**.
3. Probá una respuesta incorrecta y corregila antes de que llegue. El error debe conservar la selección y no quitar vida.
4. Dejá llegar un objetivo sin resolver: debe quitar una vida. Tres llegadas terminan la partida.
5. Revisá puntos, pérdidas, partitura y solución. Cada acierto suma 100 puntos.
6. Repetí prácticas cortas en **N2 · Sol ampliada**, **N3 · Fa ampliada** y **N4 · Sol y fa**.
7. Activá alteraciones y revisá naturales, sostenidos, bemoles y líneas adicionales. Probá **Do–Si** y **C–B**.
8. Probá con cuatro enemigos y viaje más corto, hasta **3 s**. En práctica elegí **Intervalo de aparición → Manual** y ajustá entre **1 y 6 s** con pasos de 0,5 s. Comprobá controles, notación, resumen y conservación de configuración en el historial. El cupo es un máximo: puede haber menos objetivos si no hay espacio.
9. Volvé a **Automático**: conserva la curva de aparición de 4 a 2,5 s. En progresión no se usa el intervalo manual y el viaje empieza en 10 s; con buen desempeño puede bajar hasta 3 s, solo para enemigos nuevos. Los parámetros son experimentales.

**Esperado:** correspondencia entre notación y solución, sin premiar una grafía enarmónica distinta de la requerida. Error y llegada son eventos diferentes. Los objetivos no ocultan controles esenciales.

### D2. Acordes por nombre

1. Elegí **Práctica → Acordes por nombre**.
2. Para cada fila de la tabla, empezá en fundamental y sol. Respondé **raíz escrita + alteración + calidad**, sin octava. Comprobá sonido y solución.
3. Probá primera y segunda inversión en las tríadas; agregá tercera en las séptimas.
4. Hacé al menos una partida corta en fa y otra en mezcla. Revisá acordes con segundas cercanas y varias alteraciones cuando aparezcan.
5. Cambiá de séptima en tercera inversión a una tríada: la selección debe ajustarse a una inversión válida.

| OK | Calidad |
| --- | --- |
| [ ] | Mayor |
| [ ] | Menor |
| [ ] | Disminuida |
| [ ] | Aumentada |
| [ ] | Dominante 7 |
| [ ] | Mayor 7 |
| [ ] | Menor 7 |
| [ ] | Semidisminuida 7 |

**Esperado:** tres o cuatro voces legibles según calidad; la raíz de respuesta puede diferir del bajo. Solo el acierto reproduce el acorde exacto si el sonido musical está activo. La fila de calidades se puede desplazar. No hace falta recorrer las 1.502 realizaciones del catálogo para completar esta muestra manual; anotá qué combinaciones probaste realmente.

### D3. Pausa, adaptación y cierre del resultado

1. En una partida activa, pulsá **Pausa**, esperá unos segundos y revisá objetivos ocultos, tiempo congelado y audio detenido.
2. Abrí **Progreso y DDA** desde la pausa. Volvé: la partida debe requerir **Reanudar**.
3. Mandá la app al fondo y regresá: debe quedar pausada, sin consumir vidas durante la pausa.
4. Elegí **Progresión** y una etapa/objetivo disponible. Resolvé suficientes objetivos para obtener decisiones y revisalas en el inspector.
5. Comprobá que cualquier cambio de tiempo de viaje afecta objetivos futuros; los visibles conservan su recorrido y plazo.
6. Terminá por tres llegadas. Luego abrí historial, prepará otra partida o volvé al menú para cerrar el resumen y confirmar su registro.
7. Reabrí **Historial de Defender**. Revisá cierre, notas/acordes, claves, inversiones, pérdidas y decisiones del intento.

**Esperado:** la práctica calcula propuestas sin aplicar adaptación. En progresión, los cambios de viaje respetan 3–14 segundos y pasos máximos de un segundo. El resumen inicial puede ser provisional: no concede progreso hasta cerrar su superficie mediante navegación. No debe haber doble resultado al confirmarlo.

Notas y acordes tienen progreso separado. Una partida corta o con evidencia insuficiente no garantiza desbloqueo aunque tenga buenos aciertos. Para probar cadenas largas, usá las simulaciones del panel.

## 8. Prueba técnica de voz

Ruta: **Más → Prueba de voz**; la pantalla se titula **Afiná tu voz**.

### V1. Permiso, silencio y captura

1. Si el micrófono ya está autorizado y querés probar la denegación, revocá solo ese permiso desde la información de Neuma en Android; no borres datos.
2. Tocá **Iniciar micrófono** y denegá el permiso. Revisá mensaje, ausencia de captura y acceso a ajustes.
3. Autorizá el permiso. Si hace falta, usá **Abrir ajustes de permisos**. Volvé y pulsá de nuevo **Iniciar micrófono**: autorizar no debe empezar a escuchar por sí solo.
4. Elegí un objetivo cómodo y escuchá su referencia. Luego iniciá captura explícitamente.
5. Hacé unos segundos de silencio; cantá una sola nota sostenida; interrumpí y volvé a cantar.
6. Cantá algo más grave y más agudo respecto del objetivo. Revisá nota, frecuencia, signo de cents y huecos en la gráfica.
7. Pulsá **Detener y guardar mediciones** y buscá la captura en **Más → Resultados**.

**Esperado:** ausencia de lectura válida cuando no se admite una señal; una lectura vieja no sigue presentándose como actual durante el silencio. La gráfica conserva huecos y su límite visual no recorta el valor numérico del error. Se guardan mediciones derivadas, no una grabación de voz.

En un ambiente con ruido puede aparecer una frecuencia válida ajena a tu voz: registrá lo ocurrido y las condiciones. Cantar una nota supuestamente afinada no alcanza para medir exactitud; no uses tu propia voz como patrón conocido.

### V2. Referencia y liberación del micrófono

1. Con captura activa, pulsá escuchar objetivo: debe detener la captura para reproducir la referencia. Después iniciá el micrófono explícitamente.
2. Probá **Detener**, salir al menú, Inicio de Android y bloqueo de pantalla en capturas separadas.
3. Al volver, revisá estado de captura y el indicador de uso de micrófono del sistema.
4. Abrí instrumentos o Simón: no debe quedar una captura anterior escuchando detrás del juego.

**Esperado:** salida y captura coordinadas; la prueba técnica de voz no reanuda sola después de referencia, salida o segundo plano.

## 9. Intervalos vocales

Ruta: **Juegos → Intervalos vocales → Jugar**. Usá **Práctica** para recorrer los tres tipos de ayuda sin depender de desbloqueos. Elegí registros cómodos: el rango técnico de análisis es 55–1.000 Hz.

### IV1. Imitación: dos notas escuchadas y dos capturas

1. Elegí **Práctica → Ayuda auditiva: Imitación → Unísono** y una referencia cómoda.
2. Pulsá **Escuchar intervalo**. Comprobá dos notas y micrófono apagado durante la reproducción.
3. Pulsá **Cantar primera nota** y sostenela sin vibrato hasta que se confirme.
4. Quedate en silencio: la segunda etapa no debe empezar sola. Pulsá **Cantar segunda nota** y sostené la misma altura.
5. Revisá error relativo, resultado y guardado. Abrí **Historial vocal** y el detalle.
6. Repetí con una quinta u octava ascendente y después descendente. Intentá también una segunda nota claramente incorrecta.
7. Probá una octava equivocada: no debe convertirse en un acierto por ignorar la octava.

**Esperado:** dos capturas separadas, evaluación relativa a tu primera nota cantada y registro del modo de ayuda. La desviación absoluta de tu primera nota respecto de la referencia no debe confundirse con el error del intervalo que realmente cantaste.

### IV2. Ancla dada: solo se canta la respuesta

1. Elegí **Práctica → Ancla dada** y un intervalo cómodo.
2. Pulsá **Escuchar ancla**: debe sonar solo la primera nota, sin revelar la respuesta.
3. Pulsá **Cantar nota objetivo** y cantá directamente la segunda nota.
4. Revisá que no te pida una primera captura cantada y que el historial identifique un ancla proporcionada.
5. Cambiá intervalo o registro y comprobá que solicita escuchar la referencia nueva.

**Esperado:** una sola captura de objetivo y evaluación desde el ancla reproducida. No se inventan mediciones de una primera nota que no cantaste.

### IV3. Producción libre: ancla elegida por vos

1. Elegí **Práctica → Producción libre → Unísono**.
2. Comprobá que no ofrece escucha ni selector de registro de referencia.
3. Pulsá **Cantar primera nota**, elegí una altura cómoda y sostenela.
4. Pulsá **Cantar segunda nota** y repetí esa altura. Repetí luego con otro intervalo y un ancla diferente.
5. Consultá historial y detalle: debe indicar ancla cantada y ninguna escucha de referencia.

**Esperado:** las dos capturas usan tu ancla real, sin emitir notas de referencia ni asignarle una nota MIDI ficticia en los datos de referencia.

### IV4. Serie de progresión, inspección y accesos

1. Elegí **Progresión → Imitación** y un contenido disponible. En un perfil nuevo están unísono y octavas ascendente/descendente.
2. Pulsá **Iniciar serie** y completá las cinco rondas con **Siguiente ronda**. Revisá número de ronda y guardado.
3. En **Progreso y DDA → Perfil**, inspeccioná decisiones del mismo intervalo y dirección: evidencia, reglas y tolerancia antes/después.
4. Comprobá que la tolerancia cambia para la siguiente ronda, sin modificar el criterio de una respuesta en curso.
5. Revisá **Ancla dada** y **Producción libre** en Progresión. Si faltan hitos, deben explicar el bloqueo; la práctica debe seguir disponible.
6. Si conseguís dos series altas del mismo contexto, comprobá el hito y acceso correspondiente. Si no, dejalo pendiente y verificá la cadena con simulación.

**Esperado:** series de cinco; tolerancias experimentales entre 30 y 80 cents, cambios de hasta cinco. Cada ayuda tiene tolerancia, racha y evidencia independientes. Ancla dada exige el hito de imitación de ese intervalo/dirección; producción libre exige el de ancla dada. Estar dentro de tolerancia no garantiza que el motor clasifique la ronda o la serie como alta.

La selección global automática de cuánta ayuda dar sigue pendiente: actualmente elegís explícitamente Imitación, Ancla dada o Producción libre. No lo registres como fallo de esta versión.

### IV5. Silencio, cancelación y tiempo máximo

1. Iniciá una ronda y cancelá durante la primera captura.
2. En otra, confirmá la primera nota y enviá la app al fondo antes de la segunda.
3. En otra, empezá captura y mantené silencio hasta que venza el límite global de aproximadamente 60 segundos. No fuerces la voz para sostener una nota tanto tiempo.
4. Volvé al historial y revisá los cierres.

**Esperado:** interrupción o timeout sin acierto inventado, micrófono liberado y sin reanudación automática. El límite incluye la espera entre las capturas. Ninguna serie incompleta debe acreditarse como una serie alta completa.

## 10. Afinador: configuración, Libre, Guiado y Oído

**Reprueba de cuerdas agudas en 0.12.23:** revisá primero segunda (Si/B) y primera
(Mi/E), pulsando una sola vez y dejando decaer, en los tres modos. La confirmación
requiere 400 ms válidos dentro del margen, no un segundo. Si todavía no confirma,
volvé a pulsar: el micrófono debe seguir activo. En Libre, al decaer se distingue
la última confirmación de una lectura vigente; no cuenta como estabilidad nueva.
En Oído, una comprobación ya afinada puede avanzar; una desafinada conserva su
error inicial y pide corrección. Registrar si la nota aparece, si se confirma,
si avanza el recorrido y si figura micrófono activo, como observaciones separadas.

Ruta: **Afinador** desde el menú. Para revisar afinación física usá una guitarra de seis cuerdas y tocá una cuerda por vez, apagando las demás. Si no tenés guitarra, podés probar configuración, referencias, permisos e interrupciones; dejá el recorrido instrumental **BLOQUEADO por falta de instrumento**, sin sustituirlo por una validación con voz.

### A1. Guitarras y afinaciones

1. Abrí Libre y localizá la configuración de guitarra y afinación. En un perfil nuevo debe existir **Guitarra**, con afinación estándar.
2. Creá una guitarra de prueba con nombre reconocible.
3. Recorré las seis afinaciones predefinidas. Revisá orden sexta → primera, nota y octava; en Drop D la sexta es Re2 y en Drop C es Do2.
4. Creá una afinación personalizada con seis notas y octavas entre Do2 y Mi4. Guardala, elegila y reabrí la aplicación.
5. Probá guardar una nota sin octava, una lista que no tenga seis alturas y una nota fuera del rango. Corregí los datos después de comprobar el rechazo.
6. Editá la afinación personalizada y comprobá que cambian sus objetivos, sin activar el micrófono.

**Esperado:** nombres y preferencias persistidos; mensajes para entradas inválidas, sin cambios de octava o sustituciones silenciosas. La configuración no inicia captura por sí sola.

### A2. Libre: seis cuerdas, precisión y referencias

1. Elegí estándar, **Normal** y **Cuerdas**. Iniciá el micrófono explícitamente después de conceder permiso.
2. Tocá cada cuerda: **6 Mi2, 5 La2, 4 Re3, 3 Sol3, 2 Si3 y 1 Mi4**. Anotá nota medida, objetivo, estabilidad y posibles saltos de octava.
3. Fijá una cuerda y tocá otra: el objetivo fijado debe mantenerse. Pulsá **Liberar cuerda** y revisá el seguimiento automático.
4. Cambiá a **Cromático** y tocá una altura ajena a la afinación: debe compararse con la nota temperada próxima.
5. Probá **Normal ±5 cents** y **Pro ±2 cents**. Comprobá que no declara afinación por una lectura fugaz y que silencio o cambio de objetivo reinician estabilidad.
6. Compará referencias **Guitarra, Piano y Tono puro** con una cuerda fijada, primero sin micrófono.
7. Con micrófono activo, pulsá **Escuchar**: debe suspender captura, limpiar lectura y reproducir. Al terminar puede reabrirla automáticamente solo si estaba activa antes, con evidencia nueva.
8. Durante reproducción/espera, detené, salí o mandá la app al fondo: no debe volver a escuchar sola. Este comportamiento difiere de una referencia completada normalmente.
9. Comprobá las referencias de los presets, especialmente Do2 de Drop C. Reabrí y revisá que recuerda el timbre elegido.

**Esperado:** no toma su propia referencia como ejecución tuya. Una nota muy distante de la cuerda fijada no se pliega de octava para aparentar afinación. Libre no guarda desempeño en el historial del afinador.

Sin un patrón de frecuencia de exactitud conocida, registrá estabilidad y comportamiento, pero no concluyas que el S22 Ultra cumple físicamente ±2 cents. Si usás una fuente externa conocida, anotá fuente, frecuencia y método; la comparación con otro afinador es orientativa salvo que conozcas su exactitud.

### A3. Guiado: primera pasada y revisión

1. Elegí **Guiado**, estándar, Normal, **Guardar seguimiento** activo y revisión final activa.
2. Iniciá el recorrido y seguí sexta → primera. Tocá una cuerda por vez hasta que se confirme; la permanencia requerida es de 400 ms continuos dentro del margen.
3. Al cambiar de cuerda, comprobá que la lectura anterior no aprueba inmediatamente la siguiente.
4. Completá la primera pasada y entrá en revisión. Si una cuerda necesita corrección, probá **Dejar pendiente y continuar** y volvé después.
5. Completá sin pendientes. Revisá resumen e historial.
6. Repetí con revisión final apagada: terminar la primera pasada no debe declarar que hizo la comprobación final del conjunto.
7. Empezá otro recorrido y detenelo o salí antes de terminar.

**Esperado:** objetivos y etapas correctos, pendientes recuperables durante la revisión y recorridos incompletos identificados como interrumpidos. No se retoma automáticamente al volver del fondo.

### A4. Entrenamiento de oído: medición oculta y comprobación

1. Abrí **Entrenamiento de oído**. Elegí guitarra, afinación, Normal y seguimiento activo.
2. Iniciá. Escuchá la referencia y ajustá la cuerda: durante esta etapa no deben mostrarse frecuencia, dirección ni indicador de acierto, tampoco mediante TalkBack.
3. Pulsá **Comprobar** y tocá nuevamente la cuerda. Debe usar una captura nueva.
4. Probá primero silencio o sonido inestable: no debería consumir un intento válido.
5. Hacé un intento válido deliberadamente desafinado. Revisá su resultado; corregí luego con ayuda hasta avanzar.
6. Comprobá que el error inicial sigue siendo error en el resumen aunque lo hayas corregido.
7. Completá las seis cuerdas y la revisión final. Revisá que la revisión no suma intentos iniciales de oído.
8. En otro recorrido activá la clasificación previa **Grave / Agudo / Afinado**. Clasificá antes de ajustar; revisá la devolución solo después de comprobar.

**Esperado:** intento inicial, clasificación opcional, corrección y revisión permanecen separados. Pulsar Comprobar de nuevo tras revelar el intento no genera intentos iniciales duplicados. La advertencia excepcional de distancia extrema puede aparecer sin revelar una medición continua.

### A5. Seguimiento y filtros del afinador

1. Abrí **Historial del afinador** y revisá los recorridos de Guiado/Oído de A3 y A4.
2. Consultá **Oído**, filtros por guitarra, afinación/revisión, criterio y cuerda, y **Ver detalle**.
3. Consultá **Instrumento**, seleccioná condiciones comparables y una etapa. Revisá que distingue ajuste, intento inicial, corrección y revisión.
4. Editá tu afinación de prueba: los registros anteriores deben conservar las alturas y revisión con que se iniciaron.
5. Apagá **Guardar seguimiento**, hacé un recorrido corto y comprobá que hay resumen actual pero no una sesión nueva persistida. Volvé a activar el seguimiento para las pruebas restantes.
6. Cerrá y reabrí: preferencias e historial confirmado permanecen y el micrófono está detenido.

**Esperado:** métricas solo sobre intentos válidos; sin porcentajes inventados cuando faltan datos. Las diferencias entre etapas no se presentan como diagnóstico mecánico de la guitarra.

## 11. Resultados generales y persistencia

### H1. Historial general y detalles

1. Entrá en **Más → Resultados**.
2. Filtrá por juego, nivel y origen. Abrí un resultado de Simón, uno de instrumentos y una captura de prueba de voz.
3. Contrastá secuencia/error, respuestas/reescuchas y mediciones de voz con las pruebas anteriores.
4. Aplicá filtros sin coincidencias, revisá el estado vacío y quitalos.
5. Si hay más de veinte registros, recorré la paginación. Si no, dejá esa parte pendiente sin fabricar partidas solo para llenarla.
6. Consultá también los historiales propios de ritmo, Defender, intervalos y afinador: no supongas que todos están incluidos en el historial general.

**Esperado:** consultar no cambia resultados ni progreso; un registro antiguo sin un dato nuevo lo identifica como ausente. Los filtros se pueden quitar y la pantalla vacía permite volver.

### H2. Reapertura con datos confirmados

1. Esperá los avisos de guardado y anotá un registro reciente y el progreso de cada módulo.
2. Salí al menú, cerrá la app desde recientes y volvé a abrirla.
3. Comprobá resultados, niveles, guitarras, afinaciones y preferencias persistentes del afinador.
4. Verificá que no empieza audio ni micrófono automáticamente.

**Esperado:** los datos confirmados sobreviven. No se exige conservar la página del menú, su ajuste temporal de movimiento reducido ni una partida/captura activa después de morir el proceso. Los guardados pendientes en memoria no tienen esa garantía: resolvelos o exportalos antes.

## 12. Bancos, simulaciones y archivos

### B1. DDA de Simón e instrumentos

1. Anotá progreso real e identificadores de los últimos resultados antes de simular.
2. En **Datos y demostración**, ejecutá **Dos partidas altas en ambos juegos** y **Alta, baja y recuperación de racha**.
3. Revisá pasos, rachas y nivel final, con la marca de datos simulados.
4. Ejecutá **Ejecutar banco DDA completo** y revisá el resumen de comprobaciones. Anotá cualquier fila que indique FALLA.
5. En el inspector de instrumentos, abrí **Simulación** y ejecutá sus escenarios. Revisá decisiones de reducción, recuperación y mantenimiento de ayuda.
6. Regresá al perfil real y comparalo con el punto 1.

**Esperado:** simulación sin audio ni partidas o desbloqueos nuevos en el perfil. Puede existir un informe de simulación exportable; eso no debe aparecer como desempeño real.

### B2. Simulaciones de ritmo, Defender e intervalos

1. En **Rhythm Tap → Progreso y DDA → Simulación**, ejecutá cada escenario ofrecido. Inspeccioná al menos uno de avance, contenido repetido y fallo/reintento sintético de guardado.
2. En **Defender → Progreso y DDA**, recorré las simulaciones disponibles para notas y acordes. Ejecutá el catálogo completo si querés comprobar su ruta de objetivos sin jugarlos todos.
3. En **Intervalos vocales → Progreso y DDA → Simulación**, elegí cada ayuda y recorré los escenarios ofrecidos: precisión, desviación, octava, interrupción, práctica y ruta completa según disponibilidad.
4. Esperá a que termine cada cálculo antes de iniciar otro. Inspeccioná una decisión, volvé a Perfil y contrastá el progreso real.

**Esperado:** resultados explicables, sin permiso ni captura de micrófono y sin modificar el perfil. Los prerrequisitos incorporados para ejecutar una ruta sintética deben identificarse como sintéticos. Un fallo simulado de guardado no es un fallo real de almacenamiento del teléfono.

### B3. Laboratorio sintético de tono

1. En **Datos y demostración → Pruebas sintéticas de tono**, pulsá **Ejecutar 83 casos**.
2. Esperá el final y revisá cantidad de casos, ventanas, criterios obligatorios y casos exploratorios.
3. Exportá **informe Markdown** mediante el selector de Android y abrí el archivo en el teléfono o la computadora.
4. Iniciá otra ejecución e intentá cancelarla antes de que termine. Si completa demasiado rápido para hacerlo, registrá esa limitación.

**Esperado:** no reproduce sonido ni necesita micrófono; el informe identifica señales sintéticas. Un caso exploratorio con error no es automáticamente un criterio obligatorio fallido. Estos datos caracterizan el estimador y no miden exactitud de tu micrófono ni voz humana.

### E1. Exportación JSON y cancelación

1. Entrá en **Datos y demostración → Exportar JSON**.
2. Elegí una ubicación local accesible, como Descargas, y un nombre con fecha y versión.
3. Volvé a la app y comprobá su mensaje de última operación.
4. Abrí el archivo en el teléfono o copialo a la computadora. Comprobá que es legible y contiene ejemplos de los módulos que probaste: Simón, instrumentos, prueba de voz, configuración/seguimiento del afinador, ritmo, Defender e intervalos.
5. Revisá que resultados reales, simulaciones y pruebas sintéticas están diferenciados. En voz/intervalos debe haber mediciones y metadatos, sin grabaciones PCM.
6. Compará al menos un resultado y una decisión con su pantalla de historial.
7. Volvé a exportar y cancelá el selector. Revisá que conserva los datos y permite seguir usando la app.

**Esperado:** archivo realmente guardado, mensaje coherente, datos presentes y cancelación sin borrar ni duplicar resultados. Si no podés abrirlo o interpretarlo en el celular, conservá el archivo y dejá su revisión de contenido pendiente para la computadora.

### E2. ZIP de originales y comportamiento ante errores

1. Elegí **Exportar originales para recuperación**, guardá el ZIP y revisá que se puede abrir.
2. Comprobá que incluye los archivos existentes de los módulos usados y su documentación; no todos los archivos tienen que existir en un perfil que no usó todas las funciones.
3. Si aparece un error real de lectura/guardado durante cualquier bloque, anotá mensaje y caso. Usá el reintento disponible y conservá JSON/ZIP antes de cerrar el proceso si hay pendientes.

**Esperado:** ZIP de originales separado del JSON de resultados. Exportar no repara ni importa datos. No se implementa importación para restaurar tu perfil desde ese archivo.

No corrompas archivos ni llenes el almacenamiento para forzar estos errores antes de la reunión. Su inyección controlada corresponde a las pruebas técnicas; acá se registra la recuperación si el problema aparece de verdad.

## 13. Comprobaciones transversales

### T1. Segundo plano, pantalla bloqueada y cambio entre módulos

En cada fila, iniciá actividad, salí a Inicio de Android, esperá unos segundos y volvé. Repetí con bloqueo de pantalla. Hacelo después de resolver cualquier guardado pendiente.

| Módulo | Qué debe ocurrir |
| --- | --- |
| Simón | Partida interrumpida al abandonar; audio detenido |
| Instrumentos | Partida interrumpida; sin reescuchas en segundo plano |
| Rhythm Tap | Ejercicio interrumpido; audio y ejecución detenidos |
| Defender | Pausa; volver exige Reanudar y no consume vidas mientras está pausado |
| Prueba de voz | Captura detenida; no reinicia sola |
| Intervalos vocales | Ronda interrumpida; no reinicia sola ni acredita una completa |
| Afinador Libre | Micrófono detenido; requiere inicio explícito |
| Afinador Guiado/Oído | Recorrido interrumpido; no continúa solo |

Después alterná **voz → instrumentos → afinador → ritmo → intervalos**. Comprobá que no se mezclan referencias, capturas o sonidos de pantallas anteriores.

### T2. Pantalla y accesibilidad

1. Con tus ajustes habituales, revisá menú, preparación, ejecución, resultado e historial de cada actividad. Buscá controles tapados por la navegación de Samsung, texto cortado y acciones fuera de alcance.
2. Aumentá tamaño de fuente y zoom desde los ajustes del teléfono. Anotá el valor usado y repetí esas superficies, prestando atención a piano, taps, respuestas de acordes y editores de afinación. Luego restaurá tus preferencias.
3. Activá **Más → Reducir movimiento**. Revisá menú, ritmo y Defender. En Defender esperá objetivos quietos con indicación temporal; la evaluación musical y el tiempo disponible no deben desaparecer por reducir movimiento.
4. Si usás la opción equivalente del sistema para reducir/quitar animaciones, probala por separado y restaurala al terminar.
5. Activá TalkBack si podés manejarlo. Recorré menú, una preparación, un resultado y controles principales de cada módulo. Comprobá nombre, orden de foco y acceso a volver/detener. En entrenamiento de oído no debería leer la medición que está oculta.
6. Desactivá TalkBack al terminar si no lo usás habitualmente. No mezcles una prueba de sincronía rítmica con una interacción táctil modificada por el lector.

**Esperado:** notación legible, controles alcanzables y estados comprensibles sin depender únicamente del color. Una descripción accesible incorrecta o un control inalcanzable se registra como problema aunque visualmente la pantalla se vea bien.

### T3. Sin conexión y distintas salidas de audio

1. Con la app instalada, desactivá Wi-Fi/datos o activá modo avión y asegurate de que Bluetooth esté apagado para esta pasada.
2. Reabrí y hacé un recorrido corto en cada juego, afinador y prueba de voz. Abrí historiales, simulaciones y notación de ritmo.
3. Exportá a almacenamiento local. No uses un destino en la nube para juzgar funcionamiento offline.
4. Si vas a usar auriculares en la reunión, repetí la demo con esa salida. Registrá USB-C o Bluetooth y cualquier demora o cambio de volumen.

**Esperado:** las funciones locales y recursos musicales funcionan sin conexión. Una diferencia perceptible con Bluetooth se registra con esa ruta de audio; el puntaje rítmico por sí solo no permite calcular latencia acústica.

## 14. Reinicio del perfil — opcional, al final

Este bloque **borra historiales, mediciones, progreso, guitarras, afinaciones personalizadas y pendientes**. Los archivos exportados se conservan, pero la app no tiene importación para reconstruir automáticamente el perfil. Si querés conservarlo para la reunión, hacé solo Q1 y dejá Q2 sin ejecutar.

### Q1. Cancelar el reinicio

1. Anotá un resultado, un nivel y una guitarra personalizada existentes.
2. En **Datos y demostración → Reiniciar datos**, leé el diálogo y elegí **Cancelar**.
3. Revisá los tres ejemplos y reabrí la app.

**Esperado:** no cambió ningún dato.

### Q2. Confirmar el reinicio, solo si querés borrar tu perfil

1. Exportá JSON y ZIP y comprobá que podés abrir ambos fuera de la app.
2. Elegí **Reiniciar datos → Borrar y reiniciar** y esperá la confirmación.
3. Revisá historiales vacíos, progresos iniciales y guitarra/afinación predeterminadas; no debe quedar micrófono ni audio activos.
4. Cerrá y reabrí. Comprobá que no reaparecen resultados anteriores y que siguen disponibles los archivos exportados.

**Esperado:** borrado persistente de todos los módulos, no solo de Simón/instrumentos. Si algún mensaje menciona únicamente los juegos originales, anotá la inconsistencia del texto y verificá igualmente el efecto real en los demás.

## 15. Elegir y ensayar la demo para el director

Después de probar, elegí únicamente recorridos que hayan salido bien en esta versión y este teléfono.

- [ ] La versión instalada es la que figura en el registro.
- [ ] Puedo recorrer menú → instrumentos → resultado → inspector DDA.
- [ ] Puedo mostrar un escenario reproducible de adaptación y señalar si es simulado.
- [ ] La captura de voz o el ejercicio vocal elegido funciona con mi registro y el ambiente previsto.
- [ ] Puedo mostrar un resultado guardado y volver al menú sin errores.
- [ ] Tengo un JSON exportado que abre correctamente.
- [ ] Tengo un video corto de respaldo del recorrido funcionando.
- [ ] Hice dos ensayos completos sin cambiar versión ni configuración entre ellos.
- [ ] Separé funciones verificadas, problemas conocidos y trabajo pendiente.
- [ ] Dejé cargado el teléfono y probé la salida de audio que usaré en la reunión.

No hace falta borrar tu perfil para que la demo parezca un primer uso. Podés explicar el progreso existente y usar las simulaciones identificadas para mostrar estados que no quieras alcanzar jugando durante la reunión.

## 16. Plantillas de registro

### Resultado por caso

Copiá una fila por cada ID de la guía; no marques bloques completos si quedaron casos sin ejecutar. Adjuntá las matrices de células y acordes con las combinaciones realmente recorridas.

| Fecha | Versión | Caso | Resultado: OK/FALLA/BLOQUEADO/PENDIENTE | Condición o evidencia | Observaciones |
| --- | --- | --- | --- | --- | --- |
| | | P1 | | | |
| | | P2 | | | |
| | | N1 | | | |
| | | S1 | | | |
| | | S2 | | | |
| | | S3 | | | |
| | | I1 | | | |
| | | I2 | | | |
| | | I3 | | | |
| | | R1 | | | |
| | | R2 | | | |
| | | R3 | | | |
| | | D1 | | | |
| | | D2 | | | |
| | | D3 | | | |
| | | V1 | | | |
| | | V2 | | | |
| | | IV1 | | | |
| | | IV2 | | | |
| | | IV3 | | | |
| | | IV4 | | | |
| | | IV5 | | | |
| | | A1 | | | |
| | | A2 | | | |
| | | A3 | | | |
| | | A4 | | | |
| | | A5 | | | |
| | | H1 | | | |
| | | H2 | | | |
| | | B1 | | | |
| | | B2 | | | |
| | | B3 | | | |
| | | E1 | | | |
| | | E2 | | | |
| | | T1 | | | |
| | | T2 | | | |
| | | T3 | | | |
| | | Q1 | | | |
| | | Q2 | | | |

### Reporte de un problema

```text
ID del problema:
Caso de la guía:
Versión de Neuma / Android / One UI:
Pantalla y modo (Práctica/Progresión, ayuda auditiva, etc.):
Estado previo del perfil relevante:
Ruta de audio, instrumento y entorno, si aplica:
Pasos exactos:
1.
2.
3.
Esperaba:
Ocurrió:
¿Pasa siempre o cuántas veces sobre cuántos intentos?:
¿Se puede seguir usando la app?:
¿Se perdió o duplicó información?:
Captura/video/archivo exportado:
Prioridad para la reunión: bloquea demo / se puede evitar / ajuste menor:
```

### Cierre de cada sesión de prueba

```text
Fecha / versión:
Casos terminados:
Casos pendientes o bloqueados y motivo:
Problemas que bloquean la demo:
Problemas que puedo evitar con un recorrido conocido:
Recorridos que repetiría frente al director:
Archivos de evidencia conservados:
Próximo caso a ejecutar:
```

## Referencias del recorrido

Guía contrastada con el menú y los módulos Android actuales; las descripciones antiguas se usan solo donde siguen vigentes. Para resolver diferencias, tomá como referencia esta versión y registrá el texto observado.

- [Estado del prototipo](../.agents/estado-prototipo.md) y [ejecución/alcance Android](../Plataforma/README.md).
- [Demostración base: progresión, voz y datos](Demostracion-prototipo-v0.5.md), [historiales y bancos](Demostracion-prototipo-v0.10.md).
- [Ritmo: catálogo R4](Rhythm-Tap-R4.md), [historial R5](Rhythm-Tap-R5.md), [progresión R6](Rhythm-Tap-R6.md) y [lectura lateral](Rhythm-Tap-lectura-lateral.md).
- [Defender D3](Demostracion-defender-D3.md) y [acordes/inversiones D5](Demostracion-defender-D5.md).
- [Intervalos: progresión V4](Intervalos-vocales-V4.md), [ancla dada V5](Intervalos-vocales-V5.md) y [producción libre V6](Intervalos-vocales-V6.md).
- [Afinador: configuración A2](Demostracion-afinador-A2.md), [guiado A3](Demostracion-afinador-A3.md), [referencias A4](Demostracion-afinador-A4.md), [oído A5](Demostracion-afinador-A5.md) y [seguimiento A6](Demostracion-afinador-A6.md).

Completar esta guía aporta observaciones funcionales en tu dispositivo. No acredita por sí solo eficacia pedagógica, exactitud física del estimador ni latencia acústica.

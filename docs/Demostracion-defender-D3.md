# Defender D3 — notas con DDA, progreso e historial

Implementación local del 2026-10-30, **0.12.13/código 28**, según [#25](https://github.com/SaladinoJC/Tesis/issues/25) y la [especificación aprobada del prototipo](Especificacion-defender-v1.md). Verificación Android pendiente: continúa el pedido del usuario de no abrir el emulador. No modifica el protocolo académico ni acredita eficacia pedagógica.

## Recorrido

1. Abrir Defender desde Juegos. **Práctica** ofrece N1–N4 y viaje manual de 6 a 14 s, en pasos de un segundo. **Progresión** ofrece las etapas desbloqueadas, inicialmente N1. Cada partida de progresión comienza con viaje de 10 s.
2. Elegir etapa, alteraciones opcionales, nombres Do–Si/C–B, cupo de uno a cuatro enemigos y audio. El cupo solo se llena si hay espacio; no se acumulan apariciones pendientes.
3. Jugar leyendo el pentagrama y construyendo nombre/alteración. **Tocar** evalúa por el timestamp de pulsación; un error conserva selección y no quita vida. Solo una llegada quita vida. La tercera llegada produce game over. Pausa congela tiempo/audio y oculta objetivos; segundo plano pausa, volver al menú interrumpe.
4. El DDA calcula decisiones al resolver enemigos. Cambia únicamente el viaje asignado a futuros enemigos; los visibles conservan viaje, trayectoria y deadline. La curva fija de aparición y su cupo no dependen del centroide.
5. El resumen se guarda primero como **provisional**, sin otorgar progreso. Preparar otra partida, abrir historial/inspector o volver al menú cierra la entrada anterior y confirma el resultado automáticamente. No se agrega un botón de confirmación.
6. **Progreso y DDA** permite consultar rachas y requisitos, elegir una resolución y ver su ventana, eventos, entradas, pertenencias, reglas, centroide, propuesta, acción y motivo. Abierto desde una partida activa, pausa; regresar requiere pulsar Reanudar. Desde el historial inspecciona el registro elegido, sin sustituirlo por la partida actual.
7. El historial filtra por etapa, origen, alteraciones y cierre. El detalle conserva puntos, resoluciones, tiempo activo/pausas, semilla/configuración, revisión, enemigos cancelados y pérdidas con su partitura/solución. Los datos completos se exportan desde Datos.

## DDA intrapartida

Se reutiliza el motor Mamdani con la configuración experimental `defender-travel-v1`: ventana de cinco resueltos, tres nuevas resoluciones entre evaluaciones aplicables, viaje 6–14 s y cambio máximo de un segundo. Una evaluación válida que propone mantener o alcanza el límite también consume el enfriamiento. Una inferencia sin área no produce cambio.

Supervivencia = eliminados/resueltos. Precisión = aciertos/(aciertos+fallidos), con cero si no hubo intentos. La ventana conserva índices de eventos inclusivo/exclusivo para reconstruir intentos globales, incluso con resoluciones simultáneas; no se corta solo por timestamps iguales. Rapidez = 1 − promedio de la fracción de viaje consumida por cada resolución: una llegada aporta una fracción de uno. Solidez = mínimo entre supervivencia y precisión. Los cancelados no son resueltos.

Las funciones trapezoidales, siete reglas, mínimo/máximo y centroide de 1001 puntos son los de la especificación. Centroide mayor que 0,25 propone alargar; menor que −0,25, acortar; los extremos exactos conservan. Práctica calcula propuestas pero no las aplica. Cada configuración se conserva con el resultado.

## Progresión por contenido

`defender-mastery-v1` usa supervivencia y precisión de la partida. Requiere game over legítimo, al menos diez resueltos y tres resueltos por clave objetivo. Alto desde centroide 0,75; medio desde 0,40. Dos completas elegibles altas consecutivas de la misma etapa habilitan la siguiente: N1→N2→N3→N4. Una elegible no alta reinicia la racha; práctica, interrupción o datos insuficientes la conservan. Los desbloqueos adquiridos no se retiran ni una etapa fácil acredita otra. Alteraciones no agrega un nivel.

N4 exige datos y dominio alto de sol y fa por separado, además del conjunto. Elección técnica conservadora del agente: cada clave usa su supervivencia y la precisión global como condición común. Los intentos fallidos no se atribuyen artificialmente a una nota o clave sin objetivo elegido. El progreso identifica su variante; los logros de notas no acreditan acordes o piano, que siguen fuera de D3.

Los valores y el grafo son configuración de prueba. Puntaje, centroide y la etiqueta «dominio» no constituyen una evaluación musical o pedagógica validada.

## Cierre, timestamps y guardado

Una pulsación entregada después de un tick puede corregir una llegada usando su timestamp original. Si era la tercera, la partida puede volver a estar activa. Por eso el guardado inicial del resumen lleva `sealed=false` y no genera racha ni desbloqueos. Al cerrar esa superficie por navegación se invalida el checkpoint de entrada y se confirma una nueva revisión del mismo intento. No se agrega una espera arbitraria ni un offset de latencia para decidir cuándo un toque es válido.

Si la tercera llegada se corrige antes del cierre y el jugador abandona luego, el único intento queda interrumpido y no se acredita como completo. Si el proceso muere con un resumen provisional, ese registro aparece como «Sin cierre confirmado» y no concede avance; no se completa automáticamente al reabrir. No se implementa recuperación de una partida activa tras muerte de proceso.

Resultado, configuración, eventos, decisiones y snapshots de progreso se confirman en una sola sustitución atómica de **`defender-results.json`**, bajo la barrera compartida del perfil. UUID/revisión y secuencia de confirmación evitan duplicados y separan orden de guardado del reloj de pared. Una corrección posterior de un registro ya usado por otra partida conserva la decisión y su `evaluatedRevision`, como política técnica provisional de trazabilidad; una sesión cerrada ya no admite nuevas pulsaciones.

Lectura/guardado fallidos se muestran y conservan pendientes en memoria. Nuevas partidas de progresión esperan recuperarlos; la práctica continúa disponible. Los pendientes pueden exportarse para recuperación, pero morir el proceso antes de guardar/exportar puede perderlos.

El perfil tiene ahora **ocho archivos conocidos**, incluyendo `defender-results.json`. Exportación agrega resultados/avance/pendientes de Defender; el ZIP incluye originales y `pending-defender-saves.json` cuando corresponde. Reinicio elimina también Defender con la confirmación existente de Datos. Se conservan formatos y datos de Simón, instrumentos, voz, afinador y ritmo. No se guarda PCM.

## Simulaciones y verificación reproducible

**Verificación nueva de host:** 196 pruebas JVM aprobadas (18 agregadas), lint **0 errores/17 advertencias**, ensamblados de app y AndroidTest aprobados en **5 min 20 s**. Dos pruebas Android nuevas compiladas, sin ejecutar. Se cotejaron **340 archivos** de fuentes/configuración con la copia aislada y los **149 assets empaquetados** son idénticos a R6. Logs, fuentes y resultados completos: `Plataforma/build/verification/2026-10-30-D3/`; síntesis duradera y XML seleccionados: `docs/evidencias/defender-d3/`.

[APK candidato sin instalar](../Plataforma/build/apk-archives/neuma-v0.12.13-defender-d3-prueba-sin-emulador.apk): **15.092.664 bytes**, SHA-256 `d6bd7a9a700fb3b153ad13290f3db16ae207fc4126e37908b9d04b1f7cdff341`. Firma debug compatible con entregas previas; paquete, nombre Neuma y versión/código comprobados en el APK. La última entrega completa verificada en Android continúa siendo 0.12.10.

Ocho escenarios aislados en memoria, sin audio ni escrituras al perfil: rápidos hasta 6 s; lentos hasta 14 s; aciertos a mitad de viaje; llegadas sin intentos; dos altas de N1; ruta hasta N4/ambas claves; práctica manual; fallo/reintento. Las pérdidas utilizadas para cerrar una simulación pueden proponer alargar aunque sus aciertos anteriores propusieran mantener. No confundir ese cambio con un fallo del motor.

Desde `Plataforma/`, con Java/SDK como en README:

```powershell
.\gradlew.bat --offline --no-daemon --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

La suite comprueba la sesión pública, enfriamiento/límites, supervivencia/rapidez con pérdidas, intentos globales, ventanas por eventos, progreso por clave, pausa, cierre/tap tardío, fallos, concurrencia, idempotencia, lectura suspendida, JSON ilegible, exportación y recuperación junto a ritmo. El banco genera `app/build/verification/defender-d3/scenarios.json` desde sesiones públicas. No usar estos resultados como desempeño de estudiantes.

`DefenderD3AndroidTest` está preparado para dos partidas, desbloqueo al cerrar, reapertura/detalle y simulación aislada. Las pruebas anteriores adaptan la etiqueta de vidas al texto accesible. Se compilan sin ejecutarse. Antes de dar por cerrado Android: ejecutar casos nuevos y regresión D1/D2, comprobar 411/360/320 dp y 320 dp con texto 200 %, reducción de movimiento, cuatro objetivos/controles, pause/retorno, guardado/exportación nativa y JSON. La validación física y TalkBack por gestos/locución siguen pendientes.

## Identidad visual

Se adapta la pantalla espacial heredada a Music Garden: papel cálido, verdes de `GardenTokens`, Fraunces/Nunito Sans, árbol sin rostro, siluetas de polilla con alas vegetales y acción «Tocar». Árbol y polilla son gráficos propios simplificados de Canvas, con fuente editable y procedencia en el inventario; no se extraen de las referencias ni se agregan bitmaps o bibliotecas. La partitura sigue usando los datos estructurados y el trazado musical existente.

La reducción de movimiento mantiene objetivos quietos en sus entradas, muestra el tiempo a la próxima llegada y omite el anillo ornamental de acierto; no modifica tiempos, matching ni evaluación. La distribución vertical/radial y la reserva de espacio siguen la lógica del arcade. Los controles nuevos siguen el tamaño táctil objetivo de 48 dp y la notación conserva su escala musical.

Se reutilizan tema, selector `HistoryChoice`, estados de almacenamiento y presentación de pertenencias/reglas compartida con ritmo. La skill de diseño aporta una dirección orientativa para el panel; Music Garden y la interfaz Android existente prevalecen sobre la hoja de escritorio. La revisión de capturas y estados renderizados queda pendiente por pedido de no emular; no se usa una réplica HTML como validación de Compose.

Dirección orientativa de la skill: `framed-sheet`, semilla **1067899079**, con paleta y tipografía conservadas. La estructura de escritorio no se traslada al juego: el panel usa la columna nativa y componentes existentes. Desviación consciente respecto al arte final: árbol/polillas simplificados y vidas textuales, sin las tres hojas finales; no se presentan como ilustraciones aprobadas. La pregunta del inspector es «por qué cambia el viaje y qué falta para abrir la próxima etapa», respondida con eventos reales o escenarios rotulados como sintéticos.

D3 queda implementado localmente, con cierre Android pendiente. No se abrió emulador ni se instaló en dispositivos, no se modificaron issues ni hubo push. Personaje continúa pausado. Siguiente software: D4/#26, tríadas por nombre, fuera de esta sesión.

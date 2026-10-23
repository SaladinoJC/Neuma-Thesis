# Defender de notas — incremento D1 / #23 (WIP)

Estado al detener la sesión del 2026-10-23 por pedido del usuario: implementación en progreso, **sin entrega verificada**. El agente raíz todavía debe integrar la rama y comprobarla. No presentar #23 como completado ni generar APK definitivo con este checkpoint.

Primer recorrido de práctica: notas naturales Mi4–Fa5 en clave de sol, un enemigo radial, tres vidas y 100 puntos por eliminación. El recorrido no guarda resultados ni modifica desbloqueos. El DDA, las alteraciones y varios enemigos pertenecen a los siguientes tickets.

La composición toma como referencia `docs/imagenes/capturadefender.png`: campo espacial oscuro, HUD arriba, nave/base abajo y controles de respuesta inferiores. El dibujo de naves, estrellas, pentagrama y clave es vectorial propio; no se incorpora la captura como asset ni se descargan sprites o fuentes.

## Recorrido previsto

1. Abrir Defender y revisar preparación: práctica, sol dentro del pentagrama, viaje de 10 segundos y un enemigo.
2. Elegir nombres Do–Si o C–B. Los controles cambian etiquetas, no alturas ni respuestas esperadas.
3. Iniciar, leer la nota en su pentagrama, elegir nombre y pulsar Disparar. No se exige octava.
4. Acertar elimina inmediatamente y suma 100; una respuesta incorrecta conserva la selección y muestra aviso abajo. Solo la llegada a base cuesta vida.
5. Activar/desactivar sonido de nota y efectos por separado. El objetivo no suena antes de resolverlo.
6. Pausar o cambiar de aplicación: se ocultan objetivos y controles de respuesta, se detiene audio y el tiempo de juego queda congelado. Volver no reanuda por sí solo.
7. Dejar llegar tres enemigos para ver resumen y revisión de contenidos/soluciones; salir antes produce interrupción.

## Parámetros experimentales

`defender-d1-v1`: viaje fijo 10.000 ms. Primera aparición al comenzar; intervalo 4.000 ms, 3.500 desde 30 s, 3.000 desde 60 s y 2.500 desde 90 s de tiempo activo. Cupo uno en D1; se omiten apariciones ocupadas sin acumular ráfagas. El reloj monótono activo excluye pausas. Un disparo exactamente en el deadline pierde frente a la llegada.

El sonido de nota usa `referenceTone`: seno de 600 ms, amplitud 0,2 y fade de 20 ms, con altura/octava reales, temperamento igual y La4=440 Hz. Los efectos son senos de 100 ms y amplitud 0,1: acierto 1.800 Hz, fallo 180 Hz y llegada 90 Hz, con entrada/salida suavizadas. Todo se reproduce como PCM16 mono a 44.100 Hz; cada pista se libera al terminar o cancelar. Son elecciones técnicas provisionales del agente. No hay muestras nuevas de terceros ni micrófono. Las reglas temporales, audibilidad y comodidad táctil requieren comprobación en teléfono; una prueba de emulador no acredita latencia acústica ni eficacia pedagógica.

## Verificación

- Primer rojo: compilación falló por ausencia de los tipos del dominio/VM, antes de su implementación (59 s).
- Primer verde real: `:app:testDebugUnitTest --tests '*DefenderSessionTest'` aprobó un tracer test de disparo antes del deadline y empate, y compiló UI/audio (2 min 7 s).
- Última ejecución real: `:app:testDebugUnitTest --tests '*Defender*Test'` ejecutó tres pruebas; el tracer siguió verde y fallaron dos regresiones conductuales (1 min 54 s): `prepare→background` resucitaba el estado interrumpido y un disparo anterior al deadline entregado después del tick perdía la vida.
- Después de esos fallos se aplicaron correcciones y se amplió la suite; **todavía no se compilaron ni ejecutaron**: transición `session.prepare()` real; timestamp/selección capturados en pointer-down; recomputación de ticks desde la última acción aceptada con generación por semilla+ID y deduplicación de sonido por enemigo. También quedaron pruebas nuevas de pausa, cancelación, generación, serialización y audio aislado.
- Pruebas Android escritas pero **no ejecutadas**: partida controlada completa, pausa/objetivos ocultos, extremos Mi4–Fa5 en clave vectorial y reproducción AudioTrack real. No existen capturas de esta UI Android todavía. Revisar especialmente los imports/gestos de pointer-down antes de atribuir compilación aprobada al checkpoint actual.

No queda un build activo de este agente. Reanudar con el comando JVM anterior, usando un turno coordinado por memoria:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME="$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:testDebugUnitTest --tests '*Defender*Test' --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

El agente raíz integrará entrada al menú y ciclo de vida antes de verificar el APK conjunto. Contrato: `defender.DefenderViewModel()` sin Context, `ui.DefenderApp(ui, model, onHome)`, `prepare()` al entrar, `background()` al pausar Activity, `home()` al salir. El reloj de prueba es inyectable. Resultados/configuración/eventos son serializables en memoria, sin persistencia ni progreso.

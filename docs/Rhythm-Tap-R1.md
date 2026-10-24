# Rhythm Tap — incremento R1 / #16

**Integrado en v0.12, 2026-10-24. Verificación JVM y Android/API 34 aprobadas.** El usuario autorizó continuar los tres frentes sin superponer trabajo. La raíz reunió el módulo con Afinador A2 y Defender D1: 97 JVM y 32 Android aprobadas; [verificación y límites](Verificacion-prototipo-v0.12.md). La rama `trabajo/ritmo-r1` y su worktree conservan los checkpoints; el código integrado es la base para el próximo incremento. No se publicó ni se cerró #16.

## Estado comprobado y próximo paso

- La primera prueba de generación fue roja por ausencia de la sesión y pasó después de implementarla (1 min 36 s).
- La prueba de Play/conteo/evaluación fue roja por APIs todavía ausentes (37 s), y después se implementaron sesión, audio y pantalla.
- Antecedente rojo del 2026-10-23: **6 JVM, 4 aprobadas y 2 rojas** (1 min 34 s). Un timestamp de conteo entregado tarde podía devolver la fase PLAYING a COUNT_IN; el ViewModel descartaba un timestamp táctil válido entregado después de finalizar el ticker. Los parches se guardaron sin aprobación final al pausar.
- **Verificación del 2026-10-24 sobre `a284810`: 6 JVM aprobadas, 0 fallos/errores/omitidas; `BUILD SUCCESSFUL in 1m 31s`.** Se ejecutó el comando indicado abajo sin cambios adicionales de producción. Compilaron producción, adaptador de audio y UI. Las cinco pruebas de sesión y una del ViewModel comprueban generación con semilla/cuota y corpus de 90 selecciones/semillas, serialización de la partitura, conteo, ventanas inclusivas, asociación exclusiva, reparación de omisiones con timestamps originales, repetición, interrupción y ausencia de audio nuevo al reparar un tap tras resultados. Los informes locales están en `Plataforma/app/build/test-results/testDebugUnitTest/TEST-ar.edu.unmdp.entrenamiento.rhythm.*.xml`.
- Al entregar inicialmente la rama al agente raíz, las dos pruebas Android estaban escritas y sin ejecutar; la actualización siguiente registra sus intentos posteriores en integración. No se corrieron lint, APK ni emulador desde este worktree. No se modificaron MainActivity, menú, gradle, README o estado global; la integración queda a cargo del agente raíz. No se hizo push ni se modificó GitHub. Gradle se ejecutó en un único turno y se liberó antes de que empezara A2.

Actualización de integración del 2026-10-24: la prueba Android de audio real aprobó; el primer recorrido UI falló al exigir un tap de preparación ausente. El probe mostró que, antes de inyectar el gesto, el estado ya era `RESULTS`, con tiempo transcurrido de 16.003.761.061 ns y sin taps. La sincronización automática de Compose había esperado el cierre de la ejecución. Se corrigió el test para avanzar explícitamente frames de Compose durante el reloj real, observar la fase pública e inyectar con `uptimeMillis` real mediante `DeviceTouch.kt` compartido. **Los dos recorridos Android aprobaron dentro de la suite final de 32/API 34**, manteniendo preparación, partitura repetida e interrupción por ciclo de vida. Se retiraron probes; no se cambió producción por ese fallo.

La raíz ya integró `game-rhythm`, `RhythmApp(ui,model,onHome)`, `RhythmViewModel()` y `background()` en pausa/salida, con lint/APK y regresiones compartidas aprobados. Próximo paso funcional: prueba física de sincronía y comodidad; los incrementos R2–R6 permanecen separados de esta tanda. El siguiente comando permite reproducir las seis pruebas JVM aprobadas:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:testDebugUnitTest --tests '*Rhythm*Test' --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Alcance: práctica local de cuatro compases 4/4 con Negras (R20) y Dos corcheas (R21). Es el primer incremento de la [especificación](Especificacion-ritmo-v1.md); las otras categorías, persistencia de resultados y DDA siguen en R2–R6.

## Decisiones técnicas provisionales

El generador `rhythm-basic-v1` construye dieciséis células de un pulso: diez objetivo (62,5 % de duración) y seis de conexión. Cada categoría seleccionada aparece al menos dos veces; si se eligen ambas, cinco apariciones objetivo de cada una. Las células de conexión usan únicamente negras o dos corcheas. Una aparición tiene una sola procedencia, aunque su dibujo coincida con una categoría elegida. No se suman coincidencias visuales a la cuota. En este subconjunto todas las selecciones no vacías tienen solución constructiva acotada, sin necesitar búsqueda por rechazo.

Las duraciones se conservan como racionales en unidades de negra; el reloj se deriva después de fijar 60 BPM. Cada partitura conserva semilla, versión, compás, categorías y procedencia. No se promete cobertura de silencios, tresillos o ligaduras en R1: sus campos semánticos están previstos sin ofrecer esas categorías ni dibujarlas antes de implementarlas.

Los márgenes de evaluación definidos en la especificación son experimentales. Una demostración funcional, una prueba de reloj controlado y el audio virtual del emulador no acreditan sincronía acústica, comodidad física ni eficacia pedagógica.

## Audio, entrada y ejecución

Conteo de un compás audible y metrónomo opcional se escriben como PCM16 mono/44.100 Hz en una única timeline estática. Los clicks de 35 ms usan envolvente decreciente y seno (1.300 Hz al comenzar cada compás, 900 Hz otros pulsos). El tap real usa otro AudioTrack con click de 550 Hz y no se coloca sobre una grilla ideal. No hay muestras externas ni grabaciones; la señal es sintetizada por código. No se reproduce ninguna secuencia derivada de las notas objetivo.

Si Android entrega `AudioTimestamp`, el origen se proyecta desde su posición de frames al reloj monótono. Si no está disponible en 500 ms, se conserva explícitamente el origen aproximado de la llamada `play` y la incertidumbre total desconocida. En ambos casos no se conoce la latencia acústica. Los eventos táctiles usan `uptimeMillis` original y una conversión al reloj monótono fijada al comenzar. Su resolución de milisegundos no es una cota de incertidumbre total del sistema. La pantalla observa ese reloj absoluto; no encadena retrasos de UI para programar pulsos.

Límite actual identificado por inspección: el área de tap aparece cuando termina esa preparación del reloj de audio, hasta 500 ms después de iniciar el conteo. Un contacto en ese intervalo inicial no se captura; las respuestas empiezan tras el compás completo de cuatro segundos. Verificar esta transición y el seguimiento visual en el host integrado. La suite JVM no acredita audibilidad, precisión acústica ni legibilidad física.

Ventana estrecha: `min(60 ms, 0,20d)`; amplia: `min(150 ms, 0,45d)`, siendo d el intervalo local mínimo entre ataques. Fronteras inclusivas; las omisiones se cierran al superar la frontera amplia. Cada tap confirma como máximo un ataque y no se reasigna después. Un evento con timestamp anterior que Android entregue tarde puede reparar únicamente una omisión, conservando el desvío original; nunca un tap confirmado. Durante el conteo se registra como preparación, sin convertirse en respuesta. La interrupción no se convierte en una partida completada.

Las opciones de audio se conservan al salir/reentrar mientras viva el ViewModel. No se guardan resultados ni preferencias en archivos nuevos en R1. Los resultados de práctica y la partitura exacta permanecen en memoria; repetir conserva contenido y huella sin atribuir progreso.

## Recorrido de demostración

1. Abrir Rhythm Tap desde el menú, elegir Negras y/o Dos corcheas y generar. Revisar los cuatro compases desplazándose antes de Play. Generar/revisar no inicia audio ni ejecución.
2. Elegir metrónomo y sonido de taps por separado. Pulsar Play y escuchar cuatro pulsos de entrada. Los taps de ese conteo son de preparación.
3. Tapear sobre el área inferior; sostener el dedo no repite golpes. El marcador y el scroll siguen la posición sin gobernar los timestamps táctiles.
4. Terminar y revisar aciertos, adelantos/atrasos con signo, extras, omisiones y desglose de apariciones objetivo por categoría. Repetir conserva el mismo patrón y vuelve a vista previa; requiere otro Play.
5. Iniciar de nuevo y cambiar de aplicación: el recorrido queda interrumpido, sin reanudar ni completar automáticamente. No se solicita permiso de micrófono.

La prueba física pendiente debe registrar Android/app, dispositivo, ruta de audio (altavoz/cable/Bluetooth), entorno y método externo de comparación de click/contacto. Mantenerla separada de las comprobaciones de AudioTrack y relojes internos del emulador. El DDA, otros compases y el resto del catálogo siguen en sus tickets.

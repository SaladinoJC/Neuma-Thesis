# Rhythm Tap — incremento R1 / #16

**Checkpoint WIP, 2026-10-23.** El usuario pidió detener el trabajo por hoy y continuar en una próxima sesión con una ventana de cinco horas. No es una entrega terminada y no debe presentarse #16 como completado. La rama `trabajo/ritmo-r1` y el worktree `C:/Users/Juan/Tesis-worktrees/ritmo-r1` conservan el trabajo para integrar después.

## Estado comprobado y próximo paso

- La primera prueba de generación fue roja por ausencia de la sesión y pasó después de implementarla (1 min 36 s).
- La prueba de Play/conteo/evaluación fue roja por APIs todavía ausentes (37 s), y después se implementaron sesión, audio y pantalla.
- Última ejecución completa del subconjunto: **6 JVM, 4 aprobadas y 2 rojas** (1 min 34 s). La producción, el adaptador de audio y la UI compilaban en ese punto. Fallos conductuales: un timestamp de conteo entregado tarde podía devolver la fase PLAYING a COUNT_IN; el ViewModel descartaba un timestamp táctil válido entregado después de finalizar el ticker.
- Se aplicaron parches para ambos casos, para actualizar el resultado al recibir preparación tardía y para conservar el listener táctil en RESULTS, sin reproducir audio ni aceptar nuevos taps posteriores. **Estos últimos cambios no están verificados.** La ejecución verde ya había arrancado al llegar el pedido de detención y se canceló antes de completar compilación/test; no cuenta como aprobación. Los dos procesos Java identificados de esa ejecución (wrapper y daemon de esta rama) fueron detenidos. No quedan compilaciones de este agente en segundo plano.
- Las dos pruebas Android están escritas, **no ejecutadas**. No se corrieron lint, APK ni emulador de esta rama. No se modificaron MainActivity, menú, gradle, README o estado global; la integración queda a cargo del agente raíz. No se hizo push ni se modificó GitHub.

Próximo paso exacto: ejecutar desde `Plataforma/` el comando siguiente, verificar las seis pruebas y revisar el parche reciente antes de integrar. Después, integrar entrada `game-rhythm`, `RhythmApp(ui,model,onHome)` con `RhythmViewModel()` y llamada `background()` en pausa/salida; ejecutar las dos pruebas Android y lint/compilación por turnos. No avanzar R2 antes de cerrar estas verificaciones.

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

Ventana estrecha: `min(60 ms, 0,20d)`; amplia: `min(150 ms, 0,45d)`, siendo d el intervalo local mínimo entre ataques. Fronteras inclusivas; las omisiones se cierran al superar la frontera amplia. Cada tap confirma como máximo un ataque y no se reasigna después. Un evento con timestamp anterior que Android entregue tarde puede reparar únicamente una omisión, conservando el desvío original; nunca un tap confirmado. Durante el conteo se registra como preparación, sin convertirse en respuesta. La interrupción no se convierte en una partida completada.

Las opciones de audio se conservan al salir/reentrar mientras viva el ViewModel. No se guardan resultados ni preferencias en archivos nuevos en R1. Los resultados de práctica y la partitura exacta permanecen en memoria; repetir conserva contenido y huella sin atribuir progreso.

## Recorrido de demostración

1. Abrir Rhythm Tap desde el menú, elegir Negras y/o Dos corcheas y generar. Revisar los cuatro compases desplazándose antes de Play. Generar/revisar no inicia audio ni ejecución.
2. Elegir metrónomo y sonido de taps por separado. Pulsar Play y escuchar cuatro pulsos de entrada. Los taps de ese conteo son de preparación.
3. Tapear sobre el área inferior; sostener el dedo no repite golpes. El marcador y el scroll siguen la posición sin gobernar los timestamps táctiles.
4. Terminar y revisar aciertos, adelantos/atrasos con signo, extras, omisiones y desglose de apariciones objetivo por categoría. Repetir conserva el mismo patrón y vuelve a vista previa; requiere otro Play.
5. Iniciar de nuevo y cambiar de aplicación: el recorrido queda interrumpido, sin reanudar ni completar automáticamente. No se solicita permiso de micrófono.

La prueba física pendiente debe registrar Android/app, dispositivo, ruta de audio (altavoz/cable/Bluetooth), entorno y método externo de comparación de click/contacto. Mantenerla separada de las comprobaciones de AudioTrack y relojes internos del emulador. El DDA, otros compases y el resto del catálogo siguen en sus tickets.

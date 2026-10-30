# Rhythm Tap: lectura lateral y feedback por nota

2026-10-29. Retoma tras apagado. El usuario autorizó «ponete con esos comportamientos, si se puede usar VexFlow buenísimo, sino adapta todo lo que puedas para que funcione lo más similar posible», después de la investigación de Glisso. Alcance: presentación de Rhythm Tap; R6/DDA y Personaje siguen pendientes. Versión **0.12.11/código 26 en verificación**; todavía sin entrega final.

## Conducta y decisiones técnicas

La partitura ocupa una línea SVG transparente sobre el papel de Music Garden. Antes de Play y al terminar se puede deslizar para revisar los cuatro compases. Durante la ejecución se desplaza bajo un marcador fijo; la posición se interpola entre las coordenadas grabadas y los tiempos musicales enviados desde Kotlin. El tempo, el conteo y los timestamps conservan el reloj nativo. Una actualización visual no crea ni reasigna respuestas.

Los ataques a tiempo colorean de verde la cabeza; adelantos, atrasos y omisiones usan `errorText`. Las plicas, barras, puntillos y ligaduras conservan tinta. Las continuaciones ligadas comparten el resultado del ataque original. Los taps extra continúan en el feedback textual y resumen, sin colorear otra nota. La recepción tardía de un tap puede corregir una omisión con su timestamp original. Repetir conserva la partitura y limpia las marcas. El historial usa la partitura y evaluación guardadas, sin regenerarlas.

Se usa [VexFlow 5.0.0](https://vexflow.github.io/vexflow-examples/guides/getting-started/) con Bravura/Academico incluidas, SVG y un WebView local. La biblioteca ocupa 728.171 bytes sin comprimir y no necesita descargar fuentes. MIT/OFL y autores se conservan junto a los recursos. El adaptador es propio; no se copia código de Glisso. Kotlin/Compose continúa siendo el stack de la aplicación. No se añaden dependencias Gradle.

El WebView sirve únicamente tres archivos empaquetados bajo un origen HTTPS local; red, archivos, contenido externo y navegación están bloqueados. Su único puente hacia Kotlin informa carga/fallo. La entrada táctil y accesible del pad, audio, evaluación, guardado y exportación continúan nativos. Las actualizaciones visuales se agrupan mientras hay una llamada JavaScript en vuelo; el grabado se repite al cambiar partitura/ancho/configuración, no por cada tick. Cuatro compases no justifican virtualizar sectores como Glisso.

Play espera el grabado inicial para no iniciar el conteo con una partitura vacía. Un fallo o diez segundos sin completar la carga muestran la alternativa Canvas por compases y habilitan Play. El tiempo de una ejecución ya iniciada no espera al renderer. En la alternativa, el seguimiento se revisa manualmente.

Con reducción manual o del sistema, la ventana permanece quieta dentro de cada pulso y cambia inmediatamente al siguiente, sin marcador móvil. Esto permite mostrar todas las subdivisiones aun cuando un compás denso exceda el ancho del teléfono. No hay easing o animación independiente del tempo. Las preferencias no modifican tolerancias ni resultados.

## Identidad y límites

Papel cálido, tema, controles de al menos 48 dp y tokens existentes; fondo transparente, sin nuevos adornos, texturas ni ilustraciones. El verde por nota es una excepción explícita solicitada al acierto ocre común. Bravura se justifica por el grabado musical; Academico queda dentro de la partitura. La UI continúa con Fraunces/Nunito Sans y sigue escalando con el sistema; la notación conserva proporción musical en dp. Descripción semántica nativa por compás y resultados textuales complementan el SVG. El inspector web verifica el renderer; no acredita toda la interfaz Compose ni TalkBack por gestos/locución.

No se incorporan Hold, canciones, reproducción del patrón ideal, calibración automática ni compensaciones arbitrarias. La implementación funcional no demuestra eficacia pedagógica, precisión física o FPS estables en teléfono.

## Reproducción de pruebas

Desde `Plataforma/`, configurar Java/SDK como en README y ejecutar:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
node tools/verify_rhythm_notation.mjs
```

La prueba JVM `RhythmEngravingTest` genera `app/build/verification/rhythm-engraving/catalog.json` desde las 61 partituras reales compatibles del catálogo. El script utiliza Chrome/Edge instalado y el cliente CDP de `design-taste-frontend` en esta máquina; se puede indicar otra ruta mediante `CDP_CLIENT`. Comprueba todas las partituras a 320/390/768 px, transparencia, un grupo por figura, anclas crecientes, marcador fijo, avance, ausencia de nuevo grabado en ticks, corrección tardía, ligaduras, repetición, revisión manual y ventanas reducidas.

Android: `RhythmEngravingAndroidTest` prueba WebView real, swipe manual, feedback compartido por ligadura, seguimiento con reloj controlado, repetición, reducción de movimiento y fallo simulado del renderer con alternativa nativa. Las pruebas históricas se adaptaron de renglones a línea continua y esperan la carga asíncrona antes de Play. Usar solo un emulador de pruebas para la regresión; algunos casos reinician perfiles.

## Estado de verificación

El usuario pidió cerrar la sesión: «Tengo que irme, finalizá». Se conserva el código y un [APK de prueba](../Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.11-ritmo-prueba.apk), 15.490.123 bytes; SHA-256 `0b08882802de147ad19edd6152f880af26dd2b31213fc949ea57ddfe45b0911d`. **No es una entrega final validada.** Incluye la marca temporal Neuma que se integraba en otra sesión, conservando Music Garden.

167 JVM y ensamblados de app/pruebas aprobados; último build de 1 min 21 s. 186 comprobaciones Chrome: 61 partituras a tres anchos y tres grupos de comportamiento. Tres Android nuevas aprobaron a 411 dp (84,119 s) y 320 dp/texto 200 % (49,262 s), **antes del ajuste final de posición vertical**. Se inspeccionaron capturas de acierto/ligadura, repetición, movimiento reducido y alternativa nativa. Con texto 200 % se observó recorte inferior de la partitura: al iniciar se lleva ahora al área visible. Ese ajuste está compilado y falta comprobarlo en Android.

La regresión aprobó 10/11 casos, incluidos audio/tap real del emulador, R2–R4 e historial. El recorrido de exportación falló al consultar Compose mientras DocumentsUI retornaba. Se corrigió la espera del arnés, sin cambiar la exportación del producto, y falta reejecutarla. Las esperas de carga/swipe y los frames explícitos también se adaptaron al WebView asíncrono.

La tanda de lint del 2026-10-29 encontró una propiedad API 27 del tema de marca declarada para minSdk 26 (1 error/17 advertencias). Se separó el tema base y `values-v27/styles.xml`. **Repetición del 2026-10-30 aprobada: 0 errores/17 advertencias**; ya no está pendiente ese control. El reporte anterior se conserva como antecedente.

La construcción se aisló en `C:/Users/Juan/Tesis-worktrees/rhythm-engraving-verify/Plataforma`. El AVD privado `Rhythm_Verify` usa la carpeta `android-avd` contigua y puerto 5574/API 34; animaciones globales 1, densidades 420/540 y escala 1/2. El modo reducido se solicita en el escenario. Perfiles previos sufrieron bloqueos del sistema y del instalador/capturas; son antecedentes, sin atribución a la evaluación. El perfil compartido tenía animaciones 0, que activaban correctamente movimiento reducido.

Evidencia y resumen: `Plataforma/build/verification/2026-10-29-Rhythm-lateral/`. `summary.json` distingue aprobaciones y pendientes; `sources.zip` conserva las fuentes del APK de prueba. Las capturas son anteriores al último ajuste vertical y no acreditan su corrección.

Para continuar: repetir `RhythmEngravingAndroidTest` en 411 y 320/texto 200 %, y `RhythmHistoryNavigationAndroidTest`; revisar capturas, JSON nativo y marca Neuma en Android, y después preparar entrega final. Teléfono físico y gestos/locución TalkBack pendientes. R6 y Personaje no se implementaron en esta retoma.

## Retoma del 2026-10-30 sin emulador

El usuario solicitó continuar sin abrir el emulador para poder usar la PC y avisarle al terminar antes de decidir si se inicia. Se completaron los controles de host, sin instalación ni pruebas Android o navegador. Se verificó por SHA-256 que los 324 archivos de fuentes/configuración de raíz coinciden con la copia aislada indicada arriba.

Gradle terminó correctamente en 3 min 12 s. Lint se ejecutó de nuevo y reportó 0 errores/17 advertencias. Los tests JVM y ensamblados de app/pruebas quedaron `UP-TO-DATE`: los XML conservan 167 casos aprobados, sin nueva ejecución. El APK resultante coincide byte a byte con el de prueba descrito arriba; no hay un nuevo binario ni cambio de versión. También se comprobaron el PNG del logo contra el original corregido, los colores de marca contra tokens y el hash/licencias empaquetadas de VexFlow. No hubo cambios de producto, pantallas ni assets; la validación visual de los últimos ajustes continúa pendiente.

Se limitó la carga con ejecución offline, un worker, heap de 1536 MiB, SerialGC, dos procesadores visibles, prioridad BelowNormal y daemon de una sola ejecución. Comando usado desde la copia aislada, con Java/SDK configurados como en README:

```powershell
.\gradlew.bat --offline --no-daemon --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Evidencia local en `Plataforma/build/verification/2026-10-30-host/`: resumen, log, informes lint, XML JVM comprimidos, comprobación de assets y manifiesto de fuentes. Los avisos de versiones XML del SDK y deprecaciones Gradle no impidieron el build. La verificación de host no acredita el ajuste vertical con texto 200 % ni el retorno de DocumentsUI: ambos requieren repetir sus recorridos en Android cuando el usuario permita abrir el emulador.

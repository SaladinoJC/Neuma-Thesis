# Rhythm Tap — R3 / #18

Implementación local autorizada al continuar los pendientes después de A6 y retomada por el usuario el 2026-10-29. **0.12.8/código 23**. Se trabaja un ticket por vez, sin subagentes, push ni cambios de issues. Personaje continúa pausado.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.8.apk`, **14.901.046 bytes**; SHA-256 `5ff51bfa0886391e43318703a995539b2c803a42daf904eba9872fe14014fb94`. Firma compatible con 0.12.7 verificada y hash igual al instalado en el emulador. Instalar encima con `adb install -r`, sin desinstalar para conservar el perfil.

## Conducta disponible

Ocho categorías nuevas de un pulso, en 2/4, 3/4 y 4/4:

| Código | Célula, negra = 1 | Ataques |
| --- | --- | --- |
| R03 | 1/4 + 1/4 + 1/4 + 1/4 | 4 |
| R04 | 1/2 + 1/4 + 1/4 | 3 |
| R05 | 1/4 + 1/4 + 1/2 | 3 |
| R06 | 1/4 + 1/2 + 1/4 | 3 |
| R07 | 3/4 + 1/4 | 2 |
| R08 | 1/4 + 3/4 | 2 |
| R13 | silencio 1/4 + tres notas 1/4 | 3 |
| R14 | tres notas 1/4 + silencio 1/4 | 3 |

Selección explícita → Generar → vista previa silenciosa → Play → conteo → taps → resumen/repetición. Se pueden mezclar con las categorías anteriores cuando caben. Conexión solo con negras/corcheas, mínimo dos apariciones por elegida y cuota conjunta 50–75 %, cercana a 60 %. Seleccionar las ocho a la vez excede la cuota de cuatro compases: se informa sin quitar categorías. `rhythm-simple-v3` identifica partituras con células R3; los recorridos R1/v1 y las selecciones R2/v2 conservan sus versiones.

Las ventanas existentes se recalculan desde la distancia al ataque vecino: estrecha `min(60 ms, 20 % d)`, amplia `min(150 ms, 45 % d)`. Para semicorcheas a 120 BPM, `d=125 ms`: 25 ms/56,25 ms. Son márgenes experimentales, con fronteras inclusivas. Cada tap se asigna una sola vez y conserva su tiempo/desvío; no se desplaza al ritmo ideal. Un ataque vecino válido no se penaliza otra vez como extra de un silencio.

## Interfaz y notación

Papel, verdes y fuentes Music Garden mediante el tema compartido existente; sin nuevos assets ni dependencias. Cabezas, plicas, puntillos, barras y silencios derivan de las duraciones racionales. Barra primaria por grupo de pulso; secundaria para semicorcheas, con tramos cortos orientados dentro del grupo cuando corresponde. El silencio de semicorchea tiene dos ganchos; no se prolongan barras a través de silencios.

Referencias primarias consultadas: [tabla de silencios SMuFL](https://smufl.formats.music/latest/tables/rests.html) y [barras/agrupación en LilyPond](https://lilypond.org/doc/v2.24/Documentation/notation/beams). Se mantiene trazado vectorial propio, sin copiar arte ni incorporar esas bibliotecas/fuentes.

La selección de categorías se puede volver a abrir tras generar; queda plegada para priorizar la partitura. El área de tap sigue fija y acepta contactos iniciales, no repeticiones por sostener el dedo. El desplazamiento conserva el reloj independiente; con reducción manual del menú o del sistema se mueve de inmediato al compás, sin animación.

## Recorrido reproducible

1. Menú → Juegos → Rhythm Tap → Jugar. Elegir 2/4, 3/4 o 4/4 y una célula nueva; por ejemplo, Cuatro semicorcheas. Generar no reproduce el patrón objetivo.
2. Revisar los cuatro compases y pulsar Play. Escuchar el compás de entrada y tocar sobre el área TAP; no hace falta sostener notas ni tapear silencios.
3. Repetir con Corchea con puntillo + semicorchea y con Silencio de semicorchea + tres semicorcheas. Revisar orden, puntillos y agrupación por pulso.
4. Combinar Cuatro semicorcheas, Corchea con puntillo + semicorchea, Tres semicorcheas + silencio y Negra con puntillo + corchea en 4/4. Apagar el metrónomo posterior al conteo; el reloj del ejercicio continúa sin realinear los golpes.
5. Adelantar, atrasar, omitir y agregar golpes. Revisar el resumen por categoría y los desvíos con signo. «Repetir mismo patrón» conserva la partitura; «Cambiar células» vuelve a abrir la selección.
6. Elegir las ocho categorías nuevas juntas: la app informa que dos apariciones por elegida exceden la cuota. No quita selecciones silenciosamente.

## Verificación

- 145 JVM aprobadas: catálogo nuevo por métrica, mezclas/semilla/JSON, fronteras de ventanas, silencio corto y deriva sin realinear la grilla, además de la suite previa.
- Dos Android R3 aprobadas a 411 dp: 24 ejemplos canónicos y ejercicio combinado sin metrónomo, con adelanto/retraso/extra/omisión y repetición. El escenario combinado usa reloj/sonido controlados y la acción accesible de tap, no mide audio físico. La regresión táctil/audio real de R1/R2 se comprueba aparte.
- App/pruebas ensambladas y lint **0 errores/14 advertencias**. Tanda integrada de **11/11 Android** (R3, R1, R2, notación y menú) aprobada a 411 dp en 96,305 s. No es una suite Android completa de toda la aplicación; A6 conserva como antecedente sus 50/50.
- Dos Android R3 también aprobadas a **360 y 320 dp**; el flujo combinado además a **320 dp/texto 200 %**. Las capturas definitivas usan el arnés con espera de tres vsync más un segundo de margen para el compositor software. El margen solo afecta la evidencia de pruebas, no el reloj o la interacción del producto. [Capturas y condiciones](imagenes/ritmo-r3/README.md).
- Un primer escenario mezcló timestamps reales de inyección táctil con reloj sintético; no registró los taps esperados. Se corrigió la prueba usando explícitamente la acción semántica, conservando la prueba original de entrada táctil real por separado.
- Algunas capturas anteriores quedaron vacías o mostraron la célula previa; tres vsync solos no bastaron en esta retoma (R13 todavía mostraba R08). Esas capturas se conservan como antecedentes en `build/verification`, pero no se usan como evidencia final. El emulador mostró un diálogo de fallo de Bluetooth al arrancar; se desactivó Bluetooth y se cerró el diálogo antes de repetir las capturas válidas. No se atribuye ese fallo al producto.

Las láminas repiten en un compás la célula real obtenida del generador para revisar **máxima densidad** en cada métrica. Son ejemplos de renderizado: no se presentan como partidas que cumplan la cuota del 60 %. Las capturas del flujo sí usan un ejercicio combinado generado de cuatro compases.

Evidencia local: `Plataforma/build/verification/2026-10-29-R3/summary.json`, resultados y reporte JVM comprimidos, XML/HTML de lint, logs Android, firmas y capturas por dimensión. `final-build.log` corresponde a las 145 JVM/ensamblados; `final-selected-411.log`, a las 11 Android; `retoma-capture-build-2.log`, al arnés definitivo/lint; `capturas-verificadas-*.log`, a la repetición final por dimensiones.

Reproducción desde `Plataforma/`, con Java/SDK como en README:

```powershell
.\gradlew.bat --no-daemon --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
$adb = Join-Path $env:ANDROID_HOME 'platform-tools/adb.exe'
& $adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
& $adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
& $adb -s emulator-5554 shell am instrument -w -e class ar.edu.unmdp.entrenamiento.RhythmR3AndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Usar solo un emulador para pruebas instrumentadas: pueden reiniciar datos. Comprobar el cierre `OK (2 tests)` en la salida, no solo el código de adb. Para las dimensiones verificadas, mantener 1080×2400 físicos y usar densidades 420/480/540 (411/360/320 dp); `settings put system font_scale 2.0` amplía texto. Restaurar densidad 420 y escala 1.0 al terminar. SerialGC es un ajuste temporal de la ejecución, sin cambiar la configuración permanente del proyecto.

## Criterios visuales y límites

Se conservaron papel cálido, verdes naturales, Fraunces/Nunito Sans y tokens comunes; no se agregaron assets, iconos, bibliotecas ni animaciones decorativas. La partitura sigue siendo vectorial desde datos estructurados. Preparación, previa, resumen/repetición y las láminas canónicas se comprobaron en las dimensiones indicadas; texto 200 % usa desplazamiento sin reducir la tipografía. El área TAP permanece fija y el reloj no depende del scroll.

La revisión usa las reglas de pantallas de aplicación de `design-taste-frontend` adaptadas a Compose. Se mantiene el diseño existente y la identidad obligatoria del repositorio; no se sortea otra dirección artística. Excepciones conscientes: paleta marfil/verde, títulos serif y selectores nativos existentes frente a las preferencias genéricas de dashboards de la skill. El inspector web no verifica Compose; la evidencia es instrumentación y capturas nativas, sin atribuir una inspección de navegador a Android.

Prueba física pendiente en S22 Ultra: leer semicorcheas a 60 BPM, probar metrónomo apagado, activar/desactivar sonido de taps y comparar timestamps/audio con un método externo si se mide latencia. Emulador y reloj controlado no acreditan sincronía acústica ni eficacia pedagógica. R4–R6 y D3–D7 siguen pendientes; no se agrega DDA ni persistencia de ritmo en R3.

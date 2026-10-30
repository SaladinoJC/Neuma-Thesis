# Rhythm Tap — R4 / #19

Continuación local autorizada por el usuario al retomar los pendientes el 2026-10-29, después de cerrar R3. **0.12.9/código 24, terminada en software y verificada en emulador**. Un ticket por vez, sin subagentes. Personaje permanece pausado; sin push ni cambios de issues.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.9.apk`, **14.375.783 bytes**; SHA-256 `55ff409f4c8e78dc21bfc4545c6a3955de766d7529a05dfa2251b512c46dcd26`. Firma compatible con 0.12.8 y hash igual al instalado en el emulador, comprobados. Instalar encima con `adb install -r`, sin desinstalar para conservar el perfil.

## Conducta

Se completa el catálogo inicial de 21 categorías en compases simples 2/4, 3/4 y 4/4:

| Código | Plantilla, negra = 1 | Ataques por aparición |
| --- | --- | --- |
| R15 | Tres corcheas de tresillo: 1/3 + 1/3 + 1/3 | 3 |
| R16 | Corchea + negra + corchea: 1/2 + 1 + 1/2 | 3 |
| R17 | Dos negras ligadas: 1 + 1, ligadura en la primera | 1 |

R16 comienza en tiempo entero y la negra central ataca a medio pulso, atravesando el siguiente. R17 conserva dos cabezas/plicas con curva de prolongación; no se reemplaza visualmente por una blanca ni se pide sostener el dedo. Tapear su continuación es un extra si no corresponde a otro ataque válido.

Las células completas ocupan tiempos enteros; la cabida, suma por compás y cuota se calculan con fracciones exactas, sin convertir a `Double` ni truncar para colocarlas. Se conserva la búsqueda acotada, cancelable, con semilla y rechazo explícito cuando no cabe la selección. `rhythm-simple-v4` identifica ejercicios con estas células; los recorridos R1/v1, R2/v2 y R3/v3 conservan sus versiones.

Cada ataque convierte su posición absoluta racional a nanosegundos con el tempo fijado. No se suman tercios de pulso ya redondeados, por lo que no se acumula el error compás a compás. Las ventanas siguen dependiendo de la distancia local a los ataques vecinos y no se cambian en este incremento.

Antes de aceptar una vista previa se verifica la estructura de cuatro compases, posiciones continuas, índices, métrica, procedencia y notas de cada plantilla aprobada. Una suma incorrecta, ternaria alterada o ligadura incompleta deja un aviso y no permite iniciar. No se agregan variantes de silencios ternarios, otras ligaduras ni síncopas fuera del catálogo acordado.

## Notación e identidad

Se reutiliza el trazado vectorial estructurado existente: barra de corcheas y número/corchete de tresillo; banderas separadas para las corcheas de R16; curva de ligadura debajo de las dos negras en R17. La cifra ternaria mantiene su proporción con las plicas en dp, también con texto de interfaz ampliado; la descripción accesible nombra la célula. No se usa una imagen ni un carácter como sustituto de figuras musicales.

Referencia primaria para el tratamiento de [tresillos](https://lilypond.org/doc/v2.24/Documentation/notation/writing-rhythms#tuplets) y [ligaduras de prolongación](https://lilypond.org/doc/v2.24/Documentation/notation/writing-rhythms#ties): manual de LilyPond 2.24. El renderer sigue siendo propio, sin copiar assets ni incorporar LilyPond o bibliotecas nuevas.

Papel cálido, verdes, Fraunces/Nunito Sans y tokens existentes; misma columna nativa, selección plegable, controles y área TAP fija. Sin nuevos assets, fuentes, audio o animaciones. Se mantiene el desplazamiento inmediato con reducción de movimiento; el marcador y la evaluación derivan del reloj de la sesión.

## Recorrido reproducible

1. Menú → Juegos → Rhythm Tap → Jugar. Elegir Tresillo de corcheas y generar. Revisar tres figuras por pulso, cifra 3 y agrupación.
2. Pulsar Play y esperar el compás de entrada. Dar tres taps iguales por pulso de tresillo. La vista previa no reproduce el patrón objetivo.
3. Generar Corchea + negra + corchea. Comprobar el ataque de la negra entre pulsos y su duración atravesando el siguiente.
4. Generar Dos negras ligadas. Tapear solo la primera; dar un segundo golpe deliberado durante la continuación para revisar el extra.
5. En 4/4 combinar las tres nuevas categorías con Cuatro semicorcheas. Se necesitan dos apariciones de cada una, que ocupan 12 de los 16 pulsos (75 %, límite superior de la cuota). La misma selección en 2/4 es rechazada sin quitar categorías.
6. Apagar/activar Sonido de mis taps reales independientemente del metrónomo. Revisar resumen por categoría, repetir la partitura exacta y salir durante el nuevo conteo para comprobar interrupción.

## Verificación

Seis pruebas JVM nuevas comenzaron en rojo porque no existían R15/R16/R17. Tras añadir las plantillas, las **23 JVM de ritmo** aprobaron. La suite completa posterior tiene **151 JVM aprobadas**, incluidos corpus por categoría/métrica/semilla, cuota, JSON, ataque ternario absoluto, síncopa, reataque de ligadura, rechazo de partituras inválidas y mezcla con R3.

Ensamblado de app/pruebas y lint aprobados: **0 errores/14 advertencias**. Evidencia: `Plataforma/build/verification/2026-10-29-R4/`; SerialGC temporal, sin cambiar Gradle permanentemente.

**13/13 Android seleccionadas** en Pixel_6/API 34 a 411 dp, en 114,143 s: R4, R3, ritmo básico/R2, audio real de las tres métricas, notación y menú. No es una suite Android completa de toda la aplicación. Los dos casos R4 también aprobaron a **360 y 320 dp** (22,442 s y 23,273 s); el flujo combinado a **320 dp/texto 200 %** (16,799 s).

Los dos casos nuevos cubren nueve ejemplos canónicos (tres categorías × tres métricas), ejercicio combinado con R3, evaluación del reataque en la ligadura como extra, taps opcionales sin reproducción/reproducción solicitada, resumen por categoría, repetición exacta e interrupción. El flujo nuevo utiliza reloj/audio controlados y la acción accesible de tap; no es una medición de destreza humana o sonido físico. La regresión R1/R2 comprueba por separado entrada táctil y avance real del adaptador de audio.

**17 capturas nativas inspeccionadas**, sin cuadros vacíos/desfasados: [dimensiones y condiciones](imagenes/ritmo-r4/README.md). Se reutiliza el arnés de R3 con tres vsync más un segundo de margen para el compositor software. Esa espera existe solo al tomar evidencia y no altera el reloj del producto. Notación legible en los tres anchos, curvas/corchetes dentro de su renglón y texto de interfaz ampliado desplazable.

Se aplicaron las reglas de pantallas de aplicación de `design-taste-frontend` conservando la identidad obligatoria y el diseño existente. No se eligió otra dirección ni se agregaron dependencias. Excepciones conscientes a las preferencias genéricas de dashboards: marfil/verde y títulos serif del sistema Music Garden, notación dimensionada en dp y columna nativa móvil. El inspector web no verifica Compose; se usaron instrumentación y capturas Android.

Evidencia consolidada: `Plataforma/build/verification/2026-10-29-R4/summary.json`, ZIP de resultados/reporte JVM y fuentes de ritmo, XML/HTML de lint, logs de red/green/ensamblado y de Android por dimensión, firmas, hash instalado y capturas. Se conserva la entrega 0.12.8 de R3 por separado.

Reproducción desde `Plataforma/`, con Java/SDK como en README:

```powershell
.\gradlew.bat --no-daemon --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
$adb = Join-Path $env:ANDROID_HOME 'platform-tools/adb.exe'
& $adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
& $adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
& $adb -s emulator-5554 shell am instrument -w -e class ar.edu.unmdp.entrenamiento.RhythmR4AndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Ejecutar pruebas instrumentadas solo sobre emulador: pueden reiniciar datos. Exigir `OK (2 tests)` en la salida, no solo el código de adb. Con 1080×2400 físicos, densidades 420/480/540 equivalen a 411/360/320 dp; `settings put system font_scale 2.0` amplía el texto. Restaurar densidad 420 y escala 1.0 al terminar. Para repetir la regresión seleccionada, usar la lista de clases que registra `android-411.log`.

La prueba física del S22 Ultra, locución/gestos TalkBack, sincronía acústica y eficacia pedagógica continúan pendientes. Historial de ritmo y DDA corresponden a R5–R6; los resultados actuales siguen en memoria.

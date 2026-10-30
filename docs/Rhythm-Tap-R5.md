# Rhythm Tap — R5 / #20

Continuación autorizada por el usuario mediante «Ok, sigamos», después de R4, el 2026-10-29. **0.12.10/código 25, terminada en software y verificada en emulador**. Un ticket por vez, sin subagentes; Personaje sigue pausado. No se hace push ni se modifican issues.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.10.apk`, **14.985.872 bytes**; SHA-256 `19a45a80dbef26478afe7ca92e28fc7d93a68319895cd365971ba1895588826b`. Firma compatible con 0.12.9 y hash igual al instalado en el emulador, comprobados. Instalar encima con `adb install -r`, sin desinstalar para conservar el perfil.

## Conducta

Los ejercicios que se ejecutan desde esta versión conservan la partitura exacta y sus resultados en `rhythm-results.json`. Preparar o generar sin Play no crea una partida. Completar, interrumpir, salir o pasar a segundo plano sella un intento completo/interrumpido. Las posiciones aún sin evaluar no se convierten en omisiones por abandonar.

El historial permite filtrar por categoría seleccionada, compás, tempo, origen y metrónomo. Solo se ofrece Práctica; progresión/DDA siguen en R6. Las listas muestran bloques de 20 y permiten consultar el resto. El detalle muestra la partitura guardada, apariciones objetivo/conexión, ataques, silencios, desvíos, extras y posiciones sin evaluar, sin regenerar ni recalcular el ejercicio histórico.

«Repetir como práctica» recupera esa partitura, sus ayudas y su huella. Se revisa en silencio antes de Play y el nuevo intento recibe otro UUID con referencia al original; conserva la marca de repetición. Cambiar el ID no constituye evidencia nueva para una futura progresión. No se reconstruyen partidas anteriores a R5 que solo se habían mantenido en memoria.

## Datos y decisiones técnicas

- Documento JSON versión 1 y registros versión 1: UUID, inicio/fin civil, partitura/semilla/versión de generador, huella/versionado, tempo/métrica, selección y procedencia, ayudas, origen, repetición, evaluación temporal original, ventanas efectivas, taps crudos/timestamps/desvíos, silencios y metadatos del reloj. No contiene grabaciones PCM.
- Se reutiliza `JsonResultsFile` y la barrera de perfil existente. Una nueva operación de reemplazo permite guardar revisiones del mismo intento por ID, con sustitución atómica y protección frente a un reinicio concurrente. Los demás juegos conservan la operación anterior de agregar sin duplicar.
- Un tap entregado después del resumen puede reparar una omisión usando su timestamp original. R5 conserva el UUID e incrementa la revisión del resultado; el fin civil del intento se mantiene. Reintentar la misma revisión es idempotente y una revisión anterior no reemplaza una nueva.
- Una escritura suspendida no elimina revisiones más recientes en espera. Una lectura suspendida no borra resultados nuevos, pendientes o filtros. Los fallos dejan aviso y reintento; el original ilegible/de esquema desconocido se conserva.
- Los pendientes de escritura permanecen en memoria durante esa apertura. Pueden exportarse separados; cerrar forzosamente el proceso antes de guardar/exportar puede perderlos. No se acredita una cola durable ante muerte del proceso.
- Exportación general versión 1 ampliada con `rhythm` y `pendingRhythmSaves`, opcionales al leer documentos anteriores. Los consumidores anteriores deben ignorar campos nuevos. El ZIP copia siete JSON conocidos que existan y, cuando corresponda, `pending-rhythm-saves.json`; también conserva las copias pendientes de voz/afinador. Recuperar no repara ni importa.
- Exportar espera a que terminen las acciones de ritmo. El reinicio confirmado drena operaciones, borra el archivo nuevo junto a los anteriores y vacía estado/pendientes del ViewModel. Se preservan los archivos ya exportados.

Estas son elecciones técnicas del agente dentro del ticket aprobado, no parámetros calibrados ni decisiones nuevas del director.

## Recorrido reproducible

1. Juegos → Rhythm Tap. Elegir células, generar, revisar y pulsar Play. Completar un ejercicio o usar Interrumpir.
2. Comprobar «Ejercicio guardado». Abrir Historial de ritmo; cerrar y volver a abrir la app y consultar la misma partitura.
3. Abrir filtros; elegir categoría, compás, tempo, Práctica y/o metrónomo. Consultar un filtro sin coincidencias y quitarlo.
4. Abrir Ver partitura y resultado. Revisar compases, categorías objetivo/conexión, ataques y silencios. Una interrupción conserva datos ausentes como sin evaluar.
5. En Datos del ejercicio consultar huella, semilla, versiones, revisión y reloj, sin atribuir precisión acústica a esos metadatos.
6. Repetir como práctica: la vista previa coincide, no suena sola y el nuevo intento no altera el anterior.
7. Datos y exportación → Exportar JSON o Exportar originales para recuperación. Si una lectura falla, conservar ZIP antes de un reinicio voluntario. El reinicio requiere confirmación en la app.
8. Si falla el guardado, revisar el pendiente y reintentar. No cerrar el proceso antes de conservar una copia si el fallo persiste.

## Identidad visual

Se reutilizan tema, tokens, papel cálido, verdes, Fraunces/Nunito Sans, selector de filtros del afinador y renderer musical existente. El resumen es compartido por juego e historial. Sin nuevos assets, fuentes, audio, bibliotecas ni animaciones. La notación continúa derivada de datos estructurados. La lista, el detalle y los filtros admiten desplazamiento; controles nativos de al menos 48 dp.

Se mantiene la revisión de pantallas de aplicación de `design-taste-frontend` adaptada a Compose y a la identidad obligatoria. La pregunta del historial es qué ejercicio se hizo y cómo resultó; la partitura precede el detalle temporal y los datos técnicos son desplegables. Se conserva el estilo existente; no se sortea otra dirección. Excepciones conscientes frente a las preferencias genéricas de dashboards: marfil/verde, títulos serif y columna móvil. El inspector web no acredita esta interfaz Android.

Se revisaron **40 capturas** de lista, filtros sin coincidencias, detalle de partitura/silencios/metadatos, repetición, fallos y recuperación en 411/360/320 dp y texto 200 %. La app real también comprueba perfil, exportación, reinicio y retorno al menú a 411 dp. La revisión detectó iconos blancos del sistema sobre papel claro: ahora Ritmo, Afinador y Datos solicitan iconos oscuros; el menú conserva los claros sobre el bosque. No se cambian assets. [Capturas y condiciones](imagenes/ritmo-r5/README.md).

## Verificación

La primera tanda de historial falló al no existir todavía las APIs R5; tras implementarlas aprobaron ocho pruebas nuevas y las regresiones de ritmo/almacenamiento. Dos pruebas de exportación/recuperación se vieron fallar por ausencia de ritmo y luego se integraron esas rutas; el primer intento de esa tanda tenía un nombre de propiedad de afinador equivocado, corregido antes de la ejecución roja pertinente.

**163 JVM completas aprobadas**: las 151 anteriores y doce nuevas de historial, ViewModel y exportación. Incluyen revisión por tap tardío sin duplicados, guardado suspendido con revisión nueva, lectura concurrente, filtros, interrupción, repetición, fallos después de escribir, recuperación de originales, lectura de exportaciones anteriores y reinicio. La exportación mantiene los datos del afinador y los casos de resiliencia previos.

**57/57 Android completas** en Pixel_6/API 34 a 411 dp, **670,681 s**. Incluyen los dos casos de interfaz R5 y el recorrido sobre `MainActivity`/perfil real del emulador: pausa, guardado interrumpido, recreación, historial, selector del sistema, escritura en Downloads, comprobación del JSON escrito y reinicio. El archivo exportado conserva exactamente el registro original. Copia de evidencia: `native-export.json`.

Los dos casos de interfaz también aprobaron a **360 dp** (42,867 s), **320 dp** (45,723 s) y **320 dp/texto 200 %** (40,401 s). Esos casos usan audio/reloj controlados y acción accesible de tap; crean un ViewModel nuevo sobre el archivo real para probar reapertura sin regeneración ni inicio automático de audio. No equivalen a una ejecución humana. La suite previa conserva entrada táctil y reproducción/captura reales del emulador.

Lint **0 errores/14 advertencias**, ensamblado de app y pruebas aprobados. La primera ejecución conjunta se cortó después de las JVM por `EXCEPTION_ACCESS_VIOLATION` de JBR 21.0.6/SerialGC en `jvm.dll`. No se atribuye causa raíz a la app, al recolector ni al hardware. Las repeticiones y la entrega aprobaron con un solo worker y SerialGC temporal, sin cambios permanentes en Gradle. Se conserva `hs_err_pid17808.log`.

El primer recorrido de pausa real falló **antes de pausar**: la espera automática de Compose agotó los 20 s de ejecución y dejó una partida completa guardada. Se reutilizó el control explícito de frames de R1 y la observación del estado público; la pausa interrumpida y el guardado aprobaron después. Esto corrige el arnés, sin cambiar el reloj del producto. La revisión usó la skill `diagnose` para distinguir el fallo de prueba de una pérdida de datos.

Evidencia: `Plataforma/build/verification/2026-10-29-R5/summary.json`, resultados/reporte JVM y fuentes relevantes comprimidos, XML/HTML de lint, logs de ensamblado, suite completa y dimensiones, firmas, hash instalado, JSON nativo y capturas. `build-delivery.log` corresponde a la entrega; `android-full-411.log` a las 57 Android; `android-final-*.log` a dimensiones. Los logs/capturas iniciales se conservan como antecedentes, no como evidencia final.

Reproducción desde `Plataforma/`, con Java/SDK como en README:

```powershell
.\gradlew.bat --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
$adb = Join-Path $env:ANDROID_HOME 'platform-tools/adb.exe'
& $adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
& $adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
& $adb -s emulator-5554 shell am instrument -w ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Usar solo un emulador de pruebas: la suite reinicia perfiles. Exigir `OK (57 tests)` en la salida, no solo el código de adb. Para R5, seleccionar `RhythmHistoryAndroidTest` y `RhythmHistoryNavigationAndroidTest` con `-e class`. El caso del selector usa el proveedor de API 34 en Downloads, verificado previamente; genera un nombre único para el JSON de prueba.

Dimensiones: 1080×2400 físicos, densidades 420/480/540 para 411/360/320 dp y `settings put system font_scale 2.0` para ampliar texto. Al finalizar se restauraron densidad 420 y escala 1.0. La captura espera tres vsync y un segundo para el compositor software; esa espera no afecta al producto.

Emulador/relojes controlados no acreditan sincronía acústica, teléfono físico ni eficacia pedagógica. S22 Ultra y revisión de gestos/locución TalkBack pendientes. R6 y D3–D7 siguen pendientes; Personaje permanece pausado.

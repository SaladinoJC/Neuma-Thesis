# Afinador A6 — historial y seguimiento

Implementación local del ticket #14, autorizada por el usuario al solicitar continuar con los pendientes. **0.12.7/código 22, verificada el 2026-10-29**. A1–A5 siguen disponibles; Personaje P0–P7 continúa pausado. Sin push ni cierre de issues.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.7.apk`, 14.876.576 bytes; SHA-256 `7d7515f3176c725e8da34ff3f9b607f97823ba25fab0dfb17eb404af6686398a`. Firma compatible verificada; el APK instalado en el emulador tiene el mismo hash. Instalar con `adb install -r <apk>`, conservando la instalación anterior.

## Recorrido reproducible

1. Abrir Afinador → Guiado u Oído. «Guardar seguimiento» está activado inicialmente; se puede apagar antes de iniciar y se recuerda. Libre no guarda desempeño.
2. Completar un recorrido, detenerlo o salir al menú/segundo plano. Con seguimiento activo se conserva como completo/interrumpido. Sin seguimiento se puede leer el resumen actual, pero no se agrega una sesión ni mediciones al historial.
3. Abrir «Historial del afinador». Oído muestra aciertos/intentos válidos, desvío inicial absoluto medio en cents, desglose por cuerda y clasificación opcional separada. Sin intentos válidos no calcula porcentajes; sin clasificación lo informa.
4. Filtrar por guitarra, afinación con revisión/alturas, criterio o cuerda. «Ver detalle» conserva intento de oído, corrección con ayuda, ajuste guiado, revisión y corrección final como etapas diferentes. Una corrección no transforma un error inicial en acierto.
5. En Instrumento elegir guitarra, afinación, criterio y una etapa. Se comparan objetivos equivalentes entre recorridos; ajustar guiado y corregir son mediciones posteriores al ajuste, no un estado previo del instrumento. La app no atribuye causas mecánicas a las diferencias.
6. Cambiar guitarra o editar una afinación personalizada. Los recorridos anteriores conservan nombre, alturas, revisión y opciones con que se iniciaron.
7. Menú → Más → Datos de demostración: exportar JSON, recuperar originales ZIP o reiniciar. Un JSON ilegible se conserva; usar recuperación antes de reiniciar si se necesitan sus bytes originales.

## Datos y límites

- `tuner-results.json`: documento versión 1, IDs UUID por recorrido y reemplazo atómico bajo la barrera de perfil existente. Reintentar no duplica un ID, incluso si una escritura se confirmó en disco antes de informar un fallo.
- Copia versionada del instrumento/afinación/criterio, modo, revisión final, clasificación, referencia inicial y referencias solicitadas durante el recorrido. Se conservan tolerancia, estabilidad, continuidad, versión de comprobación e información de configuración YIN; no son mediciones de precisión acústica.
- Solo se guardan resultados sellados por etapa, sin PCM ni serie continua de mediciones ocultas. Una interrupción sin resultados válidos puede guardar un recorrido vacío, identificado explícitamente. Las lecturas inválidas no suman intentos de oído.
- El desvío medio usa valores absolutos de los intentos iniciales válidos; el detalle conserva el signo. Las correcciones no entran en ese denominador. La clasificación usa su propia lectura previa y se cuenta aparte.
- La clasificación se guarda al revelar la comprobación asociada. Abandonar antes no produce un resultado sellado de esa cuerda. Su `atMillis` conserva el reloj monótono de la captura; las fechas civiles del recorrido son `startedAtMillis`/`finishedAtMillis`.
- Un fallo de guardado deja aviso, reintento y copia pendiente en memoria. Puede incluirse en exportación/recuperación; cerrar forzosamente el proceso puede perder esa copia aún no guardada. Apagar seguimiento afecta nuevos recorridos, no elimina los anteriores. El reinicio explícito elimina también pendientes en memoria.
- Exportación versión 1 ampliada con campos opcionales `tuner` y `pendingTunerSaves`; los documentos anteriores se leen con listas vacías. Los consumidores antiguos deben ignorar campos nuevos. ZIP incluye los seis JSON conocidos que existan y pendientes separados, sin interpretar originales dañados.

## Verificación

**140 JVM aprobadas**, incluidos seguimiento apagado, copia de afinación, varias guitarras, fallo después de escribir, reintento, etapas, filtros, exportación, lectura de datos previos, recuperación y reinicio. Una regresión red→green comprueba que una carga suspendida no borre una sesión que termina ni un filtro que cambia durante la lectura.

**50/50 Android completas** en Pixel_6/API 34, 527,35 s. Incluye los dos casos nuevos A6: seis cuerdas/guardar/consultar/filtrar/detalle/reabrir/apagar seguimiento y fallo/reintento. Ambos también aprobaron a **360 dp** y **320 dp/texto 200 %**. Los datos de afinación se inyectan como estimaciones sintéticas; los otros recorridos conservan las condiciones de sus pruebas. [Capturas y dimensiones](imagenes/afinador-a6/README.md).

Lint: **0 errores/14 advertencias**. Una ejecución se cortó por `EXCEPTION_ACCESS_VIOLATION` de JBR/G1 y otra falló dentro del PSI de lint al analizar `RhythmR2Test.kt`. Repetir lint con SerialGC temporal aprobó; causa raíz sin confirmar y sin cambio permanente en Gradle. System UI mostró un ANR al arrancar el emulador; se cerró y estabilizó antes de las capturas válidas. La prueba de interfaz inicial falló por no existir todavía el control; un recorrido posterior necesitó localizar el elemento de lista mediante scroll.

Evidencia: `Plataforma/build/verification/2026-10-29-A6/summary.json`, ZIP de 140 resultados JVM y reporte, XML/HTML de lint, logs Android por dimensión/completo y registro de la caída de JVM. Compilación y ensamblados aprobados; el cierre de la ejecución conjunta falló solo en lint y la repetición independiente aprobó.

Reproducción desde `Plataforma/`, con Java/SDK configurados como en README:

```powershell
.\gradlew.bat --no-daemon --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
$adb = Join-Path $env:ANDROID_HOME 'platform-tools/adb.exe'
& $adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
& $adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
& $adb -s emulator-5554 shell am instrument -w ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Usar un emulador: las pruebas reinician datos. Para repetir solo A6, añadir `-e class ar.edu.unmdp.entrenamiento.TunerHistoryAndroidTest` antes del runner. Los logs deben terminar en `OK`; el código de salida de adb por sí solo no confirma éxito.

No se acredita guitarra física, precisión Pro, latencia acústica ni eficacia pedagógica. S22 Ultra y revisión auditiva/táctil con TalkBack siguen pendientes.

## Identidad visual

Se reutilizan papel, verdes, Fraunces/Nunito Sans y tokens existentes; tema compartido por afinador, historial y datos. No se agregan assets, dependencias ni animaciones. La comparación textual prioriza etapas y unidades; no hay datos de demostración inventados en la pantalla de producto. Las listas admiten desplazamiento y cargan recorridos en bloques de 20.

La skill de interfaz seleccionó `accent-ladder` conservando paleta y tipografías. Adaptación consciente a Compose móvil: una columna nativa en lugar de la grilla web; controles táctiles de al menos 48 dp, estado textual y sin gráficos innecesarios. El inspector web no verifica Compose: la comprobación usa UI Android y capturas.

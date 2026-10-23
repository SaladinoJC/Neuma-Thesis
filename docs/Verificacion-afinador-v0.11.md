# Verificación del afinador A1 / v0.11

Fecha: 2026-10-23. Alcance: [#9](https://github.com/SaladinoJC/Tesis/issues/9), sobre la base local v0.10. Reglas de sesión independientes de Android, captura compartida sin reutilizar guardado de voz y pantalla Libre accesible desde el menú.

## JVM y PCM

68 pruebas JVM aprobadas, incluyendo cinco nuevas: seguimiento/fijación/octavas, márgenes inclusivos Normal/Pro y continuidad, cromático/notas sin cuerda, PCM y ciclo de captura con reloj controlado. La primera prueba de sesión falló por ausencia de `TunerSession` y pasó tras implementarla.

PCM: ventana 4.096, 44.100 Hz, fundamental de amplitud 0,3 más segundo armónico de amplitud 0,1, sin ruido añadido. Frecuencias nominales: 82,406889 / 110 / 146,832384 / 195,997718 / 246,941651 / 329,627557 Hz, más 164,813778 Hz como octava de sexta. Error máximo observado: 0,03694 cents; notas/octavas esperadas conservadas. Silencio y ruido uniforme de semilla 93 rechazados. Son resultados sintéticos bajo estas condiciones, sin acreditar exactitud sobre guitarra real. Las fronteras de los márgenes se verificaron con lecturas controladas, independientemente del error del estimador.

La prueba de captura sustituye únicamente micrófono/reloj: comprueba estabilidad, pérdida de muestras, detención, liberación y reinicio explícito sin evidencia heredada. No hay repositorio de resultados en el afinador A1.

## Compilación y entorno

Compilación de app y APK de pruebas completadas. Lint: cero errores y 14 advertencias. La primera verificación cayó dentro de `jvm.dll` con `EXCEPTION_ACCESS_VIOLATION`; el registro se preservó en `Plataforma/build/verification/afinador-a1/hs_err_pid3244.log`. Las ejecuciones posteriores usaron un worker y heap de 768–1.024 MiB, compilación Kotlin en proceso en el último APK de pruebas.

El primer recorrido API 34 se abortó por avisos de ANR de Android System UI/system que impedían el foco de la actividad. Se preservaron resultados fallidos y capturas del entorno. Un intento de abrir API 36 no comenzó porque su AVD estaba ocupado; no se probó API 36 en esta sesión. Revocar permisos desde la instrumentación activa también interrumpía el proceso: se retiró ese procedimiento del test y se separó de la prueba de captura.

## Android y recorrido manual

Ejecución final en Pixel_6/API 34 (Android 14), una vez cerrados los avisos del sistema: **14/14 pruebas aprobadas**, en 251,794 segundos. Dos del afinador, dos de referencia de voz, siete de Simón y tres de instrumentos. Salida completa: `Plataforma/build/verification/afinador-a1/android-final.log`, con `OK (14 tests)`.

Afinador: captura AudioRecord real del emulador a 44.100 Hz y reapertura después de cancelar/liberar; selección Pro y sexta cuerda; silencio sin afinación correcta; recreación, pausa/reanudación y salida/reentrada con micrófono detenido; archivo de historial de voz sin cambios. La señal del micrófono virtual en silencio no valida captación de guitarra física. Los flujos anteriores recorren preparación, partidas, persistencia y reproducción de los bancos reales.

Permiso rechazado comprobado por separado al terminar la instrumentación: revocar mediante `adb`, abrir la app, entrar a Afinador, pulsar Iniciar, rechazar el diálogo Android. Se observó «Micrófono detenido», ausencia de tono y aviso «Micrófono no autorizado. Podés habilitarlo en Ajustes de Android.». XML/captura preservados como `permission-dialog.xml`, `permission-denied.xml` y `permission-denied.png`. La pantalla Libre también se inspeccionó visualmente en `tuner.png`. Esta comprobación no se cuenta como otro test automatizado.

## Artefacto y reproducción

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.11.apk`, 12.193.977 bytes.

SHA-256: `f4b77caa95e7bb937a575ac12cf7f36fe50ea55f017b4b3e7563537fcb4a45d0`.

Informes JVM, lint y logs Android conservados en `Plataforma/build/verification/afinador-a1/`. Mismo paquete y firma debug; instalar encima sin borrar datos. Guía y prueba física pendiente: [Demostración](Demostracion-afinador-v0.11.md).

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SERIAL = 'emulator-5554'
Set-Location Plataforma
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --max-workers=1
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.TunerAndroidTest'
```

Si se ejecuta instrumentación directamente para reducir carga, instalar ambos APK con `adb -s emulator-5554 install -r` y usar `adb -s emulator-5554 shell am instrument -w -e class ar.edu.unmdp.entrenamiento.TunerAndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner`. Conservar salida y comprobar `OK`; el código de salida de `adb` por sí solo no acredita aprobación.

No se probó S22 Ultra, guitarra física ni precisión Pro, latencia acústica o eficacia pedagógica. No se modificó el protocolo, se hizo push ni se cerró el ticket.

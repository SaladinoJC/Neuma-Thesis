# Afinador A4 — referencias sonoras

Entrega local **0.12.4 / código 19**, ticket #12. Autorizada por el usuario el 2026-10-26; rama `trabajo/afinador-a4`, sobre A3/R2/D2 verificados. Sin push ni cambios en GitHub Issues.

## Recorrido para el teléfono

Instalar `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.4.apk` encima de la versión anterior, sin desinstalar, para conservar datos. Mantiene el paquete `ar.edu.unmdp.entrenamiento` y firma debug.

1. Abrir Afinador en Libre, fijar una cuerda y pulsar **Escuchar**. Puede reproducirse sin activar ni autorizar el micrófono.
2. Comparar **Guitarra**, **Piano** y **Tono puro**. Guitarra es eléctrica limpia, grabada directamente; no imita la guitarra personal. Cerrar/reabrir y comprobar que se recuerda el timbre.
3. Con micrófono activo, repetir Escuchar: la captura se suspende, la lectura se limpia y la salida no se evalúa. Al terminar se reabre automáticamente solo si estaba activo; la estabilidad vuelve a comenzar desde cero.
4. En Guiado, completar una cuerda y repetir referencia en la siguiente: conserva fase, objetivo y resultados previos. Completar la pasada y revisión como en A3.
5. Durante reproducción o espera, pulsar **Cancelar**, salir al menú o enviar la app al fondo: no debe volver a iniciar solo. Después, el micrófono se inicia explícitamente.
6. Probar los seis presets, especialmente Do2 de Drop C. En personalizadas cubre Do2–Mi4. En cromático, una nota fuera de ese rango no tiene referencia: no se cambia la octava.

No se añadió entrenamiento A5 ni seguimiento A6. Los resultados guiados continúan en memoria; se guarda configuración, no grabaciones ni desempeño.

## Audio y condiciones

Banco independiente de 58 OGG mono/44,1 kHz/1200 ms; tono puro de 600 ms. Fuentes, permisos, transformaciones, medición y hashes en [Audio-referencias-afinador-A4.md](Audio-referencias-afinador-A4.md) y el manifiesto de assets. Máximo observado en archivos: 0,831 cents con autocorrelación guiada, que no equivale a precisión física. Margen de salida de **600 ms provisional**, además de esperar frames y liberar AudioTrack; la nueva captura crea otro AudioRecord y otra ventana YIN.

## Verificación reproducible

Desde `Plataforma/`, con JDK/SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest '-Dorg.gradle.jvmargs=-Xmx2048m -XX:+UseSerialGC -Dfile.encoding=UTF-8'
python tools/prepare_tuner_references.py --verify
```

Solo sobre un **emulador de pruebas**, nunca el perfil del teléfono:

```powershell
adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s emulator-5554 install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb -s emulator-5554 shell pm grant ar.edu.unmdp.entrenamiento android.permission.RECORD_AUDIO
adb -s emulator-5554 shell am instrument -w -r -e class ar.edu.unmdp.entrenamiento.TunerReferenceAndroidTest,ar.edu.unmdp.entrenamiento.TunerAndroidTest,ar.edu.unmdp.entrenamiento.GuidedTunerAndroidTest,ar.edu.unmdp.entrenamiento.GuidedTunerLifecycleAndroidTest,ar.edu.unmdp.entrenamiento.TunerConfigurationAndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner
```

Evidencia local en `Plataforma/build/verification/2026-10-26-A4/`. El primer ciclo JVM falló por ausencia de A4; después aprobaron las pruebas del afinador. El primer build completo pasó compilación/JVM/paquetes pero lint detectó `SuspiciousIndentation` con posiciones inconsistentes en un archivo de saltos de línea mezclados; normalizar los archivos Kotlin modificados a LF eliminó el error, sin desactivar reglas. El siguiente build completo aprobó. Se agregó luego Cancelar junto a Escuchar para dejar la salida a mano durante reproducción, con verificación final de esa modificación.

La primera ejecución Android aprobó **2/2** en Pixel_6/API 34, 71,002 s: captura real liberada antes de salida, reanudación tras al menos 600 ms y cancelación sin reinicio; decodificación de 58 OGG y YIN en tres ventanas por archivo, sin cambiar octavas, máximo **4,386 cents** con YIN, y reproducción real de los tres timbres en Do2/Mi2/Mi4, 52.920 o 26.460 frames. El margen de 5 cents del control YIN de muestras no se confunde con el criterio Pro de 2 cents; el método de preparación y YIN no producen medidas idénticas.

La pasada posterior detectó una carrera introducida al proteger el desbloqueo de controles: con guardado inmediato del timbre, `Dispatchers.Main.immediate` podía completar el trabajo antes de asignarlo a `configurationJob` y dejar `configurationBusy` activo. Se reprodujo en JVM con `UnconfinedTestDispatcher` (6 pruebas, 1 fallo) y se corrigió creando el trabajo en modo `LAZY`, asignándolo antes de iniciarlo, como las otras operaciones de configuración. La regresión comprueba que cambiar timbre libera controles y conserva fase, cuerda y captura guiadas. No se aumentó el timeout para ocultar el fallo.

La instrumentación conjunta intermedia dio 5/7: además de ese fallo, `GuidedTunerAndroidTest` conservaba bytecode incremental contra el constructor previo de `TunerViewModel` (`NoSuchMethodError`, firma sin el reproductor de referencias). Se repitió con `:app:clean` y reconstrucción de app y pruebas para comprobar las fuentes actuales juntas; no se alteró el test guiado para resolverlo.

**Resultado final:** reconstrucción limpia de las 81 tareas en **1 min 42 s**, **122 JVM / 27 clases aprobadas**, lint **0 errores / 14 advertencias**, APK y pruebas Android generados. En Pixel_6/API 34 a **360 dp**, siete pruebas distintas aprobadas: la tanda completa dio 6/7 en 90,435 s; la prueba antigua de captura suponía que Pro y la sexta cuerda estaban visibles sin desplazar. Se agregó `performScrollTo()` antes de esos dos clics y el caso aprobó en **13,505 s**, conservando todas sus aserciones. La prueba de A4 a 360 dp, la verificación YIN/reproducción, las dos pasadas, el ciclo de vida y la configuración persistida aprobaron en la tanda. No se ejecutó toda la suite Android.

APK archivado: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.4.apk`, **13.189.030 bytes**, SHA-256 `037accd324d6a74ffb6f1d8d30a2d0bf2a9e04ace949117cf914eb34c4c8ee96`. Certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`, idéntico al APK 0.12.3, verificado con `apksigner verify --print-certs`. El último ajuste fue exclusivamente de desplazamiento en una prueba Android; no cambió el APK de la app. Capturas de controles reales en `docs/imagenes/afinador-a4/360dp/` y `411dp/`.

La prueba de captura/referencia/cancelación se repitió a **411 dp** y aprobó en **10,227 s**, con capturas nuevas inspeccionadas en ambos anchos. Los tres timbres y Cancelar caben a 360 dp; se conserva la pantalla desplazable previa del afinador. Se usó la skill de interfaz para añadir estos controles al estilo existente; el inspector web no corresponde a esta aplicación Compose nativa. Informes JVM/lint comprimidos y resumen de artefacto en la carpeta de evidencia. Emulador propio detenido al cerrar.

## Pendiente físico

S22 Ultra con versión de Android, guitarra, afinación, distancia, entorno, volumen y ruta de audio registrados. Comparar audibilidad de Do2–Mi4, ataque, equilibrio de volumen y comportamiento después de escuchar; repetir con altavoz y auriculares/Bluetooth si se usan. Si el sonido residual se sigue detectando al reanudar, medir antes de ajustar el margen. Usar una fuente patrón para valorar precisión: una prueba funcional no demuestra ±2 cents sobre guitarra real, latencia acústica ni eficacia pedagógica. No se ejecuta API 36 ni se da por realizada la prueba del usuario.

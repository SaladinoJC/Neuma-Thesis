# Defender D2 / #24 — práctica ampliada

Continuación individual autorizada por el usuario el 2026-10-26 mediante «dale, vamos con eso». Rama local `trabajo/defender-d2-continuacion`, sobre A3/R2 verificados y el análisis documental de Glisso. Se recuperaron únicamente los cuatro checkpoints D2 (`f2ea44c`, `0db1ddb`, `b2b496f`, `7cba850`). La integración triple v0.13 no se utiliza como entrega validada. Versión 0.12.3/código 18, sin cambios externos en GitHub.

## Comportamiento

- Preparación: N1 sol Mi4–Fa5; N2 sol La3–Do6; N3 fa Do2–Mi4; N4 mezcla de los dos rangos ampliados. Hasta dos líneas adicionales. Todas son prácticas; no hay desbloqueos, DDA ni historial de Defender todavía (D3/#25).
- Alteraciones simples opcionales e independientes: natural, bemol y sostenido. Se responde por nombre y alteración escritos, sin octava; un enarmónico con otra escritura no es válido. La respuesta comienza en natural; acertar limpia la selección y vuelve a natural, fallar conserva ambas partes sin quitar vida. Nombres Do–Si o C–B.
- Cupo configurable 1–4, predeterminado de interfaz 4; solo aparecen enemigos cuando hay espacio reservado para la trayectoria completa. Una oportunidad ocupada se descarta sin acumular ráfagas. La configuración sin argumentos de la sesión conserva cupo 1 para reproducir D1.
- Duplicados permitidos: se elimina uno por disparo, según distancia geométrica normalizada a la frontera de base x=0,25–0,75/y=0,84, con desempate por aparición/ID. El banco de distintas velocidades prueba matching, sin introducir DDA en la práctica.
- Tres vidas; solo las llegadas quitan una. Viaje fijo 10 s, curva de aparición 4→2,5 s y 100 puntos por acierto son parámetros experimentales. Pausa oculta objetivos y congela tiempo/audio; el tercer impacto termina y cancela los enemigos restantes sin contarlos como llegadas.
- Resumen en memoria: etapa, clave/rango y opción de alteraciones; revisión por nota y octava, grafía en ambos sistemas de nombres, clave, alteración y eliminado/llegada. No sobrevive a cerrar el proceso. Los cancelados no se presentan como pérdidas.

## Audio

Se conserva la síntesis propia `referenceTone`: seno de 600 ms, PCM16 mono/44,1 kHz, amplitud 0,2, fades de 20 ms y La4=440 Hz/temperamento igual. El corpus completo con alteraciones abarca MIDI 35–85, dentro del rango 24–96 admitido. No se incorporan recursos de terceros ni se cambia de octava para suplir muestras. Sonido de nota al acertar y efectos independientes; cancelación/liberación conservadas. El avance de frames de AudioTrack acredita reproducción en emulador, no audibilidad o latencia física.

## Repetir la verificación

Desde `Plataforma/`, con JDK y SDK configurados:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -XX:+UseSerialGC -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Usar únicamente un emulador de pruebas: la integración reinicia su perfil. Instalar ambos APK, desbloquear la pantalla y conceder `android.permission.RECORD_AUDIO` antes de los recorridos de afinador. Ejecutar `am instrument` con serial explícito y las clases `DefenderAndroidTest`, `ParallelFeaturesAndroidTest`, `GuidedTunerAndroidTest`, `GuidedTunerLifecycleAndroidTest`, `RhythmAndroidTest` y `RhythmR2AndroidTest`, todas bajo `ar.edu.unmdp.entrenamiento`, runner `ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner`.

Para comprobar el ancho de 360 dp en Pixel_6/API 34/densidad 420: `adb -s emulator-5554 shell wm size 945x2100`; repetir cuatro enemigos, controles alterados y resumen. Restaurar con `wm size reset`. Las capturas se exportan a `/sdcard/Android/data/ar.edu.unmdp.entrenamiento/files/defender-d2/`.

## Recorrido físico pendiente

Instalar v0.12.3 encima de la anterior sin desinstalar. Desde Defender, elegir cada etapa y probar naturales y alteraciones; revisar extremos con dos líneas adicionales en ambas claves, controles Do–Si/C–B y legibilidad con cuatro enemigos. Acertar, fallar sin perder vida, comprobar selección/natural, pausar/reanudar y perder tres vidas; revisar soluciones y resumen. Salir al menú y volver: ofrece preparación, sin un desbloqueo ficticio. Comprobar volumen multimedia, sonido/efectos por separado y que el menú conserve los módulos y datos anteriores.

Registrar modelo, Android, versión, ruta de audio y condiciones en S22 Ultra. No se midió latencia acústica ni se calibró eficacia pedagógica.

## Verificación 2026-10-26

- **116 JVM en 26 clases aprobadas**, cero fallos/errores/omitidas. Incluye 19 de Defender: corpus de extremos/grafías/claves, matching, reservas futuras, llegadas/cancelación, selección y reloj.
- **Lint: 0 errores, 14 advertencias**; app y APK de pruebas compilados. Build final completo: **5 min 01 s**, un worker, heap 768 MiB e in-process. Se conserva el aviso anterior de nulabilidad en `PitchLabSessionTest`, sin error de compilación.
- **15 Android diferentes aprobadas en Pixel_6/API 34**, con 2 GiB de RAM configurados. Siete de Defender: cuatro enemigos mixtos/alterados y pausa, controles táctiles C–B/alteraciones, resumen y tres pérdidas, audio de extremos y efectos, acierto por pulsación y partida completa. Otras ocho conservan guiado/recreación del afinador, R1/R2/audio y navegación/ciclo de vida/compatibilidad de datos.
- Primera tanda final: **12/15 en 187,706 s**; las tres fallidas esperaban foco. Se comprobó con `dumpsys window` y captura que `com.android.systemui` tenía un diálogo ANR. Cerrado el aviso, **las tres afectadas aprobaron en 71,649 s**, sin cambiar el APK ni sus aserciones. No se presenta la primera tanda como totalmente aprobada.
- A **360 dp (945×2100 px, densidad 420)** se repitieron cuatro enemigos, controles y resumen: **3/3 en 16,639 s**, sin cambiar APK. Son repeticiones de pruebas ya contadas, no tres pruebas diferentes adicionales. Pantalla restaurada y emulador detenido al terminar.
- Antes de corregir el resumen se ejecutaron seis Android: **5/6**, con fallo real por ausencia de «N3 · Fa ampliada»; el mismo test pasó después de agregar etapa/rango/alteraciones. Ese rojo se conserva por separado.

Evidencia local: `Plataforma/build/verification/2026-10-26-D2/`, con `summary.json`, logs de compilación/instrumentación, resultados JVM, lint, crash de JVM y captura del ANR. Primer build interrumpido por `EXCEPTION_ACCESS_VIOLATION` nativa en un thread G1 de JBR 21.0.6, antes de correr pruebas. Cambiar solo el recolector a `UseSerialGC` permitió el primer build completo (6 min 54 s); el build final volvió a aprobar con ese comando. Es un workaround local, sin atribuir corrección del runtime ni cambiar `gradle.properties`.

Revisión con `design-taste-frontend`, adaptada a Kotlin/Compose nativo: se conservan referencia espacial, paleta, tipografía y controles; la pregunta principal es qué nota responder antes de llegar a base. No se aplica el inspector HTML/Chrome. Se inspeccionaron cuatro enemigos, controles y resúmenes a ambos anchos, además de extremos de sol/fa con alteraciones (renderer sin cambios entre primer y último build). Pentagramas/naves comprobados dentro del campo y sin intersección en el recorrido controlado; controles visibles y revisión desplazable. [Cuatro enemigos a 411 dp](imagenes/defender-d2/411dp/d2-cuatro-mixta.png), [a 360 dp](imagenes/defender-d2/360dp/d2-cuatro-mixta.png) y [resumen](imagenes/defender-d2/360dp/d2-resumen-fa-alteraciones.png). La comodidad física y escalas de fuente distintas siguen sin medirse.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.3.apk`, **12.472.627 bytes**, SHA-256 `C9A6E9FC0DAA7B090877194E68C9063381157C02DEC7C486CAF6439A831C6701`. Certificado SHA-256 igual al de v0.12.2, comprobado con `apksigner`: permite actualización encima sin desinstalar.

No se ejecutó toda la suite Android ni API 36; no se probó S22 Ultra ni se midió latencia acústica o eficacia pedagógica. A3/R2 se conservan, sin avanzar A4/R3/D3 ni las propuestas de Glisso.

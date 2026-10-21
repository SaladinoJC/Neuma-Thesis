# Verificación de iteraciones posteriores a v0.5

Autorización: el usuario pidió el 2026-10-21 «Dale, ponete con lo que puedas», sobre `Plan-iteraciones-post-v0.5.md`. Trabajo local, sin publicación externa. Teléfono físico pendiente.

Las rutas de APK por hito registran las salidas generadas en ese momento; Gradle puede reemplazar el directorio de salidas. Los checkpoints Git permiten reconstruir cada versión. La entrega v0.10 conserva además una copia estable en `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.10.apk` y copias por ejecución de verificación.

## v0.5.1 — Operaciones y progreso

- Snackbar con consumo por identificador; la última operación permanece consultable. Exportación cancelada, fallida e interrumpida se distinguen; sin instantánea no se abre ni escribe el destino. Se conserva la espera mientras el selector está abierto y se impiden callbacks duplicados durante escritura.
- Preparación/resumen muestran próximo nivel y partidas restantes; último nivel explícito. Reglas y configuraciones DDA conservadas.
- Regresión roja→verde de devolución del selector sin instantánea. 46 pruebas JVM y tres Android de voz/datos aprobadas en Pixel_6/API 34; compilación y APK completados en 1 min 13 s. Pruebas nuevas de guardado, fallo de destino, cancelación, consumo de aviso y reintento.
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.1.apk`: 11997310 bytes, SHA-256 `d87932ec9eb29dc5a17f948e43ec29c41dc7c36d3f74d111017e0f49a239d86b`.
- No se probó el S22 Ultra ni se acredita eficacia pedagógica. La inspección visual final y regresión global se realizarán sobre la entrega integrada.

## v0.6 — Historial completo

- Pantalla de resultados con filtros por juego/nivel/origen y presentación en bloques de 20, sin borrar registros. Detalle de secuencia/error de Simón, melodías/respuestas/escuchas y trazas DDA de instrumentos, configuración y resumen de voz. Los datos históricos ausentes se identifican; simulaciones excluidas.
- Voz: estimaciones válidas, desviación media/mediana absoluta respecto del objetivo y procesamiento p50/p95; no se presentan como precisión sobre canto real ni latencia acústica.
- Regresión roja→verde del acceso paginado a 26 resultados y filtros. 48 pruebas JVM aprobadas; una Android nueva comprueba paginación, detalle conservado al recrear y cambio a historial vacío en Pixel_6/API 34. Ejecución 40 s, APK generado. Un historial ilegible no oculta los de otros componentes.
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.6.apk`: 12062768 bytes, SHA-256 `6d4db3033bf4bacd670763c27536024686e52c619b8ca1c5d99afd87194c7579`.

## v0.7 — Laboratorio de tono

- 83 casos/332 ventanas por ejecución, sin micrófono ni reproducción: senoidales 55–1.000 Hz/amplitudes/fases, armónicos, ruido, silencio/DC/PCM inválido y transición nota–silencio–nota. JSON separa `syntheticPitchTests`; reinicio incluye su archivo y el informe Markdown es exportable.
- Generación: seno `A sin(2π f n/fs + fase)`; armónicos `(sin θ + 0,5 sin 2θ + 0,25 sin 3θ)/1,75`. Ruido uniforme con media retirada y RMS ajustado al SNR objetivo; semilla 42+índice de ventana. Si el pico supera 0,95 se aplica ganancia común conservada por ventana. No se guardan buffers. Errores de octava: múltiplo entero no nulo de 1.200 cents con tolerancia de 50, solo clasificación técnica.
- 51 JVM y una Android nueva aprobadas en Pixel_6/API 34; banco completo, persistencia/exportación separada y recreación verificados; APK generado en 45 s. Cancelación sin informe parcial y repetibilidad numérica comprobadas.
- [Informe medido en JVM](Laboratorio-YIN-v0.7-JVM.md): los criterios obligatorios pasan. A SNR 10 dB se observaron errores máximos cercanos a 96 cents en un caso; a 0 dB las ventanas de prueba se rechazaron. Es caracterización de esas señales, no una medición de voz humana. Se conservó `yin-v1` sin recalibrar para ocultar límites.
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.7.apk`: 12347825 bytes, SHA-256 `90d13fe89a9674a910433d8a79397f3735cd063a5a65be9a66b39a5ac8b29cc7`.

## v0.8 — Referencia sonora y gráfica de voz

- Tono objetivo sintetizado (44,1 kHz/600 ms, fades 20 ms, amplitud 0,2), sin nuevos assets. Detiene y espera captura/guardado antes de emitir; iniciar micrófono queda deshabilitado durante reproducción y no se reinicia automáticamente. Pausa/salida cancelan ambos recursos.
- Indicador de cents y diez segundos de traza derivada, sin suavizado; silencio corta líneas y marcador actual, números sin recorte y gráfica acotada a ±100. Resumen de última captura conservado; detalles técnicos desplegables. PCM de referencia solo en memoria.
- 54 JVM aprobadas: tono/frecuencia/duración, exclusión de captura/referencia, buffer temporal y silencio. Tres Android anteriores de voz/datos pasaron; dos nuevos de referencia/UI detectaron la validación prematura del estado de AudioTrack estático, corregida con carga antes de validación. Esos dos pasaron al reejecutar (38 s), con avance ≥26.460 frames para Do3/La4. No acredita audibilidad en teléfono.
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.8.apk`: 12174168 bytes, SHA-256 `79fbbad1774f9c08ca29bbc65d3f8c9330d7b2676bf0766f8bd5c4e2ed06fd88`.

Límite del laboratorio v0.7: los casos exactos de 1.000 Hz se rechazan con la configuración actual por el control del límite superior de la frecuencia estimada. El informe lo expone; no se modificó el detector para forzar aprobación. Los objetivos vocales actuales están entre Do3 y La4 y la calibración física sigue pendiente.

## v0.9 — Banco DDA

- Nueve escenarios, 56 pasos simulados con antes/después, 93 fronteras de dominio y 21 comprobaciones aprobadas. Incluye ayuda, errores, alternancia, interrupciones, manual entre automáticas, último nivel, duplicación y ausencia de salida de ayuda. Simón no tiene reescuchas: en el escenario de ayuda se demuestra su práctica manual, identificado en los pasos.
- Datos aislados y origen `simulated`, configuración y pertenencias inspeccionables; exportación opcional `ddaScenarios` separada del perfil. Se mantienen las reglas de producción. Las fronteras describen la calificación actual, no validación pedagógica.
- 55 JVM y una Android del banco/recreación aprobadas en Pixel_6/API 34; APK generado en 53 s. [Informe reproducible](Banco-DDA-v0.9.md).
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.9.apk`: 12161213 bytes, SHA-256 `a12f18da4a3c0d7734d5ba0376e95c0437d40bc54c1e780c0abd8b22648b4207`.


## v0.10 — Resistencia de datos y entrega

- Se reprodujo un fallo de escrituras concurrentes entre repositorios sobre el mismo archivo (AccessDeniedException en Windows y riesgo de sobrescribir resultados). La barrera ahora es compartida por directorio: 30 guardados concurrentes desde instancias distintas conservan sus 30 identificadores. Exportación y reinicio usan esa misma barrera; una generación invalida escrituras anteriores que todavía estaban en cola.
- Se conservan JSON truncados y versiones desconocidas; fallar al leer o guardar no sustituye el original. El juego afectado bloquea nuevas partidas y permite reintentar lectura; los demás componentes y el menú siguen accesibles. Se corrigieron el aviso que podía tapar controles y la salida al menú que dependía de una lectura exitosa.
- Recuperación ZIP mediante selector Android: copia los cuatro archivos conocidos sin parsearlos. No repara ni importa y no incluye PCM. Las mediciones pendientes de voz se incluyen, separadas, como `pending-voice-save.json`; la exportación normal las identifica en `pendingVoiceSave`. La captura con fallo de guardado conserva su identificador, se puede reintentar sin duplicación y bloquea otra captura hasta resolver el pendiente. Ese pendiente vive solo en memoria y no sobrevive a la muerte del proceso.
- El reinicio confirmado cancela recursos y descarta sesiones activas antes de borrar: una salida posterior no vuelve a guardar esas sesiones. Se conserva el marcador para completar un reinicio interrumpido. La recuperación del marcador se coordina con la misma barrera. No se cambian reglas DDA, parámetros YIN ni formatos de resultados anteriores.
- `Plataforma/tools/verify-prototype.ps1` valida JDK/SDK y serial de emulador, ejecuta JVM/lint/APK/Android, conserva log e informes comprimidos por ejecución y genera un resumen con APK/tamaño/SHA-256. Rechaza teléfonos físicos porque las pruebas reinician el perfil. El primer intento reveló el límite de longitud de rutas al copiar logs en Windows: se cambió el archivado a ZIP.
- 63 pruebas JVM aprobadas, incluidas conservación de originales, concurrencia, deduplicación, reintento de voz, bloqueo/relectura y descarte para reinicio. La prueba Android de corrupción/selector cancelado/reinicio pasó tras corregir navegación. La primera integración incompleta se conserva como antecedente de diagnóstico; la aceptación final se registra a continuación.


Verificación de API 34: `verify-prototype.ps1 -Serial emulator-5554`, 6 min 56 s, 63 JVM y 22 Android aprobadas; lint 0 errores/14 advertencias. Informes y APK archivados en `Plataforma/build/verification/20261021-111104-486/`. Pixel_6, 1080×2400/densidad 420 (aprox. 411 dp de ancho); recreación, reproducción/captura nativas, pausa/salida, historial, bancos, exportación cancelada y recuperación/reinicio comprobados.

Primer recorrido API 36: Medium_Phone_API_36.0, 1080×2400/densidad 480 (360 dp), 21/22 casos aprobados. Falló únicamente la espera de 15 s del primer selector externo, coincidiendo con el arranque frío del AVD y sus proveedores. Se amplió a 30 s esa espera de comprobación, sin modificar conducta de la app ni criterios de audio. Antecedente archivado en `Plataforma/build/verification/20261021-112003-077/`; se repite la suite completa. Se ajustó además el manejo de stderr nativo en PowerShell para conservar un código de salida explícito en los fallos de Gradle.


**Aceptación final API 36:** `verify-prototype.ps1 -Serial emulator-5556`, 5 min 34 s, **22/22 Android aprobadas** con densidad 480/360 dp; mismos 63 resultados JVM aprobados y lint 0 errores/14 advertencias. Evidencia en `Plataforma/build/verification/20261021-112922-923/`. Se comprobó además el modo `-SkipAndroid` del comando actualizado (sin instalar): `Plataforma/build/verification/20261021-113549-952/`. Todos produjeron el mismo APK.

Advertencias restantes de lint: una de Gradle/AGP, siete `GradleDependency`, tres `NewerVersionAvailable`, una de orientación bloqueada, una `DiscouragedApi` y una `DataExtractionRules`. También aparece el aviso de deprecaciones de Gradle previas a Gradle 9. No son errores y no se actualizaron dependencias fuera de alcance.

**Comprobación manual con selector real en API 36/360 dp:** permiso concedido al micrófono virtual del emulador, captura detenida y guardada; JSON exportado a Downloads y leído externamente con Python. 2.801.025 bytes, esquema 1/app 0.10, una captura/34 mediciones, cero estimaciones válidas en ese entorno de silencio, 44.100 Hz y error nulo; banco DDA separado con nueve escenarios/21 comprobaciones aprobadas. No hay buffers PCM. ZIP de recuperación guardado por el selector: 1.566 bytes, `voice-results.json` + `LEEME.txt`; integridad ZIP correcta y los 7.914 bytes del JSON de voz coinciden exactamente con el original privado y con los resultados del JSON exportado. Las copias de diagnóstico están en `Plataforma/build/verification-v010/`, sin agregarse al repositorio como datos de participantes.

Revisión visual: [menú](imagenes/prototipo-v0.10/menu-api36.png), [voz sin tono válido](imagenes/prototipo-v0.10/voz-api36.png), [confirmación JSON](imagenes/prototipo-v0.10/exportacion-api36.png) y [confirmación de recuperación](imagenes/prototipo-v0.10/recuperacion-api36.png). Capturas reales de emulador, sin editar. Se comprobó desplazamiento hasta los controles y avisos de guardado visibles con contenido largo. El ajuste de densidad del AVD se restaura al terminar.

**APK final:** `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.10.apk`, **12.438.198 bytes**, SHA-256 `8f578fb5297924f4b6669eb571aac48e05bea773b60a1c216ed7bd4200468c26`. También disponible en `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.10.apk`; la copia de `build/apk-archives` se conserva frente a nuevos empaquetados. Paquete `ar.edu.unmdp.entrenamiento`, versionCode 13. Certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`, coincidente con el APK v0.3.1 archivado. Checkpoints locales v0.5 `08eb2c6`, v0.5.1 `2e81846`, v0.6 `a4a74d0`, v0.7 `f02a32a`, v0.8 `b163f62` y v0.9 `d5d6219`; v0.10 se agrega en el commit de cierre. No hubo push ni publicación/cierre de issues.

**Pendiente:** S22 Ultra con canto real, audibilidad/volumen, experiencia de uso y actualización sobre el perfil del usuario. No se midió latencia acústica ni eficacia pedagógica; los tiempos sintéticos/JVM/emulador no las acreditan. El límite superior de YIN y su respuesta con ruido permanecen documentados, sin recalibración. [Guía de entrega](Demostracion-prototipo-v0.10.md).

# Verificación del prototipo Android v0.5

Fecha: 2026-10-21. Implementación autorizada mediante «Implement the plan». Alcance de software: #5 (progresión), #6 (voz), #7 (exportación/reinicio). No se publicaron commits ni se cerraron tickets en GitHub durante esta sesión.

## Entorno y comando

Windows/PowerShell, JDK de Android Studio, SDK 36, Gradle Wrapper del proyecto. Emulador Pixel_6/API 34 (`emulator-5554`), sin teléfono físico conectado. Las pruebas sustituyen azar/audio/almacenamiento en el límite de sesión; las pruebas Android verifican integración real de reproducción y captura en el emulador.

Desde `Plataforma/`:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
$env:ANDROID_SERIAL = 'emulator-5554'
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug
```

## Resultados

- Suite general: **43 pruebas JVM y 16 Android aprobadas**, compilación/APK correctos; ejecución completa en 5 min 14 s.
- La pasada manual posterior detectó una pausa rápida sin `onStop`. Se añadió el caso STARTED → RESUMED al test de captura, se detiene voz en `onPause` y se publican las mediciones en Main mientras captura/YIN continúan en IO. El caso nuevo falló antes de corregirlo (incluida una excepción de hilo de la infraestructura Compose al pausar/reanudar con publicaciones desde IO).
- Verificación final de ese ajuste: **43 JVM y los 3 Android de voz/datos aprobados**, lint y APK correctos, en 1 min 15 s. No se repitieron las regresiones de juegos que ya habían pasado; el ajuste final afecta solo al ciclo de vida/publicación de voz y al texto de ausencia de nota.
- Lint: **0 errores, 14 advertencias**; corresponden a versiones de herramientas/dependencias, orientación y configuración de backup. No se actualizaron dependencias dentro de este incremento.

Cobertura incorporada: primera/segunda alta, avance para próxima partida, reinicio de racha por partida baja, práctica manual e interrupciones excluidas, último nivel, persistencia real JSON al reabrir, conservación de aciertos ayudados y deduplicación. Exportación parseable sin alterar resultados, simulaciones aisladas y recuperación de reinicio pendiente. Captura `AudioRecord` real del emulador, reinicio de captura, guardado derivado y liberación al salir. Selector de exportación y cancelación; diálogo de reinicio con cancelar/confirmar. Se conservan las pruebas anteriores de ambos juegos y DDA intrapartida.

Las pruebas Android detectaron inicialmente accesos nuevos sin conectar en el menú, corregidos antes de la pasada final. Se corrigió además la codificación de tildes del script de edición. Se reprodujo además una carrera de carga inicial del historial: una copia de estado tomada antes de suspender podía deshacer la apertura de un juego. Dos regresiones JVM fallaron antes de corregir ambos ViewModels y pasaron después. Se actualiza ahora el estado vigente tras finalizar las lecturas. Las pruebas Android esperan foco de ventana y orientación al volver al menú; la cancelación del selector contempla que el primer Atrás cierre el teclado.

## Comprobación sintética YIN

Señales float32 de 4.096 muestras, 44.100 Hz, amplitud 0,4, fase inicial cero, sin ruido. Se estima una ventana completa por frecuencia; no son grabaciones de voz ni mediciones de latencia. Resultados JVM obtenidos con `YinTest`:

| Referencia | Frecuencia estimada | Error respecto de referencia |
| --- | --- | --- |
| 110 Hz | 110,000308 Hz | +0,004847 cents |
| 220 Hz | 220,000958 Hz | +0,007536 cents |
| 440 Hz | 440,018708 Hz | +0,073608 cents |

Las tres satisfacen el criterio inicial ≤10 cents. Silencio, señal constante, tamaño incorrecto, NaN y un bloque de ruido uniforme con semilla 42 no producen tono válido. Se verifica nota MIDI 69 para 440 Hz y diferencia de una octava respecto de La3. No se deduce de esto precisión sobre canto real, señales polifónicas o todos los niveles de ruido.

## Artefactos y límites

APK debug actualizable, paquete/firma conservados: `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.apk`, **12.193.303 bytes**, SHA-256 `f317a85fcb41d693043bba816e5f6af30cb99714b189fa899a62211e1c955223`.

Informes en `Plataforma/app/build/reports/`; resultados XML JVM y Android en `app/build/test-results/` y `app/build/outputs/androidTest-results/`. El informe Android actual contiene la revalidación final de tres casos; el HTML de los 16 casos de la suite general se conservó en `Plataforma/build/verification-v05/full-suite-report/`.

Comprobación manual adicional en emulador con el APK final: rechazo real del permiso, mensaje y enlace a Ajustes; concesión para iniciar; salida a Inicio y regreso con micrófono detenido. Exportación mediante DocumentsUI a Downloads, guardado y lectura externa con Python: JSON de 14.425 bytes, esquema 1/app 0.5, 25 mediciones derivadas, origen `microphone`, tasa real 44.100 Hz y error nulo. Se comprobaron `valid`/`midi` y ausencia de PCM. Son mediciones del micrófono virtual del emulador, no voz del usuario ni una prueba de precisión física. La confirmación de exportación aparece al final de la pantalla y puede requerir desplazarla.

Capturas inspeccionadas: [menú](imagenes/prototipo-v0.5/menu.png), [permiso rechazado](imagenes/prototipo-v0.5/voz-permiso-rechazado.png), [captura detenida](imagenes/prototipo-v0.5/voz-detenida.png) y [datos](imagenes/prototipo-v0.5/datos.png). El JSON de comprobación está en `Plataforma/build/verification-v05/export.json`, fuera del control de versiones.

Prueba física pendiente: actualizar sobre v0.4 en S22 Ultra, permisos rechazados/concedidos, voz sostenida y silencio, micrófono en segundo plano, audibilidad/volumen de ambos juegos, progresión, exportación y reapertura. [Recorrido completo y parámetros](Demostracion-prototipo-v0.5.md). No se midieron latencia acústica extremo a extremo ni eficacia pedagógica.

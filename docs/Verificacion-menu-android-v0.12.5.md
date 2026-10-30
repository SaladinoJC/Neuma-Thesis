# Verificación del menú Android v0.12.5

Fecha: 2026-10-28. Retoma solicitada por el usuario tras un apagado. Se conserva la integración Compose de Music Garden; el plan de Personaje P0–P7 continúa pendiente por pedido explícito del usuario.

## Resultado

Verificación final aprobada en Pixel_6/API 34 (`emulator-5554`): **46/46 Android, sin errores ni omisiones; 122 JVM aprobadas; lint 0 errores y 14 advertencias**. `BUILD SUCCESSFUL` en 10 min 40 s. JVM se reutilizó como `UP-TO-DATE`, sin cambios de producto desde su ejecución previa. La primera tanda de esta retoma terminó con 43/46; tras corregir los tres recorridos descritos debajo, la segunda tanda completa aprobó.

Evidencia final: `Plataforma/build/verification/20261028-174536-062/`, con `summary.json`, log e informes comprimidos. APK archivado en `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.5.apk`, versión 0.12.5/código 20, 14.724.740 bytes. SHA-256: `2ac4b67b0e694f99e90c7a79013a347d82ff26a62d71adf71e93071b78d62ac8`. Firma verificada: certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`, igual al de las entregas anteriores.

## Diagnóstico y correcciones

El informe anterior al apagado, `Plataforma/build/verification/20261028-171434-984/`, registró 25 fallos, muchos al esperar el menú. No se reprodujo ese patrón con los fuentes actuales recompilados y el emulador estabilizado. Durante el arranque en frío apareció un ANR de System UI; después el caso aislado `DdaBenchAndroidTest` aprobó. Esto no demuestra la causa de los 25 fallos históricos.

La tanda `20261028-173709-184` completó 46 pruebas y permitió identificar tres expectativas incompatibles con el menú actual:

- Dos casos de `InstrumentAndroidTest` buscaban el resumen del historial en el menú. Ahora recorren Más → Resultados → Instrumentos, filtran el nivel, abren el detalle y verifican estado y nivel tras recrear la actividad.
- `SimonAndroidTest.responderConErrorMuestraResumenYConservaPartidaCompletaAlReabrir` encontraba tres botones «Ver detalle» en el historial compartido por la suite. Selecciona el primero de los resultados completados, ordenados del más reciente al más antiguo. El caso de interrupción también abre su detalle antes de comprobar persistencia.

Esta retoma modifica pruebas y documentación; no cambia reglas, audio, DDA ni código de producto. La suite existente proporciona la reproducción y la regresión, sin agregar pruebas que dupliquen su cobertura.

## Identidad y alcance visual

Se conserva papel cálido, verdes naturales, ilustraciones 2D y fuentes Fraunces/Nunito Sans, tokens compartidos y controles nativos sobre el arte. Ocho WebP coinciden byte a byte con sus fuentes y con los SHA-256 del manifiesto Android. No se agregan assets ni dependencias en esta retoma.

El registro anterior comprobó las cuatro portadas, navegación, índice, vuelta desde Defender, recreación, segundo plano y reducción manual del movimiento a 411 dp; también Personaje a 320 dp y texto 200 %. Las capturas se conservan en `docs/imagenes/menu-android/`. Los interiores de juegos mantienen su diseño previo y Personaje es una ilustración genérica estática: son límites deliberados de esta entrega, sin iniciar el editor ni las recompensas.

## Reproducción

Desde `Plataforma/`, con el emulador listo y desbloqueado:

```powershell
.\tools\verify-prototype.ps1 -Serial emulator-5554
```

El script rechaza teléfonos y conserva log, XML, informes, APK y hash en `Plataforma/build/verification/<fecha>/`. Las pruebas reinician datos del emulador. Para instalar en teléfono, usar `adb -s <serial> install -r <apk>` sin desinstalar la aplicación.

## Revisión parcial de TalkBack

TalkBack 14.2.0.618048417 activado después de la suite, a 411 dp y texto normal. Servicio enlazado y foco de accesibilidad comprobados mediante `dumpsys accessibility`; archivo `talkback-accessibility.txt` junto a la evidencia final. Con TAB/ENTER se abrió Juegos, se alcanzó Jugar, se abrió Más, se activó Reducir movimiento, se cerró el diálogo y se abrió el Índice para elegir Instrumentos. El foco visible se conserva al regresar al libro. Capturas `411-talkback-*.png` en `docs/imagenes/menu-android/`. Se restauró el estado previo sin servicios de accesibilidad al finalizar.

Es una revisión de foco y activación con teclado mientras TalkBack está activo. Los swipes inyectados con ADB no proporcionaron un recorrido verificable; no se dan por comprobadas exploración táctil, gestos, orden de locución ni calidad de voz. Tampoco se verificó TalkBack con texto 200 %. Esos recorridos siguen pendientes en dispositivo físico.

## Pendientes

La revisión visual, audibilidad, latencia y uso táctil/locución con TalkBack en el S22 Ultra requieren prueba física; un emulador no acredita esas condiciones ni eficacia pedagógica. No hubo push, cierre de issues ni implementación del plan de Personaje.

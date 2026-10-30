# Evidencia visual de Rhythm Tap R5

Pixel_6/API 34, emulador de pruebas, 2026-10-29; producto 0.12.10/código 25. Cuarenta PNG originales de 1080×2400 px, sin retoques, obtenidos con UiAutomation tras tres vsync y un segundo para el compositor software. No son assets de producto ni mediciones acústicas.

| Prefijo | Configuración | Capturas |
| --- | --- | --- |
| `411-1.0` | 411 dp, texto 100 % | Nueve del componente y cuatro del perfil real |
| `360-1.0` | 360 dp, texto 100 % | Nueve del componente |
| `320-1.0` | 320 dp, texto 100 % | Nueve del componente |
| `320-2.0` | 320 dp, texto 200 % | Nueve del componente |

Estados del componente: lista, filtros sin coincidencias, partitura, silencios, datos del ejercicio, repetición, fallo de escritura, guardado recuperado y fallo de lectura. Reloj/audio controlados, acción accesible de tap y archivo JSON real en un directorio de pruebas. Se crea un ViewModel nuevo sobre ese archivo; la partitura y sus resultados se recuperan sin regenerar ni iniciar audio. El caso de fallo usa almacenamiento controlado para verificar mensajes y reintento sin duplicados.

El ejercicio de esas capturas contiene R10/R15/R17 con conexión, 23 ataques a tiempo y silencios respetados; es un escenario sintético de prueba, no desempeño de una persona. El detalle conserva categorías objetivo/conexión y ausencia de evaluación. Las tomas muestran distintas posiciones de scroll: con texto 200 % el encabezado o parte del detalle pueden quedar fuera del viewport y se recuperan desplazando. Los controles y el hash se ajustan a varias líneas; la notación mantiene sus proporciones.

Cuatro capturas adicionales a 411 dp recorren `MainActivity` y el perfil del emulador: `perfil-real`, `datos-exportados`, `menu-retorno` y `vacio-perfil`. Se pausa la actividad durante el conteo, se conserva un ejercicio interrumpido y se recrea la actividad. El selector del sistema guarda en Downloads un JSON cuyo registro se compara con el original. Luego el reinicio confirmado vacía el historial. La copia del archivo exportado está en `Plataforma/build/verification/2026-10-29-R5/native-export.json`.

Revisión: papel/verde, Fraunces/Nunito Sans y controles compartidos; notación estructurada sin nuevos assets. Iconos del sistema oscuros sobre papel y claros al volver al bosque. No se observaron desbordes horizontales ni solapamiento de controles; la lectura larga se desplaza. El snackbar de confirmación es transitorio y convive con el mensaje de operación. Inspector web no aplicable a Compose; teléfono físico y gestos/locución TalkBack pendientes.

Origen final: `Plataforma/build/verification/2026-10-29-R5/screens-final-*/`. Se copió solo el prefijo correspondiente a cada dimensión, porque las carpetas de origen también conservan capturas de ejecuciones anteriores. Hashes y condiciones en `summary.json`; resultados en `android-full-411.log` y `android-final-*.log`. [Guía y reproducción](../../Rhythm-Tap-R5.md).

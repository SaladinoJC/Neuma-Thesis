# Evidencia visual de Rhythm Tap R4

Capturas nativas del emulador Pixel_6/API 34, producto 0.12.9/código 24, 2026-10-29. Diecisiete PNG originales de 1080×2400 px, sin retoques. Obtenidos con UiAutomation, tres vsync y un segundo de margen para el compositor software; esa espera solo afecta la captura de pruebas.

| Prefijo | Ancho lógico / texto | Contenido |
| --- | --- | --- |
| `411-1.0` | 411 dp, 100 % | R15/R16/R17 y previa/resumen |
| `360-1.0` | 360 dp, 100 % | R15/R16/R17 y previa/resumen |
| `320-1.0` | 320 dp, 100 % | R15/R16/R17 y previa/resumen |
| `320-2.0` | 320 dp, 200 % | Previa/resumen del flujo combinado |

Cada lámina canónica repite la célula real a máxima densidad en 2/4, 3/4 y 4/4; en 3/4 las plantillas de dos tiempos dejan una negra de conexión. Son láminas de notación, no ejercicios que cumplan la cuota del 60 %. R15 muestra tres corcheas, barra, cifra 3 y corchete; R16 conserva la negra sincopada; R17, dos figuras negras y curva de prolongación, con un solo ataque.

El flujo usa una partitura de cuatro compases generada con R15/R16/R17/R03, metrónomo de ejecución activado y sonido de taps inicialmente apagado. El arnés inyecta taps por la acción accesible con reloj/audio controlados: 27 ataques a tiempo y un reataque extra deliberado en la ligadura. Luego activa sonido, repite exactamente el patrón e interrumpe el nuevo recorrido. No es ejecución humana ni audio físico medido. Las pruebas recorren todas las filas del resumen aunque cada captura solo muestra su posición de scroll.

Se inspeccionaron correspondencia entre nombre/título/figuras, número y corchete ternarios, curvas completas, separación entre figuras y ausencia de desborde horizontal. Texto al 200 % se consulta desplazando; los símbolos musicales mantienen su proporción en dp. Continúan papel cálido, verdes, tipografías y controles compartidos, sin nuevos assets. El inspector web no aplica a Compose; prueba física y gestos/locución TalkBack pendientes.

Origen exacto, logs y hashes: `Plataforma/build/verification/2026-10-29-R4/screens-*/` y `summary.json`. Se copió solo el prefijo correspondiente a cada dimensión; las carpetas de origen también contienen capturas conservadas de ejecuciones anteriores. [Guía y reproducción](../../Rhythm-Tap-R4.md).

# Evidencia visual de Rhythm Tap R3

Capturas nativas en Pixel_6/API 34, emulador de pruebas, 2026-10-29. Producto 0.12.8/código 23. No son assets de la aplicación ni mediciones acústicas.

32 PNG originales de 1080×2400 px, obtenidos con UiAutomation después de tres vsync y un margen de un segundo para el compositor software. No se retocaron. Selección final copiada exclusivamente de `Plataforma/build/verification/2026-10-29-R3/capturas-verificadas-*/`; cada carpeta de origen conserva además archivos de ejecuciones previas, por lo que el prefijo del nombre determina la dimensión correcta. Hashes en `summary.json` de esa evidencia.

| Prefijo | Ancho lógico / texto | Contenido |
| --- | --- | --- |
| `411-1.0` | 411 dp, 100 % | Ocho láminas y previa/resumen |
| `360-1.0` | 360 dp, 100 % | Ocho láminas y previa/resumen |
| `320-1.0` | 320 dp, 100 % | Ocho láminas y previa/resumen |
| `320-2.0` | 320 dp, 200 % | Previa/resumen del ejercicio combinado |

Cada lámina R03/R04/R05/R06/R07/R08/R13/R14 muestra la célula real del generador repetida a máxima densidad en 2/4, 3/4 y 4/4. No es un ejercicio que cumpla la cuota del 60 %: sirve para inspeccionar barras por pulso, tramos secundarios, puntillos, silencios y separación. Se verificó visualmente la correspondencia entre nombre de archivo, título y figuras; las capturas anteriores desfasadas/vacías se conservan solo en el directorio de trabajo y no se incluyen aquí.

El flujo combinado usa cuatro compases generados, metrónomo posterior al conteo apagado, audio/reloj controlados y acción accesible de tap. Presenta un adelanto, un retraso, un extra y una omisión deliberados; no mide coordinación humana ni reproducción acústica. Las capturas de resumen/previa muestran distintas posiciones de scroll; a 200 % parte del contenido continúa fuera del viewport y se consulta desplazando. Las pruebas recorren todas las categorías del resumen y repiten el patrón.

Revisión: papel y verdes/tipografías comunes, notación desde datos, sin solapamiento horizontal ni compresión de figuras a 320 dp; etiquetas largas pasan a varias líneas. La reducción de movimiento conserva desplazamiento inmediato. Sin nuevos assets ni desviación de identidad. Inspector web no aplicable a Compose. Prueba física y gestos/locución TalkBack pendientes. Detalles en [guía R3](../../Rhythm-Tap-R3.md).

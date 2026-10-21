# Verificación de iteraciones posteriores a v0.5

Autorización: el usuario pidió el 2026-10-21 «Dale, ponete con lo que puedas», sobre `Plan-iteraciones-post-v0.5.md`. Trabajo local, sin publicación externa. Teléfono físico pendiente.

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

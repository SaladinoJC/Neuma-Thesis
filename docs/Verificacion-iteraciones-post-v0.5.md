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

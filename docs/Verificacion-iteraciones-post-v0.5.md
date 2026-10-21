# Verificación de iteraciones posteriores a v0.5

Autorización: el usuario pidió el 2026-10-21 «Dale, ponete con lo que puedas», sobre `Plan-iteraciones-post-v0.5.md`. Trabajo local, sin publicación externa. Teléfono físico pendiente.

## v0.5.1 — Operaciones y progreso

- Snackbar con consumo por identificador; la última operación permanece consultable. Exportación cancelada, fallida e interrumpida se distinguen; sin instantánea no se abre ni escribe el destino. Se conserva la espera mientras el selector está abierto y se impiden callbacks duplicados durante escritura.
- Preparación/resumen muestran próximo nivel y partidas restantes; último nivel explícito. Reglas y configuraciones DDA conservadas.
- Regresión roja→verde de devolución del selector sin instantánea. 46 pruebas JVM y tres Android de voz/datos aprobadas en Pixel_6/API 34; compilación y APK completados en 1 min 13 s. Pruebas nuevas de guardado, fallo de destino, cancelación, consumo de aviso y reintento.
- APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.1.apk`: 11997310 bytes, SHA-256 `d87932ec9eb29dc5a17f948e43ec29c41dc7c36d3f74d111017e0f49a239d86b`.
- No se probó el S22 Ultra ni se acredita eficacia pedagógica. La inspección visual final y regresión global se realizarán sobre la entrega integrada.

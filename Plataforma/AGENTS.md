# Implementación Android

Leer el contexto de raíz, `CONTEXT.md` y los ADR antes de modificar juegos. La aplicación usa Kotlin/Compose; las reglas de sesión no dependen de Compose ni de Android.

## Límites de prueba aprobados

- Acciones del jugador → estado, resultado, historial y progreso mediante la sesión pública. Controlar audio, almacenamiento, tiempo y azar en sus límites; comprobar resultados a través de la sesión, sin consultar internals.
- Muestras PCM → estimación de tono cuando se implemente la prueba de voz.
- Verificar reproducción/captura también en Android; una prueba JVM no acredita hardware.

Trabajar por ciclos pequeños red → green. Consultar `README.md` de esta carpeta para ejecución y verificaciones. Documentar los valores de audio y los resultados medidos, distinguiendo emulador de teléfono real.

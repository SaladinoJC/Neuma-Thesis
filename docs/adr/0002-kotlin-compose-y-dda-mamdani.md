# Aplicación Android en Kotlin/Compose y DDA Mamdani

El usuario confirmó el 2026-10-02 Kotlin y Jetpack Compose para la aplicación, con las reglas de juego y DDA separadas de la interfaz. Al ser Android la plataforma exclusiva, se elige desarrollo nativo frente a añadir una capa multiplataforma. La eventual incorporación de C++ al audio queda pendiente de evidencia técnica.

Para el DDA se eligió inferencia difusa Mamdani frente a una salida Sugeno: la prioridad de esta demostración es poder explicar y depurar reglas lingüísticas y sus funciones de pertenencia. Las reglas, rangos y parámetros iniciales serán configuraciones de prueba; esta elección no acredita eficacia pedagógica ni garantiza estabilidad temporal.

Referencias consultadas: [Jetpack Compose](https://developer.android.com/compose), [comparación oficial Mamdani/Sugeno](https://www.mathworks.com/help/fuzzy/types-of-fuzzy-inference-systems.html).

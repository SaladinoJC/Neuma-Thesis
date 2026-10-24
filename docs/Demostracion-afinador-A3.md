# Afinador A3 — guiado y revisión final

Implementación local de [#11](https://github.com/SaladinoJC/Tesis/issues/11), autorizada por el usuario el 2026-10-24 sobre v0.12 (`d0f1624`). Estado inicial: pruebas de reglas escritas, implementación y verificación pendientes. No es una entrega comprobada.

Contrato previsto: elegir Libre/Guiado, guitarra/afinación y Normal/Pro antes de comenzar. Guiado recorre sexta → primera, un segundo continuo dentro del margen; cada objetivo nuevo descarta toda lectura previa. Revisión final activada inicialmente y recordada. La segunda pasada permite dejar cuerdas pendientes y volver a ellas; solo el final sin pendientes declara conjunto comprobado. Desactivarla completa la primera pasada sin declarar comprobación final.

Resumen únicamente en memoria, separado por primera pasada, revisión y correcciones, con copia de guitarra/afinación/criterio. Detener, salir o pasar a segundo plano interrumpe el recorrido sin completarlo ni guardar resultados; iniciar otra vez requiere acción explícita y empieza un recorrido nuevo. Recrear la vista no debe duplicar avances. A4 (referencias) y A5–A6 (entrenamiento e historial) permanecen fuera de este incremento.

Parámetros provisionales del agente para el aviso «Muy lejos, revisar cuerda correcta»: distancia absoluta ≥700 cents durante al menos 400 ms de lecturas válidas continuas, separación máxima250 ms; despeje a ≤500 cents, lectura inválida u objetivo nuevo. No identifica inequívocamente una cuerda ni pliega octavas. Permanencia de afinación conserva `tuner-continuity-v1`: un segundo, hueco máximo250 ms, Normal±5/Pro±2 cents. No están calibrados físicamente.

Verificación pendiente: sesión JVM completa, integración Compose/captura, Android y S22 Ultra/guitarra real. Distinguir lecturas sintéticas y emulador de mediciones físicas. No se guarda PCM ni se modifica el historial de voz.

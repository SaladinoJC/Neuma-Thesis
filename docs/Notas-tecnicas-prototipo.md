# Notas técnicas para la primera demostración

Revisión de fuentes primarias y entorno local: 2026-10-02. Estas notas apoyan las decisiones de la entrevista; no son resultados experimentales ni una validación pedagógica.

## Aplicación y entorno

El usuario eligió Android exclusivo y Kotlin/Jetpack Compose. [Compose](https://developer.android.com/compose) es el toolkit nativo recomendado por Android. [Oboe](https://developer.android.com/games/sdk/oboe) es una biblioteca de audio para Android; la afirmación previa del protocolo de que abstrae Android/iOS requiere corrección. Su incorporación al prototipo aún no está decidida ni se midió su latencia.

Se detectaron Android Studio, SDK con plataformas 34/35/36, NDK 26.3.11579264 y CMake 3.22.1. El teléfono del usuario es un Samsung S22 Ultra; no estaba conectado por ADB durante la inspección. La compatibilidad de JDK/Gradle/AGP se comprobará al crear la aplicación mediante Gradle Wrapper.

## Inferencia difusa

[Mamdani y Sugeno](https://www.mathworks.com/help/fuzzy/types-of-fuzzy-inference-systems.html) difieren en la representación y agregación de las salidas. Se eligió Mamdani por la prioridad de inspeccionar reglas lingüísticas y funciones de pertenencia. El [proceso de inferencia](https://www.mathworks.com/help/fuzzy/fuzzy-inference-process.html) requiere entradas, rangos, funciones, reglas y una salida; escoger lógica difusa no determina la política del juego ni garantiza estabilidad temporal.

Las reglas y parámetros iniciales serán configurables. La exactitud reciente y el uso de reescuchas describen el reconocimiento tímbrico; Simón utiliza longitud máxima completada correctamente. Los indicadores deben interpretarse por juego y nivel. La asociación entre reescuchas y necesidad de ayuda es una hipótesis de diseño.

## Muestras instrumentales

Banco candidato: [University of Iowa Musical Instrument Samples](https://theremin.music.uiowa.edu/MIS.html). La página declara permiso de descarga y uso en cualquier proyecto sin restricciones y describe grabaciones cromáticas. La declaración no se denomina CC0; conservar su procedencia y texto de permiso junto a los archivos seleccionados.

Catálogo técnico propuesto, pendiente de inspeccionar los audios concretos:

| Nivel | Opciones |
| --- | --- |
| A | Piano, violín, flauta, marimba |
| B | Flauta, clarinete, trompeta, trombón |
| C | Flauta, clarinete, oboe, fagot |

Se buscará una altura sonora común, dinámica comparable y duración de presentación controlada. Verificar altura real, ataque, decaimiento y reverberación; no asumirlos a partir del nombre del archivo. Iowa advierte que el piano se grabó fuera de la cámara anecoica, a diferencia de los demás instrumentos. Aún no se descargaron ni procesaron muestras.

Alternativa: [VSCO 2 CE](https://versilian-studios.com/vsco-community/) y su [repositorio original con licencia CC0](https://github.com/sgossner/VSCO-2-CE). En el catálogo revisado, flauta y clarinete/oboe no tienen una altura originalmente grabada en común; transponer introduciría un cambio de timbre. Por ello se propone Iowa para respetar la misma altura mediante grabaciones reales. La dificultad relativa de los tres niveles y el control de pistas ajenas al timbre deberán comprobarse; no están validados por este inventario.

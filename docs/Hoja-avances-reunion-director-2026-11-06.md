# Neuma — hoja de avances

**Trabajo Final de Ingeniería en Informática · Universidad Nacional de Mar del Plata**  
Juan Cruz Saladino · Director: Leonel Guccione  
Reunión: viernes **6 de noviembre de 2026** · Estado al **3 de noviembre**

## Síntesis del avance

Desde la presentación del protocolo avancé hacia un prototipo Android con cinco actividades musicales, captura y estimación de tono, adaptación mediante lógica difusa y registros locales exportables. El ejercicio de intervalos vocales reúne estos componentes en una misma actividad. También inicié el informe, con fundamentos musicales, antecedentes y documentación de las decisiones de diseño e implementación.

El aporte de ingeniería que propongo evaluar es **la integración del análisis vocal, la retroalimentación y la adaptación de dificultad en el dispositivo**, junto con su comportamiento técnico y uso exploratorio. El siguiente paso es comprobar la candidata actual en el teléfono y acordar los criterios de evaluación.

## Del protocolo al prototipo

| Frente previsto | Avance concreto | Qué falta comprobar o completar |
| --- | --- | --- |
| Plataforma móvil y ejecución local | Aplicación Android en Kotlin/Jetpack Compose, funcionamiento offline y un perfil local. Reglas musicales y motor difuso separados de la interfaz. | Recorrido integrado de la candidata en el S22 Ultra y revisión de compatibilidad/usabilidad. |
| Captura y análisis vocal | Captura con AudioRecord y estimador YIN; referencia sonora, frecuencia, nota y desviación. Intervalos evalúa la relación entre notas cantadas. | Precisión con referencias conocidas, comportamiento con voz real y demora de la retroalimentación. |
| Dificultad adaptativa | Motor Mamdani reutilizado con configuraciones por actividad, adaptación y desbloqueos; inspectores de entradas, reglas y decisiones. | Calibración de parámetros y evaluación del comportamiento. Selección global automática de ayuda vocal pendiente. |
| Actividades musicales | Simón; reconocimiento de instrumentos con melodías; Rhythm Tap; lectura de notas/acordes en Defender; intervalos vocales. | Estabilizar recorridos y acordar qué actividades tendrán evaluación y acabado completos en la entrega. |
| Seguimiento y trazabilidad | Historiales, progreso persistido, decisiones inspeccionables, exportación JSON y ZIP de originales. Se conservan mediciones derivadas de voz, sin grabaciones. | Comprobar persistencia, exportación y recuperación en el teléfono con la candidata actual. |
| Informe y fundamentos | Borradores de introducción/objetivos, marco teórico y primera síntesis de antecedentes; documentación técnica y registros parciales de pruebas. | Revisión del director, reconstrucción de la revisión bibliográfica histórica y completar capítulos/evaluación. |

Además desarrollé un afinador Libre/Guiado/Entrenamiento de oído con perfiles y seguimiento. Es un módulo complementario: el compromiso de integración vocal se aborda mediante **intervalos**, no solo con la prueba de tono o el afinador.

## Qué ofrece cada actividad

- **Simón:** escuchar y repetir secuencias con piano; práctica en tres escalas y desbloqueo entre partidas.
- **Instrumentos:** reconocer timbres a partir de melodías; el DDA ajusta las escuchas disponibles entre preguntas.
- **Rhythm Tap:** leer cuatro compases y marcar ataques; 21 categorías de células, resultados por ejecución y progresión de tempo/contenidos.
- **Defender:** leer notas y acordes en sol/fa; acordes por raíz/calidad, inversiones y adaptación del tiempo de viaje de objetivos futuros. La respuesta con piano sigue pendiente.
- **Intervalos vocales:** imitación de dos notas, ancla dada y producción libre; series, tolerancia adaptativa y progreso por intervalo/dirección. Las tres ayudas se eligen explícitamente.

Los recorridos y límites están documentados en el [estado del prototipo](../.agents/estado-prototipo.md) y las [guías de implementación](../Plataforma/README.md).

## Evidencia disponible y validación pendiente

| Evidencia | Alcance real |
| --- | --- |
| Candidata **0.12.20**, verificación del 30/10 | **292 pruebas JVM aprobadas**, lint sin errores y APK/app de pruebas compilados. Comprueba reglas y componentes en computadora; las pruebas Android nuevas están compiladas, sin ejecutar. [Registro](evidencias/intervalos-vocales-v6/host-summary.json). |
| Versión **0.12.10**, anterior a las últimas ampliaciones | **57/57 pruebas Android** en emulador Pixel 6/API 34; casos de interfaz de ritmo también a distintos anchos y texto ampliado. Esta suite no acredita las ampliaciones posteriores de la candidata actual. [Registro](Rhythm-Tap-R5.md#verificación). |
| Bancos de reglas y señales sintéticas | Permiten reproducir decisiones, desbloqueos y casos del estimador sin depender del desempeño durante la demo. Sus resultados están identificados como sintéticos. |
| Prueba manual estructurada en S22 Ultra | Prevista para el **4/11** con una [guía de 39 casos](Guia-pruebas-manuales-S22-Ultra.md). Resultados todavía pendientes de registrar. |

La evaluación técnica con referencias y las sesiones exploratorias con participantes quedan por realizar. Las pruebas de software y simulaciones disponibles no establecen exactitud acústica ni mejora del aprendizaje.

## Decisiones para conversar con el director

| Tema | Propuesta de trabajo para revisar | Acuerdo que necesito |
| --- | --- | --- |
| Alcance final | Concentrar la evaluación en intervalos vocales y una segunda actividad; decidir el acabado de las demás y el lugar del afinador. | El protocolo presenta cinco actividades iniciales; el plan fija como mínimo una vocal y otra sencilla, con tres opcionales. ¿Qué debe quedar completo para la entrega? |
| Arquitectura de audio | Medir primero la línea base actual con YIN; considerar C++/Oboe y ML según resultados y tiempo disponible. | El protocolo los incluye con más fuerza que el plan adjunto. Acordar cómo justificar y documentar esta diferencia. |
| Evaluación técnica | Medir error de tono, proporción de señales aceptadas, respuesta temporal y decisiones del DDA en condiciones registradas. | Referencias, rangos de operación, método de medición y criterios de aceptación; distinguir procesamiento del estimador y latencia de la interacción. |
| Sesiones de uso | Pruebas exploratorias inicialmente con estudiantes de música mayores de edad y conocimientos básicos. | Acceso a participantes, tareas, registro y condiciones de participación. El objetivo inicial sería observar uso y dificultades; no hay un estudio de mejora del aprendizaje diseñado. |
| Cronograma y revisiones | Reordenar el trabajo según estabilización, mediciones, sesiones y escritura. | Revisar los hitos del protocolo —informes 1/12 y 1/2, finalización 27/2— y la defensa provisional de marzo de 2027; acordar próxima entrega y frecuencia de revisión. |
| Dirección visual | Mostrar Music Garden implementado y Quiet Studio como exploración de migración. | Recoger observaciones sobre adecuación al público, legibilidad y equilibrio entre práctica y juego, sin condicionar la validación técnica a un rediseño completo. |

Estas propuestas no modifican por sí solas el protocolo ni acreditan aprobación académica. Las diferencias de alcance, arquitectura y planificación están localizadas en el [contraste del protocolo y el desarrollo](../Informe/contraste-protocolo.md). La exploración visual tiene su [acuerdo propio](Diseno-Quiet-Studio.md).

## Próximo incremento propuesto

1. Ejecutar la prueba manual del S22 Ultra, registrar incidencias y corregir las que afecten al recorrido de demostración.
2. Elegir una demo breve con una actividad adaptativa, un ejercicio vocal y un resultado persistido; preparar un video de respaldo.
3. Con los acuerdos de la reunión, cerrar el alcance evaluable y el método de medición antes de ampliar funciones.

## Actualización después de la prueba y de la reunión

Completar con observaciones reales; no trasladar resultados de simulaciones a estos campos.

```text
Prueba del S22 Ultra — fecha y versión:
Recorridos comprobados:
Problemas que afectan la demo:
Casos pendientes o bloqueados:
Evidencia conservada (registro, capturas, JSON o video):

Acuerdos con el director — fecha:
Alcance de entrega:
Criterios y próximos pasos de evaluación:
Decisión sobre arquitectura pendiente del protocolo:
Observaciones visuales:
Próxima entrega y fecha de revisión:
```

**Documentos de referencia:** [protocolo entregado](PROTOCOLO_Saladino.doc), [plan adjunto](Plan_de_trabajo_y_Gantt_Saladino.xlsx), [objetivos de trabajo](../Informe/capitulos/02-objetivos.tex) y [estado de escritura](../Informe/seguimiento.md). Esta hoja resume el avance para la reunión; el informe mantiene sus capítulos como fuente de la redacción académica.

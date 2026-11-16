# Mejoras de práctica: referencias melódicas y constancia

**Origen:** entrevista del marco teórico Q1–Q13, 2026-11-02.
**Solicitud:** el usuario pide documentar todas las propuestas «para implementarlas en
un rato». Agenda para próxima implementación, con detalles aún por especificar.
**Estado:** registro documental; las dos mejoras nuevas no están implementadas en esta sesión.

Acuerdos de escritura y experiencia del autor:
[`Informe/guia-marco-teorico.md`](../Informe/guia-marco-teorico.md).
Estas mejoras no implican por sí mismas modificar el alcance académico acordado con el director.

## M1. Referencia melódica después de un error de intervalo

### Conducta solicitada

Después de un error, ofrecer una canción o melodía conocida cuyo comienzo contenga
el intervalo objetivo. Ejemplo aportado por el autor: equivocarse en una sexta mayor
y disponer de una referencia melódica que comience con una sexta mayor.

La referencia sirve como apoyo a la práctica del intervalo. El autor aclaró que
cantar corresponde a la respuesta del ejercicio y no constituye una ayuda nueva
reservada a los errores. No convertir esta propuesta en un ejercicio adicional
de selección múltiple ni tratar las modalidades vocales existentes como pendientes.

### Base existente que se debe conservar

- [Ficha de intervalos por reproducción vocal](Ideas%20Desarrolladas/01-Ear-training-intervalos-reproduccion-vocal.md).
- [V4: imitación](Intervalos-vocales-V4.md), [V5: ancla dada](Intervalos-vocales-V5.md)
  y [V6: producción libre](Intervalos-vocales-V6.md): el canto integra los tres modos;
  consultar guías, código y límites de verificación antes de modificar el flujo.
- V6 se caracteriza por ausencia de reproducción de referencia durante su recorrido.
  Una ayuda audible nueva exige definir su relación con ese modo antes de habilitarla,
  preservando la interpretación del historial y del progreso.

### Decisiones pendientes para especificar

- En qué estados se ofrece la ayuda y cómo continúa el ejercicio; ofrecerla tras
  un error no establece que deba reproducirse automáticamente.
- Qué se considera error y cuándo se confirma. Relacionarlo con la evaluación
  existente, manteniendo separados los fallos técnicos de una respuesta musical.
- Repertorio de referencias, familiaridad del estudiante, variantes y direcciones.
  Todavía no hay canciones seleccionadas; verificar los intervalos de cada fragmento.
- Presentación de la referencia: título, ejemplo sonoro, notación o combinación;
  adaptar al sistema visual vigente cuando se implemente.
- Cómo registrar oferta y uso de la ayuda, y su relación con las reglas existentes
  de práctica, progresión y DDA. No modificar ni atribuir dominio autónomo silenciosamente.

### Criterios de comprobación propuestos por el agente

Comprobar la correspondencia entre intervalo/dirección y fragmento, el flujo desde
el error, la disponibilidad de ayuda, el comportamiento cuando no haya referencia
adecuada y la compatibilidad de historial/progreso. Verificar la coordinación con
reproducción/captura y los tres modos vocales, además del registro de ayuda utilizada.
La comprobación funcional no demuestra que las referencias mejoren el aprendizaje.

## M2. Apoyo a la constancia de práctica

### Conducta elegida

- **Meta semanal de días de práctica** como referencia principal, configurable por el usuario.
- **Racha diaria opcional** como recurso secundario.

El autor aceptó esta recomendación en Q11 y solicitó documentarla para implementación
posterior en Q13. Su motivación es facilitar la práctica sostenida entre clases.
Cantidad de días, duración o volumen mínimo y detalles de presentación no están definidos.

### Decisiones pendientes para especificar

- Qué actividad cuenta como práctica de un día y cómo tratar interrupciones o
  fallos técnicos. Evitar confundir un día practicado con dominio musical.
- Qué actividades de la plataforma participan y cómo contabilizar varias sesiones
  en la misma fecha sin multiplicar los días.
- Semana de referencia, zona horaria, cambios de fecha y cambios de la meta.
- Visibilidad, activación/desactivación de la racha y tratamiento de descansos.
- Presentación del historial de práctica; el calendario es una alternativa propuesta,
  pendiente de diseño. No se acordaron notificaciones ni recompensas.
- Persistencia, exportación, compatibilidad y reinicio según el perfil local existente.
  Distinguir esta racha de días de las rachas de resultados usadas por el DDA.

### Criterios de comprobación propuestos por el agente

Usar tiempo controlado para comprobar sesiones del mismo día, transición de días
y semanas, cambios de zona/meta, persistencia y recuperación. Verificar la opción
de ocultar/desactivar la racha. Los cambios de asistencia deben preservar los
resultados musicales y las reglas de progreso existentes.

## Criterios pedagógicos relacionados

La revisión del 02/11 sobre espaciamiento se registra en
[`Informe/investigacion-spacing-musical.md`](../Informe/investigacion-spacing-musical.md).
M2 sigue orientada a días de práctica: el efecto de espaciamiento requiere examinar
retornos a contenidos y retención. Un planificador de repasos por contenido no forma
parte de la funcionalidad especificada hasta ahora; se conserva como decisión adicional
por considerar al diseñar ese frente.

Repetir un fragmento y resolver material nuevo son situaciones que deberán
distinguirse al fundamentar el diseño. La preocupación del autor por memorizar
un subconjunto de ejercicios orienta el análisis; no demuestra que la repetición
sea ineficaz ni exige regenerar todas las actividades indiscriminadamente.

Al retomar software, revisar primero las funciones existentes de generación,
repetición y separación práctica/progresión. Evitar duplicarlas como nuevas tareas.

## Próximo paso de implementación

Fundamentación inicial redactada el 02/11 en el
[capítulo 3](../Informe/capitulos/03-marco-teorico-y-estado-del-arte.tex): apartados
de retroalimentación/andamiaje, referencias melódicas y metas/rachas. Fuentes y
límites en [el registro bibliográfico](../Informe/fuentes-marco-teorico.md).
Este avance de escritura mantiene M1/M2 pendientes de implementación.

Tomar M1 o M2 como un incremento acotado cuando se retome software. Completar las
decisiones funcionales relevantes con el contexto y el código existentes; registrar
la especificación y las tareas en el issue tracker del proyecto según
`docs/agents/issue-tracker.md`. No se crearon ni publicaron issues en esta sesión.
Este documento conserva la solicitud y no declara que ambas mejoras estén listas
para ejecutar sin completar sus criterios. Mantener las prioridades previas hasta
elegir el próximo frente con el usuario; la entrevista actual corresponde al informe.

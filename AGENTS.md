# Contexto para Codex

## Proyecto y estado

Este directorio reúne materiales de una tesis / Trabajo Final de Ingeniería en Informática de la Universidad Nacional de Mar del Plata sobre un sistema de entrenamiento auditivo musical mediante juegos serios. El foco de ingeniería propuesto es procesar audio en tiempo real en el dispositivo, estimar el tono de la voz y ajustar dinámicamente la dificultad a partir del desempeño del estudiante.

El usuario confirmó que quiere comenzar a prototipar. Consultar `.agents/estado-prototipo.md` al iniciar trabajo de implementación o retomar una sesión: registra el avance, las decisiones y el próximo paso. El nombre formal, el alcance final, la arquitectura y el equipo siguen sujetos a confirmación con el director. Las alternativas técnicas de los borradores son propuestas, no decisiones implementadas.

## Mapa de documentos

`Tesis/` es la raíz del repositorio. Los materiales académicos están en `docs/` y la aplicación se desarrollará en `Plataforma/`.

- `docs/PROTOCOLO_Saladino.doc`: documento de protocolo disponible. Consultarlo al definir objetivos, alcance o plan de trabajo. El contexto anterior mencionaba `PROTOCOLO-contenido.md` y `PROTOCOLO.doc`, pero esos archivos no están presentes; verificar su ubicación si se necesitan.
- `docs/Plan_de_trabajo_y_Gantt_Saladino.xlsx`: planificación disponible; consultarla al trabajar sobre cronograma o entregables.
- `docs/InvestigacionSistematica.md`: revisión extensa del estado del arte, brechas y opciones técnicas. `docs/InvestigacionSistematica.html` es una representación HTML del mismo material; comprobar cuál versión está actualizada antes de editar.
- `docs/Fase 1 Tesis Educación Musical.md`, `docs/Investigación App Entrenamiento Musical.md` y `docs/Tesis Juegos Serios Entrenamiento Musical.md`: fases y borradores de investigación que pueden reflejar hipótesis o stacks distintos.
- `docs/IdeasPuntuales.md`: inventario de propuestas. `docs/Ideas Desarrolladas/`: 18 fichas detalladas de minijuegos y sistema de progresión.
- `docs/IdeasPrototipo.md`: selección y orden **sugeridos** de cinco minijuegos para un primer prototipo; no tratar la selección como aprobada.

## Orientación técnica actual

Decisiones confirmadas por el usuario el 2026-10-02: Android como plataforma para el proyecto; primera demostración con Simón y ¿Qué instrumento es?, DDA basado en lógica difusa y una prueba técnica separada de captura/estimación de tono. El DDA deberá contemplar adaptación intrapartida y desbloqueo de niveles. Consultar `.agents/estado-prototipo.md` para las preguntas aún abiertas y `docs/adr/0001-plataforma-android.md` para la decisión de plataforma. Las fichas conservan el diseño anterior del DDA por promedio móvil y umbrales; ese diseño está en revisión.

El contexto previo atribuye al borrador del protocolo un MVP con audio monofónico, estimación de tono con una línea base DSP (YIN) y posible apoyo de un modelo ML liviano, telemetría para validar la precisión y un motor reutilizable de ajuste dinámico de dificultad (DDA). También describe ejecución local de audio, procesamiento asíncrono y exclusión de extracción de voz en entornos polifónicos, multijugador en tiempo real y XR/3D pesado. Comprobar estas atribuciones en el protocolo disponible antes de usarlas para fijar el alcance. `docs/IdeasPrototipo.md` propone empezar por minijuegos sin captura de audio y avanzar hacia ritmo y reproducción vocal de intervalos.

Estos detalles son hipótesis de diseño: al planificar o implementar, comprobar su viabilidad y registrar la decisión concreta. En particular, los documentos tempranos exploran una arquitectura web con React/Next.js, WebAudio y RNN/LSTM; el protocolo posterior propone audio nativo/edge. No asumir que ambos stacks se usarán juntos.

## Cómo trabajar con este material

- Responder y redactar en español, salvo que la tarea pida otro idioma. Conservar nombres de archivos, términos técnicos y citas tal como correspondan.
- Antes de afirmar que algo está decidido, distinguir entre investigación, propuesta, borrador de protocolo y decisión confirmada por el usuario o el director. Ante contradicciones, señalar los documentos y pedir resolución solo cuando afecte la tarea actual.
- Para investigación académica, rastrear cada afirmación, cifra, cita y referencia bibliográfica hasta una fuente comprobable. Tratar las métricas y atribuciones presentes en los borradores como pendientes de verificación hasta consultar la fuente primaria; no inventar resultados ni referencias.
- Al actualizar el protocolo, mantener correspondencia con las secciones del documento oficial disponible y preservar los marcadores `[COMPLETAR]` para datos personales o administrativos desconocidos.
- Al desarrollar una idea, partir de su ficha en `docs/Ideas Desarrolladas/` y comprobar que sus requisitos encajen con el alcance vigente del protocolo. Si cambia una decisión transversal, actualizar también los documentos afectados o dejar explícita la discrepancia.
- Mantener este archivo como índice breve de contexto estable. Registrar aquí las decisiones confirmadas y cambiar o retirar las instrucciones que dejen de representar al proyecto.

## Trabajo sobre el prototipo

- Antes de implementar un minijuego, leer `docs/IdeasPrototipo.md`, su ficha en `docs/Ideas Desarrolladas/` y el estado del prototipo. Las fichas pueden registrar decisiones particulares como confirmadas; conservar esa atribución y distinguirla de la aprobación del conjunto de cinco juegos o del stack.
- Construir incrementos pequeños que se puedan ejecutar y demostrar. Registrar cómo ejecutarlos y comprobar la conducta modificada con pruebas o una verificación manual reproducible, según corresponda.
- Para audio y DDA, distinguir valores propuestos, mediciones reales y resultados de simulación. Registrar condiciones y método de medición; una demostración funcional no acredita eficacia pedagógica.
- Al cerrar una sesión con cambios sustanciales, actualizar `.agents/estado-prototipo.md` con decisiones, avance, verificación, pendientes y próximo paso. Registrar una decisión como confirmada solo con su origen; identificar las elecciones técnicas del agente como provisionales.
- Cuando exista código en `Plataforma/`, agregar allí un `AGENTS.md` si hay instrucciones propias de implementación que convenga aplicar solo a esa carpeta.

## Agent skills

### Issue tracker

Tareas y especificaciones en GitHub Issues de SaladinoJC/Tesis.
Consultar `docs/agents/issue-tracker.md`.

### Triage labels

Se utilizan las cinco etiquetas predeterminadas.
Consultar `docs/agents/triage-labels.md`.

### Domain docs

Un único contexto: `CONTEXT.md` y `docs/adr/`.
Consultar `docs/agents/domain.md`.

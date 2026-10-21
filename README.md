# Tesis — Entrenamiento auditivo musical

Materiales y prototipo del Trabajo Final de Ingeniería en Informática de la Universidad Nacional de Mar del Plata. El proyecto explora juegos serios, procesamiento de audio y ajuste dinámico de dificultad; el alcance y la arquitectura están en definición.

## Organización

- [`docs/`](docs/): protocolo, investigación, planificación y fichas de minijuegos. [`IdeasPrototipo.md`](docs/IdeasPrototipo.md) contiene una selección sugerida para el prototipo.
- [`Plataforma/`](Plataforma/README.md): aplicación Android Kotlin/Compose; Simón, reconocimiento de instrumentos, DDA Mamdani, progresión persistida, prueba de voz YIN y exportación/reinicio. Incluye instrucciones de compilación, ejecución y pruebas.
- [`AGENTS.md`](AGENTS.md): contexto e instrucciones para trabajar en el proyecto.
- [`.agents/estado-prototipo.md`](.agents/estado-prototipo.md): decisiones, avance y próximo paso para continuar entre sesiones.
- [Especificación](docs/Especificacion-prototipo-v1.md) y [plan de tareas](docs/Plan-tareas-prototipo-v1.md): primera demostración aprobada y enlaces a las issues de GitHub.
- [`CONTEXT.md`](CONTEXT.md) y [`docs/adr/`](docs/adr/): vocabulario y decisiones de diseño.

La raíz `Tesis/` es la raíz del repositorio Git. Abrir `Plataforma/` en Android Studio para ejecutar la app. La versión 0.5 reúne el recorrido de demostración: [guía y límites](docs/Demostracion-prototipo-v0.5.md), [verificación](docs/Verificacion-prototipo-v0.5.md). La prueba física completa en S22 Ultra sigue pendiente.

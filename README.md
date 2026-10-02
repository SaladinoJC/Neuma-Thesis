# Tesis — Entrenamiento auditivo musical

Materiales y prototipo del Trabajo Final de Ingeniería en Informática de la Universidad Nacional de Mar del Plata. El proyecto explora juegos serios, procesamiento de audio y ajuste dinámico de dificultad; el alcance y la arquitectura están en definición.

## Organización

- [`docs/`](docs/): protocolo, investigación, planificación y fichas de minijuegos. [`IdeasPrototipo.md`](docs/IdeasPrototipo.md) contiene una selección sugerida para el prototipo.
- [`Plataforma/`](Plataforma/README.md): aplicación Android Kotlin/Compose; menú, Simón musical con piano táctil y muestras reales, e historial local. Incluye instrucciones de compilación, ejecución y pruebas.
- [`AGENTS.md`](AGENTS.md): contexto e instrucciones para trabajar en el proyecto.
- [`.agents/estado-prototipo.md`](.agents/estado-prototipo.md): decisiones, avance y próximo paso para continuar entre sesiones.
- [Especificación](docs/Especificacion-prototipo-v1.md) y [plan de tareas](docs/Plan-tareas-prototipo-v1.md): primera demostración aprobada y enlaces a las issues de GitHub.
- [`CONTEXT.md`](CONTEXT.md) y [`docs/adr/`](docs/adr/): vocabulario y decisiones de diseño.

La raíz `Tesis/` es la raíz del repositorio Git. Abrir `Plataforma/` en Android Studio para ejecutar la app. Simón v0.2.1 incorpora entrada rápida y pantallas horizontales sin scroll. Su [registro de verificación](docs/Verificacion-Simon-v0.2.1.md) distingue las pruebas del emulador de la comprobación pendiente de la nueva versión en el S22 Ultra.

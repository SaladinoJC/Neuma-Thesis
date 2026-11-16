# Especificación: acabado transversal Music Garden

Síntesis y división confirmadas por el autor con «Ok, hagamos eso», 16/11/2026.
Origen detallado: `Acabado-Music-Garden.md`, Q1–Q6. Aceptación del alcance visual
y del orden de trabajo; no acredita validación nativa ni eficacia pedagógica.

## Problem Statement

Neuma combina un menú de libro ilustrado con interiores de distintas etapas,
incluidos estilos oscuros heredados. Esa discontinuidad dificulta reconocer una
experiencia de juego coherente y deja pantallas auxiliares con acabado desigual.

## Solution

Extender Music Garden a todos los recorridos existentes de Garden. Preparación,
resultados, historial y progreso se presentan como páginas de papel del libro;
las partidas tienen escenarios propios y controles claros. Reorganizar la
jerarquía de información sin cambiar reglas musicales, audio, DDA ni datos.

## User Stories

1. Como jugador, quiero reconocer el mismo mundo visual al pasar del libro a una actividad.
2. Como jugador, quiero entender qué voy a practicar y cómo empezar.
3. Como jugador, quiero distinguir progresión automática y práctica manual.
4. Como jugador, quiero reconocer el nivel seleccionado sin depender solo del color.
5. Como jugador, quiero jugar Instrumentos en un invernadero que no revele la respuesta.
6. Como jugador, quiero responder durante la melodía y conservar las reglas de reescucha.
7. Como jugador, quiero ver una devolución clara de acierto o error y saber cómo continuar.
8. Como jugador, quiero revisar resultados y progreso sobre páginas legibles.
9. Como jugador, quiero acceder al historial de Instrumentos y regresar al recorrido de origen.
10. Como jugador, quiero probar ritmo en una terraza con partitura y pad despejados.
11. Como jugador, quiero probar intervalos en un claro de canto con estados de captura comprensibles.
12. Como jugador, quiero reconocer el árbol, las polillas y las hojas de vida en Defender.
13. Como jugador, quiero conservar el piano horizontal de Simón dentro del claro del jardín.
14. Como jugador, quiero que afinador, datos e inspectores compartan la misma legibilidad.
15. Como jugador, quiero leer errores de permisos, señal o guardado y tener acciones reales de recuperación.
16. Como jugador, quiero usar texto ampliado y controles accesibles en pantallas pequeñas.
17. Como jugador, quiero reducir movimiento sin cambiar evaluación ni tiempos musicales.
18. Como jugador, quiero actualizar la app conservando historial y progreso.

## Implementation Decisions

- Mantener Kotlin/Compose, temas y tokens existentes; introducir componentes
  compartidos solo cuando el recorrido los necesite, sin bibliotecas nuevas.
- Identidad fija: papel cálido, verdes naturales, Fraunces/Nunito Sans,
  ilustración botánica 2D. Notación siempre desde datos estructurados.
- Escenarios: invernadero Instrumentos; terraza Rhythm Tap y claro de canto vocal
  elegidos para probar; árbol/polillas Defender y claro/piano Simón conservados.
  Las elecciones no equivalen a aprobación de una composición concreta.
- Preservar contenido musical, sesiones, políticas DDA, audio y formatos de
  persistencia. Ninguna decoración modifica pistas, respuestas o tiempos.
- Conservar estructura del libro y Simón horizontal. Quiet Studio permanece como
  variante histórica instalable, sin una migración estética general.
- Mantener los inspectores disponibles en accesos secundarios y los errores
  funcionales explícitos. Permitir scroll y crecimiento de controles con texto.
- Registrar cualquier recurso nuevo con editable, procedencia, estados y tamaño.
- Entregar seis incrementos demostrables: Instrumentos; ritmo; vocal; Defender;
  Simón; afinador/superficies auxiliares y revisión transversal. Los incrementos
  2–5 dependen de la referencia compartida establecida en 1; 6 cierra todos.

## Testing Decisions

- Confirmado el enfoque de validación visible presentado en la síntesis: probar
  recorridos públicos, acciones, estados y resultados, sin tests de detalles internos.
- Reutilizar las pruebas existentes de sesión/audio/DDA/persistencia como regresión
  y ampliar pruebas de UI solo para comportamiento nuevo significativo.
- Comprobar compilación, lint y APK de Garden; comprobar compatibilidad de compilación
  de Quiet Studio cuando se toquen fuentes compartidas.
- Revisar preparación, escucha, respuesta, feedback, finalización, interrupción,
  historial, vacío, carga y error; 411/360/320 dp y texto ampliado al 200 %.
- No abrir emulador por la restricción vigente. Distinguir revisión de código,
  estudio visual, compilación, pruebas JVM y ejecución nativa en teléfono.
- Ni un estudio visual ni las JVM acreditan legibilidad nativa, sincronización
  acústica, TalkBack, rendimiento físico o eficacia pedagógica.

## Out of Scope

Nuevas mecánicas o ayudas musicales, piano de Defender, nuevos desbloqueos o
medallas, implementación del personaje pausado, migraciones de perfil, rediseño
general Quiet Studio, cambios de audio/DDA y publicación de una versión de
producción. **Definir e incorporar sonidos de interfaz y respuesta táctil queda
pendiente para otro incremento.**

## Further Notes

El orden confirmado evita una reescritura general antes de mostrar un recorrido
completo. Las revisiones en teléfono y posibles ajustes de composición siguen
siendo parte del cierre; no marcar una tarea totalmente validada por compilar.
Tiempo personal en H0014, pendiente de información del autor.

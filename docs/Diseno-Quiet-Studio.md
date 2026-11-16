# Neuma — acuerdo visual Quiet Studio

**Actualización 12/11/2026:** el autor autoriza las dos aplicaciones Neuma Garden
y Neuma Quiet Studio para comparar con su director el 13/11. Acepta la propuesta
de menú y Rhythm Tap. Esta entrega acota la migración a ese recorrido y conserva
Garden completo; no decide una sustitución global. Implementación, respaldo,
compilación y límites en [la comparación 0.12.24](Comparacion-Garden-Quiet-Studio-v0.12.24.md).
Las secciones siguientes conservan el acuerdo original del 31/10.

Fecha: 2026-10-31. Origen: entrevista con el usuario, Q1–Q13.

## Estado y alcance

El usuario solicita migrar temporalmente de Music Garden a Quiet Studio y
conservar Music Garden por si cambia de opinión. Esta decisión sustituye la
obligatoriedad anterior de Music Garden para la próxima migración; no implica
que la interfaz Android ya haya cambiado ni aprobación íntegra de `preview/`.
La entrevista se cierra por pedido del usuario: «Cerremos acá que me tengo que ir».
No se implementó la migración durante esta sesión.

## Decisiones acordadas

- Experiencia de estudio musical íntimo y táctil, tranquila y agradable a los
  sentidos; evitar una plataforma educativa blanca genérica.
- Colores claros cálidos, sin blancos puros. Materialidad de cerámica mate,
  fieltro y pequeños acentos de madera clara; avena, lino, salvia grisácea y
  arcilla apagada como dirección. Valores de tokens y acabado exacto pendientes.
  Texto y notación deben mantener contraste firme.
- Inicio con acceso inmediato al catálogo, sin práctica protagonista por ahora.
  Lista vertical de entradas amplias, nombre, frase breve y elemento visual
  propio; varios juegos visibles, sin recorrido obligatorio de a uno.
- Quietud como base y movimiento al interactuar. No animación ambiental continua.
  El movimiento necesario para jugar se mantiene y se respeta reducción de movimiento.
- Respuestas sonoras y vibraciones breves y discretas, configurables por separado.
  En Simón, vibración mínima al tocar el usuario una tecla; reproducción de la
  secuencia sonora y visual sin vibración. En lectura, respuesta distinta al
  resolver un objetivo y una sola respuesta compacta para una resolución múltiple.
  No superponer efectos decorativos a referencias musicales, ritmo o captura vocal.
- Mantener inicialmente las reglas de los juegos: cambiar representación y
  comunicación, sin alterar tiempos, oportunidades, evaluación ni progresión.
  Feedback preciso sin castigo audiovisual ni presión por rachas diarias.
- Defender abandona bichos y la idea de proteger. Explorar **cuerdas que entran
  en resonancia**. La correspondencia visual con objetivos, vencimientos, acordes
  y respuestas todavía debe diseñarse; no sustituir esta elección por las piezas
  suspendidas propuestas anteriormente por el agente. Nombre definitivo pendiente.
- Resultado: cierre breve, resultado útil, repetir o salir; métricas desplegables.
  Resolución visual, sonora y táctil discreta, sin confeti ni retención obligatoria.
- Personaje conservado, pausado y fuera de la navegación de la nueva interfaz.
- Primera prueba: **Catálogo → Ritmo → Resultado** o **Catálogo → Defender → Resultado**.
  El usuario no eligió entre esos dos recorridos. No empezar por Simón como se
  había recomendado en la entrevista.

## Conservación y próxima sesión

Conservar código, tokens, fuentes, ilustraciones, maestros, licencias,
documentación y evidencias de Music Garden. Antes de modificar implementación,
fijar una referencia Git verificable y documentar recuperación de la versión
completa. Existe la etiqueta `v0.12.20-preview.1`, pero no asumir que incluye todo
el trabajo posterior. Un selector de temas en ejecución no fue solicitado.

`preview/neuma-concept.html` es referencia de exploración: contiene blancos y
diferencias funcionales respecto de Android. No copiar su flujo de ritmo de dos
compases con escucha previa ni su Defender simplificado como reglas de producción.

Próximo paso: retomar desde este acuerdo, revisar componentes/tokens Compose,
elegir o proponer uno de los dos recorridos permitidos y preparar una prueba
concreta. Antes de implementar, actualizar las instrucciones de identidad en
`AGENTS.md`, la dirección de arte y el inventario sin borrar los antecedentes.
Validar tamaños, texto ampliado, contraste, notación, movimiento reducido y estados;
sonido/vibración requieren revisión en dispositivo. No atribuir sensaciones o
eficacia a pruebas que todavía no se hicieron.

Pendientes funcionales independientes: selección automática de ayuda vocal,
Defender D6/#28 y D7/#29, validación Android y voz real. La lista de issues abiertas
requiere conciliación con implementaciones existentes antes de tratarlas como
trabajo faltante. Esta sesión no ejecutó emulador, pruebas ni publicaciones.

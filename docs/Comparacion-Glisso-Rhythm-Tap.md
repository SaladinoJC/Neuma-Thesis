# Glisso Rhythm y Rhythm Tap: comparación y mejoras propuestas

Fecha de observación: 2026-10-26. Rhythm Tap: versión 0.12.2, commit `fb54fb5`. Este análisis propone mejoras; no cambia requisitos aprobados, prioridades de tickets ni código de la app.

## Qué se comprobó

Se recorrió [Glisso Rhythm](https://glisso.net/rhythm) en Chrome headless, como invitado, en escritorio 1280×900 y móvil emulado 390×844 con user agent Android. Se abrieron calibración, ayuda, niveles, configuración personalizada y ajustes; se inició una ejecución, se observaron partitura/indicadores y una finalización sin responder, y se volvió a la práctica mediante Replay. Los valores 0 % y 24 omisiones de la captura son de esa ejecución automatizada, no de un estudiante.

Las capturas de Glisso están en [imagenes/comparacion-glisso-rhythm/](imagenes/comparacion-glisso-rhythm/). Son evidencia del sitio de terceros, cuya autoría corresponde a Glisso. Las capturas propias están en [imagenes/ritmo-r2/](imagenes/ritmo-r2/). También se contrastó el código de [RhythmApp.kt](../Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/ui/RhythmApp.kt), las reglas de sesión y la [especificación vigente](Especificacion-ritmo-v1.md).

No se escuchó ni midió el audio físico, no se completó la calibración ni se verificaron sus algoritmos de puntuación, generación o adaptación. La emulación móvil no acredita funcionamiento en un teléfono real. No se infiere eficacia pedagógica de la interfaz. La [descripción oficial](https://glisso.net/about) presenta práctica rítmica generada y sobre canciones, mediante tap o mantenimiento; eso describe el producto, no valida su precisión.

## Comparación

| Aspecto | Glisso observado | Rhythm Tap implementado | Qué conviene tomar |
| --- | --- | --- | --- |
| Preparación | Barra compacta con modo, nivel y acceso al repertorio; selección personalizada en un diálogo. | Diez categorías en una columna de chips, compás y ayudas; la configuración sigue apareciendo en previa y resultados. Durante la ejecución ya se oculta. | Mostrar configuración completa al preparar y un resumen compacto después de generar. |
| Selección de células | Tarjetas con símbolo y nombre, agrupadas en notas, puntillos/síncopas y grupos irregulares. Silencios mediante un interruptor general en ese diálogo. | Nombres textuales; las categorías de silencios son seleccionables individualmente. | Añadir ejemplos gráficos y agrupación visual, conservando selección individual y procedencia de nuestras categorías. |
| Partitura | Línea rítmica continua con notación musical, métrica, tempo y números de compás; se observó desplazamiento respecto de un marcador. | Cuatro compases, cada uno en un bloque de 100 dp; figuras dibujadas con Canvas y cursor temporal por compás. | Mejorar grabado y aprovechar el ancho. Mantener inicialmente nuestros renglones; comparar antes de adoptar desplazamiento horizontal. |
| Móvil | Partitura arriba; pad grande abajo, separado de reproducción y reinicio; navegación compacta. | Pad fijo de 100 dp durante conteo/ejecución y también deshabilitado al finalizar; partitura en zona desplazable. | Dar más superficie útil al tap y reservar espacio estable a la lectura; recuperar espacio visual al mostrar resultados. |
| Conteo | Se observaron puntos de pulso y un estado de reproducción con botón de detener. | Conteo audible según métrica y texto «n de 2/3/4», seguido de ejecución. | Combinar nuestro conteo con pulsos visuales discretos, sin depender solo del color. |
| Devolución | Notas coloreadas; al finalizar, exactitud y puntaje destacados, omisiones y tempo, acciones New rhythm/Replay y partitura anotada. | Feedback inmediato global y resumen textual detallado por categoría y golpe; la partitura no pinta cada resultado. | Poner primero un resumen breve y las acciones; representar aciertos/errores sobre los ataques correspondientes; desplegar el detalle técnico a pedido. |
| Latencia | Aviso inicial opcional y calibración en ajustes; invita a repetirla al cambiar la salida de audio. | Conservamos procedencia e incertidumbre del reloj; falta validación acústica física y compensación calibrada de la interacción. | Tratar ruta de audio, medición y eventual compensación como un trabajo técnico separado. No copiar un offset arbitrario. |
| Ayudas y modos | Tap/Hold, opción de escuchar el ritmo ideal, sonido de taps y aviso Early/Late. Se observaron sus controles; no se validaron todas sus conductas. | Tap por ataque, metrónomo y sonido del tap opcionales; sin reproducción del patrón ideal. | Tomar claridad de controles. Hold y escucha ideal requieren aprobación como modos distintos. |

Fuentes de los comportamientos de Glisso: observación directa de [Rhythm](https://glisso.net/rhythm), documentada en las capturas. La disponibilidad de niveles manuales no demuestra DDA ni desbloqueos automáticos.

## Mejoras concretas, por orden sugerido

### Primero: organizar el flujo y hacer legible el resultado

Un único incremento de interfaz puede conservar el motor actual y ordenar cuatro estados:

1. **Preparar:** compás, tarjetas de células con símbolo y nombre, ayudas y Generar. Mostrar selecciones y errores de factibilidad sin quitarlas.
2. **Revisar:** partitura protagonista; resumen «4 compases · 3/4 · 60 BPM · 2 categorías», botón Cambiar configuración e Iniciar.
3. **Tocar:** lectura estable, pulso/conteo visible, pad amplio y una acción secundaria de interrupción. Ocultar controles que no se usan durante la ejecución, como ya hacemos.
4. **Revisar resultados:** completado/interrumpido, ataques a tiempo/esperados, omisiones, extras y silencios; Repetir, Nuevo ejercicio y detalle expandible. Repetir conserva exactamente la partitura.

Hay dos correcciones especialmente concretas en el estado actual:

- Una categoría exclusivamente de silencios puede mostrar **«0 / 0 a tiempo»** porque no contiene ataques, aunque debajo sí se informa su evaluación de silencio. Allí debe encabezar «Silencios respetados: x/y»; las categorías mixtas muestran ambas métricas. No inventar porcentajes con denominador cero.
- En resultados reaparece la selección completa antes de llegar al resumen y permanece un pad deshabilitado de 100 dp. Conviene priorizar el resultado y recuperar ese espacio. Al implementarlo hay que preservar el contrato de recepción de eventos táctiles tardíos de la sesión actual.

Esto es una mejora de presentación, sin cambiar márgenes temporales, semilla, cuotas, ayudas, conteo, clasificación de taps ni reglas de práctica.

### Después: partitura que se parezca más a una partitura

La ganancia no está en reproducir los colores de Glisso, sino en sus símbolos reconocibles, agrupación y jerarquía musical. En su página se observaron fuentes musicales cargadas, incluida Bravura; esto no permite afirmar qué biblioteca o fuente dibuja cada elemento.

Proponer para Android un componente común de notación: símbolos consistentes, plicas y corchetes cuidados, puntillo separado, compás y tempo integrados, marcación por nota y tamaño de lectura ajustable. Evaluar recursos tipográficos con licencia compatible antes de incorporarlos.

Probar uno o dos compases por renglón **según ancho y densidad**, no comprimir siempre dos: ocho corcheas necesitan más espacio que una redonda. El cursor debe seguir la misma línea temporal aunque cambie la distribución gráfica. Conservar anticipación visual del compás siguiente y evitar saltos de scroll que distraigan. Este cambio merece su propia verificación a 360/411 dp y con casos densos.

### Trabajo técnico separado: confianza temporal

El mensaje de calibración de Glisso reconoce un problema que también tenemos que evaluar. Una calibración mediante taps mezcla retraso de audio, entrada y respuesta del usuario; no debe presentarse como medición aislada del hardware. Primero medir y registrar condiciones en el S22 Ultra; después decidir compensación, método repetible y asociación con la salida de audio. No ampliar tolerancias para esconder latencia ni reutilizar automáticamente una corrección al pasar de altavoz a Bluetooth.

## Qué no trasladar automáticamente

- **Hold:** hoy evaluamos ataques, no duración sostenida. Cambia eventos, modelo, criterios y pruebas.
- **Escuchar el patrón:** está excluido del flujo vigente de lectura. Podría investigarse como ayuda diferenciada, registrando su uso, si el usuario lo aprueba.
- **Ocho compases y catálogo avanzado:** Glisso muestra ocho compases y niveles hasta ocho, pero nuestra entrega actual tiene cuatro y ampliaciones R3–R6 previstas. No confundir amplitud de catálogo con mejor aprendizaje.
- **Un puntaje único como sustituto del diagnóstico:** es útil para motivar, pero no reemplaza nuestros desvíos, errores, silencios y desempeño por categoría.
- **Rachas, clasificaciones, canciones y cuentas:** no son necesarias para mejorar el Rhythm Tap local actual.

Glisso tampoco debe copiarse literalmente: en escritorio deja mucha superficie vacía y algunos controles dependen de iconos o ayuda para descubrirse. Nuestra acción de inicio explícita y el detalle explicable por categoría son valiosos. La propuesta es combinar su claridad visual con esas fortalezas, sin atribuir resultados educativos a ninguna de las dos interfaces.

## Propuesta de siguiente paso

Si se decide priorizar esta comparación, preparar **un solo ticket de interfaz de Rhythm Tap**, centrado en preparación/previa/resultados y selector gráfico, con las mismas reglas y las mismas partituras de prueba. El grabado de notación y la calibración quedarían en incrementos posteriores. No se creó ese ticket ni se alteró el próximo D2 registrado: esta priorización sigue siendo una recomendación.

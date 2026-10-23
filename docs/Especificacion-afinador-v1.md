# Afinador de instrumentos con entrenamiento de oído — especificación v1

Estado: alcance funcional, puntos de prueba y división en seis incrementos aprobados por el usuario el 2026-10-22. A1 implementado en software en v0.11 el 2026-10-23; A2–A6 y prueba física pendientes. [Verificación de A1](Verificacion-afinador-v0.11.md).

Publicada como [issue #8](https://github.com/SaladinoJC/Tesis/issues/8); [plan de implementación y tickets #9–#14](Plan-tareas-afinador-v1.md).

Origen: [definición y entrevista](Definicion-afinador.md). Plataforma y tecnología vigentes: Android, Kotlin/Compose y procesamiento local. El DDA Mamdani de los juegos permanece vigente; este afinador no incorpora DDA inicialmente.

## Problema

Afinar mirando continuamente una medición puede convertirse en una tarea mecánica sin ejercitar la comparación auditiva. El usuario también encuentra reconocimientos de nota incorrectos y quiere una alternativa guiada que reduzca confusiones de cuerda, compruebe el resultado completo y permita observar su evolución.

## Solución

Afinador offline para guitarra de seis cuerdas con tres modos: Libre, Guiado y Entrenamiento. Mantiene el uso cromático habitual y añade práctica de oído como función protagonista. Las referencias sonoras, la comprobación final, el seguimiento y la clasificación auditiva previa se configuran sin obligar a realizar un ejercicio para afinar.

Primero guitarra; ukelele, bajo, violín, viola, cello y otros instrumentos se definirán en ampliaciones. Nombrar varias guitarras no implica soporte validado de otras familias.

## Historias de usuario

1. Como guitarrista, quiero un afinador cromático libre para afinar sin comenzar una práctica.
2. Como guitarrista, quiero reconocimiento automático de cuerda según mi afinación seleccionada para evitar seleccionarla en cada toque.
3. Como guitarrista, quiero fijar una cuerda cuando el reconocimiento no funciona bien, conservando su objetivo aunque cambie la nota detectada.
4. Como guitarrista, quiero ver nota medida, nota objetivo y desviación para interpretar qué estoy corrigiendo.
5. Como guitarrista, quiero elegir Normal o Pro con el margen visible para conocer la exigencia aplicada.
6. Como guitarrista, quiero elegir estándar, Drop D, medio tono abajo, Drop C, Open G o DADGAD.
7. Como guitarrista, quiero ingresar nota y octava por cuerda y guardar mi afinación con un nombre.
8. Como guitarrista, quiero registrar guitarras con nombres distintos para separar sus mediciones.
9. Como usuario nuevo, quiero un instrumento predeterminado para comenzar sin crear perfiles.
10. Como guitarrista, quiero un recorrido guiado de sexta a primera con avance automático cuando cada cuerda quede afinada de forma estable.
11. Como guitarrista, quiero un aviso de posible cuerda equivocada si estoy muy lejos del objetivo.
12. Como guitarrista, quiero activar una segunda pasada para comprobar el conjunto después de los ajustes.
13. Como estudiante, quiero escuchar la referencia con timbre de guitarra, piano o tono puro en la nota y octava objetivo.
14. Como estudiante, quiero que el timbre del instrumento sea la referencia predeterminada.
15. Como estudiante, quiero repetir la referencia y ajustar de oído sin aguja, pistas ni avisos hablados.
16. Como estudiante, quiero decidir cuándo comprobar, ver qué nota afiné y conocer el resultado de mi intento.
17. Como estudiante, quiero corregir con ayuda visual después de comprobar y continuar con la cuerda siguiente.
18. Como estudiante, quiero que una corrección no convierta retroactivamente un intento errado en un acierto de oído.
19. Como estudiante, quiero activar una clasificación previa grave/agudo/afinado para practicar reconocer la diferencia antes de ajustar.
20. Como estudiante, quiero un resumen por cuerda con resultados iniciales y posteriores para comprender mi sesión.
21. Como estudiante, quiero seguir mi progreso y filtrar por guitarra, afinación y criterio para comparar prácticas equivalentes.
22. Como guitarrista, quiero consultar mediciones del instrumento separadas de mis resultados de oído.
23. Como usuario, quiero desactivar el seguimiento y ver el resumen sin que se guarde la sesión.
24. Como usuario, quiero que se recuerden mis preferencias y mis afinaciones guardadas.
25. Como usuario, quiero que silencio, señal insuficiente o fallo del micrófono no se contabilicen como errores de oído.
26. Como usuario, quiero salir o pausar sin perder los resultados ya guardados ni registrar una sesión incompleta como completada.
27. Como usuario del prototipo, quiero conservar los historiales existentes al actualizar y poder exportar los nuevos resultados.

## Decisiones de implementación y contratos de conducta

Las conductas confirmadas se distinguen de las concreciones técnicas provisionales del agente que siguen. Estas concreciones permiten implementar y probar, sin atribuirles calibración empírica.

### Afinaciones y objetivos

- Seis objetivos ordenados por número de cuerda. Guardar altura exacta con octava; no deducir el orden por frecuencia, porque una afinación personalizada puede no ser ascendente.
- Presets de trabajo, sexta → primera: estándar E2–A2–D3–G3–B3–E4; Drop D D2–A2–D3–G3–B3–E4; medio tono abajo E♭2–A♭2–D♭3–G♭3–B♭3–E♭4; Drop C C2–G2–C3–F3–A3–D4; Open G D2–G2–D3–G3–B3–D4; DADGAD D2–A2–D3–G3–A3–D4.
- Provisional del agente: temperamento igual, A4=440 Hz, coherente con el estimador actual. La elección de otra referencia de concierto queda fuera de v1.
- Validar que las seis alturas sean representables tanto por captura como por las referencias disponibles. Rechazar una configuración no soportada con explicación; no cambiar la octava silenciosamente. El rango soportado se documenta tras verificarlo; no se promete cualquier frecuencia.
- Cada sesión conserva una copia identificable de su afinación, criterio y configuración. Editar una afinación guardada no reinterpreta sesiones anteriores. Distinguir identificación de cuerda y estimación de frecuencia; la cuerda fijada no obliga al estimador a declarar afinada una señal incompatible.

### Criterios Normal y Pro

- Normal: desviación absoluta ≤5 cents. Pro: ≤2 cents. Son criterios experimentales aceptados por el usuario para probar.
- Avance al permanecer dentro del margen durante un segundo de mediciones válidas. Usar tiempo monótono y una política de continuidad versionada. Una medición fuera de margen, inválida o una discontinuidad de captura reinicia la permanencia; no acumular intervalos desconectados ni reutilizar lecturas de otra cuerda.
- La resolución que muestra la pantalla no acredita la exactitud del estimador. Pro no garantiza precisión de concierto.
- Elegir criterio antes del recorrido; un cambio de criterio o afinación durante él requiere reiniciar el recorrido, sin mezclar resultados. Es una concreción provisional del agente.

### Libre y Guiado

- Libre distingue cromático de seguimiento de las seis cuerdas. En automático puede mostrar la nota detectada sin asignar cuerda si la correspondencia es ambigua. Permite fijar/liberar objetivo manualmente, sin forzar otra octava para hacer coincidir el resultado.
- Guiado sigue sexta → primera con medición visible y avance automático. Una medición de la cuerda anterior no puede completar la siguiente.
- Durante los recorridos, una lectura confiable muy lejana al objetivo produce «Muy lejos, revisar cuerda correcta». Es advertencia, no prueba de qué cuerda se tocó. Umbral, persistencia e histéresis quedan como configuración técnica provisional que debe documentarse y probarse, incluyendo errores de octava y armónicos. No se promete evitar roturas.
- La comprobación final opcional recorre nuevamente las seis cuerdas. Las que no superan el criterio quedan pendientes de corrección. Solo declarar comprobado el conjunto al terminar la revisión; no contar esa revisión como otro intento independiente de oído.

### Referencias sonoras y captura

- Tres timbres seleccionables: instrumento afinado (guitarra en v1), piano y tono puro. Instrumento predeterminado significa referencia preparada del tipo de instrumento; no clonar automáticamente el timbre de la guitarra personal.
- Misma altura/octava objetivo en los tres timbres. Documentar fuente, permiso, procesamiento y comprobación de afinación de los assets antes de incorporarlos. El piano existente comienza en un registro más agudo que parte de la guitarra; su existencia no acredita cobertura de estos objetivos.
- Suspender análisis/captura antes de reproducir referencia y reanudar después sin evaluar la referencia ni muestras pendientes. No convertir esa suspensión en acierto, error o tiempo estable acumulado. El margen de reanudación debe verificarse en Android; no basta un callback de reproducción.
- Permiso e inicio de escucha explícitos; detener recursos al salir o ir a segundo plano. No guardar audio PCM; guardar únicamente resultados derivados si el seguimiento está activo. Concreción técnica que conserva la política actual del prototipo.

### Entrenamiento

1. Elegir guitarra, afinación, precisión y preferencias. Comenzar por sexta cuerda.
2. Si se activó clasificación previa: escuchar referencia y cuerda sin revelar medición, responder grave/agudo/afinado y registrar esa clasificación por separado del ajuste posterior. El instante de referencia de esa respuesta debe ser una medición válida previa al ajuste; sin lectura válida se solicita repetir. Esta temporización es una concreción provisional del agente para que el resultado sea verificable.
3. Repetir referencia y ajustar de oído libremente, con micrófono activo. Ocultar nota medida, frecuencia, cents, clasificación y estados de acierto. Únicamente se permite el aviso excepcional de posible cuerda equivocada; sin pistas de dirección ni voz.
4. «Comprobar» descarta las mediciones previas como evidencia de evaluación. Pedir tocar y tomar la primera medición válida y estable posterior al botón; conservar nota, desviación con signo y resultado según el criterio seleccionado.
5. Estabilidad para evaluar un error no exige estar afinado: se necesita una señal estable aunque esté fuera de margen. Su definición y duración son configuración técnica versionada, separada del segundo de permanencia dentro de margen requerido para avanzar. No esperar a que se afine para evaluar el intento.
6. Revelar medición y permitir corregir con ayuda visual. Un intento válido fuera de margen cuenta como error y permanece así aunque se corrija. Lecturas inválidas no consumen un intento ni se convierten en cero cents. Impedir que pulsaciones repetidas de «Comprobar» sobre la misma cuerda creen nuevos intentos de oído tras revelar la respuesta.
7. Avanzar cuando cumpla el segundo estable de afinación. No registrar un resultado asistido como primer acierto de oído. Al cerrar, mostrar resumen y, si corresponde, realizar comprobación final separada.

### Seguimiento, preferencias y compatibilidad

- Preferencias persistidas: modo, precisión, referencia y opciones; no reabrir automáticamente el micrófono por recordar el modo. Comprobación final y seguimiento activos inicialmente en guiado/entrenamiento; clasificación previa desactivada.
- Seguimiento desactivado: resumen y corrección funcionan en memoria, sin guardar mediciones o resultados de esa sesión ni enviarlos a historiales de voz. Guardar preferencias y afinaciones no equivale a guardar desempeño. La opción se toma al comenzar la sesión; cambiarla para el siguiente recorrido evita persistencia parcial confusa.
- Con seguimiento: sesión con identificador único, completa/interrumpida, instrumento, copia de afinación, modo, criterio y versión de parámetros, referencia, fecha, dispositivo y configuración de captura. Por cuerda: intento inicial, clasificación opcional, resultado asistido y comprobación final en apartados diferentes, con validez y tiempos. No inventar datos ausentes en registros previos.
- Progreso de oído: aciertos sobre intentos válidos, desviación inicial absoluta y con signo por cuerda, clasificación opcional y evolución temporal. Una clasificación incorrecta y un ajuste incorrecto son métricas separadas. No atribuir mejora causal ni eficacia pedagógica a una tendencia.
- Seguimiento del instrumento: comparar mediciones de etapas equivalentes, filtradas por guitarra, afinación, cuerda y criterio. No interpretar lecturas finales después de corregir como prueba de cuánto se desafinó entre sesiones. El entrenamiento no mide por sí solo la afinación previa al ajuste.
- Persistencia idempotente, errores de guardado visibles y reintento sin duplicados. Exportación, recuperación de originales y reinicio incorporan los nuevos datos manteniendo compatibilidad con la entrega v0.10. Una sesión interrumpida no se completa automáticamente; al recrear la interfaz no duplicar resultados.
- Reutilizar captura, estimador, reproducción y almacenamiento en sus límites cuando resulte adecuado. Reglas de afinación independientes de Android/Compose. No reutilizar el guardado automático de la prueba de voz como si fuera una sesión del afinador.

## Puntos y decisiones de prueba propuestos

### 1. Sesión pública del afinador

Acciones del usuario y lecturas de tono con tiempo controlado → estado visible, objetivos, evaluación, resumen y persistencia. Audio, captura y repositorios se sustituyen solo en sus límites; comprobar conducta externa, sin inspeccionar estructuras privadas. Preferir una sesión pública común para los tres modos.

Comprobar: fijación y avance, ambos márgenes y sus fronteras, permanencia, discontinuidades, no revelar pistas, intento erróneo seguido de corrección, repetición de botón, lectura inválida, clasificación previa, revisión final, preferencias, seguimiento apagado, guardado fallido e interrupciones. Antecedentes del repositorio: pruebas de sesiones de instrumentos y pruebas de captura/referencia/guardado de voz.

### 2. PCM → estimación, y flujo real en Android

Mantener el límite existente PCM → estimación: señales de frecuencia conocida en el rango de presets, márgenes de ambos criterios, transitorios, armónicos, silencio/ruido y errores de octava. Registrar configuración y resultados; sintéticos no equivalen a guitarra real.

En Android: permisos, captura real, reproducción de los tres timbres, exclusión referencia/captura, pausa/salida/recreación, persistencia y exportación. Ampliar pruebas existentes de referencia y resiliencia; no repetir comprobaciones sin relación con el cambio.

Prueba física reproducible: S22 Ultra, versión de app/Android, guitarra y afinación, entorno, distancia al micrófono, ruta de audio, instrumento patrón o fuente de frecuencia conocida y método de comparación. Probar especialmente sexta cuerda, confusiones de octava, variación tras ataque, Pro y audibilidad de referencias graves. Sin patrón no afirmar exactitud de ±2 cents sobre guitarra real. Emulador y pruebas sintéticas se reportan por separado; el teléfono pendiente no se da por probado.

## Fuera de alcance

- Otros instrumentos o cantidad de cuerdas; modos polifónicos o detección simultánea de todas las cuerdas.
- Clonación del timbre personal, diagnóstico mecánico o garantía de protección de cuerdas.
- ML, DDA, cuentas remotas, nube, ranking o grabaciones de audio.
- Afirmar novedad comercial, precisión profesional o eficacia pedagógica sin evaluación.
- Rhythm Tap, generador/renderizador y Defender: se retoman después.
- Actualizar el protocolo oficial o presentar la ampliación como aprobación del director.

## Notas y trazabilidad

Se consultaron «OBJETIVO DEL PROYECTO» y «PRODUCTO ESPERADO» del protocolo Word disponible mediante lectura de su contenido OLE. El documento centra la evaluación en respuestas vocales, latencia y ajuste de dificultad; su selección inicial de juegos no incluye explícitamente un afinador instrumental. Esta especificación registra la ampliación solicitada por el usuario, sin sustituir ese eje ni atribuir aprobación del director. No modifica el protocolo ni fija fechas o carga horaria.

La entrevista fue confirmada por el usuario mediante «ok». Los puntos de prueba y el plan de seis incrementos fueron aprobados mediante «Perfecto», autorizando su publicación en GitHub. Los parámetros experimentales y decisiones técnicas provisionales deben seguir identificados así al implementar y medir.

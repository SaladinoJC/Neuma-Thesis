# Iteraciones posteriores a v0.5

Estado al 2026-10-21: **seis incrementos implementados y verificados**, hasta v0.10. 63 JVM y 22 Android por cada API 34/36 aprobadas; [registro y artefactos](Verificacion-iteraciones-post-v0.5.md), [guía actual](Demostracion-prototipo-v0.10.md). La prueba física sigue pendiente. El texto siguiente conserva el plan que autorizó esta ejecución.


Fecha: 2026-10-21. El usuario pidió seguir iterando y preparar varios incrementos que puedan trabajarse sin su intervención ni pruebas inmediatas en el teléfono. Este documento define el trabajo propuesto; las versiones indicadas son entregas futuras, no funcionalidades ya implementadas ni decisiones del director.

## Objetivo y forma de trabajo

Mejorar la demostración y obtener evidencia técnica reproducible sobre el audio y el DDA. Mantener Android/Kotlin/Compose, funcionamiento offline, los dos juegos existentes y un perfil local. La elección de otros minijuegos sigue pendiente. Se conserva la validación física del S22 Ultra como pendiente independiente: no bloquea estos incrementos y no se sustituye con resultados de emulador.

Antes del primer incremento, conservar v0.5 como referencia con APK/hash, historial de cambios y un checkpoint Git local de los cambios ya verificados, sin incluir archivos ignorados. No publicar en remoto ni cerrar tickets como parte de esta planificación. GitHub #3–#7 siguen abiertos aunque sus incrementos de software estén implementados; no crear duplicados del trabajo anterior.

Cada incremento debe terminar con una conducta ejecutable, pruebas pertinentes, APK actualizable y documentación de verificación. Actualizar `.agents/estado-prototipo.md` con lo terminado y el siguiente incremento. Resolver elecciones reversibles del agente sin reabrir la entrevista; registrar parámetros nuevos como provisionales. Conservar los formatos históricos y el paquete/firma de instalación.

## 1. v0.5.1 — Operaciones y progreso claros

**Entrega:** exportar, cancelar, reiniciar y consultar el progreso con respuestas visibles y consistentes.

- Mostrar confirmaciones y errores mediante un Snackbar de Scaffold, visibles sin desplazar la pantalla. Mantener un estado consultable de la última operación y evitar repetir notificaciones por recomposición.
- Presentar nivel actual, próximo desbloqueo y racha en la preparación y el resumen de ambos juegos; indicar último nivel cuando corresponda. Explicar la exclusión de práctica manual y simulación en textos breves.
- Asegurar que operaciones ocupadas bloqueen acciones incompatibles y que volver del selector o recrear la pantalla no deje un indicador de carga permanente. Si Android destruye el proceso durante el selector, informar que debe iniciarse otra exportación; no escribir un archivo vacío como éxito.
- Conservar las reglas de puntuación, ayuda y dominio de v0.5.

**Cierre:** pruebas de exportación exitosa/cancelada/fallida, recomposición y recreación; confirmación visible con contenido largo; navegación y progreso correctos en primer/último nivel. Verificación visual en emulador, sin exigir S22 Ultra.

## 2. v0.6 — Historial y detalle de resultados

**Entrega:** acceder a todos los resultados guardados y entender cómo se obtuvo cada uno.

- Añadir una pantalla de resultados desde el menú. Listar resultados por juego y permitir filtrar por nivel y origen automático/manual; ordenar por fecha y cargar la presentación en bloques de 20, sin borrar registros antiguos.
- Detalle de Simón: escala/tónica, origen, completo/interrumpido, longitud completada, secuencia intentada y error; detalle de instrumentos: respuestas, melodía, escuchas y decisiones DDA. Reutilizar la inspección de dominio existente.
- Incluir sesiones de voz con dispositivo/configuración, duración, cantidad/proporción de estimaciones válidas y resumen de cents válidos y tiempos de procesamiento. Usar «desviación respecto del objetivo», sin atribuirla a error del detector sobre voz desconocida.
- Identificar registros antiguos que no tienen dominio, melodías o trazas. Nunca completar esos datos con resultados inventados. Las simulaciones continúan fuera del historial real.

**Cierre:** historiales vacíos, antiguos y con más de 20 entradas; filtros, fechas empatadas e interrupciones; detalle conservado al recrear; resultados originales sin cambios después de consultarlos.

## 3. v0.7 — Laboratorio reproducible de YIN

**Entrega:** ejecutar comprobaciones de señales conocidas sin micrófono y exportar sus resultados por separado.

- Añadir «Pruebas sintéticas» dentro de la sección técnica. Reutilizar `YinEstimator` con la configuración de producción identificada y ejecución cancelable fuera de Main. No reproducir audio ni generar registros de micrófono.
- Banco limpio inicial: 55, 65,406, 82,407, 110, 146,832, 220, 261,626, 440, 523,251, 880 y 1.000 Hz; amplitudes 0,02/0,1/0,4; fases 0 y π/3. Evaluar cuatro ventanas consecutivas completas por combinación y registrar frecuencia realmente generada, configuración y semilla.
- Agregar silencio/DC/PCM inválido, transiciones nota–silencio y, en 110/220/440 Hz, señales armónicas y ruido con SNR 20/10/0 dB y semilla 42. Documentar la fórmula exacta y la normalización del generador antes de comparar.
- Reportar proporción válida, error absoluto en cents cuando hay referencia, errores de octava y distribución de tiempo de procesamiento. Distinguir pruebas matemáticas de mediciones de ejecución en JVM/emulador.
- Mantener el criterio ya aprobado de ≤10 cents para las tres senoidales limpias de 110/220/440 Hz y rechazo de silencio. Los demás casos caracterizan límites; un caso desfavorable debe aparecer en el informe, sin ajustar silenciosamente los umbrales para ocultarlo.
- Persistir únicamente parámetros y resultados derivados en un archivo separado. Añadir un campo opcional `syntheticPitchTests` a la exportación y abarcar ese archivo en el reinicio, conservando lectores de historiales existentes. Generar también un informe Markdown para revisión fuera de la app.

**Cierre:** resultados reproducibles con igual entrada/configuración; cancelación sin afectar el perfil; separación de orígenes; informe exportado parseable. No guardar los buffers PCM generados.

## 4. v0.8 — Pantalla de voz más útil

**Entrega:** escuchar el objetivo y ver la evolución de la desviación con claridad.

- Añadir «Escuchar objetivo» mediante un tono senoidal sintetizado desde la frecuencia del objetivo: 600 ms, 44.100 Hz y fades de 20 ms como parámetros provisionales. Mantener todos los objetivos actuales sin descargar muestras nuevas.
- Antes de reproducir la referencia, detener y esperar la captura/guardado. Reproducir solo en primer plano y liberar al pausar o salir. El usuario inicia explícitamente la siguiente captura, evitando registrar la referencia del altavoz como su voz.
- Mostrar un indicador centrado de cents y una gráfica de los últimos diez segundos usando las mediciones derivadas existentes. El número conserva la desviación completa; el indicador gráfico acota su escala a ±100 cents y marca cuando queda fuera.
- Representar ausencia de tono con huecos, sin conectar tramos a través del silencio ni mantener una nota anterior como actual. Mantener los valores crudos del estimador en exportación; esta entrega no añade suavizado ni una nota pedagógica de afinación.
- Separar los detalles de RMS/periodicidad/tiempo en un panel técnico desplegable. Conservar resumen de la última captura después de detenerla, sin indicar micrófono activo.

**Cierre:** tono sintetizado verificado por frecuencia/duración y reproducción Android; exclusión entre referencia y captura; pausa, salida y cambio de objetivo; gráfica con silencio, valores extremos y final de captura. Audibilidad física y utilidad al cantar quedan pendientes de teléfono.

## 5. v0.9 — Banco de escenarios y análisis del DDA

**Entrega:** demostrar y revisar ayuda, dominio y progresión paso a paso, sin partidas reales largas.

- Ampliar los escenarios con dos altas, alta–baja–alta–alta, aciertos ayudados, errores sostenidos, alternancia, interrupciones, práctica manual entre automáticas y último nivel. Incluir repetición de un identificador para demostrar que no duplica progreso.
- Mostrar para cada partida simulada dominio, racha antes/después y acción de desbloqueo con motivo; para ayuda, conservar ventana, propuesta, espera, límites y aplicación a la próxima pregunta.
- Ejecutar análisis determinista de casos fronterizos de las configuraciones actuales: pertenencias solapadas, clasificación alrededor del umbral de dominio, autonomía cero y ausencia de salida. Verificarlo mediante las sesiones públicas, manteniendo pruebas sobre conducta observable.
- Generar un informe de escenarios y sus configuraciones/exportación. Señalar discontinuidades o comportamientos discutibles como hallazgos técnicos. Mantener la configuración de producción hasta justificar una revisión documentada; no interpretar simulaciones como calibración pedagógica.
- Conservar aislamiento en memoria y origen simulado; no incorporar un editor general de reglas.

**Cierre:** mismos escenarios y configuración producen las mismas decisiones; no se mezclan juegos/niveles/orígenes, no se retiran desbloqueos y consultar/ejecutar el laboratorio no altera el perfil.

## 6. v0.10 — Resistencia de datos y entrega reproducible

**Entrega:** conservar resultados ante fallos de almacenamiento y preparar una versión más fácil de verificar.

- Probar fallos de lectura/escritura, archivos truncados o de versión desconocida y reinicio interrumpido a través de los límites públicos de aplicación. Preservar siempre el archivo original ilegible; impedir nuevas escrituras en el historial afectado y permitir reintentar o exportar una copia original de recuperación. Los otros componentes siguen disponibles.
- Conservar en memoria las mediciones derivadas cuya escritura haya fallado y ofrecer reintentar el guardado con el mismo identificador, evitando duplicados. Informar expresamente que ese pendiente no sobrevive a la muerte del proceso; no retener grabaciones.
- Coordinar operaciones sobre un mismo archivo entre instancias y mantener el reinicio detrás de una barrera de operaciones: ningún guardado pendiente debe reponer resultados después del borrado. Mantener el marcador de recuperación y la sustitución atómica; no migrar a Room sin evidencia de necesidad.
- Añadir un comando PowerShell reproducible de verificación y empaquetado que seleccione explícitamente dispositivo, compruebe requisitos y conserve informes por ejecución, APK, tamaño y SHA-256. No instalar en un teléfono ni borrar su perfil sin una instrucción específica.
- Recorrer API 34 y API 36 en emulador, tamaños de pantalla y recreaciones pertinentes. Repetir suites completas al cerrar la entrega; en cambios intermedios ejecutar solo las pruebas justificadas. Documentar las advertencias restantes sin actualizar dependencias solo para silenciarlas.

**Cierre:** guardado fallido recuperable sin duplicación, corrupción preservada, reinicio sin resurrección de datos, formatos históricos legibles y paquete de entrega reproducible. La prueba física del S22 Ultra permanece en una lista separada.

## Dependencias y límites

Orden propuesto: 1 → 2 → 3 → 4 → 5 → 6. Cada entrega se puede ejecutar por sí misma una vez integrada; si una ampliación del laboratorio descubre un defecto relevante, corregirlo con una regresión antes de continuar.

No incorporar otros juegos, cuentas, backend, ML, polifonía, editor general de reglas o actualización del protocolo Word dentro de estos incrementos. No borrar resultados reales, importar perfiles ni publicar cambios externos para avanzar este plan. Las propuestas técnicas son reversibles; los cambios de reglas pedagógicas y afirmaciones de eficacia requieren evidencia y revisión posteriores.

Las seis entregas ya tienen implementación y evidencia, registradas fuera de este plan. Al retomar, partir del estado actualizado en `.agents/estado-prototipo.md` y del recorrido físico pendiente en la guía v0.10; no repetir los incrementos completados.

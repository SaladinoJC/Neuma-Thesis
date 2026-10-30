# Afinador A5 — entrenamiento de oído

Implementación local de [#13](https://github.com/SaladinoJC/Tesis/issues/13), iniciada el 2026-10-28 por «ok segui con lo que haya pendiente», sobre A3/A4 y el menú v0.12.5. Rama `trabajo/afinador-a5`, un ticket por vez, sin subagentes ni acciones externas. **Entrega 0.12.6/código 21 verificada en software.** Los parámetros son experimentales y no acreditan eficacia pedagógica ni precisión sobre guitarra física.

APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.6.apk`; instalar encima con `adb -s <serial> install -r <apk>`, sin desinstalar para conservar el perfil. 14.774.754 bytes; SHA-256 `d735327db3e584c6efe4f3c1c7b15468bb543c0c14ac274e5ab09575b0947eac`. Firma verificada con el certificado de las entregas anteriores; el APK instalado en el emulador coincide en SHA-256 con el archivado.

## Recorrido

En el libro, abrir Afinador → Entrenamiento de oído. Elegir guitarra, afinación, Normal/Pro, revisión final y clasificación previa opcional. Clasificación empieza desactivada; se recuerdan modo y opciones sin encender el micrófono. Pulsar Iniciar entrenamiento; si se solicita permiso, autorizar y pulsar nuevamente para iniciar explícitamente.

Seguir sexta → primera. Escuchar la referencia con guitarra eléctrica limpia, piano o tono puro; repetirla libremente. La captura se libera durante la salida y se reinicia con el margen provisional de 600 ms de A4. Durante el ajuste solo se informa el objetivo y la advertencia excepcional «Muy lejos, revisar cuerda correcta»; no se muestran medición, dirección, estabilidad ni acierto, tampoco en accesibilidad.

Con clasificación activa, tocar antes de ajustar y elegir Grave/Agudo/Afinado respecto de la referencia. Sin una lectura estable reciente se pide tocar nuevamente. La respuesta queda sellada sin revelar la solución hasta comprobar el ajuste; se evalúa por separado del intento de afinación posterior.

Pulsar Comprobar y tocar nuevamente: se drena la captura previa y se obtiene evidencia nueva. Silencio, lectura incierta, huecos o variación no consumen intentos. Cuando la señal es estable se revela el intento inicial con nota, cents y acierto/error. Un error permanece en el resumen aunque luego se corrija. Cada cuerda tiene un único intento inicial válido; repetir el botón tras revelar no crea otros.

Corregir con la medición visible y sostener un segundo dentro del margen para avanzar. Al completar las seis, la revisión final opcional recorre el conjunto y permite dejar cuerdas pendientes para corregirlas después; no suma intentos de oído. El resumen separa intento inicial, clasificación, corrección y revisión.

Salir o pasar a segundo plano detiene audio/captura e interrumpe el recorrido. Iniciar de nuevo comienza otro; el resumen actual existe solo en memoria. **A6 sigue pendiente:** no hay historial de afinador guardado ni exportación de desempeño de oído en esta versión. Las preferencias sí se incluyen en la configuración/exportación existente; no se escriben resultados en el historial de voz ni se guarda PCM.

## Parámetros técnicos provisionales

`ear-check-v1`: para evaluar tanto afinación correcta como incorrecta, ventana continua mínima de 400 ms con amplitud máxima de 5 cents y separación máxima de 250 ms entre lecturas. La clasificación requiere además una ventana válida de antigüedad máxima 250 ms al responder. El valor sellado es la última lectura de esa ventana; su timestamp se conserva. Estos valores son elecciones del agente para probar, no calibración física.

La estabilidad de evaluación es independiente de la aprobación Normal ±5 cents / Pro ±2 cents. Después de revelar se reinicia la estabilidad de corrección y se exige un segundo nuevo dentro del margen. Se reutilizan las reglas A3 de distancia absoluta: activar aviso a partir de 700 cents sostenidos 400 ms, retirar a 500 cents o con discontinuidad. No plegar octavas ni afirmar qué cuerda física se tocó.

La revisión reutiliza `GuidedTunerSession` comenzando directamente en su segunda pasada. `TunerViewModel` conserva el mecanismo A4 de generaciones de captura y lo aplica también al botón Comprobar: callbacks antiguos no pueden evaluar el intento ni acumular estabilidad. Ningún criterio usa DDA.

## Diseño y límites visuales

Pantalla Compose de una columna con papel cálido, verdes, Fraunces/Nunito Sans y espaciados/colores de `design-tokens.json`. Sin imágenes ni dependencias nuevas. Se reutilizan selector de modos, editor de guitarras/afinaciones y referencias; las filas de modos/timbres admiten salto de línea. Preparación, ajuste oculto, corrección y resumen tienen acciones explícitas y scroll vertical.

La guía de pantallas se adapta al stack nativo: jerarquía por tarea y datos reales, revisión de capturas Android; no se incorpora HTML, un framework web ni otro estilo sorteado sobre la identidad confirmada. No se añaden ilustraciones ornamentales ni animaciones. Los interiores Libre/Guiado conservan el diseño previo. Foco táctil, permiso, contraste y legibilidad física requieren verificación en teléfono.

## Verificación

**130 JVM aprobadas; lint 0 errores/14 advertencias; APK y APK de pruebas compilados.** Primer ciclo rojo→verde: prueba del intento errado antes de implementar la sesión. Segundo ciclo rojo→verde: clasificación retenida entre recorridos, corregida al comenzar otro. Las pruebas públicas también cubren lectura insuficiente, huecos, variación, suspensión/reanudación, revisión con pendientes, advertencia sin filtrar dirección, referencias intercaladas, drenaje de captura/callbacks viejos y preferencias compatibles con archivos anteriores.

**12/12 Android seleccionadas aprobadas en Pixel_6/API 34 a 411 dp**, tanda integrada de 4 min 18 s: oído completo, cuatro casos del menú, guiado, ciclo de vida guiado, dos de captura/afinador, configuración y dos de referencia con audio real. Es selección de regresión, no la suite Android completa ni API 36. El recorrido A5 usa estimaciones sintéticas en el límite de captura; los tests existentes verifican aparte captura y reproducción reales del emulador.

Después de esa tanda se adelantó el resumen a la preparación y se agregó vuelta inmediata al inicio de la página al completar/interrumpir un recorrido con resultados. Se repitieron las 130 JVM, lint y ensamblados (1 min 35 s), y el recorrido A5 completo sobre el APK final en **411 dp, 360 dp y 320 dp con texto 200 %**: tres ejecuciones directas aprobadas en 16,407 / 16,983 / 19,495 s. La entrada desde el menú sin encender micrófono también se repitió a 320 dp/texto 200 %: aprobada en 11,166 s. No se volvió a ejecutar toda la selección de 12 tras ese último cambio de orden visual; las reglas y adaptadores no cambiaron.

Las primeras tandas presentaron PixelCopy al capturar después del índice y una espera de ciclo de vida; el emulador dejó de responder también a ADB. Se reinició en frío con `-gpu swiftshader_indirect` y se cambió solo la captura de evidencia del menú a `UiAutomation.takeScreenshot()`, conservando todas las aserciones funcionales/de geometría. La tanda de 12 posterior aprobó. Esas dos intervenciones no permiten atribuir inequívocamente el fallo a una única causa. La primera tanda de 9 terminó con 8/9 y sus informes se conservaron; la siguiente se interrumpió al detener el emulador y Gradle.

Comando de JVM: `./gradlew.bat :app:testDebugUnitTest`. Android de A5: `./gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.EarTunerAndroidTest'`. Solo ejecutar pruebas instrumentadas en emuladores: reinician datos. Evidencia en `Plataforma/build/verification/2026-10-28-A5/`: XML JVM/Android/lint, log integrado, logs de las tres repeticiones finales, logs de arranque y `summary.json`. Para conservar capturas después de terminar, se instalaron APK/app de tests y se ejecutó `adb shell am instrument -w -e class ar.edu.unmdp.entrenamiento.EarTunerAndroidTest ar.edu.unmdp.entrenamiento.test/androidx.test.runner.AndroidJUnitRunner`; a diferencia de Gradle, ese comando no desinstala al cerrar.

Capturas finales en `docs/imagenes/afinador-a5/final-411/`, `360/` y `320-texto200/`, cuatro estados por tamaño: preparación, corrección, siguiente cuerda oculta y resumen. Se revisaron saltos de línea, alcance con scroll, ocultamiento de lecturas, contraste de colores sólidos, controles y jerarquía del resumen. A 200 % se necesita más scroll vertical; no se comprime la letra para hacer entrar todo. La carpeta `411/` conserva el primer estudio anterior a corregir el color de selección. No se verificó locución/gestos de TalkBack de A5; sí ausencia de lecturas en ambos árboles semánticos de Compose.

Prueba física pendiente: S22 Ultra, versión Android/app, guitarra/afinación, ruta de salida y volumen, entorno/distancia al micrófono y referencia patrón si se desea medir precisión. Probar ajuste errado y corrección, clasificación previa, sexta cuerda, referencia intercalada, cambios de app, revisión final y texto ampliado. No interpretar resultados sintéticos como desempeño de un estudiante ni como eficacia del entrenamiento.

# Acabado vocal — candidata 0.12.27 / código 42 (16/11/2026)

Tercer incremento Music Garden: portada propia y claro de canto SVG/vector,
papel compartido y paneles en captura/resultados/historial/progreso. Tres modos,
permisos explícitos, referencias, audio, DDA y datos conservados. Previa del
recurso revisada en navegador; revisión nativa/acústica pendiente, sin emulador.
APK Garden: `build/apk-archives/neuma-garden-v0.12.27-vocal.apk`.
[Guía, evidencia y comprobación](../docs/Acabado-Intervalos-vocales-v0.12.27.md).
Regenerar el recurso: `python tools/prepare_vocal_clearing.py` desde la raíz.

# Acabado Rhythm Tap — candidata 0.12.26 / código 41 (16/11/2026)

Segundo incremento Music Garden: terraza estática, partitura limpia, preparación
plegable, resultados con detalles, historial/progreso accesibles y pad adaptable.
Calibración y recorridos auxiliares con papel; Quiet conserva su paleta y omite
la decoración Garden. Audio, relojes, evaluación y datos conservados.
308 JVM aprobadas, lint sin errores, APK/AndroidTest compilados; revisión nativa
pendiente, sin emulador. Instalar encima de Garden sin desinstalar:
`build/apk-archives/neuma-garden-v0.12.26-ritmo.apk`.
[Guía, evidencia y comprobación](../docs/Acabado-Rhythm-Tap-v0.12.26.md).

# Acabado Music Garden — candidata 0.12.25 / código 40 (16/11/2026)

Primer incremento: Instrumentos con preparación/resultados en papel, invernadero
vectorial, selección/foco accesibles y acceso directo al historial con retorno al
origen. Historial e inspector compartidos adaptados al tema. Reglas, audio, DDA
y datos conservados; sin emulador ni revisión nativa atribuida. APK Garden:
`build/apk-archives/neuma-garden-v0.12.25-instrumentos.apk`. Procedimiento,
evidencia y pendientes en [guía de la candidata](../docs/Acabado-Instrumentos-v0.12.25.md).
Especificación y seis tareas: GitHub #31–#37. Sonidos de interfaz, respuesta
táctil y Personaje continúan pendientes. Los registros siguientes son históricos.

# Comparación para la reunión — 0.12.24 (12/11/2026)

Instalar `build/apk-archives/neuma-garden-v0.12.24.apk` sobre Neuma existente;
instalar también `build/apk-archives/neuma-quiet-studio-v0.12.24.apk` como app
independiente. Quiet Studio adapta catálogo y Rhythm Tap. Ambas contienen las
correcciones funcionales de 0.12.21–0.12.23. [Instalación, compilación, respaldo
y verificación](../docs/Comparacion-Garden-Quiet-Studio-v0.12.24.md).


# Neuma — aplicación Android

**Candidata local 0.12.23 / código 38 (12/11/2026):** afinador adaptado a
pulsaciones breves: 400 ms de permanencia dentro del margen, análisis específico
con umbral RMS 0,003 y última confirmación identificada al decaer la señal en
Libre. Guiado y Oído comparten la política; una comprobación de Oído ya afinada
puede avanzar sin pedir otra pulsación. No se modifican márgenes Normal/Pro ni
análisis vocal. [Evidencia y límites](../docs/Verificacion-afinador-v0.12.23.md).
APK: `build/apk-archives/neuma-v0.12.23-afinador-cuerdas-pulsadas.apk`.
Incluye cambios anteriores de instrumentos, ritmo y Defender. Instalar encima;
respuesta en guitarra real y nueva presentación siguen pendientes de probar.

**Candidata local 0.12.22 / código 37 (12/11/2026):** corrige el conteo de Rhythm
Tap al tempo real, incorpora calibración previa explícita y registra su ajuste
por partida. El pad recibe contactos nativos y el sonido de taps usa SoundPool
precargado; la mejora acústica requiere prueba en el teléfono. Defender permite
viaje de 3 s y aparición manual de 1–6 s en práctica; progresión conserva su
curva de apariciones y puede adaptar viaje hasta 3 s. Incluye la mejora de
Instrumentos de 0.12.21. [Diagnóstico, verificación y recorrido](../docs/Verificacion-ritmo-defender-v0.12.22.md).
APK: `build/apk-archives/neuma-v0.12.22-ritmo-calibracion-defender.apk`.
Instalar encima sin desinstalar. Los resultados físicos informados sobre
0.12.20 no validan automáticamente esta candidata.

**Candidata local 0.12.21 / código 36 (12/11/2026):** ¿Qué instrumento es?
permite responder desde el inicio real de la melodía, incluida una reescucha.
La elección cancela el audio y conserva una única respuesta y las escuchas
consumidas. [Verificación y recorrido](../docs/Verificacion-instrumentos-v0.12.21.md).
APK: `build/apk-archives/neuma-v0.12.21-instrumentos-respuesta-anticipada.apk`.
Instalar encima para conservar datos. Validación física del cambio pendiente;
las observaciones del autor sobre 0.12.20 conservan su versión original.

[Entrega 0.12.20 en GitHub](../docs/Entrega-GitHub-v0.12.20.md): PR #30 en borrador y etiqueta `v0.12.20-preview.1`; APK, informe PDF, respaldo de verificaciones V1–V6 y hashes adjuntos a una entrega preliminar en borrador.

**Intervalos vocales V6: 0.12.20/código 35, candidata sin ejecutar Android.** Producción libre sin referencia sonora: elegís tu ancla al cantar y completás ambas notas en dos capturas. Acceso por hito de ancla dada del mismo intervalo/dirección; progreso, tolerancia y racha independientes. Historial con cero escuchas/referencias MIDI nulas; perfiles V5 compatibles y JSON/ZIP/reset integrados sin nuevo archivo. **292 JVM aprobadas (11 nuevas)**, ruta sintética de 750 rondas/25 objetivos por modo, lint **0 errores/10 avisos** y ambos ensamblados correctos. Dos Android nuevas compiladas; recuento directo de diez casos vocales actuales, todos pendientes de ejecución (corrige los totales históricos). [Contrato, evidencia y límites](../docs/Intervalos-vocales-V6.md). APK `build/apk-archives/neuma-v0.12.20-intervalos-vocales-v6-prueba-sin-emulador.apk`, sin instalar, firma compatible con V5. Music Garden conservado sin assets/dependencias nuevos. Los tres andamiajes se eligen explícitamente; selección automática global pendiente. Conservar no emular; última validación Android completa 0.12.10. Las entradas siguientes describen incrementos históricos.

**Intervalos vocales V5: 0.12.19/código 34, candidata sin ejecutar Android.** Ancla dada reproduce solo el ancla y captura únicamente el objetivo. Acceso por hito de imitación del mismo intervalo/dirección, con tolerancia, racha e hitos independientes; práctica sin avance, historial/procedencia y panel por modo. Perfiles V4 compatibles, nueve archivos y JSON/ZIP/reset integrados. **281 JVM aprobadas (18 nuevas)**, ruta sintética de 500 rondas/25 objetivos por modo, lint **0 errores/10 avisos** y ambos ensamblados correctos. Diez pruebas vocales Android compiladas, sin ejecutar. [Flujo, evidencia y límites](../docs/Intervalos-vocales-V5.md). APK `build/apk-archives/neuma-v0.12.19-intervalos-vocales-v5-prueba-sin-emulador.apk`, sin instalar. Music Garden reutilizado, sin assets/dependencias nuevos. Producción libre y selección automática global de ayuda pendientes; modos elegidos explícitamente. Conservar no emular; última validación Android completa 0.12.10. Las entradas siguientes describen incrementos históricos.

**Intervalos vocales V4: 0.12.18/código 33, candidata sin ejecutar Android.** Progresión predeterminada con series de cinco rondas de imitación, tolerancia Mamdani entre rondas y hitos por intervalo/dirección en orden fijo. Práctica separada sin avance; panel con pertenencias/reglas/centroide/snapshots y seis simulaciones aisladas. **263 JVM aprobadas (14 nuevas)**, ruta sintética de 250 rondas/25 objetivos; dos Android nuevas compiladas (ocho vocales pendientes de ejecutar). [Flujo, reglas y límites](../docs/Intervalos-vocales-V4.md). APK `build/apk-archives/neuma-v0.12.18-intervalos-vocales-v4-prueba-sin-emulador.apk`, sin instalar. Mismo archivo vocal/perfil de nueve archivos; sin grabaciones, assets ni dependencias nuevas. Ancla dada/producción libre pendientes. Conservar no emular; última validación Android completa 0.12.10.

**Intervalos vocales V3: 0.12.17/código 32, candidata sin ejecutar Android.** Guardado automático de completas/interrumpidas, historial y detalle, pendientes/reintento sin duplicación y exportación JSON/ZIP/reset integrados. **249 JVM aprobadas (10 nuevas)**, lint **0 errores/10 avisos**, app y AndroidTest ensamblados; dos Android nuevas compiladas, sin ejecutar. [Recorrido, esquema y límites](../docs/Intervalos-vocales-V3.md). APK `build/apk-archives/neuma-v0.12.17-intervalos-vocales-v3-prueba-sin-emulador.apk`, sin instalar, firma compatible con V2. Noveno archivo `vocal-interval-results.json`, sin grabaciones; DDA/progresión vocal siguen pendientes. Music Garden y assets existentes conservados. No abrir emulador; última validación Android completa 0.12.10.

**Intervalos vocales V2: 0.12.16/código 31, candidata sin validación Android.** Quinta página de Juegos: escuchar intervalo, cantar ancla, confirmar segunda captura y revisar error relativo. Permiso explícito, cancelación y pausa sin reanudación automática; dos capturas nuevas y liberación coordinada. **239 JVM sin fallos (11 nuevas)** y dos Android preparadas para navegación/resumen sintético. [Recorrido, parámetros y pendientes](../docs/Intervalos-vocales-V2.md). APK `build/apk-archives/neuma-v0.12.16-intervalos-vocales-v2-prueba-sin-emulador.apk`, sin instalar. Resumen vocal solo en memoria, sin historial/exportación ni DDA todavía. Music Garden reutilizado, sin nuevas dependencias/assets; portada compartida con Instrumentos provisional. Mantener la orden de no emular; la última validación Android completa sigue siendo 0.12.10.

**Intervalos vocales V1 (núcleo, 2026-10-30):** sesión manual de imitación con ancla cantada, sostenido y evaluación relativa en cents. Conserva mediciones y separa cierre musical de interrupción; todavía no está conectado como juego en la app. **228 JVM sin fallos, 10 nuevas**, incluido PCM sintético → YIN → sesión. [Contrato, parámetros experimentales y ejecución](../docs/Intervalos-vocales-V1.md). La versión entregable sigue siendo la candidata D5 0.12.15; no se genera una nueva entrega de usuario por este núcleo. Siguiente incremento vocal: ronda jugable con reproducción/captura y ciclo de vida; DDA/persistencia posteriores. La orden de no emular y los pendientes Android anteriores siguen vigentes.

**D5/#27 implementado localmente: 0.12.15/código 30, candidata sin verificación Android.** Ocho calidades, inversiones cerradas, sol/fa/mezcla y 84 objetivos con evidencia independiente. Catálogo finito de 1.502 realizaciones; raíz distinta del bajo, segundas con cabezas alternadas y síntesis de tres/cuatro voces. Compatibilidad de perfiles D3/D4, historial/filtros/inspector ampliados y ruta sintética de 168 partidas sin tocar el perfil. **218 JVM sin fallos (9 nuevas)**, lint **0 errores/17 advertencias**, app y AndroidTest compilados. [Guía D5 y datos del APK](../docs/Demostracion-defender-D5.md). Candidata `build/apk-archives/neuma-v0.12.15-defender-d5-prueba-sin-emulador.apk`, firma compatible, sin instalar. Dos Android nuevas compiladas, sin ejecutar; el usuario mantiene la orden de no emular. Legibilidad nativa, controles a texto ampliado y sonido físico pendientes. Se conserva Music Garden sin assets/dependencias nuevos. Última entrega completa verificada en Android 0.12.10; Personaje pausado. D6/D7 aún fuera de alcance.

**D4/#26 implementado localmente: 0.12.14/código 29, candidata sin revisión Android.** Defender de tríadas mayores, menores, disminuidas y aumentadas por raíz escrita/alteración/calidad, en fundamental y sol. Sonido simultáneo de tres voces al acertar, DDA reutilizado y progreso independiente de notas. Catálogo generado de 115 realizaciones sin dobles alteraciones; historial/exportación compatibles con los registros anteriores. **209 JVM sin fallos** (13 nuevas), lint **0 errores/17 advertencias**, app/AndroidTest compilados; dos Android nuevas preparadas, sin ejecutar. [Guía D4 y límites](../docs/Demostracion-defender-D4.md). APK `build/apk-archives/neuma-v0.12.14-defender-d4-prueba-sin-emulador.apk`, sin instalar. Music Garden conservado con pentagramas estructurados; legibilidad nativa de cuatro tríadas/texto ampliado y sonido físico pendientes. Continúa la orden de no emular; última entrega completa verificada en Android 0.12.10. Próximo software D5/#27; Personaje pausado.

**D3/#25 implementado localmente: 0.12.13/código 28, candidata sin revisión Android.** Defender de notas con DDA de viaje, práctica/progresión N1–N4, historial, exportación/recuperación/reinicio y ocho escenarios aislados. **196 JVM ejecutadas sin fallos**, lint **0 errores/17 advertencias**, app/AndroidTest compilados. El resumen se guarda provisionalmente sin conceder progreso hasta cerrar la entrada por navegación; las pulsaciones tardías no pueden regalar un game over. Music Garden en componentes nativos, con acabado visual pendiente. [Guía y límites](../docs/Demostracion-defender-D3.md). APK `build/apk-archives/neuma-v0.12.13-defender-d3-prueba-sin-emulador.apk`, firma compatible, sin instalar. Continúa la orden de no emular; las dos pruebas Android nuevas están compiladas, sin ejecutar. La última entrega completa verificada sigue siendo 0.12.10; Personaje pausado.

**R6/#21 implementado localmente: 0.12.12/código 27, candidata sin verificación Android.** Práctica con tempo manual, progresión por célula/tempo, guardado coordinado, decisiones Mamdani y siete escenarios sintéticos aislados. **Suite completa de 178 JVM ejecutada sin fallos** (11 casos agregados); lint **0 errores/17 advertencias**, APK de app/pruebas generados. El usuario pidió no emular; las dos pruebas Android nuevas están compiladas, sin ejecutar. [Guía, evidencia y límites](../docs/Rhythm-Tap-R6.md). Candidata: `build/apk-archives/neuma-v0.12.12-r6-prueba-sin-emulador.apk`; firma compatible con R5, sin instalar. La última entrega completa verificada continúa siendo 0.12.10. Personaje sigue pausado.

**Rhythm Tap lateral 0.12.11: APK de prueba disponible.** VexFlow offline, fondo integrado, marcador fijo y cabezas verdes. Retoma 2026-10-30 sin emulador: lint **0 errores/17 advertencias**; Gradle confirma vigentes los ensamblados y las 167 JVM previas sin fallos (`UP-TO-DATE`, sin nueva ejecución). APK idéntico al archivado. Falta la verificación final en Android; el usuario pidió avisarle antes de decidir si se abre el emulador. [APK, alcance y pendientes](../docs/Rhythm-Tap-lectura-lateral.md).

**Marca temporal (2026-10-29):** nombre Neuma, icono Clivis oscuro/adaptable/monocromático, apertura nativa sobre marfil y logo horizontal en el menú; originales corregidos en `../docs/logo/`. Regenerar con `python tools/prepare_neuma_brand.py`. Conserva el paquete, firma y datos de la app existente. Music Garden sigue siendo la dirección artística vigente; Quiet Studio continúa como propuesta de rediseño. Esta incorporación se realiza sobre 0.12.11 en desarrollo y no acredita por sí sola la verificación pendiente del renderizador de ritmo.

Versión 0.12.10: **Rhythm Tap R5 / #20**, historial con partitura/resultados exactos, filtros, detalle, repetición práctica y revisiones idempotentes para taps entregados tarde. Exportación/ZIP/reinicio incluyen ritmo y pendientes. **163 JVM**, **57/57 Android completas** en API 34, dos casos R5 en 411/360/320 dp y texto 200 %, lint 0 errores/14 advertencias. Exportación real por selector del sistema a Downloads verificada. [Guía y límites](../docs/Rhythm-Tap-R5.md). APK `build/apk-archives/entrenamiento-auditivo-v0.12.10.apk`, firma compatible/hash instalado comprobados; instalar encima sin desinstalar. Music Garden conservado, sin nuevos assets; DDA de ritmo sigue en R6, teléfono y TalkBack pendientes.

Versión 0.12.9: **Rhythm Tap R4 / #19**, tresillos, síncopas y dos negras ligadas; catálogo inicial de 21 categorías completo como práctica. Tresillo con número/corchete, ligadura de un solo ataque, generación con suma racional y validación de partituras antes de iniciar. **151 JVM**, **13/13 Android seleccionadas**, dos casos R4 en 411/360/320 dp y flujo con texto 200 %, lint 0 errores/14 advertencias. [Guía y verificación](../docs/Rhythm-Tap-R4.md). APK `build/apk-archives/entrenamiento-auditivo-v0.12.9.apk`, firma compatible/hash instalado comprobados; instalar encima sin desinstalar. Conserva Music Garden sin nuevos assets; teléfono físico y TalkBack pendientes.

Versión 0.12.8: **Rhythm Tap R3 / #18**, ocho células de semicorcheas/puntillos y silencios cortos, mezclables en 2/4, 3/4 y 4/4; ventanas según separación local, resumen de anticipos/retrasos/extras/omisiones y notación desde duraciones racionales. Papel, verdes y tipografías Music Garden, sin nuevos assets ni dependencias. [Guía y verificación](../docs/Rhythm-Tap-R3.md). APK `build/apk-archives/entrenamiento-auditivo-v0.12.8.apk`; instalar encima sin desinstalar. Ritmo aún muestra resultados en memoria; DDA/historial quedan en R5–R6.

Versión 0.12.7: **Afinador A6 / #14**, historial opcional de Guiado/Oído, progreso filtrable, etapas diferenciadas, configuración copiada y reintento de guardado idempotente. Exportación/ZIP/reinicio incluyen seguimiento; apagarlo conserva solo el resumen del nuevo recorrido. [Guía y verificación](../docs/Demostracion-afinador-A6.md): 140 JVM, **50/50 Android** en API 34, dos casos A6 a 411/360/320 dp (texto 200 % a 320), lint 0 errores/14 advertencias. APK `build/apk-archives/entrenamiento-auditivo-v0.12.7.apk`, firma compatible verificada; instalar encima sin desinstalar. Audio físico y TalkBack por gestos/locución pendientes.

Versión 0.12.6: **Afinador A5 / #13**, entrenamiento de oído con ajuste sin pistas, Comprobar con captura nueva, clasificación opcional, corrección y revisión final. Resumen por cuerda conserva el error inicial aunque luego se corrija. Resultados solo en memoria; historial/seguimiento A6 pendiente. [Guía, parámetros y verificación](../docs/Demostracion-afinador-A5.md): 130 JVM, 12 Android seleccionadas y recorrido final a 411/360/320 dp (200 % a 320); lint sin errores/14 advertencias. APK `build/apk-archives/entrenamiento-auditivo-v0.12.6.apk`, instalar encima sin desinstalar.

Versión 0.12.5: menú **Music Garden** con Personaje estático, cuatro portadas, índice y accesos existentes. Entrega local verificada: 46/46 Android en API 34, 122 JVM y lint sin errores/14 advertencias. TalkBack: revisión parcial de foco y activación por teclado; gestos y locución en teléfono pendientes. [Evidencia, correcciones y límites](../docs/Verificacion-menu-android-v0.12.5.md). APK: `build/apk-archives/entrenamiento-auditivo-v0.12.5.apk`, instalar encima sin desinstalar. El editor y las recompensas del personaje siguen pendientes.

Versión 0.12.4: agrega **Afinador A4 / #12**, referencias de guitarra eléctrica limpia, piano y tono puro en Do2–Mi4, con timbre recordado. Suspende y libera captura antes de reproducir, descarta lecturas previas y retoma la misma cuerda guiada sin acumular estabilidad; salir o detener cancela la reanudación. Conserva A3, R2 y D2. Ver [audio y procedencia](../docs/Audio-referencias-afinador-A4.md) y [guía/verificación A4](../docs/Demostracion-afinador-A4.md). La audibilidad y el margen acústico en teléfono siguen pendientes.

Versión 0.12.3: agrega **Defender D2 / #24**, práctica N1–N4 con clave de sol/fa, hasta dos líneas adicionales, alteraciones simples opcionales y cupo de uno a cuatro enemigos. Responde por nombre y alteración escrita; un acierto elimina solo el duplicado más próximo a la base. El resumen distingue etapa, rango, clave, alteración y pérdidas. Conserva A3 y R2; DDA, desbloqueos e historial de Defender continúan en D3. Ver [guía y verificación D2](../docs/Demostracion-defender-D2.md).

Versión 0.12.2: agrega **Rhythm Tap R2 / #17**, con compases 2/4, 3/4 y 4/4, blancas, redondas, negra con puntillo y silencios básicos. La generación comprueba cabida y cuota antes de la búsqueda; el resumen distingue ataques y silencios. Conserva el afinador A3 de 0.12.1 y Defender D1. Sin DDA ni historial de ritmo todavía. Ver [guía y verificación R2](../docs/Rhythm-Tap-R2.md).

Versión 0.12.1: agrega únicamente **Afinador A3 / #11**, con recorrido guiado sexta → primera, revisión final opcional recordada y corrección de cuerdas pendientes. El resumen distingue ambas pasadas y permanece en memoria; recrear, pausar o salir interrumpe la captura sin completar el recorrido. Los cambios de Ritmo R2 y Defender D2 siguen guardados aparte. Ver [guía y verificación A3](../docs/Demostracion-afinador-A3.md). Instalar encima de v0.12 conserva los datos; las tolerancias siguen siendo experimentales.

Versión 0.12: incorpora **Afinador con perfiles, seis presets y afinaciones personalizadas**, **Rhythm Tap** de cuatro compases con negras/corcheas y **Defender de notas** en clave de sol. Los tres módulos comparten menú y ciclo de vida; la configuración del afinador participa en exportación JSON, recuperación ZIP y reinicio. Ritmo y Defender muestran resúmenes de práctica en memoria; su persistencia y DDA siguen en los tickets posteriores. [Recorrido](../docs/Demostracion-prototipo-v0.12.md) y [verificación integrada](../docs/Verificacion-prototipo-v0.12.md).

APK actual: `build/apk-archives/entrenamiento-auditivo-v0.12.10.apk`. Mismo paquete y certificado debug: instalar encima, sin desinstalar. Se conservan las funciones previas, incluido el afinador Libre de v0.11 ([antecedente](../docs/Demostracion-afinador-v0.11.md)), entrenamiento de oído A5 y seguimiento A6. Los criterios Normal/Pro y el DDA actual son experimentales y requieren comprobación física.

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*tuner*'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.TunerAndroidTest'
```

Usar únicamente un emulador para pruebas instrumentadas; para teléfono seguir el recorrido manual. Configurar `JAVA_HOME`, `ANDROID_HOME` y `ANDROID_SERIAL` como en la verificación reproducible de abajo.

Versión 0.10: Simón y «¿Qué instrumento es?» con progresión automática persistida, inspección Mamdani y práctica manual. Incluye historial completo con filtros y detalle, laboratorio YIN de 83 casos, referencia sonora/gráfica de voz y banco DDA de nueve escenarios. «Datos y demostración» exporta JSON y originales ZIP para recuperación, y permite reiniciar el perfil con confirmación. La voz conserva en memoria un guardado fallido y permite reintentarlo. Las reglas son experimentales; la prueba física del S22 Ultra sigue pendiente. [Guía actual](../docs/Demostracion-prototipo-v0.10.md) y [verificación por incremento](../docs/Verificacion-iteraciones-post-v0.5.md).

El piano usa 23 muestras `mf` de University of Iowa, Do4–Si♭5, incluidas como OGG mono a 44,1 kHz, recortadas a 600 ms con fade final. Los OGG ocupan aproximadamente 186 KiB en total. `MediaCodec` las decodifica fuera del hilo de interfaz; el PCM de cada nota presentada se decodifica una vez, se conserva en memoria y se reproduce con `AudioTrack`. Las teclas usan `SoundPool` precargado, con hasta 16 colas simultáneas y volumen de reproducción 0,8, sin bloquear la entrada por los 600 ms de duración. La entrada de notas se mantiene ordenada e independiente del audio; al exceder 16 colas, se reemplazan las más viejas, no se descartan respuestas por ese límite. Procedencia, condiciones y script de conversión en [Audio-piano-Simon.md](../docs/Audio-piano-Simon.md). No se necesita conexión para jugar.

Los resultados se guardan en almacenamiento privado como JSON versionado. El menú muestra las cinco partidas más recientes; «Ver todos los resultados» permite consultar todas en bloques de 20, filtrar y abrir detalles. Los archivos originales se conservan al consultar. El de Simón mantiene su esquema anterior; el de instrumentos incluye cada respuesta, opciones, objetivo, acierto, escuchas, nivel, fechas y versión del banco. Las salidas al menú o a segundo plano se guardan como interrupciones. La nueva versión mantiene el identificador de la app y su firma debug para actualizar el APK anterior sin desinstalarlo y conservar su historial.

Instrumentos utiliza 45 muestras reales `mf` de Iowa: Do4, Re4, Mi4, Sol4 y La4 para cada uno de los nueve instrumentos. Los OGG mono/44,1 kHz de 900 ms ocupan 497.231 bytes (~486 KiB), además del banco histórico v1 conservado. El generador usa seis notas, saltos acotados, al menos tres alturas distintas y cierre en Do4: cinco notas de 400 ms, final de 800 ms y separaciones de 50 ms (3,05 s en total). La gramática es común a los instrumentos; sus parámetros son provisionales. La app compone una única pista PCM con fade al terminar cada nota, sin transposición, y registra la frase por respuesta. El contador confirma una escucha cuando avanza AudioTrack; si falla antes de comenzar, no consume ayuda. Un fallo posterior conserva la escucha consumida. [Procedencia, conversión y mediciones](../docs/Melodias-instrumentos-v0.3.1.md). El modo automático adapta las escuchas; la selección manual A/B/C conserva ayuda fija. Los desbloqueos están disponibles desde v0.5.

## Preparación

**Menú Music Garden 0.12.5 (adaptación nativa en revisión).** Abre en Personaje con ilustración genérica estática. Los señaladores Juegos, Afinador y Logros muestran las secciones del libro; Juegos conserva la página elegida al volver de una partida y el índice permite saltar entre cuatro portadas. Afinador ofrece Libre y Guiado; Más conserva Resultados, Prueba de voz y Datos. La preferencia Reducir movimiento se aplica al menú durante la sesión. Al cerrar el proceso, el menú vuelve a Personaje. Los juegos y pantallas interiores mantienen el diseño anterior. Para regenerar imágenes, fuentes y tokens: `python tools/prepare_garden_assets.py` desde `Plataforma/`. Los assets y su procedencia están en `assets/ASSET_MANIFEST.md`. Personaje sigue siendo una vista de menú, sin editor ni recompensas del plan P0–P7.

Verificación local: `./gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest`; en emulador propio, `./gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.GardenMenuAndroidTest'`. El APK es `app/build/outputs/apk/debug/app-debug.apk`; instalar con `adb install -r` para conservar el perfil. Revisar también el menú a 320, 360 y 411 dp, texto 200 %, reducción de movimiento y TalkBack antes de considerarlo final.

El DDA Mamdani `mamdani-help-v1` evalúa las últimas tres respuestas desde la tercera. Considera exactitud y autonomía (aciertos con primera escucha entre aciertos), sin utilizar tiempo de respuesta. Cambia como máximo una escucha, solo en la próxima pregunta, con dos rondas entre cambios. El panel DDA permite recorrer decisiones reales e inspeccionar cuatro escenarios preparados, con nivel A/B/C elegido manualmente dentro de la simulación. Los casos simulados no reproducen sonido ni escriben en el historial real. Parámetros, reglas y resolución son valores de prueba pendientes de calibración: [DDA v0.4](../docs/DDA-instrumentos-v0.4.md).

Abrir esta carpeta en Android Studio. Se requieren SDK 36, build-tools 35, JDK 17 o 21 y acceso inicial a los repositorios de dependencias. El proyecto incluye Gradle Wrapper 8.14.3 con checksum de distribución, AGP 8.11.0 y Kotlin/Compose compiler 2.2.10. Es compatible desde Android 8 (API 26).

Configurar el SDK desde Android Studio o mediante `ANDROID_HOME`; las rutas locales no se versionan. Ejemplo PowerShell para esta máquina:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

## Ejecutar

Seleccionar la configuración `app` y un dispositivo en Android Studio y pulsar Run. Alternativamente:

```powershell
.\gradlew.bat :app:assembleDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\app\build\outputs\apk\debug\app-debug.apk
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n ar.edu.unmdp.entrenamiento/.MainActivity
```

Si hay varios dispositivos, agregar `-s <serial>` a los comandos ADB. El S22 Ultra requiere depuración USB habilitada y autorizar el equipo. Simón e instrumentos no requieren micrófono. La prueba de voz solicita permiso al iniciarse.

## Verificación

```powershell
# Sesión: secuencia, respuestas, puntaje y persistencia
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonSessionTest'

# Regresión de salida al menú durante guardado
.\gradlew.bat :app:testDebugUnitTest --tests '*SimonViewModelTest'

# Android: solo sobre un emulador de pruebas; las pruebas reinician datos
$env:ANDROID_SERIAL = "emulator-5554"
.\gradlew.bat :app:connectedDebugAndroidTest
```

Recorrido manual: abrir el menú, elegir Simón, seleccionar escala e iniciar partida. Escuchar el piano, repetir la secuencia en sus teclas y comprobar que crece. Equivocarse, revisar longitud completada y error, volver al menú, cerrar/reabrir y comprobar el historial. Abandonar otra partida y comprobar que figura como interrumpida. Comprobar que la preparación y el teclado entran completos en horizontal, repetir rápidamente una secuencia, girar el teléfono hacia ambos lados sin perder la partida y comprobar vuelta al menú vertical. Probar las tres escalas, teclas blancas/negras y notas atenuadas fuera de escala, volumen multimedia, vuelta al menú y salida de la app durante reproducción.

Los informes de pruebas y lint se generan en `app/build/reports/`. Las verificaciones de emulador deben identificarse como tales; no demuestran audibilidad, latencia o funcionamiento en el teléfono físico.

Consultar el [registro de verificación v0.2.1](../docs/Verificacion-Simon-v0.2.1.md) para este ajuste y el recorrido pendiente en el S22 Ultra. El [registro v0.2](../docs/Verificacion-Simon-v0.2.md) conserva la verificación inicial del menú y piano. El [registro v0.1](../docs/Verificacion-Simon-v0.1.md) conserva la verificación del primer APK.

## Verificar instrumentos

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*InstrumentSessionTest' --tests '*InstrumentViewModelTest'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.InstrumentAndroidTest'
```

Recorrido: elegir «¿Qué instrumento es?» en el menú; seleccionar A, B o C e iniciar. Comprobar primera reproducción automática y dos escuchas restantes. Agotar las dos reescuchas y comprobar que el botón se deshabilita. Responder y revisar el instrumento correcto; seguir hasta diez preguntas y comprobar resumen e historial al salir/reabrir. Abandonar una segunda partida durante reproducción: debe guardarse como interrumpida. La recreación por configuración conserva la sesión; salir realmente de la app la interrumpe. El tiempo de respuesta no modifica el resultado. En el S22 Ultra revisar audibilidad, timbre, equilibrio de volumen y comodidad de los botones.

La versión 0.3.1 usa el mismo paquete y firma debug que v0.3: instalar encima, sin desinstalar, conserva los historiales. Las partidas nuevas guardan la melodía; las anteriores conservan MIDI 60 y se identifican como nota aislada. Registro actual: [Instrumentos v0.3.1](../docs/Verificacion-Instrumentos-v0.3.1.md); antecedente: [Instrumentos v0.3](../docs/Verificacion-Instrumentos-v0.3.md).

Para comprobar el banco nuevo: `python tools/verify_instrument_melodies.py`. Para prepararlo desde los originales cacheados o descargar los que falten: `python tools/prepare_instrument_melodies.py` (Python/NumPy/SciPy/SoundFile). Comprobar en el teléfono que las reescuchas repiten la misma frase, las siguientes preguntas cambian la melodía y el volumen/ataque/articulación resultan adecuados en los tres niveles.

## Verificar adaptación e inspección DDA

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*InstrumentDdaSessionTest' --tests '*InstrumentViewModelTest'
.\gradlew.bat :app:connectedDebugAndroidTest '-Pandroid.testInstrumentationRunnerArguments.class=ar.edu.unmdp.entrenamiento.InstrumentDdaAndroidTest'
```

En preparación, elegir Automático e iniciar: nivel A y tres escuchas iniciales. Contestar y revisar la ayuda anunciada para la próxima pregunta; en DDA → Partida real, recorrer las preguntas y contrastar entradas, pertenencias, reglas, centroide y motivo. En DDA → Simulación, elegir nivel y «Aciertos sin reescuchas»: cambios tras 3 y 6; «Aciertos y luego errores»: reducción tras 3 y recuperación tras 6; los otros dos escenarios mantienen ayuda. Cerrar el panel y comprobar que la partida real continúa igual. Seleccionar una tarjeta de nivel activa práctica manual; iniciar allí conserva tres escuchas. La nueva versión se instala encima de v0.3.1 y conserva los historiales. Registro: [Verificación DDA v0.4](../docs/Verificacion-DDA-v0.4.md).


## Verificar v0.5

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:connectedDebugAndroidTest :app:lintDebug :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.apk`. Mismo paquete y firma debug: instalar encima para conservar datos. Abrir «Datos y demostración» para exportar resultados antes de un reinicio voluntario. Ver [demostración v0.5](../docs/Demostracion-prototipo-v0.5.md) y [verificación](../docs/Verificacion-prototipo-v0.5.md). La verificación física en S22 Ultra sigue pendiente.

## Verificación y paquete reproducible

Desde `Plataforma/`, con un emulador iniciado:

```powershell
.\tools\verify-prototype.ps1 -Serial emulator-5554
# Solo JVM/lint/APK, sin instalar en ningún dispositivo:
.\tools\verify-prototype.ps1 -SkipAndroid
```

El comando comprueba JDK/SDK y que el serial sea un emulador. Conserva log, informes comprimidos, APK, tamaño y SHA-256 en `build/verification/<fecha>/summary.json`. No admite teléfonos físicos: las pruebas Android reinician el perfil de la app. `ANDROID_SERIAL` queda configurado en la sesión de PowerShell; indicarlo nuevamente al cambiar de emulador.

El guardado JSON usa una barrera compartida por directorio, sustitución atómica e identificadores para deduplicar. Ritmo reemplaza revisiones del mismo UUID para conservar taps entregados tarde sin duplicar intentos. Exportar el JSON genera una instantánea coherente. Un archivo ilegible o de esquema desconocido se conserva y bloquea el uso afectado; se puede reintentar la lectura o conservar los originales en ZIP antes de reiniciar. La recuperación copia bytes, no repara ni importa. El ZIP incluye los siete JSON conocidos que existan (incluidos `tuner-configuration.json`, `tuner-results.json` y `rhythm-results.json`), el LEEME y, si corresponde, `pending-voice-save.json`/`pending-tuner-saves.json`/`pending-rhythm-saves.json`; nunca grabaciones. Los pendientes de afinador/ritmo se conservan en memoria durante esa apertura y pueden perderse si el proceso se cierra antes de guardar o exportar.

`pendingVoiceSave` en el JSON y `pending-voice-save.json` en el ZIP identifican mediciones derivadas pendientes de persistencia. No se mezclan con las sesiones guardadas. El pendiente vive en memoria y **no sobrevive a la muerte del proceso**; reintentar usa el mismo identificador. Reiniciar con confirmación también descarta ese pendiente. No se migra a Room ni se cambia la configuración YIN/DDA en esta entrega.

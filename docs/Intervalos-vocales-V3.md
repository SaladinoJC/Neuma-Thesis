# Intervalos vocales V3 — persistencia, historial y exportación

Registro histórico de V3. [V4](Intervalos-vocales-V4.md) incorpora series
Mamdani y progresión para imitación; sus pendientes prevalecen al retomar.

Usuario, 2026-10-30: «sigamos con lo que puedas», después de entregar V2 y dejar
persistencia/exportación como siguiente incremento. **0.12.17/código 32**, candidata
sin ejecutar Android. Se conserva la prohibición de abrir el emulador y el trabajo
en un solo frente, sin subagentes. El DDA no forma parte de este incremento.

## Recorrido de uso

En **Juegos → Índice → Intervalos vocales**, la práctica sigue siendo manual:
escuchar, cantar ancla, cantar segunda nota y revisar. Al completar o interrumpir
una ronda iniciada, se guarda automáticamente un resultado con identificador
estable. La pantalla distingue actualización, ronda guardada y guardado pendiente.
Denegar permiso o cancelar una referencia antes de empezar a cantar no crea una ronda.

**Historial vocal** permite consultar rondas completas e interrumpidas, abrir
su detalle y desplegar las mediciones y condiciones de captura. Se presentan
bloques de 20 rondas y de 20 mediciones; no se dibuja toda la serie al abrir.
Una ronda completa fuera de tolerancia se diferencia de una interrupción sin
evaluación final. Reabrir la aplicación permite recuperar el historial guardado.

Si falla el guardado, la copia queda en memoria, visible también en el historial.
**Reintentar historial** confirma el mismo identificador sin duplicarlo. No se
puede iniciar otra ronda mientras exista un resultado pendiente o se esté
actualizando el almacenamiento. **Datos y demostración** permite exportar JSON
o un ZIP con originales y pendientes. Un pendiente en memoria no sobrevive por sí
solo a la muerte del proceso: reintentar o exportar antes de cerrar. Una captura
en curso tampoco tiene un checkpoint de recuperación tras muerte de proceso.

## Registro y coordinación

`VocalIntervalResults` reutiliza `JsonResultsFile` y la transacción del perfil:
documento versionado y sustitución del archivo completo con comprobación de
generación para no reintroducir datos de un reinicio. `VocalIntervalHistorySession`
sella una única copia terminal por ronda y retiene pendientes; el modelo serializa
lecturas y escrituras en una cola propia. El identificador y la fecha inicial se
asignan al comenzar a cantar, no al reintentar.

Se guarda en **`vocal-interval-results.json`**, noveno archivo conocido del perfil.
No se mezcla con `voice-results.json`, correspondiente al laboratorio técnico,
ni se convierte retrospectivamente una sesión del laboratorio en práctica vocal.
Perfiles de ocho archivos funcionan sin migración: la ausencia del noveno equivale
a un historial vocal vacío hasta guardar la primera ronda.

Cada `VocalIntervalRecord` contiene:

- UUID, inicio/cierre, versión de app y de configuración.
- Origen `manual`, andamiaje de imitación `1`, dispositivo y versión Android.
- Referencias MIDI y número de escuchas completas de la selección usada.
- Resultado relativo, frecuencias representativas, tolerancia y configuración de
  sostenido; interrupción o error de captura cuando corresponda.
- Configuración YIN, tasa real confirmada y serie completa: tiempo, etapa,
  frecuencia válida/rechazo, error relativo, procesamiento por ventana,
  periodicidad y RMS. Diagnósticos no finitos se conservan como ausencia de valor.

No se guarda PCM. El procesamiento por ventana no es latencia acústica; origen
`manual` distingue modo de práctica, no acredita que una señal de prueba sea voz
humana. La evidencia JVM identifica expresamente su origen sintético.

El cierre espera la liberación de audio antes de encolar el guardado. Cancelar
varias veces no cambia el resultado ya sellado. Un fallo después de escribir
pero antes de confirmar se reintenta por identidad; si el archivo ya contiene
el UUID, no se duplica. El drenaje espera la cola antes de exportar o reiniciar.
Reiniciar el perfil borra también el noveno archivo y limpia las copias en memoria
solo después de completar el reinicio solicitado por el usuario.

## Exportación y recuperación

El JSON conserva `schemaVersion=1` con campos aditivos `vocalIntervals` y
`pendingVocalIntervalSaves`. Las listas de laboratorio de voz y otros juegos
conservan sus campos anteriores. Leer una exportación anterior sin los nuevos
campos produce listas vocales vacías. Los pendientes se identifican por separado:
en una confirmación incierta puede haber el mismo UUID en disco y en pendientes;
no se interpretan como dos rondas distintas.

El ZIP copia `vocal-interval-results.json` sin interpretarlo y, si corresponde,
agrega `pending-vocal-interval-saves.json`. JSON dañado o una versión no compatible
producen un error visible; no se reemplaza el original ni se inicia reparación.
El ZIP permite conservar originales incluso cuando la exportación JSON no puede
leerlos. No se implementa importación ni reparación automática.

## Sistema visual

Se reutilizan tema del afinador, tokens Music Garden, Fraunces/Nunito Sans y
controles nativos. Historial, detalles, estados y errores usan papel/verde y
mensajes textuales; las mediciones técnicas se despliegan bajo una acción explícita.
Controles con mínimo 48 dp y lectura vertical desplazable; sin animaciones,
imágenes, iconos, fuentes o dependencias nuevas. La portada compartida provisional
de V2 no cambia. Aplicación de `design-taste-frontend`: jerarquía de la tarea,
datos reales del historial y estados vacíos/error honestos dentro del stack Android;
no se introduce otro shell ni una dirección visual distinta.

Los estados preparados son historial vacío, cargando, lista, detalle paginado,
guardado, pendiente y error. La revisión nativa de 411/360/320 dp, texto 200 %,
TalkBack y capturas sigue pendiente por la restricción de no emular. No se da por
comprobada legibilidad nativa por compilar las pantallas.

## Verificación reproducible

Cierre de host: **249 JVM aprobadas (10 nuevas)**, lint **0 errores/10 avisos**,
app y AndroidTest ensamblados; dos Android nuevas compiladas, cuatro vocales
preparadas en total, sin ejecutar. Host final **3 min 17 s**, con los recursos
limitados del comando siguiente. **360 fuentes/configuraciones** cotejadas por
SHA-256 con la copia aislada; **149 assets** empaquetados idénticos a V2.
APK: **15.240.120 bytes**, SHA-256
`e7dd2e2122ae3dbea337229016b0438d949b845dead145f6578aaa912ca80718`,
metadatos 0.12.17/32, código nuevo y firma compatible con V2 verificados, sin
instalar. Informe: tres pasadas XeLaTeX, 38 páginas, sin errores, referencias
indefinidas ni desbordes horizontales; cinco avisos verticales y bibliografía
vacía. Páginas físicas 27/30/31 inspeccionadas.

Desde `Plataforma/`, JDK y SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Acciones públicas de práctica/historial → resultado, reapertura y exportación:
ronda completa con la serie, interrupción idempotente, laboratorio intacto,
fallo tras commit/reintento, bloqueo de otra ronda, corrupción/esquema incompatible,
recuperación sin alterar bytes originales, drenaje antes de reset, lectura tardía
sin pérdida del pendiente, saneamiento de diagnósticos y callbacks de exportación.
Las pruebas controlan audio, tiempo y almacenamiento en sus límites; no miden
hardware ni estudiantes. Se mantienen los parámetros experimentales de V1/V2.

Dos Android adicionales preparadas para historial tras reabrir el modelo y para
fallo/reintento con almacenamiento controlado. Las anteriores de V2 conservan
navegación y ronda sintética; su reloj de prueba separa las dos etapas. Se compilan,
sin ejecutarlas. Cuando el usuario permita emular, ejecutar las cuatro y regresión
de Datos, reset, menú y juegos anteriores; verificar luego voz real en teléfono.

APK candidata:
`Plataforma/build/apk-archives/neuma-v0.12.17-intervalos-vocales-v3-prueba-sin-emulador.apk`.
Síntesis y XML seleccionados: `docs/evidencias/intervalos-vocales-v3/`.
Logs, snapshot, reportes y PDF: `Plataforma/build/verification/2026-10-30-vocal-v3/`.
No se instala, publica ni cambia el protocolo oficial o las issues en esta sesión.

Siguiente pendiente vocal: DDA Mamdani inspeccionable y progresión por intervalo,
revisando la ficha que todavía conserva umbrales/promedio móvil anteriores.
Persistir mediciones no acredita precisión vocal calibrada ni eficacia pedagógica.

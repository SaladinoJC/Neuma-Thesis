# Intervalos vocales V6 — producción libre

Usuario, 2026-10-30: «dale, segui con eso», tras ofrecer producción libre como
siguiente modo vocal. Candidata **0.12.20/código 35**, tercer andamiaje de la
[ficha](Ideas%20Desarrolladas/01-Ear-training-intervalos-reproduccion-vocal.md).
Mantiene Android/Kotlin/Compose, YIN y reglas Mamdani experimentales. No cambia
el protocolo oficial, incorpora ML ni acredita eficacia pedagógica. Continúan
no emular, sin subagentes y Personaje pausado.

## Recorrido

1. En Intervalos vocales, elegir **Producción libre** en Ayuda auditiva.
2. Elegir intervalo y dirección. Práctica permite todos; Progresión exige el
   hito de ancla dada de ese mismo intervalo y dirección.
3. En Progresión, iniciar una serie de cinco rondas. Habilitar micrófono si hace
   falta: conceder permiso por sí solo no inicia captura.
4. Tocar **Cantar primera nota** y sostener una nota cómoda elegida por el usuario.
   No hay botón Escuchar ni selector de registro de referencia. Una llamada a
   `playReference()` tampoco produce audio en este modo.
5. Tras confirmar el ancla y liberar la primera captura, tocar **Cantar segunda
   nota** y sostener el objetivo. La aplicación no reproduce ninguna de las notas.
6. Revisar error relativo, guardado e historial. Siguiente ronda conserva modo
   y aplica la tolerancia confirmada para el próximo ejercicio.

Se reutiliza `VocalIntervalSession` con ancla cantada: mediana logarítmica del
sostenido estable, congelada al confirmar la primera nota; objetivo relativo a
esa frecuencia. Ejemplo controlado: ancla 196 Hz y objetivo 392 Hz son 1200 cents,
independientemente del selector de referencia de otro modo. No se evalúa la
afinación absoluta del ancla ni se pliega un error de octava.

El indicador de desviación aparece durante el objetivo, respecto de
`1200 × log2(objetivoHz/anclaHz) − semitonos × 100`. Los valores de V1 se mantienen:
sostenido 500 ms, hueco máximo 100 ms, estabilidad 35 cents, rango 55–1000 Hz,
tiempo total de 60 s, incluida la espera entre capturas. Son parámetros de prueba,
no un criterio validado de aptitud vocal. El usuario debe elegir un registro en
el que ambas notas queden dentro del rango de análisis.

Cancelación, segundo plano, error o timeout cierran sin reanudación automática.
Callbacks tardíos no convierten una interrupción en una respuesta completa. Una
nueva ronda requiere liberar captura y resolver guardados pendientes.

## Progresión e inspección

El acceso de nivel 3 requiere el hito de nivel 2 para el intervalo firmado;
el hito de imitación por sí solo no alcanza. `VocalProgress.freeProductionIntervals`
conserva tolerancia, racha, evidencia e hito independientes. Empieza en 50 cents,
sin heredar los 30 cents o las series altas de las ayudas anteriores. Se mantienen
cinco rondas, dos series altas, límites 30–80, pasos de hasta 5 cents y las mismas
reglas Mamdani. Solo cambia la tolerancia de una ronda futura.

El commit comprueba contexto de serie y procedencia cantada. Producción libre
requiere además evidencia sostenida del ancla; no acepta como desempeño libre
un resultado de captura exclusiva de objetivo con ancla proporcionada. Mezclar
andamiajes en una serie no concede avance. Abandonar una serie parcial reinicia
su racha propia; no elimina hitos adquiridos de otros modos.

La práctica manual se etiqueta como tal y no habilita acceso ni alimenta DDA.
Los tres modos siguen seleccionándose explícitamente. La política global que
elige automáticamente cuánta ayuda dar sigue pendiente; este incremento completa
los recorridos de los tres andamiajes, no esa gobernanza de la ficha.

Historial y panel distinguen Producción libre y ancla cantada. Las simulaciones
usan almacenamiento aislado. Cuando necesitan prerrequisitos, incorporan hitos
sintéticos de imitación y ancla dada y los identifican como simulación. La ruta
completa de nivel 3 comprende **750 rondas: 250 por modo y 25 objetivos por modo**,
sin escribir al perfil ni participar estudiantes.

## Datos y compatibilidad

Se mantiene el archivo vocal dentro de los **nueve archivos** del perfil. Una
ronda libre registra `scaffoldingLevel=3`, `referenceListens=0`,
`referenceAnchorMidi=null`, `referenceTargetMidi=null`, `anchorSource=SUNG`, las
mediciones reales derivadas de ambas fases y configuración
`vocal-free-production-v1` o `vocal-free-production-dda-v1`. No se inventa un MIDI
de referencia para una nota elegida por el usuario. Los otros modos conservan
sus referencias y escuchas previas.

Los snapshots V5 sin los nuevos campos cargan con mapa libre vacío. Los hitos
previos de ancla dada pueden abrir acceso, sin acreditar respuestas libres ni
transferir tolerancia. Decisión y resultado siguen confirmándose en una sola
escritura; un reintento de confirmación incierta conserva el UUID y no duplica
avance. JSON exporta el mapa nuevo y las referencias nulas. ZIP conserva el mismo
original vocal; reinicio elimina los resultados de los tres modos. No hay nuevo
archivo, grabaciones, importación o checkpoint de una captura en curso. Los
pendientes en memoria siguen sin garantía de supervivencia a muerte del proceso.

## Presentación y verificación

Music Garden conserva papel cálido, verdes, tema/tokens, Fraunces y Nunito Sans.
Se reutilizan controles ≥48 dp, scroll y los estados existentes. Preparación,
espera entre notas, resultado, historial, panel y bloqueo sin accesos se adaptan
al modo. No hay assets, iconos, fuentes, librerías o animaciones nuevas; portada
compartida provisional conservada. La información del intervalo proviene de
semitonos estructurados, sin emojis ni imágenes como notación didáctica.

Se aplica `tdd` a las acciones/sesiones públicas y límites PCM/audio/almacenamiento
aprobados en `Plataforma/AGENTS.md`. `design-taste-frontend` se adapta al producto
Android existente: jerarquía de la tarea, datos reales derivados y estados vacíos
honestos; no impone un shell web o una dirección artística ajena.

Desde `Plataforma/`, con JDK y SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Pruebas: silencio de referencia, dos capturas separadas, ancla propia, series y
tolerancias independientes, bloqueo por dirección, práctica sin crédito,
compatibilidad V5, confirmación incierta, reapertura/exportación/ZIP/reset,
proveniencia incorrecta, series mezcladas/abandonadas y señales negativas.
La ruta sintética caracteriza reglas de software, no voz humana o aprendizaje.

Resultado final: **292 JVM aprobadas (11 nuevas)**, sin fallos, errores u omisiones;
lint **0 errores/10 avisos**. App/AndroidTest ensamblados en **4 min 1 s** sobre copia
aislada, con **370 fuentes/configuraciones** cotejadas por SHA-256 y **149 assets**
idénticos a V5. APK de **15.305.656 bytes**, SHA-256
`7c617dd076f7d547b6d19785f317edf9d8dfdeeebcc3158fe8cb4e8610f3f40c`;
metadatos/código empaquetado y firma compatible con V5 verificados. Informe
compilado en tres pasadas: **41 páginas**, sin errores/referencias indefinidas/
desbordes horizontales, cinco avisos verticales y bibliografía vacía. Páginas
físicas 28/29/33/34 inspeccionadas; corresponde al PDF académico, no a pantallas Android.

Dos Android adicionales preparadas para flujo silencioso/historial y bloqueo
sin hito de ancla dada. Solo compilar; **diez casos actuales en
`VocalIntervalAndroidTest`, pendientes de ejecución**, según recuento directo
de métodos `@Test` (corrige los totales sobreestimados de las guías anteriores).
Cuando el usuario permita emular: revisar 411/360/320 dp y 320/texto 200 %,
estados de permiso, controles ausentes, ambas capturas/liberación, pausa/timeout,
historial, panel/JSON/ZIP/reset, TalkBack y regresiones anteriores. Falta además
verificar voz real y calibrar parámetros; no se sustituye por simulación.

APK candidata:
`Plataforma/build/apk-archives/neuma-v0.12.20-intervalos-vocales-v6-prueba-sin-emulador.apk`.
Evidencia duradera: `docs/evidencias/intervalos-vocales-v6/`; logs, fuentes, banco
y PDF en `Plataforma/build/verification/2026-10-30-vocal-v6/`. Sin instalar,
push, publicación ni cambios de issues. Pendientes: selección automática global
de ayuda, validación Android/acústica y D6/#28; no iniciar otro incremento al cerrar V6.

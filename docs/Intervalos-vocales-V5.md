# Intervalos vocales V5 — ancla dada

Nota histórica: [V6](Intervalos-vocales-V6.md), 0.12.20, incorpora producción libre.
Los pendientes que siguen describen el cierre de V5.

Usuario, 2026-10-30: «Dale, vamos con ancla dada». Se implementa el andamiaje
**2/ancla dada** en **0.12.19/código 34**. La app reproduce solo el ancla y solicita
por nombre el intervalo; el jugador canta únicamente la nota objetivo. Se conservan
imitación y sus datos. Producción libre sigue pendiente. No se abre emulador ni se
instala la candidata; las pruebas Android solo se compilan.

## Uso

1. En Intervalos vocales, elegir **Ayuda auditiva → Ancla dada**.
2. Elegir **Práctica** para probar cualquier intervalo, o **Progresión** para una
   serie de cinco rondas de un intervalo habilitado.
3. Seleccionar intervalo/dirección y registro del ancla. Si falta permiso, puede
   habilitarse el micrófono desde la preparación, antes de iniciar la serie.
4. **Escuchar ancla** reproduce una nota, sin revelar sonoramente la respuesta.
5. **Cantar nota objetivo** abre una única captura. Sostener la nota solicitada
   sin vibrato hasta confirmar el sostenido y consultar la desviación.
6. Revisar el resultado, continuar la serie o preparar otra ronda. Historial,
   reintento, exportación y cancelación conservan el comportamiento de V3/V4.

Conceder permiso no inicia captura automáticamente. Cambiar ayuda auditiva,
intervalo o registro exige escuchar la referencia nueva. Las selecciones quedan
congeladas durante una ronda o serie. Pausar, cancelar o enviar la app a segundo
plano interrumpe sin reanudación automática ni acierto inventado.

## Evaluación y procedencia del ancla

La frecuencia sintetizada del ancla fija el denominador de
`1200 * log2(frecuenciaCantada/frecuenciaAncla)`. Se resta el intervalo pedido
para obtener la desviación. Se mantiene la evaluación de una relación entre
frecuencias; en este andamiaje la primera frecuencia procede del sistema.

La respuesta debe conservar esa relación con el ancla escuchada en el registro
elegido. Cantar solo el objetivo una octava más arriba conserva un error de
+1200 cents; no se pliega módulo octava. Para cambiar de registro, seleccionar
otra referencia antes de escuchar. Esta decisión implementa «desde acá» de la
ficha, sin exigir recantar el ancla ni atribuir al micrófono una frecuencia no medida.

`VocalIntervalSession` recibe opcionalmente `givenAnchorHz`, comienza preparada
para el objetivo y registra únicamente mediciones `TARGET`. Los criterios de
sostenido, silencio/gaps, periodicidad de YIN, timeout y cancelación se reutilizan.
El resultado conserva `anchorSource=GIVEN`; imitación usa `SUNG`, también valor por
defecto al leer resultados anteriores. El ancla reproducida no cuenta como muestra
de voz. Una respuesta sostenida pero incorrecta termina con feedback de error.

Se comparte `referenceFrequency` con el sintetizador para que reproducción y
evaluación usen el mismo valor. La referencia nominal continúa siendo una sinusoide
propia de 600 ms, amplitud 0,2 y fades de 20 ms. No hay segunda nota ni espera de
150 ms en ancla dada. El micrófono se abre por una acción posterior al cierre de
la reproducción. Esto acredita coordinación lógica, no ausencia medida de eco
acústico ni latencia del dispositivo.

## DDA y progreso separados

En progresión, ancla dada se habilita **por intervalo y dirección** al obtener el
hito correspondiente de imitación. No exige completar toda una etapa de ancla dada
para practicar otro intervalo que ya tenga su propio antecedente en imitación.
Esto separa la introducción de contenidos de los niveles de ayuda de la ficha.

El acceso habilitado no transfiere desempeño: ancla dada comienza en **50 cents**,
con racha cero y sin hito propio, aunque imitación haya llegado a 30 cents.
`VocalProgress.givenAnchorIntervals` conserva su estado independiente de `intervals`.
Las dos direcciones también permanecen separadas. Práctica manual permite todos
los intervalos y no concede acceso ni desempeño automático.

Se reutilizan las series de cinco rondas, Mamdani y parámetros experimentales de
V4: error absoluto medio y dispersión del sostenido final, paso máximo de 5 cents,
límites 30–80 y dos series altas para un hito del contexto. La tolerancia no cambia
mientras se canta. El commit valida el nivel, la procedencia y frecuencia del ancla,
las etapas de las mediciones y que una serie no mezcle andamiajes. Una metadata de
referencia incoherente no concede avance.

El modo de ayuda se elige explícitamente; V5 habilita el acceso por desempeño,
sin cambiar automáticamente una serie de imitación a ancla dada. Producción libre,
su desbloqueo y una política global de selección automática de ayuda quedan pendientes.
Los parámetros siguen siendo elecciones técnicas experimentales, sin eficacia acreditada.

## Persistencia e inspección

Se mantienen **nueve archivos** y los formatos contenedores versionados de V3/V4.
Cada registro conserva `scaffoldingLevel=2`, configuración específica, referencias,
resultado, `anchorSource` y serie/decisión cuando corresponde. `referenceTargetMidi`
representa el objetivo teórico; en ancla dada no significa que se haya reproducido.
El historial muestra el modo y distingue «Ancla reproducida» de «Ancla cantada».

Decisión y resultado siguen confirmándose juntos. Los snapshots incluyen ambos
mapas y el acceso nuevo a ancla dada; una confirmación incierta se reintenta por
UUID sin duplicación ni cruce de progreso. El JSON exporta ambos mapas mediante
`vocalIntervalProgress`; ZIP, pendientes y reset siguen incluyendo el mismo archivo.

Los perfiles V4 sin los campos nuevos se leen como imitación, con el mapa de ancla
dada vacío. Un hito anterior de imitación puede habilitar acceso a su intervalo,
sin acreditar ninguna ronda de ancla dada. No se migra ni reinterpreta una medición
antigua como nota cantada bajo el nuevo andamiaje.

El panel Progreso y DDA filtra por ayuda auditiva. Las simulaciones de ancla dada
incluyen hitos sintéticos de imitación cuando son necesarios; el panel identifica
esa preparación. Una ruta completa recorre **500 rondas: 250 de imitación y 250 de
ancla dada**, para 25 objetivos por modo. Usa memoria aislada, sin escribir al perfil.

## Presentación y verificaciones

Music Garden conserva tema, tokens, papel/verde, Fraunces/Nunito Sans y portada
compartida provisional. Selector de ayuda, mensajes de objetivo único, historial
e inspector reutilizan controles ≥48 dp, scroll y flujo adaptable. Sin nuevos
assets, fuentes, librerías o animaciones. La información musical deriva de
intervalos estructurados; no hay emojis o imágenes como sustitutos de notación.

La adaptación con `design-taste-frontend` mantiene jerarquía de la tarea y estados
vacíos/error honestos dentro del stack Android existente. No se aplica otro shell
web ni se afirma revisión visual nativa bajo la restricción de no emular.

Desde `Plataforma/`, JDK y SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Pruebas públicas: una referencia sin objetivo sonoro, una captura sin ancla vocal,
dirección/unísono/octava, silencio/timeout, PCM sintético → YIN → sesión, perfil V4,
historial/exportación/reset, series separadas, prerrequisito por dirección,
procedencia incorrecta, confirmación incierta, cancelación/callbacks y ruta sintética.
No miden voz humana, hardware ni eficacia pedagógica.

Resultado final en copia aislada: **281 JVM aprobadas (18 nuevas), sin fallos,
errores ni omisiones**. Lint **0 errores/10 avisos**; ensamblados app y AndroidTest
correctos en **3 min 16 s**. **368 fuentes/configuraciones** cotejadas por SHA-256;
**149 assets empaquetados** idénticos a V4. APK **15.305.656 bytes**, SHA-256
`eed2ede208a50ba282df44c8547f4f9db549c92a3a7106c72992c1a085069f0e`;
metadatos, código vocal empaquetado y firma compatible con V4 comprobados.
Informe compilado en tres pasadas: **40 páginas**, sin errores, referencias
indefinidas ni desbordes horizontales; cinco avisos verticales y bibliografía vacía.
Páginas físicas 28/29/32/33 revisadas visualmente; esta revisión corresponde al
PDF académico y no sustituye la inspección de pantallas Android.

Dos Android nuevas preparadas: práctica de ancla dada con reproducción/captura
controladas y guardado real en archivo temporal; acceso automático bloqueado sin
hito previo y práctica sin acreditarlo. Diez vocales en total, compiladas sin
ejecutar. Cuando el usuario habilite emular, verificar esas diez, regresiones,
411/360/320 dp y 320/texto 200 %, selector con contenido vacío, permisos antes/después
de referencia, registro, pausa/cancelación, TalkBack, JSON/ZIP/reset y sonido/voz real.

APK candidata:
`Plataforma/build/apk-archives/neuma-v0.12.19-intervalos-vocales-v5-prueba-sin-emulador.apk`.
Evidencia duradera: `docs/evidencias/intervalos-vocales-v5/`; logs, snapshot, banco y
PDF/capturas en `Plataforma/build/verification/2026-10-30-vocal-v5/`. Sin instalar,
push, publicación ni cambios de issues o protocolo oficial. Próximo modo vocal:
producción libre, todavía por implementar según la ficha.

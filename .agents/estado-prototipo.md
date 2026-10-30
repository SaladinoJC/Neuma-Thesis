# Estado del prototipo

Última actualización: 2026-10-30.

## Publicación en GitHub de V6 — 2026-10-30

Usuario: «ok, dejá todo en github con buenas practicas de git». Autorización de
commit/push y entrega externa que sustituye las restricciones de publicación de
las entradas históricas. Rama `trabajo/integracion-v0.12.15`, dos commits separados:
`b2b524b` (implementación/pruebas/guías) y `413061a` (informe/evidencia V1–V6).
Push atómico de rama y etiqueta anotada `v0.12.20-preview.1`, sin reescribir historial.
La etiqueta apunta a `413061a22a3648bb3bf1ab51838235d55319cf30`.

PR #30 se actualiza con el alcance final y se conserva en borrador hacia `main`.
Entrega preliminar en borrador con cinco adjuntos (APK, PDF, ZIP de 195 archivos
de evidencia V1–V6, manifiesto y SHA256SUMS), conservada en el repositorio privado.
Enlaces, commits y condiciones en `docs/Entrega-GitHub-v0.12.20.md`.
Este registro se versiona en un commit documental posterior al tag de la entrega.
No hay merge, instalación, emulación ni nueva validación Android; código sin
cambios desde las 292 JVM/lint/ensamblados de V6. Próximos pendientes vocales y D6
conservados; Personaje pausado, sin subagentes.

## Intervalos vocales V6 — producción libre

Usuario: «dale, segui con eso», tras ofrecer producción libre como siguiente
modo vocal. Implementada **0.12.20/código 35**: no reproduce referencia, el usuario
elige su nota inicial al cantar y completa ancla/objetivo en dos capturas explícitas.
Evalúa intervalo relativo sin exigir afinación absoluta ni plegar errores de
octava. UI omite escucha y registro de referencia; historial conserva ancla propia,
cero escuchas y referencias MIDI nulas. No se inventa un MIDI para el ancla cantada.

El hito de ancla dada habilita nivel 3 por intervalo/dirección; tolerancia, racha
y evidencia independientes en `freeProductionIntervals`. Mismas series de cinco,
Mamdani y commit atómico; práctica sin crédito. Compatibilidad V5, JSON/ZIP/reset
en los mismos nueve archivos; inspección/simulaciones por modo. Ruta completa
sintética **750 rondas, 250 por modo y 25 objetivos por modo**, sin hardware,
estudiantes o escritura al perfil. Parámetros experimentales sin calibrar.

**292 JVM aprobadas (11 nuevas)**, sin fallos/errores/omisiones. Lint **0 errores/
10 avisos**; app y AndroidTest ensamblados en **4 min 1 s**. Dos Android nuevas
compiladas, sin ejecutar. **Recuento directo actual: diez métodos `@Test` en
`VocalIntervalAndroidTest`**; corrige los totales vocales históricos sobreestimados
(V5 tenía ocho, no diez). No agregar casos solo para sostener esos totales.
**370 fuentes/configuraciones** cotejadas por SHA-256; **149 assets** idénticos a V5.
Gradle offline/un worker/1536 MiB/SerialGC/dos procesadores/BelowNormal.
Candidata sin instalar:
`Plataforma/build/apk-archives/neuma-v0.12.20-intervalos-vocales-v6-prueba-sin-emulador.apk`,
**15.305.656 bytes**, SHA-256
`7c617dd076f7d547b6d19785f317edf9d8dfdeeebcc3158fe8cb4e8610f3f40c`.
Metadatos/código nuevo y firma compatible con V5 comprobados.

Guía `docs/Intervalos-vocales-V6.md`; síntesis/XML/manifiesto en
`docs/evidencias/intervalos-vocales-v6/`, logs/fuentes/banco/PDF en
`Plataforma/build/verification/2026-10-30-vocal-v6/`. Informe: tres pasadas XeLaTeX,
**41 páginas**, sin errores/referencias indefinidas/desbordes horizontales; cinco
avisos verticales y bibliografía vacía. Páginas físicas 28/29/33/34 inspeccionadas.
Capítulos 10/11, seguimiento, ficha, README y arte/inventario actualizados.
Music Garden conserva tokens/papel/verdes/Fraunces/Nunito Sans y componentes;
sin assets/dependencias/animaciones nuevas. Portada compartida provisional
conservada. Sin inspección nativa de dimensiones/texto 200 %/TalkBack ni voz real.
No emulador, instalación, subagentes, push, publicación ni cambios de issues o
protocolo oficial; Personaje pausado.

**Próximo pendiente vocal:** política global de selección automática de ayuda
por intervalo/dirección. Los tres recorridos están implementados y se eligen
explícitamente; no repetir V1–V6 ni dar por terminada esa gobernanza de la ficha.
D6/#28 sigue pendiente. Cuando el usuario permita emular, ejecutar los diez casos
vocales actuales y regresiones; revisar 411/360/320 dp y 320/texto 200 %, permisos,
capturas/liberación, pausa/timeout, historial/panel/JSON/ZIP/reset, TalkBack y voz
real. Última validación Android completa 0.12.10; candidata actual V6 0.12.20.
Las entradas siguientes son antecedentes históricos.

## Intervalos vocales V5 — ancla dada

Usuario: «Dale, vamos con ancla dada». Implementada **0.12.19/código 34**:
segundo andamiaje seleccionable, una referencia sonora del ancla y una única
captura del objetivo. Evaluación relativa a la frecuencia reproducida, sin plegar
errores de octava ni inventar mediciones de ancla cantada. Historial/exportación
distinguen procedencia y ayuda auditiva. Permiso solicitado explícitamente antes
de iniciar; concederlo no inicia captura automáticamente.

El hito de imitación abre ese intervalo/dirección en ancla dada. Tolerancia,
racha e hitos propios, sin transferir desempeño; mismas series de cinco rondas,
Mamdani y confirmación atómica. Práctica manual sin avance. Perfiles V4 compatibles,
nueve archivos, JSON/ZIP/reset integrados; panel y simulaciones por andamiaje.
Selección explícita de ayuda; producción libre y selección automática global
siguen pendientes. Parámetros experimentales del agente, sin calibración.

**281 JVM aprobadas (18 nuevas)**; ruta sintética de **500 rondas**, 250 por modo,
25 objetivos por modo. Lint **0 errores/10 avisos**, app/AndroidTest ensamblados en
**3 min 16 s**. Dos Android nuevas compiladas, diez vocales preparadas en total,
ninguna ejecutada. **368 fuentes/configuraciones** cotejadas por SHA-256 y
**149 assets** idénticos a V4. Gradle offline, un worker, 1536 MiB, SerialGC,
dos procesadores y BelowNormal. Candidata sin instalar:
`Plataforma/build/apk-archives/neuma-v0.12.19-intervalos-vocales-v5-prueba-sin-emulador.apk`,
**15.305.656 bytes**, SHA-256
`eed2ede208a50ba282df44c8547f4f9db549c92a3a7106c72992c1a085069f0e`.
Metadatos, código empaquetado y firma compatible con V4 verificados.

Guía `docs/Intervalos-vocales-V5.md`; evidencia duradera en
`docs/evidencias/intervalos-vocales-v5/` y logs/snapshot/banco completo en
`Plataforma/build/verification/2026-10-30-vocal-v5/`. Informe: tres pasadas XeLaTeX,
**40 páginas**, sin errores/referencias indefinidas/desbordes horizontales; cinco
avisos verticales y bibliografía vacía. Páginas físicas 28/29/32/33 inspeccionadas.
Ficha, README, arte/inventario y capítulos 10/11/seguimiento actualizados.
Music Garden conserva tokens, papel/verdes, fuentes y componentes; sin assets,
dependencias o animaciones nuevas. Portada compartida provisional conservada.
Android, dimensiones/texto ampliado, TalkBack, voz real y eficacia siguen sin
validar. No emulador, instalación, subagentes, push, publicación ni cambios de
issues/protocolo oficial. Personaje pausado.

**Próximo frente vocal:** producción libre y su acceso según la ficha; posteriormente
selección automática global de ayuda. D6/#28 sigue pendiente. Cuando el usuario
permita emular, ejecutar las diez vocales y regresiones anteriores; revisar
411/360/320 dp y 320/texto 200 %, permisos, ciclo de vida, historial/JSON/ZIP/reset,
TalkBack y voz real. Última validación Android completa: 0.12.10; candidata actual
V5 0.12.19. Las entradas siguientes son antecedentes históricos.

## Intervalos vocales V4 — series Mamdani y progreso en imitación

Usuario: «ok, metele», tras proponer DDA/progresión. Se implementa **0.12.18/código 33**,
limitado al andamiaje **1/imitación**: partidas de cinco rondas, adaptación de
tolerancia entre rondas e hitos por intervalo/dirección. Ancla dada/producción
libre todavía pendientes. Se mantienen no emular, sin subagentes y Personaje pausado.

Modo Progresión predeterminado en la app; Práctica manual sin avance conserva V3.
Mamdani común, cinco reglas vocales, entradas del error absoluto medio y dispersión
en el sostenido final. Configuración experimental del agente: precisión=1−MAE/100,
regularidad=1−desviación/35 (ambas limitadas 0–1), centroide de 1001 puntos,
alto ≥0,70/medio ≥0,40; tolerancia inicial 50, rango 30–80, paso ±5 solo para la siguiente
ronda. Alto exige más que entrar en tolerancia. No se implementa CA validada.

Dos series de cinco rondas elegibles altas registran hito por intervalo firmado.
Orden provisional: unísono/octavas → cuartas/quintas → terceras/sextas → segundas/
séptimas → tritonos, 25 objetivos en total; cada etapa requiere todos los hitos
de la anterior. Direcciones separadas; accesos permanentes. Manual, bloqueados,
ordinales repetidos/fuera de orden, tolerancia ajena, interrupciones y protocolo
abreviado no conceden avance. Serie parcial rompe su racha al empezar otra;
pasar a segundo plano cierra incluso entre rondas, sin reanudar automáticamente.

Resultado+decisión/snapshots/configuración se confirman juntos en el mismo archivo
vocal, sin décimo archivo. Pendientes no avanzan el estado visible; reintento por
UUID no duplica decisiones. Perfiles V3 siguen siendo manuales sin avance retroactivo.
JSON agrega `vocalIntervalProgress`; ZIP/reset siguen con nueve archivos.
Panel Progreso y DDA con funciones/reglas/centroide/deltas y seis escenarios aislados,
sin guardar simulaciones como desempeño. Historial expone origen/serie/decisión.

**263 JVM aprobadas (14 nuevas)**. Ruta pública sintética de **250 rondas para 25
objetivos**, sin voz real ni eficacia acreditada. Dos Android nuevas compiladas;
ocho vocales preparadas en total, ninguna ejecutada. Lint **0 errores/10 avisos**,
app/AndroidTest ensamblados en **3 min 25 s**, offline/un worker/1536 MiB/SerialGC/
dos procesadores/BelowNormal. **365 fuentes/configuraciones** idénticas a lo probado,
**149 assets** iguales a V3. Candidata
`Plataforma/build/apk-archives/neuma-v0.12.18-intervalos-vocales-v4-prueba-sin-emulador.apk`,
**15.289.272 bytes**, SHA-256
`ca5f835849fd173e4df3d4ba2e22f327cded56ad1c7e04be10cef81bfb79c9c8`;
metadatos/código nuevo y firma compatible con V3 verificados. Informe: tres pasadas,
**39 páginas**, sin errores/referencias indefinidas/desbordes horizontales, cinco
avisos verticales y bibliografía vacía; páginas 27/28/32 inspeccionadas.
Síntesis en `docs/evidencias/intervalos-vocales-v4/`;
logs/snapshot/banco completo en `Plataforma/build/verification/2026-10-30-vocal-v4/`.
Guía `docs/Intervalos-vocales-V4.md`; ficha, README, arte/inventario y capítulos
10/11/seguimiento actualizados. Sin assets, dependencias ni animaciones nuevas;
tema/tokens/fuentes y portada compartida provisional conservados. Capturas/tamaños/
texto 200 %/TalkBack/sonido nativos pendientes. Sin instalar, push, publicación,
modificación de issues o protocolo oficial. Las reglas no están calibradas.

**Próximo frente vocal:** definir el incremento de ancla dada/producción libre
conforme la ficha; no darlos por implementados ni repetir V1–V4. Cuando el usuario
permita emular, ejecutar las ocho vocales, regresiones anteriores y revisar
411/360/320 dp y 320/texto 200 %, panel real/simulado, permisos, ciclo de vida,
historial/JSON/ZIP/reset y voz real. D6/#28 sigue pendiente. Última entrega completa
validada Android sigue siendo 0.12.10; candidata más reciente V4 0.12.18.

## Intervalos vocales V3 — persistencia/exportación verificadas en host

Usuario: «sigamos con lo que puedas», tras entregar V2 y dejar persistencia/
exportación como próximo incremento. Se implementa V3, **0.12.17/código 32**,
sin emulador, subagentes, instalación ni publicación; Personaje sigue pausado.

Rondas completas/interrumpidas se guardan por UUID estable en
`vocal-interval-results.json`, **noveno archivo** del perfil. No se mezclan con
el laboratorio `voice-results.json`. Registro con referencia/escuchas completas,
origen manual/andamiaje 1, fechas, versiones, dispositivo, configuración YIN/tasa
y serie derivada completa, ahora con procesamiento/periodicidad/RMS saneados.
No se guarda PCM. Historial/detalle paginados, estados guardado/pendiente/error,
reintento idempotente y bloqueo de otra ronda mientras haya pendiente/almacenamiento
ocupado. Conserva pendientes en memoria y exportables; no hay checkpoint de captura
abierta ni garantía de conservar memoria tras muerte de proceso.

JSON agrega `vocalIntervals` y `pendingVocalIntervalSaves`, con listas vacías por
defecto para exportaciones antiguas. ZIP conserva el nuevo original sin parsear
y agrega `pending-vocal-interval-saves.json` cuando corresponde. Corrupción o
esquema incompatible no reemplazan originales; no se implementa importación.
Drenaje/reinicio esperan almacenamiento y limpian los nueve archivos/copias en
memoria después del reset solicitado. Perfiles antiguos funcionan sin migración.

**249 JVM aprobadas, 10 nuevas**, incluidos reapertura/serie/telemetría, commit
incierto/reintento, bloqueo, interrupción única, originales dañados/incompatibles,
ZIP con pendientes, reset/drenaje, lectura tardía, saneamiento y callbacks de Datos.
Dos Android nuevas preparadas/compiladas; cuatro vocales en total, sin ejecutar.
Lint **0 errores/10 avisos**, app/AndroidTest ensamblados en **3 min 17 s**, offline/
un worker/1536 MiB/SerialGC/dos procesadores/BelowNormal. **360 fuentes/configuraciones**
idénticas por SHA-256 a lo probado; **149 assets** iguales a V2. Candidata
`Plataforma/build/apk-archives/neuma-v0.12.17-intervalos-vocales-v3-prueba-sin-emulador.apk`,
**15.240.120 bytes**, SHA-256
`e7dd2e2122ae3dbea337229016b0438d949b845dead145f6578aaa912ca80718`,
metadatos/código nuevo y firma compatible con V2 verificados. Sin instalar.

Guía `docs/Intervalos-vocales-V3.md`; evidencia `docs/evidencias/intervalos-vocales-v3/`;
logs, snapshot, PDF/capturas en `Plataforma/build/verification/2026-10-30-vocal-v3/`.
Informe: tres pasadas, **38 páginas**, sin errores/referencias indefinidas/desbordes
horizontales; cinco avisos verticales, bibliografía vacía, páginas 27/30/31 inspeccionadas.
Music Garden conserva tema/tokens/fuentes/portada compartida provisional de V2;
sin assets, dependencias o animaciones nuevas. Historial vacío/cargando/lista/
detalle/pendiente/error preparados; tamaños/texto 200 %/TalkBack aún sin revisión
nativa. Ficha, README, arte/inventario y capítulos 10/11/seguimiento actualizados.
Sin push, cambios de issues ni protocolo oficial; parámetros siguen experimentales.

**Próximo pendiente vocal:** DDA Mamdani inspeccionable y progresión por intervalo,
revisando la ficha que conserva umbrales/promedio móvil anteriores. V1/V2/V3 ya
implementados, no repetirlos. Cuando el usuario habilite emular, ejecutar las
cuatro vocales, Datos/historial/JSON/ZIP/reset y regresiones anteriores; revisar
411/360/320 dp y 320/texto 200 %, ciclo de vida, TalkBack y voz real. D6/#28 sigue
pendiente; última entrega completa validada Android continúa siendo 0.12.10.

## Intervalos vocales V2 — ronda manual conectada, Android pendiente

Usuario: «dale, hacelo» después de proponer conectar V1 a una ronda jugable con
referencia y micrófono. Se implementa V2 sin emulador ni subagentes; Personaje
continúa pausado. **0.12.16/código 31**, candidata de prueba sin validación Android.

Quinta portada de Juegos/índice; imitación manual con selección de intervalo,
dirección y referencia (por defecto quinta ascendente/La3). Reutiliza referencia
sinusoidal y capturador YIN de Android. Referencia completa antes de cantar;
dos capturas separadas con liberación entre etapas, acciones serializadas y
generación contra callbacks antiguos. Cancelaciones repetidas conservan la
espera de liberación del recurso anterior. Permiso concedido no inicia captura
automática. Ciclo de vida interrumpe, no reanuda; volver al menú espera limpieza.
Timeout global 60 s incluso sin callbacks, mediciones completas sin recorte del
laboratorio, feedback continuo relativo en segunda nota y resumen en memoria.

**239 JVM sin fallos (11 nuevas)**, incluidos flujo completo, fallos, selección,
timeout, liberación lenta/cancelaciones repetidas y callbacks tardíos. Dos Android
preparadas y compiladas para navegación y resumen sintético; sin ejecutar. Guía
`docs/Intervalos-vocales-V2.md`; evidencia duradera
`docs/evidencias/intervalos-vocales-v2/`; logs/snapshot completos en
`Plataforma/build/verification/2026-10-30-vocal-v2/`. Lint **0 errores/10 avisos**,
app y AndroidTest ensamblados. Host final **3 min 32 s**, offline/un worker/1536 MiB/
SerialGC/dos procesadores/BelowNormal. **356 fuentes/configuraciones** idénticas
por SHA-256 a lo probado; **149 assets** idénticos a D5. APK archivado
`Plataforma/build/apk-archives/neuma-v0.12.16-intervalos-vocales-v2-prueba-sin-emulador.apk`,
**15.888.475 bytes**, SHA-256
`add1cf61345f419856adc2b8ea0cedd264ad5600b9600253168e4ce1b882dcf0`,
metadatos/código vocal y firma compatible con D5 comprobados. Informe: tres
pasadas, **38 páginas**, sin errores/referencias indefinidas/desbordes horizontales,
seis avisos verticales y bibliografía vacía; páginas físicas 26/27/30 inspeccionadas.

Music Garden con tema/tokens/fuentes existentes, controles de 48 dp, scroll
vertical, fila de dirección desplazable y regiones accesibles de estado, sin
animaciones nuevas. Portada vocal reutiliza **provisionalmente** la ilustración
de Instrumentos; no es arte vocal final. Sin bitmaps, fuentes o dependencias
nuevos. Sin revisión nativa de capturas/tamaños/texto 200 %/TalkBack/audio.

Sin persistencia/exportación ni DDA/progresión vocales todavía; no escribe perfil
ni audio. Resumen en memoria hasta Nueva ronda, reinicio o muerte de proceso.
Los ocho archivos existentes se conservan; drenaje/reinicio incluyen el modelo
vocal. Intervalos/registros y parámetros de V1 son elecciones experimentales del
agente, sin currículum ni calibración acreditados. Logros no inventa progreso vocal.
Ficha/README/arte/inventario y capítulos 10/11/seguimiento actualizados. Sin push,
publicación, instalación, cambios de issues ni protocolo oficial.

**Próximo incremento vocal:** persistencia/exportación y recuperación del resultado
antes de incorporar DDA Mamdani inspeccionable y progresión por intervalo. Cuando
el usuario habilite emular, ejecutar las dos Android, regresión de menú y pendientes
anteriores, con 411/360/320 dp, 320/texto 200 %, permisos, pausa/recreación, TalkBack
y sonido real. No reimplementar V1/V2/R6/D3/D4/D5. D6/#28 permanece pendiente.
La candidata más reciente es V2 0.12.16; última validada Android: 0.12.10.

## Intervalos vocales V1 — núcleo verificado en JVM, integración pendiente

Usuario: «continuá con lo que haya pendiente». Se retoma la prioridad vocal
expresada en la entrevista del informe; el usuario confirma explícitamente
«Seguir con el ejercicio vocal» en la consulta frente a D6 durante esta sesión.
Esto confirma el frente, sin aprobar una nueva especificación completa.
Continúan **sin emulador, sin subagentes y Personaje pausado**.

`VocalIntervalSession` implementa una ronda manual de imitación: adquirir ancla
cantada sostenida, comenzar explícitamente la segunda nota y evaluar el salto
relativo firmado, sin plegar errores de octava. Conserva serie temporal y configuración;
silencio/discontinuidad reinician el sostenido; timeout, salida o límite de datos
interrumpen sin resultado musical. Núcleo puro sin pantalla ni escritura al perfil.
Parámetros del agente, experimentales: 500 ms, hueco máximo 100 ms, amplitud
35 cents, tolerancia ±50 cents, 55–1000 Hz, límite 60 s/10.000 mediciones.
No se implementan aún DDA Mamdani, CA, progresión, ML u Oboe.

**228 JVM sin fallos, 10 nuevas**; primer caso rojo por API ausente y verde posterior.
Casos públicos de relación ascendente/descendente/unísono/transposición, error
de octava, señal inválida, discontinuidad, ataque inestable, timeout,
interrupción, callbacks antiguos y límite de datos. PCM sinusoidal → YIN → sesión:
cuarta de **500,027915 cents**, error **+0,027915 cents** en ese único caso sintético,
sin acreditar precisión vocal ni latencia física. Guía `docs/Intervalos-vocales-V1.md`;
evidencia `docs/evidencias/intervalos-vocales-v1/` y logs locales
`Plataforma/build/verification/2026-10-30-vocal-v1/`. Lint **0 errores/10 avisos**
en esta ejecución offline; app y AndroidTest ensamblados, sin ejecutar Android.
Host final **2 min 14 s**, un worker/1536 MiB/SerialGC/dos procesadores/BelowNormal.
352 fuentes/configuraciones comparadas: 351 SHA-256 idénticos; una diferencia
exclusivamente de línea vacía final en `EarTunerApp.kt` de la copia de verificación,
sin cambio funcional; snapshot exacto de lo probado conservado. Informe: tres
pasadas XeLaTeX, **36 páginas**, sin errores ni desbordes horizontales, cuatro
avisos verticales y bibliografía vacía; páginas físicas 25/26/29 inspeccionadas.

No se modifican pantallas, assets, tokens, marca ni animaciones; Music Garden vigente.
**Todavía no es un juego disponible en la app**, no se instala ni se genera nueva
entrega de usuario: candidata vigente D5 0.12.15, última validada Android 0.12.10.
Ficha, README y registro técnico/plan de los capítulos 10/11 actualizados.
Sin push, publicación, cambios de issues ni modificación del protocolo oficial.

**Próximo paso vocal:** integrar referencia y captura en una ronda de imitación
jugable, con permiso, sostenido sin vibrato, separación de etapas, pausa/cancelación
y errores visibles. Consumir las estimaciones completas del capturador, no la serie
reducida del laboratorio (200 ms); serializar acciones y descartar callbacks de
capturas anteriores. Después persistencia/exportación y DDA Mamdani inspeccionable.
Android solo cuando el usuario lo permita. D6/#28 y los pendientes Android
R6/D3/D4/D5 y anteriores siguen disponibles; no reimplementar esos incrementos.
El promedio móvil de la ficha no sustituye la decisión Mamdani; la discrepancia
académica Oboe/ML sigue en `Informe/contraste-protocolo.md`.

## Agradecimientos del informe — 2026-10-30

Tras entrevista Q1–Q16, usuario autoriza guía y primer borrador. Guía persistente:
`Informe/guia-agradecimientos.md`; fuente: `Informe/preliminares/agradecimientos.tex`.
Padres, amigos, Leonel y profesores redactados para revisión; Abril, Lydia, hermanos,
menciones individuales y canciones pendientes se retoman cuando el autor quiera.
Dedicatoria propia a Abril acordada, texto pendiente. El autor elegirá las canciones,
sin forzarlas. Compilación y revisión visual registradas en `Informe/seguimiento.md`.
Trabajo exclusivamente documental; no modifica el prototipo.

## Acuerdos para el informe — 2026-10-30

Usuario confirma síntesis de entrevista Q1–Q11 y pide aplicarla con `thesis-writing`.
Capítulos 1/2 revisados: integración vocal/retroalimentación/DDA; destinatarios principalmente
de conservatorio con base musical y uso complementario; motivación personal; variaciones por
habilidad sin acreditar transferencia; sesiones iniciales con adultos; entrega gratuita,
offline y código/documentación abiertos. «Aprender» queda fuera del alcance actual.
Usuario mantiene el ejercicio vocal de intervalos con adaptación y lo identifica como próximo
desarrollo deseado; esta sesión solo redacta, no inicia código ni cancela tickets anteriores.
Al retomar implementación, considerar esta prioridad expresada por el usuario.
Autor acepta Oboe/ML como alternativas sujetas a medición; acuerdo con director pendiente.
Procedencia y discrepancias: `Informe/contraste-protocolo.md`; escritura: `Informe/seguimiento.md`.

## D5 implementado y verificado en host — Android pendiente

Usuario, 2026-10-30: «Seguí con el D5». Se implementa **D5/#27**, sin subagentes y conservando la prohibición de **abrir el emulador**. Sin instalación ni ejecución de pruebas en dispositivos. Personaje continúa pausado. Este pedido concreto rige el incremento actual; no cancela la prioridad vocal expresada en la entrevista del informe.

**0.12.15/código 30, candidata:** ocho calidades, inversiones cerradas (hasta segunda en tríadas y tercera en séptimas), sol/fa/mezcla. Modelo con raíz conceptual escrita y voces efectivas; respuesta raíz+alteración+calidad, sin octava/inversión. Catálogo de **1.502 realizaciones**, 751 por clave, voces MIDI 35–85 y hasta dos líneas adicionales. Pool finito, sin dobles alteraciones ni respuestas duplicadas vivas entre registros/inversiones/claves. Mezcla alterna claves por aparición efectiva. Audio propio de tres/cuatro sinusoides en el registro mostrado; runtime 44.100 Hz, banco espectral de extremos a 8.000 Hz de análisis. No se guarda PCM.

Elecciones técnicas del agente, según especificación provisional: **84 objetivos** por calidad/inversión/claves en `chordContents`, conservando `qualities` solo como fundamental/sol de D4. Las cuatro tríadas dominadas habilitan dominante7 en sol; no acreditan fa/inversiones. Ocho fundamentales en sol habilitan fa y primeras inversiones; ocho en fa habilitan mezcla; cada inversión exige su antecedente en el mismo contexto. Mezcla exige evidencia y dominio por ambas claves, con precisión global como condición común. Misma persistencia/sellado/revisiones/recuperación de D3/D4, ocho archivos de perfil, exportación compatible ampliada. Parámetros y grafo experimentales, sin eficacia pedagógica acreditada.

**218 JVM sin fallos (9 nuevas)**: catálogo completo, grafías/inversiones/segundas, unicidad, audio de extremos, grafo sin ciclos, ruta pública sintética de **168 partidas** para 84 objetivos, mezcla sin fa e inversión ajena insuficientes, compatibilidad D3/D4, reapertura/exportación y ViewModel. Dos Android nuevas preparadas para séptima invertida/fa y mezcla/reducción/pausa/bloqueos; sin ejecutar. Lint/ensamblados y artefactos finales registrados en `docs/Demostracion-defender-D5.md` y `docs/evidencias/defender-d5/host-summary.json`; logs/fuentes en `Plataforma/build/verification/2026-10-30-D5/`. Gradle offline, un worker, heap 1536 MiB, SerialGC, dos procesadores y prioridad BelowNormal; 350 fuentes/configuraciones cotejadas por SHA-256 con la copia aislada.

Music Garden conserva tema/tokens/fuentes/gráficos de D3, sin bitmaps ni dependencias nuevas. Canvas de acordes 144×120, tres columnas de alteraciones y cabezas alternadas en segundas; tarjetas/reservas mayores y fila de calidades desplazable. Preparación, respuesta, resumen, pérdidas, filtros e inspector ampliados. Persiste la desviación de gráficos simplificados y vidas textuales. **Sin validación de capturas, cuatro objetivos, texto 200 %, TalkBack ni sonido Android**. Registro técnico/plan en capítulos 10/11 y seguimiento. Sin push, publicación ni cambios/cierre de issues.

Cierre de host: lint **0 errores/17 advertencias**, app/AndroidTest ensamblados, **10 min 40 s**. Candidata `Plataforma/build/apk-archives/neuma-v0.12.15-defender-d5-prueba-sin-emulador.apk`, **15.158.200 bytes**, SHA-256 `c53c44fe54e5699b2f89ba0ae73ef484e382e2bb93b39bd0d83eaffb2cc22ba3`; firma compatible con D4 y metadatos comprobados, sin instalar. **149 assets** idénticos a D4.

Informe tras D5: **36 páginas** en tres pasadas XeLaTeX sin errores, referencias indefinidas ni desbordes horizontales. Revisión visual de páginas físicas 25/26/28/29; bibliografía vacía y cinco avisos verticales por contenido provisional. Portada, estilo, capítulos 1/2 y Agradecimientos conservados. Evidencia local del PDF/log/capturas junto a la verificación de D5.

**Siguiente en la ruta Defender: D6/#28**, piano inicial, fuera de D5; la prioridad entre esa ruta y el ejercicio vocal se toma del próximo pedido del usuario. No reimplementar R6/D3/D4/D5. Cuando el usuario habilite emular, ejecutar D5/D4/D3 y regresión D1/D2, con 411/360/320 dp y 320/texto 200 %, cuatro objetivos, segundas/alteraciones, controles desplazables, reducción, pausa, persistencia/exportación/JSON nativo y sonido de cuatro voces. R6 y lectura/marca anteriores conservan pendientes. Última entrega completa verificada en Android: 0.12.10. La orden de no emular sigue vigente.

## D4 implementado y verificado en host — Android pendiente

Usuario, 2026-10-30: «Dale, metele a D4». Se implementa **D4/#26**, sin subagentes y manteniendo la prohibición de **abrir el emulador**. No se instalaron APK ni se ejecutaron pruebas en dispositivos. Personaje sigue pausado.

**0.12.14/código 29, candidata de prueba:** Defender de tríadas mayor/menor/disminuida/aumentada, en fundamental y sol, con raíz escrita+alteración+calidad sin octava. Catálogo finito de **115 realizaciones** con terceras/quintas diatónicas, hasta dos líneas adicionales, sin dobles alteraciones; grafías enarmónicas no intercambiables. Sin respuestas duplicadas vivas aunque cambie el registro. Error conserva selección; acierto la limpia y reproduce las tres voces reales sintetizadas. Se reutilizan tiempos, pausa, cierre provisional/sellado, DDA y guardado coordinado de D3.

Elecciones técnicas provisionales del agente: calidades y progresos independientes por variante en el mismo `defender-results.json`, sin noveno archivo. Notas no habilitan tríadas y viceversa. Cadena mayor→menor→disminuida→aumentada; práctica ofrece las cuatro. Exportación conserva el campo anterior de notas y agrega `defenderProgressByVariant`; lectura de JSON D3 sin nuevos campos comprobada. Historial/filtros/detalle/inspector y banco aislado admiten tríadas. Al cambiar de variante se descarta la simulación anterior para no mezclar su alcance. Las reglas y los umbrales continúan experimentales, sin eficacia pedagógica acreditada.

**Verificación:** **209 JVM sin fallos (13 nuevas)**, lint **0 errores/17 advertencias**, app y AndroidTest compilados; dos casos Android nuevos preparados, sin ejecutar. Gradle offline, un worker, heap 1536 MiB, SerialGC, dos procesadores, prioridad BelowNormal; **346 fuentes/configuraciones** cotejadas por SHA-256 con la copia aislada. Evidencia completa en `Plataforma/build/verification/2026-10-30-D4/`, selección duradera en `docs/evidencias/defender-d4/`. Candidata `Plataforma/build/apk-archives/neuma-v0.12.14-defender-d4-prueba-sin-emulador.apk`, sin instalar; datos de firma/hash en la guía y síntesis de evidencia.

Music Garden conservado: tema/tokens/fuentes/controles y árbol/polillas de D3, pentagrama Canvas con tres voces estructuradas, plica común y alteraciones escalonadas; tarjetas/reservas ampliadas. Inventario y guía de arte actualizados, sin bitmaps, dependencias ni animaciones nuevas. Persiste la desviación provisional de gráficos simplificados/vidas textuales. **Sin revisión de capturas, legibilidad de cuatro tríadas, dimensiones, texto ampliado, TalkBack ni sonido Android**. Guía `docs/Demostracion-defender-D4.md`; registro técnico/plan de pruebas en capítulos 10/11 y seguimiento del informe. No hubo push, publicación ni modificación/cierre de issues.

Artefacto D4: **15.265.361 bytes**, SHA-256 `0ffa063af2a83d779be5c56c15039129d5f0d5d4069cfe24c96f5adbf2cbb718`, firma compatible con D3 verificada; **149 assets** idénticos a D3. Verificación final tras revisión: **5 min 58 s**. Informe compilado en tres pasadas XeLaTeX: **34 páginas**, sin errores, referencias indefinidas ni desbordes horizontales; revisión visual de páginas físicas 25/27. Bibliografía vacía y cuatro avisos verticales por contenido provisional, sin cambios de estilo.

**Próximo software: D5/#27**, séptimas, inversiones, fa y mezcla; consultar ticket/especificación antes de implementar. No reimplementar R6/D3/D4. Cuando el usuario permita emular, ejecutar `DefenderD4AndroidTest`, D3 y regresión D1/D2; revisar 411/360/320 dp y 320/texto 200 %, cuatro objetivos/controles, reducción, pausa, persistencia/exportación/JSON nativo y audio de tres voces. R6 y lectura/marca anteriores conservan pendientes. Última entrega completa verificada en Android: 0.12.10. La prohibición de emular sigue vigente hasta que el usuario la cambie.

## D3 implementado y verificado en host — Android pendiente

Usuario, 2026-10-30: «Dale, metele» tras proponer D3 de Defender como siguiente incremento. Se implementa **D3/#25**, un frente y sin subagentes, manteniendo la instrucción anterior de **no abrir el emulador**. No se ejecutaron pruebas en dispositivos. Personaje sigue pausado.

**0.12.13/código 28, candidata de prueba:** notas con práctica/progresión, viaje manual 6–14 s, DDA Mamdani intrapartida (cinco resueltos, enfriamiento tres, paso máximo un segundo solo a futuros enemigos), ruta N1→N4, evidencia de cada clave en mezcla y dominio separado de otras variantes. Ventana por índices de eventos para contar fallos globales sin inventar atribución de objetivo. Historial/filtros/detalle/pérdidas, inspección de ambas inferencias y ocho simulaciones aisladas. Configuraciones experimentales; no acreditan calibración ni eficacia pedagógica.

Elecciones técnicas provisionales del agente: resultado/eventos/configuración/decisiones/snapshots en una sola sustitución de `defender-results.json`; el perfil pasa a ocho archivos, con exportación/ZIP/reinicio y pendientes de Defender integrados. Guardado inicial del resumen **provisional (`sealed=false`)**, sin progreso: cerrar esa superficie mediante navegación confirma automáticamente y cierra el checkpoint de entrada. Así una pulsación tardía que corrige la tercera llegada puede continuar la partida sin regalar un completo/desbloqueo; abandonar luego guarda interrupción. Muerte de proceso con resultado provisional conserva «Sin cierre confirmado», sin autoacreditarlo. Fallos visibles/reintento idempotente, pendientes exportables en memoria y bloqueo de nuevas progresiones hasta recuperarlos. Política de revisiones y límites en `docs/Demostracion-defender-D3.md`.

Adaptación visual de Defender al sistema vigente: papel/verde, Fraunces/Nunito Sans, árbol sin rostro/polilla vegetal simplificados en Canvas, «Tocar», vida textual y reducción con objetivos quietos. Se reutilizan tokens/tema/selectores y reglas difusas compartidas con ritmo, sin dependencias ni bitmaps nuevos. Inventario actualizado. Las tres hojas finales y acabado ilustrado son una desviación provisional explícita; **sin capturas ni validación de interfaz/dimensiones/TalkBack en esta retoma**.

**Verificación nueva:** **196 JVM completas aprobadas** (18 agregadas), lint **0 errores/17 advertencias**, ensamblados de app y AndroidTest aprobados en **5 min 20 s**. Dos Android nuevas escritas y compiladas, sin ejecutar; etiquetas previas de vidas actualizadas al texto accesible. Gradle offline, un worker, heap 1536 MiB, SerialGC, dos procesadores y prioridad BelowNormal. **340 fuentes/configuraciones** coinciden con `C:/Users/Juan/Tesis-worktrees/rhythm-engraving-verify/Plataforma`; **149 assets empaquetados** iguales a R6. Candidata `Plataforma/build/apk-archives/neuma-v0.12.13-defender-d3-prueba-sin-emulador.apk`, **15.092.664 bytes**, SHA-256 `d6bd7a9a700fb3b153ad13290f3db16ae207fc4126e37908b9d04b1f7cdff341`, firma compatible comprobada; sin instalar.

Guía `docs/Demostracion-defender-D3.md`; fuentes/logs/reportes/banco completo en `Plataforma/build/verification/2026-10-30-D3/`; respaldo duradero seleccionado en `docs/evidencias/defender-d3/`. Registro técnico y plan del informe actualizados; XeLaTeX genera 30 páginas sin errores ni desbordes horizontales, con bibliografía vacía y avisos verticales por contenido provisional. No hubo push, publicación ni cambios de issues. Última entrega completa verificada en Android: 0.12.10; R6 y D3 son candidatas comprobadas en host.

**Próximo software: D4/#26**, tríadas por nombre; consultar ticket/especificación, no reimplementar D3 ni empezar D4 como parte de este cierre. Cuando el usuario permita emular, ejecutar `DefenderD3AndroidTest` y regresión D1/D2; revisar cuatro objetivos/controles, 411/360/320 dp y 320/texto 200 %, reducción, pausa, historial/exportación/JSON nativo. Conservar también R6 y lectura/marca/exportación anteriores pendientes. La prohibición de abrir el emulador sigue vigente hasta que el usuario la cambie.

## R6 implementado y verificado en host — Android pendiente

Usuario, 2026-10-30, después de completar lint de 0.12.11: «Sigamos con cosas pendientes, todavía no emules nada». Autoriza el siguiente incremento de software sin emulador. Se implementa **R6/#21**, un frente y sin subagentes. No se abrió emulador ni se ejecutaron pruebas en dispositivos; Personaje sigue pausado.

**0.12.12/código 27, candidata de prueba:** práctica con todas las categorías y tempo manual 60–120/+5; progresión con R20/R21 iniciales, mínimo tempo recomendado en mezclas, evidencia objetivo por categoría, Mamdani inspeccionable, dos altos elegibles, desbloqueos permanentes y techo 120. Repetición/huella musical, interrupciones, evidencia insuficiente y trabajo a tempo inferior no regalan progreso. Tempo fijo en ejecución. Panel con grafo por prerrequisito, entradas/pertenencias/reglas/centroide/acciones y siete escenarios sintéticos aislados. Historial distingue orígenes y expone decisiones. Valores experimentales, sin calibración ni eficacia pedagógica acreditadas.

Elecciones técnicas provisionales del agente: resultado/decisión/snapshots se confirman en una sola sustitución atómica de `rhythm-results.json`, sin otro archivo de perfil; exportación agrega `rhythmProgress`, recuperación/reinicio siguen usando los siete archivos. R1–R5 conservan práctica sin progreso retroactivo. Fallos dejan pendientes exportables en memoria y bloquean nuevos ejercicios de progresión hasta recuperarlos. Revisión tardía del último intento recalcula desde su snapshot anterior conservando avances adquiridos; si ya hay un ejercicio posterior, actualiza la evaluación musical y conserva la decisión usada, identificada por `evaluatedRevision`. Guía y límites en `docs/Rhythm-Tap-R6.md`.

**Verificación nueva:** **178 JVM completas aprobadas** (11 nuevas), lint **0 errores/17 advertencias**, ensamblados de app y AndroidTest aprobados en **6 min 35 s**. Dos Android nuevas escritas/compiladas, **sin ejecutar**. Gradle offline, un worker, heap 1536 MiB, SerialGC, dos procesadores y prioridad BelowNormal. **330 fuentes/configuraciones** coinciden con la copia aislada `C:/Users/Juan/Tesis-worktrees/rhythm-engraving-verify/Plataforma`; los 149 assets empaquetados son idénticos a 0.12.11. Sin nuevas dependencias, assets ni animaciones; controles/tema/tokens Music Garden reutilizados. Revisión visual de estados/dimensiones pendiente por la restricción del usuario.

APK sin instalar: `Plataforma/build/apk-archives/neuma-v0.12.12-r6-prueba-sin-emulador.apk`, **14.977.972 bytes**, SHA-256 `199fdb9573f7b52837ca25cc4a24d613686103ce270d47794048339ed9e54187`; firma debug compatible con R5 comprobada. Evidencia local completa, fuentes y APK AndroidTest en `Plataforma/build/verification/2026-10-30-R6/`; síntesis duradera y XML seleccionados en `docs/evidencias/ritmo-r6/`. Registro técnico parcial y plan de pruebas incorporados a los capítulos 10/11 del informe y su seguimiento, sin dar por cerrados objetivos académicos ni validación Android.

**Próximos pendientes:** cuando el usuario permita emular, ejecutar `RhythmProgressionAndroidTest`, revisar 411/360/320 dp y 320/texto 200 %, y comprobar lectura lateral final, historial/exportación/JSON nativo y marca Neuma pendientes de 0.12.11. R6 está implementado y comprobado en host, **no cerrado en Android**; no reimplementarlo. Siguiente incremento de software: **D3/#25**, consultar ticket/especificación antes de empezar. No se inició en esta sesión. Sin push, publicación ni cambios de issues. La orden de no abrir el emulador continúa vigente.

## Retoma sin emulador — verificación de host completada

Usuario, 2026-10-30: autoriza continuar los pendientes y pide **no abrir el emulador** mientras usa la PC; al terminar, avisar antes de decidir juntos si se abre. En esta retoma no se inició ningún emulador, navegador ni prueba instrumentada. No interpretar la autorización general de continuar como permiso para iniciar el emulador.

Se completó el pendiente de lint de **0.12.11/código 26**: **0 errores/17 advertencias**, incluida la comprobación del tema API 27 separado para minSdk 26. Gradle offline terminó en **3 min 12 s** con un worker, heap de 1536 MiB, SerialGC, dos procesadores visibles y prioridad BelowNormal; daemon de una sola ejecución. Fuentes/configuración de raíz y copia aislada coinciden por SHA-256 en **324 archivos**. JVM y ensamblados de app/pruebas quedaron `UP-TO-DATE`: se conservan **167 JVM sin fallos**, sin afirmar una nueva ejecución de esos tests. El APK coincide byte a byte con el de prueba archivado (SHA-256 `0b08882802de147ad19edd6152f880af26dd2b31213fc949ea57ddfe45b0911d`). Logo corregido, colores de marca contra tokens, hash de VexFlow y presencia de licencias comprobados. No cambió código de producto ni se generaron nuevos assets.

Evidencia local: `Plataforma/build/verification/2026-10-30-host/` (`summary.json`, `gradle.log`, informes lint, resultados JVM conservados, `asset-checks.json`, `source-manifest.json`). Los resultados anteriores de Chrome/Android no se repitieron. **La entrega sigue siendo de prueba**: falta comprobar el último ajuste vertical con `RhythmEngravingAndroidTest` a 411 dp y 320 dp/texto 200 %, repetir `RhythmHistoryNavigationAndroidTest` con exportación nativa e inspección del JSON, y revisar marca/launcher/apertura/menú en Android. Avisar al usuario antes de iniciar esa etapa; su pedido de no abrir el emulador sigue vigente. Después del cierre de este incremento, próximo software R6/#21; Personaje permanece pausado. Esta entrada sustituye los pendientes de lint de los antecedentes siguientes, sin acreditar teléfono físico ni TalkBack por gestos/locución.

## Escritura del informe — base preparada

Usuario, 2026-10-29: confirma **tipo 1, Proyecto y/o Diseño de Ingeniería**, expresa preferencia por LaTeX y autoriza preparar los cuatro archivos de `Informe/` para consulta. Se conserva cada PDF y se generan extracciones Markdown por página física, con SHA-256 y script reproducible (123 páginas en total). Se prepara una estructura LaTeX por capítulos, bibliografía APA y seguimiento de escritura/trazabilidad; elección técnica provisional del agente, no aprobación de título, alcance o protocolo. Consultar `Informe/LEEME.md`, `Informe/requisitos-tipo-1.md` e `Informe/seguimiento.md` al trabajar en redacción. La base contiene pendientes, no una tesis redactada. Próximo paso de escritura: contrastar el protocolo y las decisiones vigentes antes de redactar introducción, objetivos y alcance. Este trabajo documental no modifica el estado ni las verificaciones pendientes del prototipo.

Actualización del usuario, 2026-10-29: acepta la base LaTeX, elige `Informe/TesisPrueba.pdf` como referencia **solo estética** y solicita una hoja de **Agradecimientos**. Tipografía Latin Modern de 12 puntos, márgenes de una pulgada, portada centrada y encabezados adaptados; observaciones y límites en `Informe/estilo.md`. Se conserva la estructura tipo 1; no se trasladan textos ni categorías de la referencia. Agradecimientos tiene hoja propia después de la portada y entrada en el índice; texto personal pendiente del autor.

Portada completada por pedido del usuario, 2026-10-29: título **Plataforma de entrenamiento auditivo con retroalimentación vocal y dificultad adaptativa: Neuma**, autor **Juan Cruz Saladino**, director **Leonel Guccione**, sin codirector; defensa **provisional en marzo de 2027**. Datos centralizados en `Informe/datos.tex`. Se retiran la línea “Aplicación: Neuma”, el bloque de codirector y la leyenda de borrador de la portada. El pedido se limita a completar esa página; el cuerpo del informe sigue pendiente y no se da por aprobado el protocolo.

Verificación documental: extracciones vigentes comprobadas contra los originales y conteos independientes de páginas; compilación inicial XeLaTeX/Biber aprobada con bibliografía aún sin citas. Tras el ajuste estético, XeLaTeX genera 27 páginas sin errores; se revisaron portada, Agradecimientos, índice, encabezados e introducción. Se comprobaron los 13 capítulos y ausencia de hojas vacías. Avisos esperados por bibliografía vacía y contenido provisional poco lleno. MiKTeX ya estaba instalado; se agregó `csquotes`. Ajustes de compatibilidad Babel/APA y arranque local de Biber documentados en `Informe/LEEME.md`. La presentación responde a la preferencia del usuario; no atribuirla como exigencia institucional.

## Neuma — nombre confirmado y marca temporal

Usuario, 2026-10-29: elige **Neuma** como nombre de la aplicación y pide aplicar logo/icono temporales de `docs/logo/`; corrige el paquete antes de la integración («Ahora sí, estaban mal antes los logos»). Se usan exclusivamente los originales corregidos descritos en `docs/logo/LEEME.md`. La adopción del nombre no aprueba el rediseño Quiet Studio ni elige una variante del HTML; Music Garden sigue como dirección artística actual, Personaje pausado.

Aplicación local: etiqueta Android desde `app_name`, logo horizontal PNG en el menú, icono oscuro/adaptable/monocromático y apertura nativa con símbolo verde sobre marfil. HTML con logo SVG/favIcon Clivis embebidos, sin red. Origen, editables y adaptaciones en `assets/ASSET_MANIFEST.md`; colores propios bajo `brand` en `design-tokens.json`; regeneración con `python Plataforma/tools/prepare_neuma_brand.py`. Paquete/firma/datos sin migración y versión 0.12.11 en desarrollo conservada. AGENTS, glosario, guía visual y README actualizados. HTML: cuatro anchos 320/390/768/1440 sin desborde ni recorte de marca, controles separados, sin errores de consola; evidencia `Plataforma/build/neuma-brand/`. Compilación Android/lint y revisión de emulador en curso; no presentar todavía una entrega final ni dar por verificado el renderizador por este cambio.

## Rhythm Tap lateral — sesión cerrada, verificación final pendiente

Retoma tras apagado (usuario, 2026-10-29). Se recuperó la instrucción posterior a la investigación: «ponete con esos comportamientos, si se puede usar VexFlow buenísimo, sino adapta todo lo que puedas para que funcione lo más similar posible». Autoriza implementar scroll lateral continuo, fondo integrado y feedback sobre las notas. Sustituye la restricción de solo investigación para este incremento; no autoriza otros modos de juego ni un cambio de identidad general. Un frente, sin subagentes. Personaje sigue pausado y R6 continúa pendiente.

**Cierre solicitado por el usuario (2026-10-29):** «Tengo que irme, finalizá». Código preservado en raíz; 0.12.11/código 26 compilada, con VexFlow/Bravura/Academico locales, SVG transparente, scroll lateral y marcador fijo, cabezas verdes por ataque y ligadura, revisión manual, repetición limpia, historial y alternativa nativa ante fallo. Taps/audio/evaluación/persistencia siguen en Kotlin. Movimiento reducido usa ventanas quietas por pulso. Sin dependencias Gradle nuevas, subagentes, push ni cambios de issues; R6 y Personaje siguen pendientes.

**APK de prueba, sin validación final:** `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.11-ritmo-prueba.apk`, 15.490.123 bytes, SHA-256 `0b08882802de147ad19edd6152f880af26dd2b31213fc949ea57ddfe45b0911d`. Último ensamblado de app/pruebas y 167 JVM aprobados (`rhythm-final-layout-build.log`, 1 min 21 s). 186 comprobaciones Chrome aprobadas sobre 61 partituras a 320/390/768 px. Tres Android nuevas aprobaron a 411 dp (84,119 s) y 320 dp/texto 200 % (49,262 s), **antes del último ajuste vertical**. Regresión anterior: 10/11; exportación nativa falló al consultar Compose mientras DocumentsUI retornaba. Se corrigió la espera, pendiente de reejecución.

Últimos cambios compilados y pendientes de comprobar en Android: al iniciar se lleva verticalmente la partitura al área visible para no recortar ligaduras con texto 200 %; espera tolerante al retorno de DocumentsUI. Lint detectó API 27 en el tema de marca con minSdk 26; se separaron `NeumaCoreTheme`/`NeumaBaseTheme` y `values-v27/styles.xml`. **Falta repetir lint; no afirmar cero errores con el último código.** El verde por nota es excepción solicitada al ocre común. No se acredita teléfono físico, FPS, precisión acústica ni gestos/locución TalkBack.

Construcción aislada: `C:/Users/Juan/Tesis-worktrees/rhythm-engraving-verify/Plataforma`, fuentes de app iguales a raíz al compilar. AVD privado `Rhythm_Verify`, `ANDROID_AVD_HOME=C:/Users/Juan/Tesis-worktrees/rhythm-engraving-verify/android-avd`, puerto 5574/API 34; clonado del perfil Pixel_6 sin modificar su original. Animaciones globales 1, densidades 420/540, escala 1/2, SwiftShader, memoria 2048 y pantalla despierta. Evitar varias compilaciones/emuladores pesados simultáneos: hubo bloqueos del sistema/instalador, sin atribuirlos a la app. El perfil compartido tenía animaciones 0 y activaba correctamente la reducción de movimiento. Arnés corregido para carga/swipe asíncronos, foco tras carga y frames explícitos.

Evidencia, resumen, capturas, logs y fuentes: `Plataforma/build/verification/2026-10-29-Rhythm-lateral/`; guía `docs/Rhythm-Tap-lectura-lateral.md`. **Próximo paso:** repetir lint en la copia aislada; instalar los APK recién compilados y repetir `RhythmEngravingAndroidTest` a 411 y 320/texto 200 % y `RhythmHistoryNavigationAndroidTest`. Inspeccionar lectura y JSON nativo antes de preparar entrega final. Última entrega completa R5/0.12.10 conservada.

## Antecedente: investigación del renderizador de Glisso

El pedido inicial del 2026-10-29 fue solo de investigación. Se comprobó VexFlow/SVG, Bravura, seguimiento con `scrollLeft` desde tiempo musical, contenedores transparentes y coloreado de cabezas por resultado. Informe y capturas: `docs/Investigacion-renderizador-Glisso.md`; evidencia en `Plataforma/build/verification/2026-10-29-glisso-renderer/`. La instrucción posterior y la retoma autorizaron la implementación descrita arriba. R5 sigue completo y R6/#21 pendiente.

## Decisiones y origen

- **R5/#20 terminado en software (2026-10-29):** usuario solicita «Ok, sigamos» tras R4. Entrega **0.12.10/código 25**: historial JSON con partitura/resultados exactos, UUID/revisiones para taps tardíos, filtros, detalle, repetición práctica y reintento idempotente; exportación/ZIP/reinicio incluyen séptimo archivo y pendientes. **163 JVM**, **57/57 Android completas** en API 34 (670,681 s), lint **0 errores/14 advertencias** y ensamblados aprobados. Dos casos de interfaz R5 también a 360/320 dp y 320/texto 200 %; **40 capturas inspeccionadas**. Recorrido real sobre perfil de emulador: pausa, recreación, historial, selector del sistema, JSON guardado en Downloads e igual al registro original, reinicio y vuelta al menú. Iconos oscuros sobre papel de Ritmo/Afinador/Datos y claros en bosque comprobados. Primera tanda de Gradle sufrió acceso inválido de JBR 21.0.6/SerialGC; repetición/entrega con un worker aprobadas, causa raíz sin atribuir y sin cambio permanente. Arnés de pausa corregido con frames explícitos de R1: la espera automática agotaba la partida antes de pausar; no era pérdida de guardado. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.10.apk`, firma compatible y SHA-256 igual al instalado; guía `docs/Rhythm-Tap-R5.md`; evidencia/resumen/exportación nativa/fuentes `Plataforma/build/verification/2026-10-29-R5/`; capturas `docs/imagenes/ritmo-r5/`. Sin assets/dependencias nuevas, DDA de ritmo, hardware, gestos/locución TalkBack, push ni cambios externos de issues. Pendientes en memoria pueden perderse al matar el proceso antes de guardarlos/exportarlos. Un ticket por vez, sin subagentes; Personaje pausado. Próximo **R6/#21**, después D3–D7.

- **R4/#19 terminado en software (2026-10-29):** continuación secuencial autorizada tras cerrar R3. Entrega **0.12.9/código 24**, R15/R16/R17 seleccionables: tresillo de tres ataques exactos, síncopa cruzando el pulso y dos negras ligadas con un solo ataque. Generación con suma racional, validación antes de Play y notación con número/corchete/curva. Seis JVM nuevas red→green; **151 JVM completas**, lint **0 errores/14 advertencias**, **13/13 Android seleccionadas** a 411 dp en API 34 (114,143 s); dos casos R4 también a 360/320 y flujo a 320/texto 200 % aprobados. **17 capturas inspeccionadas** en `docs/imagenes/ritmo-r4/`; arnés compartido con R3, sin esperas añadidas al producto. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.9.apk`, firma compatible y SHA-256 igual al instalado; guía `docs/Rhythm-Tap-R4.md`; reportes/resumen/fuentes de ritmo en `Plataforma/build/verification/2026-10-29-R4/`. Tema/tokens existentes, sin assets/dependencias nuevas ni DDA/historial de ritmo. Reloj/audio controlados en flujo nuevo; regresión de audio/tacto R1/R2 aparte. Sin suite Android completa nueva, precisión acústica, teléfono, gestos/locución TalkBack, push ni cambios externos de issues. Gradle con SerialGC temporal. Un ticket por vez, sin subagentes; Personaje pausado. Próximo **R5/#20**, después R6/#21 y D3–D7.

- **R3/#18 terminado en software (2026-10-29):** el usuario pide «Retomemos lo pendiente», conservando la autorización de continuar. Entrega **0.12.8/código 23**, ocho células R03/R04/R05/R06/R07/R08/R13/R14, ventanas densas y deriva sin realinear comprobadas. Tema Music Garden, barras dobles/beamlets, puntillos/silencios, selección plegable y scroll inmediato con reducción de movimiento. **145 JVM**, lint 0 errores/14 advertencias y **11/11 Android seleccionadas** de ritmo/menú a 411 dp (96,305 s); dos casos R3 a 411/360/320 y flujo a 320/texto 200 % aprobados. **32 capturas definitivas inspeccionadas** en `docs/imagenes/ritmo-r3/`: tres vsync solos no bastaron; el arnés añade un segundo de margen para el compositor software, sin cambiar producto. Diálogo Bluetooth del emulador cerrado y Bluetooth desactivado antes de capturas válidas. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.8.apk`, firma compatible y SHA-256 igual al instalado; guía `docs/Rhythm-Tap-R3.md`; evidencia/resumen `Plataforma/build/verification/2026-10-29-R3/`. Gradle con SerialGC temporal. Sin assets/dependencias nuevas, DDA/historial de ritmo, suite Android completa nueva, prueba física, push ni cambios externos de issues. Un ticket por vez, sin subagentes; Personaje pausado. Próximo: R4/#19, después R5–R6 y D3–D7; S22 Ultra/TalkBack físico pendientes.

- **A6/#14 terminado en software (2026-10-29):** usuario solicita continuar hasta completar pendientes o que lo detenga. Un ticket por vez, sin subagentes; Personaje P0–P7 sigue pausado. Entrega **0.12.7/código 22**: seguimiento opcional de Guiado/Oído, copias de configuración, intento/corrección/revisión separados, reintento idempotente, filtros/progreso, exportación/ZIP/reinicio. Lectura concurrente corregida con regresión red→green para no perder nuevos pendientes/filtros. **140 JVM**, **50/50 Android completas** en API 34 (527,35 s), dos Android A6 también a 360 dp y 320 dp/texto 200 %, lint 0 errores/14 advertencias. JBR/G1 sufrió una violación de acceso y otra ejecución de lint falló en PSI; repetir lint con SerialGC temporal aprobó, sin atribuir causa raíz ni cambiar Gradle permanentemente. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.7.apk`, firma anterior compatible y SHA-256 igual al instalado. Guía `docs/Demostracion-afinador-A6.md`; capturas `docs/imagenes/afinador-a6/`; reportes y resumen `Plataforma/build/verification/2026-10-29-A6/`. Fallos de guardado conservan pendientes en memoria exportables; cierre forzado puede perderlos si no se guardaron. Sin PCM, precisión física acreditada, gestos/locución TalkBack A6, push ni cierre de issues. Próximo: R3/#18, semicorcheas/puntillos de Rhythm Tap; luego R4–R6 y D3–D7. S22 Ultra pendiente cuando esté disponible.

- **A5/#13 terminado en software (2026-10-28):** usuario autorizó continuar mediante «ok segui con lo que haya pendiente»; elegido entrenamiento de oído, un único ticket sin subagentes, en `trabajo/afinador-a5`. Entrega **0.12.6/código 21**: ajuste oculto también en accesibilidad, clasificación opcional, Comprobar con captura nueva, error inicial sellado, corrección y revisión final separadas; preferencias compatibles y resultados solo en memoria. Parámetros provisionales `ear-check-v1`: 400 ms/amplitud 5 cents/hueco 250 ms, aparte del segundo afinado. **130 JVM aprobadas**, lint 0 errores/14 advertencias; **12/12 Android seleccionadas** en API 34 tras reiniciar emulador con SwiftShader y usar captura nativa del menú, por PixelCopy/bloqueo de entorno previos. Tras adelantar el resumen: JVM/lint/ensamblados y recorrido completo A5 aprobados a 411, 360 y 320 dp/texto 200 %; acceso desde menú repetido a 320/200 %. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.6.apk`, firma compatible verificada y hash igual al instalado; guía/evidencia en `docs/Demostracion-afinador-A5.md` y `Plataforma/build/verification/2026-10-28-A5/`. Pantalla papel/verde/tokens, sin nuevos assets ni dependencias; capturas finales guardadas. Sin suite Android completa/API 36, prueba física ni locución/gestos TalkBack de A5. Cambios locales sin push/cierre de issues ni implementación de Personaje P0–P7. Próximo software: A6/#14, seguimiento/historial/exportación; validación S22 Ultra cuando esté disponible.

- **Retoma tras apagado, menú v0.12.5 verificado (2026-10-28):** usuario solicita retomar lo pendiente. Suite completa final Pixel_6/API 34: **46/46 Android**, 122 JVM (reutilizadas sin cambios de producto), lint 0 errores/14 advertencias; `BUILD SUCCESSFUL` en 10 min 40 s. Evidencia `Plataforma/build/verification/20261028-174536-062/`; APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.5.apk`, firma compatible verificada. Se corrigieron dos recorridos antiguos del historial de Instrumentos y selección ambigua de detalles de Simón; la primera tanda de esta retoma tuvo 43/46. Los 25 fallos históricos no se reprodujeron con fuentes recompilados y emulador estabilizado, sin atribuirles causa confirmada. Ocho assets coinciden con manifiesto. TalkBack activo: foco y activación por teclado verificados en Juegos/Jugar/Más/Reducir movimiento/Índice/Instrumentos; capturas guardadas. Gestos táctiles, locución, texto 200 % con TalkBack y S22 Ultra siguen pendientes. Detalle, hashes y reproducción en `docs/Verificacion-menu-android-v0.12.5.md`. Esta retoma no cambia producto ni inicia Personaje P0–P7; sin push ni cierre de issues. Próximo: instalar con `adb install -r` y validar visual/audio/accesibilidad en teléfono cuando esté disponible.

- **Menú Android Music Garden v0.12.5, verificación local (2026-10-28):** se verificó la integración Compose previamente iniciada en la rama `trabajo/afinador-a4`. Menú con Personaje estático provisional, Juegos de cuatro portadas/índice, Afinador Libre/Guiado, Logros y Más; se mantienen las rutas existentes. Se retiró el bucle ambiental de hojas por la regla vigente del menú. Navegación de pruebas adaptada al libro; acceso a Libre/Guiado con etiquetas estables. En Pixel_6/API 34, 3/3 pruebas del menú aprobaron a 411 dp y 1/1 a 320 dp con texto 200 %; se corrigió recorte de señaladores y título detectado en captura. Capturas `docs/imagenes/menu-android/`; JVM completa, ensamblado y lint aprobaron (0 errores/14 advertencias). Regresión seleccionada: Simón 7/7 y, de la tanda inicial de 10, los tres casos de integración/afinador también aprobaron; los tres fallos iniciales de Simón eran expectativas del menú anterior y aprobaron tras actualizar el recorrido de Resultados. Fuente/exports de imágenes, fuentes OFL y tokens documentados en manifiesto; prueba en S22 Ultra, TalkBack y regresión Android completa siguen pendientes. El personaje no tiene editor, capas, armario ni recompensas P0–P7; su plan continúa pendiente. Se conserva código local sin push ni cierre de issues. Próximo: ejecutar la suite Android completa y revisar TalkBack antes de entregar el APK; validar visualmente en teléfono cuando esté disponible.

- **Personaje, plan confirmado e implementación pendiente (usuario, 2026-10-28):** «La confirmo, todavía no implementemos nada, dejalo como pendiente». Síntesis, Q1–Q24 y orden P0–P7 de `docs/Plan-personaje-Music-Garden.md` confirmados; entrevista cerrada, sin más confirmaciones de diseño pendientes. No iniciar P0, código, prototipos ni assets del personaje hasta que el usuario solicite retomar. Las entradas anteriores sobre confirmación de síntesis pendiente quedan superadas. Se conserva todo el plan y sus dependencias; esta actualización solo registra estado, sin implementación ni publicación externa.

- **Personaje, Q1–Q24 resueltas y plan consolidado (usuario, 2026-10-28, cierre Q20–Q24):** cambio gratuito de familia conservando hitos/modelos y equivalentes; cinco familias con tres modelos cada una según tabla Q22 aceptada. Timbre coherente por tipo (criolla/eléctrica), compartible entre acabados; instrumento usado afinado. Piano junto al personaje/sentado al sonar. Se elige familia incluso al omitir apariencia; usuarios existentes sin pérdida de historial. `docs/Plan-personaje-Music-Garden.md` consolida ocho incrementos P0–P7 y criterios de aceptación; son división técnica propuesta, no código iniciado. Actualizados glosario, guía v0.17, menú y nota de producción del manifiesto; ADR 0003 registra hitos independientes de familia. Dependencias: muestra/capas y quince modelos instrumentales más seis premios no instrumentales, audio criolla/nylon y UUID/persistencia de partidas ritmo/Defender. Las preguntas pendientes de entradas anteriores quedan resueltas por las rondas siguientes. Próximo: confirmación de síntesis compartida requerida por `grilling`; después iniciar solo el incremento que corresponda al encargo. No hay implementación, assets nuevos, mediciones de hardware, push ni issues publicados en esta planificación.

- **Personaje, ronda 4 confirmada (usuario, 2026-10-28, Q16–Q19):** cinco familias iniciales Guitarra/Piano/Trompeta/Flauta/Violín, modelo inicial genérico/desgastado y desbloqueos de otros modelos de la familia elegida. Sustituye flauta fija inicial y premios fijos violín/trompeta; se aceptan los otros seis objetos. Frase breve común aceptada y umbrales Q18 aceptados para probar (sin validación pedagógica). Entrega automática, aviso al terminar partida, Nuevo y sin autoequipar. Plan/glosario/guía v0.16/menú actualizados; sin código/assets. Q20–Q24 pendientes: cambiar familia y equivalencias, sonido por modelo, catálogo de quince modelos propuesto, escena del piano y elección musical al omitir apariencia. Guitarra criolla requiere audio apropiado: el banco actual es eléctrico. Igualdad de recompensa ahora depende de hito+familia; no confundir ocho hitos por jugador con ocho assets totales. Próximo: respuestas de ronda 5 y síntesis compartida, no implementar todavía.

- **Personaje, ronda 3 confirmada (usuario, 2026-10-28, Q11–Q15):** todos los rasgos/prendas compatibles con ambos tipos de cuerpo, cambio posterior sin pérdida de objetos. Accesorios libres con control por ubicación, par de aros=un objeto y aviso antes de sustituir. Acción Hacerlo sonar reproduce pequeña melodía con instrumento equipado, ampliando su rol estético. Nombre/ilustración/Personalizar y armario con objetos bloqueados/hito/progreso confirmados. Ocho recompensas iniciales, práctica y desempeño por cada uno de los cuatro juegos, además de opciones gratuitas. Plan/glosario/guía v0.15/menú actualizados, sin implementar ni producir assets. Ronda 4/Q16–Q19 pendiente en el plan: melodía/instrumentos, catálogo, hitos experimentales y entrega. Seis incrementos provisionales documentados; no aprobados. Audio de flauta/violín/trompeta reutilizable, ritmo/Defender necesitan UUID y persistencia de partida. Protocolo consultado por extracción OLE: permite encuadre como gamificación, sin demostrar beneficio pedagógico. Próximo: respuestas de ronda 4, luego síntesis compartida. Las rondas pendientes de entradas anteriores quedan superadas por las respuestas posteriores.

- **Personaje, ronda 2 confirmada (usuario, 2026-10-28, Q6–Q10):** rasgos: piel, peinado/color, nombre, vestimenta inicial, dos tipos de cuerpo sin etiquetas de género y vello facial. Equipamiento: cabeza, torso, piernas, instrumento, accesorio 1 y accesorio 2 (joyas, relojes, aros, etc.); sustituye propuesta de calzado/espalda. Práctica fácil/repetida válida con mínimo de actividad por juego, sin abandonos; manual cuenta para práctica y desempeño requiere condiciones explícitas. Historial previo otorga cosméticos verificables sin duplicados. Plan/glosario/guía v0.14/menú actualizados, sin implementar ni producir assets. Ronda 3/Q11–Q15 pendiente en `docs/Plan-personaje-Music-Garden.md`: compatibilidad por cuerpo, colisiones de accesorios, función del instrumento, armario/objetivos y colección inicial. La entrada de ronda 1 conserva antecedentes; su ronda 2 pendiente queda superada. Próximo: respuestas de ronda 3 y después hitos concretos/incrementos.

- **Menú C/Escenario confirmado y refinado (usuario, 2026-10-28):** base definitiva 2D/natural/libro/ligero low-poly; jerarquía categoría→título→ilustración→descripción→Jugar, aumento +15–20 %, cartón irregular/activa +8–12 px, fondo/sombras suaves, flechas ≥44×44, Jugar verde y transición/presión. Implementado solo Simón en `simple.html?variant=C`, predeterminado de la ruta de estudio. Doce raster originales conservan SHA-256; sin nuevos assets o dependencias. `escenario.*` y transición `book-motion.*` hacia destinos existentes sin rediseñarlos. Aumento 18 % a 360, 16,2 % a 411, 17,9 % a 600; a 320 se limita a 8 % por bordes. Activa +10 px; etiquetas 13 px a 360; flechas 48×48. Sin bucles en esta portada; la excepción de Personaje se conserva y su entrevista sigue independiente. Quince vistas generales y cinco específicas verificadas; sin desborde/recorte, navegación real a Instrumentos y reducción manual/sistema conservada en destino; repetición sin cola y controles habilitados. Inspector 390/768/1440 con excepciones documentadas. Guía, diseño, manifiesto y referencias actualizados sin cambiar definiciones de Personaje. Conservación `prototipo/menu-escenario-refinado-2026-10-28`. Abrir `http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=C`. C confirmada como base; refinamiento técnico disponible para revisión. No se extiende a otras pantallas/Android, no se hace push ni publicación externa.

- **Personaje, ronda 1 confirmada (usuario, 2026-10-28, Q1–Q5):** humano explorador 2D, silueta compartida y papel recortado con planos suaves; apariencia final pendiente de muestra. Cada hito otorga un accesorio fijo idéntico para todos los jugadores; tienda diferida. Recompensas por práctica y desempeño, con accesorios asociados a cada juego para impedir completar la colección solo con Simón. Primera creación omitible, personaje predeterminado y edición posterior gratuita. Respiración/balanceo continuos mientras esté visible y gestos al entrar, tocarlo o cambiar ropa; excepción explícita a menú sin bucles, con reducción de movimiento. Actualizados glosario, guía v0.13 y definición del menú. Entrevista en `docs/Plan-personaje-Music-Garden.md`, ronda 2/Q6–Q10 pendiente: rasgos, categorías, práctica válida, modo manual e historial previo. Sin plan cerrado, implementación, nuevos assets ni acciones externas. Relevamiento: no hay editor/economía; Simón e Instrumentos persisten resultados, ritmo/Defender aún en memoria. Próximo: respuestas de ronda 2; no asumir catálogo o recompensas implementados.

- **Simplificación 2D y tres muestras de una sola pantalla (usuario, 2026-10-28):** descarta acabado realista/recargado v2; pide formas limpias, papel/grosor discreto, señaladores físicos de cartón detrás de las páginas, activa más alta, fondo simple, controles suaves y sin animación decorativa constante. Tres variantes solo de Simón en `simple.html?variant=A|B|C`, galería `simple-gallery.html`: A/Cuento centrada/enmarcada; B/Cuaderno editorial/acción lateral; C/Escenario imagen primero. Ninguna elegida. No llegaron nuevas imágenes adjuntas; se usó pedido escrito y referencia guardada `menu.png`, sin afirmar haber visto una segunda imagen nueva. Seis maestros `imagegen`/prompts/metadata en `assets/source/menu/simple-v3/`, WebP en `assets/exports/prototype-menu-simple-v3/` (396.270 bytes). Otros destinos mantienen acceso al prototipo anterior mediante `section`/`game`, sin extender el arte nuevo a otras secciones ni cambiar inicio de producto en Personaje. Quince vistas (tres variantes × cinco tamaños/estados, incluyendo 320×640 y texto 200 %) sin problemas; siete comprobaciones de navegación/reducción y cero animaciones en reposo. Inspector adicional 390/768/1440 con excepciones documentadas. Guía v0.12 y manifiesto actualizados; v2 pasa a acabado descartado. Conservación `prototipo/menu-libro-simple-v3-2026-10-28`. Ejecutar servidor habitual y abrir `http://127.0.0.1:8765/prototype-menu-book/simple-gallery.html`. Próximo: elección del usuario, sin extender todavía. Sin Android, nuevas bibliotecas, push ni acciones externas.

- **A/Atlas elegida y versión ilustrada solicitada (usuario, 2026-10-28):** pide salto artístico con imágenes reales: jardín mágico, papel/volumen, títulos/ornamentos, señaladores/flechas/Jugar ilustrados, cuatro portadas y animación ambiental. Preparada `http://127.0.0.1:8765/prototype-menu-book/?variant=A&art=2`; galería `art-gallery.html`. Ocho imágenes generadas con herramienta integrada `imagegen`, PNG maestros/prompts/procedencia en `assets/source/menu/atlas-v2/`; WebP en `assets/exports/prototype-menu-v2/` (2.554.198 bytes frente a 22.850.054 de maestros, reducción 88,8 %). Textos reales y áreas táctiles independientes; Fraunces/Nunito Sans, sin nuevas fuentes/bibliotecas. Libro/atlas raster con composición editable, sin fingir maestros internos por capas. 21 vistas base y 12 adicionales (320×640, 360×800 y texto 200 %) sin desborde/recorte/solapamiento; retorno/índice/límites/reinicio comprobados; 9 animaciones ambientales y cero con reducción manual/sistema; avances rápidos sin cola. Inspector 390/768/1440 con excepciones de identidad documentadas; sin hallazgos restantes. Guía v0.11 y manifiesto actualizados. Rama de conservación `prototipo/menu-libro-atlas-ilustrado-2026-10-28`. Próximo paso: revisión visual del usuario; A está elegida, el acabado generado sigue sin aprobación final. Sin Android, audio, progreso inventado, push ni acciones externas. Se conserva el boceto anterior.

- **Síntesis del menú confirmada y prototipos solicitados (usuario, 2026-10-28):** confirma L1–L3 y solicita `prototype` para comparar maquetas. Tres variantes interactivas descartables en `design/specimens/prototype-menu-book/`: A/Atlas arriba, B/Herbario lateral, C/Libro de campo abajo/acción adelantada. Inicio Personaje solo con ilustración; navegación y destinos simulados, sin datos del perfil. SVG propios provisionales inventariados EXP-007/008; sin assets finales ni variante elegida. Servidor: `python design/specimens/prototype-menu-book/serve.py`, URL `http://127.0.0.1:8765/prototype-menu-book/?variant=A`. Verificación de 36 vistas 360/411/600 px y cinco interacciones aprobada; inspector adicional 390/768/1440 con excepciones documentadas para identidad/libro. Pendiente elección/combinación, texto ampliado y prueba nativa. Rama local de conservación `prototipo/menu-libro-visual-2026-10-28`; sin cambios Android, nuevas dependencias, push ni publicaciones en issues. Las entradas anteriores de síntesis pendiente quedan superadas.

- **Menú L3 respondido (usuario, 2026-10-28):** Q8 solo personaje genérico en Personaje, acceso a juegos por señalador Juegos existente, sin botón extra; Q9 índice aprobado; Q10 conserva página al volver desde otra app/bloqueo y abre Personaje tras cierre/nuevo arranque; Q11 acepta movimiento breve/silencioso, sin bloquear ni acumular animaciones y alternativa inmediata reducida. Diseño, guía v0.9 y referencia actualizados. Frontera general resuelta; pendiente confirmación de síntesis compartida antes de maqueta según grilling. Disposición/proporciones a revisar en maqueta; creación y progresión estética del personaje para después. Las recomendaciones L3 anteriores quedan resueltas por estas respuestas. Sin nuevos assets, cambios Android ni acciones externas.

- **Menú L2 respondido y Personaje inicial (usuario, 2026-10-28):** Q4 acepta por ahora Medallas/Progreso; Q5 conserva página durante el uso y al volver de partida, pero no tras cerrar la app; Q6 confirma principio/fin e indicador, sin confirmar explícitamente índice; Q7 acepta orden de cuatro juegos y Más. Agrega cuarto señalador Personaje, predeterminado al abrir, con personaje genérico pendiente de producción. Progresión estética, creación inicial y personalización quedan para después. Sustituye inicio en Juegos y propuesta de persistencia entre aperturas. Actualizados diseño, glosario, guía v0.8, referencia e inventario. L3/Q8–Q11 pendientes al inicio del documento del menú: contenido Personaje, índice, segundo plano/cierre y movimiento. Las entradas de pausa y L2 pendiente del 2026-10-26 son antecedentes superados. Sin maqueta nueva, assets generados, cambios Android ni acciones externas.

- **Cierre de sesión solicitado (usuario, 2026-10-26):** pidió «Anotate esas preguntas para la próxima sesión, ahora me tengo que ir». Guardadas las preguntas exactas L2/Q4–Q7, con sus recomendaciones, al inicio de `docs/Diseno-menu-libro-Music-Garden.md`: organización Medallas/Progreso, memoria de página, principio/fin e índice, y catálogo/accesos secundarios. Siguen **sin responder**; no asumir aceptación. L1/Q1–Q3 confirmado. Retomar por esas cuatro preguntas; no repetir la primera ronda ni avanzar con la maqueta o implementación hasta las respuestas. No se generaron nuevos assets ni se modificó Android en este cierre.

- **Estructura del libro confirmada (usuario, 2026-10-26, L1/Q1–Q3):** una página protagonista vertical como la captura con tapa/hojas detrás; portada + descripción breve + Jugar; Logros con medallas y progreso/desbloqueos diferenciados. La preparación se conserva dentro de cada juego. Actualizados diseño del libro, ART_DIRECTION v0.7, REF-007 y vocabulario (página de juego, Logros y medalla). No equivale a aprobar una maqueta o implementar medallas. Próxima ronda L2/Q4–Q7: organización de Logros, memoria de página, límites/índice y accesos existentes. Las recomendaciones están marcadas pendientes. Sin nueva maqueta, cambios Android, dependencias, builds ni acciones externas.

- **Revisión de menú a libro (usuario, 2026-10-26):** aporta `design/references/music-garden/menu.png` y propone bosque, libro central y señaladores Juegos/Afinador/Logros; Juegos por defecto, un minijuego por página, navegación izquierda/derecha con hojas; Logros paginado; Afinador con libre, guiado y ejercicio de oído. Entrevista `grill-with-docs` en `docs/Diseno-menu-libro-Music-Garden.md`, ronda L1 pendiente (geometría, contenido de página y significado de Logros). La composición de lista Q8 se reabre, conservando paleta/materiales/tipo/estados; no marcar el libro como diseño final aprobado. Captura inspeccionada y registrada REF-007. Exploración delegada acotada de solo lectura: Libre/Guiado existen, oído A5 y seguimiento A6 pendientes, perfiles actuales de guitarras; no hay colección de medallas implementada; progreso/desbloqueos e historial son conceptos distintos. No se generó maqueta nueva ni se modificó código/dependencias. Próximo paso: respuestas L1; luego navegación detallada, maqueta móvil y encargo modular de assets. Sin push ni cambios externos.

- **Definición visual inicial documentada (usuario, 2026-10-26, Q11–Q13):** acepta Defender, Simón y movimiento «como un primer acercamiento». Registrar expresamente aceptación provisional, sin convertirla en diseño final ni autorización de implementación. `docs/ART_DIRECTION.md` v0.5 consolida reglas comunes, colores/roles, tipografía, componentes/estados, variantes, assets, movimiento y procedimiento para agentes; referencias REF-004/005/006 marcadas provisionales. Q1–Q10 mantienen su aprobación previa. Propuesta, manifiesto, README de muestras y nota de tokens coherentes con esos alcances; renderer existente preferido, Bravura/SMuFL solo ante necesidad concreta. No quedan preguntas abstractas necesarias para redactar la base inicial. Pendiente: assets finales y revisión de los acercamientos funcionando en Android, incluyendo notación, tacto, TalkBack, texto ampliado, trayectorias, audio y rendimiento. Próximo incremento técnico propuesto para cuando el usuario solicite implementación: tema compartido y solo menú de referencia en Compose; luego un juego por vez. Esta actualización es documental: sin cambios en `Plataforma/`, dependencias, nuevas pruebas de ejecución, push o issues.

- **Menú y estados aprobados (usuario, 2026-10-26, Q8–Q10 «ok»):** composición del menú v1, Fraunces/Nunito Sans renderizadas y familia de estados comunes. Registradas en `ART_DIRECTION.md` v0.4, propuesta, manifiesto y referencias REF-002/003. Colores semánticos de los estados promovidos al JSON, aún sin consumo desde la app. Se prepararon estudios `defender-v1.html`, `simon-v1.html` y `motion-v1.html`, generadores y capturas bajo `design/specimens/`; SVG propios de árbol/polilla simplificados, todavía pendientes de aprobación específica/exportación. Doce casos Chrome: seis Defender 360×800/411×891, tres Simón horizontal 640/800/891 y tres de movimiento. Fuentes cargadas, sin desbordes/recorte de texto y con contenido dentro del viewport; objetivos sin intersección en las posiciones estáticas, sin acreditar trayectorias. Notación trasladada de NoteStaff desde datos, piano Do4–Si4 con geometría existente. Movimiento: mensaje inmediato y repetición habilitada; 10 animaciones frente a 0 con reducción manual/sistema. Datos sintéticos de diseño, no telemetría; sin Android, audio ni prueba física. No se modificó `Plataforma/` ni se agregaron dependencias. Próximo paso: Q11 Defender, Q12 Simón y Q13 movimiento; después consolidar guía base, sin implementar hasta autorización del usuario.

- **Referencia ilustrativa aprobada (usuario, 2026-10-26, Q5–Q7):** acepta acabado de v2, árbol de copa ancha/tronco curvo sin rostro y polilla botánica, con simplificación por escala. Registrado en `docs/ART_DIRECTION.md` v0.3, propuesta, manifiesto y `design/references/music-garden/README.md`; la lámina no se convierte en asset final de runtime. Se preparó `design/specimens/menu-v1.html`, estudio pasivo generado desde tokens, con Fraunces/Nunito Sans reales (TTF/OFL/procedencia/hashes solo bajo `design/`) y motivos SVG propios provisionales. Capturas Chrome a 360/411/600 px, texto 200% a 360 px y hoja de estados a 411 px; cinco casos sin desborde horizontal ni recorte de texto, ambas fuentes cargadas, datos en `menu-v1-verification.json`. Se observaron las capturas; no se verificaron Android, tacto, TalkBack, barras del sistema ni animación. Sin cambios en `Plataforma/`, tokens canónicos o dependencias. Próximo paso: Q8 composición/densidad, Q9 tipografías renderizadas y Q10 estados; después muestras de Defender y Simón y movimiento antes de implementar.

- **Bases visuales confirmadas (usuario, 2026-10-26, respuestas Q1–Q4):** papel recortado con tinta; menú de atlas con tarjetas; árbol sin rostro, polillas de alas vegetales y acción «Tocar» en Defender; marfil/verdes/ocre y Fraunces + Nunito Sans como base para muestras. Registradas en `docs/ART_DIRECTION.md` v0.2 y en el borrador de identidad; inventario distingue concepto aceptado de recurso aprobado. Se generaron y observaron dos láminas mediante `imagegen`, sin UI ni notación: `design/specimens/music-garden-material-v1.png` y `v2.png` (ambas 1536×1024), con prompts exactos y README. V2 recomendada por el agente tras reducir detalle/relieve; ninguna aprobada por el usuario ni utilizable todavía como asset final. Se reconciliaron las notas visuales de definición/especificación de Defender sin modificar issues ni reglas; glosario agrega árbol guardián y Tocar (Defender). No se modifican código, dependencias o `design-tokens.json`; sin nueva ejecución Android. Próximo paso: Q5–Q7 sobre acabado de v2, silueta del árbol y polilla; después composición/tipografía y menú estático. La confirmación de las bases no aprueba dimensiones, animaciones, ilustraciones ni implementación.

- **Definición visual en revisión (usuario, 2026-10-26):** adopta Music Garden como nombre provisional de la dirección artística transversal y solicita una propuesta concreta con entrevista `grill-with-docs`, sin implementar ni modificar interfaz. Borrador en `docs/Propuesta-identidad-visual-Music-Garden.md`: acabados, tokens semánticos adicionales, componentes/estados, ejemplos de menú/Defender/Simón, assets y movimiento. Las elecciones concretas del agente siguen propuestas; no se modificaron `ART_DIRECTION.md`, `design-tokens.json`, el manifiesto ni código/dependencias. Se incorporó al glosario únicamente el término expresado por el usuario. Contrastes calculados en colores sólidos, sin nueva ejecución Android ni prueba física. Se conserva Simón horizontal como decisión previa; los documentos espaciales de Defender requieren reconciliación después de acordar su traducción botánica. Próximo paso: respuestas de ronda 1 y revisión de referencias/muestras antes de consolidar reglas o implementar. La skill pide exploración delegada de hechos; se usó una comprobación acotada de solo lectura, sin desarrollo paralelo de tickets.

- **A4 individual autorizado (usuario, 2026-10-26):** «Dale, metele con A4», sobre referencias de guitarra/piano/tono puro. Rama `trabajo/afinador-a4` desde D2/A3/R2 verificados; un único ticket #12, sin subagentes, push ni cambios externos en issues. Versión prevista 0.12.4/código 19. Guitarra Emilyguitar limpia/CC0 y piano Iowa; banco separado Do2–Mi4, transposiciones y afinación documentadas. Margen provisional de salida de 600 ms, pendiente de medición física. No incluye entrenamiento A5 ni seguimiento A6.

- **D2 individual autorizado (usuario, 2026-10-26):** aceptó «dale, vamos con eso» sobre retomar D2/#24 como único ticket, sin subagentes. Rama `trabajo/defender-d2-continuacion`, desde A3/R2 verificados y la comparación documental con Glisso; cuatro checkpoints D2 recuperados. Versión prevista 0.12.3/código 18. Completar resumen y verificar interfaz, JVM, Android e integración; sin push ni cambios externos en issues. El avance de Glisso continúa como propuesta, fuera de este ticket.

- **R2 individual autorizado (usuario, 2026-10-26):** pidió «Vamos con otro ticket» después de entregar A3. Se retoma R2/#17 sobre A3 verificado, en `trabajo/ritmo-r2-continuacion`, recuperando sus ocho checkpoints anteriores. Versión prevista 0.12.2/código 17; solo un ticket activo y sin subagentes. D2 permanece guardado. Sin push ni cambios externos en issues.

- **Continuación secuencial (usuario, 2026-10-26):** pidió retomar con un solo ticket por vez para cuidar tokens. Esta instrucción reemplaza la modalidad de tres frentes simultáneos. Se eligió A3/#11, guiado y revisión final del afinador; rama local `trabajo/afinador-a3-continuacion`, desde `main` con únicamente los seis checkpoints A3 recuperados. R2/#17 y D2/#24 permanecen guardados, sin continuarlos ni incorporarlos a esta entrega. Versión prevista 0.12.1 para distinguirla de la integración triple 0.13. Sin push ni cambios externos en issues.

- **Segunda tanda y reserva de cuota (usuario, 2026-10-24):** aceptó «Dale» a A3/#11, R2/#17 y D2/#24 mientras prueba v0.12; pidió cortar al quedar menos del 5% de su ventana de cinco horas. Se encontró `rate_limits.primary` (ventana 300 minutos) en el registro local de esta sesión, primera lectura 55% usado/45% restante. El agente consultará `.agents/tools/check-codex-budget.py` entre incrementos, comenzará el cierre al 10% restante y detendrá desarrollo al 5%; la lectura puede tener demora, no es un corte transaccional garantizado. Worktrees nuevos `afinador-a3`, `ritmo-r2`, `defender-d2`, base común `d0f1624`; integración `trabajo/integracion-v0.13`. Un dueño por módulo, host/documentación global del principal y Gradle por turnos. Al cerrar conservar commits WIP y pendientes precisos antes de consumir la reserva; `main`/APK v0.12 siguen como última entrega validada hasta nueva integración comprobada. Sin push ni publicaciones.

- **Reanudación de los tres frentes (usuario, 2026-10-24):** «Sigamos con los 3 frentes como dejamos ayer […] tené en cuenta que no se pisen». Se retomaron A2/#10, R1/#16 y D1/#23 con agentes en sus worktrees originales y turnos exclusivos de Gradle; el agente principal mantiene los archivos compartidos e integración en `trabajo/integracion-v0.12-continuacion`. La pausa del 2026-10-23 quedó levantada. Continúa el alcance local, sin push ni cierre de tickets.

- **Trabajo paralelo autorizado (usuario, 2026-10-23):** aceptó «hagamos eso» sobre tres frentes con subagentes y worktrees: A2/#10, R1/#16 y D1/#23. Base común local `c0f9307` (v0.11 y especificaciones antes sin commit), ramas `trabajo/afinador-a2`, `trabajo/ritmo-r1`, `trabajo/defender-d1` en `C:/Users/Juan/Tesis-worktrees/`. Un agente por frente; integración compartida y verificación pesada por turnos a cargo del agente principal. Autorización de implementación local, sin push ni publicación externa. No equivale a implementar los tickets dependientes posteriores en esta misma tanda.

- **Continuación autorizada (usuario, 2026-10-23):** «ponete a hacer lo que definimos la otra vez», remitiendo a los tickets de GitHub. Se consultaron issues abiertas y #9 con comentarios. Elección del agente: comenzar por A1/#9, primer incremento del afinador, sin reabrir entrevista ni modificar los frentes de ritmo/Defender. Sin publicación externa.

- **Continuación autorizada (usuario, 2026-10-21):** «Dale, ponete con lo que puedas» sobre el plan de seis iteraciones post-v0.5. Avanzar incrementos verificables sin pruebas inmediatas del teléfono; sin publicación externa.

- **Implementación autorizada por el usuario (2026-10-21):** pidió avanzar todo lo posible sin mucha intervención y luego «Implement the plan». Alcance: completar #5, #6 y #7, conservar compatibilidad e historiales, verificar en emulador y dejar identificada la prueba física pendiente. Las elecciones técnicas reversibles del agente y parámetros experimentales están en `docs/Demostracion-prototipo-v0.5.md`.

- **Continuación autorizada por el usuario (2026-10-09):** «Ok segui desarrollando lo siguiente que siga, yo mientras pruebo la app» y «continua» después de la interrupción: implementar #4, DDA intrapartida de escuchas e inspección. Se conservan las melodías de v0.3.1. Elección provisional del agente: automático inicia en A; práctica manual A/B/C conserva tres escuchas y registra propuestas no aplicadas. Los parámetros difusos de la especificación se concretan como valores de prueba sin calibración; desbloqueos y voz siguen en sus tickets.

- **Confirmado por el usuario (2026-10-02):** comenzar a prototipar la tesis y preparar contexto persistente para las próximas sesiones.
- **Organización solicitada por el usuario (2026-10-02):** usar `Tesis/` como raíz del repositorio y reunir los documentos académicos en `docs/`. El código se ubicará en `Plataforma/`.
- **Configuración aprobada por el usuario (2026-10-02):** repositorio privado `SaladinoJC/Tesis`, GitHub Issues como gestor de tareas, las cinco etiquetas de triage predeterminadas y documentación de dominio single-context (`CONTEXT.md` + `docs/adr/`, creados cuando hagan falta). Aprobó aplicar el borrador, crear las etiquetas y subir el commit inicial.
- **Propuesta documental:** `docs/IdeasPrototipo.md` sugiere el orden Simón musical armónico → reconocimiento tímbrico → defensa de acordes en partitura → eco rítmico → intervalos por reproducción vocal. La selección completa y el orden siguen pendientes de confirmación.
- **Atribución de la ficha:** `docs/Ideas Desarrolladas/17-Simon-musical-armonico.md`, §17.1, registra la inclusión de Simón en el MVP como decisión explícita del usuario. Esto no confirma que sea el primer juego a implementar ni determina la plataforma.
- **Pendiente de comprobación técnica:** valores numéricos del DDA, referencias académicas de Simón, procedencia/permiso de muestras para los próximos juegos y mediciones en teléfono. El banco de piano actual ya tiene procedencia y condiciones documentadas. La compilación Android del primer incremento ya se comprobó. El alcance funcional y los criterios de demostración ya fueron confirmados.
- **Entrevista grill-with-docs, ronda 1 (usuario, 2026-10-02):** objetivo inicial de demostración al director; primeros probadores: el usuario y el director. No hay fecha de reunión definida. El usuario quiere prototipar un par de minijuegos y un primer intento del DDA antes de mostrarlo. La propuesta de empezar por Simón fue aceptada como punto de partida; el segundo juego y el comportamiento del DDA siguen abiertos.
- **Ronda 2 (usuario, 2026-10-02):** Android para todo el proyecto; segundo juego ¿Qué instrumento es?; incluir prueba técnica de voz separada. El usuario cambió el DDA a lógica difusa, con adaptación intrapartida y desbloqueo por desempeño. Señaló que la adaptación intrapartida encaja menos en Simón que en reconocimiento tímbrico; su aplicación exacta en Simón sigue abierta.
- **Ronda 3 (usuario, 2026-10-02):** reconocimiento tímbrico tendrá partidas de 10 preguntas como valor inicial; el DDA variará las escuchas entre rondas, observando aciertos recientes y uso de reescuchas, sin penalizar tiempo de respuesta. Se mantienen cuatro opciones y el nivel de instrumentos de la partida. Los desbloqueos serán permanentes. Simón conserva escala y tónica fijas, crecimiento de una nota y fin al primer error; el DDA difuso solo desbloqueará escalas entre partidas en esta primera versión.
- **Ronda 4 (usuario, 2026-10-02):** Kotlin + Jetpack Compose con reglas de juego/DDA independientes de interfaz; inferencia Mamdani explicable; funcionamiento offline, un perfil local con resultados y progreso persistidos y opción de reinicio. Prueba de voz separada con YIN, tono/nota aproximada y desviación respecto de una nota objetivo; dispositivo inicial Samsung S22 Ultra del usuario. ML queda para una comparación posterior.
- **Valores iniciales aprobados, pendientes de calibración:** instrumentos comienza con 3 escuchas totales por pregunta, incluida la primera, y puede variar entre 1 y 3 para la siguiente ronda. Evaluar después de cada respuesta desde la tercera, sobre las últimas 3 rondas; cambios máximos de una escucha con dos rondas entre cambios. No retirar escuchas de una pregunta iniciada.
- **Ronda 5 (usuario, 2026-10-02):** modo automático inicia en el nivel más alto desbloqueado; dos partidas automáticas completas consecutivas calificadas como dominio alto en ese nivel desbloquean el siguiente, efectivo en la próxima partida. Simón puntúa por longitud máxima completada correctamente, conservando secuencia intentada y posición de error. Aciertos con reescuchas siguen siendo correctos, con menor evidencia de autonomía; acertar tiene mayor peso. Panel de inspección con selección manual de nivel, entradas, reglas, salida y ajuste; partidas manuales no desbloquean y casos simulados se distinguen de datos jugados.
- **Criterios de demostración aprobados:** partidas completas y progreso persistido al cerrar la app; aumento/reducción de ayuda y desbloqueo mediante casos reproducibles; prueba de voz en S22 Ultra con tono y desviación respecto del objetivo; comprobar estimador con señales de frecuencia conocida y silencio; exportar resultados/decisiones sin guardar grabaciones de voz inicialmente.
- **Cierre de entrevista (usuario, 2026-10-02):** confirmó la síntesis completa y pasar a `to-spec`/`to-tickets`. Se prepararon `docs/Especificacion-prototipo-v1.md` y `docs/Plan-tareas-prototipo-v1.md` como borradores concretos. Las skills exigen comprobar puntos de prueba y división de tickets antes de publicar; esa revisión es el próximo paso, no una reapertura de la entrevista.
- **Publicación aprobada y completada (2026-10-02):** el usuario aceptó los dos puntos de prueba y seis incrementos. Especificación #1 y tickets #2–#7 publicados en GitHub con `ready-for-agent`; dependencias nativas verificadas. Los parámetros numéricos conservan su condición de propuestas iniciales no calibradas.
- **Discrepancia documental explícita:** se agregaron actualizaciones confirmadas al inicio de las fichas de Simón y reconocimiento tímbrico, con precedencia sobre la gobernanza anterior por promedio móvil/umbrales y las escuchas fijas. Las reglas difusas concretas siguen abiertas. Esta dirección todavía no se incorporó al protocolo Word.
- **Primer incremento autorizado (usuario, «Dale», 2026-10-02):** implementar #2, Simón musical Android. Selección manual de las tres escalas; DDA, instrumentos, progresión y voz siguen en sus tickets.
- **Elecciones técnicas provisionales del agente:** minSdk 26/target 36, Gradle 8.14.3/AGP 8.11.0/Kotlin 2.2.10; historial JSON versionado en almacenamiento privado. El primer incremento usó senoidales de 300 ms a 48 kHz; desde la versión 0.2 se sustituyen por piano `mf` en OGG mono/44,1 kHz/600 ms. Recorte, fade de 70 ms y ganancia común ×5 son provisionales, no parámetros pedagógicos calibrados ni decisiones del director.
- **Correcciones solicitadas por el usuario tras probar v0.1 (2026-10-02):** menú principal antes de entrar al juego; estilo aproximado de `docs/ui simon.png`; piano táctil en lugar de grilla de botones; descargar únicamente samples `mf` necesarios del piano de University of Iowa y convertirlos a OGG. La imagen orienta el aspecto y las pantallas, sin aprobar vidas, otras escalas o nuevos juegos.

- **Ajustes solicitados por el usuario al probar v0.2 (2026-10-02):** aceptar notas rápidas sin esperar el audio anterior; Simón en horizontal, teclado más ancho y pantallas sin scroll; conservar la partida al girar el teléfono. Quitar «Tu espacio musical» y «A tu ritmo…» del menú y sustituir «Pequeños juegos…» por la versión instalada.

- **Segundo incremento autorizado (usuario, «Retomemos […] vamos con el desarrollo de qué instrumento es?» y «Sí, seguí», 2026-10-02):** implementar #3 usando el flujo `ask-matt` → `implement`/TDD/revisión. Se conserva el alcance ya aprobado: niveles A/B/C manuales, diez preguntas y tres escuchas. DDA intrapartida y desbloqueos siguen en #4 y #5.
- **Elecciones técnicas provisionales del agente para instrumentos:** catálogo candidato de la especificación (A: piano/violín/flauta/marimba; B: flauta/clarinete/trompeta/trombón; C: flauta/clarinete/oboe/fagot); Do4 real, dinámica `mf`, OGG mono/44,1 kHz/1,5 s, RMS objetivo 0,08 antes del fade de 80 ms. Primera escucha automática; pantalla vertical; feedback antes de continuar y botones fijos en resumen. No son parámetros pedagógicos calibrados ni decisiones del director.

## Avance comprobado

- **A4/#12 terminado en software (2026-10-26):** rama `trabajo/afinador-a4`, entrega **0.12.4/código 19** sobre A3/R2/D2. Referencias de guitarra eléctrica limpia Emilyguitar/CC0, piano Iowa y tono puro; timbre persistido con default compatible A2/A3. Banco separado de 58 OGG/682.763 bytes para Do2–Mi4, hashes/transformaciones documentados y máximo 0,831 cents por autocorrelación guiada. YIN Android en tres ventanas por muestra: sin cambio de octava y máximo 4,386 cents; no equivale a precisión física ni calibración Pro. Captura invalidada/liberada antes de salida, margen provisional 600 ms y ventana nueva; conserva pasada/cuerda guiadas y reinicia estabilidad. Cancelar/salir/fallar no reabre captura. **122 JVM/27 clases aprobadas**, lint 0 errores/14 advertencias; reconstrucción limpia de 81 tareas en 1 min 42 s. **Siete Android distintas aprobadas en Pixel_6/API 34 a 360 dp**: tanda 6/7 y caso antiguo corregido con desplazamiento explícito aprobado en 13,505 s. Se corrigió una carrera de guardado inmediato del timbre mediante trabajo LAZY, con regresión roja→verde; la reconstrucción limpia eliminó bytecode de test contra constructor viejo. Evidencia `Plataforma/build/verification/2026-10-26-A4/`; guías `docs/Demostracion-afinador-A4.md` y `docs/Audio-referencias-afinador-A4.md`. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.4.apk`, 13.189.030 bytes, mismo certificado que 0.12.3; instalar encima. Sin suite Android completa/API 36 ni teléfono físico. Audibilidad, margen residual y guitarra real pendientes. A5/A6 no implementados; sin push ni cambios externos en issues, `main` sigue en v0.12.

- **D2/#24 terminado individualmente (2026-10-26):** cuatro checkpoints recuperados sobre A3/R2 en `trabajo/defender-d2-continuacion`. Entrega **0.12.3/código 18**, práctica N1–N4, sol/fa/mezcla con dos líneas adicionales, alteraciones opcionales y hasta cuatro enemigos con reservas. Se completó el resumen de etapa/rango/clave/alteración/pérdidas y se corrigió la grafía C–B de las alteraciones. **116 JVM/26 clases aprobadas**, lint 0 errores/14 advertencias; app/pruebas compiladas en 5 min 01 s. **15 Android distintas aprobadas en Pixel_6/API 34**, incluidas siete de Defender, A3, R1/R2 e integración. Primer recorrido: 12/15, con tres esperas de foco fallidas por ANR de `com.android.systemui` documentado con captura; cerrando el aviso, las tres aprobaron sin modificar tests. Tres de interfaz repetidas a 360 dp aprobadas en 16,639 s; capturas a 411/360 dp en `docs/imagenes/defender-d2/`. Primer build sufrió crash nativo G1 de JBR; los dos builds completos siguientes aprobaron con `-XX:+UseSerialGC` (ajuste local del comando). APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.3.apk`, mismo certificado que 0.12.2; guía en `docs/Demostracion-defender-D2.md`, evidencia en `Plataforma/build/verification/2026-10-26-D2/`. Sin suite Android completa ni API 36; S22 Ultra, audibilidad/latencia y calibración pendientes. Defender sigue sin DDA/progreso/historial (D3). No se implementaron las propuestas Glisso ni otro ticket; sin push/cierre de issues, `main` conserva v0.12. Emulador propio detenido al cerrar.

- **Comparación con Glisso solicitada (2026-10-26):** se inspeccionó `https://glisso.net/rhythm` en navegador real, escritorio y móvil emulado, y se contrastó con Rhythm Tap 0.12.2. Hallazgos y propuestas en `docs/Comparacion-Glisso-Rhythm-Tap.md`, con seis capturas del sitio en `docs/imagenes/comparacion-glisso-rhythm/`. Se recomienda un incremento individual de interfaz (selector gráfico y preparación/previa/resultados), seguido por notación y medición temporal separadas. Son propuestas, sin aprobación de cambios de alcance ni de prioridad; no se modificó código de la app, no se creó ticket ni se inició D2. La verificación física sigue pendiente.

- **R2/#17 terminado individualmente (2026-10-26):** ocho checkpoints recuperados sobre A3 en `trabajo/ritmo-r2-continuacion`. Versión **0.12.2/código 17**, figuras largas, puntillo, silencios y métricas 2/4–4/4; factibilidad/cuota/búsqueda acotada y evaluación sin penalización duplicada. **110 JVM/25 clases aprobadas**, lint sin errores/14 advertencias y app/pruebas compiladas en 6 min 04 s; **cinco Android seleccionadas aprobadas en Pixel_6/API 34, 102,205 s**. Ajuste posterior solo del contenedor de capturas: compilación 52 s y tres pruebas afectadas repetidas/aprobadas en 50,367 s. Ocho láminas/22 ejemplos y tres vistas reales inspeccionadas; archivos en `docs/imagenes/ritmo-r2/`. Guía y límites en `docs/Rhythm-Tap-R2.md`, evidencia local en `Plataforma/build/verification/2026-10-26-R2/`. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.2.apk`. No se ejecutó toda la suite Android ni API 36; S22 Ultra y sincronía acústica pendientes. D2 sigue guardado, sin incorporarlo ni comenzar otro ticket. Sin push ni cambios externos en issues; `main` conserva v0.12.

- **A3/#11 terminado individualmente (2026-10-26):** checkpoints del afinador incorporados a `trabajo/afinador-a3-continuacion`, junto al callback de limpieza de resumen al reiniciar perfil y una prueba Android de ciclo de vida guiado. **104 JVM/24 clases aprobadas**, lint sin errores/14 advertencias, APK y pruebas compilados; **cinco Android seleccionadas aprobadas en Pixel_6/API 34, 77,249 s**. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.1.apk` (código 16). Dos pasadas con estimaciones controladas; captura real solo en emulador. Intentos iniciales afectados por ANR de System UI y preparación de permiso; ejecución final desde datos limpios, permiso concedido antes y 2 GiB de RAM. Guía, comandos, hashes y límites en `docs/Demostracion-afinador-A3.md`; evidencia en `Plataforma/build/verification/2026-10-26-A3/`. No se ejecutó toda la suite Android ni API 36. S22 Ultra/guitarra real pendiente. R2/D2 siguen guardados sin nuevos cambios; no hubo push ni cierre de issues. `main` conserva v0.12 y la integración triple 0.13 continúa sin acreditarse.

- **Segunda tanda guardada por reserva de cuota (2026-10-24):** A3/#11, R2/#17 y D2/#24 con checkpoints independientes incorporados a `trabajo/integracion-v0.13`, todavía sin verificación conjunta ni APK nuevo. Se cerró al llegar a aproximadamente 8% restante conforme al margen anunciado al usuario. A3: 104 JVM de su rama aprobadas; D2: 19 JVM del módulo aprobadas; R2: 12 JVM y compilación de sus pruebas Android aprobadas. Ningún recorrido Android nuevo fue ejecutado y la integración no se compiló; no sumar estos conteos aislados como si fueran una suite conjunta. Tres worktrees limpios, sin compilaciones activas. Consultar `.agents/tanda-paralela-v0.13.md` para hashes, riesgos y próximo paso. `main` conserva código/APK v0.12 y recibe solo continuidad documental; los verdes parciales no acreditan v0.13. No reanudar automáticamente hasta que el usuario pida continuar.

- **v0.12 integrada y verificada en software (2026-10-24):** completados los incrementos locales A2/#10, R1/#16 y D1/#23 de esta tanda, con tres agentes/worktrees separados y compilaciones por turnos. Integración en `trabajo/integracion-v0.12-continuacion` para avance local de `main`. **97 JVM/22 clases, 32 Android/API 34 y 10 Android de los frentes/captura/integración en API 36 aprobadas; lint 0 errores/14 advertencias.** Primer Android completo: 28/32; los cuatro fallos se diagnosticaron como problemas de los tests (uptime virtual de Compose, espera de inactividad durante ticker y coincidencia completa de texto). Se conservaron aserciones conductuales y se retiraron probes. Las suites finales aprobaron en 10 min 23 s (comando conjunto/API 34) y 3 min 39 s (10/API 36). A2: perfiles, seis presets, personalizadas con revisión y configuración persistida/exportable/recuperable/reiniciable; R1: cuatro compases 4/4 con negras/corcheas, conteo/taps/evaluación/repetición sin historial; D1: notas naturales Mi4–Fa5, enemigo radial, tres vidas, disparo, pausa y resumen en memoria. Guía `docs/Demostracion-prototipo-v0.12.md`, evidencia `docs/Verificacion-prototipo-v0.12.md`, capturas `docs/imagenes/prototipo-v0.12/`. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.apk`, mismo certificado que v0.11. Sin push ni cierre de issues. S22 Ultra pendiente; no se calibró precisión acústica ni eficacia pedagógica. El estado pausado siguiente es antecedente.

- **Tanda v0.12 pausada por el usuario (2026-10-23):** pidió dejar el trabajo por hoy por el límite de la ventana de cinco horas. Checkpoints WIP separados para A2/#10, R1/#16 y D1/#23, e integración compartida en `trabajo/integracion-v0.12` (`d6e3d7e`). El código de `main` se mantiene en v0.11; no hay v0.12 integrada ni APK nuevo verificado. Hay verdes parciales y regresiones/correcciones pendientes; leer **`.agents/retomar-paralelo-v0.12.md`** para ramas, hashes, contratos y orden de pruebas. No repetir desde cero ni tratar los tickets como completados. No continuar hasta que el usuario pida retomar.

- **v0.11 / afinador A1 implementado en software (2026-10-23):** entrada Afinador → Libre; guitarra estándar con cromático/seguimiento de cuerdas, fijación/liberación sin plegar octavas, Normal ±5/Pro ±2 cents y un segundo continuo. `tuner-continuity-v1` (hueco máximo 250 ms, vigilancia 100 ms) y selección por distancia ≤75 cents/ventaja ≥25 son elecciones provisionales del agente. Reutiliza `yin-v1`/AudioRecord sin cambiar voz/DDA ni guardar PCM/resultados del afinador. Pausa/salida detienen captura y requieren nuevo inicio explícito; conceder permiso tampoco inicia solo. 68 JVM aprobadas; app/APK de pruebas compilados y lint 0 errores/14 advertencias. Captura real del emulador a 44,1 kHz, reapertura tras liberación y recorrido de afinador/pausa/recreación/salida aprobados. Historial de voz conservado byte a byte en el recorrido. Los intentos iniciales tuvieron caída de JVM y ANR de System UI, documentados sin contarlos como aprobación. Guía: `docs/Demostracion-afinador-v0.11.md`; evidencia: `docs/Verificacion-afinador-v0.11.md`. APK `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.11.apk`. Prueba física S22 Ultra pendiente; #9 sigue abierto, sin push.

- **v0.10 completada en software:** cerradas las seis iteraciones autorizadas. Barrera compartida por perfil para guardado/exportación/reinicio; archivos dañados o futuros preservados, juego afectado bloqueado y lectura reintentable; recuperación ZIP sin parsear y con pendiente de voz separado. Voz conserva guardado fallido en memoria/reintenta mismo ID, sin duplicación; no sobrevive al proceso. Reinicio confirmado descarta sesiones activas para evitar resurrección de datos, conserva marcador y completa borrado interrumpido. Comando `Plataforma/tools/verify-prototype.ps1` conserva informes/APK/hash y solo admite emuladores para Android. Elecciones técnicas provisionales del agente, sin cambiar reglas DDA/YIN.
- **Verificación final v0.10:** 63 JVM y 22 Android aprobadas en cada API 34 (411 dp) y 36 (360 dp); lint 0 errores/14 advertencias. Un primer recorrido API 36 tuvo 21/22 por espera del proveedor externo en arranque frío; se amplió espera de prueba y la suite completa posterior pasó. Exportaciones reales JSON/ZIP guardadas, parseadas y contrastadas; 34 mediciones de micrófono virtual en silencio, cero válidas, sin PCM; nueve escenarios DDA separados. Avisos y pantallas inspeccionados visualmente. Evidencia/hashes/advertencias en `docs/Verificacion-iteraciones-post-v0.5.md`.
- **Entrega actual:** `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.10.apk`, 12.438.198 bytes, SHA-256 `8f578fb5297924f4b6669eb571aac48e05bea773b60a1c216ed7bd4200468c26`; guía `docs/Demostracion-prototipo-v0.10.md`. Código documentado en checkpoints locales, sin push ni cierre de issues. Próximo: prueba física del S22 Ultra cuando esté disponible; después revisar límites YIN/parámetros DDA con evidencia. La validación física, latencia acústica y eficacia pedagógica siguen pendientes; no se incorporó el cambio al protocolo Word.

- **v0.9 completada:** banco DDA con nueve escenarios, 93 fronteras, 56 pasos y 21 comprobaciones. 55 JVM y una Android del banco/recreación aprobadas. Exportación `ddaScenarios` separada; reglas de producción conservadas. Informe `docs/Banco-DDA-v0.9.md`. Próximo: resistencia de datos y entrega v0.10.

- **v0.8 completada:** referencia sonora con exclusión captura/reproducción, gráfica de diez segundos con huecos y resumen al detener. 54 JVM y cinco casos Android de voz/datos/referencia aprobados; los dos de referencia reejecutados tras corregir inicialización de AudioTrack estático. APK y evidencia en `docs/Verificacion-iteraciones-post-v0.5.md`. Próximo: escenarios DDA v0.9.

- **v0.7 completada:** laboratorio YIN de 83 casos/332 ventanas, cancelable, sin micrófono, con JSON separado y Markdown. 51 JVM y una Android del banco/exportación/recreación aprobadas. Se observaron límites con ruido sin cambiar `yin-v1`. Informe `docs/Laboratorio-YIN-v0.7-JVM.md`; APK/evidencia en `docs/Verificacion-iteraciones-post-v0.5.md`. Próximo: pantalla de voz v0.8.

- **v0.6 completada:** historial por juego/nivel/origen, páginas de 20 y detalle con datos de juegos/DDA/voz. 48 JVM y una Android de historial aprobadas (paginación, detalle y recreación); APK generado. Ver `docs/Verificacion-iteraciones-post-v0.5.md`. Próximo: laboratorio YIN v0.7.

- **v0.5.1 completada:** operaciones con Snackbar y aviso consumible, última operación retenida, selector interrumpido distinguido de cancelación y sin escritura vacía; progreso/próximo nivel visibles. 46 JVM y tres Android de voz/datos aprobadas en Pixel_6/API 34, APK generado. Referencia v0.5 conservada en commit local `08eb2c6`; sin push. Evidencia y artefactos en `docs/Verificacion-iteraciones-post-v0.5.md`. Próximo incremento: historial completo v0.6.

- **v0.5, sesión 2026-10-21:** implementados #5/#6/#7 en software. Simón e instrumentos automáticos parten del mayor nivel desbloqueado y progresan con dos partidas completas consecutivas de dominio alto. Resultado y configuración de dominio se guardan juntos; el progreso se reconstruye del historial sin avances duplicados ni desbloqueos retroactivos. Manuales/interrupciones/simulaciones no alteran rachas. Escenarios aislados de dos altas y recuperación, e inspección del dominio en resúmenes.
- **Voz v0.5:** YIN Kotlin, AudioRecord mono/44,1 kHz, objetivo/nota/frecuencia/cents, permiso y errores. Procesamiento fuera de Main y publicación en Main. Se detiene al pausar la actividad (incluidos cambios rápidos de aplicación); solo guarda mediciones derivadas. Ventana 4.096/salto 1.024, umbral 0,15/RMS 0,01, rango 55–1.000 Hz y máximo dos minutos son elecciones provisionales del agente, no parámetros calibrados.
- **Datos v0.5:** exportación JSON mediante selector Android, con partidas/decisiones/configuración/progreso/voz y simulaciones separadas. Reinicio confirmado coordinado con guardados y marcador para completar un borrado interrumpido. Mantiene el paquete/firma y lectura de historiales anteriores. El protocolo Word sigue sin actualizarse.
- **Verificación v0.5:** 43 pruebas JVM y 16 Android aprobadas en Pixel_6/API 34; suite general 5 min 14 s. Un ajuste posterior de pausa de voz se revalidó con las 43 JVM y los tres Android de voz/datos, lint/APK, en 1 min 15 s. Lint: 0 errores/14 advertencias. Dos regresiones rojas→verdes corrigen la carga de historial que podía deshacer navegación en ambos juegos. Se comprobó manualmente permiso rechazado, captura/detención al salir a Inicio y exportación guardada/parseada (25 mediciones del emulador, sin audio crudo). Sintéticas 110/220/440 Hz: error máximo 0,074 cents bajo las condiciones documentadas; no acredita voz real.
- **Entrega v0.5:** APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.5.apk` (12.193.303 bytes). Guía `docs/Demostracion-prototipo-v0.5.md`; evidencia, hash y límites en `docs/Verificacion-prototipo-v0.5.md`. Sin S22 Ultra conectado, sin medición de latencia acústica ni eficacia pedagógica. Código/documentación permanecen locales y sin un commit nuevo; no hubo push ni cierre de issues en esta sesión.

Los apartados siguientes conservan los antecedentes de versiones anteriores.


- **Incremento DDA v0.4:** motor Mamdani Kotlin independiente de Android, configuración `mamdani-help-v1` persistida y centroide con 1.001 puntos/paso 0,002. Evalúa últimas tres respuestas desde la tercera usando exactitud/autonomía; ajusta máximo una escucha en [1,3] para la próxima pregunta y espera dos rondas entre cambios. Las partidas reinician ventana/espera/ayuda. Mantiene resultados ayudados como correctos, sin tiempo de respuesta. Panel DDA con datos reales, entradas/pertenencias/reglas/centroide/propuesta/aplicación/motivo y cuatro escenarios aislados en memoria, identificados como simulados y sin audio. Automático en A; práctica manual conserva A/B/C y tres escuchas. Resultados guardan configuración, traza y origen; historiales anteriores se conservan.
- **Verificación v0.4:** 34 pruebas JVM y 13 Android aprobadas en Pixel_6/API 34, lint sin errores/3 advertencias y APK generado; ejecución final 5 min 12 s. Revisiones independientes Standards/Spec sin hallazgos pendientes; P2 del inspector que mostraba defaults durante partidas activas corregido con regresión roja→verde y verificación de configuración efectiva también en simulación. Registro `docs/Verificacion-DDA-v0.4.md`, reglas/escenarios `docs/DDA-instrumentos-v0.4.md`, revisión `docs/Revision-prototipo-v0.4.md`. APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.4.apk` (12.034.978 bytes). No se conectó ni probó S22 Ultra; no se midieron latencia acústica ni eficacia pedagógica. Los cambios de melodías y DDA se incluyen en el commit de cierre local; no se publicó ni cerró #3/#4 en GitHub.

- **Prueba y cambio solicitados por el usuario (2026-10-09):** informó que probó v0.3 en el celular y pidió melodías breves generadas aleatoriamente al sortear el instrumento. No describió condiciones de audio ni resultados detallados del recorrido de verificación. La solicitud actualiza la nota aislada de v0.3; ficha 15, selección y especificación registran la precedencia del cambio.
- **Incremento v0.3.1 comprobado:** generación de una frase por pregunta, conservada en reescuchas y registrada por respuesta; gramática común a los instrumentos y variación aunque se repita el objetivo. Elecciones provisionales del agente: seis notas en Do pentatónico, al menos tres alturas, saltos máximos de cinco semitonos, cierre en tónica, cinco notas de 400 ms y final de 800 ms con separaciones de 50 ms (3,05 s). Son parámetros de prueba sin calibración pedagógica. Regresión para un azar que repetía una frase con primera nota única: la alternativa considera varias posiciones y conserva las restricciones.
- **Banco nuevo comprobado offline:** 45 OGG mf reales (Do4/Re4/Mi4/Sol4/La4 por cada instrumento), 497.231 bytes; se conservan banco e historiales de v0.3. Sin transposición. Se añadió solo una grabación de violín de Iowa; las demás notas se extraen de fuentes ya cacheadas. Detalles y control de afinación en `docs/Melodias-instrumentos-v0.3.1.md`. Verificación final: 25 pruebas JVM y 11 Android aprobadas en Pixel_6/API 34, lint sin errores/14 advertencias y APK generado, en 4 min 14 s. Registro `docs/Verificacion-Instrumentos-v0.3.1.md`; APK `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.3.1.apk` (11.940.300 bytes). No se probó v0.3.1 en S22 Ultra; no se midieron latencia acústica ni eficacia pedagógica. Ese ajuste se incorpora al commit de cierre junto con el DDA de v0.4; no se publicó en el remoto ni se cerró #3.

- **Incremento de instrumentos v0.3:** «¿Qué instrumento es?» disponible en el menú. Preparación con niveles A/B/C manuales, diez preguntas con cuatro opciones aleatorias, tres escuchas totales incluida la primera y respuesta sin límite temporal. El contador confirma avance real de AudioTrack; impide reescuchas superpuestas y una cuarta escucha. Resumen por pregunta e historial local independiente del de Simón, incluyendo partidas interrumpidas. Reglas de sesión independientes de Android. Reproductor PCM y archivo JSON compartidos con Simón; SoundPool del piano y formato del historial previo conservados. Prueba de compatibilidad con documento v0.2.1 incluida.
- **Banco de instrumentos:** nueve OGG `mf` con Do4 nominal compartido, 142.201 bytes (~139 KiB), sin transposición. Permiso comprobado en la página primaria de Iowa; enlaces exactos, hashes, recortes, ganancias y medidas en `docs/Audio-instrumentos.md`, `Plataforma/tools/prepare_instruments.py` y manifiesto de assets. Marimba remuestreada de 96 a 44,1 kHz. Afinación natural y RMS eléctrico documentados; la sonoridad percibida sigue pendiente de revisión en teléfono.

- `Plataforma/` contiene la aplicación Android Kotlin/Compose y Gradle Wrapper. En v0.3 Simón conserva el comportamiento de v0.2.1: menú principal vertical con versión instalada → preparación de Simón horizontal → partida con piano ancho, tema oscuro/rosa y muestras reales. Preparación/partida/resumen sin scroll; entrada rápida sin esperar el audio previo y sesión conservada al girar/recrear por configuración. Escalas pentatónica, diatónica mayor o cromática; reproduce/resalta notas, aumenta la secuencia y termina al primer error. Conserva resultados e interrupciones y muestra historial local. `Plataforma/README.md` documenta ejecución y pruebas; `Plataforma/AGENTS.md` registra instrucciones de implementación.
- Banco de piano: 23 notas `mf`, MIDI 60–82/Do4–Si♭5, solo las necesarias para las tónicas actuales y las tres escalas. OGG: 190.589 bytes totales (~186 KiB). Fuente, condiciones, SHA-256 y conversión en `docs/Audio-piano-Simon.md`, `Plataforma/tools/prepare_piano.py` y manifiesto en assets. AIFF originales ignorados en `Plataforma/build/piano-sources/`, sin incluirlos en el APK.
- Se inicializó Git en la raíz `Tesis/` y se configuró `origin` como `https://github.com/SaladinoJC/Tesis.git`. El repositorio remoto es privado y tiene Issues habilitadas.
- La configuración de las skills está en `docs/agents/issue-tracker.md`, `docs/agents/triage-labels.md` y `docs/agents/domain.md`, referenciada desde `AGENTS.md`.
- `CONTEXT.md` contiene el glosario; dos ADR registran Android y Kotlin/Compose/Mamdani. Las fichas de los dos juegos y la selección de prototipo explicitan las decisiones actualizadas.
- La especificación y el plan publicado están en `docs/Especificacion-prototipo-v1.md` y `docs/Plan-tareas-prototipo-v1.md`; `.agents/prototipo-issues.json` conserva identificadores y dependencias para retomar sin crear duplicados. `docs/Notas-tecnicas-prototipo.md` recoge fuentes primarias y límites técnicos.
- Las cinco etiquetas aprobadas están disponibles en GitHub; `wontfix` ya existía y se conservó.
- Los materiales académicos y `Ideas Desarrolladas/` se trasladaron a `docs/`, conservando su organización interna. `README.md` describe la estructura y `.gitignore` excluye archivos temporales de Office y del sistema operativo.
- Se actualizó `AGENTS.md` y se creó este registro de continuidad.
- Se revisó el texto de `docs/PROTOCOLO_Saladino.doc` mediante extracción de su formato Word/OLE. «PRODUCTO ESPERADO» propone una plataforma móvil inicialmente Android y cinco juegos; «OBJETIVO DEL PROYECTO» plantea evaluación técnica de tono, latencia y DDA. No define un framework de interfaz ni confirma el orden de los juegos. Las atribuciones bibliográficas y capacidades técnicas del texto siguen sin verificar.

## Verificación de esta sesión

Sesión 2026-10-23 / A1: 68 JVM y ejecución final de 14/14 Android en Pixel_6/API 34 aprobadas (251,794 s), lint 0 errores/14 advertencias. Android incluye dos del afinador, dos de referencia de voz, siete de Simón y tres de instrumentos. Rechazo del permiso comprobado manualmente fuera de instrumentación: micrófono detenido, aviso visible, sin tono válido. Evidencia y fallos de entorno iniciales en `docs/Verificacion-afinador-v0.11.md` y `Plataforma/build/verification/afinador-a1/`. APK 12.193.977 bytes, SHA-256 `f4b77caa95e7bb937a575ac12cf7f36fe50ea55f017b4b3e7563537fcb4a45d0`. No se ejecutó API 36 ni teléfono físico en esta sesión. Próximo software A2/#10; no hubo push ni cierre de issues.

En la etapa inicial se verificó mediante SHA-256 que los diez documentos trasladados conservaron su contenido. Se leyó el contexto y las fichas; los archivos de bloqueo de Office quedaron excluidos de Git. El protocolo se consultó posteriormente como registra el avance; sus referencias académicas siguen sin verificar.

La configuración aprobada se escribió y se verificaron el remoto privado, Issues habilitadas y las cinco etiquetas. La revisión de espacios de los documentos originales señaló espacios finales existentes, incluidos saltos de línea Markdown; se conservó ese material sin reformatearlo.

Se verificaron siete issues publicadas, todas con `ready-for-agent`, cuerpos con saltos de línea reales y criterios de aceptación en cada ticket. Bloqueos comprobados por API: #3←#2, #4←#3, #5←#4, #6←#2, #7←#5/#6. La issue de especificación queda abierta como referencia; no es un bloqueo.

Para #2 se leyeron ticket, especificación, glosario, ADR y fichas. Ocho pruebas JVM y tres Android en emulador Pixel_6/API 34 pasaron; compilación, lint sin errores y generación del APK completadas. La prueba Android del reproductor real observó avance de frames de `AudioTrack`. Dos revisiones independientes de estándares/especificación quedaron sin hallazgos de código pendientes tras corregir una carrera de salida/guardado con regresión roja→verde. Detalles y comandos en `docs/Verificacion-Simon-v0.1.md`.

Implementación subida en `24afc0c`. La issue #2 quedó cerrada con criterios comprobados y enlace al registro; su cierre habilita #3 y #6. La demostración física completa continúa pendiente como indica el registro, sin confundirse con el cierre del incremento de software.

En la verificación automatizada no se conectó ni probó el Samsung S22 Ultra. Después de solicitar el APK para instalarlo en su celular, el usuario informó (2026-10-02): «va queriendo, falta corregir muchos detalles pero anda». Esto registra una primera prueba manual de Simón con funcionamiento general reportado por el usuario; no detalla versión de Android, condiciones de audio, escalas recorridas ni incidencias concretas. La comprobación física estructurada sigue pendiente. No se midieron latencia acústica ni eficacia pedagógica. Instrumentos se incorporó en v0.3 y DDA en v0.4; v0.5 incorpora desbloqueos, voz y exportación/reinicio. La prueba física completa sigue pendiente.

Menú/piano v0.2: ocho pruebas JVM y cinco Android aprobadas en Pixel_6/API 34; lint sin errores y APK generado. Ciclos rojos→verdes para navegación, OGG real y respuesta al apoyar el dedo. La prueba Android recorrió las 23 notas y observó al menos 26.460 frames por reproducción. Se validaron formato, longitud y hashes de cada OGG y se inspeccionaron pantallas del emulador. Registro en `docs/Verificacion-Simon-v0.2.md`. El APK nuevo actualiza el anterior con el mismo identificador y firma debug; no hace falta desinstalar. El usuario probó luego v0.2 y reportó pérdida de pulsaciones rápidas, dificultad para acertar teclas y partida interrumpida al girar el teléfono; sus correcciones dan origen a v0.2.1. No se midió latencia física.

### Verificación de Simón v0.2.1

Verificación v0.2.1 completada: 11 pruebas JVM (7 sesión + 4 ViewModel) y 8 Android (7 flujo/audio + 1 teclado), lint sin errores/14 advertencias y APK firmado debug generado. Ejecución final completa: 3 min 16 s en Pixel_6/API 34. Regresiones rojas→verdes para respuestas rápidas, conservación al recrear la actividad y fallo de carga de audio por tiempo. Dos dedos y notas repetidas a 10 ms entre eventos registrados en orden; las 23 notas avanzaron al menos 26.460 frames. SoundPool aceptó 69 ataques distintos en 481 ms, tras precarga de 23 notas en 7.602 ms en este emulador; no es latencia acústica. Capturas finales inspeccionadas y registro en `docs/Verificacion-Simon-v0.2.1.md`. La nueva versión todavía requiere prueba del usuario en el S22 Ultra.

Elecciones técnicas provisionales: SoundPool para teclas, con 16 colas y volumen 0,8; AudioTrack/PCM cacheado para presentar la secuencia. Precarga conjunta de la escala, límite de 15 s y aviso durante preparación; sin bloquear respuestas por duración del sonido. Resalte de 100 ms y espera de 600 ms tras la última respuesta antes de la pausa/presentación siguiente. Rotación gestionada en la actividad y recreaciones por configuración conservan ViewModel; salir realmente de la app conserva la política previa de interrupción.

### Verificación de instrumentos v0.3

Pruebas de sesión y ViewModel: límite real de tres escuchas, intentos antes del audio, primera reproducción, fallo/cancelación, diez respuestas y persistencia, interrupción durante sonido y salida durante el guardado final. Pruebas Android en Pixel_6/API 34: partida completa de nivel C con reescuchas agotadas y recreación, niveles manuales y salida durante presentación, banco de nueve instrumentos con avance de al menos 66.150 frames y una sola notificación por escucha; también se comprueba Simón. Los nueve OGG tienen hashes, mono/44,1 kHz/66.150 frames, RMS decodificado 0,0769–0,0806 y picos menores que 0,95 comprobados. Capturas inspeccionadas en `docs/imagenes/prototipo-v0.3/`.

Ejecución final v0.3: 21 pruebas JVM y 11 Android aprobadas, lint sin errores/14 advertencias y APK debug generado; 3 min 50 s en Pixel_6/API 34. Revisión en dos ejes: Spec sin hallazgos; Standards sin incumplimientos documentados, con dos duplicaciones corregidas mediante los componentes PCM/JSON compartidos y un ajuste para conservar la cancelación del timeout al notificar inicio de audio. Registro y APK en `docs/Verificacion-Instrumentos-v0.3.md`; artefacto `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.3.apk` (11.329.014 bytes). No se probó el S22 Ultra ni se midió latencia acústica o eficacia pedagógica.

## Próximo paso sugerido

**Prioridad vigente tras V6 vocal, 2026-10-30:** producción libre implementada
en 0.12.20 con progreso independiente, 292 JVM aprobadas y lint sin errores.
Los tres andamiajes se eligen explícitamente; queda selección automática global
de ayuda. Validación nativa/acústica pendiente; diez casos vocales actuales
compilados, sin ejecutar. Mantener no emular, sin subagentes y Personaje pausado.
D6/#28 pendiente. Las prioridades anteriores quedan como antecedentes.

**Prioridad vigente tras V5 vocal, 2026-10-30:** ancla dada implementada en
0.12.19 con progreso independiente, 281 JVM aprobadas y lint sin errores.
Siguiente modo pendiente: producción libre; falta también selección automática
global de ayuda. Mantener no emular, sin subagentes y Personaje pausado.
D6/#28 pendiente. Las prioridades anteriores quedan como antecedentes.

**Prioridad vigente tras V4 vocal, 2026-10-30:** series de imitación con Mamdani,
hitos y panel implementados en 0.12.18, 263 JVM aprobadas. Siguiente pendiente vocal:
andamiajes ancla dada/producción libre, todavía por definir como incremento. No
abrir emulador hasta nueva autorización. Los párrafos siguientes son antecedentes.

**Prioridad vigente tras V3 vocal, 2026-10-30:** persistencia, historial y exportación
implementados en 0.12.17 con 249 JVM aprobadas. Próximo frente vocal DDA Mamdani/
progresión, revisando la ficha. Android permanece pendiente: conservar la orden
de no emular. Los siguientes párrafos son antecedentes superados.

**Prioridad vigente tras V2 vocal, 2026-10-30:** ronda manual conectada en 0.12.16,
239 JVM aprobadas; siguiente incremento vocal persistencia/exportación antes de
DDA/progresión. Android sigue pendiente por la orden de no emular. D6 queda
disponible para otra prioridad explícita. Los párrafos siguientes son antecedentes.

**Prioridad vigente tras V1 vocal, 2026-10-30:** núcleo de imitación implementado,
228 JVM aprobadas. Próximo incremento: ronda vocal jugable con referencia/captura,
permiso y ciclo de vida; después persistencia y DDA. D6/#28 sigue pendiente.
Conservar la prohibición de emular y los pendientes Android de D5 y anteriores.
Los próximos pasos de los párrafos siguientes son antecedentes superados.

**Prioridad vigente tras D5, 2026-10-30:** D5/#27 implementado en 0.12.15 con **218 JVM sin fallos**, sin Android por pedido del usuario. Siguiente en la ruta Defender D6/#28; conservar también la prioridad vocal expresada en la entrevista y tomar el próximo frente del pedido del usuario. No reimplementar R6/D3/D4/D5. Revisiones de interfaz/persistencia/audio nativos pendientes, incluyendo las anteriores, cuando el usuario permita abrir el emulador. Personaje pausado y sin subagentes. Los siguientes próximos pasos son antecedentes superados.

**Prioridad vigente 2026-10-30:** R6/#21 implementado en 0.12.12, **178 JVM y lint 0 errores/17 advertencias**, sin ejecución Android por pedido del usuario. Continuar con D3/#25 si se retoma software; para verificar interfaz, esperar que el usuario permita abrir el emulador. Conservar pendientes de lectura/exportación/marca de 0.12.11 y las pruebas Android R6. Personaje pausado, sin subagentes. Los próximos pasos de R5/R6 de los párrafos siguientes son antecedentes superados.

**Prioridad vigente tras R5 (2026-10-29):** R1–R5 terminados en software; entrega actual **v0.12.10**, catálogo de 21 categorías e historial/repetición/exportación de práctica. No repetir implementación ni capturas descartadas. Continúa la autorización del usuario para pendientes, un ticket por vez y sin subagentes. Próximo **R6/#21**, progresión por categoría/tempo y DDA inspeccionable; después D3–D7, consultando ticket/especificación. Personaje P0–P7 permanece pausado. Validar S22 Ultra cuando esté disponible. Los párrafos siguientes son antecedentes superados.

**Prioridad vigente tras A4 (2026-10-26):** el usuario está probando v0.12.3 en el móvil; puede instalar **v0.12.4** encima para probar referencias y enviar correcciones. A3/R2/D2/A4 están terminados en software: no reimplementarlos ni recuperar checkpoints antiguos. Mantener un solo ticket por vez, sin subagentes. Candidatos posteriores A5/#13 (ya tiene A3+A4), R3/#18 o D3/#25, consultando ticket/dependencias cuando autorice continuar. Propuestas Glisso sin implementación aprobada. No empezar otro ticket al cerrar esta sesión; los siguientes párrafos son antecedentes superados.

**Prioridad vigente (2026-10-26):** un solo ticket por vez, sin agentes ni frentes simultáneos. A3/#11, R2/#17 y D2/#24 están terminados en software en la rama actual; no reimplementarlos ni recuperar de nuevo sus checkpoints. Probar **v0.12.3** en S22 Ultra cuando el usuario pueda, instalando encima sin desinstalar. Los siguientes incrementos disponibles son A4/#12, R3/#18 y D3/#25; elegir uno cuando el usuario autorice continuar, consultando su especificación/dependencias. La comparación Glisso sigue como propuesta sin ticket/aprobación de implementación. No iniciar otro ticket en este cierre ni tomar la antigua integración triple v0.13 como entrega validada. Los párrafos siguientes son antecedentes y su modalidad paralela queda reemplazada.

**Prioridad actual (2026-10-24):** probar la entrega v0.12 en S22 Ultra cuando el usuario pueda, instalando encima sin desinstalar; registrar afinación de guitarra, condiciones de audio/entrada y legibilidad. Esta tanda de A2/R1/D1 está terminada en software; no reiniciar sus implementaciones ni usar las ramas WIP antiguas como base nueva. A3/#11, R2/#17 y D2/#24 son los siguientes incrementos de cada frente, fuera de esta tanda autorizada; consultar sus tickets y dependencias antes de continuar. El registro siguiente de la pausa se conserva como antecedente.

**Prioridad al retomar después de la pausa del 2026-10-23:** leer `.agents/retomar-paralelo-v0.12.md`. A2/#10, R1/#16 y D1/#23 ya tienen código WIP en tres worktrees; primero verificar las correcciones y suites pendientes, después integrar junto a `d6e3d7e` y ejecutar comprobaciones completas. Los párrafos siguientes conservan antecedentes; «próximo A2» no significa que deba reimplementarse desde cero. La última entrega comprobada sigue siendo v0.11.

**Continuación actual (2026-10-23):** A1/#9 ya tiene implementación y entrega v0.11; consultar la verificación para el alcance comprobado y la prueba física pendiente. Próximo incremento de software: **A2/#10**, perfiles, presets y afinaciones guardadas, con exportación/reinicio/recuperación compatibles. No repetir A1 ni tratar #9 abierto como ausencia de implementación. Afinador A3–A6, ritmo #16–#21 y Defender #23–#29 siguen pendientes. Los párrafos de publicación del 2026-10-22 siguientes se conservan como antecedentes; su «primer ticket disponible #9» quedó superado por este avance.

Defender definido y publicado (usuario, 2026-10-22): síntesis/valores iniciales aceptados mediante «perfeto» y límites de prueba/siete incrementos aprobados mediante «perfecto». Especificación #22 y tickets #23–#29 publicados con `ready-for-agent`, criterios y dependencias nativas verificadas. Documentos: `docs/Definicion-defender.md`, `docs/Especificacion-defender-v1.md`, `docs/Plan-tareas-defender-v1.md`; identificadores en `.agents/defender-issues.json`. D1–D3 notas, D4–D5 acordes por nombre, D6–D7 piano. Vertical espacial/radial según captura, máximo cuatro, tres vidas; errores no quitan vida y conservan selección. DDA solo nuevos enemigos; viaje10 s(6–14,paso1), aparición4→2,5, ventana5/enfriamiento3 y dos partidas altas con≥10 resueltos son valores de prueba sin calibración. Próximo ticket disponible #23, independiente de afinador/ritmo. Los tres frentes solicitados quedan definidos; al implementar consultar su issue/especificación sin reabrir entrevistas ni repetir aprobaciones. No se implementó código ni se hizo push en esta fase; no se modificaron issues anteriores.

Ritmo definido y publicado (usuario, 2026-10-22): síntesis/valores experimentales confirmados mediante «ok» y puntos de prueba/división aprobados mediante «perfecto». Especificación #15 y tickets #16–#21 publicados con `ready-for-agent`, criterios y dependencias nativas verificadas. Referencias: `docs/Definicion-ritmo.md`, `docs/Catalogo-ritmico-borrador.md` (primer conjunto de 21 categorías aceptado), `docs/Especificacion-ritmo-v1.md`, `docs/Plan-tareas-ritmo-v1.md`; identificadores en `.agents/ritmo-issues.json`. Primer ticket disponible: #16, independiente del afinador. Una partida=un ejercicio de cuatro compases, categorías→Generar→vista previa→Play→conteo→ejecución→resultados; cuota conjunta ≈60 %, conexión negras/corcheas; taps reales y metrónomo opcionales; repetición práctica. DDA entre partidas por categoría y tempo mínimo al mezclar; 60 BPM/+5/techo120 y ventanas son valores experimentales sin calibración. No reabrir entrevista ni repetir aprobaciones. Ocho/dieciséis, compuestos/irregulares y avanzadas después. Sin código ni push en esta fase; no se modificaron issues anteriores. Definición de Defender también completada, ver registro anterior.

Ampliación del afinador (usuario, 2026-10-22): síntesis funcional confirmada mediante «ok», registrada en `docs/Definicion-afinador.md`. Guitarra de seis cuerdas; Libre/Guiado/Entrenamiento; entrenamiento de oído protagonista, revisión final y seguimiento opcionales; perfiles, presets y personalizadas; referencias instrumento/piano/tono puro, con instrumento predeterminado. Normal ±5 cents y Pro ±2 cents con un segundo estable son valores experimentales para probar, sin DDA. Puntos de prueba y seis incrementos aprobados mediante «Perfecto»; se publicaron especificación #8 y tickets #9–#14 con `ready-for-agent` y dependencias nativas. Documentos: `docs/Especificacion-afinador-v1.md` y `docs/Plan-tareas-afinador-v1.md`; identificadores en `.agents/afinador-issues.json`. Primer ticket disponible: #9 (A1). No reabrir entrevista ni pedir aprobación de la misma división. No se implementó código, se hizo push ni se modificaron issues anteriores. Ritmo/generador/renderizador y Defender también definidos; pruebas físicas de v0.10 pendientes.

Las seis iteraciones autorizadas post-v0.5 están implementadas hasta v0.10, con 63 JVM y 22 Android por cada API 34/36 aprobadas. No retomar el incremento 6 como pendiente: ver `docs/Verificacion-iteraciones-post-v0.5.md` y la guía `docs/Demostracion-prototipo-v0.10.md`. El usuario no puede probar ahora; esa validación física no se da por hecha.

Cuando esté disponible el S22 Ultra, instalar la entrega v0.10 encima sin desinstalar para conservar su perfil. Revisar audibilidad/volumen, historial/progresión/ayuda y voz real/permisos/pausa; guardar el JSON para registrar Android, dispositivo y condiciones. El reinicio es opcional y exige confirmación explícita en la app; no usar las pruebas instrumentadas sobre el perfil del teléfono. Después, decidir ajustes de YIN/DDA a partir de mediciones, sin atribuir calibración o eficacia a los bancos actuales. El protocolo Word sigue sin actualizarse. El código/documentación y los checkpoints permanecen locales; no hubo push ni cierre de issues en esta sesión.

## Mantenimiento

Mantener este registro como una síntesis del estado actual. Reemplazar el avance y el próximo paso al completar trabajo; conservar las decisiones vigentes con su origen. Agregar los comandos de ejecución y las limitaciones observadas cuando exista una aplicación.

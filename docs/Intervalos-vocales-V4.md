# Intervalos vocales V4 — series con Mamdani y progresión

Nota histórica: [V5](Intervalos-vocales-V5.md), 0.12.19, incorpora ancla dada con
progreso independiente. Los pendientes mencionados aquí corresponden al cierre de V4.

Usuario, 2026-10-30: «ok, metele», tras proponer DDA y progresión. Incremento
**0.12.18/código 33**, limitado a la imitación ya implementada: una partida es
una serie de cinco rondas del mismo intervalo/dirección. No implementa todavía
los andamiajes de ancla dada ni producción libre de la ficha. La restricción de
no abrir emulador sigue vigente; sin instalación ni ejecución Android.

## Uso

La aplicación abre por defecto en **Progresión**. Elegir un intervalo disponible,
registro de referencia e **Iniciar serie**. En cada ronda: escuchar las dos notas,
cantar ancla, confirmar segunda captura y revisar el resultado. **Siguiente ronda**
prepara otra referencia y aplica la tolerancia recomendada, sin cambiar la ronda
que acaba de evaluarse. Después de cinco rondas se prepara otra serie explícitamente.

**Práctica** conserva el recorrido individual de V3 con todos los intervalos,
tolerancia manual de prueba y origen `manual`; no concede progreso. El modo
automático usa origen `dda`, andamiaje `1`, identificador de serie y ordinal 1–5.
Los dos modos conservan historial, exportación y recuperación.

**Progreso y DDA** muestra accesos, requisitos, rachas e hitos por intervalo con
dirección. Permite inspeccionar decisiones confirmadas: entradas, pertenencias,
cinco reglas activadas, centroide, cambio propuesto/aplicado y snapshots. Las
funciones trapezoidales y configuración se pueden desplegar. El detalle del
historial también permite inspeccionar la decisión de esa ronda.

El panel ofrece seis simulaciones aisladas: dos series precisas, desviación,
octava, interrupciones, manual y ruta completa. Usan las sesiones públicas y el
mismo commit, con almacenamiento propio en memoria, sin micrófono ni escrituras
en el perfil. Su origen exterior es `simulated`; los registros interiores mantienen
el modo de juego que se está simulando. No son desempeño observado de un jugador.

## Dos capas de adaptación

La adaptación **intrapartida** ocurre entre rondas de una misma serie. Se modifica
solo la tolerancia de la siguiente ronda, nunca el objetivo o criterio mientras
se está cantando. La progresión persistente habilita nuevos contenidos al completar
hitos por intervalo, sin retirar accesos adquiridos. No se confunde desbloquear
contenidos con haber implementado los otros niveles de ayuda auditiva.

Cada ronda completa se evalúa con el sostenido final de la segunda nota. Se
consideran las mediciones de los últimos 500 ms: al menos cinco, cobertura de
400 ms o más y huecos de hasta 100 ms. El núcleo exige previamente el sostenido
estable; la comprobación de evidencia evita acreditar un protocolo abreviado.
El protocolo automático requiere 500 ms de sostenido, máximo hueco 100 ms y
amplitud estable 35 cents. Interrupción o evidencia insuficiente no producen
una inferencia musical elegible.

| Variable | Definición experimental |
| --- | --- |
| Error absoluto medio | Media de valores absolutos del error relativo en la ventana final |
| Dispersión | Desviación estándar poblacional del error en esa ventana |
| Precisión | `clamp(1 - errorAbsolutoMedio/100, 0, 1)` |
| Regularidad | `clamp(1 - dispersión/35, 0, 1)` |
| Inferencia | AND/implicación mínimo, agregación máximo, centroide, 1001 puntos |
| Categoría | Alto ≥0,70; medio ≥0,40; bajo para el resto |
| Acción | Alto: −5 cents; bajo: +5; medio: conservar |
| Límites | Inicial 50, mínimo 30 y máximo 80 cents |

La configuración `vocal-mamdani-v1` define precisión baja/media/alta y regularidad
baja/alta. Sus reglas son: precisión baja → bajo; precisión media con regularidad
baja → bajo y con alta → medio; precisión alta con regularidad baja → medio y con
alta → alto. Las funciones y activaciones completas se almacenan con la decisión.
Se reutiliza el motor Mamdani común; las funciones vocales son independientes de
las de ritmo. Son elecciones del agente para experimentar, sin calibración vocal.

La métrica no es CA ni una escala pedagógica validada. Entrar en la tolerancia
de práctica no equivale a inferencia alta: una producción oscilante puede estar
dentro del margen y no reunir las condiciones de una serie alta. Un error de
octava conserva ±1200 cents; no se pliega para producir un acierto.

## Hitos y orden de acceso

Una serie alta requiere sus cinco rondas elegibles y altas. Dos series altas
consecutivas **para el mismo intervalo y dirección** registran su hito. Otros
intervalos y la práctica manual no aportan evidencia a ese contexto. Una serie
no alta o interrumpida reinicia la racha; el hito adquirido se conserva. Empezar
otra serie después de una parcial rompe su racha al confirmar la nueva ronda.

Orden provisional de contenidos, inspirado en la secuencia propuesta de la ficha,
sin presentarlo como currículum aprobado ni como orden pedagógico validado:

| Etapa | Contenidos | Acceso |
| --- | --- | --- |
| 1 | Unísono; octava ascendente/descendente | Inicial, tres objetivos |
| 2 | Quinta y cuarta justas, ambas direcciones | Todos los hitos de etapa 1 |
| 3 | Terceras y sextas mayores/menores, ambas direcciones | Todos los hitos de etapa 2 |
| 4 | Segundas y séptimas mayores/menores, ambas direcciones | Todos los hitos de etapa 3 |
| 5 | Tritono, ambas direcciones | Todos los hitos de etapa 4 |

Son 25 objetivos firmados (−12…+12 semitonos), todos disponibles en práctica.
La agrupación final del tritono es una elección provisional del agente. Cada
dirección exige su propia evidencia. Cambiar el registro no cambia el contexto,
porque la evaluación de imitación sigue siendo relativa al ancla cantada.

## Persistencia y cierre

Resultado y decisión se confirman en la misma sustitución de
`vocal-interval-results.json`, usando `upsertPrepared` bajo el lock del perfil.
La decisión guarda secuencia, configuración, snapshots antes/después, métricas,
inferencia, motivo, deltas, cierre de serie y nuevos accesos. Los pendientes no
avanzan el estado visible; una confirmación incierta se reintenta por UUID y no
vuelve a ejecutar una decisión ya confirmada. La siguiente ronda espera la cola.

El commit comprueba acceso, orden de ordinales, identidad de serie/contexto,
antecedentes elegibles, tolerancia recomendada y andamiaje de imitación. Repetir
un ordinal con otro UUID, usar otra tolerancia o inventar una serie sobre un
intervalo bloqueado no concede avance. Estas comprobaciones preservan consistencia
de la app; no constituyen protección contra edición externa maliciosa del perfil.

Cancelar o salir a segundo plano cierra la serie incluso entre rondas. No se
reanuda automáticamente una serie parcial al volver o tras muerte de proceso.
Las rondas ya guardadas y su adaptación siguen disponibles; una serie de menos
de cinco rondas no acredita un hito. Se puede iniciar otra desde el principio.

Se conservan **nueve archivos**: no se agrega otro para progreso. Los registros
V3 sin `series`/`dda` se leen como manuales, sin avance retroactivo. JSON agrega
`vocalIntervalProgress`, derivado de decisiones confirmadas, además de listas
de resultados y pendientes. ZIP/reset conservan la coordinación de V3. Las
simulaciones del panel no se guardan ni se incluyen como resultados del jugador.

## Presentación y verificación

Cierre de host: **263 JVM aprobadas (14 nuevas)**, lint **0 errores/10 avisos**,
app y AndroidTest ensamblados en **3 min 25 s**, offline/un worker/1536 MiB/
SerialGC/dos procesadores/BelowNormal. **365 fuentes/configuraciones** idénticas
por SHA-256 a la copia aislada probada; **149 assets** idénticos a V3. APK de
**15.289.272 bytes**, SHA-256
`ca5f835849fd173e4df3d4ba2e22f327cded56ad1c7e04be10cef81bfb79c9c8`;
metadatos 0.12.18/33, código nuevo y firma compatible con V3 comprobados.
Informe compilado en tres pasadas: **39 páginas**, sin errores, referencias
indefinidas ni desbordes horizontales; cinco avisos verticales y bibliografía
vacía. Páginas físicas 27/28/32 inspeccionadas. No se instala la candidata.

Music Garden conserva tema/tokens, papel/verde, Fraunces/Nunito Sans y portada
compartida provisional de V2. Selectores, serie, historial y panel reutilizan
controles con mínimo 48 dp, scroll/flujo adaptable y `FuzzyRuleDetails`. No se
agregan assets, fuentes, librerías ni animaciones. Los parámetros técnicos se
inspeccionan fuera del flujo de canto. Se aplica `design-taste-frontend` a la
jerarquía y estados dentro de Android, sin imponer un shell web o nueva marca.

Desde `Plataforma/`, con JDK/SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Pruebas públicas: cinco rondas enlazadas, criterio congelado, dos series altas,
direcciones/etapas, error de octava, dispersión, protocolo insuficiente, series
parciales, bloqueos/ordinales/tolerancia ajena, guardado incierto y continuidad
tras reintento, reapertura/exportación/reset y simulaciones sin alterar perfil.
Ruta sintética completa: **250 rondas para 25 objetivos**, sin voz real.

Dos Android nuevas preparadas para preparación/cancelación/simulación sin escribir
el perfil y para cinco rondas sintéticas con tolerancia congelada. Ocho vocales
preparadas en total, compiladas sin ejecutar. Cuando el usuario habilite emular:
ejecutarlas, revisar 411/360/320 dp y 320/texto 200 %, selección de intervalos largos,
panel real/simulado, memoria/pendientes, reset, pausa y regresión del menú. TalkBack,
sonido y voz real permanecen pendientes; compilar no acredita su funcionamiento.

Candidata:
`Plataforma/build/apk-archives/neuma-v0.12.18-intervalos-vocales-v4-prueba-sin-emulador.apk`.
Evidencia duradera en `docs/evidencias/intervalos-vocales-v4/`, logs/snapshot/PDF en
`Plataforma/build/verification/2026-10-30-vocal-v4/`. Sin push, publicación, cambios
de issues o protocolo oficial. Próximo frente vocal: definir el siguiente incremento
de andamiaje (ancla dada/producción libre) según la ficha y las mediciones disponibles.

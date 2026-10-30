# Rhythm Tap R6 — progresión por categoría y tempo

Incremento [#21](https://github.com/SaladinoJC/Tesis/issues/21), según la [especificación vigente](Especificacion-ritmo-v1.md). Implementación local del 2026-10-30, **0.12.12/código 27**, con verificación en Android pendiente. El usuario autorizó continuar el software sin abrir el emulador. No se modifica el protocolo ni se presenta una evaluación pedagógica.

## Recorrido

1. Entrar en Rhythm Tap. **Práctica** conserva acceso a todas las células y permite tempo manual de 60 a 120 BPM, en pasos de cinco. No modifica rachas ni desbloqueos.
2. Elegir **Progresión**. Inicialmente se pueden seleccionar R20 (negras) y R21 (dos corcheas), ambas a 60 BPM. Cambiar de modo invalida la vista previa; una ejecución iniciada no permite cambiarlo.
3. Elegir células y compás, generar y revisar. El tempo es el menor recomendado de la selección. Si la partitura ya fue evaluada, se identifica como práctica y se pide generar otra para progresar. No se cambia silenciosamente la música para convertirla en un ejercicio nuevo.
4. Pulsar Play. Conteo, audio, taps, evaluación y lectura lateral conservan el funcionamiento existente. El tempo queda fijo durante todo el ejercicio. Se puede progresar con metrónomo apagado.
5. Al completar y guardar, el resultado y su decisión de progreso se confirman juntos. Dos resultados altos elegibles de una categoría aumentan cinco BPM para el siguiente ejercicio y registran dominio para abrir sus dependientes. El techo de prueba es 120 BPM; los accesos adquiridos se conservan.
6. **Progreso y DDA** permite elegir una célula, consultar tempo, racha, prerrequisitos e identificadores de evidencia. La última decisión real muestra unidades, precisión/control, pertenencias, reglas, centroide, propuesta, acción y motivo. El historial también ofrece esos datos en su sección técnica y permite filtrar práctica/progresión.
7. **Simulación** ejecuta escenarios sintéticos separados del perfil. Se puede elegir el ejercicio dentro de cada escenario para inspeccionar sus decisiones. Repetir desde resultados o historial siempre vuelve a práctica y conserva la partitura exacta.

## Evidencia y reglas

Se cuentan únicamente las apariciones objetivo de la categoría. Una unidad es un ataque esperado o un intervalo de silencio. Se requieren dos apariciones y dos unidades. A tiempo y silencio respetado suman éxitos; anticipos, atrasos, omisiones y silencios con extras no los suman. Cada extra se atribuye por su posición a una única aparición; los de conexión permanecen en el resultado general. Las ligaduras comparten un ataque, sin exigir reataque.

Precisión = éxitos/unidades; control = 1 − min(1, extras/unidades). Se reutiliza Mamdani con las pertenencias y cinco reglas de la especificación, mínimo/máximo e integración del centroide en 1001 puntos sobre [0,1]. Dominio alto desde 0,75; medio desde 0,40. Versión `rhythm-mamdani-v1`; el registro conserva la configuración completa.

Un ejercicio completo no alto, con evidencia suficiente y al tempo recomendado, reinicia la racha. Práctica, repetición, interrupción, información insuficiente y éxito a tempo inferior conservan el estado. En mezclas, la categoría más rápida no gana tempo ni pierde su racha por trabajar al tempo de otra. No se reducen tempos ni se revocan categorías. El grafo experimental de prerrequisitos está en `RhythmProgression.kt` y se consulta por célula; no exige llegar al techo para desbloquear.

## Guardado y compatibilidad

Elección técnica provisional del agente: **un solo archivo `rhythm-results.json`**, con decisión y snapshots de progreso opcionales en cada registro. Los resultados previos R1–R5 siguen siendo práctica; no se les asigna progreso retroactivo. No se agrega otro archivo de perfil ni otra dependencia. El avance visible proviene exclusivamente de registros confirmados.

La decisión se prepara bajo la barrera compartida del perfil y se confirma mediante la misma sustitución atómica que el resultado. UUID/revisión deduplican reintentos; una secuencia de confirmación ordena los snapshots sin depender del reloj de pared. La huella musical omite semilla, UUID y BPM, para que la misma música evaluada no vuelva a contar al cambiar esos datos. Los intentos interrumpidos no consumen esa evidencia como ejercicio completado.

Si falla el guardado, el ejercicio queda pendiente en memoria y exportable; no se aplica progreso ni se permite otra ejecución de progresión hasta recuperar el historial. El reintento usa la misma identidad. Matar el proceso antes de guardar/exportar puede perder esos pendientes, como en R5.

Las revisiones tardías del último ejercicio se recalculan desde su snapshot anterior, conservando tempo e hitos ya adquiridos, sin duplicar aumentos. Si ya se confirmó un ejercicio posterior, se actualiza la evaluación musical del anterior pero se conserva la decisión que se usó para avanzar: `evaluatedRevision` identifica esa evidencia sellada y el panel lo informa. Es una política técnica conservadora, no una nueva medición de desempeño. Los snapshots y las decisiones originales quedan disponibles para contrastarla.

El JSON exportado agrega `rhythmProgress` y mantiene registros, decisiones/configuración y pendientes. Recuperación ZIP y reinicio incluyen el avance mediante el mismo archivo de ritmo, sin agregar un octavo archivo ni cambiar los otros módulos. La reapertura no inicia audio. No se promete volver a instalar versiones antiguas sobre datos nuevos.

## Banco sintético y verificación

Resultado de host: **178 JVM completas aprobadas**, de las cuales 11 son nuevas de R6; lint **0 errores/17 advertencias**; ensamblados de app y pruebas Android aprobados en **6 min 35 s**. Las dos pruebas Android nuevas fueron compiladas, sin ejecutar. Se cotejaron por SHA-256 los 330 archivos de fuentes/configuración entre raíz y copia aislada. Los 149 assets empaquetados son idénticos a 0.12.11.

[APK candidato sin instalar](../Plataforma/build/apk-archives/neuma-v0.12.12-r6-prueba-sin-emulador.apk): 14.977.972 bytes, SHA-256 `199fdb9573f7b52837ca25cc4a24d613686103ce270d47794048339ed9e54187`. Firma debug compatible con R5 comprobada con `apksigner`; paquete `ar.edu.unmdp.entrenamiento`, etiqueta Neuma, versión 0.12.12/código 27 comprobados en el APK. La última entrega completa verificada en Android sigue siendo 0.12.10.

Escenarios implementados: dos altos; alto seguido de omisiones; mezcla con una categoría más lenta; evidencia insuficiente; avance hasta techo; contenido repetido con otra semilla; fallo/reintento de guardado. Cada uno usa sesiones públicas y un almacén privado en memoria. La UI real nunca usa sus resultados para desbloquear. Los valores 60/+5/120 y las funciones difusas son experimentales, sin calibración acústica ni validación con estudiantes.

Desde `Plataforma/`, con Java/SDK configurados como en README:

```powershell
.\gradlew.bat --offline --no-daemon --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Las pruebas `RhythmProgressionTest` y `RhythmProgressionViewModelTest` cubren sesión, modos, métricas, persistencia, concurrencia, revisiones, exportación/reinicio, fallo/reintento y aislamiento sintético. La prueba del banco genera `app/build/verification/rhythm-r6/scenarios.json` con resultados completos, no solo expectativas. Se conserva una síntesis duradera de la verificación en `docs/evidencias/ritmo-r6/`; logs, fuentes y reportes completos quedan en `Plataforma/build/verification/2026-10-30-R6/`.

Se preservan el rojo inicial por ausencia de la API y las iteraciones intermedias. La regresión detectó una lectura adicional durante el guardado que bloqueaba el caso de lectura suspendida del historial; se retiró esa lectura. Los almacenes de ritmo de producción devuelven directamente el registro confirmado bajo la transacción. La suite final completa vuelve a aprobar ese caso y los 177 restantes. El informe incorpora un registro parcial de implementación y plan de pruebas; XeLaTeX genera 29 páginas sin errores ni desbordes horizontales, con bibliografía aún vacía.

`RhythmProgressionAndroidTest` queda preparado para un emulador de pruebas autorizado: dos ejercicios, desbloqueo, panel y repetición; estado vacío y simulación sin audio ni escritura real. No se ejecuta por el pedido del usuario. Después habrá que probar a 411/360/320 dp y 320 dp con texto 200 %, revisar foco/controles y recorrer guardado/exportación con JSON nativo. También siguen pendientes los últimos ajustes de lectura lateral y retorno de DocumentsUI de 0.12.11; no quedan acreditados por R6.

## Identidad y límites

Se reutilizan `TunerGardenTheme`, `GardenTokens`, `HistoryChoice`, estados de guardado, notación y controles Compose. Papel cálido, verdes naturales, Fraunces/Nunito Sans, disposición vertical y controles nuevos de al menos 48 dp. No se agregan assets, bibliotecas ni animaciones. El detalle técnico vive en el panel de inspección y en el historial; no ocupa el área de ejecución musical.

Skill de diseño: dirección orientativa `framed-sheet`, semilla 504618464, conservando paleta y tipografía. La estructura de escritorio y la hoja blanca no se trasladan: prevalecen las instrucciones del proyecto de reutilizar la interfaz Android y Music Garden. La pantalla responde «qué célula puedo practicar para avanzar y por qué» con datos reales; los escenarios se identifican como sintéticos. La revisión de capturas, dimensiones, texto ampliado y TalkBack queda pendiente por la restricción de no emular. No se fabricó una réplica HTML para atribuirle validación de Compose.

No se abrió el emulador, no se instaló en dispositivos, no se midió sincronía acústica ni se evaluó eficacia pedagógica. R6 no se considera cerrado en Android. Personaje continúa pausado; D3/#25 es el siguiente incremento de software después de este frente.

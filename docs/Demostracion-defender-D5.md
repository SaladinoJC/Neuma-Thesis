# Defender D5 — séptimas, inversiones y claves

Fecha: 2026-10-30. Usuario: «Seguí con el D5». **D5/#27 implementado localmente**, candidata **Neuma 0.12.15/código 30**, con verificación Android pendiente. Se conserva la orden de no abrir el emulador; no hubo instalación ni pruebas en dispositivos. D6/D7 (piano) y Personaje quedan fuera de este incremento.

## Recorrido

1. En Defender, elegir **Acordes por nombre**. Práctica ofrece ocho calidades, sol/fa/mezcla y las inversiones válidas de cada calidad. Progresión permite únicamente las combinaciones habilitadas por el perfil.
2. Elegir calidad, clave e inversión del objetivo. El acorde se presenta completo, con tres o cuatro voces de disposición cerrada y hasta dos líneas adicionales por extremo. Cambiar de séptima en tercera inversión a tríada ajusta la selección a segunda; no se conserva una inversión inválida.
3. Responder **raíz escrita + alteración + calidad**, sin octava ni inversión. El bajo puede ser otra nota. La fila de calidades de respuesta se desplaza horizontalmente para mantener una sola fila táctil y dejar espacio al campo. Do sostenido y Re bemol siguen siendo respuestas diferentes.
4. Solo el acierto reproduce el acorde exacto. Error conserva selección y vidas; llegada quita una vida. Pausa, timestamps de entrada, DDA de futuros enemigos y cierre provisional/sellado siguen el contrato de D3/D4.
5. Revisar solución, voces, inversión y bajo en el resumen y las pérdidas. Historial filtra también por inversión y contexto de claves. Inspector muestra racha y prerrequisitos del objetivo elegido, evidencia de cada clave e inferencias reales o simuladas.

## Modelo y catálogo

`defender/DefenderChords.kt` agrega dominante7 [0,4,7,10], mayor7 [0,4,7,11], menor7 [0,3,7,10] y semidisminuida7 [0,3,6,10] a las cuatro tríadas. Las terceras, quintas y séptimas se escriben por distancia diatónica; luego se calcula su alteración. Se rechazan dobles alteraciones.

Para invertir se elevan una octava las primeras voces de la disposición fundamental y se rotan al final. No se omiten ni duplican voces. La raíz conserva su identidad conceptual, aunque no sea el bajo ni aparezca en su octava original. Las voces efectivamente visibles y audibles tienen alturas ordenadas, extensión menor que una octava y grafías diatónicas verificadas. Se admiten fundamental/primera/segunda de tríadas y hasta tercera de séptimas.

El catálogo finito contiene **1.502 realizaciones distintas**, 751 en sol y 751 en fa, contando registros e inversiones. Las voces ofrecidas abarcan **MIDI 35–85**; estos extremos corresponden a Do bemol2 y Do sostenido6, dentro de los límites escritos Do2–Mi4 en fa y La3–Do6 en sol. No se promete cada raíz en cualquier registro o inversión. Los pools no vacíos cubren **84 objetivos**: (4 tríadas × 3 inversiones + 4 séptimas × 4 inversiones) × 3 contextos de claves.

| Calidad | Realizaciones en ambas claves y todas sus inversiones |
| --- | ---: |
| Mayor | 180 |
| Menor | 180 |
| Disminuida | 158 |
| Aumentada | 138 |
| Dominante 7 | 200 |
| Mayor 7 | 216 |
| Menor 7 | 229 |
| Semidisminuida 7 | 201 |

La identidad de respuesta sigue excluyendo octava, inversión y clave. La unicidad se aplica entre todos los objetivos vivos, no solo dentro de un registro. En mezcla se alterna sol/fa por identificador de aparición efectiva; una oportunidad omitida no salta el turno ni acumula una ráfaga. Se filtra la respuesta antes del sorteo y se conserva la reserva de espacio de las trayectorias.

Versiones nuevas: configuración `defender-d5-v1`, acorde `defender-chords-v2`. El lector sigue admitiendo `defender-triads-v1` con sus restricciones originales. No se cambia la notación de notas ni se incorpora VexFlow a Defender.

## Prerrequisitos y evidencia

Cada objetivo se identifica por **calidad/inversión/contexto de claves** dentro de la variante `chords`. Los 84 objetivos tienen claves estables; los avances de notas permanecen separados. Reglas según la especificación vigente:

- En fundamental/sol: mayor → menor → disminuida → aumentada → dominante7 → mayor7 → menor7 → semidisminuida7.
- Dominar las ocho fundamentales en sol habilita las fundamentales en fa y las primeras inversiones en sol. No se exige otro orden entre las ocho fundamentales en fa.
- Primera inversión por calidad/clave requiere su fundamental y el hito de las ocho fundamentales en sol. Segunda requiere primera en el mismo contexto; tercera requiere segunda y solo existe para séptimas.
- Dominar las ocho fundamentales en fa habilita las fundamentales en mezcla. Las inversiones en mezcla exigen su antecedente en mezcla; no se heredan de sol o fa aisladas.

Dos completas altas elegibles del **mismo objetivo** confirman dominio; mínimo diez resueltos globales y tres resueltos por cada clave del objetivo. La supervivencia se calcula por calidad/inversión/clave efectivamente resuelta. La precisión global sigue como condición común, sin adjudicar un fallo a un enemigo no seleccionado. Una clave fuerte no compensa a la otra en mezcla. Práctica, interrupción, resultado no sellado e insuficiencia conservan sin sumar; una elegible no alta reinicia la racha propia. Desbloqueos adquiridos se conservan.

Los parámetros Mamdani, mínimos y grafo son experimentales. Aprobar estas condiciones verifica el software; no acredita dominio musical validado ni eficacia pedagógica.

## Compatibilidad y almacenamiento

Se conserva `defender-results.json` y el perfil de ocho archivos. Resultado, eventos, inferencias y snapshots se confirman atómicamente con el progreso, manteniendo reintento y revisión de D3. No se guarda PCM.

`chordContents` guarda combinaciones nuevas. El mapa previo `qualities` se conserva y se interpreta exclusivamente como fundamental/sol; solo ese contexto lo actualiza. Un perfil D4 con las cuatro tríadas dominadas habilita dominante7 en sol, sin acreditar fa, mezcla o inversiones. Las decisiones ya almacenadas no se recalculan al leer. La lectura de JSON D3 y D4 sin campos nuevos está cubierta; no se promete lectura por versiones antiguas de la aplicación.

Exportación conserva notas, los registros anteriores y `defenderProgressByVariant`, ahora con el detalle por objetivo. Recuperación/ZIP/reinicio siguen usando los ocho archivos y pendientes existentes. El historial distingue una partida antigua fundamental/sol mediante valores de lectura compatibles, sin inventar voces nuevas. Las reglas de sellado, revisión y muerte de proceso siguen en [D3](Demostracion-defender-D3.md).

## Audio y notación

La síntesis propia mezcla **tres o cuatro** `referenceTone` de las alturas de la disposición mostrada, promediando muestras PCM16. Runtime mono/44.100 Hz, 600 ms, fades de 20 ms y amplitud base 0,2. No hay muestras externas ni transporte a una octava sustituta. Los audios de notas y efectos conservan sus controles y cancelación.

El corpus espectral nuevo comprueba extremos grave/agudo de cada una de las 56 combinaciones de calidad/inversión/clave simple, a **8.000 Hz de análisis**, sin saturación ni voces ausentes. Esta frecuencia de prueba reduce costo del banco; la aplicación sigue reproduciendo a 44.100 Hz. Las comprobaciones anteriores a 44.100 Hz continúan, ahora también con séptimas fundamentales en Do natural. ViewModel comprueba el envío exacto de las cuatro voces de una semidisminuida en tercera inversión/fa y silencio de acorde ante una respuesta por el bajo incorrecto. Son señales digitales sintéticas, no mediciones acústicas.

El pentagrama Canvas usa un viewport de acordes de **144×120 unidades**, tres columnas de alteraciones y cabezas desplazadas a lados opuestos de la plica cuando hay segundas escritas. Ese desplazamiento se deriva de los datos musicales y se comprueba en todo el corpus. Las notas individuales conservan su viewport previo. Las tarjetas/reservas de trayectoria se amplían para acordes, sin forzar el cupo de cuatro si no hay lugar.

Se conservan papel cálido, verdes/tokens, Fraunces/Nunito Sans, componentes y gráficos botánicos de D3. Sin bitmaps, fuentes, dependencias ni animaciones nuevas; editable/origen en el inventario. Continúa la desviación provisional de árbol/polillas simplificados y vidas textuales. La corrección de geometría musical en JVM **no acredita** legibilidad renderizada en Android.

## Verificación reproducible y pendientes

**Cierre de host:** **218 JVM ejecutadas sin fallos (9 nuevas)**, lint **0 errores/17 advertencias**, ensamblados de app y AndroidTest aprobados en **10 min 40 s**. Dos pruebas Android nuevas compiladas, sin ejecutar. Las **350 fuentes/configuraciones** coinciden por SHA-256 con la copia aislada; los **149 assets empaquetados** son idénticos a D4.

[APK candidato sin instalar](../Plataforma/build/apk-archives/neuma-v0.12.15-defender-d5-prueba-sin-emulador.apk): **15.158.200 bytes**, SHA-256 `c53c44fe54e5699b2f89ba0ae73ef484e382e2bb93b39bd0d83eaffb2cc22ba3`. Firma debug compatible con D4 verificada; certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`. `aapt2` confirma paquete `ar.edu.unmdp.entrenamiento`, nombre Neuma y versión 0.12.15/código 30. No se instaló.

La evidencia seleccionada se conserva en `docs/evidencias/defender-d5/`: síntesis de host, XML nuevos, catálogo y ruta por objetivos. Logs, fuentes cotejadas, reportes completos y APK AndroidTest en `Plataforma/build/verification/2026-10-30-D5/`.

El banco aislado agrega **Catálogo completo**, una ruta de **168 partidas sintéticas** que domina los 84 objetivos mediante la sesión pública. Comprueba que el grafo no tenga ciclos ni dependencias ausentes, que cada contexto tenga pool válido y que la evidencia requerida esté presente. No toca el perfil real ni reproduce audio. La ruta anterior de cuatro tríadas sigue disponible y rotulada como tal.

Desde `Plataforma/`, con Java/SDK según README:

```powershell
.\gradlew.bat --offline --no-daemon --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Se usa la copia aislada con cotejo SHA-256, un worker y prioridad BelowNormal. Esta orden no emula. `DefenderD5AndroidTest` prepara una partida completa de séptima en tercera inversión/fa con sonido despachado y reapertura/detalle, más mezcla con reducción, pausa y bloqueos iniciales. **Las pruebas Android solo se compilan** en esta sesión.

Cuando el usuario habilite emular: ejecutar D5 y regresión D1–D4; revisar cuatro objetivos, ambos sentidos de plica, segundas y alteraciones, sol/fa/mezcla, controles desplazables, pérdidas y filtros. Comprobar 411/360/320 dp y 320 dp/texto 200 %, foco, pausa/retorno, reducción de movimiento, JSON/exportación/recuperación/reinicio nativos y audio de cuatro voces. Teléfono, TalkBack por gestos/locución y latencia acústica siguen pendientes. Sin capturas nuevas de la aplicación ni afirmaciones de validación visual nativa.

La última entrega completa verificada en Android sigue siendo 0.12.10. D5 queda implementado localmente; próximo software **D6/#28**, piano inicial, sujeto a la prioridad que indique el usuario. No hubo push, publicación, cambios de issues ni instalación.

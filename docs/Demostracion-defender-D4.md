# Defender D4 — tríadas por nombre

Fecha: 2026-10-30. Pedido del usuario: «Dale, metele a D4». Implementación local de **D4/#26**, candidata **Neuma 0.12.14/código 29**. Se mantuvo cerrado el emulador; no se instalaron APK ni se ejecutaron pruebas en dispositivos. D3 y R6 conservan sus verificaciones Android pendientes. Personaje sigue pausado.

## Recorrido y alcance

1. En Defender, elegir **Tríadas** y práctica o progresión. Práctica ofrece mayor, menor, disminuida y aumentada; progresión empieza por mayor. Las notas conservan su propia selección y progreso.
2. Los objetivos muestran tres notas en estado fundamental y clave de sol. La respuesta es raíz escrita, alteración y calidad, sin octava. Seleccionar las tres partes y pulsar **Tocar**. La alteración inicial es natural; la calidad exige selección explícita.
3. Un error conserva la selección y las vidas; una llegada quita una vida. El acierto elimina un objetivo, suma 100 puntos, limpia la respuesta y reproduce las tres voces si está habilitado el sonido musical. No hay preescucha ni reproducción de la raíz como sustituto del acorde.
4. Pausa, reducción de movimiento, cupo de uno a cuatro, viaje manual y DDA reutilizan D3. Los objetivos vivos mantienen sus plazos cuando cambia el viaje de los futuros. La reducción los deja quietos y muestra tiempo, sin cambiar las reglas.
5. Al terminar, revisar tríadas y voces exactas en resumen, pérdidas, historial e inspector. El cierre de entrada mediante navegación confirma el resumen provisional; hasta entonces no se otorga progreso. Exportar desde Datos conserva eventos, grafías, configuración y decisiones.

Séptimas, inversiones, clave de fa, mezcla y piano continúan en D5–D7. La entrega no representa el catálogo completo de acordes de la especificación.

## Modelo musical y respuesta

`defender/DefenderChords.kt` conserva raíz, calidad, tres voces, inversión cero y versión `defender-triads-v1`. Las fórmulas en semitonos son mayor [0,4,7], menor [0,3,7], disminuida [0,3,6] y aumentada [0,4,8], según la especificación del proyecto. Las letras de las voces están a distancias diatónicas 0/2/4: no se obtiene la notación eligiendo nombres enarmónicos arbitrarios desde MIDI.

El catálogo finito recorre raíces escritas La3–Fa5, naturales/bemoles/sostenidos, y calcula la alteración necesaria de cada tercera y quinta. Excluye toda realización que requiera dobles alteraciones. Las voces quedan dentro de La3–Do6, con hasta dos líneas adicionales. Por ejemplo, Sol sostenido aumentado se excluye porque exigiría una quinta con doble sostenido. No se promete ofrecer cada raíz con cada calidad.

La identidad de respuesta incluye **letra de raíz + alteración + calidad**. Do sostenido mayor y Re bemol mayor pueden tener los mismos MIDI pero son respuestas distintas. La octava no integra esa identidad: el generador excluye cualquier respuesta ya viva aunque aparezca en otro registro. Si no hay contenido o espacio disponible, consume la oportunidad de aparición y espera a la próxima, sin búsquedas indefinidas ni ráfagas acumuladas.

El catálogo generado contiene **115 realizaciones**: 31 mayores, 32 menores, 28 disminuidas y 24 aumentadas. Al excluir octava, corresponden a 17/17/15/13 respuestas distintas por calidad, respectivamente. Estos conteos corresponden al banco implementado, no a todo el universo musical.

Pentagrama, solución, matching y sonido parten del mismo acorde. La pulsación captura también la calidad junto al timestamp original; cambiar la selección antes de entregar un evento no cambia la respuesta capturada.

## Audio

`defenderChordTone` mezcla tres `referenceTone` simultáneos y promedia sus muestras PCM antes de reproducirlas mediante el `AudioTrack` existente. Son sinusoides generadas por código propio en memoria: mono, PCM16, 44.100 Hz, 600 ms, fades de 20 ms y amplitud base 0,2. No hay muestras de terceros, transposición aproximada, archivos PCM persistidos ni permiso de micrófono.

Las pruebas espectrales verifican las tres frecuencias de las cuatro calidades sobre Do natural, tamaño de 26.460 muestras, extremos en cero y ausencia de saturación. Otra prueba observa el límite de audio desde la sesión: silencio antes de acertar, ningún sonido de acorde al fallar y tres MIDI exactos al acertar. Esto verifica síntesis y despacho; no acredita volumen, latencia, mezcla con efectos ni calidad perceptiva del altavoz Android. La síntesis dura lo mismo que una nota individual y comparte su cancelación al pausar/salir.

## Progreso, historial y compatibilidad

Se reutilizan las reglas experimentales Mamdani y los umbrales de D3. Cadena propia: **mayor → menor → disminuida → aumentada**, con dos completas altas elegibles del contenido objetivo, al menos diez resueltos y tres del objetivo. La evidencia de calidad se calcula con sus enemigos; la precisión global sigue como condición común, sin atribuir un fallo a un objetivo no seleccionado. Práctica, interrupción, cierre no confirmado o evidencia insuficiente no otorgan avance. Las simulaciones no alteran el perfil real.

El historial calcula avances por variante (`notes`/`chords`); los logros de notas no habilitan tríadas ni viceversa. Las revisiones posteriores solo congelan una decisión si hubo una confirmación posterior de la misma variante. Se conserva una secuencia global de guardado para ordenar operaciones. Un mismo intento no puede cambiar de configuración o variante mediante revisión.

Se mantiene **un solo `defender-results.json`** y ocho archivos conocidos del perfil. Los nuevos campos son opcionales al leer datos anteriores: los registros D3 sin acorde/calidad/variante se interpretan como notas. Una prueba construye ese formato y lo reabre sin acreditar tríadas. La exportación conserva el campo previo `defenderProgress` de notas y agrega `defenderProgressByVariant`, además de los registros completos. ZIP, recuperación, reinicio, guardado atómico y reintento usan los mecanismos existentes. No se promete que versiones antiguas puedan leer futuras exportaciones.

La política provisional de sellado, pendientes y muerte de proceso sigue documentada en [D3](Demostracion-defender-D3.md). No se implementó reanudación de una partida activa tras muerte de proceso.

## Verificación y reproducción

**Cierre de host:** 209 JVM ejecutadas sin fallos (13 nuevas), lint **0 errores/17 advertencias**, ensamblados de app y AndroidTest aprobados. Dos casos Android nuevos compilados, sin ejecutar. La compilación completa inicial tardó 6 min 19 s; tras el ajuste de alcance de simulación y las comprobaciones de filtros, la verificación final completa tardó **5 min 58 s**. Se cotejaron **346 fuentes/configuraciones** por SHA-256; los **149 assets empaquetados** son idénticos a D3.

[APK candidato sin instalar](../Plataforma/build/apk-archives/neuma-v0.12.14-defender-d4-prueba-sin-emulador.apk): **15.265.361 bytes**, SHA-256 `0ffa063af2a83d779be5c56c15039129d5f0d5d4069cfe24c96f5adbf2cbb718`. `apksigner` verifica firma debug compatible con D3; certificado SHA-256 `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`. Paquete `ar.edu.unmdp.entrenamiento`, nombre Neuma, versión 0.12.14/código 29 comprobados mediante `aapt2`. No se instaló.

Las pruebas nuevas de JVM ejercitan catálogo completo, grafías, límites, respuesta incompleta, error/acierto, unicidad sin octava, entrega tardía con calidad capturada, síntesis/despacho, progreso separado, ruta de cuatro calidades, JSON anterior, reapertura, exportación, ZIP, reinicio, revisión incompatible, sellado desde ViewModel y simulaciones aisladas. Los datos del banco son **sintéticos**, no resultados de estudiantes.

Evidencia duradera seleccionada: `docs/evidencias/defender-d4/`. Fuentes verificadas, logs, reportes completos, catálogo, ruta sintética y APK AndroidTest: `Plataforma/build/verification/2026-10-30-D4/`. El catálogo y la ruta se generan desde las pruebas públicas, no se transcriben manualmente.

Desde `Plataforma/`, con Java/SDK según README:

```powershell
.\gradlew.bat --offline --no-daemon --no-parallel --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Se utilizó la copia aislada de verificación, con prioridad BelowNormal y cotejo SHA-256 contra las fuentes de raíz. Esta orden no inicia emulador. La última entrega completa verificada en Android sigue siendo 0.12.10.

## Revisión Android pendiente e identidad visual

`DefenderD4AndroidTest` prepara dos recorridos: selección completa, dos partidas con desbloqueo de menor, progreso de notas intacto y reapertura/detalle; práctica de aumentada bloqueada en progresión y simulación aislada. **Compilar estos casos no equivale a ejecutarlos.** Cuando el usuario habilite esa etapa, ejecutar D4 y regresión D1–D3, comprobar exportación/JSON nativos, persistencia y pausa/retorno.

Revisar 411/360/320 dp y 320 dp con texto 200 %, cuatro objetivos y toda la fila de calidades, alturas disponibles, alteraciones próximas, extremos de registro y líneas adicionales. Comprobar sonido simultáneo en Android y teléfono, TalkBack por gestos/locución, foco y reducción de movimiento. No hay capturas nuevas ni afirmación de legibilidad renderizada en esta sesión.

Se reutilizan papel cálido, verdes de `GardenTokens`, Fraunces/Nunito Sans, tema, botones, selectores y gráficos botánicos de D3. El pentagrama Canvas se amplía a tres cabezas con plica común, líneas adicionales y alteraciones en columnas; recibe datos estructurados. Las tarjetas y la reserva de trayectorias se amplían para tríadas. No se agregan bitmaps, dependencias, fuentes ni animaciones. Editable y procedencia registrados en el inventario.

Persiste la desviación consciente de D3: árbol/polillas simplificados y vidas textuales, sin las tres hojas del arte final. No se presenta como acabado visual aprobado. La validación nativa queda pendiente por instrucción del usuario de no emular; ninguna réplica web la sustituye.

D4 queda implementado localmente; siguiente incremento de software **D5/#27**. No hubo push, publicación, modificación de issues ni instalación.

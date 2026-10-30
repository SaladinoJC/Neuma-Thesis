# Afinador A2 — perfiles y afinaciones

Trabajo retomado por el usuario el **2026-10-24**, sobre [#10](https://github.com/SaladinoJC/Tesis/issues/10), con la base A1/v0.11. La implementación local incorpora configuración persistida, editor y contratos de exportación/reinicio. Está integrada en v0.12 con suite JVM y Android aprobadas en API 34; consultar la [verificación conjunta](Verificacion-prototipo-v0.12.md) y el [recorrido de entrega](Demostracion-prototipo-v0.12.md). No guarda desempeño ni mediciones en el historial de voz. Guiado, referencias y entrenamiento siguen en sus tickets.

## Contrato previsto para el incremento

La afinación guarda seis alturas exactas en orden sexta → primera, sin ordenar frecuencias ni cambiar octavas. Cada comienzo de captura toma una copia de la guitarra, afinación/revisión, criterio y seguimiento. Cambiar configuración detiene la captura y descarta evidencia de estabilidad; no vuelve a iniciarla automáticamente.

Rango provisional de edición: Do2–Mi4 (MIDI 36–64), temperamento igual y La4=440 Hz. Comprende los seis presets aprobados. A2 verifica objetivos con el estimador y referencia de tono puro sintética; no incorpora referencias de guitarra/piano ni promete cobertura de muestras aún pendientes de A4. Su funcionamiento físico y la precisión Pro requieren mediciones separadas.

## Recorrido de demostración

1. Abrir Afinador y la configuración. La primera apertura ofrece «Guitarra» con afinación estándar.
2. Crear una guitarra con nombre, elegirla y seleccionar Drop D. La sexta cuerda debe ofrecer Re2. Elegir Pro y seguimiento de cuerdas.
3. Cerrar y reabrir: nombre, afinación y preferencias deben conservarse, con micrófono detenido.
4. Crear una afinación personalizada con nombre y seis notas/octavas, incluyendo una disposición no ascendente si se desea. Guardar y comprobar objetivos por número de cuerda. Editarla y comprobar que cambian los objetivos sin reanudar micrófono.
5. Intentar una nota sin octava, menos/más de seis alturas o fuera de Do2–Mi4. Debe aparecer una explicación sin guardar ni cambiar alturas silenciosamente.
6. Exportar JSON y ZIP: configuración separada de resultados anteriores. Reiniciar solo con confirmación de la app; al volver debe aparecer la guitarra predeterminada.

La prueba en S22 Ultra/guitarra sigue pendiente. Registrar Android, instrumento, afinación, distancia, entorno y patrón usado antes de afirmar exactitud física.

## Verificación

Último verde real: primer slice de perfiles/presets/preferencias, `:app:testDebugUnitTest --tests '*tuner*'`, **seis pruebas aprobadas**, compilación en 1 min 45 s. Antes se comprobó rojo por ausencia de repositorio/sesión/configure.

Después se agregó el flujo personalizada → edición → copia → exportación/ZIP/reinicio y se comprobó rojo por `saveCustom` y `DemonstrationExport.tunerConfiguration` ausentes, en 45 s. Se implementó ese segundo slice y se añadieron pruebas de presets, orden/duplicados, entradas inválidas, corrupción/esquemas futuros, cancelación, rango PCM y compatibilidad.

El 2026-10-24 se ejecutó desde este worktree la **suite JVM completa: 78 pruebas, cero fallos/errores/omisiones, en 1 min 36 s**, con un worker y Kotlin en proceso. Incluye los nueve casos de `TunerConfigurationTest` y una prueba adicional del ViewModel: captura estable → cambio a Drop D → liberación de captura/limpieza de evidencia → preferencias Pro/cromático encoladas → relectura sin reabrir micrófono. Se conservan las pruebas anteriores de voz, almacenamiento y juegos. Hubo avisos del SDK XML y del receptor nullable preexistente en `PitchLabSessionTest`, sin fallo de compilación.

El caso PCM recorrió los **29 objetivos MIDI 36–64**, con `referenceTone` senoidal sintético a 44.100 Hz, ventana de 4.096 muestras desde el índice 8.000 y `yin-v1` predeterminado. Todos conservaron nota/octava; máximo error absoluto observado **0,04265 cents**. Es una simulación de señal pura, sin transitorios de guitarra, ruta acústica ni teléfono, y no acredita esa exactitud en condiciones físicas.

Comando reproducible (el padre coordina los turnos de Gradle por memoria; no iniciar compilaciones paralelas):

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
Set-Location Plataforma
.\gradlew.bat :app:testDebugUnitTest --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process'
```

Informes JVM del frente en `Plataforma/app/build/test-results/testDebugUnitTest/` de su worktree. La raíz integró la factory persistente de `MainActivity` y aprobó las dos Android A1 actualizadas y la nueva Android A2 dentro de la suite de 32 casos/API 34. El primer intento A2 falló porque seis aserciones buscaban coincidencia completa donde se esperaba un fragmento; declarar `substring = true` conservó las exigencias y el recorrido completo pasó. No se ejecutó Android, lint ni APK desde este worktree; la entrega y evidencia están en la raíz.

## Datos y compatibilidad

`tuner-configuration.json` usa `schemaVersion: 1`, perfiles con identificador/nombre/afinación seleccionada, personalizadas con identificador/nombre/seis MIDI/revisión, guitarra actual y preferencias Normal/Pro y cromático/cuerdas. El archivo se escribe por sustitución atómica bajo la misma barrera del perfil que exportación y reinicio. Un archivo incompleto, ilegible, con referencias inválidas o de versión futura no se reemplaza por valores por defecto. La pantalla permite reintentar su lectura; el ZIP conserva sus bytes y un reinicio confirmado lo elimina.

El JSON exportado añade `tunerConfiguration`, separado de resultados y nullable cuando aún no se guardó configuración. El ZIP incorpora el archivo únicamente cuando existe, conservando el comportamiento de perfiles anteriores sin afinador. Un marcador de reinicio interrumpido también elimina este archivo antes de volver a abrirlo. El reinicio debe recargar la configuración visible para no conservar en memoria la guitarra anterior.

Los seis presets siguen las alturas de la especificación, sexta → primera:

| Afinación | MIDI exactos |
| --- | --- |
| Estándar | 40, 45, 50, 55, 59, 64 |
| Drop D | 38, 45, 50, 55, 59, 64 |
| Medio tono abajo | 39, 44, 49, 54, 58, 63 |
| Drop C | 36, 43, 48, 53, 57, 62 |
| Open G | 38, 43, 50, 55, 59, 62 |
| DADGAD | 38, 45, 50, 55, 57, 62 |

El orden no depende de la frecuencia. Si dos cuerdas personalizadas comparten altura, el automático no inventa una identificación; se puede fijar una explícitamente. Crear una guitarra selecciona ese perfil y estándar; cada guitarra recuerda su afinación elegida. Editar una personalizada incrementa su revisión, actualiza sus usos futuros y conserva las copias ya tomadas.

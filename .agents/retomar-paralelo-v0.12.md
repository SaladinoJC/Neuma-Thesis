# Reanudar la tanda paralela v0.12

**Reanudada por pedido del usuario el 2026-10-24.** Integración actual en `trabajo/integracion-v0.12-continuacion`; consultar primero `.agents/estado-prototipo.md` y `docs/Verificacion-prototipo-v0.12.md`. El resto de este archivo conserva los checkpoints y pendientes de la pausa del 2026-10-23 como antecedente, no como estado actual. `main` todavía conserva v0.11 mientras se verifica Android.

## Base y distribución

Base común `c0f9307`: v0.11 y especificaciones/tickets antes sin commit. Se autorizaron tres frentes con subagentes y worktrees. No hacer push ni cerrar tickets como completos.

| Frente | Rama | Worktree | Checkpoint |
| --- | --- | --- | --- |
| Afinador A2 / #10 | `trabajo/afinador-a2` | `C:/Users/Juan/Tesis-worktrees/afinador-a2` | `563c421` |
| Ritmo R1 / #16 | `trabajo/ritmo-r1` | `C:/Users/Juan/Tesis-worktrees/ritmo-r1` | `a284810` |
| Defender D1 / #23 | `trabajo/defender-d1` | `C:/Users/Juan/Tesis-worktrees/defender-d1` | `0edf345` |
| Integración compartida | `trabajo/integracion-v0.12` | Sin worktree propio; checkpoint creado desde la raíz | `d6e3d7e` |

Los subagentes no deben suponerse disponibles en la próxima sesión. Se puede retomar su rama con agentes nuevos; los archivos, commits y documentación son la fuente de continuidad.

## Estado comprobado y pendiente

- **A2:** primer verde real de seis pruebas `*tuner*`. Segundo rojo por APIs de personalizadas/exportación aún ausentes; implementación posterior completa guardada **sin compilar ni verificar**. Incluye perfiles, seis presets, personalizadas/editado con revisión y snapshot, preferencias, JSON atómico con barrera de perfil, exportación/ZIP/reinicio. Nueve pruebas nuevas de configuración y Android escritos, pendientes. Leer `docs/Demostracion-afinador-A2.md` en su worktree. Próximo: suite JVM completa para comprobar también la integración con almacenamiento anterior.
- **R1:** generación inicial verde. Última ejecución completa: seis pruebas, cuatro aprobadas y dos regresiones rojas conocidas (fase con timestamp histórico y adaptador que descartaba un tap tras cierre). Parche posterior aplicado **sin validación final**; ejecución de verde interrumpida al pausar. Modelo racional/semilla/cuota, ejecución/evaluación, audio estático de pulsos, taps reales y UI/notación escritos. Leer `docs/Rhythm-Tap-R1.md` de su worktree y los tests; repetir `*Rhythm*Test` antes de integrar. Android pendiente.
- **D1:** tracer de disparo/deadline verde. Luego tres pruebas con dos regresiones rojas (preparación que resucitaba estado anterior al pausar; timestamp válido entregado tras tick). Correcciones posteriores **sin compilar ni verificar**: transición real `prepare`, pointer-down con timestamp/selección capturados, recomputación de ticks desde último input aceptado y generación semilla+ID, deduplicación de audio. Suite ampliada y dos Android escritos, pendientes. Leer `docs/Demostracion-defender-D1.md` en su worktree; repetir `*Defender*Test`.
- **Integración `d6e3d7e`:** menú `game-rhythm`/`game-defender`, factory persistente del afinador, `awaitConfigurationIdle/reload` en exportación/reinicio, ciclo de vida y navegación, versión 0.12/code15, prueba Android conjunta y borrador de recorrido. **No compila por sí sola:** necesita las tres ramas anteriores, todavía no incorporadas. No confundir el borrador de demostración con un APK existente.

## Contratos para integrar

- `TunerViewModel(configurationStore = JsonTunerConfiguration(File(filesDir,"tuner-configuration.json")))`; conserva constructor de pruebas A1 con recorder/clock. `reload()`, `resetDisplay()`, `awaitConfigurationIdle()`. Storage compartido modificado solo por A2 (`Demonstration.kt`); el reset también borra guitarras/afinaciones. El rango provisional de personalizadas es Do2–Mi4, no una promesa de referencias guitarra/piano de A4.
- `rhythm.RhythmViewModel()` y `ui.RhythmApp(ui, model, onHome)`. `background()/home()` interrumpen y liberan audio; nunca pedir micrófono. Verificar que timestamps históricos no retrocedan fase ni se pierdan por desmontar el listener al mostrar resultados. AudioTimestamp no acredita incertidumbre acústica de 1 ms: quedó desconocida; la cuantización táctil se documenta aparte.
- `defender.DefenderViewModel()` y `ui.DefenderApp(ui, model, onHome)`. Entrada llama `prepare()`, salida `home()`, `onPause` llama `background()` que pausa y no reanuda sola. Clave de sol y escenario son vectores propios, audio sintetizado explícito. Verificar que replay no duplique puntos, sonidos ni resultados ya confirmados.
- Root es responsable de `MainActivity.kt`, `SimonApp.kt`, `DemonstrationApp.kt`, versión, README y estado global. Los agentes conservaron esos archivos de la base para evitar conflictos. Sus tests Android de integración requieren el host del checkpoint compartido.

## Orden práctico al retomar

1. Leer este archivo, estado y documentación específica. Comprobar `git status` y `git worktree list`; no eliminar ramas/worktrees ni repetir implementaciones desde cero.
2. Autorizar **una compilación a la vez**: R1 parche→verde, A2 suite JVM completa, D1 correcciones→verde. Los agentes pueden editar/revisar módulos diferentes en paralelo, pero no correr Gradle/emuladores simultáneamente. Tratar cualquier error nuevo como pendiente, sin atribuir los verdes parciales al código posterior.
3. Tras revisar las ramas, integrar sus checkpoints y correcciones más el commit compartido en una rama de integración que parta del `main` actual (incluye este registro). Se pueden usar `git cherry-pick` de los commits WIP seguidos de fixes; no asumir que el estado WIP ya está revisado. Preservar el registro de continuidad de `main`.
4. En integración, ejecutar suite JVM completa, lint, APK y APK de pruebas con un worker. Después iniciar un solo emulador y ejecutar Android (nuevos recorridos + regresiones previas), inspeccionar pantallas y guardar evidencia/hash. Resolver fallos y solo entonces producir entrega v0.12 y actualizar README/especificaciones/estado como completados.
5. Conservar separado lo pendiente de S22 Ultra: guitarra real, precisión Pro, sincronía acústica, legibilidad y comodidad física. No dar por probada eficacia pedagógica.

## Entorno y comandos

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
# En Plataforma/ del worktree que tenga turno:
.\gradlew.bat :app:testDebugUnitTest --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
# Para cortes locales: --tests '*Rhythm*Test' o --tests '*Defender*Test'.
```

Hubo memoria disponible baja con dos procesos de emulador existentes. Se cerró únicamente el Pixel_6 de nuestra sesión (`emulator-5554`, verificado como emulador); no se tocó el otro proceso qemu preexistente. No iniciar emulador mientras se compila. No usar pruebas instrumentadas sobre teléfono físico. Los informes parciales están ignorados bajo `Plataforma/app/build/test-results/` y `build/` de cada worktree; los commits y documentos conservan qué resultados corresponden a qué corte.

# Defender D2 / #24 — ampliación de práctica (WIP)

Tanda local autorizada por el usuario mediante «Dale» el 2026-10-24, retomando tres frentes separados y con corte al llegar al 5 % de cuota. Base `d0f1624` (v0.12 comprobada). Rama `trabajo/defender-d2`, worktree `C:/Users/Juan/Tesis-worktrees/defender-d2`. No publicar ni dar #24 por completo mientras falte la verificación Android integrada.

## Comportamiento implementado, JVM comprobado y Android pendiente

- Preparación: N1 sol Mi4–Fa5, N2 sol La3–Do6, N3 fa Do2–Mi4, N4 mezcla de ambos rangos ampliados. Todas en práctica, sin desbloqueos ni DDA.
- Alteraciones simples opcionales: naturales, bemoles y sostenidos explícitos e independientes. La altura absoluta incluye la alteración; matching exige letra y alteración escrita sin octava. La respuesta empieza en natural y vuelve a natural al acertar; un fallo conserva letra/alteración.
- Cupo manual 1–4 (UI inicial 4). El constructor de configuración conserva cupo 1 para reproducir el recorrido D1 y sus pruebas. `state.enemy` sigue ofreciendo el primer enemigo como alias de compatibilidad; `state.enemies` es la colección autoritativa.
- Apariciones deterministas con semilla/ID, viaje fijo de 10 s y curva de D1. Cada oportunidad ocupada o sin espacio se descarta, sin ráfagas acumuladas. Las reservas rectangulares comprueban la vida compartida completa de cada trayectoria, mediante intersección de intervalos de movimiento relativo en ambos ejes. No dependen del tick/frame.
- Duplicados escritos permitidos: una respuesta elimina uno, elegido por distancia euclídea a la frontera común de base (segmento normalizado x=0,25–0,75; y=0,84), desempate por aparición/ID. No usa menor tiempo restante.
- UI dibuja clave de sol/fa vectorial, alteración y hasta dos líneas adicionales. Tarjetas compactas con cupo múltiple, solución y revisión por nota/grafía/clave. Pausa oculta todos los objetivos; salir y game over cancelan los vivos restantes sin contarlos como llegadas.

## Audio y condiciones

No se añaden muestras ni recursos de terceros. Se conserva `referenceTone`, seno de 600 ms, PCM16 mono a 44,1 kHz, amplitud 0,2, fades de 20 ms, temperamento igual/La4=440 Hz. El corpus completo, incluidas alteraciones extremas, ocupa MIDI 35–85, dentro del rango 24–96 admitido por el sintetizador. Es síntesis en octava real, no transposición de samples inexistentes. Nota solo al acertar y efectos independientes, con liberación al cancelar como en D1.

## Verificación y continuidad

Se escribieron seis pruebas JVM nuevas de corpus/rangos, matching enarmónico/geométrico con velocidades distintas, reservas cruzadas, respuesta completa, llegadas/cancelación y cuatro simultáneos alcanzables. Corte real del 2026-10-24 sobre `b2b496f`: **19/19 JVM aprobadas** (6 `DefenderExpandedTest`, 10 `DefenderSessionTest`, 3 `DefenderViewModelTest`), BUILD SUCCESSFUL en 1 min 46 s, con compilación productiva de sesión/VM/UI. Se usó el comando de abajo con heap 768 MiB, un worker e in-process, sin emulador concurrente. Avisos previos de SDK XML y nulabilidad en `PitchLabSessionTest`, sin error. No se ejecutó suite JVM ajena a Defender ni lint/APK en este worktree.

Dos Android nuevas escritas: alcanzar cuatro objetivos reproduciblemente, comprobar sus rectángulos sin intersección y pausa; mostrar extremos de sol/fa con bemol/sostenido y reproducir PCM real. Mantienen el helper `tapWithDeviceUptime` de v0.12 para el recorrido táctil anterior. **Android todavía no ejecutado; su legibilidad no está acreditada.**

Comando, desde `Plataforma/` con JAVA_HOME/ANDROID_HOME configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*Defender*Test' --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Integración posterior a cargo del agente principal: suite completa JVM, lint, APK/appAndroidTest, Android API 34 y capturas reales; revisar especialmente tamaño de pentagramas con cuatro tarjetas y controles de alteraciones en vertical. No se modificaron host, versión, almacenamiento compartido, README ni estado global. Prueba S22 Ultra, audibilidad, latencia acústica y comodidad física pendientes; no atribuir eficacia pedagógica al recorrido.

## Checkpoint de cierre por cuota

El agente principal indicó cierre de desarrollo al quedar 19 % de cuota compartida, para reservar integración/guardado antes del corte del 5 %. No se desarrolló D3 ni se corrieron nuevos builds después del verde anterior.

Commits, en orden sobre `d0f1624`: `f2ea44c` dominio/corpus/tests; `0db1ddb` VM/UI/configuración/notación; `b2b496f` Android/reservas/documentación; commit siguiente de cierre solo documental. Worktree limpio al entregarlo.

Contratos conservados: `DefenderViewModel()` sin Context; `ui` StateFlow; `DefenderApp(ui, model, onHome)`; `prepare()/background()/home()` con el comportamiento del host v0.12. `firePointer` admite la alteración capturada como cuarto argumento opcional natural; llamadas anteriores siguen válidas. La sesión tiene `state.enemies` y alias `state.enemy` del primero; configuración sin argumentos mantiene cupo 1, VM/UI inicia cupo 4. No se cambian historial/exportación ni micrófono.

Riesgos y pendientes concretos:

- Dos Android nuevas **escritas pero no compiladas ni ejecutadas**; validar disponibilidad de `Rect.overlaps` y colección de cuatro nodos contra la versión Compose del proyecto al compilar appAndroidTest. La JVM no compila fuentes instrumentadas.
- Tarjetas multienemigo: ancho 0,235 y alto 0,17 del campo; reservas lógicas 0,245 × 0,205. La JVM confirma ausencia de intersección de centros/reservas y posibilidad de llegar a cuatro; falta inspeccionar clave, alteración, líneas y nave decorativa a 360/411 dp. Si se agrandan tarjetas para legibilidad, agrandar reservas coherentemente y revisar cuatro alcanzables.
- El segmento de base x=0,25–0,75/y=0,84 se dibuja con cupo múltiple y sirve a matching geométrico; D1/cupo 1 conserva destino central anterior. Comprobar posición visual del impacto y texto de solución con varias llegadas cercanas.
- El resumen muestra grafía/octava y pentagrama con clave, pero todavía no tiene rótulo textual dedicado de etapa/alteraciones/clave. Los datos están en `configuration` y cada `DefenderNote`; revisar esa presentación para el criterio de resumen de #24 antes de darlo por completo.
- Los tests de distintos tiempos de viaje ejercitan el matching público con enemigos construidos en el límite, mientras que esta práctica ofrece viaje fijo 10 s. No presentar ese banco como DDA aplicado.
- Salida/game over vacían vivos sin sumarlos como llegadas; aún no se agregaron eventos específicos de cancelación de cada enemigo. La persistencia/reproducción ampliada pertenece a los siguientes incrementos; conservar esta limitación al evaluar trazabilidad.

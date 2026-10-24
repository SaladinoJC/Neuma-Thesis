# Defender D2 / #24 — ampliación de práctica (WIP)

Tanda local autorizada por el usuario mediante «Dale» el 2026-10-24, retomando tres frentes separados y con corte al llegar al 5 % de cuota. Base `d0f1624` (v0.12 comprobada). Rama `trabajo/defender-d2`, worktree `C:/Users/Juan/Tesis-worktrees/defender-d2`. No publicar ni dar #24 por completo mientras falte la verificación Android integrada.

## Comportamiento implementado, pendiente de verificar

- Preparación: N1 sol Mi4–Fa5, N2 sol La3–Do6, N3 fa Do2–Mi4, N4 mezcla de ambos rangos ampliados. Todas en práctica, sin desbloqueos ni DDA.
- Alteraciones simples opcionales: naturales, bemoles y sostenidos explícitos e independientes. La altura absoluta incluye la alteración; matching exige letra y alteración escrita sin octava. La respuesta empieza en natural y vuelve a natural al acertar; un fallo conserva letra/alteración.
- Cupo manual 1–4 (UI inicial 4). El constructor de configuración conserva cupo 1 para reproducir el recorrido D1 y sus pruebas. `state.enemy` sigue ofreciendo el primer enemigo como alias de compatibilidad; `state.enemies` es la colección autoritativa.
- Apariciones deterministas con semilla/ID, viaje fijo de 10 s y curva de D1. Cada oportunidad ocupada o sin espacio se descarta, sin ráfagas acumuladas. Las reservas rectangulares comprueban la vida compartida completa de cada trayectoria, mediante intersección de intervalos de movimiento relativo en ambos ejes. No dependen del tick/frame.
- Duplicados escritos permitidos: una respuesta elimina uno, elegido por distancia euclídea a la frontera común de base (segmento normalizado x=0,25–0,75; y=0,84), desempate por aparición/ID. No usa menor tiempo restante.
- UI dibuja clave de sol/fa vectorial, alteración y hasta dos líneas adicionales. Tarjetas compactas con cupo múltiple, solución y revisión por nota/grafía/clave. Pausa oculta todos los objetivos; salir y game over cancelan los vivos restantes sin contarlos como llegadas.

## Audio y condiciones

No se añaden muestras ni recursos de terceros. Se conserva `referenceTone`, seno de 600 ms, PCM16 mono a 44,1 kHz, amplitud 0,2, fades de 20 ms, temperamento igual/La4=440 Hz. El corpus completo, incluidas alteraciones extremas, ocupa MIDI 35–85, dentro del rango 24–96 admitido por el sintetizador. Es síntesis en octava real, no transposición de samples inexistentes. Nota solo al acertar y efectos independientes, con liberación al cancelar como en D1.

## Verificación y continuidad

Se escribieron seis pruebas JVM nuevas de corpus/rangos, matching enarmónico/geométrico con velocidades distintas, reservas cruzadas, respuesta completa, llegadas/cancelación y cuatro simultáneos alcanzables. Se ejecutarán junto con las 13 regresiones D1, por turno exclusivo y sin emulador concurrente.

Dos Android nuevas escritas: alcanzar cuatro objetivos reproduciblemente, comprobar sus rectángulos sin intersección y pausa; mostrar extremos de sol/fa con bemol/sostenido y reproducir PCM real. Mantienen el helper `tapWithDeviceUptime` de v0.12 para el recorrido táctil anterior. **Android todavía no ejecutado; su legibilidad no está acreditada.**

Comando, desde `Plataforma/` con JAVA_HOME/ANDROID_HOME configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*Defender*Test' --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Integración posterior a cargo del agente principal: suite completa JVM, lint, APK/appAndroidTest, Android API 34 y capturas reales; revisar especialmente tamaño de pentagramas con cuatro tarjetas y controles de alteraciones en vertical. No se modificaron host, versión, almacenamiento compartido, README ni estado global. Prueba S22 Ultra, audibilidad, latencia acústica y comodidad física pendientes; no atribuir eficacia pedagógica al recorrido.

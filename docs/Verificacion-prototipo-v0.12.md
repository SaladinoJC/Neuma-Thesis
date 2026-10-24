# Verificación integrada v0.12

Fecha: 2026-10-24. Trabajo local para A2/[#10](https://github.com/SaladinoJC/Tesis/issues/10), R1/[#16](https://github.com/SaladinoJC/Tesis/issues/16) y D1/[#23](https://github.com/SaladinoJC/Tesis/issues/23). Las ramas de cada frente se conservaron en worktrees independientes; compilaciones y emulador se coordinaron por turnos. No se publicaron cambios ni se cerraron tickets.

**Verificación Android en curso. Este informe todavía no acredita una entrega completa.**

## Software y compilación

- Suite JVM integrada: **97 pruebas, 22 clases, sin fallos, errores ni omisiones**. Incluye reglas existentes, perfiles/configuración del afinador, generación/evaluación rítmica y supervivencia de Defender.
- Lint: **0 errores y 14 advertencias**. APK de aplicación y APK de pruebas compilados. Comando conjunto: 3 min 10 s.
- Después se ajustó únicamente el reloj de una prueba táctil Android de Defender: avanzar 1 s desde el comienzo simulado evita que la demora real de entrega ubique el evento antes de la partida. Reconstrucción del APK de pruebas: 31 s. No se modificó producción con ese ajuste.
- Comandos desde `Plataforma/`, JBR de Android Studio y SDK local:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --max-workers=1 --no-daemon '-Dorg.gradle.jvmargs=-Xmx768m -Dfile.encoding=UTF-8' '-Pkotlin.compiler.execution.strategy=in-process' --console=plain
```

Los logs locales están en `Plataforma/build/verification/v0.12/`. Los XML JVM y el informe de lint están bajo `Plataforma/app/build/`.

## Correcciones y límites

Los seis casos JVM de Ritmo verificaron los parches pendientes de la sesión anterior: un evento histórico no retrocede la fase y un tap válido entregado tras el cierre puede corregir una omisión conservando el timestamp original. La espera inicial de hasta 500 ms por AudioTimestamp puede retrasar la aparición del área de tap durante el conteo; no equivale a una medición de latencia acústica.

Defender aprobó 13 pruebas JVM. Se verifican preparación tras interrupción, pausa, empate de disparo/llegada, corrección por timestamp y tercer impacto sin duplicar resultados. Una comparación geométrica exacta de Float se cambió a tolerancia de 1e-6 (`0,82` frente a `0,82000005`). Si ya sonó una llegada antes de recibirse un disparo histórico, se corrige el estado pero no se puede retirar ese sonido: se evita emitir una segunda resolución del mismo enemigo.

Afinador aprobó la suite de su rama con 78 JVM antes de integrar. Configurar detiene y drena la captura; las preferencias se serializan y recargar no inicia el micrófono. La prueba sintética de 29 alturas MIDI 36–64 usa senoidales a 44,1 kHz y ventanas de 4096 muestras desde la muestra 8000: máximo error absoluto observado de 0,042644243 cents, sin cambiar octava. Es simulación PCM, no precisión medida con guitarra.

## Pendiente físico

No se probó el Samsung S22 Ultra. Quedan guitarra real y exactitud Pro, sincronía acústica y respuesta táctil, legibilidad y comodidad de Defender, permisos y ciclo de vida en el teléfono. No se midió eficacia pedagógica. Guiado/entrenamiento del afinador y los incrementos posteriores de ritmo/Defender conservan su alcance en los tickets dependientes.

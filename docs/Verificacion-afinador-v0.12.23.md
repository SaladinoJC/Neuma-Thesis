# Afinador: cuerdas pulsadas breves — 0.12.23

Fecha: 12/11/2026. Candidata local código 38. Incluye 0.12.21/0.12.22.

## Observación y diagnóstico

El autor informa que, en Libre/Guiado/Oído con guitarra criolla, segunda Si/B
y primera Mi/E muestran la nota pero decaen antes de confirmarla; lo percibe
como cancelación de escucha. No hay mensaje de error ni nueva versión instalada
informados para este bloque. [Condiciones originales](Pruebas-manuales-S22-Ultra-2026-11-12.md).

Hipótesis comunicadas antes de intervenir: duración de evidencia de un segundo,
umbral de señal que recorta la cola y posible detención de captura. Pruebas
mediante VoiceCapture inyectado, ViewModel público y YIN real reprodujeron dos
patrones: nota válida de 500 ms sin confirmación y nota periódica decreciente
descartada antes de acumular evidencia. Ambas fallaron antes y pasan después.
En el primer patrón el micrófono permanece activo al perder señal; **no se
reprodujo una detención automática de captura por silencio**. Esto no identifica
por completo el fenómeno del teléfono ni mide el decaimiento de su guitarra.

## Cambio

- Política de app `tuner-plucked-v2`: **400 ms continuos** dentro del margen,
  en Libre/Guiado/Oído y revisión final. Se conservan ±5/±2 cents, octava exacta,
  reinicio al perder validez o salir de margen y corte de continuidad >250 ms.
- Perfil YIN exclusivo del afinador `yin-tuner-plucked-v1`: RMS mínimo **0,003**
  en vez de 0,01. Conserva periodicidad, rango, ventana y hop. Los módulos de voz
  no cambian. Valores experimentales del agente; falta comprobar sensibilidad
  y falsos positivos en el ambiente real.
- Libre conserva una **última confirmación** al decaer, identificada como anterior.
  La lectura vigente se vacía y `inTune` se desactiva: el silencio no suma tiempo.
  La memoria se borra ante lectura nueva no confirmada, cambio de cuerda/modo/
  configuración o parada. Oído no revela esa memoria en fases ocultas.
- Oído: si la nueva comprobación ya satisface afinación y estabilidad, avanza
  conservando `initialPassed=true`, sin atribuir una corrección asistida.
  Si falla, se conserva error inicial y se requiere señal nueva para corregir.
- Historial de nuevos recorridos registra permanencia de 400 ms, configuración
  YIN efectiva y `ear-check-v2-plucked`. Defaults históricos de resultados y
  bancos puros de 1 s se conservan; no se reevalúan intentos anteriores.

## Verificación

**308 JVM aprobadas**, cero fallos/errores/omitidas: seis nuevas respecto de 0.12.22.
Cubren pulsaciones breves, caída de señal sin parar micrófono, YIN con amplitud
decreciente, seis cuerdas en Guiado/Oído, silencio/huecos/desvíos, distinción de
última lectura, ruido, error inicial y corrección nueva. La prueba de drenaje
de referencia se ajustó a la duración de 400 ms y sigue verificando callbacks
viejos sin sumar evidencia. Historial comprueba parámetros efectivos.

El banco decreciente usa senos de 246,941651 y 329,627557 Hz, ventanas de 4096
muestras a 44.100 Hz y amplitud `0,025 × exp(-t/0,4 s)`, cada 50 ms durante
700 ms. Es una señal sintética, sin equivalencia calibrada con el instrumento real.

Lint **0 errores/17 advertencias**; ambos APK compilados. Sin ejecución Android,
emulador, nueva grabación, medición física o validación pedagógica. Presentación
Libre (señal activa/decayó), avance guiado y ocultación de Oído se revisaron por
código/JVM; dimensiones, texto ampliado y TalkBack nativos quedan pendientes.
Se reutilizan estilos, tokens y scroll; sin nuevos assets/dependencias/animaciones.

```powershell
# Desde Plataforma, con JDK/SDK configurados como en README
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

XML JVM/lint: `Plataforma/build/verification/2026-11-12-afinador-pulsadas/`.
[APK 0.12.23](../Plataforma/build/apk-archives/neuma-v0.12.23-afinador-cuerdas-pulsadas.apk),
15.338.424 bytes. SHA-256:
`f6424891d6da4c964239ff76df970c99190e70da0ea54b065d9faacad9087195`.
Paquete `ar.edu.unmdp.entrenamiento`, versión/código comprobados con aapt;
firma debug idéntica a la candidata previa, comprobada con apksigner.

## Reprueba física pendiente

Actualizar encima sin desinstalar. Seguir el bloque Afinador de la
[guía vigente](Guia-pruebas-manuales-S22-Ultra.md): una pulsación en Si y Mi
agudas por modo, después las demás cuerdas. Separar nota visible, confirmación,
avance del recorrido y micrófono activo. Probar también cuerda desafinada,
silencio y ambiente habitual para detectar falsas confirmaciones.

Si no alcanza la señal para 400 ms, volver a pulsar; no se suman pulsaciones
separadas ni se fija una nota únicamente porque haya aparecido un instante.
Guardar JSON del seguimiento y versión/condiciones. No atribuir a este banco
exactitud física ni solución confirmada en guitarra real.

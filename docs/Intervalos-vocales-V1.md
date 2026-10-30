# Intervalos vocales V1 — núcleo de imitación

Registro histórico de V1. La integración posterior en la aplicación se documenta
en [V2](Intervalos-vocales-V2.md); sus pendientes prevalecen al retomar.

Incremento técnico del 2026-10-30, retomado ante «continuá con lo que haya pendiente».
Se toma la prioridad vocal expresada por el usuario en la entrevista del informe,
confirmada durante esta sesión con «Seguir con el ejercicio vocal» frente a D6.
La ficha de la idea 1 sigue siendo el diseño de referencia; sus afirmaciones de
literatura no se usan como evidencia. V1 implementa únicamente la evaluación
relativa de una ronda manual de imitación. **Todavía no aparece como juego en la app.**

## Conducta implementada

`VocalIntervalSession`, en el paquete `voice`, recibe las estimaciones del YIN existente.
Primero se solicita sostener una nota ancla; después de adquirirla, una acción
explícita comienza la medición de la segunda nota. La separación permite practicar
unísono sin inferir ataques ni confundir la continuación de la primera nota con
la respuesta. El futuro controlador deberá terminar la reproducción antes de
abrir captura y ejecutar las acciones de sesión en un único contexto secuencial.

La relación se calcula como `1200 * log2(objetivoHz / anclaHz)`; el error resta
`100 * semitonosEsperados`. El registro absoluto del ancla no condiciona la
respuesta, dentro del rango de captura. No se reduce el resultado módulo octava:
una quinta cantada una octava más arriba conserva su error de +1200 cents.
Una segunda nota estable pero desafinada produce una ronda completa con
`inTune=false`; la ausencia de señal suficiente deja la ronda sin completar.

Cada medición conserva tiempo, etapa, frecuencia válida, desviación relativa y
motivo de rechazo. El resultado contiene la configuración, las frecuencias
representativas y una copia de esa serie. Interrumpir no inventa error ni acierto;
las entradas posteriores al cierre no cambian el resultado. Este núcleo no
escribe archivos ni modifica los ocho archivos del perfil.

## Parámetros provisionales del agente

| Parámetro | Valor inicial | Criterio operativo |
| --- | --- | --- |
| Sostenido | 500 ms | Ventana continua de estimaciones |
| Separación máxima | 100 ms | Un hueco mayor reinicia el sostenido |
| Estabilidad | 35 cents | Amplitud máxima entre extremos de la ventana |
| Tolerancia final | ±50 cents | Error relativo entre las dos medianas |
| Rango de frecuencia | 55–1000 Hz | Compatible con la configuración YIN existente |
| Intervalos | −12 a +12 semitonos | Ascendente, descendente y unísono |
| Límite de ronda | 60 s | Incluye espera entre etapas; requiere `tick` periódico |
| Límite de mediciones | 10.000 | Interrumpe sin conceder resultado musical |

La frecuencia representativa es la mediana en escala logarítmica. La ventana
abarca como mínimo 500 ms; con muestreo irregular conserva la muestra anterior
al borde, hasta una separación adicional. Silencio, frecuencia inválida y
discontinuidad temporal reinician la ventana. Tiempos negativos, repetidos o
anteriores al último evento se ignoran. El controlador debe llamar `tick` aun
sin muestras para aplicar el límite; utiliza el mismo reloj monotónico que la captura.

Estos valores no están calibrados con voces ni estudiantes. No se distingue
vibrato de desafinación. No se implementó todavía CA ni otra puntuación
pedagógica; se conserva el error firmado en cents.

## Uso del núcleo

```kotlin
val round = VocalIntervalSession(targetSemitones = 7)
// La referencia sonora debe haber terminado antes de comenzar.
round.beginAnchor()
// Callback de captura: round.accept(elapsedMillis, yinEstimate)
// Cuando phase == READY_TARGET, solicitar la segunda nota:
round.beginTarget()
// Continuar enviando estimaciones hasta COMPLETED o INTERRUPTED.
// Temporizador: round.tick(elapsedMillis); salida/pausa: round.interrupt().
val result = round.state.result
```

El ejemplo representa llamadas separadas por interacción y callbacks, no una
secuencia ejecutable sin muestras entre ellas. No pasar al núcleo la serie del
laboratorio visual, que se reduce a una lectura cada 200 ms: supera la separación
máxima. Usar las estimaciones de captura antes de ese filtrado de interfaz.

## Verificación reproducible

Desde `Plataforma/`, con JDK y SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests '*VocalIntervalSessionTest' --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Se verifica por la sesión pública: acciones y estimaciones → estado/resultado.
El recorrido sintético PCM → YIN → sesión se ejecuta en JVM; no acredita captura,
audibilidad, latencia ni precisión sobre voz real. Evidencia de host en
`docs/evidencias/intervalos-vocales-v1/` y logs locales en
`Plataforma/build/verification/2026-10-30-vocal-v1/`.

Cierre de host: **228 pruebas JVM sin fallos (10 nuevas)**, lint **0 errores y
10 avisos** en esta ejecución offline, app y AndroidTest ensamblados. Sin
ejecución Android. El caso PCM de cuarta produce 500,027915 cents: error
+0,027915 cents, exclusivamente para esa señal sintética. Se conserva el
snapshot de las fuentes probadas; frente al repositorio coinciden 351 de 352
archivos por SHA-256 y el restante difiere solo en una línea vacía final
preexistente en la copia aislada (`EarTunerApp.kt`). No se modificó ese componente.
Informe compilado en tres pasadas: 36 páginas, sin errores ni desbordes
horizontales; revisión visual de páginas físicas 25/26/29.

## Próximo incremento

Conectar reproducción/captura y una ronda jugable de imitación con instrucciones
de sostenido sin vibrato, permiso, cancelación y errores visibles; luego incorporar
persistencia/exportación y verificar el ciclo en Android cuando el usuario habilite
emulación. El DDA Mamdani, su inspección y la progresión por intervalo requieren
otro incremento. Los niveles 2/3, el currículum, ML y Oboe siguen fuera de V1.
No se adopta el promedio móvil con umbrales de la ficha como sustituto de Mamdani.
La discrepancia con el protocolo sobre Oboe/ML permanece registrada en
`Informe/contraste-protocolo.md`; este incremento no cambia el alcance académico.

No se modificaron pantallas, assets, marca, tokens ni animaciones. Sigue vigente
Music Garden; esta sesión no agrega evidencia visual ni de hardware. D6 de Defender
y las pruebas Android de incrementos anteriores conservan sus pendientes.

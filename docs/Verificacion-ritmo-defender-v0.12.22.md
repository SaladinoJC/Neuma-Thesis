# Rhythm Tap y presión de Defender — 0.12.22

Fecha: 12/11/2026. Candidata local Android, código 37. Incluye la mejora de
Instrumentos de 0.12.21. No instalada ni ejecutada por el agente en Android.

## Observaciones y diagnóstico

El autor informa en su pasada iniciada con 0.12.20: conteo visual desalineado
al subir BPM, dificultad con golpes rápidos y retraso audible de taps. Pide
calibración previa. Defender funciona según su observación, pero pide viaje
mínimo de 3 s y control de apariciones. [Condiciones y atribución](Pruebas-manuales-S22-Ultra-2026-11-12.md).

Hipótesis ordenadas comunicadas antes de probarlas:

1. Conteo a 60 BPM fijos: cambiar tempo hace divergir texto y timeline. **Confirmada**
   por prueba fallida a través del estado de sesión usado por la pantalla; corregida.
2. Pausar/recargar/reiniciar AudioTrack por tap: puede retrasar feedback. Se
   sustituyó esa ruta por SoundPool precargado. **Causa acústica no aislada ni
   magnitud verificada**: requiere comparación en teléfono.
3. Desfase constante de equipo/usuario: debería desplazar sistemáticamente
   clasificaciones, con mayor efecto relativo en ataques densos. **Reproducido
   con tiempos controlados de ±100 ms** y corregible mediante ajuste explícito;
   no demuestra que el desfase del S22 sea de ese valor ni que explique todos
   sus problemas. El pad pasa a ACTION_DOWN/ACTION_POINTER_DOWN originales,
   sin esperar liberación del dedo ni bucle de eventos de Compose.
4. Espera inicial de hasta 500 ms para obtener AudioTimestamp: puede consumir
   parte de un pulso rápido antes de mostrar la pantalla. Se incorpora una
   entrada silenciosa de 750 ms y estado de preparación antes del primer pulso.
   **No verificada acústicamente**. El fallback conserva incertidumbre desconocida.

No se añadieron logs temporales ni prototipos. No se atribuye reproducción
física resuelta a las pruebas JVM.

## Calibración explícita

La sección **Antes de tocar: calibración** precede a los controles del juego.
El usuario puede calibrar, volver sin cambiar el ajuste o jugar sin ajuste.
Se reproduce metrónomo a 80 BPM: cuatro pulsos de escucha y doce para tocar.
No hay sonido de feedback ni partitura objetivo que puntúe en esa actividad.

Cada contacto se asocia al pulso más cercano de los doce; se conserva un
contacto por pulso y se contabilizan duplicados. Se calcula mediana del desfase
firmado y mediana de desviaciones absolutas (MAD). Se requieren al menos 8/12
pulsos, |desfase| ≤250 ms, MAD ≤45 ms, al menos 75 % de muestras a ≤60 ms de la
mediana y no más de tres contactos duplicados. El corte ocurre medio pulso
después del último pulso esperado. Estos rangos son elecciones experimentales
del agente, pendientes de comprobación física/pedagógica.

Solo **Usar ajuste y volver** aplica el candidato. La compensación resta ese
desfase al tiempo relativo del contacto; no altera música, tempo, visualización
ni ventanas estrecha/amplia existentes. El cierre de omisiones y la cola final
respetan la compensación. Cada resultado guarda ajuste, método, cantidad, MAD
y desvíos de las muestras; cada tap conserva timestamp original y tiempo
relativo sin corregir y corregido. Versionado `rhythm-timing-v2-user-calibration`.

Se conserva en memoria durante la vida del ViewModel; volver al menú no lo
borra, pero un nuevo proceso o reinicio del perfil sí. Recalibrar al cambiar
salida. No se actualiza automáticamente con el desempeño ni cambia partidas
anteriores. Combina sincronización personal y equipo: **no mide por separado
latencia física, reacción ni precisión acústica**. Compensar evaluación no
elimina un eco audible de taps.

## Preparación de audio

Metrónomo PCM a frecuencia de salida nativa, con reserva de 48 kHz; AudioTrack
solicita modo de bajo retardo y conserva AudioTimestamp o fallback explícito.
El primer pulso sigue a 750 ms de silencio. El sonido de tap, seno propio de
35 ms/550 Hz, se genera en caché y carga en SoundPool antes de tocar; hasta
ocho streams disponibles, sin pausa/recarga por contacto. Stop libera ambos
recursos. No hay nueva biblioteca ni muestras externas.

La elección se apoya en las API primarias de
[SoundPool](https://developer.android.com/reference/android/media/SoundPool),
[AudioTrack](https://developer.android.com/reference/android/media/AudioTrack),
[AudioTimestamp](https://developer.android.com/reference/android/media/AudioTimestamp)
y [MotionEvent](https://developer.android.com/reference/android/view/MotionEvent).
Es una elección de implementación; esas API no garantizan una latencia medida
para este dispositivo. El WAV regenerable está inventariado, fuera del perfil.

## Defender

Viaje manual/DDA mínimo **3 s**, máximo 14 s, inicio de progresión 10 s y paso
de 1 s. Ventana/enfriamiento y reglas difusas se conservan; solo futuros
enemigos reciben el nuevo viaje. Práctica permite intervalo fijo **1–6 s**,
paso de 0,5 s, o curva automática existente de 4/3,5/3/2,5 s según tiempo activo.
La progresión conserva esa curva, sin heredar el intervalo manual. No se añade
adaptación del intervalo según desempeño: ese eje sigue siendo temporal.

Se conservan cupo de cuatro, restricciones de espacio y pausa sin ráfagas.
Configuración `defender-pressure-v2`, con intervalo incluido en resultado y
exportación. Los documentos históricos guardan valores explícitos y mantienen
su política original; pruebas de compatibilidad D3/D4 conservan mínimo antiguo.
El autor percibió el límite anterior como 5 s; el código fijaba 6 s.

## Verificación local y artefacto

- **302 JVM aprobadas**, cero fallos/errores/omitidas (ocho nuevas frente a 0.12.21).
- Regresiones rojo/verde: conteo a 60/90/120 BPM y métricas 2–4; ajuste de golpes
  densos con desfase ±100 ms; candidato de calibración; viaje de 3 s y aparición
  manual. Integración adicional: calibración cancelada, sin puntuar y aplicada
  solo explícitamente al siguiente resultado/historial; configuración de práctica
  no trasladada a progresión; conservación de políticas históricas.
- **Lint: 0 errores/17 advertencias**; APK de aplicación y pruebas Android compilados.
- No se ejecutaron pruebas Android, emulador, locución TalkBack ni mediciones físicas.
- Firma debug igual a 0.12.20/0.12.21; paquete/versión comprobados con aapt.

```powershell
# Desde Plataforma, con JDK/SDK configurados como indica README.md
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Respaldo local: `Plataforma/build/verification/2026-11-12-ritmo-defender/` (XML JVM/lint).
[APK 0.12.22](../Plataforma/build/apk-archives/neuma-v0.12.22-ritmo-calibracion-defender.apk),
15.338.424 bytes. SHA-256:
`2ddc908aba473aa8f375516e1ed8f99c41ee8a62c3566d324cd5b12db35aa437`.
Actualizar encima sin desinstalar. No cambia el esquema de documentos del perfil.

## Revisión visual y siguiente prueba

Se reutilizan tema, tokens, tipografías, pad, controles y FlowRow existentes;
papel/verdes, scroll, controles ≥48 dp, sin nueva ilustración o animación.
La calibración presenta preparación/conteo/toma/candidato/error/cancelación;
Defender permite ver y ajustar viaje/aparición en práctica. Resultado muestra
compensación aplicada. Notación estructurada se conserva. Se aplicó la orientación
de la skill de diseño al stack nativo y al sistema vigente, sin introducir
una estética web ni nuevas dependencias. Dimensiones 320/360/411 dp, texto 200 %,
TalkBack y capturas siguen pendientes por la restricción de no emular.

Ejecutar **R0/R1 y D1** de la [guía actualizada](Guia-pruebas-manuales-S22-Ultra.md).
Separar: números/metrónomo, cantidad de contactos capturados, clasificación de
golpes con/sin ajuste y eco audible con/sin sonido de taps. Guardar JSON y
condiciones. La mejora de latencia no se declara confirmada hasta ese contraste.

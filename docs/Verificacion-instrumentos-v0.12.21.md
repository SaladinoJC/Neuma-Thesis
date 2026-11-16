# Instrumentos: respuesta durante la melodía — 0.12.21

Solicitud del autor, 12/11/2026: tras probar 0.12.20 en S22 Ultra e informar
funcionamiento correcto, permitir responder antes del final de la melodía.
Sus observaciones originales se conservan en el [registro físico](Pruebas-manuales-S22-Ultra-2026-11-12.md).

## Comportamiento

Las opciones se habilitan al notificarse el inicio real del audio. Antes del
primer inicio no se acepta respuesta. Elegir cancela la reproducción, espera
su liberación bajo la exclusión de acciones existente y evalúa una respuesta.
El feedback no se reemplaza al finalizar/cancelar la escucha. No se superponen
reescuchas; la escucha iniciada cuenta como usada aunque se responda temprano.
La política DDA y la ausencia de penalización por tiempo se conservan.

Detener el audio al elegir es una elección técnica del agente. Se reutiliza
la cancelación del reproductor Android existente, que libera AudioTrack en
`finally`; no se incorporan dependencias ni muestras nuevas.

## Verificación local

Se siguió el ciclo rojo/verde: una nueva prueba de sesión falló por rechazar
respuesta durante PLAYBACK; tras habilitarla pasó. Una prueba del ViewModel
falló con el bloqueo anterior y pasó tras permitir la acción y cancelar audio.

- **294 pruebas JVM, cero fallos/errores/omitidas.** Las dos nuevas cubren sesión
  pública y flujo de diez preguntas: inicio real, respuesta anticipada, primera
  escucha y reescucha, doble toque, feedback, cancelación y resultado único.
- **Lint: cero errores y 17 advertencias**, sobre versiones de herramientas y
  dependencias, manifest/backup/orientación y recursos de launcher. No se
  atribuye la diferencia con recuentos históricos a este cambio sin comparación
  del mismo entorno y fecha.
- APK de aplicación y APK de pruebas Android compilados correctamente.
- Sin ejecución Android/emulador, instalación automática ni medición acústica.
- Certificado debug idéntico a 0.12.20, comprobado con `apksigner verify`.
- Paquete `ar.edu.unmdp.entrenamiento`, versión **0.12.21**, código **36**,
  comprobados en el APK con `aapt dump badging`.

Comando desde `Plataforma/`, con JDK/SDK configurados como en su README:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Respaldo local de XML JVM/lint: `Plataforma/build/verification/2026-11-12-instrumentos-respuesta/`.

## Entrega y comprobación física pendiente

[APK 0.12.21](../Plataforma/build/apk-archives/neuma-v0.12.21-instrumentos-respuesta-anticipada.apk),
15.305.652 bytes. SHA-256:
`26520e4954fcbd7d50598b6e9aeab0c5cca1ce9cc96b1ec3ccdf379ada2e5b1b`.
Actualizar encima de 0.12.20 sin desinstalar. No hay cambios de esquema de datos.

Seguir I1 de la [guía](Guia-pruebas-manuales-S22-Ultra.md): elegir durante primera
escucha y reescucha, comprobar corte intencional de audio, una respuesta por
pregunta, conteo de escuchas y resumen/historial. Incluir giro e interrupción.
Las observaciones ya informadas sobre 0.12.20 no validan automáticamente 0.12.21.

## Interfaz y dirección artística

Se reutilizan controles, medidas y estilos existentes y se añade el texto
«Podés responder mientras suena.». Sin assets, animaciones ni bibliotecas nuevas;
no corresponde alta en el manifiesto. No se modifica la notación. Se consultaron
dirección artística e inventario. El módulo conserva su estética azul oscura
heredada, una desviación existente de Music Garden que este cambio funcional
no resuelve. Pantallas/estados revisados mediante código y pruebas: preparación,
reproducción, respuesta, feedback y resumen; dimensiones, texto ampliado,
TalkBack y comportamiento visual/acústico en teléfono quedan pendientes.

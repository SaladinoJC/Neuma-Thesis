# Rhythm Tap: terraza Music Garden — 0.12.26

Fecha: 16/11/2026. Código 41. Segundo incremento del acabado transversal,
[#33](https://github.com/SaladinoJC/Tesis/issues/33), especificación
[#31](https://github.com/SaladinoJC/Tesis/issues/31).

El autor pide continuar con el siguiente incremento tras la entrega 0.12.25.
Esto autoriza avanzar con ritmo; no implica aprobación visual ni validación
en teléfono de Instrumentos. Ambas revisiones nativas siguen pendientes.

## Cambios del recorrido

- Preparación, calibración, historial, progreso y resultados Garden reutilizan
  papel, tokens y fuentes compartidos. La terraza es una primera composición
  vectorial original para probar, sin aprobación de arte final atribuida.
- Partitura sobre superficie limpia, con terraza estática de 80 dp y atril vacío
  como marco de la previa/ejecución. La notación real sigue siendo VexFlow desde
  datos estructurados. La decoración se omite en resultados y durante ejecución
  con texto superior al 130 %, para dar prioridad a la lectura.
- Preparación plegada al generar o terminar un ejercicio. El botón Preparación
  y calibración permite volver a las opciones sin cambiar el patrón por abrirlas.
- Historial y Progreso/DDA permanecen accesibles sin desplegar la preparación,
  fuera de la ejecución. Los avisos de guardado/error/reintento también quedan
  fuera del bloque plegado.
- Resultado breve con acceso al desglose completo, Nuevo ejercicio y Repetir
  mismo patrón. La nueva acción usa el generador existente, sin alterar sus reglas.
- Pad con borde, tintas/estados de tokens y altura mínima que puede crecer con
  el texto. Se conserva el bloque composable durante ejecución y resultados,
  con el mismo listener nativo incondicional para contactos entregados tarde.
- Quiet Studio conserva su paleta y no muestra terraza ni encuadernación Garden;
  comparte mejoras de preparación/acciones, paneles y crecimiento del pad.

Sesiones, audio, relojes/timestamps, evaluación, DDA, generador y persistencia
sin modificaciones. Sin nuevas fuentes, bibliotecas ni bitmaps. La escena es
estática y no añade una referencia temporal; el seguimiento musical existente
conserva su reducción de movimiento. No se añaden sonidos de interfaz ni
respuesta táctil; los sonidos musicales de metrónomo/taps existentes se conservan.
Personaje continúa pausado.

## Recursos y comprobación

Verificación local final: **308 JVM** ejecutadas sin fallos/errores/omitidas;
lint **0 errores**, 17 avisos Garden y 19 Quiet Studio. Ambos APK y AndroidTest
compilados; reconstrucción AndroidTest posterior a corregir el recorrido R2
correcta. Paquetes/versión 0.12.26/código 41 y firmas compatibles con 0.12.25
comprobados con aapt/apksigner. Hashes SHA-256 en `host-summary.json`.

MG-RHYTHM-TERRACE en `assets/ASSET_MANIFEST.md`; editable Kotlin en
`RhythmGardenInterior.kt`. MG-INTERIOR-PAPER reutilizado mediante
`RhythmPageScaffold`. `GardenPaperPanel` admite color de la variante, sin duplicar
componentes. Materiales, espaciado y radios proceden de tokens existentes.

La revisión de código Standards/Spec terminó sin hallazgos abiertos. Se corrigió
un recorrido de prueba R2 que consultaba el compás antes de desplegar preparación
tras repetir el ejercicio. No se atribuye a esta revisión evidencia nativa.

Se preparó `RhythmGardenAndroidTest`: previa a 320 dp/texto 200 % → desplegar
preparación → calibración sin iniciar audio → historial → progreso → retorno,
comprobando partitura conservada y cero inicios de audio. Los recorridos
instrumentados existentes acceden a la preparación plegable y expanden los
detalles cuando corresponde. Compilación no equivale a ejecución.

Evidencia duradera: `docs/evidencias/garden-ritmo-0.12.26/`. Copia del estado al
inicio, diff de este incremento y registros de herramientas:
`Plataforma/build/verification/2026-11-16-rhythm-garden/`.

## APK y reproducción

Garden: `Plataforma/build/apk-archives/neuma-garden-v0.12.26-ritmo.apk`.
Quiet Studio: `Plataforma/build/apk-archives/neuma-quiet-studio-v0.12.26.apk`.
Instalar encima de la variante correspondiente, sin desinstalar ni borrar datos.
Versión, firmas y hashes se registran en `host-summary.json` de la evidencia.

Desde `Plataforma/`:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:lintQuietStudio :app:assembleDebug :app:assembleQuietStudio :app:assembleDebugAndroidTest
```

## Pasada manual pendiente

1. Libro → Rhythm Tap. Revisar papel, instrucciones, práctica/progresión,
   tempo, compás, selección y calibración. Conservar los datos personales.
2. Generar: preparación plegada, terraza visible, partitura limpia y Play
   disponible cuando termine de cargar el grabado. Desplegar preparación sin
   regenerar; entrar/salir de calibración sin cambiar el patrón.
3. Volver de historial y progreso: comprobar mismo ejercicio/configuración.
   Verificar estados de carga, vacío y fallo/reintento con perfil de prueba.
4. Ejecutar: conteo al BPM real, partitura y pad accesibles, taps rápidos,
   metrónomo y sonido de taps. Interrumpir y repetir. Las medidas acústicas
   requieren registrar dispositivo, salida y método; esta sesión no las aporta.
5. Resultado: comprobar resumen, expansión de todos los detalles, guardado,
   historial, repetición exacta y Nuevo ejercicio. Revisar que los avisos de
   guardado no queden ocultos por la preparación cerrada.
6. Revisar 411/360/320 dp, texto 200 %, reducción de movimiento, teclado/TalkBack,
   contraste y altura del pad. La escena debe ceder espacio a la partitura.
7. Quiet Studio: revisar paleta propia y ausencia de decoración Garden, con
   los mismos recorridos funcionales.

Sin emulador, instalación ni ejecución Android en esta sesión. La legibilidad,
presentación, accesibilidad, acústica y comportamiento físico siguen pendientes.
No se acredita eficacia pedagógica. Los restantes cuatro incrementos del acabado
siguen pendientes; el siguiente es intervalos vocales #34. Horas personales de
la jornada pendientes en H0014, sin sumar duración de herramientas.

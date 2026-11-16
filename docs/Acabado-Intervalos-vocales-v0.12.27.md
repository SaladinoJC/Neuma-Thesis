# Intervalos vocales: claro de canto Music Garden — 0.12.27

Fecha: 16/11/2026. Código 42. Tercer incremento del acabado transversal,
[#34](https://github.com/SaladinoJC/Tesis/issues/34), especificación
[#31](https://github.com/SaladinoJC/Tesis/issues/31). El autor pide continuar con
«Dale, ponete con eso»; no implica aprobación visual ni validación física de
las candidatas anteriores.

## Recorrido y recursos

- Portada vocal propia: claro de canto con pequeño anfiteatro natural vacío.
  Sustituye la imagen de Instrumentos en el libro y se reutiliza en preparación.
- Claro estático durante referencia/captura; ausente en resultados y omitido
  durante práctica con texto >130 % para priorizar las indicaciones y controles.
- Preparación, estado de captura, devolución, historial y progreso con papel,
  tipografías y paneles compartidos. No se pliegan ni ocultan errores, permisos,
  guardado pendiente o acciones de recuperación.
- Mensajes de señal insuficiente y resultado conservados; ayuda de contexto
  visible durante toda la captura para evitar saltos por lecturas intermitentes.
  La ausencia de señal no genera una nueva clasificación musical.
- Imitación, Ancla dada y Producción libre conservan sus ayudas, controles,
  tolerancias y acceso por contenido. Solicitar permiso no inicia captura.
- `GardenPageScaffold` centraliza papel/áreas seguras; la delegación de ritmo
  conserva la superficie sin decoración de Quiet Studio. Las pantallas vocales
  compartidas se actualizan también allí; el catálogo Quiet no se rediseña.

Sin cambios de captura, referencias, YIN, sesiones, relojes, evaluación, DDA,
persistencia ni formatos de datos. Sin bibliotecas, fuentes, bitmaps o sonidos
nuevos. Sonidos de interfaz y respuesta táctil siguen pendientes; Personaje pausado.

MG-VOCAL-CLEARING se registra en `assets/ASSET_MANIFEST.md`: SVG editable
`assets/source/interiors/vocal-clearing.svg`, 768 × 512, 26 paths, exportado a
VectorDrawable mediante `python tools/prepare_vocal_clearing.py`. El exportador
comprueba colores contra `design-tokens.json`; exportación repetida idempotente.
Ilustración original, sin arte externo, texto, instrumentos ni notación musical.
La composición concreta es una primera versión para probar, sin aprobación final.

## Verificación y límites

Verificación final: **308 pruebas JVM** ejecutadas sin fallos/errores/omitidas;
lint **0 errores**, 17 avisos Garden y 19 Quiet Studio. Ambos APK y AndroidTest
compilados. Paquetes/versión 0.12.27/código 42 y firmas compatibles con 0.12.26
comprobados con aapt/apksigner. Se redujo el tamaño intrínseco del vector a
192 × 128 dp conservando viewport y tamaños de uso, resolviendo el aviso
VectorRaster sin suprimirlo. La comprobación final de recursos/lint/APK se
repitió después de ese ajuste; no se ejecutaron pruebas Android.

Se revisó la previa SVG en navegador y se conserva como `asset-preview.png` en
la evidencia. **Es una previa del recurso, no una captura Android.** No acredita
la composición de las pantallas ni accesibilidad en teléfono.

Revisión `code-review`: Spec sin hallazgos; Standards detectó una instrucción que
aparecía/desaparecía con cada lectura inválida. Se corrigió dejándola visible
durante la captura y se confirmó el cierre del hallazgo. No se añadió una prueba
que replique detalles decorativos; se reutilizan regresiones JVM y los recorridos
Android vocales existentes. La compilación de AndroidTest no equivale a ejecución.

Evidencia duradera: `docs/evidencias/garden-vocal-0.12.27/`. Copia de fuentes al
inicio y registros de herramientas: `Plataforma/build/verification/2026-11-16-vocal-garden/`.
Sin emulador, instalación ni ejecución Android. Revisión visual nativa, micrófono
físico, accesibilidad y acústica siguen pendientes. Sin eficacia pedagógica atribuida.

## APK y reproducción

Garden: `Plataforma/build/apk-archives/neuma-garden-v0.12.27-vocal.apk`.
Quiet Studio: `Plataforma/build/apk-archives/neuma-quiet-studio-v0.12.27.apk`.
Instalar encima de la variante correspondiente, sin desinstalar ni borrar datos.
Hashes SHA-256, paquetes y firmas se registran en `host-summary.json`.

Desde `Plataforma/`:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:lintQuietStudio :app:assembleDebug :app:assembleQuietStudio :app:assembleDebugAndroidTest
```

## Pasada manual pendiente

1. Libro → Intervalos vocales: comprobar portada propia y retorno al libro.
   Preparación legible, selección de ayuda/modo/intervalo/dirección y registro.
2. Imitación: escuchar referencia con micrófono detenido; iniciar primera captura
   explícitamente, sostener nota, confirmar primera, iniciar segunda y revisar
   resultado. Escena fuera del texto y sin referencias musicales adicionales.
3. Ancla dada: solo referencia del ancla y captura de objetivo; no introducir una
   primera captura adicional. Revisar historial y modo indicado en cada resultado.
4. Producción libre: sin escucha de referencia ni selector de registro; dos
   capturas con ancla cantada propia. Mismos bloqueos/progresión que antes.
5. Permiso rechazado, silencio, ruido, captura cancelada, tiempo agotado y segundo
   plano: mensajes explícitos, sin reinicio automático ni éxito musical inventado.
6. Resultado completo/interrumpido: escena omitida, devolución en papel, avance
   o nueva ronda, estado guardado/pendiente/reintento y retorno desde historial/progreso.
7. Revisar 411/360/320 dp, texto 200 %, reducción de movimiento, teclado/TalkBack
   y contraste. Comprobar que no se desplazan controles por aparición intermitente
   de la ayuda de señal y que la escena cede espacio al texto.
8. Recorrer ritmo en ambas variantes para comprobar áreas seguras equivalentes
   tras compartir scaffold. Quiet conserva ausencia de decoración en ritmo.

Los tres incrementos siguientes siguen pendientes: Defender #35, Simón #36 y
afinador/auxiliares/revisión transversal #37. Dedicación personal pendiente en
H0014, sin sumar tiempos autónomos de herramientas.

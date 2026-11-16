# Instrumentos: primer recorrido Music Garden — 0.12.25

Fecha: 16/11/2026. Código de versión 40. Incremento 1/6 de la especificación
confirmada: [#31](https://github.com/SaladinoJC/Tesis/issues/31), implementación
[#32](https://github.com/SaladinoJC/Tesis/issues/32). Candidata de revisión;
presentación nativa y acabado del invernadero pendientes de evaluación.

## Recorrido implementado

- Preparación y resultados sobre papel cálido, Fraunces/Nunito Sans, verdes y
  controles existentes de Music Garden. Imagen de preparación MG3-N-001 reutilizada.
- Invernadero vectorial estático durante escucha, respuesta y feedback, igual
  para todos los instrumentos. No añade pistas ni representa notación.
- Reescucha y respuesta durante audio conservadas. Opciones reorganizadas en
  una columna con poco ancho o texto grande, dos en el resto; contenido desplazable.
- Selección con marca en caja fija, semántica seleccionada y anillo de foco
  independiente; estados deshabilitados con tokens. Feedback textual además de color.
- Progreso visible, historial directo y retorno a Instrumentos conservando
  modalidad/nivel o resultado de origen. Inspección DDA como acción secundaria,
  también disponible durante la partida, sin alterar reglas.
- Historial compartido e inspector adaptados al tema claro; barras del sistema
  con iconos oscuros en estas superficies. Roles tipográficos compartidos completados.

Sin cambios de sesión, audio, DDA o formatos de datos. Personaje continúa
pausado; sonidos de interfaz y respuesta táctil siguen pendientes. No se
incorpora animación ambiental: escena estática y respuestas Material existentes.
Quiet Studio conserva paquete/perfil y catálogo/ritmo propios; hereda los cambios
en las superficies compartidas de Instrumentos e historial, sin migración general.

## Referencias y recursos

`GardenInterior.kt` conserva el editable de papel, invernadero y marca de
selección. Inventario: MG-INTERIOR-PAPER, MG-INSTRUMENT-GREENHOUSE y
MG-LEVEL-SELECTION en `assets/ASSET_MANIFEST.md`. Código original, sin arte
extraído ni bibliotecas, fuentes o bitmaps nuevos. La composición vectorial
es una primera versión nativa, no una ilustración final aprobada por el autor.

## Comprobaciones y límites

Evidencia duradera en `docs/evidencias/garden-instrumentos-0.12.25/`;
registro de compilación y copia de fuentes iniciales en
`Plataforma/build/verification/2026-11-16-garden/`.

Se agregó `InstrumentGardenAndroidTest`: preparación → selección manual C →
historial filtrado → retorno con selección conservada. Los tests instrumentados
existentes se adaptaron a controles desplazables. La compilación de AndroidTest
no equivale a ejecución de ese caso.

Verificación final: **308 pruebas JVM**, cero fallos/errores/omitidas; lint
**0 errores**, 17 avisos Garden y 19 Quiet Studio. APK Garden/Quiet Studio y
AndroidTest compilados. Paquetes y versión 0.12.25/código 40 comprobados con aapt;
firmas compatibles con las dos variantes 0.12.24 comprobadas con apksigner.
Los hashes SHA-256 se conservan en `host-summary.json`.

Revisión `code-review` contra copia al inicio de la tarea: Standards detectó y
se corrigieron geometría al seleccionar, foco y deshabilitado; Spec detectó y
se completó el inventario. Ambas revisiones confirmaron cero hallazgos abiertos
de los reportados. No son una revisión visual ni una medición de uso.

No se abrió emulador ni se instaló/ejecutó Android. Quedan pendientes revisión
visual nativa, texto ampliado, navegación con teclado/TalkBack, audio físico y
condiciones del teléfono. No se acredita eficacia pedagógica.

## APK y ejecución

Garden: `Plataforma/build/apk-archives/neuma-garden-v0.12.25-instrumentos.apk`.
Quiet Studio: `Plataforma/build/apk-archives/neuma-quiet-studio-v0.12.25.apk`.
Instalar encima de la variante correspondiente, sin desinstalar ni reiniciar datos.
Consultar hashes y firmas en la evidencia antes de transferirlos.

Desde `Plataforma/`, con JDK de Android Studio y SDK existentes:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:lintQuietStudio :app:assembleDebug :app:assembleQuietStudio :app:assembleDebugAndroidTest
```

## Pasada manual reproducible pendiente

1. Abrir Garden → Juegos → Instrumentos. Comprobar preparación sobre papel,
   opciones de modalidad/nivel y retorno al libro.
2. Elegir nivel C: selección visible sin salto de tamaño. Abrir Ver historial:
   filtro Instrumentos seleccionado; volver y comprobar modalidad/nivel conservados.
3. Iniciar partida. Comprobar invernadero sin pistas, escucha inicial, respuesta
   durante melodía, reescucha y cuatro opciones. El desplazamiento debe permitir
   alcanzar todos los controles sin cortar texto.
4. Responder bien/mal: identificar respuesta correcta y propia mediante texto,
   no solo color. Continuar; el scroll vuelve al inicio de la nueva pregunta.
5. Completar las diez preguntas, revisar puntaje, respuestas, progreso, historial
   y retorno al resultado. Probar Otra partida y volver al libro.
6. Abrir Inspección DDA en preparación y durante una partida. Revisar real y
   simulación; cerrar sin cambiar progreso ni resultado real por simular.
7. Revisar interrupción, error/reintento de lectura y estado vacío de historial
   con un perfil de prueba, sin borrar el perfil personal.
8. Repetir a 411/360/320 dp y texto 200 %, reducción de movimiento, teclado y
   TalkBack. Comprobar foco, tamaños táctiles y contraste sobre la escena real.

## Continuidad

El siguiente incremento es Rhythm Tap (#33), usando estos componentes donde
corresponda. Las otras cinco actividades/agrupaciones del acabado siguen
pendientes; esta candidata no equivale a completar toda la aplicación.
Tiempo personal de la jornada pendiente en H0014.

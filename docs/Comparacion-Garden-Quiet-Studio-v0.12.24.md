# Neuma Garden y Neuma Quiet Studio — comparación Android

Fecha: 12/11/2026. El autor solicita dos aplicaciones para comparar con su director
el 13/11 y acepta menú + Rhythm Tap. No constituye adopción definitiva ni evaluación
de usabilidad o eficacia pedagógica. Base funcional: 0.12.23, con todas las correcciones
de Instrumentos, ritmo, Defender y afinador de esta jornada.

## Instalación y demostración

Ambas son **0.12.24, código 39**, firmadas con la clave debug existente.

| Aplicación | APK en `Plataforma/build/apk-archives/` | Paquete y datos |
| --- | --- | --- |
| Neuma Garden | `neuma-garden-v0.12.24.apk` | `ar.edu.unmdp.entrenamiento`: actualiza Neuma existente y conserva su perfil. |
| Neuma Quiet Studio | `neuma-quiet-studio-v0.12.24.apk` | `ar.edu.unmdp.entrenamiento.quietstudio`: instalación independiente y perfil nuevo. |

Instalar ambos APK sin desinstalar Neuma. Garden conserva icono bosque; Quiet Studio
usa símbolo bosque sobre marfil. Los iconos temáticos de Android pueden homogeneizar
los colores: distinguir también por el nombre del launcher. No hay migración automática
entre perfiles ni selector de estética dentro de una app.

Recorrido sugerido: Garden → Juegos → Rhythm Tap → generar → Play → resultado;
luego Quiet Studio → Rhythm Tap → generar → Play → resultado → detalles. Usar igual
modo, BPM, métrica, células, salida y volumen. Los patrones generados y el progreso
son independientes: igual configuración no garantiza igual patrón aleatorio. Si se
aplica calibración, hacerlo en cada apertura de cada aplicación; ese ajuste sigue
siendo temporal y no se comparte. Mostrar otros juegos desde Garden para centrar
la comparación visual en el recorrido adaptado.

## Alcance implementado

- Garden conserva menú de libro/ilustraciones y paleta. Identificación en launcher
  y diálogo Más como Neuma Garden.
- Quiet Studio inicia en catálogo vertical desplazable, con cinco accesos directos,
  afinador y herramientas; no incorpora navegación de Personaje ni datos ficticios.
- Superficies cálidas de avena y cerámica, salvia grisácea, tinta oscura y acento de
  arcilla. Paleta provisional desde `design-tokens.json:quietStudio`, sin blanco puro.
- Rhythm Tap comparte sesión, ViewModel, audio, contactos nativos y grabado VexFlow.
  El mismo tema acotado llega a preparación, calibración, partitura, ejecución,
  resultado, historial y panel de progreso. Los demás interiores conservan su UI.
- Preparación plegable tras generar; notación sobre superficie cerámica y pad mate
  más amplio. Resultado breve con detalles desplegables; repetir y salir disponibles.
  No se cambian tiempos, evaluación, reglas difusas, desbloqueos ni historial musical.
- Sin animación ambiental, audio/háptica decorativos, librerías o fuentes nuevas.
  Fraunces/Nunito Sans existentes; notación musical siempre desde datos estructurados.

## Compilación y recuperación

Desde `Plataforma/`, con JDK de Android Studio y SDK existentes:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:lintQuietStudio :app:assembleDebug :app:assembleQuietStudio :app:assembleDebugAndroidTest
```

`debug` produce Garden (`app/build/outputs/apk/debug/app-debug.apk`);
`quietStudio` hereda debug, añade sufijo de paquete y produce
`app/build/outputs/apk/quietStudio/app-quietStudio.apk`. `BuildConfig.QUIET_STUDIO`
selecciona únicamente la presentación. Para regenerar paletas Compose:
`python tools/prepare_quiet_tokens.py` desde la raíz.

Antes de editar se conservó el snapshot completo de `app/src`, Gradle, tokens y
acuerdo visual, incluidos archivos sin seguimiento:
`Plataforma/build/visual-backups/neuma-pre-quiet-studio-0.12.23.zip`.
SHA256: `9150148b09ab9b976e35402cb1ef2004534059020fd2917f00746a39877cdf4f`.
Referencia Git: `687bfa9d5957c27b7c252f67b7ac97e958613c0a`; **HEAD solo no incluye
los cambios locales de esta jornada**. La recuperación completa usa el ZIP sobre
una copia separada del proyecto, evitando sobrescribir cambios posteriores.
Para ejecutar Garden no hace falta restaurar: ambas presentaciones siguen en código.
El APK 0.12.23 archivado conserva además la entrega previa exacta.

## Verificación y límites

Resultados reproducibles y hashes finales en
`Plataforma/build/verification/2026-11-12-garden-quiet-studio/`.
Se verifica compilación de ambos APK, identificación, firma, pruebas JVM y lint.
Resultado: **308 JVM aprobadas**, sin fallos, errores ni omitidas. Lint en cada
variante: **0 errores**, **17 avisos en Garden y 19 en Quiet Studio**. Los mensajes
de Quiet ya están presentes en Garden, con ubicaciones adicionales de recursos.
APK de pruebas Android compilado, sin ejecución.
Ambos paquetes: versión 0.12.24/código 39, nombres de launcher comprobados con aapt.
Firma SHA256 común: `4e9a10f84ec8218fb0927606a0c84617c3753a452e0c772b1bae1bd377a0c90a`.
Hashes SHA256 de artefactos:

- Garden: `6bf15b35b0c5cf3e5810f23bcf843f4b636ae32993d8f500a3cfd7044e8f8ce5`.
- Quiet Studio: `7927a378e2cdd2a40ae5a50897c702278a725488c0362f30b6d773c31b348279`.

La comparación con el snapshot confirma que sesiones, audio, DDA y almacenamiento
conservan su código previo; el único cambio Kotlin fuera de UI es el contraste de
las barras del sistema en `MainActivity`.

Contrastes calculados sobre tokens Quiet: texto/fondo 11,34:1; secundario/fondo
5,59:1; secundario/superficie 6:1; texto del botón/fondo primario 6,95:1;
error/fondo 5,51:1. Son cálculos de color, no mediciones de legibilidad en teléfono.
Controles conservan mínimo táctil de 48 dp; catálogo, preparación y detalles
desplazables; títulos y descripciones pueden crecer sin altura fija de fila.

**Sin ejecución Android, capturas nativas ni emulador.** La referencia web existente
se inspeccionó como orientación; no se presenta como captura de esta variante.
Pendiente antes de la reunión en S22 Ultra: instalar ambas y comprobar que Garden
conserva datos, abrir catálogo, entrar/salir de cada juego, calibrar, generar, tocar,
abrir detalles/repetir/historial, volver al menú y cerrar/reabrir. Revisar también
320/360/411 dp, texto 200 %, TalkBack, reducción de movimiento y contraste de barras.
No se atribuye validación física a las pruebas JVM ni a la compilación.

La desviación visual respecto de Music Garden está autorizada solo para Quiet Studio
y este recorrido. La paleta y acabado concretos son propuestas revisables del agente;
la comparación con el director aún no ocurrió.

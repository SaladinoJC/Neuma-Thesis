# Herramientas: procedencia, configuración y distribución del contenido

Fecha: **2026-11-04**. Capítulo 5 redactado con `thesis-writing` después de
`grill-me` → `grilling`, Q1–Q9 y síntesis confirmada con «ok».

## Declaraciones y decisiones del autor

- Q1–Q3: mantener procedimientos y responsabilidades en Metodología; concentrar
  identificación, función y selección de herramientas en el capítulo 5. Incluir
  aplicación, investigación, organización, relevamiento y escritura. Distinguir
  elecciones explícitas del autor de propuestas del agente aceptadas y revisadas.
- Q4/Q8: Gemini Deep Research desde Gemini al inicio; Claude Code CLI para comenzar
  desarrollo; migración a Codex CLI; actualmente Codex y GrokBot en simultáneo.
  Claude y GPT utilizados desde sus agentes de terminal también para discusión y
  escritura. No atribuir uso de sus sitios web ni contribuciones exclusivas.
- Modelos declarados: «Opus 5.5» y «GPT 6.1 Sol», con variantes más livianas para
  tareas sencillas y razonamiento alto para diseño, planificación y tareas complejas.
  Se conservan como recuerdo de uso del autor, no como inventario de versiones
  reconstruido mediante logs o comparación de rendimiento. La documentación de
  los productos respalda su función general, no estas asignaciones históricas.
- Q5/Q7: experiencia Android limitada, previamente con React Native. Elección
  Kotlin/Compose por acceso directo a capacidades Android, control buscado de
  latencia, interacción/juegos y posible teclado MIDI. Motivos confirmados por
  el autor; no comparación experimental con React Native. MIDI no se implementó
  como parte de este incremento ni se presenta como funcionalidad existente.
- Q6: secciones breves y tabla de configuración relevante. Comandos extensos y
  procedimientos de instalación permanecen en documentación técnica/anexos.
- Q9: el autor pide omitir la herramienta exploratoria mencionada inicialmente.
  No se incorpora al capítulo ni se le atribuyen entregas.

Las declaraciones complementan los ADR 0001/0002 del 02/10; no modifican las
decisiones de plataforma ni acreditan aprobación del director.

## Evidencia del repositorio consultada

| Bloque | Evidencia | Alcance |
| --- | --- | --- |
| Kotlin/Compose y plataforma | `docs/adr/0001-plataforma-android.md`, `0002-kotlin-compose-y-dda-mamdani.md` | Decisiones confirmadas del 02/10; audio C++ pendiente de evidencia |
| Versiones de configuración | `Plataforma/build.gradle.kts`, `app/build.gradle.kts`, `gradle/wrapper/gradle-wrapper.properties` | Neuma 0.12.20, Kotlin 2.2.10, BOM 2025.08.01, Lifecycle 2.9.2, coroutines 1.10.2, serialization 1.9.0, AGP 8.11.0, Wrapper 8.14.3, JVM 17, API 26/36/36, dependencias de pruebas |
| Audio y algoritmos propios | `voice/AndroidVoiceCapture.kt`, `voice/AndroidReferencePlayer.kt`, `voice/YinEstimator.kt`, `dda/Mamdani.kt` bajo el paquete de la app | AudioRecord/AudioTrack, captura con corrutinas, YIN y Mamdani propios; sin medición nueva |
| Persistencia | `MainActivity.kt`, `storage/JsonResultsFile.kt`, adaptadores `Json*Results` | Archivos en `filesDir`, serialización y exportación; sin afirmar comparación formal con bases de datos |
| Notación y recursos | `docs/Rhythm-Tap-lectura-lateral.md`, `assets/ASSET_MANIFEST.md`, recursos locales `assets/rhythm/` de la app | VexFlow 5.0.0, SVG/WebView local y licencias MIT/OFL; resto de la app nativo |
| Ilustraciones | `assets/ASSET_MANIFEST.md`, `Plataforma/tools/prepare_garden_assets.py` | Generación integrada de ilustraciones estáticas y exportación Python; marca aportada por el autor, sin atribuir todo el arte a IA |
| Construcción y pruebas | `Plataforma/AGENTS.md`, `Plataforma/tools/verify-prototype.ps1`, Gradle y guías de incrementos | JVM/Android, lint, reportes y hashes; sin nuevas pruebas de aplicación |
| Organización | `docs/agents/issue-tracker.md`, historial Git, `docs/Entrega-GitHub-v0.12.20.md` | Git/GitHub/Issues/gh y entregas; no describe un proceso de PR no utilizado |
| Encuesta | `docs/encuesta-estudiantes-musica/` y declaración del autor del 04/11 | Forms preparados para director; script conservado como apoyo, ejecución completa remota no acreditada |
| Escritura | `Informe/configuracion.tex`, `compilar.ps1`, `LEEME.md`, `tools/extraer_referencias.py` | LaTeX/XeLaTeX, MiKTeX, BibLaTeX/Biber, APA, Markdown, PowerShell, Python y pdftotext |

Las versiones son las declaradas en la configuración examinada, no un registro
histórico de todo el entorno. Android Studio aparece como proveedor de JDK en
scripts; no se atribuye uso cotidiano del IDE sin declaración del autor.

## Fuentes primarias consultadas el 04/11

Páginas oficiales abiertas para verificar funciones. Las versiones concretas
proceden de archivos locales; la documentación actual no sustituye ese registro.
Se agregan 13 entradas bibliográficas, además de reutilizar `androidAudioRecord`.

| Clave | Fuente | Uso y límite |
| --- | --- | --- |
| `androidCompose` | [Android Developers: Get started with Jetpack Compose](https://developer.android.com/develop/ui/compose/documentation) | Interfaz Android declarativa; no comparación de latencia con React Native |
| `androidComposeBom` | [Android Developers: Use a Bill of Materials](https://developer.android.com/develop/ui/compose/bom) | Conjunto de versiones de bibliotecas; no confundir versión BOM con una única versión Compose |
| `androidAudioRecord` | [Android Developers: AudioRecord](https://developer.android.com/reference/android/media/AudioRecord) | Lectura de audio de entrada; ya citada en el informe |
| `androidAudioTrack` | [Android Developers: AudioTrack](https://developer.android.com/reference/android/media/AudioTrack) | Reproducción mediante búferes PCM; no acredita latencia de Neuma |
| `androidArchivosPrivados` | [Android Developers: Access app-specific files](https://developer.android.com/training/data-storage/app-specific) | Archivos internos de aplicación; no equivale a cifrado ni conservación tras desinstalación |
| `androidPruebasLocales` | [Android Developers: Build local unit tests](https://developer.android.com/training/testing/local-tests) | Pruebas en JVM de host; resultados Android diferenciados |
| `vexflowDocumentacion` | [VexFlow: HTML5 Music Engraving](https://vexflow.com/) | Grabado musical JavaScript, SVG/Canvas; integración específica documentada localmente |
| `gradleWrapper` | [Gradle: Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) | Distribución declarada; no garantiza por sí sola identidad binaria. La página versionada 8.14.3 no abrió, se utiliza la actual para el concepto |
| `kotlinCorrutinas` | [JetBrains: Coroutines guide](https://kotlinlang.org/docs/coroutines-guide.html) | Suspensión, contextos y tareas asíncronas; no mejora medida de rendimiento |
| `kotlinSerializacion` | [JetBrains: Serialization](https://kotlinlang.org/docs/serialization.html) | Serialización JSON; biblioteca con versión independiente de Kotlin |
| `latexIntroduccion` | [The LaTeX Project: An introduction to LaTeX](https://www.latex-project.org/about/) | Preparación de documentos técnicos; configuración APA contrastada en proyecto |
| `geminiDeepResearch` | [Google: Use Deep Research in Gemini Apps](https://support.google.com/gemini/answer/15719111?hl=en) | Investigación asistida; las fases históricas proceden del prompt y declaraciones |
| `anthropicClaudeCode` | [Anthropic: Overview](https://code.claude.com/docs/en/overview), [CLI reference](https://code.claude.com/docs/en/cli-usage) | Terminal, archivos y comandos; la bibliografía cita Overview. Sin atribuir modelos o versiones históricas desde la página actual |
| `openaiCodexCli` | [OpenAI: Codex CLI](https://learn.chatgpt.com/docs/codex/cli) | Terminal, archivos y comandos; URL developers.openai.com/codex/cli redirige a esta página oficial |

No se añaden referencias externas para GrokBot ni se infieren su implementación,
modelo o capacidades: se conserva la declaración de uso del autor.

## Distribución para evitar duplicación

- Capítulo 3: conceptos y fundamentos, con YIN/DDA y antecedentes.
- Capítulo 4: proceso, fases de investigación, SDD/harness, supervisión, contraste
  entre asistentes y estrategia de evaluación. Se retira la enumeración de
  asistentes y se enlaza a `sec:herramientas-ia`.
- Capítulo 5: identificación, función, motivos de elección y configuración.
- Capítulos 9/10: arquitectura, reglas y detalles de implementación.
- Capítulo 11: casos, ejecución y resultados, sin trasladar recuentos al 5.

El nombre de una herramienta puede aparecer donde sea necesario para entender un
procedimiento; se evita repetir su presentación y justificación completas.

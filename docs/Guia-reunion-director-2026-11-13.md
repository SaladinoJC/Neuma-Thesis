# Neuma: guía para explicar el proyecto al director

Preparada el 13/11/2026 a partir del código local 0.12.24/código 39, el estado del prototipo y los registros de verificación. Es material de preparación, no un acta de acuerdos ni una evaluación nueva. Los números de pruebas se toman de ejecuciones documentadas; no se volvieron a ejecutar para elaborar esta guía.

## 1. Presentación en un minuto

> Estoy desarrollando Neuma, una aplicación Android de práctica musical mediante juegos serios. El aporte de ingeniería que propongo evaluar es integrar captura y análisis vocal, retroalimentación musical y adaptación de dificultad en el dispositivo. Ya tengo cinco actividades funcionales, un afinador complementario, un motor difuso reutilizable e historiales exportables. Separé las reglas de los juegos de la interfaz para poder verificarlas con entradas controladas. Ahora necesito cerrar el alcance con usted, medir precisión y respuesta temporal, comprobar las correcciones en el teléfono y realizar sesiones exploratorias de uso. Todavía no afirmo que el sistema mejore el aprendizaje.

El destinatario principal propuesto es quien estudia música y tiene conocimientos básicos, incluida la lectura que requiere cada actividad. La app complementa otras formas de aprendizaje; no implementa un curso desde cero.

## 2. Qué hay y qué se propone entregar

| Actividad | Qué hace hoy | Dificultad y progreso |
| --- | --- | --- |
| Simón | Reproduce secuencias con piano y pide repetirlas; escala pentatónica, diatónica mayor o cromática. Termina al primer error. | La secuencia crece al acertar; existe evaluación de dominio y desbloqueo entre partidas. No confundir crecimiento de secuencia con una adaptación continua de todos sus parámetros. |
| Instrumentos | Identificar timbres escuchando melodías; diez preguntas por partida. Se puede responder desde el inicio real de la reproducción. | En automático adapta las escuchas disponibles para la siguiente pregunta y registra evidencia para desbloquear niveles. |
| Rhythm Tap | Genera un ejercicio de cuatro compases desde categorías rítmicas; presenta la partitura y evalúa contactos sobre un pad. | Progreso por categoría; adapta el tempo entre ejercicios. El BPM no cambia durante una ejecución. |
| Defender | Lectura de notas y acordes en claves de sol/fa, con objetivos que avanzan hacia una base y respuesta por nombre. Incluye tríadas e inversiones. | Adapta el tiempo de viaje de futuros objetivos y conserva progresos por contenido/variante. Piano como forma de respuesta pendiente. |
| Intervalos vocales | Escuchar y cantar, cantar un objetivo con ancla dada o producir dos notas sin referencia. Evalúa distancia entre notas y muestra devolución. | Ajusta tolerancia para la siguiente ronda; series y progreso por intervalo, dirección y ayuda. La elección entre ayudas sigue siendo explícita. |
| Afinador complementario | Libre, guiado por cuerdas y entrenamiento de oído; referencias y seguimiento. | Criterios Normal/Pro fijos; no tiene DDA. |

El alcance de entrega propuesto por el autor el 04/11 reúne **intervalos vocales, instrumentos, Rhythm Tap y Defender**, sujeto al director. Simón está en revisión y el afinador es complementario. Tener código de una actividad no equivale a comprometer su evaluación y acabado completos en la tesis.

Dos compromisos propuestos aún pendientes: **piano en Defender** y **selección automática de ayuda vocal**. Personaje, armario y recompensas cosméticas tienen diseño documentado, pero su implementación está pausada.

## 3. Mapa del repositorio

| Ruta | Para qué sirve |
| --- | --- |
| `Plataforma/` | Proyecto Android ejecutable y herramientas de preparación/verificación. |
| `Plataforma/app/src/main/java/ar/edu/unmdp/entrenamiento/` | Fuentes Kotlin de la aplicación. |
| `ui/` | Pantallas y componentes Compose; también algunos ViewModels. |
| `simon/`, `instruments/`, `rhythm/`, `defender/` | Reglas, modelos y coordinación de cada juego; la ubicación de ViewModels no es totalmente uniforme. |
| `voice/` | Captura, estimación YIN, intervalos vocales y progresión vocal. |
| `tuner/` | Modos y configuración del afinador. |
| `dda/` | Cálculo difuso Mamdani común. |
| `audio/` | Adaptadores de reproducción Android. |
| `storage/`, `progress/`, `history/` | Infraestructura de perfil, progreso e historial; algunos repositorios específicos están junto a su actividad. |
| `Plataforma/app/src/main/assets/` | Muestras de audio y recursos locales de notación. |
| `Plataforma/app/src/main/res/` | Recursos Android: ilustraciones, fuentes, iconos y estilos. |
| `Plataforma/app/src/test/` | Pruebas locales JVM. |
| `Plataforma/app/src/androidTest/` | Pruebas instrumentadas para Android. |
| `docs/` | Protocolo, planificación, investigación, especificaciones y registros de prueba. No todo representa decisiones vigentes. |
| `Informe/` | Fuente LaTeX por capítulos, bibliografía y trazabilidad de escritura. |
| `CONTEXT.md`, `docs/adr/` | Vocabulario común y decisiones de arquitectura con su origen. |
| `.agents/estado-prototipo.md` | Estado vigente y antecedentes; comenzar por las entradas recientes. |
| `design-tokens.json`, `assets/ASSET_MANIFEST.md` | Valores visuales compartidos e inventario/procedencia de recursos. |

Hay un único módulo Gradle, **`:app`**. Son separaciones internas de responsabilidades, no microservicios ni aplicaciones cliente/servidor.

## 4. Cómo explicar la arquitectura

El recorrido habitual es:

**Acción en pantalla → ViewModel → sesión/reglas → nuevo estado → pantalla.**

El ViewModel coordina además audio, operaciones asíncronas y guardado; las dependencias concretas se proporcionan desde la composición de la aplicación.

- **Compose:** describe la interfaz a partir del estado. Por ejemplo, el botón para responder depende de la fase de la pregunta.
- **ViewModel:** coordina acciones y recursos durante el ciclo de vida. No debería confundirse con el algoritmo musical.
- **Sesión:** aplica reglas y transiciones. Por ejemplo, decide si una respuesta es correcta, cuándo termina una partida y qué resultado corresponde.
- **Adaptador:** conecta una necesidad de la sesión con Android, como capturar audio o reproducir una muestra.
- **Repositorio de resultados:** ofrece lectura/guardado sin obligar al núcleo a gestionar archivos directamente.

Puede describirse como una separación práctica de estilo MVVM, con sesiones de dominio. No hace falta presentarla como una implementación estricta de Clean Architecture. La inyección de dependencias es manual, mediante constructores o funciones, sin un contenedor adicional.

**Ventaja comprobable:** en las pruebas se puede sustituir reloj, azar, audio y almacenamiento. Así se evalúan acciones y resultados sin depender de tocar a tiempo o cantar frente al teléfono.

**Límite actual:** `MainActivity` concentra composición, navegación y coordinación; separar responsabilidades no significa que todo esté modularizado físicamente o que no haya deuda de mantenimiento.

## 5. Por qué Android, Kotlin y Compose

La decisión registrada es Android como plataforma exclusiva y desarrollo nativo para acceder a sus capacidades y mantener control del audio y la interacción. Kotlin es el lenguaje; Compose construye la interfaz; coroutines organizan trabajo asíncrono. Gradle compila y reúne dependencias.

Configuración actual: Android mínimo API 26, compilación/objetivo API 36 y bytecode JVM 17. No hay que memorizar todas las versiones: importa poder explicar cada responsabilidad.

**Pregunta probable: «¿Por qué no React Native o una web?»**

> Priorizamos Android y acceso directo a sus servicios. El desarrollo nativo reduce capas que necesitaría integrar para este alcance. Es una elección de diseño; todavía no hice una comparación experimental de latencia que demuestre superioridad frente a otras alternativas.

La WebView de Rhythm Tap no convierte el proyecto en una aplicación web: solo representa la partitura con VexFlow y recursos empaquetados. Generación, contactos, reloj y evaluación permanecen en Kotlin.

## 6. Audio y estimación de tono

### Recorrido implementado

1. `AudioRecord` captura audio mono PCM de 16 bits.
2. La captura trabaja en una coroutine sobre `Dispatchers.IO`.
3. Se normalizan muestras y se arman bloques superpuestos.
4. `YinEstimator` busca periodicidad y obtiene frecuencia fundamental.
5. El juego decide si hay evidencia estable y cómo se compara musicalmente con el objetivo.

La configuración vocal base es 44.100 muestras/s, ventana de 4096 muestras y salto de 1024, rango de prueba 55–1000 Hz. La ventana representa unos **92,9 ms** y el salto unos **23,2 ms**. Son duraciones calculadas, **no latencia medida** de la app. El costo del cálculo tampoco equivale a demora entre cantar y ver la devolución.

### YIN explicado sin fórmulas

Una nota periódica tiene un patrón que se repite. El estimador compara la señal con copias desplazadas, busca un desplazamiento que la haga parecerse a sí misma y lo interpreta como período. Cuanto más corto el período, más agudo el sonido. La frecuencia resulta de dividir la frecuencia de muestreo por ese período.

El código incluye diferencias, normalización, umbral e interpolación. Es **una implementación basada en YIN**, con decisiones propias; no reproduce todas las etapas del artículo ni hereda sus resultados publicados. No usa una red neuronal ni un modelo entrenado.

El nivel RMS permite descartar señal insuficiente; no identifica una voz ni mide decibeles calibrados. El indicador de periodicidad tampoco es una probabilidad calibrada de acertar la nota.

### Qué se evalúa en intervalos

Si el usuario canta dos notas, se compara su relación: `1200 × log2(f2/f1)`. Si canta ambas un poco más agudas conservando la distancia, el intervalo puede seguir siendo correcto. Desplazar solo una una octava sí cambia la respuesta. Con ancla dada se compara el objetivo cantado contra la referencia reproducida.

Un semitono equivale a 100 cents. Hay tres criterios distintos: aceptar una señal, considerarla estable y aprobar la afinación musical. Una nota estable puede ser musicalmente incorrecta.

La adquisición vocal experimental exige 500 ms de sostenido, con condiciones de continuidad y dispersión. La tolerancia adaptativa empieza en 50 cents y cambia hasta 5 por ronda dentro de 30–80 cents. Son parámetros para evaluar, no umbrales pedagógicos validados.

El ajuste del afinador de 0.12.23 es independiente: 400 ms y RMS mínimo 0,003 para admitir mejor cuerdas pulsadas; conserva Normal ±5/Pro ±2 cents. La corrección requiere reprueba con guitarra real.

### Qué queda fuera o pendiente

La entrada prevista es monofónica. No se implementó separación de voz entre acompañamientos ni reconocimiento general de acordes desde micrófono. C++/Oboe y ML no integran la solución actual: deben justificarse si las mediciones muestran necesidad. El protocolo los planteaba con más fuerza; hace falta acordar esa diferencia con el director, según `Informe/contraste-protocolo.md`.

## 7. DDA y lógica difusa

**DDA** significa ajuste dinámico de dificultad. El sistema toma evidencia reciente y propone modificar una exigencia; después aplica límites y condiciones particulares del juego.

El motor común usa **Mamdani**:

1. Convierte números en grados de pertenencia: un desempeño puede ser parcialmente medio y parcialmente alto.
2. Activa reglas explícitas según esas pertenencias.
3. Combina sus consecuencias.
4. Obtiene una salida numérica por centroide.
5. La política del juego la transforma en una acción limitada.

El código utiliza funciones trapezoidales, mínimo para AND/implicación, máximo para agregación e integración trapezoidal del centroide. La configuración predeterminada muestrea 1001 puntos. Si faltan entradas válidas o no existe área de salida, devuelve diagnóstico sin centroide.

**Ejemplo real de Instrumentos:** observa las últimas tres respuestas; calcula exactitud y autonomía. Autonomía es la proporción de aciertos resueltos en la primera escucha entre todos los aciertos. Si ambas son altas, puede reducir la ayuda. Cambia como máximo una escucha, mantiene entre una y tres y deja dos preguntas entre cambios. No modifica la pregunta ya presentada ni penaliza el tiempo de respuesta.

| Juego | Evidencia usada | Ajuste |
| --- | --- | --- |
| Instrumentos | Exactitud y autonomía de escucha. | Escuchas de la pregunta siguiente. |
| Intervalos vocales | Error absoluto y dispersión del sostenido final. | Tolerancia de la ronda siguiente. |
| Rhythm Tap | Precisión de ataques/silencios y control de golpes extra atribuibles. | Tempo y progreso por categoría entre ejercicios. |
| Defender | Solidez que combina supervivencia/precisión, y rapidez relativa. | Tiempo de viaje de futuros objetivos; los visibles conservan el suyo. |

**Adaptar y desbloquear son distintos:** bajar temporalmente la exigencia no quita contenidos ya habilitados. La práctica manual queda separada de la evidencia que concede avance.

**Pregunta probable: «¿Por qué difusa en vez de reglas por umbral o ML?»**

> Permite expresar criterios lingüísticos y revisar qué reglas participaron en cada ajuste. Comparte matemática entre actividades sin imponerles la misma política. No sostengo que sea óptima: los rangos y reglas son experimentales y falta evaluar su comportamiento con usuarios.

«Dominio alto» es una categoría operativa del sistema, no una acreditación de dominio musical. Los inspectores y escenarios simulados sirven para explicar decisiones; no representan estudiantes reales.

## 8. Ritmo y tiempos de entrada

El modelo rítmico representa duraciones racionales, figuras, silencios y ligaduras. De los mismos datos salen partitura, ataques esperados y evaluación. Una continuación ligada no crea un ataque adicional. VexFlow realiza el grabado local.

El evaluador distingue adelantos/atrasos, omisiones y golpes extra. Los contactos nativos conservan su timestamp original: la animación no decide cuándo tocó el usuario. En Defender también importa el momento original de la respuesta, no cuándo terminó de procesarse.

En 0.12.22 se corrigió un conteo que seguía a 60 BPM, se revisó la entrada rápida y se precargaron taps en SoundPool. **Son cambios implementados y comprobados localmente; su efecto acústico requiere reprueba física.**

La calibración ofrece cuatro pulsos de escucha y doce de toma a 80 BPM. Propone la mediana del desfase, verifica regularidad y pide aplicación explícita. Combina sincronización personal y equipo: no aísla latencia del hardware. Vive en memoria y debe repetirse al cambiar salida de audio o reiniciar el proceso.

Repetir un mismo ejercicio sirve para practicar, pero no debe contarse como evidencia nueva independiente para subir dificultad. Una huella del contenido permite distinguirlo aunque cambie su identificador.

## 9. Datos, privacidad y recuperación

La aplicación funciona localmente y no requiere un servidor para los recorridos implementados. El manifiesto solicita micrófono y no declara permiso de Internet. El micrófono es opcional para usar las otras actividades.

Se conservan documentos JSON por actividad y configuración, sin Room/SQLite. Se registran resultados, contexto, decisiones y mediciones derivadas; **no grabaciones de voz**. Un perfil local no equivale a una cuenta ni demuestra que lo use una sola persona.

La escritura prepara archivos temporales e intenta reemplazo atómico, con alternativa ordinaria. Un mutex por perfil coordina operaciones. Identificadores y revisiones permiten reintentar sin conceder dos veces el mismo progreso. No hay garantía absoluta ante cualquier cierre o fallo: resultados pendientes pueden existir solo en memoria.

- **JSON exportado:** datos interpretados con configuración y procedencia para análisis externo.
- **ZIP:** copia de archivos originales, incluso incompatibles o dañados, como respaldo; no es importación automática.
- **Reinicio:** borra el perfil con confirmación en la app; un marcador permite completar un reinicio interrumpido. No borra exportaciones externas.

La ausencia de grabaciones no vuelve automáticamente anónima toda información exportada: deben revisarse campos y condiciones antes de recolectar datos de participantes.

## 10. Diseño visual y variantes

**Garden:** identidad Music Garden/Botanical Storybook, papel cálido, verdes, ilustración botánica 2D y menú de libro. Fraunces/Nunito Sans, tokens compartidos, controles legibles y reducción de movimiento.

**Quiet Studio:** comparación autorizada en menú/catálogo y recorrido completo de Rhythm Tap, con superficies cálidas y menos ornamentación. Los demás interiores conservan su presentación actual. No es una migración aprobada de toda la app.

Ambas variantes son 0.12.24 y se instalan a la vez gracias a paquetes distintos. Garden actualiza el perfil existente; Quiet Studio tiene perfil nuevo independiente. Comparten sesiones, audio, DDA y almacenamiento. No hay selector de tema dentro de una sola app ni sincronización de perfiles.

La comparación sirve para conversar sobre adecuación al público, legibilidad y jerarquía de controles. No constituye un estudio controlado: igual configuración puede generar patrones diferentes. La notación siempre procede de datos musicales; las ilustraciones no sustituyen partituras.

## 11. Qué evidencia existe

Última verificación documentada de 0.12.24: **308 pruebas JVM aprobadas**, lint con **0 errores**, 17 avisos Garden/19 Quiet Studio; ambos APK y el APK de pruebas Android compilados. **Las pruebas Android de esta candidata no se ejecutaron.** Compilar una prueba no equivale a pasarla.

Hay pruebas Android ejecutadas sobre versiones anteriores, pero no acreditan automáticamente ampliaciones posteriores. Las pruebas locales comprueban reglas y componentes con entradas controladas; no prueban audibilidad, latencia física ni eficacia pedagógica.

El 12/11 el autor informó buen funcionamiento de Simón, Instrumentos y Defender en la pasada iniciada con 0.12.20. También informó problemas de ritmo y fijación de cuerdas agudas; las correcciones posteriores requieren nueva pasada física. Los registros no afirman cobertura exhaustiva ni validación de la última variante.

Para la evaluación faltan criterios acordados de precisión de tono, tasa de señal aceptada y respuesta temporal, condiciones de medición y sesiones exploratorias. La encuesta de necesidades está preparada; no se atribuyen respuestas o resultados. Evaluar facilidad de uso no demuestra aprendizaje o transferencia.

## 12. Preguntas incómodas que conviene poder responder

**«¿Cuál es el aporte propio si ya hay afinadores y juegos musicales?»**

La propuesta es integrar análisis vocal, evaluación musical, adaptación configurable y trazabilidad dentro de una app local; hacer explícitas las decisiones y evaluarlas. No se afirma haber inventado YIN o Mamdani ni demostrar novedad mundial sin una comparación bibliográfica suficiente.

**«¿Cómo sabés que la dificultad está bien elegida?»**

Hoy se sabe que la política ejecuta reglas verificables. La adecuación de parámetros todavía requiere calibración y observación; las simulaciones no responden por sí solas esa pregunta.

**«¿Qué pasa con ruido, silencio o voz inestable?»**

Se filtran señales inadmisibles y se exige estabilidad antes de evaluar. Eso puede dejar un ejercicio sin completar y no debe interpretarse como error musical. Falta caracterizar tasas de aceptación y errores con condiciones reales registradas.

**«¿Qué pasa si salís de la aplicación?»**

Las sesiones distinguen interrupciones; la captura se cancela y libera recursos. Operaciones tardías se invalidan para no afectar una sesión nueva. La conservación exacta depende del recorrido y del guardado confirmado; no se promete reanudación después de terminar el proceso.

**«¿Cómo usaste herramientas de IA?»**

Explicar el uso real documentado: investigación asistida, especificaciones, propuestas de código y revisión cruzada, con supervisión y decisiones del autor. Mostrar una cadena concreta: observación en teléfono → caso de prueba → corrección → verificación → reprueba pendiente. No atribuirse escritura manual de cada línea ni presentar la salida de un modelo como validación.

**«¿Ya está listo para publicarse?»**

Hay APK de demostración firmados en debug. No equivale a una distribución de producción ni a publicación final del código. Licencia y publicación efectiva están pendientes, según el contraste del protocolo.

**«¿Cómo va el informe?»**

Existe base LaTeX por capítulos y redacción de contexto, metodología, tecnologías, factibilidad, planificación, requerimientos, diseño e implementación. Pruebas necesita consolidación; evaluación y conclusiones dependen de evidencia aún pendiente. El número de páginas no es porcentaje de avance ni aprobación académica.

## 13. Demo y acuerdos buscados

Recorrido sugerido, ajustado a lo que se haya comprobado en el teléfono:

1. Presentar propósito y alcance en un minuto.
2. Mostrar Instrumentos y una decisión de adaptación real; si se usa un escenario preparado, identificarlo como simulado.
3. Mostrar intervalos: referencia, respuesta vocal, devolución y resultado. Es la integración central; un fallo de señal también permite explicar el límite actual.
4. Mostrar historial y cómo una decisión conserva sus entradas/configuración.
5. Comparar Garden/Quiet Studio en Rhythm Tap con condiciones semejantes, sin confundir preferencias visuales con eficacia.

Buscar acuerdos concretos sobre alcance final, criterios/método de medición, diferencia frente a C++/ML del protocolo, participantes y sesiones, prioridades pendientes y próxima revisión. No hace falta resolver todo mediante más funcionalidades.

## Referencias internas para seguir estudiando

- [Estado vigente](../.agents/estado-prototipo.md).
- [Vocabulario](../CONTEXT.md) y [decisión Kotlin/Compose/Mamdani](adr/0002-kotlin-compose-y-dda-mamdani.md).
- [Ejecución y guías técnicas](../Plataforma/README.md).
- [Diseño del sistema](../Informe/capitulos/09-diseno.tex) e [implementación](../Informe/capitulos/10-implementacion.tex).
- [Contraste del protocolo](../Informe/contraste-protocolo.md).
- [Prueba física informada](Pruebas-manuales-S22-Ultra-2026-11-12.md).
- [Comparación 0.12.24 y evidencia](Comparacion-Garden-Quiet-Studio-v0.12.24.md).
- [Estado del informe](../Informe/seguimiento.md).

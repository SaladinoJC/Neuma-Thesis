# Dictado rítmico y escucha de conjuntos: propuestas

**Origen:** segunda entrevista Q1–Q13 del marco teórico, síntesis confirmada con
«ok perfecto», 2026-11-02. **Estado:** acuerdos para especificación posterior;
sin implementación, tickets nuevos ni resultados de evaluación de estas actividades.
Registro: [guía de entrevista](../Informe/guia-ritmo-timbre-lectura.md).

## P1. Dictado rítmico por selección de partitura

El estudiante escucha un patrón y elige la representación correcta entre varias
partituras. Se prioriza identificación con reescuchas permitidas; no se pide
escritura libre. Falta definir catálogo/origen, generación o banco, opciones,
reproducción, ayudas, feedback y registro. No se acordaron número de opciones,
límites de escucha ni DDA del dictado.

Consultar generador, catálogo, representación y audio existentes al especificar.
Comprobar equivalencias entre opciones: una grafía distinta puede representar el
mismo ritmo. Mantener correspondencia entre sonido y respuesta.

**El ejercicio rítmico actual conserva lectura a primera vista y ejecución mediante
taps, sin modelo sonoro previo.** El autor rechazó agregar esa ayuda durante la
entrevista; su experiencia de escuchar y repetir no autoriza a modificar Rhythm Tap.

## P2. Instrumentos y funciones en fragmentos de conjunto

El autor aportará canciones propias o de uso permitido, precargadas en la aplicación,
y marcará los instrumentos presentes en el tiempo. Conservar origen, permiso/licencia
y anotaciones al incorporar material. Esta sesión no añade grabaciones ni declara
autorizado un archivo concreto.

Se sortearán fragmentos dentro de **tramos aptos**, con etiquetas coherentes y
objetivos audibles/evaluables. Seis segundos es un ejemplo, no duración final.
Dos pasos aceptados: identificar instrumentos y asociar sus funciones dentro del
fragmento, admitiendo varias cuando corresponda. El banco necesita también esas
anotaciones; distinguir instrumento, función y figuración como arpegio al definirlas.

Se comienza con pocos instrumentos claros y se avanza hacia conjuntos más complejos.
El usuario solicita **DDA**, con desempeño separado de identificación instrumental
y reconocimiento de funciones. La adaptación puede seleccionar fragmentos posteriores
y ayudas según sus etiquetas; no se definió modificar la mezcla durante reproducción.

### Para especificar

Catálogo de instrumentos/funciones/figuraciones, cambios temporales, tramos aptos,
duración y balance de muestreo, opciones/distractores, respuestas parciales,
reescuchas/feedback, adaptación, progresión, historial y exportación. Revisar la
reutilización del motor Mamdani vigente; no hay umbrales ni funciones nuevas aprobadas.

Se evalúa lo que el estudiante identifica en una mezcla anotada. Esta propuesta
no incorpora separación computacional de fuentes ni altera la delimitación de
captura vocal monofónica. Composición, arreglos y transcripción motivan su diseño;
su transferencia requiere fundamentación y evaluación propias.

## Retomar implementación

Consultar fichas, especificaciones de ritmo/instrumentos, estado y código antes de
crear componentes. Completar un incremento acotado y sus tareas en el issue tracker
según `docs/agents/issue-tracker.md`. La confirmación de la entrevista no acredita
implementación ni selecciona el próximo ticket de software. El alcance académico
se mantiene sujeto al acuerdo con el director.

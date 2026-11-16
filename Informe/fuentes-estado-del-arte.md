# Estado del arte: fuentes, selección y extracción

Consulta complementaria: **2026-11-03**. El autor solicita continuar el estado
del arte después de condensar el marco teórico. Se emplea `thesis-writing` y se
mantienen tres subsecciones y una tabla comparativa. Este registro documenta
una selección focalizada para redactar; no es un protocolo sistemático exhaustivo
ni reconstruye la ejecución histórica de los borradores.

## Procedencia y preguntas de comparación

Se revisan candidatos de `docs/InvestigacionSistematica.md`, especialmente las
referencias de fase 2 y fase 3. Tras la respuesta del autor se consulta también
`docs/Investigación App Entrenamiento Musical.md`, con el estado del arte y sus
referencias. Ambos aportan candidatos coincidentes, sin considerarlos evidencia
independiente de las publicaciones originales. No se comprobó equivalencia literal.
El archivo sistemático mezcla propuestas metodológicas,
matrices narrativas y afirmaciones que requieren comprobación. Se preservan
ese original y su representación HTML, sin atribuir equivalencia entre versiones.
La búsqueda de artefactos locales no localizó planillas independientes de selección
o exportaciones RIS/CSV: las planillas XLSX encontradas corresponden a planificación.
Esto no demuestra inexistencia de registros fuera del repositorio. El autor
identifica los dos Markdown mencionados y autoriza investigar lo que sea necesario;
se conservan como insumos de la búsqueda y no se atribuye a esa respuesta la
entrega de planillas o exportaciones adicionales.

Preguntas de extracción:

- ¿Qué habilidad y respuesta musical aborda el antecedente?
- ¿Qué sistema, algoritmo o conjunto de datos presenta?
- ¿Cómo interviene la adaptación y con qué propósito?
- ¿Qué evaluación se informa y qué conclusiones permite para la comparación?

Se incluyen sistemas musicales, métodos de análisis vocal y adaptación relacionados
con esas preguntas, con pasajes primarios consultables. La documentación comercial
se limita a prestaciones declaradas. Se dejan fuera afirmaciones sin procedencia
comprobada y materiales redundantes, sin interpretar esas decisiones como una
evaluación completa de calidad de todo el corpus histórico.

## Consultas efectivamente realizadas

Herramienta web: búsqueda general y apertura de páginas de editoriales, actas,
autores y responsables de productos. Consultas textuales:

1. `SingPAD adaptive singing pitch auditory training paper`
2. `"Music Puzzle" Spiegel 2017`
3. `"Serious Games for Music Education" 2013`
4. `"Steady the Drums" storytelling Pesek`
5. `"Computerised Adaptive Beat Alignment Test" 2018 DOI`
6. `"pYIN" Mauch Dixon 2014 pdf`
7. `"CREPE" Kim Salamon Li Bello 2018 pdf`
8. `site.earmaster.com ear training microphone adaptive exercises`
9. `"The Effects of Musical Experience and Hearing Loss" "Spiegel"`

Se siguieron los enlaces originales del borrador a iClef, SingPAD y CREPE y las
referencias localizadas mediante esas consultas. Perfect Ear se contrastó en su
sitio oficial. No se consultaron de forma estructurada índices completos ni se
aplicó un intervalo temporal formal. No se reportan conteos PRISMA o tasas de
inclusión para este recorrido de navegación. El resultado de búsqueda no sustituyó
la consulta del pasaje original utilizado.

## Fuentes incorporadas y localización de la evidencia

| Clave BibLaTeX | Fuente primaria y pasajes consultados | Extracción utilizada y límite |
| --- | --- | --- |
| `barate2013iclef` | Baratè y Ludovico (2013), [PDF editorial](https://www.scitepress.org/papers/2013/43649/43649.pdf), pp. 234–237, especialmente §§4–5 | Lectura de claves, dispositivo móvil y cuestionarios de aceptación. La publicación propone ampliar la evaluación de eficacia |
| `znidersic2025storytelling` | Žnideršič, Pregelj y Pesek (2025), [texto editorial](https://www.frontiersin.org/journals/virtual-reality/articles/10.3389/frvir.2025.1674655/full), §§3–6; métodos, desempeño, Tapping-PROMS y experiencia | Dos versiones activas de entrenamiento rítmico. Se distingue valoración hedónica de ejecución y no se atribuye retención a largo plazo |
| `mauch2014pyin` | Mauch y Dixon (2014), [PDF de Queen Mary](https://webspace.eecs.qmul.ac.uk/s.e.dixon/pub/2014/MauchDixon-PYIN-ICASSP2014.pdf), resumen, §§2–3 y evaluación | Candidatos probabilísticos y trayectoria temporal; no evalúa aprendizaje ni despliegue en el teléfono de Neuma |
| `kim2018crepe` | Kim, Salamon, Li y Bello (2018), [PDF del coautor](https://www.justinsalamon.com/uploads/4/3/9/4/4394963/kim_crepe_icassp_2018.pdf), resumen, arquitectura y evaluación | Estimación convolucional sobre forma de onda y pruebas con ruido; no se trasladan sus cifras de precisión al proyecto |
| `harrison2018cabat` | Harrison y Müllensiefen (2018), [texto editorial](https://www.nature.com/articles/s41598-018-30318-8?error=cookies_not_supported&code=0), resumen, Test Design y discusión | Percepción del pulso, adaptación psicométrica y límites de contexto. No se presenta como entrenamiento mediante DDA |
| `zhang2024singpad` | Zhang y colaboradores (2024), [actas completas en HTML](https://educationaldatamining.org/EDM2024/proceedings/2024.EDM-short-papers.30/index.html), Data Collection, Data Cleaning/Annotation y Validation; [BibTeX oficial](https://educationaldatamining.org/EDM2024/proceedings/2024.EDM-short-papers.30/2024.EDM-short-papers.30.bib) | Canto, anotación por nota y predicción de respuestas. Se distingue el conjunto de datos SingPAD de la plataforma SingMaster |
| `perfectearFunciones` | Crazy Ootka Software AB, [Perfect Ear](https://www.perfectear.app/), Perfect Ear features y pie de autoría | Prestaciones declaradas. Se excluyen reseñas de usuarios, eslóganes y afirmaciones de eficacia |
| `earmasterCloud` | EarMaster, [EarMaster Cloud for education](https://www.earmaster.com/products/ear-training-sight-singing/earmaster-cloud-edition.html), presentación y Topics covered | Práctica auditiva/vocal y gestión docente. Sin inferir eficacia, algoritmo de adaptación, precios o funcionamiento sin conexión |

### Metadatos y acceso

Se agregan ocho referencias, hasta 43 entradas citadas en el informe. Los DOI de
pYIN, CREPE y el artículo de Frontiers se contrastaron mediante Crossref. Los
intentos de consulta Crossref de iClef y CA-BAT no produjeron respuestas utilizables;
se emplearon los datos de sus originales/editoriales. SingPAD se registra con
el DOI de Zenodo y las páginas 332–340 del BibTeX oficial de las actas, sin asumir
que el DOI corresponda al conjunto de datos descargado. No se descargó ni ejecutó
ese conjunto de datos.

Se preservan la autoría real de Žnideršič/Pregelj/Pesek, las grafías y diacríticos,
y el nombre Matthias Mauch. iClef corresponde a la comunicación CSEDU de dos
autores, distinta del artículo de tres autores *Development of Serious Games
for Music Education* (2013), localizado pero no necesario en esta selección.
Las fuentes comerciales se registran sin fecha de publicación y con fecha de acceso;
no se interpreta el copyright del sitio como fecha de las prestaciones actuales.

La caché regenerable `build/fuentes-estado-arte/` conserva tres PDF (iClef,
pYIN y CREPE), sus extracciones, el HTML de SingPAD/Frontiers y su BibTeX. El primer
enlace de pYIN falló por verificación TLS; se utilizó otro dominio institucional,
sin desactivar la verificación. El PDF de iClef respondió en descarga local aunque
su apertura web devolvió 403. Nature respondió con una página de verificación en
local; el texto se consultó mediante la herramienta web con el parámetro editorial
de cookies indicado. No se afirma disponer de copia local íntegra de CA-BAT.
`accesos.json` documenta los primeros intentos; la descarga posterior de pYIN
se identifica por su archivo. Los fallos de descarga no se confunden con consulta
solo de resúmenes cuando se accedió al texto por otra vía.

## Cotejo y correcciones frente al borrador

Se registra lo siguiente sin modificar el documento histórico:

- El diseño de *Steady the Drums!* compara versiones con/sin narrativa. No se
  importa la afirmación del borrador sobre mejoras «a largo plazo». Se revisaron
  los métodos y los distintos resultados antes de sintetizar.
- SingPAD es un conjunto de datos para contrastar modelos predictivos; no se
  importa su clasificación como una intervención adaptativa de eficacia demostrada.
- CA-BAT evalúa percepción mediante selección adaptativa. Se conserva la distinción
  entre medir capacidad y regular la experiencia de entrenamiento.
- No se trasladan las descalificaciones generales del borrador sobre aplicaciones
  comerciales, ni supuestas ausencias universales de canto, adaptación o validación.
- *Music Puzzle* se buscó como candidato, pero no se incorporó: en esta consulta
  no se verificaron sus atribuciones con un original suficiente. El segundo
  documento facilitó el enlace MDPI 2076-3417/7/12/1278, cuya apertura y PDF
  devolvieron 429. Esto no constituye una conclusión sobre su valor o sobre la
  existencia de otros estudios.
- Se localizó otra comunicación de 2025 sobre narrativa y ritmo del mismo equipo;
  se eligió el artículo editorial identificado, sin contar dos publicaciones como
  dos muestras independientes.

La interpretación comparativa y el planteo del aporte de Neuma son elaboración
del proyecto. Se evita afirmar que la integración sea inédita o que un método
sea superior sin una comparación pertinente. El estado del arte queda como
primera síntesis focalizada para revisión del autor y eventual ampliación.

## Pendientes de reconstrucción

Recuperar registros originales de búsqueda, fechas, criterios y exclusiones,
resolver la equivalencia de versiones del borrador y ampliar antecedentes si
esa evidencia lo justifica. Este registro no reemplaza la revisión histórica ni
da por cerrada la metodología. Los fundamentos de memoria secuencial de Simón
siguen como pendiente específico del marco, sin impedir esta primera comparación.

## Revisión editorial posterior — 2026-11-03

Se refuerza el vínculo entre cada antecedente y las decisiones de Neuma, y se
presenta el aporte como integración y evaluación de ingeniería. La selección,
las fuentes y la tabla se conservan; no hay nuevas consultas, resultados o
comparaciones de superioridad. Los límites específicos siguen en los estudios
y en la tabla, y el alcance pedagógico se reúne en la síntesis del marco.

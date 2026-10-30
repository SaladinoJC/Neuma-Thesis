# 2026_06_05_Apuntes revisión sistemática

- Original: `../2026_06_05_Apuntes revisión sistemática.pdf`
- SHA-256 del original: `d916496035ef05c92786e79390206601fa53e6290506f24e0636619ed6fc5557`
- Páginas PDF: 22. La numeración es física, desde 1; puede diferir de la impresa o del sumario.
- Extracción automática con `pdftotext -layout -enc UTF-8`. Los bloques conservan la disposición del texto; no reproducen imágenes.
- Ante dudas sobre tablas, fórmulas, diagramas o formato, consultar el PDF. Este archivo es una copia de consulta y se regenera; editar el original o las notas, no esta extracción.

## Página PDF 1

```text
        UNIVERSIDAD NACIONAL DE     FACULTAD DE INGENIERÍA
             MAR DEL PLATA




                            Apunte:

         Revisión Sistemática de la
                 Literatura




CARRERA INGENIERÍA EN INFORMÁTICA
DEPARTAMENTO DE INGENIERÍA EN INFORMÁTICA
Asignatura: Trabajo Final
Profesor: Leonel Guccione
```

## Página PDF 2

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                                                Revisión Sistemática de la Literatura




Sumario
Revisión Sistemática............................................................................................................................................................................ 4
  Definición.............................................................................................................................................................................................4
  Definición conceptual.................................................................................................................................................................... 4
  Características principales..........................................................................................................................................................4
  Objetivo de una revisión sistemática...................................................................................................................................... 4
  Etapas típicas de una revisión sistemática..........................................................................................................................4
  Descripción de las etapas............................................................................................................................................................5
      1. Planificación............................................................................................................................................................................ 5
          Definición del problema de investigación................................................................................................................ 5
          Formulación de las preguntas de investigación................................................................................................... 5
          Construcción del protocolo............................................................................................................................................ 5
      2. Búsqueda de estudios........................................................................................................................................................6
          Selección de fuentes.........................................................................................................................................................6
          Construcción de las cadenas de búsqueda............................................................................................................6
          Ejecución de la búsqueda............................................................................................................................................... 6
      3. Selección de estudios.........................................................................................................................................................7
          Eliminación de duplicados...............................................................................................................................................7
          Aplicación de criterios de inclusión............................................................................................................................ 7
          Aplicación de criterios de exclusión........................................................................................................................... 7
          Selección del material.......................................................................................................................................................7
             Nivel 1: título.................................................................................................................................................................... 7
             Nivel 2: resumen............................................................................................................................................................ 7
             Nivel 3: texto completo...............................................................................................................................................7
      4. Evaluación de calidad.........................................................................................................................................................8
          Objetivos.................................................................................................................................................................................8
          Criterios habituales............................................................................................................................................................8
          Sistema de puntuación.................................................................................................................................................... 8
      5. Extracción de datos.............................................................................................................................................................8
          Diseño de formularios de extracción..........................................................................................................................8
          Información típica............................................................................................................................................................... 9
          Importancia............................................................................................................................................................................9
      6. Síntesis de resultados........................................................................................................................................................9
          Síntesis descriptiva............................................................................................................................................................9
          Síntesis temática................................................................................................................................................................ 9
          Identificación de vacíos..................................................................................................................................................10
          Meta-análisis (cuando corresponde)........................................................................................................................ 10
          Elaboración de conclusiones....................................................................................................................................... 10
  Uso de planillas para la Selección del material................................................................................................................10
      Planilla A: Caracterización del estudio...........................................................................................................................10
      Planilla B: Extracción de datos para responder las preguntas de investigación..........................................11
          Paso 1: Revisar las preguntas de investigación....................................................................................................11
          Paso 2: Definir las columnas de extracción............................................................................................................11
          Paso 3: Leer cada estudio buscando evidencia....................................................................................................11
          Paso 4: Registrar información de forma consistente........................................................................................12
          Paso 5: Registrar evidencia, no opiniones..............................................................................................................12
          Paso 6: Incorporar citas o referencias internas................................................................................................... 12
          Paso 7: Extraer únicamente información relevante............................................................................................13
          Paso 8: Preparar la planilla para el análisis posterior.......................................................................................13
  Secuencia lógica para el diseño de la planilla B.............................................................................................................. 13

                                                                                                   2
```

## Página PDF 3

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                                             Revisión Sistemática de la Literatura

     1. Tema de investigación.......................................................................................................................................................13
     2. Preguntas de investigación............................................................................................................................................ 14
     3. Diseño de la Planilla B...................................................................................................................................................... 14
  Lo que ocurre durante la lectura del texto completo (Nivel 3)...................................................................................14
  La regla más importante............................................................................................................................................................. 15
  Otro ejemplo..................................................................................................................................................................................... 15
  Una recomendación práctica.................................................................................................................................................... 16
  Estructura sintética de una Revisión Sistemática..........................................................................................................16
     Título............................................................................................................................................................................................. 16
     Resumen...................................................................................................................................................................................... 17
     Introducción................................................................................................................................................................................17
     Objetivo de la investigación................................................................................................................................................18
     Método..........................................................................................................................................................................................18
         Planificación........................................................................................................................................................................ 18
         Identificación de las necesidades que motivan esta revisión.......................................................................19
            Estado actual de la temática.................................................................................................................................19
            Preguntas de investigación...................................................................................................................................20
            Selección de bases de datos................................................................................................................................20
         Definición de un Protocolo de revisión................................................................................................................... 20
         Definición de los criterios de Inclusión y Exclusión...........................................................................................21
         Preparación de los datos susceptibles al análisis..............................................................................................21
     Resultados.................................................................................................................................................................................. 21
     Conclusiones.............................................................................................................................................................................22
     Referencias................................................................................................................................................................................22




                                                                                                  3
```

## Página PDF 4

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                   Revisión Sistemática de la Literatura


Revisión Sistemática
Definición
Una revisión sistemática (RS) es un método de investigación científica utilizado para identificar,
evaluar y sintetizar de manera rigurosa y transparente la evidencia disponible sobre una pregunta
de investigación específica, siguiendo un protocolo previamente definido y reproducible.


Definición conceptual
Una definición ampliamente utilizada en ingeniería de software y ciencias aplicadas es la propuesta por
Barbara Kitchenham:
      Una revisión sistemática es un medio para identificar, evaluar e interpretar toda la
      investigación disponible relevante para una pregunta de investigación particular, área
      temática o fenómeno de interés.


Características principales
Una revisión sistemática se distingue de una revisión narrativa tradicional porque:
    1. Parte de una pregunta de investigación claramente definida
       (por ejemplo: ¿qué prácticas de programación orientada a objetos se aplican en el desarrollo de
       videojuegos educativos?).
    2. Utiliza un protocolo explícito
       Define previamente:
               bases de datos a consultar
               cadenas de búsqueda
               criterios de inclusión y exclusión
               proceso de selección de estudios.
    3. Sigue un proceso replicable y transparente
       Otro investigador podría repetir la revisión y obtener resultados similares.
    4. Evalúa la calidad de los estudios incluidos
       Se analizan aspectos metodológicos de cada artículo.
    5. Sintetiza la evidencia científica
       Puede realizarse mediante:
               síntesis cualitativa
               meta-análisis cuantitativo (cuando los datos lo permiten).


Objetivo de una revisión sistemática
El propósito principal es obtener una visión completa y confiable del conocimiento existente sobre
un tema, evitando sesgos derivados de seleccionar estudios de manera arbitraria.


Etapas típicas de una revisión sistemática
Generalmente el proceso incluye:
    1. Planificación

                                                     4
```

## Página PDF 5

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                   Revisión Sistemática de la Literatura

                definición de la pregunta de investigación
                elaboración del protocolo.
    2. Búsqueda de estudios
                consulta en bases como Scopus, IEEE, ACM, Web of Science.
    3. Selección de estudios
                aplicación de criterios de inclusión/exclusión.
    4. Evaluación de calidad
                análisis metodológico de los trabajos.
    5. Extracción de datos
                registro de información relevante de cada estudio.
    6. Síntesis de resultados
                análisis comparativo y conclusiones.


Descripción de las etapas
1. Planificación
La planificación constituye la etapa fundacional de la revisión sistemática. Su objetivo es diseñar el
estudio antes de comenzar la búsqueda bibliográfica, reduciendo el riesgo de sesgos y decisiones
improvisadas.
Durante esta fase se realizan las siguientes actividades:

Definición del problema de investigación
Se establece claramente qué fenómeno se desea estudiar y cuál es la necesidad de conocimiento que
motiva la revisión.
Ejemplo:
        Existe una gran cantidad de trabajos sobre analíticas de aprendizaje en juegos serios,
        pero no está claro qué mecanismos de recomendación se utilizan para personalizar la
        experiencia educativa.


Formulación de las preguntas de investigación
Las preguntas deben ser específicas y responder a aspectos concretos del fenómeno estudiado.
Ejemplos:
    •    ¿Qué técnicas de recomendación se utilizan en juegos serios?
    •    ¿Qué datos del estudiante emplean dichas recomendaciones?
    •    ¿Qué métricas se utilizan para evaluar su efectividad?

Construcción del protocolo
El protocolo es el documento metodológico que describe cómo se realizará la revisión.
Normalmente incluye:
    •    preguntas de investigación;
    •    bases de datos a consultar;


                                                      5
```

## Página PDF 6

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                    Revisión Sistemática de la Literatura

     •   cadenas de búsqueda;
     •   criterios de inclusión y exclusión;
     •   procedimiento de selección;
     •   estrategia de extracción de datos;
     •   criterios de evaluación de calidad.
El protocolo cumple una función similar al diseño experimental en una investigación empírica.


2. Búsqueda de estudios
Una vez definido el protocolo, se procede a localizar toda la literatura potencialmente relevante.
El objetivo es lograr una búsqueda lo más exhaustiva posible para minimizar el riesgo de omitir estudios
importantes.

Selección de fuentes
Dependiendo del área disciplinar, pueden utilizarse bases como:
     •   IEEE Xplore
     •   Association for Computing Machinery Digital Library
     •   Elsevier Scopus
     •   Clarivate
     •   Google Scholar
     •   Springer Link
En ingeniería informática suelen utilizarse principalmente IEEE, ACM, Scopus y Web of Science.

Construcción de las cadenas de búsqueda
Las cadenas combinan palabras clave mediante operadores booleanos.
Ejemplo:
("serious game" OR "educational game")
AND
("learning analytics")
AND
(recommendation OR recommender)

El objetivo es equilibrar:
     •   sensibilidad (recuperar muchos estudios);
     •   precisión (evitar estudios irrelevantes).

Ejecución de la búsqueda
Las búsquedas se ejecutan en cada base de datos y se registran:
     •   fecha;
     •   cantidad de resultados obtenidos;
     •   cadena utilizada;
     •   filtros aplicados.
Esta documentación es fundamental para la reproducibilidad.



                                                     6
```

## Página PDF 7

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                   Revisión Sistemática de la Literatura


3. Selección de estudios
La búsqueda suele recuperar cientos o miles de resultados. Por ello es necesario aplicar filtros
sistemáticos.

Eliminación de duplicados
Un mismo artículo puede aparecer en varias bases.
Por ejemplo:
    •   IEEE Xplore
    •   Scopus
    •   Google Scholar
pueden contener el mismo trabajo.
Antes de continuar se eliminan los registros repetidos.

Aplicación de criterios de inclusión
Determinan qué estudios son relevantes.
Ejemplos:
    •   estudios revisados por pares;
    •   artículos completos;
    •   publicaciones entre 2015 y 2025;
    •   trabajos relacionados con aprendizaje adaptativo.

Aplicación de criterios de exclusión
Descartan estudios no pertinentes.
Ejemplos:
    •   resúmenes sin texto completo;
    •   trabajos duplicados;
    •   artículos fuera del dominio de aplicación;
    •   estudios no escritos en inglés o español.

Selección del material
Normalmente se realiza en tres niveles:

Nivel 1: título
Se analiza únicamente el título.

Nivel 2: resumen
Se revisa el abstract.

Nivel 3: texto completo
Ver→ Uso de planillas para la Selección del material
Se evalúa el artículo completo.
Cada etapa reduce progresivamente la cantidad de estudios.

                                                       7
```

## Página PDF 8

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                      Revisión Sistemática de la Literatura

Ejemplo:
        Etapa       Estudios
Recuperados             1200
Sin duplicados           950
Tras título              300
Tras resumen               90
Texto completo             35

4. Evaluación de calidad
No todos los artículos poseen el mismo rigor científico.
La evaluación de calidad permite determinar el grado de confianza que puede depositarse en cada
estudio.

Objetivos
    •    identificar trabajos metodológicamente sólidos;
    •    detectar limitaciones;
    •    ponderar la evidencia encontrada.

Criterios habituales
Algunas preguntas frecuentes son:
    •    ¿Los objetivos están claramente definidos?
    •    ¿La metodología está adecuadamente descrita?
    •    ¿Los resultados son verificables?
    •    ¿Las conclusiones se derivan de los datos?
    •    ¿Existen amenazas a la validez identificadas por los autores?

Sistema de puntuación
Puede utilizarse una escala como:
         Pregunta              Sí   Parcial       No
Objetivos claros           1        0.5       0
Metodología adecuada       1        0.5       0
Resultados verificables 1           0.5       0
Luego se calcula un puntaje total para cada estudio.
La evaluación de calidad no necesariamente elimina estudios, pero ayuda a interpretar los resultados
con mayor cautela.


5. Extracción de datos
Una vez seleccionados los estudios, se recopila la información necesaria para responder las preguntas
de investigación.

Diseño de formularios de extracción
Se construyen planillas donde se registra información homogénea para todos los artículos.


                                                       8
```

## Página PDF 9

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                     Revisión Sistemática de la Literatura

Ejemplo:
 Campo                Valor
Autor        Smith
Año          2023
País         Canadá
Técnica Redes neuronales
Dataset Moodle
Métrica Accuracy

Información típica
Dependiendo de la revisión pueden registrarse:
       •   autores;
       •   año;
       •   país;
       •   contexto de aplicación;
       •   metodología;
       •   tecnologías utilizadas;
       •   resultados obtenidos;
       •   limitaciones identificadas.

Importancia
La extracción sistemática evita que la síntesis posterior se base únicamente en impresiones subjetivas
del investigador.


6. Síntesis de resultados
Es la etapa donde la evidencia recopilada se transforma en conocimiento útil.
El propósito es responder explícitamente las preguntas de investigación.

Síntesis descriptiva
Se resumen características generales de los estudios.
Por ejemplo:
       •   distribución por año;
       •   países más activos;
       •   tipos de metodologías empleadas.
Frecuentemente se utilizan tablas y gráficos.

Síntesis temática
Se identifican patrones, tendencias y categorías emergentes.
Ejemplo:
En una revisión sobre recomendación en juegos serios podrían aparecer categorías como:
       •   recomendación basada en desempeño;


                                                      9
```

## Página PDF 10

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                      Revisión Sistemática de la Literatura

      •    recomendación basada en preferencias;
      •    recomendación basada en perfiles de aprendizaje;
      •    recomendación híbrida.

Identificación de vacíos
La revisión también busca detectar áreas insuficientemente exploradas.
Ejemplo:
          Se observó una escasa utilización de técnicas de aprendizaje profundo para
          personalización en juegos serios destinados a educación superior.


Meta-análisis (cuando corresponde)
En disciplinas como medicina o psicología, donde los estudios reportan resultados comparables, es
posible combinar estadísticamente los resultados para obtener una estimación global del efecto
observado.

Elaboración de conclusiones
Finalmente se responde cada pregunta de investigación y se presentan:
      •    hallazgos principales;
      •    implicancias teóricas;
      •    implicancias prácticas;
      •    limitaciones de la revisión;
      •    líneas futuras de investigación.
Desde una perspectiva metodológica, estas seis etapas forman una cadena lógica:
Planificación → Búsqueda → Selección → Evaluación de calidad → Extracción de datos → Síntesis
Cada etapa utiliza como entrada los resultados de la anterior y proporciona los insumos necesarios para
la siguiente. Si alguna de ellas se ejecuta de forma deficiente, la validez de toda la revisión sistemática
puede verse comprometida. En consecuencia, la fortaleza de una revisión sistemática depende tanto de
la calidad de los estudios incluidos como del rigor aplicado en cada una de estas etapas.


Uso de planillas para la Selección del material
Luego de los dos primeros niveles de análisis de material se realiza el Nivel 3, lectura del texto completo.
Se sugiere el uso de dos planillas para volcar la información.


Planilla A: Caracterización del estudio
Una fila por artículo.
 ID       Autor Año      País    Tipo estudio     Contexto    Calidad
S01 Smith 2024 USA              Experimental    Universidad 8
S02 Wang        2023 China Caso de estudio Secundaria         7
Esta planilla sirve para responder preguntas descriptivas:
      •    ¿Qué países investigan más?
      •    ¿Qué metodologías predominan?
      •    ¿Cómo evolucionó la producción científica?



                                                        10
```

## Página PDF 11

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                  Revisión Sistemática de la Literatura


Planilla B: Extracción de datos para responder las preguntas de
investigación
La Planilla B es probablemente el instrumento más importante de toda la revisión sistemática. Su
función no es almacenar artículos ni registrar datos bibliográficos; su función es capturar evidencia.
Dicho de otra manera:
      La Planilla B debe permitir que, al finalizar la lectura de los estudios seleccionados, las
      respuestas a las preguntas de investigación ya estén prácticamente construidas.

Por este motivo, la estructura de la planilla debe derivarse directamente de las preguntas de
investigación definidas en el protocolo.

Paso 1: Revisar las preguntas de investigación
Considere una revisión sistemática con el siguiente título:
Técnicas de recomendación en juegos serios: una revisión sistemática de los mecanismos
utilizados, los datos considerados y las estrategias de evaluación.
Y sean las preguntas de investigación:
PI1: ¿Qué técnicas de recomendación se utilizan en juegos serios?
PI2: ¿Qué datos del estudiante son utilizados por los mecanismos de recomendación?
PI3: ¿Qué técnicas de inteligencia artificial se emplean?
PI4: ¿Cómo se evalúan los sistemas propuestos?
Observe que cada pregunta solicita un tipo diferente de información.
Por lo tanto, cada pregunta terminará convirtiéndose en una o más columnas de la planilla.

Paso 2: Definir las columnas de extracción
Una práctica habitual consiste en crear una columna para cada aspecto (basado en cada Pregunta de
Investigación) que deba ser analizado posteriormente.
Por ejemplo:
                           Técnica de            Datos                       Método de
 ID   Referencia                                              Técnica IA                        Hallazgos
                         recomendación         utilizados                    evaluación
ID: identificador del artículo
Referencia: cómo encontrar el texto relacionado
Observe que las columnas reflejan directamente aquello que las preguntas de investigación buscan
responder.
No es necesario que las columnas tengan exactamente el mismo nombre que las preguntas. Lo
importante es que permitan obtener la información necesaria para responderlas.

Paso 3: Leer cada estudio buscando evidencia
Aquí aparece una diferencia importante entre una lectura académica tradicional y una lectura orientada
a una revisión sistemática.
En una lectura tradicional, el investigador intenta comprender el trabajo completo.
En una revisión sistemática, además de comprender el trabajo, debe preguntarse constantemente:
      ¿Qué información de este artículo me ayuda a responder mis preguntas de
      investigación?



                                                    11
```

## Página PDF 12

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                       Revisión Sistemática de la Literatura

Por ejemplo, al leer un artículo puede encontrarse la siguiente afirmación:
           "El sistema utiliza filtrado colaborativo para recomendar actividades a los estudiantes."

En ese caso, la información relevante se registra inmediatamente:
  ID         Técnica de recomendación
S05 Filtrado colaborativo
No es necesario copiar párrafos completos del artículo.
Lo que interesa es registrar la evidencia de manera resumida y uniforme.

Paso 4: Registrar información de forma consistente
Este es uno de los errores más frecuentes en revisiones realizadas por estudiantes.
Supongamos que tres artículos utilizan la misma técnica:
       •    Filtrado colaborativo
       •    Collaborative Filtering
       •    Recomendación colaborativa
Si cada estudio se registra de forma distinta, luego será difícil realizar el análisis.
Por ello conviene normalizar los términos.
Por ejemplo:
 ID        Técnica de recomendación
S01 Filtrado colaborativo
S02 Filtrado colaborativo
S03 Filtrado colaborativo
La consistencia facilitará posteriormente los conteos y agrupamientos.

Paso 5: Registrar evidencia, no opiniones
La planilla debe contener información extraída del estudio, no interpretaciones personales.
Por ejemplo:
Incorrecto:
             Hallazgo
Sistema muy interesante
Correcto:
                         Hallazgo
Incremento del 12% en la tasa de finalización
La interpretación vendrá después, durante la síntesis.
Durante la extracción conviene limitarse a registrar hechos observables.

Paso 6: Incorporar citas o referencias internas
Cuando la cantidad de estudios crece, suele ser difícil recordar dónde se encontró determinada
información.
Por ello es recomendable agregar una columna adicional.
 ID        Página             Observación
S05 8               Describe el algoritmo utilizado



                                                        12
```

## Página PDF 13

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                          Revisión Sistemática de la Literatura

o bien
 ID               Sección
S05 4.2 Experimental Results
Esto permite volver rápidamente al documento original si surge alguna duda durante la redacción.

Paso 7: Extraer únicamente información relevante
No todo lo que aparece en un artículo debe registrarse.
Una revisión sistemática no busca resumir cada estudio individualmente.
Busca responder preguntas específicas.
Por ejemplo, si la pregunta es:
          ¿Qué técnicas de inteligencia artificial se utilizan?

Probablemente no sea necesario registrar:
      •    afiliación de los autores;
      •    cantidad de páginas;
      •    formato del artículo;
      •    diseño gráfico del sistema.
Solo debe extraerse aquello que contribuya a responder las preguntas planteadas.

Paso 8: Preparar la planilla para el análisis posterior
Una vez completada la extracción, cada columna contendrá información comparable entre estudios.
Por ejemplo:
 ID           Técnica IA
S01 Redes neuronales
S02 Redes neuronales
S03 Lógica difusa
S04 Árboles de decisión
S05 Redes neuronales
A partir de esta información será posible calcular frecuencias:
      Técnica IA               Cantidad
Redes neuronales           3
Lógica difusa              1
Árboles de decisión 1
Y con ello responder objetivamente una pregunta de investigación.


Secuencia lógica para el diseño de la planilla B
1. Tema de investigación
Primero se define el fenómeno que se desea estudiar.
Ejemplo:
          Uso de mecanismos de recomendación en juegos serios basados en analíticas de
          aprendizaje.


                                                           13
```

## Página PDF 14

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                     Revisión Sistemática de la Literatura

Este tema todavía es demasiado amplio para realizar una revisión sistemática.


2. Preguntas de investigación
A partir del tema se formulan preguntas que permitan estudiarlo de manera concreta.
Por ejemplo:
PI1: ¿Qué mecanismos de recomendación se utilizan en juegos serios basados en analíticas de
aprendizaje?
PI2: ¿Qué datos de los estudiantes son utilizados para realizar las recomendaciones?
PI3: ¿Qué técnicas de inteligencia artificial son empleadas?
PI4: ¿Cómo se evalúan los mecanismos propuestos?
Estas preguntas delimitan exactamente qué evidencia deberá buscarse en cada artículo.


3. Diseño de la Planilla B
Una vez definidas las preguntas, se construye la planilla.
Observe la relación:
 Pregunta de investigación                Información a extraer
PI1                               Mecanismo de recomendación
PI2                               Datos utilizados
PI3                               Técnica de IA
PI4                               Método de evaluación
Por lo tanto, las columnas de la planilla surgen naturalmente de las preguntas.
                              Mecanismo de              Datos                     Método de
      ID     Referencia                                            Técnica IA                   Resultados
                             recomendación            utilizados                  evaluación
La coherencia aparece porque cada columna existe para responder una pregunta específica.


Lo que ocurre durante la lectura del texto completo
(Nivel 3)
Cuando el investigador comienza a leer cada artículo (S01, S02, S03, etc.), ya sabe exactamente qué está
buscando.
No lee el artículo para resumirlo.
Lee el artículo para encontrar evidencia que responda a las preguntas.
Por ejemplo:
Fragmento del artículo:
           "El sistema emplea filtrado colaborativo utilizando el historial de interacción de los
           estudiantes. La evaluación se realizó mediante precisión y recall."

Durante la lectura se registra:
        Pregunta de
                                              Campo                              Valor
       investigación
PI1                          Mecanismo de recomendación              Filtrado colaborativo
PI2                          Datos utilizados                        Historial de interacción
PI3                          Técnica IA                              No especificada


                                                          14
```

## Página PDF 15

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                       Revisión Sistemática de la Literatura


       Pregunta de
                                             Campo                               Valor
      investigación
PI4                          Método de evaluación                  Precisión y Recall
Observe que no se copió el párrafo completo.
Se extrajo únicamente la evidencia relevante para la revisión.


La regla más importante
Cada dato que se registra en la Planilla B debe poder responder alguna pregunta de investigación. No es
obligatorio que todos los artículos respondan a todas las preguntas de investigación.
Si una columna no contribuye a responder ninguna pregunta, probablemente no debería estar en la
planilla.
Por ejemplo, si ninguna pregunta se relaciona con:
      •    afiliación institucional;
      •    cantidad de páginas;
      •    país de publicación;
      •    formato del artículo;
entonces esos datos no pertenecen a la Planilla B.
(En cambio, podrían formar parte de la Planilla A de caracterización).


Otro ejemplo
Supongamos el tema:
          Aplicación de Inteligencia Artificial en la detección de phishing.

Las preguntas podrían ser:
      •    PI1: ¿Qué técnicas de IA se utilizan?
      •    PI2: ¿Qué conjuntos de datos emplean los estudios?
      •    PI3: ¿Qué métricas utilizan para evaluar los modelos?
      •    PI4: ¿Qué resultados obtienen?
La Planilla B podría ser:
          ID            Técnica IA                   Dataset                   Métrica         Resultado
S01                Random Forest         PhishTank                      Accuracy         94%
S02                CNN                   URL Dataset                    F1 Score         96%
S03                Transformer           Mendeley Dataset               Precision        97%
….
Cuando finaliza la extracción:
      •    PI1 se responde analizando la columna "Técnica IA".
      •    PI2 se responde analizando la columna "Dataset".
      •    PI3 se responde analizando la columna "Métrica".
      •    PI4 se responde analizando la columna "Resultado".
Por eso suele decirse que:
          La Planilla B es la materialización operativa de las preguntas de investigación.



                                                        15
```

## Página PDF 16

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                     Revisión Sistemática de la Literatura

Si el tema define qué se quiere estudiar y las preguntas definen qué se quiere saber, la Planilla B define
qué evidencia debe extraerse de cada estudio para poder responder esas preguntas. Esa relación debe
mantenerse desde el protocolo hasta la redacción final de la revisión sistemática.


Una recomendación práctica
Cuando se comienza una revisión sistemática, es habitual que las primeras versiones de la planilla no
sean perfectas.
Por eso suele ser conveniente:
     1. Seleccionar tres o cuatro estudios.
     2. Realizar una extracción piloto.
     3. Revisar las columnas creadas.
     4. Agregar, eliminar o modificar columnas si es necesario.
     5. Recién entonces comenzar la extracción definitiva.
Esta prueba piloto permite detectar información relevante que no había sido prevista durante la
elaboración del protocolo y evita tener que rehacer la planilla cuando ya se han leído decenas de
artículos.
En la práctica, una buena Planilla B funciona como una base de conocimiento estructurada de la
revisión sistemática. Cuando la extracción está bien realizada, la etapa de síntesis deja de consistir en
"volver a leer artículos" y pasa a consistir en "analizar la evidencia ya organizada". Esa es una de las
principales diferencias entre una revisión sistemática rigurosa y una revisión bibliográfica tradicional.


Estructura sintética de una Revisión Sistemática
    1.   Título
    2.   Resumen
    3. Introducción
    4. Objetivo de la investigación
    5. Método
         1.   Planificación
              1.   Identificación de las necesidades que motivan esta revisión
                   1.   Estado actual de la temática
                   2.   Preguntas de investigación
                   3. Selección de bases de datos
         2.   Definición de un Protocolo de revisión
              1.   Definición de los criterios de Inclusión y Exclusión
              2.   Preparación de los datos suceptibles al análisis
    6. Resultados
    7.   Conclusiones
    8. Referencias


Título
Definición:
El título identifica el tema central de la revisión sistemática. Debe expresar de manera clara y precisa el


                                                        16
```

## Página PDF 17

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                  Revisión Sistemática de la Literatura

objeto de estudio y, cuando sea pertinente, indicar que se trata de una revisión sistemática de la
literatura. Un buen título facilita la comprensión del alcance del trabajo y su recuperación en bases de
datos académicas.
Qué es:
Es el nombre del trabajo. Debe indicar con claridad el tema estudiado y, de ser posible, que se trata de
una revisión sistemática.
Para qué sirve:
Permite identificar rápidamente el contenido del estudio y facilita su localización en búsquedas
académicas.
Cómo redactarlo:
Conviene que sea preciso, informativo y que incluya las variables o conceptos principales del tema.
Micro-ejemplo:
“Uso de inteligencia artificial en evaluación educativa: una revisión sistemática de la literatura”.


Resumen
Definición:
El resumen es una síntesis breve del contenido del artículo. Debe presentar el problema de investigación,
el objetivo del estudio, la metodología utilizada para realizar la revisión, los principales resultados
encontrados y las conclusiones más relevantes. Su función es permitir que el lector comprenda
rápidamente el contenido del trabajo.
No contiene citas bibliográficas.
Qué es:
Es una síntesis breve de todo el trabajo. Resume el problema, el objetivo, el método, los resultados
principales y la conclusión general.
Para qué sirve:
Permite que el lector comprenda en pocos párrafos de qué trata el estudio y decida si le resulta
relevante.
Cómo redactarlo:
Se escribe al final, aunque aparece al comienzo del trabajo. Debe ser claro, breve y autosuficiente.
Micro-ejemplo:
“Este estudio presenta una revisión sistemática sobre el uso de inteligencia artificial en evaluación
educativa. Se analizaron artículos indexados en Scopus, WoS e IEEE entre 2018 y 2025. Los resultados
muestran un predominio de enfoques orientados a retroalimentación automática. Se concluye que el
área presenta crecimiento reciente, aunque aún existen vacíos metodológicos”.


Introducción
Definición:
La introducción contextualiza el tema de estudio y presenta el problema de investigación. En esta
sección se describe el área de conocimiento, la importancia del tema, los antecedentes relevantes y la
motivación para realizar la revisión sistemática. También se justifica la necesidad de analizar la literatura
existente para comprender el estado actual del conocimiento.
Debe contener citas bibliográficas de artículos que no forman parte de la revisión propiamente
dicha. Los artículos consultados deben aportar a construir un contexto del tema.
Qué es:
Es la sección que presenta el tema, lo contextualiza y justifica por qué merece ser estudiado.



                                                     17
```

## Página PDF 18

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                   Revisión Sistemática de la Literatura


Para qué sirve:
Ayuda al lector a comprender el problema general, su importancia y la necesidad de realizar la revisión.
Cómo redactarla:
Debe ir de lo general a lo específico. Primero se presenta el área, luego el problema, después el vacío de
conocimiento y finalmente la necesidad de la revisión.
Micro-ejemplo:
“En los últimos años ha crecido el interés por incorporar inteligencia artificial en contextos educativos.
Sin embargo, los estudios disponibles se encuentran dispersos y no siempre permiten identificar con
claridad qué aplicaciones predominan, qué resultados se reportan y qué limitaciones persisten. Por ello,
resulta pertinente realizar una revisión sistemática de la literatura”.


Objetivo de la investigación
Definición:
El objetivo de la investigación establece qué se pretende lograr mediante la revisión sistemática. Debe
expresar con claridad el propósito del estudio, por ejemplo identificar tendencias de investigación,
analizar enfoques metodológicos existentes o determinar el estado del arte de un tema específico.
No contiene citas bibliográficas.
Qué es:
Es la formulación clara de lo que se quiere lograr con la revisión.
Para qué sirve:
Orienta todo el estudio. El objetivo define qué se va a buscar, analizar y sintetizar.
Cómo redactarlo:
Debe expresarse con verbos como identificar, analizar, describir, comparar, clasificar o determinar.
Micro-ejemplo:
“Analizar el estado de la literatura científica sobre el uso de inteligencia artificial en procesos de
evaluación educativa”.


Método
Definición:
En esta sección se describe el procedimiento utilizado para realizar la revisión sistemática. Debe explicar
de manera clara cómo se buscaron los estudios, cómo se seleccionaron los artículos relevantes, qué
criterios se utilizaron para analizarlos y cómo se sintetizaron los resultados. El método debe ser
suficientemente detallado para permitir la replicación del estudio.
Qué es:
Es la explicación detallada de cómo se realizó la revisión sistemática.
Para qué sirve:
Asegura transparencia, rigurosidad y posibilidad de replicación.
Cómo redactarlo:
Debe describir cada decisión metodológica: planificación, bases de datos, cadena de búsqueda, criterios
de selección, proceso de filtrado y forma de análisis.
Micro-ejemplo:
“Se realizó una revisión sistemática siguiendo un protocolo previamente definido. La búsqueda se
efectuó en Scopus, Web of Science e IEEE Xplore mediante cadenas booleanas adaptadas a cada base”.


Planificación
Definición:


                                                      18
```

## Página PDF 19

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                  Revisión Sistemática de la Literatura


La planificación corresponde a la etapa inicial de la revisión sistemática en la que se define la estrategia
de investigación. En esta fase se establecen los objetivos del estudio, las preguntas de investigación,
las fuentes de información y el procedimiento general que se seguirá para realizar la revisión.
Qué es:
Es la etapa inicial en la que se define la estrategia general del estudio.
Para qué sirve:
Permite organizar la revisión antes de comenzar la búsqueda y evitar improvisaciones.
Cómo redactarla:
Debe indicar qué se quiere estudiar, por qué, dónde se buscará y bajo qué reglas se seleccionarán los
trabajos.
Micro-ejemplo:
“En la etapa de planificación se definieron el objetivo, las preguntas de investigación, las bases de datos
y los criterios de inclusión y exclusión”.


Identificación de las necesidades que motivan esta revisión
Definición:
En esta subsección se explica por qué es necesario realizar la revisión sistemática. Se identifican las
limitaciones del conocimiento actual, la dispersión de la literatura o la falta de estudios integradores que
justifiquen la realización del trabajo.
Qué es:
Es la justificación específica de por qué esta revisión resulta necesaria.
Para qué sirve:
Muestra que existe un problema real de dispersión, falta de síntesis o vacíos en la literatura.
Cómo redactarla:
Debe apoyarse en la observación del campo y en antecedentes que evidencien la necesidad de una
síntesis rigurosa.
Micro-ejemplo:
“Aunque existen numerosos trabajos sobre inteligencia artificial en educación, no se dispone de una
síntesis actualizada centrada específicamente en su uso para evaluación del aprendizaje”.

Estado actual de la temática
Definición:
Describe la situación actual del área de investigación. Se presentan los principales enfoques existentes,
las tendencias de estudio y los avances logrados hasta el momento. Este análisis permite comprender el
contexto en el que se desarrolla la revisión.
Qué es:
Es una breve descripción del conocimiento existente sobre el tema antes de iniciar formalmente la
revisión.
Para qué sirve:
Permite ubicar la investigación en contexto y mostrar qué se sabe hasta el momento.
Cómo redactarlo:
Se describen tendencias generales, crecimiento del área, enfoques dominantes y problemas
detectados.
Micro-ejemplo:
“La literatura reciente muestra un aumento de publicaciones sobre corrección automática, analítica del
aprendizaje y tutores inteligentes. Sin embargo, muchos estudios se enfocan en casos aislados y no
permiten identificar patrones globales”.



                                                     19
```

## Página PDF 20

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                  Revisión Sistemática de la Literatura

Preguntas de investigación
Definición:
Las preguntas de investigación orientan todo el proceso de revisión sistemática. Son interrogantes
concretos que buscan ser respondidos a partir del análisis de la literatura científica. Estas preguntas
delimitan el alcance del estudio y guían la búsqueda, selección y análisis de los artículos.
Qué es:
Son las preguntas concretas que la revisión busca responder.
Para qué sirven:
Estructuran el estudio y determinan qué información debe extraerse de los artículos seleccionados.
Cómo redactarlas:
Deben ser claras, específicas y coherentes con el objetivo. Conviene enumerarlas.
Micro-ejemplos:
PI1: ¿qué clase de soluciones basadas en inteligencia artificial se utilizan en la evaluación educativa?
PI2: ¿Existen implementaciones de soluciones basadas en inteligencia artificial se utilizan en la
evaluación educativa ?
PI3: ¿Se han publicado estudios de soluciones basadas en inteligencia artificial se utilizan en la
evaluación educativa?

Selección de bases de datos
Definición:
En este apartado se indican las bases de datos científicas utilizadas para buscar los artículos relevantes.
La selección de estas fuentes debe garantizar el acceso a publicaciones académicas de calidad y cubrir
adecuadamente el área de investigación.
Qué es:
Es la elección de las fuentes académicas en las que se realizará la búsqueda.
Para qué sirve:
Garantiza que los estudios recuperados provengan de repositorios confiables y relevantes.
Cómo redactarla:
Debe justificarse por qué se eligen ciertas bases y no otras, según el área de estudio.
Micro-ejemplo:
“Se seleccionaron Scopus y Web of Science por su cobertura multidisciplinaria, e IEEE Xplore por su
relevancia en estudios de base tecnológica”.


Definición de un Protocolo de revisión
Definición:
El protocolo de revisión establece las reglas que guían todo el proceso de revisión sistemática. Define los
criterios de selección de estudios, la estrategia de búsqueda y el procedimiento de análisis de la
información. Su objetivo es asegurar que el proceso sea sistemático, transparente y reproducible.
Qué es:
Es el conjunto de reglas que organizan toda la revisión sistemática.
Para qué sirve:
Evita decisiones arbitrarias y da consistencia al proceso.
Cómo redactarlo:
Debe explicar la estrategia de búsqueda, los criterios de selección, el procedimiento de depuración y la
forma de análisis de los datos.
Micro-ejemplo:
“El protocolo estableció el período 2018–2025, el uso de artículos revisados por pares, la exclusión de


                                                    20
```

## Página PDF 21

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                  Revisión Sistemática de la Literatura


duplicados y la extracción de datos mediante una matriz de análisis”.


Definición de los criterios de Inclusión y Exclusión
Definición:
Los criterios de inclusión determinan qué estudios serán considerados dentro de la revisión, mientras
que los criterios de exclusión establecen qué estudios serán descartados. Estos criterios se definen
previamente para asegurar una selección objetiva y coherente de los artículos.
Qué es:
Son las condiciones que permiten decidir qué estudios se aceptan y cuáles se descartan.
Para qué sirve:
Asegura que la muestra final de artículos sea pertinente al objetivo de la revisión.
Cómo redactarlos:
Deben ser claros, objetivos y aplicables de manera consistente.
Micro-ejemplo de criterios de inclusión:
“Se incluyeron artículos en inglés o español, publicados entre 2018 y 2025, que abordaran aplicaciones
de inteligencia artificial en evaluación educativa”.
Micro-ejemplo de criterios de exclusión:
“Se excluyeron trabajos duplicados, artículos sin texto completo y estudios que trataran inteligencia
artificial en educación pero sin relación con evaluación”.


Preparación de los datos susceptibles al análisis
Definición:
Una vez seleccionados los estudios relevantes, se realiza la organización y preparación de la información
que será analizada. Esto puede implicar la extracción de datos clave de los artículos, la construcción de
tablas de síntesis o la clasificación de los estudios según diferentes categorías.
Qué es:
Es la organización de la información de los artículos seleccionados para poder analizarla de manera
sistemática.
Para qué sirve:
Permite comparar estudios entre sí y responder las preguntas de investigación con base ordenada.
Cómo redactarla:
Se debe indicar qué datos se extrajeron y cómo se registraron.
Micro-ejemplo:
“De cada artículo se extrajeron autor, año, país, objetivo, metodología, tipo de técnica utilizada,
resultados principales y limitaciones reportadas”.


Resultados
Definición:
En esta sección se presentan los hallazgos obtenidos a partir del análisis de los estudios seleccionados.
Los resultados pueden incluir la identificación de tendencias, enfoques metodológicos, tipos de
soluciones propuestas o vacíos en la literatura. Su propósito es sintetizar el conocimiento existente
sobre el tema.
Debe contener citas bibliográficas de artículos que SI forman parte de la revisión
Qué es:
Es la sección donde se presentan los hallazgos obtenidos a partir del análisis de los estudios incluidos.
Para qué sirve:


                                                     21
```

## Página PDF 22

```text
Universidad Nacional de Mar del Plata
Facultad de Ingeniería
Departamento de Informática


                                   Revisión Sistemática de la Literatura


Permite responder las preguntas de investigación y mostrar de manera organizada qué dice la literatura.
Cómo redactarlos:
Los resultados pueden organizarse por pregunta de investigación, por categorías temáticas, por
métodos o por tendencias encontradas. Conviene usar tablas, cuadros o esquemas de síntesis.
Micro-ejemplo:
“Los resultados evidenciaron tres grandes líneas de aplicación: corrección automática,
retroalimentación personalizada y predicción del rendimiento. La mayoría de los estudios reportó
mejoras en rapidez del proceso evaluativo, aunque con escasa validación longitudinal”.


Conclusiones
Definición:
Las conclusiones sintetizan los principales aportes de la revisión sistemática. Se destacan los hallazgos
más relevantes, se identifican las limitaciones de la investigación y se sugieren posibles líneas de
trabajo futuro. Esta sección permite comprender qué se ha aprendido a partir del análisis de la literatura.
No contiene citas bibliográficas.
Qué es:
Es la síntesis final e interpretativa de los resultados.
Para qué sirve:
Permite señalar qué se aprendió con la revisión, cuáles son los principales hallazgos y qué aspectos
requieren mayor investigación.
Cómo redactarlas:
No deben limitarse a repetir resultados. Deben integrarlos, interpretarlos y vincularlos con el objetivo
inicial.
Micro-ejemplo:
“La revisión muestra que la inteligencia artificial posee un creciente potencial en evaluación educativa,
especialmente en automatización y personalización. Sin embargo, se observa una limitada
estandarización metodológica y una escasa evidencia sobre impacto a largo plazo”.


Referencias
Definición:
Las referencias bibliográficas incluyen todas las fuentes científicas citadas en el trabajo. Deben
presentarse siguiendo un formato de citación académico y permiten que el lector identifique los estudios
utilizados, verifique la información y profundice en los trabajos originales.
Qué es:
Es el listado completo de las fuentes citadas en el trabajo.
Para qué sirve:
Da sustento académico al estudio, permite verificar la información y facilita que otros investigadores
consulten las fuentes originales.
Cómo redactarlas:
Deben ajustarse al estilo bibliográfico exigido, como APA 7, IEEE o Vancouver.
Micro-ejemplo en APA 7:
Kitchenham, B., & Charters, S. (2007). Guidelines for performing systematic literature reviews in software
engineering.




                                                      22
```

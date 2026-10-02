# **Fundamentos Metodológicos y Delimitación Conceptual para el Desarrollo de Entornos Digitales de Educación Musical**

El presente documento establece el marco fundamental y metodológico correspondiente a la primera fase de investigación para el desarrollo de una arquitectura tecnológica orientada a la educación musical. En estricto apego a los parámetros establecidos, este análisis despliega un protocolo sistemático para evaluar la viabilidad, eficacia y las limitaciones infraestructurales de la implementación de algoritmos de inteligencia artificial (modelos predictivos, redes neuronales recurrentes), procesamiento digital de señales (DSP) en tiempo real y mecánicas lúdicas dentro de ecosistemas web y móviles.  
La aproximación aquí descrita se abstiene deliberadamente de confirmar suposiciones iniciales por complacencia. Por el contrario, somete las hipótesis de diseño (como la viabilidad del análisis MIDI bidireccional en navegadores o la supuesta ubicuidad de los beneficios de la gamificación) a un severo escrutinio empírico y teórico. El objetivo subyacente es estructurar una revisión sistemática de la literatura que exponga los vacíos del estado del arte, sustentando así el rigor científico de las decisiones arquitectónicas que eventualmente configurarán la plataforma propuesta.

## **1\. Fundamentación Analítica de la Investigación**

La intersección entre la computación interactiva y el entrenamiento musical representa un dominio de alta complejidad debido a las estrictas tolerancias de latencia auditiva y la carga cognitiva intrínseca que exige el procesamiento de información polifónica o rítmica. El propósito rector de esta revisión es diseccionar el estado del arte sobre la aplicación de tecnologías inmersivas, juegos serios y sistemas adaptativos orientados al entrenamiento auditivo, el reconocimiento de intervalos y la percepción rítmica.  
Considerando que el ecosistema técnico propuesto involucra tecnologías *full-stack* (Next.js, React, Node.js) y el despliegue de redes neuronales recurrentes (RNN/LSTM) en el lado del cliente o servidor, la investigación debe trascender la mera evaluación pedagógica. Se exige una auditoría profunda de las limitaciones de concurrencia en los navegadores modernos, específicamente los cuellos de botella generados al sincronizar el árbol de objetos del documento (DOM) con las interfaces de programación de aplicaciones de audio (WebAudio API) y la ejecución de tensores mediante compilación WebAssembly (WASM)1. Por consiguiente, la investigación se formula para mapear tanto la eficacia instruccional demostrada en ensayos clínicos o de cohorte, como las topologías de software que hacen posible dicha instrucción sin degradar la experiencia de usuario.

## **2\. Delimitación Ontológica y Fronteras Conceptuales**

El ecosistema de la tecnología educativa musical sufre de una severa polisemia. Términos como "juego serio" y "gamificación" se utilizan erróneamente de forma intercambiable en la literatura comercial y, ocasionalmente, en la académica. Para fundamentar el diseño de una plataforma de entrenamiento auditivo, es imperativo establecer los límites precisos de cada constructo. A continuación, se definen, contrastan y evalúan conceptualmente.

### **2.1 Serious Games vs. Gamificación**

La diferenciación más rigurosa entre estos términos proviene de la ontología establecida por Deterding et al. (2011), la cual distingue entre el diseño lúdico integral y la aplicación fragmentada de mecánicas4.

* **Serious Games (SG):** Se estructuran como sistemas completos que operan bajo el paradigma de *ludus* (juego estructurado por reglas orientadas a un objetivo) o *paidia* (juego libre y exploratorio), pero cuyo propósito primario trasciende el entretenimiento puro, apuntando a objetivos educativos, terapéuticos o de entrenamiento4. En la disciplina musical, un SG provee un entorno donde los errores (por ejemplo, fallar en la identificación de un acorde mayor) no tienen consecuencias en el mundo real, permitiendo una asimilación libre de ansiedad6. La literatura reporta un sesgo en los SG de realidad mixta actuales, los cuales predominan en el entrenamiento de habilidades receptivas (reconocimiento de tono) pero carecen de implementaciones rítmicas robustas6.  
* **Gamificación (Gamification):** Es el uso de elementos de diseño de juegos (puntos, medallas, tablas de clasificación, avatares) en contextos ajenos a los juegos4. No se crea un universo lúdico autónomo, sino que se inyecta *gamefulness* (la cualidad experiencial del juego) en un sistema de software tradicional (como un LMS o un módulo de dictado armónico) para motivar extrínsecamente al usuario4.

**Contraste Empírico:** Mientras que la gamificación ha demostrado mejorar la motivación auto-regulada y el sentido de competencia en la enseñanza de teoría musical (evidencia derivada de ensayos controlados)10, su aplicación superficial puede ser contraproducente. La adición de narrativas y tablas de clasificación en simuladores rítmicos de realidad virtual ha demostrado elevar las cualidades hedónicas, pero a costa de reducir el enfoque y el rendimiento objetivo del usuario en las tareas de sincronización rítmica11.

### **2.2 Game-Based Learning (GBL) y Digital Game-Based Learning (DGBL)**

Estos conceptos operan en la capa pedagógica. Representan la filosofía instruccional de utilizar juegos como vehículo de aprendizaje.

* **Game-Based Learning (GBL):** Abarca el uso de cualquier juego (incluyendo juegos de mesa físicos o dinámicas de aula) para alcanzar resultados de aprendizaje5.  
* **Digital Game-Based Learning (DGBL):** Es la traslación de esta metodología al ámbito computacional. Estudios recientes (2020-2025) señalan que el DGBL experimentó un despliegue masivo debido a la transición post-pandemia hacia modelos híbridos de educación13. En el contexto musical, el DGBL se ha materializado mayoritariamente en plataformas de *ear training* (entrenamiento auditivo) y cuestionarios de teoría musical13. Sin embargo, la evidencia revela que el enfoque exclusivo del DGBL actual en la exactitud rítmica y la igualación de tonos ha creado una brecha instruccional, marginando el aprendizaje de habilidades complejas como la comprensión del timbre y la improvisación13.

### **2.3 Computer-Assisted Music Learning (CAML) vs. Music Education Technology**

* **Computer-Assisted Music Learning (CAML):** Es el constructo fundacional. Surgió en las décadas de los setenta y ochenta con sistemas lineales y ramificados (como TICCIT y PLATO) que ofrecían instrucción directa y evaluación binaria15. La investigación histórica en CAML demostró tempranamente que las máquinas podían presentar conceptos de manera más paciente y menos crítica que los humanos16.  
* **Music Education Technology:** Es el término contemporáneo de nivel macro que encapsula al CAML, la producción musical asistida (DAWs), los SG, el DGBL y las herramientas de análisis musicológico digital17. Implica la modernización del aprendizaje asistido a través de ecosistemas en red y *software* colaborativo.

### **2.4 Mobile/Interactive Music Learning y Limitaciones de Entorno**

El aprendizaje interactivo móvil se define por su dependencia de dispositivos de consumo masivo y navegadores móviles. En el contexto de la música, este enfoque presenta desafíos de ingeniería formidables. El procesamiento de audio en la web interactiva depende de la **WebAudio API**, la cual, históricamente, sufría conflictos de concurrencia al compartir el hilo principal con el renderizado visual de la interfaz (requestAnimationFrame) en JavaScript1. La evidencia técnica señala que cuando un sistema de aprendizaje interactivo móvil intenta renderizar retroalimentación visual (ej. indicadores de progresión de acordes) simultáneamente con la evaluación algorítmica de la señal de audio (DSP) o la inferencia de un modelo, se producen *jitter* (fluctuación de retardo), caída de *frames* y clics audibles1. En consecuencia, cualquier análisis del estado del arte debe separar estrictamente los sistemas que operan de manera nativa frente a aquellos que intentan ejecutar inferencia compleja en el navegador.

### **2.5 Intelligent Tutoring Systems (ITS) y Adaptive Learning**

Estos representan el ápice técnico de la instrucción digital personalizada.

* **Adaptive Learning (Aprendizaje Adaptativo):** Es un mecanismo metodológico donde el sistema modifica la dificultad o el tipo de contenido en respuesta a las interacciones del estudiante en tiempo real. Por ejemplo, ajustar la alteración de tono de fragmentos sonoros modificados mediante transformadas de Fourier (cents) o ecualización algorítmica (cortes de frecuencias) basándose en las tasas de éxito iterativas del usuario20.  
* **Intelligent Tutoring Systems (ITS):** Un ITS implementa algoritmos de inteligencia artificial para modelar el estado cognitivo y afectivo del usuario ("Student Model"), imitando las decisiones de un tutor humano experto21. Los ITS modernos aplican *Machine Learning* para proporcionar instrucciones finamente sintonizadas, superando las simples estructuras de árbol de decisiones del CAML tradicional21.

### **2.6 Aplicabilidad y Síntesis Basada en la Evidencia (Evidencia → Interpretación → Conclusión)**

Para definir cuáles de estos constructos estructurarán la tesis, se aplica el siguiente análisis crítico:

| Constructo Evaluado | Nivel de Evidencia Encontrada | Interpretación del Fenómeno | Conclusión y Aplicabilidad al Proyecto |
| :---- | :---- | :---- | :---- |
| **Gamificación Pura** | Meta-análisis y estudios de cohorte4. | Introduce elementos lúdicos que incrementan el *engagement* a corto plazo. Sin embargo, su exceso introduce carga cognitiva extrínseca que disminuye la eficacia en la internalización de patrones rítmicos y auditivos12. | **Uso Restringido.** Se aplicará como capa periférica de motivación (progresión y retención), pero no como mecanismo central de enseñanza. |
| **Serious Games (SG)** | Revisiones sistemáticas y empíricas en realidad mixta6. | Proporcionan un marco donde la interacción algorítmica y la manipulación de objetos de audio inmersivos facilitan la adquisición de competencias de tono de forma holística6. | **Eje Estructural.** La plataforma se concebirá como un SG, permitiendo interacciones profundas con la materia musical. |
| **Intelligent Tutoring (ITS) / Adaptive Learning con ML** | Estudios experimentales de integración de TensorFlow.js e IA Generativa21. | La IA generativa reduce las barreras de composición, pero requiere diseño "human-in-the-loop" para no erosionar la agencia. La inferencia ML en el navegador (Web Worker/WASM) previene latencias críticas3. | **Núcleo Lógico-Técnico.** El proyecto implementará un ITS basado en redes neuronales (RNN/LSTM), requiriendo un diseño asíncrono para operar eficientemente sobre arquitecturas WebAudio. |

## **3\. Diseño del Protocolo de Revisión Sistemática (SLR)**

Para minimizar el sesgo de confirmación y garantizar la reproducibilidad de la revisión del estado del arte requerida en la Fase 2, se estructurará un protocolo híbrido que adopta los estándares PRISMA (Preferred Reporting Items for Systematic Reviews and Meta-Analyses) y las directrices metodológicas de Kitchenham, que son el estándar de oro para la investigación en Ingeniería de Software29.

### **3.1 Objetivo de la Revisión**

Identificar, evaluar críticamente y sintetizar la evidencia primaria respecto a las topologías de *software* (stack web, DSP móvil, Machine Learning) y su eficacia pedagógica al implementarse en juegos serios y sistemas adaptativos orientados a la educación y el entrenamiento musical auditivo.

### **3.2 Criterios de Elegibilidad (Estrategia PICOC)**

* **Población (Population):** Usuarios de tecnologías de educación musical. Esto abarca desde estudiantes en etapas formativas iniciales y universitarios (no especialistas), hasta alumnos de conservatorios y profesionales de ingeniería de audio requiriendo entrenamiento auditivo técnico27.  
* **Intervención (Intervention):** Entornos de aprendizaje interactivos basados en computación. Específicamente: Serious Games, entornos WebAudio, aplicaciones de inferencia ML local (WASM/TensorFlow.js) y sistemas de tutoría inteligente (ITS)1.  
* **Comparación (Comparison):** Instrucción tradicional no interactiva (partituras estáticas, clases magistrales analógicas), sistemas CAML estáticos carentes de modelos algorítmicos adaptativos, o entornos carentes de retroalimentación asíncrona en tiempo real10.  
* **Resultados (Outcomes):** Mapeo de dos dominios críticos:  
  1. *Pedagógico:* Evaluación objetiva de habilidades adquiridas, alteraciones en la carga cognitiva y retención a largo plazo.  
  2. *Técnico:* Tiempos de latencia algorítmica, carga del hilo principal (Main Thread blocking), viabilidad de modelos predictivos (RNN/LSTM) en el lado del cliente y efectividad del ruteo MIDI/AudioWorklet1.  
* **Contexto (Context):** Aprendizaje asíncrono en ecosistemas digitales, laboratorios de investigación musical mediados por interfaces gráficas interactivas y despliegues comerciales operados a través de redes *cloud-edge* y navegadores web estándar.

### **3.3 Ecuaciones y Estrategia de Búsqueda**

La recuperación de documentos se llevará a cabo utilizando una superposición de operadores booleanos aplicada a metadatos primarios (Título, Resumen y Palabras Clave). Las cadenas se han estructurado para capturar los tres ejes de interés: la metodología lúdica, la aplicación disciplinar y la arquitectura subyacente.  
Se emplearán las siguientes combinaciones algorítmicas:

> 1. **Eje Pedagógico / Lúdico:** ("serious game\*" OR "gamification" OR "digital game-based learning" OR "intelligent tutoring system" OR "adaptive learning")  
> 2. **Eje Disciplinar (Musical):** ("music education" OR "ear training" OR "pitch recognition" OR "rhythm perception" OR "music theory" OR "aural skills")  
> 3. **Eje Técnico y Computacional:** ("machine learning" OR "artificial intelligence" OR "RNN" OR "LSTM" OR "WebAudio" OR "DSP" OR "WebAssembly" OR "TensorFlow.js")

La búsqueda principal consistirá en la intersección (operador AND) de estos tres conjuntos.

### **3.4 Criterios de Inclusión y Exclusión Sistemáticos**

| Criterio | Descripción Analítica | Justificación Metodológica |
| :---- | :---- | :---- |
| **Inclusión 1** | Estudios empíricos revisados por pares (conferencias de ciencias computacionales o revistas de psicología/educación musical). | Asegura que la eficacia reportada posee validez estadística y rigor investigativo (Ej. IEEE, ACM, MDPI)25. |
| **Inclusión 2** | Investigaciones centradas en arquitecturas informáticas y DSP interactivo en el navegador web (WebAudio API, WebWorkers). | Crucial para determinar el estado del arte técnico aplicable al trasfondo *full-stack* de la tesis propuesta1. |
| **Inclusión 3** | Análisis de usabilidad y carga cognitiva en interfaces interactivas de aprendizaje musical. | Permite integrar la Teoría de la Carga Cognitiva en el diseño de UX/UI, evitando el efecto de redundancia visual24. |
| **Exclusión 1** | Proyectos que reportan mecánicas de gamificación pero carecen de una evaluación empírica (pre/post test) de adquisición de habilidades. | Aislar la literatura que confunde el mero aumento temporal de la motivación con el aprendizaje profundo y sustentable14. |
| **Exclusión 2** | Aplicaciones estrictamente médicas (musicoterapia pasiva para paliación) o intervenciones lúdicas exclusivas para habilidades motoras finas sin componente analítico musical. | Garantizar la concentración del estado del arte en el entrenamiento auditivo (perceptual, armónico, rítmico) que es el objetivo de la plataforma6. |

### **3.5 Método Estructurado de Extracción de Datos**

Para la recolección de los datos (Fase 2), se diseñará una matriz multidimensional. Por cada *paper* seleccionado, se extraerá: a) Tipología arquitectónica (Servidor vs. Cliente, uso de WebAssembly/RNN), b) Habilidad musical específica que se evalúa (intervalos, afinación, ritmo), c) Estrategias aplicadas para mitigar la carga cognitiva (manejo de carga extrínseca), d) Latencia computacional reportada (ms), y e) Nivel de evidencia (prototipo de laboratorio vs. entorno real).

## **4\. Gestión de Bases de Datos y Protocolo Ante Muros de Pago (Paywalls)**

Las investigaciones publicadas bajo los sellos editoriales de mayor influencia en la ingeniería (IEEE Xplore, ACM Digital Library) y las ciencias multidisciplinares (Scopus, Springer) frecuentemente operan bajo esquemas de restricción de acceso (paywalls). Dada la limitación operativa de este agente de investigación para eludir sistemas de pago corporativos o *proxies* institucionales en tiempo real, se establece el siguiente protocolo de mitigación para la Fase 2:

> 1. **Explotación Inicial de Acceso Abierto:** La prioridad algorítmica será escanear e integrar literatura procedente de repositorios institucionales de vía verde (Green Open Access), arXiv, PubMed Central (vital para artículos que exploran los sustratos neuro-cognitivos del entrenamiento musical32), DOAJ y MDPI.  
> 2. **Extracción de Metadatos y Abstract:** Para los documentos cruciales alojados tras barreras de pago en IEEE y ACM, se empleará el conocimiento preentrenado y la indexación pública para capturar el *Abstract* extendido, las palabras clave, los índices de citación y la taxonomía del estudio. Frecuentemente, el resumen de estudios de *Software Engineering* describe detalladamente la arquitectura técnica y el hallazgo principal6.  
> 3. **Identificación y Delegación de Extracción:** Cuando se detecte una brecha crítica en el conocimiento (por ejemplo, un estudio seminal reciente que vincule directamente redes LSTM con WebAudio API en un ITS3) donde el Abstract resulte insuficiente, se marcará explícitamente el Identificador de Objeto Digital (DOI) y el enlace en el reporte. El investigador humano (usuario) recibirá la indicación de adquirir el texto íntegro externamente para proveer, mediante *prompts* iterativos, los segmentos de metodologías experimentales (Materiales y Métodos) y los Resultados.

## **5\. Criterios de Antigüedad y Marco Temporal de la Literatura**

El panorama de las arquitecturas de *software* web y la inteligencia artificial es altamente efímero. Por ende, los criterios de antigüedad no pueden ser un mero formalismo retrospectivo, sino que deben alinearse estratégicamente con las inflexiones tecnológicas y sociológicas recientes.

### **5.1 Ventana Primaria: 2020 \- 2026**

El núcleo de la revisión sistemática concentrará al menos el 80% de los documentos primarios en el último lustro. Esta delimitación responde a tres justificaciones demostrables:

* **Maduración de Tecnologías de Cliente:** Es durante este marco temporal que tecnologías como AudioWorklet (que reemplazó al obsoleto ScriptProcessorNode de la WebAudio API) alcanzaron el soporte universal de los navegadores, permitiendo finalmente el DSP de ultra-baja latencia en un subproceso aislado del hilo principal1. Simultáneamente, *frameworks* de inferencia de aprendizaje profundo en el navegador, como TensorFlow.js en conjunción con WebAssembly, han madurado para permitir predicciones heurísticas sin latencia de red2.  
* **El Efecto de la IA Generativa (GenAI):** La introducción y popularización de modelos de lenguaje grande (LLMs) y transformadores de audio desde 2022 ha reconfigurado el diseño de los Intelligent Tutoring Systems, disminuyendo las barreras técnicas de composición pero introduciendo riesgos documentados de alienación cognitiva (pérdida de la agencia del usuario o *psychological ownership*)27.  
* **Catalizador Pandémico del DGBL:** El imperativo del distanciamiento físico reorientó drásticamente la investigación empírica en el Digital Game-Based Learning, produciendo un cuerpo sustancial de evaluaciones de plataformas a gran escala que antes no existían13.

### **5.2 Incorporación de Literatura Seminal (Excepciones)**

Los estudios previos al año 2014 serán excluidos en materia tecnológica, pero serán imperativos para el anclaje teórico. Se integrarán obligatoriamente corpus fundamentales tales como:

* Los trabajos fundacionales sobre **Teoría de la Carga Cognitiva** (Sweller, 1988/1994, Chandler & Sweller, 1991), esenciales para entender los efectos del "diseño instructivo deficiente" y la "atención dividida" en el cerebro del estudiante32.  
* La estructuración conceptual de la **Gamificación** (Deterding et al., 2011), que permite separar la retórica comercial del diseño lúdico académico4.  
* Las guías metodológicas de **SLR en Software Engineering** (Kitchenham, 2004/2007)29.

## **6\. Formulación de Preguntas de Investigación Críticas (RQs)**

Para trascender la evaluación meramente descriptiva, las Preguntas de Investigación (RQs) se han articulado con un enfoque interrogativo severo. Su función no es validar el desarrollo de la plataforma del proyecto, sino investigar bajo qué condiciones paramétricas dicha plataforma está justificada y qué escollos metodológicos debe sortear.

### **RQ1: Eficacia Pedagógica vs. Espejismos de Engagement**

**¿En qué grado los entornos gamificados y los juegos serios producen una transferencia objetiva de habilidades musicales (reconocimiento tonal, rítmico, interválico) en contraposición al mero incremento temporal de métricas de percepción (engagement) en los estudiantes?**

* *Justificación y Relevancia:* La literatura expone que la gamificación suele generar resultados mixtos. A menudo, aumenta la retención superficial pero falla en provocar aprendizaje a largo plazo5. Más preocupante aún, investigaciones recientes sugieren que las plataformas DGBL modernas se concentran restrictivamente en destrezas perceptuales aisladas (ej. identificar una nota), careciendo de la contextualización requerida para el desarrollo del "músico holístico"13.  
* *Evidencia Requerida en Fase 2:* Se buscarán preferentemente meta-análisis y estudios cuasi-experimentales con diseños pre-test y post-test que evalúen la competencia musical pura (ej. Tapping-PROMS tests11), y no exclusivamente encuestas de satisfacción o auto-reportes de los estudiantes6.

### **RQ2: Limitaciones Arquitectónicas del Ecosistema Web/Móvil**

**¿Cuáles son los cuellos de botella computacionales y arquitectónicos predominantes que limitan la implementación de modelos de Machine Learning (RNN/LSTM) y Procesamiento Digital de Señales (DSP) en tiempo real en navegadores web para aplicaciones musicales?**

* *Justificación y Relevancia:* Dada la experiencia *full-stack* del proyecto (React/Next.js/Node.js) y el manejo de RNN/LSTM, existe una presunción inicial sobre la viabilidad técnica. Sin embargo, la investigación advierte que compaginar inferencia de ML con síntesis WebAudio interactúa agresivamente con el recolector de basura (Garbage Collection) de JavaScript, causando caídas de audio1.  
* *Evidencia Requerida en Fase 2:* Documentación de pruebas de rendimiento de latencia en ecosistemas basados en WebAssembly, evaluación del enrutamiento concurrente en arquitecturas no bloqueantes (AudioWorklet / JS Workers), y métricas de desempeño de librerías como TensorFlow.js procesando flujos de audio1.

### **RQ3: Gestión de la Carga Cognitiva y Diseño de Interfaces**

**¿Cómo interactúan las mecánicas lúdicas de retroalimentación en tiempo real (puntos, narrativas visuales) con la memoria de trabajo del alumno, y bajo qué condiciones el diseño de la interfaz incrementa la carga cognitiva extrínseca impidiendo el aprendizaje musical?**

* *Justificación y Relevancia:* El entrenamiento musical per se acarrea una elevadísima carga cognitiva intrínseca (coordinar escucha, interpretación teórica y memoria espacial)24. De acuerdo a Sweller y la cognición del aprendizaje multimedia37, añadir elementos visuales como historias narrativas inmersivas (comunes en los SG) o textos redundantes compitiendo con el audio puede generar un "efecto de atención dividida" o sobrecargar el canal visual34. Adicionalmente, características accesorias como la música de fondo o gráficos vibrantes pueden comportarse como "detalles seductores" (*seductive details*) que degradan el rendimiento instruccional general41.  
* *Evidencia Requerida en Fase 2:* Ensayos empíricos sobre diseño instruccional que aíslen variables gráficas y sonoras, y demuestren el umbral en el que la inclusión de "gamefulness" disminuye la adquisición del ritmo o la precisión del *pitch*11.

### **RQ4: Sistemas Tutores Inteligentes y Fricciones de Agencia (Agentividad)**

**¿Cuáles son los modelos heurísticos (Inteligencia Artificial, Sistemas Adaptativos) más efectivos para generar bucles de retroalimentación en el entrenamiento auditivo, y cómo afectan estos sistemas la percepción de la agencia creativa del músico?**

* *Justificación y Relevancia:* Los Sistemas Tutores Inteligentes (ITS) permiten micro-ajustes en tiempo real (ej. variaciones logarítmicas de la frecuencia (cents) de un tono de prueba basándose en los aciertos pasados del alumno)20. Sin embargo, la automatización excesiva del aprendizaje mediante inteligencia artificial, si se diseña para intervenir prematuramente, puede evitar que el alumno construya resiliencia al fracaso y erosionar el "sentido de propiedad psicológica" sobre la adquisición del conocimiento, a menos que se incruste fricción productiva (el *IKEA effect*)27.  
* *Evidencia Requerida en Fase 2:* Análisis de la arquitectura algorítmica de los motores de evaluación (Student Models) integrados en los ITS contemporáneos, y estudios psicológicos midiendo el impacto del soporte hiper-asistido por IA en el desarrollo a largo plazo del alumno de conservatorio21.

*Conclusión de la Fase 1\.* Las fundaciones de la investigación han sido estructuradas. El protocolo expuesto subordina las expectativas de la plataforma propuesta a la viabilidad algorítmica y cognitiva validada en la literatura. Confirme formalmente su aceptación de este diseño para habilitar la ejecución inmediata de la **Fase 2: Búsqueda del Estado del Arte, Extracción de Datos y Evaluación Crítica**.

#### **Obras citadas**

> 1. Intelligent audio plugin framework for the Web Audio API, [https://www.researchgate.net/publication/318597364\_Intelligent\_audio\_plugin\_framework\_for\_the\_Web\_Audio\_API](https://www.researchgate.net/publication/318597364_Intelligent_audio_plugin_framework_for_the_Web_Audio_API)  
> 2. POLITECNICO DI TORINO, [https://webthesis.biblio.polito.it/26882/1/tesi.pdf](https://webthesis.biblio.polito.it/26882/1/tesi.pdf)  
> 3. Designing for a Pluralist and User-Friendly Live Code Language, [https://users.sussex.ac.uk/\~thm21/thor/pdfs/Bernardo\_Kiefer\_Magnusson\_ICLC.pdf](https://users.sussex.ac.uk/~thm21/thor/pdfs/Bernardo_Kiefer_Magnusson_ICLC.pdf)  
> 4. From Game Design Elements to Gamefulness: Defining Gamification, [https://www.researchgate.net/publication/230854710\_From\_Game\_Design\_Elements\_to\_Gamefulness\_Defining\_Gamification](https://www.researchgate.net/publication/230854710_From_Game_Design_Elements_to_Gamefulness_Defining_Gamification)  
> 5. Serious Gaming for Behaviour Change: A Systematic Review \- MDPI, [https://www.mdpi.com/2078-2489/13/3/142](https://www.mdpi.com/2078-2489/13/3/142)  
> 6. Mixed Reality Approach to Serious Games in Developing Musical, [https://sol.sbc.org.br/index.php/sbcm/article/download/43944/43707](https://sol.sbc.org.br/index.php/sbcm/article/download/43944/43707)  
> 7. (PDF) Development of Serious Games for Music Education, [https://www.researchgate.net/publication/278685849\_Development\_of\_Serious\_Games\_for\_Music\_Education](https://www.researchgate.net/publication/278685849_Development_of_Serious_Games_for_Music_Education)  
> 8. From Game Design Elements to Gamefulness: Defining "Gamification", [https://www.slideshare.net/slideshow/from-game-design-elements-to-gamefulness-defining-gamification/12644129](https://www.slideshare.net/slideshow/from-game-design-elements-to-gamefulness-defining-gamification/12644129)  
> 9. From game design elements to gamefulness: defining "gamification", [https://www.semanticscholar.org/paper/From-game-design-elements-to-gamefulness%3A-defining-Deterding-Dixon/b9a6985d2a0fcd988b2538c89c95ad19ccd67a66](https://www.semanticscholar.org/paper/From-game-design-elements-to-gamefulness%3A-defining-Deterding-Dixon/b9a6985d2a0fcd988b2538c89c95ad19ccd67a66)  
> 10. Enhancing Students' Motivational Capacities in Music Classrooms, [https://arch.astate.edu/cgi/viewcontent.cgi?article=1105\&context=all-etd](https://arch.astate.edu/cgi/viewcontent.cgi?article=1105&context=all-etd)  
> 11. Effects of storytelling on user experience and performance in VR, [https://www.frontiersin.org/journals/virtual-reality/articles/10.3389/frvir.2025.1674655/full](https://www.frontiersin.org/journals/virtual-reality/articles/10.3389/frvir.2025.1674655/full)  
> 12. Effects of storytelling on user experience and performance in VR, [https://www.frontiersin.org/journals/virtual-reality/articles/10.3389/frvir.2025.1674655/pdf](https://www.frontiersin.org/journals/virtual-reality/articles/10.3389/frvir.2025.1674655/pdf)  
> 13. Mapping the Use of Digital Game-Based Learning in Music Education, [https://www.researchgate.net/publication/398034792\_Mapping\_the\_Use\_of\_Digital\_Game-Based\_Learning\_in\_Music\_Education\_A\_Scoping\_Review](https://www.researchgate.net/publication/398034792_Mapping_the_Use_of_Digital_Game-Based_Learning_in_Music_Education_A_Scoping_Review)  
> 14. Blogged into the System: A Systematic Review of the Gamification in, [https://www.mdpi.com/2071-1050/15/8/6476](https://www.mdpi.com/2071-1050/15/8/6476)  
> 15. computer-assisted instruction cai: Topics by Science.gov, [https://www.science.gov/topicpages/c/computer-assisted+instruction+cai.html](https://www.science.gov/topicpages/c/computer-assisted+instruction+cai.html)  
> 16. And Others Computer Technology in Curriculum and Instruction, [https://files.eric.ed.gov/fulltext/ED255179.pdf](https://files.eric.ed.gov/fulltext/ED255179.pdf)  
> 17. Serious Games for Music Education \- SciTePress, [https://www.scitepress.org/papers/2013/43649/43649.pdf](https://www.scitepress.org/papers/2013/43649/43649.pdf)  
> 18. Ambuja Salgaonkar \- Computer Assisted Music and Dramatics \- Scribd, [https://www.scribd.com/document/726206959/Ambuja-Salgaonkar-Computer-Assisted-Music-and-Dramatics](https://www.scribd.com/document/726206959/Ambuja-Salgaonkar-Computer-Assisted-Music-and-Dramatics)  
> 19. Issues and Challenges of Audio Technologies for the Musical, [https://www.lucaturchet.it/PUBLIC\_DOWNLOADS/publications/journals/Issues\_and\_Challenges\_of\_Audio\_Technologies\_for\_the\_Musical\_Metaverse.pdf](https://www.lucaturchet.it/PUBLIC_DOWNLOADS/publications/journals/Issues_and_Challenges_of_Audio_Technologies_for_the_Musical_Metaverse.pdf)  
> 20. The Effects of Musical Experience and Hearing Loss on Solving an, [https://www.mdpi.com/2076-3417/7/12/1278](https://www.mdpi.com/2076-3417/7/12/1278)  
> 21. Intelligent Tutoring Systems: A Comprehensive Historical Survey, [https://www.researchgate.net/publication/331790537\_Intelligent\_Tutoring\_Systems\_A\_Comprehensive\_Historical\_Survey\_with\_Recent\_Developments](https://www.researchgate.net/publication/331790537_Intelligent_Tutoring_Systems_A_Comprehensive_Historical_Survey_with_Recent_Developments)  
> 22. The Transition From Intelligent to Affective Tutoring System \- SciSpace, [https://scispace.com/pdf/the-transition-from-intelligent-to-affective-tutoring-system-3n84c8bw1b.pdf](https://scispace.com/pdf/the-transition-from-intelligent-to-affective-tutoring-system-3n84c8bw1b.pdf)  
> 23. (PDF) Personalized Music Education: A Systematic Review of AI, [https://www.researchgate.net/publication/398309723\_Personalized\_Music\_Education\_a\_Systematic\_Review\_of\_AI\_Generation\_Methods](https://www.researchgate.net/publication/398309723_Personalized_Music_Education_a_Systematic_Review_of_AI_Generation_Methods)  
> 24. Balancing the Mental Load: Applying Cognitive Load Theory to, [https://medium.com/@andredpeele/balancing-the-mental-load-applying-cognitive-load-theory-to-collegiate-voice-lessons-and-242a68aa363b](https://medium.com/@andredpeele/balancing-the-mental-load-applying-cognitive-load-theory-to-collegiate-voice-lessons-and-242a68aa363b)  
> 25. (PDF) A Critical Examination of the Impact of Serious Games on, [https://www.researchgate.net/publication/392018209\_A\_Critical\_Examination\_of\_the\_Impact\_of\_Serious\_Games\_on\_Learning\_Music\_A\_Systematic\_Literature\_Review](https://www.researchgate.net/publication/392018209_A_Critical_Examination_of_the_Impact_of_Serious_Games_on_Learning_Music_A_Systematic_Literature_Review)  
> 26. A systematic literature review on serious games evaluation, [https://www.researchgate.net/publication/282635739\_A\_systematic\_literature\_review\_on\_serious\_games\_evaluation\_An\_application\_to\_software\_project\_management](https://www.researchgate.net/publication/282635739_A_systematic_literature_review_on_serious_games_evaluation_An_application_to_software_project_management)  
> 27. the associations among creative self-efficacy, flow state, learner, [https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1823846/full](https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1823846/full)  
> 28. Front-end deep learning web apps development and deployment, [https://pmc.ncbi.nlm.nih.gov/articles/PMC9709375/](https://pmc.ncbi.nlm.nih.gov/articles/PMC9709375/)  
> 29. (PDF) Convergence of Gamification and Machine Learning, [https://www.researchgate.net/publication/342882076\_Convergence\_of\_Gamification\_and\_Machine\_Learning\_A\_Systematic\_Literature\_Review](https://www.researchgate.net/publication/342882076_Convergence_of_Gamification_and_Machine_Learning_A_Systematic_Literature_Review)  
> 30. View of Protocol adaptations to conduct Systematic Literature, [https://revistas.unal.edu.co/index.php/ingeinv/article/view/46616/54822](https://revistas.unal.edu.co/index.php/ingeinv/article/view/46616/54822)  
> 31. (PDF) A New Technical Ear Training Game and Its Effect on Critical, [https://www.researchgate.net/publication/370316204\_A\_New\_Technical\_Ear\_Training\_Game\_and\_Its\_Effect\_on\_Critical\_Listening\_Skills](https://www.researchgate.net/publication/370316204_A_New_Technical_Ear_Training_Game_and_Its_Effect_on_Critical_Listening_Skills)  
> 32. Cognitive load theory and music instruction \- ResearchGate, [https://www.researchgate.net/publication/247513607\_Cognitive\_load\_theory\_and\_music\_instruction](https://www.researchgate.net/publication/247513607_Cognitive_load_theory_and_music_instruction)  
> 33. Rocking the Web With Browser-Based Simulations of Tube Guitar, [https://www.researchgate.net/publication/375817037\_Rocking\_the\_Web\_With\_Browser-Based\_Simulations\_of\_Tube\_Guitar\_Amplifiers](https://www.researchgate.net/publication/375817037_Rocking_the_Web_With_Browser-Based_Simulations_of_Tube_Guitar_Amplifiers)  
> 34. Effects of Modality and Redundancy Principles on the Learning and, [https://pdfs.semanticscholar.org/a5a1/e19016e823b68737959d0911ef1d0d27b40f.pdf](https://pdfs.semanticscholar.org/a5a1/e19016e823b68737959d0911ef1d0d27b40f.pdf)  
> 35. Digital game–based learning in music education: A systematic, [https://www.researchgate.net/publication/383618144\_Digital\_game-based\_learning\_in\_music\_education\_A\_systematic\_review\_from\_2011\_to\_2023](https://www.researchgate.net/publication/383618144_Digital_game-based_learning_in_music_education_A_systematic_review_from_2011_to_2023)  
> 36. BCI2000Web and WebFM: Browser-Based Tools for Brain Computer, [https://pmc.ncbi.nlm.nih.gov/articles/PMC6381053/](https://pmc.ncbi.nlm.nih.gov/articles/PMC6381053/)  
> 37. Effective Educational Videos: Principles and Guidelines for, [https://www.lifescied.org/doi/10.1187/cbe.16-03-0125](https://www.lifescied.org/doi/10.1187/cbe.16-03-0125)  
> 38. the associations among creative self-efficacy, flow state, learner, [https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1823846/pdf](https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1823846/pdf)  
> 39. Deterding et al. (2011) \- GBL@UZH, [https://www.gbl.uzh.ch/quartz/references/Deterding-et-al.-(2011)](https://www.gbl.uzh.ch/quartz/references/Deterding-et-al.-\(2011\))  
> 40. Optimizing cognitive load in Guzheng learning: a narrative review of, [https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1881131/full](https://www.frontiersin.org/journals/psychology/articles/10.3389/fpsyg.2026.1881131/full)  
> 41. The influence of background music on learners with varying, [https://www.researchgate.net/publication/329546583\_The\_influence\_of\_background\_music\_on\_learners\_with\_varying\_extraversion\_Seductive\_detail\_or\_beneficial\_effect](https://www.researchgate.net/publication/329546583_The_influence_of_background_music_on_learners_with_varying_extraversion_Seductive_detail_or_beneficial_effect)
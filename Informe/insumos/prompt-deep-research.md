# Prompt de investigación aportado por el autor

Texto aportado durante la entrevista metodológica del **2026-11-03**. La fecha
de ejecución de las consultas originales no se precisa aquí. Se conserva como
instrucción utilizada para orientar Gemini Deep Research, sin equiparar lo pedido
con la comprobación de que todos los pasos fueron ejecutados por la herramienta.
Las declaraciones sobre lecturas y revisión personal están en `../guia-metodologica.md`.

## Texto del prompt

```text
FASE 1: Metodología, Conceptos y Preguntas de Investigación (EJECUTA ESTO AHORA)
1. Objetivo general de la investigación
Realizar una revisión sistemática y un estado del arte sobre: Serious games, gamificación y tecnologías digitales aplicadas a la educación musical y entrenamiento auditivo, incluyendo el uso de IA/ML, procesamiento de señales y sistemas adaptativos, evaluando su efectividad y limitaciones.

2. Diferenciar conceptos
Analiza por separado y establece las fronteras entre: Serious Games, Gamificación, Game-Based Learning, Digital Game-Based Learning, Music Education Technology, Computer-Assisted Music Learning, Mobile/Interactive Music Learning, Adaptive Learning e Intelligent Tutoring Systems. Determina cuáles aplican a mi proyecto.

3. Revisión sistemática de literatura (SLR)
Diseña (sin ejecutar la búsqueda aún) el protocolo de revisión basándote en PRISMA o Kitchenham. Define: objetivo, preguntas de investigación (RQ), estrategia de búsqueda, bases de datos, términos booleanos, criterios de inclusión/exclusión y método de extracción.

4. Bases de datos y manejo de limitaciones
Como entiendo tus limitaciones para acceder al texto completo de bases de datos cerradas (IEEE, ACM, Scopus, Springer), utiliza tu conocimiento pre-entrenado y acceso a la web para identificar la literatura de acceso abierto o los abstracts más citados. Si durante la investigación encuentras un muro de pago, indícalo; yo podré buscar esos papers por mi cuenta y pasarte los abstracts para que los analices.

5. Antigüedad de las fuentes
Prioriza los últimos 10 años (especialmente 2020-2026 debido al auge de IA generativa, DSP móvil y aprendizaje adaptativo). Incluye trabajos seminales anteriores solo si son metodológicamente fundamentales.

6. Preguntas de investigación (RQs)
Proponé y justificá un set de RQs. Deben abarcar: habilidades musicales entrenadas (pitch, ritmo, intervalos, etc.), tecnologías utilizadas (DSP, ML, MIDI, mobile, web), mecánicas de juego empleadas, evidencia real de mejora del aprendizaje (separando percepción/engagement de adquisición objetiva de la habilidad) y limitaciones de los sistemas existentes.

FASE 2: Estado del Arte y Extracción de Datos (NO EJECUTAR AÚN)
7. Estado del arte tecnológico y comercial
Releva soluciones existentes (apps de ear training, prototipos académicos, software de conservatorio). Analiza su plataforma, público, tipo de feedback, gamificación, entrada (audio/MIDI) y si tienen validación científica.

8. Taxonomía y Comparación
Construye una taxonomía clasificando las soluciones (tradicionales vs. serious games vs. sistemas adaptativos/IA). Identifica combinaciones frecuentes y nichos vacíos.

9. Tecnologías de análisis musical (Enfoque en Viabilidad)
Investiga las tecnologías para ejercicios interactivos:

Pitch/Rhythm detection: CREPE, YIN, pYIN, FFT, onset detection.

Machine Learning: Modelos especializados en audio, CNNs, RNNs.

Arquitectura: Contrasta específicamente el costo computacional y viabilidad de realizar el procesamiento DSP y la inferencia ML client-side (ej. Web Audio API, TensorFlow.js, React Native) versus server-side.

10. Personalización y Feedback
Investiga el uso de adaptive difficulty, knowledge tracing y mastery learning en educación musical. Clasifica los tipos de feedback (visual, auditivo, explicativo) y la evidencia de su efectividad.

11. Gamificación vs. Aprendizaje
Analiza críticamente los riesgos de la gamificación superficial. Busca evidencia empírica sobre qué mecánicas generan engagement vs. cuáles realmente consolidan el aprendizaje a largo plazo sin causar frustración.

12. Extracción estructurada de papers (Regla Estricta)
Construye una tabla con los estudios más relevantes (Autor/año | Objetivo | Población | Habilidad | Tecnología | Gamificación | Método de Evaluación | Resultados | Limitaciones).
Regla de Escape: Si en un paper no puedes determinar un dato (ej. el tamaño de la muestra o el método exacto), escribe "No disponible en el abstract/texto accesible". Bajo ninguna circunstancia infieras o inventes un dato faltante.

FASE 3: Síntesis, Gaps y Propuestas de Tesis (NO EJECUTAR AÚN)
13. Síntesis y Evolución Temporal
Realiza una síntesis temática indicando tendencias, resultados contradictorios y limitaciones recurrentes. Construye una línea temporal relacionando la evolución pedagógica con las capacidades tecnológicas (desde primeros sistemas digitales hasta IA moderna).

14. Identificación de Gaps
Determina objetivamente y por separado:

Research gap: Lo que la academia no ha estudiado suficiente.

Technology gap: Limitaciones técnicas actuales.

Product gap: Necesidades de usuario no cubiertas.

15. Refutación de novedad
Intenta refutar la novedad de mi propuesta. Busca deliberadamente productos o tesis que ya hagan exactamente lo que propongo y señálalos.

16. Análisis para tesis de Ingeniería Informática y Viabilidad
Aterriza los hallazgos a mi disciplina. Analiza los desafíos de arquitectura, escalabilidad, procesamiento de audio e interacción. Evalúa qué partes (por tiempo, datasets y complejidad de ML/Mobile) deben quedar dentro del MVP de la tesis y cuáles deben descartarse.

17. Oportunidades de Tesis
Propón entre 5 y 10 posibles líneas de investigación/producto derivadas de los gaps, indicando para cada una su viabilidad técnica, riesgo y potencial de validación experimental.

INSTRUCCIONES CRÍTICAS (APLICAN A TODAS LAS FASES)
No confirmes mi hipótesis por complacencia. Busca evidencia que la contradiga.

No inventes papers, DOI, métricas, resultados ni estadísticas.

Diferencia claramente: Evidencia encontrada → Interpretación → Conclusión.

Para cada afirmación importante, indica el nivel de evidencia disponible (meta-análisis, prototipo, comercial, etc.).

Prioriza papers revisados por pares. Utiliza formato IEEE.
```

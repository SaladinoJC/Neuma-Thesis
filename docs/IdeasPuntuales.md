# Ideas Puntuales — Brainstorming App de Entrenamiento Musical

Notas de la sesión de grilling en base a `InvestigacionSistematica.md`. Decisiones tentativas, no cerradas — sesión exploratoria en curso.

**(C) Ear-training de intervalos por reproducción vocal:** el sistema reproduce un intervalo, el usuario lo canta de vuelta con el micrófono, se mide la afinación en cents en tiempo real.

Razones:
- Combina percepción (escuchar) + producción (cantar), más cercano al "aural skills" real de conservatorio que el simple drill de reconocimiento pasivo.
- Le da al DDA (Decisión 1-B) una razón pedagógica clara: ajustar la dificultad del intervalo objetivo según precisión de canto, no solo acierto/error binario.
- La voz es universal — no requiere que el usuario tenga un instrumento a mano, clave para una app mobile.
- La voz es la señal más "sucia" (ruido, vibrato, formantes), lo cual justifica con más fuerza la necesidad del multiplexor híbrido DSP↔ML (Decisión 1-A).

Implica: pitch **monofónico continuo únicamente** — descarta acordes, dictado armónico y polifonía instrumental (coherente con lo ya descartado en el documento base).

## Brainstorming — Minijuegos de entrenamiento musical/auditivo (sin filtrar todavía)

Lista amplia generada en sesión de brainstorming, mezclando básicos del mercado (marcados como tal) con variantes/ideas originales. Pendiente de pasar por el filtro de grilling (valor pedagógico vs. viabilidad técnica vs. potencial de gamificación).

### A. Percepción de intervalos y afinación

1. *(ya validada arriba)* Canto de intervalos con medición de cents en tiempo real.
2. **Intervalos "a ciegas" con distractor tímbrico**: se reproduce el intervalo con un timbre distinto cada ronda (piano, voz, sintetizador) para evitar que el usuario memorice por color de sonido en vez de por relación de altura.
3. **Interval Runner**: juego tipo "endless runner" donde saltar/agacharse/girar corresponde a intervalos ascendentes/descendentes que se escuchan justo antes del obstáculo — decisión bajo presión de tiempo, no reconocimiento pasivo tipo quiz.
4. **Afinación colaborativa (2 jugadores)**: cada uno canta una nota de un intervalo objetivo sin verse, el sistema muestra qué tan afinado quedó el intervalo combinado — entrena escucha "en conjunto", no solo individual.

### B. Acordes y armonía

5. **Constructor de acordes por voz (no piano)**: en vez de tocar teclas, el usuario canta las notas de un acorde una por una (arpegiado) y el sistema arma el acorde en tiempo real con lo cantado — mismo motor de pitch-tracking que el ítem A1, reutilizable.
6. **Detective de acordes ambiguos**: se presentan pares de acordes enarmónicamente confundibles (ej. Do7 vs Re♭maj7 en inversión) y el jugador debe justificar auditivamente cuál es cuál — apunta a error típico de oído "intermedio", no principiante.
7. **Progresión rota**: se reproduce una progresión de acordes con uno "fuera de lugar" (ajeno a la tonalidad) insertado en una posición aleatoria; el usuario debe marcar en qué compás ocurrió — entrena oído armónico funcional, no solo identificación aislada de acordes.

### C. Ritmo

8. **Eco rítmico con micrófono/tap**: el sistema toca un patrón, el usuario lo repite tocando/tapeando, se mide desviación en ms (no solo aciertos binarios) — mismo tipo de feedback continuo que en A1 pero en el dominio temporal.
9. **Rítmica por capas (loop layering)**: el usuario debe entrar tocando una subdivisión distinta (corchea/tresillo/semicorchea) sobre un click que sigue un patrón base, evaluando estabilidad de tempo al superponer complejidad polirrítmica.
10. **Clave oculta**: se reproduce un ritmo sincopado sin metrónomo audible; el usuario debe encontrar el pulso implícito tocando encima — entrena "sentir" el tiempo, no solo repetirlo.

### D. Melodía / dictado

11. **Dictado melódico por canto** (no por selección múltiple): el sistema toca una melodía corta, el usuario la canta de vuelta y se compara contorno + intervalos, no nota por nota exacta — más tolerante y más cercano a la práctica real de solfeo.
12. **Melodía rota / intrusa**: se toca una melodía conocida (o generada) con una nota alterada; el usuario marca en qué punto "algo sonó mal" sin necesariamente poder nombrar la nota — entrena detección temprana antes que nomenclatura.
13. **Call & response improvisado**: el sistema toca una frase corta en una escala/modo dado, el usuario debe responder con una frase propia dentro de la misma escala (validación de que las notas cantadas pertenecen al modo, no evaluación de "belleza") — acerca al terreno de improvisación, hueco que la investigación señala como desatendido por el mercado actual.

### E. Lectura y teoría (básicas del mercado, con posible vuelta de tuerca)

14. Reconocimiento visual de acordes en partitura — básico, ya existe en el mercado.
15. Reconocimiento de notas en distintas claves — básico, ya existe en el mercado.
16. Elegir notas en piano virtual para formar un acorde — básico, ya existe en el mercado.
17. **Vuelta de tuerca a las anteriores**: en vez de selección múltiple estática, un modo "contrarreloj creciente" donde la velocidad de aparición se adapta por DDA en tiempo real según tasa de acierto — convierte una mecánica trillada en el vehículo del núcleo ITS/adaptativo, en vez de una pantalla de quiz más.
18. **Traducción cruzada de clave**: se muestra una nota en clave de sol, el usuario debe cantarla y el sistema verifica que la frecuencia cantada corresponde a esa nota en clave de fa (o viceversa) — conecta lectura visual con producción auditiva real, no solo con un botón.

### F. Timbre (hueco señalado en la investigación: infravalorado en el DGBL actual)

19. **¿Qué instrumento es?**: reconocimiento tímbrico con distractores por familia (viento vs. viento, no viento vs. cuerda) para subir la dificultad progresivamente.
20. **Mismo tono, distinto timbre**: identificar si dos sonidos comparten la misma nota fundamental a pesar de tener timbres muy distintos — entrena separar altura de color tímbrico, confusión común en principiantes.
21. **EQ Detective**: se toca un sonido con un corte de frecuencia (ej. atenuación en agudos) y el usuario debe identificar qué banda fue alterada — más cercano a oído técnico/de ingeniería de audio que a oído "de conservatorio", conecta con la población de audio engineers mencionada en el PICOC.

### G. Memoria y estructura

22. **Simón musical armónico**: variante del clásico "Simon says" con notas de una escala, aumentando la secuencia — memoria auditiva pura, mecánica fácil de gamificar con progresión de dificultad clara.
23. **Reconstrucción de forma**: se escucha una pieza corta segmentada en frases (A-B-A, por ejemplo) desordenadas y hay que reordenarlas por oído — entrena forma musical, ausente en casi toda la oferta actual de ear-training.

### H. Multiplayer / social (gamificación real, no solo puntos)

24. **Duelo de oído en tiempo real**: dos usuarios reciben el mismo intervalo/acorde y compiten por responder correcto más rápido — leaderboard con consecuencia real de partida, no solo XP acumulativo.
25. **Modo "profesor por un día"**: un usuario avanzado genera el ejercicio (graba un intervalo/acorde/ritmo) y otros lo intentan adivinar — contenido generado por usuarios, reduce costo de contenido y da rol social distinto al de "alumno".

### I. Narrativa / progresión (con la salvedad de "seductive details" que marca la investigación)

26. **Mapa de habilidades no lineal** (tipo árbol de talentos) en vez de niveles secuenciales — gamificación estructural, no decorativa, evita el riesgo de carga cognitiva extrínseca señalado en RQ3.

## Pendiente de definir (próximas preguntas del grilling)

- Instrumento de entrada alternativo (¿se contempla input de instrumento real además de voz, a futuro?)
- Loop de juego concreto / mecánica de progresión
- Plataforma y stack (móvil nativo vs. web/PWA, dado que Oboe es nativo Android/iOS)
- Alcance de MVP vs. alcance de tesis (qué se implementa vs. qué se deja como trabajo futuro)
- Población objetivo (principiantes vs. avanzados/conservatorio)

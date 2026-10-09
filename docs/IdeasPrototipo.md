# Ideas para el Prototipo — Protocolo de Tesis

> Actualización 2026-10-02: el usuario eligió para la primera demostración Android (Kotlin/Compose), Simón y ¿Qué instrumento es?, un DDA Mamdani y una prueba de voz separada con YIN. Las fichas de esos dos juegos contienen las decisiones actualizadas. Las menciones posteriores a promedio móvil/umbrales describen la propuesta anterior; el resto del catálogo y su orden siguen siendo sugeridos. El funcionamiento compartido del motor tendrá indicadores y reglas específicos por juego, sin asumir que sus puntajes son intercambiables.

> Actualización 2026-10-09: después de probar v0.3 en el celular, el usuario pidió melodías breves generadas aleatoriamente para reconocimiento tímbrico. v0.3.1 cambia la frase en cada pregunta y conserva la misma en las reescuchas, con samples reales y gramática común a los instrumentos. La ficha 15 y [Melodias-instrumentos-v0.3.1.md](Melodias-instrumentos-v0.3.1.md) registran el cambio; los parámetros concretos del generador son provisionales.

Selección de 5 minijuegos para arrancar la implementación del primer
prototipo, pensada para la reunión con el director. Criterios de selección:
variedad de temas (sin repetir categoría ni mecánica) y una rampa de
complejidad deliberada, dejando el componente más riesgoso de la tesis
(captura de voz con multiplexor DSP↔ML) acotado a su versión mínima en vez de
required desde el día uno.

Cada ítem referencia su desarrollo completo en `Ideas Desarrolladas/`.

## Orden de implementación sugerido

### 1. Simón musical armónico — Memoria
`Ideas Desarrolladas/17-Simon-musical-armonico.md`

Sin captura de audio, sin DSP — solo botones, reproducción de audio y lógica
de secuencia creciente (tipo Simon). Primer vertical slice: sirve para
construir y validar de punta a punta el **motor DDA genérico** (ventana
móvil, override manual, telemetría) sin ningún riesgo de ingeniería de audio.
El resto de las ideas reutiliza este motor sin cambios.

### 2. ¿Qué instrumento es? — Timbre
`Ideas Desarrolladas/15-Reconocimiento-timbrico-que-instrumento-es.md`

Reproducción de samples + selección múltiple + curriculum de 3 niveles por
cercanía taxonómica. Sin captura de audio. Responde directamente a un hueco
documentado en la investigación (`InvestigacionSistematica.md`, §1.1, línea
407: marginación del control dinámico del timbre en la oferta actual) — buen
argumento para el protocolo desde el prototipo más temprano.

### 3. Defensa de acordes en partitura — Lectura
`Ideas Desarrolladas/12-Defensa-de-acordes-en-partitura.md`

El acorde se muestra en pentagrama; el jugador lo "escribe" (raíz + calidad,
teclado dedicado) antes de que un enemigo llegue a la base. Motor arcade
completo (spawn, vidas, DDA de dos capas: curriculum persistente + velocidad
intra-partida) y renderizado de notación musical, pero sigue sin captura de
audio. La más vistosa/demostrable de las cinco para una reunión con el
director.

### 4. Eco rítmico — Ritmo
`Ideas Desarrolladas/08-Eco-ritmico.md`

Primera idea con **captura real de señales de entrada** (tap), pero
mide tiempo/onset en vez de pitch continuo — arquitectónicamente mucho más
simple que el pipeline de intervalos (sin YIN ni multiplexor DSP↔ML). Tercer
pilar de arquitectura de la tesis, junto al multiplexor DSP↔ML y al DDA
genérico.

### 5. Ear-training de intervalos por reproducción vocal — Percepción de intervalos
`Ideas Desarrolladas/01-Ear-training-intervalos-reproduccion-vocal.md`

Un eje arquitectónico central para la tesis: multiplexor DSP↔ML, DDA por
varianza continua de cents. Para el prototipo, acotar a la **versión mínima**
(Nivel 1 — imitación de un solo intervalo), sin la escalera completa de 3
niveles todavía. Se deja último a propósito en la rampa de complejidad, pero
se incluye desde el primer prototipo porque es el componente de investigación
más novedoso y el director probablemente va a querer ver que está
contemplado desde el principio.

## Por qué esta selección

- **Variedad real de temas:** memoria, timbre perceptivo, lectura de
  partitura, ritmo, percepción de intervalos — 5 categorías distintas del
  brainstorming original, sin repetir mecánica.
- **Rampa de complejidad deliberada:** sin audio → receptivo con selección →
  arcade sin captura → captura de tiempo/onset → captura de voz con DSP↔ML.
- **Reutilización de arquitectura:** el motor DDA genérico construido en el
  primer ítem (Simón) lo reutilizan las otras cuatro ideas sin cambios — no
  es trabajo descartable, es la base de todo el catálogo.

## Alternativas descartadas en esta selección

- **EQ Detective (Idea 16):** reemplazado por la Defensa de acordes en
  partitura (Idea 12) — se prefirió cubrir el tema de lectura de partitura
  (ausente en el resto del set) y un prototipo más demostrable visualmente,
  aunque el EQ Detective sigue siendo una opción sólida dentro del MVP para
  una iteración posterior.
- **Progresión rota (Idea 7) y Melodía rota/intrusa (Idea 11):** evaluadas
  como alternativas más simples para el tercer puesto (armonía funcional y
  melodía respectivamente), descartadas a favor de la Idea 12 por cubrir un
  tema no representado (lectura) y ser más vistosa para la reunión con el
  director.

# 18. Mapa de habilidades no lineal

> Ítem 26 del brainstorming. Estructura de navegación entre minijuegos tipo
> "árbol de talentos", en vez de niveles secuenciales fijos — gamificación
> estructural (organiza el acceso al catálogo), no decorativa (sin narrativa
> ni "detalles seductores"). No es un minijuego nuevo: es la capa de
> navegación que organiza el acceso a todos los demás.

**Estado:** DESARROLLADA

## 18.1 Fundamento

**Anclaje directo en RQ3 de la investigación.** `InvestigacionSistematica.md`
(líneas 148-153) advierte explícitamente que elementos lúdicos como
"narrativas visuales inmersivas" o "gráficos vibrantes" pueden comportarse
como *seductive details* que "degradan el rendimiento instruccional
general", y que el uso de gamificación debe ser "restringido... como capa
periférica de motivación (progresión y retención), pero no como mecanismo
central de enseñanza" (línea 55). El ítem 26 ya anticipa esta tensión en su
propia formulación ("gamificación estructural, no decorativa") — esta idea
resuelve concretamente cómo lograrlo: un mapa funcional de progreso, sin capa
narrativa ni recompensas decorativas, que organiza el acceso al catálogo sin
competir por el canal atencional/visual del usuario durante el propio
aprendizaje musical (que ocurre dentro de cada minijuego, no en el mapa).

**Por qué esta idea es de naturaleza distinta a todas las demás del
catálogo.** Ninguna idea anterior (1-17) es un minijuego — es la capa de
**navegación entre** minijuegos. No introduce una habilidad musical nueva a
entrenar, ni un componente de captura/DSP: es un componente de arquitectura
de producto/UX, análogo en su rol a un "menú principal" con estado, pero
diseñado explícitamente como grafo no lineal en vez de lista secuencial.

**Alcance del grafo — RESUELTO: gobierna solo la navegación entre
minijuegos, sin tocar el curriculum interno de ninguno.** Cada una de las
Ideas 1, 6, 7, 8, 10, 11, 12-17 ya resolvió explícitamente un **orden fijo**
de su propio curriculum interno (intervalos, calidades de acorde, patrones
rítmicos, niveles de ajenidad, ejes de EQ, escalas, etc.), justificado en
cada caso por comparabilidad entre usuarios para la validación empírica de la
tesis. Esta idea no reordena nada de eso — el mapa es una capa por encima,
que decide **qué minijuegos están disponibles y en qué orden el usuario
elige engancharse con cada uno**, dejando el curriculum interno de cada uno
intacto y gobernado por su propio motor DDA ya resuelto.

**Sin prerrequisitos entre minijuegos — RESUELTO.** Todos los minijuegos del
MVP están disponibles desde el inicio, sin dependencias entre ellos. Cada uno
mide una habilidad relativamente independiente (percepción de intervalos,
armonía, ritmo, melodía, timbre, memoria, oído técnico) — imponer un orden
artificial entre ellos sería la misma gamificación decorativa que el ítem 26
busca evitar, solo que a nivel de meta-progresión en vez de nivel de
minijuego individual. La "no linealidad" de la idea está en que el usuario
**elige libremente** el orden en que se engancha con cada uno, no en que el
sistema imponga un camino.

**Rol dentro de la tesis — CONFIRMADO: dentro del MVP.** A diferencia de las
demás ideas dentro del MVP (1, 6, 8, 10, 15, 16, 17), esta no es opcional: es
la capa de acceso necesaria para presentar el catálogo de minijuegos del MVP
como un producto coherente, no una colección de pantallas sueltas. Es
también la instancia concreta de la "capa periférica de motivación" que RQ3
exige mantener acotada — su diseño reducido (sin narrativa, sin decoración)
es en sí mismo parte de cómo la tesis responde a esa pregunta de
investigación.

## 18.2 Base técnica

**Sin componente de DSP/ML ni motor DDA propio.** No introduce arquitectura
nueva de audio — es una capa de UI/estado que **lee** el progreso ya expuesto
por el motor DDA de cada minijuego individual (el `nivel_dificultad_actual`
o el curriculum desbloqueado que cada idea ya registra en sus datos por
ronda/partida), sin escribir ni modificar esos datos.

```
Catálogo de minijuegos del MVP (Ideas 1, 6, 8, 10, 15, 16, 17), agrupados
   por categoría (percepción de intervalos, armonía, ritmo, melodía,
   timbre, memoria — mismas secciones del brainstorming original)
   → Grafo de nodos sin prerrequisitos (todos desbloqueados desde el
        inicio) → cada nodo = un minijuego completo
   → Cada nodo lee el estado agregado de progreso de su minijuego (nivel de
        curriculum actual, ya expuesto por el DDA de esa idea) y lo muestra
        como indicador mínimo (ej. anillo de progreso), sin narrativa
   → El usuario selecciona un nodo → entra al minijuego elegido, que
        gobierna su propio curriculum interno de ahí en adelante
```

**Granularidad de nodos — RESUELTO: un nodo por minijuego completo, no por
paso de su curriculum interno.** Cada minijuego ya tiene su propio mecanismo
de progreso interno (niveles de andamiaje, ventanas de curriculum,
combinaciones desbloqueadas) — el mapa no debe duplicar esa granularidad
dentro del grafo global, solo mostrar el estado agregado de cada uno.
Profundizar en el detalle de progreso de un minijuego específico ocurre
dentro de ese minijuego, no en el mapa.

**Agrupamiento por categoría — RESUELTO: mismas secciones del brainstorming
original (`IdeasPuntuales.md`), sin inventar una taxonomía nueva:**
- **Percepción de intervalos y afinación:** Idea 1.
- **Acordes y armonía:** Idea 6.
- **Ritmo:** Idea 8.
- **Melodía / dictado:** Idea 10.
- **Timbre:** Ideas 15 y 16.
- **Memoria y estructura:** Idea 17.

No hay rama de "Lectura y teoría" todavía, porque las ideas de esa sección
(12-14) están clasificadas como trabajo futuro, fuera del MVP.

**Alcance del catálogo mostrado — RESUELTO: solo minijuegos dentro del MVP.**
El mapa que efectivamente se implementa en la tesis cubre únicamente las
ideas clasificadas dentro del MVP (1, 6, 8, 10, 15, 16, 17). Las ideas de
trabajo futuro (5, 7, 9, 11, 12, 13, 14) no forman parte del producto de
tesis, así que no corresponde mostrarlas como nodos (ni bloqueados ni
disponibles) en esta primera versión — evita generar expectativa sobre
contenido que no se va a entregar.

## 18.3 Diseño

**Visualización mínima, sin narrativa — RESUELTO.** Cada nodo muestra:
nombre del minijuego, categoría (color/ícono de familia), y un indicador de
progreso agregado simple (ej. anillo de progreso proporcional al nivel de
curriculum alcanzado dentro de ese minijuego). Sin misiones, sin historia,
sin personajes, sin texto narrativo — únicamente información funcional de
estado, coherente con el criterio de "gamificación estructural, no
decorativa" del ítem original y con la advertencia de RQ3 sobre *seductive
details*.

**Disposición del grafo — RESUELTO: agrupado por rama/categoría, sin
jerarquía de prerrequisitos entre nodos.** Visualmente, los nodos se
organizan en clusters por categoría (mismo agrupamiento de 18.2), pero dentro
y entre clusters no hay líneas de dependencia — es una disposición
organizativa, no un grafo de desbloqueo. Esto es lo que technically distingue
a esta idea de un "árbol de talentos" en el sentido estricto de videojuego
(que sí tiene prerrequisitos): se toma la metáfora visual del árbol/mapa no
lineal, pero sin la mecánica de gatekeeping que normalmente la acompaña,
coherente con la decisión de no imponer orden entre minijuegos.

**Relación con la gobernanza `origen: manual|dda` ya usada en cada
minijuego.** El mapa no introduce un mecanismo de gobernanza nuevo — cada
minijuego sigue gestionando su propio override manual y su propia ventana
móvil de curriculum. El mapa solo consume y muestra ese estado, sin
interferir.

**Extensibilidad — nota de diseño, no decisión de tuning.** Si en una
iteración futura de la tesis (fuera del alcance de esta sesión) se
incorporaran minijuegos de "trabajo futuro" al producto, se agregarían como
nuevos nodos en su rama de categoría correspondiente, sin requerir cambios
estructurales al grafo — el diseño ya contempla categorías (F, G, etc.) que
hoy tienen más de un nodo (Timbre: Ideas 15 y 16).

**Datos a registrar (telemetría de navegación, no de desempeño musical):**
`{timestamp, usuario_id, nodo_seleccionado (idea_id), origen_navegacion
(mapa), nivel_progreso_mostrado_en_nodo}` — mínimo, solo para analizar
patrones de elección entre minijuegos (qué categorías se eligen primero, si
hay abandono sistemático de alguna), no para gobernar dificultad (eso ya lo
hace cada minijuego internamente).

## 18.4 Decisiones de diseño — historial de resolución

Todas las decisiones quedaron resueltas en sesión de `/grill-me` (ver
18.1–18.3, marcadas **RESUELTO**/**CONFIRMADO**):

- Alcance del grafo: gobierna solo la navegación entre minijuegos, sin
  reordenar el curriculum interno ya resuelto de cada uno (Ideas 1, 6, 7, 8,
  10, 11, 12-17) — 18.1.
- Sin prerrequisitos entre minijuegos: todos disponibles desde el inicio,
  sin dependencias — 18.1.
- Granularidad de nodos: un nodo por minijuego completo, no por paso de
  curriculum interno — 18.2.
- Agrupamiento por categoría: mismas secciones del brainstorming original,
  sin taxonomía nueva — 18.2.
- Catálogo mostrado: solo minijuegos dentro del MVP (Ideas 1, 6, 8, 10, 15,
  16, 17); ideas de trabajo futuro no se muestran — 18.2.
- Visualización sin narrativa ni decoración: solo indicador de progreso
  agregado por nodo — anclado explícitamente en RQ3 — 18.3.
- Disposición del grafo: agrupada por categoría, sin jerarquía de
  prerrequisitos — 18.3.
- Alcance de tesis: dentro del MVP, como capa de acceso necesaria (no
  opcional) al catálogo de minijuegos del MVP — 18.1.

**Lo único que queda como valor de tuning** (a calibrar empíricamente, no
decisión de diseño): diseño visual concreto del indicador de progreso por
nodo (forma, umbral de "completo"); disposición espacial exacta de los
clusters por categoría.

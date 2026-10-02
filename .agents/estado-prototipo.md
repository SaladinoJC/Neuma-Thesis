# Estado del prototipo

Última actualización: 2026-10-02.

## Decisiones y origen

- **Confirmado por el usuario (2026-10-02):** comenzar a prototipar la tesis y preparar contexto persistente para las próximas sesiones.
- **Organización solicitada por el usuario (2026-10-02):** usar `Tesis/` como raíz del repositorio y reunir los documentos académicos en `docs/`. El código se ubicará en `Plataforma/`.
- **Configuración aprobada por el usuario (2026-10-02):** repositorio privado `SaladinoJC/Tesis`, GitHub Issues como gestor de tareas, las cinco etiquetas de triage predeterminadas y documentación de dominio single-context (`CONTEXT.md` + `docs/adr/`, creados cuando hagan falta). Aprobó aplicar el borrador, crear las etiquetas y subir el commit inicial.
- **Propuesta documental:** `docs/IdeasPrototipo.md` sugiere el orden Simón musical armónico → reconocimiento tímbrico → defensa de acordes en partitura → eco rítmico → intervalos por reproducción vocal. La selección completa y el orden siguen pendientes de confirmación.
- **Atribución de la ficha:** `docs/Ideas Desarrolladas/17-Simon-musical-armonico.md`, §17.1, registra la inclusión de Simón en el MVP como decisión explícita del usuario. Esto no confirma que sea el primer juego a implementar ni determina la plataforma.
- **Pendiente de comprobación técnica:** valores numéricos del DDA y referencias de Simón, samples concretos y permiso/procedencia, compatibilidad de build Android y mediciones en teléfono. El alcance funcional y los criterios de demostración ya fueron confirmados.
- **Entrevista grill-with-docs, ronda 1 (usuario, 2026-10-02):** objetivo inicial de demostración al director; primeros probadores: el usuario y el director. No hay fecha de reunión definida. El usuario quiere prototipar un par de minijuegos y un primer intento del DDA antes de mostrarlo. La propuesta de empezar por Simón fue aceptada como punto de partida; el segundo juego y el comportamiento del DDA siguen abiertos.
- **Ronda 2 (usuario, 2026-10-02):** Android para todo el proyecto; segundo juego ¿Qué instrumento es?; incluir prueba técnica de voz separada. El usuario cambió el DDA a lógica difusa, con adaptación intrapartida y desbloqueo por desempeño. Señaló que la adaptación intrapartida encaja menos en Simón que en reconocimiento tímbrico; su aplicación exacta en Simón sigue abierta.
- **Ronda 3 (usuario, 2026-10-02):** reconocimiento tímbrico tendrá partidas de 10 preguntas como valor inicial; el DDA variará las escuchas entre rondas, observando aciertos recientes y uso de reescuchas, sin penalizar tiempo de respuesta. Se mantienen cuatro opciones y el nivel de instrumentos de la partida. Los desbloqueos serán permanentes. Simón conserva escala y tónica fijas, crecimiento de una nota y fin al primer error; el DDA difuso solo desbloqueará escalas entre partidas en esta primera versión.
- **Ronda 4 (usuario, 2026-10-02):** Kotlin + Jetpack Compose con reglas de juego/DDA independientes de interfaz; inferencia Mamdani explicable; funcionamiento offline, un perfil local con resultados y progreso persistidos y opción de reinicio. Prueba de voz separada con YIN, tono/nota aproximada y desviación respecto de una nota objetivo; dispositivo inicial Samsung S22 Ultra del usuario. ML queda para una comparación posterior.
- **Valores iniciales aprobados, pendientes de calibración:** instrumentos comienza con 3 escuchas totales por pregunta, incluida la primera, y puede variar entre 1 y 3 para la siguiente ronda. Evaluar después de cada respuesta desde la tercera, sobre las últimas 3 rondas; cambios máximos de una escucha con dos rondas entre cambios. No retirar escuchas de una pregunta iniciada.
- **Ronda 5 (usuario, 2026-10-02):** modo automático inicia en el nivel más alto desbloqueado; dos partidas automáticas completas consecutivas calificadas como dominio alto en ese nivel desbloquean el siguiente, efectivo en la próxima partida. Simón puntúa por longitud máxima completada correctamente, conservando secuencia intentada y posición de error. Aciertos con reescuchas siguen siendo correctos, con menor evidencia de autonomía; acertar tiene mayor peso. Panel de inspección con selección manual de nivel, entradas, reglas, salida y ajuste; partidas manuales no desbloquean y casos simulados se distinguen de datos jugados.
- **Criterios de demostración aprobados:** partidas completas y progreso persistido al cerrar la app; aumento/reducción de ayuda y desbloqueo mediante casos reproducibles; prueba de voz en S22 Ultra con tono y desviación respecto del objetivo; comprobar estimador con señales de frecuencia conocida y silencio; exportar resultados/decisiones sin guardar grabaciones de voz inicialmente.
- **Cierre de entrevista (usuario, 2026-10-02):** confirmó la síntesis completa y pasar a `to-spec`/`to-tickets`. Se prepararon `docs/Especificacion-prototipo-v1.md` y `docs/Plan-tareas-prototipo-v1.md` como borradores concretos. Las skills exigen comprobar puntos de prueba y división de tickets antes de publicar; esa revisión es el próximo paso, no una reapertura de la entrevista.
- **Publicación aprobada y completada (2026-10-02):** el usuario aceptó los dos puntos de prueba y seis incrementos. Especificación #1 y tickets #2–#7 publicados en GitHub con `ready-for-agent`; dependencias nativas verificadas. Los parámetros numéricos conservan su condición de propuestas iniciales no calibradas.
- **Discrepancia documental explícita:** se agregaron actualizaciones confirmadas al inicio de las fichas de Simón y reconocimiento tímbrico, con precedencia sobre la gobernanza anterior por promedio móvil/umbrales y las escuchas fijas. Las reglas difusas concretas siguen abiertas. Esta dirección todavía no se incorporó al protocolo Word.

## Avance comprobado

- `Plataforma/` contiene solo `.gitkeep` para conservar la carpeta en Git. Todavía no hay aplicación, dependencias ni comandos de ejecución o pruebas.
- Se inicializó Git en la raíz `Tesis/` y se configuró `origin` como `https://github.com/SaladinoJC/Tesis.git`. El repositorio remoto es privado y tiene Issues habilitadas.
- La configuración de las skills está en `docs/agents/issue-tracker.md`, `docs/agents/triage-labels.md` y `docs/agents/domain.md`, referenciada desde `AGENTS.md`.
- `CONTEXT.md` contiene el glosario; dos ADR registran Android y Kotlin/Compose/Mamdani. Las fichas de los dos juegos y la selección de prototipo explicitan las decisiones actualizadas.
- La especificación y el plan publicado están en `docs/Especificacion-prototipo-v1.md` y `docs/Plan-tareas-prototipo-v1.md`; `.agents/prototipo-issues.json` conserva identificadores y dependencias para retomar sin crear duplicados. `docs/Notas-tecnicas-prototipo.md` recoge fuentes primarias y límites técnicos.
- Las cinco etiquetas aprobadas están disponibles en GitHub; `wontfix` ya existía y se conservó.
- Los materiales académicos y `Ideas Desarrolladas/` se trasladaron a `docs/`, conservando su organización interna. `README.md` describe la estructura y `.gitignore` excluye archivos temporales de Office y del sistema operativo.
- Se actualizó `AGENTS.md` y se creó este registro de continuidad.
- Se revisó el texto de `docs/PROTOCOLO_Saladino.doc` mediante extracción de su formato Word/OLE. «PRODUCTO ESPERADO» propone una plataforma móvil inicialmente Android y cinco juegos; «OBJETIVO DEL PROYECTO» plantea evaluación técnica de tono, latencia y DDA. No define un framework de interfaz ni confirma el orden de los juegos. Las atribuciones bibliográficas y capacidades técnicas del texto siguen sin verificar.

## Verificación de esta sesión

Se revisaron el listado de archivos, el `AGENTS.md` existente, `docs/IdeasPrototipo.md` y el inicio de la ficha de Simón (§17.1 y parte de §17.2). Se verificó mediante SHA-256 que los diez documentos trasladados desde la raíz conservaron su contenido y se actualizaron las rutas del contexto. Los archivos de bloqueo de Office se conservaron en su ubicación y se excluyeron de Git. No se verificaron referencias académicas ni el contenido del protocolo, y no se ejecutaron pruebas de software porque aún no hay código.

La configuración aprobada se escribió y se verificaron el remoto privado, Issues habilitadas y las cinco etiquetas. La revisión de espacios de los documentos originales señaló espacios finales existentes, incluidos saltos de línea Markdown; se conservó ese material sin reformatearlo.

Se verificaron siete issues publicadas, todas con `ready-for-agent`, cuerpos con saltos de línea reales y criterios de aceptación en cada ticket. Bloqueos comprobados por API: #3←#2, #4←#3, #5←#4, #6←#2, #7←#5/#6. La issue de especificación queda abierta como referencia; no es un bloqueo.

## Próximo paso sugerido

Iniciar `/implement` sobre [#2: Jugar una partida de Simón en Android](https://github.com/SaladinoJC/Tesis/issues/2), única tarea sin bloqueos. Leer cuerpo y comentarios completos, especificación #1, glosario y ADR antes de implementar. Cuando termine #2 se habilitan instrumentos (#3) y voz (#6), de manera independiente. No comenzó la implementación; `Plataforma/` sigue sin aplicación. Los valores iniciales del DDA/tono requerirán comprobación técnica y posterior calibración.

## Mantenimiento

Mantener este registro como una síntesis del estado actual. Reemplazar el avance y el próximo paso al completar trabajo; conservar las decisiones vigentes con su origen. Agregar los comandos de ejecución y las limitaciones observadas cuando exista una aplicación.

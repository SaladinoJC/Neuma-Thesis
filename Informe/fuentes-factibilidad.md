# Factibilidad: insumos y condiciones de redacción

Preparación y redacción: **2026-11-04**. Continuación autorizada por el autor
después del capítulo 5. Se aplica `thesis-writing`; el capítulo incorpora sus
respuestas sobre recursos, gastos, dedicación y acceso a posibles participantes.

## Pauta institucional

`referencias/2026_05_27_INSTRUCTIVO_TRABAJO FINAL.md`, páginas físicas 42–44:
el miniinforme es un análisis preliminar de viabilidad, con dimensiones técnica,
operativa, temporal, económica cuando corresponda, riesgos y conclusión.
La planificación detallada se desarrolla aparte, desde la página 44.

El capítulo 6 conservará la distinción entre análisis inicial y situación conocida
al escribir, con referencia al protocolo y al prototipo. No se describirá como
evaluación inicial una comprobación realizada después de comenzar el desarrollo.
El capítulo 5 presenta herramientas; el 6 debe explicar suficiencia de recursos,
condiciones y restricciones. El 7 conservará distribución de tareas y Gantt;
los resultados técnicos corresponden al 11.

## Originales consultados

Se leyó `docs/PROTOCOLO_Saladino.doc` mediante tabla de piezas OLE, sin modificarlo.
Se consultaron las tres hojas de `docs/Plan_de_trabajo_y_Gantt_Saladino.xlsx`
con openpyxl, `data_only=True`, sin recalcular fórmulas ni actualizar estados.
La identidad de las copias de Informe/docs ya está documentada en
`contraste-protocolo.md`.

### Protocolo

- Secciones revisadas: Análisis de factibilidad, Síntesis, Producto esperado,
  Plan de trabajo y FODA.
- La factibilidad original propone C++/Oboe, buffers sin bloqueos y DSP/ML;
  contiene garantías/cifras que no se adoptan como evidencia verificada.
- El FODA declara conocimiento musical, tiempo y red de colegas; experiencia
  limitada en audio en tiempo real y pruebas de uso; riesgos de micrófonos y ruido.
  Son declaraciones de planificación, no mediciones o aceptación de participantes.
- Fechas: inicio 01/08/2026, finalización 27/02/2027, informes 01/12/2026 y
  01/02/2027, 400 horas estimadas. Defensa marzo de 2027 provisional según autor.

### Plan adjunto

- Hoja «Carga semanal»: capacidad declarada **10–20 h/semana**. Debe contrastarse
  con disponibilidad actual del autor antes de concluir compatibilidad temporal.
- Totales: **30 h previas estimadas + 335 h previstas + 35 h de reserva = 400 h**.
  No representan medición de horas ni saldo actual de trabajo. La reserva es
  presupuesto de tiempo, no una tarea que necesariamente deba consumirse.
- «Detalle de tareas», F02/F09/F10: mínimo vocal + otro juego, tres adicionales
  opcionales. F03/F07: Oboe/ML condicionados a pruebas y tiempo.
- F14–F16: preparar/evaluar uso; el texto histórico «acceso a estudiantes confirmado»
  no implica que existan hoy participantes comprometidos, consentimientos o sesiones.
- F20/F21 y reserva finalizan el 20/02/2027, distinto del fin 27/02 del protocolo;
  no atribuir función al margen sin acuerdo. Desarrollo real y escritura comenzaron
  en orden distinto de las filas previstas; reconstrucción detallada para capítulo 7.

## Evidencia posterior

- `docs/evidencias/intervalos-vocales-v6/host-summary.json`: candidata 0.12.20,
  verificación de host del 30/10, construcción y reglas comprobadas; Android nuevo
  sin ejecutar, sin instalación. Ruta sintética sin voz física ni estudiantes.
  Puede apoyar existencia de una base técnica implementada, no suficiencia acústica.
- `docs/Guia-pruebas-manuales-S22-Ultra.md`: guía preparada el 03/11, sin resultados
  físicos registrados. El modelo previsto no sustituye confirmar equipos disponibles.
- `docs/Hoja-avances-reunion-director-2026-11-06.md`: resumen para reunión y asuntos
  por acordar; no constituye acta de acuerdos o comprobación nueva.
- `docs/Entrega-GitHub-v0.12.20.md`: respaldo preliminar privado. No demuestra
  publicación abierta final; licencia/auditoría de recursos quedan por cerrar.
- Capítulos 1/2/4/5 y ADR: alcance, evaluación exploratoria, plataforma nativa y
  decisiones de herramientas. Evitar repetir sus explicaciones en el 6.

## Información solicitada al autor

Tres preguntas asincrónicas al iniciar la preparación:

1. Computadora y teléfonos disponibles, preexistencia y compras específicas.
2. Gastos: suscripciones/servicios, pago propio o cubierto por terceros,
   preexistencia y preferencia por importes aproximados o descripción sin cifras.
3. Horas semanales actuales, vigencia de marzo de 2027 como fecha provisional y
   acceso real a personas a quienes proponer pruebas.

### Respuesta parcial del autor — 2026-11-04

- Notebook MSI Thin GF63, 16 GB RAM DDR4, Intel Core i5-10500H y NVIDIA RTX 3050
  con 4 GB de memoria gráfica. Características declaradas por el autor; no se
  ejecutó un inventario de hardware ni un benchmark en esta sesión.
- Teléfono Samsung S22 Ultra. No se informa versión de Android, variante exacta
  ni resultados nuevos de pruebas físicas.
- Ambos equipos eran preexistentes; no compró equipamiento para el proyecto.
  Esto permite registrar ausencia de compras específicas, sin atribuir costo
  económico nulo a los recursos ni estimar amortización.
- Actualmente paga mensualmente USD 20 por el plan que identifica como Codex y
  USD 30 por Grok: USD 50 mensuales en conjunto. Importes declarados, no tarifas
  comerciales verificadas ni costo acumulado. No se informó desde qué mes,
  si se utilizan exclusivamente para el proyecto, ni impuestos/tipo de cambio.

### Disponibilidad y fechas confirmadas — 2026-11-04

El autor declara aproximadamente **12 horas semanales**, mantiene **fines de
febrero de 2027** como fecha prevista de finalización y **marzo de 2027** para
defensa, y cuenta con personas a quienes proponer pruebas. Esto respalda una vía
de convocatoria, sin atribuir compromisos, número de participantes o sesiones
ya realizadas. Las fechas se conservan como previsiones, sin aprobación
administrativa o del director inferida.

La proyección temporal del capítulo toma el 27/02/2027 del protocolo como extremo:
desde el 04/11/2026 son 115 días, 16,43 semanas y 197,14 horas a 12 h/semana.
Se expresa como **aproximadamente 200 horas potenciales**, sin descontar pausas,
feriados u otras obligaciones y para el conjunto del trabajo, no solo desarrollo.
Es una estimación derivada de fechas y dedicación declarada, no una medición o
garantía de capacidad. No se calcula trabajo restante restando avances a las
335 horas iniciales: no existe un registro de horas reales que lo permita.

No inferir costos nulos por usar bibliotecas abiertas ni atribuir al proyecto el
precio completo de equipos preexistentes. Distinguir gasto incremental de recursos
disponibles y dedicación personal. Si se usan cifras, registrar moneda, período,
fecha y origen; no tomar tarifas comerciales actuales como gastos efectivamente
pagados por el autor.

## Criterios de identificación de riesgos

Audio/ruido/dispositivos; calibración del DDA; experiencia Android/audio limitada;
dependencia de asistentes y revisión; ampliación de alcance; disponibilidad de
participantes y revisiones; cierre documental y publicación/licencias. Las medidas
de mitigación pueden formularse como propuestas técnicas del agente y deben
separarse de acciones ejecutadas. No asignar probabilidades o impactos cuantitativos
sin un método y datos que los respalden.

## Tratamiento en el capítulo 6

Se redactan seis secciones: factibilidad técnica, operativa, temporal, económica,
riesgos y síntesis. Se preserva el análisis inicial del protocolo como antecedente,
sin atribuirle resultados posteriores. Las comprobaciones actuales solo sostienen
la existencia de una base implementada; condiciones acústicas, calibración y
evaluación de uso siguen pendientes.

La tabla de riesgos y medidas es una síntesis técnica propuesta por el agente
desde los artefactos y las restricciones declaradas; no es una valoración
cuantitativa ni un plan aprobado por el director. Evita probabilidades inventadas
y distingue acciones previstas de las ejecutadas. La conclusión es condicional:
recursos disponibles para continuar, con cierre sujeto a alcance, mediciones,
pruebas, escritura y revisiones.

No se agregan fuentes externas, tarifas comerciales ni bibliografía: los insumos
son documentos del proyecto, evidencia local y declaraciones directas. Se mantienen
las 60 referencias citadas existentes. El capítulo no modifica el cronograma
original ni sustituye la reconstrucción del capítulo 7.

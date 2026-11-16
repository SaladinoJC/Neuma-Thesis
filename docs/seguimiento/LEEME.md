# Registro de dedicación al Trabajo Final

Decisión del autor, **2026-11-04**: conservar el Gantt original, registrar horas
anteriores y de próximas sesiones y comparar planificación/ejecución al cierre
del informe en **febrero de 2027**. Actualmente no tiene vacaciones previstas.

## Fuente y tipos de datos

`horas.csv` es la única tabla de dedicación. `desde` y `hasta` son fechas inclusivas;
`horas` admite decimales con punto y queda vacío si falta el dato. Los tipos son:

- `estimacion_retrospectiva`: aproximación reconstruida con método y fuente.
- `informado`: tiempo efectivo declarado por el autor para una sesión/período.
- `medido`: tiempo personal registrado con inicio, fin y pausas explícitos.
- `pendiente`: actividad conocida cuyo tiempo no está determinado; no suma horas.

El campo `tareas_plan` conserva IDs del XLSX solo cuando la actividad puede
relacionarse sin inferir un reparto de horas. Puede contener varios IDs separados
por `|`; no implica una asignación de horas a cada tarea. Registrar actividades
fuera del catálogo sin forzar su correspondencia.

## Estimación inicial

El autor indica que dedica aproximadamente 12 h/semana desde el comienzo de la
implementación. Git conserva el primer código en `24afc0c`, **02/10/2026 16:10:31
-03:00**, con altas de Gradle, manifest y clases Kotlin. La creación local de
Gradle/manifest figura el mismo día 15:39:20; se usa Git como ancla durable.
La fecha local puede cambiar por copias/restauraciones y no se usa para medir
dedicación. El commit previo `3021587` del mismo día documenta la definición de
la primera demostración; `e8f5b25` contiene organización/documentación.

Se registran cuatro semanas de 12 h y cinco días proporcionales (12 × 5/7),
desde el **02/10 hasta el 03/11**, con corte al iniciar el 04/11: **56,57 h
estimadas**, aproximadamente 57 h. Es una extrapolación del ritmo declarado,
sin medición por sesión ni reparto por actividad. Las horas de investigación
anteriores al 02/10 quedan por reconstruir; las 30 h previas del plan son otra
estimación histórica y no se suman automáticamente a esta tabla.

### Incorporación y correcciones solicitadas el 04/11

El autor solicita incorporar las horas previas del Gantt: P01 9 h, P02 7 h,
P03 5 h, P04 5 h y P05 4 h, con sus fechas originales (agosto–septiembre).
Se agregan H0007–H0011 como `estimacion_retrospectiva`, 30 h en total; incluyen
investigación, análisis, fichas y arquitectura/borrador del protocolo. Los IDs
existentes se conservan y las filas nuevas se colocan antes por cronología.
Las fechas de actividades diferentes pueden superponerse sin que una fila sea
el subtotal de las otras; cada P se incorpora una sola vez.

También corrige H0003 a **15 h** y H0004 a **20 h** porque dedicó más tiempo
esas semanas. Ambas pasan a `informado`: reemplazan las estimaciones de 12 h,
sin convertirse en mediciones con reloj. Esta actualización sustituye el
subtotal inicial descrito arriba; los importes vigentes se consultan en CSV.

## Procedimiento para próximas sesiones

1. Al comenzar, consultar la última fecha y los registros pendientes; conservar
   la continuidad del período. Una sesión que cruza medianoche puede registrarse
   con ambas fechas y una sola duración, o dividirse si se conoce el desglose.
2. Al cerrar, registrar actividad, fecha, relación con el plan si se conoce y
   tiempo efectivo informado. Cuando falte, dejar `horas` vacío y `tipo=pendiente`;
   pedirlo una vez al cerrar o retomar, sin reiterar la consulta en cada turno.
3. Para medir una sesión, distinguir el tiempo personal del autor de la ejecución
   de herramientas/agentes. Registrar inicio/fin/pausas si el autor decide usar
   esa modalidad. El tiempo de conversación o entre commits no equivale por sí
   solo a dedicación personal.
4. Al recibir el dato faltante, completar la misma fila. Si corrige un período
   estimado, reemplazarlo o dividirlo antes de agregar sesiones del mismo período,
   para evitar doble conteo. Conservar el motivo de la corrección en `notas`;
   Git conserva la versión anterior.
5. Mantener los subtotales por tipo. El análisis de desvíos, porcentajes de avance
   y comparación Gantt/ejecución se hará en febrero, según decisión del autor.

No convertir la duración de compilaciones, la actividad de commits o estimaciones
de los agentes en horas medidas del autor. Tampoco asignar cero a una fila pendiente.

Consulta reproducible de los subtotales desde la raíz:

```powershell
python -c "import csv,collections; from decimal import Decimal; t=collections.defaultdict(Decimal); rows=list(csv.DictReader(open('docs/seguimiento/horas.csv',encoding='utf-8'))); [t.__setitem__(r['tipo'], t[r['tipo']]+Decimal(r['horas'])) for r in rows if r['horas']]; print(dict(t)); print('Pendientes:',sum(r['tipo']=='pendiente' for r in rows))"
```

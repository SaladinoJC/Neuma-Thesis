# Planificación: original, horas y comparación diferida

Fecha: **2026-11-04**. Continuación de escritura autorizada después de Factibilidad.

## Decisión vigente del autor

Durante la preparación pidió **no comparar todavía Gantt/ejecución**, porque
el desarrollo continúa; esa comparación queda para el cierre del informe en
**febrero de 2027**. Pidió registrar horas anteriores y futuras para facilitarla.
No tiene vacaciones previstas. Se retira la intención inicial del agente de
preparar una comparación actual y una propuesta de replanificación.

Ante la consulta de fecha de comienzo del ritmo de 12 h/semana, el autor indica
que fue aproximadamente desde el inicio de implementación y pide buscarlo en
el repositorio. Esto autoriza una reconstrucción estimada, no atribuir horas
medidas o transformar timestamps en dedicación efectiva.

## Fuentes consultadas

- Instructivo, páginas físicas 44–47: planificación, Gantt, actividades y camino
  crítico. Los ejemplos no aportan duraciones ni resultados de este proyecto.
- `docs/Plan_de_trabajo_y_Gantt_Saladino.xlsx`, tres hojas, leído mediante
  openpyxl con `data_only=True`, sin recalcular ni modificar celdas.
- `docs/PROTOCOLO_Saladino.doc`, fechas e hitos contrastados en la lectura de
  Factibilidad; procedencia en `fuentes-factibilidad.md` y `contraste-protocolo.md`.
- El plan prevé 27 filas con ID: cinco P, veintiuna F y reserva R01. Suma 400 h,
  desglosadas 30/335/35. Las tareas terminan el 20/02/2027 y el protocolo el 27/02;
  no se atribuye una función no documentada a esos siete días.

## Representación del Gantt

`tools/generar_gantt_original.py` lee «Detalle de tareas» y genera dos PDF
vectoriales, dos SVG y un JSON con filas, fuente y SHA-256 en `figuras/`.
Conserva intervalos y tipos del plan original; las etiquetas se abrevian sin
alterar los nombres completos conservados en el JSON.

- Barras con fecha final incluida; misma escala agosto–febrero en ambas partes.
- P: actividades previas estimadas según el archivo, sin certificación actual.
- F: previstas. R01: reserva presupuestada, no horas consumidas.
- No barras reales, porcentajes, diferencias ni reparto retrospectivo por tarea.
- El script comprueba IDs únicos, referencias a dependencias y total de 400 h.
- El original conserva el SHA-256 registrado en `contraste-protocolo.md`.

La sección de camino crítico conserva dependencias expresas F06/F08→F09,
F09/F10/F12→F13, F13/F14→F15, F15→F16 y F07/F13→F17; F20 y F21 reúnen resultados.
No se inventa un CPM con holguras: el plan no explicita tipos de precedencia y
hay intervalos superpuestos. Se declara pendiente su formalización al cierre.

## Ancla de comienzo y estimación de horas

Consultas: `git log --reverse -- Plataforma`, altas históricas de manifest,
Gradle y clases Kotlin; fechas de creación locales, como comprobación secundaria.

| Evidencia | Fecha/hora (-03:00) | Interpretación |
| --- | --- | --- |
| `e8f5b25` | 02/10/2026 14:52:23 | Organización y documentación; no primer código |
| `3021587` | 02/10/2026 15:31:40 | Definición de demostración y plan |
| Alta local de Gradle y manifest | 02/10/2026 15:39:20 | Coincide con el día; timestamps susceptibles de copias/restauraciones |
| `24afc0c` | 02/10/2026 16:10:31 | Primer código versionado: manifest, Gradle, MainActivity, audio y Simón |

Se adopta el **02/10** como ancla documental y se aplica el ritmo aproximado
de 12 h/semana indicado por el autor hasta el **03/11**, excluyendo la jornada
actual para evitar doble conteo al registrarla. Son 33 días = 4 semanas + 5 días:
48 + 12 × 5/7 = **56,57 h estimadas**. Las semanas estimadas no prueban
actividad uniforme. No se suman las 30 h previas del plan como horas medidas.

Tabla única: `docs/seguimiento/horas.csv`. Reglas, consulta de subtotales y
procedimiento futuro: `docs/seguimiento/LEEME.md`. La jornada del 04/11 tiene
entrada pendiente de tiempo informado. AGENTS incorpora un enlace de inicio/
cierre de sesión para mantener la continuidad, mediante `writing-for-agents`.

No se agregan referencias externas ni resultados al informe. La dedicación real
de cada tarea y la comparación original/ejecutado permanecen abiertas hasta el
cierre; este incremento prepara los insumos.

## Actualización del registro por pedido del autor — 2026-11-04

El autor solicita incorporar P01–P05 del Gantt como horas previas estimadas
(9/7/5/5/4, total 30) y corregir H0003/H0004 a 15/20 h porque trabajó más esas
semanas. Se conservan fechas e IDs, agregando H0007–H0011 para las tareas previas.
Las correcciones semanales se tipifican como tiempo informado y reemplazan los
12 h iniciales. Las fechas P se superponen por actividades diferentes; no se
incluyen subtotales duplicados.

Tras la corrección: **62,57 h estimadas + 35 h informadas = 97,57 h registradas**,
con H0006 todavía pendiente. El subtotal inicial de 56,57 h conserva su carácter
histórico; el registro vigente es `docs/seguimiento/horas.csv`. No se realiza
comparación con la previsión ni se modifica el Gantt o el texto LaTeX.

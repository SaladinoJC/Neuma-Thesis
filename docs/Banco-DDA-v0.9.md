# Banco DDA · Datos simulados
Configuración: dda-bench-v1. Sin audio, historial ni progreso reales.

| Comprobación | Esperado | Observado | Resultado |
| --- | --- | --- | --- |
| Dos altas consecutivas · Simón | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Dos altas consecutivas · Instrumentos | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Alta, baja, alta, alta · Simón | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Alta, baja, alta, alta · Instrumentos | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Aciertos con ayuda · Simón | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Aciertos con ayuda · Instrumentos | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Errores sostenidos · Simón | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Errores sostenidos · Instrumentos | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Alternancia de resultados · Simón | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Alternancia de resultados · Instrumentos | Progress(level=0, streak=0) | Progress(level=0, streak=0) | Cumple |
| Interrupción entre altas · Simón | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Interrupción entre altas · Instrumentos | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Práctica manual entre altas · Simón | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Práctica manual entre altas · Instrumentos | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Recorrido hasta último nivel · Simón | Progress(level=2, streak=2) | Progress(level=2, streak=2) | Cumple |
| Recorrido hasta último nivel · Instrumentos | Progress(level=2, streak=2) | Progress(level=2, streak=2) | Cumple |
| Resultado repetido sin doble avance · Simón | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Resultado repetido sin doble avance · Instrumentos | Progress(level=1, streak=0) | Progress(level=1, streak=0) | Cumple |
| Manuales/interrupciones aislados | Sin cambios | 6 pasos excluidos | Cumple |
| Límites de nivel/racha | 0..2 | 56 pasos | Cumple |
| Ayuda sin salida válida | Mantener 3 escuchas | 8 decisiones, sin reducción=true | Cumple |

## Fronteras de dominio · Parámetros sin calibrar
| Juego/nivel | Caso | Centroide | Alto |
| --- | --- | --- | --- |
| simon/1 | 0 notas completadas | 0.14090848484848492 | false |
| simon/1 | 1 notas completadas | 0.14090848484848492 | false |
| simon/1 | 2 notas completadas | 0.24842111438261238 | false |
| simon/1 | 3 notas completadas | 0.5000000000000002 | false |
| simon/1 | 4 notas completadas | 0.7515788856173896 | true |
| simon/1 | 5 notas completadas | 0.8590915151515149 | true |
| simon/1 | 6 notas completadas | 0.8590915151515149 | true |
| simon/2 | 0 notas completadas | 0.14090848484848492 | false |
| simon/2 | 1 notas completadas | 0.14090848484848492 | false |
| simon/2 | 2 notas completadas | 0.14090848484848492 | false |
| simon/2 | 3 notas completadas | 0.2978080274013583 | false |
| simon/2 | 4 notas completadas | 0.5000000000000008 | false |
| simon/2 | 5 notas completadas | 0.6131972996320421 | false |
| simon/2 | 6 notas completadas | 0.8590915151515149 | true |
| simon/2 | 7 notas completadas | 0.8590915151515149 | true |
| simon/2 | 8 notas completadas | 0.8590915151515149 | true |
| simon/3 | 0 notas completadas | 0.14090848484848492 | false |
| simon/3 | 1 notas completadas | 0.14090848484848492 | false |
| simon/3 | 2 notas completadas | 0.14090848484848492 | false |
| simon/3 | 3 notas completadas | 0.14090848484848492 | false |
| simon/3 | 4 notas completadas | 0.3230054395196871 | false |
| simon/3 | 5 notas completadas | 0.5000000000000002 | false |
| simon/3 | 6 notas completadas | 0.5315984110668677 | false |
| simon/3 | 7 notas completadas | 0.7126295582677356 | false |
| simon/3 | 8 notas completadas | 0.8590915151515149 | true |
| simon/3 | 9 notas completadas | 0.8590915151515149 | true |
| simon/3 | 10 notas completadas | 0.8590915151515149 | true |
| instruments/1 | 0 aciertos; 0 autónomos | 0.14090848484848492 | false |
| instruments/1 | 1 aciertos; 0 autónomos | 0.14090848484848492 | false |
| instruments/1 | 1 aciertos; 1 autónomos | 0.14090848484848492 | false |
| instruments/1 | 2 aciertos; 0 autónomos | 0.14090848484848492 | false |
| instruments/1 | 2 aciertos; 1 autónomos | 0.14090848484848492 | false |
| instruments/1 | 2 aciertos; 2 autónomos | 0.14090848484848492 | false |
| instruments/1 | 3 aciertos; 0 autónomos | 0.14090848484848492 | false |
| instruments/1 | 3 aciertos; 1 autónomos | 0.14090848484848492 | false |
| instruments/1 | 3 aciertos; 2 autónomos | 0.14090848484848492 | false |
| instruments/1 | 3 aciertos; 3 autónomos | 0.14090848484848492 | false |
| instruments/1 | 4 aciertos; 0 autónomos | 0.24842111438261238 | false |
| instruments/1 | 4 aciertos; 1 autónomos | 0.24842111438261238 | false |
| instruments/1 | 4 aciertos; 2 autónomos | 0.24842111438261238 | false |
| instruments/1 | 4 aciertos; 3 autónomos | 0.24842111438261238 | false |
| instruments/1 | 4 aciertos; 4 autónomos | 0.24842111438261238 | false |
| instruments/1 | 5 aciertos; 0 autónomos | 0.41000060646491765 | false |
| instruments/1 | 5 aciertos; 1 autónomos | 0.41000060646491765 | false |
| instruments/1 | 5 aciertos; 2 autónomos | 0.41000060646491765 | false |
| instruments/1 | 5 aciertos; 3 autónomos | 0.41000060646491765 | false |
| instruments/1 | 5 aciertos; 4 autónomos | 0.41000060646491765 | false |
| instruments/1 | 5 aciertos; 5 autónomos | 0.41000060646491765 | false |
| instruments/1 | 6 aciertos; 0 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 1 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 2 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 3 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 4 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 5 autónomos | 0.5000000000000002 | false |
| instruments/1 | 6 aciertos; 6 autónomos | 0.5000000000000002 | false |
| instruments/1 | 7 aciertos; 0 autónomos | 0.5000000000000022 | false |
| instruments/1 | 7 aciertos; 1 autónomos | 0.5000000000000022 | false |
| instruments/1 | 7 aciertos; 2 autónomos | 0.5000000000000022 | false |
| instruments/1 | 7 aciertos; 3 autónomos | 0.5899993935350856 | false |
| instruments/1 | 7 aciertos; 4 autónomos | 0.5899993935350856 | false |
| instruments/1 | 7 aciertos; 5 autónomos | 0.5899993935350856 | false |
| instruments/1 | 7 aciertos; 6 autónomos | 0.5899993935350856 | false |
| instruments/1 | 7 aciertos; 7 autónomos | 0.5899993935350856 | false |
| instruments/1 | 8 aciertos; 0 autónomos | 0.5000000000000003 | false |
| instruments/1 | 8 aciertos; 1 autónomos | 0.5000000000000003 | false |
| instruments/1 | 8 aciertos; 2 autónomos | 0.5000000000000003 | false |
| instruments/1 | 8 aciertos; 3 autónomos | 0.5371305021793858 | false |
| instruments/1 | 8 aciertos; 4 autónomos | 0.7177484975453367 | false |
| instruments/1 | 8 aciertos; 5 autónomos | 0.7515788856173896 | true |
| instruments/1 | 8 aciertos; 6 autónomos | 0.7187427961579487 | false |
| instruments/1 | 8 aciertos; 7 autónomos | 0.7515788856173896 | true |
| instruments/1 | 8 aciertos; 8 autónomos | 0.7515788856173896 | true |
| instruments/1 | 9 aciertos; 0 autónomos | 0.5000000000000002 | false |
| instruments/1 | 9 aciertos; 1 autónomos | 0.5000000000000002 | false |
| instruments/1 | 9 aciertos; 2 autónomos | 0.5000000000000002 | false |
| instruments/1 | 9 aciertos; 3 autónomos | 0.5000000000000002 | false |
| instruments/1 | 9 aciertos; 4 autónomos | 0.6276640358404001 | false |
| instruments/1 | 9 aciertos; 5 autónomos | 0.8536360244837508 | true |
| instruments/1 | 9 aciertos; 6 autónomos | 0.8507915254237345 | true |
| instruments/1 | 9 aciertos; 7 autónomos | 0.8476923674779466 | true |
| instruments/1 | 9 aciertos; 8 autónomos | 0.8590915151515149 | true |
| instruments/1 | 9 aciertos; 9 autónomos | 0.8590915151515149 | true |
| instruments/1 | 10 aciertos; 0 autónomos | 0.5000000000000002 | false |
| instruments/1 | 10 aciertos; 1 autónomos | 0.5000000000000002 | false |
| instruments/1 | 10 aciertos; 2 autónomos | 0.5000000000000002 | false |
| instruments/1 | 10 aciertos; 3 autónomos | 0.5000000000000002 | false |
| instruments/1 | 10 aciertos; 4 autónomos | 0.5688268469178364 | false |
| instruments/1 | 10 aciertos; 5 autónomos | 0.7177484975453367 | false |
| instruments/1 | 10 aciertos; 6 autónomos | 0.8590915151515149 | true |
| instruments/1 | 10 aciertos; 7 autónomos | 0.8463939890710434 | true |
| instruments/1 | 10 aciertos; 8 autónomos | 0.8513306906125416 | true |
| instruments/1 | 10 aciertos; 9 autónomos | 0.8590915151515149 | true |
| instruments/1 | 10 aciertos; 10 autónomos | 0.8590915151515149 | true |
Las fronteras registran el comportamiento actual; no equivalen a dominio musical validado.

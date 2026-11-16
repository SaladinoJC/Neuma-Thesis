# Figuras del informe

Estos recursos pertenecen al informe académico, no a la interfaz de Neuma.

| Recursos | Propósito y formato | Origen / estado / dimensiones |
| --- | --- | --- |
| `gantt-original-1.pdf`, `gantt-original-2.pdf` y sus `.svg` | Gantt original dividido para lectura; PDF vectorial y SVG editable | Elaboración propia mediante `../tools/generar_gantt_original.py` desde el XLSX entregado; 8 × 5 pulgadas por figura; refleja previsión histórica, sin avance real |
| `gantt-original-datos.json` | Extracción con fechas, horas, dependencias y hash del original | Datos de «Detalle de tareas»; permite comprobar la correspondencia sin modificar el XLSX |
| `casos-uso.pdf`, `casos-uso.svg` | Diagrama UML de casos de uso del capítulo 8 | Elaboración propia mediante `../tools/generar_casos_uso.py`; PDF vectorial/SVG editable, 8 × 7,3 pulgadas; seis casos y dos roles, alcance propuesto del 04/11 |
| `arquitectura-neuma.pdf`, `.svg` | Colaboraciones lógicas de la arquitectura | Elaboración propia desde código y ADR mediante `../tools/generar_diseno.py`; 8 × 6,5 pulgadas; vista de la implementación 0.12.20, no diagrama de despliegues independientes |
| `dominio-neuma.pdf`, `.svg` | Relaciones conceptuales del dominio | Elaboración propia desde glosario y modelos de actividad con el mismo script; 8 × 7 pulgadas; no representa una base de datos o una jerarquía de clases compartida |

Regenerar desde la raíz con `python Informe/tools/generar_gantt_original.py`.
Requiere matplotlib y openpyxl. Las etiquetas de tareas se abrevian para la figura;
los nombres completos y estados del original permanecen en JSON/XLSX.
Las barras incluyen el último día de cada intervalo. La reserva identifica
presupuesto de tiempo, sin atribuir trabajo realizado.

El diagrama de casos se regenera con `python Informe/tools/generar_casos_uso.py`.
Las asociaciones no indican secuencia ni permisos/cuentas de acceso. Su contenido
debe mantenerse consistente con las especificaciones textuales CU-01–CU-06.

Las vistas del capítulo 9 se regeneran con `python Informe/tools/generar_diseno.py`.
No contienen recursos de terceros; los SVG y el generador conservan el editable.

# Requerimientos: procedencia y trazabilidad del capítulo 8

Redacción: **2026-11-04**, con `thesis-writing`, desde especificaciones locales y
dos decisiones explícitas del autor. No se modifica software ni se atribuye
aprobación del director, ejecución de pruebas o satisfacción de requisitos.

## Alcance confirmado como propuesta

1. El autor elige «Las cuatro como alcance de entrega propuesto, sujeto a revisión
   del director»: intervalos vocales, instrumentos, Rhythm Tap y Defender.
2. Ante las funciones pendientes responde «Incluir ambas como compromisos de
   entrega propuestos»: piano en Defender y selección automática de ayuda vocal.

Se incorpora esa propuesta al capítulo 1 y al 8. Se mantiene el mínimo histórico
del protocolo/plan como antecedente y los objetivos OE1–OE7 como formulación de
trabajo pendiente de revisión del director. El capítulo 8 concreta un alcance
propuesto mayor que el mínimo, sin reescribir el protocolo original.

Simón permanece pendiente de reformulación/reemplazo/eliminación; el afinador es
complementario según el tratamiento ya adoptado. Las metas de práctica, cosméticos,
dictado rítmico y escucha de conjuntos no se convierten en compromisos de esta
entrega por figurar en las guías de propuestas.

## Documentos consultados y criterio de vigencia

- Instructivo, páginas físicas 47–49: requisitos funcionales identificados y
  verificables; no funcionales con método de comprobación; restricciones;
  diagrama UML y especificación textual de casos principales (actor,
  precondiciones, flujo, alternativas y postcondiciones).
- `CONTEXT.md`, `docs/agents/domain.md` y ADR 0001/0002: vocabulario de ronda,
  partida, ayuda, adaptación y desbloqueo; Android/Kotlin/Compose/Mamdani.
- `docs/Especificacion-prototipo-v1.md`: perfil único, offline, permisos,
  instrucciones, instrumentos, registro, progreso e inspección/simulación.
- `docs/DDA-instrumentos-v0.4.md`, melodías y demostración v0.5: las referencias
  antiguas a nota aislada o ausencia de desbloqueos se interpretan en su fecha;
  la melodía se conserva en reescuchas y el acierto no se invalida por ayuda.
- `docs/Ideas Desarrolladas/01-Ear-training-intervalos-reproduccion-vocal.md`,
  `docs/Intervalos-vocales-V1.md` a `V6.md`: recorridos y estado incremental.
  V6 tiene tres ayudas elegidas explícitamente; la selección global automática
  sigue pendiente. No revivir promedio móvil/CA de la ficha como regla actual
  del Mamdani. Su política concreta deberá especificarse antes de implementarla.
- `docs/Especificacion-ritmo-v1.md`, `docs/Definicion-ritmo.md`,
  `docs/Rhythm-Tap-R6.md`: categorías y compás, taps, métrica exacta, repetición
  como práctica y adaptación entre ejecuciones. El resumen inicial R6 pendiente
  no prevalece sobre la guía posterior. Lectura lateral sustituye la propuesta
  histórica de desplazamiento vertical.
- `docs/Especificacion-defender-v1.md`, `docs/Definicion-defender.md` y guía D5:
  tres variantes previstas; notas/acordes por nombre existentes; piano D6/D7
  pendiente. La referencia espacial antigua no reemplaza Music Garden. Se usa
  «Tocar» como concepto vigente, sin trasladar títulos de controles históricos.
- `docs/Especificacion-afinador-v1.md`: función complementaria, no sustituto
  de integración vocal ni un quinto juego obligatorio en este capítulo.
- `Informe/guia-juegos-serios-y-gamificacion.md`, capítulo 9 y metodología:
  autonomía con orientación, conservación de progreso y separación entre
  práctica, progresión y propuestas futuras.

## Matriz de procedencia y comprobación propuesta

Los IDs RF/RNF/RES/CU son organización editorial del capítulo 8. Los casos de
verificación siguientes describen cómo contrastarlos, sin afirmar ejecución.

| IDs | Origen principal | Comprobación / relación |
| --- | --- | --- |
| RF-01 | Especificaciones de juegos y acuerdos de autonomía | Preparar modos/contenidos y explicar acceso; CU-01/02, OE3/4 |
| RF-02 | Prototipo, V2/V5/V6 y especificaciones de instrumentos/ritmo | Comparar referencias y separar reproducción/captura; CU-01/02, OE1/3 |
| RF-03 | Prototipo y V1/V2/V6 | Permiso rechazado, señal insuficiente, cancelación/segundo plano; CU-01, OE1 |
| RF-04 | Ficha vocal y V1–V6 | Errores relativos/octava y procedencia de ancla por ayuda; CU-01, OE1/3 |
| RF-05 | Prototipo, melodías y DDA de instrumentos | Ejemplo conservado en reescucha y resultado/ayuda separados; CU-02, OE3 |
| RF-06 | Especificación de ritmo y R6 | Generación, taps, errores y repetición identificada; CU-02, OE3 |
| RF-07 | Especificación Defender, D5 y compromiso del autor del 04/11 | Grafía por nombre / conjunto exacto con octavas en piano; CU-02, OE3. Piano pendiente |
| RF-08 | Especificaciones y guía de juegos/gamificación | Devoluciones, reintentos, progreso preservado; CU-01/02, OE3/4 |
| RF-09 | DDA de instrumentos, R6, D3/D5 y V4–V6 | Decisión aplicada solo en momento permitido; CU-01/02/06, OE2/3 |
| RF-10 | Prototipo, R6, Defender y vocal | Accesos preservados; manual/simulado no acredita progreso; CU-01/02/03, OE2/4 |
| RF-11 | Prototipo, R6 y V3–V6 | Confirmación, fallo y reintento deduplicado; CU-01/02/03/04, OE4 |
| RF-12 | Historiales de las guías de cada actividad | Estado vacío/detalle/error sin modificación; CU-03, OE4 |
| RF-13 | Prototipo, R6, D5 y V3–V6 | Exportación/cancelación, procedencia/configuración; CU-04, OE4/5 |
| RF-14 | Prototipo y guías de datos | Confirmar/cancelar/fallo sin borrar exportaciones; CU-05, OE4 |
| RF-15 | Prototipo, DDA, R6, D3 y V4–V6 | Inspección/simulación aislada; CU-06, OE2/5 |
| RF-16 | Ficha vocal y compromiso explícito del autor del 04/11 | Selección de ayuda por intervalo, evidencia y origen manual/automático; CU-01, OE2/3. Pendiente |
| RNF-01 | Acuerdo offline y perfil local | Recorridos sin conexión/cuenta |
| RNF-02 | Protocolos de guardado R6 y vocal | Fallos, reapertura y deduplicación |
| RNF-03 | Especificaciones musicales de ritmo/Defender/vocal | Corpus de duración, ataques, grafía/alturas y evaluación |
| RNF-04 | OE5, protocolo y capítulo 4 | Método de precisión/disponibilidad/tiempos; límites por acordar con director |
| RNF-05 | Guías R6/Defender/vocal y pautas de interfaz existentes | Matriz propuesta 320/360/411 dp, texto 200 %, controles 48 dp, lector y movimiento reducido; no validación acreditada |
| RNF-06 | Perfil y datos de voz en prototipo/V3–V6 | Procesamiento local, mediciones derivadas, sin audio almacenado/enviado |
| RNF-07 | Especificaciones y OE4/5 | Relacionar configuración y datos con casos controlados y exportados |
| RES-01/03 | ADR 0001/0002 y decisiones del autor | Restricciones tecnológicas; no son resultados funcionales |
| RES-02 | Alcance revisado del protocolo | Entrada monofónica y exclusiones |
| RES-04 | Entrega revisada con el autor y OE7 | Gratuidad/publicación efectiva/licencias, pendientes hasta entrega |
| RES-05 | Factibilidad y metodología | Recursos/plazos y evaluación técnica/de uso |

## Límites y pendientes

- Los valores de sostenido, tolerancia, BPM, velocidad y pertenencias son
  configuraciones experimentales: permanecen en diseño/implementación.
- RNF-04 no es un criterio cuantitativo cerrado: faltan rango/referencias,
  error admisible, disponibilidad mínima y latencia máxima. No inventar umbrales
  ni confundir tolerancia de respuesta del estudiante con precisión del detector.
- RNF-05 es una propuesta de aceptación desde matrices existentes, con dispositivos
  y ejecución por identificar. No se promete conformidad certificada con normas.
- RF-07 (piano) y RF-16 amplían lo pendiente de la entrega. Sus políticas requieren
  especificación/implementación/verificación; no se declaran cumplidos por V6/D5.
- No se inventan datos de encuesta, eficacia pedagógica, usuarios autenticados,
  administración remota, nube o importación de perfiles.

## Diagrama y extensión

`tools/generar_casos_uso.py` genera un UML de seis casos y dos roles con
asociaciones, frontera del sistema y elipses; PDF vectorial y SVG en `figuras/`.
Los roles no son cuentas ni permisos. CU-02 agrupa las tres actividades sin
captura para evitar repetir el flujo común; las diferencias musicales quedan
expresas en requisitos y especificaciones. No es un diagrama de flujo ni una
descripción de pantallas.

Se mantiene la escala editorial acordada: requisitos resumidos y seis casos
principales en el cuerpo; detalle de reglas, catálogos y pruebas fuera de este
capítulo. No se agregan fuentes bibliográficas externas ni paquetes LaTeX.

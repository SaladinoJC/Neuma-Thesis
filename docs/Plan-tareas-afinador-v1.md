# Plan de incrementos del afinador v1

Estado: puntos de prueba, granularidad y dependencias aprobados por el usuario mediante «Perfecto» el 2026-10-22. Especificación y seis tickets publicados. A1 y A2 implementados y verificados en software hasta v0.12; prueba física e issues abiertas pendientes. A3 autorizado y en desarrollo el 2026-10-24; A4–A6 pendientes. [Verificación de la entrega](Verificacion-prototipo-v0.12.md).

Fuente: [especificación](Especificacion-afinador-v1.md), basada en el alcance confirmado de [la entrevista](Definicion-afinador.md). Publicado en SaladinoJC/Tesis con `ready-for-agent` y bloqueos nativos. Especificación: [#8](https://github.com/SaladinoJC/Tesis/issues/8), referencia que no debe cerrarse para comenzar. No se modificaron ni cerraron issues anteriores.

## División propuesta

| Incremento | Issue | Entrega demostrable | Bloqueado por |
| --- | --- | --- | --- |
| A1 | [#9](https://github.com/SaladinoJC/Tesis/issues/9) | Afinar libremente una guitarra estándar con Normal/Pro | Ninguno |
| A2 | [#10](https://github.com/SaladinoJC/Tesis/issues/10) | Afinar con perfiles, presets y afinaciones personalizadas guardadas | A1 (#9) |
| A3 | [#11](https://github.com/SaladinoJC/Tesis/issues/11) | Completar afinación guiada y comprobación final opcional | A2 (#10) |
| A4 | [#12](https://github.com/SaladinoJC/Tesis/issues/12) | Escuchar referencias de guitarra, piano y tono puro sin autoevaluarlas | A1 (#9) |
| A5 | [#13](https://github.com/SaladinoJC/Tesis/issues/13) | Practicar de oído, comprobar, corregir y ver resultados | A3 y A4 (#11 y #12) |
| A6 | [#14](https://github.com/SaladinoJC/Tesis/issues/14) | Guardar y consultar seguimiento separado de instrumento y oído | A5 (#13) |

A3 y A4 pueden desarrollarse en paralelo una vez listos sus bloqueos. A4 necesita el contrato de objetivo de A1; no necesita el editor de A2. A5 integra las afinaciones alternativas por dependencia transitiva de A3. A6 incorpora toda la sesión de entrenamiento por A5; no requiere un ticket horizontal previo de almacenamiento. No se propone un refactor transversal sin evidencia de que sea necesario: cualquier extracción local acompaña el primer comportamiento que la utiliza.

Cada ticket debe integrar interfaz, reglas, datos que necesite y pruebas apropiadas. Los resultados pueden permanecer en memoria antes de A6; hasta entonces no presentar un seguimiento persistente como disponible. Los datos persistentes de A2 son configuración, no resultados de práctica.

## A1 — Afinar libremente guitarra estándar con Normal y Pro

**Qué construir:** entrada «Afinador» desde la app, uso cromático y seguimiento automático de cuerdas estándar, fijación manual y criterios visibles. Mantener accesible la prueba de voz existente sin mezclar sus historiales.

**Bloqueado por:** ninguno; reutiliza la base local v0.10 existente, sin esperar cierres administrativos de sus issues.

- [ ] Abrir modo Libre, solicitar permiso e iniciar/detener micrófono explícitamente; salir o pausar libera recursos y no reactiva captura por sí solo.
- [ ] Mostrar nota medida, objetivo cuando corresponda, frecuencia y cents; silencio/lectura inválida no aparentan afinación correcta.
- [ ] Uso cromático normal, reconocimiento de cuerdas estándar y fijación/liberación de una cuerda sin forzar octava ni cambiar su objetivo al detectar otra nota.
- [ ] Normal ±5 cents y Pro ±2 cents visibles; indicador afinado tras un segundo continuo válido dentro del margen, sin acumular periodos separados.
- [ ] Reglas comprobadas mediante sesión pública; señales PCM para graves de guitarra, fronteras de criterios, ruido y octavas. Documentar rango y parámetros provisionales.
- [ ] Verificación Android de captura/permisos/pausa y recorrido físico documentado; separar mediciones reales de sintéticos y emulador.
- [ ] No guardar resultados bajo el historial de voz; comprobar que los flujos previos continúan funcionando.

## A2 — Afinar con perfiles y afinaciones guardadas

**Qué construir:** seleccionar guitarra y afinación, crear una personalizada de seis cuerdas y volver a usarla al reabrir.

**Bloqueado por:** A1.

- [ ] Instrumento predeterminado, creación de guitarras con nombre y selección de la guitarra de uso actual persistidas.
- [ ] Estándar, Drop D, medio tono abajo, Drop C, Open G y DADGAD con alturas/octavas exactas y número de cuerda según especificación.
- [ ] Crear/editar afinación con nombre y seis notas/octavas; validar el rango soportado, sin cambiar alturas silenciosamente.
- [ ] Cerrar/reabrir conserva configuraciones; la afinación seleccionada cambia realmente los objetivos y la detección del modo Libre.
- [ ] Mantener Normal/Pro y preferencias de modo sin reiniciar micrófono automáticamente.
- [ ] Versionar configuraciones y tomar copia para cada recorrido; futuras ediciones no alterarán su interpretación histórica.
- [ ] Pruebas conductuales de guardado/relectura, entradas inválidas, perfiles y detección con una afinación alternativa; recorrido Android demostrable.
- [ ] Integrar configuración nueva con exportación/reinicio/recuperación existentes sin romper historiales anteriores, como condición de añadir persistencia.

## A3 — Completar guiado y revisión final

**Qué construir:** recorrido completo de sexta a primera con corrección visible y segunda pasada opcional, válido para las afinaciones seleccionables de A2.

**Bloqueado por:** A2.

- [ ] Preparación con guitarra, afinación y precisión; objetivos sexta → primera identificados por cuerda y nota/octava.
- [ ] Avance automático tras un segundo válido dentro del margen; reiniciar evidencia al avanzar, cambiar objetivo o interrumpir captura.
- [ ] Aviso «Muy lejos, revisar cuerda correcta» ante distancia confiable excesiva, sin declarar inequívocamente otra cuerda. Umbral y persistencia explícitos como provisionales.
- [ ] Activar/desactivar revisión final y recordar preferencia; activada por defecto en Guiado.
- [ ] Segunda pasada de las seis cuerdas; corregir pendientes y declarar conjunto comprobado únicamente al terminarla.
- [ ] Resumen en memoria que distingue primera pasada y revisión, completa o interrumpida; salir/recrear no duplica avances ni completa el recorrido por sí solo.
- [ ] Pruebas de sesión: afinación desviada, señal inválida, cuerda incorrecta, lectura heredada, repaso fallido y corrección; recorrido Android completo.

## A4 — Referencias del instrumento, piano y tono puro

**Qué construir:** escuchar/repetir la nota objetivo en los tres timbres desde el afinador sin que el sistema evalúe su propia salida.

**Bloqueado por:** A1.

- [ ] Seleccionar guitarra —predeterminada—, piano o tono puro y repetir referencia en la altura/octava objetivo.
- [ ] Procedencia, permisos, transformaciones y hashes documentados para los assets; verificar afinación y cobertura, incluyendo objetivos graves de los seis presets previstos y rango admitido de personalizadas.
- [ ] No asumir cobertura de guitarra por el piano ya existente; rechazar objetivos fuera del rango publicado sin cambiar octava.
- [ ] Captura/análisis suspendidos antes de sonar la referencia; reanudar sin contar audio residual, muestras anteriores ni el intervalo suspendido como estabilidad.
- [ ] Cancelación y fallo de reproducción no dejan recursos activos ni producen aciertos/errores de afinación.
- [ ] Recordar timbre; no incluir voz hablada ni captura/clonación del timbre personal.
- [ ] Pruebas del contrato de audio y Android con reproducción real de los tres timbres; documentar audibilidad en teléfono como pendiente hasta medirla.

## A5 — Entrenamiento de oído con comprobación y corrección

**Qué construir:** completar una práctica de sexta a primera usando referencias, revelar/evaluar al comprobar, corregir y recibir resultados. Incluir clasificación previa como opción del mismo recorrido.

**Bloqueado por:** A3 y A4.

- [ ] Elegir preparación y comenzar; durante ajuste ocultar todas las pistas de afinación salvo el aviso excepcional de A3. Permitir repetir cualquiera de los timbres de A4.
- [ ] «Comprobar» exige una lectura nueva válida y estable; revelar nota real, desviación y evaluación inicial, aunque esté desafinada. Estabilidad para evaluar no depende de estar dentro de margen.
- [ ] Sin lectura válida pedir tocar nuevamente; no consumir intento ni generar acierto por ausencia de datos.
- [ ] Corrección visual y avance automático conservan el error inicial; pulsaciones repetidas no crean intentos nuevos después de revelar la respuesta.
- [ ] Clasificación previa opcional grave/agudo/afinado, desactivada inicialmente, con referencia temporal válida y resultado separado del ajuste.
- [ ] Revisión final opcional sin sumar nuevos intentos de oído. Resumen por cuerda con intentos válidos, errores, desviación inicial y resultado corregido.
- [ ] Recordar modo y opciones; recorrido utilizable sin guardar seguimiento hasta A6, con disponibilidad del guardado indicada honestamente.
- [ ] Pruebas públicas de sesión y Android: un error luego corregido, primer acierto, lectura insuficiente, referencia intercalada, clasificación y revisión; no filtrar mediciones ocultas en etiquetas de accesibilidad.

## A6 — Seguimiento diferenciado, exportación y preferencias completas

**Qué construir:** guardar opcionalmente sesiones, consultar resultados y evolución por guitarra/afinación/criterio, separando desempeño de oído y mediciones del instrumento; integrar los datos con la app existente.

**Bloqueado por:** A5.

- [ ] Seguimiento activo por defecto en Guiado/Entrenamiento, desactivable antes de empezar; preferencias recordadas. Desactivado permite resumen sin guardar sesión ni mediciones en ningún historial de desempeño.
- [ ] Historial persistente con sesión completa/interrumpida, configuración copiada/versionada, instrumento, referencia y etapas por cuerda; reintento de guardado idempotente y fallos visibles.
- [ ] Detalle distingue intento de oído, corrección y revisión final; cambios futuros de perfil/afinación no alteran resultados guardados.
- [ ] Progreso de oído con proporción de aciertos sobre intentos válidos y desviación inicial por cuerda/sesión; clasificación opcional separada, filtros y ausencia de datos explícita.
- [ ] Vista del instrumento permite comparar etapas equivalentes y no atribuye cambios a causas mecánicas ni toma mediciones posteriores al ajuste como estado inicial.
- [ ] Exportación incluye configuraciones y resultados nuevos sin perder compatibilidad con los anteriores; recuperación de originales y reinicio incluyen el nuevo almacenamiento de forma consistente.
- [ ] Pruebas de seguimiento apagado, varias guitarras, modificación de afinación, fallo de guardado, reintento, interrupción, exportación y lectura de datos previos.
- [ ] Compilación y comprobaciones pertinentes pasan; guía de demostración y registro de verificación distinguen sintéticos, emulador y pruebas físicas pendientes. No declarar precisión Pro o mejora pedagógica por aprobar tests.

## Puntos de prueba aprobados junto con la división

1. **Sesión pública:** acciones + lecturas/tiempo controlados → estado, evaluación, resumen y persistencia. Captura, audio y almacenamiento se controlan solo en sus límites.
2. **PCM y Android:** estimador con señales conocidas; Android para recursos/permisos/audio y recorrido real. La verificación física del S22 Ultra se registra aparte, sin reemplazarla por emulador.

## Registro de publicación

Publicadas #8–#14 con cuerpos de texto UTF-8 y saltos de línea reales mediante `--body-file`. Todas llevan `ready-for-agent`. Bloqueos nativos: #10←#9, #11←#10, #12←#9, #13←#11/#12, #14←#13; #9 no tiene bloqueos. Los identificadores y el resultado de la verificación se conservan en `.agents/afinador-issues.json` para retomar sin duplicados.

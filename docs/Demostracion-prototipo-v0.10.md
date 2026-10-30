# Demostración del prototipo v0.10

Entrega local de las seis iteraciones autorizadas el 2026-10-21. Mantiene el paquete y firma debug anteriores: actualizar encima sin desinstalar conserva los historiales. APK: `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.10.apk`. No se instaló esta entrega en el teléfono del usuario.

## Recorrido

1. **Simón / instrumentos:** automático usa el mayor nivel desbloqueado; dos partidas completas consecutivas de dominio alto desbloquean el siguiente. Manual permite practicar sin modificar rachas. El resumen muestra dominio, entradas, reglas y próximo nivel. Instrumentos ajusta las escuchas entre preguntas; Simón conserva el crecimiento de una nota. Son las reglas experimentales de v0.5, sin recalibración.
2. **Ver todos los resultados:** filtrar juego/nivel/origen, recorrer bloques de 20 y abrir un detalle. Simón conserva secuencia/error; instrumentos, melodías/escuchas/decisiones; voz, configuración y resumen derivado. Un dato ausente en registros anteriores se identifica como ausente. Consultar no modifica resultados.
3. **Datos y demostración:** ejecutar escenarios individuales o el banco DDA. Sus nueve escenarios, 56 pasos, 93 fronteras y 21 comprobaciones se calculan en memoria; no alteran el perfil. Inspeccionar las reglas permite explicar las decisiones, sin acreditar calibración pedagógica.
4. **Pruebas sintéticas de tono:** ejecutar el banco de 83 casos/332 ventanas, cancelar o exportar el informe Markdown. No requiere permiso de micrófono ni reproduce audio. Los informes se guardan separados de las capturas. Los límites observados con ruido y en el extremo de 1.000 Hz están documentados en [el informe JVM](Laboratorio-YIN-v0.7-JVM.md).
5. **Afiná tu voz:** elegir objetivo, escuchar referencia e iniciar explícitamente el micrófono. La referencia detiene y espera la captura; la app no escucha mientras emite el tono. La gráfica conserva diez segundos, representa silencio con huecos y muestra el valor completo de cents aunque el dibujo quede acotado a ±100. Al detener, queda un resumen de la captura. Se guardan mediciones derivadas, sin grabaciones.
6. **Exportar JSON:** elegir destino en Android. Incluye perfil/progreso/decisiones/configuración/voz y apartados separados para simulaciones, pruebas sintéticas y, si corresponde, un guardado de voz pendiente. La operación se confirma con un aviso visible y un registro de última operación.

## Fallos y recuperación

Si no se puede leer un historial, el juego afectado permite reintentar lectura y conserva el archivo original; las demás secciones siguen accesibles. «Exportar originales para recuperación» genera un ZIP sin parsear los cuatro JSON conocidos. Si existe un pendiente de voz, se agrega como `pending-voice-save.json`. Es una copia para diagnóstico, sin reparación ni importación automática.

Si falla el guardado de voz, las mediciones se retienen en memoria. «Reintentar guardado» usa el mismo identificador y bloquea una nueva captura hasta resolver el pendiente. También se puede exportar desde Datos. **El pendiente no sobrevive al cierre del proceso.** No contiene PCM.

«Reiniciar datos» requiere confirmación y borra historiales, pruebas sintéticas, rachas, desbloqueos y pendientes en memoria; conserva archivos ya exportados. Cancela recursos, descarta sesiones activas y coordina escrituras para evitar que repongan datos anteriores. Si el proceso se interrumpe durante el borrado, un marcador permite completarlo al iniciar. Exportar los originales antes de reiniciar permite conservar evidencia de un fallo.

## Verificación y límites

Comando reproducible desde `Plataforma/`: `./tools/verify-prototype.ps1 -Serial emulator-5554`; detalles y requisitos en [README](../Plataforma/README.md). Sus pruebas Android reinician datos y solo admiten emuladores. Resultados, advertencias, versiones y hashes en [el registro de verificación](Verificacion-iteraciones-post-v0.5.md).

Queda pendiente en el S22 Ultra: permiso de micrófono, captura al cantar en ambiente tranquilo, audibilidad y volumen de las muestras/referencia, comodidad visual y persistencia al actualizar. Los tiempos de procesamiento de YIN no equivalen a latencia acústica. Ni una demostración funcional, ni simulaciones, ni señales sintéticas acreditan eficacia pedagógica o precisión sobre voz humana desconocida. Se conserva el alcance Android/offline, sin nuevos juegos, backend ni ML. El protocolo Word no se actualizó en estos incrementos.

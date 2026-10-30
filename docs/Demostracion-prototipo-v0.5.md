# Demostración Android v0.5

Implementación del plan solicitado por el usuario el 2026-10-21: completar desbloqueos (#5), prueba de voz (#6) y exportación/reinicio (#7). Complementa v0.4; no modifica el protocolo Word. Las reglas son parámetros experimentales, no evidencia de eficacia pedagógica. La demostración física completa en Samsung S22 Ultra sigue pendiente.

## Progresión y dominio

Automático inicia en el nivel más alto desbloqueado: pentatónica → diatónica mayor → cromática en Simón, A → B → C en instrumentos. Dos partidas automáticas completas consecutivas de dominio alto del nivel actual habilitan el siguiente para la próxima partida. Una automática completa no alta reinicia la racha; manuales, interrupciones y simulaciones no la alteran. No se retiran niveles. En el último nivel la racha se limita a dos y no hay otro avance.

Simón conserva el fin al primer error. Normaliza longitud completada/referencia (5, 7 y 9 según escala), acotada a [0,1]. Instrumentos usa exactitud y autonomía de las diez respuestas: aciertos/10 y aciertos con primera escucha/aciertos (cero si no hay aciertos). Los aciertos con ayuda siguen siendo correctos. Las reglas y funciones de pertenencia de dominio son las propuestas numéricas de `Especificacion-prototipo-v1.md`: salida [0,1], alto desde 0,75, centroide con 1.001 puntos y paso 0,001; configuraciones `mastery-simon-v1` y `mastery-instruments-v1`. Un resultado sin inferencia válida no concede dominio ni modifica progreso.

Cada resultado nuevo conserva entradas, funciones, reglas, activaciones, centroide, umbral y referencia. El resumen ofrece inspección y muestra el nivel desbloqueado y la racha. La práctica manual conserva acceso a todos los niveles; instrumentos mantiene tres escuchas en manual.

El progreso se reconstruye del historial persistido, en orden de guardado, deduplicando identificadores. Resultado y evidencia de dominio comparten la escritura atómica existente: no hay un segundo archivo de progreso que pueda quedar desactualizado por un cierre. Los historiales anteriores sin dominio se conservan, sin desbloqueos retroactivos. Los parámetros de cada resultado quedan fijados al guardarlo; no se recalifican partidas antiguas con reglas nuevas.

## Prueba de voz

Acceso desde «Afiná tu voz». Permiso `RECORD_AUDIO` al iniciar; rechazo explicado con acceso a Ajustes. `AudioRecord`, fuente `VOICE_RECOGNITION`, PCM de 16 bits mono/44.100 Hz, ventana 4.096, salto 1.024 y rango 55–1.000 Hz. Si el dispositivo no admite la configuración o falla la captura, se informa el error. Procesamiento fuera del hilo de interfaz. La captura finaliza al detener, salir o pausar la actividad, incluso ante un cambio rápido de aplicación que no llegue a `onStop`. El objetivo y el estado de pantalla se conservan en ViewModel; una pausa requiere iniciar otra captura. Las mediciones se publican en el hilo principal y YIN/captura siguen fuera de él.

Implementación propia de una línea base YIN: diferencia cuadrática sobre 2.048 muestras por retardo, normalización media acumulada, primer valle bajo 0,15 y refinamiento parabólico. El refinamiento se hace sobre la función normalizada; se rechaza una ventana sin valle bajo el umbral, en lugar de elegir un mínimo global. No incluye la búsqueda de mejor estimación local del artículo completo. Umbral RMS de 0,01 sobre PCM normalizado con media retirada; silencio, PCM inválido, ausencia de periodicidad o frecuencia fuera de rango devuelven ausencia de tono. Sin suavizado temporal ni ML. Estos valores no están calibrados para voz real.

Muestra frecuencia, nota MIDI aproximada y cents respecto de un objetivo seleccionable. Una medición derivada cada 200 ms, con duración máxima de dos minutos por sesión para limitar el volumen de datos. Guarda configuración, dispositivo/Android, fuente, frecuencia real de muestreo, tiempos, objetivo, validez, RMS, periodicidad, frecuencia, cents y tiempo de procesamiento. No persiste PCM ni grabaciones. La periodicidad no es una probabilidad calibrada y el tiempo de procesamiento no es latencia acústica extremo a extremo. Una sesión interrumpida por cierre forzado del proceso puede perder sus mediciones aún no guardadas.

Referencia primaria: de Cheveigné y Kawahara (2002), *YIN, a fundamental frequency estimator for speech and music*, DOI [10.1121/1.1458024](https://doi.org/10.1121/1.1458024). Se consultó el [manuscrito de los autores alojado en Columbia](https://www.ee.columbia.edu/~dpwe/papers/deChevK02-yin.pdf), pasos 2–5; no se reproducen aquí sus resultados experimentales. Captura contrastada con la [API oficial AudioRecord](https://developer.android.com/reference/android/media/AudioRecord).

## Exportación y reinicio

«Datos y demostración» permite ejecutar dos escenarios de progresión: dos altas; y alta, baja, alta, alta. Usan las sesiones reales con audio/azar controlados y almacenamiento en memoria. Ambos juegos muestran sus resultados y el nivel final; cada dominio puede inspeccionarse. Son simulaciones, no desempeño de personas.

Exportar usa el selector de archivos de Android (`CreateDocument`, MIME `application/json`), sin permisos de almacenamiento adicionales; ver [documentación oficial de Storage Access Framework](https://developer.android.com/training/data-storage/shared/documents-files). Antes de tomar la instantánea se espera a que finalicen captura y acciones de las sesiones. Cancelar el selector conserva los datos. Exportar no avanza rachas ni desbloqueos.

JSON `schemaVersion=1`: versión de app, fecha, `simon`, `instruments`, `voice`, `simonProgress`, `instrumentProgress`, `simulations` y `helpSimulation`. Las simulaciones de progreso visibles y el último escenario de ayuda ejecutado en el inspector se incluyen separados de los historiales reales y con origen `simulated`. Los campos `level` de progreso son índices 0/1/2 del curriculum de cada juego; la interfaz los presenta como primer/segundo/tercer nivel. El dominio alto se obtiene comparando el centroide guardado con su umbral. Se conservan las configuraciones completas en resultados. No hay importación desde la app: el archivo se puede parsear externamente para inspección.

Reiniciar requiere «Borrar y reiniciar» en un diálogo. Espera guardados pendientes, borra los tres historiales y refresca pantallas y progreso. Un marcador permite completar un borrado interrumpido al próximo inicio. No elimina APK, muestras, configuración de fábrica ni archivos exportados. Los errores de lectura/escritura se presentan al usuario; no se reemplaza silenciosamente un historial ilegible.

## Recorrido en S22 Ultra

1. Instalar el APK encima de v0.4, sin desinstalar; comprobar conservación de historiales.
2. Simón: automático en pentatónica, completar al menos cinco notas y fallar la siguiente. Repetir: el segundo resultado debe habilitar diatónica para la próxima partida. Probar práctica manual sin alterar progreso. Revisar piano, notas rápidas y giro.
3. Instrumentos: automático, diez preguntas. Observar reducción de ayuda con aciertos autónomos y recuperación con errores; revisar inspector DDA. Dos partidas de dominio alto deben habilitar B. Inspeccionar también un resultado correcto con reescuchas.
4. Datos: ejecutar ambos escenarios y comprobar su marca simulada; salir y verificar que el perfil real no cambió.
5. Voz: rechazar primero el permiso; verificar mensaje y acceso a Ajustes. Concederlo y probar silencio, nota sostenida y cambio de objetivo entre capturas. Detener, volver a iniciar, salir y pasar a segundo plano; comprobar liberación del micrófono. Registrar Android, salida de audio/auriculares, ruido y observaciones.
6. Exportar JSON y abrirlo externamente. Comprobar orígenes, decisiones, configuración y mediciones, sin audio crudo. Cancelar otra exportación sin alterar datos.
7. Cerrar/reabrir: resultados y niveles deben conservarse. Cancelar el reinicio; luego confirmar y comprobar primer nivel, racha cero e historiales vacíos también al reabrir.

Completar este recorrido y registrar condiciones del teléfono sigue pendiente. La prueba de emulador no valida precisión vocal, audibilidad física ni latencia acústica.

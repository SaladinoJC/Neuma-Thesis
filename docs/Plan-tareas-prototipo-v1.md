# Plan publicado de la primera demostración

Estado: puntos de prueba y división en seis tickets aprobados por el usuario el 2026-10-02. Cada ticket se publica con criterios de aceptación y dependencias. La especificación es la issue de referencia, no un bloqueo que deba cerrarse para trabajar.

Especificación: [#1](https://github.com/SaladinoJC/Tesis/issues/1). Todas las issues llevan `ready-for-agent`; los bloqueos nativos de GitHub se verificaron contra este plan.

| Incremento | Issue | Bloqueada por |
| --- | --- | --- |
| 1. Simón | [#2](https://github.com/SaladinoJC/Tesis/issues/2) | Ninguna |
| 2. Instrumentos | [#3](https://github.com/SaladinoJC/Tesis/issues/3) | #2 |
| 3. DDA intrapartida | [#4](https://github.com/SaladinoJC/Tesis/issues/4) | #3 |
| 4. Desbloqueos | [#5](https://github.com/SaladinoJC/Tesis/issues/5) | #4 |
| 5. Voz | [#6](https://github.com/SaladinoJC/Tesis/issues/6) | #2 |
| 6. Exportación/reinicio | [#7](https://github.com/SaladinoJC/Tesis/issues/7) | #5 y #6 |

## 1. Jugar una partida de Simón en Android

**Bloqueado por:** ninguno.

**Qué entrega:** aplicación instalable con una partida de Simón completa, audio real y resumen, que conserva su resultado al cerrar y reabrir. En este primer incremento se puede seleccionar escala manualmente; la progresión automática llegará con el ticket 4.

- [ ] Proyecto reproducible con Gradle Wrapper y comandos documentados; compilación y pruebas pertinentes pasan.
- [ ] Inicio e instrucciones en español; acceder a Simón y volver al menú.
- [ ] Secuencia inicial de una nota, reproducción con resalte, botones con etiqueta/posición/sonido y entrada habilitada solo al responder.
- [ ] Escala y tónica fijas, secuencia crece una nota por ronda correcta y primer error termina la partida.
- [ ] Puntaje por longitud máxima completada; conserva secuencia intentada y posición del error, incluido completar cuatro y fallar cinco.
- [ ] Resultado local persistido y visible tras reabrir; no registrar como completa una partida abandonada.
- [ ] Pruebas de sesión sobre acierto, error, puntaje y persistencia; verificación Android de reproducción real.

## 2. Jugar diez preguntas de reconocimiento tímbrico

**Bloqueado por:** 1.

**Qué entrega:** segundo minijuego ejecutable con cuatro opciones, banco real de samples y resumen de diez preguntas. Permite seleccionar manualmente los niveles A/B/C y empieza con tres escuchas totales por pregunta; aún no ajusta ayuda.

- [x] Incorporar muestras con permiso y procedencia comprobados, catálogo/transformaciones documentadas, altura sonora compartida y presentación comparable.
- [x] Cuatro opciones con una correcta, respuesta sin presión temporal y máximo de tres reproducciones, incluida la primera.
- [x] Contar cada reproducción real y mostrar escuchas restantes sin permitir superar el límite.
- [x] Diez respuestas cierran partida con resumen y resultados persistidos; cada pregunta registra selección, corrección y escuchas usadas.
- [x] Probar aciertos/errores, repeticiones, diez rondas y cierre; comprobar el banco de audio en Android.

Software comprobado en emulador Pixel_6/API 34. Evidencia y recorrido físico pendiente en [Verificacion-Instrumentos-v0.3.md](Verificacion-Instrumentos-v0.3.md); catálogo y tratamiento del audio en [Audio-instrumentos.md](Audio-instrumentos.md). No se acredita sonoridad perceptual idéntica, calibración pedagógica ni prueba en S22 Ultra.

Actualización 2026-10-09: el usuario informó que probó v0.3 en el celular y solicitó melodías aleatorias. El ajuste v0.3.1 conserva el alcance del incremento y añade una frase generada por pregunta, reescuchas idénticas y registro de notas/duraciones con banco real ampliado. Pruebas y límites en [Verificacion-Instrumentos-v0.3.1.md](Verificacion-Instrumentos-v0.3.1.md). La prueba física de la versión nueva sigue pendiente. #3 continúa abierta en GitHub; no se publicó ni cerró desde esta sesión.



## 3. Ajustar escuchas con DDA Mamdani e inspeccionar sus decisiones

**Bloqueado por:** 2.

**Qué entrega:** reconocimiento tímbrico automático con adaptación de ayuda observable, motor Mamdani y panel para reproducir escenarios. El modo automático inicia en A hasta que el ticket 4 agregue desbloqueos.

- [x] Motor independiente de interfaz, configuración identificable y propuestas numéricas de la especificación documentadas como no calibradas.
- [x] Desde tercera respuesta evaluar últimas tres rondas; usar exactitud/autonomía y cambiar máximo una escucha dentro de [1, 3].
- [x] Aplicar solo a próxima pregunta; dos rondas entre cambios; iniciar cada partida con tres escuchas y ventana vacía.
- [x] Mostrar entradas, pertenencias, reglas, salida/acción propuestas, acción aplicada y motivo de espera/límite.
- [x] Casos reproducibles de aciertos, errores, reescuchas y alternancia; simulaciones aisladas de desempeño y progreso real.
- [x] Mantener resultados correctos como correctos aunque haya ayuda; no penalizar tiempo de respuesta.
- [x] Pruebas conductuales de aumento/reducción/mantenimiento, límites, espera, datos insuficientes y fallo seguro sin salida.

Comprobado en v0.4: 34 pruebas JVM y 13 Android, lint sin errores y APK generado. Verificación en [Verificacion-DDA-v0.4.md](Verificacion-DDA-v0.4.md); configuración y escenarios en [DDA-instrumentos-v0.4.md](DDA-instrumentos-v0.4.md). El S22 Ultra sigue pendiente. #4 permanece abierta en GitHub, sin publicación/cierre en esta sesión.

## 4. Desbloquear niveles y conservar progreso en ambos juegos

**Bloqueado por:** 3 (incluye transitivamente 1 y 2).

**Qué entrega:** modo automático inicia en el nivel más alto desbloqueado y habilita el siguiente tras dos partidas consecutivas de dominio alto, utilizando el mismo motor con indicadores específicos de cada juego.

- [x] Añadir calificación de dominio de partida completa de instrumentos y puntaje completado de Simón; referencias por escala/configuración visibles como parámetros de prueba.
- [x] Dos automáticas completas consecutivas altas del mismo juego/nivel habilitan siguiente; una completa no alta reinicia racha.
- [x] Cambio efectivo en próxima partida; progreso permanente, sin avanzar más allá del último nivel.
- [x] Simón mantiene su mecánica sin adaptación intrapartida adicional.
- [x] Manuales y simulaciones no alteran rachas ni desbloqueos; separar historial por juego/nivel/origen.
- [x] Persistir progreso/rachas y evitar doble avance; cerrar/reabrir mantiene nivel y resumen.
- [x] Demostrar desbloqueo con escenarios identificados, sin presentarlos como datos de usuarios reales.
- [x] Pruebas del primer/segundo alto, reinicio de racha, orígenes, niveles, duplicación, persistencia y nivel final.


Implementación v0.5: [conducta y recorrido](Demostracion-prototipo-v0.5.md), [verificación](Verificacion-prototipo-v0.5.md). La validación física completa sigue pendiente; no se cerraron estos tickets en GitHub.

## 5. Inspeccionar tono vocal con YIN en Android

**Bloqueado por:** 1. Puede trabajarse sin esperar 2–4.

**Qué entrega:** pantalla de prueba de voz accesible desde la app, con captura real, objetivo seleccionable y mediciones exportables de frecuencia/nota/desviación. La verificación física final será en S22 Ultra.

- [x] Permiso de micrófono y estado de rechazo claros; iniciar/detener y liberar captura al salir.
- [x] Estimar fuera del hilo de interfaz; mostrar frecuencia, nota aproximada y cents respecto del objetivo durante nota sostenida.
- [x] Silencio/estimación inválida no produce una nota ficticia; no guardar audio crudo.
- [x] Registrar configuración del estimador/muestreo, validez y tiempo de procesamiento, sin llamarlo latencia acústica extremo a extremo.
- [x] Señales sintéticas de 110/220/440 Hz con error inicial de prueba ≤ 10 cents después del arranque; silencio sin tono válido. Documentar resultados y límites.
- [ ] Verificación real en S22 Ultra con datos de dispositivo/configuración y observaciones; si falta acceso, dejar esta comprobación pendiente en lugar de declarar éxito.
- [x] No incorporar ML ni exigir C++/Oboe sin evidencia de necesidad.


Implementación v0.5: [conducta y recorrido](Demostracion-prototipo-v0.5.md), [verificación](Verificacion-prototipo-v0.5.md). La validación física completa sigue pendiente; no se cerraron estos tickets en GitHub.

## 6. Exportar y reiniciar una demostración reproducible

**Bloqueado por:** 4 y 5.

**Qué entrega:** recorrido completo de los dos juegos y voz, exportación portable de resultados/decisiones y reinicio explícito para repetir la demostración desde cero.

- [x] Exportar partidas, decisiones y medidas de voz con esquema/versiones, origen y configuración identificables; sin grabaciones de voz.
- [x] Exportación parseable y usable fuera de la app; exportar no modifica progreso.
- [x] Reinicio con confirmación explícita elimina perfil/resultados/rachas/desbloqueos, conserva configuración de fábrica y se refleja al reabrir.
- [x] Guía de demostración: Simón con puntaje claro; diez preguntas; ayuda aumenta/disminuye; desbloqueo por dos altas; modo manual/simulado aislado; voz y silencio; cierre/reapertura; exportación/reinicio.
- [ ] Evidencia de recorrido en S22 Ultra y checks apropiados por componente; separar resultados sintéticos, simulados y reales.
- [x] Actualizar instrucciones de ejecución, estado del prototipo y limitaciones observadas.


Implementación v0.5: [conducta y recorrido](Demostracion-prototipo-v0.5.md), [verificación](Verificacion-prototipo-v0.5.md). La validación física completa sigue pendiente; no se cerraron estos tickets en GitHub.

## Puntos de prueba propuestos

1. **Sesión de aplicación:** acciones del jugador → estado visible, resultados, decisiones y progreso/exportación. Audio y almacenamiento controlables para pruebas; completar recorridos de juego por este límite evita probar solo un motor aislado.
2. **Estimación de tono:** PCM conocido → estimación/validez. Complementar con captura/reproducción real en Android, porque la prueba matemática no verifica el micrófono.

No hay prefactorización: aún no existe aplicación. La división evita tickets separados solo para interfaz, esquema, motor o pruebas; cada incremento agrega una conducta demostrable.

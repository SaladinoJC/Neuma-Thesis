# Demostración v0.12 — afinador, Rhythm Tap y Defender

**Entrega local integrada, 2026-10-24.** Suite conjunta aprobada: 97 pruebas JVM, 32 Android en API 34 y lint sin errores/14 advertencias. [Evidencia y límites](Verificacion-prototipo-v0.12.md). La prueba física del S22 Ultra sigue pendiente.

Recorrido para los tickets [#10](https://github.com/SaladinoJC/Tesis/issues/10), [#16](https://github.com/SaladinoJC/Tesis/issues/16) y [#23](https://github.com/SaladinoJC/Tesis/issues/23). Las funciones corresponden a los incrementos A2, R1 y D1; no suponen completar los tickets posteriores ni validar eficacia pedagógica. No se publicaron cambios ni se cerraron issues.

## Instalación

Instalar `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.12.apk` encima de la versión anterior, sin desinstalar, para conservar los datos. El menú debe mostrar versión 0.12. Las pruebas instrumentadas se ejecutan únicamente en emulador; en el teléfono seguir este recorrido manual.

## Afinador: perfiles y afinaciones

Abrir Afinador. Crear una guitarra con nombre y seleccionar una afinación predefinida; comprobar cuerda, nota y octava. Crear una afinación personalizada de seis notas y volver a usarla al reabrir. Probar un dato inválido: debe explicarse sin cambiar alturas silenciosamente. Cambiar entre Normal/Pro y fijar/liberar una cuerda. Al cerrar/reabrir se conservan las configuraciones, con el micrófono detenido. No se guardan resultados de desempeño del afinador en este incremento.

Exportar desde Datos y demostración para conservar la configuración junto con los datos anteriores. La recuperación de originales incluye el archivo de configuración sin interpretarlo; el reinicio confirmado lo elimina. Hacer un reinicio solo si se desea borrar el perfil y después de exportar los datos que se quieran conservar.

## Rhythm Tap: cuatro compases

Abrir Rhythm Tap. Elegir negras y/o dos corcheas, generar y revisar los cuatro compases 4/4. La vista previa no reproduce la respuesta. Play inicia un compás de conteo audible; después marcar los ataques con el área de taps. Probar los controles independientes de metrónomo y sonido de taps reales. Revisar aciertos, adelantos, atrasos, extras y omisiones; repetir mantiene la misma partitura como práctica. Salir o enviar la aplicación a segundo plano interrumpe el ejercicio.

Este incremento no guarda historial ni desbloquea categorías. El tempo inicial 60 BPM y las ventanas de evaluación son valores experimentales. La sincronía de timestamps internos no acredita latencia acústica ni precisión física del teléfono.

## Defender: notas en clave de sol

Abrir Defender de notas y comenzar una práctica. Reconocer las notas naturales Mi4–Fa5 por su pentagrama, seleccionar su nombre y pulsar Disparar. El objetivo se aproxima radialmente a la nave. El acierto elimina y suma 100 puntos; el error conserva la selección y no quita vida. Solo la llegada a base quita una de las tres vidas. Pausar oculta los objetivos y congela el tiempo; volver de segundo plano debe requerir reanudación explícita. Al tercer impacto revisar el resumen y las soluciones de enemigos perdidos.

El viaje inicial de diez segundos y la curva de aparición son configuraciones experimentales. Esta práctica todavía no incluye DDA, desbloqueos ni historial persistente. Los sonidos de notas y efectos se controlan por separado.

## Prueba física pendiente

Registrar S22 Ultra, Android, versión de app, ruta de audio, entorno y condiciones de interacción. En afinador comparar graves y octavas con fuente patrón de exactitud conocida; no afirmar exactitud Pro sin esa comparación. En ritmo contrastar timestamps y sonido con un método externo, sin inferir latencia a partir del puntaje. En Defender revisar legibilidad de pentagramas y controles, trayectorias, pausa y audibilidad. Conservar por separado datos sintéticos, emulador y teléfono.

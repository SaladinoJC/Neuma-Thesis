# Cambios para probar en el celular — v0.3 → v0.10

La última prueba en tu celular registrada como realizada fue con **v0.3, el 09/10/2026**. Esta lista reúne los cambios posteriores.

Instalar [el APK v0.10](../Plataforma/build/apk-archives/entrenamiento-auditivo-v0.10.apk) **encima de la app anterior, sin desinstalar**, para conservar el historial. El menú debe mostrar «Versión 0.10».

## Cambios y recorrido de prueba

- [ ] **Instrumentos con melodías:** ahora suena una frase breve aleatoria en lugar de una nota aislada. Comprobar timbres, volumen y que las reescuchas repitan la misma frase.
- [ ] **Ayuda adaptativa:** en automático, instrumentos ajusta entre una y tres escuchas para la siguiente pregunta según aciertos y reescuchas. Jugar una partida usando distinta cantidad de ayuda y revisar las decisiones desde «DDA».
- [ ] **Desbloqueos en ambos juegos:** automático comienza en el mayor nivel desbloqueado; dos partidas completas consecutivas de dominio alto en ese nivel habilitan el siguiente. La práctica manual no desbloquea. Revisar racha y próximo nivel, cerrar/reabrir y comprobar que se conservan.
- [ ] **Historial completo:** desde «Ver todos los resultados», probar filtros y abrir detalles de partidas anteriores y nuevas. Ahora se pueden consultar respuestas, melodías y decisiones DDA.
- [ ] **Prueba de voz:** entrar a «Afiná tu voz», conceder permiso, elegir objetivo y escucharlo. Iniciar el micrófono, sostener una nota y revisar frecuencia, nota, desviación y gráfica. Hacer silencio y comprobar que deja de mostrar un tono actual.
- [ ] **Salida de voz:** detener y guardar; después probar salir a Inicio y volver. El micrófono debe quedar detenido. Escuchar el objetivo también detiene la captura. Solo se guardan mediciones, sin grabaciones.
- [ ] **Bancos de demostración:** en «Datos y demostración», probar escenarios DDA y «Pruebas sintéticas de tono». Son pruebas separadas que no modifican el progreso jugado; no todos los casos sintéticos son criterios de aprobación.
- [ ] **Exportación:** guardar un JSON y un ZIP de originales. Comprobar que aparecen los archivos y el aviso de éxito; cancelar otra exportación y comprobar que permite volver a intentar.

También se reforzó el guardado: recuperación de originales y reintento de mediciones de voz pendientes. No hace falta provocar fallos en el teléfono. **«Reiniciar datos» borra el perfil y es opcional**; exportar antes si se quiere conservarlo.

Para reportar un problema: anotar versión de Android, pasos, qué esperabas y qué ocurrió; adjuntar captura o JSON si ayuda. Esta entrega ya pasó pruebas en emuladores; falta comprobarla en el S22 Ultra.

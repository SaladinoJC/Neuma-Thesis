# Afinador libre — v0.11 / A1

Implementa el incremento de software [#9](https://github.com/SaladinoJC/Tesis/issues/9). La prueba física sigue pendiente. Los siguientes incrementos incorporan perfiles/afinaciones, guiado, referencias y entrenamiento; esta entrega permite usar el modo Libre sin guardar desempeño.

Instalar `Plataforma/build/apk-archives/entrenamiento-auditivo-v0.11.apk` encima de la versión anterior, sin desinstalar. Conserva paquete y firma debug. El menú debe mostrar versión 0.11.

1. Abrir **Afinador**. Pulsar **Iniciar micrófono**; si Android pide permiso, autorizar y volver a pulsar el botón. El micrófono no se inicia por conceder permiso ni al volver de segundo plano.
2. En **Cuerdas**, tocar una sola cuerda de guitarra estándar: sexta Mi2, quinta La2, cuarta Re3, tercera Sol3, segunda Si3, primera Mi4. Revisar nota medida, frecuencia, objetivo y cents. Una nota sin correspondencia próxima mantiene su lectura sin asignar cuerda.
3. Fijar una cuerda con su botón. El objetivo conserva su nota/octava aunque se toque otra cuerda. **Liberar cuerda** vuelve a seguimiento automático. **Cromático** compara con la nota temperada más próxima y permite sonidos ajenos a la afinación estándar.
4. Probar **Normal ±5 cents** y **Pro ±2 cents**. «Afinado» exige un segundo continuo dentro del margen. Silencio, lectura inválida, cambio de objetivo/criterio y pausa reinician esa evidencia.
5. Detener, salir al menú, bloquear pantalla o cambiar de aplicación. Al volver debe figurar **Micrófono detenido**. Iniciarlo requiere otra pulsación explícita.
6. Volver a la prueba de voz y a los juegos. El afinador no crea sesiones en sus historiales; exportación y reinicio conservan sus contratos previos.

## Recorrido físico pendiente

Registrar S22 Ultra, Android, versión de app, guitarra, cuerdas, afinación, entorno, distancia al micrófono y ruta de audio. Comparar las seis cuerdas con una fuente patrón cuya exactitud se conozca; comprobar especialmente Mi2, ataque/decaimiento, armónicos y octavas. Registrar errores con signo, pérdidas de señal y tiempo de estabilización. Sin patrón no afirmar exactitud física de ±2 cents. Mantener separados resultados sintéticos, emulador y teléfono; esta demostración no acredita eficacia pedagógica.

## Parámetros provisionales

Se conserva `yin-v1`: mono PCM16, 44.100 Hz, ventana 4.096, salto 1.024, rango técnico 55–1.000 Hz, umbral YIN 0,15 y RMS mínimo 0,01. El rango comprobado aquí con sintéticos corresponde a objetivos estándar Mi2–Mi4 y Mi3 como caso de octava; no acredita todos los presets futuros.

Temperamento igual, La4=440 Hz. Seguimiento automático por distancia absoluta, sin plegado de octava: candidato a ≤75 cents y ventaja ≥25 cents respecto del segundo candidato. Fuera de esa correspondencia no se asigna cuerda. Son elecciones del agente sin calibración física.

`tuner-continuity-v1`: margen inclusivo, permanencia mínima 1.000 ms y separación máxima de 250 ms entre lecturas, usando reloj monótono. Una vigilancia cada 100 ms descarta la lectura mostrada si no llegan muestras durante más de 250 ms. El criterio se reinicia al cambiar precisión, seguimiento o cuerda, incluso con el micrófono activo. No hay DDA ni corrección de octavas ni almacenamiento de PCM/resultados.

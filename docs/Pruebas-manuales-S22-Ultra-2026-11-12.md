# Pruebas manuales de Neuma — 12/11/2026

Observaciones informadas por el autor al seguir la [guía S22 Ultra](Guia-pruebas-manuales-S22-Ultra.md).
No representan pruebas ejecutadas por el agente.

## Condiciones

- Fecha y hora: 12/11/2026, 19:16 (Argentina).
- Samsung Galaxy S22 Ultra SM-S908E.
- Sistema interpretado como **One UI 8.0 / Android 16**, pendiente de confirmar
  esa lectura del mensaje original: «Android 8.0 One UI Android 16».
- Versión anterior: 0.12.20. Instaló el APK indicado para 0.12.20;
  comprobación posterior en Más no informada.
- Batería: 63 %, sin ahorro de energía.
- Salida: parlantes del móvil; volumen: 90 %.
- Habitación sin ruido ambiente según el autor; sin medición acústica.
- Guitarra criolla en afinación estándar; distancia fuente–teléfono: 5–10 cm.
- Sin grabación de pantalla.
- Fuente/zoom, navegación y exportación previa: no informados.

## Simón dice

**Resultado global informado: funciona correctamente, sin problemas observados.**
El autor probó funcionamiento, DDA, resultados, giro del dispositivo e
interrupción/cierre de la app. No informó cantidades de partidas, valores de
dificultad, pasos detallados ni archivos de evidencia. No se asigna aprobación
automática a todos los casos de la guía ni se infiere calibración del DDA.

## ¿Qué instrumento es?

**Resultado global informado: todo funciona correctamente**, con una mejora
solicitada: poder responder antes de que termine la melodía. No se atribuye
cobertura individual a todos los casos I1/I2 ni a cada muestra del banco.

Mejora implementada en la candidata **0.12.21/código 36**;
[verificación local](Verificacion-instrumentos-v0.12.21.md). La instalación y
prueba física de esa candidata no fueron informadas todavía. Los resultados
anteriores pertenecen a 0.12.20.

## Rhythm Tap

Problemas informados en la pasada iniciada con 0.12.20 (no se informó haber
instalado 0.12.21): conteo visual desalineado del metrónomo al subir BPM,
dificultad para registrar correctamente cuatro semicorcheas o tempos altos y
retraso audible de taps reales que desconcentra. El autor indica que necesita
jugar con ese sonido apagado y solicita una sección previa de calibración.
No se informaron BPM exactos, exportaciones ni mediciones de latencia.

## Defender

El autor informa funcionamiento correcto sin errores encontrados por el momento.
Solicita controlar también el intervalo de aparición de enemigos y permitir
viajes de hasta 3 s en práctica o con buen desempeño. Describe el límite que
percibe como 5 s; el código de esta candidata fija 6 s, diferencia conservada
sin sustituir su observación. No se infiere cobertura de todos los casos.

## Continuación

### Afinador: cuerdas agudas

El autor informa que en **Libre, Guiado y Oído**, con cuerdas **2 (Si/B) y
1 (Mi/E)** de su guitarra criolla, aparece la nota reconocida pero el sonido
decae antes de confirmarla; lo describe como cancelación de la escucha. No
informó mensaje de error ni una nueva versión instalada para este bloque.
Se conserva como observación de la pasada en curso, sin medir duración o RMS
del instrumento real ni dar por confirmada exactitud en cents.

La candidata **0.12.23/código 38** implementa un ajuste provisional del afinador
y conserva las mejoras anteriores. [Banco, cambios y límites](Verificacion-afinador-v0.12.23.md).
Instalación y prueba física de esta candidata todavía no informadas.

Correcciones de Rhythm Tap y ampliación de presión de Defender implementadas
en candidata **0.12.22/código 37**, con 302 JVM aprobadas y lint sin errores.
[Método, APK y límites](Verificacion-ritmo-defender-v0.12.22.md). Instalación y
prueba física de esta candidata todavía no informadas. Seguir R0/R1/D1 de la
guía actualizada y conservar por separado los resultados de cada versión.
Duración personal de la sesión pendiente; la hora de preparación no basta para medirla.

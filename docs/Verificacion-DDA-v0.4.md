# Verificación de DDA de escuchas v0.4

Fecha: 2026-10-09. Incremento #4 autorizado por el usuario mientras prueba v0.3.1. Alcance y configuración en [DDA-instrumentos-v0.4.md](DDA-instrumentos-v0.4.md); revisión en [Revision-prototipo-v0.4.md](Revision-prototipo-v0.4.md).

## Comando final

Windows, JBR de Android Studio, SDK Android local y emulador Pixel_6/API 34 (Android 14), `emulator-5554`. No se conectó ni probó el S22 Ultra. El emulador se ejecutó sin salida acústica.

Desde `Plataforma/`:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = 'C:\Users\Juan\AppData\Local\Android\Sdk'
$env:ANDROID_SERIAL = 'emulator-5554'
.\gradlew.bat --offline :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:connectedDebugAndroidTest
```

Resultado final: **BUILD SUCCESSFUL en 5 min 12 s**. 34 pruebas JVM (7 DDA/sesión de instrumentos, 10 sesión de instrumentos, 8 sesión de Simón, 5 ViewModel de instrumentos y 4 ViewModel de Simón) y 13 Android (2 DDA, 3 instrumentos, 7 Simón, 1 teclado) aprobadas, sin fallos ni omisiones. Lint sin errores y 3 advertencias. APK debug compilado. Los avisos de Gradle obsoleto no impidieron construir.

## Conductas comprobadas mediante sesión pública

- Automático comienza en A aunque se intente indicar C. Cada partida arranca con tres escuchas, sin ventana/espera heredadas.
- Tres aciertos con primera escucha reducen 3→2 para la cuarta pregunta; después de las rondas 4/5 sin otro ajuste, seis aciertos reducen 2→1 para la séptima. La pregunta iniciada conserva su ayuda y el mínimo es una escucha.
- Tres aciertos seguidos de errores recuperan 2→3 tras la sexta respuesta. La traza posterior distingue propuesta de aumento del límite superior que impide aplicarla.
- Aciertos usando las reescuchas siguen siendo correctos: exactitud uno, autonomía cero y centroide cero mantienen ayuda. La alternancia tampoco la reduce con los parámetros actuales.
- Las primeras dos respuestas registran datos insuficientes, sin inferencia. Configuración con reglas vacías registra diagnóstico, centroide nulo y ninguna retirada de ayuda.
- Los centroides extremos se comparan con ±11/18, valor continuo independiente, con tolerancia 0,003; se comprueban pertenencias en los extremos y ventana exacta.
- La décima respuesta registra inferencia sin aplicar cambio para una pregunta inexistente. Manual conserva nivel A/B/C seleccionado, tres escuchas y una propuesta no aplicada.
- Reabrir el archivo conserva configuración, origen, ventana, pertenencias, reglas y decisión. Las pruebas previas comprueban compatibilidad con registros de v0.3 y preservación de melodías.

## ViewModel y simulación

La simulación usa la misma sesión con audio ficticio y almacenamiento en memoria, fuera del hilo principal. Una prueba verifica que no incrementa las reproducciones del reproductor real, no cambia estado de la partida y deja vacío el historial real mientras guarda únicamente su resultado simulado.

Otra regresión, originada en la revisión Spec, configura reglas vacías, versión distinta y umbral 0,4: el inspector recibe la configuración efectiva con partida aún activa y sin resultado; la simulación utiliza la misma configuración y mantiene ayuda por falta de salida. Pasó rojo → verde.

## Android

Dos nuevas pruebas recorren el panel y los escenarios: recuperación de ayuda 3→2/2→3 en preguntas 3/6, mantenimiento de ayuda en aciertos asistidos/alternancia, navegación por decisiones y cierre del panel. Se inicia después una partida automática real en A con las tres escuchas intactas y se inspecciona la insuficiencia de datos tras la primera respuesta.

Se conservan las once pruebas Android anteriores, incluyendo diez preguntas manuales, recreación, historial, límites de escucha y reproducción real de los 45 OGG de instrumentos en nueve frases. También se verifican reproducción/teclado/rotación/persistencia de Simón.

Las primeras aserciones del panel comparaban textos parciales como si fueran completos; se corrigió la prueba para pedir coincidencia por subcadena. Dos verificaciones Android se superpusieron y una encontró bloqueado el archivo UTP de resultados en Windows; no se trató como un defecto de la app. La comprobación final se ejecuta secuencialmente, sin otra ejecución Gradle concurrente.

Después de las pruebas se instaló v0.4 y se inspeccionaron preparación automática/manual y panel de simulación en el emulador. Capturas en `docs/imagenes/prototipo-v0.4/`: menú, preparación con inicio fijo visible y datos/reglas/ajuste de la tercera pregunta simulada. El nivel C de preparación requiere desplazarse en la pantalla observada; el botón de inicio permanece accesible. Se comprobó visualmente texto, controles y separación entre simulación y partida real, sin escucha acústica.

## APK y recorrido físico

Artefacto: `Plataforma/app/build/outputs/apk/debug/entrenamiento-auditivo-v0.4.apk`, versión 0.4/versionCode 6, mismo paquete y firma debug para actualizar sin desinstalar. Tamaño: 12.034.978 bytes. SHA-256: `4c535c06f8444a43b946e07aaf05e1f88a236e47d4fa5a7dc97668023457c0ba`. Se conserva también una copia de v0.3.1 en `Plataforma/build/apk-archives/` y se restauró su ruta de descarga tras compilar.

En S22 Ultra:

1. Instalar encima de v0.3.1 y comprobar historiales.
2. Elegir Automático: iniciar A con tres escuchas; jugar diez preguntas y revisar ayuda para la próxima pregunta, contador, feedback y resumen.
3. Abrir DDA durante feedback y recorrer sus decisiones; contrastar indicadores, pertenencias, reglas, centroide, propuesta y motivo.
4. En Simulación, ejecutar los cuatro escenarios y recorrer preguntas 3/6. Comprobar marca de simulación, ausencia de audio y partida/historial reales intactos al cerrar.
5. Elegir manualmente A/B/C: conservar tres escuchas, independientemente de aciertos.
6. Girar/recrear, salir durante reproducción y reabrir; verificar la conducta previa y la persistencia de resultados.

Anotar versión Android, altavoz/auriculares, volumen/ruido y observaciones. Los escenarios son resultados simulados, no desempeño humano. No se midieron latencia acústica ni eficacia pedagógica. El motor y sus parámetros están pendientes de calibración; desbloqueos/voz/exportación/reinicio corresponden a tickets posteriores.

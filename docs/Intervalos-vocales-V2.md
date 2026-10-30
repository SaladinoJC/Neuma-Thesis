# Intervalos vocales V2 — ronda manual en Android

Registro histórico de V2. [V3](Intervalos-vocales-V3.md) agrega persistencia,
historial y exportación; sus pendientes prevalecen al retomar.

Usuario, 2026-10-30: «dale, hacelo», después de proponer conectar V1 a una ronda
jugable con referencia y micrófono. Implementación local **0.12.16/código 31**,
candidata sin ejecución Android: se mantiene la orden de no abrir el emulador.

## Recorrido

1. Abrir **Juegos → Índice → Intervalos vocales → Jugar**, quinta página del libro.
2. Elegir intervalo, dirección y primera nota de referencia. Por defecto: quinta
   justa ascendente desde La3. Se ofrecen unísono e intervalos de 1–12 semitonos,
   ascenso/descenso y Do3/La3/Do4/La4 como primeras notas de referencia.
3. **Escuchar intervalo** reproduce las dos notas completas. El micrófono queda
   detenido. Cambiar la selección exige escuchar la nueva referencia.
4. Si falta permiso, **Habilitar micrófono** abre la solicitud Android. Conceder
   permiso no inicia captura: tocar luego **Cantar primera nota**. Una respuesta
   de permiso tardía nunca inicia una ronda automáticamente.
5. Sostener la primera nota sin vibrato hasta su confirmación. El capturador se
   libera antes de habilitar **Cantar segunda nota**. La nota cantada fija el ancla
   relativa; ambas notas pueden trasladarse a un registro cómodo, dentro del rango.
6. Tocar ese botón y sostener la segunda nota. El indicador muestra frecuencia y
   error en cents respecto del intervalo esperado, con orientación grave/agudo.
7. Al adquirir una segunda nota estable se muestra el intervalo cantado, el error
   firmado y si quedó dentro de la tolerancia experimental. Una nota estable
   incorrecta completa la ronda con feedback de error; no exige acertar para terminar.
8. **Nueva ronda** reinicia la práctica y requiere volver a escuchar. **Cancelar**,
   volver al menú o pasar a segundo plano interrumpen sin completar ni reanudar
   automáticamente. El resumen sigue en memoria al volver, hasta Nueva ronda,
   reinicio del perfil o muerte del proceso.

La práctica no escribe historial ni progreso y no aparece en exportación JSON/ZIP.
La pantalla lo indica. No se guarda audio. DDA, persistencia/exportación y niveles
de andamiaje superiores quedan para incrementos posteriores.

## Coordinación de audio y ciclo de vida

`VocalIntervalViewModel` reutiliza `AndroidReferencePlayer`, `AndroidVoiceCapture`
y `VocalIntervalSession`. Acciones y mutaciones se serializan en Main; el capturador
cruza explícitamente desde IO. Una generación invalida callbacks anteriores a una
cancelación. Cada operación espera la liberación de la anterior antes de comenzar;
el botón de segunda nota permanece bloqueado durante la liberación de la primera.
No hay reproducción y captura simultáneas dentro del juego.

La transición entre notas usa **dos capturas nuevas**, evitando que ventanas PCM
pendientes de la primera etapa se evalúen como segunda nota. Volver al menú espera
la liberación de audio antes de navegar. El drenaje/reinicio de Datos incluye el
modelo vocal, sin agregar archivos de perfil. Una recreación o pausa interrumpe
la actividad de audio; conserva el resumen mediante ViewModel cuando corresponde.

El límite global de 60 s empieza al iniciar la primera captura e incluye la espera
entre notas. Un watchdog de 50 ms comprueba el límite sin consumir timestamps de
las estimaciones; funciona también en silencio o sin callbacks. Al expirar libera
el capturador y conserva un cierre por timeout, sin acierto/error inventados.

Se consumen **todas** las estimaciones de YIN, sin el recorte de 200 ms del laboratorio.
Se conserva en memoria la serie de V1. La tasa de captura real debe ser 44.100 Hz;
un error, fin inesperado de captura o fallo de referencia se comunica en pantalla.
No se incorporan ML, Oboe, persistencia ni nuevas dependencias.

## Parámetros y límites

Se mantienen los parámetros provisionales de [V1](Intervalos-vocales-V1.md):
500 ms de sostenido, hueco máximo 100 ms, amplitud máxima 35 cents, tolerancia
±50 cents, rango 55–1000 Hz y máximo 10.000 mediciones. No se distingue vibrato.
Referencia: dos sinusoides propias de 600 ms cada una, amplitud 0,2, fades de
20 ms y espera programada de 150 ms entre reproducciones. Esa espera no es una
medición de separación acústica ni de latencia del dispositivo.

La selección de intervalos/registros y el acceso manual completo son elecciones
técnicas provisionales para probar el flujo, no un currículum aprobado ni contenido
desbloqueado. Los registros ofrecidos mantienen ambas referencias en el rango
de YIN incluso con ±12 semitonos. El núcleo conserva errores de octava, no los
reduce módulo 1200. No se implementa CA ni una puntuación pedagógica validada.

## Presentación Music Garden

Tema `TunerGardenTheme`, tokens `GardenTokens`, Fraunces/Nunito Sans, papel cálido
y acciones verdes reutilizados. Pantalla con lectura vertical, acciones explícitas,
selectores con texto y áreas táctiles mínimas de 48 dp; fila de dirección desplazable
y scroll vertical para texto ampliado. Los estados importantes tienen región viva
accesible; el indicador de frecuencia no anuncia cada frame. No hay animaciones
nuevas, por lo que la tarea no añade movimiento que reducir.

La portada reutiliza provisionalmente la ilustración existente de Instrumentos:
su uso compartido no la convierte en arte vocal final. No se agregaron bitmaps,
fuentes, iconos ni librerías. No hay pentagrama: nombres y distancias se derivan
del intervalo estructurado. No se usa imagen generada ni emoji como notación.
Logros conserva sus cuatro páginas anteriores; V2 no inventa progreso vocal.

Aplicación de `design-taste-frontend`: se toman jerarquía de la tarea, estados
honestos, legibilidad y acción principal. Se conserva el diseño Android vigente;
no se aplica un shell web ni otra dirección visual de la librería. La inspección
de navegador y las capturas nativas no se ejecutan bajo la restricción de no emular.
La validación visual real sigue pendiente, sin afirmar comprobación en tamaños
que todavía no se ejecutaron.

## Verificación y demostración pendientes

Cierre de host: **239 JVM sin fallos (11 nuevas)**, lint **0 errores/10 avisos**,
app y AndroidTest ensamblados. Última comprobación: **3 min 32 s**, offline,
un worker, 1536 MiB, SerialGC, dos procesadores y prioridad BelowNormal.
**356 fuentes/configuraciones** idénticas por SHA-256 a la copia aislada probada;
snapshot exacto conservado. Los **149 assets empaquetados** coinciden con D5.
APK: **15.888.475 bytes**, SHA-256
`add1cf61345f419856adc2b8ea0cedd264ad5600b9600253168e4ce1b882dcf0`;
metadatos 0.12.16/31, presencia del código vocal y firma compatible con D5
comprobados, sin instalar. PDF de 38 páginas en tres pasadas, sin errores,
referencias indefinidas ni desbordes horizontales; seis avisos verticales y
bibliografía vacía. Páginas físicas 26/27/30 inspeccionadas.

Pruebas por acciones públicas del modelo con captura, reproducción y reloj
controlados en sus límites: ronda completa, dos recursos separados, error
estable, cancelación, callback tardío, timeout, fallo de referencia/captura,
cambio de selección y protección de la ronda al pedir otra antes de terminar.
Son casos sintéticos JVM, no sesiones de estudiantes.

Dos Android preparadas: navegación real a quinta página/retorno y referencia
con dos notas sintéticas hasta el resumen. La segunda usa capturador controlado,
sin atribuir funcionamiento al hardware. Se actualiza la regresión del libro
para que la última página de Juegos sea la quinta y Logros conserve cuatro.

Desde `Plataforma/`, con JDK/SDK configurados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest --offline --no-daemon --max-workers=1 '-Dorg.gradle.jvmargs=-Xmx1536m -XX:+UseSerialGC -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8'
```

Cuando el usuario permita emular: ejecutar `VocalIntervalAndroidTest` y regresión
del menú; comprobar 411/360/320 dp, texto 200 % a 320, etiquetas largas, popups,
señal válida/silencio, error estable, cancelación durante cada nota de referencia,
permiso denegado/aceptado/revocado, retorno de Ajustes, recreación y segundo plano
en ambas capturas y entre notas. Revisar TalkBack sin anunciar cada frame.
Con teléfono disponible, comprobar audibilidad, ruido, registro vocal y liberación
del micrófono; no dar por calibrados los parámetros por aprobar pruebas sintéticas.

La candidata se archiva como
`Plataforma/build/apk-archives/neuma-v0.12.16-intervalos-vocales-v2-prueba-sin-emulador.apk`.
La evidencia de host y hashes quedan en `docs/evidencias/intervalos-vocales-v2/`;
logs/snapshot completos, en `Plataforma/build/verification/2026-10-30-vocal-v2/`.
No se instala ni se publica en esta sesión. Próximo incremento: persistencia y
exportación del resultado vocal antes de incorporar DDA y progresión.

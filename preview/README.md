# Neuma — prototipo visual descartable

Exploración **Quiet Studio + Editorial**, solicitada el 2026-10-29. Pregunta: ¿puede una plataforma de educación musical sentirse como un instrumento de estudio sereno y agradable? **Neuma es el nombre elegido por el usuario**; logo horizontal y favicon temporales provienen del paquete corregido `docs/logo/`. La dirección visual y las variantes siguen en evaluación y no sustituyen Music Garden en Android.

## Abrir

Abrir **`neuma-concept.html` con doble clic** en Chrome, Edge, Firefox o Safari moderno. Todo el código, los iconos y los contornos musicales están dentro del HTML. No requiere servidor, cuenta, instalación, conexión ni archivos de sonido. Los ajustes y resultados viven en memoria y se pierden al recargar.

También se puede servir desde la raíz del repositorio:

```powershell
python -m http.server 8766 --bind 127.0.0.1 --directory preview
```

Visitar `http://127.0.0.1:8766/neuma-concept.html`. Para verlo en un teléfono se puede copiar el HTML o servirlo en una red local de confianza con una dirección accesible desde el dispositivo; el comando anterior escucha solamente en la propia computadora.

## Recorrido sugerido

1. **Inicio:** tocar Continuar práctica o Ritmo. Lectura abre Convergencia. Oído y Teoría ofrecen pequeñas muestras interactivas, no pantallas vacías. La navegación inferior abre el catálogo, el progreso y los ajustes.
2. **Sonido:** abrir Ajustes, activar Efectos de interfaz y comparar Fieltro, Madera, Papel y Confirmación. El contexto de audio se habilita con una interacción. Empezar con el volumen del dispositivo bajo. El icono de altavoz de la cabecera activa/desactiva solo los efectos decorativos.
3. **Ritmo:** Escuchar ejemplo reproduce la partitura de dos compases. Empezar práctica da cuatro pulsos de entrada, luego hay que tocar cada ataque en la superficie grande. También se puede usar Espacio, salvo cuando otro botón tiene el foco. Repetir conserva el patrón; Reiniciar limpia el intento. No se necesita micrófono.
4. **Convergencia:** Comenzar lectura muestra notas en clave de sol. Responder con Do–Si o las teclas 1–7. Una respuesta correcta resuelve la nota de ese nombre más próxima a su llegada; una incorrecta permite reintentar sin consumir oportunidades. Tres llegadas terminan el recorrido. Pausar congela el reloj y tapa las notas. Reiniciar vuelve a la preparación. Se pausa al salir o esconder la pestaña.
5. **Composiciones del inicio:** usar las flechas del comparador; en móvil, abrirlo con el pequeño icono de composición de la cabecera. Las flechas del teclado también cambian la variante cuando se está en Inicio. El resto de las pantallas conserva su diseño para poder comparar la entrada al mismo producto.

| URL | Composición |
| --- | --- |
| `neuma-concept.html?variant=A` | **Estudio:** práctica destacada en una superficie salvia, categorías en dos columnas. Predeterminada. |
| `neuma-concept.html?variant=B` | **Cuaderno:** ficha editorial blanca y categorías como índice vertical. |
| `neuma-concept.html?variant=C` | **Sesión:** parámetros primero, partitura abierta y acción independiente. |
| `neuma-concept.html?screen=rhythm` | Acceso directo a ritmo. |
| `neuma-concept.html?screen=defender` | Acceso directo a Convergencia. |

## Decisiones de esta muestra

- Paleta explícita del encargo, con variables CSS locales. No se modifica `design-tokens.json`. Marfil `#F7F7F2`, blanco, verde `#365E56`, salvia `#DCE9E1`, terracota ocasional y texto `#202B2A`. Un tono más oscuro de terracota se usa para texto de feedback por contraste.
- Sans del sistema para controles y Georgia para la voz editorial. Sin fuentes remotas ni peso de descarga. El aspecto tipográfico exacto puede variar por sistema operativo.
- Sin imágenes de stock, personajes, emojis, glassmorphism, gradientes ni música ambiental. Las pequeñas sombras físicas son intencionales en controles, no un efecto de vidrio. El marco de teléfono solo aparece en escritorio.
- SVG musicales desde datos. Los glifos de clave y cabezas son contornos de **Bravura**, no caracteres improvisados ni imágenes de una partitura. Las duraciones suman exactamente ocho negras en 4/4; las corcheas se agrupan de a dos. La notación rítmica usa clave de percusión. Las notas de Convergencia abarcan Mi4–Re5 en clave de sol.
- Web Audio separa efectos y música. Clics de ruido filtrado y resonancias amortiguadas; confirmación breve Do–Sol–Do. Los efectos decorativos se cortan y bloquean durante la cuenta y ejecución de ritmo, el ejemplo rítmico y las escuchas de Oído/Teoría. El sonido opcional del tap pertenece al ejercicio y reproduce los toques reales. Sin sonido de error en Convergencia.
- Presión de 150 ms y respuestas de 180–350 ms. `prefers-reduced-motion` y el ajuste manual eliminan transiciones, ondas y desplazamiento continuo; en Convergencia quedan notas fijas, tiempo más amplio y señal visual de cercanía del vencimiento. Es una alternativa provisional de accesibilidad, no una solución validada. La vibración es optativa y depende del navegador y dispositivo.
- Las superficies táctiles principales tienen al menos 44 px. El inicio completo entra a 390 × 844; a menor altura o con texto ampliado se permite desplazamiento interno, manteniendo la navegación fuera de ese desplazamiento.

## Qué se simula y qué se mide

El inicio parte de **tres prácticas de muestra, 6 + 7 + 5 = 18 minutos**, con una meta propuesta de cinco prácticas. Cada ejecución finalizada suma su tiempo y resultado de esta sesión, separado de las muestras. Esto no acredita constancia real ni eficacia pedagógica.

Ritmo usa una partitura fija y 80 bpm. El tiempo musical se toma de `AudioContext.currentTime`; la edad del evento de entrada se descuenta usando su `timeStamp`. Cada tap se asigna al ataque no utilizado más cercano dentro de **±180 ms**, un margen propuesto solo para esta demo. El resultado muestra aciertos, omisiones, extras y desvío medio absoluto de los ataques asignados. Sin taps hay cero aciertos y nueve omisiones; no se generan resultados al azar. **No hay calibración de latencia acústica ni validación en hardware móvil.**

Convergencia usa una secuencia reproducible de notas naturales, máximo dos objetivos simultáneos y reservas de trayectoria para evitar que se superpongan los pentagramas. Viaje normal de 12 segundos; la llegada se evalúa antes que la respuesta con reloj activo monótono. Los objetivos conservan tamaño y orientación. Sin DDA, acordes, selección de alteraciones, desbloqueos, persistencia ni economía de recompensas.

Se inspeccionaron `MusicGardenMenu.kt`, `RhythmApp.kt`, `RhythmSession.kt`, `DefenderSession.kt`, `DefenderMusic.kt`, las especificaciones y las fichas de ritmo/lectura. La **escucha previa** y los **dos compases** responden a este encargo y difieren del flujo Android confirmado de cuatro compases sin escucha del patrón antes de comenzar. El centro geométrico, el máximo de dos objetivos y la denominación Convergencia son propuestas visuales; no cambian las decisiones funcionales de producción.

## Evidencia y conservación

Capturas, mediciones y revisión en [`review/`](review/). `verification.json` registra la comprobación funcional en navegador Chromium de escritorio emulando tamaños móviles; las capturas no equivalen a una prueba física en teléfono. El navegador automatizado silencia su salida: la señal de audio se comprobó con su analizador, no mediante una escucha humana. Para juzgar timbre, volumen y comodidad hay que escuchar la demo.

La revisión visual y sus excepciones justificadas están en [`review/REVIEW.md`](review/REVIEW.md). Los assets y licencias figuran en [`ASSET_MANIFEST.md`](ASSET_MANIFEST.md).

Skills aplicadas: [prototype](C:/Users/Juan/.agents/skills/prototype/SKILL.md), rama UI, y [design-taste-frontend](C:/Users/Juan/.agents/skills/design-taste-frontend/SKILL.md), modo app. Estilo de apoyo `editorial-greeting`, semilla `2107736648`, con paleta y tipografías conservadas del encargo. Se priorizan el marco móvil, los controles táctiles y los microdesplazamientos pedidos por el usuario sobre el shell de escritorio y las restricciones generales de la skill.

No se ha elegido una variante ni aprobado el rediseño general. El nombre y la marca temporal sí fueron adoptados, también en Android. El estudio se conserva en `preview/` (no se encontró la rama de respaldo mencionada en la entrega anterior). Antes de integrar el resto del diseño habrá que acordar la dirección, reescribirla para Compose y verificar todas las pantallas y estados nativos.

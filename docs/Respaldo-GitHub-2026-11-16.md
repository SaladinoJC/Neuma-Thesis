# Cierre y recuperación del trabajo — 16/11/2026

Pedido del autor: detener el desarrollo por hoy y dejar el trabajo en GitHub
para poder recuperarlo si pierde la computadora. Informa **8 horas** personales;
se completa H0014 sin duplicar la jornada. No se inicia Defender ni otra tarea.

Repositorio privado: https://github.com/SaladinoJC/Tesis

## Dónde recuperar la versión actual

La rama de trabajo es **`trabajo/integracion-v0.12.15`**. Su nombre histórico no
indica la versión de la aplicación: al cierre contiene Neuma **0.12.27/código 42**
y el informe de **92 páginas**. No se integra automáticamente en `main`.

El respaldo se identifica mediante la etiqueta
**`backup-2026-11-16-v0.12.27`** y una entrega en borrador dentro del repositorio
privado. La entrega es de recuperación; no acredita aceptación de los cambios
visuales ni validación nativa/acústica. El PR #30 existente conserva su estado.

## Contenido del respaldo

- Git: código, documentos, originales académicos, LaTeX, bibliografía, figuras,
  assets/editables, scripts, encuesta, guías, horas y evidencias duraderas.
- `neuma-repositorio-20261116.bundle`: historia y referencias Git locales,
  incluidas ramas de prototipos y trabajos anteriores que no estén en el remoto.
- `neuma-apk-historicos-20261116.zip`: archivos de APK conservados localmente.
- `neuma-materiales-locales-20261116.zip`: materiales/evidencias adicionales
  ignorados por Git, originales de audio, fuentes consultadas, verificaciones y
  resultados de compilación seleccionados. Mantiene rutas relativas al proyecto.
- `neuma-worktrees-20261116.zip`: inventario de las seis carpetas de trabajo
  anteriores y sus resultados conservados. Sus fuentes confirmados se recuperan
  desde las ramas incluidas en el bundle; no tenían cambios sin confirmar.
- APK actuales de Garden y Quiet Studio, informe PDF y manifiesto con hashes.

No se guardan credenciales, configuración local del SDK, cachés de Gradle/Kotlin,
perfiles temporales de navegador, bloqueos de Office ni compilación intermedia
reconstruible. Se registra qué se excluye y por qué; no se borran archivos locales.
La clave debug situada fuera del proyecto, sesiones y credenciales de servicios
no forman parte del respaldo. En otra PC se genera una clave debug distinta;
para instalar sobre una app ya existente debe conservarse aparte la firma usada.

## Recuperación desde otra computadora

Con Git, GitHub CLI y acceso a la cuenta propietaria:

```powershell
gh auth login
gh repo clone SaladinoJC/Tesis Tesis
Set-Location Tesis
git switch trabajo/integracion-v0.12.15
gh release download backup-2026-11-16-v0.12.27 --dir ../respaldo-neuma
```

La entrega es un borrador privado: usar una cuenta con acceso de escritura al
repositorio. También puede descargarse desde Releases al iniciar sesión en GitHub.
Verificar los archivos descargados contra `SHA256SUMS.txt` y el manifiesto antes
de extraerlos. Para consultar un hash en PowerShell:

```powershell
Get-FileHash ../respaldo-neuma/neuma-materiales-locales-20261116.zip -Algorithm SHA256
```

Extraer el ZIP de materiales y el de APK en la raíz recuperada del proyecto,
conservando sus rutas. El ZIP de worktrees mantiene una carpeta por trabajo para
consulta; no se debe extraer todo encima del proyecto principal.

El bundle permite recuperar las ramas locales aunque no estén publicadas como
ramas individuales. Puede inspeccionarse y clonarse por separado:

```powershell
git bundle verify ../respaldo-neuma/neuma-repositorio-20261116.bundle
git clone ../respaldo-neuma/neuma-repositorio-20261116.bundle ../Tesis-bundle
```

Para compilar: instalar Android Studio/JDK/SDK y seguir `Plataforma/README.md`;
recrear `ANDROID_HOME`/`JAVA_HOME` según la nueva PC. Para el informe, instalar
XeLaTeX/Biber y seguir `Informe/LEEME.md`. El PDF adjunto permite leerlo sin TeX.
Las herramientas instaladas se reconstruyen; el trabajo propio se conserva en
Git, bundle y archivos adjuntos.

## Estado de la jornada

Music Garden confirmado con el director según relato del autor. Incrementos de
Instrumentos, Rhythm Tap e intervalos vocales implementados localmente. Última
verificación: 308 JVM aprobadas, lint sin errores (17/19 avisos), APK de ambas
variantes y AndroidTest compilados. Revisión nativa, accesibilidad y acústica
pendientes; #32/#33/#34 continúan abiertos. Próximo trabajo: Defender #35.
Personaje pausado; sonidos de interfaz y respuesta táctil pendientes.

La comprobación remota y los identificadores finales se registran al terminar
la transferencia. No se atribuye una nueva ejecución de pruebas de producto a
este cierre: se respalda la evidencia ya obtenida.

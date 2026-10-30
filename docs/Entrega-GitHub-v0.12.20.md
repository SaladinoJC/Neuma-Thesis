# Entrega de Neuma 0.12.20 en GitHub

Usuario, 2026-10-30: «ok, dejá todo en github con buenas practicas de git».
Se conserva la rama publicada `trabajo/integracion-v0.12.15` y se actualiza el
[PR #30](https://github.com/SaladinoJC/Tesis/pull/30) en borrador. No se integra
en `main`; siguen pendientes las verificaciones nativas descritas en la guía V6.

## Historial y alcance

- `b2b524b`: implementación vocal, pruebas y guías de los tres andamiajes.
- `413061a`: informe, trazabilidad y evidencia V1–V6.
- El registro de esta publicación se conserva en un commit documental posterior.

La etiqueta anotada **`v0.12.20-preview.1`** señala exactamente
`413061a22a3648bb3bf1ab51838235d55319cf30`. Rama y etiqueta se suben mediante
push atómico, sin reescribir el historial. Los fuentes de la entrega coinciden
con los 370 archivos cotejados durante V6; no se modificó código ni se repitieron
pruebas durante esta publicación. `git diff --check` pasó y la revisión de patrones
de secretos no encontró coincidencias en los cambios ni en las evidencias adjuntas.

## Adjuntos

[Entrega preliminar en borrador](https://github.com/SaladinoJC/Tesis/releases/tag/untagged-2f5704ed0bbd5a11475f),
conservada dentro del repositorio privado:

| Archivo | Contenido |
| --- | --- |
| `neuma-v0.12.20-intervalos-vocales-v6-prueba-sin-emulador.apk` | APK debug firmado, 15.305.656 bytes |
| `neuma-informe-v0.12.20.pdf` | Informe de 41 páginas |
| `neuma-verificaciones-vocales-v1-v6.zip` | 195 archivos: fuentes probadas, logs, resultados, bancos sintéticos y capturas del informe |
| `manifest-entrega.json` | Commit, versión, condiciones de verificación, tamaños y hashes |
| `SHA256SUMS.txt` | SHA-256 de APK, PDF, ZIP y manifiesto |

APK SHA-256: `7c617dd076f7d547b6d19785f317edf9d8dfdeeebcc3158fe8cb4e8610f3f40c`.
Los tamaños y hashes de los cinco adjuntos se cotejan con los metadatos de GitHub.
Los generados se adjuntan a la entrega; `.gitignore` mantiene excluidos compilación,
cachés y configuración local. El código, las guías y la síntesis de evidencia
permanecen versionados en Git.

## Estado de validación

292 JVM aprobadas, lint 0 errores/10 avisos y ambos ensamblados correctos.
Diez casos vocales Android actuales compilados, sin ejecutar. Ruta sintética de
750 rondas, sin voz real ni estudiantes. Informe sin errores ni desbordes
horizontales; cinco avisos verticales y bibliografía vacía.

La publicación no agrega validación Android, acústica o pedagógica. No se abrió
emulador ni se instaló APK; Personaje sigue pausado. Quedan selección automática
global de ayuda, revisión nativa/acústica, TalkBack, calibración y D6/#28.

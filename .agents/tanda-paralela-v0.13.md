# Tanda A3 / R2 / D2

Autorizada por el usuario el 2026-10-24 al aceptar los siguientes tres tickets mientras prueba v0.12. Base verificada común: `d0f1624`. Sin push ni cambios externos en issues. Última entrega validada al iniciar: v0.12, en `main` y en `Plataforma/build/apk-archives/`.

| Frente | Rama | Worktree | Dueño |
| --- | --- | --- | --- |
| Afinador A3 / #11 | `trabajo/afinador-a3` | `C:/Users/Juan/Tesis-worktrees/afinador-a3` | agente `afinador_a2` |
| Ritmo R2 / #17 | `trabajo/ritmo-r2` | `C:/Users/Juan/Tesis-worktrees/ritmo-r2` | agente `ritmo_r1` |
| Defender D2 / #24 | `trabajo/defender-d2` | `C:/Users/Juan/Tesis-worktrees/defender-d2` | agente `defender_d1` |
| Integración | `trabajo/integracion-v0.13` | raíz | principal |

## Concurrencia y reserva

- Cada agente edita módulo, UI y pruebas propios. Principal mantiene host/navegación, versión, README y estado global. Cualquier cambio de almacenamiento compartido requiere coordinar dueño y alcance antes de editar.
- Gradle por turnos A3 → R2 → D2; pruebas pesadas/emulador solo en integración después. Un worker, 768 MiB, Kotlin in-process y no-daemon como en el registro anterior.
- Consultar `python .agents/tools/check-codex-budget.py` entre incrementos. Usa solo cuota reportada para la sesión activa, no una estimación de tokens ni tiempo transcurrido. La lectura puede tener demora; revisar `age_seconds`.
- Al 10% restante empezar cierre: detener nuevas ampliaciones, pedir checkpoints y verificar/documentar lo que alcance. Al 5% detener desarrollo. Si la lectura no está disponible, conservar estado y resolver visibilidad antes de iniciar otro incremento.
- Cerrar significa commits por frente, estado de pruebas exacto, APIs/contratos para integrar y próximo paso concreto. Un WIP no equivale a ticket completado ni habilita un APK de entrega. Preservar `main` y APK v0.12 si v0.13 no alcanza verificación.

## Alcance de esta tanda

A3: guiado sexta→primera, un segundo válido por cuerda y revisión final opcional con resumen en memoria; defaults compatibles de configuración. Sin entrenamiento/referencias de A4–A6.

R2: figuras largas/silencios y métricas 2/4, 3/4, 4/4, factibilidad/cuota y notación/evaluación correspondientes. Sin categorías avanzadas, DDA ni persistencia de R3–R6.

D2: N1–N4, clave de fa/mezcla, rangos y alteraciones simples, hasta cuatro enemigos legibles y matching geométrico por grafía. Sin DDA, desbloqueos ni acordes de D3–D7.

## Estado inicial

Agentes trabajando en sus worktrees; todavía sin integración ni nuevas verificaciones. La primera lectura de cuota fue 55% usado/45% restante. Consultar commits y `.agents/estado-prototipo.md` al retomar: este documento se actualizará al cerrar la tanda.

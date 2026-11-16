"""Genera vistas de arquitectura y dominio del capítulo 9, en PDF/SVG.

Ejecutar desde la raíz: python Informe/tools/generar_diseno.py
La arquitectura muestra colaboraciones lógicas; el dominio, relaciones conceptuales.
"""
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle


OUTPUT = Path(__file__).resolve().parents[1] / "figuras"


def box(ax, x, y, text, width=3.1, height=1.25):
    ax.add_patch(Rectangle((x-width/2, y-height/2), width, height,
                           facecolor="white", edgecolor="black", linewidth=1, zorder=3))
    ax.text(x, y, text, ha="center", va="center", fontsize=12, zorder=4)


def arrow(ax, a, b, label="", label_at=None, style="->"):
    ax.annotate("", xy=b, xytext=a,
                arrowprops={"arrowstyle": style, "linewidth": 1, "color": "0.25"}, zorder=2)
    if label:
        x, y = label_at if label_at else ((a[0]+b[0])/2, (a[1]+b[1])/2)
        ax.text(x, y, label, fontsize=10, ha="center", va="center",
                bbox={"facecolor": "white", "edgecolor": "none", "pad": 1.5}, zorder=5)


def canvas(height):
    fig, ax = plt.subplots(figsize=(8, height))
    ax.set_xlim(0, 10.4)
    ax.set_ylim(0.3, 10)
    ax.axis("off")
    fig.subplots_adjust(left=0.02, right=0.98, top=0.99, bottom=0.02)
    return fig, ax


def save(fig, name):
    fig.savefig(OUTPUT / f"{name}.pdf", metadata={"CreationDate": None, "ModDate": None})
    fig.savefig(OUTPUT / f"{name}.svg")
    plt.close(fig)


def main():
    OUTPUT.mkdir(exist_ok=True)
    plt.rcParams.update({"font.family": "DejaVu Sans", "svg.fonttype": "none"})
    fig, ax = canvas(6.5)
    box(ax, 5, 9.0, "Presentación\nCompose y notación local de Rhythm Tap", width=7.4)
    box(ax, 5, 7.1, "Coordinación por actividad\nViewModels, estado observable y ciclo de vida", width=7.4)
    box(ax, 5, 5.2, "Sesiones y reglas musicales\nObjetivos, respuestas, evaluación e historial", width=7.4)
    box(ax, 1.8, 2.6, "Adaptación y progreso\nPolíticas por actividad\nMotor Mamdani")
    box(ax, 5.1, 2.6, "Datos del perfil\nRepositorios de resultados\nArchivos locales")
    box(ax, 8.4, 2.6, "Audio y análisis\nAdaptadores Android\nEstimador YIN")
    arrow(ax, (5, 8.36), (5, 7.74), "acciones y estado", (6.5, 8.05), "<->")
    arrow(ax, (5, 6.46), (5, 5.84), "comandos y resultados", (6.6, 6.15), "<->")
    arrow(ax, (3.0, 4.56), (1.8, 3.24), "evidencia\ny decisión", (1.8, 4.05), "<->")
    arrow(ax, (5.1, 4.56), (5.1, 3.24), "lectura y\nconfirmación", (6.1, 3.95), "<->")
    # Colaboración ViewModel/audio por el borde, sin atravesar las sesiones.
    ax.plot([8.72, 10.05, 10.05, 8.4], [7.1, 7.1, 3.7, 3.7], color="0.25", linewidth=1)
    arrow(ax, (8.4, 3.7), (8.4, 3.24), style="<->")
    ax.text(9.8, 5.1, "captura y reproducción", fontsize=10, rotation=90, ha="center", va="center",
            bbox={"facecolor": "white", "edgecolor": "none", "pad": 2})
    ax.text(5.1, 1.15, "Un módulo Android :app · ejecución local\nLos bloques son responsabilidades, no módulos desplegados por separado.",
            ha="center", va="center", fontsize=11)
    save(fig, "arquitectura-neuma")

    fig, ax = canvas(7.0)
    for x, y, text in [
        (2.6, 8.9, "Actividad musical"),
        (7.7, 8.9, "Configuración\nde práctica"),
        (2.6, 6.6, "Partida"),
        (7.7, 6.6, "Respuesta musical"),
        (2.6, 4.3, "Ronda"),
        (7.7, 4.3, "Decisión\nde adaptación"),
        (2.6, 2.0, "Resultado\nde práctica"),
        (7.7, 2.0, "Progreso\npor habilidad"),
    ]:
        box(ax, x, y, text, width=3.4)
    arrow(ax, (2.6, 8.25), (2.6, 7.24), "organiza", (2.6, 7.75))
    arrow(ax, (6, 8.55), (4.3, 6.94), "fija condiciones", (5.0, 8.0))
    arrow(ax, (2.6, 5.95), (2.6, 4.94), "agrupa cuando\ncorresponde", (2.6, 5.45))
    arrow(ax, (6, 6.35), (4.3, 2.35), "se evalúa", (5.15, 4.5))
    arrow(ax, (2.6, 3.65), (2.6, 2.64), "evaluación del ejercicio", (2.6, 3.15))
    arrow(ax, (4.3, 2.35), (6, 3.95), "evidencia elegible", (5.15, 3.15))
    arrow(ax, (7.7, 3.65), (7.7, 2.64), "puede actualizar", (7.7, 3.15))
    # El cierre de partida también tiene resultado en juegos sin rondas discretas.
    ax.plot([0.9, 0.35, 0.35, 0.85], [6.6, 6.6, 2.0, 2.0], color="0.25", linewidth=1)
    arrow(ax, (0.85, 2.0), (0.9, 2.0))
    ax.text(0.38, 4.5, "cierre del conjunto", rotation=90, fontsize=10, ha="center", va="center",
            bbox={"facecolor": "white", "edgecolor": "none", "pad": 2})
    save(fig, "dominio-neuma")
    print("Arquitectura y modelo conceptual generados en PDF/SVG.")


if __name__ == "__main__":
    main()

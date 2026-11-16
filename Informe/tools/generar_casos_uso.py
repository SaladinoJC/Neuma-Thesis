"""Genera el diagrama UML de casos de uso del capítulo 8 (PDF/SVG).

Desde la raíz: python Informe/tools/generar_casos_uso.py
Requiere matplotlib. Los actores son roles, no cuentas/permisos de la aplicación.
"""
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import Ellipse, Rectangle, Circle


OUTPUT = Path(__file__).resolve().parents[1] / "figuras"
CASES = {
    "CU-01": (4.6, 7.6, "Practicar intervalos\nvocales"),
    "CU-02": (4.6, 6.3, "Practicar sin\ncaptura vocal"),
    "CU-03": (4.6, 5.0, "Consultar resultados\ny progreso"),
    "CU-04": (4.6, 3.7, "Exportar registros"),
    "CU-05": (4.6, 2.4, "Reiniciar perfil"),
    "CU-06": (4.6, 1.1, "Inspeccionar DDA\ny simulaciones"),
}


def actor(ax, x, y, label):
    ax.add_patch(Circle((x, y + 0.55), 0.17, fill=False, linewidth=1))
    ax.plot([x, x], [y + 0.38, y - 0.25], color="black", linewidth=1)
    ax.plot([x - 0.32, x + 0.32], [y + 0.15] * 2, color="black", linewidth=1)
    ax.plot([x - 0.27, x, x + 0.27], [y - 0.65, y - 0.25, y - 0.65], color="black", linewidth=1)
    ax.text(x, y - 0.85, label, ha="center", va="top", fontsize=12)


def main():
    OUTPUT.mkdir(exist_ok=True)
    plt.rcParams.update({"font.family": "DejaVu Sans", "svg.fonttype": "none"})
    fig, ax = plt.subplots(figsize=(8, 7.3))
    ax.set_xlim(-0.1, 9.2)
    ax.set_ylim(-0.05, 8.9)
    ax.set_aspect("equal")
    ax.axis("off")
    ax.add_patch(Rectangle((2.4, 0.25), 4.4, 8.2, fill=False, linewidth=1))
    ax.text(4.6, 8.65, "Neuma", ha="center", fontsize=14)
    actor(ax, 0.7, 5.8, "Usuario")
    actor(ax, 8.2, 3.65, "Responsable\nde evaluación")
    # Asociaciones UML sin flechas: no representan orden de ejecución.
    for code in ("CU-01", "CU-02", "CU-03", "CU-04", "CU-05"):
        _, y, _ = CASES[code]
        ax.plot([1.03, 3.12], [5.95, y], color="0.45", linewidth=0.8, zorder=1)
    for code in ("CU-03", "CU-04", "CU-06"):
        _, y, _ = CASES[code]
        ax.plot([6.08, 7.87], [y, 3.8], color="0.45", linewidth=0.8, zorder=1)
    for code, (x, y, label) in CASES.items():
        ax.add_patch(Ellipse((x, y), 3.0, 1.04, facecolor="white", edgecolor="black", linewidth=0.9, zorder=2))
        ax.text(x, y, f"{code}\n{label}", ha="center", va="center", fontsize=12, zorder=3)
    fig.subplots_adjust(left=0.01, right=0.99, bottom=0.01, top=0.99)
    fig.savefig(OUTPUT / "casos-uso.pdf", metadata={"CreationDate": None, "ModDate": None})
    fig.savefig(OUTPUT / "casos-uso.svg")
    plt.close(fig)
    print("Diagrama UML: seis casos, dos roles; PDF y SVG generados.")


if __name__ == "__main__":
    main()

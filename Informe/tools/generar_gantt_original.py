"""Representa el plan XLSX entregado, sin reconstruir su ejecución real.

Desde la raíz: python Informe/tools/generar_gantt_original.py
Requiere openpyxl y matplotlib. Los originales solo se leen.
"""

import hashlib
import json
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.dates as mdates
import matplotlib.pyplot as plt
from matplotlib.patches import Patch
from openpyxl import load_workbook


ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / "docs/Plan_de_trabajo_y_Gantt_Saladino.xlsx"
OUTPUT = ROOT / "Informe/figuras"
LABELS = {
    "P01": "Revisión bibliográfica inicial",
    "P02": "Estado del arte",
    "P03": "Mecánicas y alcance",
    "P04": "Fichas de actividades",
    "P05": "Arquitectura preliminar",
    "F01": "Cierre bibliográfico",
    "F02": "Alcance y criterios",
    "F03": "Viabilidad Android",
    "F04": "Proyecto y compilación",
    "F05": "Captura de audio",
    "F06": "Estimación de tono",
    "F07": "Medición de línea base",
    "F08": "Motor de dificultad",
    "F09": "Juego vocal de intervalos",
    "F10": "Segundo juego",
    "F11": "Gamificación acotada",
    "F12": "Registro y métricas",
    "F13": "Integración y correcciones",
    "F14": "Preparación de pruebas de uso",
    "F15": "Pruebas de uso",
    "F16": "Análisis de observaciones",
    "F17": "Validación técnica",
    "F18": "Primer informe de avance",
    "F19": "Segundo informe de avance",
    "F20": "Redacción de memoria",
    "F21": "Documentación y publicación",
    "R01": "Reserva de tiempo",
}


def main():
    book = load_workbook(SOURCE, data_only=True, read_only=True)
    records = []
    try:
        for row in book["Detalle de tareas"].iter_rows(values_only=True):
            if row[0] not in LABELS:
                continue
            records.append({
                "id": row[0], "etapa": row[1], "tarea": row[2],
                "inicio": row[4].date().isoformat(),
                "fin": row[5].date().isoformat(),
                "horas": row[6], "estado_original": row[7],
                "depende_de": [x.strip() for x in (row[9] or "").split(",") if x.strip()],
            })
    finally:
        book.close()
    assert len(records) == 27 and len({r["id"] for r in records}) == 27
    assert sum(r["horas"] for r in records) == 400
    assert all(d in LABELS for r in records for d in r["depende_de"])
    OUTPUT.mkdir(exist_ok=True)
    metadata = {
        "fuente": str(SOURCE.relative_to(ROOT)).replace("\\", "/"),
        "sha256_fuente": hashlib.sha256(SOURCE.read_bytes()).hexdigest(),
        "hoja": "Detalle de tareas", "corte_registro": "2026-11-04",
        "interpretacion": "Plan original; no representa ejecución, avance ni horas medidas.",
        "tareas": records,
    }
    (OUTPUT / "gantt-original-datos.json").write_text(
        json.dumps(metadata, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    plt.rcParams.update({"font.family": "DejaVu Sans", "font.size": 10, "svg.fonttype": "none"})
    for part, group in enumerate((records[:13], records[13:]), 1):
        fig, ax = plt.subplots(figsize=(8.0, 5.0))
        for index, record in enumerate(group):
            start = mdates.datestr2num(record["inicio"])
            end = mdates.datestr2num(record["fin"]) + 1  # fecha final incluida
            kind = record["id"][0]
            ax.barh(index, end - start, left=start, height=0.57,
                    color={"P": "0.75", "F": "0.35", "R": "white"}[kind],
                    edgecolor="0.25", linewidth=0.6, hatch="///" if kind == "R" else None)
        ax.set_yticks(range(len(group)), [f'{r["id"]}  {LABELS[r["id"]]}' for r in group])
        ax.invert_yaxis()
        ax.set_xlim(mdates.datestr2num("2026-08-01"), mdates.datestr2num("2027-03-01"))
        ticks = [f"2026-{m:02d}-01" for m in range(8, 13)] + ["2027-01-01", "2027-02-01"]
        ax.set_xticks([mdates.datestr2num(t) for t in ticks],
                      ["Ago.\n2026", "Sep.", "Oct.", "Nov.", "Dic.", "Ene.\n2027", "Feb."])
        ax.grid(axis="x", color="0.88", linewidth=0.6)
        ax.set_axisbelow(True)
        ax.tick_params(axis="y", length=0, labelsize=9)
        ax.tick_params(axis="x", labelsize=9)
        for spine in ("top", "right", "left"):
            ax.spines[spine].set_visible(False)
        legend = [Patch(facecolor="0.75", edgecolor="0.25", label="Previas: estimación original"),
                  Patch(facecolor="0.35", label="Previstas"),
                  Patch(facecolor="white", edgecolor="0.25", hatch="///", label="Reserva")]
        fig.legend(handles=legend, loc="lower center", ncol=3, fontsize=8, frameon=False)
        fig.subplots_adjust(left=0.36, right=0.98, top=0.98, bottom=0.17)
        fig.savefig(OUTPUT / f"gantt-original-{part}.pdf",
                    metadata={"CreationDate": None, "ModDate": None})
        fig.savefig(OUTPUT / f"gantt-original-{part}.svg")
        plt.close(fig)
    print(f"Gantt original: {len(records)} tareas, 400 horas estimadas. Original sin cambios.")


if __name__ == "__main__":
    main()

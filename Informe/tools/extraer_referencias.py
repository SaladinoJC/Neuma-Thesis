"""Genera copias de consulta de los PDF; requiere pdftotext (MiKTeX/Poppler)."""

from __future__ import annotations

import argparse
import hashlib
import json
from pathlib import Path
import shutil
import subprocess


ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "referencias"
SOURCES = (
    "2026_05_27_Estructura Informe TF.pdf",
    "2026_05_27_INSTRUCTIVO_TRABAJO FINAL.pdf",
    "2026_05_29_GUIA OPERATIVA PARA EL DESARROLLO DEL TF V3.pdf",
    "2026_06_05_Apuntes revisión sistemática.pdf",
)


def extract(source: Path) -> tuple[str, dict]:
    result = subprocess.run(
        ["pdftotext", "-layout", "-enc", "UTF-8", str(source), "-"],
        capture_output=True,
        check=True,
    )
    raw = result.stdout.decode("utf-8").replace("\r\n", "\n").replace("\r", "\n")
    pages = raw.split("\f")
    if not pages[-1].strip():
        pages.pop()
    if not pages or any(not page.strip() for page in pages):
        raise ValueError(f"{source.name}: hay páginas sin texto; revisar el PDF/OCR.")
    digest = hashlib.sha256(source.read_bytes()).hexdigest()
    header = (
        f"# {source.stem}\n\n"
        f"- Original: `../{source.name}`\n"
        f"- SHA-256 del original: `{digest}`\n"
        f"- Páginas PDF: {len(pages)}. La numeración es física, desde 1; "
        "puede diferir de la impresa o del sumario.\n"
        "- Extracción automática con `pdftotext -layout -enc UTF-8`. "
        "Los bloques conservan la disposición del texto; no reproducen imágenes.\n"
        "- Ante dudas sobre tablas, fórmulas, diagramas o formato, consultar el PDF. "
        "Este archivo es una copia de consulta y se regenera; editar el original "
        "o las notas, no esta extracción.\n\n"
    )
    body = "\n".join(
        f"## Página PDF {number}\n\n```text\n{page.strip(chr(10)).rstrip()}\n```\n"
        for number, page in enumerate(pages, 1)
    )
    metadata = {
        "original": source.name,
        "consulta": source.stem + ".md",
        "sha256": digest,
        "bytes_original": source.stat().st_size,
        "paginas_pdf": len(pages),
        "caracteres_texto": sum(len(page) for page in pages),
    }
    return header + body, metadata


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--check", action="store_true", help="Comprueba sin escribir.")
    args = parser.parse_args()
    if not shutil.which("pdftotext"):
        parser.error("Falta pdftotext en PATH (incluido en MiKTeX o Poppler).")
    sources = [ROOT / name for name in SOURCES]
    missing = [path.name for path in sources if not path.is_file()]
    if missing:
        parser.error("Faltan PDF originales: " + ", ".join(missing))
    outputs = {}
    metadata = []
    for source in sources:
        content, entry = extract(source)
        outputs[OUTPUT / entry["consulta"]] = content
        metadata.append(entry)
    outputs[OUTPUT / "manifest.json"] = json.dumps(
        {"metodo": "pdftotext -layout -enc UTF-8", "documentos": metadata},
        ensure_ascii=False, indent=2,
    ) + "\n"
    if args.check:
        stale = [str(path.relative_to(ROOT)) for path, content in outputs.items()
                 if not path.exists() or path.read_text(encoding="utf-8") != content]
        if stale:
            parser.exit(1, "Copias faltantes o desactualizadas:\n" + "\n".join(stale) + "\n")
        print(f"OK: {len(sources)} documentos y manifest coinciden con los originales.")
    else:
        OUTPUT.mkdir(parents=True, exist_ok=True)
        for path, content in outputs.items():
            path.write_text(content, encoding="utf-8", newline="\n")
        for entry in metadata:
            print(f"{entry['paginas_pdf']:2} páginas: {entry['consulta']}")


if __name__ == "__main__":
    main()

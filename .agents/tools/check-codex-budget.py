"""Consulta local y de solo lectura de la cuota informada para esta sesión.

No consulta credenciales ni estima cuota a partir de tokens. Salida 10: cerrar
incrementos (<=10% restante); salida 20: detener desarrollo (<=5%).
"""
import json
import os
from datetime import datetime, timezone
from pathlib import Path


def main():
    thread = os.environ.get("CODEX_THREAD_ID")
    if not thread:
        print(json.dumps({"available": False, "reason": "CODEX_THREAD_ID ausente"}))
        return 2
    root = Path(os.environ.get("CODEX_HOME", str(Path.home() / ".codex"))) / "sessions"
    latest = None
    for path in root.rglob(f"*{thread}.jsonl"):
        with path.open(encoding="utf-8") as stream:
            for line in stream:
                if '"token_count"' not in line:
                    continue
                try:
                    event = json.loads(line)
                except json.JSONDecodeError:
                    continue
                payload = event.get("payload", {})
                limits = payload.get("rate_limits")
                if event.get("type") != "event_msg" or payload.get("type") != "token_count" or not limits:
                    continue
                primary = limits.get("primary") or {}
                if primary.get("window_minutes") != 300 or "used_percent" not in primary:
                    continue
                observed = event.get("timestamp", "")
                if latest is None or observed > latest[0]:
                    latest = (observed, primary)
    if latest is None:
        print(json.dumps({"available": False, "reason": "Sin lectura de ventana de 300 minutos"}))
        return 2
    observed, primary = latest
    age = (datetime.now(timezone.utc) - datetime.fromisoformat(observed.replace("Z", "+00:00"))).total_seconds()
    remaining = max(0.0, 100.0 - primary["used_percent"])
    action = "stop" if remaining <= 5 else "close" if remaining <= 10 else "work"
    print(json.dumps({"available": True, "observed_at": observed, "age_seconds": round(age),
                      "used_percent": primary["used_percent"], "remaining_percent": remaining,
                      "resets_at": primary.get("resets_at"), "action": action,
                      "note": "Lectura reportada, no contador continuo; puede haber demora."}))
    return 20 if action == "stop" else 10 if action == "close" else 0


if __name__ == "__main__":
    raise SystemExit(main())

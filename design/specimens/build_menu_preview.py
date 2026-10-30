"""Produce un documento estático de diseño; no pertenece a la app Android."""

import html
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parent.parent
TOKENS = json.loads((ROOT / 'design-tokens.json').read_text(encoding='utf-8'))

# Los roles de los estados aprobados en Q10 ya están en el JSON canónico.
colors = TOKENS['color']
variables = '\n'.join(f'--{name}: {value};' for name, value in colors.items())
variables += '\n' + '\n'.join(f'--space-{name}: {value}px;' for name, value in TOKENS['spacing'].items())
variables += '\n' + '\n'.join(f'--radius-{name}: {value}px;' for name, value in TOKENS['radius'].items())

# Dibujos editables de estudio. Son motivos de categoría, nunca notación musical.
leaf = '<path d="M10 31Q7 8 28 4Q32 24 10 31Z" fill="var(--sage)"/><path d="M10 31 25 9" fill="none"/>'
icons = {
    'simon': '<path d="M7 25Q32 18 57 25V52H7Z" fill="var(--surface)"/><path d="M7 32H57M17 32V52M27 32V52M37 32V52M47 32V52"/><path d="M14 32H20V43H14ZM24 32H30V43H24ZM44 32H50V43H44Z" fill="var(--text)"/><path d="M8 23Q5 12 17 8Q23 19 8 23ZM48 22Q48 8 61 5Q65 19 48 22Z" fill="var(--sage)"/>',
    'instruments': '<path d="M12 49V24L32 9 52 24V49Z" fill="var(--surface)"/><path d="M12 27H52M32 9V49"/><path d="M24 20Q21 23 25 27L21 40Q19 46 25 46Q32 46 30 40L27 27Q31 23 28 20Z" fill="var(--success)"/><path d="M39 31V45Q47 48 49 43L43 41V31Z" fill="var(--sage)"/>',
    'voice': '<path d="M8 21Q17 10 23 21T38 21T54 21M8 32Q17 21 23 32T38 32T54 32M8 43Q17 32 23 43T38 43T54 43" fill="none"/><path d="M51 48Q49 39 59 36Q63 44 51 48Z" fill="var(--sage)"/>',
    'tuner': '<path d="M28 10H36V27Q47 31 47 41Q47 55 32 55Q17 55 17 41Q17 31 28 27Z" fill="var(--success)"/><circle cx="32" cy="39" r="5" fill="var(--text)"/><path d="M30 10V47M34 10V47M26 7H38"/><path d="M10 25Q5 12 17 8Q22 20 10 25Z" fill="var(--sage)"/>',
    'rhythm': '<ellipse cx="30" cy="25" rx="20" ry="7" fill="var(--surface)"/><path d="M10 25V45Q30 57 50 45V25M14 29 23 47 32 30 41 48 47 29M17 10 31 21M46 9 36 22" fill="none"/><path d="M51 51Q48 40 60 36Q65 47 51 51Z" fill="var(--sage)"/>',
    'defender': '<path d="M10 43Q10 29 24 25Q16 14 30 11Q39 5 44 18Q57 17 58 30Q62 43 44 45Z" fill="var(--sage)"/><path d="M34 24Q31 41 26 53H41Q34 39 34 24Z" fill="var(--success)"/><path d="M14 11Q7 4 4 10Q1 17 13 20Q10 28 17 26Q25 23 17 17Q31 12 24 7Q20 4 14 11Z" fill="var(--surface)"/>',
}
games = [
    ('simon', 'Simón dice musical', 'Escuchá y repetí una secuencia en el piano.', ''),
    ('instruments', '¿Qué instrumento es?', 'Reconocé instrumentos por su sonido.', ''),
    ('voice', 'Afiná tu voz', 'Explorá las notas que cantás.', 'Prueba técnica'),
    ('tuner', 'Afinador', 'Compará y ajustá la afinación de tu instrumento.', ''),
    ('rhythm', 'Rhythm Tap', 'Leé el ritmo y marcá el pulso.', 'Práctica'),
    ('defender', 'Defender de notas', 'Leé notas y protegé el árbol guardián.', 'Práctica'),
]

def motif(kind):
    return f'<svg class="motif" viewBox="0 0 64 64" aria-hidden="true">{icons[kind]}</svg>'

cards = '\n'.join(
    f'<li class="game-card" data-control="card">{motif(kind)}'
    f'<div class="card-copy"><h3>{html.escape(title)}</h3><p>{html.escape(description)}</p>'
    + (f'<span class="status">{html.escape(status)}</span>' if status else '')
    + '</div><svg class="chevron" viewBox="0 0 16 24" aria-hidden="true"><path d="m5 6 6 6-6 6"/></svg></li>'
    for kind, title, description, status in games
)

document = '''<!doctype html>
<html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Music Garden · estudio estático del menú</title>
<style>
@font-face{font-family:Fraunces;src:url(fonts/fraunces-variable.ttf);font-weight:100 900;font-display:block}
@font-face{font-family:'Nunito Sans';src:url(fonts/nunito-sans-variable.ttf);font-weight:200 1000;font-display:block}
:root{__VARIABLES__ font-size:16px}
*{box-sizing:border-box}body{margin:0;background:var(--background);color:var(--text);font-family:'Nunito Sans',sans-serif}
main{max-width:480px;margin:auto;padding:var(--space-lg) var(--space-base) var(--space-xl)}
svg{stroke:var(--primary);stroke-width:1.6;stroke-linecap:round;stroke-linejoin:round}
header{display:grid;grid-template-columns:minmax(0,1fr) 86px;gap:var(--space-sm);align-items:center;padding:var(--space-sm) 0 var(--space-lg)}
h1{font-family:Fraunces,serif;font-size:1.875rem;line-height:1.2;font-weight:600;letter-spacing:-.025em;margin:0 0 var(--space-md);font-variation-settings:'SOFT' 30,'WONK' 0}
header p{font-size:1rem;line-height:1.5;margin:0;color:var(--textSecondary)}
.branch{width:86px;height:124px;align-self:center}
h2{font-size:1.125rem;line-height:1.4;font-weight:700;margin:0 0 var(--space-md)}
.games{display:grid;gap:var(--space-md);list-style:none;margin:0;padding:0}
.game-card{display:grid;grid-template-columns:60px minmax(0,1fr) 12px;gap:var(--space-md);align-items:center;background:var(--surface);border-radius:var(--radius-large);padding:var(--space-base);min-height:104px}
.motif{width:60px;height:64px;stroke-width:1.4}
.card-copy{min-width:0}.card-copy h3{font-size:1.0625rem;line-height:1.35;font-weight:700;margin:0 0 var(--space-xs);overflow-wrap:break-word}
.card-copy p{font-size:.9375rem;line-height:1.4;color:var(--textSecondary);margin:0;overflow-wrap:break-word}
.status{display:block;font-size:.875rem;line-height:1.4;color:var(--primary);margin-top:var(--space-sm);font-weight:600}
.chevron{width:12px;height:20px;fill:none;stroke-width:2}
.secondary{margin-top:var(--space-lg);display:grid;gap:var(--space-sm)}
.secondary .text-control{min-height:48px;display:flex;align-items:center;justify-content:center;font:700 1rem/1.4 'Nunito Sans';padding:var(--space-md) var(--space-base);border:1px solid var(--primary);border-radius:var(--radius-large);color:var(--primary);text-align:center}
.secondary .quiet{border:0;font-weight:600}
.history-note{font-size:.875rem;line-height:1.5;color:var(--textSecondary);margin:var(--space-sm) var(--space-base) 0;text-align:center}
.specimens{display:none}.states-mode main{max-width:480px}.states-mode .menu{display:none}.states-mode .specimens{display:block}
.specimens h1{font-size:1.5rem}.specimens section{margin:var(--space-lg) 0}.specimens p{font-size:1rem;line-height:1.5}
.control{font:700 1rem/1.4 'Nunito Sans';display:flex;align-items:center;justify-content:center;min-height:52px;margin:var(--space-md) 0;padding:var(--space-base) var(--space-lg);border-radius:var(--radius-large);background:var(--primary);color:var(--surface)}
.control.pressed{background:var(--primaryPressed)}.control.selected{background:var(--surfaceSelected);color:var(--primary);border:2px solid var(--primary)}
.control.disabled{background:var(--disabledSurface);color:var(--disabledText)}.control.focus{outline:2px solid var(--focus);outline-offset:2px}
.message{padding:var(--space-base);border-radius:var(--radius-medium);background:var(--success)}
.message.error{background:var(--errorSurface);color:var(--errorText)}
.large-text header{grid-template-columns:1fr}.large-text .branch{display:none}.large-text .game-card{grid-template-columns:minmax(0,1fr) 12px}.large-text .motif{display:none}
@media(min-width:600px){main{padding:var(--space-xl) var(--space-lg)}}
</style></head><body><main>
<div class="menu"><header><div><h1>Entrená tu oído</h1><p>Elegí una práctica y explorá la música.</p></div>
<svg class="branch" viewBox="0 0 86 124" aria-hidden="true"><path d="M17 115Q44 72 61 12" fill="none"/>
<path d="M28 94Q3 91 6 65Q29 66 28 94Z" fill="var(--sage)"/><path d="M38 76Q37 48 13 43Q8 67 38 76Z" fill="var(--moss)"/>
<path d="M46 55Q18 47 29 22Q51 30 46 55Z" fill="var(--sage)"/><path d="M55 33Q41 15 60 3Q74 20 55 33Z" fill="var(--moss)"/>
<path d="M48 53Q51 29 79 35Q74 58 48 53Z" fill="var(--sage)"/><path d="M35 82Q49 61 77 68Q65 92 35 82Z" fill="var(--moss)"/>
<path d="M27 95 12 73M38 76 19 51M46 55 32 29M55 33 60 10M48 53 72 39M35 82 70 72" fill="none"/></svg></header>
<h2>Tu próxima práctica</h2><ul class="games">__CARDS__</ul>
<div class="secondary"><div class="text-control" data-control="secondary">Ver todos los resultados</div><div class="text-control quiet" data-control="secondary">Datos y demostración</div></div>
<p class="history-note">Tus partidas aparecerán en los resultados.</p></div>
<div class="specimens"><h1>Controles y estados</h1><p>Estudio visual con las fuentes propuestas. Cada etiqueta identifica el estado ilustrado.</p>
<section><h2>Acción principal</h2><div class="control">Normal · Comenzar</div><div class="control pressed">Presionado · Comenzar</div><div class="control focus">Con foco · Comenzar</div><div class="control disabled">Deshabilitado · Comenzar</div></section>
<section><h2>Selección</h2><div class="control selected">Seleccionado · Escala mayor</div></section>
<section><h2>Resultado</h2><p class="message">Correcto. Completaste la secuencia.</p><p class="message error">No coincide. Probá otra nota.</p></section></div>
</main><script>
const params=new URLSearchParams(location.search);if(params.has('states'))document.body.classList.add('states-mode');
if(params.has('large')){document.documentElement.style.fontSize='32px';document.body.classList.add('large-text')}
document.fonts.ready.then(()=>{document.documentElement.dataset.fonts='ready'})
</script></body></html>'''

(HERE / 'menu-v1.html').write_text(document.replace('__VARIABLES__', variables).replace('__CARDS__', cards), encoding='utf-8')
print('Generado design/specimens/menu-v1.html desde tokens canónicos y roles propuestos.')

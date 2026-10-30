"""Estudios estáticos Music Garden. No modifica ni ejecuta la aplicación Android."""

import html
import json
from pathlib import Path

HERE = Path(__file__).resolve().parent
TOKENS = json.loads((HERE.parent.parent / 'design-tokens.json').read_text(encoding='utf-8'))
variables = '\n'.join(f'--{k}:{v};' for k,v in TOKENS['color'].items())
variables += '\n'.join(f'--s-{k}:{v}px;' for k,v in TOKENS['spacing'].items())
variables += '\n'.join(f'--r-{k}:{v}px;' for k,v in TOKENS['radius'].items())

LEAF = '<path d="M4 21Q1 4 23 2Q25 19 4 21Z"/><path d="M4 21 19 6" fill="none"/>'
MOTH = '''<g stroke="var(--primary)" stroke-width="1.4" stroke-linejoin="round">
<path d="M31 22Q15 0 2 6Q-2 21 25 28Q5 29 15 43Q25 46 31 27Z" fill="var(--sage)"/>
<path d="M33 22Q49 0 62 6Q66 21 39 28Q59 29 49 43Q39 46 33 27Z" fill="var(--sage)"/>
<path d="M31 20 12 12M33 20 53 12M29 29 20 36M35 29 44 36" fill="none"/>
<path d="M30 20Q28 37 32 43Q36 37 34 20Z" fill="var(--primary)"/>
<path d="M31 20 26 13M33 20 38 13" fill="none"/></g>'''
TREE = '''<g stroke="var(--primary)" stroke-width="1.5" stroke-linejoin="round">
<path d="M12 72Q0 53 22 44Q12 26 39 23Q39 8 61 14Q70 0 90 14Q110 7 120 27Q148 23 146 45Q170 48 157 69Q167 91 136 91Q123 105 103 91Q84 107 66 93Q43 104 30 89Q8 96 12 72Z" fill="var(--sage)"/>
<path d="M19 58Q8 40 36 36Q39 18 63 26Q66 51 42 62Z" fill="var(--moss)"/>
<path d="M101 22Q128 15 135 38Q157 35 153 58L128 72Q102 62 101 22Z" fill="var(--moss)"/>
<path d="M65 136Q84 116 83 88L66 69 58 47 83 72 91 49 96 29 101 58 94 88Q96 117 113 137Z" fill="var(--success)"/>
<path d="M82 130Q91 112 88 92M91 82 73 58M92 88 118 63" fill="none"/>
<path d="M40 139Q72 132 128 140" fill="none"/></g>'''

def svg(inner, box, cls, **attributes):
    attrs=' '.join(f'{k}="{html.escape(str(v),quote=True)}"' for k,v in attributes.items())
    return f'<svg viewBox="{box}" class="{cls}" {attrs}>{inner}</svg>'

def staff(name, octave, accidental='natural', clef='treble'):
    """Traslación SVG de NoteStaff: mismas coordenadas y reglas de staffStep."""
    names=['Do','Re','Mi','Fa','Sol','La','Si']
    step=octave*7+names.index(name)-(30 if clef=='treble' else 18)
    y=76-step*6
    shapes=['<g transform="translate(0 8)" fill="var(--text)" stroke="var(--text)">']
    shapes += [f'<path d="M4 {28+line*12}H96" fill="none" stroke-width=".9"/>' for line in range(5)]
    if clef=='treble':
        shapes += ['<path d="M19 92C32 94 28 79 25 69L16 24C10 -1 39 0 28 21C18 39 1 40 5 62C10 80 36 77 35 58C33 44 12 44 11 60C10 72 23 75 29 65" fill="none" stroke-width="2.3"/>']
    else:
        shapes += ['<path d="M8 43C5 20 36 16 31 40C29 53 17 61 9 64" fill="none" stroke-width="3"/><circle cx="9" cy="40" r="2.6"/><circle cx="39" cy="34" r="1.8"/><circle cx="39" cy="46" r="1.8"/>']
    ledgers=range(-2,step-1,-2) if step<0 else range(10,step+1,2) if step>8 else []
    shapes += [f'<path d="M61 {76-s*6}H85" fill="none" stroke-width=".9"/>' for s in ledgers]
    if accidental=='sharp':
        shapes += [f'<path d="M50 {y-10}V{y+9}M55 {y-10}V{y+9}" fill="none" stroke-width="1.3"/>',f'<path d="M46 {y-1} 59 {y-5}M46 {y+5} 59 {y+1}" fill="none" stroke-width="2"/>']
    elif accidental=='flat':
        shapes += [f'<path d="M49 {y-14}V{y+6}C64 {y-4} 57 {y-10} 49 {y-2}" fill="none" stroke-width="1.6"/>']
    shapes += [f'<ellipse cx="72" cy="{y}" rx="6" ry="4.4"/>',f'<path d="M{66 if step>=4 else 78} {y}v{29 if step>=4 else -29}" fill="none" stroke-width="1.4"/>','</g>']
    data=json.dumps({'name':name,'octave':octave,'accidental':accidental,'clef':clef,'staffStep':step},ensure_ascii=False)
    return svg(''.join(shapes),'0 0 100 120','staff',preserveAspectRatio='xMidYMid meet',**{'data-note':data,'aria-hidden':'true'})

notes=[('La',3,'flat','treble'),('Mi',4,'flat','bass'),('Do',6,'sharp','treble'),('Do',2,'sharp','bass')]
enemies=''.join(f'<div class="enemy enemy-{i}" data-target="true">{staff(*note)}{svg(MOTH,"0 0 64 46","moth",**{"aria-hidden":"true"})}</div>' for i,note in enumerate(notes))
leaf=svg(LEAF,'0 0 28 26','life',**{'aria-hidden':'true'})
life_row=''.join(f'<span class="leaf-{i}">{leaf}</span>' for i in range(3))
choices=''.join(f'<div class="choice {"chosen" if name=="Re" else ""}" data-control="note">{name}</div>' for name in ['Do','Re','Mi','Fa','Sol','La','Si'])

defender=f'''<div class="game defender"><header><span class="back" data-control="menu">Menú</span><h1>Defender de notas</h1><span class="back" data-control="pause">Pausa</span></header>
<div class="hud"><span>Sol y fa · práctica</span><span class="points">0 pts</span><span class="lives">{life_row}<span class="lives-label">3 vidas</span></span></div>
<div class="arena"><svg class="border-plant left" viewBox="0 0 28 120" aria-hidden="true"><path d="M4 115Q16 65 16 8" fill="none" stroke="var(--sage)"/><path d="M12 88Q0 78 3 65Q24 71 12 88ZM16 53Q4 43 6 30Q25 38 16 53Z" fill="var(--sage)" stroke="none"/></svg>
{enemies}<div class="guardian">{svg(TREE,'0 0 170 148','tree',**{'aria-hidden':'true'})}</div>
<svg class="sound-wave" viewBox="0 0 200 200" aria-hidden="true"><circle cx="100" cy="100" r="66" fill="none" stroke="var(--audio)" stroke-width="3"/>{''.join(f'<circle cx="{x}" cy="{y}" r="2.8" fill="var(--success)"/>' for x,y in [(42,53),(74,25),(132,40),(170,74),(150,150),(95,175),(41,133),(27,92)])}</svg>
<div class="solution">La bemol · octava 3</div><div class="pause-card"><h2>En pausa</h2><p>Tiempo y audio detenidos.</p><div class="action">Reanudar</div></div></div>
<section class="answer"><p class="feedback">Elegí una nota y tocá.</p><div class="note-grid">{choices}</div><div class="accidentals"><div class="choice" data-control="accidental">Bemol</div><div class="choice chosen" data-control="accidental">Natural</div><div class="choice" data-control="accidental">Sostenido</div></div></section>
<div class="action" data-control="confirm">Tocar</div></div>'''

def keyboard():
    # Misma construcción cromática y proporciones de PianoKeyboard. Escala de Do mayor.
    whites=[m for m in range(60,72) if m%12 not in {1,3,6,8,10}]
    black=[m for m in range(60,72) if m%12 in {1,3,6,8,10}]
    white_width=100/len(whites)
    names={0:'Do',1:'Do sostenido',2:'Re',3:'Re sostenido',4:'Mi',5:'Fa',6:'Fa sostenido',7:'Sol',8:'Sol sostenido',9:'La',10:'La sostenido',11:'Si'}
    parts=[]
    for i,midi in enumerate(whites):
        label=f'{names[midi%12]}{midi//12-1}'
        parts.append(f'<div class="key white {"sounding" if midi==64 else ""}" style="left:{i*white_width}%;width:{white_width}%" data-midi="{midi}"><span>{label}</span><i class="sound-mark"></i></div>')
    for midi in black:
        before=sum(m<midi for m in whites)
        parts.append(f'<div class="key black" style="left:{before*white_width-white_width*.31}%;width:{white_width*.62}%" data-midi="{midi}" aria-label="{names[midi%12]} {midi//12-1}, fuera de la escala"></div>')
    return '<div class="keyboard">'+''.join(parts)+'</div>'

simon=f'''<div class="game simon"><header><span class="back" data-control="menu">Menú</span><h1>Simón dice musical</h1><span class="round">Ronda 4</span></header>
<div class="simon-details"><span>Diatónica mayor · tónica Do4</span><span>3 secuencias completadas</span></div>
<div class="phase"><strong class="phase-title">Escuchá la secuencia</strong><span class="phase-progress">4 notas</span></div>
<div class="piano-frame"><svg class="piano-leaf" viewBox="0 0 28 26" aria-hidden="true">{LEAF}</svg>{keyboard()}</div>
<p class="piano-help">Las teclas atenuadas no pertenecen a esta escala.</p></div>'''

styles='''
@font-face{font-family:Fraunces;src:url(fonts/fraunces-variable.ttf);font-weight:100 900;font-display:block}
@font-face{font-family:'Nunito Sans';src:url(fonts/nunito-sans-variable.ttf);font-weight:200 1000;font-display:block}
:root{__VARIABLES__}*{box-sizing:border-box}body{margin:0;background:var(--background);color:var(--text);font:16px/1.45 'Nunito Sans',sans-serif}
.game{padding:12px 16px 16px;display:flex;flex-direction:column;gap:8px;height:100svh;min-height:770px;max-width:480px;margin:auto}
header{display:flex;align-items:center;gap:12px;min-height:48px}h1{font:600 20px/1.25 Fraunces,serif;margin:0;flex:1}h2{font:600 24px/1.3 Fraunces;margin:0 0 12px}p{margin:0}
.back{display:flex;align-items:center;justify-content:center;min-height:48px;min-width:48px;font-weight:600;color:var(--primary)}
.hud{display:flex;justify-content:space-between;align-items:center;gap:8px;min-height:36px;font-size:14px;color:var(--textSecondary)}
.lives{display:flex;gap:3px;align-items:center;white-space:nowrap;color:var(--text)}.life{width:20px;height:20px;fill:var(--moss);stroke:var(--primary);stroke-width:1.3}.lives-label{margin-left:4px}
.arena{position:relative;flex:1;min-height:318px;isolation:isolate}.border-plant{position:absolute;left:0;top:26%;width:20px;height:120px;opacity:.55;z-index:-1}
.enemy{position:absolute;width:94px;display:flex;flex-direction:column;align-items:center;gap:2px}.enemy-0{left:4px;top:4px}.enemy-1{right:4px;top:28px}.enemy-2{left:4px;top:154px}.enemy-3{right:4px;top:178px}
.staff{width:94px;height:112.8px;border-radius:12px;background:var(--surface);flex:none}.moth{width:37px;height:27px}.guardian{position:absolute;bottom:0;left:50%;transform:translateX(-50%);width:108px}.tree{width:100%;display:block}
.answer{padding:12px;border-radius:var(--r-large);background:var(--surface);display:grid;gap:8px}.feedback{font-size:14px;line-height:20px;color:var(--textSecondary);min-height:20px}
.note-grid{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:8px}.choice{display:flex;align-items:center;justify-content:center;min-height:48px;border:1px solid var(--border);border-radius:var(--r-medium);font-weight:700;color:var(--primary)}
.chosen{border:2px solid var(--primary);background:var(--surfaceSelected)}.chosen:after{content:'';width:5px;height:5px;border-radius:50%;background:var(--primary);margin-left:6px}
.accidentals{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:8px}.accidentals .choice{font-size:14px}
.action{display:flex;align-items:center;justify-content:center;min-height:52px;border-radius:var(--r-large);background:var(--primary);color:var(--onPrimary);font-weight:700;flex:none}
.sound-wave{display:none;position:absolute;width:150px;height:150px;left:50%;bottom:38px;transform:translateX(-50%);z-index:-1}.solution{display:none;position:absolute;left:0;top:100px;max-width:130px;padding:8px;border-radius:8px;background:var(--errorSurface);color:var(--errorText);font-size:14px}
.pause-card{display:none;position:absolute;inset:28px 12px auto;background:var(--surface);padding:24px;border-radius:16px;text-align:center}.pause-card .action{margin-top:20px}
.state-hit .enemy-0,.state-life .enemy-0{visibility:hidden}.state-hit .sound-wave{display:block}.state-hit .feedback{color:var(--text);background:var(--success);border-radius:8px;padding:0 8px}.state-hit .defender>.action{background:var(--disabledSurface);color:var(--disabledText)}
.state-error .feedback,.state-life .feedback{color:var(--errorText)}.state-error .answer{outline:2px solid var(--errorText);outline-offset:-2px}.state-life .leaf-2 .life{fill:none;stroke:var(--border)}.state-life .solution{display:block}
.state-pause .enemy,.state-pause .guardian,.state-pause .border-plant{visibility:hidden}.state-pause .pause-card{display:block}.state-pause .answer,.state-pause .defender>.action{visibility:hidden}
.game.simon{max-width:1200px;min-height:340px;height:100svh;padding:8px 16px;gap:6px}.simon h1{font-size:22px}.round{font-weight:700}.simon-details{display:flex;justify-content:space-between;gap:12px;font-size:14px;color:var(--textSecondary)}
.phase{display:flex;justify-content:space-between;gap:12px;align-items:center;background:var(--surface);padding:10px 16px;border-radius:12px;min-height:48px}.phase-title{color:var(--focus)}.phase-progress{font-weight:600;color:var(--textSecondary)}
.piano-frame{padding:12px 8px 0;background:var(--sage);border-radius:16px 16px 8px 8px;flex:1;position:relative;min-height:160px}.piano-leaf{position:absolute;right:4px;top:-10px;width:26px;height:26px;fill:var(--moss);stroke:var(--primary);stroke-width:1.2}
.keyboard{position:relative;height:100%;min-height:148px}.key{position:absolute;top:0;border:1px solid var(--border);border-radius:0 0 8px 8px;display:flex;align-items:flex-end;justify-content:center;padding-bottom:12px}
.white{height:100%;background:var(--surface)}.black{height:62%;background:var(--text);z-index:1}.white span{font-size:14px;font-weight:700}.sounding{background:var(--success);border:2px solid var(--primary)}
.sound-mark{display:none}.sounding .sound-mark{position:absolute;bottom:40px;width:9px;height:9px;background:var(--primary);border-radius:50%;display:block}
.piano-help{font-size:14px;text-align:center;color:var(--textSecondary)}.response .sounding{background:var(--surface);border:1px solid var(--border)}.response .sound-mark{display:none}.response .phase-title{color:var(--primary)}
@media(max-width:380px){.guardian{width:96px}.hud{font-size:13px}.life{width:18px}}
'''

script='''const p=new URLSearchParams(location.search);const state=p.get('state')||'ready';document.body.classList.add('state-'+state);
const feedback=document.querySelector('.feedback');if(feedback){if(state==='hit'){feedback.textContent='Correcto. El jardín resuena.';document.querySelector('.points').textContent='100 pts';document.querySelectorAll('.note-grid .chosen').forEach(e=>e.classList.remove('chosen'))}if(state==='error')feedback.textContent='No coincide. Probá otra nota.';if(state==='life'){feedback.textContent='Una criatura alcanzó el árbol.';document.querySelector('.lives-label').textContent='2 vidas'}}
if(state==='response'){document.body.classList.add('response');document.querySelector('.phase-title').textContent='Ahora repetí la secuencia';document.querySelector('.phase-progress').textContent='0 de 4 notas'}
document.fonts.ready.then(()=>{document.documentElement.dataset.fonts='ready'});'''

for name,body in [('defender-v1',defender),('simon-v1',simon)]:
    content='<!doctype html><html lang="es"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Music Garden · estudio estático</title><style>'+styles.replace('__VARIABLES__',variables)+'</style><body>'+body+'<script>'+script+'</script></body></html>'
    (HERE/(name+'.html')).write_text(content,encoding='utf-8')

# Registro de estímulos de la maqueta; no son datos de un estudiante.
(HERE/'games-v1-data.json').write_text(json.dumps({'kind':'design_specimen','defenderNotes':[{'name':n,'octave':o,'accidental':a,'clef':c} for n,o,a,c in notes],'simon':{'tonicMidi':60,'scale':'Diatónica mayor','rangeMidi':[60,71],'highlightedMidi':64,'round':4,'completedSequences':3},'renderSource':'NoteStaff/PianoKeyboard, revisión 2026-10-26; traslación propia para estudio SVG, sin ejecución Android'},ensure_ascii=False,indent=2),encoding='utf-8')
print('Generados defender-v1.html, simon-v1.html y datos de estudio.')

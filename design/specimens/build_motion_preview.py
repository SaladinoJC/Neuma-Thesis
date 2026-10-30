"""Muestra aislada de movimiento, sin audio, sesión ni lógica de juego."""
from pathlib import Path
from build_games_preview import TREE, MOTH, TOKENS

here=Path(__file__).resolve().parent
colors=''.join(f'--{k}:{v};' for k,v in TOKENS['color'].items())
motion=TOKENS['motion']
particles=''.join(f'<i style="--x:{x}px;--y:{y}px"></i>' for x,y in [(-36,-30),(0,-45),(32,-28),(44,0),(30,33),(0,40),(-34,27),(-44,0)])
page='''<!doctype html><html lang="es"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Music Garden · estudio de movimiento</title>
<style>
@font-face{font-family:Fraunces;src:url(fonts/fraunces-variable.ttf);font-weight:100 900;font-display:block}
@font-face{font-family:'Nunito Sans';src:url(fonts/nunito-sans-variable.ttf);font-weight:200 1000;font-display:block}
:root{__COLORS__ --duration-press:__PRESS__ms;--duration-success:__SUCCESS__ms;--ease:__EASE__}
*{box-sizing:border-box}body{margin:0;font:16px/1.5 'Nunito Sans';background:var(--background);color:var(--text)}main{max-width:480px;margin:auto;padding:24px 16px}
h1{font:600 24px/1.3 Fraunces;margin:0 0 12px}p{margin:0 0 16px}.scene{position:relative;height:240px;background:var(--surface);border-radius:16px;overflow:hidden;margin:24px 0 12px}
.tree{position:absolute;width:140px;height:122px;left:20px;bottom:16px}.moth{position:absolute;width:60px;height:44px;left:65%;top:50px}.ring{position:absolute;left:calc(65% - 36px);top:14px;width:132px;height:132px;border:2px solid var(--audio);border-radius:50%;opacity:0}
.pollen{position:absolute;left:calc(65% + 30px);top:72px}.pollen i{position:absolute;width:5px;height:5px;background:var(--success);border-radius:50%;opacity:0}
.feedback{min-height:52px;padding:12px 16px;background:var(--surface);border-radius:12px;color:var(--textSecondary);margin-bottom:16px}.running .feedback{background:var(--success);color:var(--text)}
button{border:0;border-radius:16px;min-height:52px;width:100%;padding:12px 24px;background:var(--primary);color:var(--onPrimary);font:700 16px/1.4 'Nunito Sans';transition:transform var(--duration-press) var(--ease);cursor:pointer}button:active{transform:scale(.98)}button:focus-visible{outline:2px solid var(--focus);outline-offset:2px}
label{display:flex;gap:12px;align-items:center;min-height:48px;margin:12px 0;font-size:16px}input{width:20px;height:20px;accent-color:var(--primary)}.note{font-size:14px;color:var(--textSecondary)}
.running .moth{animation:resolve var(--duration-success) var(--ease) forwards}.running .ring{animation:resonate var(--duration-success) ease-out forwards}.running .pollen i{animation:pollen var(--duration-success) ease-out forwards}
@keyframes resolve{0%{opacity:1;transform:scale(1)}100%{opacity:0;transform:scale(.96)}}
@keyframes resonate{0%{opacity:0;transform:scale(.35)}20%{opacity:1}100%{opacity:0;transform:scale(1)}}
@keyframes pollen{0%{opacity:0;transform:translate(0,0)}20%{opacity:1}100%{opacity:0;transform:translate(var(--x),var(--y))}}
.reduced .moth,.reduced .ring,.reduced .pollen i{animation:none!important}.reduced.running .moth{visibility:hidden}.reduced button{transition:none}
@media(prefers-reduced-motion:reduce){.moth,.ring,.pollen i{animation:none!important}.running .moth{visibility:hidden}button{transition:none}}
</style><body><main><h1>Movimiento de Music Garden</h1><p>Muestra del acierto: el resultado aparece al pulsar; la onda y el polen lo acompañan.</p>
<div class="scene"><svg class="tree" viewBox="0 0 170 148" aria-hidden="true">__TREE__</svg><svg class="moth" viewBox="0 0 64 46" aria-hidden="true">__MOTH__</svg><div class="ring"></div><div class="pollen">__PARTICLES__</div></div>
<div class="feedback" aria-live="polite">Listo para ver el efecto.</div><button type="button" id="play" data-control="preview">Ver acierto</button><label><input type="checkbox" id="reduce">Reducir movimiento</label><p class="note">Ilustración de movimiento de 450 ms. No reproduce audio ni evalúa una respuesta musical. Podés repetirla mientras está en curso.</p></main>
<script>
const p=new URLSearchParams(location.search),reduce=document.querySelector('#reduce');reduce.checked=p.has('reduce');document.body.classList.toggle('reduced',reduce.checked);reduce.onchange=()=>document.body.classList.toggle('reduced',reduce.checked);
function run(){document.body.classList.remove('running');document.querySelector('.scene').getBoundingClientRect();document.querySelector('.feedback').textContent='Correcto. El jardín resuena.';document.body.classList.add('running')}
document.querySelector('#play').onclick=run;
document.fonts.ready.then(()=>{if(p.has('frame')){run();requestAnimationFrame(()=>{document.getAnimations().forEach(a=>{a.pause();a.currentTime=Number(p.get('frame'))});document.documentElement.dataset.fonts='ready'})}else document.documentElement.dataset.fonts='ready'});
</script></body></html>'''
for key,value in {'__COLORS__':colors,'__PRESS__':str(motion['pressMs']),'__SUCCESS__':str(motion['successMs']),'__EASE__':motion['easing'],'__TREE__':TREE,'__MOTH__':MOTH,'__PARTICLES__':particles}.items():
    page=page.replace(key,value)
(here/'motion-v1.html').write_text(page,encoding='utf-8')
print('Generada motion-v1.html, muestra aislada de movimiento.')

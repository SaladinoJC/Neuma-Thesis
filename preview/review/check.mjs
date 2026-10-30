// Verificación exploratoria del prototipo; no forma parte de la aplicación.
// Usa el inspector de la skill local, sin paquetes npm.
import {Browser} from 'file:///C:/Users/Juan/.agents/skills/design-taste-frontend/scripts/lib/cdp.mjs';
import {mkdirSync,writeFileSync} from 'node:fs';
import {resolve} from 'node:path';
import {pathToFileURL} from 'node:url';
const out=resolve('preview/review'),url=pathToFileURL(resolve('preview/neuma-concept.html')).href;
mkdirSync(out,{recursive:true});
const b=await Browser.launch(),log={url,at:new Date().toISOString(),layouts:[],actions:[],errors:[]};
const wait=ms=>new Promise(r=>setTimeout(r,ms));
const check=(name,value)=>{log.actions.push({name,passed:!!value});console.log(value?'OK':'FAIL',name);if(!value)log.errors.push(name)};
async function click(p,selector){const box=await p.eval(s=>{const r=document.querySelector(s).getBoundingClientRect();return{x:r.x+r.width/2,y:r.y+r.height/2}},selector);await p.send('Input.dispatchMouseEvent',{type:'mousePressed',...box,button:'left',clickCount:1});await p.send('Input.dispatchMouseEvent',{type:'mouseReleased',...box,button:'left',clickCount:1});await wait(40)}
async function shot(p,name){await wait(320);await p.screenshot({path:resolve(out,name+'.png'),format:'png'})}
try{
 const p=await b.newPage({width:390,height:844,mobile:true});
 for(const [w,h] of [[390,844],[360,780],[320,640]]){
  await p.setViewport({width:w,height:h,mobile:true});
  for(const variant of ['A','B','C']){
   await p.goto(url+'?variant='+variant,{settleMs:120});
   const m=await p.eval(()=>{const e=document.querySelector('#screen-home'),card=document.querySelector('.continue-card'),button=card.querySelector('.primary');return{scroll:e.scrollHeight-e.clientHeight,horizontal:document.documentElement.scrollWidth-innerWidth,mainHeight:e.clientHeight,continueVisible:button.getBoundingClientRect().bottom<=card.getBoundingClientRect().bottom+4,progressVisible:document.querySelector('.home-progress').getBoundingClientRect().bottom<=e.getBoundingClientRect().bottom}});
   log.layouts.push({w,h,variant,...m});check(`Inicio ${variant} ${w}×${h}, sin scroll horizontal`,m.horizontal<=1);if(w===390){check(`Inicio ${variant} 390×844, sin scroll vertical`,m.scroll<=1);check(`Inicio ${variant}: continuar y progreso visibles`,m.continueVisible&&m.progressVisible)}
   await shot(p,`home-${variant}-${w}`);
  }
 }
 await p.setViewport({width:390,height:844,mobile:true});await p.goto(url+'?screen=rhythm',{settleMs:180});await shot(p,'rhythm-390');
 check('SVG musicales cargados sin red',await p.eval(()=>document.querySelector('#rhythm-score svg path[transform]').getAttribute('d').length>50));
 await click(p,'#rhythm-listen');await wait(180);check('Ejemplo usa AudioContext activo, efectos silenciados',await p.eval(()=>NeumaPrototype.AudioEngine.ctx.state==='running'&&NeumaPrototype.AudioEngine.blocked&&NeumaPrototype.rhythm.phase==='listening'));await shot(p,'rhythm-listening-390');
 await click(p,'#rhythm-reset');check('Reinicio detiene patrón y limpia estado',await p.eval(()=>NeumaPrototype.rhythm.phase==='idle'&&![...NeumaPrototype.AudioEngine.voices].some(v=>v.kind==='music')));
 await click(p,'#rhythm-start');
 // Esperas con reloj real del navegador: taps que pasan por pointerdown, no por la evaluación interna.
 const target=await p.eval(()=>NeumaPrototype.rhythm.start);
 for(const n of await p.eval(()=>NeumaPrototype.notes.map(x=>({beat:x.beat})))){
  const delay=await p.eval(t=>(t-NeumaPrototype.AudioEngine.ctx.currentTime)*1000,target+n.beat*.75);
  if(delay>0)await wait(delay);await click(p,'#tap-pad');
 }
 await wait(1800);const real=await p.eval(()=>NeumaPrototype.rhythm.result);log.realTaps=real;check('Pulsaciones reales generan resultado coherente con sus timestamps',real&&real.matched>=7&&real.matched+real.omitted===9&&real.taps.length===9&&real.matched+real.extra===9&&real.mean===Math.round(real.taps.filter(t=>t.index!==null).reduce((s,t)=>s+Math.abs(t.error),0)/real.matched));await shot(p,'rhythm-result-390');
 // Una ejecución sin entradas debe producir omisiones, nunca un resultado positivo aleatorio.
 await click(p,'#rhythm-start');await wait(9500);const empty=await p.eval(()=>NeumaPrototype.rhythm.result);log.noTaps=empty;check('Sin pulsaciones: nueve omisiones y cero aciertos',empty&&empty.matched===0&&empty.omitted===9&&empty.mean===null);
 await p.eval(()=>NeumaPrototype.navigate('home'));await click(p,'#fx-toggle');check('Control global apaga FX y conserva canal musical',await p.eval(()=>!NeumaPrototype.settings.fx&&NeumaPrototype.AudioEngine.musicBus.gain.value>0));
 await p.eval(()=>NeumaPrototype.openSheet('settings'));await wait(3000);await shot(p,'sound-settings-390');await p.eval(()=>NeumaPrototype.closeSheet());
 await p.eval(()=>NeumaPrototype.navigate('defender'));await shot(p,'defender-ready-390');await click(p,'#defender-begin');await wait(500);await shot(p,'defender-running-390');
 const targetNote=await p.eval(()=>NeumaPrototype.defender.objects[0].pitch.name);
 await click(p,`[data-answer="${targetNote==='Do'?'Re':'Do'}"]`);check('Respuesta errónea no consume oportunidades',await p.eval(()=>NeumaPrototype.defender.lives===3&&NeumaPrototype.defender.wrong===1));
 await click(p,`[data-answer="${targetNote}"]`);check('Nombre correcto resuelve nota y reproduce altura',await p.eval(()=>NeumaPrototype.defender.hits===1&&NeumaPrototype.defender.lives===3));
 await wait(800);await click(p,'#defender-pause');const paused=await p.eval(()=>NeumaPrototype.defender.time);await wait(750);check('Pausa congela reloj y oculta notas',await p.eval(t=>NeumaPrototype.defender.phase==='paused'&&NeumaPrototype.defender.time===t&&!document.querySelector('#arena-cover').hidden,paused));await shot(p,'defender-paused-390');
 await click(p,'#defender-resume');check('Reanudar conserva oportunidades',await p.eval(()=>NeumaPrototype.defender.phase==='running'&&NeumaPrototype.defender.lives===3));
 await wait(12500);check('Llegada al centro consume una oportunidad',await p.eval(()=>NeumaPrototype.defender.lives<3));await shot(p,'defender-expired-390');
 await p.eval(()=>NeumaPrototype.navigate('rhythm'));check('Salir de Defender pausa su simulación',await p.eval(()=>NeumaPrototype.defender.phase==='paused'));
 await click(p,'#rhythm-start');await p.eval(()=>NeumaPrototype.navigate('home'));check('Salir de ritmo cancela cuenta y fuentes musicales',await p.eval(()=>NeumaPrototype.rhythm.phase==='idle'&&![...NeumaPrototype.AudioEngine.voices].some(v=>v.kind==='music')));
 await p.eval(()=>NeumaPrototype.openSheet('ear'));await click(p,'#ear-listen');await click(p,'[data-ear="third"]');check('Oído: ejemplo y respuesta funcional',await p.eval(()=>document.querySelector('#ear-feedback').textContent.startsWith('Sí.')));await p.eval(()=>NeumaPrototype.closeSheet());
 await p.eval(()=>NeumaPrototype.openSheet('theory'));await click(p,'#theory-listen');await click(p,'[data-theory="major"]');check('Teoría: acorde y respuesta funcional',await p.eval(()=>document.querySelector('#theory-feedback').textContent.startsWith('Exacto.')));await p.eval(()=>NeumaPrototype.closeSheet());
 await p.eval(()=>NeumaPrototype.openSheet('progress'));await shot(p,'progress-390');check('Progreso conserva resultados solo en esta sesión',await p.eval(()=>NeumaPrototype.history.length===5));await p.eval(()=>NeumaPrototype.closeSheet());
 // Muestreo de señal real en AudioContext. El navegador de revisión silencia su salida física.
 const sound=await p.eval(async()=>{const a=NeumaPrototype.AudioEngine;NeumaPrototype.settings.fx=true;a.apply();await a.ctx.resume();a.silenceEffects(false);await new Promise(r=>setTimeout(r,100));a.fx('confirm');await new Promise(r=>setTimeout(r,80));const data=new Float32Array(a.analyser.fftSize);a.analyser.getFloatTimeDomainData(data);return{rms:Math.sqrt(data.reduce((s,x)=>s+x*x,0)/data.length),peak:Math.max(...data.map(Math.abs)),rate:a.ctx.sampleRate}});log.sound=sound;check('Síntesis emite señal real sin clipping en confirmación',sound.rms>.0001&&sound.peak<1);
 for(const screen of ['rhythm','defender']){await p.setViewport({width:320,height:640,mobile:true});await p.eval(s=>NeumaPrototype.navigate(s),screen);await shot(p,screen+'-320');log.layouts.push({screen,w:320,h:640,...await p.eval(()=>({horizontal:document.documentElement.scrollWidth-innerWidth,scroll:document.querySelector('.screen:not([hidden])').scrollHeight-document.querySelector('.screen:not([hidden])').clientHeight}))})}
 // Texto duplicado: se permite desplazamiento interno, se conservan controles y navegación.
 await p.setViewport({width:390,height:844,mobile:true});await p.eval(()=>{document.querySelectorAll('button,p,h1,h2,h3,span,small').forEach(e=>{if(!e.closest('svg')){const f=parseFloat(getComputedStyle(e).fontSize);e.style.fontSize=f*2+'px'}});NeumaPrototype.navigate('home')});await shot(p,'home-text-200');check('Texto 200% sin desborde de documento',await p.eval(()=>document.documentElement.scrollWidth<=innerWidth+1));
 log.errors.push(...p.consoleErrors);await p.close();
 const reduced=await b.newPage({width:390,height:844,mobile:true,reducedMotion:true});await reduced.goto(url+'?screen=defender',{settleMs:120});await click(reduced,'#defender-begin');await wait(250);const pos=await reduced.eval(()=>document.querySelector('.note-object').style.transform);await wait(450);check('Reducción de movimiento: nota fija y tiempo activo',await reduced.eval(pos=>document.querySelector('.note-object').style.transform===pos&&NeumaPrototype.defender.time>400,pos));await shot(reduced,'defender-reduced-motion');log.errors.push(...reduced.consoleErrors);await reduced.close();
 const end=await b.newPage({width:390,height:844,mobile:true});await end.goto(url+'?screen=defender',{settleMs:120});await click(end,'#defender-begin');
 // Tres llegadas reales, sin respuestas. Se muestrean también las reservas de notación.
 let overlap=false;
 for(let i=0;i<36;i++){await wait(1000);overlap ||= await end.eval(()=>{const els=[...document.querySelectorAll('.note-object:not(.expired):not(.resolving)')];if(els.length<2)return false;const [a,b]=els.map(e=>e.getBoundingClientRect());return a.left<b.right&&b.left<a.right&&a.top<b.bottom&&b.top<a.bottom});if(await end.eval(()=>NeumaPrototype.defender.phase==='done'))break;}
 check('Convergencia sin respuestas termina tras tres llegadas',await end.eval(()=>NeumaPrototype.defender.phase==='done'&&NeumaPrototype.defender.lives===0&&NeumaPrototype.defender.missed.length===3));check('Los pentagramas móviles no se superponen en el recorrido',!overlap);await shot(end,'defender-result-390');log.errors.push(...end.consoleErrors);await end.close();
 for(const w of [768,1440]){const p=await b.newPage({width:w,height:w===1440?900:1024});await p.goto(url,{settleMs:150});await shot(p,'desktop-'+w);log.errors.push(...p.consoleErrors);await p.close()}
}catch(e){log.errors.push(e.stack);console.error(e)}finally{writeFileSync(resolve(out,'verification.json'),JSON.stringify(log,null,2));await b.close()}
console.log('Errores:',log.errors.length);process.exitCode=log.errors.length?1:0;

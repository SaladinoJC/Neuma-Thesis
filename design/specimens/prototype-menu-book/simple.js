// Una sola pantalla de referencia. Se conserva app.js como navegación del estudio previo.
const simpleVariants={A:['Cuento','Portada centrada y enmarcada: título, imagen, descripción y Jugar.'],B:['Cuaderno','Lectura alineada a la izquierda; descripción primero, piano sin marco y acción lateral.'],C:['Escenario','La ilustración abre la página; título y acción forman el cierre del relato.']};
const rects={green:[80,150,1090,290],wood:[80,540,1090,290],arrow:[510,890,235,280]};
function simpleSlice(key){const r=rects[key];return `<svg class="material" viewBox="${r.join(' ')}" preserveAspectRatio="none" aria-hidden="true"><image href="/simple-assets/controls.webp" width="1254" height="1254"/></svg>`}
function legacy(section,game=0){const url=new URL('./',location);url.searchParams.set('variant','A');url.searchParams.set('art','2');url.searchParams.set('section',section);url.searchParams.set('reduce',state.reduced?'1':'0');if(section==='Juegos')url.searchParams.set('game',game);location.assign(url)}
if(!new URLSearchParams(location.search).has('variant'))variant='C';
state.section='Juegos';state.game=0;state.large=false;
$('.forest').innerHTML='<img src="/simple-assets/garden.webp" alt="">';
render=function(animate=true){
 if(state.section!=='Juegos'||state.game!==0){legacy(state.section,state.game);return}
 document.body.dataset.variant=variant;
 document.body.classList.toggle('reduced',state.reduced);
 document.body.classList.toggle('text-large',state.large);
 $('.bookmarks').replaceChildren(...sections.map(s=>{const b=document.createElement('button');b.textContent=s;if(s==='Juegos')b.setAttribute('aria-current','page');b.onclick=()=>{if(s==='Juegos'){b.animate?.([{transform:'translateY(-2px)'},{transform:'translateY(0)'}],{duration:state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches?0:(parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--press'))||140)})}else legacy(s)};return b}));
 const title='<header class="page-title"><p class="eyebrow">Memoria musical</p><h1>Simón</h1></header>';
 const image=`<div class="illustration-window"><img class="hero" src="/simple-assets/piano-${variant.toLowerCase()}.webp" alt="" decoding="async"></div>`;
 const copy='<p class="description">Escuchá una secuencia y repetila en el piano.</p>';
 const play=`<button class="primary" id="play">${simpleSlice(variant==='B'?'wood':'green')}<span>Jugar</span></button>`;
 const page=$('#page');page.innerHTML=variant==='A'?title+image+copy+play:variant==='B'?title+copy+image+play:image+title+copy+play;
 $('#play').onclick=()=>modal('Simón','<p>La preparación y la partida conservan su funcionamiento. Este prototipo explora solo la portada.</p><button class="primary" id="returnGame">Simular fin de partida</button><button id="legacyGame">Abrir la versión anterior</button>');
 $('#index').textContent='1 de 4';$('#prev').disabled=true;$('#next').disabled=false;
 $('#prev').innerHTML=simpleSlice('arrow');$('#next').innerHTML=simpleSlice('arrow');$('#next').firstElementChild.classList.add('flipped');
 $('#variantLabel').textContent=`${variant} · ${simpleVariants[variant][0]}`;$('#variantDescription').textContent=simpleVariants[variant][1];
 $('#state').textContent=JSON.stringify({variant,screen:'Simón',page:1,total:4,section:state.section,reduced:state.reduced,large:state.large,otherScreens:'prototipo anterior'},null,2);
 if(animate&&!state.reduced&&!matchMedia('(prefers-reduced-motion:reduce)').matches){page.getAnimations().forEach(a=>a.cancel());page.animate([{opacity:.55,transform:'translateX(6px)'},{opacity:1,transform:'none'}],{duration:parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--enter'))||220,easing:'ease-out'})}
 document.documentElement.dataset.simple='ready';
 console.info('PROTOTIPO 2D',JSON.stringify({variant,...state}));
};
switchVariant=function(delta){const keys=Object.keys(simpleVariants);variant=keys[(keys.indexOf(variant)+delta+keys.length)%keys.length];const u=new URL(location);u.searchParams.set('variant',variant);history.replaceState(null,'',u);render()};
$('#variantPrev').onclick=()=>switchVariant(-1);$('#variantNext').onclick=()=>switchVariant(1);
$('#largeText').onchange=e=>{state.large=e.target.checked;render(false)};
$('#restart').onclick=()=>legacy('Personaje');
document.addEventListener('click',e=>{if(e.target.id==='legacyGame')legacy('Juegos',0)});
document.addEventListener('pointerdown',e=>{const b=e.target.closest('.stage button');if(!b||b.disabled||state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches)return;b.animate([{filter:'brightness(1)'},{filter:'brightness(.94)'},{filter:'brightness(1)'}],{duration:parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--press'))||140})});
$('#reduce').addEventListener('change',()=>{if(state.reduced)document.getAnimations().forEach(a=>a.cancel())});
matchMedia('(prefers-reduced-motion:reduce)').addEventListener('change',e=>{if(e.matches)document.getAnimations().forEach(a=>a.cancel())});
Promise.all(['garden','book','controls','piano-a','piano-b','piano-c'].map(key=>{const i=new Image();i.src=`/simple-assets/${key}.webp`;return i.decode()})).then(()=>document.documentElement.dataset.simpleAssets='ready');
render(false);

// A · Atlas ilustrado. Arte raster independiente del texto, controles y estado.
const artFiles={piano:'simon.webp',guitar:'instruments.webp',drum:'rhythm.webp',tree:'defender.webp'};
const chapters=['Memoria musical','Reconocimiento de instrumentos','Pulso y lectura','Lectura musical'];
const slices={ribbon:[235,40,205,505],selected:[825,40,205,505],play:[12,603,617,288],paper:[644,604,600,288],arrow:[146,950,350,260],ornament:[650,985,580,200]};
function rasterSlice(name,classes=''){
 return `<svg class="raster-slice ${classes}" viewBox="${slices[name].join(' ')}" preserveAspectRatio="none" aria-hidden="true"><image href="/art-assets/controls.webp" width="1254" height="1254"/></svg>`;
}
artwork = kind => `<img class="hero-painting" src="/art-assets/${artFiles[kind]}" alt="" decoding="async">`;
variant='A';
switchVariant=()=>location.assign('?variant=A');
$('.masthead>span').innerHTML=rasterSlice('paper','brand-material')+'<span>Music Garden</span>';
$('.forest').innerHTML='<img class="garden-painting" src="/art-assets/garden.webp" alt=""><div class="ambient" aria-hidden="true"></div>';
for(let i=0;i<7;i++){const mote=document.createElement('i');mote.style.setProperty('--i',i);$('.ambient').append(mote)}
const canopy=document.createElement('div');canopy.className='canopy-leaf';canopy.setAttribute('aria-hidden','true');canopy.innerHTML=rasterSlice('ornament');$('.stage').append(canopy);
const pageEdge=document.createElement('div');pageEdge.className='paper-edge';pageEdge.setAttribute('aria-hidden','true');pageEdge.innerHTML='<svg class="raster-slice" viewBox="923 105 70 1260" preserveAspectRatio="none" aria-hidden="true"><image href="/art-assets/book.webp" width="1024" height="1536"/></svg>';$('.book').append(pageEdge);
const pageRender=render;
render=function(animate=true){
 pageRender(false);
 $('.stage').dataset.section=state.section;
 $('.bookmarks').querySelectorAll('button').forEach(b=>{const selected=b.getAttribute('aria-current')==='page';b.innerHTML=`${rasterSlice(selected?'selected':'ribbon')}<span>${b.textContent}</span>`});
 const page=$('#page');
 if(state.section==='Personaje'){
  page.innerHTML=`<div class="chapter-heading"><h1>Personaje</h1>${rasterSlice('ornament','heading-ornament')}</div><div class="cover-art character-art"><img class="hero-painting character-painting" src="/art-assets/character.webp" alt="" decoding="async"></div>`;
 }else{
  const h1=page.querySelector('h1');const head=document.createElement('div');head.className='chapter-heading';h1.before(head);head.append(h1);head.insertAdjacentHTML('beforeend',rasterSlice('ornament','heading-ornament'));
  if(state.section==='Juegos')page.querySelector('.eyebrow').textContent=chapters[state.game];
  if(state.section==='Logros'&&state.achievement==='Medallas'){
   page.querySelector('.empty-art')?.remove();page.querySelector('.empty-note').insertAdjacentHTML('afterbegin',rasterSlice('ornament','medal-ornament'));
  }
 }
 if(animate&&!state.reduced&&!matchMedia('(prefers-reduced-motion:reduce)').matches){
  page.getAnimations().forEach(a=>a.cancel());
  page.animate([{transform:'perspective(900px) rotateY(-6deg)',opacity:.65},{transform:'none',opacity:1}],{duration:parseFloat(getComputedStyle(document.documentElement).getPropertyValue('--enter'))||220,easing:'ease-out'});
 }
 const play=$('#play');if(play)play.innerHTML=`${rasterSlice('play')}<span>Jugar</span>`;
 $('#prev').innerHTML=rasterSlice('arrow');$('#next').innerHTML=rasterSlice('arrow','reverse');
 $('#more').innerHTML=`${rasterSlice('paper')}<span>Más</span>`;
 $('#variantLabel').textContent='Atlas · Estudio ilustrado';$('#variantDescription').textContent='A elegida. Compará con el boceto anterior usando las flechas de esta barra. Arte v2 en revisión.';
 $('#variantPrev').onclick=()=>location.assign('?variant=A');$('#variantNext').onclick=()=>location.assign('?variant=A');
 $('#state').textContent=JSON.stringify({variant:'A',art:2,...state},null,2);
 document.documentElement.dataset.art='ready';
};
// La geometría de los controles permanece estable. La animación no condiciona acciones.
document.addEventListener('pointerdown',e=>{
 if(state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches)return;
 const item=e.target.closest('.bookmarks button,.primary,.pagination button,.cover-art');
 if(!item||item.disabled)return;
 item.animate([{transform:'scale(1)'},{transform:'scale(.97)'},{transform:'scale(1)'}],{duration:140,easing:'ease-out'});
});
function stopMotion(){if(state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches)document.getAnimations().forEach(a=>a.cancel())}
$('#reduce').addEventListener('change',stopMotion);
matchMedia('(prefers-reduced-motion:reduce)').addEventListener('change',stopMotion);
document.addEventListener('visibilitychange',()=>document.body.classList.toggle('backgrounded',document.hidden));
const ornamentImages=['garden.webp','book.webp','controls.webp','character.webp',...Object.values(artFiles)];
Promise.all(ornamentImages.map(src=>new Promise(resolve=>{const i=new Image();i.onload=()=>resolve(true);i.onerror=()=>resolve(false);i.src='/art-assets/'+src}))).then(results=>{document.documentElement.dataset.artAssets=results.every(Boolean)?'ready':'missing'});
render(false);

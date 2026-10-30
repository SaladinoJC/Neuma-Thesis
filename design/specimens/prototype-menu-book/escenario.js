// C elegida por el usuario. Refinar solo esta portada; el resto conserva su destino.
const unrefinedRender=render;
const prefersLessMotion=()=>state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches;
const motionMs=name=>parseFloat(getComputedStyle(document.documentElement).getPropertyValue(name))||140;
render=function(animate=true){
 unrefinedRender(animate&&variant!=='C');
 const book=$('.book');
 if(variant!=='C'){$('.book-skin')?.remove();return}
 if(state.section!=='Juegos'||state.game!==0)return;
 if(!$('.book-skin')){const skin=document.createElement('div');skin.className='book-skin';skin.setAttribute('aria-hidden','true');skin.innerHTML='<img src="/simple-assets/book.webp" alt="">';book.prepend(skin)}
 const page=$('#page'),heading=page.querySelector('.page-title');page.prepend(heading);
 for(const [id,path]of [['prev','M16 5 8 12l8 7M8 12h14'],['next','m8 5 8 7-8 7M16 12H2']]){
  $('#'+id).innerHTML=`<svg class="nav-arrow" viewBox="0 0 24 24" aria-hidden="true"><path d="${path}"/></svg>`;
 }
 $('#variantLabel').textContent='C · Escenario refinado';
 $('#variantDescription').textContent='C elegida. Categoría → título → ilustración +18% → descripción → Jugar. Assets originales conservados.';
 $('#state').textContent=JSON.stringify({variant:'C',revision:'refinamiento',screen:'Simón',page:1,total:4,reduced:state.reduced,large:state.large,assets:'originales sin reemplazo'},null,2);
 if(animate&&!prefersLessMotion()){
  page.getAnimations().forEach(a=>a.cancel());$('.book-skin').getAnimations().forEach(a=>a.cancel());
  page.animate([{opacity:.6,transform:'translateX(8px)'},{opacity:1,transform:'none'}],{duration:motionMs('--enter'),easing:'ease-out'});
  $('.book-skin').animate([{transform:'rotate(.18deg)'},{transform:'rotate(0deg)'}],{duration:motionMs('--enter'),easing:'ease-out'});
 }
 document.documentElement.dataset.escenario='ready';
};
// Captura para que el feedback de presión use geometría además de brillo.
document.addEventListener('pointerdown',e=>{
 if(variant!=='C'||prefersLessMotion())return;
 const b=e.target.closest('.stage button');if(!b||b.disabled)return;
 b.getAnimations().forEach(a=>a.cancel());
 b.animate([{transform:'translateY(0) scale(1)'},{transform:'translateY(1.5px) scale(.98)'},{transform:'translateY(0) scale(1)'}],{duration:motionMs('--press'),easing:'ease-out'});
},true);
const transitionPreview=document.createElement('button');transitionPreview.id='previewTurn';transitionPreview.textContent='Repetir transición de página';transitionPreview.onclick=()=>render(true);$('.prototype-tools details').insertBefore(transitionPreview,$('#state'));
render(false);

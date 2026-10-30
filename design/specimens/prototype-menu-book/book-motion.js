// Los navegadores sin transiciones entre documentos conservan la navegación inmediata.
window.__bookTransition={active:false,skipped:false};
let activeBookTransition;
function pageTransition(event){
 const transition=event.viewTransition;
 window.__bookTransition={event:event.type,active:!!transition,skipped:false};
 if(!transition)return;
 transition.ready.catch(()=>{});
 if(state.reduced||matchMedia('(prefers-reduced-motion:reduce)').matches){transition.skipTransition();window.__bookTransition.skipped=true;return}
 activeBookTransition=transition;
 transition.finished.then(()=>activeBookTransition=null,()=>activeBookTransition=null);
}
window.addEventListener('pageswap',pageTransition);window.addEventListener('pagereveal',pageTransition);
// Una nueva acción tiene prioridad sobre la animación visual del cambio anterior.
document.addEventListener('pointerdown',()=>activeBookTransition?.skipTransition(),true);

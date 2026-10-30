// Verificacion visual acotada de C y de su transicion nativa.
import {spawn} from 'node:child_process';
import {mkdtemp,writeFile,mkdir} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
const folder=path.dirname(fileURLToPath(import.meta.url));
const profile=await mkdtemp(path.join(tmpdir(),'music-book-prototype-'));
const chrome=spawn('C:/Program Files/Google/Chrome/Application/chrome.exe',['--headless=new','--remote-debugging-port=0','--no-first-run',`--user-data-dir=${profile}`,'about:blank'],{windowsHide:true,stdio:['ignore','ignore','pipe']});
let socket;const timer=setTimeout(()=>{chrome.kill();process.exit(1)},55000);
try{
 const endpoint=await new Promise(resolve=>{let s='';chrome.stderr.on('data',d=>{s+=d;const m=s.match(/DevTools listening on (ws:\/\/\S+)/);if(m)resolve(m[1])})});
 socket=new WebSocket(endpoint);await new Promise(resolve=>socket.onopen=resolve);
 let id=0;const pending=new Map();socket.onmessage=e=>{const m=JSON.parse(e.data);if(m.id){const p=pending.get(m.id);pending.delete(m.id);m.error?p.reject(m.error):p.resolve(m.result)}};
 const call=(method,params={},sessionId)=>new Promise((resolve,reject)=>{const key=++id;pending.set(key,{resolve,reject});socket.send(JSON.stringify({id:key,method,params,...sessionId?{sessionId}:{}}))});
 const {targetId}=await call('Target.createTarget',{url:'about:blank'});const {sessionId}=await call('Target.attachToTarget',{targetId,flatten:true});
 const cmd=(method,params)=>call(method,params,sessionId);
 const evaluate=async expression=>(await cmd('Runtime.evaluate',{expression,returnByValue:true,awaitPromise:true})).result.value;
 await cmd('Page.enable');const reports=[];
 async function openReference(spec){
  await cmd('Emulation.setDeviceMetricsOverride',{width:spec.width,height:spec.height,deviceScaleFactor:1,mobile:true});
  await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=C'});
  for(let i=0;i<180;i++){if(await evaluate(`document.documentElement.dataset.escenario==='ready'&&document.documentElement.dataset.simpleAssets==='ready'&&document.documentElement.dataset.tokens==='ready'`))break;await new Promise(r=>setTimeout(r,35))}
  await evaluate(`state.large=${!!spec.large};render(false);Promise.all([...document.querySelectorAll('#page img')].map(i=>i.decode()))`);
 }
 for(const spec of [{width:360,height:800},{width:411,height:891},{width:600,height:1000},{width:320,height:640},{width:360,height:800,large:true}]){
  await openReference(spec);
  const metrics=await evaluate(`(()=>{const page=document.querySelector('#page'),img=page.querySelector('.hero'),box=img.getBoundingClientRect(),r=img.naturalWidth/img.naturalHeight,painted=Math.min(box.width,box.height*r),baseline=Math.min(page.clientWidth+28,246*r);const tabs=[...document.querySelectorAll('.bookmarks button')];const active=tabs.find(t=>t.getAttribute('aria-current')==='page');const nav=document.querySelector('.bookmarks').getBoundingClientRect();return {width:innerWidth,scrollWidth:document.documentElement.scrollWidth,height:document.documentElement.scrollHeight,viewportHeight:innerHeight,headingOrder:[...page.children].map(e=>e.className),growthPercent:Math.round((painted/baseline-1)*1000)/10,paintedWidth:painted,baselineWidth:baseline,tabRise:tabs.filter(t=>t!==active).map(t=>Math.round(t.getBoundingClientRect().top-active.getBoundingClientRect().top)),tabLegibility:tabs.map(t=>{const range=document.createRange();range.selectNodeContents(t);const text=range.getBoundingClientRect(),b=t.getBoundingClientRect(),visible=b.left+b.width/2>nav.left&&b.left+b.width/2<nav.right;return {label:t.textContent,font:parseFloat(getComputedStyle(t).fontSize),fits:text.width<=b.width+1,hit:!visible||document.elementFromPoint(b.left+b.width/2,b.top+25)?.closest('button')===t}}),navControls:['prev','next'].map(id=>{const b=document.querySelector('#'+id),r=b.getBoundingClientRect();return {id,width:r.width,height:r.height,stroke:getComputedStyle(b.querySelector('svg')).stroke}}),forestFilter:getComputedStyle(document.querySelector('.forest img')).filter,bookFilter:getComputedStyle(document.querySelector('.book-skin img')).filter,playMaterial:document.querySelector('#play image')?.getAttribute('href'),ambient:document.getAnimations().length}})()`);
  reports.push({...spec,...metrics});
  const shot=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true,clip:{x:0,y:0,width:spec.width,height:metrics.height,scale:1}});await writeFile(path.join(folder,`escenario-${spec.width}${spec.large?'-text200':''}.png`),Buffer.from(shot.data,'base64'));
 }
 await openReference({width:360,height:800});
 const preview=await evaluate(`(()=>{document.querySelector('#previewTurn').click();return {page:document.querySelector('#page').getAnimations().length,book:document.querySelector('.book-skin').getAnimations().length,enabled:!document.querySelector('#next').disabled}})()`);
 const rapid=await evaluate(`(()=>{document.querySelector('#previewTurn').click();document.querySelector('#previewTurn').click();return {page:document.querySelector('#page').getAnimations().length,book:document.querySelector('.book-skin').getAnimations().length}})()`);
 await evaluate(`document.querySelector('#reduce').click();document.querySelector('#previewTurn').click()`);const manualReduced=await evaluate(`document.getAnimations().length===0`);
 await evaluate(`document.querySelector('#next').click()`);for(let i=0;i<160;i++){if(await evaluate(`document.documentElement.dataset.artAssets==='ready'`))break;await new Promise(r=>setTimeout(r,35))}
 const reducedDestination=await evaluate(`({game:state.game,reduced:state.reduced,transition:window.__bookTransition})`);
 await cmd('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:'no-preference'}]});
 await openReference({width:411,height:891});
 await evaluate(`document.querySelector('#next').click()`);for(let i=0;i<160;i++){if(await evaluate(`document.documentElement.dataset.artAssets==='ready'&&document.querySelector('h1').textContent==='Instrumentos'`))break;await new Promise(r=>setTimeout(r,35))}
 await evaluate(`new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r)))`);
 const navigation=await evaluate(`({game:state.game,title:document.querySelector('h1').textContent,transition:window.__bookTransition})`);
 await cmd('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:'reduce'}]});
 await openReference({width:360,height:800});await evaluate(`document.querySelector('#previewTurn').click()`);const systemReduced=await evaluate(`document.getAnimations().length===0`);
 const interaction={preview,rapid,manualReduced,systemReduced,reducedDestination,navigation};
 await writeFile(path.join(folder,'escenario-verification.json'),JSON.stringify({reports,interaction},null,2));
 console.log(JSON.stringify({reports,interaction},null,2));
}finally{clearTimeout(timer);socket?.close();chrome.kill()}

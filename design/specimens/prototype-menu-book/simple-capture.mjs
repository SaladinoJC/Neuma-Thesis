// Capturas y comprobaciones del prototipo de una sola pantalla 2D.
import {spawn} from 'node:child_process';
import {mkdtemp,writeFile,mkdir} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import path from 'node:path';
import {fileURLToPath} from 'node:url';
const folder=path.dirname(fileURLToPath(import.meta.url));
const artMode=process.argv.includes('--art');
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
 await cmd('Page.enable');
 if(process.argv.includes('--export')||process.argv.includes('--export-native')){
  await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=A'});
  const native=process.argv.includes('--export-native');
  const destination=path.resolve(folder,native?'../../../assets/exports/android-menu-v1':'../../../assets/exports/prototype-menu-simple-v3');await mkdir(destination,{recursive:true});const exported=[];
  for(const key of native?['instruments','rhythm','defender','character']:['garden','book','controls','piano-a','piano-b','piano-c']){
   const data=await evaluate(`(async()=>{const i=new Image();i.src='/simple-assets/${native?'native/':''}${key}.png';await i.decode();const scale=Math.min(1,${key==='controls'?1254:key==='book'?1024:key==='character'?512:768}/i.width);const c=document.createElement('canvas');c.width=Math.round(i.width*scale);c.height=Math.round(i.height*scale);c.getContext('2d').drawImage(i,0,0,c.width,c.height);return {width:c.width,height:c.height,url:c.toDataURL('image/webp',.9)}})()`);
   const b=Buffer.from(data.url.split(',')[1],'base64');await writeFile(path.join(destination,key+'.webp'),b);exported.push({file:key+'.webp',width:data.width,height:data.height,bytes:b.length});
  }
  await writeFile(path.join(destination,'metadata.json'),JSON.stringify(exported,null,2));console.log(JSON.stringify(exported));
 }else{
  const reports=[];
  for(const spec of [{width:360,height:800},{width:411,height:891},{width:600,height:1000},{width:320,height:640},{width:360,height:800,large:true}])for(const variant of ['A','B','C']){
   await cmd('Emulation.setDeviceMetricsOverride',{...spec,large:undefined,deviceScaleFactor:1,mobile:true});
   await cmd('Page.navigate',{url:`http://127.0.0.1:8765/prototype-menu-book/simple.html?variant=${variant}`});
   for(let i=0;i<160;i++){if(await evaluate(`document.documentElement.dataset.simpleAssets==='ready'&&document.documentElement.dataset.fonts==='ready'&&document.documentElement.dataset.tokens==='ready'`))break;await new Promise(r=>setTimeout(r,35))}
   await evaluate(`state.large=${!!spec.large};render(false);Promise.all([...document.querySelectorAll('#page img')].map(i=>i.decode()))`);
   const metrics=await evaluate(`(()=>{const book=document.querySelector('.book').getBoundingClientRect();const tools=document.querySelector('.prototype-tools').getBoundingClientRect();return {width:innerWidth,scrollWidth:document.documentElement.scrollWidth,height:document.documentElement.scrollHeight,fonts:[...document.fonts].map(f=>({family:f.family,status:f.status})),assets:document.documentElement.dataset.simpleAssets,animations:document.getAnimations().length,clipped:[...document.querySelectorAll('.stage h1,.stage p,.stage button')].filter(e=>{if(e.matches('.bookmarks button')){const range=document.createRange();range.selectNodeContents(e);const text=range.getBoundingClientRect(),box=e.getBoundingClientRect();return text.left<box.left-1||text.right>box.right+1||text.top<box.top-1||text.bottom>box.bottom+1}return e.clientWidth&&(e.scrollWidth>e.clientWidth+1||e.scrollHeight>e.clientHeight+1)}).map(e=>e.textContent),outsidePaper:[...document.querySelectorAll('#page h1,#page p,#page button')].filter(e=>e.getBoundingClientRect().bottom>book.bottom-20).map(e=>e.textContent),smallControls:[...document.querySelectorAll('.stage button')].filter(e=>e.getBoundingClientRect().height<48).map(e=>e.textContent),tabHits:[...document.querySelectorAll('.bookmarks button')].map(e=>{const r=e.getBoundingClientRect();const hit=document.elementFromPoint(r.x+r.width/2,r.y+25);const nav=document.querySelector('.bookmarks').getBoundingClientRect();const visible=r.x+r.width/2>nav.left&&r.x+r.width/2<nav.right;return {name:e.textContent,visible,hit:!visible||hit?.closest('button')===e,overlap:e.getBoundingClientRect().bottom>book.top}}),covered:[...document.querySelectorAll('.stage button')].filter(e=>{const r=e.getBoundingClientRect();return r.bottom>tools.top&&r.top<tools.bottom&&r.left<tools.right&&r.right>tools.left}).map(e=>e.textContent)}})()`);
   reports.push({...spec,variant,...metrics});
   if(spec.width===411||spec.large){const shot=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true,clip:{x:0,y:0,width:spec.width,height:metrics.height,scale:1}});await writeFile(path.join(folder,`simple-${variant}${spec.large?'-text200':''}.png`),Buffer.from(shot.data,'base64'))}
  }
  await evaluate(`state.large=false;state.reduced=false;render(false);document.querySelector('#play').click();document.querySelector('#returnGame').click()`);
  const returned=await evaluate(`state.section==='Juegos'&&state.game===0&&!document.querySelector('#dialog').open`);
  await evaluate(`document.querySelector('#variantNext').click()`);const variantURL=await evaluate(`new URLSearchParams(location.search).get('variant')===variant`);
  await evaluate(`document.querySelector('#reduce').click();render()`);const manualReduced=await evaluate(`document.getAnimations().length===0`);
  await cmd('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:'reduce'}]});
  await evaluate(`state.reduced=false;render()`);const systemReduced=await evaluate(`document.getAnimations().length===0`);
  await evaluate(`document.querySelector('#index').click()`);const index=await evaluate(`document.querySelectorAll('[data-jump]').length===4`);await evaluate(`document.querySelector('#closeDialog').click()`);
  await evaluate(`document.querySelector('#next').click()`);for(let i=0;i<100;i++){if(await evaluate(`document.documentElement.dataset.artAssets==='ready'&&new URLSearchParams(location.search).get('game')==='1'`))break;await new Promise(r=>setTimeout(r,35))}
  const legacyNext=await evaluate(`state.section==='Juegos'&&state.game===1&&document.querySelector('h1').textContent==='Instrumentos'`);
  await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/?variant=A&art=2&section=Personaje'});for(let i=0;i<100;i++){if(await evaluate(`document.documentElement.dataset.artAssets==='ready'&&document.querySelector('h1')?.textContent==='Personaje'`))break;await new Promise(r=>setTimeout(r,35))}
  const legacyPersonaje=await evaluate(`state.section==='Personaje'&&document.querySelector('h1').textContent==='Personaje'`);
  const interaction={returned,variantURL,manualReduced,systemReduced,index,legacyNext,legacyPersonaje};
  await writeFile(path.join(folder,'simple-verification.json'),JSON.stringify({reports,interaction},null,2));
  console.log(JSON.stringify({cases:reports.length,problems:reports.filter(r=>r.scrollWidth>r.width||r.clipped.length||r.smallControls.length||r.outsidePaper.length||r.covered.length||r.tabHits.some(t=>!t.hit||!t.overlap)||r.assets!=='ready'||r.animations!==0),interaction},null,2));
  await cmd('Emulation.setDeviceMetricsOverride',{width:1200,height:1600,deviceScaleFactor:1,mobile:false});await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/simple-gallery.html'});
  await evaluate(`new Promise(resolve=>{const done=()=>Promise.all([...document.images].map(i=>i.decode())).then(resolve);document.readyState==='complete'?done():addEventListener('load',done,{once:true})})`);
  const shot=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true});await writeFile(path.join(folder,'simple-comparison.png'),Buffer.from(shot.data,'base64'));
 }
}finally{clearTimeout(timer);socket?.close();chrome.kill()}

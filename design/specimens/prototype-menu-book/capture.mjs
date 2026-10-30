// Verificación reproducible del estudio con Chrome/CDP existente; sin paquetes.
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
 await cmd('Page.enable');const reports=[];
 if(process.argv.includes('--export')){
  await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/?variant=A'});
  const destination=path.resolve(folder,'../../../assets/exports/prototype-menu-v2');await mkdir(destination,{recursive:true});
  const exported=[];
  for(const key of ['garden','book','controls','simon','instruments','rhythm','defender','character']){
   const data=await evaluate(`(async()=>{const i=new Image();i.src='/art-assets/${key}.png';await i.decode();const max=${['garden','character'].includes(key)?768:['book','controls'].includes(key)?1536:960};const scale=Math.min(1,max/i.width);const c=document.createElement('canvas');c.width=Math.round(i.width*scale);c.height=Math.round(i.height*scale);c.getContext('2d').drawImage(i,0,0,c.width,c.height);return {width:c.width,height:c.height,url:c.toDataURL('image/webp',.9)}})()`);
   const buffer=Buffer.from(data.url.split(',')[1],'base64');await writeFile(path.join(destination,key+'.webp'),buffer);exported.push({file:key+'.webp',width:data.width,height:data.height,bytes:buffer.length});
  }
  await writeFile(path.join(destination,'metadata.json'),JSON.stringify(exported,null,2));console.log(JSON.stringify(exported));
 }else{
 for(const width of [360,411,600])for(const variant of artMode?['A']:['A','B','C']){
  await cmd('Emulation.setDeviceMetricsOverride',{width,height:width===600?1000:891,deviceScaleFactor:1,mobile:true});
  await cmd('Page.navigate',{url:`http://127.0.0.1:8765/prototype-menu-book/?variant=${variant}${artMode?'&art=2':''}`});
  for(let i=0;i<150;i++){if(await evaluate(`document.documentElement.dataset.fonts==='ready'&&document.documentElement.dataset.tokens==='ready'${artMode?"&&document.documentElement.dataset.artAssets==='ready'":''}`))break;await new Promise(r=>setTimeout(r,40))}
  const scenes=artMode?[{section:'Personaje'},...Array.from({length:4},(_,game)=>({section:'Juegos',game})),{section:'Afinador'},{section:'Logros'}]:['Personaje','Juegos','Afinador','Logros'].map(section=>({section}));
  for(const scene of scenes){
   const {section}=scene;
   await evaluate(`Object.assign(state,${JSON.stringify(scene)});render(false);Promise.all([...document.querySelectorAll('#page img')].map(i=>i.decode()))`);
   const metrics=await evaluate(`(()=>{const tools=document.querySelector('.prototype-tools').getBoundingClientRect();return {fonts:[...document.fonts].map(f=>({family:f.family,status:f.status})),tokens:document.documentElement.dataset.tokens,width:innerWidth,scrollWidth:document.documentElement.scrollWidth,smallControls:[...document.querySelectorAll('.stage button')].filter(e=>e.getBoundingClientRect().height>0&&e.getBoundingClientRect().height<47).map(e=>e.textContent),clipped:[...document.querySelectorAll('.stage p,.stage h1,.stage button')].filter(e=>e.clientWidth&&(e.scrollWidth>e.clientWidth+1||e.scrollHeight>e.clientHeight+1)).map(e=>e.textContent),covered:[...document.querySelectorAll('.stage button')].filter(e=>{const r=e.getBoundingClientRect();return r.height&&r.bottom>tools.top&&r.top<tools.bottom&&r.left<tools.right&&r.right>tools.left}).map(e=>e.textContent)}})()`);
   reports.push({variant,width,...scene,...metrics});
   if(width===411){const shot=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true});await writeFile(path.join(folder,`${artMode?'art-':''}${variant}-${section}${scene.game!==undefined?'-'+scene.game:''}.png`),Buffer.from(shot.data,'base64'))}
  }
 }
 const extraReports=[];let motion=null;
 if(artMode){
  for(const spec of [{width:320,height:640},{width:360,height:800},{width:360,height:800,large:true}]){
   await cmd('Emulation.setDeviceMetricsOverride',{width:spec.width,height:spec.height,deviceScaleFactor:1,mobile:true});
   await cmd('Page.navigate',{url:'http://127.0.0.1:8765/prototype-menu-book/?variant=A&art=2'});
   for(let i=0;i<150;i++){if(await evaluate(`document.documentElement.dataset.artAssets==='ready'&&document.documentElement.dataset.fonts==='ready'`))break;await new Promise(r=>setTimeout(r,40))}
   for(const section of ['Personaje','Juegos','Afinador','Logros']){
    await evaluate(`document.querySelectorAll('.stage [style]').forEach(e=>e.style.removeProperty('font-size'));state.section=${JSON.stringify(section)};state.game=1;render(false);document.body.classList.toggle('text-large',${!!spec.large});if(${!!spec.large}){const sizes=[...document.querySelectorAll('.stage h1,.stage h2,.stage p,.stage span,.stage button')].map(e=>[e,parseFloat(getComputedStyle(e).fontSize)]);sizes.forEach(([e,size])=>e.style.fontSize=size*2+'px')}Promise.all([...document.querySelectorAll('#page img')].map(i=>i.decode()))`);
    const metrics=await evaluate(`(()=>{const tools=document.querySelector('.prototype-tools').getBoundingClientRect();return {scrollWidth:document.documentElement.scrollWidth,height:document.documentElement.scrollHeight,clipped:[...document.querySelectorAll('.stage h1,.stage h2,.stage p,.stage span,.stage button')].filter(e=>e.clientWidth&&(e.scrollWidth>e.clientWidth+1||e.scrollHeight>e.clientHeight+1)).map(e=>e.textContent),covered:[...document.querySelectorAll('.stage button')].filter(e=>{const r=e.getBoundingClientRect();return r.height&&r.bottom>tools.top&&r.top<tools.bottom&&r.left<tools.right&&r.right>tools.left}).map(e=>e.textContent),assets:[...document.querySelectorAll('#page img')].every(i=>i.complete&&i.naturalWidth>0)}})()`);
    const outsidePaper=await evaluate(`(()=>{const book=document.querySelector('.book').getBoundingClientRect();const margin=${!!spec.large}?book.height*.1:12;return [...document.querySelectorAll('#page p,#page h1,#page h2,#page button')].filter(e=>{const r=e.getBoundingClientRect();return r.height>0&&r.bottom>book.bottom-margin}).map(e=>e.textContent)})()`);
    extraReports.push({...spec,section,...metrics,outsidePaper});
    if(spec.large||spec.width===320){const shot=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true,clip:{x:0,y:0,width:spec.width,height:metrics.height,scale:1}});await writeFile(path.join(folder,`art-${spec.width}${spec.large?'-text200':''}-${section}.png`),Buffer.from(shot.data,'base64'))}
   }
  }
  await evaluate(`document.body.classList.remove('text-large');document.querySelectorAll('.stage [style]').forEach(e=>e.style.removeProperty('font-size'));state.reduced=false;render(false)`);
  const ambient=await evaluate(`document.getAnimations().length`);
  await cmd('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:'reduce'}]});
  await evaluate(`render();new Promise(r=>requestAnimationFrame(()=>requestAnimationFrame(r)))`);
  const systemReduced=await evaluate(`document.getAnimations().length===0`);
  await cmd('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:'no-preference'}]});
  await evaluate(`state.section='Juegos';state.game=0;render(false);document.querySelector('#next').click();document.querySelector('#next').click();document.querySelector('#next').click()`);
  const rapid=await evaluate(`({last:state.game===3&&document.querySelector('#next').disabled,pageAnimations:document.querySelector('#page').getAnimations().length})`);
  motion={ambient,systemReduced,rapid};
 }
 await evaluate(`state.section='Juegos';state.game=3;render(false);document.querySelector('#play').click();document.querySelector('#returnGame').click()`);
 const returned=await evaluate(`state.section==='Juegos'&&state.game===3&&!document.querySelector('#dialog').open`);
 await evaluate(`document.querySelector('#index').click();document.querySelector('[data-jump="0"]').click()`);
 const index=await evaluate(`state.game===0&&document.querySelector('#prev').disabled`);
 await evaluate(`document.querySelector('#reduce').click();state.game=2;render()`);
 const reduced=await evaluate(`document.getAnimations().length===0`);
 await evaluate(`document.querySelector('#restart').click()`);const restart=await evaluate(`state.section==='Personaje'&&state.game===0`);
 if(!artMode)await evaluate(`document.querySelector('#variantNext').click()`);const variantURL=await evaluate(`new URLSearchParams(location.search).get('variant')===variant`);
 await cmd('Emulation.setDeviceMetricsOverride',{width:1080,height:3200,deviceScaleFactor:1,mobile:false});
 await cmd('Page.navigate',{url:`http://127.0.0.1:8765/prototype-menu-book/${artMode?'art-gallery':'comparison'}.html`});
 await evaluate(`new Promise(resolve=>{const done=()=>Promise.all([...document.images].map(i=>i.decode())).then(resolve);document.readyState==='complete'?done():addEventListener('load',done,{once:true})})`);
 const comparison=await cmd('Page.captureScreenshot',{format:'png',captureBeyondViewport:true});await writeFile(path.join(folder,artMode?'art-comparison.png':'comparison.png'),Buffer.from(comparison.data,'base64'));
 await writeFile(path.join(folder,artMode?'art-verification.json':'verification.json'),JSON.stringify({reports,extraReports,motion,interaction:{returned,index,reduced,restart,variantURL}},null,2));
 console.log(JSON.stringify({cases:reports.length,problems:reports.filter(r=>r.width<r.scrollWidth||r.smallControls.length||r.clipped.length||r.covered.length),extraProblems:extraReports.filter(r=>r.width<r.scrollWidth||r.clipped.length||r.covered.length||r.outsidePaper.length||!r.assets),motion,interaction:{returned,index,reduced,restart,variantURL}},null,2));
 }
}finally{clearTimeout(timer);socket?.close();chrome.kill()}

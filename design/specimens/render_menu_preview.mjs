// Render local de un documento de diseño con Chrome existente. Sin paquetes nuevos.
import {spawn} from 'node:child_process';
import {createServer} from 'node:http';
import {readFile, writeFile, mkdtemp} from 'node:fs/promises';
import {tmpdir} from 'node:os';
import path from 'node:path';
import {fileURLToPath} from 'node:url';

const folder=path.dirname(fileURLToPath(import.meta.url));
const profile=await mkdtemp(path.join(tmpdir(),'music-garden-preview-'));
const browser=process.env.MUSIC_GARDEN_CHROME || 'C:/Program Files/Google/Chrome/Application/chrome.exe';
const server=createServer(async(req,res)=>{
  try{
    const requested=path.resolve(folder,'.'+decodeURIComponent(new URL(req.url,'http://localhost').pathname));
    if(!requested.startsWith(folder+path.sep)){res.writeHead(403);res.end();return}
    const data=await readFile(requested);const ext=path.extname(requested);
    res.setHeader('Content-Type',({'.html':'text/html; charset=utf-8','.ttf':'font/ttf','.png':'image/png'})[ext]||'text/plain');res.end(data);
  }catch{res.writeHead(404);res.end()}
});
await new Promise(resolve=>server.listen(0,'127.0.0.1',resolve));
const chrome=spawn(browser,['--headless=new','--remote-debugging-port=0','--no-first-run','--no-default-browser-check',`--user-data-dir=${profile}`,'about:blank'],{windowsHide:true,stdio:['ignore','ignore','pipe']});
let logs='';let resolveEndpoint;const endpointPromise=new Promise(resolve=>resolveEndpoint=resolve);
chrome.stderr.on('data',data=>{logs+=data;const found=logs.match(/DevTools listening on (ws:\/\/[^\s]+)/);if(found)resolveEndpoint(found[1])});
let socket;
const timeout=setTimeout(()=>{console.error('Timeout de render');chrome.kill();process.exitCode=1;server.close()},45000);
try{
  const endpoint=await endpointPromise;socket=new WebSocket(endpoint);await new Promise((resolve,reject)=>{socket.onopen=resolve;socket.onerror=reject});
  let next=0;const pending=new Map();socket.onmessage=event=>{const message=JSON.parse(event.data);if(message.id){const entry=pending.get(message.id);pending.delete(message.id);message.error?entry.reject(message.error):entry.resolve(message.result)}};
  const call=(method,params={},sessionId)=>new Promise((resolve,reject)=>{const id=++next;pending.set(id,{resolve,reject});socket.send(JSON.stringify({id,method,params,...(sessionId?{sessionId}:{})}))});
  const {targetId}=await call('Target.createTarget',{url:'about:blank'});
  const {sessionId}=await call('Target.attachToTarget',{targetId,flatten:true});
  await call('Page.enable',{},sessionId);
  const gamesMode=process.argv.includes('--games');
  const cases=gamesMode?[
    {name:'defender-v1-360',file:'defender-v1.html',width:360,height:800},
    {name:'defender-v1-411',file:'defender-v1.html',width:411,height:891},
    {name:'defender-v1-hit',file:'defender-v1.html',width:411,height:891,query:'?state=hit'},
    {name:'defender-v1-error',file:'defender-v1.html',width:360,height:800,query:'?state=error'},
    {name:'defender-v1-life',file:'defender-v1.html',width:360,height:800,query:'?state=life'},
    {name:'defender-v1-pause',file:'defender-v1.html',width:360,height:800,query:'?state=pause'},
    {name:'simon-v1-listening',file:'simon-v1.html',width:800,height:360},
    {name:'simon-v1-response',file:'simon-v1.html',width:891,height:411,query:'?state=response'},
    {name:'simon-v1-small',file:'simon-v1.html',width:640,height:360},
    {name:'motion-v1-frame220',file:'motion-v1.html',width:411,height:700,query:'?frame=220'},
    {name:'motion-v1-reduced',file:'motion-v1.html',width:411,height:700,query:'?frame=220&reduce=1'},
    {name:'motion-v1-system-reduced',file:'motion-v1.html',width:411,height:700,query:'?frame=220',systemReduced:true}
  ]:[{name:'menu-v1-360',width:360,height:800},{name:'menu-v1-411',width:411,height:891},{name:'menu-v1-600',width:600,height:900},{name:'menu-v1-360-text200',width:360,height:800,query:'?large=1'},{name:'menu-v1-states',width:411,height:900,query:'?states=1'}];
  const report=[];
  for(const specimen of cases){
    await call('Emulation.setEmulatedMedia',{features:[{name:'prefers-reduced-motion',value:specimen.systemReduced?'reduce':'no-preference'}]},sessionId);
    await call('Emulation.setDeviceMetricsOverride',{width:specimen.width,height:specimen.height,deviceScaleFactor:1,mobile:true},sessionId);
    const targetUrl=`http://127.0.0.1:${server.address().port}/${specimen.file||'menu-v1.html'}${specimen.query||''}`;
    await call('Page.navigate',{url:targetUrl},sessionId);
    for(let attempt=0;attempt<100;attempt++){
      const state=await call('Runtime.evaluate',{expression:`document.URL===${JSON.stringify(targetUrl)}&&document.documentElement.dataset.fonts==='ready'`,returnByValue:true},sessionId);
      if(state.result.value)break;
      await new Promise(resolve=>setTimeout(resolve,50));
    }
    const {result:{value:metrics}}=await call('Runtime.evaluate',{expression:`JSON.stringify({fontsReady:document.documentElement.dataset.fonts==='ready',fonts:[...document.fonts].map(f=>({family:f.family,status:f.status})),width:innerWidth,documentWidth:document.documentElement.scrollWidth,height:document.documentElement.scrollHeight,controls:[...document.querySelectorAll('[data-control]')].map(e=>({type:e.dataset.control,width:e.getBoundingClientRect().width,height:e.getBoundingClientRect().height})),overflow:[...document.querySelectorAll('h1,h2,h3,p,.card-copy,.text-control')].filter(e=>e.clientWidth>0&&(e.scrollWidth>e.clientWidth+1||e.scrollHeight>e.clientHeight+1)).map(e=>e.textContent),titleFont:getComputedStyle(document.querySelector('h1')).fontFamily,bodyFont:getComputedStyle(document.body).fontFamily})`,returnByValue:true},sessionId);
    const parsed=JSON.parse(metrics);
    if(gamesMode){
      const extra=await call('Runtime.evaluate',{expression:`JSON.stringify({viewportHeight:innerHeight,notes:[...document.querySelectorAll('[data-note]')].map(e=>JSON.parse(e.dataset.note)),targets:[...document.querySelectorAll('.enemy')].filter(e=>getComputedStyle(e).visibility!=='hidden').map(e=>{const r=e.getBoundingClientRect();return {x:r.x,y:r.y,width:r.width,height:r.height}}),arena:(()=>{const e=document.querySelector('.arena');if(!e)return null;const r=e.getBoundingClientRect();return {x:r.x,y:r.y,width:r.width,height:r.height}})(),keys:[...document.querySelectorAll('[data-midi]')].map(e=>({midi:Number(e.dataset.midi),width:e.getBoundingClientRect().width,height:e.getBoundingClientRect().height,black:e.classList.contains('black')})),feedback:document.querySelector('.feedback')?.textContent,animations:document.getAnimations().length,reduced:document.body.classList.contains('reduced')})`,returnByValue:true},sessionId);
      Object.assign(parsed,JSON.parse(extra.result.value));
      if(specimen.file==='motion-v1.html'){
        const interaction=await call('Runtime.evaluate',{expression:`JSON.stringify((()=>{const b=document.querySelector('#play');b.click();const first={message:document.querySelector('.feedback').textContent,disabled:b.disabled};b.click();const second={message:document.querySelector('.feedback').textContent,disabled:b.disabled};document.getAnimations().forEach(a=>{a.pause();a.currentTime=220});return {first,second,systemReduced:matchMedia('(prefers-reduced-motion:reduce)').matches,animations:document.getAnimations().length}})())`,returnByValue:true},sessionId);
        parsed.interaction=JSON.parse(interaction.result.value);
      }
    }
    report.push({case:specimen.name,...parsed});
    const capture=await call('Page.captureScreenshot',{format:'png',captureBeyondViewport:true,clip:{x:0,y:0,width:specimen.width,height:Math.max(specimen.height,parsed.height),scale:1}},sessionId);
    await writeFile(path.join(folder,specimen.name+'.png'),Buffer.from(capture.data,'base64'));
    if(!gamesMode&&!specimen.query){const viewport=await call('Page.captureScreenshot',{format:'png',captureBeyondViewport:false},sessionId);await writeFile(path.join(folder,specimen.name+'-viewport.png'),Buffer.from(viewport.data,'base64'))}
  }
  await writeFile(path.join(folder,gamesMode?'games-v1-verification.json':'menu-v1-verification.json'),JSON.stringify(report,null,2));
  console.log(JSON.stringify(report.map(r=>({case:r.case,fontsReady:r.fontsReady,fonts:r.fonts,overflow:r.overflow,width:r.width,documentWidth:r.documentWidth,height:r.height})),null,2));
}finally{
  clearTimeout(timeout);if(socket)socket.close();chrome.kill();server.close();
  // El perfil temporal tiene una ruta propia; no se borra recursivamente desde este helper.
}

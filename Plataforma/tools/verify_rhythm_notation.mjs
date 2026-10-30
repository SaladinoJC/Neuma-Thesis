// Run after RhythmEngravingTest. Uses installed Chrome/Edge, no npm dependencies.
import { readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { resolve, join } from 'node:path';
import { pathToFileURL } from 'node:url';
import assert from 'node:assert/strict';
const client = process.env.CDP_CLIENT || join(process.env.USERPROFILE, '.agents/skills/design-taste-frontend/scripts/lib/cdp.mjs');
const { Browser } = await import(pathToFileURL(client));
const root = resolve(import.meta.dirname, '..');
const out = join(root, 'build/verification/rhythm-engraving'); mkdirSync(out, { recursive: true });
const fixtures = JSON.parse(readFileSync(join(root, 'app/build/verification/rhythm-engraving/catalog.json'), 'utf8'));
const palette = { ink: '#28392f', correct: '#315844', missed: '#8b4436', timing: '#8b4436', focus: '#3c6872' };
const browser = await Browser.launch();
const checks = [];
try {
  for (const width of [320, 390, 768]) {
    const page = await browser.newPage({ width, height: 240 });
    await page.goto(pathToFileURL(join(root, 'app/src/main/assets/rhythm/score.html')).href, { settleMs: 500 });
    for (const [index, score] of fixtures.entries()) {
      const data = await page.eval(async ({ score, palette }) => {
        RhythmNotation.setScore({ score, reduced: false, palette });
        RhythmNotation.update({ phase: 'PREVIEW', elapsedBeats: 0, kinds: [] });
        await document.fonts.ready;
        await new Promise(requestAnimationFrame);
        return RhythmNotation.inspect();
      }, { score, palette });
      assert.equal(data.ready, true, JSON.stringify({ width, index, error: data.error }));
      assert.equal(data.background, 'rgba(0, 0, 0, 0)');
      assert.equal(data.heads.length, score.bars.flatMap(bar => bar.notes).length);
      assert.ok(data.heads.every(note => note.count === 1), JSON.stringify(data.heads.filter(note => note.count !== 1)));
      assert.ok(data.anchors.every((point, i, points) => i === 0 || point.beat > points[i - 1].beat && point.x > points[i - 1].x));
      checks.push({ width, index, meter: score.meter, notes: data.heads.length });
    }
    const score = fixtures.find(score => score.bars.some(bar => bar.notes.some(note => note.tie)));
    const result = await page.eval(async ({ score, palette }) => {
      const frame = () => new Promise(requestAnimationFrame);
      const count = Math.max(...score.bars.flatMap(bar => bar.notes).map(note => note.attackIndex ?? -1)) + 1;
      const kinds = Array(count).fill('ON_TIME'); kinds[0] = 'OMITTED';
      RhythmNotation.setScore({ score, reduced: false, palette });
      const samples = [];
      for (const beat of [0, .25, .5, 1, 2, 4, 6]) {
        RhythmNotation.update({ phase: 'PLAYING', elapsedBeats: beat, kinds }); await frame();
        samples.push(RhythmNotation.inspect());
      }
      kinds[0] = 'ON_TIME'; // Same correction as a late-delivered native timestamp.
      RhythmNotation.update({ phase: 'RESULTS', elapsedBeats: 8, kinds }); await frame();
      const corrected = RhythmNotation.inspect();
      const beforeManual = document.getElementById('scroller').scrollLeft;
      document.getElementById('scroller').scrollLeft = 0; await frame();
      const manual = RhythmNotation.inspect();
      RhythmNotation.update({ phase: 'PREVIEW', elapsedBeats: 0, kinds: [] }); await frame();
      const repeat = RhythmNotation.inspect();
      RhythmNotation.setScore({ score, reduced: true, palette });
      const reduced = [];
      for (const beat of [.25, .75, score.meter + .25]) {
        RhythmNotation.update({ phase: 'PLAYING', elapsedBeats: beat, kinds: [] }); await frame();
        reduced.push(RhythmNotation.inspect());
      }
      return { samples, corrected, beforeManual, manual, repeat, reduced };
    }, { score, palette });
    assert.ok(result.samples.every(data => data.markerX === result.samples[0].markerX && data.markerVisible));
    assert.ok(Math.abs(result.samples[0].heads[0].centerX - result.samples[0].markerX) <= 1, 'Marker passes through the note head centre');
    assert.ok(result.samples.slice(1).every((data, i) => data.scrollLeft > result.samples[i].scrollLeft));
    assert.equal(new Set(result.samples.map(data => data.renderCount)).size, 1, 'No re-engraving on clock updates');
    assert.ok(result.corrected.heads.filter(note => !note.rest).every(note => note.color === 'rgb(49, 88, 68)'));
    assert.equal(result.manual.scrollLeft, 0); assert.ok(result.beforeManual > 0);
    assert.ok(result.repeat.heads.every(note => note.color === 'rgb(40, 57, 47)'));
    assert.equal(result.reduced[0].scrollLeft, result.reduced[1].scrollLeft);
    assert.ok(result.reduced[2].scrollLeft > result.reduced[1].scrollLeft);
    assert.ok(result.reduced.every(data => !data.markerVisible));
    await page.eval(({ score, palette }) => {
      RhythmNotation.setScore({ score, reduced: false, palette });
      RhythmNotation.update({ phase: 'PLAYING', elapsedBeats: 2, kinds: Array(100).fill('ON_TIME') });
      document.body.style.background = '#f5eedc';
    }, { score, palette });
    await page.eval(() => new Promise(requestAnimationFrame));
    const screenshot = await page.send('Page.captureScreenshot', { format: 'png' });
    writeFileSync(join(out, `notation-${width}.png`), Buffer.from(screenshot.data, 'base64'));
    checks.push({ width, behavior: 'fixed marker, continuous position, no redraw, late correction, tied feedback, repeat, manual review, reduced motion' });
    assert.equal(page.consoleErrors.length, 0, JSON.stringify(page.consoleErrors));
  }
  writeFileSync(join(out, 'browser-checks.json'), JSON.stringify(checks, null, 2));
  console.log(JSON.stringify({ passed: checks.length, fixtures: fixtures.length, widths: [320, 390, 768] }));
} finally { await browser.close(); }

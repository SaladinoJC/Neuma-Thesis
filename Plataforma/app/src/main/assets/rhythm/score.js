/* Original engraving/viewport adapter. VexFlow is vendored separately under MIT. */
(() => {
  'use strict';
  const scroller = document.getElementById('scroller');
  const host = document.getElementById('score');
  const marker = document.getElementById('marker');
  let payload, transport, layout, colors, pending = false, fontsReady = false;
  let anchors = [], heads = [], renderCount = 0, error = null;
  let lastPaint = '', lastPhase = '';

  const report = message => {
    error = message || null;
    if (window.RhythmHost) window.RhythmHost.status(payload?.score.key || '', message || '');
  };
  function beatToX(beat) {
    if (!anchors.length) return 0;
    const next = anchors.findIndex(a => a.beat > beat);
    if (next < 0) return anchors.at(-1).x;
    if (next === 0) return anchors[0].x;
    const a = anchors[next - 1], b = anchors[next];
    return a.x + (b.x - a.x) * (beat - a.beat) / (b.beat - a.beat);
  }
  function engrave() {
    if (!payload || !fontsReady) return;
    const V = window.VexFlow;
    const { score, reduced, palette } = payload;
    colors = palette;
    const viewport = scroller.clientWidth;
    if (!viewport) return;
    host.replaceChildren(); anchors = []; heads = [];
    const barWidths = score.bars.map(bar => Math.max(viewport - 24, score.meter * 82, bar.notes.length * 30 + 72));
    const anchor = Math.min(112, Math.max(72, viewport * .28));
    const total = barWidths.reduce((a, b) => a + b, 0) + anchor + viewport;
    const renderer = new V.Renderer(host, V.Renderer.Backends.SVG);
    renderer.resize(total, 160);
    const context = renderer.getContext();
    context.setFillStyle(colors.ink); context.setStrokeStyle(colors.ink);
    let x = anchor;
    for (const [barIndex, bar] of score.bars.entries()) {
      const width = barWidths[barIndex];
      // Preserve the natural B4 engraving position, displaying just its rhythmic line.
      const stave = new V.Stave(x, 28, width);
      stave.setConfigForLines([false, false, true, false, false].map(visible => ({ visible })));
      stave.setBegBarType(barIndex === 0 ? V.Barline.type.SINGLE : V.Barline.type.NONE);
      stave.setEndBarType(barIndex === score.bars.length - 1 ? V.Barline.type.END : V.Barline.type.SINGLE);
      if (barIndex === 0) stave.addTimeSignature(`${score.meter}/4`);
      stave.setContext(context).draw();
      context.setFont('Academico', 12).fillText(String(bar.index + 1), x + 4, 26);
      const notes = bar.notes.map(data => {
        const note = new V.StaveNote({ keys: ['b/4'], duration: data.duration + (data.rest ? 'r' : ''),
          stemDirection: V.Stem.UP, alignCenter: data.rest && data.duration === 'w' });
        if (data.dotted) V.Dot.buildAndAttach([note], { all: true });
        return note;
      });
      const tuplets = [], beams = [], ties = [];
      // Keep the approved cell's grouping; a rest or quarter breaks a beam.
      let group = [], owner = null;
      const flush = () => { if (group.length >= 2) beams.push(new V.Beam(group)); group = []; };
      for (const [i, data] of bar.notes.entries()) {
        if (owner !== data.occurrenceIndex) flush();
        owner = data.occurrenceIndex;
        if (!data.rest && (data.duration === '8' || data.duration === '16')) group.push(notes[i]);
        else flush();
        if (data.triplet && (i === 0 || bar.notes[i - 1].occurrenceIndex !== owner)) {
          tuplets.push(new V.Tuplet(notes.slice(i, i + 3), { numNotes: 3, notesOccupied: 2, bracketed: true, ratioed: false }));
        }
        if (data.tie && notes[i + 1]) ties.push(new V.StaveTie({ firstNote: notes[i], lastNote: notes[i + 1], firstIndices: [0], lastIndices: [0] }));
      }
      flush();
      const voice = new V.Voice({ numBeats: score.meter, beatValue: 4 }).setMode(V.Voice.Mode.SOFT).addTickables(notes);
      new V.Formatter().joinVoices([voice]).formatToStave([voice], stave);
      voice.draw(context, stave);
      beams.forEach(beam => beam.setContext(context).draw());
      tuplets.forEach(tuplet => tuplet.setContext(context).draw());
      ties.forEach(tie => tie.setContext(context).draw());
      for (const [i, data] of bar.notes.entries()) {
        const note = notes[i];
        // Whole-bar rests are centred graphically, but the clock still spans the entire bar.
        const headCenter = (note.getNoteHeadBeginX() + note.getNoteHeadEndX()) / 2;
        anchors.push({ beat: data.beat, x: data.rest && data.duration === 'w' ? stave.getNoteStartX() : headCenter });
        heads.push({ data, elements: [...(note.getSVGElement()?.querySelectorAll('.vf-notehead') || [])] });
      }
      x += width;
    }
    anchors.push({ beat: score.bars.length * score.meter, x: x - 8 });
    marker.style.left = `${anchor}px`; marker.style.background = colors.focus;
    layout = { anchor, reduced, viewport };
    renderCount++; lastPaint = ''; lastPhase = '';
    paint(); report('');
  }
  function paint() {
    if (!layout || !transport) return;
    const { phase, elapsedBeats, kinds } = transport;
    const active = phase === 'PLAYING' || phase === 'COUNT_IN';
    marker.style.display = active ? 'block' : 'none';
    const signature = kinds.join('|');
    if (signature !== lastPaint) {
      for (const { data, elements } of heads) {
        const kind = data.attackIndex == null ? null : kinds[data.attackIndex];
        const color = kind === 'ON_TIME' ? colors.correct : kind === 'OMITTED' ? colors.missed
          : kind === 'EARLY' || kind === 'LATE' ? colors.timing : colors.ink;
        for (const element of elements) {
          element.style.fill = color; element.style.stroke = color;
          element.querySelectorAll('*').forEach(child => { child.style.fill = color; child.style.stroke = color; });
          element.dataset.verdict = kind || 'pending';
          element.dataset.note = data.id;
        }
      }
      lastPaint = signature;
    }
    if (active) {
      const beat = Math.max(0, elapsedBeats);
      if (layout.reduced) {
        // Discrete pulse windows also cover dense bars wider than the viewport.
        scroller.scrollLeft = Math.max(0, beatToX(Math.floor(beat)) - 24);
        // With reduced motion, a moving position marker would defeat the static reading window.
        marker.style.display = 'none';
      } else scroller.scrollLeft = Math.max(0, beatToX(beat) - layout.anchor);
    } else if (phase === 'PREVIEW' && phase !== lastPhase) scroller.scrollLeft = 0;
    lastPhase = phase;
  }
  window.RhythmNotation = {
    setScore(next) {
      payload = next;
      try { engrave(); } catch (failure) { report(String(failure)); }
    },
    update(next) {
      transport = next;
      if (!pending) { pending = true; requestAnimationFrame(() => { pending = false; paint(); }); }
    },
    inspect() {
      return { ready: !!layout && !error, error, renderCount, scrollLeft: scroller.scrollLeft,
        markerX: layout?.anchor, markerVisible: marker.style.display !== 'none',
        background: getComputedStyle(host).backgroundColor, anchors,
        heads: heads.map(({ data, elements }) => {
          const bounds = elements[0]?.getBoundingClientRect();
          return { ...data, count: elements.length, centerX: bounds ? bounds.x + bounds.width / 2 : null,
            color: elements[0] ? getComputedStyle(elements[0]).fill : null };
        }) };
    }
  };
  // The bundled build embeds both fonts: no font request or CDN at runtime.
  document.fonts.ready.then(() => { fontsReady = true; if (payload) window.RhythmNotation.setScore(payload); });
  let previousWidth = 0;
  new ResizeObserver(() => {
    if (scroller.clientWidth !== previousWidth) {
      previousWidth = scroller.clientWidth;
      if (payload) window.RhythmNotation.setScore(payload);
    }
  }).observe(scroller);
})();

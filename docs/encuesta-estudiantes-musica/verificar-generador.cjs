// Verificación local sin acceder a Google ni crear archivos remotos.
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const source = fs.readFileSync(path.join(__dirname, 'crear-formularios.gs'), 'utf8');

function entorno(fallo = false) {
  const forms = [], props = new Map();
  class Item {
    constructor(type) { this.type = type; }
    setTitle(v) { this.title = v; return this; }
    setHelpText(v) { this.help = v; return this; }
    setRequired(v) { this.required = v; return this; }
    setChoiceValues(v) { this.values = v; return this; }
    setChoices(v) { this.choices = v; return this; }
    createChoice(value, target) { assert(target); return { value, target }; }
    setRows(v) { this.rows = v; return this; }
    setColumns(v) { this.columns = v; return this; }
    setValidation(v) { this.validation = v; return this; }
    setGoToPage(v) { this.navigation = v; return this; }
  }
  class Form {
    constructor(title, published) {
      this.title = title; this.published = published; this.items = [];
      this.id = String(forms.length); this.settings = {}; forms.push(this);
    }
    getId() { return this.id; }
    getItems() { return this.items; }
    getResponses() { return []; }
    isPublished() { return this.published; }
    getEditUrl() { return 'https://fake/edit/' + this.id; }
    getPublishedUrl() { return 'https://fake/view/' + this.id; }
    setConfirmationMessage(v) { this.confirmation = v; return this; }
  }
  for (const method of ['setDescription', 'setCollectEmail', 'setLimitOneResponsePerUser',
    'setAllowResponseEdits', 'setPublishingSummary', 'setShowLinkToRespondAgain',
    'setShuffleQuestions', 'setProgressBar', 'setCustomClosedFormMessage', 'setAcceptingResponses']) {
    Form.prototype[method] = function(v) { this.settings[method] = v; return this; };
  }
  // Contrato del rechazo comunicado por el usuario: el mensaje de cierre
  // falla al actualizar un formulario recién creado sin publicar.
  Form.prototype.setCustomClosedFormMessage = function(v) {
    if (!this.published) throw Error('Datos no válidos al actualizar el formulario.');
    this.settings.setCustomClosedFormMessage = v; return this;
  };
  for (const [method, type] of Object.entries({ addPageBreakItem: 'page',
    addSectionHeaderItem: 'header', addMultipleChoiceItem: 'option',
    addCheckboxItem: 'checkbox', addGridItem: 'grid', addTextItem: 'text',
    addParagraphTextItem: 'paragraph' })) {
    Form.prototype[method] = function() {
      if (fallo && this.id === '1') throw Error('fallo simulado');
      const item = new Item(type); this.items.push(item); return item;
    };
  }
  const builder = () => ({
    requireSelectAtMost(v) { this.max = v; return this; },
    requireTextIsEmail() { this.email = true; return this; },
    setHelpText(v) { this.help = v; return this; },
    build() { return { ...this }; }
  });
  const context = vm.createContext({
    FormApp: { create: (t, p) => new Form(t, p), openById: id => forms.find(f => f.id === id),
      PageNavigationType: { SUBMIT: 'SUBMIT' }, createCheckboxValidation: builder,
      createTextValidation: builder },
    PropertiesService: { getScriptProperties: () => ({
      getProperty: k => props.get(k) || null, setProperty: (k, v) => props.set(k, v) }) },
    LockService: { getScriptLock: () => ({ waitLock() {}, releaseLock() {} }) },
    console: { log() {} }
  });
  vm.runInContext(source, context);
  return { forms, props, context, run: () => vm.runInContext('crearFormularios()', context),
    spec: vm.runInContext('ENCUESTA', context) };
}

const env = entorno(); env.run(); assert.equal(env.forms.length, 2);
for (const form of env.forms) {
  assert.equal(form.published, false);
  for (const key of ['setAcceptingResponses', 'setCollectEmail', 'setLimitOneResponsePerUser',
    'setAllowResponseEdits', 'setPublishingSummary']) assert.equal(form.settings[key], false, key);
}
const [contacto, encuesta] = env.forms;
const q = id => encuesta.items.find(i => i.title?.startsWith(id + ' —'));
// Convertir arrays de otro contexto de VM antes de comparar sus prototipos.
assert.deepEqual(Array.from(q('F0').choices, c => c.target.title),
  ['Tu experiencia musical', 'Gracias por tu interés']);
assert.deepEqual(Array.from(q('P11').choices, c => c.target.title),
  ['Tu experiencia con aplicaciones de ejercicios', 'Tu experiencia con aplicaciones de ejercicios',
    'Qué te gustaría trabajar', 'Qué te gustaría trabajar']);
assert.equal(encuesta.items.at(-1).navigation, 'SUBMIT');
for (const id of ['P09', 'P13', 'P14']) assert.equal(q(id).validation.max, 3);
assert.equal(q('P07').rows.length, 6); assert.equal(q('P07').required, false);
assert(encuesta.confirmation.includes(contacto.getPublishedUrl()));
assert.equal(contacto.items.length, 2); assert.equal(contacto.items[1].validation.email, true);
assert.equal(encuesta.items.filter(i => !['page', 'header'].includes(i.type)).length, 17);
const snapshot = JSON.stringify(env.forms);
env.run(); assert.equal(env.forms.length, 2); assert.equal(JSON.stringify(env.forms), snapshot);
const parcial = entorno(true);
assert.throws(parcial.run, /fallo simulado/);
assert.equal(parcial.props.get('NEUMA_ENCUESTA_ESTADO'), 'iniciado');
assert.throws(parcial.run, /creación parcial/); assert.equal(parcial.forms.length, 2);
// Estado heredado del error original: contacto creado, configurado y vacío;
// todavía no existe la encuesta. La nueva ejecución debe reutilizar ese contacto.
const recuperable = entorno();
vm.runInContext(`
  const configurarAntesDelFallo = configurar_;
  configurar_ = function(form, descripcion) {
    configurarAntesDelFallo(form, descripcion);
    form.setCustomClosedFormMessage('Mensaje de cierre');
  };
`, recuperable.context);
assert.throws(recuperable.run, /Datos no válidos al actualizar el formulario/);
assert.equal(recuperable.forms.length, 1);
const idContactoOriginal = recuperable.props.get('NEUMA_CONTACTO_ID');
vm.runInContext('configurar_ = configurarAntesDelFallo;', recuperable.context);
recuperable.run();
assert.equal(recuperable.forms.length, 2);
assert.equal(recuperable.props.get('NEUMA_CONTACTO_ID'), idContactoOriginal);
assert.equal(recuperable.forms[0].items.length, 2);
assert.equal(recuperable.props.get('NEUMA_ENCUESTA_ESTADO'), 'completo');

// Si alguien añadió contenido al borrador, el generador debe conservarlo.
const intervenido = entorno();
vm.runInContext(`
  const configurarAntesDeIntervenir = configurar_;
  configurar_ = function(form, descripcion) {
    configurarAntesDeIntervenir(form, descripcion);
    form.setCustomClosedFormMessage('Mensaje de cierre');
  };
`, intervenido.context);
assert.throws(intervenido.run, /Datos no válidos/);
intervenido.forms[0].addTextItem().setTitle('Pregunta agregada manualmente');
const contenidoManual = JSON.stringify(intervenido.forms);
vm.runInContext('configurar_ = configurarAntesDeIntervenir;', intervenido.context);
assert.throws(intervenido.run, /creación parcial/);
assert.equal(JSON.stringify(intervenido.forms), contenidoManual);
const ids = env.spec.secciones.flatMap(s => s.preguntas.map(p => p.id));
assert.equal(new Set(ids).size, 17);
const md = fs.readFileSync(path.join(__dirname, 'cuestionario.md'), 'utf8');
for (const seccion of env.spec.secciones) for (const p of seccion.preguntas) {
  assert(md.includes(p.id + ' — ' + p.titulo));
  for (const opcion of p.opciones || []) assert(md.includes(opcion));
}
console.log('OK: borradores sin publicar, privacidad, saltos, 17 preguntas, límites de 3, contacto separado, repetición sin duplicar, recuperación del fallo original, conservación de contenido manual y correspondencia con Markdown.');
console.log('Simulación local: no acredita ejecución ni renderizado en Google.');

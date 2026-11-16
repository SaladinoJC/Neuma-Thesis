/**
 * Neuma — relevamiento exploratorio, versión 1, 2026-11-03.
 * Ejecutar crearFormularios() en un proyecto independiente de Google Apps Script.
 * Crea dos borradores sin publicar. No envía mensajes ni vincula respuestas.
 * La especificación es la fuente del cuestionario Markdown que acompaña este archivo.
 */
const ENCUESTA = {
  titulo: 'Aprendizaje musical y práctica del oído',
  introduccion: [
    'Soy Juan Cruz Saladino, estudiante de Ingeniería en Informática de la Universidad Nacional de Mar del Plata. Como parte de mi Trabajo Final, quiero conocer cómo practican el oído las personas que aprenden música y qué dificultades encuentran.',
    'Esta encuesta está dirigida a personas que actualmente aprenden o practican música, con clases o por su cuenta, de cualquier nivel. No necesitás conocer teoría musical. No es un examen y no hay respuestas correctas o incorrectas.',
    'Por práctica del oído entendemos actividades como reconocer sonidos o instrumentos, sacar una melodía de oído, repetir un ritmo o cantar una nota escuchada.',
    'La duración estimada es de 5 a 7 minutos; todavía debe comprobarse en una prueba piloto. Participar es voluntario. Podés cerrar el formulario antes de enviarlo y omitir las preguntas que no sean obligatorias.',
    'No se solicitan nombre, correo, institución ni ubicación. Evitá incluir datos que te identifiquen en los campos abiertos. Las respuestas se analizarán de forma agrupada para orientar el desarrollo y se utilizarán en el Trabajo Final. No se publicarán las respuestas individuales.',
    'Una vez enviada una respuesta, no podré identificar cuál es la tuya para retirarla. Al final habrá un enlace independiente y opcional para recibir una invitación a probar la aplicación; el contacto no se asociará con tus respuestas.',
    'Consultas sobre el relevamiento: [COMPLETAR correo de Juan Cruz Saladino]. Acceso a los datos y plazo de conservación: [COMPLETAR antes de difundir].',
    'Participación de menores: [COMPLETAR información y procedimiento acordados con el director antes de difundir; no reemplazar por una simple declaración de aceptación].'
  ].join('\n\n'),
  secciones: [
    { id: 'inicio', titulo: 'Antes de empezar', preguntas: [
      { id: 'C0', tipo: 'casillas', obligatoria: true, titulo: 'Participación voluntaria',
        ayuda: 'Si no querés participar, cerrá el formulario sin enviarlo.',
        opciones: ['Leí la información anterior y quiero participar.'] },
      { id: 'F0', tipo: 'opcion', obligatoria: true, titulo: '¿Actualmente aprendés o practicás música, con clases o por tu cuenta?',
        opciones: ['Sí', 'No'], destinos: ['perfil', 'fuera'] }
    ] },
    { id: 'perfil', titulo: 'Tu experiencia musical', preguntas: [
      { id: 'P01', tipo: 'opcion', obligatoria: false, titulo: '¿En qué grupo de edad estás?',
        opciones: ['Menos de 13 años', '13–17 años', '18–24 años', '25–34 años', '35–44 años', '45 años o más', 'Prefiero no responder'] },
      { id: 'P02', tipo: 'casillas', obligatoria: true, titulo: '¿De qué maneras aprendés música actualmente?',
        ayuda: 'Podés marcar varias. No indiques nombres de instituciones o docentes.',
        opciones: ['Conservatorio o carrera musical de nivel superior', 'Escuela o academia de música, fuera de una carrera superior', 'Clases particulares', 'Talleres o actividades grupales', 'Por mi cuenta, con videos, cursos, aplicaciones u otros materiales', 'Otra modalidad'] },
      { id: 'P03', tipo: 'opcion', obligatoria: true, titulo: '¿Cuánto tiempo de aprendizaje o práctica musical acumulás aproximadamente?',
        ayuda: 'Si tuviste pausas largas, intentá no contarlas.',
        opciones: ['Menos de 6 meses', 'Desde 6 meses hasta menos de 1 año', 'Desde 1 año hasta menos de 3 años', 'Desde 3 años hasta menos de 6 años', '6 años o más', 'No sé estimarlo'] },
      { id: 'P04', tipo: 'casillas', obligatoria: true, titulo: '¿Qué practicás actualmente?',
        ayuda: 'Podés marcar varias opciones.',
        opciones: ['Canto o uso de la voz', 'Instrumentos de teclado, como piano', 'Instrumentos de cuerda pulsada, como guitarra o bajo', 'Instrumentos de cuerda frotada, como violín', 'Instrumentos de viento', 'Percusión', 'Producción o composición con medios digitales', 'Otra práctica musical', 'Todavía no elegí un instrumento o práctica principal'] }
    ] },
    { id: 'practica', titulo: 'Cómo practicás el oído', preguntas: [
      { id: 'P05', tipo: 'opcion', obligatoria: true, titulo: 'En las últimas 4 semanas, ¿cuántos días hiciste actividades para practicar el oído?',
        ayuda: 'Contá también actividades dentro de clases o ensayos, como sacar una melodía de oído, repetir ritmos o reconocer sonidos. No cuentes solamente escuchar música sin realizar una actividad.',
        opciones: ['Ningún día', 'Entre 1 y 3 días en total', 'Entre 4 y 7 días en total', 'Entre 8 y 15 días en total', 'Entre 16 y 28 días en total', 'No lo recuerdo'] },
      { id: 'P06', tipo: 'casillas', obligatoria: true, titulo: '¿Cuáles de estas actividades hiciste en esas últimas 4 semanas?',
        ayuda: 'Podés marcar varias. Si elegís «Ninguna» o «No lo recuerdo», marcá solamente esa opción.',
        opciones: ['Reconocer qué instrumento suena', 'Repetir o escribir un ritmo escuchado', 'Sacar una melodía de oído', 'Reconocer la distancia entre dos notas (intervalos)', 'Reconocer acordes por cómo suenan', 'Cantar o tocar una nota para igualar otra que escuché', 'Otra actividad de práctica auditiva', 'Ninguna de estas actividades', 'No lo recuerdo'] },
      { id: 'P07', tipo: 'matriz', obligatoria: false, titulo: 'Según tu experiencia, ¿cuánta dificultad te presenta cada actividad?',
        ayuda: 'No hace falta que las hayas hecho recientemente. Si nunca probaste una actividad o no entendés a qué se refiere, elegí la opción correspondiente. Podés dejar filas sin responder.',
        filas: ['Reconocer qué instrumento suena', 'Repetir un ritmo escuchado', 'Sacar una melodía de oído', 'Reconocer la distancia entre dos notas (intervalos)', 'Reconocer acordes por cómo suenan', 'Cantar una nota para igualar otra que escuché'],
        columnas: ['Ninguna', 'Poca', 'Bastante', 'Mucha', 'No lo intenté', 'No entiendo la actividad'] },
      { id: 'P08', tipo: 'casillas', obligatoria: true, titulo: 'Cuando hacés una actividad de oído, ¿cómo comprobás si tu respuesta fue correcta?',
        ayuda: 'Podés marcar varias. Si elegís una de las dos últimas opciones, marcá solamente esa.',
        opciones: ['Me corrige un docente u otra persona', 'Una aplicación o herramienta me indica el resultado', 'Comparo con una respuesta, partitura o grabación de referencia', 'Uso un instrumento para comprobarlo', 'Me guío por lo que me parece al escuchar', 'No tengo una forma de comprobarlo', 'No hago estas actividades'] },
      { id: 'P09', tipo: 'casillas', obligatoria: true, maximo: 3, titulo: '¿Qué te dificulta practicar el oído con la frecuencia que te gustaría?',
        ayuda: 'Elegí hasta 3. Si elegís «No encuentro dificultades» o «No me interesa practicarlo actualmente», marcá solamente esa opción.',
        opciones: ['Falta de tiempo', 'No sé por dónde empezar o qué practicar', 'Me cuesta encontrar ejercicios adecuados a mi nivel', 'Me cuesta saber si respondo correctamente', 'Me cuesta mantener la constancia', 'Los ejercicios me resultan repetitivos o poco interesantes', 'Me faltan materiales, herramientas o un lugar adecuado', 'Otra dificultad', 'No encuentro dificultades para practicarlo', 'No me interesa practicarlo actualmente'] },
      { id: 'P10', tipo: 'casillas', obligatoria: true, titulo: 'En las últimas 4 semanas, ¿qué recursos usaste para practicar el oído?',
        ayuda: 'Podés marcar varias. Si elegís «Ninguno» o «No lo recuerdo», marcá solamente esa opción.',
        opciones: ['Clases o ejercicios indicados por un docente', 'Práctica con compañeros u otras personas', 'Videos, cursos o tutoriales', 'Libros, apuntes o partituras con ejercicios', 'Canciones o grabaciones elegidas por mí', 'Aplicaciones o sitios web con ejercicios auditivos', 'Afinador u otras herramientas de audio', 'Otros recursos', 'Ninguno', 'No lo recuerdo'] },
      { id: 'P11', tipo: 'opcion', obligatoria: true, titulo: '¿Usaste alguna vez aplicaciones o sitios web con ejercicios para entrenar el oído?',
        ayuda: 'Por ejemplo, ejercicios para reconocer intervalos, ritmos o acordes. Un afinador usado por sí solo no cuenta como una aplicación de ejercicios.',
        opciones: ['Sí, los uso actualmente', 'Sí, los usé antes, pero ahora no', 'No, nunca', 'No lo recuerdo'],
        destinos: ['aplicaciones', 'aplicaciones', 'prioridades', 'prioridades'] }
    ] },
    { id: 'aplicaciones', titulo: 'Tu experiencia con aplicaciones de ejercicios', preguntas: [
      { id: 'P12', tipo: 'texto', obligatoria: false, titulo: '¿Cuáles usaste?',
        ayuda: 'Podés escribir sus nombres si los recordás. No incluyas usuarios, correos ni datos personales.' },
      { id: 'P13', tipo: 'casillas', obligatoria: false, maximo: 3, titulo: '¿Qué dificultades encontraste al usar esas aplicaciones o sitios?',
        ayuda: 'Elegí hasta 3. Si elegís «Ninguna» o «No lo recuerdo», marcá solamente esa opción.',
        opciones: ['No entendía bien las consignas', 'La dificultad era demasiado alta', 'La dificultad era demasiado baja', 'Las devoluciones no me ayudaban a entender mis errores', 'El contenido que necesitaba era de pago', 'Me costaba mantener el interés o la constancia', 'Tenía problemas técnicos o de acceso', 'Faltaban actividades que quería practicar', 'Otra dificultad', 'Ninguna', 'No lo recuerdo'] }
    ] },
    { id: 'prioridades', titulo: 'Qué te gustaría trabajar', preguntas: [
      { id: 'P14', tipo: 'casillas', obligatoria: true, maximo: 3, titulo: '¿Qué te gustaría mejorar en tu práctica del oído?',
        ayuda: 'Elegí hasta 3 prioridades. Si elegís «No sé todavía» o «No me interesa trabajarlo actualmente», marcá solamente esa opción.',
        opciones: ['Reconocer instrumentos por su sonido', 'Escuchar y reproducir ritmos', 'Sacar melodías de oído', 'Reconocer intervalos', 'Reconocer acordes', 'Afinar la voz al cantar', 'Relacionar lo que escucho con la escritura musical', 'Otro aspecto', 'No sé todavía', 'No me interesa trabajarlo actualmente'] },
      { id: 'P15', tipo: 'parrafo', obligatoria: false, titulo: '¿Hay alguna dificultad o necesidad de tu práctica del oído que no apareció en las preguntas?',
        ayuda: 'Si querés, contala brevemente. No incluyas nombres, correos, instituciones ni otros datos que te identifiquen.' }
    ] },
    { id: 'fuera', titulo: 'Gracias por tu interés', descripcion: 'Este relevamiento está dirigido a personas que actualmente aprenden o practican música. Si no es tu caso, podés cerrar el formulario sin enviarlo. Si lo enviás, tu respuesta no se incluirá en el análisis de necesidades.', preguntas: [] }
  ]
};

const CONTACTO = {
  titulo: 'Neuma — invitación a futuras pruebas',
  introduccion: [
    'Estoy desarrollando Neuma, una aplicación Android de entrenamiento auditivo para mi Trabajo Final de Ingeniería en Informática en la Universidad Nacional de Mar del Plata.',
    'Si querés, podés dejar un correo para recibir una invitación cuando haya un prototipo estable disponible para probar. Este formulario es independiente de la encuesta y no solicita datos que permitan vincularte con sus respuestas.',
    'Dejar tu correo es opcional y no significa aceptar una prueba. Una eventual invitación explicará las actividades y condiciones; podrás decidir en ese momento.',
    'Usaré el correo únicamente para esta convocatoria. Para solicitar su eliminación: [COMPLETAR correo de Juan Cruz Saladino]. Acceso y plazo de conservación: [COMPLETAR antes de difundir].',
    'Contacto de menores: [COMPLETAR procedimiento acordado con el director; precisar si corresponde el contacto de una persona adulta responsable]. No difundas este borrador hasta resolverlo.'
  ].join('\n\n'),
  preguntas: [
    { id: 'K01', tipo: 'casillas', obligatoria: true, titulo: 'Autorización de contacto', opciones: ['Quiero recibir una invitación por correo cuando haya un prototipo estable disponible para probar, según la información anterior.'] },
    { id: 'K02', tipo: 'email', obligatoria: true, titulo: 'Correo de contacto', ayuda: 'Revisá que esté bien escrito. No hace falta dejar tu nombre.' }
  ]
};

function crearFormularios() {
  const lock = LockService.getScriptLock();
  lock.waitLock(1000);
  try {
    const propiedades = PropertiesService.getScriptProperties();
    const estado = propiedades.getProperty('NEUMA_ENCUESTA_ESTADO');
    if (estado === 'completo') {
      mostrarEnlaces_(propiedades);
      return; // Una segunda ejecución no duplica ni modifica formularios existentes.
    }
    const contactoId = propiedades.getProperty('NEUMA_CONTACTO_ID');
    const encuestaId = propiedades.getProperty('NEUMA_ENCUESTA_ID');
    let contactoPendiente = null;
    if (estado || encuestaId || contactoId) {
      // Recuperación del fallo de configuración de la primera versión:
      // solo reutilizar el contacto registrado si sigue vacío y sin publicar.
      if (estado === 'iniciado' && contactoId && !encuestaId) {
        const candidato = FormApp.openById(contactoId);
        if (!candidato.isPublished() && candidato.getItems().length === 0 && candidato.getResponses().length === 0) {
          contactoPendiente = candidato;
        }
      }
      if (!contactoPendiente) {
        mostrarEnlaces_(propiedades);
        throw new Error('Existe una creación parcial con contenido o que no se puede recuperar automáticamente. Revisá los borradores indicados en el registro. No se sobrescribieron ni duplicaron.');
      }
    }
    propiedades.setProperty('NEUMA_ENCUESTA_ESTADO', 'iniciado');

    const contacto = contactoPendiente || FormApp.create(CONTACTO.titulo, false);
    propiedades.setProperty('NEUMA_CONTACTO_ID', contacto.getId());
    configurar_(contacto, CONTACTO.introduccion);
    CONTACTO.preguntas.forEach(p => agregarPregunta_(contacto, p));
    contacto.setConfirmationMessage('Gracias. Cuando haya una convocatoria disponible, podré escribirte al correo que dejaste.');

    const encuesta = FormApp.create(ENCUESTA.titulo, false);
    propiedades.setProperty('NEUMA_ENCUESTA_ID', encuesta.getId());
    configurar_(encuesta, ENCUESTA.introduccion);
    const paginas = {};
    const ramificaciones = [];
    ENCUESTA.secciones.forEach((seccion, indice) => {
      if (indice > 0) {
        const pagina = encuesta.addPageBreakItem().setTitle(seccion.titulo);
        if (seccion.descripcion) pagina.setHelpText(seccion.descripcion);
        paginas[seccion.id] = pagina;
      } else {
        encuesta.addSectionHeaderItem().setTitle(seccion.titulo);
      }
      seccion.preguntas.forEach(p => {
        const item = agregarPregunta_(encuesta, p);
        if (p.destinos) ramificaciones.push({ item: item, pregunta: p });
      });
    });
    ramificaciones.forEach(r => r.item.setChoices(r.pregunta.opciones.map((opcion, i) =>
      r.item.createChoice(opcion, paginas[r.pregunta.destinos[i]])
    )));
    // PageBreakItem.setGoToPage controla el final de la sección ANTERIOR.
    // Por eso se coloca en «fuera»: prioridades termina enviando, sin pasar a fuera.
    paginas.fuera.setGoToPage(FormApp.PageNavigationType.SUBMIT);
    encuesta.setConfirmationMessage(
      '¡Gracias por participar! Tus respuestas ayudarán a orientar el desarrollo de mi Trabajo Final.\n\n' +
      'Estoy desarrollando Neuma, una aplicación Android de entrenamiento auditivo. Si querés recibir una invitación cuando haya un prototipo estable para probar, podés dejar un correo en este formulario independiente y opcional:\n' +
      contacto.getPublishedUrl() + '\n\nTu contacto no se asociará con las respuestas de esta encuesta.'
    );
    propiedades.setProperty('NEUMA_ENCUESTA_ESTADO', 'completo');
    mostrarEnlaces_(propiedades);
  } finally {
    lock.releaseLock();
  }
}

function configurar_(form, descripcion) {
  form.setDescription(descripcion)
    .setCollectEmail(false)
    .setLimitOneResponsePerUser(false)
    .setAllowResponseEdits(false)
    .setPublishingSummary(false)
    .setShowLinkToRespondAgain(false)
    .setShuffleQuestions(false)
    .setProgressBar(true);
  // No personalizar el mensaje de cierre durante la creación sin publicar:
  // Google rechazó esa actualización en la ejecución reportada por el usuario.
  // No se usa setRequireLogin: está deprecado. Comprobar acceso externo en la interfaz.
  form.setAcceptingResponses(false);
}

function agregarPregunta_(form, p) {
  let item;
  if (p.tipo === 'opcion') item = form.addMultipleChoiceItem().setChoiceValues(p.opciones);
  else if (p.tipo === 'casillas') {
    item = form.addCheckboxItem().setChoiceValues(p.opciones);
    if (p.maximo) item.setValidation(FormApp.createCheckboxValidation()
      .requireSelectAtMost(p.maximo).setHelpText('Elegí como máximo ' + p.maximo + ' opciones.').build());
  } else if (p.tipo === 'matriz') item = form.addGridItem().setRows(p.filas).setColumns(p.columnas);
  else if (p.tipo === 'texto' || p.tipo === 'email') {
    item = form.addTextItem();
    if (p.tipo === 'email') item.setValidation(FormApp.createTextValidation()
      .requireTextIsEmail().setHelpText('Escribí una dirección de correo válida.').build());
  } else if (p.tipo === 'parrafo') item = form.addParagraphTextItem();
  else throw new Error('Tipo de pregunta no soportado: ' + p.tipo);
  item.setTitle(p.id + ' — ' + p.titulo).setRequired(p.obligatoria);
  if (p.ayuda) item.setHelpText(p.ayuda);
  return item;
}

function mostrarEnlaces_(propiedades) {
  [['Encuesta', 'NEUMA_ENCUESTA_ID'], ['Contacto', 'NEUMA_CONTACTO_ID']].forEach(par => {
    const id = propiedades.getProperty(par[1]);
    if (!id) return;
    const form = FormApp.openById(id);
    console.log(par[0] + ' (editar): ' + form.getEditUrl());
    console.log(par[0] + ' (responder; funcionará después de publicar y abrir): ' + form.getPublishedUrl());
  });
}

# Encuesta de necesidades para Neuma

Versión 1 — 2026-11-03. Cuestionario preparado tras la entrevista `grilling` y la
confirmación final «Perfecto» del usuario. Generador corregido tras un fallo de
la primera ejecución en Google comunicado por el usuario el 2026-11-03; creación
completa y publicación no confirmadas. Sin respuestas ni piloto informados.

## Material preparado

- [Cuestionario completo](cuestionario.md): introducción, opciones, obligatoriedad,
  saltos, cierre y formulario de contacto separado.
- [Generador para Google Apps Script](crear-formularios.gs): contiene la
  especificación del instrumento y crea dos formularios sin publicar. No envía
  mensajes, no crea hojas compartidas y no solicita acceso a Gmail.

El formulario principal tiene **17 preguntas**, incluyendo aceptación y filtro;
una es una matriz opcional de seis actividades. Quien nunca usó aplicaciones
omite dos preguntas. Hay dos campos abiertos opcionales: nombres de aplicaciones
y necesidades no contempladas. La duración de **5–7 minutos es un objetivo de
diseño, no una medición**. La introducción y la matriz deben comprobarse en móvil.

## Crear los formularios en tu cuenta

No se encontró una integración conectada con escritura en Google Forms ni una
sesión de documentos que permita hacerlo desde esta conversación. La búsqueda de
integraciones devolvió Drive/Docs/Sheets/Slides, sin una herramienta de Forms.
El trabajo de redacción y automatización está preparado; la ejecución en tu cuenta
requiere estos pasos:

1. Abrí [Google Apps Script](https://script.google.com/) con la cuenta que será
   propietaria de los formularios y creá un proyecto independiente nuevo.
2. Abrí `crear-formularios.gs`, copiá todo su contenido y pegalo en `Código.gs`
   reemplazando el ejemplo inicial. Guardá el proyecto como «Encuesta Neuma».
3. Seleccioná la función `crearFormularios` y pulsá **Ejecutar**. Revisá y concedé
   los permisos de Forms solicitados por Google para este código. No requiere
   contraseñas dentro del script ni credenciales pegadas en la conversación.
4. En el registro de ejecución aparecerán los enlaces para editar cada formulario.
   Abrilos y revisá los textos. Ambos quedan **sin publicar y sin aceptar
   respuestas**. Los enlaces de respuesta no estarán habilitados todavía.
5. Completá los campos `[COMPLETAR]` en las descripciones de ambos formularios:
   correo de consultas/eliminación del contacto, personas con acceso, plazo de
   conservación e información/procedimiento de participación de menores acordado
   con el director. Este borrador no acredita aprobación institucional ni resuelve
   ese procedimiento por sí mismo.
6. Revisá los recorridos y la configuración con la lista siguiente. Después de
   resolver lo anterior, publicá/abrí para un piloto acotado. Para la difusión
   general, publicá y habilitá también el formulario de contacto, cuyo enlace
   ya estará incluido en el mensaje final de la encuesta.

### Si falló en `configurar_`, línea 165 de la primera versión

La primera versión llamó `setCustomClosedFormMessage` sobre el contacto recién
creado sin publicar. El usuario informó «Datos no válidos al actualizar el
formulario» precisamente en esa llamada, antes de crear preguntas. Se retiró
esa personalización opcional; no es necesaria para mantener el borrador sin
publicar. El rechazo se constató en la ejecución del usuario, no en una sesión
remota controlada por el agente.

1. En **el mismo proyecto** de Apps Script, reemplazá todo `Código.gs` por el
   contenido actualizado de `crear-formularios.gs` y guardá.
2. Volvé a ejecutar `crearFormularios`. No borres las propiedades del proyecto ni
   los formularios: el script utiliza el identificador del contacto que quedó
   registrado y lo reutiliza si sigue sin publicar, sin preguntas y sin respuestas.
3. Abrí los dos enlaces de edición que se imprimen cuando finaliza la ejecución.

Si ya agregaste preguntas al contacto o el estado corresponde a otro fallo
parcial, el script se detiene y muestra los enlaces disponibles para conservar
ese trabajo. No lo vacía ni lo reemplaza.

El script usa el estado de publicación de la [API oficial de
Forms](https://developers.google.com/apps-script/reference/forms/form-app#createtitle,-ispublished).
Una segunda ejecución en **el mismo proyecto de Apps Script** muestra los enlaces
existentes sin duplicar ni modificar los formularios. Otro proyecto genera otro
par. Si una ejecución falla a mitad de camino, conserva los identificadores:
puede recuperar el contacto vacío del error descrito arriba; ante contenido u
otro estado parcial se detiene para revisar, sin borrar ni reconstruir preguntas.
Modificar el código y volver a ejecutar no actualiza formularios ya creados;
editá los borradores manualmente y conservá esos cambios en la versión local.

## Configuración y recorridos a comprobar

- Recolección automática de correo desactivada en ambos formularios. Solo el
  formulario de contacto solicita una dirección mediante una pregunta explícita.
- Sin limitar a una respuesta por usuario ni exigir inicio de sesión. Verificar
  las restricciones de acceso/dominio de la cuenta desde la interfaz y probar
  desde una ventana privada sin iniciar sesión. El script no utiliza el método
  de acceso deprecado de Google.
- Sin publicación del resumen de respuestas y sin enlaces de edición posterior.
  Acceso a respuestas limitado a las personas que se indiquen en la introducción.
- Quien no quiera participar puede cerrar sin enviar; la aceptación es necesaria
  para continuar. La aceptación no sustituye el procedimiento pendiente de menores.
- F0 = «No» conduce a un cierre de inelegibilidad; no debe mostrar perfil ni
  preguntas de práctica. Si aun así se envía, se excluye del análisis.
- F0 = «Sí» recorre perfil y práctica. P11 = uso actual o pasado muestra P12/P13;
  «No, nunca» o «No lo recuerdo» pasa directamente a prioridades.
- Después de prioridades se envía, sin mostrar la página de inelegibilidad. La
  navegación al terminar una sección se implementa según la [referencia de
  PageBreakItem](https://developers.google.com/apps-script/reference/forms/page-break-item#setgotopagenavigationtype).
- En P09, P13 y P14 no se permiten más de tres selecciones. Las opciones de
  exclusión («Ninguna», «No sé», etc.) se indican en la consigna; Google Forms no
  impide sus combinaciones con otras opciones mediante la validación empleada.
- El formulario de contacto no pide nombre, edad, perfil, identificador de
  respuesta ni datos de la encuesta. No se usan enlaces precompletados,
  parámetros, códigos ni cruces por marcas de tiempo para asociar ambos archivos.
- Resolver con el director el procedimiento de contacto de menores antes de abrir
  ese formulario. El diseño de contacto no restringe por iniciativa del agente
  la población de la encuesta a mayores de edad.

## Piloto y difusión propuestos

Antes del envío general, probar con unas pocas personas de perfiles diferentes,
incluyendo alguien principiante y alguien con formación institucional. Esto es
una propuesta de ejecución, no un piloto realizado ni una cantidad aprobada.
Pedirles que indiquen términos confusos, opciones faltantes y tiempo empleado.
Si se incluye a menores en el piloto, aplicar previamente el procedimiento
acordado con el director.

Comprobar al menos en teléfono y computadora: comprensión de la introducción,
lectura de la matriz, tres rutas de navegación (sin aplicaciones, uso actual o
pasado, fuera del público), opciones excluyentes y envío/contacto. Si la matriz
resulta incómoda en móvil, convertir sus seis filas en preguntas individuales
con las mismas opciones y registrar el cambio de versión.

Separar los datos del piloto de la recolección final, por ejemplo, usando una
copia identificada como piloto. Registrar correcciones y congelar una versión
antes de difundir; no mezclar respuestas de instrumentos distintos sin indicarlo.

Canales confirmados: compañeros y colegas, Conservatorio Luis Gianneo de Mar del
Plata, Instituto de Profesorado de Arte Rodolfo Ábalos de Mar del Plata y Facultad
de Ingeniería. Son **canales previstos**, no acuerdos de difusión ni avales
institucionales. No se pregunta procedencia, país, provincia ni institución.
Usar el mismo enlace sin códigos de origen; llevar un registro externo de fechas
y canales, sin asociar cada respuesta con uno de ellos.

Texto sugerido para compartir después de la revisión y el piloto:

> Estoy haciendo mi Trabajo Final de Ingeniería en Informática en la UNMdP y
> preparé una encuesta sobre cómo practicamos el oído al aprender música.
> Si actualmente aprendés o practicás música, con clases o por tu cuenta,
> podés participar sin importar tu nivel. No se pide nombre ni correo y toma
> aproximadamente 5–7 minutos. No es un examen. ¡Gracias por ayudarme!
> [ENLACE A LA ENCUESTA]

Actualizar la duración con el piloto y añadir la indicación de participación
de menores que se acuerde antes de utilizar este mensaje. No enviarlo todavía.

## Uso de la información en la tesis

Pregunta de trabajo: **¿Qué hábitos, dificultades y necesidades de práctica
auditiva describen las personas que responden y cómo pueden orientar las
prioridades de desarrollo de Neuma?**

El alcance fue confirmado por el usuario durante Q1–Q13: participantes actuales
de cualquier modalidad/nivel y edad; edad por intervalos; sin ubicación;
anonimato y contacto separado; alcance orientativo para prioridades. La elección
de redacción, intervalos de experiencia, opciones y análisis de esta versión
es una propuesta técnica del agente para revisar mediante el piloto.

| Información | Preguntas | Uso previsto |
| --- | --- | --- |
| Inclusión y participación | C0, F0 | Determinar qué respuestas corresponden al público |
| Perfil, experiencia y práctica musical | P01–P04 | Describir participantes e interpretar diferencias |
| Frecuencia y actividades auditivas | P05–P06 | Reconocer hábitos actuales |
| Dificultad percibida | P07 | Orientar actividades y ayudas; no medir destreza real |
| Comprobación y barreras | P08–P09 | Orientar devoluciones, consignas y acceso a práctica |
| Recursos y experiencia con aplicaciones | P10–P13 | Comprender alternativas actuales y limitaciones |
| Prioridades y necesidades no previstas | P14–P15 | Revisar prioridades dentro del alcance viable |

Plan descriptivo propuesto:

1. Conservar una exportación original con acceso restringido. En una copia de
   análisis excluir F0 = «No» y registros de piloto. Documentar criterios y
   cantidades; no identificar supuestos duplicados por edad o perfil coincidente.
2. Informar cantidad de respuestas y denominador válido de cada pregunta. Separar
   ausencias, «no recuerdo», «no lo intenté» y «no entiendo». En P12/P13 el salto
   de sección es ausencia estructural; no equivale a «ninguna dificultad».
3. Para casillas, informar personas y porcentajes por opción, aclarando que la
   suma puede superar 100 %. Si se combina una alternativa excluyente con otras,
   marcar ese ítem como inconsistente y excluirlo de ese cálculo, conservando el
   resto de la respuesta. Registrar cuántos ítems resultan afectados.
4. Para la matriz, presentar cada actividad por separado. No sumar una puntuación
   de «habilidad»: dificultad autopercibida y actividades no intentadas son datos
   diferentes. Esta encuesta no es una escala validada ni una prueba auditiva.
5. Explorar diferencias por modalidad y experiencia solo si hay respuestas
   suficientes. Las modalidades pueden superponerse. Evitar tablas de cruces que
   permitan reconocer perfiles individuales, especialmente de menores.
6. Agrupar comentarios por temas y revisar datos identificatorios antes de
   incorporar ejemplos. No publicar la base individual ni hacer cruces con
   contactos, incluso mediante horarios de envío.
7. Relacionar prioridades con decisiones concretas: mantener, ajustar, incorporar
   al trabajo futuro o descartar por alcance. Registrar fecha, fundamento y
   limitaciones; una preferencia declarada no obliga a implementar una función.

Es un relevamiento exploratorio con participación voluntaria y difusión por
contactos e instituciones accesibles. No permite estimar la prevalencia de
necesidades en toda la población musical, comparar instituciones ni demostrar
eficacia pedagógica de Neuma. Las pruebas de uso del prototipo siguen siendo una
actividad distinta. No hay tamaño de muestra, período de recolección ni resultados
confirmados; registrarlos cuando se concrete la ejecución.

## Verificación realizada

Para repetir la comprobación desde la raíz del repositorio:

```powershell
node docs/encuesta-estudiantes-musica/verificar-generador.cjs
```

Se revisaron el texto, las 17 preguntas, las opciones y los saltos; se contrastaron
las llamadas de creación, publicación, validación y navegación con documentación
oficial de Google. Se ejecutó una comprobación local con una implementación
simulada de Forms: creación de dos borradores, configuración de privacidad,
destinos, límites de selección, enlace de contacto, repetición sin duplicar,
recuperación del fallo informado sin duplicar el contacto y detención ante
contenido manual u otra creación parcial. Se agregó al simulador el rechazo
reportado, por lo que la versión anterior falla y la corregida pasa. Esta
simulación no acredita ejecución en
Google, permisos de la cuenta, renderizado móvil ni duración real.

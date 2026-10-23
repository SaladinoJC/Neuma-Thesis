# Entrenamiento auditivo musical

Vocabulario del sistema de entrenamiento auditivo mediante minijuegos.

## Language

**DDA (ajuste dinámico de dificultad)**:
Ajuste de las exigencias de un minijuego a partir del desempeño del jugador. En este proyecto comprende adaptación intrapartida y progresión por desbloqueo de niveles.

**Adaptación intrapartida**:
Variación de dificultad durante una partida según el desempeño reciente. Es distinta del acceso a nuevos niveles.

**Adaptación entre partidas**:
Cambio de las exigencias de la siguiente partida a partir de resultados anteriores. En Rhythm Tap permite cambiar el tempo recomendado entre ejercicios sin alterar el tempo de una ejecución iniciada.

**Desbloqueo de nivel**:
Habilitación permanente de un nuevo nivel según el desempeño del jugador. Una posterior reducción de dificultad no retira el acceso adquirido.
_Evitar_: usar «desbloqueo» para cualquier ajuste de dificultad dentro de un nivel.

**Ronda**:
Un ejercicio completo: presentación del estímulo, respuesta del jugador y evaluación. En Simón corresponde a repetir una secuencia; en reconocimiento tímbrico, a identificar un instrumento.

**Partida**:
Unidad de juego con un inicio y un cierre común, que puede contener una o varias rondas según el minijuego. En reconocimiento tímbrico reúne una cantidad fijada de preguntas; en Simón termina al primer error; en Rhythm Tap es un único ejercicio generado que se ejecuta y evalúa.
_Evitar_: usar «partida» y «ronda» como sinónimos.

**Escucha**:
Una reproducción del ejemplo sonoro de una ronda de reconocimiento tímbrico.

**Reescucha**:
Una reproducción adicional del mismo ejemplo después de la primera escucha.

**Nivel de instrumentos**:
Etapa de reconocimiento tímbrico definida por la cercanía de los instrumentos ofrecidos como opciones. Es distinto de la ayuda que supone disponer de más escuchas.

**Longitud máxima completada**:
Mayor cantidad de notas de una secuencia repetida correctamente en una partida de Simón. Si se completan cuatro notas y se falla la secuencia de cinco, el puntaje es cuatro.
_Evitar_: «longitud máxima alcanzada» sin aclarar si la secuencia fue completada o solo presentada.

**Dominio alto**:
Calificación del desempeño producida por las reglas del DDA para un juego y un nivel determinados. Es una categoría operativa de prueba, no una afirmación validada de dominio musical.

**Partida automática**:
Partida iniciada en el nivel más alto desbloqueado, bajo la política DDA del juego. Puede contribuir a desbloquear nuevos niveles.

**Partida manual**:
Partida cuyo nivel se selecciona para practicar o experimentar. Sus resultados se registran por separado y no habilitan nuevos niveles.

**Caso simulado de DDA**:
Secuencia de resultados preparada para inspeccionar el comportamiento del motor. Sus resultados no son desempeño observado de un jugador.

**Entrenamiento de afinación de oído**:
Práctica en la que el usuario ajusta su instrumento comparándolo con una referencia repetible, sin pistas de desviación durante el ajuste, y solicita una comprobación cuando considera que está afinado. Puede incluir una clasificación previa del sonido como grave, agudo o afinado.

**Comprobación de oído**:
Etapa del entrenamiento que revela la afinación medida después de que el usuario considera terminado su ajuste de oído. Conserva el resultado del intento aunque el usuario corrija después la cuerda con ayuda visual.

**Comprobación final del instrumento**:
Segunda pasada por las cuerdas después de la afinación inicial para revisar el conjunto y corregir las que hayan quedado pendientes.

**Afinación personalizada**:
Conjunto de notas objetivo por cuerda ingresado y guardado por el usuario para afinar un instrumento.

**Criterio de afinación**:
Exigencia fija y explícita con la que se comprueba una cuerda. Normal admite una desviación mayor que Pro; estas exigencias son independientes del modo de uso del afinador y no constituyen ajuste dinámico de dificultad.

**Categoría rítmica**:
Opción de práctica asociada a una célula rítmica concreta o a sus variantes definidas. Incluye tanto patrones básicos como semicorcheas o silencios de corchea como combinaciones, por ejemplo corchea con puntillo seguida de semicorchea.
_Evitar_: separar obligatoriamente la selección del usuario en «familias» y «células».

**Enemigo de Defender**:
Objetivo musical que avanza hacia la base y se elimina mediante una respuesta correcta. Si llega sin ser eliminado, cuesta una vida; una respuesta incorrecta por sí sola no cuesta vida.

**Variante de Defender**:
Modalidad de lectura y respuesta: notas por nombre, acordes por nombre o acordes en piano virtual. Comparten la supervivencia arcade, pero conservan contenido, resultados y progreso independientes.

**Tiempo de viaje**:
Tiempo activo asignado a un enemigo desde su aparición hasta llegar a base. En Defender el DDA modifica el de futuros enemigos; los visibles conservan su tiempo asignado, excluyendo pausas.

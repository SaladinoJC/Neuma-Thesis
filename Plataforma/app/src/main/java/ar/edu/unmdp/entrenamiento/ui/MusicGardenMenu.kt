package ar.edu.unmdp.entrenamiento.ui

import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import ar.edu.unmdp.entrenamiento.BuildConfig
import ar.edu.unmdp.entrenamiento.R
import ar.edu.unmdp.entrenamiento.progress.Progress
import ar.edu.unmdp.entrenamiento.simon.Scale
import ar.edu.unmdp.entrenamiento.instruments.InstrumentLevel
import kotlin.math.roundToInt

enum class GardenSection(val label: String) { PERSONAJE("Personaje"), JUEGOS("Juegos"), AFINADOR("Afinador"), LOGROS("Logros") }

/** Navegación solo en memoria: se conserva al volver de juegos/configuración, no tras un arranque nuevo. */
class GardenMenuState : ViewModel() {
    var section by mutableStateOf(GardenSection.PERSONAJE)
    var game by mutableIntStateOf(0)
    var progressGame by mutableIntStateOf(0)
    var showProgress by mutableStateOf(false)
    var reduceMotion by mutableStateOf(false)
}

private data class GardenGame(val title: String, val category: String, val description: String, val image: Int, val tag: String)
private val gardenGames = listOf(
    GardenGame("Simón", "Memoria musical", "Escuchá una secuencia y repetila en el piano.", R.drawable.garden_simon, "game-simon"),
    GardenGame("Instrumentos", "Reconocimiento de instrumentos", "Escuchá y descubrí qué instrumento está sonando.", R.drawable.garden_instruments, "game-instruments"),
    GardenGame("Rhythm Tap", "Pulso y lectura", "Leé cuatro compases y marcá el ritmo.", R.drawable.garden_rhythm, "game-rhythm"),
    GardenGame("Defender", "Lectura musical", "Reconocé notas y acordes para proteger el árbol.", R.drawable.garden_defender, "game-defender"),
)
internal val gardenTitle = FontFamily(Font(R.font.garden_fraunces, FontWeight.SemiBold))
internal val gardenBody = FontFamily(Font(R.font.garden_nunito_sans))

@Composable
fun MusicGardenMenu(state: GardenMenuState, simon: SimonUiState, instrumentProgress: Progress,
    onPlay: (Int) -> Unit, onTuner: () -> Unit, onGuidedTuner: () -> Unit,
    onHistory: () -> Unit, onVoice: () -> Unit, onData: () -> Unit, onRetryStorage: () -> Unit,
    onTraining: () -> Unit = onTuner) {
    val systemReduced = systemReducesMotion()
    val reduced = state.reduceMotion || systemReduced
    var more by remember { mutableStateOf(false) }
    var index by remember { mutableStateOf(false) }
    val colors = lightColorScheme(primary = GardenTokens.Primary, onPrimary = GardenTokens.OnPrimary,
        background = GardenTokens.Background, surface = GardenTokens.Surface, onSurface = GardenTokens.Text,
        onBackground = GardenTokens.Text, secondaryContainer = GardenTokens.SurfaceSelected)
    MaterialTheme(colorScheme = colors, typography = Typography(
        bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontFamily = gardenBody, fontSize = 16.sp),
        bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontFamily = gardenBody, fontSize = 15.sp),
        labelLarge = MaterialTheme.typography.labelLarge.copy(fontFamily = gardenBody, fontSize = 16.sp))) {
        BoxWithConstraints(Modifier.fillMaxSize().background(GardenTokens.Background).testTag("garden-menu")) {
            Image(painterResource(R.drawable.garden_garden), null, Modifier.matchParentSize(), contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(.86f) }))
            Box(Modifier.matchParentSize().background(GardenTokens.Sage.copy(alpha = .06f)))
            val pageWidth = (maxWidth - 16.dp).coerceAtMost(500.dp)
            val minimumBookHeight = (maxHeight - 160.dp).coerceIn(500.dp, 660.dp)
            val gameIllustrationHeight = if (maxHeight < 720.dp) 178.dp else 216.dp
            val largeText = LocalConfiguration.current.fontScale >= 1.5f
            val turn = remember { Animatable(1f) }
            LaunchedEffect(state.section, state.game, state.progressGame, state.showProgress, reduced) {
                if (reduced) turn.snapTo(1f) else { turn.snapTo(0f); turn.animateTo(1f, tween(GardenTokens.CardEnterMs, easing = FastOutSlowInEasing)) }
            }
            val scroll = rememberScrollState()
            LaunchedEffect(state.section, state.game, state.progressGame) { scroll.scrollTo(0) }
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(Modifier.width(pageWidth).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(Modifier.weight(1f)) {
                        Image(painterResource(R.drawable.neuma_logo_horizontal), stringResource(R.string.app_name),
                            Modifier.widthIn(max = 200.dp).height(60.dp).testTag("neuma-brand"),
                            contentScale = ContentScale.Fit)
                    }
                    TextButton(onClick = { more = true }, modifier = Modifier.testTag("garden-more")) { Text("Más") }
                }
                Box(Modifier.width(pageWidth).padding(bottom = 24.dp)) {
                    Row(Modifier.fillMaxWidth().padding(start = pageWidth * .115f, end = pageWidth * .14f)
                        .then(if (largeText) Modifier.horizontalScroll(rememberScrollState()) else Modifier),
                        verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        GardenSection.entries.forEach { section ->
                            val selected = state.section == section
                            Box(Modifier.then(if (largeText) Modifier.width(126.dp) else Modifier.weight(if (section == GardenSection.PERSONAJE) 1.25f else 1f))
                                .height(if (selected) 76.dp else 66.dp)) {
                                Canvas(Modifier.matchParentSize()) {
                                    val shape = Path().apply { moveTo(0f, 3.dp.toPx()); lineTo(size.width * .28f, 0f)
                                        lineTo(size.width * .62f, 1.dp.toPx()); lineTo(size.width, 2.dp.toPx())
                                        lineTo(size.width, size.height); lineTo(0f, size.height); close() }
                                    drawPath(shape, if (selected) GardenTokens.Primary else GardenTokens.Sage)
                                }
                                Box(Modifier.fillMaxSize().padding(bottom = 16.dp)
                                    .testTag("garden-section-${section.name}")
                                    .selectable(selected = selected, role = Role.Tab, onClick = { state.section = section }),
                                    contentAlignment = Alignment.Center) {
                                    Text(section.label, color = if (selected) GardenTokens.OnPrimary else GardenTokens.Text,
                                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                                        maxLines = 2, lineHeight = 26.sp)
                                }
                            }
                        }
                    }
                    Box(Modifier.padding(top = 52.dp).fillMaxWidth().heightIn(min = minimumBookHeight)) {
                        NineSliceBook(Modifier.matchParentSize().graphicsLayer { rotationZ = if (reduced) 0f else (1f - turn.value) * .18f })
                        Column(Modifier.fillMaxWidth().padding(start = pageWidth * .12f, end = pageWidth * .14f,
                            top = 28.dp, bottom = 76.dp).testTag("garden-paper-content"),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Column(Modifier.fillMaxWidth().graphicsLayer {
                                alpha = if (reduced) 1f else .65f + .35f * turn.value
                                translationX = if (reduced) 0f else (1f - turn.value) * 8.dp.toPx()
                            }, horizontalAlignment = Alignment.CenterHorizontally) {
                                when (state.section) {
                                    GardenSection.PERSONAJE -> {
                                        GardenHeading("", "Personaje")
                                        GardenIllustration(R.drawable.garden_character, 270.dp)
                                        Text("Tu lugar en el jardín", color = GardenTokens.TextSecondary, textAlign = TextAlign.Center)
                                        Text("Personalización en preparación", fontSize = 13.sp, color = GardenTokens.TextSecondary,
                                            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                                    }
                                    GardenSection.JUEGOS -> {
                                        val game = gardenGames[state.game]
                                        GardenHeading(game.category, game.title)
                                        // Fit + clip dentro de la columna de papel: ningún píxel invade el borde.
                                        GardenIllustration(game.image, gameIllustrationHeight)
                                        Text(game.description, textAlign = TextAlign.Center, color = GardenTokens.TextSecondary,
                                            modifier = Modifier.padding(vertical = 12.dp))
                                        GardenPlayButton(reduced, Modifier.testTag(game.tag)) { onPlay(state.game) }
                                    }
                                    GardenSection.AFINADOR -> {
                                        GardenHeading("Escuchar y afinar", "Afinador")
                                        GardenIllustration(R.drawable.garden_instruments, 145.dp)
                                        TextButton(onClick = onTuner, modifier = Modifier.fillMaxWidth().testTag("game-tuner")) { Text("Libre") }
                                        TextButton(onClick = onGuidedTuner, modifier = Modifier.fillMaxWidth().testTag("garden-tuner-guided")) { Text("Guiado") }
                                        TextButton(onClick = onTraining, modifier = Modifier.fillMaxWidth().testTag("garden-tuner-training")) { Text("Entrenamiento de oído") }
                                    }
                                    GardenSection.LOGROS -> {
                                        GardenHeading("", "Logros")
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            FilterChip(!state.showProgress, { state.showProgress = false }, label = { Text("Medallas", fontSize = 13.sp) })
                                            FilterChip(state.showProgress, { state.showProgress = true }, label = { Text("Progreso", fontSize = 13.sp) })
                                        }
                                        if (!state.showProgress) Text("La colección de medallas estará disponible cuando se definan sus objetivos.",
                                            textAlign = TextAlign.Center, color = GardenTokens.TextSecondary, modifier = Modifier.padding(vertical = 40.dp))
                                        else GardenProgress(state.progressGame, simon.progress, instrumentProgress)
                                    }
                                }
                            }
                            if (state.section == GardenSection.JUEGOS || (state.section == GardenSection.LOGROS && state.showProgress)) {
                                val current = if (state.section == GardenSection.JUEGOS) state.game else state.progressGame
                                Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween) {
                                    PaperArrow(false, current > 0, reduced, "garden-previous") {
                                        if (state.section == GardenSection.JUEGOS) state.game-- else state.progressGame--
                                    }
                                    TextButton(onClick = { index = true }, modifier = Modifier.testTag("garden-index")) { Text("${current + 1} de 4", fontSize = 14.sp) }
                                    PaperArrow(true, current < 3, reduced, "garden-next") {
                                        if (state.section == GardenSection.JUEGOS) state.game++ else state.progressGame++
                                    }
                                }
                            }
                            simon.storageError?.let { message ->
                                Text(message, color = GardenTokens.ErrorText, modifier = Modifier.padding(top = 16.dp))
                                TextButton(onClick = onRetryStorage) { Text("Reintentar lectura") }
                            }
                        }
                    }
                }
            }
        }
        if (index) AlertDialog(onDismissRequest = { index = false }, title = { Text("Índice", fontFamily = gardenTitle) },
            text = { Column { gardenGames.forEachIndexed { number, game ->
                TextButton(onClick = { if (state.section == GardenSection.JUEGOS) state.game = number else state.progressGame = number; index = false },
                    modifier = Modifier.fillMaxWidth().testTag("garden-index-$number")) { Text(game.title) }
            } } }, confirmButton = { TextButton(onClick = { index = false }) { Text("Cerrar") } })
        if (more) AlertDialog(onDismissRequest = { more = false }, title = { Text("Más", fontFamily = gardenTitle) },
            text = { Column(Modifier.verticalScroll(rememberScrollState())) {
                TextButton(onClick = { more = false; onHistory() }, modifier = Modifier.fillMaxWidth().testTag("history-open")) { Text("Resultados") }
                TextButton(onClick = { more = false; onVoice() }, modifier = Modifier.fillMaxWidth().testTag("game-voice")) { Text("Prueba de voz") }
                TextButton(onClick = { more = false; onData() }, modifier = Modifier.fillMaxWidth().testTag("data-open")) { Text("Datos y demostración") }
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("garden-reduce-motion")
                    .toggleable(state.reduceMotion, role = Role.Switch, onValueChange = { state.reduceMotion = it }),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Reducir movimiento", modifier = Modifier.weight(1f))
                    Switch(state.reduceMotion, null)
                }
                Text("Versión ${BuildConfig.VERSION_NAME}", fontSize = 13.sp, color = GardenTokens.TextSecondary)
            } }, confirmButton = { TextButton(onClick = { more = false }, modifier = Modifier.testTag("garden-close-more")) { Text("Cerrar") } })
    }
}

@Composable private fun GardenHeading(category: String, title: String) {
    val enlarged = LocalConfiguration.current.fontScale >= 1.5f
    if (category.isNotEmpty()) Text(category, fontSize = 13.sp, color = GardenTokens.TextSecondary, textAlign = TextAlign.Center)
    Text(title, fontFamily = gardenTitle, fontSize = if (enlarged) 24.sp else if (title.length > 10) 29.sp else 36.sp,
        lineHeight = if (enlarged) 30.sp else 42.sp, maxLines = 2,
        color = GardenTokens.Primary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 2.dp, bottom = 12.dp))
}

@Composable private fun GardenIllustration(resource: Int, height: Dp) {
    Box(Modifier.fillMaxWidth().height(height).clipToBounds().testTag("garden-illustration"), contentAlignment = Alignment.Center) {
        Image(painterResource(resource), null, Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
    }
}

@Composable private fun GardenProgress(game: Int, simon: Progress, instruments: Progress) {
    GardenHeading("", gardenGames[game].title)
    val levels = when (game) { 0 -> Scale.entries.map { it.label }; 1 -> InstrumentLevel.entries.map { it.label }; else -> emptyList() }
    val unlocked = if (game == 0) simon.level else instruments.level
    if (levels.isEmpty()) Text("Práctica disponible. Los desbloqueos de este juego todavía están en preparación.", textAlign = TextAlign.Center)
    else levels.forEachIndexed { number, label -> Text("${if (number <= unlocked) "Disponible" else "Bloqueado"} · $label",
        color = GardenTokens.TextSecondary, modifier = Modifier.padding(vertical = 6.dp)) }
}

@Composable private fun GardenPlayButton(reduced: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && !reduced) .97f else 1f,
        animationSpec = tween(if (reduced) 0 else GardenTokens.PressMs), label = "Presión de Jugar")
    val atlas = ImageBitmap.imageResource(R.drawable.garden_controls)
    Box(modifier.widthIn(max = 220.dp).fillMaxWidth().heightIn(min = 60.dp)
        .graphicsLayer { scaleX = scale; scaleY = scale; translationY = if (pressed && !reduced) 1.dp.toPx() else 0f }
        .clickable(interactionSource = interactions, indication = null, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Canvas(Modifier.matchParentSize()) { drawImage(atlas, srcOffset = IntOffset(80, 150), srcSize = IntSize(1090, 290),
            dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())) }
        Text("Jugar", fontFamily = gardenTitle, fontSize = 24.sp, color = GardenTokens.Text, modifier = Modifier.padding(12.dp))
    }
}

@Composable private fun PaperArrow(next: Boolean, enabled: Boolean, reduced: Boolean, tag: String, onClick: () -> Unit) {
    val interactions = remember { MutableInteractionSource() };val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && !reduced) .94f else 1f, tween(if (reduced) 0 else GardenTokens.PressMs), label = "Presión de flecha")
    Box(Modifier.size(GardenTokens.TouchMinimum).graphicsLayer { scaleX = scale; scaleY = scale }
        .background(if (enabled) GardenTokens.SurfaceSelected else GardenTokens.DisabledSurface, RoundedCornerShape(8.dp))
        .testTag(tag).semantics { contentDescription = if (next) "Página siguiente" else "Página anterior" }
        .clickable(interactionSource = interactions, indication = null, enabled = enabled, role = Role.Button, onClick = onClick), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(22.dp)) {
            val c = if (enabled) GardenTokens.Primary else GardenTokens.DisabledText
            val direction = if (next) 1f else -1f
            val center = Offset(size.width / 2, size.height / 2)
            drawLine(c, center + Offset(-direction * size.width * .35f, 0f), center + Offset(direction * size.width * .3f, 0f), 2.5.dp.toPx())
            drawLine(c, center + Offset(direction * size.width * .3f, 0f), center + Offset(-direction * size.width * .05f, -size.height * .3f), 2.5.dp.toPx())
            drawLine(c, center + Offset(direction * size.width * .3f, 0f), center + Offset(-direction * size.width * .05f, size.height * .3f), 2.5.dp.toPx())
        }
    }
}

/** Nine-slice keeps the cover/page edges at a fixed size even with large text. */
@Composable private fun NineSliceBook(modifier: Modifier) {
    val image = ImageBitmap.imageResource(R.drawable.garden_book)
    val filter = remember { ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(.94f,0f,0f,0f,8f, 0f,.94f,0f,0f,8f, 0f,0f,.94f,0f,8f, 0f,0f,0f,1f,0f))) }
    Canvas(modifier) {
        val sx = intArrayOf(0, 96, image.width - 150, image.width)
        val sy = intArrayOf(0, 90, image.height - 210, image.height)
        val dx = intArrayOf(0, (size.width * .095f).roundToInt(), (size.width * .86f).roundToInt(), size.width.roundToInt())
        val dy = intArrayOf(0, 26.dp.toPx().roundToInt(), (size.height - 68.dp.toPx()).roundToInt(), size.height.roundToInt())
        for (x in 0..2) for (y in 0..2) drawImage(image, srcOffset = IntOffset(sx[x], sy[y]),
            srcSize = IntSize(sx[x+1] - sx[x], sy[y+1] - sy[y]), dstOffset = IntOffset(dx[x], dy[y]),
            dstSize = IntSize(dx[x+1] - dx[x], dy[y+1] - dy[y]), colorFilter = filter)
    }
}

@Composable internal fun systemReducesMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    var reduced by remember { mutableStateOf(Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f) }
    DisposableEffect(resolver) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) { reduced = Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
        }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return reduced
}

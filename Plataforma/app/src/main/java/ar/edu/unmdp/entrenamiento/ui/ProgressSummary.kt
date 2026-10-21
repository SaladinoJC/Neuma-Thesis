package ar.edu.unmdp.entrenamiento.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.ui.unit.sp
import ar.edu.unmdp.entrenamiento.progress.Progress

@Composable
internal fun ProgressSummary(progress: Progress, levels: List<String>) {
    Column {
        Text("Desbloqueado: ${levels[progress.level]} · Racha ${progress.streak}/2", color = Muted, fontSize = 11.sp)
        Text(if (progress.level == levels.lastIndex) "Último nivel del juego" else
            "Próximo: ${levels[progress.level+1]} · Faltan ${2-progress.streak} partidas de dominio alto", color = Muted, fontSize = 11.sp)
    }
}

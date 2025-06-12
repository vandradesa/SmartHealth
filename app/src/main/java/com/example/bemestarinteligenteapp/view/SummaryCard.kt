package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemestarinteligenteapp.util.formatLocalDateTime
import java.time.Instant
import java.time.LocalDate
import androidx.compose.ui.text.style.TextAlign
import com.example.bemestarinteligenteapp.view.SummaryCard

@Composable
fun SummaryCard(
    title: String,
    valueText: String?,
    unit: String = "",
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var cardModifier = modifier // Começa com o modifier passado de fora (para layout, ex: weight)
        .width(165.dp)
        .height(250.dp)

    // Adiciona o .clickable SOMENTE se uma função onClick for fornecida
    if (onClick != null) {
        cardModifier = cardModifier.clickable(onClick = onClick)
    }

    // Adiciona o padding que você tinha (atenção à ordem dos modifiers)
    // Se o padding é para o frame do card, e não para o conteúdo interno,
    // esta ordem pode ser o que você quer, mas geralmente padding interno é mais comum.
    cardModifier = cardModifier.padding(
        top = 4.dp,
        start = 4.dp,
        end = 4.dp,
        bottom = 4.dp
    )

    Card(
        modifier = cardModifier, // Usa o modifier construído
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding INTERNO para o conteúdo do Column (você tinha 24.dp, ajuste se necessário)
            verticalArrangement = Arrangement.Top, // Você tinha Top, mantive. Considere Center para melhor visual.
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                title, fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            when {
                valueText != null -> {
                    Text(
                        text = if (unit.isNotEmpty()) "$valueText $unit" else valueText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    Text(
                        text = "Sem dados disponíveis",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun StepSummaryCard(steps: Long?, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Passos",
        valueText = steps?.toString(),
        subtitle = "",
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun HeartRateSummaryCard(heartRate: Double?, measurementTime: Instant?, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Frequência Cardíaca",
        valueText = heartRate?.let { "%.1f".format(it) },
        unit = "bpm",
        subtitle = "última medição: " + measurementTime?.formatLocalDateTime() ?: "sem dados",
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun AverageHeartRateSummaryCard(averageBpm: Double?, date: LocalDate, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Média Cardíaca do Dia",
        valueText = averageBpm?.let { "%.1f".format(it) },
        unit = "bpm",
        subtitle = "",
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun OxygenSaturationSummaryCard(oxygenSaturation: Double?, measurementTime: Instant?, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Saturação de Oxigênio",
        valueText = oxygenSaturation?.let { "%.1f".format(it) },
        unit = if (oxygenSaturation != null) "%" else "",
        subtitle = measurementTime?.formatLocalDateTime() ?: "sem dados",
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun SleepSummaryCard(
    sleepDuration: Long?,
    sleepQuality: String?,  // String representando a qualidade
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val durationHours = sleepDuration?.let { (it / (1000 * 60 * 60)).toString() }
    val subtitleText = when {
        sleepDuration == null -> "Sem dados"
        !sleepQuality.isNullOrBlank() -> "Qualidade do sono: $sleepQuality"
        else -> "Sono total registrado"
    }

    SummaryCard(
        title = "Duração do Sono",
        valueText = durationHours,
        unit = if (sleepDuration != null) "h" else "",
        subtitle = subtitleText,
        modifier = modifier,
        onClick = onClick
    )
}


@Composable
fun CaloriesSummaryCard(calories: Double?, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Calorias Gastas",
        valueText = calories?.let { "%.0f".format(it) },
        unit = if (calories != null) "kcal" else "",
        subtitle = "",
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun ExerciseSummaryCard(exerciseSummary: String?, modifier: Modifier = Modifier,  onClick: () -> Unit) {
    SummaryCard(
        title = "Exercícios",
        valueText = exerciseSummary,
        subtitle = "",
        modifier = modifier,
        onClick = onClick
    )
}






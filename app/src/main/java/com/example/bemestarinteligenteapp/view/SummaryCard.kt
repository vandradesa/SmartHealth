package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun SummaryCard(
    title: String,
    valueText: String?,
    unit: String = "",
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(165.dp)
            .height(225.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4E4EB))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006064))
            Spacer(Modifier.height(16.dp))

            when {
                valueText != null -> {
                    Text(
                        text = if (unit.isNotEmpty()) "$valueText $unit" else valueText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF004D40)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = subtitle, fontSize = 14.sp, color = Color(0xFF00796B))
                }
                else -> {
                    Text(
                        text = "Sem dados disponíveis",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                }
            }
        }
    }
}

@Composable
fun StepSummaryCard(steps: Long?, modifier: Modifier = Modifier) {
    SummaryCard(
        title = "Passos",
        valueText = steps?.toString(),
        subtitle = "nas últimas 24h",
        modifier = modifier
    )
}

@Composable
fun HeartRateSummaryCard(heartRate: Double?, measurementTime: Instant?, modifier: Modifier = Modifier) {
    SummaryCard(
        title = "Frequência Cardíaca",
        valueText = heartRate?.let { "%.1f".format(it) },
        unit = "bpm",
        subtitle = measurementTime?.formatLocalDateTime() ?: "sem dados",
        modifier = modifier
    )
}

@Composable
fun AverageHeartRateSummaryCard(averageBpm: Double?, date: LocalDate, modifier: Modifier = Modifier) {
    SummaryCard(
        title = "Média Cardíaca",
        valueText = averageBpm?.let { "%.1f".format(it) },
        unit = "bpm",
        subtitle = "Hoje",
        modifier = modifier
    )
}

@Composable
fun OxygenSaturationSummaryCard(oxygenSaturation: Double?, measurementTime: Instant?, modifier: Modifier = Modifier) {
    SummaryCard(
        title = "Saturação de Oxigênio",
        valueText = oxygenSaturation?.let { "%.1f".format(it) },
        unit = if (oxygenSaturation != null) "%" else "",
        subtitle = measurementTime?.formatLocalDateTime() ?: "sem dados",
        modifier = modifier
    )
}

@Composable
fun SleepSummaryCard(
    sleepDuration: Long?,
    sleepQuality: String?,  // String representando a qualidade
    modifier: Modifier = Modifier
) {
    val durationHours = sleepDuration?.let { (it / (1000 * 60 * 60)).toString() }
    val subtitleText = when {
        sleepDuration == null -> "Sem dados"
        !sleepQuality.isNullOrBlank() -> "Qualidade: $sleepQuality"
        else -> "Sono total registrado"
    }

    SummaryCard(
        title = "Duração do Sono",
        valueText = durationHours,
        unit = if (sleepDuration != null) "h" else "",
        subtitle = subtitleText,
        modifier = modifier
    )
}


@Composable
fun CaloriesSummaryCard(calories: Double?, modifier: Modifier = Modifier) {
    SummaryCard(
        title = "Calorias Gastas",
        valueText = calories?.let { "%.0f".format(it) },
        unit = if (calories != null) "kcal" else "",
        subtitle = "hoje",
        modifier = modifier
    )
}





package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import java.time.Instant


@Composable
fun DashboardScreenContent(
    steps: Long?,
    heartRate: Double?,
    measurementTime: Instant?,
    modifier: Modifier = Modifier
) {

    // DEBUG na própria UI
    if (measurementTime != null) {
        Text(
            text = "Debug time: $measurementTime",
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier.padding(4.dp)
        )
    }
    // DEBUG: imprime no Logcat o Instant que está chegando
    LaunchedEffect(measurementTime) {
        println(">>> measurementTime raw = $measurementTime")
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Passando os dados de steps para o StepSummaryCard
            StepSummaryCard(
                steps = steps,
                modifier = Modifier.weight(1f)
            )
            // Passando os dados de frequência cardíaca para o HeartRateSummaryCard
            HeartRateSummaryCard(
                heartRate = heartRate,
                measurementTime = measurementTime,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenContentPreview() {
    BemEstarInteligenteAppTheme {
        DashboardScreenContent(
            steps = 4321,
            heartRate = 74.5,
            measurementTime = Instant.parse("2025-05-03T15:30:00Z")
        )
    }
}

@Composable
fun DashboardScreen(
    stepsViewModel: StepsViewModel,
    heartRateViewModel: HeartRateViewModel
) {
    val stepsData by stepsViewModel.stepsData.observeAsState()
    val heartRate by heartRateViewModel.latestHeartRate.observeAsState()
    val measurementTime by heartRateViewModel.latestMeasurementTime.observeAsState(initial = null)

    DashboardScreenContent(
        steps = stepsData?.count,
        heartRate = heartRate,
        measurementTime = measurementTime
    )
}

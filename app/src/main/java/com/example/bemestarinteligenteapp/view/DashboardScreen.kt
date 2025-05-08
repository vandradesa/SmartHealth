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
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModel
import java.time.Instant
import java.time.LocalDate


@Composable
fun DashboardScreenContent(
    steps: Long?,
    heartRate: Double?,
    heartMeasurementTime: Instant?,
    averageBpm: Double?,
    oxygenSaturation: Double?,// <— nova prop
    o2MeasurementTime: Instant?,
    sleepDuration: Long?,
    modifier: Modifier = Modifier
) {

 /*   // DEBUG na própria UI
    if (measurementTime != null) {
        Text(
            text = "Debug time: $measurementTime",
            fontSize = 12.sp,
            color = Color.Red,
            modifier = Modifier.padding(4.dp)
        )
    }*/
    // DEBUG: imprime no Logcat o Instant que está chegando
    LaunchedEffect(heartMeasurementTime) {
        println(">>> measurementTime raw = $heartMeasurementTime")
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
                measurementTime = heartMeasurementTime,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )
        }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ⬇️ mostra a média de hoje
                AverageHeartRateSummaryCard(
                    averageBpm = averageBpm,
                    date = LocalDate.now(),    // sempre hoje
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                )

                // Passando os dados de saturação de oxigênio para o OxygenSaturationSummaryCard
                OxygenSaturationSummaryCard(
                    oxygenSaturation = oxygenSaturation,
                    measurementTime = o2MeasurementTime,
                    modifier = Modifier
                        .weight(1f)
                        .height(250.dp)
                )
            }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ⬇️ mostra a média de hoje
            SleepSummaryCard(
                sleepDuration = sleepDuration,
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
            heartMeasurementTime = Instant.parse("2025-05-03T15:30:00Z"),
            averageBpm = 72.3,
            oxygenSaturation = null,
            o2MeasurementTime = null,
            sleepDuration = null

        )
    }
}

@Composable
fun DashboardScreen(
    stepsViewModel: StepsViewModel,
    heartRateViewModel: HeartRateViewModel,
    oxygenSaturationViewModel: OxygenSaturationViewModel,
    sleepViewModel: SleepViewModel
) {
    val stepsData by stepsViewModel.stepsData.observeAsState()
    val heartRate by heartRateViewModel.latestHeartRate.observeAsState()
    val heartMeasurementTime by heartRateViewModel.latestMeasurementTime.observeAsState(initial = null)
    val averageBpm by heartRateViewModel.averageHeartRate.observeAsState(initial = null)
    val oxygenSaturation by oxygenSaturationViewModel.latestOxygenSaturation.observeAsState(initial = null) // <— dado de oxigênio
    val o2MeasurementTime by oxygenSaturationViewModel.latestO2MeasurementTime.observeAsState() // <— dado de oxigênio
    val sleepDuration by sleepViewModel.totalSleepDurationMillis.observeAsState(initial = null)

    DashboardScreenContent(
        steps = stepsData?.count,
        heartRate = heartRate,
        heartMeasurementTime = heartMeasurementTime,
        averageBpm = averageBpm,
        oxygenSaturation = oxygenSaturation,
        o2MeasurementTime = o2MeasurementTime,
        sleepDuration = sleepDuration
    )
}

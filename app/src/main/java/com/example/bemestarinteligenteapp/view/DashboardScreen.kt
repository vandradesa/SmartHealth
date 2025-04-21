package com.example.bemestarinteligenteapp.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.view.StepSummaryCard
import com.example.bemestarinteligenteapp.viewmodel.MainViewModel

@Composable
fun DashboardScreenContent(steps:Long?) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Top,

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            // Passando os dados de steps para o StepSummaryCard
            StepSummaryCard(
                steps = steps,
                modifier = Modifier.weight(1f)
            )
            StepSummaryCard(
                steps = steps,
                modifier = Modifier.weight(1f)
            )
            // Aqui você pode adicionar outros cards: frequência cardíaca, sono, etc.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenContentPreview() {
    BemEstarInteligenteAppTheme {
        DashboardScreenContent(steps = 4321)
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val steps by viewModel.steps.observeAsState()
    DashboardScreenContent(steps = steps)
}

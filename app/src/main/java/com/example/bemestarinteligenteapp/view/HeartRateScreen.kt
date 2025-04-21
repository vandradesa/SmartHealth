package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.MainViewModel

@Composable
fun HeartRateScreenContent(steps: Long?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Total de passos nas últimas 24h:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (steps != null) {
            Text("$steps passos", style = MaterialTheme.typography.headlineMedium)
        } else {
            CircularProgressIndicator()
        }
    }
}


@Preview(showBackground = false)
@Composable
fun HeartRateScreenContentPreview() {
    BemEstarInteligenteAppTheme {
        HeartRateScreenContent(steps = 4321)
    }
}

@Composable
fun HeartRateScreen(viewModel: MainViewModel) {
    val steps by viewModel.steps.observeAsState()

    HeartRateScreenContent(steps = steps)
}


package com.example.bemestarinteligenteapp.view.steps

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory
import java.time.LocalDate
import java.util.*


@Composable
fun StepsScreen(
    healthConnectClient: HealthConnectClient,
    stepsViewModel: StepsViewModel = viewModel(
        factory = StepsViewModelFactory(context = LocalContext.current)
    )
) {
    val steps by stepsViewModel.stepsData.observeAsState()
    val context = LocalContext.current

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Passos do dia $selectedDate",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = {
            showDatePicker(context) { date ->
                selectedDate = date
                stepsViewModel.loadSteps(healthConnectClient, selectedDate)
            }
        }) {
            Text("Selecionar Data")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total de passos: ${steps ?: 0}",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    // Carrega passos automaticamente ao abrir a tela para o dia de hoje
    LaunchedEffect(Unit) {
        stepsViewModel.loadSteps(healthConnectClient, selectedDate)
    }
}

private fun showDatePicker(context: Context, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            onDateSelected(selectedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.show()
}


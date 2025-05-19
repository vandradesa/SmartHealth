package com.example.bemestarinteligenteapp.view

import android.app.DatePickerDialog
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.model.ExercisesData
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModel
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModel
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModel
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.util.Calendar
import kotlin.jvm.java
import kotlinx.serialization.encodeToString



@Composable
fun DashboardScreen(
    stepsViewModel: StepsViewModel,
    heartRateViewModel: HeartRateViewModel,
    oxygenSaturationViewModel: OxygenSaturationViewModel,
    sleepViewModel: SleepViewModel,
    caloriesViewModel: CaloriesViewModel,
    exercisesViewModel: ExercisesViewModel
) {
    val context = LocalContext.current
    val healthConnectClient = HealthConnectClient.getOrCreate(context)

    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        // DatePickerDialog nativo do Android
        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Carregar dados para a data selecionada
    LaunchedEffect(selectedDate) {
        stepsViewModel.loadSteps(healthConnectClient, selectedDate)
        heartRateViewModel.loadHeartRate(healthConnectClient, selectedDate)
        oxygenSaturationViewModel.loadOxygenSaturation(healthConnectClient, selectedDate)
        sleepViewModel.loadSleepData(healthConnectClient, selectedDate)
        caloriesViewModel.loadCalories(healthConnectClient, selectedDate)
        exercisesViewModel.loadExercises(healthConnectClient,selectedDate)
    }

    // Observar dados dos ViewModels
    val stepsData by stepsViewModel.stepsData.observeAsState()
    val heartRate by heartRateViewModel.latestHeartRate.observeAsState()
    val heartMeasurementTime by heartRateViewModel.latestMeasurementTime.observeAsState(initial = null)
    val averageBpm by heartRateViewModel.averageHeartRate.observeAsState(initial = null)
    val oxygenSaturation by oxygenSaturationViewModel.latestOxygenSaturation.observeAsState(initial = null)
    val o2MeasurementTime by oxygenSaturationViewModel.latestO2MeasurementTime.observeAsState()
    val sleepDuration by sleepViewModel.totalSleepDurationMillis.observeAsState(initial = null)
    val sleepQuality by sleepViewModel.sleepQuality.observeAsState()
    val caloriesBurned by caloriesViewModel.caloriesData.observeAsState()
    val exerciseData by exercisesViewModel.exercisesData.observeAsState()

    // Montar o resumo dos exercícios na view
    val exerciseSummary = remember(exerciseData) {
        exerciseData?.let { data ->
            // exemplo simples: criar uma string formatada com os dados crus
            buildString {
                append("Atividades:\n")
                data.forEach { exercise ->
                    val durationMinutes = java.time.Duration.between(exercise.startTime, exercise.endTime).toMinutes()
                    append("- ${exercise.exerciseType}: $durationMinutes min\n")
                }
            }
        } ?: "Nenhum exercício registrado"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Button(onClick = { showDatePicker = true }) {
            Text(text = "Selecionar Data: $selectedDate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val intent = Intent(context, DeepSeekActivity::class.java).apply {
                putExtra("selectedDate", selectedDate.toString())
                putExtra("heartRate", heartRate ?: 0.0)
                putExtra("averageHeartRate", averageBpm ?: 0.0)
                putExtra("oxygenSaturation", oxygenSaturation ?: 0.0)
                putExtra("stepsCount", stepsData?.count ?: 0L)
                putExtra("sleepDurationMillis", sleepDuration ?: 0L)
                putExtra("sleepQuality", sleepQuality ?: "Indefinido")
                putExtra("caloriesBurned", caloriesBurned ?: 0.0)
                // Enviar lista como ArrayList, que é serializável
                putExtra("exercisesData", ArrayList(exerciseData ?: emptyList()))
            }
            context.startActivity(intent)
        }) {
            Text("Ir para DeepSeek")
        }

        DashboardScreenContent(
            steps = stepsData?.count,
            heartRate = heartRate,
            heartMeasurementTime = heartMeasurementTime,
            averageBpm = averageBpm,
            oxygenSaturation = oxygenSaturation,
            o2MeasurementTime = o2MeasurementTime,
            sleepDuration = sleepDuration,
            sleepQuality = sleepQuality,
            caloriesBurned = caloriesBurned,
            selectedDate = selectedDate,
            exerciseData = exerciseData


        )
    }
}


@Composable
fun DashboardScreenContent(
    steps: Long?,
    heartRate: Double?,
    heartMeasurementTime: Instant?,
    averageBpm: Double?,
    oxygenSaturation: Double?,
    o2MeasurementTime: Instant?,
    sleepDuration: Long?,
    sleepQuality: String?,
    caloriesBurned: Double?,
    selectedDate: LocalDate,
    exerciseData: List<ExercisesData>?,  // novo parâmetro aqui
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(heartMeasurementTime) {
        println(">>> measurementTime raw = $heartMeasurementTime")
    }

    val exerciseSummary = remember(exerciseData) {
        exerciseData?.let { list ->
            if (list.isEmpty()) {
                "Nenhum exercício registrado"
            } else {
                buildString {
                    append("Atividades:\n")
                    list.forEach { exercise ->
                        val durationMinutes = java.time.Duration.between(exercise.startTime, exercise.endTime).toMinutes()
                        append("- ${exercise.exerciseType}: $durationMinutes min\n")
                    }
                }
            }
        } ?: "Nenhum exercício registrado"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Dados para o dia: $selectedDate", modifier = Modifier.padding(bottom = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StepSummaryCard(
                steps = steps,
                modifier = Modifier.weight(1f)
            )
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
            AverageHeartRateSummaryCard(
                averageBpm = averageBpm,
                date = selectedDate,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )
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
            SleepSummaryCard(
                sleepDuration = sleepDuration,
                sleepQuality = sleepQuality,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 400.dp) // ajuste conforme necessário
            )
            CaloriesSummaryCard(
                calories = caloriesBurned,
                modifier = Modifier
                    .weight(1f)
                    .height(250.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Aqui adiciona o ExerciseSummaryCard em uma Row ou sozinho
        ExerciseSummaryCard(
            exerciseSummary = exerciseSummary,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
        )
    }
}



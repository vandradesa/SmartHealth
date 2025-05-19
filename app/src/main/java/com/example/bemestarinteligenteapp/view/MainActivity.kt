package com.example.bemestarinteligenteapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.core.net.toUri
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModel
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModel
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModel
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModel
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory

// Permissões necessárias para acessar os dados de saúde
private val permissions = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(OxygenSaturationRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class)
)

class MainActivity : ComponentActivity() {

    // ViewModels
    private lateinit var stepsViewModel: StepsViewModel
    private lateinit var heartRateViewModel: HeartRateViewModel
    private lateinit var oxygenSaturationViewModel: OxygenSaturationViewModel
    private lateinit var sleepViewModel: SleepViewModel
    private lateinit var caloriesViewModel: CaloriesViewModel
    private lateinit var exercisesViewModel: ExercisesViewModel

    // HealthConnect Client
    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializando ViewModels
        stepsViewModel = ViewModelProvider(this, StepsViewModelFactory(applicationContext))[StepsViewModel::class.java]
        heartRateViewModel = ViewModelProvider(this, HeartRateViewModelFactory(applicationContext))[HeartRateViewModel::class.java]
        oxygenSaturationViewModel = ViewModelProvider(this, OxygenSaturationViewModelFactory(applicationContext))[OxygenSaturationViewModel::class.java]
        sleepViewModel = ViewModelProvider(this, SleepViewModelFactory(applicationContext))[SleepViewModel::class.java]
        caloriesViewModel = ViewModelProvider(this, CaloriesViewModelFactory(applicationContext))[CaloriesViewModel::class.java]
        exercisesViewModel = ViewModelProvider(this, ExercisesViewModelFactory(applicationContext))[ExercisesViewModel::class.java]

        lifecycleScope.launch {
            verificarSdkEPermissoes()
        }

        // Definindo a UI com Jetpack Compose
        setContent {
            BemEstarInteligenteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(
                        stepsViewModel = stepsViewModel,
                        heartRateViewModel = heartRateViewModel,
                        oxygenSaturationViewModel = oxygenSaturationViewModel,
                        sleepViewModel = sleepViewModel,
                        caloriesViewModel = caloriesViewModel,
                        exercisesViewModel = exercisesViewModel
                    )
                }
            }
        }
    }

    private suspend fun verificarSdkEPermissoes() {
        val providerPackageName = "com.google.android.apps.healthdata"
        val availability = HealthConnectClient.getSdkStatus(this, providerPackageName)

        // Se o SDK precisar de atualização, tente abrir a Play Store
        if (availability == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            try {
                val uri = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = uri.toUri()
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                })
            } catch (e: Exception) {
                Toast.makeText(this, "Erro ao abrir a Play Store: ${e.message}", Toast.LENGTH_LONG).show()
            }
            return
        }

        // Inicializa o cliente de dados de saúde
        healthConnectClient = HealthConnectClient.getOrCreate(this)
        val permissionController = healthConnectClient.permissionController
        val granted = permissionController.getGrantedPermissions()

        // Solicita permissões caso ainda não tenham sido concedidas
        if (!granted.containsAll(permissions)) {
            requestPermissions.launch(permissions)
        } else {
            carregarDados()
        }
    }

    // Método para carregar os dados das ViewModels
    private fun carregarDados() {
        lifecycleScope.launch {
            stepsViewModel.loadSteps(healthConnectClient)
            heartRateViewModel.loadHeartRate(healthConnectClient)
            oxygenSaturationViewModel.loadOxygenSaturation(healthConnectClient)
            sleepViewModel.loadSleepData(healthConnectClient)
            caloriesViewModel.loadCalories(healthConnectClient)
            exercisesViewModel.loadExercises(healthConnectClient)
        }
    }

    // Registro da solicitação de permissões
    private val requestPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(permissions)) {
            carregarDados()
        } else {
            Toast.makeText(
                this,
                "Permissões não concedidas. O app precisa delas para funcionar corretamente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
package com.example.bemestarinteligenteapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.core.net.toUri
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModel
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModel
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModel
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory
import kotlinx.coroutines.launch

// Definindo as permissões necessárias para acessar os dados de saúde
private val permissions = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(OxygenSaturationRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)

)

class MainActivity : ComponentActivity() {

    // Declaração dos ViewModels
    private lateinit var stepsViewModel: StepsViewModel
    private lateinit var heartRateViewModel: HeartRateViewModel
    private lateinit var oxygenSaturationViewModel: OxygenSaturationViewModel
    private lateinit var sleepViewModel: SleepViewModel
    private lateinit var caloriesViewModel: CaloriesViewModel


    // Inicialização do HealthConnectClient
    private lateinit var healthConnectClient: HealthConnectClient

    // Registro da solicitação de permissões
    private val requestPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        // Caso as permissões sejam concedidas, carregamos os dados
        if (granted.containsAll(permissions)) {
            lifecycleScope.launch {
                healthConnectClient = HealthConnectClient.getOrCreate(this@MainActivity)
                stepsViewModel.loadSteps(healthConnectClient) // Carrega os dados de passos
                heartRateViewModel.loadHeartRate(healthConnectClient) // Carrega os dados de frequência cardíaca
                oxygenSaturationViewModel.loadOxygenSaturation(healthConnectClient)
                sleepViewModel.loadSleepData(healthConnectClient)
                caloriesViewModel.loadCalories(healthConnectClient)
            }
        } else {
            // Caso não tenha permissões, mostramos um aviso ao usuário
            Toast.makeText(
                this,
                "Permissões não concedidas. O app precisa delas para funcionar corretamente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Instanciando ViewModel com o repositório
        //val repository = HealthDataRepositoryImpl()
        //steps
        // Inicializando os ViewModels com suas respectivas fábricas
        val stepsFactory = StepsViewModelFactory(applicationContext)
        stepsViewModel = ViewModelProvider(this, stepsFactory)[StepsViewModel::class.java]

        //heartRate
        val heartRateFactory = HeartRateViewModelFactory(applicationContext)
        heartRateViewModel = ViewModelProvider(this, heartRateFactory)[HeartRateViewModel::class.java]

        //oxygenSaturation
        val oxygenSaturationFactory = OxygenSaturationViewModelFactory(applicationContext)
        oxygenSaturationViewModel = ViewModelProvider(this, oxygenSaturationFactory)[OxygenSaturationViewModel::class.java]

        val sleepFactory = SleepViewModelFactory(applicationContext)
        sleepViewModel = ViewModelProvider(this, sleepFactory)[SleepViewModel::class.java]

        val caloriesFactory = CaloriesViewModelFactory(applicationContext)
        caloriesViewModel = ViewModelProvider(this, caloriesFactory)[CaloriesViewModel::class.java]

        lifecycleScope.launch {
            // Verificando o status do SDK do Health Connect
            val providerPackageName = "com.google.android.apps.healthdata"
            val availability = HealthConnectClient.getSdkStatus(this@MainActivity, providerPackageName)
            if (availability == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
                val uri = "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = uri.toUri()
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                })
                return@launch
            }

            // Inicializa o cliente de dados de saúde do Health Connect
            healthConnectClient = HealthConnectClient.getOrCreate(this@MainActivity)
            val permissionController = healthConnectClient.permissionController

            // Verificando se as permissões necessárias já foram concedidas
            val granted = permissionController.getGrantedPermissions()

            // Caso as permissões já tenham sido concedidas, carregamos os dados
            if (granted.containsAll(permissions)) {
                stepsViewModel.loadSteps(healthConnectClient)
                heartRateViewModel.loadHeartRate(healthConnectClient)
                oxygenSaturationViewModel.loadOxygenSaturation(healthConnectClient)
                sleepViewModel.loadSleepData(healthConnectClient)
                caloriesViewModel.loadCalories(healthConnectClient)
            } else {
                // Caso as permissões ainda não tenham sido concedidas, solicitamos ao usuário
                requestPermissions.launch(permissions)
            }
        }

        // Definindo o conteúdo da tela com Jetpack Compose
        setContent {
            BemEstarInteligenteAppTheme {
                // Layout da tela principal
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Passando os ViewModels para a tela de Dashboard
                    DashboardScreen(
                        stepsViewModel    = stepsViewModel,
                        heartRateViewModel = heartRateViewModel,
                        oxygenSaturationViewModel = oxygenSaturationViewModel,
                        sleepViewModel = sleepViewModel,
                        caloriesViewModel = caloriesViewModel
                    )
                    //StepsScreen(healthConnectClient = healthConnectClient)  // Passando explicitamente o healthConnectClient



                }
            }
        }
    }
}

package com.example.bemestarinteligenteapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.core.net.toUri
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.MainViewModel
import com.example.bemestarinteligenteapp.viewmodel.MainViewModelFactory
import kotlinx.coroutines.launch

private val permissions = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(OxygenSaturationRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class)
)

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var healthConnectClient: HealthConnectClient


    private val requestPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(permissions)) {
            lifecycleScope.launch {
                healthConnectClient = HealthConnectClient.getOrCreate(this@MainActivity)
                viewModel.readSteps(healthConnectClient)
            }
        } else {
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
        val repository = HealthDataRepositoryImpl()
        val factory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        lifecycleScope.launch {
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

            healthConnectClient = HealthConnectClient.getOrCreate(this@MainActivity)
            val permissionController = healthConnectClient.permissionController

            val granted = permissionController.getGrantedPermissions()
            if (granted.containsAll(permissions)) {
                viewModel.readSteps(healthConnectClient)
            } else {
                requestPermissions.launch(permissions)
            }
        }


        setContent {
            BemEstarInteligenteAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(viewModel = viewModel)

                }
            }
        }
    }
}

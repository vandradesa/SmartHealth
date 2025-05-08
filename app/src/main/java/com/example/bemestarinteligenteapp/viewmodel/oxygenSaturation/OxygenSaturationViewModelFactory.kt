package com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.healthconnect.oxygen.OxygenSaturationManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl

class OxygenSaturationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OxygenSaturationViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val oxygenSaturationManager = OxygenSaturationManager(healthConnectClient)
            val repository = HealthDataRepositoryImpl(
                stepsManager = null,
                heartRateManager = null,
                oxygenSaturationManager = oxygenSaturationManager,
                sleepManager = null
            )
            return OxygenSaturationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

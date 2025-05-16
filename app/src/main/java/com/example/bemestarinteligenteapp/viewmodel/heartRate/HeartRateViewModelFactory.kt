package com.example.bemestarinteligenteapp.viewmodel.heartRate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.healthconnect.heartRate.HeartRateManager

class HeartRateViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeartRateViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val heartRateManager = HeartRateManager(healthConnectClient)
            val repository = HealthDataRepositoryImpl(null, heartRateManager, null, null, null)
            return HeartRateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

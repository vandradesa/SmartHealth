package com.example.bemestarinteligenteapp.viewmodel.sleep

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.healthconnect.sleep.SleepManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl

class SleepViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val sleepManager = SleepManager(healthConnectClient) // Este é o seu SleepManager
            val repository = HealthDataRepositoryImpl(null, null, null, sleepManager, null, null)
            return SleepViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

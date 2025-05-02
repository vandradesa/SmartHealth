package com.example.bemestarinteligenteapp.viewmodel.steps

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bemestarinteligenteapp.healthconnect.steps.StepsManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl
import androidx.health.connect.client.HealthConnectClient

class StepsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepsViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val stepsManager = StepsManager(healthConnectClient)
            val repository = HealthDataRepositoryImpl(stepsManager,null)
            return StepsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

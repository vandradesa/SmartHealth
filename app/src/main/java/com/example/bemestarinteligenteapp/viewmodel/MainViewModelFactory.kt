/*package com.example.bemestarinteligenteapp.viewmodel

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bemestarinteligenteapp.healthconnect.steps.StepsManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        // Cria as dependências necessárias
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        val stepsManager = StepsManager(healthConnectClient)
        val healthDataRepository = HealthDataRepositoryImpl(stepsManager)

        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(healthDataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/

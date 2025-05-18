package com.example.bemestarinteligenteapp.viewmodel.calories


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.healthconnect.calories.CaloriesManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl

class CaloriesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaloriesViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val caloriesManager = CaloriesManager(healthConnectClient)
            val repository = HealthDataRepositoryImpl(
                stepsManager = null,
                heartRateManager = null,
                oxygenSaturationManager = null,
                sleepManager = null,
                caloriesManager = caloriesManager, // Passa o CaloriesManager aqui
                exercisesManager = null
            )
            return CaloriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

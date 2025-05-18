package com.example.bemestarinteligenteapp.viewmodel.exercise

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.healthconnect.exercises.ExercisesManager
import com.example.bemestarinteligenteapp.repository.HealthDataRepositoryImpl

class ExercisesViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            val healthConnectClient = HealthConnectClient.getOrCreate(context)
            val exercisesManager = ExercisesManager(healthConnectClient)
            val repository = HealthDataRepositoryImpl(
                stepsManager = null,
                heartRateManager = null,
                oxygenSaturationManager = null,
                sleepManager = null,
                caloriesManager = null, // Passa o CaloriesManager aqui
                exercisesManager = exercisesManager

            )
            return ExercisesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

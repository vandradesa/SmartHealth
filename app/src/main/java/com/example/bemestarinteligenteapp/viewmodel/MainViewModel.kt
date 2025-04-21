package com.example.bemestarinteligenteapp.viewmodel

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.*
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant

class MainViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _steps = MutableLiveData<Long>()
    val steps: LiveData<Long> get() = _steps

    // Função que chama readSteps de forma abstrata
    fun loadSteps(healthConnectClient: HealthConnectClient) {
        readSteps(healthConnectClient)  // Chama a função interna para ler os passos
    }

    fun readSteps(healthConnectClient: HealthConnectClient) {
        viewModelScope.launch {
            val endTime = Instant.now()
            val startTime = endTime.minusSeconds(24 * 60 * 60)
            val total = healthDataRepository.getStepsData(healthConnectClient, startTime, endTime)
            _steps.value = total
        }
    }
}

/*package com.example.bemestarinteligenteapp.viewmodel

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.*
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class MainViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _steps = MutableLiveData<Long?>()
    val steps: MutableLiveData<Long?> get() = _steps

    // Função que chama readSteps de forma abstrata
    fun loadSteps(healthConnectClient: HealthConnectClient) {
        readSteps(healthConnectClient)  // Chama a função interna para ler os passos
    }

    fun readSteps(healthConnectClient: HealthConnectClient) {
        viewModelScope.launch {
            val endTime = Instant.now()
            val startTime = endTime
                .atZone(ZoneId.systemDefault())        // converte para horário local
                .toLocalDate()                         // pega a data de hoje
                .atStartOfDay(ZoneId.systemDefault())  // define o início do dia na zona local
                .toInstant()                           // converte de volta para Instant
            val total = healthDataRepository.getStepsData(startTime, endTime)
            _steps.value = total
        }
    }
}*/

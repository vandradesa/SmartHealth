package com.example.bemestarinteligenteapp.viewmodel.steps

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.*
import com.example.bemestarinteligenteapp.model.StepsData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class StepsViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _stepsData = MutableLiveData<StepsData>()
    val stepsData: LiveData<StepsData> get() = _stepsData

    // Agora essa função pode receber uma data opcional
    fun loadSteps(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()  // Se não passar a data, usa hoje
        readStepsForDate(healthConnectClient, targetDate)
    }

    private fun readStepsForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            // Define o início do dia selecionado
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

            // Define o final do dia selecionado (23:59:59.999)
            val endTime = date.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            val total = healthDataRepository.getStepsData(
                startTime,
                endTime
            )

            _stepsData.value = StepsData(startTime, endTime, total)
        }
    }

}

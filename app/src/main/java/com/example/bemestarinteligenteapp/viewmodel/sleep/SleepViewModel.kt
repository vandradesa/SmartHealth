package com.example.bemestarinteligenteapp.viewmodel.sleep


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.model.SleepData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SleepViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _sleepData = MutableLiveData<List<SleepData>?>()
    val sleepData: LiveData<List<SleepData>?> get() = _sleepData

    private val _totalSleepDurationMillis = MutableLiveData<Long?>()
    val totalSleepDurationMillis: LiveData<Long?> get() = _totalSleepDurationMillis

    /**
     * Carrega os dados de sono para uma data específica (ou hoje, se date == null).
     */
    fun loadSleepData(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now() // Se não passar a data, usa hoje
        readSleepDataForDate(healthConnectClient, targetDate)
    }

    /**
     * Função interna que faz a query e calcula a duração total do sono.
     */
    private fun readSleepDataForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val data = healthDataRepository.getSleepData(startTime, endTime)
            val sortedData = data?.sortedBy { it.sessionStart }

            _sleepData.value = sortedData

            if (!sortedData.isNullOrEmpty()) {
                val totalDuration = sortedData.sumOf { it.durationMillis }
                _totalSleepDurationMillis.value = totalDuration

                Log.d("SleepLogs", "Sessões de sono: ${sortedData.size}")
                Log.d("SleepLogs", "Duração total: ${totalDuration / 1000 / 60} minutos")
            } else {
                _totalSleepDurationMillis.value = null
                Log.d("SleepLogs", "Nenhuma sessão de sono encontrada para a data.")
            }
        }
    }
}

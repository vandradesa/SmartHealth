package com.example.bemestarinteligenteapp.viewmodel.heartRate

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.HeartRateData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import com.example.bemestarinteligenteapp.util.formatLocalDateTime
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HeartRateViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    // LiveData para armazenar a lista de dados de frequência cardíaca
    private val _heartRateData = MutableLiveData<List<HeartRateData>?>()
    val heartRateData: LiveData<List<HeartRateData>> get() = _heartRateData as LiveData<List<HeartRateData>>

    // LiveData para armazenar o último valor de frequência cardíaca
    private val _latestHeartRate = MutableLiveData<Double?>()
    val latestHeartRate: LiveData<Double?> get() = _latestHeartRate

    // LiveData para guardar o momento da última medição
    private val _latestMeasurementTime = MutableLiveData<Instant?>()
    val latestMeasurementTime: LiveData<Instant?> get() = _latestMeasurementTime

    // LiveData: média de BPM do dia selecionado
    private val _averageHeartRate = MutableLiveData<Double?>()
    val averageHeartRate: LiveData<Double?> get() = _averageHeartRate

    /**
     * Carrega os dados de frequência cardíaca para uma data específica (ou hoje, se date == null),
     * calcula a média e preenche os LiveDatas.
     */
     fun loadHeartRate(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()  // Se não passar a data, usa hoje
        readHeartRateForDate(healthConnectClient, targetDate)
    }

    /**
     * Função interna que faz a query ao repositório, filtra o intervalo de 00:00 até 23:59 do dia,
     * popula os dados brutos, o último valor e calcula a média.
     */
    private fun readHeartRateForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val data = healthDataRepository.getHeartRateData(startTime, endTime)
            val sortedData = data?.sortedBy { it.time }

            _heartRateData.value = sortedData

            if (!sortedData.isNullOrEmpty()) {
                val latest = sortedData.last()
                _latestHeartRate.value = latest.bpm
                _latestMeasurementTime.value = latest.time

                val sumBpm = sortedData.sumOf { it.bpm}
                val avgBpm = sumBpm / sortedData.size
                _averageHeartRate.value = avgBpm

                val formatted = latest.time.formatLocalDateTime()
                Log.d("HeartRateLogs", "Hora da última medição: ${formatted}")
                Timber.tag("HeartRateLogs").d("Última frequência cardíaca: ${latest.bpm}")
                Log.d("HeartRateLogs", "Hora da última medição: ${latest.time}")
            } else {
                _latestHeartRate.value = null
                _latestMeasurementTime.value = null
                _averageHeartRate.value = null

                Log.d("HeartRateLogs", "Nenhum dado de frequência cardíaca encontrado.")
            }
        }
    }
}
package com.example.bemestarinteligenteapp.viewmodel.heartRate

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.HeartRateData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
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

    // Agora essa função pode receber uma data opcional
    fun loadHeartRate(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()  // Se não passar a data, usa hoje
        readHeartRateForDate(healthConnectClient, targetDate)
    }

    private fun readHeartRateForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            // Define o início do dia selecionado
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

            // Define o final do dia selecionado (23:59:59.999)
            val endTime = date.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            val data = healthDataRepository.getHeartRateData(
                startTime,
                endTime
            )

            _heartRateData.value = data

            // Se a lista de dados não estiver vazia, obtemos o último valor de BPM
            if (data?.isNotEmpty() == true) {
                val latest = data.last()
                _latestHeartRate.value = latest.bpm
                _latestMeasurementTime.value = latest.endTime

                // Adicionando logs para verificar os valores
                Timber.tag("HeartRateLogs").d("Última frequência cardíaca: ${latest.bpm}")
                Log.d("HeartRateLogs", "Hora da última medição: ${latest.endTime}")
            } else {
                _latestHeartRate.value = null
                _latestMeasurementTime.value = null
                // Adicionando logs quando não há dados
                Log.d("HeartRateLogs", "Nenhum dado de frequência cardíaca encontrado.")
            }
        }
    }

}
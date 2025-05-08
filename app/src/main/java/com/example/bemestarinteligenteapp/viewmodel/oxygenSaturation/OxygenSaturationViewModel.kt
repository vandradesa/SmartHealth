package com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation

import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.OxygenSaturationData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import com.example.bemestarinteligenteapp.util.formatLocalDateTime
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class OxygenSaturationViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _oxygenSaturationData = MutableLiveData<List<OxygenSaturationData>?>()
    val oxygenSaturationData: MutableLiveData<List<OxygenSaturationData>?> get() = _oxygenSaturationData

    private val _latestOxygenSaturation = MutableLiveData<Double?>()
    val latestOxygenSaturation: LiveData<Double?> get() = _latestOxygenSaturation

    private val _averageOxygenSaturation = MutableLiveData<Double?>()
    val averageOxygenSaturation: LiveData<Double?> get() = _averageOxygenSaturation

    // LiveData para guardar o momento da última medição
    private val _latestO2MeasurementTime = MutableLiveData<Instant?>()
    val latestO2MeasurementTime: LiveData<Instant?> get() = _latestO2MeasurementTime

    fun loadOxygenSaturation(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()
        readOxygenSaturationForDate(targetDate)
    }

    private fun readOxygenSaturationForDate(date: LocalDate) {
        viewModelScope.launch {
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val data = healthDataRepository.getOxygenSaturationData(startTime, endTime)
            val sortedData = data?.sortedBy { it.time }

            _oxygenSaturationData.value = sortedData

            if (!sortedData.isNullOrEmpty()) {
                val latest = sortedData.last()
                _latestOxygenSaturation.value = latest.percentage
                _latestO2MeasurementTime.value = latest.time
                _averageOxygenSaturation.value = sortedData.map { it.percentage }.average()


                val formatted = latest.time.formatLocalDateTime()
                Log.d("OxygenLogs", "Hora da última medição: $formatted")
                Timber.tag("OxygenLogs").d("Última saturação: ${latest.percentage}")
                Log.d("OxygenLogs", "Hora da última medição: ${latest.time}")
            } else {
                _latestOxygenSaturation.value = null
                _averageOxygenSaturation.value = null
                Log.d("OxygenLogs", "Nenhum dado de saturação de oxigênio encontrado.")
            }
        }
    }
}

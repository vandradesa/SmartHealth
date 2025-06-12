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
import com.example.bemestarinteligenteapp.view.AppDestinations
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

data class OxygenSaturationWeeklyChartData(
    val dayLabels: List<String>,
    val pastWeekAvgO2Saturation: List<Double?>,  // Média de SpO2 da semana passada
    val currentWeekAvgO2Saturation: List<Double?> // Média de SpO2 da semana atual
)

// Exemplo de função de extensão para formatar, se você a tiver em outro lugar
fun Instant.formatLocalDateTime(pattern: String = "dd/MM/yyyy HH:mm:ss", zoneId: ZoneId = ZoneId.systemDefault()): String {
    return DateTimeFormatter.ofPattern(pattern).withZone(zoneId).format(this)
}

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

    // --- NOVOS LiveData para o gráfico semanal ---
    private val _weeklyChartData = MutableLiveData<OxygenSaturationWeeklyChartData>()
    val weeklyChartData: LiveData<OxygenSaturationWeeklyChartData> get() = _weeklyChartData

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

    fun loadWeeklyOxygenSaturationData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val locale = Locale.getDefault() // Ou especifique um Locale
            val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek

            val startOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            val startOfPastWeek = startOfCurrentWeek.minusWeeks(1)

            val dayLabels = List(7) { i ->
                firstDayOfWeek.plus(i.toLong()).getDisplayName(TextStyle.SHORT, locale)
            }

            val pastWeekDeferred = (0..6).map { dayIndex ->
                async {
                    val date = startOfPastWeek.plusDays(dayIndex.toLong())
                    getAverageO2SaturationForSingleDay(date) // Função auxiliar para buscar a média do dia
                }
            }

            val currentWeekDeferred = (0..6).map { dayIndex ->
                async {
                    val date = startOfCurrentWeek.plusDays(dayIndex.toLong())
                    if (date.isAfter(today)) {
                        null // Dias futuros não têm dados
                    } else {
                        getAverageO2SaturationForSingleDay(date)
                    }
                }
            }

            val pastWeekAvgO2 = pastWeekDeferred.awaitAll()
            val currentWeekAvgO2 = currentWeekDeferred.awaitAll()

            _weeklyChartData.value = OxygenSaturationWeeklyChartData(
                dayLabels = dayLabels,
                pastWeekAvgO2Saturation = pastWeekAvgO2,
                currentWeekAvgO2Saturation = currentWeekAvgO2
            )
        }
    }

    /**
     * Função auxiliar para buscar a MÉDIA de saturação de oxigênio para um único dia.
     * Esta média será usada no gráfico semanal.
     */
    private suspend fun getAverageO2SaturationForSingleDay(date: LocalDate): Double? {
        val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        val data = healthDataRepository.getOxygenSaturationData(startTime, endTime)
        // Calcula a média, similar ao que readOxygenSaturationForDate faz
        return if (!data.isNullOrEmpty()) {
            data.mapNotNull { it.percentage }.average().takeIf { !it.isNaN() } // Evita NaN se a lista de doubles for vazia após mapNotNull
        } else {
            null
        }
    }
}





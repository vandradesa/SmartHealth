package com.example.bemestarinteligenteapp.viewmodel.calories

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.CaloriesData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

data class CaloriesWeeklyChartData(
    val dayLabels: List<String>,          // Ex: ["Seg", "Ter", ..., "Dom"]
    val pastWeekCalories: List<Double?>,  // Calorias diárias da semana passada (Double? para calorias)
    val currentWeekCalories: List<Double?>// Calorias diárias da semana atual
)

class CaloriesViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _caloriesData = MutableLiveData<Double?>()
    val caloriesData: LiveData<Double?> get() = _caloriesData

    // LiveData para os dados do gráfico semanal de calorias
    private val _weeklyChartData = MutableLiveData<CaloriesWeeklyChartData>()
    val weeklyChartData: LiveData<CaloriesWeeklyChartData> get() = _weeklyChartData

    fun loadCalories(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()
        readCaloriesForDate(healthConnectClient, targetDate)
    }

    private fun readCaloriesForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            val startTime: Instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime: Instant = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val totalKcal = healthDataRepository.getCaloriesData(startTime, endTime)

            _caloriesData.value = totalKcal

        }
    }

    fun loadWeeklyCaloriesData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            val locale = Locale.getDefault()
            val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek

            val startOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            val startOfPastWeek = startOfCurrentWeek.minusWeeks(1)

            val dayLabels = List(7) { i ->
                firstDayOfWeek.plus(i.toLong()).getDisplayName(TextStyle.SHORT, locale)
            }

            val pastWeekDeferred = (0..6).map { dayIndex ->
                async {
                    val date = startOfPastWeek.plusDays(dayIndex.toLong())
                    getCaloriesForSingleDay(date) // Não passa HealthConnectClient
                }
            }

            val currentWeekDeferred = (0..6).map { dayIndex ->
                async {
                    val date = startOfCurrentWeek.plusDays(dayIndex.toLong())
                    if (date.isAfter(today)) {
                        null // Para Double?, null é mais apropriado que 0.0 para "sem dados futuros"
                    } else {
                        getCaloriesForSingleDay(date) // Não passa HealthConnectClient
                    }
                }
            }

            val pastWeekCalories = pastWeekDeferred.awaitAll()
            val currentWeekCalories = currentWeekDeferred.awaitAll()

            _weeklyChartData.value = CaloriesWeeklyChartData(
                dayLabels = dayLabels,
                pastWeekCalories = pastWeekCalories,
                currentWeekCalories = currentWeekCalories
            )
        }
    }

    /**
     * Função auxiliar para buscar o total de calorias para um único dia.
     * Segue o modelo de getStepsForSingleDay.
     * Não recebe HealthConnectClient, implicando que o repositório lida com isso.
     */
    private suspend fun getCaloriesForSingleDay(date: LocalDate): Double? {
        val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        // Chamada ao repositório.
        return healthDataRepository.getCaloriesData(startTime, endTime)
    }



}

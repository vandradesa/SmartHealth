package com.example.bemestarinteligenteapp.viewmodel.calories

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.CaloriesData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CaloriesViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _caloriesData = MutableLiveData<Double?>()
    val caloriesData: LiveData<Double?> get() = _caloriesData

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
}

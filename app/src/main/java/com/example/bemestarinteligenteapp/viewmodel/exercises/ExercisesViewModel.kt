package com.example.bemestarinteligenteapp.viewmodel.exercise

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.ExercisesData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class ExercisesViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _exercisesData = MutableLiveData<List<ExercisesData>?>()
    val exercisesData: LiveData<List<ExercisesData>?> get() = _exercisesData

    fun loadExercises(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()
        readExercisesForDate(healthConnectClient,targetDate)
    }

    private fun readExercisesForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            val startTime: Instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime: Instant = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val exercises = healthDataRepository.getExerciseData(startTime, endTime)

            _exercisesData.value = exercises
        }
    }
}

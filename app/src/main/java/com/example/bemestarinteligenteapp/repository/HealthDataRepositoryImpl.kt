package com.example.bemestarinteligenteapp.repository

import com.example.bemestarinteligenteapp.healthconnect.calories.CaloriesManager
import com.example.bemestarinteligenteapp.healthconnect.exercises.ExercisesManager
import com.example.bemestarinteligenteapp.healthconnect.heartRate.HeartRateManager
import com.example.bemestarinteligenteapp.healthconnect.oxygen.OxygenSaturationManager
import com.example.bemestarinteligenteapp.healthconnect.sleep.SleepManager
import com.example.bemestarinteligenteapp.healthconnect.steps.StepsManager
import com.example.bemestarinteligenteapp.model.ExercisesData
import com.example.bemestarinteligenteapp.model.HeartRateData
import com.example.bemestarinteligenteapp.model.OxygenSaturationData
import com.example.bemestarinteligenteapp.model.SleepData
import java.time.Instant

interface HealthDataRepository {
    suspend fun getStepsData(
        startTime: Instant,
        endTime: Instant
    ): Long?

    suspend fun getHeartRateData(
        startTime: Instant,
        endTime: Instant
    ): List<HeartRateData>? // lista com os batimentos médios por leitura

    suspend fun getOxygenSaturationData(
        startTime: Instant,
        endTime: Instant
    ): List<OxygenSaturationData>?

    suspend fun getSleepData(
        startTime: Instant,
        endTime: Instant
    ): List<SleepData>?

    suspend fun getCaloriesData(
        startTime: Instant,
        endTime: Instant)
    : Double?

    suspend fun getExerciseData(
        startTime: Instant,
        endTime: Instant
    ): List<ExercisesData>?

}

class HealthDataRepositoryImpl(
    private val stepsManager: StepsManager?,
    private val heartRateManager: HeartRateManager?,
    private val oxygenSaturationManager: OxygenSaturationManager?,
    private val sleepManager: SleepManager?,
    private val caloriesManager: CaloriesManager?,
    private val exercisesManager: ExercisesManager? // Novo parâmetr// Novo

) : HealthDataRepository {

    override suspend fun getStepsData(
        startTime: Instant,
        endTime: Instant
    ): Long? {
        return stepsManager?.readSteps(startTime, endTime)
    }

    override suspend fun getHeartRateData(
        startTime: Instant,
        endTime: Instant
    ): List<HeartRateData>? {
        return heartRateManager?.readHeartRate(startTime, endTime)
    }

    override suspend fun getOxygenSaturationData(
        startTime: Instant,
        endTime: Instant
    ): List<OxygenSaturationData>? {
        return oxygenSaturationManager?.readOxygenSaturation(startTime, endTime)
    }

    override suspend fun getSleepData(
        startTime: Instant,
        endTime: Instant
    ): List<SleepData>? {
        return sleepManager?.readSleepSessions(startTime, endTime)
    }

    override suspend fun getCaloriesData(
        startTime: Instant,
        endTime: Instant
    ): Double? {
        return caloriesManager?.readCalories(startTime, endTime)
    }

    override suspend fun getExerciseData(
        startTime: Instant,
        endTime: Instant
    ): List<ExercisesData>? {
        return exercisesManager?.readExercises(startTime, endTime)
    }

}

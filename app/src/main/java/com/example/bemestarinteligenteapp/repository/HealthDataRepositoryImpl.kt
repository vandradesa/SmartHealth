package com.example.bemestarinteligenteapp.repository

import com.example.bemestarinteligenteapp.healthconnect.heartRate.HeartRateManager
import com.example.bemestarinteligenteapp.healthconnect.steps.StepsManager
import com.example.bemestarinteligenteapp.model.HeartRateData
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
}


class HealthDataRepositoryImpl(
    private val stepsManager: StepsManager?,
    private val heartRateManager: HeartRateManager?
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
}

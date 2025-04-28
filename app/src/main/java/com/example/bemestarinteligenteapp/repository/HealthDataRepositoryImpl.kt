package com.example.bemestarinteligenteapp.repository

import com.example.bemestarinteligenteapp.healthconnect.steps.StepsManager
import java.time.Instant

interface HealthDataRepository {
    suspend fun getStepsData(
        startTime: Instant,
        endTime: Instant
    ): Long
}


class HealthDataRepositoryImpl(
    private val stepsManager: StepsManager
) : HealthDataRepository {

    override suspend fun getStepsData(
        startTime: Instant,
        endTime: Instant
    ): Long {
        return stepsManager.readSteps(startTime, endTime)
    }
}

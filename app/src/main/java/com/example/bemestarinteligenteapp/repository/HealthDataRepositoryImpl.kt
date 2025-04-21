package com.example.bemestarinteligenteapp.repository

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

interface HealthDataRepository {
    suspend fun getStepsData(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): Long
}

class HealthDataRepositoryImpl : HealthDataRepository {
    override suspend fun getStepsData(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ): Long {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.count.toLong() }
    }
}

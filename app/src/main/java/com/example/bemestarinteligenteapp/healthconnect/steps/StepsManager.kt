package com.example.bemestarinteligenteapp.healthconnect.steps


import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant


class StepsManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readSteps(startTime: Instant, endTime: Instant): Long {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.count.toLong() }
    }
}
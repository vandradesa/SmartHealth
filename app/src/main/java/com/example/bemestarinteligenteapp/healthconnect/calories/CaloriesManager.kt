package com.example.bemestarinteligenteapp.healthconnect.calories

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class CaloriesManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readCalories(startTime: Instant, endTime: Instant): Double {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                ActiveCaloriesBurnedRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response.records.sumOf { it.energy.inKilocalories }
    }
}

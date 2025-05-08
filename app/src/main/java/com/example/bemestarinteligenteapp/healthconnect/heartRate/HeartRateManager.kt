package com.example.bemestarinteligenteapp.healthconnect.heartRate

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.bemestarinteligenteapp.model.HeartRateData
import java.time.Instant

class HeartRateManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readHeartRate(startTime: Instant, endTime: Instant): List<HeartRateData> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.Companion.between(startTime, endTime)
            )
        )
        // Aqui extraímos cada sample como um dado individual
        return response.records.flatMap { record ->
            record.samples.map { sample ->
                HeartRateData(
                    time = sample.time,
                    bpm = sample.beatsPerMinute.toDouble()
            )
        }
    }
}}
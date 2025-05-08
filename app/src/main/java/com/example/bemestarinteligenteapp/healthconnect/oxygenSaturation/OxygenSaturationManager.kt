package com.example.bemestarinteligenteapp.healthconnect.oxygen

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.bemestarinteligenteapp.model.OxygenSaturationData
import java.time.Instant

class OxygenSaturationManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readOxygenSaturation(startTime: Instant, endTime: Instant): List<OxygenSaturationData> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                OxygenSaturationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        return response.records.map { record ->
            OxygenSaturationData(
                time = record.time,
                percentage = record.percentage.value
                )
        }
    }
}

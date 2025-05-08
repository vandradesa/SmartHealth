package com.example.bemestarinteligenteapp.healthconnect.sleep

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.bemestarinteligenteapp.model.SleepData
import com.example.bemestarinteligenteapp.model.SleepStageData
import java.time.Duration
import java.time.Instant

class SleepManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readSleepSessions(startTime: Instant, endTime: Instant): List<SleepData> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        return response.records.map { session ->
            val duration = Duration.between(session.startTime, session.endTime).toMillis()

            val stages = session.stages.map { stage ->
                SleepStageData(
                    startTime = stage.startTime,
                    endTime = stage.endTime,
                    stage = stage.stage // Ex: "REM", "DEEP", "LIGHT", "AWAKE"
                )
            }

            SleepData(
                sessionStart = session.startTime,
                sessionEnd = session.endTime,
                durationMillis = duration,
                stages = stages
            )
        }
    }
}

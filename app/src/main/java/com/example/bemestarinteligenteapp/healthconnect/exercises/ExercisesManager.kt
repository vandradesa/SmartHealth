package com.example.bemestarinteligenteapp.healthconnect.exercises

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.example.bemestarinteligenteapp.model.ExercisesData
import java.time.Instant

class ExercisesManager(private val healthConnectClient: HealthConnectClient) {

    suspend fun readExercises(startTime: Instant, endTime: Instant): List<ExercisesData> {
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                ExerciseSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        return response.records.map { record ->
            ExercisesData(
                startTime = record.startTime,
                endTime = record.endTime,
                exerciseType = getExerciseTypeName(record.exerciseType)
            )
        }
    }

    fun getExerciseTypeName(type: Int): String {
        return when (type) {
            ExerciseSessionRecord.EXERCISE_TYPE_WALKING -> "Caminhada"
            ExerciseSessionRecord.EXERCISE_TYPE_RUNNING -> "Corrida"
            ExerciseSessionRecord.EXERCISE_TYPE_BIKING -> "Ciclismo"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER-> "Natação"
            ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_POOL-> "Natação"
            ExerciseSessionRecord.EXERCISE_TYPE_HIKING -> "Trilha"
            ExerciseSessionRecord.EXERCISE_TYPE_YOGA -> "Yoga"
            ExerciseSessionRecord.EXERCISE_TYPE_STRENGTH_TRAINING -> "Treinamento de força"
            ExerciseSessionRecord.EXERCISE_TYPE_PILATES -> "Pilates"
            ExerciseSessionRecord.EXERCISE_TYPE_ROWING_MACHINE -> "Remo indoor"
            ExerciseSessionRecord.EXERCISE_TYPE_ELLIPTICAL -> "Elíptico"
            ExerciseSessionRecord.EXERCISE_TYPE_DANCING -> "Dança"
            ExerciseSessionRecord.EXERCISE_TYPE_BOXING -> "Boxe"
            ExerciseSessionRecord.EXERCISE_TYPE_MARTIAL_ARTS -> "Artes marciais"
            ExerciseSessionRecord.EXERCISE_TYPE_TENNIS -> "Tênis"
            ExerciseSessionRecord.EXERCISE_TYPE_BASKETBALL -> "Basquete"
            ExerciseSessionRecord.EXERCISE_TYPE_SOCCER -> "Futebol"
            ExerciseSessionRecord.EXERCISE_TYPE_SKATING -> "Patinação"
            ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT -> "Outro exercício"
            else -> "Desconhecido"
        }
    }

}



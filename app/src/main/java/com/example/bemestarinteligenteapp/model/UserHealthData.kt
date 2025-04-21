package com.example.bemestarinteligenteapp.model

data class UserHealthData(
    val heartRate: Double?,         // Batimentos cardíacos (média)
    val stepCount: Int?,            // Número de passos
    val stressLevel: String?,       // Ex: "baixo", "moderado", "alto"
    val sleepDurationMinutes: Int?, // Minutos de sono
    val exerciseMinutes: Int?,      // Minutos de atividade física
    val distanceMeters: Double?,    // Distância percorrida
    val spo2: Double?               // Saturação de oxigênio no sangue (%)
)

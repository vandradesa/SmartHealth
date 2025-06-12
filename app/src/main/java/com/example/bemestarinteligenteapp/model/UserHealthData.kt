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

// Relembrando a data class genérica (coloque em um arquivo comum de modelos)
data class GenericWeeklyChartData<T_Data>(
    val dayLabels: List<String>,
    val pastWeekData: List<T_Data?>,
    val currentWeekData: List<T_Data?>,
    // Adicionando campos que podem ser úteis e estavam implícitos ou no ViewModel antes
    val metricName: String? = null, // Ex: "Passos", "Calorias" (para o título, se não for passado diretamente)
    val unit: String? = null        // Ex: "passos", "kcal"
)
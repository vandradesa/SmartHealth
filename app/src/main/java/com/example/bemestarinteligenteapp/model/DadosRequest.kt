package com.example.bemestarinteligenteapp.model

data class DadosRequest(
    val date: String,  // Data da coleta dos dados ("yyyy-MM-dd")

    // Dados de saúde
    val heartRate: Double?,  // Última frequência cardíaca registrada
    val averageHeartRate: Double?,  // BPM médio do dia
    val oxygenSaturation: Double?,  // Última saturação de oxigênio registrada
    val stepsCount: Long?,  // Quantidade total de passos no dia
    val sleepDurationMillis: Long?,  // Duração total do sono em milissegundos
    val sleepQuality: String?,  // **Qualidade do sono** (exemplo: "Boa", "Ruim", "Moderada")
    val caloriesBurned: Double?,  // Calorias totais queimadas
    val activeCaloriesBurned: Double?,  // Calorias queimadas durante atividades físicas

    // Dados de exercícios
    val exercises: ArrayList<ExercisesData>?  // Lista com detalhes dos exercícios realizados
)

// Estrutura para armazenar dados de exercícios
data class ExerciseData(
    val exerciseType: String,  // Tipo de exercício (corrida, caminhada, ciclismo, etc.)
    val durationMinutes: Long?,  // Duração do exercício em minutos
    val startTime: String?,  // Horário de início do exercício (formato ISO 8601)
    val endTime: String?  // Horário de término do exercício (formato ISO 8601)
)
package com.example.bemestarinteligenteapp.viewmodel.steps

import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.*
import com.example.bemestarinteligenteapp.model.StepsData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

// No seu arquivo de ViewModel ou em um arquivo de modelos de UI
data class StepsWeeklyChartData(
    val dayLabels: List<String>,          // Ex: ["Seg", "Ter", ..., "Dom"]
    val pastWeekSteps: List<Long?>,      // Passos diários da semana passada
    val currentWeekSteps: List<Long?>    // Passos diários da semana atual
    // Você pode adicionar 'activityName = "Passos"' se for usar uma estrutura mais genérica depois
)

class StepsViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    // LiveData para os dados do gráfico semanal de passos
    private val _weeklyChartData = MutableLiveData<StepsWeeklyChartData>()
    val weeklyChartData: LiveData<StepsWeeklyChartData> get() = _weeklyChartData

    private val _stepsData = MutableLiveData<StepsData>()
    val stepsData: LiveData<StepsData> get() = _stepsData

    // Agora essa função pode receber uma data opcional
    fun loadSteps(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now()  // Se não passar a data, usa hoje
        readStepsForDate(healthConnectClient, targetDate)
    }

    private fun readStepsForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            // Define o início do dia selecionado
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()

            // Define o final do dia selecionado (23:59:59.999)
            val endTime = date.plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            val total = healthDataRepository.getStepsData(
                startTime,
                endTime
            )

            _stepsData.value = StepsData(startTime, endTime, total)
        }
    }

    fun loadWeeklyStepsData() {
        viewModelScope.launch {
            val today = LocalDate.now()
            // Use o Locale desejado para os nomes dos dias e para determinar o primeiro dia da semana
            // Locale("pt", "BR") para Português do Brasil
            val locale = Locale.getDefault() // Ou especifique um Locale, ex: Locale("pt", "BR")
            val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek // Ex: DayOfWeek.MONDAY

            val startOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
            val startOfPastWeek = startOfCurrentWeek.minusWeeks(1)

            // Gera rótulos para os dias da semana (ex: "Seg", "Ter", ...)
            // Garante que os rótulos correspondam à ordem dos dados (começando pelo primeiro dia da semana)
            val dayLabels = List(7) { i ->
                firstDayOfWeek.plus(i.toLong()).getDisplayName(TextStyle.SHORT, locale)
            }
            // Alternativamente, uma lista fixa se preferir:
            // val dayLabels = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
            // (Certifique-se que a ordem da lista fixa corresponda ao seu loop de busca de dados)

            // Busca dados para cada dia da semana passada
            val pastWeekDeferred = (0..6).map { dayIndex ->
                async { // Executa cada busca de dia em uma coroutine separada (paralelização)
                    val date = startOfPastWeek.plusDays(dayIndex.toLong())
                    getStepsForSingleDay(date)
                }
            }

            // Busca dados para cada dia da semana atual
            val currentWeekDeferred = (0..6).map { dayIndex ->
                async {
                    val date = startOfCurrentWeek.plusDays(dayIndex.toLong())
                    // Para a semana atual, não busque dados de dias futuros (serão 0)
                    if (date.isAfter(today)) {
                        0L // Retorna 0 para dias futuros
                    } else {
                        getStepsForSingleDay(date)
                    }
                }
            }

            // Espera todas as buscas assíncronas terminarem
            val pastWeekSteps = pastWeekDeferred.awaitAll()
            val currentWeekSteps = currentWeekDeferred.awaitAll()

            _weeklyChartData.value = StepsWeeklyChartData(
                dayLabels = dayLabels,
                pastWeekSteps = pastWeekSteps,
                currentWeekSteps = currentWeekSteps
            )
        }
    }

    /**
     * Função auxiliar para buscar o total de passos para um único dia.
     */
    private suspend fun getStepsForSingleDay(date: LocalDate): Long? {
        // Define o início do dia (00:00:00)
        val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        // Define o final do dia (início do próximo dia, exclusivo)
        val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return healthDataRepository.getStepsData(startTime, endTime)
    }


}

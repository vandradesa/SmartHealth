package com.example.bemestarinteligenteapp.viewmodel.sleep


import android.health.connect.datatypes.SleepSessionRecord
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.health.connect.client.HealthConnectClient
import com.example.bemestarinteligenteapp.model.SleepData
import com.example.bemestarinteligenteapp.repository.HealthDataRepository
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class SleepViewModel(
    private val healthDataRepository: HealthDataRepository
) : ViewModel() {

    private val _sleepData = MutableLiveData<List<SleepData>?>()
    val sleepData: LiveData<List<SleepData>?> get() = _sleepData

    private val _totalSleepDurationMillis = MutableLiveData<Long?>()
    val totalSleepDurationMillis: LiveData<Long?> get() = _totalSleepDurationMillis

    private val _sleepQuality = MutableLiveData<String>()
    val sleepQuality: LiveData<String> get() = _sleepQuality

    /**
     * Carrega os dados de sono para uma data específica (ou hoje, se date == null).
     */
    fun loadSleepData(healthConnectClient: HealthConnectClient, date: LocalDate? = null) {
        val targetDate = date ?: LocalDate.now() // Se não passar a data, usa hoje
        readSleepDataForDate(healthConnectClient, targetDate)
    }

    /**
     * Função interna que faz a query e calcula a duração total do sono.
     */
    private fun readSleepDataForDate(healthConnectClient: HealthConnectClient, date: LocalDate) {
        viewModelScope.launch {
            val startTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endTime = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

            val data = healthDataRepository.getSleepData(startTime, endTime)
            val sortedData = data?.sortedBy { it.sessionStart }

            _sleepData.value = sortedData

            if (!sortedData.isNullOrEmpty()) {
                val totalDuration = sortedData.sumOf { it.durationMillis }
                _totalSleepDurationMillis.value = totalDuration
                evaluateSleepQuality(sortedData)


                Log.d("SleepLogs", "Sessões de sono: ${sortedData.size}")
                Log.d("SleepLogs", "Duração total: ${totalDuration / 1000 / 60} minutos")
            } else {
                _totalSleepDurationMillis.value = null
                _sleepQuality.value = "Nenhuma sessão de sono encontrada."

                Log.d("SleepLogs", "Nenhuma sessão de sono encontrada para a data.")
            }
        }
    }

    private fun evaluateSleepQuality(sleepSessions: List<SleepData>) {
        if (sleepSessions.isEmpty()) {
            _sleepQuality.value = "Nenhum dado suficiente para avaliar a qualidade do sono."
            return
        }

        var deepSleepDuration = 0L
        var remSleepDuration = 0L
        var lightSleepDuration = 0L
        var awakeningsCount = 0

        sleepSessions.forEach { session ->
            session.stages.forEach { stage ->
                val stageDuration = stage.endTime.toEpochMilli() - stage.startTime.toEpochMilli()

                when (stage.stage) {
                    2 -> deepSleepDuration += stageDuration
                    0 -> remSleepDuration += stageDuration
                    1 -> lightSleepDuration += stageDuration
                }
            }
            awakeningsCount += session.stages.count { it.stage == 3 }
        }

        val totalSleepMillis = deepSleepDuration + remSleepDuration + lightSleepDuration
        if (totalSleepMillis <= 0) {
            _sleepQuality.value = "Não foi possível calcular a qualidade do sono devido à falta de dados de sono."
            return
        }

        val totalSleepHours = totalSleepMillis / 1000 / 60 / 60
        val deepSleepPercentage = (deepSleepDuration * 100) / totalSleepMillis
        val remSleepPercentage = (remSleepDuration * 100) / totalSleepMillis
        val lightSleepPercentage = (lightSleepDuration * 100) / totalSleepMillis

        val sleepQuality = when {
            totalSleepHours >= 7 && deepSleepPercentage in 15..20 && remSleepPercentage in 20..25 && lightSleepPercentage in 45..55 && awakeningsCount < 3 ->
                "Bom"
            totalSleepHours >= 6 && deepSleepPercentage in 10..15 && remSleepPercentage in 15..20 && lightSleepPercentage in 40..60 && awakeningsCount < 5 ->
                "Razoável"
            else -> "Ruim"
        }

        val mainIssue = when {
            deepSleepPercentage < 15 -> "Pouco sono profundo. Tente evitar luz azul e cafeína antes de dormir."
            remSleepPercentage < 20 -> "Sono REM abaixo do ideal. Pode ser causado por estresse ou falta de relaxamento antes de dormir."
            lightSleepPercentage > 55 -> "Sono leve excessivo. Talvez sua rotina esteja irregular ou o ambiente não seja propício para o descanso."
            awakeningsCount > 5 -> "Muitos despertares durante a noite. Verifique se há ruídos ou fatores externos afetando seu sono."
            totalSleepHours < 6 -> "Sono insuficiente. Tente dormir mais cedo e estabelecer uma rotina consistente."
            else -> "Seu sono parece bem equilibrado! Continue mantendo uma boa rotina de descanso."
        }

        _sleepQuality.value = "Qualidade do Sono: **$sleepQuality**\nPrincipal Problema: $mainIssue"
    }

}

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
            val startTime = date.minusDays(1).atTime(18, 0).atZone(ZoneId.systemDefault())
                .toInstant() // 18h dia anterior
            val endTime = date.atTime(12, 0).atZone(ZoneId.systemDefault())
                .toInstant()               // 12h do dia atual

            val data = healthDataRepository.getSleepData(startTime, endTime)
            Log.d("SleepLogs", "Dados brutos recebidos: $data")

            val sortedData = data?.sortedBy { it.sessionStart }

            _sleepData.value = sortedData

            if (!sortedData.isNullOrEmpty()) {
                val filteredSessions = filterOverlappingSessions(sortedData)
                val totalDuration = filteredSessions.sumOf { it.durationMillis }
                _totalSleepDurationMillis.value = totalDuration
                evaluateSleepQuality(filteredSessions)


                Log.d("SleepLogs", "Sessões de sono após filtro: ${filteredSessions.size}")
                Log.d(
                    "SleepLogs",
                    "Duração total após filtro: ${totalDuration / 1000 / 60} minutos"
                )
            } else {
                _totalSleepDurationMillis.value = null
                _sleepQuality.value = "Nenhuma sessão de sono encontrada."

                Log.d("SleepLogs", "Nenhuma sessão de sono encontrada para a data.")
            }
        }
    }

    private fun filterOverlappingSessions(sessions: List<SleepData>): List<SleepData> {
        val sortedSessions = sessions.sortedBy { it.sessionStart }
        val filteredSessions = mutableListOf<SleepData>()

        for (session in sortedSessions) {
            if (filteredSessions.isEmpty()) {
                filteredSessions.add(session)
            } else {
                val lastSession = filteredSessions.last()
                // Se o início da sessão atual for antes do fim da última sessão, tem sobreposição
                if (session.sessionStart < lastSession.sessionEnd) {
                    // Mantém a sessão com maior duração
                    val lastDuration =
                        lastSession.sessionEnd.toEpochMilli() - lastSession.sessionStart.toEpochMilli()
                    val currentDuration =
                        session.sessionEnd.toEpochMilli() - session.sessionStart.toEpochMilli()

                    if (currentDuration > lastDuration) {
                        // Substitui a última sessão pela atual (maior duração)
                        filteredSessions[filteredSessions.size - 1] = session
                    }
                    // Se a duração for menor, ignora esta sessão
                } else {
                    filteredSessions.add(session)
                }
            }
        }

        return filteredSessions
    }


    private fun evaluateSleepQuality(sleepSessions: List<SleepData>) {
        if (sleepSessions.isEmpty()) {
            _sleepQuality.value = "Nenhum dado suficiente para avaliar a qualidade do sono."
            return
        }

        var deepSleepDuration = 0L
        var remSleepDuration = 0L
        var lightSleepDuration = 0L
        var sleepingDuration = 0L
        var awakeningsCount = 0

        sleepSessions.forEach { session ->
            session.stages.forEach { stage ->
                val duration = stage.endTime.toEpochMilli() - stage.startTime.toEpochMilli()
                when (stage.stage) {
                    5 -> deepSleepDuration += duration // DEEP
                    6 -> remSleepDuration += duration  // REM
                    4 -> lightSleepDuration += duration // LIGHT
                    2 -> sleepingDuration += duration // dormindo mas o estagio é desconhecido
                    3 -> awakeningsCount += 1          // AWAKE OUT OF BED
                    1 -> awakeningsCount += 1
                }
            }
        }

        val totalSleepMillis =
            deepSleepDuration + remSleepDuration + lightSleepDuration + sleepingDuration
        if (totalSleepMillis <= 0) {
            _sleepQuality.value =
                "Não foi possível calcular a qualidade do sono devido à falta de dados de sono."
            return
        }

        val totalSleepHours = totalSleepMillis.toDouble() / 1000 / 60 / 60

        Log.d("Sono", "Deep: $deepSleepDuration ms")
        Log.d("Sono", "REM: $remSleepDuration ms")
        Log.d("Sono", "Light: $lightSleepDuration ms")
        Log.d("Sono", "Total: $totalSleepMillis ms")
        Log.d("Sono", "Total horas de sono: $totalSleepHours")

        val deepSleepPercentage = (deepSleepDuration * 100) / totalSleepMillis
        val remSleepPercentage = (remSleepDuration * 100) / totalSleepMillis
        val lightSleepPercentage = (lightSleepDuration * 100) / totalSleepMillis

        Log.d("Sono", "Porcentagem Sono Profundo (Deep): $deepSleepPercentage%")
        Log.d("Sono", "Porcentagem Sono REM: $remSleepPercentage%")
        Log.d("Sono", "Porcentagem Sono Leve (Light): $lightSleepPercentage%")

        // Verificação dos problemas
        val issues = mutableListOf<String>()

        if (deepSleepPercentage !in 20..40) issues.add("sono profundo fora da faixa ideal")
        if (remSleepPercentage !in 10..30) issues.add("sono REM fora da faixa ideal")
        if (lightSleepPercentage !in 20..60) issues.add("sono leve fora da faixa ideal")
        if (awakeningsCount > 2) issues.add("muitos despertares")

        val sleepQuality = when {
            totalSleepHours >= 7 -> {
                if (issues.isEmpty()) "Bom" else "Razoável"
            }

            totalSleepHours >= 6 -> {
                if (issues.isEmpty()) "Razoável" else "Ruim"
            }

            else -> "Ruim"
        }

        // Mensagem sobre o tempo total de sono
        val sleepTimeMessage = if (totalSleepHours < 6) {
            "Sono insuficiente. Tente dormir mais cedo e manter uma rotina regular."
        } else if (totalSleepHours >= 7) {
            "Tempo de sono ideal."
        } else {
            "Tempo de sono razoável, mas pode melhorar."
        }

// Mensagem sobre problemas nos estágios do sono
        val sleepStageMessage = if (issues.isEmpty()) {
            "Não houve problemas nos estágios de sono profundo, REM ou leve."
        } else {
            when {
                issues.any { it.contains("profundo") } -> "Pouco sono profundo. Evite luz azul e estresse antes de dormir."
                issues.any { it.contains("REM") } -> "Sono REM abaixo do ideal. Pratique relaxamento antes de dormir."
                issues.any { it.contains("leve") } -> "Sono leve excessivo. Verifique ruídos ou iluminação no quarto."
                issues.any { it.contains("despertares") } -> "Você acordou muitas vezes. Verifique se há desconfortos ou barulhos."
                else -> "Seu sono apresenta pequenas irregularidades."
            }
        }

// Combina as duas mensagens para exibir
        _sleepQuality.value = "$sleepQuality\n - $sleepTimeMessage\n - $sleepStageMessage"


    }

}
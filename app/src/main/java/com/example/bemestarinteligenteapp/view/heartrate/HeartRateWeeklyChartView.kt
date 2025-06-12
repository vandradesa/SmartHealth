package com.example.bemestarinteligenteapp.view.heartrate

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModel
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModelFactory
// Imports específicos para Frequência Cardíaca

import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
// Se for usar as constantes de AppChartConstants
// import com.example.bemestarinteligenteapp.common.AppChartConstants

@Composable
fun HeartRateWeeklyChartView(
    heartRateViewModel: HeartRateViewModel = viewModel(
        factory = HeartRateViewModelFactory(
            LocalContext.current.applicationContext
        )
    )
) {
    val chartDataState by heartRateViewModel.weeklyChartData.observeAsState()

    // Cores e tamanhos do tema
    val corDoTextoPrincipalNoTema = MaterialTheme.colorScheme.onSurface.toArgb()
    val corDoTextoDoEixoNoTema = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val tamanhoTextoEixo = 12f
    val tamanhoTextoValorNaBarra = 10f
    val tamanhoTextoLegenda = 12f

    // Cores das barras (sugestão de cores diferentes para variar)
    val pastWeekBarColor = Color.parseColor("#42A5F5") // Azul
    val currentWeekBarColor = Color.parseColor("#EF5350") // Vermelho claro
    // Ou use as cores primárias do AppChartConstants, se preferir consistência total:
    // val pastWeekBarColor = AppChartConstants.COLOR_BAR_PAST_WEEK_PRIMARY
    // val currentWeekBarColor = AppChartConstants.COLOR_BAR_CURRENT_WEEK_PRIMARY


    LaunchedEffect(key1 = Unit) {
        heartRateViewModel.loadWeeklyHeartRateData()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            // text = AppChartConstants.TITLE_CHART_HEART_RATE, // Se usar constantes
            text = "Evolução Semanal da Frequência Cardíaca",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        val currentChartData = chartDataState
        if (currentChartData != null) {
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        description.text = ""
                        animateY(1000)
                        legend.isEnabled = true
                        setDrawGridBackground(false)
                        setPinchZoom(false)
                        isDoubleTapToZoomEnabled = false
                        axisRight.isEnabled = false

                        this.legend.textColor = corDoTextoPrincipalNoTema
                        this.legend.textSize = tamanhoTextoLegenda
                        this.xAxis.textColor = corDoTextoDoEixoNoTema
                        this.xAxis.textSize = tamanhoTextoEixo
                        this.axisLeft.textColor = corDoTextoDoEixoNoTema
                        this.axisLeft.textSize = tamanhoTextoEixo
                        this.axisLeft.axisMinimum = 0f // BPM geralmente não é 0, mas para um gráfico de barras é um bom início
                        // Você pode querer um mínimo mais realista, ex: 40f, se souber que os dados nunca serão menores.
                    }
                },
                update = { barChart ->
                    val entriesSemanaPassada = ArrayList<BarEntry>()
                    currentChartData.pastWeekAvgHeartRate.forEachIndexed { index, value ->
                        entriesSemanaPassada.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Int? para Float
                            )
                        )
                    }
                    val entriesSemanaAtual = ArrayList<BarEntry>()
                    currentChartData.currentWeekAvgHeartRate.forEachIndexed { index, value ->
                        entriesSemanaAtual.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Int? para Float
                            )
                        )
                    }

                    val dataSetPassada = BarDataSet(entriesSemanaPassada, /* AppChartConstants.LEGEND_LABEL_PAST_WEEK */ "Semana Passada").apply {
                        color = pastWeekBarColor
                        valueTextColor = corDoTextoPrincipalNoTema
                        valueTextSize = tamanhoTextoValorNaBarra
                    }
                    val dataSetAtual = BarDataSet(entriesSemanaAtual, /* AppChartConstants.LEGEND_LABEL_CURRENT_WEEK */ "Semana Atual").apply {
                        color = currentWeekBarColor
                        valueTextColor = corDoTextoPrincipalNoTema
                        valueTextSize = tamanhoTextoValorNaBarra
                    }

                    val barData = BarData(dataSetPassada, dataSetAtual)
                    val groupSpace = 0.1f
                    val barSpace = 0.05f
                    val barWidth = 0.4f
                    barData.barWidth = barWidth
                    barChart.data = barData

                    barChart.xAxis.apply {
                        valueFormatter = IndexAxisValueFormatter(currentChartData.dayLabels)
                        position = XAxis.XAxisPosition.BOTTOM
                        granularity = 1f
                        isGranularityEnabled = true
                        setCenterAxisLabels(true)
                        axisMinimum = 0f
                        axisMaximum = currentChartData.dayLabels.size.toFloat()
                        textColor = corDoTextoDoEixoNoTema
                        textSize = tamanhoTextoEixo
                    }

                    barChart.axisLeft.apply {
                        textColor = corDoTextoDoEixoNoTema
                        textSize = tamanhoTextoEixo
                        axisMinimum = 0f // Garante que comece em 0, mas pode ser ajustado
                        // Exemplo de ajuste dinâmico (opcional):
                        // val allValues = (currentChartData.pastWeekAvgHeartRate.filterNotNull() +
                        //                 currentChartData.currentWeekAvgHeartRate.filterNotNull())
                        // if (allValues.isNotEmpty()) {
                        //    val dataMin = allValues.minOrNull()?.toFloat() ?: 40f
                        //    axisMinimum = (dataMin - 10f).coerceAtLeast(0f)
                        // }
                    }

                    barChart.legend.apply {
                        textColor = corDoTextoPrincipalNoTema
                        textSize = tamanhoTextoLegenda
                    }

                    barChart.groupBars(0f, groupSpace, barSpace)
                    barChart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(top = 8.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Carregando dados do gráfico...",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
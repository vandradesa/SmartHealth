package com.example.bemestarinteligenteapp.view.oxygen


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
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModelFactory
// Imports do MPAndroidChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
// Se for usar as constantes de AppChartConstants
// import com.example.bemestarinteligenteapp.common.AppChartConstants

@Composable
fun OxygenSaturationWeeklyChartView(
    oxygenSaturationViewModel: OxygenSaturationViewModel = viewModel(
        factory = OxygenSaturationViewModelFactory(
            LocalContext.current.applicationContext
        )
    )
) {
    val chartDataState by oxygenSaturationViewModel.weeklyChartData.observeAsState()

    // Cores e tamanhos do tema (como nos outros gráficos)
    val corDoTextoPrincipalNoTema = MaterialTheme.colorScheme.onSurface.toArgb()
    val corDoTextoDoEixoNoTema = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val tamanhoTextoEixo = 12f
    val tamanhoTextoValorNaBarra = 10f
    val tamanhoTextoLegenda = 12f

    // Cores das barras (pode usar AppChartConstants se preferir)
    val pastWeekBarColor = Color.parseColor("#FFA726") // Laranja (Semana Passada)
    val currentWeekBarColor = Color.parseColor("#66BB6A") // Verde (Semana Atual)
    // Para Saturação de Oxigênio, talvez outras cores fossem mais temáticas, e.g., tons de azul/vermelho claro
    // val pastWeekBarColor = Color.parseColor("#42A5F5") // Azul claro
    // val currentWeekBarColor = Color.parseColor("#FF7043") // Laranja avermelhado claro


    LaunchedEffect(key1 = Unit) {
        oxygenSaturationViewModel.loadWeeklyOxygenSaturationData()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            // text = AppChartConstants.TITLE_CHART_OXYGEN_SATURATION, // Se usar constantes
            text = "Saturação de Oxigênio",
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
                        this.axisLeft.axisMinimum = 0f // SpO2 é uma porcentagem
                        // Considerar um máximo se fizer sentido, ex: this.axisLeft.axisMaximum = 100f;
                        // Ou um mínimo mais realista, ex: this.axisLeft.axisMinimum = 80f; (CUIDADO: isso pode esconder barras se houver valores menores)
                    }
                },
                update = { barChart ->
                    val entriesSemanaPassada = ArrayList<BarEntry>()
                    currentChartData.pastWeekAvgO2Saturation.forEachIndexed { index, value ->
                        entriesSemanaPassada.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Double? para Float
                            )
                        )
                    }
                    val entriesSemanaAtual = ArrayList<BarEntry>()
                    currentChartData.currentWeekAvgO2Saturation.forEachIndexed { index, value ->
                        entriesSemanaAtual.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Double? para Float
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
                        axisMinimum = 0f // Reforça o mínimo
                        // Se os valores de SpO2 forem todos altos (ex: >90%),
                        // você pode querer ajustar o axisMinimum para (ex: 85f ou 90f)
                        // para melhor visualização das variações, mas tenha cuidado
                        // para não cortar dados. Ou defina um axisMaximum = 100f.
                        // Ex:
                        // val minValue = currentChartData.pastWeekAvgO2Saturation.filterNotNull().minOrNull() ?: 90.0
                        // val maxValue = currentChartData.pastWeekAvgO2Saturation.filterNotNull().maxOrNull() ?: 100.0
                        // axisMinimum = (minValue - 5).toFloat().coerceAtLeast(0f)
                        // axisMaximum = (maxValue + 5).toFloat().coerceAtMost(100f)
                        // Isso é uma lógica mais avançada para ajuste de eixo. Por agora, 0f é o padrão.
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
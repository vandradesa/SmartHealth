package com.example.bemestarinteligenteapp.view.calories

import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize // Adicionado para centralizar o loading
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModel // MUDOU
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModelFactory // MUDOU
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesWeeklyChartData // MUDOU
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@Composable
fun CaloriesWeeklyChartView( // MUDOU O NOME DA FUNÇÃO
    caloriesViewModel: CaloriesViewModel = viewModel( // MUDOU o tipo do ViewModel
        factory = CaloriesViewModelFactory( // MUDOU a Factory
            LocalContext.current.applicationContext
        )
    )
) {
    val chartDataState by caloriesViewModel.weeklyChartData.observeAsState() // MUDOU para caloriesViewModel

    val corDoTextoPrincipalNoTema = MaterialTheme.colorScheme.onSurface.toArgb()
    val corDoTextoDoEixoNoTema = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    val tamanhoTextoEixo = 12f
    val tamanhoTextoValorNaBarra = 10f
    val tamanhoTextoLegenda = 12f

    LaunchedEffect(key1 = Unit) {
        caloriesViewModel.loadWeeklyCaloriesData() // MUDOU para o método do CaloriesViewModel
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Ajustado padding geral
    ) {
        Text(
            text = "Calorias", // MUDOU o título
            style = MaterialTheme.typography.titleLarge, // Sugestão de ajuste de estilo
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // A Column com weight(1f) foi removida daqui, assumindo que o chamador
        // cuidará do layout maior se necessário. O foco é no gráfico.
        // Se quiser o mesmo comportamento de centralização do CircularProgressIndicator,
        // veja abaixo no 'else' branch.

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
                        // Aplicar cores do tema aqui também
                        this.legend.textColor = corDoTextoPrincipalNoTema
                        this.legend.textSize = tamanhoTextoLegenda
                        this.xAxis.textColor = corDoTextoDoEixoNoTema
                        this.xAxis.textSize = tamanhoTextoEixo
                        this.axisLeft.textColor = corDoTextoDoEixoNoTema
                        this.axisLeft.textSize = tamanhoTextoEixo
                        this.axisLeft.axisMinimum = 0f
                    }
                },
                update = { barChart ->
                    val entriesSemanaPassada = ArrayList<BarEntry>()
                    // MUDOU para currentChartData.pastWeekCalories
                    currentChartData.pastWeekCalories.forEachIndexed { index, value ->
                        entriesSemanaPassada.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Double? para Float
                            )
                        )
                    }
                    val entriesSemanaAtual = ArrayList<BarEntry>()
                    // MUDOU para currentChartData.currentWeekCalories
                    currentChartData.currentWeekCalories.forEachIndexed { index, value ->
                        entriesSemanaAtual.add(
                            BarEntry(
                                index.toFloat(),
                                value?.toFloat() ?: 0f // Converte Double? para Float
                            )
                        )
                    }

                    val dataSetPassada =
                        BarDataSet(entriesSemanaPassada, "Semana Passada").apply {
                            color = Color.parseColor("#FFA726") // Mesmas cores do gráfico de passos
                            valueTextColor = corDoTextoPrincipalNoTema
                            valueTextSize = tamanhoTextoValorNaBarra
                        }
                    val dataSetAtual = BarDataSet(entriesSemanaAtual, "Semana Atual").apply {
                        color = Color.parseColor("#66BB6A") // Mesmas cores
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
                        axisMinimum = 0f
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
            // Mantém o indicador de carregamento centralizado
            Column(
                modifier = Modifier.fillMaxSize(), // Ocupa o espaço disponível para centralizar
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
package com.example.bemestarinteligenteapp.view.steps

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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsWeeklyChartData

@Composable
fun StepsWeeklyChartView(
    stepsViewModel: StepsViewModel = viewModel(
        factory = StepsViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    val chartDataState by stepsViewModel.weeklyChartData.observeAsState()

    // 1. As cores são capturadas aqui, no escopo @Composable
    val corDoTextoPrincipal = MaterialTheme.colorScheme.onSurface.toArgb()
    val corDoTextoDoEixo = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    LaunchedEffect(key1 = Unit) {
        stepsViewModel.loadWeeklyStepsData()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Passos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

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
                        axisLeft.axisMinimum = 0f
                    }
                },
                update = { barChart ->
                    // 2. Passamos os valores de cor já resolvidos para a função
                    setupBarChartWithData(
                        barChart,
                        currentChartData,
                        corDoTextoPrincipal,
                        corDoTextoDoEixo
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 16.dp)
            )
        } else {
            Column(
                modifier = Modifier.height(300.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Carregando dados do gráfico...",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// 3. A função agora recebe as cores como Int e não precisa saber sobre o MaterialTheme
private fun setupBarChartWithData(
    barChart: BarChart,
    data: StepsWeeklyChartData,
    corTextoPrincipal: Int,
    corTextoEixo: Int
) {
    val entriesSemanaPassada = ArrayList<BarEntry>()
    data.pastWeekSteps.forEachIndexed { index, value ->
        entriesSemanaPassada.add(BarEntry(index.toFloat(), value?.toFloat() ?: 0f))
    }

    val entriesSemanaAtual = ArrayList<BarEntry>()
    data.currentWeekSteps.forEachIndexed { index, value ->
        entriesSemanaAtual.add(BarEntry(index.toFloat(), value?.toFloat() ?: 0f))
    }

    val dataSetPassada = BarDataSet(entriesSemanaPassada, "Semana Passada").apply {
        color = Color.parseColor("#FFA726")
        valueTextColor = corTextoPrincipal
        valueTextSize = 10f
    }

    val dataSetAtual = BarDataSet(entriesSemanaAtual, "Semana Atual").apply {
        color = Color.parseColor("#66BB6A")
        valueTextColor = corTextoPrincipal
        valueTextSize = 10f
    }

    val barData = BarData(dataSetPassada, dataSetAtual)
    val groupSpace = 0.1f
    val barSpace = 0.05f
    val barWidth = 0.4f
    barData.barWidth = barWidth
    barChart.data = barData

    barChart.xAxis.apply {
        valueFormatter = IndexAxisValueFormatter(data.dayLabels)
        position = XAxis.XAxisPosition.BOTTOM
        granularity = 1f
        isGranularityEnabled = true
        setCenterAxisLabels(true)
        axisMinimum = 0f
        axisMaximum = data.dayLabels.size.toFloat()
        textColor = corTextoEixo
        textSize = 12f
    }

    barChart.axisLeft.apply {
        textColor = corTextoEixo
        textSize = 12f
        axisMinimum = 0f
    }

    barChart.legend.apply {
        textColor = corTextoPrincipal
        textSize = 12f
    }

    barChart.groupBars(0f, groupSpace, barSpace)
    barChart.invalidate()
}
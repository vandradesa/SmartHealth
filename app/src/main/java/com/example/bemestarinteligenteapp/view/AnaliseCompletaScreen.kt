package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemestarinteligenteapp.view.calories.CaloriesWeeklyChartView
import com.example.bemestarinteligenteapp.view.oxygen.OxygenSaturationWeeklyChartView
import com.example.bemestarinteligenteapp.view.steps.StepsWeeklyChartView

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.example.bemestarinteligenteapp.view.heartrate.HeartRateWeeklyChartView
import kotlinx.coroutines.launch


// import com.example.bemestarinteligenteapp.view.steps.StepsWeeklyChartView
// import com.example.bemestarinteligenteapp.view.sleep.SleepWeeklyChartView
// import com.example.bemestarinteligenteapp.view.heartrate.HeartRateWeeklyChartView

@Composable
fun AnaliseCompletaScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    // A Column rolável é a chave para empilhar todos os gráficos
    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Usando o estado que criamos
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Análise Semanal",
            style = MaterialTheme.typography.headlineMedium, // Estilo de destaque para o título
            modifier = Modifier.padding(horizontal = 16.dp) // Alinha com o padding dos gráficos
        )
        Spacer(modifier = Modifier.height(24.dp)) // Espaço entre o título e o primeiro gráfico
        // Cada gráfico é simplesmente chamado em sequência.

        // 1. Gráfico de Calorias
        CaloriesWeeklyChartView()

        // Um divisor para separar visualmente os gráficos
        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))


        // 2. Gráfico de Passos (Exemplo)
        StepsWeeklyChartView()

        // Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))

        // 3. Gráfico de Sono (Exemplo)
        // SleepWeek()

         Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))

         //4. Gráfico de Frequência Cardíaca (Exemplo)
         HeartRateWeeklyChartView()

        Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp))

        // 5. Gráfico de Saturação de O2
        OxygenSaturationWeeklyChartView()


        // Adicione quantos gráficos e divisores forem necessários.
        Spacer(modifier = Modifier.height(16.dp)) // Espaço extra no final
    }

        val showScrollIndicator by remember {
            derivedStateOf { scrollState.value < 10 && scrollState.maxValue > 0 }
        }

        AnimatedVisibility(
            visible = showScrollIndicator,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            exit = fadeOut()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // Dentro do clique, iniciamos uma corrotina para animar a rolagem
                    scope.launch {
                        // Rola a tela suavemente para baixo em 800 pixels
                        scrollState.animateScrollTo(800)
                    }
                }
            ) {
                Text(
                    "Role para ver mais",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Rolar para baixo",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}
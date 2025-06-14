package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bemestarinteligenteapp.BuildConfig
import com.example.bemestarinteligenteapp.viewmodel.deepSeek.DeepSeekViewModel
import com.example.bemestarinteligenteapp.viewmodel.deepSeek.DeepSeekViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.deepSeek.ReportUiState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepSeekScreen(
    navController: NavController,
    healthDataPrompt: String,
    factory: DeepSeekViewModelFactory // Recebemos a Factory para criar o ViewModel
) {
    // Usamos a factory para criar a instância da sua DeepSeekViewModel
    val viewModel: DeepSeekViewModel = viewModel(factory = factory)

    // Dispara a geração do relatório uma única vez
    LaunchedEffect(key1 = Unit) {
        viewModel.enviarMensagem(BuildConfig.API_KEY, healthDataPrompt)
    }

    val uiState by viewModel.uiState.collectAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatório com IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        // O 'when' reage aos estados emitidos pelo seu ViewModel
        when (val state = uiState) {
            is ReportUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(16.dp))
                        Text("Analisando seus dados...")
                    }
                }
            }
            is ReportUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Análise e Recomendações",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Divider(Modifier.padding(vertical = 8.dp))
                            Text(state.report, lineHeight = 24.sp)
                        }
                    }
                }
            }
            is ReportUiState.Error -> {
                Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Ocorreu um Erro",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(state.message, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
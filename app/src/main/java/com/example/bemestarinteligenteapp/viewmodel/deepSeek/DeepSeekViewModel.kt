package com.example.bemestarinteligenteapp.viewmodel.deepSeek

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.repository.DeepSeekRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ReportUiState {
    object Loading : ReportUiState
    data class Success(val report: String) : ReportUiState
    data class Error(val message: String) : ReportUiState
}

class DeepSeekViewModel(private val repository: DeepSeekRepository) : ViewModel() {



    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun enviarMensagem(apiKey: String, mensagem: String) {
        viewModelScope.launch {
            // Informa a UI que estamos carregando
            _uiState.value = ReportUiState.Loading
            try {
                // Sua lógica original, 100% aproveitada
                val response = repository.enviarMensagem(apiKey, mensagem)
                val reportContent = response.choices.firstOrNull()?.message?.content ?: "Sem resposta da IA."
                // Informa a UI que tivemos sucesso e envia o relatório
                _uiState.value = ReportUiState.Success(reportContent)

            } catch (e: Exception) {
                // Informa a UI que ocorreu um erro
                _uiState.value = ReportUiState.Error("Erro ao se comunicar com a IA: ${e.message}")
            }
        }
    }
}
package com.example.bemestarinteligenteapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.repository.DeepSeekRepository
import kotlinx.coroutines.launch



class DeepSeekViewModel(private val repository: DeepSeekRepository) : ViewModel() {

    private val _respostaLiveData = MutableLiveData<String>()
    val respostaLiveData: LiveData<String> = _respostaLiveData

    fun enviarMensagem(apiKey: String, mensagem: String) {
        viewModelScope.launch {
            try {
                val response = repository.enviarMensagem(apiKey, mensagem)
                _respostaLiveData.value = response.choices.firstOrNull()?.message?.content ?: "Sem resposta"
            } catch (e: Exception) {
                _respostaLiveData.value = "Erro: ${e.message}"
            }
        }
    }
}

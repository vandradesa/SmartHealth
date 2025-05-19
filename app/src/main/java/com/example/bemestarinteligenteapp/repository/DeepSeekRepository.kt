package com.example.bemestarinteligenteapp.repository

import com.example.bemestarinteligenteapp.remote.ChatRequest
import com.example.bemestarinteligenteapp.remote.ChatResponse
import com.example.bemestarinteligenteapp.remote.DeepSeekApiService
import com.example.bemestarinteligenteapp.remote.Message


class DeepSeekRepository(private val apiService: DeepSeekApiService) {
    suspend fun enviarMensagem(apiKey: String, mensagem: String): ChatResponse {
        val request = ChatRequest(
            model = "deepseek/deepseek-chat:free",
            messages = listOf(
                Message("system", "Você é um assistente útil."),
                Message("user", mensagem)
            ),
            stream = false
        )
        return apiService.chatCompletion(request)
    }
}




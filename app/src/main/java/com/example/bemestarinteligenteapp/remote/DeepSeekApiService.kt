package com.example.bemestarinteligenteapp.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Call

data class Message(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false
)

data class ChatResponse(
    // Defina os campos conforme a resposta da API
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

interface DeepSeekApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(
       @Body request: ChatRequest
    ): ChatResponse
}



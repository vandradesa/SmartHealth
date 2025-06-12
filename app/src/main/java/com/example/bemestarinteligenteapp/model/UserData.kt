package com.example.bemestarinteligenteapp.model
import androidx.annotation.Keep

@Keep
data class UserData(
    val nomeCompleto: String = "",
    val email: String = "",
    val genero: String = "",
    val dataNascimento: String = ""
)

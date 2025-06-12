package com.example.bemestarinteligenteapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch // Para viewModelScope.launch sem await
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Sinaliza sucesso no login para a UI poder navegar
    private val _loginSuccessEvent = MutableStateFlow(false)
    val loginSuccessEvent: StateFlow<Boolean> = _loginSuccessEvent.asStateFlow()

    // --- ESTADOS PARA REDEFINIÇÃO DE SENHA ---
    private val _isLoadingResetPassword = MutableStateFlow(false)
    val isLoadingResetPassword: StateFlow<Boolean> = _isLoadingResetPassword.asStateFlow()

    // Sinaliza se o e-mail foi enviado com sucesso (true) ou se houve falha (false)
    // null indica o estado inicial (nenhuma operação tentada ainda)
    private val _passwordResetEmailSentStatus = MutableStateFlow<Boolean?>(null)
    val passwordResetEmailSentStatus: StateFlow<Boolean?> = _passwordResetEmailSentStatus.asStateFlow()

    private val _passwordResetError = MutableStateFlow<String?>(null)
    val passwordResetError: StateFlow<String?> = _passwordResetError.asStateFlow()

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginError.value = "Email e senha não podem estar vazios."
            return
        }
        _isLoading.value = true
        _loginError.value = null
        _loginSuccessEvent.value = false // Reseta o evento

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    _loginSuccessEvent.value = true
                } else {
                    // Falha no login
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidUserException -> {
                            _loginError.value = "Usuário não encontrado ou desabilitado."
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            _loginError.value = "Senha incorreta. Tente novamente."
                        }
                        else -> {
                            _loginError.value = "Falha no login: ${exception?.localizedMessage ?: "Erro desconhecido"}"
                        }
                    }
                }
                _isLoading.value = false
            }
    }

    // Chamado pela UI quando a mensagem de erro for exibida e tratada
    fun clearLoginError() {
        _loginError.value = null
    }

    // Chamado pela UI após a navegação ter ocorrido
    fun resetLoginSuccessEvent() {
        _loginSuccessEvent.value = false
    }
    // --- FUNÇÃO PARA ENVIAR E-MAIL DE REDEFINIÇÃO DE SENHA ---
    fun sendPasswordResetEmail(email: String) {
        if (email.isBlank()) {
            _passwordResetError.value = "O campo de e-mail não pode estar vazio."
            _passwordResetEmailSentStatus.value = false // Indica falha
            return
        }

        viewModelScope.launch {
            _isLoadingResetPassword.value = true
            _passwordResetError.value = null      // Limpa erro anterior
            _passwordResetEmailSentStatus.value = null // Reseta o status do evento

            try {
                Log.d("AuthViewModel", "Tentando enviar e-mail de reset para: $email")
                auth.sendPasswordResetEmail(email).await()
                Log.d("AuthViewModel", "E-mail de reset enviado com sucesso para: $email (do ponto de vista do Firebase)")
                _passwordResetEmailSentStatus.value = true // Sucesso no envio (Firebase não retorna erro se e-mail não existe)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Falha ao enviar e-mail de reset para: $email", e)
                // Captura exceções como formato de e-mail inválido ou problemas de rede.
                // O Firebase não lança exceção se o e-mail não estiver registrado.
                _passwordResetError.value = e.localizedMessage ?: "Ocorreu um erro ao enviar o e-mail."
                _passwordResetEmailSentStatus.value = false // Indica falha
            } finally {
                _isLoadingResetPassword.value = false
            }
        }
    }

    // --- FUNÇÃO PARA LIMPAR O STATUS DA REDEFINIÇÃO DE SENHA (APÓS A UI MOSTRAR A MENSAGEM) ---
    fun clearPasswordResetStatus() {
        _passwordResetEmailSentStatus.value = null
        _passwordResetError.value = null
    }


}


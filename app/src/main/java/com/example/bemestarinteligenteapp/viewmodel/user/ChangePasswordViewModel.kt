package com.example.bemestarinteligenteapp.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.repository.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _changeSuccess = MutableStateFlow(false)
    val changeSuccess: StateFlow<Boolean> = _changeSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository.changeUserPassword(currentPassword, newPassword)
                _changeSuccess.value = true
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.w("ChangePasswordVM", "Erro de credencial: Senha atual incorreta.", e)
                _error.value = "A senha atual está incorreta."
            } catch (e: Exception) {
                Log.e("ChangePasswordVM", "Erro genérico ao alterar senha.", e)
                _error.value = "Ocorreu um erro. Tente novamente."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetSuccessEvent() {
        _changeSuccess.value = false
    }
}
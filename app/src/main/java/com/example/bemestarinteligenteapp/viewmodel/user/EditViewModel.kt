package com.example.bemestarinteligenteapp.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bemestarinteligenteapp.model.UserData
import com.example.bemestarinteligenteapp.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditViewModel(
    // Injetar o repositório é uma boa prática para facilitar testes e manutenção
    private val userRepository: UserRepository = UserRepository() // Em um app real, use injeção de dependência (Hilt, Koin)
) : ViewModel() {

    // --- Estados Privados (só o ViewModel modifica) ---

    private val _userData = MutableStateFlow<UserData?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _updateSuccess = MutableStateFlow(false)
    private val _updateError = MutableStateFlow<String?>(null)

    // --- Estados Públicos (a UI apenas observa) ---

    val userData: StateFlow<UserData?> = _userData.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()
    val updateError: StateFlow<String?> = _updateError.asStateFlow()

    /**
     * Carrega os dados do usuário do repositório.
     * Chamado uma vez quando a tela é iniciada.
     */
    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simula uma chamada de rede ou busca no banco de dados
                val user = userRepository.getLoggedInUserData()
                _userData.value = user
            } catch (e: Exception) {
                _updateError.value = "Falha ao carregar seus dados. Tente novamente."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Atualiza os dados do usuário no repositório.
     */
    fun updateUserData(
        nomeCompleto: String,
        email: String,
        genero: String,
        dataNascimento: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedUserData = UserData(
                    nomeCompleto = nomeCompleto,
                    email = email,
                    genero = genero,
                    dataNascimento = dataNascimento
                )
                // Simula o envio dos dados atualizados para o servidor/banco
                userRepository.updateUserData(updatedUserData)
                _updateSuccess.value = true // Sinaliza sucesso para a UI

            } catch (e: Exception) {
                _updateError.value = "Não foi possível salvar as alterações."
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Limpa a mensagem de erro depois de ser exibida na UI (ex: Snackbar).
     */
    fun clearUpdateError() {
        _updateError.value = null
    }

    /**
     * Reseta o evento de sucesso para evitar que ele seja disparado novamente (ex: em uma rotação de tela).
     */
    fun resetUpdateSuccessEvent() {
        _updateSuccess.value = false
    }
}
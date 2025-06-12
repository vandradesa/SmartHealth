package com.example.bemestarinteligenteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _signUpError = MutableStateFlow<String?>(null)
    val signUpError: StateFlow<String?> = _signUpError.asStateFlow()

    // Evento para sinalizar sucesso no cadastro
    private val _signUpSuccessEvent = MutableStateFlow(false)
    val signUpSuccessEvent: StateFlow<Boolean> = _signUpSuccessEvent.asStateFlow()

    fun signUpUser(
        email: String,
        senha: String,
        nomeCompleto: String,
        genero: String,          // Corrigido: nome do parâmetro e tipo
        dataNascimento: String
        // Você pode adicionar outros campos aqui se precisar salvá-los
        // no perfil do usuário ou em um banco de dados (Firestore/Realtime Database)
        // Por exemplo: genero: String, dataNascimento: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _signUpError.value = null
            try {
                // 1. Criar o usuário com email e senha
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, senha).await()
                val user = authResult.user

                if (user != null) {
                    // 2. (Opcional, mas recomendado) Atualizar o perfil do usuário com o nome
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(nomeCompleto)
                        // Você também pode adicionar uma photoUrl aqui se tiver uma
                        // .setPhotoUri(Uri.parse("url_da_foto"))
                        .build()

                    user.updateProfile(profileUpdates).await() // Espera a atualização do perfil

                    // 3. Salvar dados adicionais do usuário no Cloud Firestore
                    val userProfileData = hashMapOf(
                        "uid" to user.uid, // Opcional, já que será o ID do documento
                        "nomeCompleto" to nomeCompleto,
                        "email" to email, // Redundante com Auth, mas pode ser útil ter no documento
                        "genero" to genero,
                        "dataNascimento" to dataNascimento // Salva como String "dd/MM/yyyy"
                        // Para melhor consulta/ordenação, você pode querer salvar como Timestamp do Firestore:
                        // "dataNascimentoTimestamp" to convertStringToFirebaseTimestamp(dataNascimento)
                    )

                    // Salva os dados na coleção "users" com o documento ID sendo o UID do usuário
                    Firebase.firestore.collection("users").document(user.uid)
                        .set(userProfileData)
                        .await() // Espera a conclusão da escrita no Firestore


                    _isLoading.value = false
                    _signUpSuccessEvent.value = true // Sinaliza sucesso
                } else {
                    _isLoading.value = false
                    _signUpError.value = "Não foi possível criar o usuário. Tente novamente."
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                _isLoading.value = false
                _signUpError.value = "Este e-mail já está cadastrado."
            } catch (e: FirebaseAuthWeakPasswordException) {
                _isLoading.value = false
                _signUpError.value = "Senha muito fraca. Use pelo menos 6 caracteres."
            } catch (e: Exception) {
                _isLoading.value = false
                _signUpError.value = "Erro ao cadastrar: ${e.localizedMessage ?: "Tente novamente."}"
                e.printStackTrace() // Log para depuração
            }
        }
    }

    fun clearSignUpError() {
        _signUpError.value = null
    }

    fun resetSignUpSuccessEvent() {
        _signUpSuccessEvent.value = false
    }
}
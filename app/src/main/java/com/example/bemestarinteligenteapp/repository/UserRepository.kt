package com.example.bemestarinteligenteapp.repository

import android.util.Log
import com.example.bemestarinteligenteapp.model.UserData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // O nome da sua coleção no Firestore onde os dados dos usuários são guardados
    private val usersCollection = db.collection("users")

    /**
     * Altera a senha do usuário no Firebase Authentication.
     * Exige reautenticação com a senha atual por segurança.
     * Busca os dados do usuário atualmente logado no Firestore.
     * @param currentPassword A senha atual do usuário para verificação.
     * @param newPassword A nova senha desejada.
     * @throws IllegalStateException se o usuário não estiver logado.
     * @throws FirebaseAuthInvalidCredentialsException se a senha atual estiver incorreta.
     * @throws Exception para outras falhas.
    */
    suspend fun getLoggedInUserData(): UserData {
        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException("Usuário não está logado.")

        // Busca o documento no Firestore usando o UID do usuário como ID do documento
        val documentSnapshot = usersCollection.document(firebaseUser.uid).get().await()

        if (documentSnapshot.exists()) {
            // Converte o documento do Firestore diretamente para a sua classe UserData
            // O '.toObject' pode retornar nulo se a conversão falhar.
            return documentSnapshot.toObject(UserData::class.java)
                ?: throw Exception("Falha ao converter os dados do usuário.")
        } else {
            // Este caso pode acontecer se um usuário foi criado no Auth mas seu documento
            // não foi criado no Firestore.
            throw Exception("Perfil do usuário não encontrado no banco de dados.")
        }
    }

    /**
     * Atualiza os dados do usuário no Firestore e no perfil do Firebase Authentication.
     * @param newUserData Os novos dados a serem salvos.
     */
    suspend fun updateUserData(newUserData: UserData) {
        val firebaseUser = auth.currentUser
            ?: throw IllegalStateException("Usuário não está logado.")

        // --- PASSO 1: Atualizar dados no Cloud Firestore ---
        // Atualiza (ou cria, se não existir) o documento com os novos dados
        usersCollection.document(firebaseUser.uid).set(newUserData).await()
        Log.d("UserRepository", "Dados atualizados com sucesso no Firestore.")

        // --- PASSO 2: Atualizar dados no Firebase Authentication ---
        // O Firebase Auth tem seu próprio perfil (nome de exibição, email, etc.)

        // Atualiza o nome de exibição (displayName)
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newUserData.nomeCompleto)
            .build()
        firebaseUser.updateProfile(profileUpdates).await()
        Log.d("UserRepository", "Nome de exibição atualizado no Firebase Auth.")

        // Atualiza o e-mail se ele foi alterado
        if (firebaseUser.email != newUserData.email) {
            firebaseUser.updateEmail(newUserData.email).await()
            Log.d("UserRepository", "E-mail atualizado no Firebase Auth.")
            // NOTA: A atualização de e-mail é uma operação sensível e pode
            // exigir que o usuário faça login novamente.
        }
    }

    suspend fun changeUserPassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
            ?: throw IllegalStateException("Usuário não está logado para alterar a senha.")

        // Passo 1: Criar a credencial com a senha atual para reautenticação.
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        // Passo 2: Reautenticar o usuário. O Firebase fará a verificação da senha atual.
        // Se a senha estiver errada, esta linha lançará uma exceção.
        user.reauthenticate(credential).await()
        Log.d("UserRepository", "Usuário reautenticado com sucesso.")

        // Passo 3: Se a reautenticação passou, atualizar para a nova senha.
        user.updatePassword(newPassword).await()
        Log.d("UserRepository", "Senha alterada com sucesso no Firebase.")
    }
}
package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bemestarinteligenteapp.R
import com.example.bemestarinteligenteapp.viewmodel.user.ChangePasswordViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    changePasswordViewModel: ChangePasswordViewModel = viewModel()
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }

    var isCurrentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isNewPasswordVisible by rememberSaveable { mutableStateOf(false) }

    var currentPasswordError by remember { mutableStateOf<String?>(null) }
    var newPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val isLoading by changePasswordViewModel.isLoading.collectAsState()
    val error by changePasswordViewModel.error.collectAsState()
    val success by changePasswordViewModel.changeSuccess.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efeito para mostrar Snackbar de erro do ViewModel
    LaunchedEffect(error) {
        error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
            changePasswordViewModel.clearError()
        }
    }

    // Efeito para mostrar sucesso e navegar de volta
    LaunchedEffect(success) {
        if (success) {
            scope.launch {
                snackbarHostState.showSnackbar("Senha alterada com sucesso!")
            }
            navController.popBackStack()
            changePasswordViewModel.resetSuccessEvent()
        }
    }

    fun validateFields(): Boolean {
        currentPasswordError = if (currentPassword.isBlank()) "Campo obrigatório" else null
        newPasswordError = if (newPassword.isBlank()) "Campo obrigatório" else if (newPassword.length < 6) "A senha deve ter no mínimo 6 caracteres" else null
        confirmPasswordError = if (confirmNewPassword != newPassword) "As senhas não coincidem" else null

        return currentPasswordError == null && newPasswordError == null && confirmPasswordError == null
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    // A Row engloba tanto a imagem quanto o texto
                    Column(
                        // Ocupa toda a largura para permitir o alinhamento
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {



                        // 3. O Texto
                        Text("Alterar Senha")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            // Campo Senha Atual
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it; currentPasswordError = null },
                label = { Text("Senha Atual") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isCurrentPasswordVisible = !isCurrentPasswordVisible }) {
                        Icon(if (isCurrentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle visibility")
                    }
                },
                isError = currentPasswordError != null,
                supportingText = { currentPasswordError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Nova Senha
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it; newPasswordError = null },
                label = { Text("Nova Senha") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                        Icon(if (isNewPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle visibility")
                    }
                },
                isError = newPasswordError != null,
                supportingText = { newPasswordError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo Confirmar Nova Senha
            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it; confirmPasswordError = null },
                label = { Text("Confirmar Nova Senha") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPasswordError != null,
                supportingText = { confirmPasswordError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Botão Salvar
            Button(
                onClick = {
                    if (validateFields()) {
                        changePasswordViewModel.changePassword(currentPassword, newPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("SALVAR NOVA SENHA")
                }
            }
        }
    }
}
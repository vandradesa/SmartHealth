package com.example.bemestarinteligenteapp.view // Seu pacote

import com.example.bemestarinteligenteapp.R
import android.util.Patterns
import androidx.compose.foundation.Image

import androidx.compose.material3.CircularProgressIndicator // Novo import
import androidx.compose.material3.SnackbarHost // Novo import
import androidx.compose.material3.SnackbarHostState // Novo import
import androidx.compose.runtime.LaunchedEffect // Novo import
import androidx.compose.runtime.collectAsState // Novo import
import androidx.compose.runtime.getValue // Novo import
import androidx.compose.runtime.remember // Novo import
import androidx.compose.runtime.rememberCoroutineScope // Novo import
import androidx.lifecycle.viewmodel.compose.viewModel // Novo import
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController // << NOVO: Importar NavController
import com.example.bemestarinteligenteapp.ui.theme.InputBackground
import com.example.bemestarinteligenteapp.viewmodel.user.LoginViewModel

import kotlinx.coroutines.launch

// Se você tiver um arquivo Theme.kt, importe-o.
// Ex: import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartHealthLoginScreenLight(
    navController: NavController,
    onForgotPasswordClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    showSuccessMessage: Boolean,
    loginViewModel: LoginViewModel = viewModel() // Injeta o ViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // Observa os estados do ViewModel
    val isLoading by loginViewModel.isLoading.collectAsState()
    val loginError by loginViewModel.loginError.collectAsState()
    val loginSuccessEvent by loginViewModel.loginSuccessEvent.collectAsState()

    // Para exibir Snackbars de erro
    //val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val accentColor = MaterialTheme.colorScheme.primary
    val inputBackground = InputBackground
    val snackbarHostState = remember { SnackbarHostState() }


    // 2. Use um LaunchedEffect para mostrar a mensagem QUANDO a tela for carregada com o parâmetro
    if (showSuccessMessage) {
        LaunchedEffect(Unit) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Cadastro realizado com sucesso!",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }


    // Efeito para mostrar Snackbar quando houver erro
    LaunchedEffect(loginError) {
        loginError?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMsg,
                    duration = SnackbarDuration.Short
                )
            }
            loginViewModel.clearLoginError() // Limpa o erro após mostrar
        }
    }

    // Efeito para navegar quando o login for bem-sucedido
    LaunchedEffect(loginSuccessEvent) {
        if (loginSuccessEvent) {
            navController.navigate(AppDestinations.DASHBOARD_ROUTE) {
                popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                launchSingleTop = true
            }
            loginViewModel.resetLoginSuccessEvent() // Reseta o evento após navegar
        }
    }

    var localEmailError by remember { mutableStateOf<String?>(null) }
    var localPasswordError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Surface(
            modifier = modifier.fillMaxSize().padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) { // Box para SnackbarHost
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        // Carrega a imagem da pasta drawable
                        painter = painterResource(id = R.drawable.logo_smarthealth), // 👈 Use o nome do seu arquivo aqui

                        // Texto para acessibilidade (importante para leitores de tela)
                        contentDescription = "Logo da Smart Health",

                        // Modificadores para tamanho e espaçamento
                        modifier = Modifier
                            .size(250.dp) // 🎨 Defina o tamanho que desejar
                            .clip(CircleShape)
                            .padding(bottom = 48.dp) // Mantém o espaçamento que você já tinha
                    )

                  /*
                    Text(
                        text = "Smart Health", // Placeholder do Logo
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor, // Usando accentColor como exemplo para logoTextColor
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                    */


                    Text(
                        text = "Bem-vindo(a)!",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Faça login para acessar seus insights.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it.trim()
                            localEmailError = null
                        },
                        label = { Text("Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        isError = localEmailError != null || loginError != null, // Erro se local OU do ViewModel
                        supportingText = {
                            if (localEmailError != null) {
                                Text(localEmailError!!)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            //unfocusedContainerColor = inputBackground,
                            //focusedContainerColor = inputBackground,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = accentColor,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error,
                            errorCursorColor = MaterialTheme.colorScheme.error,
                            errorSupportingTextColor = MaterialTheme.colorScheme.error
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            localPasswordError = null
                        },
                        label = { Text("Senha") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) "Esconder senha" else "Mostrar senha"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(30.dp),
                        isError = localPasswordError != null || loginError != null, // Erro se local OU do ViewModel
                        supportingText = {
                            if (localPasswordError != null) {
                                Text(localPasswordError!!)
                            }
                        }, // Mostra erro no campo se houver
                        colors = OutlinedTextFieldDefaults.colors(
                            //unfocusedContainerColor = inputBackground,
                            //focusedContainerColor = inputBackground,
                            // Define a cor do texto que o usuário digita
                            focusedTextColor = MaterialTheme.colorScheme.onSurface, // Use uma cor de contraste
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),

                            // Define a cor do rótulo ("Email", "Senha")
                            //focusedLabelColor = accentColor, // Cor do rótulo quando o campo está focado
                            //unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), // Cor quando não focado
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = accentColor,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            errorLabelColor = MaterialTheme.colorScheme.error,
                            errorCursorColor = MaterialTheme.colorScheme.error,
                            errorSupportingTextColor = MaterialTheme.colorScheme.error
                        )
                    )

                    TextButton(
                        onClick = onForgotPasswordClicked,
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    ) {
                        Text("Esqueceu a senha?", color = accentColor)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Limpa erros anteriores do ViewModel antes de uma nova tentativa, se desejar
                            // loginViewModel.clearLoginError() // Opcional, pois o LaunchedEffect já faz isso depois

                            var emailValido = true
                            var senhaValida = true

                            // Validação do E-mail
                            if (email.isBlank()) {
                                localEmailError = "E-mail é obrigatório."
                                emailValido = false
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                localEmailError = "Formato de e-mail inválido."
                                emailValido = false
                            } else {
                                localEmailError =
                                    null // Garante que está limpo se passou na validação local
                            }

                            // Validação da Senha
                            if (password.isBlank()) {
                                localPasswordError = "Senha é obrigatória."
                                senhaValida = false
                            } else if (password.length < 6) {
                                localPasswordError = "Senha deve ter pelo menos 6 caracteres."
                                senhaValida = false
                            } else {
                                localPasswordError =
                                    null // Garante que está limpo se passou na validação local
                            }

                            // Se ambos forem válidos localmente, tenta o login
                            if (emailValido && senhaValida) {
                                loginViewModel.loginUser(email, password)
                            } else {
                                // Opcional: Log ou Snackbar indicando para verificar os campos
                                scope.launch { snackbarHostState.showSnackbar("Verifique os campos destacados.") }
                            }
                        },
                        enabled = !isLoading, // Desabilita o botão durante o carregamento
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "ENTRAR",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Não tem uma conta?")
                        TextButton(onClick = onSignUpClicked) {
                            Text(
                                "Cadastre-se",
                                color = accentColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ForgotPasswordDialog(
    onDismissRequest: () -> Unit, // Chamado quando o diálogo deve ser dispensado (clique fora ou botão cancelar)
    onSendEmailClicked: (email: String) -> Unit // Chamado quando o botão "Enviar E-mail" é clicado com um e-mail
) {
    var email by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) } // Para erro de validação local

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Redefinir Senha")
        },
        text = {
            Column {
                Text("Digite o e-mail associado à sua conta. Enviaremos um link para você criar uma nova senha.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.trim() // Remove espaços extras
                        emailError = null  // Limpa o erro ao digitar
                    },
                    label = { Text("Seu e-mail de cadastro") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth(), // Opcional, se quiser que preencha a largura do diálogo
                    shape = RoundedCornerShape(30.dp)
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validação básica do e-mail antes de chamar o callback
                    if (email.isBlank()) {
                        emailError = "O e-mail não pode estar em branco."
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Por favor, insira um formato de e-mail válido."
                    } else {
                        emailError = null // Limpa qualquer erro anterior
                        onSendEmailClicked(email) // Chama o callback com o e-mail fornecido
                    }
                }
            ) {
                Text("Enviar E-mail")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}
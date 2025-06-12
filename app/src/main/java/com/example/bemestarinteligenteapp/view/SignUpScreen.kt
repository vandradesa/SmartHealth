package com.example.bemestarinteligenteapp.view

import android.util.Log
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.rememberDatePickerState
import android.util.Patterns // Import para validação de email
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.DatePickerDefaults.dateFormatter
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController // Para o Preview
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel // Para injetar ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.bemestarinteligenteapp.R
import com.example.bemestarinteligenteapp.ui.theme.InputBackground
import com.example.bemestarinteligenteapp.viewmodel.user.SignUpViewModel // Importe seu ViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable


fun SignUpScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    signUpViewModel: SignUpViewModel = viewModel() // Injeta o SignUpViewMode
) {

    var nomeCompleto by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var senha by rememberSaveable { mutableStateOf("") }
    var confirmarSenha by rememberSaveable { mutableStateOf("") }
    var senhaVisivel by rememberSaveable { mutableStateOf(false) }
    var confirmarSenhaVisivel by rememberSaveable { mutableStateOf(false) }
    var generoSelecionado by rememberSaveable { mutableStateOf("") }
    var dataNascimento by rememberSaveable { mutableStateOf("") }
    var concordaTermos by rememberSaveable { mutableStateOf(false) }




    // Estados para mensagens de erro
    var nomeCompletoErro by rememberSaveable { mutableStateOf<String?>(null) }
    var emailErro by rememberSaveable { mutableStateOf<String?>(null) }
    var senhaErro by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmarSenhaErro by rememberSaveable { mutableStateOf<String?>(null) }
    var generoErro by rememberSaveable { mutableStateOf<String?>(null) }
    var dataNascimentoErro by rememberSaveable { mutableStateOf<String?>(null) }
    var termosErro by rememberSaveable { mutableStateOf<String?>(null) } // Para erro de termos não aceitos

    val generos = listOf("Masculino", "Feminino", "Outro", "Prefiro não informar")
    var generoDropdownExpanded by remember { mutableStateOf(false) }

    // Observa os estados do SignUpViewModel
    val isLoading by signUpViewModel.isLoading.collectAsState()
    val signUpError by signUpViewModel.signUpError.collectAsState()
    val signUpSuccessEvent by signUpViewModel.signUpSuccessEvent.collectAsState()

    // Para exibir Snackbars de erro
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Efeito para mostrar Snackbar quando houver erro de cadastro
    LaunchedEffect(signUpError) {
        signUpError?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMsg,
                    duration = SnackbarDuration.Long // Duração maior para ler o erro
                )
            }
            signUpViewModel.clearSignUpError() // Limpa o erro após mostrar
        }
    }

    // Efeito para navegar quando o cadastro for bem-sucedido
    LaunchedEffect(signUpSuccessEvent) {
        if (signUpSuccessEvent) {
            // Navega para a tela de login com um parâmetro de sucesso
            navController.navigate("${AppDestinations.LOGIN_ROUTE}?showSuccess=true") {
                // Limpa a pilha para que o usuário não volte para a tela de cadastro
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
            signUpViewModel.resetSignUpSuccessEvent() // Reseta o evento
        }
    }

    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()
    val calendario = Calendar.getInstance()
    //val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

// Variável para habilitar o botão "OK"
    val confirmEnabled = datePickerState.selectedDateMillis != null
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        selectedDate?.let { date -> // Usa a data já selecionada se existir
            calendar.set(date.year, date.monthValue - 1, date.dayOfMonth)
        }
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        android.app.DatePickerDialog(
            context, // Certifique-se que 'context' está definido (LocalContext.current)
            { _, year, month, dayOfMonth -> // Este é o callback de seleção!
                val newSelectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                selectedDate = newSelectedDate // Atualiza o estado do LocalDate (bom para lógica interna)

                // PONTO CRÍTICO: ATUALIZAR A STRING DO TEXTFIELD
                dataNascimento = newSelectedDate.format(dateTimeFormatter)

                dataNascimentoErro = null // Limpa erro ao selecionar data, se aplicável
                showDatePicker = false    // Esconde o diálogo
                Log.d("DatePickerDebug", "Data selecionada e formatada: ${dataNascimento}")
            },
            initialYear,
            initialMonth,
            initialDay
        ).apply {
            setOnDismissListener {
                showDatePicker = false
                Log.d("DatePickerDebug", "DatePickerDialog dispensado.")
            }
            datePicker.maxDate = System.currentTimeMillis()
            show()
            Log.d("DatePickerDebug", "DatePickerDialog.show() foi chamado.") // Confirme que o diálogo é mostrado
        }
    }

    // No início do seu Composable (ou onde showDatePicker é definido)
    Log.d("DatePickerDebug", "Composable recomposed. showDatePicker: $showDatePicker")

    // Cores e Estilos
    //val backgroundColor = Color(0xFFF7F9FC)
    val primaryTextColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val logoTextColor = accentColor
    val errorColor = MaterialTheme.colorScheme.error

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        //unfocusedContainerColor = InputBackground,
        //focusedContainerColor = InputBackground,
        focusedBorderColor = accentColor,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = accentColor,
        focusedLabelColor = accentColor,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorBorderColor = errorColor,
        errorLabelColor = errorColor,
        errorCursorColor = errorColor,
        errorSupportingTextColor = errorColor
    )

    // Função de validação
    fun validarCampos(): Boolean {
        var valido = true
        // Nome Completo
        if (nomeCompleto.isBlank()) {
            nomeCompletoErro = "Nome completo é obrigatório"
            valido = false
        } else {
            nomeCompletoErro = null
        }
        // Email
        if (email.isBlank()) {
            emailErro = "E-mail é obrigatório"
            valido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailErro = "Formato de e-mail inválido"
            valido = false
        } else {
            emailErro = null
        }
        // Senha
        if (senha.isBlank()) {
            senhaErro = "Senha é obrigatória"
            valido = false
        } else if (senha.length < 6) { // Exemplo de regra de senha
            senhaErro = "Senha deve ter pelo menos 6 caracteres"
            valido = false
        } else {
            senhaErro = null
        }
        // Confirmar Senha
        if (confirmarSenha.isBlank()) {
            confirmarSenhaErro = "Confirmação de senha é obrigatória"
            valido = false
        } else if (senha != confirmarSenha) {
            confirmarSenhaErro = "As senhas não coincidem"
            valido = false
        } else {
            confirmarSenhaErro = null
        }
        // Gênero
        if (generoSelecionado.isBlank()) {
            generoErro = "Gênero é obrigatório"
            valido = false
        } else {
            generoErro = null
        }
        // Data de Nascimento
        if (dataNascimento.isBlank()) {
            dataNascimentoErro = "Data de nascimento é obrigatória"
            valido = false
        } else {
            dataNascimentoErro = null
        }
        /*    // Termos
        if (!concordaTermos) {
            termosErro = "Você deve aceitar os termos e condições"
            valido = false // Embora o botão já controle isso, é bom ter no log
        } else {
            termosErro = null
        }*/
        return valido
    }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                // Carrega a imagem da pasta drawable
                painter = painterResource(id = R.drawable.logo_smarthealth), // 👈 Use o nome do seu arquivo aqui

                // Texto para acessibilidade (importante para leitores de tela)
                contentDescription = "Logo da Smart Health",

                // Modificadores para tamanho e espaçamento
                modifier = Modifier
                    .size(150.dp) // 🎨 Defina o tamanho que desejar
                    .clip(CircleShape)
                    .padding(bottom = 48.dp) // Mantém o espaçamento que você já tinha
            )
            Text(
                text = "Crie sua conta",
                style = MaterialTheme.typography.headlineSmall,
                color = primaryTextColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Monitore seu bem-estar de forma inteligente.",
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryTextColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Nome Completo
            OutlinedTextField(
                value = nomeCompleto,
                onValueChange = { nomeCompleto = it; nomeCompletoErro = null },
                label = { Text("Nome Completo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = textFieldColors,
                isError = nomeCompletoErro != null,
                supportingText = { if (nomeCompletoErro != null) Text(nomeCompletoErro!!) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim(); emailErro = null },
                label = { Text("E-mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = textFieldColors,
                isError = emailErro != null,
                supportingText = { if (emailErro != null) Text(emailErro!!) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it; senhaErro = null; if(confirmarSenha.isNotEmpty()) confirmarSenhaErro = null },
                label = { Text("Senha") },
                visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (senhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                        Icon(imageVector = image, if (senhaVisivel) "Esconder senha" else "Mostrar senha", tint = secondaryTextColor)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = textFieldColors,
                isError = senhaErro != null,
                supportingText = { if (senhaErro != null) Text(senhaErro!!) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Confirmar Senha
            OutlinedTextField(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it; confirmarSenhaErro = null },
                label = { Text("Confirmar Senha") },
                visualTransformation = if (confirmarSenhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmarSenhaVisivel) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmarSenhaVisivel = !confirmarSenhaVisivel }) {
                        Icon(imageVector = image, if (confirmarSenhaVisivel) "Esconder senha" else "Mostrar senha", tint = secondaryTextColor)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = textFieldColors,
                isError = confirmarSenhaErro != null,
                supportingText = { if (confirmarSenhaErro != null) Text(confirmarSenhaErro!!) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Gênero
            ExposedDropdownMenuBox(
                expanded = generoDropdownExpanded,
                onExpandedChange = { generoDropdownExpanded = !generoDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = generoSelecionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gênero") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoDropdownExpanded) },
                    colors = textFieldColors,
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier.menuAnchor().fillMaxWidth(), // menuAnchor é importante aqui
                    isError = generoErro != null,
                    supportingText = { if (generoErro != null) Text(generoErro!!) }
                )
                ExposedDropdownMenu(
                    expanded = generoDropdownExpanded,
                    onDismissRequest = { generoDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    generos.forEach { genero ->
                        DropdownMenuItem(
                            text = { Text(genero) },
                            onClick = {
                                generoSelecionado = genero
                                generoDropdownExpanded = false
                                generoErro = null
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Data de Nascimento
            OutlinedTextField(
                value = dataNascimento, // Usa o estado 'dataNascimento'
                onValueChange = {},     // Não é alterado diretamente
                label = { Text("Data de Nascimento") },
                readOnly = true,        // O campo é apenas para exibição
                enabled = true,         // Mantido habilitado para aparência normal
                trailingIcon = {
                    IconButton(onClick = {
                        Log.d("DatePickerDebug", "!!! ÍCONE DE CALENDÁRIO CLICADO! Setting showDatePicker = true !!!")
                        showDatePicker = true
                    }) {
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = "Selecionar Data",
                            tint = secondaryTextColor // Sua cor secundária
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = textFieldColors,
                isError = dataNascimentoErro != null,
                supportingText = { if (dataNascimentoErro != null) Text(dataNascimentoErro!!) },
                interactionSource = remember { MutableInteractionSource() } // Pode manter para o ripple do campo se desejado
            )
            Spacer(modifier = Modifier.height(16.dp))

           /* // Termos e Condições
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = concordaTermos,
                    onCheckedChange = { concordaTermos = it; if(it) termosErro = null },
                    colors = CheckboxDefaults.colors(checkedColor = accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Eu li e concordo com os Termos de Serviço e a Política de Privacidade.",
                    style = MaterialTheme. typography.bodySmall,
                    color = if (termosErro != null && !concordaTermos) errorColor else secondaryTextColor
                )
            }
            if (termosErro != null && !concordaTermos) {
                Text(
                    text = termosErro!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = errorColor,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))*/

            Button(
                onClick = {
                    Log.d("SignUpDebug", "Botão CRIAR CONTA clicado.") // Adicione um log aqui
                    if (validarCampos()) { // DESCOMENTE E USE ESTA LINHA
                        Log.d("SignUpDebug", "Validação passou. Chamando signUpUser.")
                        // Se a validação passar, chame o método do ViewModel
                        signUpViewModel.signUpUser( // Ou o nome do método que você tem no seu ViewModel
                            nomeCompleto = nomeCompleto,
                            email = email,
                            senha = senha,
                            genero = generoSelecionado,
                            dataNascimento = dataNascimento // A string formatada
                            // Considere se o ViewModel prefere selectedDate (o LocalDate)
                        )
                    } else {
                        Log.d("SignUpDebug", "Validação falhou. Verifique os campos.")
                        // Se a validação falhar, as mensagens de erro já foram definidas
                        // dentro de validarCampos(). A UI deve ser recomposta para mostrá-las.
                        // Você pode opcionalmente mostrar um Snackbar/Toast geral aqui também,
                        // mas as mensagens de erro nos campos são o principal.
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Por favor, corrija os erros no formulário.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                enabled = true // Sua lógica de enabled aqui
            ) {
                Text("CRIAR CONTA", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Já tem uma conta?", color = primaryTextColor)
                TextButton(onClick = {
                    // Sua navegação para login
                     navController.navigate(AppDestinations.LOGIN_ROUTE)
                }) {
                    Text("Faça login", color = accentColor, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Preview(showBackground = true, device = "id:pixel_6_pro")
@Composable
fun SmartHealthSignUpScreenPreview() {
    MaterialTheme {
        SignUpScreen(navController = rememberNavController())
    }
}
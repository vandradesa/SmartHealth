package com.example.bemestarinteligenteapp.view
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.bemestarinteligenteapp.R
import com.example.bemestarinteligenteapp.viewmodel.user.EditViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    editViewModel: EditViewModel = viewModel() // 👈 ViewModel para a lógica de edição
) {
    // Estados dos campos do formulário
    var nomeCompleto by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var generoSelecionado by rememberSaveable { mutableStateOf("") }
    var dataNascimento by rememberSaveable { mutableStateOf("") }

    // Estados para mensagens de erro
    var nomeCompletoErro by rememberSaveable { mutableStateOf<String?>(null) }
    var emailErro by rememberSaveable { mutableStateOf<String?>(null) }
    var generoErro by rememberSaveable { mutableStateOf<String?>(null) }
    var dataNascimentoErro by rememberSaveable { mutableStateOf<String?>(null) }

    // Lógica do Dropdown de Gênero
    val generos = listOf("Masculino", "Feminino", "Outro", "Prefiro não informar")
    var generoDropdownExpanded by remember { mutableStateOf(false) }

    // Lógica do Date Picker
    val context = LocalContext.current
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
    var showDatePicker by remember { mutableStateOf(false) }

    // Observa os estados do ViewModel
    val isLoading by editViewModel.isLoading.collectAsState()
    val updateError by editViewModel.updateError.collectAsState()
    val updateSuccess by editViewModel.updateSuccess.collectAsState()
    val userData by editViewModel.userData.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // EFEITO 1: Carrega os dados do usuário UMA VEZ quando a tela é iniciada
    LaunchedEffect(Unit) {
        editViewModel.loadUserData()
    }

    // EFEITO 2: Preenche os campos do formulário quando os dados do usuário são carregados do ViewModel
    LaunchedEffect(userData) {
        userData?.let { user ->
            nomeCompleto = user.nomeCompleto
            email = user.email
            generoSelecionado = user.genero
            dataNascimento = user.dataNascimento
        }
    }

    // EFEITO 3: Mostra Snackbar em caso de erro na atualização
    LaunchedEffect(updateError) {
        updateError?.let { errorMsg ->
            scope.launch {
                snackbarHostState.showSnackbar(message = errorMsg, duration = SnackbarDuration.Long)
            }
            editViewModel.clearUpdateError()
        }
    }

    // EFEITO 4: Mostra mensagem de sucesso e/ou navega para outra tela
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Dados atualizados com sucesso!",
                    duration = SnackbarDuration.Short
                )
            }
            // Opcional: navegar para a tela anterior
            // navController.popBackStack()
            editViewModel.resetUpdateSuccessEvent()
        }
    }

    // Lógica do DatePickerDialog
    if (showDatePicker) {
        val initialDate = try {
            LocalDate.parse(dataNascimento, dateTimeFormatter)
        } catch (e: Exception) {
            LocalDate.now()
        }

        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newSelectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                dataNascimento = newSelectedDate.format(dateTimeFormatter)
                dataNascimentoErro = null
                showDatePicker = false
            },
            initialDate.year,
            initialDate.monthValue - 1,
            initialDate.dayOfMonth
        ).apply {
            setOnDismissListener { showDatePicker = false }
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    // Cores e Estilos (pode reutilizar do seu tema)
    val accentColor = MaterialTheme.colorScheme.primary
    val primaryTextColor = MaterialTheme.colorScheme.onBackground
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val errorColor = MaterialTheme.colorScheme.error
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = accentColor,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = accentColor,
        focusedLabelColor = accentColor,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        errorBorderColor = errorColor
    )

    // Função de validação simplificada (sem senhas)
    fun validarCampos(): Boolean {
        nomeCompletoErro = if (nomeCompleto.isBlank()) "Nome completo é obrigatório" else null
        emailErro = if (email.isBlank()) "E-mail é obrigatório" else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) "Formato de e-mail inválido" else null
        generoErro = if (generoSelecionado.isBlank()) "Gênero é obrigatório" else null
        dataNascimentoErro = if (dataNascimento.isBlank()) "Data de nascimento é obrigatória" else null
        return nomeCompletoErro == null && emailErro == null && generoErro == null && dataNascimentoErro == null
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Surface(
            modifier = modifier.fillMaxSize().padding(paddingValues),
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
                    painter = painterResource(id = R.drawable.logo_smarthealth),
                    contentDescription = "Logo da Smart Health",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .padding(bottom = 24.dp)
                )
                Text(
                    text = "Editar Dados Cadastrais", // 👈 TÍTULO ALTERADO
                    style = MaterialTheme.typography.headlineSmall,
                    color = primaryTextColor,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Campo Nome Completo
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

                // Campo Email
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

                // Campo Gênero
                ExposedDropdownMenuBox(
                    expanded = generoDropdownExpanded,
                    onExpandedChange = { generoDropdownExpanded = !generoDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = generoSelecionado,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gênero") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoDropdownExpanded) },
                        colors = textFieldColors,
                        shape = RoundedCornerShape(30.dp),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = generoErro != null,
                        supportingText = { if (generoErro != null) Text(generoErro!!) }
                    )
                    ExposedDropdownMenu(
                        expanded = generoDropdownExpanded,
                        onDismissRequest = { generoDropdownExpanded = false }
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

                // Campo Data de Nascimento
                OutlinedTextField(
                    value = dataNascimento,
                    onValueChange = {},
                    label = { Text("Data de Nascimento") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.CalendarToday, "Selecionar Data", tint = secondaryTextColor)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    colors = textFieldColors,
                    isError = dataNascimentoErro != null,
                    supportingText = { if (dataNascimentoErro != null) Text(dataNascimentoErro!!) }
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Botão Salvar Alterações
                Button(
                    onClick = {
                        if (validarCampos()) {
                            editViewModel.updateUserData(
                                nomeCompleto = nomeCompleto,
                                email = email,
                                genero = generoSelecionado,
                                dataNascimento = dataNascimento
                            )
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor, corrija os erros.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    enabled = !isLoading // Desabilita o botão enquanto carrega
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "SALVAR ALTERAÇÕES", // 👈 TEXTO DO BOTÃO ALTERADO
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Botão de Voltar (opcional)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Voltar", color = accentColor, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


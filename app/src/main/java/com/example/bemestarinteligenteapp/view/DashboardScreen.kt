package com.example.bemestarinteligenteapp.view

import com.example.bemestarinteligenteapp.R
import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.bemestarinteligenteapp.model.ExercisesData // Sua classe de modelo
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModel
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModel
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModel
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModel
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModel
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModel
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

// Definição das permissões do Health Connect
private val HEALTH_CONNECT_PERMISSIONS = setOf(
    HealthPermission.getReadPermission(HeartRateRecord::class),
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
    HealthPermission.getReadPermission(OxygenSaturationRecord::class),
    HealthPermission.getReadPermission(DistanceRecord::class),
    HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
    HealthPermission.getReadPermission(ExerciseSessionRecord::class)
)

// Data class para os itens da navegação, para manter o código limpo
data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    stepsViewModel: StepsViewModel,
    heartRateViewModel: HeartRateViewModel,
    oxygenSaturationViewModel: OxygenSaturationViewModel,
    sleepViewModel: SleepViewModel,
    caloriesViewModel: CaloriesViewModel,
    exercisesViewModel: ExercisesViewModel,

) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var permissionsGranted by remember { mutableStateOf(false) }
    var isInitialSetupInProgress by remember { mutableStateOf(true) }
    var healthConnectClient by remember { mutableStateOf<HealthConnectClient?>(null) }
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM) }
    val navigationItems = listOf(
        BottomNavigationItem("Início", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Análise Semanal", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        BottomNavigationItem("Relatório IA", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
        BottomNavigationItem("Conta", Icons.Filled.Person, Icons.Outlined.Person)
    )

    // Estado para saber qual item está selecionado na barra de navegação
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        isInitialSetupInProgress = false
        if (grantedPermissions.containsAll(HEALTH_CONNECT_PERMISSIONS)) {
            permissionsGranted = true
        } else {
            permissionsGranted = false
            Toast.makeText(
                context,
                "Permissões do Health Connect não concedidas.",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    suspend fun checkPermissionsAndLoadData(
        currentClient: HealthConnectClient?,
        currentDate: LocalDate
    ) {
        val providerPackageName =
            "com.google.android.apps.healthdata" // Pacote correto do Health Connect
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, providerPackageName)

        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            try {
                val uriString =
                    "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
                context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = uriString.toUri()
                    putExtra("overlay", true)
                    putExtra("callerId", context.packageName)
                })
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Atualize o app Health Connect pela Play Store.",
                    Toast.LENGTH_LONG
                ).show()
            }
            isInitialSetupInProgress = false
            return
        }
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            Toast.makeText(
                context,
                "Health Connect não está disponível neste dispositivo.",
                Toast.LENGTH_LONG
            ).show()
            isInitialSetupInProgress = false
            return
        }

        val client = currentClient ?: HealthConnectClient.getOrCreate(context)
        healthConnectClient = client

        val currentPermissions = try {
            client.permissionController.getGrantedPermissions()
        } catch (e: Exception) {
            // Tratar exceção ao buscar permissões, pode acontecer se o HC não estiver pronto
            isInitialSetupInProgress = false;
            permissionsGranted = false;
            Toast.makeText(
                context,
                "Erro ao verificar permissões do Health Connect.",
                Toast.LENGTH_SHORT
            ).show();
            return
        }

        if (currentPermissions.containsAll(HEALTH_CONNECT_PERMISSIONS)) {
            permissionsGranted = true
            loadDataForSelectedDate(
                client,
                currentDate,
                stepsViewModel,
                heartRateViewModel,
                oxygenSaturationViewModel,
                sleepViewModel,
                caloriesViewModel,
                exercisesViewModel
            )
        } else {
            permissionsGranted = false // Garante que está false se não tem todas
            requestPermissionsLauncher.launch(HEALTH_CONNECT_PERMISSIONS)
        }
        isInitialSetupInProgress =
            false // Movido para o final, após tentativas de carregar ou pedir permissão
    }

    LaunchedEffect(selectedDate, permissionsGranted) {
        if (permissionsGranted) {
            isInitialSetupInProgress = true
            healthConnectClient?.let { client ->
                loadDataForSelectedDate(
                    client,
                    selectedDate,
                    stepsViewModel,
                    heartRateViewModel,
                    oxygenSaturationViewModel,
                    sleepViewModel,
                    caloriesViewModel,
                    exercisesViewModel
                )
                isInitialSetupInProgress = false
            } ?: run {
                // Se healthConnectClient é nulo mas permissionsGranted é true (ex: após rotação de tela ou retorno à app)
                // Precisamos garantir que o cliente seja recriado se necessário.
                coroutineScope.launch { checkPermissionsAndLoadData(null, selectedDate) }
            }
        } else if (healthConnectClient == null && isInitialSetupInProgress) { // Primeira vez, ou cliente se perdeu
            coroutineScope.launch { checkPermissionsAndLoadData(null, selectedDate) }
        }
        // Não precisa de 'else if (!isInitialSetupInProgress && !permissionsGranted)' aqui,
        // pois a UI já mostrará o botão "Conceder Permissões" se permissionsGranted for false.
    }


    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        calendar.set(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                showDatePicker = false // Esconde o picker após seleção
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { showDatePicker = false } // Esconde se o usuário dispensar
            datePicker.maxDate = System.currentTimeMillis() // Impede datas futuras
            show()
        }
    }

    val stepsDataState by stepsViewModel.stepsData.observeAsState()
    val heartRateState by heartRateViewModel.latestHeartRate.observeAsState()
    val heartMeasurementTimeState by heartRateViewModel.latestMeasurementTime.observeAsState()
    val averageBpmState by heartRateViewModel.averageHeartRate.observeAsState()
    val oxygenSaturationState by oxygenSaturationViewModel.latestOxygenSaturation.observeAsState()
    val o2MeasurementTimeState by oxygenSaturationViewModel.latestO2MeasurementTime.observeAsState()
    val sleepDurationState by sleepViewModel.totalSleepDurationMillis.observeAsState()
    val sleepQualityState by sleepViewModel.sleepQuality.observeAsState()
    val caloriesBurnedState by caloriesViewModel.caloriesData.observeAsState()
    val exerciseDataState by exercisesViewModel.exercisesData.observeAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            // A LÓGICA DA IA É TRATADA DE FORMA ESPECIAL
                            if (item.title == "Relatório IA") {
                                // A MESMA LÓGICA DO SEU BOTÃO ANTIGO
                                val intent = Intent(context, DeepSeekActivity::class.java).apply {
                                    putExtra("selectedDate", selectedDate.toString())
                                    putExtra("heartRate", heartRateState ?: 0.0)
                                    putExtra("averageHeartRate", averageBpmState ?: 0.0)
                                    putExtra("oxygenSaturation", oxygenSaturationState ?: 0.0)
                                    putExtra("stepsCount", stepsDataState?.count ?: 0L)
                                    putExtra("sleepDurationMillis", sleepDurationState ?: 0L)
                                    putExtra("sleepQuality", sleepQualityState ?: "Indefinido")
                                    putExtra("caloriesBurned", caloriesBurnedState ?: 0.0)
                                    putExtra(
                                        "exercisesData",
                                        ArrayList(exerciseDataState ?: emptyList())
                                    )
                                }
                                context.startActivity(intent)
                            } else {
                                // Para os outros itens, apenas muda a aba selecionada
                                selectedItemIndex = index
                            }
                        },
                        label = { Text(text = item.title, fontSize = 11.sp) },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // Padding da Scaffold para todo o conteúdo
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo_smarthealth),
                contentDescription = "Logo SmartHealth",
                modifier = Modifier
                    .padding(1.dp) // Um espaço no topo
                    .size(80.dp)
            )
            // Espaço entre a logo global e o conteúdo da aba
            Spacer(modifier = Modifier.height(8.dp))

            // O conteúdo da tela muda de acordo com a aba selecionada
            when (selectedItemIndex) {
                0 -> // Conteúdo da aba "INÍCIO"
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(
                                selectedDate.format(dateFormatter),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary

                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Selecionar Data",
                                tint = MaterialTheme.colorScheme.onPrimary

                            )
                        }
                        // Conteúdo específico da tela de início (lógica de permissões)
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))

                            if (isInitialSetupInProgress && !permissionsGranted) {
                                Box(
                                    modifier = Modifier.fillMaxSize().padding(top = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Verificando permissões...",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            } else if (!permissionsGranted) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text("As permissões do Health Connect são necessárias para exibir seus dados de saúde.")
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = {
                                        isInitialSetupInProgress = true
                                        requestPermissionsLauncher.launch(HEALTH_CONNECT_PERMISSIONS)
                                    }) { Text("Conceder Permissões") }
                                }
                            } else {
                                DashboardScreenContent(
                                    steps = stepsDataState?.count,
                                    heartRate = heartRateState,
                                    heartMeasurementTime = heartMeasurementTimeState,
                                    averageBpm = averageBpmState,
                                    oxygenSaturation = oxygenSaturationState,
                                    o2MeasurementTime = o2MeasurementTimeState,
                                    sleepDuration = sleepDurationState,
                                    sleepQuality = sleepQualityState,
                                    caloriesBurned = caloriesBurnedState,
                                    selectedDate = selectedDate,
                                    exerciseData = exerciseDataState,
                                    navController = navController
                                )
                            }
                        }
                    }

                1 -> // Conteúdo da aba "ANÁLISE"
                    AnaliseCompletaScreen(
                        modifier = Modifier.padding(innerPadding)
                    )

                // A aba de Relatório IA é uma AÇÃO, não uma TELA, por isso não tem case aqui.

                3 -> // Conteúdo da aba "CONTA"
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp)
                    ) {
                        // Título da tela
                        Text(
                            text = "Minha Conta",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Opções da conta
                        AccountOptionItem(
                            icon = Icons.Default.Person,
                            text = "Editar Dados",
                            onClick = {
                                // TODO: Navegar para a tela de Edição de Dados
                                // Ex: navController.navigate("edit_profile_route")
                            }
                        )
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        AccountOptionItem(
                            icon = Icons.Default.Lock,
                            text = "Alterar Senha",
                            onClick = {
                                // TODO: Navegar para a tela de Alteração de Senha
                                // Ex: navController.navigate("change_password_route")
                            }
                        )

                        // Espaço para empurrar o botão de logout para baixo
                       // Spacer(modifier = Modifier.weight(1f))

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        // O botão de logout agora é um item de menu, como os outros
                        AccountOptionItem(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            text = "Sair",
                            // Passamos a cor de erro do tema para destacar em vermelho

                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate("login") { // Use a sua rota de login
                                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )

                        }
                    }
            }
        }
    }


    // Função auxiliar para carregar dados
    private fun loadDataForSelectedDate(
        client: HealthConnectClient,
        date: LocalDate,
        stepsVM: StepsViewModel,
        heartRateVM: HeartRateViewModel,
        oxygenSaturationVM: OxygenSaturationViewModel,
        sleepVM: SleepViewModel,
        caloriesVM: CaloriesViewModel,
        exercisesVM: ExercisesViewModel
    ) {
        stepsVM.loadSteps(client, date)
        heartRateVM.loadHeartRate(client, date)
        oxygenSaturationVM.loadOxygenSaturation(client, date)
        sleepVM.loadSleepData(client, date)
        caloriesVM.loadCalories(client, date)
        exercisesVM.loadExercises(client, date)
    }

    @Composable
    fun DashboardScreenContent(
        steps: Long?,
        heartRate: Double?,
        heartMeasurementTime: Instant?,
        averageBpm: Double?,
        oxygenSaturation: Double?,
        o2MeasurementTime: Instant?,
        sleepDuration: Long?,
        sleepQuality: String?,
        caloriesBurned: Double?,
        selectedDate: LocalDate,
        exerciseData: List<ExercisesData>?,
        navController: NavController,
        modifier: Modifier = Modifier // Adicionei modifier aqui para flexibilidade
    ) {

        val exerciseSummaryForContent = remember(exerciseData) {
            exerciseData?.let { list ->
                if (list.isEmpty()) {
                    "Nenhum exercício registrado para $selectedDate"
                } else {
                    buildString {
                        //append("Atividades do dia")
                        list.forEach { exercise ->
                            val durationMinutes =
                                java.time.Duration.between(exercise.startTime, exercise.endTime)
                                    .toMinutes()
                            append("- ${exercise.exerciseType}: $durationMinutes min\n")
                        }
                    }
                }
            } ?: "Carregando exercícios..."
        }

        Column(
            modifier = modifier // Usando o modifier passado
                .fillMaxWidth() // Removi fillMaxSize para permitir que o pai controle o tamanho no scroll
                .padding(horizontal = 0.dp) // Ajuste o padding se necessário, o pai já tem 16.dp
                .padding(top = 0.dp), // Ajuste o padding se necessário
            // .wrapContentWidth(Alignment.CenterHorizontally), // Removido, pois Column já é fillMaxWidth
            verticalArrangement = Arrangement.Top // Mantido
        ) {
            /*Text(text = "Dados para o dia: $selectedDate",
            style = MaterialTheme.typography.titleLarge, // Um pouco maior para destaque
            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
        )*/

            // Linha 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espaço entre os cards
            ) {
                StepSummaryCard(
                    steps = steps,
                    modifier = Modifier.weight(1f),
                    onClick = { // <<< Ação de clique definida aqui
                        navController.navigate(AppDestinations.STEPS_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                    }
                )

                HeartRateSummaryCard(
                    heartRate = heartRate,
                    measurementTime = heartMeasurementTime,
                    // averageBpm = averageBpm, // Removido daqui, pois temos AverageHeartRateSummaryCard
                    modifier = Modifier.weight(1f),
                    onClick = { // <<< Ação de clique definida aqui
                        navController.navigate(AppDestinations.HEART_RATE_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                    }


                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaço entre as linhas de cards

            // Linha 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AverageHeartRateSummaryCard(
                    averageBpm = averageBpm,
                    date = selectedDate, // Passando a data selecionada
                    modifier = Modifier.weight(1f),
                    onClick = { // <<< Ação de clique definida aqui
                        navController.navigate(AppDestinations.STEPS_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                    }
                )
                OxygenSaturationSummaryCard(
                    oxygenSaturation = oxygenSaturation,
                    measurementTime = o2MeasurementTime,
                    modifier = Modifier.weight(1f),
                    onClick = { // <<< Ação de clique definida aqui
                        navController.navigate(AppDestinations.OXYGEN_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Linha 3
           // Row(
             //   modifier = Modifier.fillMaxWidth(),
               // horizontalArrangement = Arrangement.spacedBy(16.dp)
            //) {


                CaloriesSummaryCard(
                    calories = caloriesBurned,
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    onClick = { // <<< Ação de clique definida aqui
                        navController.navigate(AppDestinations.CALORIES_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                    }
                )
            //}

            Spacer(modifier = Modifier.height(16.dp))

            SleepSummaryCard(
                sleepDuration = sleepDuration,
                sleepQuality = sleepQuality,
                modifier = Modifier.fillMaxWidth(),
                onClick = { // <<< Ação de clique definida aqui
                    navController.navigate(AppDestinations.STEPS_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card de Exercícios (ocupando a largura toda)
            ExerciseSummaryCard(
                exerciseSummary = exerciseSummaryForContent,
                modifier = Modifier.fillMaxWidth(),
                onClick = { // <<< Ação de clique definida aqui
                    navController.navigate(AppDestinations.STEPS_WEEKLY_ANALYSIS_ROUTE)// Ocupa metade do espaço disponível
                }
            )
        }
    }


    @Composable
    fun AccountOptionItem(
        icon: ImageVector,
        text: String,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick) // Torna a linha inteira clicável
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // O texto já descreve a ação
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f) // Ocupa o espaço restante
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ir para $text",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DashboardScreenPreview() {
        val context = LocalContext.current
        val app = context.applicationContext as Application
        BemEstarInteligenteAppTheme {
            DashboardScreen(
                navController = rememberNavController(),
                stepsViewModel = viewModel(factory = StepsViewModelFactory(app)),
                heartRateViewModel = viewModel(factory = HeartRateViewModelFactory(app)),
                oxygenSaturationViewModel = viewModel(factory = OxygenSaturationViewModelFactory(app)),
                sleepViewModel = viewModel(factory = SleepViewModelFactory(app)),
                caloriesViewModel = viewModel(factory = CaloriesViewModelFactory(app)),
                exercisesViewModel = viewModel(factory = ExercisesViewModelFactory(app))
            )
        }
    }


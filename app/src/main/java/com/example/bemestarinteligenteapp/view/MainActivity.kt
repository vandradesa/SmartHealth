package com.example.bemestarinteligenteapp.view

import android.app.Application // Para passar ao ViewModelFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme
import com.example.bemestarinteligenteapp.viewmodel.calories.CaloriesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.exercise.ExercisesViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.heartRate.HeartRateViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.oxygenSaturation.OxygenSaturationViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.sleep.SleepViewModelFactory
import com.example.bemestarinteligenteapp.viewmodel.steps.StepsViewModelFactory
import com.google.firebase.auth.FirebaseAuth // Importe FirebaseAuth
import com.google.firebase.auth.ktx.auth // Importe auth se não estiver global
import com.google.firebase.ktx.Firebase // Importe Firebase se não estiver global
import androidx.compose.ui.platform.LocalContext // Para obter o contexto da aplicação para as factories
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel // Import para o delegate viewModel()
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bemestarinteligenteapp.view.calories.CaloriesWeeklyChartView
import com.example.bemestarinteligenteapp.view.heartrate.HeartRateWeeklyChartView
import com.example.bemestarinteligenteapp.view.oxygen.OxygenSaturationWeeklyChartView
import com.example.bemestarinteligenteapp.view.steps.StepsWeeklyChartView
import com.example.bemestarinteligenteapp.viewmodel.user.EditViewModel
import com.example.bemestarinteligenteapp.viewmodel.user.LoginViewModel
import kotlinx.coroutines.launch

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val LOGIN_ROUTE = "login"
    const val DASHBOARD_ROUTE = "dashboard"
    const val SIGNUP_ROUTE = "signup"
    const val EDIT_ROUTE = "edit"
    const val CHANGE_PASSWORD_ROUTE = "change_password"
    const val STEPS_WEEKLY_ANALYSIS_ROUTE = "steps_weekly_analysis"
    const val CALORIES_WEEKLY_ANALYSIS_ROUTE = "calories_weekly_analysis"
    const val HEART_RATE_WEEKLY_ANALYSIS_ROUTE = "heart_rate_weekly_analysis"
    const val SLEEP_WEEKLY_ANALYSIS_ROUTE = "sleep_weekly_analysis"
    const val OXYGEN_WEEKLY_ANALYSIS_ROUTE = "distance_weekly_analysis"
    const val EXERCISES_WEEKLY_ANALYSIS_ROUTE = "active_minutes_weekly_analysis"


    // Adicione outras rotas como SIGNUP_ROUTE, FORGOT_PASSWORD_ROUTE aqui
}

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        Log.i("MainActivity", "onCreate usuario atual: ${auth.currentUser}")

         setContent {
            BemEstarInteligenteAppTheme(
                darkTheme = isSystemInDarkTheme(), // Você pode manter ou remover se não for usar tema escuro dinâmico
                dynamicColor = false // <<<<<<< ADICIONE ESTA LINHA
            ) { // Seu tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chama o Composable de Navegação, passando o estado de login atual
                    AppNavigation(isUserLoggedIn = auth.currentUser != null)
                }
            }
        }
    }



@Composable
fun AppNavigation(isUserLoggedIn: Boolean) {
    val navController = rememberNavController()
    // Define a tela inicial baseada no estado de login do usuário
    val startDestination = if (isUserLoggedIn) AppDestinations.DASHBOARD_ROUTE else AppDestinations.HOME_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AppDestinations.CALORIES_WEEKLY_ANALYSIS_ROUTE) {
            CaloriesWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }
        composable(AppDestinations.HEART_RATE_WEEKLY_ANALYSIS_ROUTE) {
            HeartRateWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }
        composable(AppDestinations.SLEEP_WEEKLY_ANALYSIS_ROUTE) {
            CaloriesWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }
        composable(AppDestinations.OXYGEN_WEEKLY_ANALYSIS_ROUTE) {
            OxygenSaturationWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }

        composable(AppDestinations.EXERCISES_WEEKLY_ANALYSIS_ROUTE) {
            CaloriesWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }

        composable(AppDestinations.STEPS_WEEKLY_ANALYSIS_ROUTE) {
            StepsWeeklyChartView() // Este Composable busca seu próprio ViewModel
        }

        composable(
            // <<< MUDANÇA 1: Adicionar o argumento opcional à rota
            route = "${AppDestinations.LOGIN_ROUTE}?showSuccess={showSuccess}",
            arguments = listOf(
                navArgument("showSuccess") {
                    type = NavType.BoolType
                    defaultValue = false // Valor padrão é falso
                }
            )
        ) { backStackEntry -> // <<< MUDANÇA 2: Receber o backStackEntry para ler o argumento

            val loginViewModel: LoginViewModel = viewModel()
            var showForgotPasswordDialog by remember { mutableStateOf(false) }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()

            // <<< MUDANÇA 3: Adicionar o LaunchedEffect para mostrar a mensagem
            // Pega o argumento da navegação
            val showSuccessMessage = backStackEntry.arguments?.getBoolean("showSuccess") ?: false
            if (showSuccessMessage) {
                // Este efeito será lançado apenas uma vez quando a tela for exibida com este argumento
                LaunchedEffect(Unit) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Cadastro realizado com sucesso!",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            SmartHealthLoginScreenLight(
                navController = navController,
                onForgotPasswordClicked = {
                    Log.d("AppNavigation", "Forgot Password Clicado - Implementar navegação")
                    showForgotPasswordDialog = true
                },
                onSignUpClicked = {
                    Log.d("AppNavigation", "Sign Up Clicado - Implementar navegação ou lógica de cadastro")
                    navController.navigate(AppDestinations.SIGNUP_ROUTE)
                },
                loginViewModel = loginViewModel,
                snackbarHostState = snackbarHostState,
                showSuccessMessage = showSuccessMessage
            )
            // Se showForgotPasswordDialog for true, mostre o diálogo
            if (showForgotPasswordDialog) {
                ForgotPasswordDialog( // O diálogo que você criou na Parte 2
                    onDismissRequest = {
                        showForgotPasswordDialog = false // Fecha o diálogo
                        // Chama a função para limpar o status/erro no ViewModel
                        loginViewModel.clearPasswordResetStatus() // Certifique-se que esta função existe no LoginViewModel
                    },
                    onSendEmailClicked = { email ->
                        Log.d("AppNavigation", "Dialog: Enviar e-mail de reset para: $email")
                        // Chama a função sendPasswordResetEmail do LoginViewModel
                        loginViewModel.sendPasswordResetEmail(email)
                        showForgotPasswordDialog = false // Fecha o diálogo
                        // O feedback (Snackbar) será tratado na Parte 4
                    }
                )
            }

            val isLoadingResetPassword by loginViewModel.isLoadingResetPassword.collectAsState()
            val passwordResetStatus by loginViewModel.passwordResetEmailSentStatus.collectAsState()
            val passwordResetError by loginViewModel.passwordResetError.collectAsState()

// Efeito para mostrar Snackbar de SUCESSO na redefinição de senha
            LaunchedEffect(passwordResetStatus) {
                if (passwordResetStatus == true) { // Se o envio foi bem-sucedido
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "E-mail de redefinição enviado! Verifique sua caixa de entrada (e spam).",
                            duration = SnackbarDuration.Long
                        )
                    }
                    loginViewModel.clearPasswordResetStatus() // Limpa o evento/status no ViewModel
                }
            }

// Efeito para mostrar Snackbar de ERRO na redefinição de senha
            LaunchedEffect(passwordResetError) {
                passwordResetError?.let { errorMsg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = errorMsg, // Mensagem de erro vinda do ViewModel
                            duration = SnackbarDuration.Long
                        )
                    }
                    loginViewModel.clearPasswordResetStatus() // Limpa o evento/status no ViewModel
                }
            }


        }

        composable(AppDestinations.DASHBOARD_ROUTE) {
            // Obtendo o contexto da aplicação para as Factories dos ViewModels
            val application = LocalContext.current.applicationContext as Application

            // Sua DashboardScreen atualizada para receber NavController e instanciando ViewModels
            DashboardScreen(
                navController = navController,
                stepsViewModel = viewModel(factory = StepsViewModelFactory(application)),
                heartRateViewModel = viewModel(factory = HeartRateViewModelFactory(application)),
                oxygenSaturationViewModel = viewModel(factory = OxygenSaturationViewModelFactory(application)),
                sleepViewModel = viewModel(factory = SleepViewModelFactory(application)),
                caloriesViewModel = viewModel(factory = CaloriesViewModelFactory(application)),
                exercisesViewModel = viewModel(factory = ExercisesViewModelFactory(application))
            )
        }

        composable(AppDestinations.SIGNUP_ROUTE) {
            SignUpScreen(navController = navController)
        }

        composable(AppDestinations.HOME_ROUTE) {
            WelcomeScreen(navController = navController)
        }

        composable(AppDestinations.EDIT_ROUTE) {
            val editViewModel: EditViewModel = viewModel()

            EditScreen(
                navController = navController,
                editViewModel = editViewModel // Passa a instância gerenciada
            )

        }

        composable(AppDestinations.CHANGE_PASSWORD_ROUTE) {
            ChangePasswordScreen(navController = navController)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BemEstarInteligenteAppTheme {
             AppNavigation(isUserLoggedIn = true)  // Simula usuário logado
    }
}
}
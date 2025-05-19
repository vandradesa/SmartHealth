package com.example.bemestarinteligenteapp.view
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bemestarinteligenteapp.BuildConfig
import com.example.bemestarinteligenteapp.model.DadosRequest
import com.example.bemestarinteligenteapp.model.ExercisesData
import com.example.bemestarinteligenteapp.remote.RetrofitClient
import com.example.bemestarinteligenteapp.repository.DeepSeekRepository

import com.example.bemestarinteligenteapp.viewmodel.DeepSeekViewModel
import com.example.bemestarinteligenteapp.viewmodel.DeepSeekViewModelFactory

class DeepSeekActivity : ComponentActivity() {
    private lateinit var viewModel: DeepSeekViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = DeepSeekRepository(RetrofitClient.instance)
        val factory = DeepSeekViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DeepSeekViewModel::class.java]

        setContent {
            val intent = intent
            val exercisesData = intent.getSerializableExtra("exercisesData") as? ArrayList<ExercisesData>

            DeepSeekScreen(
                selectedDate = intent.getStringExtra("selectedDate") ?: "",
                heartRate = intent.getDoubleExtra("heartRate", 0.0),
                averageHeartRate = intent.getDoubleExtra("averageHeartRate", 0.0),
                oxygenSaturation = intent.getDoubleExtra("oxygenSaturation", 0.0),
                stepsCount = intent.getLongExtra("stepsCount", 0L),
                sleepDurationMillis = intent.getLongExtra("sleepDurationMillis", 0L),
                sleepQuality = intent.getStringExtra("sleepQuality") ?: "Indefinido",
                caloriesBurned = intent.getDoubleExtra("caloriesBurned", 0.0),
                exerciseData = exercisesData,
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun DeepSeekScreen(
    selectedDate: String,
    heartRate: Double,
    averageHeartRate: Double,
    oxygenSaturation: Double,
    stepsCount: Long,
    sleepDurationMillis: Long,
    sleepQuality: String,
    caloriesBurned: Double,
    exerciseData: ArrayList<ExercisesData>?,
    viewModel: DeepSeekViewModel
) {
    var isLoading by remember { mutableStateOf(true) }
    val resposta by viewModel.respostaLiveData.observeAsState()

    // 📌 Pegando o contexto corretamente dentro do @Composable
    val context = LocalContext.current
    val apiKey = BuildConfig.API_KEY

    // Criando objeto DadosRequest
    val dados = DadosRequest(
        date = selectedDate,
        heartRate = heartRate,
        averageHeartRate = averageHeartRate,
        oxygenSaturation = oxygenSaturation,
        stepsCount = stepsCount,
        sleepDurationMillis = sleepDurationMillis,
        sleepQuality = sleepQuality,
        caloriesBurned = caloriesBurned,
        activeCaloriesBurned = 0.0,
        exercises = exerciseData
    )

    // Criar a mensagem personalizada para análise, incluindo exercícios
    val mensagemParaDeepSeek = buildString {
        append("Olá, DeepSeek! Aqui estão os dados de saúde do usuário para análise:\n\n")
        append("📅 Data da coleta dos dados: $selectedDate\n")
        averageHeartRate?.let {
            if (it > 0) append("❤️ Frequência cardíaca média do dia: $it bpm\n")
        }
        oxygenSaturation?.let {
            if (it > 0) append("🩸 Saturação de oxigênio: $it%\n")
        }
        stepsCount?.let {
            if (it > 0) append("👣 Passos do dia: $it\n")
        }
        sleepQuality?.takeIf { it.isNotBlank() && it.lowercase() != "indefinido" }?.let {
            val horasSono = sleepDurationMillis?.div(3600000) ?: 0L
            if (horasSono > 0) {
                append("😴 Qualidade do sono: $it | Duração: $horasSono horas\n")
            }
        }
        caloriesBurned?.let {
            if (it > 0) append("🔥 Calorias queimadas: $it kcal\n")
        }

        if (exerciseData?.isNotEmpty() == true) {
            append("🏋️ Dados de exercícios:\n")
            exerciseData?.forEach { exercise ->
                append("- ${exercise.exerciseType} (Início: ${exercise.startTime}, Término: ${exercise.endTime})\n")
            }
        } else {
            append("🏋️ Nenhum exercício registrado\n")
        }

        append("\nCom base nesses dados, poderia fornecer **análises e recomendações**?\n")
        append("\nDê dicas relacionadas a melhoria do bem-estar e qualidade de vida\n")
        append("\nNão faça diagnósticos médicos, se ver algo preocupante, fale apenas para a pessoa procurar um medico\n")
        append("\nPode ser algo resumido, não precisa ser muito detalhado\n")
        //append("🔹 O usuário precisa melhorar algum aspecto da saúde?\n")
        //append("🔹 Há riscos ou padrões preocupantes?\n")
        //append("🔹 Quais hábitos podem ser ajustados?\n")
        append("🔹 Por favor, ignore aqueles em que o valor for 0 ou inexistente. Mas não precisa falar pro usuário que ignorou, só ignore e pronto." +
                " Se não tiver dados sobre sono por exemplo, considere que o usuário possa apenas não ter ligado o monitoramento " +
                "de sono ou não ter usado o smartwatch na hora de dormir. Não dê recomendações baseadas nesses dados inexistentes. " +
                "No máximo fale que a análise seria melhor se houvesse mais dados.\n")
        append("🔹 Coloque espaços entre parágrafos ou itens, e use emojis para melhorar a visualização." +
                "Evite fazer parágrafos longos\n")
        append("🔹 Coloque no início do texto a data dos dados no formato PT-BR. Exemplo: Aqui está uma análise baseada nos dados do dia 13/05/2025\n")

        append("\nAgradeço pelas sugestões! 😃")
    }

    // 🔥 Agora usamos diretamente a chave ao enviar os dados
    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.enviarMensagem(BuildConfig.API_KEY, mensagemParaDeepSeek)

        isLoading = false
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // adiciona scroll vertical
    verticalArrangement = Arrangement.Top, // usar Top pra rolar bem
    horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = resposta ?: "Aguardando resposta...")

        if (isLoading) {
            CircularProgressIndicator()
        }
    }
}
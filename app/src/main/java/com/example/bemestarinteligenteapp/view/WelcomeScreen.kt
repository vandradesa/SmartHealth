package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme

import com.example.bemestarinteligenteapp.R
@Composable
fun WelcomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val accentColor = MaterialTheme.colorScheme.primary
    // Scaffold fornece a estrutura básica de layout do Material Design
    Scaffold(
         containerColor = MaterialTheme.colorScheme.secondary
    ) { paddingValues ->
        // Column organiza os elementos verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica o padding do Scaffold
                .padding(16.dp), // Adiciona nosso próprio padding
            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza tudo horizontalmente
            //verticalArrangement = Arrangement.Center // Centraliza tudo verticalmente
        ) {
            Image(
                // Carrega a imagem da pasta drawable
                painter = painterResource(id = R.drawable.logo_smarthealth), // 👈 Use o nome do seu arquivo aqui

                // Texto para acessibilidade (importante para leitores de tela)
                contentDescription = "Logo da Smart Health",

                // Modificadores para tamanho e espaçamento
                modifier = Modifier
                    .size(150.dp) // 🎨 Defina o tamanho que desejar
                    .clip(CircleShape)
                    .padding(bottom = 5.dp) // Mantém o espaçamento que você já tinha
            )

            // 1. Imagem Principal
            Image(
                painter = painterResource(id = R.drawable.imagem_home),
                contentDescription = "Logo do Aplicativo",
                modifier = Modifier
                    .fillMaxWidth() // A imagem ocupa 60% da largura da tela
                    .fillMaxHeight(0.6f)
                    //.size(600.dp)
                    .padding(bottom = 5.dp) // Espaço grande abaixo da imagem
            )
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Monitore, Entenda, Evolua. ",
                fontSize = 35.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth() // 1. Faz o componente ocupar a largura toda
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Seu bem-estar, decifrado pela IA",
                fontSize = 20.sp,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth() // 1. Faz o componente ocupar a largura toda
                    .padding(bottom = 42.dp),
                textAlign = TextAlign.Center, // 2. Alinha o texto ao centro
                color = MaterialTheme.colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), // A Row ocupa toda a largura
                horizontalArrangement = Arrangement.Center // Centraliza os botões (opcional)
            ) {
                // 2. Botão de login
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .weight(1f) // Ocupa uma porção igual do espaço na Row
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("LOGIN",

                    )
                }


                // 3. Espaçamento horizontal entre os botões
                Spacer(modifier = Modifier.width(16.dp))

                // 4. Botão de cadastro
                Button(
                    onClick = { navController.navigate("signup") },
                    modifier = Modifier
                        .weight(1f) // Ocupa uma porção igual do espaço na Row
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)

                ) {
                    Text("CADASTRE-SE",
                        color = MaterialTheme.colorScheme.onSecondary
                    )

                }
            }
        }
    }
}

// Preview para visualização no Android Studio
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    BemEstarInteligenteAppTheme  {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}
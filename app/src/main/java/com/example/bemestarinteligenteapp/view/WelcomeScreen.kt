package com.example.bemestarinteligenteapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bemestarinteligenteapp.ui.theme.BemEstarInteligenteAppTheme

import com.example.bemestarinteligenteapp.R
@Composable
fun WelcomeScreen() {
    val accentColor = MaterialTheme.colorScheme.primary
    // Scaffold fornece a estrutura básica de layout do Material Design
    Scaffold(
         containerColor = MaterialTheme.colorScheme.onPrimary // Ex: um azul bem claro
    ) { paddingValues ->
        // Column organiza os elementos verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplica o padding do Scaffold
                .padding(16.dp), // Adiciona nosso próprio padding
            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza tudo horizontalmente
            verticalArrangement = Arrangement.Center // Centraliza tudo verticalmente
        ) {

            // 1. Imagem Principal
            Image(
                painter = painterResource(id = R.drawable.imagem_home),
                contentDescription = "Logo do Aplicativo",
                modifier = Modifier
                    .fillMaxWidth() // A imagem ocupa 60% da largura da tela
                    .fillMaxHeight(0.8f)
                    //.size(600.dp)
                    .padding(bottom = 5.dp) // Espaço grande abaixo da imagem
            )

            // 2. Botão de Cadastro


            Row(
                modifier = Modifier.fillMaxWidth(), // A Row ocupa toda a largura
                horizontalArrangement = Arrangement.Center // Centraliza os botões (opcional)
            ) {
                // 2. Botão de Cadastro
                Button(
                    onClick = { /* TODO: Adicionar navegação para a tela de Cadastro */ },
                    modifier = Modifier
                        .weight(1f) // Ocupa uma porção igual do espaço na Row
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Login")
                }

                // 3. Espaçamento horizontal entre os botões
                Spacer(modifier = Modifier.width(16.dp))

                // 4. Botão de Login
                Button(
                    onClick = { /* TODO: Adicionar navegação para a tela de Login */ },
                    modifier = Modifier
                        .weight(1f) // Ocupa uma porção igual do espaço na Row
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Cadastre-se")
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
        WelcomeScreen()
    }
}
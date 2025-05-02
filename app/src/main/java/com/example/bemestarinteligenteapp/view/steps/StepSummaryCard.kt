package com.example.bemestarinteligenteapp.view.steps

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StepSummaryCard(steps: Long?, modifier: Modifier = Modifier) {

    Card(
        modifier = Modifier
        .width(165.dp)  // opcional
        .height(225.dp) // define a altura
        .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE4E4EB)) // azul clarinho
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Passos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006064))
            Spacer(modifier = Modifier.height(16.dp))
            if (steps != null) {
                Text(
                    "$steps",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF004D40)
                )
                Text("nas últimas 24h", color = Color(0xFF00796B))
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

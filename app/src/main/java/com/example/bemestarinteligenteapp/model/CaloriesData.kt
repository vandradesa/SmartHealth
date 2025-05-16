package com.example.bemestarinteligenteapp.model

import java.time.Instant

data class CaloriesData(
    val startTime: Instant,
    val endTime: Instant,
    val kilocalories: Double?
)


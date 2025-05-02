package com.example.bemestarinteligenteapp.model

import java.time.Instant

data class HeartRateData(
    val startTime: Instant,
    val endTime: Instant,
    val bpm: Double
)

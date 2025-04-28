package com.example.bemestarinteligenteapp.model

import java.time.Instant

data class StepsData(
    val startTime: Instant,
    val endTime: Instant,
    val count: Long
)

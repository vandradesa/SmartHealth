package com.example.bemestarinteligenteapp.model

import java.time.Instant

data class SleepData(
    val sessionStart: Instant,
    val sessionEnd: Instant,
    val durationMillis: Long,
    val stages: List<SleepStageData>
)

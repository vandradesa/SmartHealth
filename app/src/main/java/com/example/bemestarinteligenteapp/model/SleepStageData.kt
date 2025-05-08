package com.example.bemestarinteligenteapp.model

import java.time.Instant


data class SleepStageData(
    val startTime: Instant,
    val endTime: Instant,
    val stage: Int // Ex: "REM", "LIGHT", "DEEP", "AWAKE"
)
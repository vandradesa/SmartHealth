package com.example.bemestarinteligenteapp.model

import java.time.Instant

data class OxygenSaturationData(
    val time: Instant,
    val percentage: Double
)

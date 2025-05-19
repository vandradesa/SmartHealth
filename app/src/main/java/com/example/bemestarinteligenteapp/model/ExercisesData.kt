package com.example.bemestarinteligenteapp.model


import android.os.Parcelable
import java.io.Serializable

import java.time.Instant



data class ExercisesData(
    val startTime: Instant,
    val endTime: Instant,
    val exerciseType: String

): Serializable

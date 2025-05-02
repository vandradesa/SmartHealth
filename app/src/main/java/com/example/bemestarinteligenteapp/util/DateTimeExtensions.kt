package com.example.bemestarinteligenteapp.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Formata um Instant para "dd/MM/yyyy HH:mm" no fuso local.
 */
fun Instant.formatLocalDateTime(): String {
    val ldt = this.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return ldt.format(fmt)
}

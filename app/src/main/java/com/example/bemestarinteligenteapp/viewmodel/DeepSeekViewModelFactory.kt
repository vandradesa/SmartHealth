package com.example.bemestarinteligenteapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bemestarinteligenteapp.repository.DeepSeekRepository

class DeepSeekViewModelFactory(
    private val repository: DeepSeekRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeepSeekViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeepSeekViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

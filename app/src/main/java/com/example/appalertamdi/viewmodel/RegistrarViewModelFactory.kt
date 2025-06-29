// src/main/java/com/example/appalertamdi/viewmodel/RegistrarViewModelFactory.kt
package com.example.appalertamdi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appalertamdi.config.ApiWeb

class RegistrarViewModelFactory(private val apiWeb: ApiWeb) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrarViewModel(apiWeb) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
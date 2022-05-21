package com.example.google_translate.repository.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.google_translate.repository.TranslateRepository
import com.example.google_translate.ui.TranslateViewModel
import java.lang.IllegalArgumentException

class TranslateViewModelFactory(private val repository: TranslateRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TranslateViewModel::class.java)) {
            return TranslateViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknow ViewModel Class")
    }
}
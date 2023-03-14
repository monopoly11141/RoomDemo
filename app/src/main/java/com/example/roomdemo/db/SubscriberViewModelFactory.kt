package com.example.roomdemo.db

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubscriberViewModelFactory(private val repository: SubscriberRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriberViewModel::class.java)) {
            return SubscriberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}
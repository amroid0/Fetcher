package com.amroid.fetcher.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amroid.fetcher.App
import com.amroid.fetcher.domain.usecases.SendRequestUseCase

class HomeViewModel(val sendRequestUseCase: SendRequestUseCase) : ViewModel() {

  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val application = (this[APPLICATION_KEY] as App)
        HomeViewModel(application.appModule.sendRequestUseCase)
      }
    }
  }
}
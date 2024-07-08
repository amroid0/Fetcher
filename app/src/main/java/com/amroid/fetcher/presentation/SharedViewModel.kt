package com.amroid.fetcher.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amroid.fetcher.domain.entities.Response


class SharedViewModel : ViewModel() {
  private val _responseListSateLiveData = MutableLiveData<Response>()

  public fun responseDetailState(): LiveData<Response> {
    return _responseListSateLiveData
  }

  fun naviagteToDetail(response: Response) {
    _responseListSateLiveData.postValue(response)
  }


  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        SharedViewModel()
      }
    }
  }
}
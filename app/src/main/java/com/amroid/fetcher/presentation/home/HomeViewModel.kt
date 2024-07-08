package com.amroid.fetcher.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amroid.fetcher.App
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.exceptions.InvalidUrlException
import com.amroid.fetcher.domain.exceptions.NoInternetException
import com.amroid.fetcher.domain.usecases.SendRequestUseCase
import com.amroid.fetcher.utils.SingleLiveEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HomeViewModel(val sendRequestUseCase: SendRequestUseCase) : ViewModel() {
  private val _homeSateLiveData = SingleLiveEvent<HomeState>()
  private var task: ExecutorService = Executors.newSingleThreadExecutor()

  public fun getHomeState(): LiveData<HomeState> {
    return _homeSateLiveData
  }

  fun sendRequest(request: Request) {
    _homeSateLiveData.value = HomeState.Loading
    task.execute {
      try {
        sendRequestUseCase(request) {
          _homeSateLiveData.postValue(HomeState.Success(it))
        }
      } catch (ex: NoInternetException) {
        _homeSateLiveData.postValue(HomeState.OnError(ex.message.toString()))

      } catch (ex: InvalidUrlException) {
        _homeSateLiveData.postValue(HomeState.OnError("Url is Empty or Not invalid"))
      }


    }


  }


  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val application = (this[APPLICATION_KEY] as App)
        HomeViewModel(application.appModule.sendRequestUseCase)
      }
    }
  }
}
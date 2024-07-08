package com.amroid.fetcher.presentation.cacheList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.amroid.fetcher.App
import com.amroid.fetcher.domain.entities.SortType
import com.amroid.fetcher.domain.usecases.GetCachedRequestsUseCase
import com.amroid.fetcher.utils.SingleLiveEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CacheListViewModel(val getCachedRequestsUseCase: GetCachedRequestsUseCase) : ViewModel() {
  private val _cacheListSateLiveData = SingleLiveEvent<CacheListState>()
  private var task: ExecutorService = Executors.newSingleThreadExecutor()

  public fun getCacheListState(): LiveData<CacheListState> {
    return _cacheListSateLiveData
  }

  fun getCachedRequestList(sort: SortType) {
    _cacheListSateLiveData.value = CacheListState.Loading
    task.execute {
      try {
        getCachedRequestsUseCase(sort) {
          _cacheListSateLiveData.postValue(CacheListState.Success(it))
        }
      } catch (ex: Exception) {
        _cacheListSateLiveData.postValue(CacheListState.OnError(ex.message.toString()))

      }

    }

  }


  companion object {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
      initializer {
        val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App)
        CacheListViewModel(application.appModule.getCachedRequestsUseCase)
      }
    }
  }
}
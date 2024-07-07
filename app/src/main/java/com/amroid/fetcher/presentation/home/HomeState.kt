package com.amroid.fetcher.presentation.home

import com.amroid.fetcher.domain.entities.Response

sealed class HomeState {
  data object Loading : HomeState()
  data class Success(val response: Response) : HomeState()
  data class OnError(val message: String) : HomeState()


}

package com.amroid.fetcher.presentation.cacheList

import com.amroid.fetcher.domain.entities.Response

sealed class CacheListState {
  data object Loading : CacheListState()
  data class Success(val responses: List<Response>) : CacheListState()
  data class OnError(val message: String) : CacheListState()
  
}

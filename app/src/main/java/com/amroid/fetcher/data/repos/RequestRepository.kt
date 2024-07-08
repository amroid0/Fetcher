package com.amroid.fetcher.data.repos

import com.amroid.fetcher.data.cache.SQLiteHelper
import com.amroid.fetcher.data.remote.ApiClient
import com.amroid.fetcher.domain.IRequestRepository
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.Response
import com.amroid.fetcher.domain.entities.SortType
import com.amroid.fetcher.domain.exceptions.NoInternetException
import com.amroid.fetcher.utils.NetworkUtils

class RequestRepository(
  private val apiClient: ApiClient,
  private val sqliteHelper: SQLiteHelper,
  private val networkUtils: NetworkUtils
) :
  IRequestRepository {


  override fun sendRequest(request: Request, onCallback: (Response) -> Unit) {
    if (!networkUtils.isInternetAvailable()) throw NoInternetException("No Internet")
    apiClient.sendRequest(request) { res ->
      sqliteHelper.insertResponse(res)
      onCallback(res)
    }
  }

  override fun getCachedResponseList(sortType: SortType, onCallback: (List<Response>) -> Unit) {
    when (sortType) {
      SortType.TIME -> {
        onCallback(sqliteHelper.getAllResponsesSortedByExecutionTime())
      }

      SortType.RESPONSE_STATUS -> {
        onCallback(sqliteHelper.getAllResponsesSortedByResponseCode())
      }

      SortType.REQUEST_TYPE -> {
        onCallback(sqliteHelper.getAllResponsesSortedByRequestType())

      }
    }
  }


}
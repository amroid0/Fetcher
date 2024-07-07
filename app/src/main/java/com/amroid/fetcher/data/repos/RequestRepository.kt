package com.amroid.fetcher.data.repos

import com.amroid.fetcher.data.cache.SqliteHelper
import com.amroid.fetcher.data.remote.ApiClient
import com.amroid.fetcher.domain.IRequestRepository
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.Response
import com.amroid.fetcher.domain.exceptions.NoInternetException
import com.amroid.fetcher.utils.NetworkUtils

class RequestRepository(
  private val apiClient: ApiClient,
  private val sqliteHelper: SqliteHelper,
  private val networkUtils: NetworkUtils
) :
  IRequestRepository {


  override fun sendRequest(request: Request, onCallback: (Response) -> Unit) {
    if (!networkUtils.isInternetAvailable()) throw NoInternetException("No Internet")
       apiClient.sendRequest(request,{res ->

       })
  }


}
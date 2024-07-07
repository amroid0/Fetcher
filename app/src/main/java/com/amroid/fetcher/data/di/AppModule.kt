package com.amroid.fetcher.data.di

import android.content.Context
import com.amroid.fetcher.data.cache.SqliteHelper
import com.amroid.fetcher.data.remote.ApiClient
import com.amroid.fetcher.data.repos.RequestRepository
import com.amroid.fetcher.domain.usecases.GetCachedRequestsUseCase
import com.amroid.fetcher.domain.usecases.SendRequestUseCase
import com.amroid.fetcher.utils.NetworkUtils

class AppModule(private val appContext: Context) : IAppModule {
  override val apiClient: ApiClient by lazy {
    ApiClient()
  }
  override val networkUtils: NetworkUtils by lazy {
    NetworkUtils(appContext)
  }
  override val sqliteHelper: SqliteHelper by lazy {
    SqliteHelper()
  }
  override val requestRepository: RequestRepository by lazy {
    RequestRepository(apiClient, sqliteHelper, networkUtils)
  }
  override val sendRequestUseCase: SendRequestUseCase by lazy {
    SendRequestUseCase(requestRepository)
  }
  override val getCachedRequestsUseCase: GetCachedRequestsUseCase by lazy {
    GetCachedRequestsUseCase(requestRepository)
  }


}
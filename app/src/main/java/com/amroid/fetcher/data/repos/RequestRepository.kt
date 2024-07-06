package com.amroid.fetcher.data.repos

import com.amroid.fetcher.data.cache.SqliteHelper
import com.amroid.fetcher.data.remote.ApiClient
import com.amroid.fetcher.domain.IRequestRepository

class RequestRepository(private val apiClient: ApiClient, private val sqliteHelper: SqliteHelper) :
  IRequestRepository {}
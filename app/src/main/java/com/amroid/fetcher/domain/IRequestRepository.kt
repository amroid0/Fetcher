package com.amroid.fetcher.domain

import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.Response

interface IRequestRepository {
  fun sendRequest(request: Request,onCallback:(Response)->Unit)
}
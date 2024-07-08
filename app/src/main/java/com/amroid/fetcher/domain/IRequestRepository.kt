package com.amroid.fetcher.domain

import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.Response
import com.amroid.fetcher.domain.entities.SortType

interface IRequestRepository {
  fun sendRequest(request: Request, onCallback: (Response) -> Unit)

  fun getCachedResponseList(sortType: SortType, onCallback: (List<Response>) -> Unit)

}
package com.amroid.fetcher.domain.usecases

import com.amroid.fetcher.domain.IRequestRepository
import com.amroid.fetcher.domain.entities.Response
import com.amroid.fetcher.domain.entities.SortType

class GetCachedRequestsUseCase(private val requestRepository: IRequestRepository) {
  operator fun invoke(sortType: SortType, callback: (List<Response>) -> Unit) {
    requestRepository.getCachedResponseList(sortType, callback)
  }
}
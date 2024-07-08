package com.amroid.fetcher.domain.usecases

import com.amroid.fetcher.domain.IRequestRepository
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.Response
import com.amroid.fetcher.domain.exceptions.InvalidUrlException
import com.amroid.fetcher.utils.isValidUrl

class SendRequestUseCase(private val requestRepository: IRequestRepository) {
  operator fun invoke(request: Request,callback:(Response)->Unit) {
    if (!request.url.isValidUrl()){
      throw InvalidUrlException("invalid url")
    }
    requestRepository.sendRequest(request,callback)
  }
}
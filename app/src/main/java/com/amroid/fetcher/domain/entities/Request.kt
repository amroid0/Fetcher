package com.amroid.fetcher.domain.entities

class Request(
  val url: String,
  val requestType: RequestType,
  val headers: List<Param> = listOf(),
  val queryParam: List<Param> = listOf(),
  val formData: List<Param> = listOf(),
  val rawBody: String = ""
)
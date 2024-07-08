package com.amroid.fetcher.domain.entities

data class Response(
  val responseCode: Int,
  val headers: List<Param>,
  val body: String,
  val isSuccess: Boolean,
  val request: Request,
  val executionTime: Long = 0L
)
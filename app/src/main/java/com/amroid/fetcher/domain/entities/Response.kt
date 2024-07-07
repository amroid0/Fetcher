package com.amroid.fetcher.domain.entities

data class Response(val responseCode: Int, val headers: Map<String, List<String>>, val body: String,val request: Request)
package com.amroid.fetcher.data.remote

import android.util.Log
import com.amroid.fetcher.domain.entities.Param
import com.amroid.fetcher.domain.entities.ParamType
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.RequestType
import com.amroid.fetcher.domain.entities.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class ApiClient {
  var startTime = 0L
  fun sendRequest(request: Request, onCallback: (Response) -> Unit) {
    val urlWithParams = buildUrlWithParams(request.url, request.queryParam)
    val url = URL(urlWithParams)
    val connection = (url.openConnection() as HttpURLConnection).apply {
      connectTimeout = TIMEOUT
      readTimeout = TIMEOUT
      requestMethod = request.requestType.name
    }
    startTime = System.currentTimeMillis()
    try {
      applyHeaders(connection, request.headers)
      when (request.requestType) {
        RequestType.GET -> {
          onCallback(readResponse(connection, request))
        }

        RequestType.POST -> {
          handlePostRequest(connection, request)
          onCallback(readResponse(connection, request))
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
      val executionTime = System.currentTimeMillis() - startTime
      onCallback(
        Response(
          connection.responseCode,
          readResponseHeader(connection.headerFields),
          e.message ?: "Error",
          false,
          request,
          executionTime
        )
      )
    } finally {
      connection.disconnect()
    }
  }

  private fun readResponseHeader(headerFields: Map<String, MutableList<String>>): List<Param> {
    val headers = mutableListOf<Param>()
    headerFields.forEach { (key, values) ->
        if (key!=null&&values.isNotEmpty())
        headers.add(Param(key, values[0], ParamType.TEXT))

    }
    return headers
  }

  private fun handlePostRequest(connection: HttpURLConnection, request: Request) {
    if (request.formData.isEmpty()) {
      connection.setRequestProperty("Content-Type", "application/json; utf-8")
      connection.setRequestProperty("Accept", "application/json")
      connection.doOutput = true

      connection.outputStream.use { os ->
        OutputStreamWriter(os, "UTF-8").use { writer ->
          writer.write(request.rawBody)
          writer.flush()
        }
      }
    } else {
      val boundary = "Boundary-${System.currentTimeMillis()}"
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
      connection.doOutput = true

      DataOutputStream(connection.outputStream).use { dos ->
        for (param in request.formData) {
          if (param.isFile) {
            writeMultiPartFile(dos, boundary, param)
          } else {
            writeMultiPartField(dos, boundary, param)
          }
        }
        dos.writeBytes("--$boundary--\r\n")
      }
    }
  }

  private fun readResponse(connection: HttpURLConnection, request: Request): Response {
    val responseCode = connection.responseCode
    val headers = connection.headerFields
    val isSuccess = responseCode in 200..299
    val inputStream = if (isSuccess) {
      connection.inputStream
    } else {
      connection.errorStream
    }
    val body = inputStream.bufferedReader().use { it.readText() }
    Log.d("http-response", "readResponse: $body $responseCode")

    return Response(
      responseCode,
      readResponseHeader(headers),
      body,
      isSuccess,
      request,
      System.currentTimeMillis() - startTime
    )
  }

  private fun applyHeaders(connection: HttpURLConnection, headers: List<Param>) {
    headers.forEach { header ->
      connection.setRequestProperty(header.key, header.value)
    }
  }

  private fun buildUrlWithParams(url: String, params: List<Param>): String {
    if (params.isEmpty()) return url
    val queryString = params.joinToString("&") { "${it.key}=${it.value}" }
    return "$url?$queryString"
  }

  private fun writeMultiPartFile(dos: DataOutputStream, boundary: String, param: Param) {
    val file = File(param.fileUri)
    dos.writeBytes("--$boundary\r\n")
    dos.writeBytes("Content-Disposition: form-data; name=\"${param.key}\"; filename=\"${file.name}\"\r\n")
    dos.writeBytes("Content-Type: ${URLConnection.guessContentTypeFromName(file.name)}\r\n")
    dos.writeBytes("Content-Transfer-Encoding: binary\r\n\r\n")

    FileInputStream(file).use { fis ->
      val buffer = ByteArray(4096)
      var bytesRead: Int
      while (fis.read(buffer).also { bytesRead = it } != -1) {
        dos.write(buffer, 0, bytesRead)
      }
    }
    dos.writeBytes("\r\n")
  }

  private fun writeMultiPartField(dos: DataOutputStream, boundary: String, param: Param) {
    dos.writeBytes("--$boundary\r\n")
    dos.writeBytes("Content-Disposition: form-data; name=\"${param.key}\"\r\n\r\n")
    dos.writeBytes("${param.value}\r\n")
  }

  companion object {
    private const val TIMEOUT = 30000
  }
}

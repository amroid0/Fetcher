package com.amroid.fetcher.data.remote

import com.amroid.fetcher.domain.entities.Param
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.RequestType
import com.amroid.fetcher.domain.entities.Response
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class ApiClient {

  private fun readResponse(connection: HttpURLConnection, request: Request): Response {
    val responseCode = connection.responseCode
    val headers = connection.headerFields
    val inputStream = if (responseCode in 200..299) {
      connection.inputStream
    } else {
      connection.errorStream
    }
    val body = inputStream.bufferedReader().use { it.readText() }
    return Response(responseCode, headers, body, request)
  }

  private fun applyHeaders(connection: HttpURLConnection, headers: List<Param>) {
    for (header in headers) {
      connection.setRequestProperty(header.key, header.value)
    }
  }

  private fun buildUrlWithParams(url: String, params: List<Param>): String {
    if (params.isEmpty()) return url
    val queryString = params.joinToString("&") { "${it.key}=${it.value}" }
    return "$url?$queryString"
  }

  fun sendRequest(request: Request, onCallback: (Response) -> Unit) {
    val urlWithParams = buildUrlWithParams(request.url, request.queryParam)
    val url = URL(urlWithParams)
    val connection = url.openConnection() as HttpURLConnection
    connection.connectTimeout = TIMEOUT
    connection.readTimeout = TIMEOUT

    applyHeaders(connection, request.headers)

    when (request.requestType) {
      RequestType.GET -> {
        connection.requestMethod = "GET"
        onCallback(readResponse(connection, request))
      }

      RequestType.POST -> {
        if (request.formData.isEmpty()) {
          connection.requestMethod = "POST"
          connection.setRequestProperty("Content-Type", "application/json; utf-8")
          connection.setRequestProperty("Accept", "application/json")
          connection.doOutput = true
          connection.outputStream.use { os ->
            OutputStreamWriter(os, "UTF-8").use { writer ->
              writer.write(request.rawBody)
              writer.flush()
            }
          }
          onCallback(readResponse(connection, request))
        } else {
          val boundary = "Boundary-${System.currentTimeMillis()}"
          connection.requestMethod = "POST"
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
          onCallback(readResponse(connection, request))
        }
      }
    }
  }

  private fun writeMultiPartFile(
    dos: DataOutputStream,
    boundary: String,
    param: Param
  ) {
    dos.writeBytes("--$boundary\r\n")
    dos.writeBytes("Content-Disposition: form-data; name=\"${param.key}\"; filename=\"${"file.name"}\"\r\n")
    dos.writeBytes("Content-Type: ${URLConnection.guessContentTypeFromName("file.name")}\r\n")
    dos.writeBytes("Content-Transfer-Encoding: binary\r\n\r\n")

    FileInputStream(File(param.fileUri)).use { fis ->
      val buffer = ByteArray(4096)
      var bytesRead: Int
      while (fis.read(buffer).also { bytesRead = it } != -1) {
        dos.write(buffer, 0, bytesRead)
      }
    }
    dos.writeBytes("\r\n")
  }

  private fun writeMultiPartField(
    dos: DataOutputStream,
    boundary: String,
    param: Param
  ) {
    dos.writeBytes("--$boundary\r\n")
    dos.writeBytes("Content-Disposition: form-data; name=\"${param.key}\"\r\n\r\n")
    dos.writeBytes("${param.value}\r\n")
  }

  companion object {
    private const val TIMEOUT = 5000

  }
}
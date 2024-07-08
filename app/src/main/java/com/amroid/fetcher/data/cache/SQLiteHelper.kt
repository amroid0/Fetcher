package com.amroid.fetcher.data.cache

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.amroid.fetcher.domain.entities.Param
import com.amroid.fetcher.domain.entities.ParamType
import com.amroid.fetcher.domain.entities.Request
import com.amroid.fetcher.domain.entities.RequestType
import com.amroid.fetcher.domain.entities.Response

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

  override fun onCreate(db: SQLiteDatabase) {
    val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_URL TEXT,
                $COLUMN_REQUEST_TYPE TEXT,
                $COLUMN_HEADERS TEXT,
                $COLUMN_QUERY_PARAMS TEXT,
                $COLUMN_FORM_DATA TEXT,
                $COLUMN_RAW_BODY TEXT,
                $COLUMN_RESPONSE_CODE INTEGER,
                $COLUMN_RESPONSE_HEADERS TEXT,
                $COLUMN_BODY TEXT,
                $COLUMN_EXECUTION_TIME LONG,
                $COLUMN_IS_SUCCESS INT
            )
        """
    db.execSQL(createTableQuery)
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(db)
  }

  fun insertResponse(response: Response) {
    val db = this.writableDatabase
    val values = ContentValues().apply {
      put(COLUMN_URL, response.request.url)
      put(COLUMN_REQUEST_TYPE, response.request.requestType.name)
      put(COLUMN_HEADERS, response.request.headers.joinToString(",") { "${it.key}::${it.value}" })
      put(COLUMN_QUERY_PARAMS, response.request.queryParam.joinToString(",") { "${it.key}::${it.value}" })
      put(COLUMN_FORM_DATA, response.request.formData.joinToString(",") { "${it.key}::${if (it.isFile) it.fileUri else it.value}" })
      put(COLUMN_RAW_BODY, response.request.rawBody)
      put(COLUMN_RESPONSE_CODE, response.responseCode)
      put(COLUMN_RESPONSE_HEADERS, response.headers.joinToString(",") { "${it.key}::${it.value}" })
      put(COLUMN_BODY, response.body)
      put(COLUMN_EXECUTION_TIME, response.executionTime)
      put(COLUMN_IS_SUCCESS, if (response.isSuccess) 1 else 0)
    }
    db.insert(TABLE_NAME, null, values)
    db.close()
  }

  fun getAllResponsesSortedByExecutionTime(): List<Response> {
    return getAllResponsesSortedBy(COLUMN_EXECUTION_TIME)
  }

  fun getAllResponsesSortedByRequestType(): List<Response> {
    return getAllResponsesSortedBy(COLUMN_REQUEST_TYPE)
  }

  fun getAllResponsesSortedByResponseCode(): List<Response> {
    return getAllResponsesSortedBy(COLUMN_RESPONSE_CODE)
  }

  private fun getAllResponsesSortedBy(column: String): List<Response> {
    val responses = mutableListOf<Response>()
    val db = this.readableDatabase
    val cursor = db.query(
      TABLE_NAME,
      null,
      null,
      null,
      null,
      null,
      "$column ASC"
    )
    if (cursor.moveToFirst()) {
      do {
        val response = Response(
          responseCode = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESPONSE_CODE)),
          headers = parseParams(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSE_HEADERS))),
          body = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BODY)),
          request = Request(
            url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
            requestType = RequestType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_TYPE))),
            headers = parseParams(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADERS))),
            queryParam = parseParams(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUERY_PARAMS))),
            formData = parseParams(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FORM_DATA))),
            rawBody = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RAW_BODY))
          ),
          executionTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_EXECUTION_TIME)),
          isSuccess = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SUCCESS)) == 1
        )
        responses.add(response)
      } while (cursor.moveToNext())
    }
    cursor.close()
    db.close()
    return responses
  }



  private fun parseParams(paramString: String?): List<Param> {
    return paramString?.takeIf { it.isNotEmpty() }?.split(",")?.mapNotNull {
      val parts = it.split("::")
      if (parts.size == 2) Param(parts[0], parts[1], ParamType.TEXT) else null
    } ?: emptyList()
  }

  companion object {
    private const val DATABASE_NAME = "responses.db"
    private const val DATABASE_VERSION = 1

    private const val TABLE_NAME = "response"
    private const val COLUMN_ID = "id"
    private const val COLUMN_URL = "url"
    private const val COLUMN_REQUEST_TYPE = "request_type"
    private const val COLUMN_HEADERS = "headers"
    private const val COLUMN_QUERY_PARAMS = "query_params"
    private const val COLUMN_FORM_DATA = "form_data"
    private const val COLUMN_RAW_BODY = "raw_body"
    private const val COLUMN_RESPONSE_CODE = "response_code"
    private const val COLUMN_RESPONSE_HEADERS = "response_headers"
    private const val COLUMN_BODY = "body"
    private const val COLUMN_EXECUTION_TIME = "execution_time"
    private const val COLUMN_IS_SUCCESS = "is_success"
  }
}

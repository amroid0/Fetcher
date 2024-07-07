package com.amroid.fetcher.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import java.io.File

class FilePicker(
  private val context: Context
) : ActivityResultContract<String, List<@JvmSuppressWildcards String>>() {

  override fun createIntent(context: Context, input: String): Intent {
    return Intent(Intent.ACTION_GET_CONTENT).apply {
      addCategory(Intent.CATEGORY_OPENABLE)
      type = input
      putExtra(Intent.EXTRA_LOCAL_ONLY, true)
      putExtra(
        Intent.EXTRA_ALLOW_MULTIPLE,
        false
      ).addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
  }

  override fun getSynchronousResult(
    context: Context, input: String
  ): SynchronousResult<List<String>>? = null

  override fun parseResult(resultCode: Int, intent: Intent?): List<String> {
    return intent.takeIf {
      resultCode == Activity.RESULT_OK
    }?.getClipDataUris(context) ?: emptyList()
  }

  internal companion object {
    internal fun Intent.getClipDataUris(context: Context): List<String> {
      val resultSet = LinkedHashSet<String>()
      data?.let { data ->
        resultSet.add(data.toString())
      }
      val clipData = clipData
      if (clipData == null && resultSet.isEmpty()) {
        return emptyList()
      } else if (clipData != null) {
        for (i in 0 until clipData.itemCount) {
          val uri = clipData.getItemAt(i).uri
          if (uri != null) {
            val fileUri = getUri(uri, context)
            resultSet.add(fileUri.toString())
          }
        }
      }
      return ArrayList(resultSet)
    }

    private fun getUri(uri: Uri, context: Context): Uri {
      var resultURI = uri
      if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
        val cr: ContentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extensionFile = mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))
        val file = File.createTempFile(
          "temp${System.currentTimeMillis()}", ".$extensionFile", context.cacheDir
        )
        val input = cr.openInputStream(uri)
        file.outputStream().use { stream ->
          input?.copyTo(stream)
        }
        input?.close()
        resultURI = Uri.fromFile(file)
      }
      return resultURI
    }
  }

}
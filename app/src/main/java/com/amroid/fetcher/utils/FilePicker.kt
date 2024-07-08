package com.amroid.fetcher.utils
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FilePicker(private val activity: FragmentActivity) {
  private val pickFileLauncher: ActivityResultLauncher<Intent>
  private var listener: FilePickerListener? = null

  init {
    pickFileLauncher = activity.registerForActivityResult(
      ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
      if (result.resultCode == Activity.RESULT_OK) {
        if (result.data != null) {
          val uri = result.data!!.data
          if (listener != null) {
            listener?.onFilePicked(
              uri, fileFromContentUri(
                activity, uri!!
              )
            )
          }
        }
      } else {
        if (listener != null) {
          listener?.onFilePickFailed()
        }
      }
    }
  }

  fun setFilePickerListener(listener: FilePickerListener?) {
    this.listener = listener
  }

  fun pickFile() {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    pickFileLauncher.launch(Intent.createChooser(intent, "Select File"))
  }

  interface FilePickerListener {
    fun onFilePicked(uri: Uri?, filePath: String?)
    fun onFilePickFailed()
  }

  companion object {
    private fun fileFromContentUri(context: FragmentActivity, contentUri: Uri): String {

      val fileExtension = getFileExtension(context, contentUri)
      val fileName = "temporary_file${System.currentTimeMillis()}" + if (fileExtension != null) ".$fileExtension" else ""

      val tempFile = File(context.cacheDir, fileName)
      tempFile.createNewFile()

      try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
          copy(inputStream, oStream)
        }

        oStream.flush()
      } catch (e: Exception) {
        e.printStackTrace()
      }

      return tempFile.absolutePath
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
      val fileType: String? = context.contentResolver.getType(uri)
      return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    @Throws(IOException::class)
    private fun copy(source: InputStream, target: OutputStream) {
      val buf = ByteArray(8192)
      var length: Int
      while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
      }
    }
  }
}
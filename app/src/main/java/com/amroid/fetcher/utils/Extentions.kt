package com.amroid.fetcher.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun EditText.watchText(afterChanged: (text: String?) -> Unit) {
  addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
      afterChanged(editable.toString())
    }
  })
}
fun String.isValidUrl(): Boolean {
  return this.isNotEmpty() && Patterns.WEB_URL.matcher(this).matches()
}

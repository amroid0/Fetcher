package com.amroid.fetcher.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.watchText(afterChanged: (text: String?) -> Unit) {
  addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
      afterChanged(editable.toString())
    }
  })
}
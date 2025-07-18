package com.example.agrvai

import android.text.Editable
import android.text.TextWatcher

class SimpleTextWatcher(private val onAfterTextChanged: (String) -> Unit) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        onAfterTextChanged(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
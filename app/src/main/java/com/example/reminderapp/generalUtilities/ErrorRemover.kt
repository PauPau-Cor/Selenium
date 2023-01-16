package com.example.reminderapp.generalUtilities

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout


class ErrorRemover(private val field: TextInputLayout) : TextWatcher {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {
        field.error = null
    }

}
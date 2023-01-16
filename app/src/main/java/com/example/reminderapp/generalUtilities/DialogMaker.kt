package com.example.reminderapp.generalUtilities

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.reminderapp.R
import com.example.reminderapp.generalDialogs.DialogDatePicker
import com.google.android.material.textfield.TextInputLayout

class DialogMaker {

    fun pickDate(field: TextInputLayout, fragmentManager: FragmentManager, context: Context) {
        val datePicker = DialogDatePicker { day, month, year -> run { field.editText?.setText(context.getString(R.string.date_formatter, day, month+1,year)) } }
        datePicker.show(fragmentManager, "datePicker")
    }
}
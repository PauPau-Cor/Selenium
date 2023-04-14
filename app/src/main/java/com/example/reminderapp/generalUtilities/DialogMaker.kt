package com.example.reminderapp.generalUtilities

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.widget.TimePicker
import androidx.fragment.app.FragmentManager
import com.example.reminderapp.R
import com.example.reminderapp.generalDialogs.DialogDatePicker
import com.google.android.material.textfield.TextInputLayout


class DialogMaker {

    fun pickDate(field: TextInputLayout, fragmentManager: FragmentManager, context: Context) {
        val datePicker = DialogDatePicker { day, month, year -> run { field.editText?.setText(context.getString(R.string.date_formatter, day, month+1,year)) } }
        datePicker.show(fragmentManager, "datePicker")
    }

    fun pickTime(field: TextInputLayout, context: Context){
        var hour = 0
        var minute = 0
        val onTimeSetListener =
            OnTimeSetListener { _: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                hour = selectedHour
                minute = selectedMinute
                field.editText?.setText(GeneralUtilities().round5Minutes(selectedHour, selectedMinute))
            }
        val timePickerDialog = TimePickerDialog(
            context, onTimeSetListener,
            hour, minute, true
        )
        timePickerDialog.show()
    }
}
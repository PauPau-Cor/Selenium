package com.example.reminderapp.generalDialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DialogDatePicker (val listener: (day: Int, month: Int, year: Int) -> Unit) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        listener(day,month, year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity as Context, this, year, month, day)
    }

}
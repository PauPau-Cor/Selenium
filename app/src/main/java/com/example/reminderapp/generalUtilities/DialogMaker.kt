package com.example.reminderapp.generalUtilities

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.view.View
import android.widget.TimePicker
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.FragmentManager
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.generalDialogs.DialogAddFolder
import com.example.reminderapp.generalDialogs.DialogDatePicker
import com.example.reminderapp.models.CategoryModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore


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

    fun addFolder(fragmentManager: FragmentManager, view: View, db: FirebaseFirestore, userID: String){
        val addFolder = DialogAddFolder{ title  -> run{
            db.collection(Constants.CategoriesCollection).add(CategoryModel(userID = userID, title = title))
                .addOnSuccessListener {
                    Snackbar.make(view, R.string.folder_uploaded, LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Snackbar.make(view, view.context.getString(R.string.err_default, it.message), LENGTH_SHORT).show()
                }
        }}
        addFolder.show(fragmentManager, "folderAdder")
    }
}
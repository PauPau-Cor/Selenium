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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DialogMaker(var date: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)) {

    fun pickDate(field: TextInputLayout, fragmentManager: FragmentManager) {
        val datePicker = DialogDatePicker { day, month, year -> run {
            date = date.withYear(year)
            date = date.withMonth(month+1)
            date = date.withDayOfMonth(day)
            field.editText?.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)))
        } }
        datePicker.show(fragmentManager, "datePicker")
    }

    fun pickTime(field: TextInputLayout, context: Context){
        val onTimeSetListener =
            OnTimeSetListener { _: TimePicker?, selectedHour: Int, selectedMinute: Int ->
                date = date.withHour(selectedHour)
                date = date.withMinute(selectedMinute)
                val mod = selectedMinute % 5
                date = date.plusMinutes((if (mod < 3) -mod else 5 - mod).toLong())
                field.editText?.setText(date.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
            }
        val timePickerDialog = TimePickerDialog(
            context, onTimeSetListener,
            date.hour, date.minute, false
        )
        timePickerDialog.show()
    }

    fun addFolder(fragmentManager: FragmentManager, view: View, db: FirebaseFirestore, userID: String){
        val addFolder = DialogAddFolder(false) { title  -> run{
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

    fun editFolder(fragmentManager: FragmentManager, view: View, db: FirebaseFirestore, folder: CategoryModel){
        val updateBatch = db.batch()
        val addFolder = DialogAddFolder(true) { title  -> run{
            val categoryRef = db.collection(Constants.CategoriesCollection).document(folder.categoryID!!)
            updateBatch.update(categoryRef, Constants.titleField, title)

            db.collection(Constants.TasksCollection).whereEqualTo(Constants.categoryIDField, folder.categoryID).get().addOnSuccessListener { snapshot ->
                snapshot.forEach {
                    updateBatch.update(it.reference, Constants.categoryNameField, title)
                }
                updateBatch.commit().addOnSuccessListener {
                    Snackbar.make(view, R.string.folder_updated, LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Snackbar.make(view, view.context.getString(R.string.err_default, it.message), LENGTH_SHORT).show()
                }
            }
        }}
        addFolder.show(fragmentManager, "folderAdder")
    }
}
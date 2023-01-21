package com.example.reminderapp.generalUtilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.example.reminderapp.R
import com.example.reminderapp.databinding.DialogAdvancedAddTaskBinding
import com.example.reminderapp.generalDialogs.DialogDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class DialogMaker {

    fun pickDate(field: TextInputLayout, fragmentManager: FragmentManager, context: Context) {
        val datePicker = DialogDatePicker { day, month, year -> run { field.editText?.setText(context.getString(R.string.date_formatter, day, month+1,year)) } }
        datePicker.show(fragmentManager, "datePicker")
    }

    fun advancedTask(context: Context, title : String?, date: String?, priority: Int?) {
        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding: DialogAdvancedAddTaskBinding = DialogAdvancedAddTaskBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
        (dialogBinding.TaskFolder.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        dialogBinding.TaskTitle.editText!!.setText(title)
        dialogBinding.TaskDate.editText!!.setText(date)
        when(priority){
            0 -> dialogBinding.PriorityGroup.selectButton(R.id.lowPriorityBtn)
            1 -> dialogBinding.PriorityGroup.selectButton(R.id.middlePriorityBtn)
            2 -> dialogBinding.PriorityGroup.selectButton(R.id.highPriorityBtn)
        }
        dialogBinding.closeBtn.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }


}
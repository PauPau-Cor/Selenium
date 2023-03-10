package com.example.reminderapp.generalUtilities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.reminderapp.R
import com.example.reminderapp.adapters.AddToDoTimeViewPagerAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.DialogAdvancedAddTaskBinding
import com.example.reminderapp.generalDialogs.DialogDatePicker
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.UserModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore


class DialogMaker {

    fun pickDate(field: TextInputLayout, fragmentManager: FragmentManager, context: Context) {
        val datePicker = DialogDatePicker { day, month, year -> run { field.editText?.setText(context.getString(R.string.date_formatter, day, month+1,year)) } }
        datePicker.show(fragmentManager, "datePicker")
    }

    fun advancedTask(context: Context, fragment: Fragment, userModel: UserModel, title : String?, date: String?, priority: Int?) {
        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding: DialogAdvancedAddTaskBinding = DialogAdvancedAddTaskBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        setupDropdownOptions(dialogBinding, userModel, context)
        dialogBinding.TaskTitle.editText!!.setText(title)
        setupTabLayout(dialogBinding, fragment, context)
        dialogBinding.closeBtn.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupTabLayout(dialogBinding: DialogAdvancedAddTaskBinding, fragment: Fragment, context: Context) {
        dialogBinding.viewPager.adapter = AddToDoTimeViewPagerAdapter(fragment)
        TabLayoutMediator(dialogBinding.timeDefinition, dialogBinding.viewPager) { tab, pos ->
            when(pos){
                0 -> {
                    tab.text = context.getString(R.string.time_undefined)
                    tab.icon = AppCompatResources.getDrawable(context, R.drawable.ic_no_event)
                }

                1 -> {
                    tab.text = context.getString(R.string.time_defined)
                    tab.icon = AppCompatResources.getDrawable(context, R.drawable.ic_event)
                }

                2 -> {
                    tab.text = context.getString(R.string.time_repetitive)
                    tab.icon = AppCompatResources.getDrawable(context, R.drawable.ic_event_repeat)
                }
            }
        }.attach()
    }

    //TODO: handle no connection situation
    private fun setupDropdownOptions(dialogBinding: DialogAdvancedAddTaskBinding, userModel: UserModel, context: Context){

        val importances : Array<String> = arrayOf(context.getString(R.string.high), context.getString(R.string.mid), context.getString(R.string.low))
        val importancesAdapter = ArrayAdapter(context, R.layout.holder_dropdown_item, importances)
        (dialogBinding.TaskImportance.editText as? MaterialAutoCompleteTextView)?.setAdapter(importancesAdapter)

        val db : FirebaseFirestore = FirebaseFirestore.getInstance()
        val query = db.collection(Constants.CategoriesCollection).whereEqualTo(Constants.userIDField, userModel.userID)
        query.get().addOnSuccessListener {
            val folders: ArrayList<CategoryModel> = it.toObjects(CategoryModel::class.java) as ArrayList<CategoryModel>
            val names = ArrayList<String>()
            for (folder in folders) names.add(folder.title)
            names.add(0, context.getString(R.string.add))
            names.add(0, context.getString(R.string.no_folder))
            val dropDownOptions: Array<String> = names.toArray(arrayOfNulls(names.size))
            val foldersAdapter = ArrayAdapter(context, R.layout.holder_dropdown_item, dropDownOptions)
            (dialogBinding.TaskFolder.editText as? MaterialAutoCompleteTextView)?.setAdapter(foldersAdapter)
        }.addOnFailureListener{

        }

    }

}
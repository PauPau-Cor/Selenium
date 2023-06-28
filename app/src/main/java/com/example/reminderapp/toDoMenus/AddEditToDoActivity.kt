package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.View.GONE
import android.widget.ArrayAdapter
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.reminderapp.R
import com.example.reminderapp.adapters.AddToDoTimeViewPagerAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityAddEditToDoBinding
import com.example.reminderapp.generalUtilities.FieldsValidator
import com.example.reminderapp.generalUtilities.GeneralUtilities
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.ZoneId
import java.util.Date
import kotlin.streams.toList

class AddEditToDoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditToDoBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var userModel : UserModel
    private var categoryModel: CategoryModel? = null
    private lateinit var folders: ArrayList<CategoryModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        @Suppress("DEPRECATION")
        userModel = intent.getParcelableExtra(Constants.PutExUser)!!

        @Suppress("DEPRECATION")
        if(intent.hasExtra(Constants.PutExFolder)){
            categoryModel = intent.getParcelableExtra(Constants.PutExFolder)
            binding.TaskFolder.visibility = GONE
        }

        setupDropdownOptions()
        setupTabLayout()

        binding.Toolbar.setNavigationOnClickListener {finish() }

        binding.confirmButton.setOnClickListener{
            validateData()
        }
    }

    private fun validateData() {
        val validator = FieldsValidator()
        val fieldsToValEmpt : Array<TextInputLayout>

        when(binding.viewPager.currentItem){
            1 -> {
                fieldsToValEmpt = arrayOf(
                    binding.TaskTitle,
                    binding.TaskImportance,
                    (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter).taskWithTime.binding.TaskTime,
                    (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter).taskWithTime.binding.TaskDate,
                    )
            }
            2 -> {
                fieldsToValEmpt = arrayOf(
                    binding.TaskTitle,
                    binding.TaskImportance,
                    (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter).taskRepeating.binding.TaskTime,
                    )
                if ((binding.viewPager.adapter as AddToDoTimeViewPagerAdapter).taskRepeating.binding.WeekGroup.selectedButtons.size == 0){
                    Snackbar.make(binding.root, this.getString(R.string.err_select_days), LENGTH_SHORT).show()
                    return
                }
            }
            else -> {
                fieldsToValEmpt = arrayOf(
                    binding.TaskTitle,
                    binding.TaskImportance,
                )
            }
        }

        if(!validator.checkIfNotEmpty(fieldsToValEmpt, this)){ return }
        if(categoryModel == null && !validator.checkIfNotEmpty(binding.TaskFolder, this)){ return }

        makeModel()
    }

    private fun makeModel() {
        val priority : Int = when(binding.TaskImportance.editText?.text.toString()){
            this.getString(R.string.high) ->{
                2
            }
            this.getString(R.string.mid) ->{
                1
            }
            this.getString(R.string.low) ->{
                0
            }
            else -> {
                1
            }
        }
        val newTask = TaskModel(
            userID = userModel.userID!!,
            title = binding.TaskTitle.editText?.text.toString().trim(),
            priority = priority,
            token = userModel.token
        )

        if(categoryModel != null){
            newTask.categoryName = categoryModel!!.title
            newTask.categoryID = categoryModel!!.categoryID!!
        }else{
            newTask.categoryName = binding.TaskFolder.editText?.text.toString()

            if(newTask.categoryName  != this.getString(R.string.no_folder)){
                folders.stream().filter{it.title == newTask.categoryName }
                    .limit(1)
                    .toList().forEach{
                        newTask.categoryID = it.categoryID.toString()
                    }
            }
        }


        when(binding.viewPager.currentItem){
            1 -> {
                newTask.dueType = 1
                newTask.setDate = Date.from(
                    (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter).getSetDate()
                        .atZone(ZoneId.systemDefault()).toInstant()
                )
            }
            2 -> {
                newTask.dueType = 2
                newTask.repeatDates = arrayListOf()
                val genUtil = GeneralUtilities()
                val time : String = (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter)
                    .taskRepeating.binding.TaskTime.editText?.text.toString()
                (binding.viewPager.adapter as AddToDoTimeViewPagerAdapter)
                    .taskRepeating.binding.WeekGroup.selectedButtons.forEach {
                        when(it.text){
                            this.getString(R.string.abr_sunday) -> {
                                newTask.days.add(0)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.SUNDAY, time))
                            }
                            this.getString(R.string.abr_monday) -> {
                                newTask.days.add(1)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.MONDAY, time))
                            }
                            this.getString(R.string.abr_tuesday) -> {
                                newTask.days.add(2)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.TUESDAY, time))
                            }
                            this.getString(R.string.abr_wednesday) -> {
                                newTask.days.add(3)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.WEDNESDAY, time))
                            }
                            this.getString(R.string.abr_thursday) -> {
                                newTask.days.add(4)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.THURSDAY, time))
                            }
                            this.getString(R.string.abr_friday) -> {
                                newTask.days.add(5)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.FRIDAY, time))
                            }
                            this.getString(R.string.abr_saturday) -> {
                                newTask.days.add(6)
                                newTask.repeatDates!!.add(genUtil.nextOrSameWithTime(DayOfWeek.SATURDAY, time))
                            }
                        }
                    }
                newTask.days.sort()
            }
        }
        uploadAdvancedTask(newTask)
    }

    private fun uploadAdvancedTask(taskModel: TaskModel) {
        binding.TaskTitle.editText?.setText("")
        binding.TaskFolder.editText?.setText("")
        binding.TaskImportance.editText?.setText("")
        binding.viewPager.setCurrentItem(0, true)
        val batch = db.batch()
        val taskRef = db.collection(Constants.TasksCollection).document()
        batch.set(taskRef, taskModel)
        if(taskModel.categoryID.isNotBlank()){
            val folderRef = db.collection(Constants.CategoriesCollection).document(taskModel.categoryID)
            batch.update(folderRef, Constants.lastEditedField, Date())
        }

        batch.commit().addOnSuccessListener {
            Snackbar.make(binding.root, this.getString(R.string.task_uploaded), Snackbar.LENGTH_LONG).show()
        }.addOnFailureListener{
            Snackbar.make(binding.root, this.getString(R.string.err_default, it.message), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun setupTabLayout() {
        binding.viewPager.adapter = AddToDoTimeViewPagerAdapter(this)
        TabLayoutMediator(binding.timeDefinition, binding.viewPager) { tab, pos ->
            when(pos){
                0 -> {
                    tab.text = this.getString(R.string.time_undefined)
                    tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_no_event)
                }

                1 -> {
                    tab.text = this.getString(R.string.time_defined)
                    tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_event)
                }

                2 -> {
                    tab.text = this.getString(R.string.time_repetitive)
                    tab.icon = AppCompatResources.getDrawable(this, R.drawable.ic_event_repeat)
                }
            }
        }.attach()
    }

    //TODO: handle no connection situation
    private fun setupDropdownOptions(){

        val importances : Array<String> = arrayOf(this.getString(R.string.high), this.getString(R.string.mid), this.getString(R.string.low))
        val importancesAdapter = ArrayAdapter(this, R.layout.holder_dropdown_item, importances)
        (binding.TaskImportance.editText as? MaterialAutoCompleteTextView)?.setAdapter(importancesAdapter)
        val query = db.collection(Constants.CategoriesCollection).whereEqualTo(Constants.userIDField, userModel.userID)
        query.get().addOnSuccessListener {
            folders = it.toObjects(CategoryModel::class.java) as ArrayList<CategoryModel>
            folders.add(0, CategoryModel(title = this.getString(R.string.no_folder)))
            val names = ArrayList<String>()
            for (folder in folders) names.add(folder.title)
            val dropDownOptions: Array<String> = names.toArray(arrayOfNulls(names.size))
            val foldersAdapter = ArrayAdapter(this, R.layout.holder_dropdown_item, dropDownOptions)
            (binding.TaskFolder.editText as? MaterialAutoCompleteTextView)?.setAdapter(foldersAdapter)
        }.addOnFailureListener{

        }

    }

}
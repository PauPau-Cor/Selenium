package com.example.reminderapp.toDoMenus

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.adapters.FolderTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.ActivityFolderTasksBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.WrappedLinearLayoutManager
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Locale

class FolderTasksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderTasksBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var userModel : UserModel
    private lateinit var folderModel: CategoryModel
    private lateinit var upcAdapter: FolderTasksAdapter
    private lateinit var weeklyAdapter: FolderTasksAdapter
    private lateinit var noDateAdapter: FolderTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()
        @Suppress("DEPRECATION")
        userModel = intent.getParcelableExtra(Constants.PutExUser)!!

        setUpFolderModelOrDefault()

        binding.FolderTitle.text = folderModel.title


        setUpUpc()
        setUpWeekly()
        setUpNoDate()
        setUpAdd()
    }

    private fun setUpFolderModelOrDefault() {
        if(intent.hasExtra(Constants.PutExFolder)){
            @Suppress("DEPRECATION")
            folderModel = intent.getParcelableExtra(Constants.PutExFolder)!!

            val localTime = folderModel.lastEdited.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            binding.folderDate.text = localTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

            val dialogMaker = DialogMaker()
            binding.editFolderBtn.setOnClickListener{
                dialogMaker.editFolder(supportFragmentManager, binding.root, db, folderModel)
            }
        }else{
            folderModel = CategoryModel("", title= getString(R.string.no_folder))
            binding.folderDate.visibility = GONE
            binding.editFolderBtn.visibility = GONE
        }
    }

    private fun setUpUpc() {
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.categoryIDField, folderModel.categoryID)
            .whereEqualTo(Constants.dueTypeField, 1).orderBy(Constants.setDateField)
            .whereEqualTo(Constants.finishedField, 0)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        upcAdapter = FolderTasksAdapter(options, this, binding.upcNoResults)
        binding.upcRV.adapter = upcAdapter
        binding.upcRV.layoutManager = WrappedLinearLayoutManager(this)
    }
    private fun setUpWeekly() {
        val currentDate = Calendar.getInstance(Locale.getDefault())
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.categoryIDField, folderModel.categoryID)
            .whereEqualTo(Constants.dueTypeField, 2).whereArrayContains(Constants.daysField, currentDate.get(
                Calendar.DAY_OF_WEEK)-1)
            .whereEqualTo(Constants.finishedField, 0)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        weeklyAdapter = FolderTasksAdapter(options, this, binding.weeklyNoResults)
        binding.weeklyRV.adapter = weeklyAdapter
        binding.weeklyRV.layoutManager = WrappedLinearLayoutManager(this, RecyclerView.HORIZONTAL)
    }

    private fun setUpNoDate() {
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.categoryIDField, folderModel.categoryID)
            .whereEqualTo(Constants.dueTypeField, 0).whereEqualTo(Constants.finishedField, 0)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        noDateAdapter = FolderTasksAdapter(options, this, binding.noDateNoResults)
        binding.noDateRV.adapter = noDateAdapter
        binding.noDateRV.layoutManager = WrappedLinearLayoutManager(this)
    }

    private fun setUpAdd() {
        binding.AddTaskBT.setOnClickListener {
            startActivity(Intent(this, AddEditToDoActivity::class.java)
                .putExtra(Constants.PutExUser, userModel).putExtra(Constants.PutExFolder, folderModel))
        }
    }

    override fun onStart() {
        super.onStart()
        upcAdapter.startListening()
        weeklyAdapter.startListening()
        noDateAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        upcAdapter.stopListening()
        weeklyAdapter.stopListening()
        noDateAdapter.stopListening()
    }
}
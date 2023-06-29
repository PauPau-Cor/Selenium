package com.example.reminderapp.generalMenus

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.adapters.TodayWeeklyTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentHomeBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.GeneralUtilities
import com.example.reminderapp.generalUtilities.WrappedLinearLayoutManager
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
import com.example.reminderapp.toDoMenus.AddEditToDoActivity
import com.example.reminderapp.toDoMenus.FolderTasksActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userModel : UserModel
    private lateinit var db : FirebaseFirestore
    private lateinit var adapter: TodayWeeklyTasksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            userModel = it.getParcelable(Constants.PutExUser)!!
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).setDuration(650)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).setDuration(650)
        db = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.wlcMsg.text = getString(R.string.welcome, userModel.name)

        setupPastDueTaskData()
        setupUpcomingTaskData()
        setupWeeklyTodayTasks()
        setupFolders()
        setupAddTask()
    }

    private fun setupAddTask() {
        binding.AddTaskBT.setOnClickListener{
            startActivity(Intent(context, AddEditToDoActivity::class.java).putExtra(Constants.PutExUser, userModel))
        }
    }

    private fun setupPastDueTaskData() {
        var currentDate = Date()
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.dueTypeField, 1).whereEqualTo(Constants.finishedField, 0)
            .whereLessThanOrEqualTo(Constants.setDateField, currentDate)
            .orderBy(Constants.setDateField, Query.Direction.DESCENDING).limit(1)
        query.addSnapshotListener{ value, error ->
            if (error == null && value != null && !value.isEmpty) {
                binding.pastDueGroup.visibility = VISIBLE
                val pastDueTask: TaskModel = value.documents[0].toObject()!!
                binding.pastDueTitle.text = pastDueTask.title

                val localTime = pastDueTask.setDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                binding.pastDueTime.text = localTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

                currentDate = Date()
            }else{
                binding.pastDueGroup.visibility = GONE
                currentDate = Date()
            }
        }
    }

    private fun setupUpcomingTaskData() {
        var currentDate = Date()
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.dueTypeField, 1).whereEqualTo(Constants.finishedField, 0)
            .whereGreaterThan(Constants.setDateField, currentDate).orderBy(Constants.setDateField).limit(1)
        query.addSnapshotListener{ value, error ->
            if (error == null && value != null && !value.isEmpty) {
                val upcomingtask: TaskModel = value.documents[0].toObject()!!
                binding.upTaskTitle.text = upcomingtask.title

                val localTime = upcomingtask.setDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                binding.upTaskTime.text = localTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

                binding.upTaskPrio.visibility = VISIBLE
                binding.separator.visibility = VISIBLE
                binding.upcDone.visibility = VISIBLE

                when(upcomingtask.priority){
                    0 ->{
                        binding.upTaskPrio.setImageResource(R.drawable.ic_prio_low)
                    }
                    1 ->{
                        binding.upTaskPrio.setImageResource(R.drawable.ic_prio_mid)
                    }
                    2 ->{
                        binding.upTaskPrio.setImageResource(R.drawable.ic_prio_high)
                    }
                }

                binding.upcmnCrd.setOnLongClickListener{
                    showTaskPopUpMenu(upcomingtask, it)
                    return@setOnLongClickListener true
                }

                currentDate = Date()
            }else{
                binding.upTaskTitle.text = getString(R.string.no_upc_task)
                binding.upTaskTime.text = ""
                binding.upTaskPrio.visibility = INVISIBLE
                binding.separator.visibility = GONE
                binding.upcDone.visibility = GONE
                currentDate = Date()
            }
        }
    }

    private fun showTaskPopUpMenu(model: TaskModel, view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.task_popup_menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener handleFolderPopUpMenuClick(model, view, it)
        }

        popupMenu.show()
    }

    private fun handleFolderPopUpMenuClick(model: TaskModel, view: View, item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.edit_task -> {
                startActivity(Intent(view.context, AddEditToDoActivity::class.java)
                    .putExtra(Constants.PutExTask, model))
                true
            }

            //TODO: confirmation dialog
            R.id.delete_task -> {
                db.collection(Constants.TasksCollection).document(model.taskID!!).delete()
                true
            }

            R.id.progress_task -> {
                db.collection(Constants.TasksCollection).document(model.taskID!!)
                    .update(Constants.inProgressField, !model.inProgress)
                true
            }

            else -> {
                false
            }
        }

    }

    private fun setupWeeklyTodayTasks(){
        val currentDate = Calendar.getInstance(Locale.getDefault())
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.dueTypeField, 2).whereArrayContains(Constants.daysField, currentDate.get(Calendar.DAY_OF_WEEK)-1)
            .whereEqualTo(Constants.finishedField, 0)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        adapter = TodayWeeklyTasksAdapter(options, binding.todNoResults)
        binding.todList.adapter = adapter
        binding.todList.layoutManager = WrappedLinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL)
    }
    
    private fun setupFolders(){
        val query = db.collection(Constants.CategoriesCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .orderBy(Constants.lastEditedField, Query.Direction.DESCENDING)
        query.addSnapshotListener{ value, error ->
            if (error == null && value != null && !value.isEmpty) {
                val recentFolder: CategoryModel = value.documents[0].toObject()!!
                binding.recentFolTitle.text = recentFolder.title

                binding.recentFolder.setOnClickListener{
                    startActivity(Intent(context, FolderTasksActivity::class.java)
                        .putExtra(Constants.PutExFolder, recentFolder).putExtra(Constants.PutExUser, userModel))
                }

                binding.recentFolder.setOnLongClickListener{
                    showFolderPopUpMenu(recentFolder, it, FirebaseFirestore.getInstance())
                    return@setOnLongClickListener true
                }
            }
        }

        binding.defaultFolder.setOnClickListener {
            startActivity(Intent(context, FolderTasksActivity::class.java).putExtra(Constants.PutExUser, userModel))
        }
    }

    private fun showFolderPopUpMenu(model: CategoryModel, view: View, db: FirebaseFirestore) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.folder_popup_menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener handleFolderPopUpMenuClick(model, it, db)
        }

        popupMenu.show()
    }

    private fun handleFolderPopUpMenuClick(model: CategoryModel, item: MenuItem, db: FirebaseFirestore): Boolean {
        return when(item.itemId){
            R.id.edit_folder -> {
                val dialogMaker = DialogMaker()
                dialogMaker.editFolder(childFragmentManager, binding.root, db, model)
                true
            }

            R.id.delete_folder -> {
                AlertDialog.Builder(context).setTitle(R.string.CONF_delete_folder)
                    .setMessage(R.string.CONF_delete_folder_msg)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> run{
                        GeneralUtilities().deleteFolder(model, db)
                        dialog.dismiss()
                    }}
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }.show()
                true
            }

            else -> {
                false
            }
        }

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    companion object {
        @JvmStatic
        fun newInstance(userModel: UserModel) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.PutExUser, userModel)
                }
            }
    }
}
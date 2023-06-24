package com.example.reminderapp.generalMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.adapters.TodayWeeklyTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentHomeBinding
import com.example.reminderapp.generalUtilities.WrappedLinearLayoutManager
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
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

        setUpcomingTaskData()
        setUpWeeklyTodayTasks()
        setUpRecentFolder()
    }

    private fun setUpcomingTaskData() {
        var currentDate = Date()
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.dueTypeField, 1).whereEqualTo(Constants.progressField, 0)
            .whereGreaterThan(Constants.setDateField, currentDate).orderBy(Constants.setDateField).limit(1)
        query.addSnapshotListener{ value, error ->
            if (error == null && value != null && !value.isEmpty) {
                val upcomingtask: TaskModel = value.documents[0].toObject()!!
                binding.upTaskTitle.text = upcomingtask.title

                val localTime = upcomingtask.setDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                binding.upTaskTime.text = localTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

                binding.upTaskPrio.visibility = VISIBLE
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

                currentDate = Date()
            }else{
                binding.upTaskTitle.text = getString(R.string.no_upc_task)
                binding.upTaskTime.text = ""
                binding.upTaskPrio.visibility = GONE
                binding.separator.visibility = GONE
                binding.upcDone.visibility = GONE
                currentDate = Date()
            }
        }
    }

    private fun setUpWeeklyTodayTasks(){
        val currentDate = Calendar.getInstance(Locale.getDefault())
        val query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.dueTypeField, 2).whereArrayContains(Constants.daysField, currentDate.get(Calendar.DAY_OF_WEEK)-1)
            .whereEqualTo(Constants.progressField, 0)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        adapter = TodayWeeklyTasksAdapter(options, binding.todNoResults)
        binding.todList.adapter = adapter
        binding.todList.layoutManager = WrappedLinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL)

    }
    
    private fun setUpRecentFolder(){
        val query = db.collection(Constants.CategoriesCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .orderBy(Constants.lastEditedField, Query.Direction.DESCENDING)
        query.addSnapshotListener{ value, error ->
            if (error == null && value != null && !value.isEmpty) {
                val recentFolder: CategoryModel = value.documents[0].toObject()!!
                binding.recentFolTitle.text = recentFolder.title
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
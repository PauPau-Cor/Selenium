package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reminderapp.adapters.SimpleTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentAllToDoBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.WrappedLinearLayoutManager
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AllToDoFragment : Fragment() {

    private lateinit var binding : FragmentAllToDoBinding
    private lateinit var userModel : UserModel
    private var priorityValue : Int = 1
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: SimpleTasksAdapter

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
        binding = FragmentAllToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.PriorityGroup.selectButton(binding.middlePriorityBtn.id)

        setUpAddTaskMenu()
        setUpRV()
    }

    private fun setUpRV() {
        val query: Query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereNotEqualTo(Constants.progressField, 2).orderBy(Constants.progressField, Query.Direction.DESCENDING)
            .orderBy(Constants.categoryIDField).orderBy(Constants.priorityField, Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        adapter = SimpleTasksAdapter(options)
        binding.TasksRV.adapter = adapter
        binding.TasksRV.layoutManager = WrappedLinearLayoutManager(requireContext())
    }

    private fun setUpAddTaskMenu() {
        val dialogMaker = DialogMaker()

        binding.PriorityGroup.setOnSelectListener {
            when(it){
                binding.lowPriorityBtn -> priorityValue = 0
                binding.middlePriorityBtn -> priorityValue = 1
                binding.highPriorityBtn -> priorityValue = 2
            }
        }

        binding.AddTaskMore.setOnClickListener{
            dialogMaker.advancedTask(requireContext(), this, userModel, binding.AddTaskET.editText!!.text.toString(), "", priorityValue)
        }

        binding.AddTaskBT.setOnClickListener{
            createNewTask()
        }
    }

    private fun createNewTask() {
        val title : String = binding.AddTaskET.editText?.text.toString()
        val newTask = TaskModel(userID = userModel.userID!!, title = title, priority = priorityValue)
        db.collection(Constants.TasksCollection).add(newTask)
        binding.AddTaskET.editText!!.setText("")
        binding.PriorityGroup.selectButtonWithAnimation(binding.middlePriorityBtn.id)
        priorityValue = 1
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
            AllToDoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.PutExUser, userModel)
                }
            }
    }
}
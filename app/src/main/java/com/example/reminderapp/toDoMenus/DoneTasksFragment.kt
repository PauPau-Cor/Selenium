package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.adapters.DoneTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentDoneTasksBinding
import com.example.reminderapp.generalUtilities.WrappedLinearLayoutManager
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.models.UserModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DoneTasksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentDoneTasksBinding
    private lateinit var userModel : UserModel
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DoneTasksAdapter

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
        binding = FragmentDoneTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRV()
    }
    private fun setUpRV() {
        val query: Query = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.progressField, 2).orderBy(Constants.dateFinishedField, Query.Direction.DESCENDING)
            .orderBy(Constants.categoryIDField).orderBy(Constants.priorityField, Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
        adapter = DoneTasksAdapter(options)
        binding.TasksRV.adapter = adapter
        binding.TasksRV.layoutManager = WrappedLinearLayoutManager(requireContext())
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
        fun newInstance(userModel:  UserModel) =
            DoneTasksFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.PutExUser, userModel)
                }
            }
    }
}
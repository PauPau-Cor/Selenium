package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reminderapp.adapters.DoneTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentDoneTasksBinding
import com.example.reminderapp.generalUtilities.DateQuery
import com.example.reminderapp.generalUtilities.DialogMaker
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
    private lateinit var baseQuery: Query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            userModel = it.getParcelable(Constants.PutExUser)!!
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).setDuration(650)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).setDuration(650)
        db = FirebaseFirestore.getInstance()

        baseQuery = db.collection(Constants.TasksCollection).whereEqualTo(Constants.userIDField, userModel.userID)
            .whereEqualTo(Constants.finishedField, 2)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDoneTasksBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRV()
        setUpDateSearch()
        setUpEraseFiltersButton()
    }

    private fun setUpDateSearch() {
        val dialogMaker = DialogMaker()
        binding.SelectDateET.editText?.setOnClickListener { activity?.let { it1 ->
            dialogMaker.pickDate(
                binding.SelectDateET,
                it1.supportFragmentManager,
            )
        }}

        binding.SelectDateET.editText!!.addTextChangedListener(
            DateQuery(binding.SelectDateET, baseQuery) { query -> run{
                binding.removeDateFilter.visibility = VISIBLE
                val newOptions = FirestoreRecyclerOptions.Builder<TaskModel>()
                    .setQuery(query, TaskModel::class.java).setLifecycleOwner(this).build()
                adapter.updateOptions(newOptions)
            }}
        )
    }

    private fun setUpRV() {
        val options = FirestoreRecyclerOptions.Builder<TaskModel>()
            .setQuery(baseQuery.orderBy(Constants.dateFinishedField, Query.Direction.DESCENDING), TaskModel::class.java)
            .setLifecycleOwner(this).build()
        adapter = DoneTasksAdapter(options)
        binding.TasksRV.adapter = adapter
        binding.TasksRV.layoutManager = WrappedLinearLayoutManager(requireContext())
    }

    private fun setUpEraseFiltersButton() {
        binding.removeDateFilter.setOnClickListener {
            val newOptions = FirestoreRecyclerOptions.Builder<TaskModel>()
                .setQuery(baseQuery.orderBy(Constants.dateFinishedField, Query.Direction.DESCENDING), TaskModel::class.java)
                .setLifecycleOwner(this).build()
            adapter.updateOptions(newOptions)
            binding.removeDateFilter.visibility = GONE
            binding.SelectDateET.editText!!.setText("")
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
        fun newInstance(userModel:  UserModel) =
            DoneTasksFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.PutExUser, userModel)
                }
            }
    }
}
package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.R
import com.example.reminderapp.adapters.SimpleTasksAdapter
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentAllToDoBinding
import com.example.reminderapp.databinding.FragmentDoneTasksBinding
import com.example.reminderapp.models.UserModel
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.firestore.FirebaseFirestore

class DoneTasksFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding : FragmentDoneTasksBinding
    private lateinit var userModel : UserModel
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
        binding = FragmentDoneTasksBinding.inflate(inflater, container, false)
        return binding.root
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
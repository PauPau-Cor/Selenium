package com.example.reminderapp.generalMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentHomeBinding
import com.example.reminderapp.models.UserModel
import com.google.android.material.transition.MaterialSharedAxis

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userModel : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            @Suppress("DEPRECATION")
            userModel = it.getParcelable(Constants.PutExUser)!!
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward = */ true).setDuration(650)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward = */ false).setDuration(650)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("increible")
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
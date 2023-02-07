package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.databinding.FragmentAllToDoBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.models.UserModel
import com.google.android.material.transition.MaterialSharedAxis

class AllToDoFragment : Fragment() {

    private lateinit var binding : FragmentAllToDoBinding
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
        binding = FragmentAllToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.PriorityGroup.selectButton(binding.middlePriorityBtn.id)

        val dialogMaker = DialogMaker()

        var priorityValue : Int = 1

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
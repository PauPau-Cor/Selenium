package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.databinding.FragmentAddEditTaskRepeatingFieldsBinding
import com.example.reminderapp.generalUtilities.DialogMaker

private const val ARG_TIME = "TIME"

class AddEditTaskRepeatingFields : Fragment() {
    private var time: String? = null
    lateinit var binding: FragmentAddEditTaskRepeatingFieldsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            time = it.getString(ARG_TIME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditTaskRepeatingFieldsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialogMaker = DialogMaker()
        binding.TaskTime.editText?.setText(time)

        binding.TaskTime.editText?.setOnClickListener {
            dialogMaker.pickTime(
                binding.TaskTime,
                requireContext()
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(time: String,) =
            AddEditTaskRepeatingFields().apply {
                arguments = Bundle().apply {
                    putString(ARG_TIME, time)
                }
            }
    }
}
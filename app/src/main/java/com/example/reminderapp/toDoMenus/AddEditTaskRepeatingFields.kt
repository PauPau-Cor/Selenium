package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.databinding.FragmentAddEditTaskRepeatingFieldsBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.GeneralUtilities

private const val ARG_HOUR = "HOUR"
private const val ARG_MINUTE = "MINUTE"

class AddEditTaskRepeatingFields : Fragment() {
    private var hour: Int = 0
    private var minute: Int = 0
    lateinit var binding: FragmentAddEditTaskRepeatingFieldsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            hour = it.getInt(ARG_HOUR)
            minute = it.getInt(ARG_MINUTE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditTaskRepeatingFieldsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialogMaker = DialogMaker()
        binding.TaskTime.editText?.setText(GeneralUtilities().round5Minutes(hour, minute))

        binding.TaskTime.editText?.setOnClickListener {
            dialogMaker.pickTime(
                binding.TaskTime,
                requireContext()
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(hour: Int, minute: Int) =
            AddEditTaskRepeatingFields().apply {
                arguments = Bundle().apply {
                    putInt(ARG_HOUR, hour)
                    putInt(ARG_MINUTE, minute)
                }
            }
    }
}
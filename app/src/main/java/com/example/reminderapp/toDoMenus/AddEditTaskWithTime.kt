package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.databinding.FragmentAddEditTaskWithTimeBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.GeneralUtilities

private const val ARG_DATE = "DATE"
private const val ARG_HOUR = "HOUR"
private const val ARG_MINUTE = "MINUTE"

class AddEditTaskWithTime : Fragment() {
    private var date: String? = null
    private var hour: Int = 0
    private var minute: Int = 0
    lateinit var binding : FragmentAddEditTaskWithTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE)
            hour = it.getInt(ARG_HOUR)
            minute = it.getInt(ARG_MINUTE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditTaskWithTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dialogMaker = DialogMaker()
        binding.TaskDate.editText?.setText(date)
        binding.TaskTime.editText?.setText(GeneralUtilities().round5Minutes(hour, minute))

        binding.TaskDate.editText?.setOnClickListener { activity?.let { it1 ->
                dialogMaker.pickDate(
                    binding.TaskDate,
                    it1.supportFragmentManager,
                    requireContext()
                )
            }
        }

        binding.TaskTime.editText?.setOnClickListener {
            dialogMaker.pickTime(
                binding.TaskTime,
                requireContext()
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(date: String, hour: Int, minute: Int) =
            AddEditTaskWithTime().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                    putInt(ARG_HOUR, hour)
                    putInt(ARG_MINUTE, minute)
                }
            }
    }
}
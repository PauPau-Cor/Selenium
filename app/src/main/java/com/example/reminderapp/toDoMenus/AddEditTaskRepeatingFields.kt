package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reminderapp.R
import com.example.reminderapp.databinding.FragmentAddEditTaskRepeatingFieldsBinding
import com.example.reminderapp.generalUtilities.DialogMaker

private const val ARG_TIME = "TIME"
private const val ARG_DAYS = "DAYS"

class AddEditTaskRepeatingFields : Fragment() {
    private var time: String? = null
    private var days: ArrayList<Int>? = null
    lateinit var binding: FragmentAddEditTaskRepeatingFieldsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            time = it.getString(ARG_TIME)
            days = it.getIntegerArrayList(ARG_DAYS)
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

        days!!.forEach {
            when(it){
                0 -> {
                    binding.WeekGroup.selectButton(R.id.sundayBT)
                }
                1 -> {
                    binding.WeekGroup.selectButton(R.id.mondayBT)
                }
                2 -> {
                    binding.WeekGroup.selectButton(R.id.tuesdayBT)
                }
                3 -> {
                    binding.WeekGroup.selectButton(R.id.wednesdayBT)
                }
                4 -> {
                    binding.WeekGroup.selectButton(R.id.thursdayBT)
                }
                5 -> {
                    binding.WeekGroup.selectButton(R.id.fridayBT)
                }
                6 -> {
                    binding.WeekGroup.selectButton(R.id.saturdayBT)
                }
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
        fun newInstance(time: String, days: ArrayList<Int>) =
            AddEditTaskRepeatingFields().apply {
                arguments = Bundle().apply {
                    putString(ARG_TIME, time)
                    putIntegerArrayList(ARG_DAYS, days)
                }
            }
    }
}
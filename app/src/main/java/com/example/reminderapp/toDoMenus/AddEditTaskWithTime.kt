package com.example.reminderapp.toDoMenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reminderapp.databinding.FragmentAddEditTaskWithTimeBinding
import com.example.reminderapp.generalUtilities.DialogMaker

private const val ARG_DATE = "DATE"
private const val ARG_TIME = "TIME"

class AddEditTaskWithTime : Fragment() {
    private var date: String? = null
    private var time: String? = null
    lateinit var dialogMaker: DialogMaker
    lateinit var binding : FragmentAddEditTaskWithTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            date = it.getString(ARG_DATE)
            time = it.getString(ARG_TIME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditTaskWithTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogMaker = DialogMaker()
        binding.TaskDate.editText?.setText(date)
        binding.TaskTime.editText?.setText(time)

        binding.TaskDate.editText?.setOnClickListener { activity?.let { it1 ->
                dialogMaker.pickDate(
                    binding.TaskDate,
                    it1.supportFragmentManager,
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
        fun newInstance(date: String, time: String,) =
            AddEditTaskWithTime().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, date)
                    putString(ARG_TIME, time)
                }
            }
    }
}
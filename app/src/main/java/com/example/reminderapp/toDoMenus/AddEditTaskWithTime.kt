package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reminderapp.databinding.FragmentAddEditTaskWithTimeBinding
import com.example.reminderapp.generalUtilities.DialogMaker
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val ARG_LOCAL_DT = "LCLDT"

class AddEditTaskWithTime : Fragment() {
    private var date: String? = null
    private var time: String? = null
    private var localDateTime : LocalDateTime? = null
    lateinit var dialogMaker: DialogMaker
    lateinit var binding : FragmentAddEditTaskWithTimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        arguments?.let {
            localDateTime = it.getSerializable(ARG_LOCAL_DT) as LocalDateTime?
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditTaskWithTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogMaker = DialogMaker(localDateTime!!)

        date = localDateTime?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        time = localDateTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
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
        fun newInstance(localDateTime: LocalDateTime,) =
            AddEditTaskWithTime().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_LOCAL_DT, localDateTime)
                }
            }
    }
}
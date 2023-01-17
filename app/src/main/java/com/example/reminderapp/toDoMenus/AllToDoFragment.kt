package com.example.reminderapp.toDoMenus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reminderapp.R
import com.example.reminderapp.databinding.FragmentAllToDoBinding


class AllToDoFragment : Fragment() {
    private lateinit var binding : FragmentAllToDoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAllToDoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.PriorityGroup.setOnSelectListener {
            Toast.makeText(context, it.text, Toast.LENGTH_SHORT).show()
        }
    }
}
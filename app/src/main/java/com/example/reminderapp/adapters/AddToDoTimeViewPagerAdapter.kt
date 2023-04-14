package com.example.reminderapp.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reminderapp.generalMenus.EmptyFragmentView
import com.example.reminderapp.toDoMenus.AddEditTaskRepeatingFields
import com.example.reminderapp.toDoMenus.AddEditTaskWithTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddToDoTimeViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val current = LocalDateTime.now()

    val taskWithTime = AddEditTaskWithTime.newInstance(
        current.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        current.hour,
        current.minute,
        )
    val taskRepeating = AddEditTaskRepeatingFields.newInstance(
        current.hour,
        current.minute,
        )

    private val fragmentList: ArrayList<Fragment> =
        arrayListOf(EmptyFragmentView(),
            taskWithTime,
            taskRepeating,
            )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}
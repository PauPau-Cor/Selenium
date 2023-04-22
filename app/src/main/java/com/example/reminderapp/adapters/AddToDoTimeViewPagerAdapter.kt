package com.example.reminderapp.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reminderapp.generalMenus.EmptyFragmentView
import com.example.reminderapp.toDoMenus.AddEditTaskRepeatingFields
import com.example.reminderapp.toDoMenus.AddEditTaskWithTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class AddToDoTimeViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val current = LocalDateTime.now()
    private val roundedTime = current.plusMinutes((if (current.minute % 5 < 3) -(current.minute % 5) else 5 - (current.minute % 5)).toLong())

    val taskWithTime = AddEditTaskWithTime.newInstance(
        current.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
        roundedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
    val taskRepeating = AddEditTaskRepeatingFields.newInstance(
        roundedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
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

    fun getSetDate(): LocalDateTime {
        return taskWithTime.dialogMaker.date
    }

}
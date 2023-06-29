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


class AddToDoTimeViewPagerAdapter(activity: FragmentActivity, date: LocalDateTime, days: ArrayList<Int>)
    : FragmentStateAdapter(activity) {

    private val roundedTime = date.plusMinutes((if (date.minute % 5 < 3) -(date.minute % 5) else 5 - (date.minute % 5)).toLong())
        .withSecond(0).withNano(0)

    val taskWithTime = AddEditTaskWithTime.newInstance(
        roundedTime
        )
    val taskRepeating = AddEditTaskRepeatingFields.newInstance(
        roundedTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
        days
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
package com.example.reminderapp.adapters


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.reminderapp.generalMenus.EmptyFragmentView
import com.example.reminderapp.toDoMenus.AddEditTaskRepeatingFields
import com.example.reminderapp.toDoMenus.AddEditTaskWithTime


class AddToDoTimeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentList: ArrayList<Fragment> =
        arrayListOf(EmptyFragmentView(),
            AddEditTaskWithTime.newInstance("aaa", "aaa"),
            AddEditTaskRepeatingFields.newInstance("waos", "waos"))

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}
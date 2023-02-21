package com.example.reminderapp.generalUtilities

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class WrappedLinearLayoutManager(context: Context?, orientation: Int = RecyclerView.VERTICAL, reverseLayout: Boolean = false) :
    LinearLayoutManager(context, orientation, reverseLayout) {
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}
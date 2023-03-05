package com.example.reminderapp.generalUtilities

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrappedGridLayoutManager(context:Context, spanCount: Int,
                               orientation: Int = RecyclerView.VERTICAL
                               , reverseLayout: Boolean = false):
    GridLayoutManager(context, spanCount, orientation,reverseLayout) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }

}
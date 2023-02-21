package com.example.reminderapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.models.TaskModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SimpleTasksAdapter(options : FirestoreRecyclerOptions<TaskModel>) : FirestoreRecyclerAdapter<TaskModel, TasksViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int, model: TaskModel) {
        holder.TaskTitle.text = model.title
        holder.TaskPriority.text = model.priority.toString()
    }
}

class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val TaskTitle : TextView = itemView.findViewById(android.R.id.text1)
    val TaskPriority : TextView = itemView.findViewById(android.R.id.text2)
}
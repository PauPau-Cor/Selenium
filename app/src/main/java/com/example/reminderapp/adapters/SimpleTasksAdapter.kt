package com.example.reminderapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.TaskModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class SimpleTasksAdapter(options : FirestoreRecyclerOptions<TaskModel>) : FirestoreRecyclerAdapter<TaskModel, TasksViewHolder>(options) {

    private lateinit var db: FirebaseFirestore

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_simple_task, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int, model: TaskModel) {
        holder.taskTitle.text = model.title

        if(model.categoryID.isBlank()){
            holder.taskCategory.setText(R.string.no_folder)
        }else{
            holder.taskCategory.text = model.categoryID
            //TODO("Look up different categories")
        }

        when(model.priority){
            0 ->{
                holder.taskPriority.setImageResource(R.drawable.ic_prio_low)
            }
            1 ->{
                holder.taskPriority.setImageResource(R.drawable.ic_prio_mid)
            }
            2 ->{
                holder.taskPriority.setImageResource(R.drawable.ic_prio_high)
            }
        }

        if(model.progress == 1){
            holder.taskProgressIndicator.visibility = View.VISIBLE
        }

        holder.taskDone.setOnClickListener{
            db = FirebaseFirestore.getInstance()
            db.collection(Constants.TasksCollection).document(model.taskID!!).update(Constants.progressField, 2)
        }
    }
}

class TasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val taskTitle : TextView = itemView.findViewById(R.id.TaskTitle)
    val taskCategory : TextView = itemView.findViewById(R.id.TaskCategory)
    val taskPriority : ImageView = itemView.findViewById(R.id.PriorityIndicator)
    val taskDone : ImageView= itemView.findViewById(R.id.doneBtn)
    val taskProgressIndicator : ImageView = itemView.findViewById(R.id.ProgressIndicator)
}
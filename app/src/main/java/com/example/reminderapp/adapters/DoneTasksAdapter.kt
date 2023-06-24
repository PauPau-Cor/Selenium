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
import java.text.DateFormat

class DoneTasksAdapter(options : FirestoreRecyclerOptions<TaskModel>) : FirestoreRecyclerAdapter<TaskModel, DoneTasksViewHolder>(options) {

    private lateinit var db: FirebaseFirestore
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoneTasksViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_done_task, parent, false)
        return DoneTasksViewHolder(view)
    }
    override fun onBindViewHolder(holder: DoneTasksViewHolder, position: Int, model: TaskModel) {
        holder.taskTitle.text = model.title
        holder.taskCategory.text = model.categoryName
        holder.taskDateDone.text = DateFormat.getInstance().format(model.dateFinished)

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

        holder.taskDelete.setOnClickListener{
            if(itemCount != 0){
                db = FirebaseFirestore.getInstance()
                db.collection(Constants.TasksCollection).document(model.taskID!!).delete()
            }
        }
    }
}

class DoneTasksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val taskTitle : TextView = itemView.findViewById(R.id.TaskTitle)
    val taskCategory : TextView = itemView.findViewById(R.id.TaskCategory)
    val taskPriority : ImageView = itemView.findViewById(R.id.PriorityIndicator)
    val taskDelete : ImageView = itemView.findViewById(R.id.deleteBtn)
    val taskDateDone : TextView = itemView.findViewById(R.id.TaskDateDone)
}
package com.example.reminderapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.example.reminderapp.R
import com.example.reminderapp.models.TaskModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class FolderTasksAdapter(options: FirestoreRecyclerOptions<TaskModel>, private val context: Context, private val group: Group) : FirestoreRecyclerAdapter<TaskModel, SimpleTasksViewHolder>(options) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val viewTypeSimpleTask = 0
    private val viewTypeSetDateTask = 1
    private val viewTypeRepeatingTask = 2
    private val viewTypeMissingSetDateTask = 3

    override fun onDataChanged() {
        super.onDataChanged()
        group.visibility = if(itemCount == 0){
            VISIBLE
        }else{
            INVISIBLE
        }
    }


    override fun getItemViewType(position: Int): Int {
        val model = getItem(position)
        return if(model.dueType == viewTypeSetDateTask && Date().after(model.setDate)){
            viewTypeMissingSetDateTask
        }else{
            model.dueType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleTasksViewHolder {
        return when(viewType){
            viewTypeSimpleTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_simple_task, parent, false)
                FolderTasksViewHolder(view)
            }

            viewTypeSetDateTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_set_date_task, parent, false)
                SetDateFolderTasksViewHolder(view, false)
            }

            viewTypeRepeatingTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_weekly_today_task, parent, false)
                RepeatingFolderTasksViewHolder(view, context)
            }

            viewTypeMissingSetDateTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_missed_set_date_task, parent, false)
                SetDateFolderTasksViewHolder(view, true)
            }

            else -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_simple_task, parent, false)
                FolderTasksViewHolder(view)
            }

        }
    }

    override fun onBindViewHolder(holder: SimpleTasksViewHolder, position: Int, model: TaskModel) {
        holder.setData(model, db)
    }
}

open class FolderTasksViewHolder(itemView: View) : SimpleTasksViewHolder(itemView){
    private val taskFolder: TextView = itemView.findViewById(R.id.TaskCategory)

    @SuppressLint("SetTextI18n")
    override fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean){
        super.setData(model, db, false)
        taskFolder.text = ""
    }
}

class RepeatingFolderTasksViewHolder(itemView: View, context: Context) : RepeatingTasksViewHolder(itemView, context){
    private val taskFolder: TextView = itemView.findViewById(R.id.TaskCategory)

    @SuppressLint("SetTextI18n")
    override fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean){
        super.setData(model, db, false)
        taskFolder.text = ""
    }
}

class SetDateFolderTasksViewHolder(itemView: View, private val pastDue: Boolean) : SetDateTasksViewHolder(itemView, pastDue){
    private val taskFolder: TextView = itemView.findViewById(R.id.TaskCategory)

    @SuppressLint("SetTextI18n")
    override fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean){
        super.setData(model, db, pastDue)
        taskFolder.text = ""
    }
}
package com.example.reminderapp.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView.INVISIBLE
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.TaskModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

class TodayWeeklyTasksAdapter(options: FirestoreRecyclerOptions<TaskModel>, private val group: Group) : FirestoreRecyclerAdapter<TaskModel, WeeklyTodayTaskHolder>(options) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onDataChanged() {
        super.onDataChanged()
        group.visibility = if(itemCount == 0){
            VISIBLE
        }else{
            INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyTodayTaskHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_weekly_today_task, parent, false)
        return WeeklyTodayTaskHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyTodayTaskHolder, position: Int, model: TaskModel) {
        holder.setData(model, db)
    }
}

open class WeeklyTodayTaskHolder(itemView: View) : ViewHolder(itemView){
    private val taskTitle : TextView = itemView.findViewById(R.id.TaskTitle)
    private val taskCategory : TextView = itemView.findViewById(R.id.TaskCategory)
    private val taskPriority : ImageView = itemView.findViewById(R.id.PriorityIndicator)
    private val taskTime: TextView = itemView.findViewById(R.id.TaskTime)
    private val taskDone : ImageView= itemView.findViewById(R.id.doneBtn)
    private val taskProgressIndicator : ImageView = itemView.findViewById(R.id.ProgressIndicator)

    open fun setData(model: TaskModel, db: FirebaseFirestore){
        taskTitle.text = model.title
        taskCategory.text = model.categoryName

        val localTime = model.repeatDates?.get(0)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        taskTime.text =  localTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))

        when(model.priority){
            0 ->{
                taskPriority.setImageResource(R.drawable.ic_prio_low)
            }
            1 ->{
                taskPriority.setImageResource(R.drawable.ic_prio_mid)
            }
            2 ->{
                taskPriority.setImageResource(R.drawable.ic_prio_high)
            }
        }

        if(model.inProgress){
            taskProgressIndicator.visibility = View.VISIBLE
        }else{
            taskProgressIndicator.visibility = View.GONE
        }

        taskDone.setOnClickListener{
            db.collection(Constants.TasksCollection).document(model.taskID!!).update(Constants.finishedField, 2, Constants.dateFinishedField, Date())
        }
    }
}


package com.example.reminderapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.TaskModel
import com.example.reminderapp.toDoMenus.AddEditToDoActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

class SimpleTasksAdapter(options: FirestoreRecyclerOptions<TaskModel>, private val context: Context)
    : FirestoreRecyclerAdapter<TaskModel, SimpleTasksViewHolder>(options) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val viewTypeSimpleTask = 0
    private val viewTypeSetDateTask = 1
    private val viewTypeRepeatingTask = 2
    private val viewTypeMissingSetDateTask = 3

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
                SimpleTasksViewHolder(view)
            }

            viewTypeSetDateTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_set_date_task, parent, false)
                SetDateTasksViewHolder(view, false)
            }

            viewTypeRepeatingTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_repeating_task, parent, false)
                RepeatingTasksViewHolder(view, context)
            }

            viewTypeMissingSetDateTask -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_missed_set_date_task, parent, false)
                SetDateTasksViewHolder(view, true)
            }

            else -> {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.holder_simple_task, parent, false)
                SimpleTasksViewHolder(view)
            }

        }
    }

    override fun onBindViewHolder(holder: SimpleTasksViewHolder, position: Int, model: TaskModel) {
        holder.setData(model, db, false)
    }
}

open class SimpleTasksViewHolder(itemView: View) : ViewHolder(itemView){
    private val taskTitle : TextView = itemView.findViewById(R.id.TaskTitle)
    private val taskCategory : TextView = itemView.findViewById(R.id.TaskCategory)
    private val taskPriority : ImageView = itemView.findViewById(R.id.PriorityIndicator)
    private val taskDone : ImageView= itemView.findViewById(R.id.doneBtn)
    private val taskProgressIndicator : ImageView = itemView.findViewById(R.id.ProgressIndicator)

    open fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean = false){
        taskTitle.text = model.title
        taskCategory.text = model.categoryName

        if(!missing){
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
        }

        if(model.inProgress){
            taskProgressIndicator.visibility = View.VISIBLE
        }else{
            taskProgressIndicator.visibility = View.GONE
        }

        taskDone.setOnClickListener{
            db.collection(Constants.TasksCollection).document(model.taskID!!).update(Constants.finishedField, 2, Constants.dateFinishedField, Date())
        }

        itemView.setOnLongClickListener {
            showPopUpMenu(model, it, db)
            return@setOnLongClickListener true
        }
    }

    private fun showPopUpMenu(model: TaskModel, view: View, db: FirebaseFirestore) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.task_popup_menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener handlePopUpMenuClick(model, view, it, db)
        }

        popupMenu.show()
    }

    private fun handlePopUpMenuClick(model: TaskModel, view: View, item: MenuItem, db: FirebaseFirestore): Boolean {
        return when(item.itemId){
            R.id.edit_task -> {
                view.context.startActivity(Intent(view.context, AddEditToDoActivity::class.java)
                    .putExtra(Constants.PutExTask, model))
                true
            }

            //TODO: confirmation dialog
            R.id.delete_task -> {
                db.collection(Constants.TasksCollection).document(model.taskID!!).delete()
                true
            }

            R.id.progress_task -> {
                db.collection(Constants.TasksCollection).document(model.taskID!!)
                    .update(Constants.inProgressField, !model.inProgress)
                true
            }

            else -> {
                false
            }
        }

    }
}

open class RepeatingTasksViewHolder(itemView: View, private val context: Context) : SimpleTasksViewHolder(itemView){
    private val taskTime: TextView = itemView.findViewById(R.id.TaskTime)

    override fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean) {
        super.setData(model, db, false)
        val localTime = model.repeatDates?.get(0)?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
        var repeatsConcat = localTime?.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)) + " "
        if(model.days.size == 7){
            repeatsConcat += context.getString(R.string.daily)
        }else {
            model.days.forEachIndexed { i, it ->
                repeatsConcat += when (it) {

                    0 -> {
                        context.getString(R.string.abr_sunday)
                    }

                    1 -> {
                        context.getString(R.string.abr_monday)
                    }

                    2 -> {
                        context.getString(R.string.abr_tuesday)
                    }

                    3 -> {
                        context.getString(R.string.abr_wednesday)
                    }

                    4 -> {
                        context.getString(R.string.abr_thursday)
                    }

                    5 -> {
                        context.getString(R.string.abr_friday)
                    }

                    6 -> {
                        context.getString(R.string.abr_saturday)
                    }

                    else -> {
                        ""
                    }
                }
                if (i + 1 != model.days.size) {
                    repeatsConcat += ", "
                }
            }
        }
        taskTime.text = repeatsConcat

    }
}

open class SetDateTasksViewHolder(itemView: View, private val pastDue: Boolean) : SimpleTasksViewHolder(itemView){
    private val taskDate: TextView = itemView.findViewById(R.id.TaskDate)

    @SuppressLint("SetTextI18n")
    override fun setData(model: TaskModel, db: FirebaseFirestore, missing: Boolean) {
        super.setData(model, db, pastDue)
        val localTime = model.setDate!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        taskDate.text = localTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))

        if(pastDue){
            val taskIgnore: ImageView = itemView.findViewById(R.id.ignoreBtn)
            taskIgnore.setOnClickListener{
                db.collection(Constants.TasksCollection).document(model.taskID!!).update(Constants.finishedField, 1, Constants.dateFinishedField, Date())
            }
        }
    }
}
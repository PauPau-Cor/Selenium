package com.example.reminderapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.UserModel
import com.example.reminderapp.toDoMenus.FolderTasksActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FoldersAdapter(options : FirestoreRecyclerOptions<CategoryModel>, private val userModel: UserModel, private val context: Context)
    : FirestoreRecyclerAdapter<CategoryModel, FolderViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int, model: CategoryModel) {
        holder.folderName.text = model.title

        holder.itemView.setOnClickListener{
            context.startActivity(Intent(context, FolderTasksActivity::class.java)
                .putExtra(Constants.PutExFolder, model).putExtra(Constants.PutExUser, userModel))
        }
    }
}

class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val folderName : TextView = itemView.findViewById(R.id.folderName)
}
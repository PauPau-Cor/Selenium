package com.example.reminderapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.models.CategoryModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FoldersAdapter(options : FirestoreRecyclerOptions<CategoryModel>) : FirestoreRecyclerAdapter<CategoryModel, FolderViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int, model: CategoryModel) {
        holder.folderName.text = model.title
    }
}

class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val folderName : TextView = itemView.findViewById(R.id.folderName)
}
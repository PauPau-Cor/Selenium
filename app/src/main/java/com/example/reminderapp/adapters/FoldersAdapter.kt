package com.example.reminderapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.generalUtilities.DialogMaker
import com.example.reminderapp.generalUtilities.GeneralUtilities
import com.example.reminderapp.models.CategoryModel
import com.example.reminderapp.models.UserModel
import com.example.reminderapp.toDoMenus.FolderTasksActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore


class FoldersAdapter(
    options: FirestoreRecyclerOptions<CategoryModel>,
    private val userModel: UserModel,
    private val context: Context,
    private val fragmentManager: FragmentManager,
    private val rootView: View,
) : FirestoreRecyclerAdapter<CategoryModel, FolderViewHolder>(options) {

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

        holder.itemView.setOnLongClickListener {
            showPopUpMenu(model, it, FirebaseFirestore.getInstance())
            return@setOnLongClickListener true
        }
    }

    private fun showPopUpMenu(model: CategoryModel, view: View, db: FirebaseFirestore) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.folder_popup_menu)

        popupMenu.setOnMenuItemClickListener {
            return@setOnMenuItemClickListener handlePopUpMenuClick(model, it, db)
        }

        popupMenu.show()
    }

    private fun handlePopUpMenuClick(model: CategoryModel, item: MenuItem, db: FirebaseFirestore): Boolean {
        return when(item.itemId){
            R.id.edit_folder -> {
                val dialogMaker = DialogMaker()
                dialogMaker.editFolder(fragmentManager, rootView, db, model)
                true
            }

            R.id.delete_folder -> {
                AlertDialog.Builder(context).setTitle(R.string.CONF_delete_folder)
                    .setMessage(R.string.CONF_delete_folder_msg)
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> run{
                        GeneralUtilities().deleteFolder(model, db)
                        dialog.dismiss()
                    }}
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }.show()
                true
            }

            else -> {
                false
            }
        }

    }

}

class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val folderName : TextView = itemView.findViewById(R.id.folderName)
}
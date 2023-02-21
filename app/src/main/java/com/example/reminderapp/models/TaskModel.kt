package com.example.reminderapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId

@Parcelize
data class TaskModel(
    @DocumentId val taskID: String? = null,
    val userID: String = "",
    val title : String = "",
    val categoryID : String = "",
    val priority : Int = 0,
    val dueType : Int = 0,
    val date : String = "",
    val days : List<Char> = listOf(),
    val times : List<String> = listOf()) : Parcelable

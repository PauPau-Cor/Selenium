package com.example.reminderapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId
import java.util.Date

@Parcelize
data class TaskModel(
    @DocumentId val taskID: String? = null,
    val userID: String = "",
    val title : String = "",
    val progress : Int = 0,
    val categoryID : String = "",
    val categoryName : String = "",
    val dateFinished: Date = Date(),
    val priority : Int = 0,
    val dueType : Int = 0,
    val date : String = "",
    val days : List<Char> = listOf(),
    val times : List<String> = listOf()) : Parcelable

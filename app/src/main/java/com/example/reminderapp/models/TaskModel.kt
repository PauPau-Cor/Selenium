package com.example.reminderapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId

@Parcelize
data class TaskModel(
    @DocumentId val taskID: String? = null,
    val userID: String = "",
    val tittle : String = "",
    val date : String = "") : Parcelable

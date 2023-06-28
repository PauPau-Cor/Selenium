package com.example.reminderapp.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class TaskModel(
    @DocumentId val taskID: String? = null,
    val userID: String = "",
    var token : ArrayList<String> = arrayListOf(),
    val title : String = "",
    val progress : Int = 0,
    var categoryID : String = "",
    var categoryName : String = "",
    val dateFinished: Date = Date(),
    val priority : Int = 0,
    var dueType : Int = 0,
    var days : ArrayList<Int> = arrayListOf(),
    var setDate : Date? = null,
    var repeatDates : ArrayList<Date>? = null,
    ) : Parcelable

package com.example.reminderapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId

@Parcelize
data class CategoryModel(
    @DocumentId val categoryID: String? = null,
    val userID: String = "",
    val title : String = "") : Parcelable

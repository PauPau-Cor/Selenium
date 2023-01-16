package com.example.reminderapp.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    @DocumentId val userID: String? = null,
    val name : String = "",
    val mail : String = "",
    val birthday : String = "") : Parcelable

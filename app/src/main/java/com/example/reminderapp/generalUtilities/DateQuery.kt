package com.example.reminderapp.generalUtilities

import android.text.Editable
import android.text.TextWatcher
import com.example.reminderapp.dataClasses.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date

class DateQuery(private val field: TextInputLayout, private val query: Query, val listener: (query : Query) -> Unit)
    : TextWatcher {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {
        if(s.toString() == ""){
            return
        }

        val date = LocalDate.parse(field.editText!!.text.toString(), DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
        val startOfDay = Date.from(LocalDateTime.of(date, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant())
        val endOfDay = Date.from(LocalDateTime.of(date, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())

        val newQuery = query.whereGreaterThanOrEqualTo(Constants.dateFinishedField, startOfDay)
            .whereLessThanOrEqualTo(Constants.dateFinishedField, endOfDay)
            .orderBy(Constants.dateFinishedField, Query.Direction.ASCENDING)
        listener(newQuery)
    }

}
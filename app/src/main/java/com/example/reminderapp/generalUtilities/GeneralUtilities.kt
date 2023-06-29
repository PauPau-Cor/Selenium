package com.example.reminderapp.generalUtilities

import com.example.reminderapp.dataClasses.Constants
import com.example.reminderapp.models.CategoryModel
import com.google.firebase.firestore.FirebaseFirestore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.util.Date

class GeneralUtilities {

    fun nextOrSameWithTime(dayOfWeek: DayOfWeek, time: String): Date{
        val currentLocalDateTime = LocalDateTime.of(
            LocalDate.now(),
            LocalTime.parse(time, DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
        )
        var newLocalDateTime = LocalDateTime.now().with(TemporalAdjusters.nextOrSame(dayOfWeek))
        if(newLocalDateTime.dayOfWeek.equals(currentLocalDateTime.dayOfWeek)
            && newLocalDateTime.isAfter(currentLocalDateTime)){
            newLocalDateTime = newLocalDateTime.plusWeeks(1)
        }
        newLocalDateTime = newLocalDateTime.with(currentLocalDateTime.toLocalTime())
        return Date.from(newLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }

    fun deleteFolder(model: CategoryModel, db: FirebaseFirestore) {
        val batch = db.batch()
        val folderRef = db.collection(Constants.CategoriesCollection).document(model.categoryID!!)
        batch.delete(folderRef)

        db.collection(Constants.TasksCollection).whereEqualTo(Constants.categoryIDField, model.categoryID)
            .get().addOnSuccessListener {
                it.forEach { it1 ->
                    batch.delete(it1.reference)
                }
                batch.commit()
            }
    }

}
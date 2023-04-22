package com.example.reminderapp.generalUtilities

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

}
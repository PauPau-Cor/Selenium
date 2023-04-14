package com.example.reminderapp.generalUtilities

import java.util.Calendar
import java.util.Locale

class GeneralUtilities {

    fun round5Minutes(hour: Int, minutes: Int): String{
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        val mod = minutes % 5
        calendar.add(Calendar.MINUTE, if (mod < 3) -mod else 5 - mod)
        return String.format(
            Locale.getDefault(), "%02d:%02d",
            calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE]
        )
    }

}
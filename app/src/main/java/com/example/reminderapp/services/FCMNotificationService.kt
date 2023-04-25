package com.example.reminderapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import com.example.reminderapp.R
import com.example.reminderapp.dataClasses.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//TODO: Implement onNewToken()
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FCMNotificationService : FirebaseMessagingService() {
    @SuppressLint("MissingPermission")
    override fun onMessageReceived(message: RemoteMessage) {
        getSystemService(NotificationManager::class.java).createNotificationChannelGroup(
            NotificationChannelGroup(
                Constants.notifChannelGroupTasks,
                getString(R.string.tasks))
        )
        val upTasksChannel = NotificationChannel(
            Constants.notifChannelUpTasks,
            getString(R.string.upTasks),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        upTasksChannel.group = Constants.notifChannelGroupTasks

        val dueTasksChannel = NotificationChannel(
            Constants.notifChannelDueTasks,
            getString(R.string.dueTask),
            NotificationManager.IMPORTANCE_HIGH
        )
        dueTasksChannel.group = Constants.notifChannelGroupTasks

        getSystemService(NotificationManager::class.java).createNotificationChannels(listOf(
            upTasksChannel,
            dueTasksChannel,
        ))

        val channelId : String
        val title : String
        val body : String
        when (message.data[Constants.notifDataDesc]){
            Constants.notifDescUpTaskD  -> {
                channelId = Constants.notifChannelUpTasks
                title = getString(R.string.upTask)
                body = getString(R.string.upcomingTaskDayBody, message.data[Constants.notifDataName])
            }
            Constants.notifDescUpTaskH  -> {
                channelId = Constants.notifChannelUpTasks
                title = getString(R.string.upTask)
                body = getString(R.string.upcomingTaskHourBody, message.data[Constants.notifDataName])
            }
            Constants.notifDescDueTask -> {
                channelId = Constants.notifChannelDueTasks
                title = getString(R.string.dueTask)
                body = getString(R.string.dueTaskBody, message.data[Constants.notifDataName])
            }
            else -> {
                channelId = Constants.notifChannelUpTasks
                title = getString(R.string.upTask)
                body = getString(R.string.upcomingTaskHourBody, message.data[Constants.notifDataName])
            }
        }

        val notification = Notification.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notif_logo)
            .setAutoCancel(false)



        NotificationManagerCompat.from(this).notify(message.data[Constants.notifDataID], 0, notification.build())
        super.onMessageReceived(message)
    }
}
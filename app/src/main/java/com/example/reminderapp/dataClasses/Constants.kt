package com.example.reminderapp.dataClasses

object Constants {
    const val PutExUser = "UserModel"
    const val PutExFolder = "FolderModel"
    const val PutExTask = "TaskModel"

    const val UsersCollection = "users"
    const val userIDField = "userID"
    const val tokenField = "token"

    const val CategoriesCollection = "categories"
    const val lastEditedField = "lastEdited"
    const val titleField= "title"

    const val TasksCollection = "tasks"
    const val priorityField = "priority"
    const val categoryIDField = "categoryID"
    const val categoryNameField = "categoryName"
    const val finishedField = "finished"
    const val dateFinishedField = "dateFinished"
    const val dueTypeField = "dueType"
    const val setDateField = "setDate"
    const val daysField = "days"
    const val inProgressField = "inProgress"

    const val notifDataName = "NAME"
    const val notifDataID = "ID"

    const val notifDataDesc = "DESC"

    const val notifDescUpTaskD = "UPCOMING_TASK_DAY"
    const val notifDescUpTaskH = "UPCOMING_TASK_HOUR"
    const val notifDescDueTask = "DUE_TASK"

    const val notifChannelGroupTasks = "TSKS"
    const val notifChannelUpTasks = "UPC_TSK"
    const val notifChannelDueTasks = "DUE_TSK"
}
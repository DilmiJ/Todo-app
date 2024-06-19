package com.example.daytask.core.common.util.firebase

import com.example.daytask.data.model.Message
import com.example.daytask.data.Notification
import com.example.daytask.data.NotificationKey
import com.example.daytask.data.model.SubTask
import com.example.daytask.feature.chat.ChatDestination.userId
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.daytask.data.model.Task as newTask

object FirebaseManager {
    private fun getUserRef(): DatabaseReference {
        val database = Firebase.database.reference
        val userId = Firebase.auth.uid
        return database.child("users/$userId")
    }

    fun updateUserName(name: String?): Task<Void> {
        return getUserRef().child("displayName")
            .setValue(name)
    }

    fun updateUserPhoto(photoUrl: String): Task<Void> {
        return getUserRef().child("photoUrl")
            .setValue(photoUrl)
    }

    fun uploadTask(task: newTask): Task<Void> {
        task.memberList.forEach {
            createNotification(it.userId, NotificationKey.Task, task.title)
        }
        return getUserRef().child("tasks").push()
            .setValue(task)
    }

    fun uploadSubTask(taskId: String, subTask: SubTask): Task<Void> {
        return getUserRef().child("tasks/$taskId/subTasksList").push()
            .setValue(subTask)
    }

    fun updateSubTask(taskId: String, subTaskId: String, completed: Boolean): Task<Void> {
        return getUserRef().child("tasks/$taskId/subTasksList/$subTaskId")
            .updateChildren(
                mapOf<String, Any>("completed" to completed)
            )
    }

    fun updateTask(taskId: String, task: newTask): Task<Void> {
        return getUserRef().child("tasks/$taskId")
            .updateChildren(
                mapOf<String, Any>(
                    "title" to task.title,
                    "detail" to task.detail,
                    "date" to task.date,
                    "memberList" to task.memberList,
                    "subTasksList" to task.subTasksList,
                    "taskComplete" to task.taskComplete
                )
            )
    }

    fun deleteTask(taskId: String): Task<Void> {
        return getUserRef().child("tasks")
            .updateChildren(mapOf(taskId to null))
    }

    fun updateUserStatus(isOnline: Boolean): Task<Void> {
        return getUserRef().child("isOnline")
            .setValue(isOnline)
    }

    fun uploadPrivateMessage(message: Message): Pair<Task<Void>, Task<Void>> {
        val receiverId = message.receiverId
        val task1 = Firebase.database.reference.child("users/$receiverId/message/private/$userId").push()
            .setValue(message)
        val task2 = getUserRef().child("message/private/$receiverId").push()
            .setValue(message)
        createNotification(receiverId, NotificationKey.Message)
        return Pair(task1, task2)
    }

    private fun createNotification(
        receiverId: String,
        actionKey: NotificationKey,
        destinationText: String = ""
    ) {
        val messageText: String
        val action: Pair<String, String>
        when (actionKey) {
            NotificationKey.Message -> {
                messageText = "sent you a new message"
                action = Pair("message", userId)
            }

            NotificationKey.Task -> {
                messageText = "added you to project"
                action = Pair("task", "taskId")
            }
        }

        val notification = Notification(
            senderId = userId,
            receiverId = receiverId,
            messageText = messageText,
            destinationText = destinationText,
            action = action
        )

        Firebase.database.reference.child("users/$receiverId/notification").push()
            .setValue(notification)
    }

    fun deleteNotification(id: String): Task<Void> {
        return getUserRef().child("notification")
            .updateChildren(mapOf(id to null))
    }
}
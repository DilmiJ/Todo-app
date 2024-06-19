package com.example.daytask.core.common.util.firebase

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.DataSnapshotManager.toNotificationList
import com.example.daytask.data.Notification
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class NotificationManager private constructor() {
    private val data = MutableStateFlow(NotificationManagerData())

    companion object {
        val flowComposable: StateFlow<NotificationManagerData>
            @Composable
            get() = remember { NotificationManager() }.data
        val flow: StateFlow<NotificationManagerData>
            get() = NotificationManager().data
    }

    init {
        listenNotification()
    }

    private fun listenNotification() {
        Firebase.database.reference
            .child("users/${Firebase.auth.uid}/notification")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data.update { it.copy(status = Status.Loading) }
                        val notificationList = snapshot.toNotificationList()
                        data.update {
                            it.copy(
                                notifications = notificationList,
                                status = Status.Done,
                                isNotify = notificationList.isNotEmpty()
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        data.update { state ->
                            state.copy(status = Status.Error.also {
                                it.message = error.message
                            })
                        }
                        Log.e("Firebase database", error.message)
                    }
                }
            )
    }
}

data class NotificationManagerData(
    val notifications: List<Notification> = emptyList(),
    val status: Status = Status.Loading,
    val isNotify: Boolean = false,
)
package com.example.daytask.core.common.util.firebase

import android.util.Log
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.Constants
import com.example.daytask.core.common.util.firebase.DataSnapshotManager.toTask
import com.example.daytask.data.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TaskManager private constructor(private val taskId: String) {
    private val data = MutableStateFlow(TaskManagerData())

    companion object {
        fun flow(taskId: String) = TaskManager(taskId).data
    }

    init {
        listenTask()
    }

    private fun listenTask() {
        Firebase.database.reference
            .child("users/${Firebase.auth.uid}/tasks/$taskId")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data.update { it.copy(status = Status.Loading) }
                        val task = snapshot.toTask()
                        data.update { update ->
                            if (task.id.isEmpty()) {
                                update.copy(
                                    status = Status.Error.also { it.message = Constants.NO_TASK }
                                )
                            } else {
                                update.copy(
                                    task = task,
                                    status = Status.Done
                                )
                            }
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
                })
    }
}

data class TaskManagerData(
    val task: Task = Task(),
    val status: Status = Status.Loading,
)
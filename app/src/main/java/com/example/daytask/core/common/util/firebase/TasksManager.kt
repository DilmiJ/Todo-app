package com.example.daytask.core.common.util.firebase

import android.util.Log
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.DataSnapshotManager.toTaskList
import com.example.daytask.data.model.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TasksManager private constructor() {
    private val data = MutableStateFlow(TasksManagerData())

    companion object {
        val flow: StateFlow<TasksManagerData>
            get() = TasksManager().data
    }

    init {
        listenTasks()
    }

    private fun listenTasks() {
        Firebase.database.reference
            .child("users/${Firebase.auth.uid}/tasks")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data.update { it.copy(status = Status.Loading) }
                        val taskList = snapshot.toTaskList().sortedBy { it.date }
                        data.update {
                            it.copy(
                                taskList = taskList,
                                status = Status.Done
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
                })
    }
}

data class TasksManagerData(
    val taskList: List<Task> = emptyList(),
    val status: Status = Status.Loading,
)
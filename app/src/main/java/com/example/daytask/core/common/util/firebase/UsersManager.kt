package com.example.daytask.core.common.util.firebase

import android.util.Log
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.DataSnapshotManager.toUserList
import com.example.daytask.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UsersManager private constructor() {
    private val data = MutableStateFlow(UsersManagerData())

    companion object {
        val flow: MutableStateFlow<UsersManagerData>
            get() = UsersManager().data
    }

    init {
        listenUsers()
    }

    private fun listenUsers() {
        val uid = Firebase.auth.uid
        Firebase.database.reference
            .child("users")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data.update { it.copy(status = Status.Loading) }
                        val users = snapshot.toUserList().filter {
                            it.userId != uid
                        }
                        data.update { update ->
                            update.copy(
                                users = users,
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
                }
            )
    }
}

data class UsersManagerData(
    val users: List<User> = emptyList(),
    val status: Status = Status.Loading,
)
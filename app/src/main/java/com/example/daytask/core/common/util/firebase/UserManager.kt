package com.example.daytask.core.common.util.firebase

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.DataSnapshotManager.toUser
import com.example.daytask.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class UserManager private constructor(private val userId: String) {
    private val data = MutableStateFlow(UserManagerData())

    companion object {
        fun flow(userId: String): MutableStateFlow<UserManagerData> = UserManager(userId).data

        @Composable
        fun flowComposable(userId: String): MutableStateFlow<UserManagerData> =
            remember { UserManager(userId) }.data
    }

    init {
        listenUser()
    }

    private fun listenUser() {
        Firebase.database.reference
            .child("users/$userId")
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        data.update { it.copy(status = Status.Loading) }
                        val user = snapshot.toUser()
                        data.update { update ->
                            update.copy(
                                user = user,
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

data class UserManagerData(
    val user: User = User(),
    val status: Status = Status.Loading,
)
package com.example.daytask.feature.newtask

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.R
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.Constants.timeLimitMillis
import com.example.daytask.core.common.util.NetworkManager
import com.example.daytask.core.common.util.NotifyManager
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.common.util.firebase.UsersManager
import com.example.daytask.data.model.Task
import com.example.daytask.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class NewTaskViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewTaskUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            UsersManager.flow.collectLatest { data ->
                _uiState.update {
                    it.copy(
                        users = data.users
                    )
                }
            }
        }
    }

    fun updateUiState(uiState: NewTaskUiState) = _uiState.update { uiState }

    private fun updateStatus(status: Status) = _uiState.update { it.copy(status = status) }

    fun validNewTask(): Boolean {
        val state = _uiState.value
        val currentTime = Calendar.getInstance().timeInMillis
        return state.title.isNotBlank() &&
                state.details.isNotBlank() &&
                state.date > currentTime
    }

    fun uploadNewTask(context: Context) {
        if (!NetworkManager.isNetworkAvailable(context)) {
            NotifyManager.notifyUser(context)
            return
        }
        if (!validNewTask()) {
            NotifyManager.notifyUser(context, context.getString(R.string.not_valid_task))
            return
        }
        updateStatus(Status.Loading)

        val state = _uiState.value
        val newTask = Task(
            title = state.title.trim(),
            detail = state.details.trim(),
            memberList = state.memberList,
            date = state.date,
            subTasksList = listOf(),
            taskComplete = false
        )
        FirebaseManager.uploadTask(newTask)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            updateResult = true,
                            status = Status.Done
                        )
                    }
                } else updateStatus(Status.Done)
                NotifyManager.notifyUser(task, context)
            }
    }
}

data class NewTaskUiState(
    val title: String = "",
    val details: String = "",
    val date: Long = Calendar.getInstance().timeInMillis + timeLimitMillis,
    val memberList: List<User> = listOf(),
    val status: Status = Status.Done,
    val updateResult: Boolean = false,
    val users: List<User> = emptyList()
)

package com.example.daytask.feature.edittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.common.util.firebase.TaskManager
import com.example.daytask.core.common.util.firebase.UsersManager
import com.example.daytask.data.model.SubTask
import com.example.daytask.data.model.Task
import com.example.daytask.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import com.google.android.gms.tasks.Task as GmsTask

class EditTaskViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val taskId: String = checkNotNull(savedStateHandle[EditTaskDestination.taskId])
    private val _uiState = MutableStateFlow(EditTaskUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            TaskManager.flow(taskId).collectLatest { data ->
                _uiState.update { state ->
                    when (data.status) {
                        Status.Done -> {
                            state.copy(
                                task = data.task,
                                newTitle = data.task.title,
                                newDetail = data.task.detail,
                                newDate = data.task.date,
                                newMembersList = data.task.memberList,
                                newSubTasksList = data.task.subTasksList,
                                status = data.status
                            )
                        }

                        else -> state.copy(status = data.status)
                    }
                }
            }
        }
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

    fun updateUiState(uiState: EditTaskUiState) = _uiState.update { uiState }

    private fun haveChanges(): Boolean {
        val state = _uiState.value
        val task = state.task
        return state.newTitle != task.title ||
                state.newDetail != task.detail ||
                state.newDate != task.date ||
                state.newMembersList != task.memberList ||
                state.newSubTasksList != task.subTasksList
    }

    private fun contentValidation(): Boolean {
        val state = _uiState.value
        val currentTime = Calendar.getInstance().timeInMillis
        return state.newTitle.isNotBlank() &&
                state.newDetail.isNotBlank() &&
                state.newDate > currentTime
    }

    fun validSave(): Boolean = haveChanges() && contentValidation()

    fun updateTask(): GmsTask<Void> {
        val newTask = with(_uiState.value) {
            task.copy(
                title = newTitle,
                detail = newDetail,
                date = newDate,
                memberList = newMembersList,
                subTasksList = newSubTasksList
            )
        }

        return FirebaseManager.updateTask(taskId, newTask)
    }
}

data class EditTaskUiState(
    val task: Task = Task(),
    val status: Status = Status.Loading,
    val newTitle: String = "",
    val newDetail: String = "",
    val newDate: Long = 0L,
    val newMembersList: List<User> = listOf(),
    val newSubTasksList: List<SubTask> = listOf(),
    val newSubTaskTitle: String = "",
    val users: List<User> = listOf(),
)
package com.example.daytask.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.common.util.firebase.TaskManager
import com.example.daytask.data.model.SubTask
import com.example.daytask.data.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.Task as GmsTask

class TaskDetailsViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val taskId: String = checkNotNull(savedStateHandle[TaskDetailsDestination.taskId])
    private val _uiState = MutableStateFlow(TaskDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            TaskManager.flow(taskId).collectLatest { data ->
                _uiState.update { state ->
                    when (data.status) {
                        Status.Done -> {
                            state.copy(
                                status = data.status,
                                task = data.task,
                            )
                        }

                        else -> state.copy(status = data.status)
                    }
                }
            }
        }
    }

    fun addSubTask() {
        val subTask = SubTask(
            title = _uiState.value.title.trim(),
            completed = false
        )
        FirebaseManager.uploadSubTask(taskId, subTask)
    }

    fun updateSubtask(subTaskId: String, completed: Boolean) {
        FirebaseManager.updateSubTask(taskId, subTaskId, completed)
    }

    fun validTitle(): Boolean = _uiState.value.title.isNotBlank()

    fun updateUiState(uiState: TaskDetailsUiState) = _uiState.update { uiState }

    fun finishTask(): GmsTask<Void> {
        val task = _uiState.value.task
        return FirebaseManager.updateTask(
            taskId,
            task.copy(taskComplete = !task.taskComplete)
        )
    }
}

data class TaskDetailsUiState(
    val task: Task = Task(),
    val status: Status = Status.Loading,
    val addStatus: Status = Status.Done,
    val title: String = ""
)
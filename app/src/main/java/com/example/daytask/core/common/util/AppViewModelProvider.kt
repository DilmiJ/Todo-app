package com.example.daytask.core.common.util

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.daytask.feature.chat.ChatViewModel
import com.example.daytask.feature.details.TaskDetailsViewModel
import com.example.daytask.feature.edittask.EditTaskViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskDetailsViewModel(
                this.createSavedStateHandle()
            )
        }
        initializer {
            EditTaskViewModel(
                this.createSavedStateHandle()
            )
        }
        initializer {
            ChatViewModel(
                this.createSavedStateHandle()
            )
        }
    }
}
package com.example.daytask.feature.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.core.common.util.firebase.FirebaseManager
import com.example.daytask.core.common.util.firebase.NotificationManager
import com.example.daytask.data.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        listenNotification()
    }

    private fun listenNotification() {
        viewModelScope.launch {
            NotificationManager.flow.collectLatest { data ->
                _uiState.update { it.copy(notificationList = data.notifications) }
            }
        }
    }

    fun removeNotification(id: String) {
        FirebaseManager.deleteNotification(id)
    }
}

data class NotificationUiState(
    val notificationList: List<Notification> = emptyList()
)
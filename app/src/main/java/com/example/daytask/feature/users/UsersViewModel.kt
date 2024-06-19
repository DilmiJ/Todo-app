package com.example.daytask.feature.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daytask.core.common.util.firebase.UsersManager
import com.example.daytask.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {
    private val _users = MutableStateFlow(emptyList<User>())
    val users = _users.asStateFlow()

    init {
        viewModelScope.launch {
            UsersManager.flow.collectLatest { data ->
                _users.update { data.users }
            }
        }
    }
}
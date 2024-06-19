package com.example.daytask.feature.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.daytask.core.common.ext.UiAction
import com.example.daytask.core.common.util.Constants.NAME_LENGTH
import com.example.daytask.core.common.util.Constants.PASSWORD_LENGTH
import com.example.daytask.feature.auth.ext.AuthAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private fun checkLogIn(): Boolean = checkEmail() && checkPassword()
    private fun checkSingUp(): Boolean =
        checkName() && checkLogIn() && _uiState.value.checkedPrivacy

    private fun checkEmail(): Boolean {
        val email = _uiState.value.email
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkPassword(): Boolean {
        val password = _uiState.value.password
        return password.isNotEmpty() && password.length >= PASSWORD_LENGTH
    }

    private fun checkName(): Boolean {
        val name = _uiState.value.fullName
        return name.isNotBlank() && name.length >= NAME_LENGTH
    }

    fun onAction(action: UiAction) {
        when (action) {
            is AuthAction.UpdateUiState -> _uiState.update { action.uiState }

            else -> {
                // stub
            }
        }
    }

    fun onActionBoolean(action: UiAction): Boolean {
        return when (action) {
            AuthAction.Check.EMAIL -> checkEmail()
            AuthAction.Check.PASSWORD -> checkPassword()
            AuthAction.Check.NAME -> checkName()
            AuthAction.Check.LOG_IN -> checkLogIn()
            AuthAction.Check.SIGN_UP -> checkSingUp()

            else -> false
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val checkedPrivacy: Boolean = false,
)
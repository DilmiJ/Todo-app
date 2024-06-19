package com.example.daytask.feature.auth.ext

import com.example.daytask.core.common.ext.UiAction
import com.example.daytask.feature.auth.AuthUiState

abstract class AuthAction {

    enum class OnClick : UiAction {
        GOOGLE_SIGN_IN,
    }

    enum class Check : UiAction {
        EMAIL,
        PASSWORD,
        NAME,
        LOG_IN,
        SIGN_UP,
    }

    data class SignUp(
        val email: String,
        val password: String,
        val name: String,
    ) : UiAction

    data class LogIn(
        val email: String,
        val password: String,
    ) : UiAction

    data class ForgotPassword(
        val email: String,
    ) : UiAction

    data class UpdateUiState(
        val uiState: AuthUiState
    ) : UiAction
}
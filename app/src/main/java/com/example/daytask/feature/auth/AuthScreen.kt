package com.example.daytask.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daytask.R
import com.example.daytask.core.common.ext.OnAction
import com.example.daytask.core.navigation.NavigationDestination
import com.example.daytask.core.ui.theme.DayTaskTheme
import com.example.daytask.feature.auth.presentation.AuthBody

object AuthDestination : NavigationDestination {
    override val route = "auth"
    override val titleRes = R.string.app_name_clear
}

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onAction: OnAction = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    AuthBody(
        uiState = uiState,
        onViewModelAction = viewModel::onAction,
        onViewModelActionBoolean = viewModel::onActionBoolean,
        onAction = onAction,
        modifier = modifier
    )
}

@Preview
@Composable
fun AuthScreenPreview() {
    DayTaskTheme {
        AuthScreen()
    }
}

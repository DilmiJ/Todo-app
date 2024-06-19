package com.example.daytask.feature.calendar

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daytask.R
import com.example.daytask.feature.calendar.presentation.CalendarBody
import com.example.daytask.core.navigation.NavigationDestination

object CalendarDestination : NavigationDestination {
    override val route = "calendar"
    override val titleRes = R.string.schedule
}

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel(),
    navigateToTaskDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(
        onBack = onBack,
        enabled = true
    )

    val uiState by viewModel.uiState.collectAsState()

    CalendarBody(
        uiState = uiState,
        updateDay = viewModel::updateDay,
        navigateToTaskDetail = navigateToTaskDetail,
        modifier = modifier
    )
}
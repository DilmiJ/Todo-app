package com.example.daytask.feature.newtask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.LoadingDialog
import com.example.daytask.feature.newtask.presentation.NewTaskBody
import com.example.daytask.core.navigation.NavigationDestination
import com.example.daytask.core.common.ext.Status

object NewTaskDestination : NavigationDestination {
    override val route = "new_task"
    override val titleRes = R.string.create_new_task
}

@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    viewModel: NewTaskViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.status == Status.Loading) LoadingDialog()

    LaunchedEffect(key1 = uiState.updateResult) {
        if (uiState.updateResult) navigateUp()
    }

    NewTaskBody(
        uiState = uiState,
        validCreate = viewModel.validNewTask(),
        updateUiState = viewModel::updateUiState,
        saveTask = { viewModel.uploadNewTask(context) },
        modifier = modifier
    )
}

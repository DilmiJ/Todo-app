package com.example.daytask.feature.details

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daytask.R
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.AppViewModelProvider
import com.example.daytask.core.common.util.MathManager
import com.example.daytask.core.navigation.DayTaskCenterTopAppBar
import com.example.daytask.core.navigation.NavigationDestination
import com.example.daytask.core.ui.presentation.LoadingDialog
import com.example.daytask.core.ui.theme.Secondary
import com.example.daytask.core.ui.theme.White
import com.example.daytask.feature.details.presentation.DetailAlert
import com.example.daytask.feature.details.presentation.DetailDialog
import com.example.daytask.feature.details.presentation.TaskDetailBody

object TaskDetailsDestination : NavigationDestination {
    override val route = "details"
    override val titleRes = R.string.task_details
    const val taskId = "taskId"
    val routeWithArgs = "$route/{$taskId}"
}

@Composable
fun TaskDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateUp: () -> Unit,
    navigateToEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }

    if (showDialog) {
        DetailDialog(
            onDismissRequest = {
                viewModel.updateUiState(uiState.copy(title = ""))
                showDialog = false
            },
            title = uiState.title,
            validTitle = viewModel.validTitle(),
            updateTitle = { viewModel.updateUiState(uiState.copy(title = it)) },
            buttonAction = viewModel::addSubTask
        )
    }

    if (showAlert) {
        val task = uiState.task
        DetailAlert(
            title = task.title,
            taskComplete = task.taskComplete,
            completePercentage = MathManager.countCompletePercentage(task.subTasksList),
            closeAlert = { showAlert = false },
            finishTask = {
                viewModel.finishTask()
                showAlert = false
                navigateUp()
            }
        )
    }

    Scaffold(
        topBar = {
            DayTaskCenterTopAppBar(
                currentRoute = TaskDetailsDestination.routeWithArgs,
                navigateUp = navigateUp,
                actions = {
                    IconButton(
                        onClick = { navigateToEdit(viewModel.taskId) },
                        enabled = !uiState.task.taskComplete,
                        colors = IconButtonDefaults.iconButtonColors().copy(
                            disabledContentColor = Secondary,
                            contentColor = White,
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when (uiState.status) {
            Status.Loading -> LoadingDialog()
            Status.Error -> {
                val context = LocalContext.current
                LaunchedEffect(key1 = "error") {
                    Toast.makeText(context, uiState.status.message, Toast.LENGTH_SHORT).show()
                    navigateUp()
                }
            }

            Status.Done -> {
                TaskDetailBody(
                    uiState = uiState,
                    updateSubTask = viewModel::updateSubtask,
                    finishTask = { showAlert = true },
                    showDialog = { showDialog = true },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}
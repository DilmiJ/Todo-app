package com.example.daytask.feature.details.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.CompleteCircle
import com.example.daytask.core.ui.presentation.MainButton
import com.example.daytask.core.ui.presentation.SubTaskCard
import com.example.daytask.feature.details.TaskDetailsUiState
import com.example.daytask.core.ui.theme.DetailTextColor
import com.example.daytask.core.ui.theme.ProjectDetailText
import com.example.daytask.core.ui.theme.ProjectTitleText
import com.example.daytask.core.ui.theme.TaskTitleText
import com.example.daytask.core.ui.theme.Tertiary
import com.example.daytask.core.ui.theme.White
import com.example.daytask.core.common.util.MathManager

@Composable
fun TaskDetailBody(
    modifier: Modifier = Modifier,
    uiState: TaskDetailsUiState,
    updateSubTask: (String, Boolean) -> Unit,
    finishTask: () -> Unit,
    showDialog: () -> Unit
) {
    val task = uiState.task
    var paddingBottom by remember { mutableStateOf(0.dp) }

    FinishBox(
        changedSize = { paddingBottom = it },
        titleRes = if (uiState.task.taskComplete) R.string.make_active else R.string.finish_task,
        onClick = finishTask
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24)),
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.offset_24))
    ) {
        item {
            ProjectProgressRow(
                percentage = MathManager.countCompletePercentage(task.subTasksList),
                modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_8))
            )
        }
        item {
            Text(
                text = task.title,
                style = TaskTitleText,
                color = White,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item { ProjectInfoTextColumn(detail = task.detail) }
        item {
            ProjectSquareInfo(
                openCalendar = { /*TODO: Calendar open*/ },
                openMembers = { /*TODO: Team open*/ },
                date = task.date,
                memberList = task.memberList
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8))) {
                if (!task.taskComplete) {
                    MainButton(
                        onClick = showDialog,
                        text = stringResource(R.string.add_subtask),
                        modifier = Modifier
                            .background(Tertiary)
                            .fillMaxWidth()
                    )
                }
                task.subTasksList.forEach {
                    SubTaskCard(
                        subtask = it,
                        actionSubTask = {
                            if (!task.taskComplete) {
                                updateSubTask(it.id, !it.completed)
                            }
                        }
                    )
                }
                Spacer(Modifier)
            }
        }
    }
}

@Composable
fun FinishBox(
    modifier: Modifier = Modifier,
    changedSize: (Dp) -> Unit,
    onClick: () -> Unit,
    @StringRes titleRes: Int
) {
    val density = LocalDensity.current.density
    val padding = dimensionResource(R.dimen.offset_16)
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier.fillMaxHeight()
    ) {
        MainButton(
            onClick = onClick,
            text = stringResource(titleRes),
            modifier = Modifier
                .background(Tertiary)
                .fillMaxWidth()
                .padding(padding)
                .onSizeChanged { changedSize((it.height / density + padding.value * 2).toInt().dp) }
        )
    }
}

@Composable
fun ProjectProgressRow(
    modifier: Modifier = Modifier,
    percentage: Float
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        DetailTitleTextBox(text = stringResource(R.string.task_progress))
        CompleteCircle(
            sizeRes = R.dimen.button_height,
            percentage = percentage
        )
    }
}

@Composable
fun ProjectInfoTextColumn(
    modifier: Modifier = Modifier,
    detail: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8)),
        modifier = modifier
    ) {
        DetailTitleTextBox(text = stringResource(R.string.task_details))
        Text(
            text = detail,
            style = ProjectDetailText,
            color = DetailTextColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DetailTitleTextBox(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        style = ProjectTitleText,
        color = White,
        modifier = modifier
    )
}
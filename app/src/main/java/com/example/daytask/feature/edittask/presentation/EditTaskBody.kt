package com.example.daytask.feature.edittask.presentation

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.TaskGrid
import com.example.daytask.data.model.SubTask
import com.example.daytask.feature.details.presentation.DetailDialog
import com.example.daytask.feature.edittask.EditTaskUiState
import com.example.daytask.feature.newtask.presentation.UsersDialog

@Composable
fun EditTaskBody(
    modifier: Modifier = Modifier,
    uiState: EditTaskUiState,
    updateUiState: (EditTaskUiState) -> Unit,
    updateTask: () -> Unit,
    validSave: Boolean
) {
    val state = rememberLazyGridState()
    val spanCount = 7
    var showDialog by remember { mutableStateOf(false) }
    var showUsers by remember { mutableStateOf(false) }
    var lastSubTask by remember { mutableStateOf(SubTask()) }

    if (showDialog) {
        DetailDialog(
            onDismissRequest = { showDialog = false },
            title = uiState.newSubTaskTitle,
            validTitle = uiState.newSubTaskTitle.isNotBlank() && uiState.newSubTaskTitle != lastSubTask.title,
            updateTitle = { updateUiState(uiState.copy(newSubTaskTitle = it)) },
            buttonAction = {
                updateUiState(
                    uiState.copy(
                        newSubTasksList = uiState.newSubTasksList.map {
                            if (it == lastSubTask) it.copy(title = uiState.newSubTaskTitle)
                            else it
                        }
                    )
                )
            }
        )
    }

    TaskGrid(
        spanCount = spanCount,
        state = state,
        firstInput = uiState.newTitle,
        firstOnChange = { updateUiState(uiState.copy(newTitle = it)) },
        secondInput = uiState.newDetail,
        secondOnChange = { updateUiState(uiState.copy(newDetail = it)) },
        teamHeadline = stringResource(R.string.team_members),
        membersList = uiState.newMembersList,
        addMember = { showUsers = true },
        removeMember = {
            updateUiState(
                uiState.copy(
                    newMembersList = uiState.newMembersList.minus(it)
                )
            )
        },
        date = uiState.newDate,
        saveDate = { updateUiState(uiState.copy(newDate = it)) },
        subTasksList = uiState.newSubTasksList,
        subTaskAction = {
            updateUiState(
                uiState.copy(
                    newSubTasksList = uiState.newSubTasksList.minus(it)
                )
            )
        },
        showDialog = {
            lastSubTask = it
            updateUiState(uiState.copy(newSubTaskTitle = it.title))
            showDialog = true
        },
        editMode = true,
        buttonHeadline = stringResource(R.string.save_changes),
        buttonAction = updateTask,
        buttonEnable = validSave,
        modifier = modifier
    )

    if (showUsers) {
        UsersDialog(
            onDismissRequest = { showUsers = false },
            memberList = uiState.newMembersList,
            userList = uiState.users,
            updateMemberList = { updateUiState(uiState.copy(newMembersList = it)) }
        )
    }
}
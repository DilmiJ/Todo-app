package com.example.daytask.core.ui.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.daytask.R
import com.example.daytask.data.model.SubTask
import com.example.daytask.data.model.User
import com.example.daytask.feature.newtask.presentation.ClearInputField
import com.example.daytask.feature.newtask.presentation.NewTaskHeadline
import com.example.daytask.feature.newtask.presentation.TeamGridCard
import com.example.daytask.feature.newtask.presentation.TimeDateRow
import com.example.daytask.core.ui.theme.SmallInputText
import com.example.daytask.core.ui.theme.White

@Composable
fun TaskGrid(
    modifier: Modifier = Modifier,
    spanCount: Int,
    state: LazyGridState,
    firstInput: String,
    firstOnChange: (String) -> Unit,
    secondInput: String,
    secondOnChange: (String) -> Unit,
    teamHeadline: String,
    membersList: List<User>,
    addMember: () -> Unit,
    removeMember: (User) -> Unit,
    date: Long,
    saveDate: (Long) -> Unit,
    subTasksList: List<SubTask> = emptyList(),
    subTaskAction: (SubTask) -> Unit = {},
    showDialog: (SubTask) -> Unit = {},
    editMode: Boolean = false,
    buttonHeadline: String,
    buttonAction: () -> Unit,
    buttonEnable: Boolean,
) {
    val maxCurrentLineSpan = GridItemSpan(spanCount)

    LazyVerticalGrid(
        columns = GridCells.Fixed(spanCount),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8)),
        state = state,
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.offset_24))
    ) {
        item(span = { maxCurrentLineSpan }) {
            ClearInputField(
                headlineText = stringResource(R.string.task_title),
                inputText = firstInput,
                onValueChange = firstOnChange,
                imeAction = ImeAction.Next,
                modifier = Modifier
                    .padding(
                        top = dimensionResource(R.dimen.offset_16),
                        bottom = dimensionResource(R.dimen.offset_16)
                    )
            )
        }
        item(span = { maxCurrentLineSpan }) {
            ClearInputField(
                headlineText = stringResource(R.string.task_details),
                inputText = secondInput,
                onValueChange = secondOnChange,
                textStyle = SmallInputText.copy(White),
                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.offset_16))
            )
        }
        item(span = { maxCurrentLineSpan }) {
            NewTaskHeadline(teamHeadline)
        }
        items(
            items = membersList,
            span = { GridItemSpan(spanCount / 2) }
        ) {
            TeamGridCard(
                removeMember = { removeMember(it) },
                userName = it.displayName,
                userPhoto = it.photoUrl
            )
        }
        item {
            SquareButton(
                onClick = addMember,
                sizeRes = R.dimen.offset_48,
                iconRes = R.drawable.ic_add_square,
            )
        }
        item(span = { maxCurrentLineSpan }) {
            TimeDateRow(
                headlineText = stringResource(R.string.time_date),
                saveDate = saveDate,
                currentDate = date,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.offset_16))
            )
        }
        if (subTasksList.isNotEmpty()) {
            item(span = { maxCurrentLineSpan }) {
                NewTaskHeadline(stringResource(R.string.subtasks))
            }
            items(
                items = subTasksList,
                span = { maxCurrentLineSpan }
            ) {
                SubTaskCard(
                    subtask = it,
                    actionSubTask = { subTaskAction(it) },
                    editMode = editMode,
                    showDialog = { showDialog(it) }
                )
            }
        }
        item(span = { maxCurrentLineSpan }) {
            MainButton(
                onClick = buttonAction,
                text = buttonHeadline,
                enabled = buttonEnable,
                modifier = Modifier.padding(vertical = dimensionResource(R.dimen.offset_24))
            )
        }
    }
}
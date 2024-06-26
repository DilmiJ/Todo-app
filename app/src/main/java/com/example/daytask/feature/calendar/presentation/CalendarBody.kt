package com.example.daytask.feature.calendar.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.EmptyBox
import com.example.daytask.core.ui.presentation.ErrorScreen
import com.example.daytask.core.ui.presentation.LoadingScreen
import com.example.daytask.core.ui.presentation.SmallAvatarsRow
import com.example.daytask.data.model.Task
import com.example.daytask.data.model.User
import com.example.daytask.feature.calendar.CalendarUiState
import com.example.daytask.feature.newtask.presentation.NewTaskHeadline
import com.example.daytask.core.ui.theme.Black
import com.example.daytask.core.ui.theme.CalendarCardText
import com.example.daytask.core.ui.theme.DateColor
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.ProjectTitleText
import com.example.daytask.core.ui.theme.Tertiary
import com.example.daytask.core.ui.theme.White
import com.example.daytask.core.common.util.CalendarManager
import com.example.daytask.core.common.util.DateFormatter
import com.example.daytask.core.common.ext.Status

@Composable
fun CalendarBody(
    modifier: Modifier = Modifier,
    uiState: CalendarUiState,
    updateDay: (Int) -> Unit,
    navigateToTaskDetail: (String) -> Unit
) {
    val lazyRowListState = rememberLazyListState(
        initialFirstVisibleItemIndex = with(uiState.initDay - 3) { if (this >= 0) this else 0 }
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24)),
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.offset_24))
            .padding(top = dimensionResource(R.dimen.offset_16))
    ) {
        item { NewTaskHeadline(CalendarManager.currentMonthString) }
        item {
            CalendarDaysRow(
                lazyListState = lazyRowListState,
                selectedDay = uiState.initDay,
                dayClicked = updateDay
            )
        }
        item {
            NewTaskHeadline(
                if (CalendarManager.currentDayOfMonth == uiState.initDay) stringResource(R.string.today_task)
                else DateFormatter.formatShortDate(CalendarManager.getDayDate(uiState.initDay))
            )
        }
        item {
            when (uiState.status) {
                Status.Loading -> LoadingScreen(Modifier.fillMaxSize())
                Status.Error -> ErrorScreen(Modifier.fillMaxSize())
                Status.Done -> CalendarContent(
                    tasksList = uiState.tasksList,
                    navigateToTaskDetail = navigateToTaskDetail
                )
            }
        }
    }
}

@Composable
fun CalendarContent(
    modifier: Modifier = Modifier,
    tasksList: List<Task>,
    navigateToTaskDetail: (String) -> Unit
) {
    if (tasksList.isEmpty()) EmptyBox()
    else {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
            modifier = modifier
        ) {
            tasksList.forEach { task ->
                CalendarTaskCard(
                    title = task.title,
                    date = task.date,
                    memberList = task.memberList,
                    active = task == tasksList.first(),
                    onCardClick = { navigateToTaskDetail(task.id) }
                )
            }
        }
    }
}

@Composable
fun CalendarTaskCard(
    modifier: Modifier = Modifier,
    title: String,
    date: Long,
    memberList: List<User>,
    active: Boolean,
    onCardClick: () -> Unit
) {
    val textColor = if (active) Black else White
    val dateColor = if (active) Black else DateColor
    val containerColor = if (active) MainColor else Tertiary
    val borderColor = if (active) Black else MainColor
    val density = LocalDensity.current.density
    var cardHeight by remember { mutableFloatStateOf(0f) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RectangleShape,
        onClick = onCardClick,
        modifier = modifier
            .onSizeChanged { cardHeight = it.height / density }
    ) {
        Row {
            if (!active) {
                Box(
                    modifier = Modifier
                        .height(cardHeight.dp)
                        .width(dimensionResource(R.dimen.offset_16))
                        .background(borderColor)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.offset_24),
                        vertical = dimensionResource(R.dimen.offset_16)
                    )
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        text = title,
                        style = ProjectTitleText,
                        color = textColor,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false
                    )
                    Text(
                        text = stringResource(
                            R.string.due_on,
                            DateFormatter.formatTimeFromDate(date)
                        ),
                        style = CalendarCardText,
                        color = dateColor
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    SmallAvatarsRow(
                        memberList = memberList,
                        borderColor = borderColor,
                        rowLimit = 3
                    )
                }
            }
        }
    }
}
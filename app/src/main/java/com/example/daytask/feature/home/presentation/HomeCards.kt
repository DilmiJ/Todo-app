package com.example.daytask.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.CompleteCircle
import com.example.daytask.core.ui.presentation.SmallAvatarsRow
import com.example.daytask.data.model.User
import com.example.daytask.core.ui.theme.Black
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.Secondary
import com.example.daytask.core.ui.theme.SmallTaskInfoText
import com.example.daytask.core.ui.theme.TaskInfoText
import com.example.daytask.core.ui.theme.TaskTitleText
import com.example.daytask.core.ui.theme.White
import com.example.daytask.core.common.util.DateFormatter

@Composable
fun OngoingCard(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    title: String,
    memberList: List<User>,
    date: Long,
    percentage: Float
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Secondary),
        shape = RectangleShape,
        onClick = onCardClick,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.offset_8))) {
            Text(
                text = title,
                style = TaskTitleText,
                color = White,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.offset_8))
                    .fillMaxWidth()
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.team_members),
                        style = TaskInfoText,
                        color = White
                    )
                    SmallAvatarsRow(memberList = memberList)
                    Text(
                        text = stringResource(R.string.due_on, DateFormatter.formatShortDate(date)),
                        style = TaskInfoText,
                        color = White
                    )
                }
                CompleteCircle(
                    percentage = percentage,
                    sizeRes = R.dimen.button_height
                )
            }
        }
    }
}

@Composable
fun CompletedCard(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    title: String,
    memberList: List<User>,
    containerColor: Color,
    textColor: Color,
    cardWidth: Int,
    completePercentage: Float
) {
    val density = LocalDensity.current.density
    val columnPadding = dimensionResource(R.dimen.offset_8)
    val paddingPixels = columnPadding.value * 2 * density
    val lineWidth = (cardWidth * density - paddingPixels) * completePercentage

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RectangleShape,
        onClick = onCardClick,
        modifier = modifier
            .width(cardWidth.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
            modifier = Modifier.padding(columnPadding)
        ) {
            Text(
                text = title,
                style = TaskTitleText,
                color = textColor,
                overflow = TextOverflow.Ellipsis,
                minLines = 3,
                maxLines = 3
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.team_members),
                    style = SmallTaskInfoText,
                    color = textColor
                )
                SmallAvatarsRow(memberList = memberList)
            }
            CompleteLine(
                textColor = textColor,
                lineWidth = lineWidth,
                completePercentage = completePercentage
            )
        }
    }
}

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit,
    memberListSize: Int,
    completed: Boolean,
    percent: Int,
    title: String,
    date: Long
) {
    val textColor = if (completed) Black else White
    Card(
        colors = CardDefaults.cardColors(containerColor = if (completed) MainColor else Secondary),
        shape = RectangleShape,
        onClick = onCardClick,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.offset_8))) {
            Text(
                text = title,
                style = TaskTitleText,
                color = textColor,
                modifier = Modifier
                    .padding(bottom = dimensionResource(R.dimen.offset_8))
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.team_members_count, memberListSize),
                    style = TaskInfoText,
                    color = textColor
                )
                Text(
                    text = stringResource(R.string.completed_percentage, percent),
                    style = TaskInfoText,
                    color = textColor
                )
            }
            Text(
                text = stringResource(R.string.due_on, DateFormatter.formatShortDate(date)),
                style = TaskInfoText,
                color = textColor
            )
        }
    }
}
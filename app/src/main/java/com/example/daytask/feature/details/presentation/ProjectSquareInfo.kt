package com.example.daytask.feature.details.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.SmallAvatarsRow
import com.example.daytask.core.ui.presentation.SquareButton
import com.example.daytask.data.model.User
import com.example.daytask.core.ui.theme.DetailsDateText
import com.example.daytask.core.ui.theme.HelpColor
import com.example.daytask.core.ui.theme.ProjectInfoText
import com.example.daytask.core.ui.theme.White
import com.example.daytask.core.common.util.DateFormatter

@Composable
fun ProjectSquareInfo(
    modifier: Modifier = Modifier,
    openCalendar: () -> Unit,
    openMembers: () -> Unit,
    date: Long,
    memberList: List<User>
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        ProjectInfoBox(
            iconRes = R.drawable.ic_calendar_remove,
            titleTextRes = R.string.due_date,
            onClick = openCalendar
        ) {
            Text(
                text = DateFormatter.formatShortDate(date),
                style = DetailsDateText,
                color = White,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.offset_8))
            )
        }
        ProjectInfoBox(
            iconRes = R.drawable.ic_profile_users,
            titleTextRes = R.string.task_team,
            onClick = openMembers
        ) {
            SmallAvatarsRow(
                memberList = memberList,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.offset_4))
            )
        }
    }
}

@Composable
fun ProjectInfoBox(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    @StringRes titleTextRes: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        SquareButton(
            onClick = onClick,
            sizeRes = R.dimen.button_height,
            iconRes = iconRes
        )
        Column {
            Text(
                text = stringResource(titleTextRes),
                style = ProjectInfoText,
                color = HelpColor,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.offset_8))
            )
            content()
        }
    }
}
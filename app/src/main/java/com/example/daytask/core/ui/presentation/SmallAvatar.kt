package com.example.daytask.core.ui.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.data.model.User
import com.example.daytask.core.ui.theme.HelpColor
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.PercentageText

@Composable
fun SmallAvatar(
    modifier: Modifier = Modifier,
    photoUrl: String?,
    borderColor: Color = MainColor
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.border(1.dp, borderColor, CircleShape)
    ) {
        AvatarImage(
            userPhoto = photoUrl,
            avatarSizeRes = R.dimen.offset_20
        )
    }
}

@Composable
fun SmallAvatarsRow(
    modifier: Modifier = Modifier,
    memberList: List<User>,
    borderColor: Color = MainColor,
    rowLimit: Int = 5
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-7).dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (memberList.size <= rowLimit) memberList.forEach {
            SmallAvatar(
                photoUrl = it.photoUrl,
                borderColor = borderColor
            )
        }
        else {
            var index = 0
            memberList.forEach {
                if (index == rowLimit) return@forEach
                SmallAvatar(
                    photoUrl = it.photoUrl,
                    borderColor = borderColor
                )
                index++
            }
            Text(
                text = stringResource(R.string.members_count, memberList.size - index),
                style = PercentageText,
                color = HelpColor
            )
        }
    }
}
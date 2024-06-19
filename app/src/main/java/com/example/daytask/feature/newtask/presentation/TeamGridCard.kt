package com.example.daytask.feature.newtask.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.AvatarImage
import com.example.daytask.core.ui.theme.Secondary
import com.example.daytask.core.ui.theme.TeamCardText
import com.example.daytask.core.ui.theme.White

@Composable
fun TeamGridCard(
    modifier: Modifier = Modifier,
    removeMember: () -> Unit,
    userName: String?,
    userPhoto: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Secondary),
        shape = RectangleShape,
        modifier = modifier.height(dimensionResource(R.dimen.offset_48))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarImage(
                userPhoto = userPhoto,
                avatarSizeRes = R.dimen.offset_20,
                modifier = Modifier.padding(dimensionResource(R.dimen.offset_8))
            )
            Text(
                text = userName ?: "",
                style = TeamCardText.copy(White),
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            )
            IconButton(onClick = removeMember) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_square),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}
package com.example.daytask.feature.users.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.AvatarImage
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.MessageUserNameText
import com.example.daytask.core.ui.theme.White
import com.example.daytask.data.model.User

@Composable
fun UsersBody(
    modifier: Modifier = Modifier,
    navigateToUserChat: (String) -> Unit = {},
    users: List<User> = emptyList()
) {
    val alphabetMap = remember { mutableMapOf<Char, Int>() }

    // TODO: empty users list
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24)),
        contentPadding = PaddingValues(
            bottom = dimensionResource(R.dimen.offset_24)
        ),
        modifier = modifier
    ) {
        item {
            CreateGroupRow(onClick = { /*TODO: NavTo multi user selection*/ })
        }
        items(users) { user ->
            val char = user.displayName.toString().uppercase()[0]
            val letter = if (alphabetMap[char] == null) {
                alphabetMap[char] = 0
                char
            } else ' '

            UserCard(
                userName = user.displayName,
                userPhoto = user.photoUrl,
                onClick = { navigateToUserChat(user.userId) },
                letter = letter
            )
        }
    }
}

@Composable
fun CreateGroupRow(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickable(
            onClick = onClick,
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        )
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile_users),
            contentScale = ContentScale.Inside,
            contentDescription = null,
            modifier = Modifier
                .size(dimensionResource(R.dimen.offset_48))
                .clip(CircleShape)
                .background(MainColor)
        )
        Text(
            text = stringResource(R.string.create_group),
            style = MessageUserNameText,
            color = White,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .weight(1f)
        )
    }
}

@Composable
fun UserCard(
    modifier: Modifier = Modifier,
    userName: String?,
    userPhoto: String?,
    onClick: () -> Unit,
    letter: Char = ' '
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
    ) {
        AvatarImage(
            userPhoto = userPhoto,
            avatarSizeRes = R.dimen.offset_48
        )
        Text(
            text = userName.toString(),
            style = MessageUserNameText,
            color = White,
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .weight(1f)
        )
        Text(
            text = letter.toString(),
            style = MessageUserNameText,
            color = MainColor
        )
    }
}
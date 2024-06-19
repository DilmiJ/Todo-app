package com.example.daytask.feature.notification.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.example.daytask.R
import com.example.daytask.core.common.util.firebase.UserManager
import com.example.daytask.core.ui.presentation.AvatarImage
import com.example.daytask.core.ui.presentation.EmptyBox
import com.example.daytask.core.ui.presentation.LoadingScreen
import com.example.daytask.core.ui.theme.HelpColor
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.MessageUserNameText
import com.example.daytask.core.ui.theme.NavText
import com.example.daytask.core.ui.theme.White
import com.example.daytask.data.Notification

@Composable
fun NotificationBody(
    modifier: Modifier = Modifier,
    notificationsList: List<Notification>,
    action: (Notification) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
        contentPadding = PaddingValues(
            bottom = dimensionResource(R.dimen.offset_24)
        ),
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.offset_24))
    ) {
        item {
            if (notificationsList.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.new_word),
                    style = NavText,
                    color = White,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_8))
                )
            } else EmptyBox()
        }
        items(
            items = notificationsList,
            key = { it.id }
        ) { notification ->
            val data by UserManager.flowComposable(notification.senderId).collectAsState()

            if (data.status == com.example.daytask.core.common.ext.Status.Loading) {
                LoadingScreen(Modifier.fillMaxWidth())
            } else {
                NotificationItem(
                    action = { action(notification) },
                    senderName = data.user.displayName.toString(),
                    senderPhoto = data.user.photoUrl.toString(),
                    messageText = notification.messageText,
                    destinationName = notification.destinationText
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    senderName: String,
    senderPhoto: String,
    messageText: String,
    destinationName: String
) {
    val space = " "
    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = White
            )
        ) {
            append("$senderName$space")
        }

        withStyle(
            style = SpanStyle(
                color = HelpColor,
                fontWeight = FontWeight.W400
            )
        ) {
            append("$messageText$space")
        }

        withStyle(
            style = SpanStyle(
                color = MainColor,
                fontWeight = FontWeight.W400
            )
        ) {
            append(destinationName)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
        modifier = modifier.clickable(onClick = action)
    ) {
        AvatarImage(
            userPhoto = senderPhoto,
            avatarSizeRes = R.dimen.offset_48
        )
        Text(
            text = annotatedText,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = MessageUserNameText,
            modifier = Modifier.weight(1f)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "31 min", // TODO: real time past
                style = MessageUserNameText.copy(fontSize = 8.sp),
                color = White
            )
        }
    }
}
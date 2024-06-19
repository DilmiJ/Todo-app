package com.example.daytask.feature.messages.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.daytask.R
import com.example.daytask.core.common.ext.Status
import com.example.daytask.core.common.util.firebase.UserManager
import com.example.daytask.core.ui.presentation.AvatarImage
import com.example.daytask.core.ui.presentation.CircleCanvas
import com.example.daytask.core.ui.presentation.EmptyBox
import com.example.daytask.core.ui.presentation.LoadingScreen
import com.example.daytask.core.ui.theme.Black
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.MessageColor
import com.example.daytask.core.ui.theme.MessageUserNameText
import com.example.daytask.core.ui.theme.TeamCardText
import com.example.daytask.core.ui.theme.Tertiary
import com.example.daytask.core.ui.theme.White
import com.example.daytask.data.model.Chat

@Composable
fun MessageBody(
    modifier: Modifier = Modifier,
    chatList: List<Chat>,
    navigateToUsers: () -> Unit,
    navigateToChat: (String) -> Unit
) {
    var isChat by remember { mutableStateOf(true) } // TODO: group chats
    var isStartChatVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -1) isStartChatVisible = false
                if (available.y > 1) isStartChatVisible = true
                return Offset.Zero
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24)),
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.offset_16))
                .padding(horizontal = dimensionResource(R.dimen.offset_24))
        ) {
            MessageButtonRow(
                isChat = isChat,
                changeMode = {
                    isChat = it
                    isStartChatVisible = true
                }
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24)),
                contentPadding = PaddingValues(
                    bottom = dimensionResource(R.dimen.offset_24)
                ),
                modifier = Modifier.nestedScroll(nestedScrollConnection)
            ) {
                if (isChat) {
                    if (chatList.isEmpty()) {
                        item { EmptyBox() }
                    } else {
                        items(
                            items = chatList,
                            key = { it.userId }
                        ) { chat ->
                            val data by UserManager.flowComposable(chat.userId).collectAsState()

                            val lastMsg = chat.messagesList.last()
                            if (data.status == Status.Loading) {
                                LoadingScreen(Modifier.fillMaxWidth())
                            } else {
                                MessageItem(
                                    senderName = data.user.displayName.toString(),
                                    senderPhoto = data.user.photoUrl.toString(),
                                    messageText = lastMsg.message,
                                    isRead = lastMsg.isRead,
                                    action = { navigateToChat(chat.userId) }
                                )
                            }
                        }
                    }
                } else {
                    if (true) item { EmptyBox() }
                    else {
                        /*TODO: group chats*/
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isStartChatVisible,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            MessageButton(
                action = navigateToUsers,
                textRes = R.string.start_chat,
                modifier = Modifier.padding(dimensionResource(R.dimen.offset_24))
            )
        }
    }
}

@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    senderPhoto: String,
    senderName: String,
    messageText: String,
    isRead: Boolean,
    action: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16)),
        modifier = modifier.clickable(
            onClick = action
        )
    ) {
        AvatarImage(
            userPhoto = senderPhoto,
            avatarSizeRes = R.dimen.offset_48
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = senderName,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                style = MessageUserNameText,
                color = White
            )
            Text(
                text = messageText,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                style = MessageUserNameText.copy(fontWeight = FontWeight.W400),
                color = MessageColor
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "31 min", // TODO: real time past
                style = MessageUserNameText.copy(fontSize = 8.sp),
                color = White
            )
            if (!isRead) {
                CircleCanvas(
                    radius = 6f,
                    color = MainColor,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_4))
                )
            }
        }
    }
}

@Composable
fun MessageButtonRow(
    modifier: Modifier = Modifier,
    isChat: Boolean,
    changeMode: (Boolean) -> Unit
) {
    Row(modifier = modifier.fillMaxWidth()) {
        MessageButton(
            action = { changeMode(true) },
            textRes = R.string.chat,
            active = isChat,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(dimensionResource(R.dimen.offset_16)))
        MessageButton(
            action = { changeMode(false) },
            textRes = R.string.groups,
            active = !isChat,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MessageButton(
    modifier: Modifier = Modifier,
    action: () -> Unit,
    @StringRes textRes: Int,
    active: Boolean = true
) {
    val textColor = if (active) Black else White
    val buttonColor = if (active) MainColor else Tertiary

    Button(
        onClick = action,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(textRes),
            style = TeamCardText
        )
    }
}
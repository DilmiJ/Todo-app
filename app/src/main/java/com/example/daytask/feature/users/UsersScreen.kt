package com.example.daytask.feature.users

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.daytask.R
import com.example.daytask.core.navigation.DayTaskCenterTopAppBar
import com.example.daytask.core.navigation.NavigationDestination
import com.example.daytask.core.ui.theme.DayTaskTheme
import com.example.daytask.core.ui.theme.White
import com.example.daytask.feature.users.presentation.UsersBody

object UsersDestination : NavigationDestination {
    override val route = "users"
    override val titleRes = R.string.new_message
}

@Composable
fun UsersScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    navigateToUserChat: (String) -> Unit,
    viewModel: UsersViewModel = viewModel()
) {
    val users by viewModel.users.collectAsState()

    Scaffold(
        topBar = {
            DayTaskCenterTopAppBar(
                currentRoute = UsersDestination.route,
                navigateUp = navigateUp,
                actions = {
                    IconButton(onClick = { /*TODO: search people by name*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null,
                            tint = White
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        UsersBody(
            navigateToUserChat = {
                navigateUp()
                navigateToUserChat(it)
            },
            users = users,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding() + dimensionResource(R.dimen.offset_16))
                .padding(horizontal = dimensionResource(R.dimen.offset_24))
        )
    }
}

@Preview
@Composable
fun UsersBodyPreview() {
    DayTaskTheme {
        UsersBody()
    }
}
package com.example.daytask.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.daytask.core.common.util.firebase.NotificationManager
import com.example.daytask.feature.calendar.CalendarDestination
import com.example.daytask.feature.calendar.CalendarScreen
import com.example.daytask.feature.chat.ChatDestination
import com.example.daytask.feature.chat.ChatScreen
import com.example.daytask.feature.details.TaskDetailsDestination
import com.example.daytask.feature.details.TaskDetailsScreen
import com.example.daytask.feature.edittask.EditTaskDestination
import com.example.daytask.feature.edittask.EditTaskScreen
import com.example.daytask.feature.home.HomeDestination
import com.example.daytask.feature.home.HomeScreen
import com.example.daytask.feature.messages.MessageDestination
import com.example.daytask.feature.messages.MessageScreen
import com.example.daytask.feature.newtask.NewTaskDestination
import com.example.daytask.feature.newtask.NewTaskScreen
import com.example.daytask.feature.notification.NotificationDestination
import com.example.daytask.feature.notification.NotificationScreen
import com.example.daytask.feature.profile.ProfileDestination
import com.example.daytask.feature.profile.ProfileScreen
import com.example.daytask.feature.users.UsersDestination
import com.example.daytask.feature.users.UsersScreen

@Composable
fun DayTaskNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    navigateToStart: () -> Unit = {},
) {
    var bottomBarState by remember { mutableStateOf(true) }
    var topBarState by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: HomeDestination.route

    when (currentRoute) {
        CalendarDestination.route, NotificationDestination.route -> {
            bottomBarState = true
            topBarState = true
        }

        MessageDestination.route, HomeDestination.route -> {
            bottomBarState = true
            topBarState = false
        }

        TaskDetailsDestination.routeWithArgs, UsersDestination.route, ChatDestination.routeWithArgs -> {
            bottomBarState = false
            topBarState = false
        }

        else -> {
            bottomBarState = false
            topBarState = true
        }
    }

    Scaffold(
        topBar = {
            if (topBarState) {
                DayTaskTopAppBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        },
        bottomBar = {
            val notificationFlow by NotificationManager.flowComposable.collectAsState()

            AnimatedVisibility(
                visible = bottomBarState,
                enter = slideInVertically { it } + expandVertically(),
                exit = shrinkVertically() + slideOutVertically { it }
            ) {
                DayTaskBottomAppBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    isNotify = notificationFlow.isNotify
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            composable(route = HomeDestination.route) {
                HomeScreen(
                    navigateToProfile = { navController.navigate(ProfileDestination.route) },
                    navigateToDetails = { id ->
                        navController.navigate("${TaskDetailsDestination.route}/$id")
                    }
                )
            }
            composable(route = ProfileDestination.route) {
                ProfileScreen(
                    navigateUp = { navController.navigateUp() },
                    navigateToNewTask = { navController.navigate(NewTaskDestination.route) },
                    navigateToStart = navigateToStart,
                )
            }
            composable(route = MessageDestination.route) {
                MessageScreen(
                    onBack = { navController.popBackStack(HomeDestination.route, false) },
                    navigateToUsers = { navController.navigate(UsersDestination.route) },
                    navigateToChat = { navController.navigate("${ChatDestination.route}/$it") }
                )
            }
            composable(route = CalendarDestination.route) {
                CalendarScreen(
                    navigateToTaskDetail = { id ->
                        navController.navigate("${TaskDetailsDestination.route}/$id")
                    },
                    onBack = { navController.popBackStack(HomeDestination.route, false) }
                )
            }
            composable(route = NotificationDestination.route) {
                NotificationScreen(
                    onBack = { navController.popBackStack(HomeDestination.route, false) },
                    navigateToChat = { navController.navigate("${ChatDestination.route}/$it") }
                )
            }
            composable(route = NewTaskDestination.route) {
                NewTaskScreen(
                    navigateUp = { navController.navigateUp() },
                    modifier = Modifier.imePadding()
                )
            }
            composable(
                route = TaskDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(TaskDetailsDestination.taskId) {
                    type = NavType.StringType
                })
            ) {
                TaskDetailsScreen(
                    navigateUp = { navController.navigateUp() },
                    navigateToEdit = { navController.navigate("${EditTaskDestination.route}/$it") }
                )
            }
            composable(
                route = EditTaskDestination.routeWithArgs,
                arguments = listOf(navArgument(EditTaskDestination.taskId) {
                    type = NavType.StringType
                })
            ) {
                EditTaskScreen(
                    navigateUp = { navController.navigateUp() },
                    modifier = Modifier.imePadding()
                )
            }
            composable(route = UsersDestination.route) {
                UsersScreen(
                    navigateUp = { navController.navigateUp() },
                    navigateToUserChat = { navController.navigate("${ChatDestination.route}/$it") }
                )
            }
            composable(
                route = ChatDestination.routeWithArgs,
                arguments = listOf(navArgument(ChatDestination.userId) {
                    type = NavType.StringType
                })
            ) {
                ChatScreen(
                    navigateUp = { navController.navigateUp() },
                    modifier = Modifier.imePadding()
                )
            }
        }
    }
}
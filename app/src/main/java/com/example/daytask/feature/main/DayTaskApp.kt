package com.example.daytask.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.daytask.R
import com.example.daytask.core.navigation.DayTaskNavHost
import com.example.daytask.core.navigation.NavigationDestination

object MainDestination : NavigationDestination {
    override val route = "main"
    override val titleRes = R.string.app_name_clear
}

@Composable
fun DayTaskApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigateToStart: () -> Unit = {},
) {
    DayTaskNavHost(
        navController = navController,
        navigateToStart = navigateToStart,
        modifier = modifier
    )
}

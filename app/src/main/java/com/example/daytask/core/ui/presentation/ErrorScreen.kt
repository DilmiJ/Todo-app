package com.example.daytask.core.ui.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier
) {
    // TODO: Nice Error box
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = "Error")
    }
}
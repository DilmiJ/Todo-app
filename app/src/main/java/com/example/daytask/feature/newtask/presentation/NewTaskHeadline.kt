package com.example.daytask.feature.newtask.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.daytask.core.ui.theme.NewTaskHeadlineText

@Composable
fun NewTaskHeadline(
    headlineText: String
) {
    Text(
        text = headlineText,
        style = NewTaskHeadlineText
    )
}
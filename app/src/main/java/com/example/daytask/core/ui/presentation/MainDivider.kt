package com.example.daytask.core.ui.presentation

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.daytask.R
import com.example.daytask.core.ui.theme.MainColor

@Composable
fun MainDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = dimensionResource(R.dimen.offset_2),
        color = MainColor
    )
}
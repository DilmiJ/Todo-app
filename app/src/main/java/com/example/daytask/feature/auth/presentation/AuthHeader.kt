package com.example.daytask.feature.auth.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.daytask.R
import com.example.daytask.core.ui.theme.HeadlineText
import com.example.daytask.core.ui.theme.SplashLogoBigText
import com.example.daytask.core.ui.theme.White
import com.example.daytask.feature.splash.LogoColumn

@Composable
fun AuthHeader(
    modifier: Modifier = Modifier,
    isLogIn: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = dimensionResource(R.dimen.offset_24))
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        LogoColumn(
            modifier = Modifier
                .padding(top = dimensionResource(R.dimen.offset_32))
                .align(Alignment.CenterHorizontally),
            multi = 1.5f,
            style = SplashLogoBigText
        )
        Text(
            text = if (isLogIn)
                stringResource(R.string.welcome_back) else stringResource(R.string.create_account),
            color = White,
            style = HeadlineText,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_32))
        )
        content()
    }
}
package com.example.daytask.feature.splash

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.core.navigation.NavigationDestination
import com.example.daytask.core.ui.presentation.MainButton
import com.example.daytask.core.ui.presentation.MultiColorText
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.SplashText
import com.example.daytask.core.ui.theme.White

object SplashDestination : NavigationDestination {
    override val route = "splash"
    override val titleRes = R.string.app_name_clear
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onAction: () -> Unit
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val density = LocalDensity.current.density
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    var checked by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .wrapContentHeight(Alignment.Top)
            .onSizeChanged {
                size = it
            }
            .animateContentSize()
            .padding(
                start = dimensionResource(R.dimen.offset_24),
                end = dimensionResource(R.dimen.offset_24),
                top = dimensionResource(R.dimen.offset_24)
            )
    ) {
        LogoColumn()
        val columnH = size.height / density
        val imageSize = screenHeight / 3
        if (!checked && columnH != 0f && columnH + imageSize <= screenHeight) {
            checked = true
        }
        if (checked) {
            SplashImage(imageSize = imageSize)
        }
        SplashText()
        MainButton(
            text = stringResource(R.string.lets_start),
            onClick = onAction,
            modifier = Modifier
                .padding(vertical = dimensionResource(R.dimen.offset_24))
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.offset_64))
        )
    }
}

@Composable
fun SplashImage(
    modifier: Modifier = Modifier,
    imageSize: Int
) {
    Box(
        modifier = modifier
            .padding(
                top = dimensionResource(R.dimen.offset_32),
                end = dimensionResource(R.dimen.offset_4)
            )
            .background(White)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.img_splash_image_svg),
            contentDescription = stringResource(R.string.splash_image),
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.offset_24),
                    vertical = dimensionResource(R.dimen.offset_4)
                )
                .height(imageSize.dp)
        )
    }
}

@Composable
fun SplashText(
    modifier: Modifier = Modifier
) {
    MultiColorText(
        arrayOf(
            Pair(stringResource(R.string.splash_text_1), White),
            Pair(stringResource(R.string.app_name_clear), MainColor)
        ),
        style = SplashText,
        modifier = modifier.padding(top = dimensionResource(R.dimen.offset_32))
    )
}

@Preview(
    widthDp = 360,
    heightDp = 480
)
@Composable
fun SplashPreview() {
    SplashScreen(
        onAction = {}
    )
}

package com.example.daytask.feature.profile.presentation

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.AvatarImage
import com.example.daytask.core.ui.theme.MainColor

@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    userPhoto: String?,
    saveImage: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val launchImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            @Suppress("DEPRECATION")
            saveImage(
                if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(context.contentResolver, uri)
                    )
                }
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(
                top = dimensionResource(R.dimen.offset_16),
                bottom = dimensionResource(R.dimen.offset_24)
            )
    ) {
        AvatarImage(
            avatarSizeRes = R.dimen.image_big,
            onImageClick = { launchImage.launch("image/*") },
            userPhoto = userPhoto
        )
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.large_ellipse))
                .border(
                    width = dimensionResource(R.dimen.offset_2),
                    color = MainColor,
                    shape = CircleShape
                )
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    bottom = 7.dp,
                    end = 7.dp
                )
                .size(dimensionResource(R.dimen.offset_32))
                .align(Alignment.BottomEnd)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add_square_profile),
                contentDescription = null
            )
        }
    }
}

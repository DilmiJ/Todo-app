package com.example.daytask.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.MainButton
import com.example.daytask.core.common.util.Constants

@Composable
fun ProfileDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    saveChanges: () -> Unit,
    enableSave: Boolean,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = Constants.noDismissProperties
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_24))
            ) {
                content()
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MainButton(
                        onClick = onDismissRequest,
                        text = stringResource(R.string.cancel)
                    )
                    MainButton(
                        onClick = saveChanges,
                        text = stringResource(R.string.save),
                        enabled = enableSave
                    )
                }
            }
        }
    }
}
package com.example.daytask.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.MainDivider
import com.example.daytask.core.common.util.Constants.noDismissProperties

@Composable
fun AuthDialog(
    modifier: Modifier = Modifier,
    dialogText: String,
    dialogTitle: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = noDismissProperties
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            AuthDialogBody(
                dialogText = dialogText,
                dialogTitle = dialogTitle,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
fun AuthDialogBody(
    modifier: Modifier = Modifier,
    dialogText: String,
    dialogTitle: String,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = dimensionResource(R.dimen.offset_24),
                vertical = dimensionResource(R.dimen.offset_24)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_16))
    ) {
        Text(
            text = dialogTitle,
            style = MaterialTheme.typography.titleLarge
        )
        MainDivider()
        Text(dialogText)
        Button(
            onClick = onDismissRequest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.i_have_read, dialogTitle),
                textAlign = TextAlign.Center
            )
        }
    }
}

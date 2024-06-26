package com.example.daytask.feature.newtask.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import com.example.daytask.R
import com.example.daytask.core.ui.theme.HelpText
import com.example.daytask.core.ui.theme.InputText
import com.example.daytask.core.ui.theme.PlaceholderColor
import com.example.daytask.core.ui.theme.White
import com.example.daytask.core.common.util.TextFieldManager
import com.example.daytask.core.common.util.TextFieldManager.clearFocusOnKeyboardDismiss

@Composable
fun ClearInputField(
    modifier: Modifier = Modifier,
    headlineText: String,
    inputText: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    textStyle: TextStyle = InputText.copy(White)
) {
    val focusManager = LocalFocusManager.current
    val keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction)
    val keyboardActions = KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.offset_8)),
        modifier = modifier
    ) {
        NewTaskHeadline(headlineText)
        OutlinedTextField(
            value = inputText,
            onValueChange = onValueChange,
            textStyle = textStyle,
            colors = TextFieldManager.colors(),
            shape = RectangleShape,
            placeholder = {
                Text(
                    text = stringResource(R.string.enter, headlineText.lowercase()),
                    style = HelpText.copy(PlaceholderColor)
                )
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss()
        )
    }
}
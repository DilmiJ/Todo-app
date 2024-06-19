package com.example.daytask.feature.auth.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.daytask.R
import com.example.daytask.core.ui.presentation.InputField
import com.example.daytask.core.ui.theme.HelpColor
import com.example.daytask.core.ui.theme.InputText

@Composable
fun InputColumn(
    modifier: Modifier = Modifier,
    headlineText: String,
    inputText: String,
    imeAction: ImeAction = ImeAction.Next,
    isHidden: Boolean = false,
    onValueChange: (String) -> Unit,
    @DrawableRes leadingIconRes: Int,
    @DrawableRes trailingIconsRes: Pair<Int, Int>? = null,
    @StringRes errorTextRes: Int,
    validation: () -> Boolean
) {
    val keyboardType = if (imeAction == ImeAction.Next)
        KeyboardType.Email else KeyboardType.Password

    Column(
        modifier = modifier
    ) {
        Text(
            text = headlineText,
            style = InputText,
            color = HelpColor,
            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.offset_16))
        )
        InputField(
            isHidden = isHidden,
            imeAction = imeAction,
            keyboardType = keyboardType,
            inputText = inputText,
            onValueChange = onValueChange,
            leadingIconRes = leadingIconRes,
            trailingIconsRes = trailingIconsRes,
            errorTextRes = errorTextRes,
            validation = validation,
            headlineText = headlineText,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

package com.example.daytask.feature.auth.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import com.example.daytask.R
import com.example.daytask.core.common.ext.OnAction
import com.example.daytask.core.common.ext.OnActionBoolean
import com.example.daytask.core.ui.presentation.MainButton
import com.example.daytask.core.ui.presentation.MultiColorText
import com.example.daytask.core.ui.theme.GoogleText
import com.example.daytask.core.ui.theme.HelpColor
import com.example.daytask.core.ui.theme.HelpText
import com.example.daytask.core.ui.theme.MainColor
import com.example.daytask.core.ui.theme.White
import com.example.daytask.feature.auth.AuthUiState
import com.example.daytask.feature.auth.ext.AuthAction

@Composable
fun AuthBody(
    modifier: Modifier = Modifier,
    onAction: OnAction = {},
    onViewModelAction: OnAction = {},
    onViewModelActionBoolean: OnActionBoolean = { false },
    uiState: AuthUiState = AuthUiState(),
) {
    var isLogIn by remember { mutableStateOf(false) }

    AuthHeader(
        isLogIn = isLogIn,
        modifier = modifier
    ) {
        Column {
            if (!isLogIn) {
                InputColumn(
                    headlineText = stringResource(R.string.full_name),
                    inputText = uiState.fullName,
                    errorTextRes = R.string.invalid_name,
                    leadingIconRes = R.drawable.ic_user,
                    onValueChange = {
                        onViewModelAction(
                            AuthAction.UpdateUiState(uiState.copy(fullName = it))
                        )
                    },
                    validation = {
                        onViewModelActionBoolean(AuthAction.Check.NAME)
                    },
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_8))
                )
            }
            EmailPasswordInput(
                uiState = uiState,
                errorEmailRes = R.string.invalid_email,
                errorPasswordRes = R.string.invalid_password,
                updateUiState = {
                    onViewModelAction(
                        AuthAction.UpdateUiState(it)
                    )
                },
                validEmail = {
                    onViewModelActionBoolean(AuthAction.Check.EMAIL)
                },
                validPassword = {
                    onViewModelActionBoolean(AuthAction.Check.PASSWORD)
                },
                modifier = Modifier.padding(
                    top = if (isLogIn)
                        dimensionResource(R.dimen.offset_8) else dimensionResource(R.dimen.offset_16)
                )
            )
            if (isLogIn) {
                ForgotText(
                    text = stringResource(R.string.forgot_password),
                    onClick = {
                        if (onViewModelActionBoolean(AuthAction.Check.EMAIL))
                            onAction(AuthAction.ForgotPassword(uiState.email))
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = dimensionResource(R.dimen.offset_8))
                )
            } else {
                var isShow by remember { mutableStateOf(false) }
                var termsText by remember { mutableStateOf(false) }
                TermsRow(
                    readPrivacy = {
                        isShow = true
                        termsText = false
                    },
                    readTerms = {
                        isShow = true
                        termsText = true
                    },
                    checked = uiState.checkedPrivacy,
                    changeChecked = {
                        onViewModelAction(
                            AuthAction.UpdateUiState(uiState.copy(checkedPrivacy = !uiState.checkedPrivacy))
                        )
                    },
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_8))
                )
                if (isShow) {
                    val template = LoremIpsum(200) // TODO: replace template with real strings
                        .values
                        .joinToString()
                        .replace("\n", " ")
                    AuthDialog(
                        onDismissRequest = { isShow = false },
                        dialogTitle = if (termsText) stringResource(R.string.terms_condition)
                        else stringResource(R.string.privacy_policy),
                        dialogText = if (termsText) stringResource(
                            R.string.terms_big_text,
                            template
                        )
                        else stringResource(R.string.privacy_big_text, template)
                    )
                }
            }
            ButtonColumn(
                mainButtonTextRes = if (isLogIn) R.string.log_in else R.string.sign_up,
                enabledMainButton = if (isLogIn) {
                    onViewModelActionBoolean(AuthAction.Check.LOG_IN)
                } else {
                    onViewModelActionBoolean(AuthAction.Check.SIGN_UP)
                },
                onMainClick = {
                    onAction(
                        if (isLogIn) {
                            AuthAction.LogIn(
                                uiState.email,
                                uiState.password,
                            )

                        } else {

                            AuthAction.SignUp(
                                uiState.email,
                                uiState.password,
                                uiState.fullName,
                            )
                        }
                    )
                },
                onGoogleClick = {
                    onAction(AuthAction.OnClick.GOOGLE_SIGN_IN)
                },
                modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_24))
            )
            MultiColorText(
                if (isLogIn) arrayOf(
                    Pair(stringResource(R.string.no_account), HelpColor),
                    Pair(stringResource(R.string.sign_up), MainColor)
                ) else arrayOf(
                    Pair(stringResource(R.string.already_have_account), HelpColor),
                    Pair(stringResource(R.string.log_in), MainColor)
                ),
                style = HelpText,
                modifier = Modifier
                    .padding(vertical = dimensionResource(R.dimen.offset_24))
                    .clickable(onClick = { isLogIn = !isLogIn })
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ForgotText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Text(
        text = text,
        style = HelpText,
        color = HelpColor,
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
fun TermsRow(
    checked: Boolean,
    changeChecked: () -> Unit,
    readPrivacy: () -> Unit,
    readTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = changeChecked
        ) {
            Icon(
                painter = painterResource(if (checked) R.drawable.ic_ticksquare else R.drawable.ic_square),
                contentDescription = null,
                tint = if (checked) MainColor else White
            )
        }
        AnnotatedClickableText(
            readPrivacy = readPrivacy,
            readTerms = readTerms
        )
    }
}

@Composable
fun ButtonColumn(
    modifier: Modifier = Modifier,
    @StringRes mainButtonTextRes: Int,
    enabledMainButton: Boolean,
    onMainClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        MainButton(
            onClick = onMainClick,
            enabled = enabledMainButton,
            text = stringResource(mainButtonTextRes),
            modifier = Modifier
                .height(dimensionResource(R.dimen.offset_64))
                .fillMaxWidth()
        )
        DividerRow(
            textRes = R.string.or_continue,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_32))
        )
        GoogleButton(
            modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_32)),
            onClick = onGoogleClick
        )
    }
}

@Composable
fun DividerRow(
    @StringRes textRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HelpDivider(Modifier.weight(1f))
        Text(
            text = stringResource(textRes),
            style = HelpText,
            color = HelpColor,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.offset_16))
        )
        HelpDivider(Modifier.weight(1f))
    }
}

@Composable
fun HelpDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        color = HelpColor
    )
}

@Composable
fun EmailPasswordInput(
    modifier: Modifier = Modifier,
    uiState: AuthUiState,
    updateUiState: (AuthUiState) -> Unit,
    validEmail: () -> Boolean,
    validPassword: () -> Boolean,
    errorEmailRes: Int,
    errorPasswordRes: Int
) {
    Column(
        modifier = modifier
    ) {
        InputColumn(
            headlineText = stringResource(R.string.email),
            inputText = uiState.email,
            onValueChange = {
                updateUiState(uiState.copy(email = it))
            },
            validation = validEmail,
            errorTextRes = errorEmailRes,
            leadingIconRes = R.drawable.ic_usertag
        )
        InputColumn(
            headlineText = stringResource(R.string.password),
            inputText = uiState.password,
            onValueChange = {
                updateUiState(uiState.copy(password = it))
            },
            leadingIconRes = R.drawable.ic_lock,
            isHidden = true,
            trailingIconsRes = Pair(R.drawable.ic_eyeslash, R.drawable.ic_eye),
            imeAction = ImeAction.Done,
            validation = validPassword,
            errorTextRes = errorPasswordRes,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.offset_16))
        )
    }
}

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = RectangleShape,
        border = BorderStroke(dimensionResource(R.dimen.offset_2), White),
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.offset_64))
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_google),
            contentDescription = null,
            tint = White
        )
        Text(
            text = stringResource(R.string.google),
            style = GoogleText,
            color = White,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.offset_8))
        )
    }
}
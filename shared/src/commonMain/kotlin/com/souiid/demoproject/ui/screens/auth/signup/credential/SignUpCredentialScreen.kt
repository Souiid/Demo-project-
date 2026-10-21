package com.souiid.demoproject.ui.screens.auth.signup.credentials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.email
import demoproject.shared.generated.resources.password
import demoproject.shared.generated.resources.signUp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import androidx.compose.material3.MaterialTheme
import com.souiid.demoproject.ui.screens.auth.signup.credential.SignUpCredentialViewModel
import demoproject.shared.generated.resources.confirmPassword

@Composable
fun SignUpCredentialScreen() {
    val viewModel: SignUpCredentialViewModel = koinInject()
    val validationError = viewModel.error as? AppError.Validation

    TScreen(verticalArrangement = Arrangement.Center) {
        TTextField(
            labelTitle = stringResource(Res.string.email),
            value = viewModel.email,
            onValueChange = {
                viewModel.email = it
                if (validationError?.field == AppError.Field.EMAIL) viewModel.clearError()
            },
            type = TTextFieldType.EMAIL,
            errorMessage = validationError?.takeIf {
                it.field == AppError.Field.EMAIL
            }?.message
        )

        TTextField(
            labelTitle = stringResource(Res.string.password),
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
                if (validationError?.field == AppError.Field.PASSWORD) viewModel.clearError()
            },
            type = TTextFieldType.PASSWORD,
            imeAction = ImeAction.Done,
            errorMessage = validationError?.takeIf {
                it.field == AppError.Field.PASSWORD
            }?.message
        )

        TTextField(
            labelTitle = stringResource(Res.string.confirmPassword),
            value = viewModel.confirmPassword,
            onValueChange = {
                viewModel.confirmPassword = it
                if (validationError?.field == AppError.Field.CONFIRM_PASSWORD) viewModel.clearError()
            },
            type = TTextFieldType.PASSWORD,
            imeAction = ImeAction.Done,
            errorMessage = validationError?.takeIf {
                it.field == AppError.Field.CONFIRM_PASSWORD
            }?.message
        )

        val generalError = viewModel.error?.takeUnless { it is AppError.Validation }

        if (generalError != null) {
            val message = when (generalError) {
                is AppError.Authentication -> generalError.message
                is AppError.Network -> generalError.message
                is AppError.Server -> generalError.message
                is AppError.Unknown -> generalError.message
                is AppError.Validation -> ""
            }

            TText(
                text = message,
                style = TTextStyle.LABEL,
                color = MaterialTheme.colorScheme.error
            )
        }

        TButton(
            text = stringResource(Res.string.signUp),
            onClick = { viewModel.signUp() },
            enabled = !viewModel.isLoading
        )
    }
}
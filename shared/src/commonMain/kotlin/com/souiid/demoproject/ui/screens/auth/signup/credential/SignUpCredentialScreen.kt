package com.souiid.demoproject.ui.screens.auth.signup.credential

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.ui.components.others.SignUpScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.utils.appErrorMessage
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.confirmPassword
import demoproject.shared.generated.resources.createYourAccount
import demoproject.shared.generated.resources.email
import demoproject.shared.generated.resources.password
import demoproject.shared.generated.resources.signUp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpCredentialScreen() {
    val viewModel: SignUpCredentialViewModel = koinViewModel()
    val validationError = viewModel.error as? AppError.Validation
    val generalError = viewModel.error?.takeUnless { it is AppError.Validation }

    SignUpScreen(
        title = stringResource(Res.string.createYourAccount),
        buttonText = stringResource(Res.string.signUp),
        onContinueClick = { viewModel.signUp() },
        isLoading = viewModel.isLoading
    ) {
        TTextField(
            labelTitle = stringResource(Res.string.email),
            value = viewModel.email,
            onValueChange = {
                viewModel.email = it
                if (validationError?.field == AppError.Field.EMAIL) viewModel.clearError()
            },
            type = TTextFieldType.EMAIL,
            imeAction = ImeAction.Next,
            errorMessage = validationError
                ?.takeIf { it.field == AppError.Field.EMAIL }
                ?.let { appErrorMessage(it) }
        )

        TTextField(
            labelTitle = stringResource(Res.string.password),
            value = viewModel.password,
            onValueChange = {
                viewModel.password = it
                if (validationError?.field == AppError.Field.PASSWORD) viewModel.clearError()
            },
            type = TTextFieldType.PASSWORD,
            imeAction = ImeAction.Next,
            errorMessage = validationError
                ?.takeIf { it.field == AppError.Field.PASSWORD }
                ?.let { appErrorMessage(it) }
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
            errorMessage = validationError
                ?.takeIf { it.field == AppError.Field.CONFIRM_PASSWORD }
                ?.let { appErrorMessage(it) }
        )

        if (generalError != null) {
            TText(
                text = appErrorMessage(generalError),
                style = TTextStyle.LABEL,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

package com.souiid.demoproject.ui.screens.auth.signup.name

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.ui.components.others.SignUpScreen
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.utils.appErrorMessage
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.firstName
import demoproject.shared.generated.resources.lastName
import demoproject.shared.generated.resources.whatIsYourName
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpNameScreen(onContinueClick: () -> Unit) {
    val viewModel: SignUpNameViewModel = koinViewModel()

    SignUpScreen(
        title = stringResource(Res.string.whatIsYourName),
        isContinueEnabled = viewModel.isContinueEnabled,
        onContinueClick = {
            if (viewModel.updateName()) onContinueClick()
        }
    ) {
        TTextField(
            labelTitle = stringResource(Res.string.firstName),
            value = viewModel.firstName,
            onValueChange = {
                viewModel.clearError()
                viewModel.firstName = it
            },
            type = TTextFieldType.TEXT,
            imeAction = ImeAction.Next,
            errorMessage = viewModel.error
                ?.takeIf { it.field == AppError.Field.FIRST_NAME }
                ?.let { appErrorMessage(it) }
        )

        TTextField(
            labelTitle = stringResource(Res.string.lastName),
            value = viewModel.lastName,
            onValueChange = {
                viewModel.clearError()
                viewModel.lastName = it
            },
            type = TTextFieldType.TEXT,
            imeAction = ImeAction.Done,
            errorMessage = viewModel.error
                ?.takeIf { it.field == AppError.Field.LAST_NAME }
                ?.let { appErrorMessage(it) }
        )
    }
}
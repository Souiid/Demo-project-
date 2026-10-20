package com.souiid.demoproject.ui.screens.auth.signup.name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.domain.model.AppError
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.continueLabel
import demoproject.shared.generated.resources.firstName
import demoproject.shared.generated.resources.lastName
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SignUpNameScreen(
    onContinueClick: () -> Unit
) {
    val viewModel: SignUpNameViewModel = koinInject()
    val error = viewModel.error

    TScreen(verticalArrangement = Arrangement.Center) {
        TTextField(
            labelTitle = stringResource(Res.string.firstName),
            value = viewModel.firstName,
            onValueChange = {
                viewModel.clearError()
                viewModel.firstName = it
            },
            type = TTextFieldType.TEXT,
            imeAction = ImeAction.Next,
            errorMessage = error?.takeIf {
                it.field == AppError.Field.FIRST_NAME
            }?.message
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
            errorMessage = error?.takeIf {
                it.field == AppError.Field.LAST_NAME
            }?.message
        )

        TButton(
            text = stringResource(Res.string.continueLabel),
            onClick = {
                if (viewModel.updateName()) {
                    onContinueClick()
                }
            }
        )
    }
}
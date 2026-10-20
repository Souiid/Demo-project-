package com.souiid.demoproject.ui.screens.auth.signup.name

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
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

    TScreen(verticalArrangement = Arrangement.Center) {
        TTextField(
            labelTitle = stringResource(Res.string.firstName),
            value = viewModel.firstName,
            onValueChange = { viewModel.firstName = it },
            type = TTextFieldType.TEXT,
            imeAction = ImeAction.Next
        )

        TTextField(
            labelTitle = stringResource(Res.string.lastName),
            value = viewModel.lastName,
            onValueChange = { viewModel.lastName = it },
            type = TTextFieldType.TEXT,
            imeAction = ImeAction.Done
        )

        TButton(
            text = stringResource(Res.string.continueLabel),
            onClick = {
                val isNameValidate = viewModel.updateName()
                if (isNameValidate) {
                    onContinueClick()
                }
            }
        )
    }
}
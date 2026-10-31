package com.souiid.demoproject.ui.screens.auth.signup.description

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.ui.components.others.SignUpScreen
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.description
import demoproject.shared.generated.resources.tellUsAboutYourself
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignUpDescriptionScreen(onContinueClick: () -> Unit) {
    val viewModel: SignUpDescriptionViewModel = koinViewModel()

    SignUpScreen(
        title = stringResource(Res.string.tellUsAboutYourself),
        onContinueClick = {
            viewModel.updateDescription()
            onContinueClick()
        }
    ) {
        TTextField(
            labelTitle = stringResource(Res.string.description),
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            type = TTextFieldType.DESCRIPTION,
            imeAction = ImeAction.Done
        )
    }
}

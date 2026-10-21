package com.souiid.demoproject.ui.screens.auth.signup.description

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.continueLabel
import demoproject.shared.generated.resources.description
import demoproject.shared.generated.resources.put_profile_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun SignUpDescriptionScreen(
    onContinueClick: () -> Unit
) {
    val viewModel: SignUpDescriptionViewModel = koinInject()

    TScreen(verticalArrangement = Arrangement.Center) {
        TTextField(
            labelTitle = stringResource(Res.string.description),
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            type = TTextFieldType.DESCRIPTION,
            imeAction = ImeAction.Done
        )

        TButton(
            text = stringResource(Res.string.continueLabel),
            onClick = {
                viewModel.updateDescription()
                onContinueClick()
            }
        )
    }
}
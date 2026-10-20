package com.souiid.demoproject.ui.screens.auth.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.continueLabel
import demoproject.shared.generated.resources.firstName
import demoproject.shared.generated.resources.lastName
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpNameScreen(
    onContinueClick: () -> Unit
) {

    TScreen(verticalArrangement = Arrangement.Center) {
        TTextField(
            labelTitle = stringResource(Res.string.firstName),
            value = "",
            onValueChange = {  },
            type = TTextFieldType.TEXT
        )

        TTextField(
            labelTitle = stringResource(Res.string.lastName),
            value = "",
            onValueChange = {  },
            type = TTextFieldType.TEXT
        )

        TButton(
            text = stringResource(Res.string.continueLabel),
            onClick = { onContinueClick() },
        )
    }
}
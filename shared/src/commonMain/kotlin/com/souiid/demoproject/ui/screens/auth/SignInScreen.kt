package com.souiid.demoproject.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import demoproject.shared.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import demoproject.shared.generated.resources.signIn
import demoproject.shared.generated.resources.signUp
import demoproject.shared.generated.resources.email
import demoproject.shared.generated.resources.password

@Composable
fun SignInScreen(
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TTextField(
            labelTitle = stringResource(Res.string.email),
            value = email,
            onValueChange = { email = it },
            type = TTextFieldType.EMAIL
        )

        TTextField(
            labelTitle = stringResource(Res.string.password),
            value = password,
            onValueChange = { password = it },
            type = TTextFieldType.PASSWORD
        )

        TButton(
            text = stringResource(Res.string.signIn),
            onClick = {  }
        )

        TButton(
            text = stringResource(Res.string.signUp),
            type = TButtonType.TEXT,
            onClick = onSignUpClick
        )
    }
}
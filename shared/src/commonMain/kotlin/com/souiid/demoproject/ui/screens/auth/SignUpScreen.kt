package com.souiid.demoproject.ui.screens.auth

import androidx.compose.runtime.Composable
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle

@Composable
fun SignUpScreen(
    onSignInClick: () -> Unit
) {
    TText(
        text = "Sign Up",
        style = TTextStyle.TITLE
    )
}
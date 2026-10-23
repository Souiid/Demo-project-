package com.souiid.demoproject.ui.screens.app.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle

@Composable
fun FirebaseProfileScreen() {

    TScreen(verticalArrangement = Arrangement.Center) {
        TText(
            text = "Profile",
            style = TTextStyle.TITLE
        )

    }

}
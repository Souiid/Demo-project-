package com.souiid.demoproject.ui.screens.app.firebase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.firebase
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import demoproject.shared.generated.resources.signOut

@Composable
fun FirebaseFeedScreen() {
    val viewModel: FirebaseFeedViewModel = koinInject()

    TScreen(verticalArrangement = Arrangement.Center) {
        TText(
            text = stringResource(Res.string.firebase),
            style = TTextStyle.TITLE
        )

        TButton(
            text = stringResource(Res.string.signOut),
            onClick = {
                viewModel.signOut()
            }
        )
    }
}
package com.souiid.demoproject.ui.components.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.continueLabel
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    title: String,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    isContinueEnabled: Boolean = true,
    isLoading: Boolean = false,
    buttonText: String = stringResource(Res.string.continueLabel),
    content: @Composable () -> Unit
) {
    TScreen(horizontalAlignment = Alignment.Start) {
        Column(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Dimens.standardPadding)
            ) {
                TText(text = title, style = TTextStyle.TITLE)
                content()
            }

            Spacer(modifier = Modifier.weight(1f))

            TButton(
                text = buttonText,
                enabled = isContinueEnabled,
                isLoading = isLoading,
                onClick = onContinueClick,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
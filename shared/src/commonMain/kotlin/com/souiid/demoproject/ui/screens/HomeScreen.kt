package com.souiid.demoproject.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.theme.Dimens
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.home
import demoproject.shared.generated.resources.name
import demoproject.shared.generated.resources.save
import demoproject.shared.generated.resources.your_actual_name_is
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = koinInject()
    val userName by viewModel.userName.collectAsState()
    var nameInput by remember { mutableStateOf("") }

    TScreen(verticalArrangement = Arrangement.Center) {
        TText(
            text = stringResource(Res.string.home),
            style = TTextStyle.TITLE
        )

        userName?.let {
            TText(text = stringResource(Res.string.your_actual_name_is, it))
            Spacer(modifier = Modifier.size(Dimens.smallSpace))
        }

        TTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            labelTitle = stringResource(Res.string.name)
        )

        TButton(
            text = stringResource(Res.string.save),
            onClick = {
                viewModel.updateUserName(nameInput)
            }
        )
    }
}
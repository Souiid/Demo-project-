package com.souiid.demoproject.ui.screens.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import com.souiid.demoproject.firebase.domain.model.AppError
import com.souiid.demoproject.ui.components.button.TButton
import com.souiid.demoproject.ui.components.button.TButtonType
import com.souiid.demoproject.ui.components.others.TScreen
import com.souiid.demoproject.ui.components.text.TText
import com.souiid.demoproject.ui.components.text.TTextStyle
import com.souiid.demoproject.ui.components.textfield.TTextField
import com.souiid.demoproject.ui.components.textfield.TTextFieldType
import com.souiid.demoproject.ui.theme.Dimens
import com.souiid.demoproject.ui.utils.appErrorMessage
import demoproject.shared.generated.resources.Res
import demoproject.shared.generated.resources.dontHaveAccount
import demoproject.shared.generated.resources.email
import demoproject.shared.generated.resources.password
import demoproject.shared.generated.resources.signIn
import demoproject.shared.generated.resources.signUp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignInScreen(onSignUpClick: () -> Unit) {
    val viewModel: SignInViewModel = koinViewModel()
    val validationError = viewModel.error as? AppError.Validation
    val generalError = viewModel.error?.takeUnless { it is AppError.Validation }

    Box(modifier = Modifier.fillMaxSize()) {
        SignInBackground(modifier = Modifier.fillMaxSize())
        TScreen {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.mediumSpace)
                    ) {
                        TText(
                            text = stringResource(Res.string.signIn),
                            style = TTextStyle.TITLE
                        )

                        TTextField(
                            labelTitle = stringResource(Res.string.email),
                            value = viewModel.email,
                            onValueChange = {
                                viewModel.email = it
                                if (validationError?.field == AppError.Field.EMAIL) viewModel.clearError()
                            },
                            type = TTextFieldType.EMAIL,
                            imeAction = ImeAction.Next,
                            errorMessage = validationError
                                ?.takeIf { it.field == AppError.Field.EMAIL }
                                ?.let { appErrorMessage(it) }
                        )

                        TTextField(
                            labelTitle = stringResource(Res.string.password),
                            value = viewModel.password,
                            onValueChange = {
                                viewModel.password = it
                                if (validationError?.field == AppError.Field.PASSWORD) viewModel.clearError()
                            },
                            type = TTextFieldType.PASSWORD,
                            imeAction = ImeAction.Done,
                            errorMessage = validationError
                                ?.takeIf { it.field == AppError.Field.PASSWORD }
                                ?.let { appErrorMessage(it) }
                        )

                        if (generalError != null) {
                            TText(
                                text = appErrorMessage(generalError),
                                style = TTextStyle.LABEL,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        TButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.signIn),
                            onClick = { viewModel.signIn() },
                            isLoading = viewModel.isLoading
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = Dimens.extraLargeSpace),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TText(
                        text = stringResource(Res.string.dontHaveAccount),
                        style = TTextStyle.DESCRIPTION
                    )

                    TButton(
                        text = stringResource(Res.string.signUp),
                        type = TButtonType.TEXT,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onSignUpClick
                    )
                }
            }
        }
    }
}


@Composable
fun SignInBackground(modifier: Modifier = Modifier) {
    val green = Color.Green
    val shape = RoundedCornerShape(Dimens.signInModuleRadius)

    Box(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFFDCFCE7), Color.White, Color.White)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Dimens.extraLargeSpace)
                .alpha(0.08f),
            verticalArrangement = Arrangement.spacedBy(Dimens.signInModuleSpacing)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.signInModuleSpacing)) {
                Box(
                    modifier = Modifier
                        .size(Dimens.signInModuleSize)
                        .background(green, shape)
                )
                Box(
                    modifier = Modifier
                        .size(Dimens.signInModuleSize)
                        .border(Dimens.signInModuleBorder, green, shape)
                )
            }
            Box(
                modifier = Modifier
                    .size(Dimens.signInModuleSize)
                    .border(Dimens.signInModuleBorder, green, shape)
            )
        }
    }
}
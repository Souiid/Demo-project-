package com.souiid.demoproject.ui.utils

import androidx.compose.runtime.Composable
import com.souiid.demoproject.domain.model.AppError
import demoproject.shared.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun appErrorMessage(error: AppError): String {
    return when (error) {
        is AppError.Validation -> when (error.reason) {
            AppError.ValidationReason.REQUIRED -> stringResource(Res.string.errorRequired)
            AppError.ValidationReason.INVALID_EMAIL -> stringResource(Res.string.errorInvalidEmail)
            AppError.ValidationReason.PASSWORD_TOO_SHORT -> stringResource(Res.string.errorPasswordTooShort)
            AppError.ValidationReason.PASSWORDS_DO_NOT_MATCH -> stringResource(Res.string.errorPasswordsDoNotMatch)
        }
        is AppError.Authentication -> when (error.reason) {
            AppError.AuthenticationReason.INVALID_CREDENTIALS -> stringResource(Res.string.errorInvalidCredentials)
            AppError.AuthenticationReason.EMAIL_ALREADY_IN_USE -> stringResource(Res.string.errorEmailAlreadyInUse)
            AppError.AuthenticationReason.UNABLE_TO_CREATE_ACCOUNT -> stringResource(Res.string.errorUnableToCreateAccount)
        }
        is AppError.Network -> stringResource(Res.string.errorConnectionFailed)
        is AppError.Server -> stringResource(Res.string.errorServerUnavailable)
        is AppError.Unknown -> stringResource(Res.string.errorUnknown)
    }
}
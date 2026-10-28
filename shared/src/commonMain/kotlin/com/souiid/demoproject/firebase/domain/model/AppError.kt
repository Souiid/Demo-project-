package com.souiid.demoproject.firebase.domain.model

sealed interface AppError {
    data class Validation(val field: Field, val reason: ValidationReason) : AppError
    data class Authentication(val reason: AuthenticationReason) : AppError
    data class Network(val reason: NetworkReason) : AppError
    data class Server(val reason: ServerReason) : AppError
    data class Unknown(val reason: UnknownReason = UnknownReason.GENERIC) : AppError

    enum class Field {
        FIRST_NAME,
        LAST_NAME,
        EMAIL,
        PASSWORD,
        CONFIRM_PASSWORD
    }

    enum class ValidationReason {
        REQUIRED,
        INVALID_EMAIL,
        PASSWORD_TOO_SHORT,
        PASSWORDS_DO_NOT_MATCH
    }

    enum class AuthenticationReason {
        INVALID_CREDENTIALS,
        EMAIL_ALREADY_IN_USE,
        UNABLE_TO_CREATE_ACCOUNT
    }

    enum class NetworkReason {
        CONNECTION_FAILED
    }

    enum class ServerReason {
        UNAVAILABLE
    }

    enum class UnknownReason {
        GENERIC
    }
}
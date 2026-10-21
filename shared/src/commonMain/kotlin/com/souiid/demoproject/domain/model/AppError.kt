package com.souiid.demoproject.domain.model

sealed interface AppError {

    data class Validation(val field: Field, val message: String) : AppError
    data class Authentication(val message: String) : AppError
    data class Network(val message: String) : AppError
    data class Server(val message: String) : AppError
    data class Unknown(val message: String) : AppError

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
}

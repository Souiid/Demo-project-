package com.souiid.demoproject.domain.model

sealed interface AppError {

    data class Validation(
        val field: Field,
        val message: String
    ) : AppError

    enum class Field {
        FIRST_NAME,
        LAST_NAME,
        EMAIL,
        PASSWORD
    }

}

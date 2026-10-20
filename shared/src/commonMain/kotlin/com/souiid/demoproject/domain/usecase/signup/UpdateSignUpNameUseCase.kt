package com.souiid.demoproject.domain.usecase.signup

import com.souiid.demoproject.data.SignUpRepository

class UpdateSignUpNameUseCase(
    private val repository: SignUpRepository
) {
    operator fun invoke(firstName: String, lastName: String): Boolean {
        if (firstName.isBlank() || lastName.isBlank()) return false

        repository.updateFirstName(firstName.trim())
        repository.updateLastName(lastName.trim())
        return true
    }
}